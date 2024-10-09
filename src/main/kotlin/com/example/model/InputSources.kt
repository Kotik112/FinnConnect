package com.example.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("client_id") val clientId: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String? = null, // Make refresh_token optional
    @SerialName("token_type") val tokenType: String,
    @SerialName("user_id") val userId: String,
    val issuedAt: Long = 0 // This is added by your application, so set a default value
)

@Serializable
data class WhoAmIResponse(
    val authenticated: Boolean,
    @SerialName("client_id") val clientId: String,
    @SerialName("user_id") val userId: String
)

@Serializable
data class MonzoAccountsResponse(
    val accounts: List<MonzoAccount>
)

@Serializable
data class MonzoAccount(
    val id: String,
    val description: String,
    val created: String
)

@Serializable
data class MonzoErrorResponse(
    val code: String,
    val message: String,
    val params: Map<String, String>? = null
)

@Serializable
data class BalanceResponse(
    val balance: Int,
    @SerialName("total_balance") val totalBalance: Int,
    @SerialName("balance_including_flexible_savings") val balanceIncludingFlexibleSavings: Int,
    val currency: String,
    @SerialName("spend_today") val spendToday: Int,
    @SerialName("local_currency") val localCurrency: String? = null,
    @SerialName("local_exchange_rate") val localExchangeRate: Double? = null,
    @SerialName("local_spend") val localSpend: List<LocalSpend>? = emptyList()
)

@Serializable
data class LocalSpend(
    val amount: Int? = 0,  // Nullable with default value
    val currency: String
)