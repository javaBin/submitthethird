package no.java.submit

import java.io.File

object Setup {
    private val setupData:Map<String,String>


    init {
        if (setupFile == null) {
            setupData = emptyMap()
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
            setupData = readMap
        }
    }

    private fun readValue(key:String,defaultValue:String):String {
        return setupData[key]?:defaultValue
    }


    fun isDevEnviroment(): Boolean {
        return "true".equals(readValue("isDevEnviroment","true"))
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
        return readValue("sleepingpillConferenceId","32b97b40-7d22-4687-b6e0-c5ec399b2be2")
    }

    fun sleepingPillLocation(): String {
        return readValue("sleepingPillLocation","http://localhost:8082")
    }

    fun sleepingpillUser(): String? {
        return readValue("sleepingpillUser","null").let {if (it == "null") null else it}
    }

    fun sleepingpillPassword(): String {
        return readValue("sleepingpillPassword","")
    }

}