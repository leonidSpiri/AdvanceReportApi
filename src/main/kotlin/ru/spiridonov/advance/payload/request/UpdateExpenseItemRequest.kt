package ru.spiridonov.advance.payload.request

import java.math.BigDecimal
import java.time.LocalDate

data class UpdateExpenseItemRequest(
    val description: String?,
    val date: LocalDate?,
    val quantity: Int?,
    val unitPrice: BigDecimal?
)