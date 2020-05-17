package no.java.submit.domain

import no.java.submit.FunctionalError
import org.jsonbuddy.JsonArray
import org.jsonbuddy.JsonFactory
import org.jsonbuddy.JsonObject
import java.util.stream.Collectors

enum class Language(val sleepingpillvalue:String) {
    NORWEGIAN("no"),ENGLISH("en");

    companion object {
        fun fromSleepingPill(sleepingpillvalue: String?):Language? {
            return values().firstOrNull { it.sleepingpillvalue.equals(sleepingpillvalue) }

        }
    }
}

enum class TalkFormat(val sleepingpillvalue:String) {
    LIGHTNING_TALK("lightning-talk"),PRESENTATION("presentation"),WORKSHOP("workshop");



    companion object {
        fun fromSleepingPill(sleepingpillvalue: String?):TalkFormat? {
            return values().firstOrNull { it.sleepingpillvalue.equals(sleepingpillvalue) }

        }
    }
}

private fun readDataValue(jsonObject: JsonObject,key:String):String? {
    val dataObject = jsonObject.objectValue("data").orElse(null)?:return null;
    return dataObject.objectValue(key).orElse(null)?.stringValue("value")?.orElse(null)
}

fun readTagsFromTalkObj(talkObject: JsonObject?):List<JsonObject> {
    if (talkObject == null) return emptyList()
    val dataObject = talkObject.objectValue("data").orElse(null)?:return emptyList();
    val tagobjects:List<JsonObject> = dataObject.objectValue("tagswithauthor").orElse(JsonObject()).arrayValue("value").orElse(JsonArray()).objects { it }
    return tagobjects
    //return tagobjects.map { it.stringValue("tag").orElse(null) }.filterNotNull().toSet()
}

enum class ConferencePreference(val tags:Set<String>) {
    ONLYIRL(setOf("jzirl")),ONLYVR(setOf("jzvr")),BOTHIRLVR(ONLYIRL.tags.union(ONLYVR.tags)),NOCONF(setOf("jznc"));



    companion object {
        fun fromTagSet(tags:Set<String>):ConferencePreference? = when {
            tags.containsAll(BOTHIRLVR.tags) -> BOTHIRLVR
            tags.containsAll(ONLYVR.tags) -> ONLYVR
            tags.containsAll(ONLYIRL.tags) -> ONLYIRL
            else -> null
        }

        fun fromValue(stringVal:String?):ConferencePreference? = values().filter { it.toString() == stringVal }.firstOrNull()
    }
}

class Talk(
        val id:String? = null,
        val title:String? = null,
        val language:Language? = null,
        val intendedAudience:String? = null,
        val length:String? = null,
        val format:TalkFormat? = null,
        val equipment:String? = null,
        val abstract:String? = null,
        val outline:String? = null,
        val infoToProgramCommittee:String? = null,
        val speakers:List<Speaker>? = null,
        val conferenceId: String? = null,
        val postedBy:String? = null,
        val suggestedKeywords:String? = null,
        val participation:String? = null,
        val conferencePreference: ConferencePreference? = null

) {
    @Suppress("unused")
    private constructor():this(id=null)

    constructor(talkObject: JsonObject):this(
            id = talkObject.requiredString("id"),
            conferenceId = talkObject.requiredString("conferenceId"),
            title = readDataValue(talkObject,"title"),
            language = Language.fromSleepingPill(readDataValue(talkObject,"language")),
            intendedAudience= readDataValue(talkObject,"intendedAudience"),
            length= readDataValue(talkObject,"length"),
            format= TalkFormat.fromSleepingPill(readDataValue(talkObject,"format")),
            equipment= readDataValue(talkObject,"equipment"),
            abstract= readDataValue(talkObject,"abstract"),
            outline = readDataValue(talkObject,"outline"),
            infoToProgramCommittee = readDataValue(talkObject,"infoToProgramCommittee"),
            speakers = Speaker.readFromJson(talkObject.arrayValue("speakers").orElse(null)),
            postedBy = talkObject.stringValue("postedBy").orElse(null),
            suggestedKeywords = readDataValue(talkObject,"suggestedKeywords"),
            participation = readDataValue(talkObject,"participation"),
            conferencePreference =  ConferencePreference.fromTagSet(readTagsFromTalkObj(talkObject).map { it.stringValue("tag").orElse(null) }.filterNotNull().toSet())
    )



    fun spDataObject(currentSleepingPillObject:JsonObject?):JsonObject {
        val jsonObject = JsonFactory.jsonObject()
        title?.let { jsonObject.put("title",addToData(it,false)) }
        language?.let {jsonObject.put("language",addToData(it.sleepingpillvalue,false))}
        intendedAudience?.let {jsonObject.put("intendedAudience",addToData(it,false))}
        length?.let {jsonObject.put("length",addToData(it,false))}
        format?.let {jsonObject.put("format",addToData(it.sleepingpillvalue,false))}
        equipment?.let {jsonObject.put("equipment",addToData(it,true))}
        abstract?.let {jsonObject.put("abstract",addToData(it,false))}
        outline?.let {jsonObject.put("outline",addToData(it,true))}
        infoToProgramCommittee?.let {jsonObject.put("infoToProgramCommittee",addToData(it,true))}
        suggestedKeywords?.let {jsonObject.put("suggestedKeywords",addToData(it,true))}
        participation?.let {jsonObject.put("participation",addToData(it,true))}
        if (conferencePreference != null) {
            val currentTqgs = readTagsFromTalkObj(currentSleepingPillObject)
            val newTags = currentTqgs.filter { it.stringValue("tag").isPresent && !ConferencePreference.BOTHIRLVR.tags.contains(it.stringValue("tag").get()) }.toMutableList()
            conferencePreference.tags.forEach{newTags.add(JsonObject().put("tag",it).put("author","Submitit"))}
            jsonObject.put("tagswithauthor", JsonFactory.jsonObject().put("value", JsonArray.fromNodeList(newTags)).put("privateData", true))
        }
        return jsonObject
    }

}

private fun addToData(value:String,isPrivate:Boolean):JsonObject {
    return JsonFactory.jsonObject().put("value",value).put("privateData",isPrivate)
}

class Speaker(
        val id:String? = null,
        val name:String? = null,
        val email:String? = null,
        val twitter:String? = null,
        val bio:String? = null,
        val zipCode:String? = null
) {
    companion object {
        fun readFromJson(speakerArray:JsonArray?):List<Speaker>? {
            if (speakerArray == null) {
                return null
            }
            return speakerArray.objectStream().map { Speaker(
                    id=it.stringValue("id").orElse(null),
                    name=it.stringValue("name").orElse(null),
                    email = it.stringValue("email").orElse(null),
                    twitter = readDataValue(it,"twitter"),
                    bio = readDataValue(it,"bio"),
                    zipCode = readDataValue(it,"zip-code")


            ) }.collect(Collectors.toList())
        }
    }

    @Suppress("unused")
    private constructor():this(id=null)

    fun spSpeakerObject():JsonObject {
        val jsonObject = JsonFactory.jsonObject()
        id?.let { jsonObject.put("id",it) }
        name?.let { jsonObject.put("name",it)}
        email?.let { jsonObject.put("email",it)}

        val dataObject = JsonFactory.jsonObject()
        twitter?.let { dataObject.put("twitter",addToData(it,false))}
        bio?.let { dataObject.put("bio",addToData(it,true))}
        zipCode?.let { dataObject.put("zip-code",addToData(it,true))}

        if (!dataObject.isEmpty) {
            jsonObject.put("data",dataObject)
        }

        return jsonObject
    }

    fun checkValid() {
        if (name?.trim()?.isEmpty() != false) {
            throw FunctionalError("Need a name for speaker")
        }
        if (email?.trim()?.isEmpty() != false) {
            throw FunctionalError("Need an email for speaker")
        }
    }
}

class Conference(val conferenceName: String,val talks:List<Talk>) {

}