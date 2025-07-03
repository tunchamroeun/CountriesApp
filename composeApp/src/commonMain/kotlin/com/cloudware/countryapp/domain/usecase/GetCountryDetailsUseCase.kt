package com.cloudware.countryapp.domain.usecase

import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.repository.CountryRepository
import kotlinx.coroutines.withContext

/**
 * Use case for retrieving detailed information about a specific country.
 *
 * This use case handles the business logic for fetching country details, including input
 * validation, proper error handling, and thread switching for network operations.
 */
class GetCountryDetailsUseCase(
    private val repository: CountryRepository,
    private val dispatchers: CoroutineDispatchers
) {

  /**
   * Executes the use case to retrieve details for a specific country.
   *
   * This operation validates the country code parameter, switches to the IO dispatcher for network
   * calls, and returns the result on the calling dispatcher. The repository handles all data
   * mapping and error scenarios.
   *
   * @param countryCode The country code (e.g., "US", "CA", "FR") - must not be blank
   * @return Result containing the country details or an error if the operation fails
   */
  suspend operator fun invoke(countryCode: String): Result<Country> {
    return withContext(dispatchers.io) {
      try {
        // Validate input parameters
        if (countryCode.isBlank()) {
          return@withContext Result.failure(
              InvalidCountryCodeException("Country code cannot be blank or empty"))
        }

        // Normalize country code to uppercase for consistency
        val normalizedCode = countryCode.trim().uppercase()

        // Validate country code format (2-3 character codes are typical)
        if (normalizedCode.length !in 2..3 || !normalizedCode.all { it.isLetter() }) {
          return@withContext Result.failure(
              InvalidCountryCodeException(
                  "Invalid country code format: '$countryCode'. " +
                      "Expected 2-3 letter country code (e.g., 'US', 'CA', 'FR')"))
        }

        repository.getCountryDetails(normalizedCode)
      } catch (exception: Exception) {
        Result.failure(
            GetCountryDetailsException(
                message =
                    "Failed to retrieve country details for '$countryCode': ${exception.message}",
                cause = exception))
      }
    }
  }
}

/**
 * Exception thrown when the GetCountryDetailsUseCase fails to retrieve country details.
 *
 * @param message The error message describing what went wrong
 * @param cause The underlying exception that caused this error
 */
class GetCountryDetailsException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

/**
 * Exception thrown when an invalid country code is provided to GetCountryDetailsUseCase.
 *
 * @param message The error message describing the validation issue
 */
class InvalidCountryCodeException(message: String) : Exception(message)
