package org.mojdan.md_backend.storage

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.util._

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

import scala.annotation.tailrec

trait ApplicationStorage extends DBConfig {
	import org.mojdan.md_backend.model.Tables

	def programmeByDay(days: List[Int], all: Boolean = false) = {
		val q = for {
			p <- Programme
		} yield (p.day, p.image, p.sentence, p.activityS, p.activityL, p.activitySB, p.activityLB)

		val progList = db.withSession { session =>
			q.list()(session)
		}.map(e => ProgrammeRow(e._1, e._2, e._3, e._4, e._5, e._6, e._7))

		val res = if(all) progList
							else filterResult(progList, days, List[ProgrammeRow]())
		dbLogger.debug("programmeByDay result %s, for days %s" format (res.toString, days.toString))
		res
	}

	def insertCompleted(c: CompletedRow) = db.withSession {
		implicit session =>
		Completed += c
		dbLogger.info("insertCompleted for user %d, data %s" format (c.uid, c.toString))
	}

	def completedByUid(uid: Long) = {
		val q = for {
			c <- Completed if (c.uid === uid)
		} yield (c.completed, c.active, c.dateStarted)

		val compl = db.withSession { session =>
			q.list()(session)
		}.headOption.map(e => CompletedRow(uid, e._1, e._2, e._3))

		dbLogger.debug("completedByUid for user %d, result %s" format (uid, compl.toString))

		compl
	}

	def updateCompleted(uid: Long, completed: Option[String]) = {
		val q =
			for{
				c <- Completed if(c.uid === uid)
			} yield c.completed
		db.withSession { implicit session =>
			q.update(completed)
			q.updateStatement
			q.updateInvoker
			dbLogger.debug("updateCompleted for user %d, data %s" format (uid, completed.toString))
		}
	}

	def updateActive(uid: Long, active: Int) = {
		val q =
			for{
				c <- Completed if(c.uid === uid)
			} yield c.active
		db.withSession { implicit session =>
			q.update(active)
			q.updateStatement
			q.updateInvoker
			dbLogger.debug("updateActive for user %d, data %d" format (uid, active))
		}
	}

	def insertActivityRow(adr: ActivityDiaryRow) = db.withSession {
		implicit session =>
		val aid = (ActivityDiary returning ActivityDiary.map(_.aid)) += adr
		dbLogger.info("insertActivityRow aid %d, data %s" format (aid, adr.toString))
		aid
	}

	def deleteActivityRow(aid: Long) = db.withSession {
		implicit session =>
			sqlu"delete from activity_diary where aid = $aid".first
			dbLogger.debug("deleteActivityRow aid %d" format aid)
	}

	def deleteUserActivity(uid: Long) = {
		val q = for {
			ad <- ActivityDiary if ad.uid === uid
		} yield ad

		db.withSession { implicit session =>
			val res = q.delete
			dbLogger.debug("deleteUserActivity uid %d, rows %d" format (uid, res))
			q.deleteInvoker
			q.deleteStatement
		}
	}

	def activityRowByUserAndDay(uid: Long, day: Int) = {
		val q =
			for {
				ad <- ActivityDiary if (ad.uid === uid && ad.day === day)
			} yield (ad.aid, ad.startMood)
		val oldData = db.withSession {
			implicit session =>
				q.list()(session)
		}.headOption
		dbLogger.debug("activityRowByUserAndDay for user %d, day %d, data %s" format (uid, day, oldData.toString))
		oldData
	}

	def insertMoodScales(ms: MoodScalesRow) = db.withSession {
		implicit session =>
			val msId = (MoodScales returning MoodScales.map(_.mid)) += ms
			dbLogger.info("insertMoodScales mid %d, data %s" format (msId, ms.toString))
			msId
	}

	def deleteUserScales(uid: Long) = {
		val q = for {
			ms <- MoodScales if ms.uid === uid
		} yield ms

		db.withSession { implicit session =>
			val res = q.delete
			dbLogger.debug("deleteUserScales uid %d, rows %d" format (uid, res))
			q.deleteInvoker
			q.deleteStatement
		}
	}

	def completeScales(uid: Long, records: Int) = {
		val q = for {
			ad <- ActivityDiary if (ad.day <= records && ad.uid === uid)
			ms <- MoodScales if (ms.day === ad.day && ms.uid === ad.uid)
		} yield (ad.aid, ad.uid, ad.day, ad.activity, ad.startMood, ad.expMood, ad.achMood, ad.satisfaction, ad.achievement, ad.note,
						 ms.mid, ms.uid, ms.day, ms.posContacts, ms.negContacts, ms.posActivities, ms.negActivities, ms.posThoughts, ms.negThoughts)

		val res = db.withSession { session =>
			q.list()(session)
		}.map(e =>
			(ActivityDiaryRow(e._1, e._2, e._3, e._4, e._5, e._6, e._7, e._8, e._9, e._10),
			 MoodScalesRow(e._11, e._12, e._13, e._14, e._15, e._16, e._17, e._18, e._19)))
		dbLogger.debug("completeScales for user %d, records %d, data %s" format (uid, records, res.toString))
		res
	}

	@tailrec
	private def filterResult(res: List[ProgrammeRow], filter: List[Int], output: List[ProgrammeRow]): List[ProgrammeRow] = filter.length match {
		case x if x == 0 => output
		case x => filterResult(res, filter.tail, res.filter(e => e.day == filter.head) ++ output)
	}
}
