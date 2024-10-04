package com.example.client

import com.example.dao.OAuthTokenRepository
import com.example.model.TokenResponse
import com.example.utils.TimeProvider
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import org.slf4j.LoggerFactory

class MonzoClient(private val timeProvider: TimeProvider) : HttpClientBase() {

    private val logger = LoggerFactory.getLogger(MonzoClient::class.java)

    suspend fun exchangeAuthorizationCodeForToken(
        code: String, clientId: String, clientSecret: String, redirectUri: String
    ): TokenResponse? {
        val tokenUrl = "https://api.monzo.com/oauth2/token"

        return try {
            val response: HttpResponse = client.post(tokenUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    Parameters.build {
                        append("grant_type", "authorization_code")
                        append("client_id", clientId)
                        append("client_secret", clientSecret)
                        append("code", code)
                        append("redirect_uri", redirectUri)
                    }
                )
            }

            if (response.status == HttpStatusCode.OK) {
                // Parse the response body into TokenResponse
                val tokenResponse: TokenResponse = response.body()

                // Log token details and timestamp of issuance
                val issuedAt = timeProvider.getCurrentTime().atZone(timeProvider.zoneId).toEpochSecond()
                val fullTokenResponse = tokenResponse.copy(issuedAt = issuedAt)

                logger.info("Access token successfully retrieved for user: ${fullTokenResponse.userId}")
                fullTokenResponse
            } else {
                logger.error("Failed to exchange token: ${response.status}")
                null
            }
        } catch (e: Exception) {
            logger.error("Error exchanging authorization code for token", e)
            null
        }
    }
}