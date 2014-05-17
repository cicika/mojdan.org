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

trait TBApiService extends HttpService with UserAccountService
																			 with UserAuthentication{

	def loginReg(ctx: ActorContext) = {
		method(HttpMethods.POST) {
			pathPrefix("user"){
				pathPrefix("login"){
					login(ctx)
				} ~ 
				pathPrefix("register"){
					register(ctx)
				}
			}
		}
	} 
	def tbApi = {
		authenticate(tbAuthenticator) { user => 
			method(HttpMethods.GET){
				pathPrefix("user"){
					pathPrefix("data"){
						userData(user)
					}
				}
			}			
		}
	}	~ complete(StatusCodes.NotFound)		
}
	


class TBApiServiceActor extends Actor with TBApiService {
	def actorRefFactory = context

	def receive = runRoute(loginReg(context) ~ tbApi)
}