package org.mojdan.md_backend.util

import scala.util._
import java.security.SecureRandom

trait TokenGenerator {

  val TOKEN_LENGTH = 32
  val REFRESH_TOKEN_LENGTH = 56
  val OTP_LENGTH = 72
  val TOKEN_CHARS =
     "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
  val secureRandom = new SecureRandom()

  def generateToken: String =
    generateToken(TOKEN_LENGTH)

  def generateOtp: String = generateToken(OTP_LENGTH)

  def generateToken(tokenLength: Int): String =
    if(tokenLength == 0) ""
    else TOKEN_CHARS(secureRandom.nextInt(TOKEN_CHARS.length())) +
         generateToken(tokenLength - 1)
}
