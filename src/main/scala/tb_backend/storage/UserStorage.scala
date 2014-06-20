package org.mojdan.md_backend.storage

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.util._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation


trait UserStorage extends Config{

	import org.mojdan.md_backend.model.Tables

	def login(l: Login):Option[Tuple2[Long, Option[String]]] = {
		val q = for {
							u <- User if (u.password === l.password && u.email === l.username)
							t <- Auth if (u.uid === t.uid)
						} yield (u.uid, t.token)

		val result = db.withSession { session =>
			q.list()(session)
		}
		result.headOption
	}

	def register(accessToken: String, regData: Register):Long = db.withSession{ implicit session =>
		val userId = (User returning User.map(_.uid)) += 
		UserRow(-1, regData.email, regData.username, regData.password)
		//Connectors += ConnectorsRow(userId, Some(regData.connector), None)
		Auth += AuthRow(userId, Some(accessToken))
		userId
	}

	def checkToken(accessToken: String):Option[String] = {
		val q = for {
        	t <- Auth if (t.token === accessToken)
    } yield t.uid

    val result = db.withSession{session =>
      q.list()(session)	
    }	
    result.headOption.map(e => e.toString)
	}

	def userData(uid: Long): Option[UserRow] = {
		val q = for {
			u <- User if (u.uid === uid.toLong)
		} yield (u.uid, u.email, u.username, u.firstname, u.lastname)

		val res = db.withSession{session =>
			q.list()(session)
		}
		res.headOption.map(u => UserRow(u._1, u._2, u._3, "", u._4, u._5))
	}

	def update(data: Map[String, Any]) = {
		import scala.slick.driver.JdbcDriver.backend.Database
		import Database.dynamicSession

		val query = "UPDATE user %s SET %s WHERE uid=%d" format ( 
			fields(data.tail.map(e => (e._1.toString, e._2.toString)), ""), 
			values(data.tail.map(e => (e._1.toString, e._2.toString)), ""), data("uid").asInstanceOf[Long])

		//val q = withDynSession{
		//	Q.update(query).execute
		//}
		data("uid").asInstanceOf[Long]

	}

	private def fields(data: Map[String, String], output: String):String = data.size match {
		case x if x == 0 => output.dropRight(2) + ")"
		case x if x == 1 => fields(data.tail, "(" + data.head._1 + ", ")
		case x => fields(data.tail, output + data.head._1 + ", ")
	}

	private def values(data: Map[String, String], output: String):String = data.size match {
		case x if x == 0 => output.dropRight(2) + ")"
		case x if x == 1 => fields(data.tail, "('" + data.head._2 + "', ")
		case x => fields(data.tail, output + "'" + data.head._2 + "', ")
	}
	
}