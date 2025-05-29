package ru.spiridonov.advance.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.spiridonov.advance.extensions.toDto
import ru.spiridonov.advance.model.FinancialTransaction
import ru.spiridonov.advance.model.enums.TransactionType
import ru.spiridonov.advance.payload.FinancialTransactionDto
import ru.spiridonov.advance.payload.UserBalanceDto
import ru.spiridonov.advance.payload.request.CreateFinancialTransactionRequest
import ru.spiridonov.advance.repository.FinancialTransactionRepository
import ru.spiridonov.advance.repository.UserRepository
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class FinancialService(
    private val financialTransactionRepository: FinancialTransactionRepository,
    private val userRepository: UserRepository
) {
    fun createTransaction(request: CreateFinancialTransactionRequest, createdById: Long): FinancialTransactionDto {
        val user = userRepository.findById(request.userId)
            .orElseThrow { EntityNotFoundException("User not found") }

        val createdBy = userRepository.findById(createdById)
            .orElseThrow { EntityNotFoundException("Creator not found") }

        val transaction = FinancialTransaction(
            user = user,
            amount = if (request.type == TransactionType.ADVANCE) request.amount else request.amount.negate(),
            type = request.type,
            description = request.description,
            transactionDate = request.transactionDate,
            createdBy = createdBy
        )

        return financialTransactionRepository.save(transaction).toDto()
    }

    fun getUserBalance(userId: Long): UserBalanceDto {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found") }

        val balance = financialTransactionRepository.calculateTotalBalanceForUser(user) ?: BigDecimal.ZERO

        return UserBalanceDto(
            userId = userId,
            balance = balance,
            lastUpdated = LocalDateTime.now()
        )
    }

    fun getTransactionsByUser(userId: Long): List<FinancialTransactionDto> {
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found") }

        return financialTransactionRepository.findByUser(user).map { it.toDto() }
    }

    fun getAllTransactions(): List<FinancialTransactionDto> {
        return financialTransactionRepository.findAll().map { it.toDto() }
    }
}