package com.cloudware.countryapp.presentation.features.search

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface SearchStore : Store<SearchStore.Intent, SearchStore.State, SearchStore.Label> {

  sealed interface Intent {
    data class SearchQuery(val query: String) : Intent

    data object ClearSearch : Intent

    data class SelectCountry(val countryCode: String) : Intent

    data object Retry : Intent

    data object GoBack : Intent
  }

  data class State(
      val query: String = "",
      val results: List<Country> = emptyList(),
      val isLoading: Boolean = false,
      val error: String? = null,
      val isSearching: Boolean = false
  ) {
    val hasResults: Boolean = results.isNotEmpty()
    val hasError: Boolean = error != null
    val isEmpty: Boolean = !isLoading && !hasResults && error == null && query.trim().isNotEmpty()
    val hasQuery: Boolean = query.trim().isNotEmpty()
    val showEmptyState: Boolean = isEmpty && hasQuery
    val showInitialState: Boolean = !hasQuery && !isLoading && !hasError
  }

  sealed interface Label {
    data class NavigateToDetails(val countryCode: String) : Label

    data object NavigateBack : Label
  }
}

fun SearchStore(
    storeFactory: StoreFactory,
    searchCountriesUseCase: SearchCountriesUseCase,
    dispatchers: CoroutineDispatchers
): SearchStore =
    object :
        SearchStore,
        Store<SearchStore.Intent, SearchStore.State, SearchStore.Label> by storeFactory.create(
            name = "SearchStore",
            initialState = SearchStore.State(),
            bootstrapper = null,
            executorFactory = { SearchExecutor(searchCountriesUseCase, dispatchers) },
            reducer = SearchReducer) {}

private class SearchExecutor(
    private val searchCountriesUseCase: SearchCountriesUseCase,
    dispatchers: CoroutineDispatchers
) :
    CoroutineExecutor<SearchStore.Intent, Unit, SearchStore.State, Message, SearchStore.Label>(
        mainContext = dispatchers.main) {

  private var searchJob: Job? = null
  private var currentQuery: String = ""

  override fun executeIntent(intent: SearchStore.Intent) {
    when (intent) {
      is SearchStore.Intent.SearchQuery -> searchQuery(intent.query)
      is SearchStore.Intent.ClearSearch -> clearSearch()
      is SearchStore.Intent.SelectCountry ->
          publish(SearchStore.Label.NavigateToDetails(intent.countryCode))
      is SearchStore.Intent.Retry -> retrySearch()
      is SearchStore.Intent.GoBack -> handleBackClick()
    }
  }

  private fun searchQuery(query: String) {
    // Cancel previous search
    searchJob?.cancel()

    // Store current query
    currentQuery = query

    // Update query immediately
    dispatch(Message.QueryUpdated(query))

    // Clear results if query is empty
    if (query.trim().isEmpty()) {
      dispatch(Message.SearchCleared)
      return
    }

    // Debounce search
    searchJob =
        scope.launch {
          delay(SEARCH_DEBOUNCE_MS)
          performSearch(query)
        }
  }

  private suspend fun performSearch(query: String) {
    dispatch(Message.SearchStarted)

    searchCountriesUseCase(query)
        .fold(
            onSuccess = { countries -> dispatch(Message.SearchSuccess(countries)) },
            onFailure = { error ->
              dispatch(Message.SearchFailure(error.message ?: "Search failed"))
            })
  }

  private fun clearSearch() {
    searchJob?.cancel()
    currentQuery = ""
    dispatch(Message.SearchCleared)
    dispatch(Message.QueryUpdated(""))
  }

  private fun retrySearch() {
    if (currentQuery.isNotEmpty()) {
      scope.launch { performSearch(currentQuery) }
    }
  }

  private fun handleBackClick() {
    // Cancel any ongoing searches and clean up state
    searchJob?.cancel()
    publish(SearchStore.Label.NavigateBack)
  }

  companion object {
    private const val SEARCH_DEBOUNCE_MS = 300L
  }
}

private sealed interface Message {
  data class QueryUpdated(val query: String) : Message

  data object SearchStarted : Message

  data class SearchSuccess(val countries: List<Country>) : Message

  data class SearchFailure(val error: String) : Message

  data object SearchCleared : Message
}

private object SearchReducer : Reducer<SearchStore.State, Message> {
  override fun SearchStore.State.reduce(msg: Message): SearchStore.State =
      when (msg) {
        is Message.QueryUpdated -> copy(query = msg.query, error = null)
        is Message.SearchStarted -> copy(isLoading = true, isSearching = true, error = null)
        is Message.SearchSuccess ->
            copy(isLoading = false, isSearching = false, results = msg.countries, error = null)
        is Message.SearchFailure -> copy(isLoading = false, isSearching = false, error = msg.error)
        is Message.SearchCleared ->
            copy(results = emptyList(), isLoading = false, isSearching = false, error = null)
      }
}
