package ru.spiridonov.advance.payload

import ru.spiridonov.advance.model.enums.ReportStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class AdvanceReportDto(
    val id: Long?,
    val title: String,
    val project: String,
    val businessTripLocation: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val user: UserDto,
    val supervisor: UserDto?,
    val status: ReportStatus,
    val rejectionReason: String?,
    val previousBalance: BigDecimal,
    val totalAmount: BigDecimal,
    val currentBalance: BigDecimal,
    val additionalInfo: String?,
    val supervisorComments: String?,
    val accountingComments: String?,
    val accountingReferenceNumber: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val items: List<ExpenseItemDto>
)