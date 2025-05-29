package ru.spiridonov.advance.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.spiridonov.advance.extensions.toDto
import ru.spiridonov.advance.model.Receipt
import ru.spiridonov.advance.model.ReceiptItem
import ru.spiridonov.advance.payload.ReceiptDto
import ru.spiridonov.advance.payload.request.ValidateReceiptRequest
import ru.spiridonov.advance.repository.ExpenseItemRepository
import ru.spiridonov.advance.repository.ReceiptRepository
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
@Transactional
class ReceiptService(
    private val receiptRepository: ReceiptRepository,
    private val expenseItemRepository: ExpenseItemRepository
) {
    fun validateReceipt(request: ValidateReceiptRequest): ReceiptDto {
        // TODO: Mock implementation - replace with actual Russian tax service API integration
        val receipt = Receipt(
            rawQrData = request.qrData,
            validated = true,
            storeName = "Test Store",
            storeInn = "1234567890",
            receiptNumber = "12345",
            receiptDate = LocalDateTime.now(),
            totalAmount = BigDecimal("100.00"),
            validatedAt = LocalDateTime.now()
        )

        // Add mock receipt items
        val receiptItem = ReceiptItem(
            receipt = receipt,
            name = "Test Item",
            quantity = 1.0,
            price = BigDecimal("100.00"),
            amount = BigDecimal("100.00"),
            taxRate = 20
        )
        receipt.items.add(receiptItem)

        return receiptRepository.save(receipt).toDto()
    }

    fun getReceiptById(id: Long): ReceiptDto {
        return receiptRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Receipt not found") }
            .toDto()
    }

    fun attachReceiptToExpenseItem(expenseItemId: Long, receiptId: Long) {
        val expenseItem = expenseItemRepository.findById(expenseItemId)
            .orElseThrow { EntityNotFoundException("Expense item not found") }

        val receipt = receiptRepository.findById(receiptId)
            .orElseThrow { EntityNotFoundException("Receipt not found") }

        expenseItem.receipt = receipt
        expenseItem.hasReceipt = true
        expenseItem.updatedAt = LocalDateTime.now()

        expenseItemRepository.save(expenseItem)
    }
}