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
//import spray.json.DefaultJsonProtocol._
import spray.routing._
import spray.routing.{Directives, HttpService}

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.util._

trait UserAccountService extends HttpService with AppConfig 
																						 with TokenGenerator
																						 with LinkGenerator{

	import org.mojdan.md_backend.model.Tables

	def login = (context: ActorContext) => detach(){
			apiLogger.debug("We are actuallz here....")
			entity(as[String]){s => 
				apiLogger.debug("Picked up entity...")
				Try(s.asJson.asJsObject.convertTo[Login]) match {
					case Success(login) =>
						apiLogger.debug("Json works...")
						onComplete((context.actorFor("/user/user-actor") ? login).mapTo[Option[LoginResponse]]){
							case Success(r) =>
								val res = r match {
									case Some(resp) => 
										respondWithStatus(StatusCodes.OK)
										complete(resp.toJson.toString)
									case None =>
										complete(StatusCodes.NotFound) 
									}
									res
							case Failure(ex) => complete(StatusCodes.InternalServerError)
						}						
						
					case Failure(ex) => complete(StatusCodes.BadRequest) 
				}
			}		
	} ~ complete(StatusCodes.BadRequest) 

	//POST

	def register = (context: ActorContext) => detach(){
		apiLogger.info("New user registration...")
		entity(as[String]){s =>
			Try(s.asJson.asJsObject.convertTo[Register]) match {
				case Success(regData) =>
					onComplete((context.actorFor("/user/user-actor") ? regData).mapTo[Option[LoginResponse]]) {
						case Success(res) =>
							val result = res match {
								case Some(resp) => 
									respondWithStatus(StatusCodes.OK)
									complete(resp.toJson.toString)
								case None => 
									apiLogger.error("POST /user/register failed for email {}", regData)
									complete(StatusCodes.BadRequest)
							}
							result
						case Failure(ex) => 
							apiLogger.error("POST /user/register failed for email %s, reason %s" format (s, ex.toString))
						  complete(StatusCodes.InternalServerError)
					}		
					
				case Failure(ex) => 
					apiLogger.error("POST /user/register failed. Bad JSON.")
					complete(StatusCodes.BadRequest)					
			}
		}
	}

	//def edit // POST

	def edit = (user: String, context: ActorContext) => detach(){
		entity(as[String]){s =>
			Try(s.asJson.asJsObject.convertTo[Account]) match {
				case Success(acc) =>
					onComplete((context.actorFor("/user/user-actor") ? acc.copy(uid = user.toLong)).mapTo[Option[UID]]) {
						case Success(Some(UID(uid))) => complete(StatusCodes.OK)
						case Success(None) => complete(StatusCodes.InternalServerError)
						case Failure(ex) => complete(StatusCodes.InternalServerError)
					}
				case Failure(ex) => 
					apiLogger.error("Edit account bad request {}", s)
					complete(StatusCodes.BadRequest)
			}
		}
	}

	//def resetPassword // GET

	def userData = (uid: String, context: ActorContext) => detach(){
		import org.mojdan.md_backend.model.TBJsonProtocol.UserRowJsonFormat._
		onComplete((context.actorFor("/user/user-actor") ? UID(uid.toLong)).mapTo[Option[UserRow]]) {
			case Success(res) =>
				val result = res match {
					case Some(userRow) => 
						respondWithStatus(StatusCodes.OK)
						complete(userRow.toJson.toString)
					case None => complete(StatusCodes.NotFound)
				}
				result
			case Failure(ex) =>
				apiLogger.error("Getting user data failed")
				complete(StatusCodes.InternalServerError)
		}
	}

	def forgotPass = (context: ActorContext) => detach(){
		entity(as[String]){s =>
			s.asJson.asJsObject.fields.get("email") match {
				case Some(email) =>
					onComplete((context.actorFor("/user/user-actor") ? 
											ForgotPassword(email.convertTo[String])).mapTo[Option[String]]) {
						case Success(res) =>
							res match {
								case Some(otp) => complete(StatusCodes.NoContent)
								case None => complete(StatusCodes.NotFound)
							}
						case Failure(ex) => complete(StatusCodes.InternalServerError)
					}
				case None => complete(StatusCodes.BadRequest)
			}
		}
	}

	def passReset = (context: ActorContext) => detach(){
		entity(as[String]){ s =>
			Try(s.asJson.asJsObject.convertTo[ResetPassword]) match {
				case Success(rp) =>
					onComplete((context.actorFor("/user/user-actor") ? rp).mapTo[Int]) {
						case Success(res) if res == 1 => complete(StatusCodes.NoContent)
						case Success(res) if res != 1 => complete(StatusCodes.NotFound)
						case Failure(ex) => 
							apiLogger.error("POST /user/passreset 500, reason {}", ex)
							complete(StatusCodes.InternalServerError)
					}
				case Failure(ex) => complete(StatusCodes.BadRequest)
			}
		}
	}
}