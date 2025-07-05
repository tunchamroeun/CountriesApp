package com.cloudware.countryapp.core.navigation

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.cloudware.countryapp.domain.model.Country

interface RootStore : Store<RootStore.Intent, RootStore.State, RootStore.Label> {

  sealed interface Intent {
    data object GoBack : Intent
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

    data object NavigateBack : Label
  }
}

fun RootStore(
    storeFactory: StoreFactory,
): RootStore =
    object :
        RootStore,
        Store<RootStore.Intent, RootStore.State, RootStore.Label> by storeFactory.create(
            name = "RootStore",
            initialState = RootStore.State(),
            bootstrapper = null,
            executorFactory = { RootExecutor() },
            reducer = RootReducer) {}

private class RootExecutor() :
    CoroutineExecutor<RootStore.Intent, Unit, RootStore.State, Message, RootStore.Label>() {

  override fun executeIntent(intent: RootStore.Intent) {
    when (intent) {
      is RootStore.Intent.GoBack -> publish(RootStore.Label.NavigateBack)
    }
  }
}

private sealed interface Message {
  data class LoadingFailure(val error: String) : Message
}

private object RootReducer : Reducer<RootStore.State, Message> {
  override fun RootStore.State.reduce(msg: Message): RootStore.State =
      when (msg) {
        is Message.LoadingFailure ->
            copy(isLoading = false, isRefreshing = false, error = msg.error)
      }
}
