package org.mojdan.md_backend.http

import akka.actor.{Actor, ActorRef, ActorContext}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}

import spray.http.{StatusCodes, HttpMethods}
import spray.routing.{Directives, HttpService}

import org.mojdan.md_backend.http.api._
import org.mojdan.md_backend.http.www._
import org.mojdan.md_backend.http.auth._

trait LoginService extends HttpService with UserAccountService{
	def route(ctxx: ActorContext) = {
		method(HttpMethods.GET) {
			path("passreset" / Segment){s =>
				passResetForm(ctxx, s)
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
																			 with UserAuthentication{

	def tbApi(user: String, ctxx: ActorContext) = {
		//authenticate(tbAuthenticator) { user => 
			method(HttpMethods.GET){
				path("programme" / Segment){ s =>
					val res = s match {
						case "start" => startProgramme(user)
						case "restart" => restartProgramme(user)
						case "completed" => completedDays(user)
						case "all" => programme(user)
						case "active" => dailyProgramme(user)
					}
						res
				} ~
				pathPrefix("me"){
						userData(user, ctxx)
				} ~
				pathPrefix("scales"){
					scales(user)
				} 
		//}
	}	~ 
		method(HttpMethods.POST){
			pathPrefix("activity"){
				path("complete"){
					postActivity(user)
				} ~
				path("start"){
					postStartMood(user)	
				}
			} ~
			pathPrefix("me"){
				edit(user, ctxx)
			} ~
			pathPrefix("scales"){
				postExperiences(user)
			}/* ~
					path(Segment){d =>
						complete(StatusCodes.OK)
					}*/
				//}			
		}~
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

