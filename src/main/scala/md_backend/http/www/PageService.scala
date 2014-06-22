package org.mojdan.md_backend.http.www

import akka.actor._
import akka.pattern._

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}

import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling.BasicUnmarshallers._
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.routing._
import spray.routing.{Directives, HttpService}

import org.mojdan.md_backend.model._
import org.mojdan.md_backend.model.TBJsonProtocol._
import org.mojdan.md_backend.util._

trait PageService extends HttpService with AppConfig {
	def page = (pageId: String, context: ActorContext) => {
		complete(StatusCodes.NotImplemented)
	}

	def editPage = (pageId: String, context: ActorContext) => {
		complete(StatusCodes.NotImplemented)
	}
}