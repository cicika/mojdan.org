package org.mojdan.md_backend.app

import akka.actor._
import akka.event.Logging

import java.util.Date
import java.util.Calendar

import org.joda.time._
import org.joda.time.format._

/*

class TaskScheduling extends Actor {
	
	private val log = Logging(context.system, this)
	private val config = context.system.settings.config

	val df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ")

	val morningNotificationTime = config.getInt("app.morning-push-hour")
	val eveningNotificationTime = config.getInt("app.evening-push-hour")
	val programmLenght = config.getInt("app.programme-length")

	def receive = {
		case GenerateFullProgrammeTasks(user: String) =>

		case Terminated(_) =>
		case _ =>
	}

	private def tasks = {
		val currTime = new Date()
		val year = currTime.getYear()
		val month = currTime.getMonth()
		val day = currTime.getDay()
		val nextMsgTime = currTime

	}
}
*/
