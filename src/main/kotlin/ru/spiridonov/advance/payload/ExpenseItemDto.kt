package ru.spiridonov.advance.payload

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class ExpenseItemDto(
    val id: Long?,
    val description: String,
    val date: LocalDate,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val totalCost: BigDecimal,
    val hasReceipt: Boolean,
    val receipt: ReceiptDto?,
    val attachments: List<AttachmentDto>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)