package no.java.submit.commands

import no.java.submit.CallIdentification
import no.java.submit.Command
import no.java.submit.FunctionalError
import no.java.submit.RequestError
import no.java.submit.domain.SubmissionsClosedService
import org.jsonbuddy.JsonObject
import javax.servlet.http.HttpServletResponse

class CheckLatePasswordCommand(val password:String?):Command {
    @Suppress("unused")
    constructor():this(null)

    override fun doStuff(callIdentification: CallIdentification): JsonObject {
        if (callIdentification.callerEmail == null) {
            throw RequestError(HttpServletResponse.SC_UNAUTHORIZED,"No token")
        }
        if (!SubmissionsClosedService.okToSubmit(password)) {
            throw FunctionalError("You need to supply correct password")
        }
        val returnValue = JsonObject()
        password?.let { returnValue.put("password",it) }
        return returnValue
    }

}