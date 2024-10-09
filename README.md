# Ktor Finance Insights Server

A Ktor server that leverages Openexchangerates and Monzo to give insights into your finances, regardless of what currency you use. It utilizes foreign exchange rates to show your spending and savings in any currency!

## Features
- Integrates with the Monzo API to retrieve your account information.
- Utilizes Openexchangerates to convert your financial data into any desired currency.
- Provides insights into your spending and savings across different currencies.

## Getting Started

### Prerequisites
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Kotlin](https://kotlinlang.org/docs/getting-started.html)
- [Gradle](https://gradle.org/install/)
- [Monzo API Client ID and Secret](https://developers.monzo.com/)
- [OpenExchangeRates API Key](https://openexchangerates.org/signup)

### Installation

1. **Clone the repository:**
    ```bash
    git clone https://github.com/Kotik112/FinnConnect.git
    cd FinnConnect
    ```

2. **Set up environment variables:**
   - Store sensitive data like Monzo Client ID, Client Secret, and OpenExchangeRates API Key as environment variables:

    ```bash
    export MONZO_CLIENT_ID="your_monzo_client_id"
    export MONZO_CLIENT_SECRET="your_monzo_client_secret"
    export OPENEXCHANGE_API_KEY="your_openexchangerates_api_key"
    ```

3. **Run the application:**
    ```bash
    ./gradlew run
    ```

4. **Access the application:**
   - The server will start on `http://localhost:8080`. You can test the API endpoints using Postman or your preferred tool.

### Configuration

Ensure the following configurations are set in your `application.yaml` or as environment variables:

```yaml
database:
  url: your-jdbc-url
  user: db-user
  password: db-passwd
  driver: your-db-driver
monzo:
  clientId: your-client-id
  clientSecret: your-client-secret
  redirectUri: http://localhost:8080/your-redirect-url
  accountId: your-account-id
  userId: your-user-id

openexchangerates:
  apiKey: your-api-key
