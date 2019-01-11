package no.java.submit

data class SentMail(val to: String, val subject: String, val message: String)

class MockMailSender:MailSender {
    val sentMail:MutableList<SentMail> = mutableListOf()

    override fun sendMail(to: String, subject: String, message: String) {
        sentMail.add(SentMail(to,subject,message))
    }

}