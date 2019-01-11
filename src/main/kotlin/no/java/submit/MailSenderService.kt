package no.java.submit

interface MailSender {
    fun sendMail(to:String,subject:String,message:String)
}

class ConsoleDummyMaiSender:MailSender {
    override fun sendMail(to:String,subject:String,message:String) {
        println("Sending mail to $to regarding $subject -> $message")
    }
}

object MailSenderService {
    var mailSender:MailSender? = null
    fun sendMail(to:String,subject:String,message:String) {
        if (mailSender == null) {
            mailSender = ConsoleDummyMaiSender()
        }
        mailSender!!.sendMail(to,subject,message)
    }
}