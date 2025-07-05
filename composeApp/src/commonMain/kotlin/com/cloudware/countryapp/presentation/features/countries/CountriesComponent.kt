package com.cloudware.countryapp.presentation.features.countries

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.core.utils.asValue
import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase

/**
 * Component interface for the countries list feature
 *
 * This component manages the countries list state and handles user interactions following the
 * Decompose pattern for navigation and state management.
 */
interface CountriesComponent {

  /** The current state for the UI */
  val state: Value<CountriesStore.State>

  /** Handle user intents */
  fun onIntent(intent: CountriesStore.Intent)

  /** Callback for when search is clicked */
  fun onSearchClicked()

  fun onDetailClicked(code: String)
}

/** Default implementation of CountriesComponent */
class DefaultCountriesComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val onCountrySelected: (String) -> Unit,
    private val onSearchClickedCallback: () -> Unit
) : CountriesComponent, ComponentContext by componentContext {

  private val store =
      instanceKeeper.getStore {
        CountriesStore(
            storeFactory = storeFactory,
            getCountriesUseCase = getCountriesUseCase,
            dispatchers = dispatchers)
      }

  init {
    // Handle store labels using MVIKotlin's observer API
    //    store.labels(
    //        observer(
    //            onNext = { label ->
    //              when (label) {
    //                is CountriesStore.Label.NavigateToDetails ->
    // onCountrySelected(label.countryCode)
    //              }
    //            }))

    // Load countries on start
    store.accept(CountriesStore.Intent.LoadCountries)
  }

  override val state: Value<CountriesStore.State> = store.asValue()

  override fun onIntent(intent: CountriesStore.Intent) {
    store.accept(intent)
  }

  override fun onSearchClicked() {
    onSearchClickedCallback.invoke()
  }

  override fun onDetailClicked(code: String) {
    onCountrySelected(code)
  }
}

/** Factory function for creating CountriesComponent with dependencies */
fun CountriesComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    getCountriesUseCase: GetCountriesUseCase,
    dispatchers: CoroutineDispatchers,
    onCountrySelected: (String) -> Unit,
    onSearchClicked: () -> Unit
): CountriesComponent =
    DefaultCountriesComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        getCountriesUseCase = getCountriesUseCase,
        dispatchers = dispatchers,
        onCountrySelected = onCountrySelected,
        onSearchClickedCallback = onSearchClicked)
