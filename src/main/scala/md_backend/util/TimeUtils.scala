package org.mojdan.md_backend.util

import java.sql.Timestamp
import java.util.Date
import java.util.Calendar

import org.mojdan.md_backend.model._

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

	def daysDiff(date: MDTime) = {
		val today = timestamp2mdTime(new Timestamp(now))
		if(today.year == date.year) today.dayofyear - date.dayofyear
		else mdTime2Calendar(date).getActualMaximum(Calendar.DAY_OF_YEAR) - date.dayofyear + today.dayofyear
	}

	def inPast(timestamp: Timestamp) = (daysDiff(timestamp) > 0)

	def isToday(date: MDTime): Boolean = (daysDiff(date) == 0)
	def isToday(date: Timestamp): Boolean = isToday(timestamp2mdTime(date))

	//TODO check why these implicits fail in run time
	implicit def timestamp2mdTime(timestamp: Timestamp): MDTime = {
		val calendar = Calendar.getInstance()
		calendar.set(Calendar.YEAR, timestamp.getYear())
		calendar.set(Calendar.MONTH, timestamp.getMonth())
		calendar.set(Calendar.DAY_OF_MONTH, timestamp.getDate())
		MDTime(calendar.get(Calendar.DAY_OF_YEAR), calendar.get(Calendar.YEAR))
	}

	implicit def mdTime2Calendar(date: MDTime): Calendar = {
		val calDate = Calendar.getInstance()
		calDate.set(Calendar.DAY_OF_YEAR, date.dayofyear)
		calDate.set(Calendar.YEAR, date.year)
		calDate
	}
}