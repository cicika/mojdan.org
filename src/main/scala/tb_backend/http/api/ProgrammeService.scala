package org.mojdan.md_backend.http.api

import akka.actor._
import akka.pattern._
import akka.util.Timeout

import java.sql.Timestamp

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

import org.slf4j.LoggerFactory

import spray.http.StatusCodes

import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling.BasicUnmarshallers._
import spray.json._
import spray.routing._

import spray.routing.{Directives, HttpService}

import org.mojdan.md_backend._
import model._
import model.TBJsonProtocol._
import org.mojdan.md_backend.util._


trait ProgrammeService extends HttpService with Config
																					 with TimeUtils {
	private implicit val timeout = Timeout(15 seconds)
	private val log = LoggerFactory.getLogger(classOf[UserAccountService])

	import org.mojdan.md_backend.model.Tables

	//GET /programme/start

	def startProgramme = (user: String) => {
		val q = for {
			p <- Programme if (p.day === 1)
		} yield (p.image, p.sentence, p.activityS, p.activityL, p.activitySB, p.activityLB)

		val result = db.withSession{session =>
			q.list()(session)
		}
		result.headOption match {
			case Some(p) => 
				respondWithStatus(StatusCodes.OK)
				val response = if(currentHour <= 15){
												ProgrammeRow(1, p._1, p._2, p._3, p._4, p._5, p._6)
											 }
											 else {
											 	ProgrammeRow(1, p._1, p._2, None, None, p._5, p._6)
											 }
				db.withSession{ implicit session =>
					Completed += CompletedRow(user.toLong, None, 1, Some(new Timestamp(now)))
				}
				complete(response.toJson.toString)
			case None => complete(StatusCodes.NotFound)
		}
	}

	def restartProgramme = (user: String) => {
		complete(StatusCodes.NotImplemented)
	}

	def programme = (user: String) => {
		val q = for {
			p <- Programme
		} yield (p.day, p.image, p.sentence, p.activityS, p.activityL, p.activitySB, p.activityLB)

		val progList = db.withSession{session =>
			q.list()(session)
		}.map(e => ProgrammeRow(e._1, e._2, e._3, e._4, e._5, e._6, e._7))

		val q1 = for {
			c <- Completed if (c.uid === user.toLong)
		} yield (c.completed, c.active, c.dateStarted)

		val compl = db.withSession{session =>
			q1.list()(session)
		}.headOption.map(e => CompletedRow(user.toLong, e._1, e._2, e._3))

		System.out.println(compl.map(c => c.completed).
																	  	flatten)

		val active = compl.map(c => c.active).getOrElse(1)
		val dateStarted = compl.map(c => c.dateStarted).flatten
		val activeDayCheck = activeDay(dateStarted, active)

		val finalActiveDay = 
			if(activeDayCheck > active){
				Future{
					val q2 = for {
						c <- Completed if(c.uid === user.toLong)
					} yield c.active
					db.withSession{implicit session =>
						q2.update(activeDayCheck)
						q2.updateStatement
						q2.updateInvoker
					}
				}
				activeDayCheck
			}
			else active

		val response = new JsObject(Map("programme" -> progList.toJson,
																	  "completed" -> compl.map(c => c.completed).
																	  	flatten.
																	  	map(e => e.split(",").
																	  	map(a => a.toInt)).
																	  	getOrElse(Array()).
																	  	toJson,
																	  "active" -> finalActiveDay.toJson))
		respondWithStatus(StatusCodes.OK)
		complete(response.toString)
	}

	def dailyProgramme = (user: String) => {
		val q = for {			
			c <- Completed if (c.uid === user.toLong)
			p <- Programme if (p.day === c.active)
		} yield (p.day, p.image, p.sentence, p.activityS, p.activityL, p.activitySB, p.activityLB)

		val result = db.withSession{session =>
			q.list()(session)
		}
		result.headOption match {
			case Some(p) => 
				val pd = ProgrammeRow(p._1, p._2, p._3, p._4, p._5, p._6, p._7)
				respondWithStatus(StatusCodes.OK)
				complete(pd.toJson.toString)
			case None => complete(StatusCodes.NotFound)
		}
	}

	def completedDays = (user: String) => {

		val q1 = for {
			s <- ScheduledTasks if (s.uid === user.toLong && s.tType === 0)
		} yield s.day

		val res1 = db.withSession{session =>
			q1.list()(session)
		}

		val q2 = for {
			p <- Programme if (p.day < res1.min)
		} yield (p.day, p.image, p.sentence, p.activityS, p.activityL, p.activitySB, p.activityLB)

		val result = db.withSession{session =>
			q2.list()(session)
		}
		if(!result.isEmpty){
			respondWithStatus(StatusCodes.OK)
			val res = result.map(e => ProgrammeRow(e._1, e._2, e._3, e._4, e._5, e._6, e._7))
			complete(result.map(e => res.toJson.toString))
		}
		else complete(StatusCodes.InternalServerError)
	} 

	private def activeDay(dateStarted: Option[Timestamp], active: Int) = dateStarted match {
		case Some(ts) =>
			val result = weekdayWithDelta(ts, active) match {
				case x if x == 7 => 
					if(weekdayNow == 0) active + 1
					else active
				case x if x < 7 => 
					if (weekdayNow > x) active + 1
					else active
			} 
			result			
		case None => 1
	}
}  


 
