package tb_backend

import akka.actor._
import akka.io.IO
import akka.kernel.Bootable

import com.typesafe.config.ConfigFactory

import spray.can.Http

import tb_backend.http._
import model._
import app._

object Server extends App with Kernel {
 
	override def main(args: Array[String]) = {

		
		//val userActor = system.actorOf(Props[UserActor], "user-actor")
		//val taskScheduler = system.actorOf(Props[TaskScheduling], "task-scheduler")

		val apiService = system.actorOf(Props[TBApiServiceActor], "api-service")
		val loginService = system.actorOf(Props[LoginServiceActor], "login-service")

		IO(Http) ! Http.Bind(apiService, interface = "0.0.0.0", port = 8088)
		IO(Http) ! Http.Bind(loginService, interface = "0.0.0.0", port = 8087)
	}	
}

trait Kernel extends Bootable{
	val config = ConfigFactory.load()
	val systemName = "tibiraskernel"
	lazy implicit val system = ActorSystem(systemName, config)
	def startup = {}
  def shutdown = {} 
}