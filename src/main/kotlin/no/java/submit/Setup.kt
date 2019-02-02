package no.java.submit

import java.io.File

object Setup {
    private val setupData:Map<String,String>


    init {
        setupData =
        if (submitTest != null) {
            mapOf(Pair("sleepingpillPassword", submitTest!!))
        } else if (setupFile != null) {
            val allLines = File(setupFile).readLines()
            readLinesToConfig(allLines)
        } else if (javaClass.classLoader.getResourceAsStream("config.txt") != null) {
            val lines = javaClass.classLoader.getResourceAsStream("config.txt").bufferedReader(Charsets.UTF_8).use {
                it.readLines()
            }
            readLinesToConfig(lines)
        } else {
            emptyMap()
        }
    }

    private fun readLinesToConfig(allLines: List<String>): Map<String, String> {
        val readMap: MutableMap<String, String> = mutableMapOf()
        for (line: String in allLines) {
            if (line.startsWith(";") || line.startsWith("#")) {
                continue
            }
            val pos: Int = line.indexOf("=")
            val key = line.substring(0, pos)
            val value = line.substring(pos + 1)
            readMap[key] = value
        }
        return readMap
    }

    private fun readConfigLine(line: String, readMap: MutableMap<String, String>) {
        if (line.startsWith(";") || line.startsWith("#")) {
            return
        }
        val pos: Int = line.indexOf("=")
        val key = line.substring(0, pos)
        val value = line.substring(pos + 1)
        readMap[key] = value
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


    fun minutesValidToken():Long {
        return readValue("minutesValidToken", "144000").toLong()
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

    fun configFileName():String {
        return readValue("configFileName","default");
    }

    fun mailSubject(): String {
        return readValue("mailSubject","Your link to javazone submit")
    }

}