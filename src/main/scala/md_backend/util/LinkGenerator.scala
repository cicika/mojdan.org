package org.mojdan.md_backend.util

trait LinkGenerator extends AppConfig {
	val BASE_URL = config.getString("site.base-url")

	def resetPassLink(otp: String) = "%s/passreset/%s" format (BASE_URL, otp)
}