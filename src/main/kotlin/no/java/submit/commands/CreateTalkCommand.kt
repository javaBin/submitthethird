package no.java.submit.commands

import no.java.submit.*
import no.java.submit.domain.Talk
import org.jsonbuddy.JsonArray
import org.jsonbuddy.JsonFactory
import org.jsonbuddy.JsonObject
import javax.servlet.http.HttpServletResponse

class CreateTalkCommand(val talk: Talk?):Command {
    private constructor():this(null)
    override fun doStuff(callIdentification: CallIdentification): JsonObject {
        if (callIdentification.callerEmail == null) {
            throw RequestError(HttpServletResponse.SC_UNAUTHORIZED,"No token")
        }
        if (talk == null) {
            throw RequestError(HttpServletResponse.SC_BAD_REQUEST,"Missing talkobj")
        }
        if (talk.title == null) {
            throw FunctionalError("The talk needs a title")
        }
        if (talk.speakers?.isEmpty() != false) {
            throw FunctionalError("At least one speaker must be registered")
        }
        talk.speakers.forEach { it.checkValidOnCreate() }
        val createTalkPayload = JsonObject()
        createTalkPayload.put("postedBy",callIdentification.callerEmail)
        createTalkPayload.put("conferenceId",Setup.sleepingpillConferenceId())
        createTalkPayload.put("data",talk.spDataObject())
        createTalkPayload.put("speakers",JsonArray.fromNodeList(talk.speakers.map { it.spSpeakerObject() }))

        val res = SleepingPillService.post("/data/conference/${Setup.sleepingpillConferenceId()}/session",HttpPostMethod.POST,createTalkPayload)
        return res

    }

}