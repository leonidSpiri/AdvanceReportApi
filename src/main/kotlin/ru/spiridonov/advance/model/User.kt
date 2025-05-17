package ru.spiridonov.advance.model

import jakarta.persistence.*
import ru.spiridonov.advance.model.enums.UserRole
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true)
    val username: String,

    @Column(nullable = false)
    var passwordHash: String,

    val fullName: String,

    val email: String,

    @Enumerated(EnumType.STRING)
    val role: UserRole,

    val departmentId: Long? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
)