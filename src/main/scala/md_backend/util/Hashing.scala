package org.mojdan.md_backend.util

import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac

import org.parboiled.common.Base64

trait Hashing extends DBConfig {
	def hash(input: String) = {
		val key = new SecretKeySpec(cryptoKey.getBytes, "HmacSHA1")
		val mac = Mac.getInstance("HmacSHA1")
		mac.init(key)
		Base64.rfc2045.encodeToString(mac.doFinal(input.getBytes), false)
	}
}
