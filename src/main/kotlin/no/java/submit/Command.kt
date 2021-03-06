package no.java.submit

import no.java.submit.commands.isValidEmail
import org.jsonbuddy.JsonObject
import java.lang.RuntimeException
import javax.servlet.http.HttpServletRequest

class RequestError(val errorType:Int,val errormessage: String):RuntimeException()
class SleepingPillError(val errorType:Int,val errormessage:String):RuntimeException()
class FunctionalError(val errormessage:String):RuntimeException()

private fun readEmail(req: HttpServletRequest): String? {
    val token:String = req.getHeader("submittoken")?:return null
    val decrypted:String
    try {
        decrypted = CryptoUtils.decrypt(token)
    } catch (e:Exception) {
        return null
    }
    val ind:Int = decrypted.lastIndexOf(",")
    if (ind == -1 || ind > decrypted.length-2 || ind < 1) {
        return null
    }
    val timestamp:Long
    try {
        timestamp = decrypted.substring(ind+1).toLong()
    } catch (e:Exception) {
        return null
    }
    val millisValid:Long = Setup.minutesValidToken()*60_000L
    if (System.currentTimeMillis() > timestamp+millisValid) {
        return null
    }
    val email:String = decrypted.substring(0,ind)
    if (!isValidEmail(email)) {
        return null;
    }
    return email
}

class CallIdentification(
        val callerEmail:String?,
        val pathInfo:String?) {
    constructor(req: HttpServletRequest):this(readEmail(req),req.pathInfo)
}

interface Command {
    @Throws(RequestError::class)
    fun doStuff(callIdentification: CallIdentification):JsonObject
}