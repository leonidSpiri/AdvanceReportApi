package ru.spiridonov.advance.extensions

import jakarta.persistence.EntityNotFoundException
import ru.spiridonov.advance.model.*
import ru.spiridonov.advance.payload.*
import ru.spiridonov.advance.repository.AttachmentRepository
import ru.spiridonov.advance.repository.ExpenseItemRepository
import ru.spiridonov.advance.service.UserService

fun User.toDto() = UserDto(
    id = id!!,
    username = username,
    fullName = fullName,
    email = email,
    role = role,
    departmentId = departmentId
)

fun AdvanceReport.toDto(expenseItemRepository: ExpenseItemRepository, attachmentRepository: AttachmentRepository) =
    AdvanceReportDto(
        id = id,
        title = title,
        project = project,
        businessTripLocation = businessTripLocation,
        startDate = startDate,
        endDate = endDate,
        user = user.toDto(),
        supervisor = supervisor?.toDto(),
        status = status,
        rejectionReason = rejectionReason,
        previousBalance = previousBalance,
        totalAmount = totalAmount,
        currentBalance = currentBalance,
        additionalInfo = additionalInfo,
        supervisorComments = supervisorComments,
        accountingComments = accountingComments,
        accountingReferenceNumber = accountingReferenceNumber,
        createdAt = createdAt,
        updatedAt = updatedAt,
        items = expenseItemRepository.findByReport(this).map {
            it.toDto(
                attachmentRepository = attachmentRepository
            )
        }
    )

fun ExpenseItem.toDto(attachmentRepository: AttachmentRepository) = ExpenseItemDto(
    id = id,
    description = description,
    date = date,
    quantity = quantity,
    unitPrice = unitPrice,
    totalCost = totalCost,
    hasReceipt = hasReceipt,
    receipt = receipt?.toDto(),
    attachments = attachmentRepository.findByExpenseItem(this).map { it.toDto() },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Receipt.toDto() = ReceiptDto(
    id = id,
    rawQrData = rawQrData,
    validated = validated,
    storeName = storeName,
    storeInn = storeInn,
    receiptNumber = receiptNumber,
    receiptDate = receiptDate,
    totalAmount = totalAmount,
    validatedAt = validatedAt,
    items = items.map { it.toDto() },
    attachments = attachments.map { it.toDto() },
    createdAt = createdAt
)

fun ReceiptItem.toDto() = ReceiptItemDto(
    id = id,
    name = name,
    quantity = quantity,
    price = price,
    amount = amount,
    taxRate = taxRate
)

fun Attachment.toDto() = AttachmentDto(
    id = id,
    fileName = fileName,
    fileType = fileType,
    fileSize = fileSize,
    createdAt = createdAt
)

fun FinancialTransaction.toDto() = FinancialTransactionDto(
    id = id,
    user = user.toDto(),
    amount = amount,
    type = type,
    description = description,
    transactionDate = transactionDate,
    createdBy = createdBy.toDto(),
    createdAt = createdAt
)

fun UserService.getUserByUsername(username: String): UserDto {
    return userRepository.findByUsername(username)
        .orElseThrow { EntityNotFoundException("User not found") }
        .toDto()
}

//fun AdvanceReportService.getAllReports(): List<AdvanceReportDto> {
//    return advanceReportRepository.findAll().map { it.toDto() }
//}