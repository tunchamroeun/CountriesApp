package com.cloudware.countryapp.presentation.features.countries

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase
import kotlinx.coroutines.launch

interface CountriesStore :
    Store<CountriesStore.Intent, CountriesStore.State, CountriesStore.Label> {

  sealed interface Intent {
    data object LoadCountries : Intent

    data class SelectCountry(val countryCode: String) : Intent

    data object Refresh : Intent

    data object Retry : Intent
  }

  data class State(
      val isLoading: Boolean = false,
      val countries: List<Country> = emptyList(),
      val error: String? = null,
      val isRefreshing: Boolean = false
  ) {
    val hasData: Boolean = countries.isNotEmpty()
    val isEmpty: Boolean = !isLoading && !hasData && error == null
    val hasError: Boolean = error != null
  }

  sealed interface Label {
    data class NavigateToDetails(val countryCode: String) : Label
  }
}

fun CountriesStore(
    storeFactory: StoreFactory,
    getCountriesUseCase: GetCountriesUseCase,
    dispatchers: CoroutineDispatchers
): CountriesStore =
    object :
        CountriesStore,
        Store<CountriesStore.Intent, CountriesStore.State, CountriesStore.Label> by storeFactory
            .create(
                name = "CountriesStore",
                initialState = CountriesStore.State(),
                bootstrapper = null,
                executorFactory = { CountriesExecutor(getCountriesUseCase, dispatchers) },
                reducer = CountriesReducer) {}

private class CountriesExecutor(
    private val getCountriesUseCase: GetCountriesUseCase,
    dispatchers: CoroutineDispatchers
) :
    CoroutineExecutor<
        CountriesStore.Intent, Unit, CountriesStore.State, Message, CountriesStore.Label>(
        mainContext = dispatchers.main) {

  override fun executeIntent(intent: CountriesStore.Intent) {
    when (intent) {
      is CountriesStore.Intent.LoadCountries -> loadCountries(isRefresh = false)
      is CountriesStore.Intent.SelectCountry ->
          publish(CountriesStore.Label.NavigateToDetails(intent.countryCode))
      is CountriesStore.Intent.Refresh -> loadCountries(isRefresh = true)
      is CountriesStore.Intent.Retry -> loadCountries(isRefresh = false)
    }
  }

  private fun loadCountries(isRefresh: Boolean) {
    dispatch(Message.LoadingStarted(isRefresh))

    scope.launch {
      getCountriesUseCase()
          .fold(
              onSuccess = { countries -> dispatch(Message.LoadingSuccess(countries)) },
              onFailure = { error ->
                dispatch(Message.LoadingFailure(error.message ?: "Unknown error occurred"))
              })
    }
  }
}

private sealed interface Message {
  data class LoadingStarted(val isRefresh: Boolean) : Message

  data class LoadingSuccess(val countries: List<Country>) : Message

  data class LoadingFailure(val error: String) : Message
}

private object CountriesReducer : Reducer<CountriesStore.State, Message> {
  override fun CountriesStore.State.reduce(msg: Message): CountriesStore.State =
      when (msg) {
        is Message.LoadingStarted ->
            copy(isLoading = !msg.isRefresh, isRefreshing = msg.isRefresh, error = null)
        is Message.LoadingSuccess ->
            copy(isLoading = false, isRefreshing = false, countries = msg.countries, error = null)
        is Message.LoadingFailure ->
            copy(isLoading = false, isRefreshing = false, error = msg.error)
      }
}
