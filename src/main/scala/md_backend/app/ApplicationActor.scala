package org.mojdan.md_backend.app

import akka.actor._
import akka.event.Logging
import akka.util.Timeout

import java.sql.Timestamp

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.storage._
import org.mojdan.md_backend.util._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

class ApplicationActor extends Actor with AppConfig
																		 with ApplicationStorage
																		 with TimeUtils {

																 	
	private val log = Logging(context.system, this)
	import org.mojdan.md_backend.model.Tables	
	//override implicit val timeout = Timeout(5 seconds)
	def receive = {

		case StartProgramme(uid) => 
			val replyTo = sender
			Future{ insertCompleted(CompletedRow(uid, None, 1, Some(new Timestamp(now)))) }
			Future{ programmeByDay(List(1)) } onComplete {
				case Success(p) => replyTo ! p
				case Failure(ex) =>
					log.error("StartProgramme failed for user {} uid, reason {}", uid, ex)
					replyTo ! List()
			}

		case RestartProgramme(uid) =>
			val replyTo = sender
			for {
				c <- Future { updateCompleted(uid, None) }
				a <- Future { updateActive(uid, 1) }
			} yield {
				replyTo ! Done()
				Future {
					deleteUserActivity(uid)
					deleteUserScales(uid)
				}
			}
			//TODO archive current progress (need new db table !!)
		case ProgrammeForUser(uid) =>
			val replyTo = sender
			for{
				c <- Future { completedByUid(uid) }
				p <- Future { programmeByDay(List(), true) }
			} yield {
			
				val active = c.get.active
				val dateStarted = c.get.dateStarted
				val activeDayCheck = activeDay(dateStarted, active)
			val finalActiveDay = 
				if(activeDayCheck > active){
					Future{	updateActive(uid, activeDayCheck) }
					activeDayCheck
				}
				else active
				log.debug("ProgrammeForUser result {}", Tuple2(c.map(e => e.copy(active = finalActiveDay)), p))
				replyTo ! Tuple2(c.map(e => e.copy(active = finalActiveDay)), p)
			}

		case DailyProgrammeForUser(uid) => 
			val replyTo = sender
			for {
				c <- Future{ completedByUid(uid) }
				p <- Future { programmeByDay(List(c.get.active))}
			} yield {
				replyTo ! p
			}

		case CompletedDaysForUser(uid) =>
			val replyTo = sender
			Future { completedByUid(uid) } onComplete {
				case Success(x) => replyTo ! x
				case Failure(ex) =>
					log.error("Failed to GetCompletedDays for user {}, reason {}", uid, ex)
					replyTo ! None
			}

		case StartMood(uid, startMood) => 
			val replyTo = sender
			for {
				c <- Future { completedByUid(uid) }
				aid <- Future { insertActivityRow(
												ActivityDiaryRow(-1, uid, c.get.active, None, Some(startMood), None, None, None, None, None)) }
			} yield { 
				replyTo ! aid 
			}			

		case msg: ActivityDiaryRow => 
			val replyTo = sender
			for {
				c <- Future { completedByUid(msg.uid) }
				o <- Future { activityRowByUserAndDay(msg.uid, c.get.active) }
				aid <- Future{ insertActivityRow(msg.copy(startMood = o.get._2).copy(day = c.get.active)) }
			} yield {
				val r = (o.get._1, aid, c.get.completed, c.get.active)

				replyTo ! r._2

				Future{
					deleteActivityRow(r._1)
					updateActive(msg.uid, r._4 + 1)
					updateCompleted(msg.uid, newCompletedList(r._3, msg.activity, r._4))
				}
			}

		case msg: MoodScalesRow =>
			val replyTo = sender 
			Future{ insertMoodScales(msg) } onComplete { 
				case Success(x) => replyTo ! Some(x)
				case Failure(ex) =>
					log.error("Failed to save MoodScalesRow, user {} reason {}", msg.uid, ex)
					replyTo ! None
			}

		case MoodScalesForUser(uid) => 
			val replyTo = sender
			for {
				c <- Future { completedByUid(uid) }
				s <- Future { completeScales(uid, records(c.map(e => e.active).getOrElse(1))) }
			} yield {
				replyTo ! s
			}

		case Terminated(_) => log.error("terminated...")
		case _ =>
	}

	private def newCompletedList(completedOld: Option[String], activity: Option[String], active: Int) = (completedOld, activity) match {
		case (Some(x), Some(y)) => Some(x+","+active.toString)
		case (Some(x), None) if (x != None && x.toString != "None") => Some(x)
		case (None, Some(y)) => Some("1")
		case (None, None) => None
	}

	private def activeDay(dateStarted: Option[Timestamp], active: Int):Int = dateStarted match {
		case Some(ds) =>
			val result = 
				if(isToday(ds)) active
				else daysDiff(ds) match {
					case x if x > 28 => 0
					case x if x <= 28 => x	
				} 
			result			
		case None => 1
	}

	private def records(active: Int) = active match {
			case x if x < 8 => 0
			case x if (x >= 8 && x < 15) => 7
			case x if (x >= 15 && x < 22) => 14
			case x if (x >= 22 && x < 28) => 21
			case x if x >= 28 => 28
			case _ => 0
		}
}