package tb_backend.http.api

import spray.http.StatusCodes
import spray.routing.{Directives, HttpService}

trait UserAccountService extends HttpService{

	def login = detach() {ctx =>
		ctx.complete(StatusCodes.OK)
	}//POST

	//def register //POST

	//def edit // POST

	//def resetPassword // GET

}