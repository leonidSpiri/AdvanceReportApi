package ru.spiridonov.advance.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.spiridonov.advance.extensions.toDto
import ru.spiridonov.advance.model.AdvanceReport
import ru.spiridonov.advance.model.ExpenseItem
import ru.spiridonov.advance.model.enums.ReportStatus
import ru.spiridonov.advance.payload.ExpenseItemDto
import ru.spiridonov.advance.payload.request.CreateExpenseItemRequest
import ru.spiridonov.advance.payload.request.UpdateExpenseItemRequest
import ru.spiridonov.advance.repository.AdvanceReportRepository
import ru.spiridonov.advance.repository.AttachmentRepository
import ru.spiridonov.advance.repository.ExpenseItemRepository
import ru.spiridonov.advance.repository.ReceiptRepository
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class ExpenseItemService(
    private val expenseItemRepository: ExpenseItemRepository,
    private val advanceReportRepository: AdvanceReportRepository,
    private val attachmentRepository: AttachmentRepository,
    private val receiptRepository: ReceiptRepository
) {
    fun createExpenseItem(reportId: Long, request: CreateExpenseItemRequest): ExpenseItemDto {
        val report = advanceReportRepository.findById(reportId)
            .orElseThrow { EntityNotFoundException("Report not found") }

        if (report.status != ReportStatus.DRAFT) {
            throw IllegalStateException("Can only add items to draft reports")
        }

        val totalCost = request.unitPrice.multiply(BigDecimal(request.quantity))

        val expenseItem = ExpenseItem(
            report = report,
            description = request.description,
            date = request.date,
            quantity = request.quantity,
            unitPrice = request.unitPrice,
            totalCost = totalCost,
            hasReceipt = request.hasReceipt
        )

        val savedItem = expenseItemRepository.save(expenseItem)
        updateReportTotals(report)

        return savedItem.toDto(attachmentRepository)
    }

    fun getExpenseItemsByReport(reportId: Long): List<ExpenseItemDto> {
        val report = advanceReportRepository.findById(reportId)
            .orElseThrow { EntityNotFoundException("Report not found") }
        return expenseItemRepository.findByReport(report).map { it.toDto(attachmentRepository) }
    }

    fun getExpenseItemById(reportId: Long, itemId: Long): ExpenseItemDto {
        val item = expenseItemRepository.findById(itemId)
            .orElseThrow { EntityNotFoundException("Expense item not found") }

        if (item.report.id != reportId) {
            throw IllegalArgumentException("Item does not belong to the specified report")
        }

        return item.toDto(attachmentRepository)
    }

    fun updateExpenseItem(reportId: Long, itemId: Long, request: UpdateExpenseItemRequest): ExpenseItemDto {
        val item = expenseItemRepository.findById(itemId)
            .orElseThrow { EntityNotFoundException("Expense item not found") }

        if (item.report.id != reportId) {
            throw IllegalArgumentException("Item does not belong to the specified report")
        }

        if (item.report.status != ReportStatus.DRAFT) {
            throw IllegalStateException("Can only update items in draft reports")
        }

        request.description?.let { item.description = it }
        request.date?.let { item.date = it }
        request.quantity?.let {
            item.quantity = it
            item.totalCost = item.unitPrice.multiply(BigDecimal(it))
        }
        request.unitPrice?.let {
            item.unitPrice = it
            item.totalCost = it.multiply(BigDecimal(item.quantity))
        }
        item.updatedAt = LocalDateTime.now()

        val savedItem = expenseItemRepository.save(item)
        updateReportTotals(item.report)

        return savedItem.toDto(attachmentRepository)
    }

    fun deleteExpenseItem(reportId: Long, itemId: Long) {
        val item = expenseItemRepository.findById(itemId)
            .orElseThrow { EntityNotFoundException("Expense item not found") }

        if (item.report.id != reportId) {
            throw IllegalArgumentException("Item does not belong to the specified report")
        }

        if (item.report.status != ReportStatus.DRAFT) {
            throw IllegalStateException("Can only delete items from draft reports")
        }

        val report = item.report
        expenseItemRepository.delete(item)
        updateReportTotals(report)
    }

    private fun updateReportTotals(report: AdvanceReport) {
        val items = expenseItemRepository.findByReport(report)
        report.totalAmount = items.sumOf { it.totalCost }
        report.currentBalance = report.previousBalance - report.totalAmount
        report.updatedAt = LocalDateTime.now()
        advanceReportRepository.save(report)
    }
}