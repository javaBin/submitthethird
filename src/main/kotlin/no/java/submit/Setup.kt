package no.java.submit

import java.io.File

object Setup {
    private val setupData:Map<String,String>


    init {
        setupData =
        if (submitTest != null) {
            mapOf(Pair("sleepingpillPassword", submitTest!!))
        }
        else if (setupFile == null) {
            emptyMap()
        } else {
            val readMap:MutableMap<String,String> = mutableMapOf()
            for (line:String in File(setupFile).readLines()) {
                if (line.startsWith(";") || line.startsWith("#")) {
                    continue
                }
                val pos:Int = line.indexOf("=")
                val key = line.substring(0,pos)
                val value = line.substring(pos+1)
                readMap[key] = value
            }
            readMap
        }
    }

    private fun readValue(key:String,defaultValue:String):String {
        return setupData[key]?:defaultValue
    }

    private fun readNullableValue(key:String):String? {
        return setupData[key]?.let { if ("null" == it) null else it}
    }

    fun runAsJarFile(): Boolean {
        return "true".equals(readValue("runAsJarFile","false"))
    }

    fun serverPort(): Int {
        return Integer.parseInt(readValue("serverPort","8080"))
    }

    fun serverAddress():String {
        return readValue("serverAddress","http://localhost:8080")
    }

    fun serverEncryptPassword(): String {
        return readValue("serverEncryptPassword","bullshit")
    }

    private const val millisInOneDay:Long = 24L*60L*60L*1000L

    fun millisValidToken():Long {
        return readValue("millisValidToken", millisInOneDay.toString()).toLong()
    }

    fun sleepingpillConferenceId():String {
        return readValue("sleepingpillConferenceId","577a17d5-eb3b-4dc2-ba62-57d960404e7a")
    }

    fun sleepingPillLocation(): String {
        return readValue("sleepingPillLocation","https://test-sleepingpill.javazone.no")
    }

    fun sleepingpillUser(): String? {
        return readValue("sleepingpillUser","fullaccess").let {if (it == "null") null else it}
    }

    fun sleepingpillPassword(): String {
        return readValue("sleepingpillPassword","")
    }

    fun mailSenderType():MailSenderType {
        return MailSenderType.valueOf(readValue("mailSenderType","CONSOLE"))
    }

    fun sendGridToken():String? {
        return readNullableValue("sendGridToken")
    }

}