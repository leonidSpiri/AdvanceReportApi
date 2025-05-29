package ru.spiridonov.advance.service

import org.springframework.stereotype.Service
import ru.spiridonov.advance.model.AdvanceReport

@Service
class NotificationService {
    fun sendReportSubmittedNotification(report: AdvanceReport) {
        // Implementation for sending notifications when report is submitted
    }

    fun sendReportApprovedNotification(report: AdvanceReport) {
        // Implementation for sending notifications when report is approved
    }

    fun sendReportRejectedNotification(report: AdvanceReport) {
        // Implementation for sending notifications when report is rejected
    }
}