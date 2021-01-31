package no.java.submit.commands

import no.java.submit.*
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
        val currentSleepingPillObject = SleepingPillService.get("/data/session/${URLEncoder.encode(talkid, "UTF-8")}")
        val talk = Talk(currentSleepingPillObject)


        val updateTalkPayload = JsonObject()
        updateTalkPayload.put("id",talk.id)
        updateTalkPayload.put("sessionId",talk.id)
        updateTalkPayload.put("conferenceId", Setup.sleepingpillConferenceId())


        val currentTqgs = readTagsFromTalkObj(currentSleepingPillObject).toMutableList()
        val dataObject:JsonObject = JsonObject()
        dataObject.put("tagswithauthor", JsonFactory.jsonObject().put("value", JsonArray.fromNodeList(currentTqgs)).put("privateData", true))
        updateTalkPayload.put("data",dataObject)
        SleepingPillService.post("/data/session/${URLEncoder.encode(talk.id,"UTF-8")}",HttpPostMethod.PUT,updateTalkPayload)

        return JsonObject()
    }

}