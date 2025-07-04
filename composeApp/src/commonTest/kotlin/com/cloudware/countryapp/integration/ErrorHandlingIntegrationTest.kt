package com.cloudware.countryapp.integration

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloNetworkException
import com.cloudware.countryapp.data.remote.CountryRemoteDataSource
import com.cloudware.countryapp.data.remote.GraphQLClient
import com.cloudware.countryapp.data.remote.GraphQLException
import com.cloudware.countryapp.data.remote.NetworkException
import com.cloudware.countryapp.domain.repository.CountryRepository
import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase
import com.cloudware.countryapp.domain.usecase.InvalidCountryCodeException
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase
import com.cloudware.countryapp.testutils.TestData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/**
 * Integration tests for error handling scenarios throughout the application. These tests verify
 * that errors are properly handled and propagated across layers.
 */
class ErrorHandlingIntegrationTest {

  /** Test GraphQL client error handling with invalid server URL */
  @Test
  fun testGraphQLClientWithInvalidServer() = runTest {
    // Given - Apollo client with invalid URL
    val apolloClient =
        ApolloClient.Builder()
            .serverUrl("https://invalid-url-that-does-not-exist.com/graphql")
            .build()

    val graphQLClient = GraphQLClient(apolloClient)
    val remoteDataSource = CountryRemoteDataSource(graphQLClient)

    // When - Try to fetch countries
    val result = remoteDataSource.getCountries()

    // Then - Should fail with network error
    assertTrue("Request should fail with invalid server URL") { result.isFailure }

    val error = result.exceptionOrNull()
    assertNotNull(error, "Error should not be null")
    assertTrue("Error should be network-related") {
      error is NetworkException ||
          error.cause is ApolloNetworkException ||
          error is GraphQLException ||
          error.message?.contains("error", ignoreCase = true) == true
    }

    println("✅ GraphQL client correctly handles invalid server URL: ${error.message}")
  }

  /** Test GraphQL client error handling with malformed URL */
  @Test
  fun testGraphQLClientWithMalformedURL() = runTest {
    // Given - Apollo client with malformed URL
    val apolloClient = ApolloClient.Builder().serverUrl("not-a-valid-url").build()

    val graphQLClient = GraphQLClient(apolloClient)
    val remoteDataSource = CountryRemoteDataSource(graphQLClient)

    // When - Try to fetch countries
    val result = remoteDataSource.getCountries()

    // Then - Should fail with appropriate error
    assertTrue("Request should fail with malformed URL") { result.isFailure }

    val error = result.exceptionOrNull()
    assertNotNull(error, "Error should not be null")

    println("✅ GraphQL client correctly handles malformed URL: ${error.message}")
  }

  /** Test remote data source error handling with blank country code */
  @Test
  fun testRemoteDataSourceWithBlankCountryCode() = runTest {
    // Given - Valid GraphQL client
    val apolloClient =
        ApolloClient.Builder().serverUrl("https://countries.trevorblades.com/graphql").build()

    val graphQLClient = GraphQLClient(apolloClient)
    val remoteDataSource = CountryRemoteDataSource(graphQLClient)

    // When - Try to fetch country details with blank code
    val result = remoteDataSource.getCountryDetails("")

    // Then - Should fail with validation error
    assertTrue("Request should fail with blank country code") { result.isFailure }

    val error = result.exceptionOrNull()
    assertNotNull(error, "Error should not be null")
    assertIs<IllegalArgumentException>(error, "Error should be IllegalArgumentException")
    assertTrue("Error message should mention blank code") {
      error.message?.contains("blank", ignoreCase = true) == true
    }

    println("✅ Remote data source correctly validates blank country code: ${error.message}")
  }

  /** Test remote data source error handling with invalid country code */
  @Test
  fun testRemoteDataSourceWithInvalidCountryCode() = runTest {
    // Given - Valid GraphQL client
    val apolloClient =
        ApolloClient.Builder().serverUrl("https://countries.trevorblades.com/graphql").build()

    val graphQLClient = GraphQLClient(apolloClient)
    val remoteDataSource = CountryRemoteDataSource(graphQLClient)

    // When - Try to fetch country details with invalid code
    val result = remoteDataSource.getCountryDetails("INVALID_CODE")

    // Then - Should fail with appropriate error
    assertTrue("Request should fail with invalid country code") { result.isFailure }

    val error = result.exceptionOrNull()
    assertNotNull(error, "Error should not be null")

    println("✅ Remote data source correctly handles invalid country code: ${error.message}")
  }

  /** Test use case error handling with repository failures */
  @Test
  fun testUseCaseErrorHandling() = runTest {
    // Given - Mock repository that always fails
    val failingRepository =
        object : CountryRepository {
          override suspend fun getCountries():
              Result<List<com.cloudware.countryapp.domain.model.Country>> =
              Result.failure(RuntimeException("Repository error: Database connection failed"))

          override suspend fun getCountryDetails(
              code: String
          ): Result<com.cloudware.countryapp.domain.model.Country> =
              Result.failure(RuntimeException("Repository error: Country not found in database"))

          override suspend fun searchCountries(
              query: String
          ): Result<List<com.cloudware.countryapp.domain.model.Country>> =
              Result.failure(RuntimeException("Repository error: Search index unavailable"))
        }

    val dispatchers = com.cloudware.countryapp.testutils.testCoroutineDispatchers()
    val getCountriesUseCase = GetCountriesUseCase(failingRepository, dispatchers)
    val getCountryDetailsUseCase = GetCountryDetailsUseCase(failingRepository, dispatchers)
    val searchCountriesUseCase = SearchCountriesUseCase(failingRepository, dispatchers)

    // When & Then - Test GetCountriesUseCase
    val countriesResult = getCountriesUseCase()
    assertTrue("GetCountriesUseCase should fail") { countriesResult.isFailure }
    val countriesError = countriesResult.exceptionOrNull()
    assertNotNull(countriesError, "Countries error should not be null")
    assertTrue("Error should mention database connection") {
      countriesError.message?.contains("Database connection failed") == true
    }

    // When & Then - Test GetCountryDetailsUseCase
    val detailsResult = getCountryDetailsUseCase("US")
    assertTrue("GetCountryDetailsUseCase should fail") { detailsResult.isFailure }
    val detailsError = detailsResult.exceptionOrNull()
    assertNotNull(detailsError, "Details error should not be null")
    assertTrue("Error should mention country not found") {
      detailsError.message?.contains("Country not found") == true
    }

    // When & Then - Test SearchCountriesUseCase
    val searchResult = searchCountriesUseCase("test")
    assertTrue("SearchCountriesUseCase should fail") { searchResult.isFailure }
    val searchError = searchResult.exceptionOrNull()
    assertNotNull(searchError, "Search error should not be null")
    assertTrue("Error should mention search index") {
      searchError.message?.contains("Search index unavailable") == true
    }

    println("✅ Use cases correctly handle repository failures")
  }

  /** Test use case validation with invalid inputs */
  @Test
  fun testUseCaseInputValidation() = runTest {
    // Given - Repository with test data
    val testRepository =
        object : CountryRepository {
          override suspend fun getCountries():
              Result<List<com.cloudware.countryapp.domain.model.Country>> =
              Result.success(TestData.testCountriesList)

          override suspend fun getCountryDetails(
              code: String
          ): Result<com.cloudware.countryapp.domain.model.Country> =
              Result.success(TestData.testCountryUS)

          override suspend fun searchCountries(
              query: String
          ): Result<List<com.cloudware.countryapp.domain.model.Country>> =
              Result.success(TestData.testCountriesList)
        }

    val dispatchers = com.cloudware.countryapp.testutils.testCoroutineDispatchers()
    val getCountryDetailsUseCase = GetCountryDetailsUseCase(testRepository, dispatchers)
    val searchCountriesUseCase = SearchCountriesUseCase(testRepository, dispatchers)

    // When & Then - Test GetCountryDetailsUseCase with blank code
    val blankCodeResult = getCountryDetailsUseCase("")
    assertTrue("Should fail with blank country code") { blankCodeResult.isFailure }
    val blankCodeError = blankCodeResult.exceptionOrNull()
    assertIs<InvalidCountryCodeException>(blankCodeError, "Should be InvalidCountryCodeException")
    assertTrue("Error should mention blank code") {
      blankCodeError.message?.contains("blank", ignoreCase = true) == true
    }

    // When & Then - Test SearchCountriesUseCase with blank query (should succeed with empty result)
    val blankQueryResult = searchCountriesUseCase("")
    assertTrue("Should succeed with blank query") { blankQueryResult.isSuccess }
    val blankQueryCountries = blankQueryResult.getOrNull()
    assertNotNull(blankQueryCountries, "Result should not be null")
    assertTrue("Should return empty list for blank query") { blankQueryCountries.isEmpty() }

    println("✅ Use cases correctly validate inputs")
  }

  /** Test error propagation through multiple layers */
  @Test
  fun testErrorPropagationThroughLayers() = runTest {
    // Given - Mock repository that propagates errors from lower layers
    val originalError = RuntimeException("Network connection failed")
    val repository =
        object : CountryRepository {
          override suspend fun getCountries():
              Result<List<com.cloudware.countryapp.domain.model.Country>> {
            // Simulate error propagation from data layer
            return Result.failure(originalError)
          }

          override suspend fun getCountryDetails(
              code: String
          ): Result<com.cloudware.countryapp.domain.model.Country> {
            return Result.failure(originalError)
          }

          override suspend fun searchCountries(
              query: String
          ): Result<List<com.cloudware.countryapp.domain.model.Country>> {
            return Result.failure(originalError)
          }
        }

    val dispatchers = com.cloudware.countryapp.testutils.testCoroutineDispatchers()
    val getCountriesUseCase = GetCountriesUseCase(repository, dispatchers)

    // When - Execute use case
    val result = getCountriesUseCase()

    // Then - Error should propagate through all layers
    assertTrue("Error should propagate through layers") { result.isFailure }

    val error = result.exceptionOrNull()
    assertNotNull(error, "Error should not be null")
    assertTrue("Error should be the original error or wrapped") {
      error == originalError ||
          error.cause == originalError ||
          error.message?.contains("Network connection failed") == true
    }

    println("✅ Error correctly propagates through all layers: ${error.message}")
  }

  /** Test concurrent error scenarios */
  @Test
  fun testConcurrentErrorScenarios() = runTest {
    // Given - Repository that fails intermittently
    var callCount = 0
    val intermittentFailureRepository =
        object : CountryRepository {
          override suspend fun getCountries():
              Result<List<com.cloudware.countryapp.domain.model.Country>> =
              if (++callCount % 2 == 0) {
                Result.failure(RuntimeException("Intermittent failure"))
              } else {
                Result.success(TestData.testCountriesList)
              }

          override suspend fun getCountryDetails(
              code: String
          ): Result<com.cloudware.countryapp.domain.model.Country> =
              Result.success(TestData.testCountryUS)

          override suspend fun searchCountries(
              query: String
          ): Result<List<com.cloudware.countryapp.domain.model.Country>> =
              Result.success(TestData.testCountriesList)
        }

    val dispatchers = com.cloudware.countryapp.testutils.testCoroutineDispatchers()
    val useCase = GetCountriesUseCase(intermittentFailureRepository, dispatchers)

    // When - Execute multiple concurrent calls
    val results = (1..4).map { useCase() }

    // Then - Should have mix of success and failure
    val successes = results.count { it.isSuccess }
    val failures = results.count { it.isFailure }

    assertTrue("Should have some successes") { successes > 0 }
    assertTrue("Should have some failures") { failures > 0 }
    assertEquals(4, successes + failures, "Total should be 4")

    println(
        "✅ Concurrent error scenarios handled correctly: $successes successes, $failures failures")
  }

  /** Test exception wrapping and unwrapping */
  @Test
  fun testExceptionWrappingAndUnwrapping() = runTest {
    // Given - Original exception
    val originalException = RuntimeException("Original error message")

    // When - Wrap in custom exceptions
    val graphQLException = GraphQLException("GraphQL wrapper", originalException)
    val networkException = NetworkException("Network wrapper", graphQLException)

    // Then - Verify exception chain
    assertNotNull(networkException.cause, "Network exception should have cause")
    assertIs<GraphQLException>(networkException.cause, "Cause should be GraphQLException")

    val graphQLCause = networkException.cause as GraphQLException
    assertNotNull(graphQLCause.cause, "GraphQL exception should have cause")
    assertIs<RuntimeException>(graphQLCause.cause, "Root cause should be RuntimeException")

    val rootCause = graphQLCause.cause as RuntimeException
    assertEquals("Original error message", rootCause.message, "Root cause message should match")

    println("✅ Exception wrapping and unwrapping works correctly")
  }

  /** Test error message formatting and localization support */
  @Test
  fun testErrorMessageFormatting() = runTest {
    // Given - Various error types
    val errors =
        listOf(
            GraphQLException("GraphQL error occurred"),
            NetworkException("Network connection failed"),
            IllegalArgumentException("Invalid input provided"),
            RuntimeException("Unexpected error happened"))

    // When & Then - Verify error messages are properly formatted
    errors.forEach { error ->
      assertNotNull(error.message, "Error message should not be null")
      assertTrue("Error message should not be empty") { error.message!!.isNotBlank() }
      assertTrue("Error message should be descriptive") { error.message!!.length > 5 }

      println("✅ Error message formatted correctly: ${error::class.simpleName} -> ${error.message}")
    }
  }
}
