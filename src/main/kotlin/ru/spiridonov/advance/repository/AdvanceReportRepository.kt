package ru.spiridonov.advance.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.advance.model.AdvanceReport
import ru.spiridonov.advance.model.User
import ru.spiridonov.advance.model.enums.ReportStatus
import java.time.LocalDateTime
import java.util.List

interface AdvanceReportRepository : JpaRepository<AdvanceReport, Long> {
    fun findByUser(user: User): List<AdvanceReport>
    fun findBySupervisor(supervisor: User): List<AdvanceReport>
    fun findByStatus(status: ReportStatus): List<AdvanceReport>
    fun findByUserAndStatus(user: User, status: ReportStatus): List<AdvanceReport>
    fun findByUserAndCreatedAtBetween(user: User, start: LocalDateTime, end: LocalDateTime): List<AdvanceReport>
}