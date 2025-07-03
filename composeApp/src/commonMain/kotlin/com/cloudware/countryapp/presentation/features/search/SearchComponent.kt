package com.cloudware.countryapp.presentation.features.search

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.core.utils.asValue
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase

/**
 * Component interface for the search feature
 *
 * This component manages the search state and handles user interactions following the Decompose
 * pattern for navigation and state management.
 */
interface SearchComponent {

  /** The current state for the UI */
  val state: Value<SearchStore.State>

  /** Handle user intents */
  fun onIntent(intent: SearchStore.Intent)
}

/** Default implementation of SearchComponent */
class DefaultSearchComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val searchCountriesUseCase: SearchCountriesUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val onCountrySelected: (String) -> Unit,
    private val onBackClicked: () -> Unit
) : SearchComponent, ComponentContext by componentContext {

  private val store =
      instanceKeeper.getStore {
        SearchStore(
            storeFactory = storeFactory,
            searchCountriesUseCase = searchCountriesUseCase,
            dispatchers = dispatchers)
      }

  init {
    // Handle store labels using MVIKotlin's observer API
    store.labels(
        observer(
            onNext = { label ->
              when (label) {
                is SearchStore.Label.NavigateToDetails -> onCountrySelected(label.countryCode)
                is SearchStore.Label.NavigateBack -> onBackClicked()
              }
            }))
  }

  override val state: Value<SearchStore.State> = store.asValue()

  override fun onIntent(intent: SearchStore.Intent) {
    store.accept(intent)
  }
}

/** Factory function for creating SearchComponent with dependencies */
fun SearchComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    searchCountriesUseCase: SearchCountriesUseCase,
    dispatchers: CoroutineDispatchers,
    onCountrySelected: (String) -> Unit,
    onBackClicked: () -> Unit
): SearchComponent =
    DefaultSearchComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        searchCountriesUseCase = searchCountriesUseCase,
        dispatchers = dispatchers,
        onCountrySelected = onCountrySelected,
        onBackClicked = onBackClicked)
