package com.cloudware.countryapp.presentation.features.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.cloudware.countryapp.Platform
import com.cloudware.countryapp.core.di.DIContainer
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.presentation.components.ErrorType
import com.cloudware.countryapp.presentation.components.ErrorView
import com.cloudware.countryapp.presentation.components.PlatformActionsCard
import com.cloudware.countryapp.presentation.theme.CountriesTheme
import com.cloudware.countryapp.presentation.theme.Spacing
import countriesapp.composeapp.generated.resources.*
import countriesapp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.kodein.di.instance

/**
 * Main screen for displaying detailed information about a specific country
 *
 * @param component The details component that manages state and handles user interactions
 * @param modifier Modifier for the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(component: DetailsComponent, modifier: Modifier = Modifier) {
  val state by component.state.subscribeAsState()

  Scaffold(
      modifier = modifier.fillMaxSize().testTag("DetailsScreen"),
      topBar = {
        DetailsTopBar(
            title = state.country?.getDisplayName() ?: stringResource(Res.string.country_details),
            onBackClick = { component.onIntent(DetailsStore.Intent.GoBack) })
      }) { paddingValues ->
        DetailsContent(
            state = state,
            onIntent = component::onIntent,
            modifier = Modifier.padding(paddingValues))
      }
}

/** Top bar for the details screen */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsTopBar(title: String, onBackClick: () -> Unit) {
  TopAppBar(
      title = {
        Text(
            text = title,
            style = CountriesTheme.typography.headlineMedium,
            color = CountriesTheme.colors.onSurface)
      },
      navigationIcon = {
        IconButton(onClick = onBackClick, modifier = Modifier.testTag("BackButton")) {
          Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(Res.string.navigate_back),
              tint = CountriesTheme.colors.onSurface)
        }
      },
      colors =
          TopAppBarDefaults.topAppBarColors(
              containerColor = CountriesTheme.colors.surface,
              titleContentColor = CountriesTheme.colors.onSurface,
              navigationIconContentColor = CountriesTheme.colors.onSurface))
}

/** Main content for the details screen */
@Composable
private fun DetailsContent(
    state: DetailsStore.State,
    onIntent: (DetailsStore.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
  when {
    state.isLoading -> {
      LoadingContent()
    }

    state.hasError -> {
      ErrorContent(
          error = state.error ?: "Unknown error occurred",
          countryCode = state.countryCode,
          onRetry = { onIntent(DetailsStore.Intent.Retry) })
    }

    state.hasData -> {
      CountryDetailsContent(country = state.country!!, modifier = modifier)
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
                  text = stringResource(Res.string.loading_country_details),
                  style = CountriesTheme.typography.bodyMedium,
                  color = CountriesTheme.colors.onSurfaceVariant)
            }
      }
}

/** Error state content */
@Composable
private fun ErrorContent(error: String, countryCode: String, onRetry: () -> Unit) {
  Box(modifier = Modifier.fillMaxSize().testTag("ErrorView"), contentAlignment = Alignment.Center) {
    ErrorView(
        errorMessage = stringResource(Res.string.failed_to_load_details, countryCode, error),
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

/** Country details content */
@Composable
private fun CountryDetailsContent(country: Country, modifier: Modifier = Modifier) {
  // Get platform instance from DI
  val platform by DIContainer.di.instance<Platform>()

  Column(
      modifier =
          modifier
              .fillMaxSize()
              .testTag("CountryDetailsContent")
              .verticalScroll(rememberScrollState())
              .padding(Spacing.Medium),
      verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
        // Header with flag and name
        CountryHeader(country = country)

        // Platform actions card
        PlatformActionsCard(country = country, platform = platform)

        // Basic information
        BasicInfoSection(country = country)

        // Continent information
        ContinentSection(country = country)

        // Languages section
        if (country.languages.isNotEmpty()) {
          LanguagesSection(country = country)
        }

        // Currency information
        CurrencySection(country = country)

        // Phone codes
        PhoneSection(country = country)

        // Technical information
        TechnicalInfoSection(country = country)
      }
}

/** Country header with flag and name */
@Composable
private fun CountryHeader(country: Country) {
  Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = CountriesTheme.colors.primaryContainer)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.Large),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = country.emoji,
                  style = CountriesTheme.typography.displayLarge,
                  textAlign = TextAlign.Center)
              Spacer(modifier = Modifier.height(Spacing.Small))
              Text(
                  text = country.name,
                  style = CountriesTheme.typography.headlineLarge,
                  fontWeight = FontWeight.Bold,
                  color = CountriesTheme.colors.onPrimaryContainer,
                  textAlign = TextAlign.Center)
              if (country.native != null && country.native != country.name) {
                Text(
                    text = country.native,
                    style = CountriesTheme.typography.bodyLarge,
                    color = CountriesTheme.colors.onPrimaryContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center)
              }
            }
      }
}

/** Basic information section */
@Composable
private fun BasicInfoSection(country: Country) {
  InfoCard(title = stringResource(Res.string.basic_information)) {
    InfoRow(label = stringResource(Res.string.code), value = country.code)

    if (country.hasCapital()) {
      InfoRow(label = stringResource(Res.string.capital), value = country.capital!!)
    }
  }
}

/** Continent information section */
@Composable
private fun ContinentSection(country: Country) {
  InfoCard(title = stringResource(Res.string.location)) {
    InfoRow(
        label = stringResource(Res.string.continent),
        value = "${country.continent.name} (${country.continent.code})")

    if (country.awsRegion != null) {
      InfoRow(label = stringResource(Res.string.aws_region), value = country.awsRegion)
    }
  }
}

/** Languages section */
@Composable
private fun LanguagesSection(country: Country) {
  InfoCard(title = stringResource(Res.string.languages_count, country.getLanguageCount())) {
    country.languages.forEach { language ->
      InfoRow(
          label = language.name,
          value =
              "${language.code}${if (language.native != language.name) " • ${language.native}" else ""}")
    }
  }
}

/** Currency information section */
@Composable
private fun CurrencySection(country: Country) {
  InfoCard(title = stringResource(Res.string.currency_information)) {
    val primaryCurrency = country.getPrimaryCurrency()
    if (primaryCurrency != null) {
      InfoRow(label = stringResource(Res.string.primary_currency), value = primaryCurrency)
    }

    if (country.currencies.size > 1) {
      InfoRow(
          label = stringResource(Res.string.all_currencies),
          value = country.currencies.joinToString(", "))
    }
  }
}

/** Phone codes section */
@Composable
private fun PhoneSection(country: Country) {
  InfoCard(title = stringResource(Res.string.phone_information)) {
    val primaryPhone = country.getPrimaryPhone()
    if (primaryPhone != null) {
      InfoRow(label = stringResource(Res.string.primary_phone_code), value = "+$primaryPhone")
    }

    if (country.phones.size > 1) {
      InfoRow(
          label = stringResource(Res.string.all_phone_codes),
          value = country.phones.joinToString(", ") { "+$it" })
    }
  }
}

/** Technical information section */
@Composable
private fun TechnicalInfoSection(country: Country) {
  InfoCard(title = stringResource(Res.string.technical_information)) {
    InfoRow(label = stringResource(Res.string.country_code), value = country.code)

    if (country.emojiU != null) {
      InfoRow(label = stringResource(Res.string.unicode_emoji), value = country.emojiU)
    }
  }
}

/** Reusable info card component */
@Composable
private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
  Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = CountriesTheme.colors.surfaceVariant)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
              Text(
                  text = title,
                  style = CountriesTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = CountriesTheme.colors.primary)
              HorizontalDivider(
                  color = CountriesTheme.colors.outline.copy(alpha = 0.3f),
                  modifier = Modifier.padding(vertical = Spacing.Small))
              content()
            }
      }
}

/** Reusable info row component */
@Composable
private fun InfoRow(label: String, value: String) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            style = CountriesTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = CountriesTheme.colors.onSurfaceVariant,
            modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(Spacing.Small))
        Text(
            text = value,
            style = CountriesTheme.typography.bodyMedium,
            color = CountriesTheme.colors.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End)
      }
}
