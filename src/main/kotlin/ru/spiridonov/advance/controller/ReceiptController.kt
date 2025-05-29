package ru.spiridonov.advance.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.spiridonov.advance.payload.AttachmentDto
import ru.spiridonov.advance.payload.ReceiptDto
import ru.spiridonov.advance.payload.request.ValidateReceiptRequest
import ru.spiridonov.advance.service.FileUploadService
import ru.spiridonov.advance.service.ReceiptService

@RestController
@RequestMapping("/api/v1/receipts")
class ReceiptController(
    private val receiptService: ReceiptService,
    private val fileUploadService: FileUploadService
) {
    @PostMapping("/validate")
    fun validateReceipt(@RequestBody request: ValidateReceiptRequest): ResponseEntity<ReceiptDto> {
        return ResponseEntity.ok(receiptService.validateReceipt(request))
    }

    @GetMapping("/{id}")
    fun getReceiptById(@PathVariable id: Long): ResponseEntity<ReceiptDto> {
        return ResponseEntity.ok(receiptService.getReceiptById(id))
    }

    @PostMapping("/{id}/attachments")
    fun uploadReceiptAttachment(
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<AttachmentDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileUploadService.uploadReceiptAttachment(id, file))
    }

    @PostMapping("/{receiptId}/attach/{expenseItemId}")
    fun attachReceiptToExpenseItem(
        @PathVariable receiptId: Long,
        @PathVariable expenseItemId: Long
    ): ResponseEntity<Void> {
        receiptService.attachReceiptToExpenseItem(expenseItemId, receiptId)
        return ResponseEntity.ok().build()
    }
}