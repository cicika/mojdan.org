package org.mojdan.md_backend.util

import akka.util.Timeout

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

trait Config {
	val config = ConfigFactory.load()	
	
	val dbUrl = "jdbc:postgresql://localhost/%s" format config.getString("db.database")
	val dbDriver = "org.postgresql.Driver"
	val dbUser = config.getString("db.username")
	val dbPassword = config.getString("db.password")
	val db = Database.forURL(dbUrl, driver = dbDriver, user = dbUser, password = dbPassword)

	val hostname = config.getString("mail.hostname")
	val username = config.getString("mail.username")
	val password = config.getString("mail.password")
	val infoAddress = config.getString("mail.info")
	val noReplyAddress = config.getString("mail.no-reply")
	val adminAddress = config.getString("mail.admin")

	implicit val timeout = Timeout(10 seconds)
}