package org.mojdan.md_backend.model

import java.util.Calendar

//User Actor commands
case class Login(username: String, password: String)
case class LoginResponse(uid: Option[Long], token: String)
case class Register(username: String, password: String, email: String, connector: Option[String])
case class UID(id: Long)
case class Account(uid: Long, username: Option[String], password: Option[String], email: Option[String], connector: Option[String],
                   firstname: Option[String], lastname: Option[String])

object Account {
  def asMap(acc: Account):Map[String, Any] = 
    Map("uid" -> Some(acc.uid),
        "username" -> acc.username,
        "password" -> acc.password,
        "connector" -> acc.connector,
        "email" -> acc.email,
        "firstname" -> acc.firstname,
        "lastname" -> acc.lastname).
        filter(e => e._2 != None).
        map(e => (e._1 -> e._2.get))
}

case class GenerateFullProgrammeTasks(user: String)
case class MDTime(dayofyear: Int, year: Int)

//Application Actor commands
// programme

case class StartProgramme(uid: Long)
case class RestartProgramme(uid: Long)
case class ProgrammeForUser(uid: Long)
case class DailyProgrammeForUser(uid: Long)
case class CompletedDaysForUser(uid: Long)

//mood scales 
case class StartMood(uid: Long, startMood: Int)
case class MoodScalesForUser(uid: Long)
case class ActivityHistoryForUser(uid: Long)
case class Done()