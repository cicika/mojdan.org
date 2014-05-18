package tb_backend


import com.typesafe.config.ConfigFactory

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

import java.util.Date


trait Config {
	val config = ConfigFactory.load()
	
	//val dbUrl = "jdbc:postgresql://localhost/ti_biras"
	val dbUrl = "jdbc:postgresql://localhost/%s" format config.getString("db.database")
	val dbDriver = "org.postgresql.Driver"
	//val dbUser = "hackt"
	//val dbPassword = "b4dp4ss"
	val dbUser = config.getString("db.username")
	val dbPassword = config.getString("db.password")
	val db = Database.forURL(dbUrl, driver = dbDriver, user = dbUser, password = dbPassword)
}

trait TimeUtils {
	def now = {
		val currTime = new Date()
		currTime.getTime()
	}

	def currentHour = {
		val currTime = new Date()
		currTime.getHours()
	}
}