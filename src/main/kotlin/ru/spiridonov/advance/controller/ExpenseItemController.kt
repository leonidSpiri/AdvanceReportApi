package ru.spiridonov.advance.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.spiridonov.advance.payload.ExpenseItemDto
import ru.spiridonov.advance.payload.request.CreateExpenseItemRequest
import ru.spiridonov.advance.payload.request.UpdateExpenseItemRequest
import ru.spiridonov.advance.service.ExpenseItemService

@RestController
@RequestMapping("/api/v1/reports/{reportId}/items")
@SecurityRequirement(name = "bearerAuth")
class ExpenseItemController(
    private val expenseItemService: ExpenseItemService
) {
    @GetMapping
    fun getExpenseItems(@PathVariable reportId: Long): ResponseEntity<List<ExpenseItemDto>> {
        return ResponseEntity.ok(expenseItemService.getExpenseItemsByReport(reportId))
    }

    @GetMapping("/{id}")
    fun getExpenseItemById(@PathVariable reportId: Long, @PathVariable id: Long): ResponseEntity<ExpenseItemDto> {
        return ResponseEntity.ok(expenseItemService.getExpenseItemById(reportId, id))
    }

    @PostMapping
    fun createExpenseItem(
        @PathVariable reportId: Long,
        @RequestBody request: CreateExpenseItemRequest
    ): ResponseEntity<ExpenseItemDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseItemService.createExpenseItem(reportId, request))
    }

    @PutMapping("/{id}")
    fun updateExpenseItem(
        @PathVariable reportId: Long,
        @PathVariable id: Long,
        @RequestBody request: UpdateExpenseItemRequest
    ): ResponseEntity<ExpenseItemDto> {
        return ResponseEntity.ok(expenseItemService.updateExpenseItem(reportId, id, request))
    }

    @DeleteMapping("/{id}")
    fun deleteExpenseItem(@PathVariable reportId: Long, @PathVariable id: Long): ResponseEntity<Void> {
        expenseItemService.deleteExpenseItem(reportId, id)
        return ResponseEntity.noContent().build()
    }
}