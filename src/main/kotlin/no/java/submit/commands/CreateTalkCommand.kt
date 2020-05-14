package no.java.submit.commands

import no.java.submit.*
import no.java.submit.domain.SubmissionsClosedService
import no.java.submit.domain.Talk
import org.jsonbuddy.JsonArray
import org.jsonbuddy.JsonObject
import javax.servlet.http.HttpServletResponse

class CreateTalkCommand(val talk: Talk?,val submitPassword:String?):Command {
    @Suppress("unused")
    private constructor():this(null,null)

    override fun doStuff(callIdentification: CallIdentification): JsonObject {
        if (callIdentification.callerEmail == null) {
            throw RequestError(HttpServletResponse.SC_UNAUTHORIZED,"No token")
        }
        if (talk == null) {
            throw RequestError(HttpServletResponse.SC_BAD_REQUEST,"Missing talkobj")
        }
        if (!SubmissionsClosedService.okToSubmit(submitPassword)) {
            throw RequestError(HttpServletResponse.SC_BAD_REQUEST,"Missing talkobj")
        }
        validateRequiredFields(talk)



        val createTalkPayload = JsonObject()
            .put("postedBy",callIdentification.callerEmail)
            .put("conferenceId",Setup.sleepingpillConferenceId())
            .put("data",talk.spDataObject(null))
            .put("speakers",JsonArray.fromNodeList(talk.speakers!!.map { it.spSpeakerObject() }))
            .put("status","SUBMITTED")

        val res = SleepingPillService.post("/data/conference/${Setup.sleepingpillConferenceId()}/session",HttpPostMethod.POST,createTalkPayload)
        return res

    }


    companion object {
        fun validateRequiredFields(talk:Talk) {
            validateRequiredField(talk.title,"The talk needs a title")
            if (talk.language == null) {
                throw FunctionalError("Please select a language")
            }
            validateRequiredField(talk.abstract,"You need to fill out a description")
            validateRequiredField(talk.intendedAudience,"You need to fill out intended audience")
            if (talk.format == null) {
                throw FunctionalError("You need to select format")
            }
            validateRequiredField(talk.length,"You need select talk length")



            if (talk.speakers?.isEmpty() != false) {
                throw FunctionalError("At least one speaker must be registered")
            }
            talk.speakers.forEach { it.checkValid() }
        }

        private fun validateRequiredField(fieldValue:String?,errormessage:String) {
            if (fieldValue?.trim()?.isEmpty() != false) {
                throw FunctionalError(errormessage)
            }
        }
    }


}