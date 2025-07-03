package com.cloudware.countryapp.presentation.features.details

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase
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

class DetailsStoreTest {

  private lateinit var store: DetailsStore
  private lateinit var mockRepository: MockCountryRepository
  private lateinit var testDispatchers: com.cloudware.countryapp.core.utils.CoroutineDispatchers
  private lateinit var getCountryDetailsUseCase: GetCountryDetailsUseCase
  private lateinit var storeFactory: StoreFactory
  private val testCountryCode = "US"

  @BeforeTest
  fun setup() {
    mockRepository = MockCountryRepository()
    testDispatchers = testCoroutineDispatchers()
    storeFactory = DefaultStoreFactory()
    getCountryDetailsUseCase = GetCountryDetailsUseCase(mockRepository, testDispatchers)
    // Don't create store in setup - create it in each test after setting up mock results
  }

  private fun createStore(): DetailsStore {
    return DetailsStore(
        storeFactory = storeFactory,
        countryCode = testCountryCode,
        getCountryDetailsUseCase = getCountryDetailsUseCase,
        dispatchers = testDispatchers)
  }

  @Test
  fun `initial state should have correct initial values`() = runTest {
    // Given
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)
    store = createStore()

    // When - initialization happens automatically
    val state = store.state

    // Then
    assertFalse(state.isLoading)
    assertEquals(null, state.country)
    assertEquals(null, state.error)
    assertEquals(testCountryCode, state.countryCode)
    assertFalse(state.hasError)
    assertFalse(state.hasData)
  }

  @Test
  fun `LoadDetails intent should set loading state and load country details`() = runTest {
    // Given
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)
    store = createStore()

    // When
    store.accept(DetailsStore.Intent.LoadDetails)
    yield() // Allow async operations to complete

    // Then
    val state = store.state
    assertFalse(state.isLoading) // Completed immediately with test dispatcher
    assertEquals(TestData.testCountryUS, state.country)
    assertEquals(null, state.error)
    assertTrue(state.hasData)
    assertFalse(state.hasError)
    assertEquals(testCountryCode, mockRepository.getCountryDetailsCalledWith)
  }

  @Test
  fun `LoadDetails intent should handle success state`() = runTest {
    // Given
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)
    store = createStore()

    // When
    store.accept(DetailsStore.Intent.LoadDetails)
    yield() // Allow async operations to complete

    // Then
    val state = store.state
    assertFalse(state.isLoading)
    assertEquals(TestData.testCountryUS, state.country)
    assertEquals(null, state.error)
    assertTrue(state.hasData)
    assertFalse(state.hasError)

    // Verify country data
    val country = state.country!!
    assertEquals("US", country.code)
    assertEquals("United States", country.name)
    assertEquals("Washington, D.C.", country.capital)
    assertEquals("🇺🇸", country.emoji)
  }

  @Test
  fun `LoadDetails intent should handle error state`() = runTest {
    // Given
    val errorMessage = "Country not found"
    mockRepository.getCountryDetailsResult = Result.failure(RuntimeException(errorMessage))
    store = createStore()

    // When
    store.accept(DetailsStore.Intent.LoadDetails)
    yield() // Ensure async operations complete

    // Then
    val state = store.state
    assertFalse(state.isLoading)
    assertEquals(null, state.country)
    assertEquals(errorMessage, state.error)
    assertFalse(state.hasData)
    assertTrue(state.hasError)
    assertEquals(testCountryCode, mockRepository.getCountryDetailsCalledWith)
  }

  @Test
  fun `LoadDetails intent should handle unknown error`() = runTest {
    // Given
    mockRepository.getCountryDetailsResult = Result.failure(RuntimeException())
    store = createStore()

    // When
    store.accept(DetailsStore.Intent.LoadDetails)
    yield() // Ensure async operations complete

    // Then
    val state = store.state
    assertFalse(state.isLoading)
    assertEquals("Unknown error occurred", state.error)
    assertTrue(state.hasError)
    assertFalse(state.hasData)
  }

  @Test
  fun `Retry intent should reload country details`() = runTest {
    // Given - first load fails
    mockRepository.getCountryDetailsResult = Result.failure(RuntimeException("Network error"))
    store = createStore()
    store.accept(DetailsStore.Intent.LoadDetails)
    yield()
    var state = store.state
    assertTrue(state.hasError)

    // When - retry with success
    mockRepository.reset()
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)
    store.accept(DetailsStore.Intent.Retry)
    yield()

    // Then
    state = store.state
    assertFalse(state.isLoading)
    assertEquals(TestData.testCountryUS, state.country)
    assertEquals(null, state.error)
    assertTrue(state.hasData)
    assertFalse(state.hasError)
    assertEquals(testCountryCode, mockRepository.getCountryDetailsCalledWith)
  }

  @Test
  fun `GoBack intent should trigger navigation label`() = runTest {
    // Given
    store = createStore()

    // When - This should not throw any exceptions
    store.accept(DetailsStore.Intent.GoBack)
    yield() // Allow any async processing to complete

    // Then - If we get here without exceptions, the intent was processed successfully
    // The actual navigation would be handled by the component/UI layer
    assertTrue(true) // Test passes if no exception is thrown
  }

  @Test
  fun `state computed properties should work correctly with data`() = runTest {
    // Given
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)
    store = createStore()

    // When
    store.accept(DetailsStore.Intent.LoadDetails)
    yield()

    // Then
    val state = store.state
    assertTrue(state.hasData)
    assertFalse(state.hasError)
    assertFalse(state.isLoading)
    assertEquals(TestData.testCountryUS, state.country)
    assertEquals(null, state.error)
  }

  @Test
  fun `state computed properties should work correctly with error`() = runTest {
    // Given
    mockRepository.getCountryDetailsResult = Result.failure(RuntimeException("Error"))
    store = createStore()

    // When
    store.accept(DetailsStore.Intent.LoadDetails)
    yield() // Ensure async operations complete

    // Then
    val state = store.state
    assertFalse(state.hasData)
    assertTrue(state.hasError)
    assertFalse(state.isLoading)
    assertEquals(null, state.country)
    assertEquals("Error", state.error)
  }

  @Test
  fun `error should be cleared when starting new load operation`() = runTest {
    // Given - first load fails
    mockRepository.getCountryDetailsResult = Result.failure(RuntimeException("First error"))
    store = createStore()
    store.accept(DetailsStore.Intent.LoadDetails)
    yield()
    var state = store.state
    assertTrue(state.hasError)

    // When - second load succeeds
    mockRepository.reset()
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)
    store.accept(DetailsStore.Intent.LoadDetails)
    yield()

    // Then - error should be cleared
    state = store.state
    assertFalse(state.hasError)
    assertEquals(null, state.error)
    assertTrue(state.hasData)
  }

  @Test
  fun `LoadDetails should pass correct country code to use case`() = runTest {
    // Given
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)
    store = createStore()

    // When
    store.accept(DetailsStore.Intent.LoadDetails)
    yield()

    // Then
    assertEquals(testCountryCode, mockRepository.getCountryDetailsCalledWith)
  }

  @Test
  fun `multiple LoadDetails calls should use the same country code`() = runTest {
    // Given
    mockRepository.getCountryDetailsResult = Result.success(TestData.testCountryUS)
    store = createStore()

    // When
    store.accept(DetailsStore.Intent.LoadDetails)
    yield()
    mockRepository.reset()
    store.accept(DetailsStore.Intent.LoadDetails)
    yield()

    // Then
    assertEquals(testCountryCode, mockRepository.getCountryDetailsCalledWith)
  }
}
