package no.java.submit

import no.java.submit.commands.generateTokenFromEmail
import java.net.URLEncoder

class GenerateEmcryptPw {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size < 2) {
                println("Usage <setupfile> <email>")
                return
            }
            setupFile = args[0]
            val token = generateTokenFromEmail(args[1])
            println("Generated token")
            println(token)
            println("https://talks.javazone.no/emailLogin.html?token=${URLEncoder.encode(token,"UTF-8")}")
        }
    }
}