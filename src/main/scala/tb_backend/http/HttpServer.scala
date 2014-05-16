package tb_backend.http

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}

import spray.http.{StatusCodes, HttpMethods}
import spray.routing.{Directives, HttpService}

import tb_backend.http.api._
import tb_backend.http.www._

trait TBApiService extends HttpService with UserAccountService{
	def tbApi = {
		method(HttpMethods.POST) {
			pathPrefix("user"){
				pathPrefix("login"){
					login
				} //~ 
				//pathPrefix("edit"){
					//edit
				//}
			}
		}
		
	}	~ complete(StatusCodes.NotFound)		
}
	


class TBApiServiceActor extends Actor with TBApiService {
	def actorRefFactory = context

	def receive = runRoute(tbApi)
}