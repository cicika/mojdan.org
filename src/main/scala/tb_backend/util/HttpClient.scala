package tb_backend.util

import scala.util._
import java.security.SecureRandom

trait HttpClient {}

trait TokenGenerator {
  
  val TOKEN_LENGTH = 32
  val TOKEN_CHARS = 
     "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._"
  val secureRandom = new SecureRandom()
    
  def generateToken:String =  
    generateToken(TOKEN_LENGTH)   
  
  def generateToken(tokenLength: Int): String =
    if(tokenLength == 0) "" else TOKEN_CHARS(secureRandom.nextInt(TOKEN_CHARS.length())) + 
     generateToken(tokenLength - 1)
  
}
