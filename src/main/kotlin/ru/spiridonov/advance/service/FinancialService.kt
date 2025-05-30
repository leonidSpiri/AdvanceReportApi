package ru.spiridonov.advance.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.spiridonov.advance.extensions.toDto
import ru.spiridonov.advance.model.AdvanceReport
import ru.spiridonov.advance.model.FinancialTransaction
import ru.spiridonov.advance.model.User
import ru.spiridonov.advance.model.enums.ReportStatus
import ru.spiridonov.advance.model.enums.TransactionType
import ru.spiridonov.advance.payload.FinancialTransactionDto
import ru.spiridonov.advance.payload.request.CreateFinancialTransactionRequest
import ru.spiridonov.advance.repository.AdvanceReportRepository
import ru.spiridonov.advance.repository.ExpenseItemRepository
import ru.spiridonov.advance.repository.FinancialTransactionRepository
import ru.spiridonov.advance.repository.UserRepository
import java.time.LocalDateTime

@Service
@Transactional
class FinancialService(
    private val financialTransactionRepository: FinancialTransactionRepository,
    private val userRepository: UserRepository,
    private val advanceReportRepository: AdvanceReportRepository,
    private val expenseItemRepository: ExpenseItemRepository
) {
    fun createTransaction(request: CreateFinancialTransactionRequest, createdById: Long): FinancialTransactionDto {
        val user = userRepository.findById(request.userId)
            .orElseThrow { EntityNotFoundException("User not found") }

        val createdBy = userRepository.findById(createdById)
            .orElseThrow { EntityNotFoundException("Creator not found") }

        // Check if a user has an active advance report for money transfers
        if (request.type == TransactionType.ADVANCE) {
            val activeReport = getActiveAdvanceReport(user)
                ?: throw IllegalStateException("Employee must have an active advance report to receive money transfers")

            val transaction = FinancialTransaction(
                user = user,
                amount = request.amount,
                type = request.type,
                description = request.description,
                transactionDate = request.transactionDate,
                createdBy = createdBy,
                advanceReport = activeReport
            )

            val savedTransaction = financialTransactionRepository.save(transaction)

            updateAdvanceReportBalance(activeReport)

            return savedTransaction.toDto()
        } else {
            val transaction = FinancialTransaction(
                user = user,
                amount = request.amount.negate(),
                type = request.type,
                description = request.description,
                transactionDate = request.transactionDate,
                createdBy = createdBy
            )
            return financialTransactionRepository.save(transaction).toDto()
        }
    }

    fun getTransactionsByUser(userId: Long): List<FinancialTransactionDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found") }

        return financialTransactionRepository.findByUser(user).map { it.toDto() }
    }

    fun getAllTransactions(): List<FinancialTransactionDto> {
        return financialTransactionRepository.findAll().map { it.toDto() }
    }

    private fun getActiveAdvanceReport(user: User): AdvanceReport? {
        val activeStatuses = listOf(ReportStatus.DRAFT)
        return advanceReportRepository.findByUserAndStatusIn(user, activeStatuses).firstOrNull()
    }

    private fun updateAdvanceReportBalance(report: AdvanceReport) {
        val advances = financialTransactionRepository.findByUserAndAdvanceReport(report.user, report)
            .filter { it.type == TransactionType.ADVANCE }
            .sumOf { it.amount }

        val expenses = expenseItemRepository.findByReport(report).sumOf { it.totalCost }

        report.currentBalance = report.previousBalance + advances - expenses
        report.updatedAt = LocalDateTime.now()
        advanceReportRepository.save(report)
    }
}