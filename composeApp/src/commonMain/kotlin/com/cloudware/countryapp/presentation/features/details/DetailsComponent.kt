package com.cloudware.countryapp.presentation.features.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.core.utils.asValue
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase

/**
 * Component interface for the country details feature
 *
 * This component manages the country details state and handles user interactions following the
 * Decompose pattern for navigation and state management.
 */
interface DetailsComponent {

  /** The current state for the UI */
  val state: Value<DetailsStore.State>

  /** Handle user intents */
  fun onIntent(intent: DetailsStore.Intent)
}

/** Default implementation of DetailsComponent */
class DefaultDetailsComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val countryCode: String,
    private val getCountryDetailsUseCase: GetCountryDetailsUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val onNavigateBack: () -> Unit
) : DetailsComponent, ComponentContext by componentContext {

  private val store =
      instanceKeeper.getStore {
        DetailsStore(
            storeFactory = storeFactory,
            countryCode = countryCode,
            getCountryDetailsUseCase = getCountryDetailsUseCase,
            dispatchers = dispatchers)
      }

  init {
    // Handle store labels using MVIKotlin's observer API
    store.labels(
        observer(
            onNext = { label ->
              when (label) {
                is DetailsStore.Label.NavigateBack -> onNavigateBack()
              }
            }))

    // Load details on start
    store.accept(DetailsStore.Intent.LoadDetails)
  }

  override val state: Value<DetailsStore.State> = store.asValue()

  override fun onIntent(intent: DetailsStore.Intent) {
    store.accept(intent)
  }
}

/** Factory function for creating DetailsComponent with dependencies */
fun DetailsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    countryCode: String,
    getCountryDetailsUseCase: GetCountryDetailsUseCase,
    dispatchers: CoroutineDispatchers,
    onNavigateBack: () -> Unit
): DetailsComponent =
    DefaultDetailsComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        countryCode = countryCode,
        getCountryDetailsUseCase = getCountryDetailsUseCase,
        dispatchers = dispatchers,
        onNavigateBack = onNavigateBack)
