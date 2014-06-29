package org.mojdan.md_backend

import akka.actor._
import akka.io.IO
import akka.kernel.Bootable
import akka.routing._

import com.typesafe.config.ConfigFactory

import org.mojdan.md_backend.http._
import model._
import app._

import spray.can.Http

object Server extends App with Kernel {
 
	override def main(args: Array[String]) = {
		
		val userActor = system.actorOf(Props[UserActor].withRouter(RoundRobinRouter(nrOfInstances = 5)), "user-actor")
		val applicationActor = system.actorOf(Props[ApplicationActor].
																				  withRouter(RoundRobinRouter(nrOfInstances = 15)), "application-actor")
		val maintenanceActor = system.actorOf(Props[MaintenanceActor], "maintenance-actor")

		val apiService = system.actorOf(Props[TBApiServiceActor], "api-service")
		val loginService = system.actorOf(Props[LoginServiceActor], "login-service")

		IO(Http) ! Http.Bind(apiService, interface = hostname, port = 8088)
		IO(Http) ! Http.Bind(loginService, interface = hostname, port = 8087)
	}	
}

trait Kernel extends Bootable{
	val config = ConfigFactory.load()
	val systemName = "tibiraskernel"
	val hostname = config.getString("api.host")
	lazy implicit val system = ActorSystem(systemName, config)

	def startup = {}
  def shutdown = {} 
}