package org.mojdan.md_backend.util

import org.apache.commons.mail._

trait Mailer {

	def sendEmail(hostname: String, to: Tuple2[String, String], from: String, 
								subject: String, htmlContent: String, textContent: String) = {

		val email = new HtmlEmail()

		email.setHostName(hostname)
		email.addTo(to._1, to._2)
		email.setFrom(from)
		email.setSubject(subject)
		email.setHtmlMsg(htmlContent)
		email.setTextMsg(textContent)

		email.send()
	}
}