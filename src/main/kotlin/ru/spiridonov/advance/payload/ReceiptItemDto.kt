package ru.spiridonov.advance.payload

import java.math.BigDecimal

data class ReceiptItemDto(
    val id: Long?,
    val name: String,
    val quantity: Double,
    val price: BigDecimal,
    val amount: BigDecimal,
    val taxRate: Int?
)