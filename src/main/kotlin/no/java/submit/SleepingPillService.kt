package no.java.submit

import org.jsonbuddy.JsonObject
import org.jsonbuddy.parse.JsonParser
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import javax.servlet.http.HttpServletResponse

interface SleepingPillSender {
    fun post(path:String,httpPostMethod: HttpPostMethod,payload: JsonObject):JsonObject
    fun get(path: String):JsonObject
}

class LiveSleepingPillSender:SleepingPillSender {
    override fun get(path: String): JsonObject {
        val urlConnection = openConnection(path)
        urlConnection.setRequestProperty("content-type","application/json")
        return readResult(urlConnection)
    }

    private val sleepingPillLocation = Setup.sleepingPillLocation()
    override fun post(path: String, httpPostMethod: HttpPostMethod,payload: JsonObject): JsonObject {
        val urlConnection = openConnection(path)
        urlConnection.setRequestProperty("content-type","application/json;charset=UTF-8")
        urlConnection.requestMethod = httpPostMethod.toString()

        urlConnection.doOutput = true

        PrintWriter(OutputStreamWriter(urlConnection.outputStream, Charset.forName("UTF-8"))).use {
            payload.toJson(it)
        }



        return readResult(urlConnection)

    }

    private fun openConnection(path: String):HttpURLConnection {
        val httpURLConnection = URL(sleepingPillLocation + path).openConnection() as HttpURLConnection
        val sleepingpillUser = Setup.sleepingpillUser()
        if (sleepingpillUser != null) {
            val authString = "$sleepingpillUser:${Setup.sleepingpillPassword()}"
            val authStringEnc = Base64Util.encode(authString)
            httpURLConnection.setRequestProperty("Authorization", "Basic $authStringEnc")
        }
        return httpURLConnection
    }

    private fun readResult(urlConnection: HttpURLConnection): JsonObject {
        val responseCode = urlConnection.responseCode
        if (responseCode >= 400) {
            urlConnection.errorStream.use {
                val res = toString(it)
                throw SleepingPillError(responseCode, res)
            }
        }
        return urlConnection.inputStream.use { JsonParser.parseToObject(it) }
    }

    @Throws(IOException::class)
    fun toString(inputStream: InputStream): String {
        BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
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