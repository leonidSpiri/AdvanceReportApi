package ru.spiridonov.advance.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.advance.model.AdvanceReport
import ru.spiridonov.advance.model.User
import ru.spiridonov.advance.model.enums.ReportStatus

interface AdvanceReportRepository : JpaRepository<AdvanceReport, Long> {
    fun findByUser(user: User): List<AdvanceReport>
    fun findBySupervisor(supervisor: User): List<AdvanceReport>
    fun findByStatus(status: ReportStatus): List<AdvanceReport>
    fun findByUserAndStatusIn(user: User, statuses: List<ReportStatus>): List<AdvanceReport>
    fun findTopByUserOrderByCreatedAtDesc(user: User): AdvanceReport?
}