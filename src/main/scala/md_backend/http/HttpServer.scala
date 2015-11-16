package org.mojdan.md_backend.http

import akka.actor.{Actor, ActorRef, ActorContext}
import akka.pattern.ask
import akka.util.Timeout

import java.io.File

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}

import spray.http.{StatusCodes, HttpMethods}
import spray.routing.{Directives, HttpService}

import org.mojdan.md_backend.http.api._
import org.mojdan.md_backend.http.www._
import org.mojdan.md_backend.http.auth._

trait LoginService extends HttpService with UserAccountService
																       with IndexService
																		   with PageService {
	def route(ctxx: ActorContext) = {
		method(HttpMethods.GET) {
			path("page" / Segment){ pageId =>
				page(pageId, ctxx)
			} ~
			path("www" / Segment / Segment){(arg1, fileName) =>
				getFromFile(new File("www/%s/%s" format (arg1, fileName)))
			} ~
			pathSingleSlash{
				index(ctxx)
			}
		} ~
		method(HttpMethods.POST) {
			pathPrefix("user"){
				pathPrefix("login"){
					login(ctxx)
				} ~
				pathPrefix("register"){
					register(ctxx)
				} ~
				pathPrefix("passreset"){
					passReset(ctxx)
				} ~
				pathPrefix("forgotpass"){
					forgotPass(ctxx)
				}
			}
		}
	}
}

trait TBApiService extends HttpService with UserAccountService
																			 with ProgrammeService
																			 with ActivityDiaryService
																			 with MoodScaleService
																			 with UserAuthentication
																			 with IndexService
																		 	 with PageService {

	def tbApi(user: String, ctxx: ActorContext) = {
		method(HttpMethods.GET){
			path("programme" / Segment){ s =>
				val res = s match {
					case "start" => startProgramme(user, ctxx)
					case "restart" => restartProgramme(user, ctxx)
					case "completed" => completedDays(user, ctxx)
					case "all" => programme(user, ctxx)
					case "active" => dailyProgramme(user, ctxx)
				}
					res
			} ~
			pathPrefix("me"){
					userData(user, ctxx)
			} ~
			pathPrefix("scales"){
				scales(user, ctxx)
			}
		}	~
		method(HttpMethods.POST){
			path("page" / Segment){pageId =>
				editPage(pageId, ctxx)
			} ~
			pathPrefix("activity"){
				path("complete"){
					postActivity(user, ctxx)
				} ~
				path("start"){
					postStartMood(user, ctxx)
				}
			} ~
			pathPrefix("me"){
				edit(user, ctxx)
			} ~
			pathPrefix("scales"){
				postExperiences(user, ctxx)
			}
		} ~
			complete(StatusCodes.NotFound)
	}
}

class TBApiServiceActor extends Actor with TBApiService {
	def actorRefFactory = context
	def receive = runRoute(authenticate(tbAuthenticator) {user => tbApi(user, context)})
}

class LoginServiceActor extends Actor with LoginService {
	def actorRefFactory = context
	def receive = runRoute(route(context))
}
