package com.cloudware.countryapp.presentation.features.countries

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.cloudware.countryapp.presentation.components.CountryCard
import com.cloudware.countryapp.presentation.components.EmptyStateView
import com.cloudware.countryapp.presentation.components.ErrorType
import com.cloudware.countryapp.presentation.components.ErrorView
import com.cloudware.countryapp.presentation.theme.CountriesTheme
import com.cloudware.countryapp.presentation.theme.Spacing
import countriesapp.composeapp.generated.resources.Res
import countriesapp.composeapp.generated.resources.countries
import countriesapp.composeapp.generated.resources.countries_count
import countriesapp.composeapp.generated.resources.dismiss
import countriesapp.composeapp.generated.resources.failed_to_refresh
import countriesapp.composeapp.generated.resources.loading_countries
import countriesapp.composeapp.generated.resources.no_countries_found
import countriesapp.composeapp.generated.resources.reload
import countriesapp.composeapp.generated.resources.search_for_countries
import countriesapp.composeapp.generated.resources.try_again
import countriesapp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.stringResource

/**
 * Main screen for displaying the list of countries
 *
 * @param component The countries component that manages state and handles user interactions
 * @param onSearchClicked Callback when search button is clicked
 * @param modifier Modifier for the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesScreen(
    component: CountriesComponent,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.fillMaxSize().testTag("CountriesScreen"),
      topBar = { CountriesTopBar(onSearchClicked = onSearchClicked) }) { paddingValues ->
        CountriesContent(
            state = state,
            onIntent = component::onIntent,
            modifier = Modifier.padding(paddingValues))
      }
}

/** Top bar for the countries screen */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountriesTopBar(onSearchClicked: () -> Unit) {
  TopAppBar(
      title = {
        Text(
            text = stringResource(Res.string.countries),
            style = CountriesTheme.typography.headlineMedium,
            color = CountriesTheme.colors.onSurface)
      },
      actions = {
        IconButton(onClick = onSearchClicked, modifier = Modifier.testTag("SearchButton")) {
          Icon(
              imageVector = Icons.Default.Search,
              contentDescription = stringResource(Res.string.search_for_countries),
              tint = CountriesTheme.colors.onSurface)
        }
      },
      colors =
          TopAppBarDefaults.topAppBarColors(
              containerColor = CountriesTheme.colors.surface,
              titleContentColor = CountriesTheme.colors.onSurface))
}

/** Main content for the countries screen */
@Composable
private fun CountriesContent(
    state: CountriesStore.State,
    onIntent: (CountriesStore.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxSize()) {
    when {
      state.isLoading && !state.hasData -> {
        LoadingContent()
      }

      state.hasError && !state.hasData -> {
        ErrorContent(
            error = state.error ?: stringResource(Res.string.unknown_error),
            onRetry = { onIntent(CountriesStore.Intent.Retry) })
      }

      state.isEmpty -> {
        EmptyContent(onRetry = { onIntent(CountriesStore.Intent.Retry) })
      }

      state.hasData -> {
        CountriesListContent(
            countries = state.countries,
            isRefreshing = state.isRefreshing,
            onCountryClick = { countryCode ->
              //              onIntent(CountriesStore.Intent.SelectCountry(countryCode))
            },
            onRefresh = { onIntent(CountriesStore.Intent.Refresh) },
            error = state.error,
            onDismissError = { onIntent(CountriesStore.Intent.LoadCountries) })
      }
    }
  }
}

/** Loading state content */
@Composable
private fun LoadingContent() {
  Box(
      modifier = Modifier.fillMaxSize().testTag("LoadingIndicator"),
      contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
              CircularProgressIndicator(color = CountriesTheme.colors.primary)
              Text(
                  text = stringResource(Res.string.loading_countries),
                  style = CountriesTheme.typography.bodyMedium,
                  color = CountriesTheme.colors.onSurfaceVariant)
            }
      }
}

/** Error state content */
@Composable
private fun ErrorContent(error: String, onRetry: () -> Unit) {
  Box(modifier = Modifier.fillMaxSize().testTag("ErrorView"), contentAlignment = Alignment.Center) {
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

/** Empty state content */
@Composable
private fun EmptyContent(onRetry: () -> Unit) {
  Box(
      modifier = Modifier.fillMaxSize().testTag("EmptyState"),
      contentAlignment = Alignment.Center) {
        EmptyStateView(
            message = stringResource(Res.string.no_countries_found),
            onAction = onRetry,
            actionText = stringResource(Res.string.reload))
      }
}

/** Countries list content with enhanced pull-to-refresh */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountriesListContent(
    countries: List<com.cloudware.countryapp.domain.model.Country>,
    isRefreshing: Boolean,
    onCountryClick: (String) -> Unit,
    onRefresh: () -> Unit,
    error: String?,
    onDismissError: () -> Unit
) {
  val pullToRefreshState = rememberPullToRefreshState()

  Column(modifier = Modifier.testTag("CountriesListContent")) {
    // Show error banner if there's an error but we still have data
    if (error != null) {
      Card(
          modifier = Modifier.fillMaxWidth().padding(Spacing.Medium).testTag("ErrorBanner"),
          colors =
              CardDefaults.cardColors(
                  containerColor = CountriesTheme.colors.errorContainer.copy(alpha = 0.1f))) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(Spacing.Medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(
                      text = stringResource(Res.string.failed_to_refresh, error),
                      style = CountriesTheme.typography.bodySmall,
                      color = CountriesTheme.colors.error,
                      modifier = Modifier.weight(1f))
                  TextButton(
                      onClick = onDismissError, modifier = Modifier.testTag("DismissErrorButton")) {
                        Text(stringResource(Res.string.dismiss))
                      }
                }
          }
    }

    // Countries statistics header
    CountriesStatsHeader(
        totalCount = countries.size, modifier = Modifier.testTag("CountriesStatsHeader"))

    // Enhanced pull-to-refresh implementation
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize().testTag("CountriesList"),
        contentAlignment = Alignment.TopCenter,
        indicator = {
          PullToRefreshDefaults.Indicator(
              modifier = Modifier.testTag("PullRefreshIndicator"),
              isRefreshing = isRefreshing,
              state = pullToRefreshState,
              color = CountriesTheme.colors.primary)
        }) {
          LazyColumn(
              contentPadding = PaddingValues(horizontal = Spacing.Medium, vertical = Spacing.Small),
              verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
                items(items = countries, key = { country -> country.code }) { country ->
                  CountryCard(
                      country = country,
                      onClick = { onCountryClick(country.code) },
                      modifier = Modifier.testTag("CountryCard_${country.code}"))
                }

                // Add some bottom padding for the last item
                item { Spacer(modifier = Modifier.height(Spacing.Medium).testTag("BottomSpacer")) }
              }
        }
  }
}

/** Statistics header showing total count */
@Composable
private fun CountriesStatsHeader(totalCount: Int, modifier: Modifier = Modifier) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      color = CountriesTheme.colors.surfaceVariant.copy(alpha = 0.5f)) {
        Text(
            text = stringResource(Res.string.countries_count, totalCount),
            style = CountriesTheme.typography.bodyMedium,
            color = CountriesTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(Spacing.Small))
      }
}
