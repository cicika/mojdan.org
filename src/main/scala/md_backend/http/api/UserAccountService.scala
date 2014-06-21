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
import spray.json.DefaultJsonProtocol._
import spray.routing._

import spray.routing.{Directives, HttpService}

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.util._

trait UserAccountService extends HttpService with Config 
																						 with TokenGenerator{

	private val log = LoggerFactory.getLogger(classOf[UserAccountService])

	import org.mojdan.md_backend.model.Tables

	def login = (context: ActorContext) => detach(){
			log.debug("We are actuallz here....")
			entity(as[String]){s => 
				log.debug("Picked up entity...")
				Try(s.asJson.asJsObject.convertTo[Login]) match {
					case Success(login) =>
						log.debug("Json works...")
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
		log.info("New user registration...")
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
									log.error("Registration failed for email {}", regData)
									complete(StatusCodes.BadRequest)
							}
							result
						case Failure(ex) => 
							log.error("Registration failed for email {}", regData)
						  complete(StatusCodes.BadRequest)
					}		
					
				case Failure(ex) => 
					log.error("Registration failed. Bad JSON.")
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
					log.error("Edit account bad request {}", s)
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
				log.error("Getting user data failed")
				complete(StatusCodes.InternalServerError)
		}
	}

	def forgotPass = (context: ActorContext) => {
		complete(StatusCodes.NotImplemented)
	}

	def passReset = (context: ActorContext) => {
		complete(StatusCodes.NotImplemented)
	}

	def passResetForm = (context: ActorContext, otp: String) => {
		complete(StatusCodes.NotImplemented)
	}

}