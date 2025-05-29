package ru.spiridonov.advance.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.spiridonov.advance.extensions.toDto
import ru.spiridonov.advance.model.Attachment
import ru.spiridonov.advance.payload.AttachmentDto
import ru.spiridonov.advance.repository.AttachmentRepository
import ru.spiridonov.advance.repository.ExpenseItemRepository
import ru.spiridonov.advance.repository.ReceiptRepository
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
@Transactional
class FileUploadService(
    private val attachmentRepository: AttachmentRepository,
    private val receiptRepository: ReceiptRepository,
    private val expenseItemRepository: ExpenseItemRepository
) {
    @Value("\${file.upload.dir:/uploads}")
    private lateinit var uploadDir: String

    fun uploadReceiptAttachment(receiptId: Long, file: MultipartFile): AttachmentDto {
        val receipt = receiptRepository.findById(receiptId)
            .orElseThrow { EntityNotFoundException("Receipt not found") }

        val fileName = generateUniqueFileName(file.originalFilename ?: "attachment")
        val filePath = saveFile(file, fileName)

        val attachment = Attachment(
            receipt = receipt,
            fileName = fileName,
            fileType = file.contentType ?: "application/octet-stream",
            fileSize = file.size,
            storagePath = filePath
        )

        return attachmentRepository.save(attachment).toDto()
    }

    fun uploadExpenseItemAttachment(expenseItemId: Long, file: MultipartFile): AttachmentDto {
        val expenseItem = expenseItemRepository.findById(expenseItemId)
            .orElseThrow { EntityNotFoundException("Expense item not found") }

        val fileName = generateUniqueFileName(file.originalFilename ?: "attachment")
        val filePath = saveFile(file, fileName)

        val attachment = Attachment(
            expenseItem = expenseItem,
            fileName = fileName,
            fileType = file.contentType ?: "application/octet-stream",
            fileSize = file.size,
            storagePath = filePath
        )

        return attachmentRepository.save(attachment).toDto()
    }

    private fun generateUniqueFileName(originalFileName: String): String {
        val timestamp = System.currentTimeMillis()
        val extension = originalFileName.substringAfterLast(".", "")
        val baseName = originalFileName.substringBeforeLast(".")
        return "${baseName}_${timestamp}${if (extension.isNotEmpty()) ".$extension" else ""}"
    }

    private fun saveFile(file: MultipartFile, fileName: String): String {
        val uploadPath = Paths.get(uploadDir)
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath)
        }

        val filePath = uploadPath.resolve(fileName)
        Files.copy(file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

        return filePath.toString()
    }
}