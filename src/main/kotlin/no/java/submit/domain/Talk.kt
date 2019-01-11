package no.java.submit.domain

import org.jsonbuddy.JsonFactory
import org.jsonbuddy.JsonObject

enum class Language(val sleepingpillvalue:String) {
    NORWEGIAN("no"),ENGLISH("en")
}

enum class TalkFormat(val sleepingpillvalue:String) {
    LIGHTNING_TALK("lightning-talk"),PRESENTATION("presentation"),WORKSHOP("workshop")
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
        val speakers:List<Speaker>? = null

) {
    @Suppress("unused")
    private constructor():this(id=null)

    fun spDataObject():JsonObject {
        val jsonObject = JsonFactory.jsonObject()
        title?.let { jsonObject.put("title",addToData(it,false)) }
        language?.let {jsonObject.put("language",addToData(it.sleepingpillvalue,false))}
        intendedAudience?.let {jsonObject.put("intendedAudience",addToData(it,false))}
        length?.let {jsonObject.put("length",addToData(it,false))}
        format?.let {jsonObject.put("format",addToData(it.sleepingpillvalue,false))}
        equipment?.let {jsonObject.put("equipment",addToData(it,true))}
        abstract?.let {jsonObject.put("abstract",addToData(it,true))}
        outline?.let {jsonObject.put("outline",addToData(it,true))}
        infoToProgramCommittee?.let {jsonObject.put("infoToProgramCommittee",addToData(it,false))}
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
}