package ru.spiridonov.advance.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.advance.model.Attachment
import ru.spiridonov.advance.model.ExpenseItem

interface AttachmentRepository : JpaRepository<Attachment, Long> {
    fun findByExpenseItem(expenseItem: ExpenseItem): List<Attachment>
}