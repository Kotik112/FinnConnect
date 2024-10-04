package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val accessToken: String,
    val clientId: String,
    val expiresIn: Int,
    val refreshToken: String?,
    val tokenType: String,
    val userId: String,
    val issuedAt: Long
)