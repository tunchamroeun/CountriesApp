package com.cloudware.countryapp.domain.repository

import com.cloudware.countryapp.domain.model.Country

/**
 * Repository interface for country-related data operations.
 *
 * This interface defines the contract for accessing country data from external sources. All methods
 * return domain models and handle errors using Result wrapper. Implementations should handle
 * network operations, data mapping, and error scenarios.
 */
interface CountryRepository {

  /**
   * Retrieves a list of all available countries.
   *
   * @return Result containing a list of countries or an error if the operation fails
   */
  suspend fun getCountries(): Result<List<Country>>

  /**
   * Retrieves detailed information for a specific country.
   *
   * @param code The country code (e.g., "US", "CA", "FR")
   * @return Result containing the country details or an error if the operation fails
   */
  suspend fun getCountryDetails(code: String): Result<Country>

  /**
   * Searches for countries based on a query string. The search can match against country names,
   * codes, or other relevant fields.
   *
   * @param query The search query string
   * @return Result containing a list of matching countries or an error if the operation fails
   */
  suspend fun searchCountries(query: String): Result<List<Country>>
}
