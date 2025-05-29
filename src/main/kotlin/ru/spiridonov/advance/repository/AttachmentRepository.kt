package ru.spiridonov.advance.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.advance.model.Attachment
import ru.spiridonov.advance.model.ExpenseItem
import ru.spiridonov.advance.model.Receipt

interface AttachmentRepository : JpaRepository<Attachment, Long> {
    fun findByExpenseItem(expenseItem: ExpenseItem): List<Attachment>

    fun findByReceipt(receipt: Receipt): List<Attachment>
}