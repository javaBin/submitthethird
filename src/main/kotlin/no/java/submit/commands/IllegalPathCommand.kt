package no.java.submit.commands

import no.java.submit.CallIdentification
import no.java.submit.Command
import no.java.submit.RequestError
import org.jsonbuddy.JsonObject
import javax.servlet.http.HttpServletResponse

class IllegalPathCommand: Command {
    override fun doStuff(callIdentification: CallIdentification): JsonObject {
        throw RequestError(HttpServletResponse.SC_BAD_REQUEST, "Unknown path ${callIdentification.pathInfo}")
    }

}