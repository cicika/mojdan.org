package org.mojdan.md_backend.app

import akka.actor._
import akka.event.Logging

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.storage._
import org.mojdan.md_backend.util._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class MaintenanceActor extends Actor with AppConfig
																		 with OtpStorage
																		 with UserStorage
																		 with TimeUtils {

	private val log = Logging(context.system, this)

	def receive = {
		case InvalidateOTPs => deleteOtp(now)
		case InvalidateAuthTokens =>
		case InvalidateRefreshTokens =>
		case Terminated(_) => 
		case _ =>
	}

	override def preStart() = {
		if(pwHashingEnabled) hashAllPasswords
		context.system.scheduler.schedule(120 seconds, 3600 seconds, self, InvalidateOTPs())
		context.system.scheduler.schedule(240 seconds, 3600 seconds, self, InvalidateAuthTokens())
		context.system.scheduler.schedule(360 seconds, 7200 seconds, self, InvalidateRefreshTokens())
	}
}
