package com.cloudware.countryapp.domain.usecase

import com.cloudware.countryapp.testutils.MockCountryRepository
import com.cloudware.countryapp.testutils.TestData
import com.cloudware.countryapp.testutils.testCoroutineDispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SearchCountriesUseCaseTest {

  private lateinit var useCase: SearchCountriesUseCase
  private lateinit var mockRepository: MockCountryRepository
  private lateinit var testDispatchers: com.cloudware.countryapp.core.utils.CoroutineDispatchers

  @BeforeTest
  fun setup() {
    mockRepository = MockCountryRepository()
    testDispatchers = testCoroutineDispatchers()
    useCase = SearchCountriesUseCase(mockRepository, testDispatchers)
  }

  @Test
  fun `invoke should return success when repository returns search results`() = runTest {
    // Given
    val query = "United"
    val expectedResults = listOf(TestData.testCountryUS)
    mockRepository.searchCountriesResult = Result.success(expectedResults)

    // When
    val result = useCase(query)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(expectedResults, result.getOrNull())
    assertEquals(query, mockRepository.searchCountriesCalledWith)
  }

  @Test
  fun `invoke should return empty list when query is empty`() = runTest {
    // Given
    val query = ""

    // When
    val result = useCase(query)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(emptyList(), result.getOrNull())
    assertEquals(null, mockRepository.searchCountriesCalledWith) // Repository should not be called
  }

  @Test
  fun `invoke should return empty list when query is only whitespace`() = runTest {
    // Given
    val query = "   "

    // When
    val result = useCase(query)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(emptyList(), result.getOrNull())
    assertEquals(null, mockRepository.searchCountriesCalledWith) // Repository should not be called
  }

  @Test
  fun `invoke should trim whitespace from query before validation`() = runTest {
    // Given
    val query = "  United  "
    val expectedResults = listOf(TestData.testCountryUS)
    mockRepository.searchCountriesResult = Result.success(expectedResults)

    // When
    val result = useCase(query)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(expectedResults, result.getOrNull())
    assertEquals("United", mockRepository.searchCountriesCalledWith)
  }

  @Test
  fun `invoke should return failure when query is too short`() = runTest {
    // Given
    val query = "A" // Only 1 character, minimum is 2

    // When
    val result = useCase(query)

    // Then
    assertFalse(result.isSuccess)

    val exception = result.exceptionOrNull()
    assertTrue(exception is InvalidSearchQueryException)
    assertTrue(exception.message!!.contains("Search query must be at least 2 characters long"))
    assertTrue(exception.message!!.contains("Current query: 'A' (1 characters)"))
    assertEquals(null, mockRepository.searchCountriesCalledWith) // Repository should not be called
  }

  @Test
  fun `invoke should return failure when query is too long`() = runTest {
    // Given
    val query = "A".repeat(51) // 51 characters, maximum is 50

    // When
    val result = useCase(query)

    // Then
    assertFalse(result.isSuccess)

    val exception = result.exceptionOrNull()
    assertTrue(exception is InvalidSearchQueryException)
    assertTrue(exception.message!!.contains("Search query is too long"))
    assertTrue(exception.message!!.contains("Maximum allowed length is 50 characters"))
    assertTrue(exception.message!!.contains("Current query length: 51 characters"))
    assertEquals(null, mockRepository.searchCountriesCalledWith) // Repository should not be called
  }

  @Test
  fun `invoke should accept minimum valid query length`() = runTest {
    // Given
    val query = "US" // Exactly 2 characters (minimum)
    val expectedResults = listOf(TestData.testCountryUS)
    mockRepository.searchCountriesResult = Result.success(expectedResults)

    // When
    val result = useCase(query)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(expectedResults, result.getOrNull())
    assertEquals(query, mockRepository.searchCountriesCalledWith)
  }

  @Test
  fun `invoke should accept maximum valid query length`() = runTest {
    // Given
    val query = "A".repeat(50) // Exactly 50 characters (maximum)
    val expectedResults = emptyList<com.cloudware.countryapp.domain.model.Country>()
    mockRepository.searchCountriesResult = Result.success(expectedResults)

    // When
    val result = useCase(query)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(expectedResults, result.getOrNull())
    assertEquals(query, mockRepository.searchCountriesCalledWith)
  }

  @Test
  fun `invoke should return empty list when no countries match`() = runTest {
    // Given
    val query = "NonExistentCountry"
    mockRepository.searchCountriesResult = Result.success(emptyList())

    // When
    val result = useCase(query)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(emptyList(), result.getOrNull())
    assertEquals(query, mockRepository.searchCountriesCalledWith)
  }

  @Test
  fun `invoke should return failure when repository returns failure`() = runTest {
    // Given
    val query = "United"
    val expectedException = RuntimeException("Network error")
    mockRepository.searchCountriesResult = Result.failure(expectedException)

    // When
    val result = useCase(query)

    // Then
    assertFalse(result.isSuccess)
    assertEquals(query, mockRepository.searchCountriesCalledWith)

    val exception = result.exceptionOrNull()
    assertEquals(expectedException, exception)
  }

  @Test
  fun `invoke should validate MIN_QUERY_LENGTH constant`() {
    assertEquals(2, SearchCountriesUseCase.MIN_QUERY_LENGTH)
  }

  @Test
  fun `invoke should validate MAX_QUERY_LENGTH constant`() {
    assertEquals(50, SearchCountriesUseCase.MAX_QUERY_LENGTH)
  }
}
