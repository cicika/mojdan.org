package org.mojdan.md_backend.util

trait LinkGenerator extends AppConfig {
	val BASE_URL = config.getString("site.base-url")

	def resetPass(otp: String) = "%s/#/passreset/%s" format (BASE_URL, otp)
}

object LinkGenerator extends AppConfig with LinkGenerator {
	override def resetPass(otp: String) = "%s/#/passreset/%s" format (BASE_URL, otp)
}
