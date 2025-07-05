package com.cloudware.countryapp.core.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase
import com.cloudware.countryapp.presentation.features.countries.CountriesComponent
import com.cloudware.countryapp.presentation.features.countries.DefaultCountriesComponent
import com.cloudware.countryapp.presentation.features.details.DefaultDetailsComponent
import com.cloudware.countryapp.presentation.features.details.DetailsComponent
import com.cloudware.countryapp.presentation.features.search.DefaultSearchComponent
import com.cloudware.countryapp.presentation.features.search.SearchComponent
import kotlinx.serialization.Serializable

interface RootComponent : BackHandlerOwner {
  val stack: Value<ChildStack<*, Child>>

  fun onIntent(intent: RootStore.Intent)

  sealed class Child {
    class CountriesChild(val component: CountriesComponent) : Child()

    class DetailsChild(val component: DetailsComponent) : Child()

    class SearchChild(val component: SearchComponent) : Child()
  }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val getCountryDetailsUseCase: GetCountryDetailsUseCase,
    private val searchCountriesUseCase: SearchCountriesUseCase,
    private val dispatchers: CoroutineDispatchers
) : RootComponent, ComponentContext by componentContext {

  private val store =
      instanceKeeper.getStore {
        RootStore(
            storeFactory = storeFactory,
        )
      }

  private val navigation = StackNavigation<Configuration>()

  override val stack: Value<ChildStack<*, RootComponent.Child>> =
      childStack(
          source = navigation,
          serializer = Configuration.serializer(),
          initialConfiguration = Configuration.Countries,
          handleBackButton = true,
          childFactory = ::child)

  init {
    store.labels(
        observer(
            onNext = { label ->
              when (label) {
                is RootStore.Label.NavigateBack -> onNavigateBack()
              }
            }))
  }

  private fun child(
      configuration: Configuration,
      componentContext: ComponentContext
  ): RootComponent.Child =
      when (configuration) {
        is Configuration.Countries ->
            RootComponent.Child.CountriesChild(
                component =
                    DefaultCountriesComponent(
                        componentContext = componentContext,
                        storeFactory = storeFactory,
                        getCountriesUseCase = getCountriesUseCase,
                        dispatchers = dispatchers,
                        onCountrySelected = { countryCode ->
                          navigation.pushNew(Configuration.Details(countryCode = countryCode))
                        },
                        onSearchClickedCallback = { navigation.pushNew(Configuration.Search) }))

        is Configuration.Details ->
            RootComponent.Child.DetailsChild(
                component =
                    DefaultDetailsComponent(
                        componentContext = componentContext,
                        storeFactory = storeFactory,
                        countryCode = configuration.countryCode,
                        getCountryDetailsUseCase = getCountryDetailsUseCase,
                        dispatchers = dispatchers,
                        onNavigateBack = { navigation.pop() }))

        is Configuration.Search ->
            RootComponent.Child.SearchChild(
                component =
                    DefaultSearchComponent(
                        componentContext = componentContext,
                        storeFactory = storeFactory,
                        searchCountriesUseCase = searchCountriesUseCase,
                        dispatchers = dispatchers,
                        onCountrySelected = { countryCode ->
                          navigation.pushNew(Configuration.Details(countryCode = countryCode))
                        },
                        onBackClicked = { navigation.pop() }))
      }

  private fun onNavigateBack() {
    navigation.pop()
  }

  override fun onIntent(intent: RootStore.Intent) {
    store.accept(intent)
  }

  @Serializable
  sealed interface Configuration {
    @Serializable data object Countries : Configuration

    @Serializable data class Details(val countryCode: String) : Configuration

    @Serializable data object Search : Configuration
  }
}
