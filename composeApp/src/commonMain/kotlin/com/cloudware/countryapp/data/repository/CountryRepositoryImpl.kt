package com.cloudware.countryapp.data.repository

import com.cloudware.countryapp.data.mapper.toDomainModel
import com.cloudware.countryapp.data.remote.CountryRemoteDataSource
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.repository.CountryRepository

/**
 * Implementation of CountryRepository that uses remote data sources.
 *
 * This implementation fetches data from the GraphQL API using the remote data source and maps it to
 * domain models. It handles errors and provides a clean interface to the domain layer.
 */
class CountryRepositoryImpl(private val remoteDataSource: CountryRemoteDataSource) :
    CountryRepository {

  /**
   * Retrieves a list of all available countries from the remote API.
   *
   * @return Result containing a list of countries or an error if the operation fails
   */
  override suspend fun getCountries(): Result<List<Country>> {
    return try {
      remoteDataSource.getCountries().map { countriesData ->
        countriesData.map { it.toDomainModel() }
      }
    } catch (e: Exception) {
      Result.failure(
          RepositoryException("Failed to get countries from repository: ${e.message}", e))
    }
  }

  /**
   * Retrieves detailed information for a specific country from the remote API.
   *
   * @param code The country code (e.g., "US", "CA", "FR")
   * @return Result containing the country details or an error if the operation fails
   */
  override suspend fun getCountryDetails(code: String): Result<Country> {
    return try {
      if (code.isBlank()) {
        return Result.failure(IllegalArgumentException("Country code cannot be blank"))
      }

      remoteDataSource.getCountryDetails(code).map { countryData -> countryData.toDomainModel() }
    } catch (e: Exception) {
      Result.failure(
          RepositoryException(
              "Failed to get country details for '$code' from repository: ${e.message}", e))
    }
  }

  /**
   * Searches for countries based on a query string using the remote API.
   *
   * @param query The search query string
   * @return Result containing a list of matching countries or an error if the operation fails
   */
  override suspend fun searchCountries(query: String): Result<List<Country>> {
    return try {
      if (query.isBlank()) {
        return Result.success(emptyList())
      }

      remoteDataSource.searchCountries(query).map { countriesData ->
        countriesData.map { it.toDomainModel() }
      }
    } catch (e: Exception) {
      Result.failure(
          RepositoryException(
              "Failed to search countries with query '$query' from repository: ${e.message}", e))
    }
  }
}

/**
 * Exception thrown when repository operations fail.
 *
 * @param message The error message describing what went wrong
 * @param cause The underlying exception that caused this error
 */
class RepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause)
