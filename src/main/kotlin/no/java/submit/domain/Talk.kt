package no.java.submit.domain

import no.java.submit.ConferenceId
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
        val participation:String? = null

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
            suggestedKeywords = talkObject.stringValue("suggestedKeywords").orElse(null),
            participation = talkObject.stringValue("participation").orElse(null)



    )



    fun spDataObject():JsonObject {
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

    fun checkValidOnCreate() {
        if (name == null) {
            throw FunctionalError("Need a name for speaker")
        }
        if (email == null) {
            throw FunctionalError("Need an email for speaker")
        }
    }
}

class Conference(val conferenceName: String,val talks:List<Talk>) {

}