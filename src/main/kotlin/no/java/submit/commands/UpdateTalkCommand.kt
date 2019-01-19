package no.java.submit.commands

import no.java.submit.*
import no.java.submit.domain.Talk
import no.java.submit.queries.oneTalkFromSleepingpill
import org.jsonbuddy.JsonArray
import org.jsonbuddy.JsonObject
import java.net.URLEncoder
import javax.servlet.http.HttpServletResponse

class UpdateTalkCommand(val talk:Talk?):Command {

    @Suppress("unused")
    private constructor():this(null)

    override fun doStuff(callIdentification: CallIdentification): JsonObject {
        if (callIdentification.callerEmail == null) {
            throw RequestError(HttpServletResponse.SC_UNAUTHORIZED,"No token")
        }
        if (talk?.id == null) {
            throw RequestError(HttpServletResponse.SC_BAD_REQUEST,"Need a talk with id")
        }
        val currentTalk = oneTalkFromSleepingpill(talk.id,callIdentification.callerEmail)
        if (currentTalk.conferenceId != Setup.sleepingpillConferenceId()) {
            throw FunctionalError("You cannot edit a talk from a previous year");
        }

        val updateTalkPayload = JsonObject()
        updateTalkPayload.put("id",talk.id)
        updateTalkPayload.put("sessionId",talk.id)
        updateTalkPayload.put("postedBy",callIdentification.callerEmail)
        updateTalkPayload.put("conferenceId", Setup.sleepingpillConferenceId())
        val spdataObject = talk.spDataObject()
        if (!spdataObject.isEmpty) {
            updateTalkPayload.put("data",spdataObject)
        }
        talk.speakers?.let {updateTalkPayload.put("speakers", JsonArray.fromNodeList(it.map { it.spSpeakerObject() }))}
        return SleepingPillService.post("/data/session/${URLEncoder.encode(talk.id,"UTF-8")}",HttpPostMethod.PUT,updateTalkPayload)
    }

}