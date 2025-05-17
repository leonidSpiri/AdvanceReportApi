package ru.spiridonov.advance.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.advance.model.AdvanceReport
import ru.spiridonov.advance.model.ExpenseItem

interface ExpenseItemRepository : JpaRepository<ExpenseItem, Long> {
    fun findByReport(report: AdvanceReport): List<ExpenseItem>
    fun deleteByReport(report: AdvanceReport)
}