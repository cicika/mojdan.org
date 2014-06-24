package org.mojdan.md_backend.app

import akka.actor._
import akka.event.Logging

import java.sql.Timestamp

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.storage._
import org.mojdan.md_backend.util._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}

class UserActor extends Actor with UserStorage
															with OtpStorage
														  with TokenGenerator
														  with AppConfig
														  with TimeUtils
														  with Mailer
														  with LinkGenerator{
	
	private val log = Logging(context.system, this)
	log.debug("Starting...")  
	import org.mojdan.md_backend.model.Tables	

	def receive = {
		case l: Login =>
			val replyTo = sender
			log.info("Received Login request...")
			Future{ login(l) } onComplete {
				case Success(value) => 
					replyTo ! value.map(e => LoginResponse(None, e._2.get))
				case Failure(ex) => 
					replyTo ! None
					log.error("Failed to login user {}. reason {}", l, ex)
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
		case ForgotPassword(email) =>
			val replyTo = sender
			Future { userData(email) } onComplete {
				case Success(x) => x match {
						case Some(u: UserRow) =>							
							val otp = OtpRow(u.email, generateOtp, new Timestamp(now))
							replyTo ! Some(otp.otp)
							Future {
								deleteOtp(email) 
								insertOtp(otp) 
							}
							Future { 
								Try(sendEmail(Tuple2(email, ""), NO_REPLY, RESET_PW_SUBJECT, 
																 mail.html.resetPassLink(otp.otp).toString, 
																 mail.txtmail.txt.resetPassLink(otp.otp).toString)) match {
									case Success(_) => log.debug("Email to {} sent!", email)
									case Failure(ex) => log.error("Email sending failed to {}, reason {}, stack trace {}", email, ex, ex.printStackTrace)
									} }
						case None => replyTo ! None
					}					
					
				case Failure(ex) =>
					replyTo ! None
					log.error("Failed to get user for email {}", email)  
			}
		case ResetPassword(otp, newPassword) =>
			val replyTo = sender
			for {
				e <- Future { emailForOtp(otp) }
				p <- Future { updatePassword(e.getOrElse(""), newPassword) }
			} yield {
				replyTo ! p

				if(p == 1) Future { deleteOtp(e.getOrElse("")) }
			}

		case Terminated(_) => log.error("terminated...")
		case _ => log.info("Unknown message received....")
	}
}