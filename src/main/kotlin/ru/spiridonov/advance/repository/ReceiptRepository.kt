package ru.spiridonov.advance.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.advance.model.Receipt

interface ReceiptRepository : JpaRepository<Receipt, Long>