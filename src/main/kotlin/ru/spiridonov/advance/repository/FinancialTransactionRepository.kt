package ru.spiridonov.advance.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.advance.model.AdvanceReport
import ru.spiridonov.advance.model.FinancialTransaction
import ru.spiridonov.advance.model.User

interface FinancialTransactionRepository : JpaRepository<FinancialTransaction, Long> {
    fun findByUser(user: User): List<FinancialTransaction>

    fun findByUserAndAdvanceReport(user: User, advanceReport: AdvanceReport): List<FinancialTransaction>
}