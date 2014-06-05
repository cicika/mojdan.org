package org.mojdan.md_backend.http.auth

import akka.actor._
import akka.actor.ActorSystem
import akka.pattern._
import akka.util._

import com.typesafe.config.ConfigFactory

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Success, Failure}

import spray.http.{GenericHttpCredentials, HttpCredentials, HttpRequest, HttpHeaders, HttpChallenge}
import spray.routing.authentication._
import spray.routing.{RequestContext, AuthenticationFailedRejection}

import org.mojdan.md_backend._
import model._

trait TBApiAuthenticator extends HttpAuthenticator[String]{
   implicit val actorSystem = ActorSystem()
   def params(ctx: RequestContext) = Map.empty
   implicit def executionContext = actorSystem.dispatcher
   def scheme: String
   def realm: String
   def params: Map[String, String] = Map.empty
   def getChallengeHeaders(httpRequest: HttpRequest) = List(HttpHeaders.`WWW-Authenticate`(HttpChallenge(scheme, realm, params)))

}

class TBAuthAuthenticator extends TBApiAuthenticator with Tables with Config{
  def realm = "TiBiras Auth Realm"
  def scheme = "TBAuth"
  implicit val timeout = Timeout(15 seconds)
  def authenticate(credentials: Option[HttpCredentials], ctx: RequestContext) = credentials match {
    case Some(creds) => 
      if(creds.toString.startsWith(scheme)) { 
        val accessToken = creds.toString.drop(scheme.length + 1)  
        val q = for{
        	t <- Auth if (t.token === accessToken)
        } yield t.uid

        val result = db.withSession{session =>
        	q.list()(session)	
        }	
        result.headOption match {
        	case Some(uuid) => Future { Some(uuid.toString) }
        	case None => Future { None }
        }
      }
      else Future { None }
    case None => Future { None }
    }
}

trait UserAuthentication{

	val tbAuthenticator = new TBAuthAuthenticator

}

