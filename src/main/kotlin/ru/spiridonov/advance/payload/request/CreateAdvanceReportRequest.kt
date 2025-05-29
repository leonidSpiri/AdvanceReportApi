package ru.spiridonov.advance.payload.request

import java.time.LocalDate

data class CreateAdvanceReportRequest(
    val title: String,
    val project: String,
    val businessTripLocation: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val additionalInfo: String?
)