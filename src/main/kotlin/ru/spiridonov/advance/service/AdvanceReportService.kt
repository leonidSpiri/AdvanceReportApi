package ru.spiridonov.advance.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.spiridonov.advance.extensions.toDto
import ru.spiridonov.advance.model.AdvanceReport
import ru.spiridonov.advance.model.User
import ru.spiridonov.advance.model.enums.ReportStatus
import ru.spiridonov.advance.payload.AdvanceReportDto
import ru.spiridonov.advance.payload.request.*
import ru.spiridonov.advance.repository.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class AdvanceReportService(
    val advanceReportRepository: AdvanceReportRepository,
    private val userRepository: UserRepository,
    private val attachmentRepository: AttachmentRepository,
    private val expenseItemRepository: ExpenseItemRepository,
    private val financialTransactionRepository: FinancialTransactionRepository
) {
    fun getAllReports(): List<AdvanceReportDto> {
        return advanceReportRepository.findAll().map { it.toDto(expenseItemRepository, attachmentRepository) }
    }

    fun createReport(userId: Long, request: CreateAdvanceReportRequest): AdvanceReportDto {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found") }

        val previousBalance = calculateUserBalance(user)

        val report = AdvanceReport(
            title = request.title,
            project = request.project,
            businessTripLocation = request.businessTripLocation,
            startDate = request.startDate,
            endDate = request.endDate,
            user = user,
            previousBalance = previousBalance,
            currentBalance = previousBalance,
            additionalInfo = request.additionalInfo
        )

        return advanceReportRepository.save(report).toDto(expenseItemRepository, attachmentRepository)
    }

    fun getReportById(id: Long): AdvanceReportDto {
        return advanceReportRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Report not found") }
            .toDto(expenseItemRepository, attachmentRepository)
    }

    fun getReportsByUser(userId: Long): List<AdvanceReportDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found") }
        return advanceReportRepository.findByUser(user).map { it.toDto(expenseItemRepository, attachmentRepository) }
    }

    fun getReportsBySupervisor(supervisorId: Long): List<AdvanceReportDto> {
        val supervisor = userRepository.findById(supervisorId)
            .orElseThrow { EntityNotFoundException("Supervisor not found") }
        return advanceReportRepository.findBySupervisor(supervisor)
            .map { it.toDto(expenseItemRepository, attachmentRepository) }
    }

    fun getReportsByStatus(status: ReportStatus): List<AdvanceReportDto> {
        return advanceReportRepository.findByStatus(status)
            .map { it.toDto(expenseItemRepository, attachmentRepository) }
    }

    fun updateReport(id: Long, request: UpdateAdvanceReportRequest): AdvanceReportDto {
        val report = advanceReportRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Report not found") }

        if (report.status != ReportStatus.DRAFT) {
            throw IllegalStateException("Only draft reports can be updated")
        }

        request.title?.let { report.title = it }
        request.project?.let { report.project = it }
        request.businessTripLocation?.let { report.businessTripLocation = it }
        request.startDate?.let { report.startDate = it }
        request.endDate?.let { report.endDate = it }
        request.additionalInfo?.let { report.additionalInfo = it }
        report.updatedAt = LocalDateTime.now()

        return advanceReportRepository.save(report).toDto(expenseItemRepository, attachmentRepository)
    }

    fun deleteReport(id: Long) {
        val report = advanceReportRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Report not found") }

        if (report.status != ReportStatus.DRAFT) {
            throw IllegalStateException("Only draft reports can be deleted")
        }

        advanceReportRepository.delete(report)
    }

    fun submitReport(id: Long, supervisorId: Long): AdvanceReportDto {
        val report = advanceReportRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Report not found") }

        if (report.status != ReportStatus.DRAFT) {
            throw IllegalStateException("Only draft reports can be submitted")
        }

        val supervisor = userRepository.findById(supervisorId)
            .orElseThrow { EntityNotFoundException("Supervisor not found") }

        report.supervisor = supervisor
        report.status = ReportStatus.SUBMITTED
        report.updatedAt = LocalDateTime.now()
        recalculateReportTotals(report)

        return advanceReportRepository.save(report).toDto(expenseItemRepository, attachmentRepository)
    }

    fun approveReport(id: Long, request: ApproveReportRequest): AdvanceReportDto {
        val report = advanceReportRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Report not found") }

        if (report.status != ReportStatus.SUBMITTED && report.status != ReportStatus.UNDER_REVIEW) {
            throw IllegalStateException("Only submitted or under review reports can be approved")
        }

        report.status = ReportStatus.APPROVED
        report.supervisorComments = request.comments
        report.updatedAt = LocalDateTime.now()

        return advanceReportRepository.save(report).toDto(expenseItemRepository, attachmentRepository)
    }

    fun rejectReport(id: Long, request: RejectReportRequest): AdvanceReportDto {
        val report = advanceReportRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Report not found") }

        if (report.status != ReportStatus.SUBMITTED && report.status != ReportStatus.UNDER_REVIEW) {
            throw IllegalStateException("Only submitted or under review reports can be rejected")
        }

        report.status = ReportStatus.REJECTED
        report.rejectionReason = request.reason
        report.supervisorComments = request.comments
        report.updatedAt = LocalDateTime.now()

        return advanceReportRepository.save(report).toDto(expenseItemRepository, attachmentRepository)
    }

    fun archiveReport(id: Long, request: ArchiveReportRequest): AdvanceReportDto {
        val report = advanceReportRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Report not found") }

        if (report.status != ReportStatus.APPROVED) {
            throw IllegalStateException("Only approved reports can be archived")
        }

        report.status = ReportStatus.ARCHIVED
        report.accountingReferenceNumber = request.accountingReferenceNumber
        report.accountingComments = request.comments
        report.updatedAt = LocalDateTime.now()

        return advanceReportRepository.save(report).toDto(expenseItemRepository, attachmentRepository)
    }

    private fun calculateUserBalance(user: User): BigDecimal {
        return financialTransactionRepository.calculateTotalBalanceForUser(user) ?: BigDecimal.ZERO
    }

    private fun recalculateReportTotals(report: AdvanceReport) {
        val items = expenseItemRepository.findByReport(report)
        report.totalAmount = items.sumOf { it.totalCost }
        report.currentBalance = report.previousBalance - report.totalAmount
    }
}
