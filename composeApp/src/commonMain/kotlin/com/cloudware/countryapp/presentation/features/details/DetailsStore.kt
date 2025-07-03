package com.cloudware.countryapp.presentation.features.details

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase
import kotlinx.coroutines.launch

interface DetailsStore : Store<DetailsStore.Intent, DetailsStore.State, DetailsStore.Label> {

  sealed interface Intent {
    data object LoadDetails : Intent

    data object GoBack : Intent

    data object Retry : Intent
  }

  data class State(
      val isLoading: Boolean = false,
      val country: Country? = null,
      val error: String? = null,
      val countryCode: String = ""
  ) {
    val hasData: Boolean = country != null
    val hasError: Boolean = error != null
  }

  sealed interface Label {
    data object NavigateBack : Label
  }
}

fun DetailsStore(
    storeFactory: StoreFactory,
    countryCode: String,
    getCountryDetailsUseCase: GetCountryDetailsUseCase,
    dispatchers: CoroutineDispatchers
): DetailsStore =
    object :
        DetailsStore,
        Store<DetailsStore.Intent, DetailsStore.State, DetailsStore.Label> by storeFactory.create(
            name = "DetailsStore",
            initialState = DetailsStore.State(countryCode = countryCode),
            bootstrapper = null,
            executorFactory = {
              DetailsExecutor(countryCode, getCountryDetailsUseCase, dispatchers)
            },
            reducer = DetailsReducer) {}

private class DetailsExecutor(
    private val countryCode: String,
    private val getCountryDetailsUseCase: GetCountryDetailsUseCase,
    private val dispatchers: CoroutineDispatchers
) :
    CoroutineExecutor<DetailsStore.Intent, Unit, DetailsStore.State, Message, DetailsStore.Label>(
        mainContext = dispatchers.main) {

  override fun executeIntent(intent: DetailsStore.Intent) {
    when (intent) {
      is DetailsStore.Intent.LoadDetails -> loadDetails()
      is DetailsStore.Intent.GoBack -> publish(DetailsStore.Label.NavigateBack)
      is DetailsStore.Intent.Retry -> loadDetails()
    }
  }

  private fun loadDetails() {
    dispatch(Message.LoadingStarted)

    scope.launch {
      getCountryDetailsUseCase(countryCode)
          .fold(
              onSuccess = { country -> dispatch(Message.LoadingSuccess(country)) },
              onFailure = { error ->
                dispatch(Message.LoadingFailure(error.message ?: "Unknown error occurred"))
              })
    }
  }
}

private sealed interface Message {
  data object LoadingStarted : Message

  data class LoadingSuccess(val country: Country) : Message

  data class LoadingFailure(val error: String) : Message
}

private object DetailsReducer : Reducer<DetailsStore.State, Message> {
  override fun DetailsStore.State.reduce(msg: Message): DetailsStore.State =
      when (msg) {
        is Message.LoadingStarted -> copy(isLoading = true, error = null)
        is Message.LoadingSuccess -> copy(isLoading = false, country = msg.country, error = null)
        is Message.LoadingFailure -> copy(isLoading = false, error = msg.error)
      }
}
