package com.cloudware.countryapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.presentation.theme.CountriesTheme
import com.cloudware.countryapp.presentation.theme.CountryAppTextStyles
import com.cloudware.countryapp.presentation.theme.Spacing
import countriesapp.composeapp.generated.resources.*
import countriesapp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

/**
 * A card component that displays country information in a list format
 *
 * @param country The country data to display
 * @param onClick Callback when the card is clicked
 * @param modifier Modifier for the card
 */
@Composable
fun CountryCard(country: Country, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Card(
      modifier = modifier.fillMaxWidth().clickable { onClick() },
      colors =
          CardDefaults.cardColors(
              containerColor = CountriesTheme.colors.surface,
              contentColor = CountriesTheme.colors.onSurface),
      elevation = CardDefaults.cardElevation(defaultElevation = Spacing.Card.Margin),
      shape = CountriesTheme.shapes.medium) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.CountryCard.Padding),
            verticalAlignment = Alignment.CenterVertically) {
              // Flag emoji
              Text(
                  text = country.emoji,
                  style = CountriesTheme.typography.headlineMedium,
                  modifier = Modifier.size(Spacing.Flag.Large))

              Spacer(modifier = Modifier.width(Spacing.CountryCard.FlagSpacing))

              // Country information
              Column(
                  modifier = Modifier.weight(1f).fillMaxHeight(),
                  verticalArrangement = Arrangement.spacedBy(Spacing.CountryCard.ContentSpacing)) {
                    // Country name
                    Text(
                        text = country.name,
                        style = CountryAppTextStyles.CountryName,
                        color = CountriesTheme.colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)

                    // Capital city
                    if (country.hasCapital()) {
                      Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement =
                              Arrangement.spacedBy(Spacing.CountryCard.TitleSpacing)) {
                            Text(
                                text = stringResource(Res.string.capital_label),
                                style = CountryAppTextStyles.Capital,
                                color = CountriesTheme.colors.onSurfaceVariant)
                            Text(
                                text = country.capital!!,
                                style = CountriesTheme.typography.bodyMedium,
                                color = CountriesTheme.colors.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                          }
                    }

                    // Continent and language count
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Medium),
                        verticalAlignment = Alignment.CenterVertically) {
                          // Continent
                          Surface(
                              color = CountriesTheme.colors.primaryContainer.copy(alpha = 0.1f),
                              shape = CountriesTheme.shapes.small,
                              modifier = Modifier.wrapContentSize()) {
                                Text(
                                    text = country.continent.name,
                                    style = CountryAppTextStyles.Continent,
                                    color = CountriesTheme.colors.primary,
                                    modifier =
                                        Modifier.padding(
                                            horizontal = Spacing.Small,
                                            vertical = Spacing.ExtraSmall))
                              }

                          // Language count if available
                          if (country.getLanguageCount() > 0) {
                            Text(
                                text =
                                    "${country.getLanguageCount()} ${if (country.getLanguageCount() == 1) "language" else "languages"}",
                                style = CountryAppTextStyles.Language,
                                color = CountriesTheme.colors.onSurfaceVariant)
                          }
                        }
                  }

              // Country code
              Surface(
                  color = CountriesTheme.colors.secondaryContainer.copy(alpha = 0.2f),
                  shape = CountriesTheme.shapes.small,
                  modifier = Modifier.wrapContentSize()) {
                    Text(
                        text = country.code,
                        style = CountryAppTextStyles.CountryCode,
                        color = CountriesTheme.colors.secondary,
                        modifier =
                            Modifier.padding(
                                horizontal = Spacing.Small, vertical = Spacing.ExtraSmall))
                  }
            }
      }
}

/** Preview version of CountryCard for development */
@Composable
private fun CountryCardPreview() {
  // This would be used in a preview annotation if needed
}
