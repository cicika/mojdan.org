package tb_backend

import akka.actor._
import akka.io.IO
import akka.kernel.Bootable

import com.typesafe.config.ConfigFactory

import spray.can.Http

import tb_backend.http._

object Server extends App with Kernel {
 
	override def main(args: Array[String]) = {

		val apiService = system.actorOf(Props[TBApiServiceActor], "api-service")

		IO(Http) ! Http.Bind(apiService, interface = "localhost", port = 8088)
	}

	
 
	
}

trait Kernel extends Bootable{
	val config = ConfigFactory.load()
	val systemName = "tibiraskernel"
	lazy implicit val system = ActorSystem(systemName, config)
	def startup = {}
  def shutdown = {} 
}