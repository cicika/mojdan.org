package tb_backend

import scala.slick.driver.PostgresDriver.simple._
//import scala.slick.session.{Session, Database} 
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation


trait Config {
	val dbUrl = "jdbc:postgresql://localhost/ti_biras"
	val dbDriver = "org.postgresql.Driver"
	val dbUser = "cicika"
	val dbPassword = "p4ssw0rd"
	val db = Database.forURL(dbUrl, driver = dbDriver, user = dbUser, password = dbPassword)
}