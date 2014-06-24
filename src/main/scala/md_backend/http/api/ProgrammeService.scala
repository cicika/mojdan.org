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
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.util._

trait ProgrammeService extends HttpService with AppConfig
																					 with TimeUtils {

	def startProgramme = (user: String, context: ActorContext) => {
		onComplete((context.actorFor("/user/application-actor") ? StartProgramme(user.toLong)).mapTo[List[ProgrammeRow]]){
			case Success(res) => res.length match {
				case x if x == 1 =>
					respondWithStatus(StatusCodes.OK)
					val response = if(currentHour <= 15) res.head
												 else res.head.copy(activityS = None).copy(activityL = None)
					complete(response.toJson.toString)
				case _ => complete(StatusCodes.InternalServerError)
			}
			case Failure(ex) => 
				apiLogger.error("GET /programme/start timeout, user {}", user)
				complete(StatusCodes.InternalServerError)
		}
	}

	def restartProgramme = (user: String, context: ActorContext) => {
		onComplete((context.actorFor("/user/application-actor") ? RestartProgramme(user.toLong)).mapTo[Done]){
			case Success(x) => complete(StatusCodes.NoContent)
			case Failure(ex) => 
				apiLogger.error("GET /programme/restart timeout, user {}", user)
				complete(StatusCodes.InternalServerError)
		}
	}

	def programme = (user: String, context: ActorContext) => {
		onComplete((context.actorFor("/user/application-actor") ? ProgrammeForUser(user.toLong)).mapTo[Tuple2[Option[CompletedRow], List[ProgrammeRow]]]){
			case Success(res) =>
				val response = new JsObject(
												Map("programme" -> res._2.toJson,
														"completed" -> res._1.map(e => e.completed.map(a => a.split(",").map(i => i.toInt))).
																								 flatten.getOrElse(Array()).toJson,
														"active" -> res._1.map(e => e.active).getOrElse(1).toJson))
				respondWithStatus(StatusCodes.OK)
				complete(response.toJson.toString)

			case Failure(ex) => 
				apiLogger.error("GET /programme/all timeout, user {}", user)
				complete(StatusCodes.InternalServerError)
		}
	}

	def dailyProgramme = (user: String, context: ActorContext) => {
		onComplete((context.actorFor("/user/application-actor") ? DailyProgrammeForUser(user.toLong)).mapTo[List[ProgrammeRow]]){
			case Success(res) => 
				val response = 
					if(res.length == 1){
						respondWithStatus(StatusCodes.OK)
						complete(res.head.toJson.toString)
					}
					else complete(StatusCodes.NoContent)
				response
				
			case Failure(ex) =>
				apiLogger.error("GET /programme/active timeout, user {}", user)
				complete(StatusCodes.InternalServerError)
		}
	}

	def completedDays = (user: String, context: ActorContext) => {
		onComplete((context.actorFor("/user/application-actor") ? CompletedDaysForUser(user.toLong)).mapTo[Option[CompletedRow]]){
			case Success(res) => 
				val response = res match {
					case Some(c) => 
						respondWithStatus(StatusCodes.OK)
						complete(c.toJson.toString)
					case None => complete(StatusCodes.NotFound)
				}
				response
			case Failure(ex) =>
				apiLogger.error("GET /programme/completed timeout, for user {}", user)
				complete(StatusCodes.InternalServerError)
		}
	} 	
}   
