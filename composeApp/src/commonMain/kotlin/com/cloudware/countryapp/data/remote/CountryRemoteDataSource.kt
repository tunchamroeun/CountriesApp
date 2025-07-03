package com.cloudware.countryapp.data.remote

import com.cloudware.countryapp.CountriesQuery
import com.cloudware.countryapp.CountryQuery

/**
 * Remote data source for country-related operations using GraphQL.
 *
 * This class handles all network operations related to countries using Apollo GraphQL client. It
 * returns GraphQL generated models and handles network exceptions appropriately.
 */
class CountryRemoteDataSource(private val graphQLClient: GraphQLClient) {

  /**
   * Fetches all countries from the GraphQL API.
   *
   * @return Result containing list of CountriesQuery.Country or an error
   */
  suspend fun getCountries(): Result<List<CountriesQuery.Country>> {
    return try {
      val query = CountriesQuery()
      val result = graphQLClient.executeQuery(query)

      result.fold(
          onSuccess = { data ->
            val countries = data.countries
            Result.success(countries)
          },
          onFailure = { error -> Result.failure(error) })
    } catch (e: Exception) {
      Result.failure(UnknownException("Failed to fetch countries: ${e.message}", e))
    }
  }

  /**
   * Fetches detailed information for a specific country by code.
   *
   * @param countryCode The ISO country code (e.g., "US", "CA", "FR")
   * @return Result containing CountryQuery.Country or an error
   */
  suspend fun getCountryDetails(countryCode: String): Result<CountryQuery.Country> {
    return try {
      if (countryCode.isBlank()) {
        return Result.failure(IllegalArgumentException("Country code cannot be blank"))
      }

      val query = CountryQuery(countryCode.uppercase())
      val result = graphQLClient.executeQuery(query)

      result.fold(
          onSuccess = { data ->
            val country = data.country
            if (country != null) {
              Result.success(country)
            } else {
              Result.failure(GraphQLException("Country with code '$countryCode' not found"))
            }
          },
          onFailure = { error -> Result.failure(error) })
    } catch (e: Exception) {
      Result.failure(
          UnknownException("Failed to fetch country details for '$countryCode': ${e.message}", e))
    }
  }

  /**
   * Searches for countries based on a query string.
   *
   * Note: The current GraphQL API doesn't support server-side search, so this implementation
   * fetches all countries and filters client-side. In a production environment, you would ideally
   * have a dedicated search endpoint.
   *
   * @param query The search query string
   * @return Result containing list of matching CountriesQuery.Country or an error
   */
  suspend fun searchCountries(query: String): Result<List<CountriesQuery.Country>> {
    return try {
      if (query.isBlank()) {
        return Result.success(emptyList())
      }

      // Fetch all countries first
      val allCountriesResult = getCountries()

      allCountriesResult.fold(
          onSuccess = { countries ->
            // Filter countries based on query (case-insensitive)
            val filteredCountries =
                countries.filter { country ->
                  val searchQuery = query.lowercase()
                  country.name.lowercase().contains(searchQuery) ||
                      country.code.lowercase().contains(searchQuery) ||
                      country.capital?.lowercase()?.contains(searchQuery) == true
                }
            Result.success(filteredCountries)
          },
          onFailure = { error -> Result.failure(error) })
    } catch (e: Exception) {
      Result.failure(
          UnknownException("Failed to search countries with query '$query': ${e.message}", e))
    }
  }

  /**
   * Validates that a country code is in the correct format.
   *
   * @param countryCode The country code to validate
   * @return true if the code is valid, false otherwise
   */
  private fun isValidCountryCode(countryCode: String): Boolean {
    return countryCode.matches(Regex("^[A-Z]{2,3}$"))
  }

  /** Cleans up resources. */
  fun dispose() {
    graphQLClient.dispose()
  }
}
