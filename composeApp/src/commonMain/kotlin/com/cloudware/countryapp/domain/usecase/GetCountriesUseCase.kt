package com.cloudware.countryapp.domain.usecase

import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.repository.CountryRepository
import kotlinx.coroutines.withContext

/**
 * Use case for retrieving a list of all available countries.
 *
 * This use case handles the business logic for fetching countries data, including proper error
 * handling and thread switching for network operations. It follows the single responsibility
 * principle by focusing only on countries retrieval.
 */
class GetCountriesUseCase(
    private val repository: CountryRepository,
    private val dispatchers: CoroutineDispatchers
) {

  /**
   * Executes the use case to retrieve all countries.
   *
   * This operation switches to the IO dispatcher for network calls and returns the result on the
   * calling dispatcher. The repository handles all data mapping and error scenarios.
   *
   * @return Result containing a list of countries or an error if the operation fails
   */
  suspend operator fun invoke(): Result<List<Country>> {
    return withContext(dispatchers.io) {
      try {
        repository.getCountries()
      } catch (exception: Exception) {
        Result.failure(
            GetCountriesException(
                message = "Failed to retrieve countries: ${exception.message}", cause = exception))
      }
    }
  }
}

/**
 * Exception thrown when the GetCountriesUseCase fails to retrieve countries data.
 *
 * @param message The error message describing what went wrong
 * @param cause The underlying exception that caused this error
 */
class GetCountriesException(message: String, cause: Throwable? = null) : Exception(message, cause)
