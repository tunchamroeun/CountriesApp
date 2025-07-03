package com.cloudware.countryapp.presentation.features.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.cloudware.countryapp.presentation.components.CountryCard
import com.cloudware.countryapp.presentation.components.ErrorType
import com.cloudware.countryapp.presentation.components.ErrorView
import com.cloudware.countryapp.presentation.components.SearchBar
import com.cloudware.countryapp.presentation.theme.CountriesTheme
import com.cloudware.countryapp.presentation.theme.Spacing
import countriesapp.composeapp.generated.resources.Res
import countriesapp.composeapp.generated.resources.navigate_back
import countriesapp.composeapp.generated.resources.no_countries_match
import countriesapp.composeapp.generated.resources.no_results_found
import countriesapp.composeapp.generated.resources.search_countries_hint
import countriesapp.composeapp.generated.resources.search_for_countries
import countriesapp.composeapp.generated.resources.searching_countries
import countriesapp.composeapp.generated.resources.try_again
import countriesapp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.stringResource

/**
 * Search screen for finding countries
 *
 * @param component The search component that manages state and handles user interactions
 * @param onBackClicked Callback when back button is clicked
 * @param modifier Modifier for the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    component: SearchComponent,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.fillMaxSize().testTag("SearchScreen"),
      topBar = {
        SearchTopBar(onBackClicked = { component.onIntent(SearchStore.Intent.GoBack) })
      }) { paddingValues ->
        SearchContent(
            state = state,
            onIntent = component::onIntent,
            modifier = Modifier.padding(paddingValues))
      }
}

/** Top bar for the search screen */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(onBackClicked: () -> Unit) {
  TopAppBar(
      title = {
        Text(
            text = stringResource(Res.string.search_for_countries),
            style = CountriesTheme.typography.headlineMedium,
            color = CountriesTheme.colors.onSurface)
      },
      navigationIcon = {
        IconButton(onClick = onBackClicked, modifier = Modifier.testTag("BackButton")) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.navigate_back),
              tint = CountriesTheme.colors.onSurface)
        }
      },
      colors =
          TopAppBarDefaults.topAppBarColors(
              containerColor = CountriesTheme.colors.surface,
              titleContentColor = CountriesTheme.colors.onSurface))
}

/** Main content for the search screen */
@Composable
private fun SearchContent(
    state: SearchStore.State,
    onIntent: (SearchStore.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxSize().testTag("SearchContent")) {
    // Search bar
    SearchBar(
        query = state.query,
        onQueryChange = { query -> onIntent(SearchStore.Intent.SearchQuery(query)) },
        onSearch = { query -> onIntent(SearchStore.Intent.SearchQuery(query)) },
        onClear = { onIntent(SearchStore.Intent.ClearSearch) },
        placeholder = stringResource(Res.string.search_countries_hint),
        enabled = !state.isSearching,
        modifier = Modifier.padding(Spacing.Medium).testTag("SearchBar"))

    // Search results content
    when {
      state.showInitialState -> {
        InitialSearchState()
      }

      state.isLoading -> {
        LoadingSearchContent()
      }

      state.hasError -> {
        ErrorSearchContent(
            error = state.error ?: stringResource(Res.string.unknown_error),
            onRetry = { onIntent(SearchStore.Intent.Retry) })
      }

      state.showEmptyState -> {
        EmptySearchResults(query = state.query)
      }

      state.hasResults -> {
        SearchResultsContent(
            results = state.results,
            onCountryClick = { countryCode ->
              onIntent(SearchStore.Intent.SelectCountry(countryCode))
            })
      }
    }
  }
}

/** Initial state when no search has been performed */
@Composable
private fun InitialSearchState() {
  Box(
      modifier = Modifier.fillMaxSize().testTag("InitialSearchState"),
      contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
              Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = CountriesTheme.colors.onSurfaceVariant,
                  modifier = Modifier.size(Spacing.Icon.ExtraLarge))
              Text(
                  text = stringResource(Res.string.search_for_countries),
                  style = CountriesTheme.typography.headlineSmall,
                  color = CountriesTheme.colors.onSurface,
                  textAlign = TextAlign.Center)
              Text(
                  text = stringResource(Res.string.search_countries_hint),
                  style = CountriesTheme.typography.bodyMedium,
                  color = CountriesTheme.colors.onSurfaceVariant,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(horizontal = Spacing.Large))
            }
      }
}

/** Loading state during search */
@Composable
private fun LoadingSearchContent() {
  Box(
      modifier = Modifier.fillMaxSize().testTag("LoadingSearchContent"),
      contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
              CircularProgressIndicator(color = CountriesTheme.colors.primary)
              Text(
                  text = stringResource(Res.string.searching_countries),
                  style = CountriesTheme.typography.bodyMedium,
                  color = CountriesTheme.colors.onSurfaceVariant)
            }
      }
}

/** Error state during search */
@Composable
private fun ErrorSearchContent(error: String, onRetry: () -> Unit) {
  Box(
      modifier = Modifier.fillMaxSize().testTag("ErrorSearchContent"),
      contentAlignment = Alignment.Center) {
        ErrorView(
            errorMessage = error,
            onRetry = onRetry,
            errorType =
                if (error.contains("network", ignoreCase = true)) {
                  ErrorType.NETWORK
                } else {
                  ErrorType.GENERAL
                },
            retryButtonText = stringResource(Res.string.try_again))
      }
}

/** Empty search results state */
@Composable
private fun EmptySearchResults(query: String) {
  Box(
      modifier = Modifier.fillMaxSize().testTag("EmptySearchResults"),
      contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
              Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = null,
                  tint = CountriesTheme.colors.onSurfaceVariant,
                  modifier = Modifier.size(Spacing.Icon.Large))
              Text(
                  text = stringResource(Res.string.no_results_found),
                  style = CountriesTheme.typography.headlineSmall,
                  color = CountriesTheme.colors.onSurface,
                  textAlign = TextAlign.Center)
              Text(
                  text = stringResource(Res.string.no_countries_match, query),
                  style = CountriesTheme.typography.bodyMedium,
                  color = CountriesTheme.colors.onSurfaceVariant,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(horizontal = Spacing.Large))
            }
      }
}

/** Search results list content */
@Composable
private fun SearchResultsContent(
    results: List<com.cloudware.countryapp.domain.model.Country>,
    onCountryClick: (String) -> Unit
) {
  Column(modifier = Modifier.testTag("SearchResultsContent")) {
    // Results count header
    SearchResultsHeader(
        resultsCount = results.size,
        modifier = Modifier.padding(horizontal = Spacing.Medium).testTag("SearchResultsHeader"))

    // Results list with enhanced performance
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("SearchResultsList"),
        contentPadding = PaddingValues(horizontal = Spacing.Medium, vertical = Spacing.Small),
        verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
          items(items = results, key = { country -> "search_${country.code}" }) { country ->
            CountryCard(
                country = country,
                onClick = { onCountryClick(country.code) },
                modifier = Modifier.testTag("SearchCountryCard_${country.code}"))
          }

          // Add some bottom padding for the last item
          item(key = "search_bottom_spacer") {
            Spacer(modifier = Modifier.height(Spacing.Medium).testTag("SearchBottomSpacer"))
          }
        }
  }
}

/** Header showing search results count */
@Composable
private fun SearchResultsHeader(resultsCount: Int, modifier: Modifier = Modifier) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      color = CountriesTheme.colors.surfaceVariant.copy(alpha = 0.5f),
      shape = CountriesTheme.shapes.small) {
        Text(
            text =
                when (resultsCount) {
                  0 -> "No results"
                  1 -> "1 result found"
                  else -> "$resultsCount results found"
                },
            style = CountriesTheme.typography.bodyMedium,
            color = CountriesTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(Spacing.Small))
      }
}
