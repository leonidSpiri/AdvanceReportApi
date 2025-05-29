package ru.spiridonov.advance.payload

import ru.spiridonov.advance.model.enums.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class FinancialTransactionDto(
    val id: Long?,
    val user: UserDto,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String?,
    val transactionDate: LocalDate,
    val createdBy: UserDto,
    val createdAt: LocalDateTime
)