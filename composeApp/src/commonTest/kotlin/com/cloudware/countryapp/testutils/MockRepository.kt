package com.cloudware.countryapp.testutils

import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.repository.CountryRepository

/** Mock implementation of CountryRepository for testing */
class MockCountryRepository : CountryRepository {

  var getCountriesResult: Result<List<Country>> = Result.success(TestData.testCountriesList)
  var getCountryDetailsResult: Result<Country> = Result.success(TestData.testCountryUS)
  var searchCountriesResult: Result<List<Country>> = Result.success(emptyList())

  var getCountriesCalled = false
  var getCountryDetailsCalledWith: String? = null
  var searchCountriesCalledWith: String? = null

  override suspend fun getCountries(): Result<List<Country>> {
    getCountriesCalled = true
    return getCountriesResult
  }

  override suspend fun getCountryDetails(code: String): Result<Country> {
    getCountryDetailsCalledWith = code
    return getCountryDetailsResult
  }

  override suspend fun searchCountries(query: String): Result<List<Country>> {
    searchCountriesCalledWith = query
    return searchCountriesResult
  }

  fun reset() {
    getCountriesResult = Result.success(TestData.testCountriesList)
    getCountryDetailsResult = Result.success(TestData.testCountryUS)
    searchCountriesResult = Result.success(emptyList())
    getCountriesCalled = false
    getCountryDetailsCalledWith = null
    searchCountriesCalledWith = null
  }
}
