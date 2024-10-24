package com.example

import com.example.utility.TestContainer
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationTest {
    private lateinit var postgresContainer: TestContainer.KPostgreSQLContainer

    @BeforeAll
    fun setUp() {
        postgresContainer = TestContainer.startPostgresContainer()
    }

    @AfterAll
    fun tearDown() {
        if (this.postgresContainer.isRunning) {
            postgresContainer.stop()
        }
    }

    @Test
    fun checkApplicationStartup() = testApplication {
        environment {
            config = MapApplicationConfig(
                "openexchangerates.apiKey" to "test-api-key",
                "monzo.clientId" to "test-client-id",
                "monzo.clientSecret" to "test-client-secret",
                "monzo.accountId" to "test-account-id",
                "monzo.userId" to "test-user-id",
                "database.url" to postgresContainer.jdbcUrl,
                "database.user" to postgresContainer.username,
                "database.password" to postgresContainer.password,
                "jwt.domain" to "https://www.test.com",
                "jwt.audience" to "test-audience",
                "jwt.realm" to "test-realm",
                "jwt.secret" to "test-secret"

            )
        }
        application {
            module()
        }

        val response = client.get("/")

        // Assert that the application responded with a valid status (e.g., OK)
        assertEquals(HttpStatusCode.OK, response.status, "Application did not start correctly or failed to respond")
    }
}