package org.mojdan.md_backend.storage

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.util._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

trait OtpStorage extends DBConfig with TokenGenerator {

	def insertOtp(otp: OtpRow) = db.withSession{ implicit session =>
		Otp += otp
		dbLogger.debug("insertOtp %s" format otp.toString)
		Done()
	}

	def deleteOtp(timestamp: Long) = db.withSession{ implicit session =>
		sqlu"delete from otps where $timestamp - created >= $otpExpiry"
		dbLogger.info("deleteOtp older than %d" format timestamp)
	}

	def deleteOtp(otp: String) = db.withSession{ implicit session =>
		sqlu"delete from otps where otp = $otp".first
		dbLogger.info("deleteOtp %s" format otp)
	}

	def emailForOtp(otp: String) = {
		val q = for {
			o <- Otp if o.otp === otp
		} yield o.email

		val res = db.withSession{session =>
			q.list()(session)
		}.headOption

		dbLogger.debug("emailForOtp %s, result %s" format(otp, res.toString))
		
		res
	}

}