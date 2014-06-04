package tb_backend.http.api

import akka.actor._
import akka.pattern._
import akka.util.Timeout

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

import tb_backend._
import model._
import http._
import model.TBJsonProtocol._
import tb_backend.util._

trait ActivityDiaryService extends HttpService with Config{

//	private val log = LoggerFactory.getLogger(classOf[TBApiServiceActor])

	import tb_backend.model.Tables

	
	def postStartMood = (user: String) => {
		entity(as[String]){s =>
			s.asJson.asJsObject.fields.get("start_mood") match {
				case Some(x) =>
					val q = for {
						c <- Completed if (c.uid === user.toLong)
					} yield c.active

					val day = db.withSession{ session =>
						q.list()(session)
					}.headOption.getOrElse(1)

					db.withSession{implicit session =>
					//	q.update(Some(day+1))
					//	val statement = q.updateStatement
					//	val invoker = q.updateInvoker

					ActivityDiary += ActivityDiaryRow(-1, user.toLong, day, None, Some(x.convertTo[Int]), None, None, None, None, None)					
					}
					
					
					complete(StatusCodes.OK)
				case None => complete(StatusCodes.BadRequest)
			}  
		}
	}
	def postActivity = (user: String) => {
		//log.info("RUnnin method...")
		entity(as[String]){s =>
			//log.info("Extracted entity...")

			Try(s.asJson.asJsObject.convertTo[ActivityDiaryRow]) match {
				case Success(ad) =>
						//log.info("And successfully....")
					val activity = ad.activity
					val exp_mood = ad.expMood
					val ach_mood = ad.achMood
					val satisfaction = ad.satisfaction
					val achievement = ad.achievement
					val note = ad.note

					val q = for {
						c <- Completed if (c.uid === user.toLong)
					} yield c.active

					val day = db.withSession{ session =>
						q.list()(session)
					}.headOption.getOrElse(1)

					//log.info("Got number of days... {}", day)

					val q2 = for{
						ad <- ActivityDiary if(ad.uid === user.toLong && ad.day === day)
					} yield (ad.aid, ad.startMood)

					val oldData = db.withSession{session =>
						q2.list()(session)
					}.headOption

					val aid = oldData.map(e => e._1)
					//log.info("Got old data tooo {}", oldData)

					val q3 = for{
						c <- Completed if(c.uid === user.toLong)
					} yield c.completed

					val q4 = for{
						c1 <- Completed if(c1.uid === user.toLong)
					} yield c1.active

					val completedOld = db.withSession{session =>
						q3.list()(session)
					}.head

					val activeOld = db.withSession{session =>
						q4.list()(session)
					}.headOption

					val completedNew = (completedOld, activity) match {
						case (Some(x), Some(y)) => Some(x+","+day.toString)
						case (Some(x), None) if (x != None && x.toString != "None") => Some(x)
						case (None, Some(y)) => Some(1)
						case (None, None) => None
					}
					//log.info("Attempting to update....")
					db.withSession{implicit session =>
						ActivityDiary += ActivityDiaryRow(-1, user.toLong, day, activity, oldData.map(e => e._2.getOrElse(1)), exp_mood, ach_mood, satisfaction, achievement, note)
						q3.update(completedNew.map(e => e.toString))
						q4.update(activeOld.map(e => e + 1).getOrElse(1))
						q4.updateStatement
						q4.updateInvoker
						sqlu"delete from activity_diary where aid = $aid".first
						//q2.delete
						val statement = q3.updateStatement
						val invoker = q3.updateInvoker
						//q2.deleteStatement
						//q2.deleteInvoker
					}						
					complete(StatusCodes.OK)
				case Failure(ex) => complete(StatusCodes.BadRequest)
			}
		}
	}

	 def activityHistory = (user: String) => {
	 		val q = for{
	 			ad <- ActivityDiary if (ad.uid === user.toLong)
	 		} yield (ad.aid, ad.uid, ad.day, ad.activity, ad.startMood, ad.expMood, ad.achMood, 
	 							ad.satisfaction, ad.achievement, ad.note)
	 		val result = db.withSession{session =>
	 			q.list()(session)
	 		}.map(e => 
	 			ActivityDiaryRow(e._1, e._2, e._3, e._4, e._5, e._6, e._7, e._8, e._9, e._10)).sortBy(e => e.day)
	 		respondWithStatus(StatusCodes.OK)
	 		complete(result.toJson.toString)
	 }  
}