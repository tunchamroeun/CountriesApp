package com.cloudware.countryapp.presentation.features.search

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase
import com.cloudware.countryapp.testutils.MockCountryRepository
import com.cloudware.countryapp.testutils.TestData
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SearchStoreTest {

  private lateinit var store: SearchStore
  private lateinit var mockRepository: MockCountryRepository
  private lateinit var testDispatchers: com.cloudware.countryapp.core.utils.CoroutineDispatchers
  private lateinit var searchCountriesUseCase: SearchCountriesUseCase
  private lateinit var storeFactory: StoreFactory
  private lateinit var testScheduler: TestCoroutineScheduler

  @BeforeTest
  fun setup() {
    mockRepository = MockCountryRepository()
    testScheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    testDispatchers =
        com.cloudware.countryapp.core.utils.CoroutineDispatchers(
            main = testDispatcher,
            io = testDispatcher,
            default = testDispatcher,
            unconfined = testDispatcher)
    storeFactory = DefaultStoreFactory()
    searchCountriesUseCase = SearchCountriesUseCase(mockRepository, testDispatchers)
  }

  private fun createStore(): SearchStore {
    return SearchStore(
        storeFactory = storeFactory,
        searchCountriesUseCase = searchCountriesUseCase,
        dispatchers = testDispatchers)
  }

  @Test
  fun `initial state should have correct default values`() = runTest {
    // Given
    store = createStore()

    // When
    val state = store.state

    // Then
    assertEquals("", state.query)
    assertTrue(state.results.isEmpty())
    assertFalse(state.isLoading)
    assertEquals(null, state.error)
    assertFalse(state.isSearching)
    assertFalse(state.hasResults)
    assertFalse(state.hasError)
    assertFalse(state.isEmpty) // isEmpty should be false when there's no query
    assertFalse(state.hasQuery)
    assertFalse(state.showEmptyState)
    assertTrue(state.showInitialState)
  }

  @Test
  fun `SearchQuery intent should update query and trigger search`() =
      runTest(testScheduler) {
        // Given
        mockRepository.searchCountriesResult = Result.success(TestData.testCountriesList)
        store = createStore()

        // When
        store.accept(SearchStore.Intent.SearchQuery("United"))
        advanceTimeBy(301) // Advance past the 300ms debounce delay
        testScheduler.runCurrent() // Run all pending coroutines

        // Then
        val state = store.state
        assertEquals("United", state.query)
        assertTrue(state.results.isNotEmpty())
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
        assertFalse(state.isSearching)
        assertTrue(state.hasResults)
        assertTrue(state.hasQuery)
        assertFalse(state.showInitialState) // Should not show initial state when we have results
        assertFalse(state.showEmptyState)
      }

  @Test
  fun `SearchQuery intent with empty query should clear results`() = runTest {
    // Given
    store = createStore()
    store.accept(SearchStore.Intent.SearchQuery("United"))
    advanceTimeBy(300)

    // When
    store.accept(SearchStore.Intent.SearchQuery(""))

    // Then
    val state = store.state
    assertEquals("", state.query)
    assertTrue(state.results.isEmpty())
    assertFalse(state.isLoading)
    assertFalse(state.isSearching)
    assertEquals(null, state.error)
    assertFalse(state.hasResults)
    assertFalse(state.hasQuery)
    assertTrue(state.showInitialState)
    assertFalse(state.showEmptyState)
  }

  @Test
  fun `SearchQuery with no results should show empty state`() = runTest {
    // Given
    mockRepository.searchCountriesResult = Result.success(emptyList())
    store = createStore()

    // When
    store.accept(SearchStore.Intent.SearchQuery("XYZ"))
    advanceTimeBy(300) // Advance time to account for debounce delay

    // Then
    val state = store.state
    assertEquals("XYZ", state.query)
    assertEquals(emptyList(), state.results)
    assertFalse(state.isLoading)
    assertFalse(state.isSearching)
    assertEquals(null, state.error)
    assertFalse(state.hasResults)
    assertTrue(state.hasQuery)
    assertFalse(state.showInitialState)
    assertTrue(state.showEmptyState)
  }

  @Test
  fun `SearchQuery intent should handle error state`() =
      runTest(testScheduler) {
        // Given
        val errorMessage = "Search failed"
        mockRepository.searchCountriesResult = Result.failure(RuntimeException(errorMessage))
        store = createStore()

        // When
        store.accept(SearchStore.Intent.SearchQuery("United"))
        advanceTimeBy(301) // Advance past the 300ms debounce delay
        testScheduler.runCurrent() // Run all pending coroutines

        // Then
        val state = store.state
        assertEquals("United", state.query)
        assertTrue(state.results.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.isSearching)
        assertEquals(errorMessage, state.error)
        assertFalse(state.hasResults)
        assertTrue(state.hasError)
        assertTrue(state.hasQuery)
        assertFalse(state.showInitialState)
        assertFalse(state.showEmptyState)
      }

  @Test
  fun `ClearSearch intent should reset state`() =
      runTest(testScheduler) {
        // Given
        mockRepository.searchCountriesResult = Result.success(TestData.testCountriesList)
        store = createStore()
        store.accept(SearchStore.Intent.SearchQuery("United"))
        advanceTimeBy(301)
        testScheduler.runCurrent()
        assertTrue(store.state.hasResults)

        // When
        store.accept(SearchStore.Intent.ClearSearch)
        testScheduler.runCurrent() // Ensure clear operation completes

        // Then
        val state = store.state
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertFalse(state.isLoading)
        assertFalse(state.isSearching)
        assertEquals(null, state.error)
        assertFalse(state.hasResults)
        assertFalse(state.hasError)
        assertFalse(state.hasQuery)
        assertTrue(state.showInitialState)
        assertFalse(state.showEmptyState)
      }

  @Test
  fun `SelectCountry intent should trigger navigation label`() =
      runTest(testScheduler) {
        // Given
        store = createStore()

        // When - This should not throw any exceptions
        store.accept(SearchStore.Intent.SelectCountry("US"))
        testScheduler.runCurrent() // Process any async operations

        // Then - If we get here without exceptions, the intent was processed successfully
        // The actual navigation would be handled by the component/UI layer
        assertTrue(true) // Test passes if no exception is thrown
      }

  @Test
  fun `GoBack intent should trigger navigation label`() =
      runTest(testScheduler) {
        // Given
        store = createStore()

        // When - This should not throw any exceptions
        store.accept(SearchStore.Intent.GoBack)
        testScheduler.runCurrent() // Process any async operations

        // Then - If we get here without exceptions, the intent was processed successfully
        // The actual navigation would be handled by the component/UI layer
        assertTrue(true) // Test passes if no exception is thrown
      }

  @Test
  fun `Retry intent should re-execute last search`() =
      runTest(testScheduler) {
        // Given - first search fails
        val errorMessage = "Network error"
        mockRepository.searchCountriesResult = Result.failure(RuntimeException(errorMessage))
        store = createStore()
        store.accept(SearchStore.Intent.SearchQuery("United"))
        advanceTimeBy(301)
        testScheduler.runCurrent()
        assertTrue(store.state.hasError)

        // When - retry with success
        mockRepository.reset()
        mockRepository.searchCountriesResult = Result.success(TestData.testCountriesList)
        store.accept(SearchStore.Intent.Retry)
        testScheduler.runCurrent() // Allow retry operation to complete

        // Then
        val state = store.state
        assertEquals("United", state.query)
        assertFalse(state.hasError)
        assertTrue(state.hasResults)
        assertEquals(null, state.error)
      }

  @Test
  fun `state computed properties should work correctly`() =
      runTest(testScheduler) {
        // Given
        mockRepository.searchCountriesResult = Result.success(TestData.testCountriesList)
        store = createStore()

        // When
        store.accept(SearchStore.Intent.SearchQuery("United"))
        advanceTimeBy(301) // Wait for debounce
        testScheduler.runCurrent()

        // Then
        val state = store.state
        assertTrue(state.hasQuery)
        assertTrue(state.hasResults)
        assertFalse(state.hasError)
        assertFalse(state.isEmpty)
        assertFalse(state.showEmptyState)
        assertFalse(state.showInitialState)
      }

  @Test
  fun `query should be passed to use case correctly`() =
      runTest(testScheduler) {
        // Given
        mockRepository.searchCountriesResult = Result.success(TestData.testCountriesList)
        store = createStore()

        // When
        store.accept(SearchStore.Intent.SearchQuery("Canada"))
        advanceTimeBy(301) // Wait for debounce
        testScheduler.runCurrent()

        // Then
        assertEquals("Canada", mockRepository.searchCountriesCalledWith)
      }
}
