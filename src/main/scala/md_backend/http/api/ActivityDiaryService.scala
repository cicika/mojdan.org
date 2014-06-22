package org.mojdan.md_backend.http.api

import akka.actor._
import akka.pattern._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}

import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling.BasicUnmarshallers._
import spray.json._
import spray.routing._
import spray.routing.{Directives, HttpService}

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.http._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.util._

trait ActivityDiaryService extends HttpService with AppConfig{

	import org.mojdan.md_backend.model.Tables
	
	def postStartMood = (user: String, context: ActorContext) => {
		entity(as[String]){s =>
			s.asJson.asJsObject.fields.get("start_mood") match {
				case Some(x) =>
					onComplete((context.actorFor("/user/application-actor") ? StartMood(user.toLong, x.convertTo[Int])).mapTo[Long]) {
						case Success(_) => complete(StatusCodes.OK)
						case Failure(ex) =>
							apiLogger.error("POST /activity/start timeout, user {}", user)
							complete(StatusCodes.InternalServerError)
					}
				case None => complete(StatusCodes.BadRequest)
			}  
		}
	}
	def postActivity = (user: String, context: ActorContext) => {
		entity(as[String]){s =>
			apiLogger.debug("POST /activity/complete Extracted entity... {}", s)

			Try(s.asJson.asJsObject.convertTo[ActivityDiaryRow]) match {
				case Success(ad) =>
					onComplete((context.actorFor("/user/application-actor") ? ad.copy(uid = user.toLong)).mapTo[Long]){
						case Success(_) => complete(StatusCodes.OK)
						case Failure(ex) =>
							apiLogger.error("POST /activity/complete timeout, for user {}", user)
							complete(StatusCodes.InternalServerError)
					}
				case Failure(ex) => complete(StatusCodes.BadRequest)
			}
		}
	}

	def activityHistory = (user: String) => {
	 	/*	val q = for{
	 			ad <- ActivityDiary if (ad.uid === user.toLong)
	 		} yield (ad.aid, ad.uid, ad.day, ad.activity, ad.startMood, ad.expMood, ad.achMood, 
	 							ad.satisfaction, ad.achievement, ad.note)
	 		val result = db.withSession{session =>
	 			q.list()(session)
	 		}.map(e => 
	 			ActivityDiaryRow(e._1, e._2, e._3, e._4, e._5, e._6, e._7, e._8, e._9, e._10)).sortBy(e => e.day)
	 		respondWithStatus(StatusCodes.OK)
	 		complete(result.toJson.toString)*/
	 		complete(StatusCodes.NotImplemented)
	 }  
}