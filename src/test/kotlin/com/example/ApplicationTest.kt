package com.example

import com.example.utility.TestContainer
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationTest {
    private lateinit var connection: Connection
    private lateinit var postgresContainer: TestContainer.KPostgreSQLContainer

    @BeforeAll
    fun setUp() {
        postgresContainer = TestContainer.startPostgresContainer()

        connection = DriverManager.getConnection(
            postgresContainer.jdbcUrl,
            postgresContainer.username,
            postgresContainer.password
        )
    }

    @AfterAll
    fun tearDown() {
        if (this.postgresContainer.isRunning) {
            postgresContainer.stop()
        }
    }

    @Test
    fun testRoot() = testApplication {
        environment {
            config = MapApplicationConfig(
                "openexchangerates.apiKey" to "test-api-key",
                "database.url" to postgresContainer.jdbcUrl,
                "database.user" to postgresContainer.username,
                "database.password" to postgresContainer.password
            )
        }
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, World!", bodyAsText())
        }
    }
}