package no.java.submit.domain

import no.java.submit.Setup
import java.time.LocalDateTime

object SubmissionsClosedService {
    fun isClosed():Boolean {
        val closeTime:LocalDateTime = Setup.closeTime()?:return false
        return LocalDateTime.now().isAfter(closeTime)
    }

    fun okToSubmit(password:String?):Boolean {
        if (!isClosed()) {
            return true
        }
        return (Setup.lateSubmitPassword() == password)
    }
}