package no.java.submit

import org.jsonbuddy.JsonObject
import org.jsonbuddy.parse.JsonParser
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import javax.servlet.http.HttpServletResponse

interface SleepingPillSender {
    fun post(path:String,httpPostMethod: HttpPostMethod,payload: JsonObject):JsonObject
    fun get(path: String):JsonObject
}

class LiveSleepingPillSender:SleepingPillSender {
    override fun get(path: String): JsonObject {
        val urlConnection = URL(sleepingPillLocation + path).openConnection() as HttpURLConnection
        urlConnection.setRequestProperty("content-type","application/json")
        return readResult(urlConnection)
    }

    private val sleepingPillLocation = Setup.sleepingPillLocation()
    override fun post(path: String, httpPostMethod: HttpPostMethod,payload: JsonObject): JsonObject {
        val urlConnection = URL(sleepingPillLocation + path).openConnection() as HttpURLConnection
        urlConnection.setRequestProperty("content-type","application/json")
        urlConnection.requestMethod = httpPostMethod.toString()

        urlConnection.doOutput = true

        PrintWriter(OutputStreamWriter(urlConnection.outputStream)).use {
            payload.toJson(it)
        }



        return readResult(urlConnection)

    }

    private fun readResult(urlConnection: HttpURLConnection): JsonObject {
        val responseCode = urlConnection.responseCode
        if (responseCode >= 400) {
            urlConnection.errorStream.use {
                val res = toString(it)
                throw RequestError(HttpServletResponse.SC_BAD_GATEWAY, "Error communicating with backend " + res)
            }
        }
        return urlConnection.inputStream.use { JsonParser.parseToObject(it) }
    }

    @Throws(IOException::class)
    fun toString(inputStream: InputStream): String {
        BufferedReader(InputStreamReader(inputStream, "utf-8")).use { reader ->
            val result = StringBuilder()
            while (true) {
                val c: Int = reader.read()
                if (c == -1) {
                    break
                }
                result.append(c.toChar())
            }
            return result.toString()
        }
    }

}

enum class HttpPostMethod {
    POST,PUT
}

object SleepingPillService {
    var sleepingPillSender:SleepingPillSender? = null

    fun post(path:String,method:HttpPostMethod,payload: JsonObject):JsonObject {
        if (sleepingPillSender == null) {
            sleepingPillSender = LiveSleepingPillSender()
        }
        return sleepingPillSender!!.post(path,method,payload)
    }

    fun get(path:String):JsonObject {
        if (sleepingPillSender == null) {
            sleepingPillSender = LiveSleepingPillSender()
        }
        return sleepingPillSender!!.get(path)
    }

}