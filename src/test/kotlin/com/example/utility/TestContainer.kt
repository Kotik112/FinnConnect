package com.example.utility

import org.testcontainers.containers.PostgreSQLContainer

object TestContainer {
    class KPostgreSQLContainer : PostgreSQLContainer<KPostgreSQLContainer>("postgres:17")

    /**
     * Starts a PostgreSQL container for testing purposes.
     *
     * This function initializes and starts a PostgreSQL container using the Testcontainers library.
     * The container is configured with a predefined database name, username, password, and an
     * initialization script.
     *
     * @return A running instance of [KPostgreSQLContainer] configured for testing.
     */
    fun startPostgresContainer() : KPostgreSQLContainer {
        val container = KPostgreSQLContainer().apply {
            withDatabaseName("test")
            withUsername("test")
            withPassword("test")
            withInitScript("init_db.sql")
            start()
        }
        return container
    }
}