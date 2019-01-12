package no.java.submit.queries

import no.java.submit.CallIdentification
import no.java.submit.ConferenceService
import no.java.submit.RequestError
import no.java.submit.SleepingPillService
import no.java.submit.domain.Conference
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
    val mappedList:List<Conference> = ConferenceService.allConferences.filter { talksMapped.containsKey(it.id) }.map { Conference(it.name,talksMapped[it.id]!!) }
    val objectList:List<JsonObject> = mappedList.map { JsonGenerator.generate(it) as JsonObject }
    return JsonObject().put("conferences",JsonArray.fromNodeList(objectList))
}