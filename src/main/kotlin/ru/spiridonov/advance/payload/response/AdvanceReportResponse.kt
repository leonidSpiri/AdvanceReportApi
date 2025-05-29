package ru.spiridonov.advance.payload.response

import ru.spiridonov.advance.model.enums.ReportStatus

data class AdvanceReportResponse (
    val id: Long? = null,
    val userId: Long,
    val supervisorId: Long?,
    val status: ReportStatus,
    val description: String?
)