package ru.spiridonov.advance.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal
import java.time.LocalDate
import org.springframework.data.jpa.repository.Query
import ru.spiridonov.advance.model.FinancialTransaction
import ru.spiridonov.advance.model.User

interface FinancialTransactionRepository : JpaRepository<FinancialTransaction, Long> {
    fun findByUser(user: User): List<FinancialTransaction>

    @Query("SELECT SUM(ft.amount) FROM FinancialTransaction ft WHERE ft.user = :user")
    fun calculateTotalBalanceForUser(user: User): BigDecimal?

    @Query("SELECT SUM(ft.amount) FROM FinancialTransaction ft WHERE ft.user = :user AND ft.transactionDate <= :date")
    fun calculateBalanceForUserAsOfDate(user: User, date: LocalDate): BigDecimal?
}