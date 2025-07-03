package com.cloudware.countryapp.domain.usecase

import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.repository.CountryRepository
import kotlinx.coroutines.withContext

/**
 * Use case for searching countries based on a query string.
 *
 * This use case handles the business logic for searching countries, including query validation,
 * empty state handling, proper error handling, and thread switching for network operations.
 */
class SearchCountriesUseCase(
    private val repository: CountryRepository,
    private val dispatchers: CoroutineDispatchers
) {

  /**
   * Executes the use case to search for countries matching the query.
   *
   * This operation validates the search query, handles empty query states, switches to the IO
   * dispatcher for network calls, and returns the result on the calling dispatcher. The repository
   * handles all data mapping and error scenarios.
   *
   * @param query The search query string - can match country names, codes, or other relevant fields
   * @return Result containing a list of matching countries or an empty list for empty queries
   */
  suspend operator fun invoke(query: String): Result<List<Country>> {
    return withContext(dispatchers.io) {
      try {
        // Handle empty query states - return empty list for blank queries
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
          return@withContext Result.success(emptyList())
        }

        // Validate query length to prevent overly short searches that might return too many results
        if (trimmedQuery.length < MIN_QUERY_LENGTH) {
          return@withContext Result.failure(
              InvalidSearchQueryException(
                  "Search query must be at least $MIN_QUERY_LENGTH characters long. " +
                      "Current query: '$query' (${trimmedQuery.length} characters)"))
        }

        // Validate query length to prevent overly long searches
        if (trimmedQuery.length > MAX_QUERY_LENGTH) {
          return@withContext Result.failure(
              InvalidSearchQueryException(
                  "Search query is too long. Maximum allowed length is $MAX_QUERY_LENGTH characters. " +
                      "Current query length: ${trimmedQuery.length} characters"))
        }

        repository.searchCountries(trimmedQuery)
      } catch (exception: Exception) {
        Result.failure(
            SearchCountriesException(
                message = "Failed to search countries with query '$query': ${exception.message}",
                cause = exception))
      }
    }
  }

  companion object {
    /** Minimum length required for search queries to prevent overly broad searches */
    const val MIN_QUERY_LENGTH = 2

    /** Maximum length allowed for search queries to prevent potential issues */
    const val MAX_QUERY_LENGTH = 50
  }
}

/**
 * Exception thrown when the SearchCountriesUseCase fails to search for countries.
 *
 * @param message The error message describing what went wrong
 * @param cause The underlying exception that caused this error
 */
class SearchCountriesException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

/**
 * Exception thrown when an invalid search query is provided to SearchCountriesUseCase.
 *
 * @param message The error message describing the validation issue
 */
class InvalidSearchQueryException(message: String) : Exception(message)
