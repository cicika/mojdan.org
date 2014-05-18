package tb_backend.http.api

import akka.actor._
import akka.pattern._
import akka.util.Timeout

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

import org.slf4j.LoggerFactory

import spray.http.StatusCodes

import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling.BasicUnmarshallers._
import spray.json._
import spray.routing._

import spray.routing.{Directives, HttpService}

import tb_backend._
import model._
import model.TBJsonProtocol._
import tb_backend.util._

trait MoodScaleService extends HttpService with Config{

	import tb_backend.model.Tables

	def postExperiences = (user: String) => post{ctx =>
		entity(as[String]){s =>
			Try(s.asJson.asJsObject.convertTo[MoodScalesRow]) match {
				case Success(ms) => 
					db.withSession{ implicit session =>
						MoodScales += ms
					}
					complete(StatusCodes.OK)
				case Failure(ex) => complete(StatusCodes.BadRequest)
			}
		}
	}

	def scales = (user: String) => post{ctx =>
		val q = for {
			c <- Completed if(c.uid === user.toLong)
		} yield c.active

		val days = db.withSession{session =>
			q.list()(session)
		}.head

		val records = days match {
			case Some(x) if x < 8 => 0
			case Some(x) if (x >= 8 && x < 15) => 7
			case Some(x) if (x >= 15 && x < 22) => 14
			case Some(x) if (x >= 22 && x < 29) => 21
			case Some(x) if x >= 29 => 28
			case None => 0
		}

		val q1 = for {
			ad <- ActivityDiary if(ad.day <= records && ad.uid === user.toLong)
		} yield (ad.aid, ad.uid, ad.day, ad.activity, ad.startMood, ad.expMood, ad.achMood, ad.satisfaction, ad.achievement, ad.note)

	}
}