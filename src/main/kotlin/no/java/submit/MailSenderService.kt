package no.java.submit

import com.sendgrid.*
import java.io.IOException

enum class MailSenderType {
    CONSOLE,SENDGRID
}

interface MailSender {
    fun sendMail(to:String,subject:String,message:String)
}

class ConsoleDummyMaiSender:MailSender {
    override fun sendMail(to:String,subject:String,message:String) {
        println("Sending mail to $to regarding $subject -> $message")
    }
}

class SendGridMailSender:MailSender {
    override fun sendMail(to: String, subject: String, message: String) {

        val sg = SendGrid(Setup.sendGridToken()?:throw RuntimeException("Send grid key not set"))
        val request = Request()
        try {
            val from = Email("program@java.no","JavaZone")
            val content = Content("text/html", message)

            val mail = Mail()

            mail.setFrom(from)
            mail.setSubject(subject)
            val personalization = Personalization()
            personalization.addTo(Email(to,to))
            mail.addPersonalization(personalization)
            mail.addContent(content)

            request.setMethod(Method.POST)
            request.setEndpoint("mail/send")
            request.setBody(mail.build())
            sg.api(request)
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }


    }

}

object MailSenderService {
    var mailSender:MailSender? = null
    fun sendMail(to:String,subject:String,message:String) {
        if (mailSender == null) {
            mailSender = when (Setup.mailSenderType()) {
                MailSenderType.CONSOLE -> ConsoleDummyMaiSender()
                MailSenderType.SENDGRID -> SendGridMailSender()
            }

        }
        mailSender!!.sendMail(to,subject,message)
    }
}