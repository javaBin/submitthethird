package no.java.submit

import no.java.submit.commands.CreateTokenCommand
import no.java.submit.commands.IllegalPathCommand
import no.java.submit.commands.isValidEmail
import org.jsonbuddy.JsonObject
import org.jsonbuddy.parse.JsonParser
import org.jsonbuddy.pojo.PojoMapper
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.reflect.KClass

class ApiServlet:HttpServlet() {
    override fun doPost(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if (req == null || resp == null) {
            return
        }
        val pathInfo:String? = req.pathInfo
        val kotlinClass: KClass<out Command> = when(pathInfo) {
            "/createToken" -> CreateTokenCommand::class
            else-> IllegalPathCommand::class
        }
        val payload:JsonObject = req.inputStream.use {JsonParser.parseToObject(it)}
        val command:Command = PojoMapper.map(payload,kotlinClass.java)
        val responsobj:JsonObject
        val callIdentification = CallIdentification(readEmail(req),pathInfo)
        try {
            responsobj = command.doStuff(callIdentification)
        } catch (e:RequestError) {
            resp.sendError(e.errorType,e.message)
            return
        }
        resp.contentType = "application/json"
        responsobj.toJson(resp.writer)

    }

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
        if (System.currentTimeMillis() > timestamp+Setup.millisValidToken()) {
            return null
        }
        val email:String = decrypted.substring(0,ind)
        if (!isValidEmail(email)) {
            return null;
        }
        return email
    }
}