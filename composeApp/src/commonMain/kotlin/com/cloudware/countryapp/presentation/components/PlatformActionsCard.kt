package com.cloudware.countryapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.cloudware.countryapp.Platform
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.presentation.theme.CountriesTheme
import com.cloudware.countryapp.presentation.theme.Spacing
import countriesapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * Card component that displays platform-specific action buttons for a country
 *
 * @param country The country for which to show actions
 * @param platform The platform instance to execute actions
 * @param modifier Modifier for the card
 */
@Composable
fun PlatformActionsCard(country: Country, platform: Platform, modifier: Modifier = Modifier) {
  Card(
      modifier = modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = CountriesTheme.colors.surfaceVariant)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
              Text(
                  text = stringResource(Res.string.quick_actions),
                  style = CountriesTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = CountriesTheme.colors.primary)

              HorizontalDivider(
                  color = CountriesTheme.colors.outline.copy(alpha = 0.3f),
                  modifier = Modifier.padding(vertical = Spacing.Small))

              // Action buttons row
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(Spacing.Small)) {
                    // Share country info
                    ActionButton(
                        onClick = {
                          val shareText = buildShareText(country)
                          platform.shareText(shareText, "Country Information: ${country.name}")
                        },
                        icon = Icons.Default.Share,
                        label = stringResource(Res.string.share),
                        modifier = Modifier.weight(1f))

                    // Call with country code (if available)
                    if (country.getPrimaryPhone() != null) {
                      ActionButton(
                          onClick = { platform.dialPhoneNumber("+${country.getPrimaryPhone()}") },
                          icon = Icons.Default.Phone,
                          label = stringResource(Res.string.call),
                          modifier = Modifier.weight(1f))
                    }

                    // Open in maps
                    ActionButton(
                        onClick = { platform.openMaps(country.name) },
                        icon = Icons.Default.LocationOn,
                        label = stringResource(Res.string.maps),
                        modifier = Modifier.weight(1f))

                    // Learn more (Wikipedia)
                    ActionButton(
                        onClick = {
                          val wikipediaUrl =
                              "https://en.wikipedia.org/wiki/${country.name.replace(" ", "_")}"
                          platform.openUrl(wikipediaUrl)
                        },
                        icon = Icons.Default.Info,
                        label = stringResource(Res.string.learn_more),
                        modifier = Modifier.weight(1f))
                  }

              // Platform info
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Platform: ${platform.name}",
                        style = CountriesTheme.typography.bodySmall,
                        color = CountriesTheme.colors.onSurfaceVariant)

                    if (!platform.isNetworkAvailable()) {
                      Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = CountriesTheme.colors.error,
                                modifier = Modifier.size(Spacing.Medium))
                            Text(
                                text = "No Network",
                                style = CountriesTheme.typography.bodySmall,
                                color = CountriesTheme.colors.error)
                          }
                    }
                  }
            }
      }
}

/** Individual action button component */
@Composable
private fun ActionButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
  OutlinedButton(
      onClick = onClick,
      modifier = modifier,
      colors = ButtonDefaults.outlinedButtonColors(contentColor = CountriesTheme.colors.primary)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall)) {
              Icon(
                  imageVector = icon,
                  contentDescription = null,
                  modifier = Modifier.size(Spacing.Medium))
              Text(text = label, style = CountriesTheme.typography.bodySmall)
            }
      }
}

/** Builds shareable text content for a country */
private fun buildShareText(country: Country): String {
  return buildString {
    appendLine("🌍 ${country.getDisplayName()}")
    appendLine()

    if (country.hasCapital()) {
      appendLine("🏛️ Capital: ${country.capital}")
    }

    appendLine("🌎 Continent: ${country.continent.name}")

    if (country.getPrimaryCurrency() != null) {
      appendLine("💰 Currency: ${country.getPrimaryCurrency()}")
    }

    if (country.getPrimaryPhone() != null) {
      appendLine("📞 Phone Code: +${country.getPrimaryPhone()}")
    }

    if (country.languages.isNotEmpty()) {
      val languages = country.languages.joinToString(", ") { it.name }
      appendLine("🗣️ Languages: $languages")
    }

    appendLine()
    appendLine("Shared from Countries App")
  }
}
