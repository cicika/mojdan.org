package tb_backend.model

import java.util.Date

//import org.joda.time._
//import org.joda.time.format._

import scala.util.{Try, Success, Failure}

import spray.json._
import spray.json.DefaultJsonProtocol._

object TBJsonProtocol extends DefaultJsonProtocol {

	//implicit val login = jsonFormat2(Login)
	//implicit val register = jsonFormat4(Register)
	implicit val loginResponse = jsonFormat2(LoginResponse)

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
      "connector" -> JsString(r.connector)
      )
    def read(value: JsValue) = value.asJsObject.fields match {
      case r: Map[String, JsValue] => Register(r("username").convertTo[String], r("password").convertTo[String],
                                               r("email").convertTo[String], r("connector").convertTo[String])
      case _ => throw new DeserializationException("Register expected...")
    }
  }
 

}