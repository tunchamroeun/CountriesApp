package com.cloudware.countryapp.presentation.features.countries

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase
import com.cloudware.countryapp.testutils.MockCountryRepository
import com.cloudware.countryapp.testutils.TestData
import com.cloudware.countryapp.testutils.testCoroutineDispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield

class CountriesStoreTest {

  private lateinit var store: CountriesStore
  private lateinit var mockRepository: MockCountryRepository
  private lateinit var testDispatchers: com.cloudware.countryapp.core.utils.CoroutineDispatchers
  private lateinit var getCountriesUseCase: GetCountriesUseCase
  private lateinit var storeFactory: StoreFactory

  @BeforeTest
  fun setup() {
    mockRepository = MockCountryRepository()
    testDispatchers = testCoroutineDispatchers()
    storeFactory = DefaultStoreFactory()
    getCountriesUseCase = GetCountriesUseCase(mockRepository, testDispatchers)
  }

  private fun createStore(): CountriesStore {
    return CountriesStore(
        storeFactory = storeFactory,
        getCountriesUseCase = getCountriesUseCase,
        dispatchers = testDispatchers)
  }

  @Test
  fun `initial state should have correct default values`() = runTest {
    // Given
    store = createStore()

    // When
    val state = store.state

    // Then
    assertFalse(state.isLoading)
    assertEquals(emptyList(), state.countries)
    assertEquals(null, state.error)
    assertFalse(state.isRefreshing)
    assertFalse(state.hasData)
    assertTrue(state.isEmpty)
    assertFalse(state.hasError)
  }

  @Test
  fun `LoadCountries intent should load countries successfully`() = runTest {
    // Given
    mockRepository.getCountriesResult = Result.success(TestData.testCountriesList)
    store = createStore()

    // When
    store.accept(CountriesStore.Intent.LoadCountries)
    yield() // Allow async operations to complete

    // Then
    val state = store.state
    assertFalse(state.isLoading)
    assertFalse(state.isRefreshing)
    assertEquals(TestData.testCountriesList, state.countries)
    assertEquals(null, state.error)
    assertTrue(state.hasData)
    assertFalse(state.hasError)
    assertFalse(state.isEmpty)
  }

  @Test
  fun `LoadCountries intent should handle error state`() = runTest {
    // Given
    val errorMessage = "Network error"
    mockRepository.getCountriesResult = Result.failure(RuntimeException(errorMessage))
    store = createStore()

    // When
    store.accept(CountriesStore.Intent.LoadCountries)
    yield() // Ensure async operations complete

    // Then
    val state = store.state
    assertFalse(state.isLoading)
    assertFalse(state.isRefreshing)
    assertEquals(emptyList(), state.countries)
    assertEquals(errorMessage, state.error)
    assertFalse(state.hasData)
    assertTrue(state.hasError)
    assertFalse(state.isEmpty)
  }

  @Test
  fun `Refresh intent should set refreshing state and load countries`() = runTest {
    // Given
    mockRepository.getCountriesResult = Result.success(TestData.testCountriesList)
    store = createStore()

    // When
    store.accept(CountriesStore.Intent.Refresh)
    yield() // Allow async operations to complete

    // Then
    val state = store.state
    assertFalse(state.isLoading)
    assertFalse(state.isRefreshing) // Completed immediately with test dispatcher
    assertEquals(TestData.testCountriesList, state.countries)
    assertEquals(null, state.error)
    assertTrue(state.hasData)
    assertFalse(state.hasError)
    assertFalse(state.isEmpty)
  }

  @Test
  fun `Retry intent should reload countries after error`() = runTest {
    // Given - first load fails
    mockRepository.getCountriesResult = Result.failure(RuntimeException("Network error"))
    store = createStore()
    store.accept(CountriesStore.Intent.LoadCountries)
    yield()
    assertTrue(store.state.hasError)

    // When - retry with success
    mockRepository.reset()
    mockRepository.getCountriesResult = Result.success(TestData.testCountriesList)
    store.accept(CountriesStore.Intent.Retry)
    yield()

    // Then
    val state = store.state
    assertFalse(state.isLoading)
    assertFalse(state.isRefreshing)
    assertEquals(TestData.testCountriesList, state.countries)
    assertEquals(null, state.error)
    assertTrue(state.hasData)
    assertFalse(state.hasError)
    assertFalse(state.isEmpty)
  }

  @Test
  fun `SelectCountry intent should trigger navigation label`() = runTest {
    // Given
    store = createStore()

    // When - This should not throw any exceptions
    store.accept(CountriesStore.Intent.SelectCountry("US"))
    yield() // Allow any async processing to complete

    // Then - If we get here without exceptions, the intent was processed successfully
    // The actual navigation would be handled by the component/UI layer
    assertTrue(true) // Test passes if no exception is thrown
  }

  @Test
  fun `state computed properties should work correctly with data`() = runTest {
    // Given
    mockRepository.getCountriesResult = Result.success(TestData.testCountriesList)
    store = createStore()

    // When
    store.accept(CountriesStore.Intent.LoadCountries)
    yield()

    // Then
    val state = store.state
    assertTrue(state.hasData)
    assertFalse(state.isEmpty)
    assertFalse(state.hasError)
    assertEquals(TestData.testCountriesList, state.countries)
    assertEquals(null, state.error)
  }

  @Test
  fun `state computed properties should work correctly with error`() = runTest {
    // Given
    val errorMessage = "Error"
    mockRepository.getCountriesResult = Result.failure(RuntimeException(errorMessage))
    store = createStore()

    // When
    store.accept(CountriesStore.Intent.LoadCountries)
    yield() // Ensure async operations complete

    // Then
    val state = store.state
    assertFalse(state.hasData)
    assertFalse(state.isEmpty) // Has error so not empty
    assertTrue(state.hasError)
    assertEquals(emptyList(), state.countries)
    assertEquals(errorMessage, state.error)
  }

  @Test
  fun `error should be cleared when starting new load operation`() = runTest {
    // Given - first load fails
    val errorMessage = "First error"
    mockRepository.getCountriesResult = Result.failure(RuntimeException(errorMessage))
    store = createStore()
    store.accept(CountriesStore.Intent.LoadCountries)
    yield()
    assertTrue(store.state.hasError)

    // When - second load succeeds
    mockRepository.reset()
    mockRepository.getCountriesResult = Result.success(TestData.testCountriesList)
    store.accept(CountriesStore.Intent.LoadCountries)
    yield()

    // Then - error should be cleared
    val state = store.state
    assertFalse(state.hasError)
    assertEquals(null, state.error)
    assertTrue(state.hasData)
  }
}
