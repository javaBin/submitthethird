package no.java.submit.commands

import no.java.submit.*
import no.java.submit.domain.ConferencePreference
import no.java.submit.domain.Talk
import no.java.submit.domain.readTagsFromTalkObj
import org.jsonbuddy.JsonArray
import org.jsonbuddy.JsonFactory
import org.jsonbuddy.JsonObject
import java.net.URLEncoder
import javax.servlet.http.HttpServletResponse

class ConferenceConfirmationCommand(val talkid:String?=null,val confirmOption:String?=null):Command {
    override fun doStuff(callIdentification: CallIdentification): JsonObject {
        if (talkid == null) {
            throw RequestError(HttpServletResponse.SC_BAD_REQUEST,"Missing parameter talkid")
        }
        val conferencePreference:ConferencePreference = ConferencePreference.fromValue(confirmOption)?:throw RequestError(HttpServletResponse.SC_BAD_REQUEST,"Missing parameter confirmOption")
        val currentSleepingPillObject = SleepingPillService.get("/data/session/${URLEncoder.encode(talkid, "UTF-8")}")
        val talk = Talk(currentSleepingPillObject)

        if (talk.conferencePreference != null) {
            throw FunctionalError("Conference preference is already reported. No need to do it again")
        }

        val updateTalkPayload = JsonObject()
        updateTalkPayload.put("id",talk.id)
        updateTalkPayload.put("sessionId",talk.id)
        updateTalkPayload.put("conferenceId", Setup.sleepingpillConferenceId())


        val currentTqgs = readTagsFromTalkObj(currentSleepingPillObject).toMutableList()
        conferencePreference.tags.forEach{currentTqgs.add(JsonObject().put("tag",it).put("author","Submitit"))}
        val dataObject:JsonObject = JsonObject()
        dataObject.put("tagswithauthor", JsonFactory.jsonObject().put("value", JsonArray.fromNodeList(currentTqgs)).put("privateData", true))
        updateTalkPayload.put("data",dataObject)
        SleepingPillService.post("/data/session/${URLEncoder.encode(talk.id,"UTF-8")}",HttpPostMethod.PUT,updateTalkPayload)

        return JsonObject()
    }

}