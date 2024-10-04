package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class MonzoAccount(
    val id: String,
    val closed: Boolean,
    val created: String,
    val description: String,
    val type: String,
    val ownerType: String,
    val isFlex: Boolean,
    val productType: String,
    val currency: String,
    val legalEntity: String,
    val countryCode: String,
    val countryCodeAlpha3: String,
    val owners: List<Owner>,
    val accountNumber: String?,
    val sortCode: String?,
    val paymentDetails: PaymentDetails
)

@Serializable
data class Owner(
    val userId: String,
    val preferredName: String,
    val preferredFirstName: String
)

@Serializable
data class PaymentDetails(
    val localeUK: LocaleUK?,
    val iban: Iban?
)

@Serializable
data class LocaleUK(
    val accountNumber: String,
    val sortCode: String
)

@Serializable
data class Iban(
    val unformatted: String,
    val formatted: String,
    val bic: String,
    val usageDescription: String,
    val usageDescriptionWeb: String
)