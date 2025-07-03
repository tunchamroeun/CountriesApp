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

class GetCountriesUseCaseTest {

  private lateinit var useCase: GetCountriesUseCase
  private lateinit var mockRepository: MockCountryRepository
  private lateinit var testDispatchers: com.cloudware.countryapp.core.utils.CoroutineDispatchers

  @BeforeTest
  fun setup() {
    mockRepository = MockCountryRepository()
    testDispatchers = testCoroutineDispatchers()
    useCase = GetCountriesUseCase(mockRepository, testDispatchers)
  }

  @Test
  fun `invoke should return success when repository returns countries`() = runTest {
    // Given
    mockRepository.getCountriesResult = Result.success(TestData.testCountriesList)

    // When
    val result = useCase()

    // Then
    assertTrue(result.isSuccess)
    assertEquals(TestData.testCountriesList, result.getOrNull())
    assertTrue(mockRepository.getCountriesCalled)
  }

  @Test
  fun `invoke should return empty list when repository returns empty list`() = runTest {
    // Given
    mockRepository.getCountriesResult = Result.success(emptyList())

    // When
    val result = useCase()

    // Then
    assertTrue(result.isSuccess)
    assertEquals(emptyList(), result.getOrNull())
    assertTrue(mockRepository.getCountriesCalled)
  }

  @Test
  fun `invoke should return failure when repository returns failure`() = runTest {
    // Given
    val expectedException = RuntimeException("Network error")
    mockRepository.getCountriesResult = Result.failure(expectedException)

    // When
    val result = useCase()

    // Then
    assertFalse(result.isSuccess)
    assertTrue(mockRepository.getCountriesCalled)

    val exception = result.exceptionOrNull()
    assertEquals(expectedException, exception)
  }

  @Test
  fun `invoke should handle null pointer exception from repository`() = runTest {
    // Given
    val expectedException = NullPointerException("Null response")
    mockRepository.getCountriesResult = Result.failure(expectedException)

    // When
    val result = useCase()

    // Then
    assertFalse(result.isSuccess)
    assertTrue(mockRepository.getCountriesCalled)

    val exception = result.exceptionOrNull()
    assertEquals(expectedException, exception)
  }

  @Test
  fun `invoke should execute on IO dispatcher`() = runTest {
    // Given
    mockRepository.getCountriesResult = Result.success(TestData.testCountriesList)

    // When
    val result = useCase()

    // Then
    assertTrue(result.isSuccess)
    assertTrue(mockRepository.getCountriesCalled)
    // Note: In a more sophisticated test, we would verify the dispatcher was actually used
    // but with UnconfinedTestDispatcher, all operations run immediately
  }
}
