package ru.spiridonov.advance.payload.request

import ru.spiridonov.advance.model.enums.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

data class CreateFinancialTransactionRequest(
    val userId: Long,
    val amount: BigDecimal,
    val type: TransactionType,
    val description: String?,
    val transactionDate: LocalDate
)