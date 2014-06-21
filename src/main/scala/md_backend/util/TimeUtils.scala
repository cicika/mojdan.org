package org.mojdan.md_backend.util

import java.sql.Timestamp
import java.util.Date
import java.util.Calendar

trait TimeUtils {
	def now = {
		val currTime = new Date()
		currTime.getTime()
	}

	def currentHour = {
		val currTime = new Date()
		currTime.getHours()
	}

	def weekdayNow = {
		val currTime = Calendar.getInstance()
		currTime.get(Calendar.DAY_OF_WEEK)
	}

	def weekday(timestamp: Timestamp) = {
		val calendar = Calendar.getInstance()
		calendar.set(Calendar.MILLISECOND, timestamp.getTime().toInt)
		calendar.get(Calendar.DAY_OF_WEEK)
	}

	def weekdayWithDelta(timestamp: Timestamp, delta: Int) = {
		val calendar = Calendar.getInstance()
		calendar.set(Calendar.MILLISECOND, timestamp.getTime().toInt)
		calendar.roll(Calendar.DAY_OF_WEEK, delta)
		calendar.get(Calendar.DAY_OF_WEEK)
	}
}