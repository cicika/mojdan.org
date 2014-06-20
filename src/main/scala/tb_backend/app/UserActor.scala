package org.mojdan.md_backend.app

import akka.actor._
import akka.event.Logging

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.storage._
import org.mojdan.md_backend.util._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

class UserActor extends Actor with UserStorage
														  with TokenGenerator{
	
	private val log = Logging(context.system, this)
	log.debug("Starting...")  

	def receive = {
		case l: Login =>
			val replyTo = sender
			log.info("Received Login request...")
			Future{ login(l) } onComplete {
				case Success(value) => 
					replyTo ! value.map(e => LoginResponse(None, e._2.get))
				case Failure(ex) => 
					replyTo ! None
					log.error("Failed to login user {}", l)
			}
		case regData: Register =>
			val accessToken = generateToken
			val replyTo = sender
			Future { register(accessToken, regData) } onComplete {
				case Success(userId) => replyTo ! Some(LoginResponse(None, accessToken))
				case Failure(ex) => 
					replyTo ! None
					log.error("Failed to register new user with {}, with reason {}", regData, ex)
			}

		case UID(uid) =>
			val replyTo = sender
			Future{ userData(uid) } onComplete {
				case Success(res) => replyTo ! res
				case Failure(ex) => 
					replyTo ! None
					log.error("Failed to get user data for uid {}, with reason", uid, ex)
				}
		case accData: Account =>
			val replyTo = sender
			Future{ update(Account.asMap(accData)) } onComplete {
				case Success(userId) => replyTo ! Some(userId)
				case Failure(ex) => 
					replyTo ! None
					log.error("Failed to update account for userId {}", accData.uid)
			}
		case Terminated(_) => log.error("terminated...")
		case _ => log.info("Unknown message received....")
	}
}