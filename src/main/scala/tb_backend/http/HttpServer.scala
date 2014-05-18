package tb_backend.http

import akka.actor.{Actor, ActorRef, ActorContext}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}

import spray.http.{StatusCodes, HttpMethods}
import spray.routing.{Directives, HttpService}

import tb_backend.http.api._
import tb_backend.http.www._
import tb_backend.http.auth._

trait LoginService extends HttpService with UserAccountService{
	def route(ctxx: ActorContext) = {
		method(HttpMethods.POST) {
			pathPrefix("user"){
				pathPrefix("login"){
					login(ctxx)
				} ~ 
				pathPrefix("register"){
					register(ctxx)
				}
			} //~ complete(StatusCodes.NotFound) 
		}
	} 
}
trait TBApiService extends HttpService with UserAccountService
																			 with ProgrammeService
																			 with ActivityDiaryService
																			 with MoodScaleService
																			 with UserAuthentication{

	def tbApi(user: String) = {
		//authenticate(tbAuthenticator) { user => 
			method(HttpMethods.GET){
				pathPrefix("me"){
						userData(user)
				} ~
				pathPrefix("scales"){
					scales(user)
				} ~
				pathPrefix("activity"){
					pathPrefix("all"){
						activityHistory(user)
					}
				} ~
				pathPrefix("programme"){
					path("start"){
						startProgramme(user)
					} ~
					path("completed"){
						completedDays(user)
					} ~
					path("all"){
						programme(user)
					} ~
					path("active"){
						dailyProgramme(user)
					}
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

	def receive = runRoute(authenticate(tbAuthenticator) {user => tbApi(user)})
}

class LoginServiceActor extends Actor with LoginService {
	def actorRefFactory = context
	def receive = runRoute(route(context))
}

