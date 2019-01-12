package no.java.submit

import java.util.stream.Collectors

class ConferenceId(val id:String,val name:String)

object ConferenceService {
    val allConferences:List<ConferenceId>

    init {
        val jsonObject =SleepingPillService.get("/data/conference")
        allConferences = jsonObject.requiredArray("conferences").objectStream().map { ConferenceId(id = it.requiredString("id"),name = it.requiredString("name")) }.collect(Collectors.toList())
    }
}