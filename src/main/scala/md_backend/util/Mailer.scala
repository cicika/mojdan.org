package org.mojdan.md_backend.util

import org.apache.commons.mail._

import org.mojdan.md_backend.util._

trait Mailer extends AppConfig {

	val RESET_PW_SUBJECT = config.getString("mail.subject.reset-pw")

	def sendEmail(to: Tuple2[String, String], from: String,
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
