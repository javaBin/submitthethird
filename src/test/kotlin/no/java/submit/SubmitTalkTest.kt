package no.java.submit

import no.java.submit.commands.CreateTalkCommand
import no.java.submit.commands.CreateTokenCommand
import org.assertj.core.api.Assertions.assertThat
import org.jsonbuddy.JsonObject.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.net.URLDecoder
import java.net.URLEncoder
import javax.servlet.http.HttpServletRequest

class SubmitTalkTest {
    @Test
    internal fun shouldSendTalk() {
        val mockMailSender = MockMailSender()
        MailSenderService.mailSender = mockMailSender
        CreateTokenCommand("a@a.com").doStuff(CallIdentification(null,null))
        assertThat(mockMailSender.sentMail).hasSize(1)
        val message = mockMailSender.sentMail.get(0).message
        val startIndex = message.indexOf("?token=") + "?token=".length
        val token = URLDecoder.decode(message.substring(startIndex,message.indexOf("\"",startIndex)),"UTF-8")
        assertThat(token).isNotNull()

        val request = Mockito.mock(HttpServletRequest::class.java)
        Mockito.`when`(request.pathInfo).thenReturn("/createTalk")
        Mockito.`when`(request.getHeader("submittoken")).thenReturn(token)


        val callIdentification = CallIdentification(request)
        assertThat(callIdentification.callerEmail).isEqualTo("a@a.com")
    }
}