package com.example.model

import com.example.utils.LocalDateTimeSerializer
import com.example.utils.TimeProvider
import com.example.utils.UUIDSerializer
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.serialization.Serializable

private val timeProvider = TimeProvider()

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID? = null,
    val username: String,
    val email: String,
    val fullName: String,
    val password: String,
    val role: Role,
    val rememberMe: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = timeProvider.getCurrentTime(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastUpdatedAt: LocalDateTime? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val lastLoginAt: LocalDateTime? = null,
)

enum class Role {
    ADMIN,
    USER,
    GUEST
}