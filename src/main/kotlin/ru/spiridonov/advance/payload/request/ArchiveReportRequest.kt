package ru.spiridonov.advance.payload.request

data class ArchiveReportRequest(
    val accountingReferenceNumber: String?,
    val comments: String?
)