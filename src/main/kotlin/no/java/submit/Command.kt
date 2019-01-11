package no.java.submit

import org.jsonbuddy.JsonObject
import java.lang.RuntimeException

class RequestError(val errorType:Int,message:String):RuntimeException()

class CallIdentification(
        val callerEmail:String?,
        val pathInfo:String?)

interface Command {
    @Throws(RequestError::class)
    fun doStuff(callIdentification: CallIdentification):JsonObject
}