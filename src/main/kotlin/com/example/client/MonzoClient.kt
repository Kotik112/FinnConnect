package com.example.client

import com.example.model.BalanceResponse
import com.example.model.Either
import com.example.model.MonzoAccountsResponse
import com.example.model.MonzoErrorResponse
import com.example.model.TokenResponse
import com.example.model.WhoAmIResponse
import com.example.service.OAuthTokenService
import com.example.utils.TimeProvider
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import org.slf4j.LoggerFactory

class MonzoClient(
    private val tokenService: OAuthTokenService,
    private val timeProvider: TimeProvider
) : HttpClientBase() {
    private val logger = LoggerFactory.getLogger(MonzoClient::class.java)
    private val contentType = "application/json"

    suspend fun exchangeAuthorizationCodeForToken(
        code: String, clientId: String, clientSecret: String, redirectUri: String
    ): TokenResponse? {
        val tokenUrl = "https://api.monzo.com/oauth2/token"

        return try {
            // Use `submitForm` to send form parameters with Content-Type `application/x-www-form-urlencoded`
            val response: HttpResponse = client.submitForm(
                url = tokenUrl,
                formParameters = Parameters.build {
                    append("grant_type", "authorization_code")
                    append("client_id", clientId)
                    append("client_secret", clientSecret)
                    append("redirect_uri", redirectUri)
                    append("code", code)
                }
            )

            // Log the raw response body for debugging
            val rawResponseBody = response.bodyAsText()
            logger.debug("Monzo token exchange raw response body: $rawResponseBody")

            if (response.status == HttpStatusCode.OK) {
                // Parse the response body into TokenResponse
                val tokenResponse: TokenResponse = response.body()

                // Add the issuedAt timestamp for the token (current time)
                val issuedAt = timeProvider.getCurrentTime().atZone(timeProvider.zoneId).toEpochSecond()
                val fullTokenResponse = tokenResponse.copy(issuedAt = issuedAt)
                tokenService.saveToken(tokenResponse = fullTokenResponse)

                logger.info("Access token successfully retrieved for user: ${fullTokenResponse.userId}")
                return fullTokenResponse
            } else {
                logger.error("Failed to exchange token: ${response.status}")
                null
            }
        } catch (e: Exception) {
            logger.error("Error exchanging authorization code for token: ${e.message}")
            return null
        }
    }

    suspend fun whoAmI(accessToken: String): WhoAmIResponse? {
        val whoAmIUrl = "https://api.monzo.com/ping/whoami"

        return try {
            val response: HttpResponse = client.get(whoAmIUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                    append(HttpHeaders.Accept, contentType)
                }
            }

            // Check if the response is successful
            if (response.status == HttpStatusCode.OK) {
                val whoAmIResponse: WhoAmIResponse = response.body()
                logger.info("Successfully retrieved whoami response: $whoAmIResponse")
                whoAmIResponse
            } else {
                logger.error("Failed to retrieve whoami response. Status: ${response.status}")
                null
            }
        } catch (e: Exception) {
            logger.error("Error making whoami request: ${e.message}")
            return null
        }
    }

    suspend fun getMonzoAccounts(accessToken: String): Either<MonzoAccountsResponse, MonzoErrorResponse> {
        val accountsUrl = "https://api.monzo.com/accounts"

        return try {
            val response: HttpResponse = client.get(accountsUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                    append(HttpHeaders.Accept, contentType)
                    append("account_type", "uk_retail")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val accountsResponse: MonzoAccountsResponse = response.body()
                logger.info("Successfully retrieved accounts: $accountsResponse")
                Either.Success(accountsResponse)
            } else {
                val errorResponse: MonzoErrorResponse = response.body()
                logger.error("Failed to retrieve accounts. Status: ${response.status}, Error: $errorResponse")
                Either.Failure(errorResponse)
            }
        } catch (e: Exception) {
            logger.error("Error retrieving accounts: ${e.message}")
            Either.Failure(MonzoErrorResponse("Error", e.message ?: "Unknown error"))
        }
    }

    suspend fun getMonzoBalance(accessToken: String, accountId: String): Either<BalanceResponse, MonzoErrorResponse> {
        val balanceUrl = "https://api.monzo.com/balance"

        return try {
            val response: HttpResponse = client.get(balanceUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                    append(HttpHeaders.Accept, contentType)
                }
                parameter("account_id", accountId)  // Include the account_id as a parameter
            }.body()

            if (response.status == HttpStatusCode.OK) {
                val balanceResponse: BalanceResponse = response.body()
                logger.info("Successfully retrieved balance: $balanceResponse")
                Either.Success(balanceResponse)
            } else {
                val errorResponse: MonzoErrorResponse = response.body()  // Parse the error response
                logger.error("Failed to retrieve balance. Status: ${response.status}, Error: $errorResponse")
                Either.Failure(errorResponse)
            }
        } catch (e: Exception) {
            logger.error("Error retrieving balance: ${e.message}")
            Either.Failure(MonzoErrorResponse("Error", e.message ?: "Unknown error"))
        }
    }

}