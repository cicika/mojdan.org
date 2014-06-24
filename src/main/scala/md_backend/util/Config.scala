package org.mojdan.md_backend.util

import akka.util.Timeout

import com.typesafe.config.ConfigFactory

import org.slf4j.LoggerFactory

import scala.concurrent.duration._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

sealed trait Config {
	val config = ConfigFactory.load()	
}
trait AppConfig extends Config{		

	val hostname = config.getString("mail.hostname")
	val username = config.getString("mail.username")
	val password = config.getString("mail.password")
	val infoAddress = config.getString("mail.info")
	val noReplyAddress = config.getString("mail.no-reply")
	val adminAddress = config.getString("mail.admin")

	implicit val timeout = Timeout(15 seconds)

	val apiLogger = LoggerFactory.getLogger(classOf[AppConfig])
}

trait DBConfig extends Config{
	val dbUrl = "jdbc:postgresql://localhost/%s" format config.getString("db.database")
	val dbDriver = "org.postgresql.Driver"
	val dbUser = config.getString("db.username")
	val dbPassword = config.getString("db.password")
	val db = Database.forURL(dbUrl, driver = dbDriver, user = dbUser, password = dbPassword)

	val dbLogger = LoggerFactory.getLogger(classOf[DBConfig])

	val cryptoKey = config.getString("crypto-key")

	val otpExpiry = config.getInt("expiry.otp")*60*60*1000l
	val tokenExpiry = config.getInt("expiry.token")*60*60*1000l
	val refreshTokenExpiry = config.getInt("expiry.refresh-token")*60*60*1000l
}