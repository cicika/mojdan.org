package tb_backend

import scala.slick.driver.PostgresDriver.simple._
//import scala.slick.session.{Session, Database} 
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation


trait Config {
	val dbUrl = "jdbc:postgresql://localhost/ti_biras"
	//val dbUrl = "jdbc:postgresql://localhost/ti_biras_7381"
	val dbDriver = "org.postgresql.Driver"
	val dbUser = "cicika"
	val dbPassword = "p4ssw0rd"
	//val dbUser = "ti_biras"
	//val dbPassword = "eN4_l38gJ#E7(?jk5"
	val db = Database.forURL(dbUrl, driver = dbDriver, user = dbUser, password = dbPassword)
}