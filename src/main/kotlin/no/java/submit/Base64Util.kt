package no.java.submit

import org.apache.commons.codec.binary.Base64

object Base64Util {
    fun encode(text: String): String {
        return Base64.encodeBase64String(text.toByteArray())
    }

    fun decode(encText: String): String {
        return String(Base64.decodeBase64(encText))
    }

}
