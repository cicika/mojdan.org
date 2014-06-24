package org.mojdan.md_backend.http.api

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

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.util._

trait MoodScaleService extends HttpService with AppConfig{

	import org.mojdan.md_backend.model.Tables

	def postExperiences = (user: String, context: ActorContext) => {
		entity(as[String]){s =>
			apiLogger.debug("POST /scales extracted {}", s)
			Try(s.asJson.asJsObject.convertTo[MoodScalesRow]) match {
				case Success(ms) => 
					onComplete((context.actorFor("/user/application-actor") ? ms.copy(uid = user.toLong)).mapTo[Option[Long]]) {
						case Success(res) => 
							val response = res match {
								case Some(x) => complete(StatusCodes.NoContent)
								case None => complete(StatusCodes.InternalServerError)
							}
							response
						case Failure(ex) => 
							apiLogger.error("POST /scales timeout, user %s, reason %s" format(user, ex))
							complete(StatusCodes.InternalServerError)
					}
				case Failure(ex) => complete(StatusCodes.BadRequest)						
			}
		}
	}

	def scales = (user: String, context: ActorContext) => {
		onComplete((context.actorFor("/user/application-actor") ? MoodScalesForUser(user.toLong)).mapTo[List[Tuple2[ActivityDiaryRow, MoodScalesRow]]]){
			case Success(res) => 
				val r = res.length match {
					case x if x == 0 => complete(StatusCodes.NoContent)
					case x if x > 0 =>
						val response = JsObject(Map(
															"activities" -> res.map(e => e._1).toJson,
															"scales" -> res.map(e => e._2).toJson))
						respondWithStatus(StatusCodes.OK)
						complete(response.toJson.toString)
				}
				r
			case Failure(ex) => 
				apiLogger.error("GET /scales timeout, user %s, reason %s"format(user, ex))
				complete(StatusCodes.InternalServerError)
		}
	}
}