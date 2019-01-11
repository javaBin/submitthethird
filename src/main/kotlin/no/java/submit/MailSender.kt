package no.java.submit

object MailSender {
    fun sendMail(to:String,subject:String,message:String) {
        println("Sending mail to $to regarding $subject -> $message")
    }
}