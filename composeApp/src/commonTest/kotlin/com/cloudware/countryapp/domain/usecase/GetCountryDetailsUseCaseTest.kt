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

class GetCountryDetailsUseCaseTest {

  private lateinit var useCase: GetCountryDetailsUseCase
  private lateinit var mockRepository: MockCountryRepository
  private lateinit var testDispatchers: com.cloudware.countryapp.core.utils.CoroutineDispatchers

  @BeforeTest
  fun setup() {
    mockRepository = MockCountryRepository()
    testDispatchers = testCoroutineDispatchers()
    useCase = GetCountryDetailsUseCase(mockRepository, testDispatchers)
  }

  @Test
  fun `invoke should return success when repository returns country details`() = runTest {
    // Given
    val countryCode = "US"
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)

    // When
    val result = useCase(countryCode)

    // Then
    assertTrue(result.isSuccess)
    assertEquals(TestData.testCountryUS, result.getOrNull())
    assertEquals("US", mockRepository.getCountryDetailsCalledWith)
  }

  @Test
  fun `invoke should normalize country code to uppercase`() = runTest {
    // Given
    val countryCode = "us"
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)

    // When
    val result = useCase(countryCode)

    // Then
    assertTrue(result.isSuccess)
    assertEquals("US", mockRepository.getCountryDetailsCalledWith)
  }

  @Test
  fun `invoke should trim whitespace from country code`() = runTest {
    // Given
    val countryCode = "  US  "
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)

    // When
    val result = useCase(countryCode)

    // Then
    assertTrue(result.isSuccess)
    assertEquals("US", mockRepository.getCountryDetailsCalledWith)
  }

  @Test
  fun `invoke should return failure when country code is blank`() = runTest {
    // Given
    val countryCode = ""

    // When
    val result = useCase(countryCode)

    // Then
    assertFalse(result.isSuccess)

    val exception = result.exceptionOrNull()
    assertTrue(exception is InvalidCountryCodeException)
    assertEquals("Country code cannot be blank or empty", exception.message)
  }

  @Test
  fun `invoke should return failure when country code is only whitespace`() = runTest {
    // Given
    val countryCode = "   "

    // When
    val result = useCase(countryCode)

    // Then
    assertFalse(result.isSuccess)

    val exception = result.exceptionOrNull()
    assertTrue(exception is InvalidCountryCodeException)
    assertEquals("Country code cannot be blank or empty", exception.message)
  }

  @Test
  fun `invoke should return failure when country code is too short`() = runTest {
    // Given
    val countryCode = "A"

    // When
    val result = useCase(countryCode)

    // Then
    assertFalse(result.isSuccess)

    val exception = result.exceptionOrNull()
    assertTrue(exception is InvalidCountryCodeException)
    assertTrue(exception.message!!.contains("Invalid country code format"))
    assertTrue(exception.message!!.contains("Expected 2-3 letter country code"))
  }

  @Test
  fun `invoke should return failure when country code is too long`() = runTest {
    // Given
    val countryCode = "ABCD"

    // When
    val result = useCase(countryCode)

    // Then
    assertFalse(result.isSuccess)

    val exception = result.exceptionOrNull()
    assertTrue(exception is InvalidCountryCodeException)
    assertTrue(exception.message!!.contains("Invalid country code format"))
  }

  @Test
  fun `invoke should return failure when country code contains numbers`() = runTest {
    // Given
    val countryCode = "U2"

    // When
    val result = useCase(countryCode)

    // Then
    assertFalse(result.isSuccess)

    val exception = result.exceptionOrNull()
    assertTrue(exception is InvalidCountryCodeException)
    assertTrue(exception.message!!.contains("Invalid country code format"))
  }

  @Test
  fun `invoke should return failure when country code contains special characters`() = runTest {
    // Given
    val countryCode = "U$"

    // When
    val result = useCase(countryCode)

    // Then
    assertFalse(result.isSuccess)

    val exception = result.exceptionOrNull()
    assertTrue(exception is InvalidCountryCodeException)
  }

  @Test
  fun `invoke should accept valid 3-letter country codes`() = runTest {
    // Given
    val countryCode = "USA"
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)

    // When
    val result = useCase(countryCode)

    // Then
    assertTrue(result.isSuccess)
    assertEquals("USA", mockRepository.getCountryDetailsCalledWith)
  }

  @Test
  fun `invoke should return failure when repository returns failure`() = runTest {
    // Given
    val countryCode = "US"
    val expectedException = RuntimeException("Network error")
    mockRepository.getCountryDetailsResult = Result.failure(expectedException)

    // When
    val result = useCase(countryCode)

    // Then
    assertFalse(result.isSuccess)
    assertEquals("US", mockRepository.getCountryDetailsCalledWith)

    val exception = result.exceptionOrNull()
    assertEquals(expectedException, exception)
  }
}
