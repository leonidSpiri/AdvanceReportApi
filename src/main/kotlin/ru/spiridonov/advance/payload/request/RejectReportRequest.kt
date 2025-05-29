package ru.spiridonov.advance.payload.request

data class RejectReportRequest(
    val reason: String,
    val comments: String?
)