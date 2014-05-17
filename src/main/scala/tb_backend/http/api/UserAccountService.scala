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
import spray.json.DefaultJsonProtocol._
import spray.routing._

import spray.routing.{Directives, HttpService}

import tb_backend._
import model._
import model.TBJsonProtocol._
import tb_backend.util._

trait UserAccountService extends HttpService with Config 
																						 with TokenGenerator{

	private implicit val timeout = Timeout(15 seconds)
	private val log = LoggerFactory.getLogger(classOf[UserAccountService])

	import tb_backend.model.Tables

	def login = (context: ActorContext) => detach(){
			log.debug("We are actuallz here....")
			entity(as[String]){s => 
				log.debug("Picked up entity...")
				Try(s.asJson.asJsObject.convertTo[Login]) match {
					case Success(login) =>
						log.debug("Json works...")
						val q = for {
								u <- User if (u.password === login.password && u.username === login.username)
								t <- Auth if (u.uid === t.uid)
							} yield (u.uid, t.token)

						val result = db.withSession { session =>
							q.list()(session)
						}
						log.debug("User login result... {}", result)
						result.headOption match {
							case Some(resp) => 
								val res = LoginResponse(resp._1, resp._2.getOrElse(""))
								respondWithStatus(StatusCodes.OK)
								complete(res.toJson.toString)
							case None =>
								complete(StatusCodes.NotFound) 
							}
						
					case Failure(ex) => complete(StatusCodes.BadRequest) 
				}
			}		
	} ~ complete(StatusCodes.BadRequest) 

	//POST

	def register = (context: ActorContext) => detach(){
		log.info("New user registration...")
		entity(as[String]){s =>
			Try(s.asJson.asJsObject.convertTo[Register]) match {
				case Success(regData) =>
					val accessToken = generateToken
					val userId = db.withSession{ implicit session =>
						val userId = (User returning User.map(_.uid)) += 
							UserRow(-1, regData.email, regData.username, regData.password)
							Connectors += ConnectorsRow(userId, Some(regData.connector), None)
							Auth += AuthRow(userId, Some(accessToken))
						userId
					}
					val response = LoginResponse(userId, accessToken)
					respondWithStatus(StatusCodes.OK)
					complete(response.toJson.toString)
				case Failure(ex) => 
					log.error("Registration failed for email")
					complete(StatusCodes.BadRequest)					
			}
		}
	}

	//def edit // POST

	//def resetPassword // GET

	def userData = (uid: String) => detach(){
		import tb_backend.model.TBJsonProtocol.UserRowJsonFormat._
		val q = for {
			u <- User if (u.uid === uid.toLong)
		} yield (u.uid, u.email, u.username, u.firstname, u.lastname)

		val res = db.withSession{session =>
			q.list()(session)
		}
		res.headOption match {
			case Some(u) => 
				val user = UserRow(u._1, u._2, u._3, "", u._4, u._5)
				respondWithStatus(StatusCodes.OK)
				complete(user.toJson.toString)
			case None => complete(StatusCodes.NotFound)
		}
	}

}