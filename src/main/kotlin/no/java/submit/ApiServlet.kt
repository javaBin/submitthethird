package no.java.submit

import no.java.submit.commands.CreateTalkCommand
import no.java.submit.commands.CreateTokenCommand
import no.java.submit.commands.IllegalPathCommand
import no.java.submit.commands.isValidEmail
import org.jsonbuddy.JsonFactory
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
        val callIdentification = CallIdentification(req)
        val kotlinClass: KClass<out Command> = when(callIdentification.pathInfo) {
            "/createToken" -> CreateTokenCommand::class
            "/createTalk" -> CreateTalkCommand::class
            else-> IllegalPathCommand::class
        }
        val payload:JsonObject = req.inputStream.use {JsonParser.parseToObject(it)}
        val command:Command = PojoMapper.map(payload,kotlinClass.java)
        val responsobj:JsonObject = try {
            command.doStuff(callIdentification).put("status","ok")
        } catch (f:FunctionalError) {
            JsonObject().put("status","error").put("errormessage",f.errormessage)
        } catch (e:RequestError) {
            resp.sendError(e.errorType,e.message)
            return
        }
        resp.contentType = "application/json"
        responsobj.toJson(resp.writer)

    }


}