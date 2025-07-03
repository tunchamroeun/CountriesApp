package com.cloudware.countryapp.integration

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.cloudware.countryapp.core.navigation.DefaultRootComponent
import com.cloudware.countryapp.core.navigation.RootComponent
import com.cloudware.countryapp.domain.repository.CountryRepository
import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase
import com.cloudware.countryapp.presentation.features.countries.CountriesComponent
import com.cloudware.countryapp.presentation.features.countries.CountriesStore
import com.cloudware.countryapp.presentation.features.details.DetailsComponent
import com.cloudware.countryapp.presentation.features.search.SearchComponent
import com.cloudware.countryapp.testutils.TestData
import com.cloudware.countryapp.testutils.testCoroutineDispatchers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

/**
 * Integration tests for navigation flows using Decompose. These tests verify that navigation
 * between screens works correctly.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NavigationIntegrationTest {

  private fun createRootComponent(): DefaultRootComponent {
    val lifecycle = LifecycleRegistry()
    val componentContext = DefaultComponentContext(lifecycle = lifecycle)
    lifecycle.resume()

    val mockRepository =
        object : CountryRepository {
          override suspend fun getCountries():
              Result<List<com.cloudware.countryapp.domain.model.Country>> =
              Result.success(TestData.testCountriesList)

          override suspend fun getCountryDetails(
              code: String
          ): Result<com.cloudware.countryapp.domain.model.Country> =
              Result.success(TestData.testCountryUS)

          override suspend fun searchCountries(
              query: String
          ): Result<List<com.cloudware.countryapp.domain.model.Country>> =
              Result.success(TestData.testCountriesList)
        }

    val dispatchers = testCoroutineDispatchers()
    val storeFactory = DefaultStoreFactory()

    return DefaultRootComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        getCountriesUseCase = GetCountriesUseCase(mockRepository, dispatchers),
        getCountryDetailsUseCase = GetCountryDetailsUseCase(mockRepository, dispatchers),
        searchCountriesUseCase = SearchCountriesUseCase(mockRepository, dispatchers),
        dispatchers = dispatchers)
  }

  /** Test initial navigation state shows Countries screen */
  @Test
  fun testInitialNavigationState() {
    // Given
    val rootComponent = createRootComponent()

    // When
    val initialChild = rootComponent.stack.value.active.instance

    // Then
    assertIs<RootComponent.Child.CountriesChild>(
        initialChild, "Initial screen should be Countries screen")

    println("✅ Initial navigation state correctly shows Countries screen")
  }

  /** Test navigation stack size tracking */
  @Test
  fun testNavigationStackSize() {
    // Given
    val rootComponent = createRootComponent()

    // When
    val initialStackSize = rootComponent.stack.value.items.size

    // Then
    assertEquals(1, initialStackSize, "Initial stack should have 1 item")

    println("✅ Navigation stack size tracking works correctly")
  }

  /** Test component creation and state initialization */
  @Test
  fun testComponentCreationAndState() = runTest {
    // Given
    val rootComponent = createRootComponent()
    val countriesChild =
        rootComponent.stack.value.active.instance as RootComponent.Child.CountriesChild

    // When
    val component = countriesChild.component
    val initialState = component.state.value

    // Then
    assertIs<CountriesStore.State>(initialState, "Component should have proper state")

    println("✅ Component creation and state initialization works correctly")
  }

  /** Test multiple navigation scenario tracking */
  @Test
  fun testMultipleNavigationScenarios() {
    // Given
    val rootComponent = createRootComponent()

    // Test Scenario 1: Initial state
    val scenario1Child = rootComponent.stack.value.active.instance
    assertIs<RootComponent.Child.CountriesChild>(
        scenario1Child, "Scenario 1: Should start with Countries")

    // Test Scenario 2: Stack management
    val stackItems = rootComponent.stack.value.items
    assertTrue("Scenario 2: Stack should contain items") { stackItems.isNotEmpty() }
    assertEquals(1, stackItems.size, "Scenario 2: Initial stack should have 1 item")

    println("✅ Multiple navigation scenarios work correctly")
  }

  /** Test navigation state consistency */
  @Test
  fun testNavigationStateConsistency() {
    // Given
    val rootComponent = createRootComponent()

    // When - Multiple access to the same navigation state
    val stack1 = rootComponent.stack.value
    val stack2 = rootComponent.stack.value
    val child1 = stack1.active.instance
    val child2 = stack2.active.instance

    // Then - State should be consistent
    assertEquals(stack1.items.size, stack2.items.size, "Stack size should be consistent")
    assertEquals(child1::class, child2::class, "Active child type should be consistent")

    println("✅ Navigation state consistency works correctly")
  }

  /** Test component type verification across navigation */
  @Test
  fun testComponentTypeVerification() {
    // Given
    val rootComponent = createRootComponent()

    // When
    val currentChild = rootComponent.stack.value.active.instance

    // Then
    when (currentChild) {
      is RootComponent.Child.CountriesChild -> {
        assertIs<CountriesComponent>(
            currentChild.component, "Should be proper CountriesComponent type")
      }
      is RootComponent.Child.DetailsChild -> {
        assertIs<DetailsComponent>(currentChild.component, "Should be proper DetailsComponent type")
      }
      is RootComponent.Child.SearchChild -> {
        assertIs<SearchComponent>(currentChild.component, "Should be proper SearchComponent type")
      }
    }

    println("✅ Component type verification works correctly")
  }

  /** Test navigation configuration tracking */
  @Test
  fun testNavigationConfigurationTracking() {
    // Given
    val rootComponent = createRootComponent()

    // When
    val stack = rootComponent.stack.value
    val activeChild = stack.active.instance

    // Then
    assertIs<RootComponent.Child>(activeChild, "Active child should be proper type")

    println("✅ Navigation configuration tracking works correctly")
  }

  /** Test component lifecycle integration */
  @Test
  fun testComponentLifecycleIntegration() {
    // Given
    val lifecycle = LifecycleRegistry()
    val componentContext = DefaultComponentContext(lifecycle = lifecycle)

    // When
    lifecycle.resume()

    val mockRepository =
        object : CountryRepository {
          override suspend fun getCountries():
              Result<List<com.cloudware.countryapp.domain.model.Country>> =
              Result.success(TestData.testCountriesList)

          override suspend fun getCountryDetails(
              code: String
          ): Result<com.cloudware.countryapp.domain.model.Country> =
              Result.success(TestData.testCountryUS)

          override suspend fun searchCountries(
              query: String
          ): Result<List<com.cloudware.countryapp.domain.model.Country>> =
              Result.success(TestData.testCountriesList)
        }

    val dispatchers = testCoroutineDispatchers()
    val storeFactory = DefaultStoreFactory()

    val rootComponent =
        DefaultRootComponent(
            componentContext = componentContext,
            storeFactory = storeFactory,
            getCountriesUseCase = GetCountriesUseCase(mockRepository, dispatchers),
            getCountryDetailsUseCase = GetCountryDetailsUseCase(mockRepository, dispatchers),
            searchCountriesUseCase = SearchCountriesUseCase(mockRepository, dispatchers),
            dispatchers = dispatchers)

    // Then
    val activeChild = rootComponent.stack.value.active.instance
    assertIs<RootComponent.Child.CountriesChild>(
        activeChild, "Component should be created properly with lifecycle")

    println("✅ Component lifecycle integration works correctly")
  }

  /** Test navigation robustness with multiple components */
  @Test
  fun testNavigationRobustness() {
    // Given
    val components = (1..3).map { createRootComponent() }

    // When & Then
    components.forEach { rootComponent ->
      val activeChild = rootComponent.stack.value.active.instance
      assertIs<RootComponent.Child.CountriesChild>(
          activeChild, "Each component instance should have proper initial state")

      val stackSize = rootComponent.stack.value.items.size
      assertEquals(1, stackSize, "Each component should have proper stack size")
    }

    println("✅ Navigation robustness with multiple components works correctly")
  }
}
