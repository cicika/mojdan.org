package org.mojdan.md_backend.model

import java.util.Date

//import org.joda.time._
//import org.joda.time.format._

import scala.util.{Try, Success, Failure}

import spray.json._
import spray.json.DefaultJsonProtocol._

object TBJsonProtocol extends DefaultJsonProtocol with Tables{

	//implicit val login = jsonFormat2(Login)
	//implicit val register = jsonFormat4(Register)
	implicit val loginResponse = jsonFormat2(LoginResponse)

  implicit object AccountJsonProtcol extends RootJsonFormat[Account]{
    def write(a: Account) = JsObject(
      "uid" -> JsNumber(a.uid),
      "username" -> JsString(a.username.getOrElse("")),
      "email" -> JsString(a.email.getOrElse("")),
      "password" -> JsString(a.password.getOrElse("")),
      "connector" -> JsString(a.connector.getOrElse("")),
      "firstname" -> JsString(a.firstname.getOrElse("")),
      "lastname" -> JsString(a.lastname.getOrElse(""))
    )

    def read(value: JsValue) = value.asJsObject.fields match {
      case m: Map[String, JsValue] =>
        Account(m.get("uid").map(e => e.convertTo[Long]).getOrElse(-1l), m.get("username").map(e => e.convertTo[String]),
                m.get("password").map(e => e.convertTo[String]), m.get("email").map(e => e.convertTo[String]),
                m.get("connector").map(e => e.convertTo[String]), m.get("firstname").map(e => e.convertTo[String]),
                m.get("lastname").map(e => e.convertTo[String]))
        case _ => throw new DeserializationException("Account expected")
    }
  }

	implicit object LoginJsonFormat extends RootJsonFormat[Login]{
    def write(t: Login) = JsObject(
      "username" -> JsString(t.username),
      "password" -> JsString(t.password)
    )
    def read(value: JsValue) = 
      value.asJsObject.fields match {
        case t: Map[String, JsValue] => Login(t("username").convertTo[String], t("password").convertTo[String])
        case _ => throw new DeserializationException("Login extepected")
      }
  } 

  implicit object RegisterJsonFormat extends RootJsonFormat[Register]{
    def write(r: Register) = JsObject(
      "username" -> JsString(r.username),
      "password" -> JsString(r.password),
      "email" -> JsString(r.email),
      "connector" -> JsString(r.connector.getOrElse(""))
      )
    def read(value: JsValue) = value.asJsObject.fields match {
      case r: Map[String, JsValue] => Register(r("username").convertTo[String], r("password").convertTo[String],
                                               r("email").convertTo[String], r.get("connector").map(e => e.convertTo[String]))
      case _ => throw new DeserializationException("Register expected...")
    }
  }

  implicit object UserRowJsonFormat extends RootJsonFormat[UserRow] {
    def write(u: UserRow) = JsObject(
      "uid" -> JsNumber(u.uid),
      "username" -> JsString(u.username),
      "email" -> JsString(u.email),
      "firstname" -> JsString(u.firstname.getOrElse("")),
      "lastname" -> JsString(u.lastname.getOrElse(""))
      )

    def read(value: JsValue) = value.asJsObject.fields match {
      case u: Map[String, JsValue] =>
        UserRow(u("uid").convertTo[Int], u("email").convertTo[String], u("username").convertTo[String], "", 
          u.get("firstname").map(e => e.convertTo[String]), u.get("lastname").map(e => e.convertTo[String]))
      case _ => throw new DeserializationException("UserRow expected")
    }
  }

  implicit object ProgrammeRowJsonProtocol extends RootJsonFormat[ProgrammeRow] {
    def write(p: ProgrammeRow) = JsObject(
      "day" -> JsNumber(p.day),
      "image" -> JsString(p.image.getOrElse("")),
      "sentence" -> JsString(p.sentence.getOrElse("")),
      "activity_s" -> JsString(p.activityS.getOrElse("")),
      "activity_l" -> JsString(p.activityL.getOrElse("")),
      "activity_s_b" -> JsString(p.activitySB.getOrElse("")),
      "activity_l_b" -> JsString(p.activityLB.getOrElse(""))
    )

    def read(value: JsValue) = value.asJsObject.fields match {
      case p: Map[String, JsValue] =>
        ProgrammeRow(p("day").convertTo[Int], p.get("image").map(e => e.convertTo[String]), 
                     p.get("sentence").map(e => e.convertTo[String]), p.get("activity_s").map(e => e.convertTo[String]),
                     p.get("activity_l").map(e => e.convertTo[String]),
                     p.get("activity_s_b").map(e => e.convertTo[String]),
                     p.get("activity_l_b").map(e => e.convertTo[String]))
      case _ => throw new DeserializationException("ProgrammeRow expected...")
    }
  }

  implicit object CompletedRowJsonProtocol extends RootJsonFormat[CompletedRow]{
    def write(c: CompletedRow) = JsObject(
      "completed" -> JsString(c.completed.getOrElse("")),
      "active" -> JsNumber(c.active)
    )

    def read(value: JsValue) = value.asJsObject.fields match {
      case c: Map[String, JsValue] =>
        CompletedRow(c("uid").convertTo[Long], c.get("completed").map(e => e.convertTo[String]),
                     c("active").convertTo[Int], None)
      case _ => throw new DeserializationException("Completed expected")
    }
  }
 
  implicit object ActivityDiaryRowJsonProtocol extends RootJsonFormat[ActivityDiaryRow]{
    def write(a: ActivityDiaryRow) = JsObject(
      "aid" -> JsNumber(a.aid),
      "uid" -> JsNumber(a.uid),
      "day" -> JsNumber(a.day),
      "activity" -> JsString(a.activity.getOrElse("")),
      "start_mood" -> JsNumber(a.startMood.getOrElse(-1)),
      "exp_mood" -> JsNumber(a.expMood.getOrElse(-1)),
      "ach_mood" -> JsNumber(a.achMood.getOrElse(-1)),
      "satisfaction" -> JsNumber(a.satisfaction.getOrElse(-1)),
      "achievement" -> JsNumber(a.achievement.getOrElse(-1)),
      "note" -> JsString(a.note.getOrElse(""))
    )

    def read(value: JsValue) = value.asJsObject.fields match {
      case t: Map[String, JsValue] =>
        ActivityDiaryRow(t.get("aid").map(e => e.convertTo[Long]).getOrElse(-1l), t.get("uid").map(e => e.convertTo[Long]).getOrElse(-1l), t("day").convertTo[Int],
          t.get("activity").map(e => e.convertTo[String]), t.get("start_mood").map(e => e.convertTo[Int]), 
          t.get("exp_mood").map(e => e.convertTo[Int]), t.get("ach_mood").map(e => e.convertTo[Int]), 
          t.get("satisfaction").map(e => e.convertTo[Int]), t.get("achievement").map(e => e.convertTo[Int]),
          t.get("note").map(e => e.convertTo[String]))
      case _ => throw new DeserializationException("ActivityDiaryRow expected")
    }
  }
  implicit object MoodScalesRowJsonProtocol extends RootJsonFormat[MoodScalesRow] {
    def write(m: MoodScalesRow) = JsObject(
        "mid" -> JsNumber(m.mid),
        "uid" -> JsNumber(m.uid),
        "day" -> JsNumber(m.day),
        "pos_contacts" -> JsNumber(m.posContacts.getOrElse(0)),
        "neg_contacts" -> JsNumber(m.negContacts.getOrElse(0)),
        "pos_activities" -> JsNumber(m.posActivities.getOrElse(0)),
        "neg_activities" -> JsNumber(m.negActivities.getOrElse(0)),
        "pos_thoughts" -> JsNumber(m.posThoughts.getOrElse(0)),
        "neg_thoughts" -> JsNumber(m.negThoughts.getOrElse(0))
      )

    def read(value: JsValue) = value.asJsObject.fields match {
      case m: Map[String, JsValue] => 
        MoodScalesRow(m.get("mid").map(e => e.convertTo[Long]).getOrElse(-1l), m.get("uid").map(e => e.convertTo[Long]).getOrElse(-1l), m("day").convertTo[Int], m.get("pos_contacts").map(e => e.convertTo[Int]), m.get("neg_contacts").map(e => e.convertTo[Int]),
                      m.get("pos_activities").map(e => e.convertTo[Int]), m.get("neg_activities").map(e => e.convertTo[Int]),
                      m.get("pos_thoughts").map(e => e.convertTo[Int]), m.get("neg_thoughts").map(e => e.convertTo[Int]))
      case _ => throw new DeserializationException("MoodScales expected") 
    }
  }

}
