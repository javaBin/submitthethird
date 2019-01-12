package no.java.submit

import no.java.submit.commands.*
import no.java.submit.queries.allTalksForSpeaker
import no.java.submit.queries.oneGivenTalk
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
            "/updateTalk" -> UpdateTalkCommand::class
            else-> IllegalPathCommand::class
        }
        val payload:JsonObject = req.inputStream.use {JsonParser.parseToObject(it)}
        val command:Command = PojoMapper.map(payload,kotlinClass.java)
        doStuff(resp) {command.doStuff(callIdentification).put("status","ok") }
    }

    private fun doStuff(resp: HttpServletResponse,executor:() -> JsonObject) {
        val responsobj:JsonObject = try {
            executor()
        } catch (f:FunctionalError) {
            JsonObject().put("status", "error").put("errormessage", f.errormessage)
        } catch (s:SleepingPillError) {
            resp.sendError(HttpServletResponse.SC_BAD_GATEWAY,"Error communicating with backend " + s.errormessage)
            return
        } catch (e:RequestError) {
            resp.sendError(e.errorType,e.errormessage)
            return
        }
        resp.contentType = "application/json"
        responsobj.toJson(resp.writer)
    }

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        if (req == null || resp == null) {
            return
        }
        val callIdentification = CallIdentification(req)
        doStuff(resp) {
            when {
                "/all".equals(callIdentification.pathInfo) -> allTalksForSpeaker(callIdentification)
                callIdentification.pathInfo?.startsWith("/talk/") == true -> oneGivenTalk(callIdentification)
                else -> throw RequestError(HttpServletResponse.SC_BAD_REQUEST, "Unknown path ${callIdentification.pathInfo}")
            }
        }
    }


}