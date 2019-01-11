package no.java.submit.commands

import no.java.submit.CallIdentification
import no.java.submit.Command
import no.java.submit.CryptoUtils
import no.java.submit.RequestError
import org.jsonbuddy.JsonFactory
import org.jsonbuddy.JsonObject
import javax.servlet.http.HttpServletResponse

fun isValidEmail(subject: String): Boolean {
    if (subject.trim().isEmpty()) {
        return false
    }
    return if (!subject.contains("@")) {
        false
    } else subject.chars().noneMatch{ Character.isWhitespace(it) }
}


class CreateTokenCommand(val email:String?):Command {
    @Suppress("unused")
    private constructor():this(null)

    override fun doStuff(callIdentification: CallIdentification): JsonObject {
        if (email == null || !isValidEmail(email)) {
            throw RequestError(HttpServletResponse.SC_BAD_REQUEST,"Not valid email")
        }
        val token = CryptoUtils.encrypt("$email,${System.currentTimeMillis()}")
        return JsonFactory.jsonObject().put("token",token)
    }




}