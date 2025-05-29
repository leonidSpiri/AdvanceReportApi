package ru.spiridonov.advance.payload

import java.math.BigDecimal
import java.time.LocalDateTime

data class UserBalanceDto(
    val userId: Long,
    val balance: BigDecimal,
    val lastUpdated: LocalDateTime
)