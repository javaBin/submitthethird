package no.java.submit.queries

import no.java.submit.CallIdentification
import no.java.submit.ConferenceService
import no.java.submit.RequestError
import no.java.submit.SleepingPillService
import no.java.submit.domain.Conference
import no.java.submit.domain.Speaker
import no.java.submit.domain.SubmissionsClosedService
import no.java.submit.domain.Talk
import org.jsonbuddy.JsonArray
import org.jsonbuddy.JsonObject
import org.jsonbuddy.pojo.JsonGenerator
import java.net.URLEncoder
import java.util.stream.Collectors
import javax.servlet.http.HttpServletResponse

fun allTalksForSpeaker(callIdentification: CallIdentification):JsonObject {
    if (callIdentification.callerEmail == null) {
        throw RequestError(HttpServletResponse.SC_UNAUTHORIZED,"Missing token")
    }
    val jsonObject = SleepingPillService.get("/data/submitter/${URLEncoder.encode(callIdentification.callerEmail,"UTF-8")}/session")
    val talks:List<Talk> = jsonObject.arrayValue("sessions").orElse(JsonArray()).objectStream().map(::Talk).collect(Collectors.toList())
    val talksMapped:Map<String?,List<Talk>> = talks.groupBy { it.conferenceId }
    val mappedList:List<Conference> = ConferenceService.allConferences.filter { talksMapped.containsKey(it.id) }.map { Conference(it.name,talksMapped[it.id]!!) }.sortedByDescending { it.conferenceName }
    val objectList:List<JsonObject> = mappedList.map { JsonGenerator.generate(it) as JsonObject }
    return JsonObject().put("conferences",JsonArray.fromNodeList(objectList))
}

private fun checkAccessToTalk(talk: Talk,email:String):Boolean {
    if (email == talk.postedBy) {
        return true;
    }
    val speaker:Speaker? = talk.speakers?.firstOrNull { email == it.email }
    return (speaker != null)
}

fun oneGivenTalk(callIdentification: CallIdentification):JsonObject {
    if (callIdentification.callerEmail == null) {
        throw RequestError(HttpServletResponse.SC_UNAUTHORIZED,"Missing token")
    }
    val talkid = callIdentification.pathInfo!!.substring("/talk/".length)
    val talk = oneTalkFromSleepingpill(talkid, callIdentification.callerEmail)

    return JsonGenerator.generate(talk) as JsonObject

}

fun oneTalkFromSleepingpill(talkid: String, callerEmail: String): Talk {
    val jsonObject = SleepingPillService.get("/data/session/${URLEncoder.encode(talkid, "UTF-8")}")
    val talk = Talk(jsonObject)
    if (!checkAccessToTalk(talk, callerEmail)) {
        throw RequestError(HttpServletResponse.SC_FORBIDDEN, "Email not assosiated with this talk")
    }
    return talk
}

fun loginEmail(callIdentification: CallIdentification):JsonObject {
    if (callIdentification.callerEmail == null) {
        throw RequestError(HttpServletResponse.SC_UNAUTHORIZED,"Missing token")
    }
    return JsonObject().put("email",callIdentification.callerEmail).put("isClosed",SubmissionsClosedService.isClosed())
}