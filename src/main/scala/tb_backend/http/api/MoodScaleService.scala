package org.mojdan.md_backend.http.api

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

import org.mojdan.md_backend._
import model._
import model.TBJsonProtocol._
import org.mojdan.md_backend.util._

trait MoodScaleService extends HttpService with Config{

	import org.mojdan.md_backend.model.Tables

	def postExperiences = (user: String) => {
		entity(as[String]){s =>
			System.out.println(s)
			Try(s.asJson.asJsObject.convertTo[MoodScalesRow]) match {
				case Success(ms) => 
					db.withSession{ implicit session =>
						MoodScales += ms.copy(uid = user.toLong)
					}
					complete(StatusCodes.OK)
				case Failure(ex) => 
					System.out.println(ex)
					complete(StatusCodes.BadRequest)
			}
		}
	}

	def scales = (user: String) => {
		val q = for {
			c <- Completed if(c.uid === user.toLong)
		} yield c.active

		val days = db.withSession{session =>
			q.list()(session)
		}.headOption

		val records = days match {
			case Some(x) if x < 8 => 0
			case Some(x) if (x >= 8 && x < 15) => 7
			case Some(x) if (x >= 15 && x < 22) => 14
			case Some(x) if (x >= 22 && x < 28) => 21
			case Some(x) if x >= 28 => 28
			case None => 0
		}

		val result = records match {
			case x if x <= 0 => complete(StatusCodes.NoContent)
			case _ =>
				val q1 = for {
					ad <- ActivityDiary if(ad.day <= records && ad.uid === user.toLong)
					ms <- MoodScales if(ms.day <= records && ms.uid === user.toLong)
				} yield (ad.aid, ad.uid, ad.day, ad.activity, ad.startMood, ad.expMood, ad.achMood, ad.satisfaction, ad.achievement, ad.note,
						 ms.mid, ms.uid, ms.day, ms.posContacts, ms.negContacts, ms.posActivities, ms.negActivities, ms.posThoughts, ms.negThoughts)

				val res = db.withSession{session =>
					q1.list()(session)
				}.map(e => 
					(ActivityDiaryRow(e._1, e._2, e._3, e._4, e._5, e._6, e._7, e._8, e._9, e._10), 
					 MoodScalesRow(e._11, e._12, e._13, e._14, e._15, e._16, e._17, e._18, e._19))).sortBy(e => e._1.day)

				respondWithStatus(StatusCodes.OK)
				complete(res.toJson.toString)
		}
		result
	}
}