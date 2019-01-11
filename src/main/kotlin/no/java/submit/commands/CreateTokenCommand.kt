package no.java.submit.commands

import no.java.submit.*
import org.jsonbuddy.JsonFactory
import org.jsonbuddy.JsonObject
import java.net.URLEncoder
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
        sendMail(email,token)
        return JsonFactory.jsonObject()
    }


    private fun sendMail(email:String,token:String) {
        val body = """
            |<html>
            |<body>
            |Login <a href="${Setup.serverAddress()}"/emailLogin.html?token=${URLEncoder.encode(token,"UTF-8")}">here</a>
            |</body>
            |</html>
        """.trimMargin()
        MailSenderService.sendMail(email,"Your link to javazone submit",body)

    }




}