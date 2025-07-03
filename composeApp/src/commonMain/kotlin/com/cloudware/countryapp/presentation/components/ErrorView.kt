package com.cloudware.countryapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.cloudware.countryapp.presentation.theme.CountriesTheme
import com.cloudware.countryapp.presentation.theme.CountryAppTextStyles
import com.cloudware.countryapp.presentation.theme.Spacing
import countriesapp.composeapp.generated.resources.*
import countriesapp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

/** Error types for different error scenarios */
enum class ErrorType {
  NETWORK,
  GENERAL,
  NO_DATA,
  TIMEOUT
}

/**
 * A component that displays error states with retry functionality
 *
 * @param errorMessage The error message to display
 * @param onRetry Callback when retry button is clicked
 * @param modifier Modifier for the error view
 * @param errorType Type of error to determine icon and styling
 * @param retryButtonText Text for the retry button
 */
@Composable
fun ErrorView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    errorType: ErrorType = ErrorType.GENERAL,
    retryButtonText: String = stringResource(Res.string.retry)
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(Spacing.Large).testTag("ErrorViewContainer"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
        // Error icon
        Icon(
            imageVector = getErrorIcon(errorType),
            contentDescription =
                when (errorType) {
                  ErrorType.NETWORK -> stringResource(Res.string.network_error_icon)
                  else -> stringResource(Res.string.error)
                },
            tint = CountriesTheme.colors.error,
            modifier = Modifier.size(Spacing.Icon.ExtraLarge).testTag("ErrorIcon"))

        // Error title
        Text(
            text = getErrorTitle(errorType),
            style = CountriesTheme.typography.headlineSmall,
            color = CountriesTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("ErrorTitle"))

        // Error message
        Text(
            text = errorMessage,
            style = CountryAppTextStyles.ErrorMessage,
            color = CountriesTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().testTag("ErrorMessage"))

        Spacer(modifier = Modifier.height(Spacing.Small))

        // Retry button
        ElevatedButton(
            onClick = onRetry,
            modifier = Modifier.testTag("RetryButton"),
            colors =
                ButtonDefaults.elevatedButtonColors(
                    containerColor = CountriesTheme.colors.errorContainer,
                    contentColor = CountriesTheme.colors.onErrorContainer),
            elevation =
                ButtonDefaults.elevatedButtonElevation(defaultElevation = Spacing.ExtraSmall)) {
              Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = null,
                  modifier = Modifier.size(Spacing.Icon.Medium))
              Spacer(modifier = Modifier.width(Spacing.Button.IconSpacing))
              Text(text = retryButtonText, style = CountriesTheme.typography.labelLarge)
            }
      }
}

/**
 * Compact error view for smaller spaces
 *
 * @param errorMessage The error message to display
 * @param onRetry Callback when retry button is clicked
 * @param modifier Modifier for the error view
 * @param retryButtonText Text for the retry button
 */
@Composable
fun CompactErrorView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    retryButtonText: String = stringResource(Res.string.retry)
) {
  Card(
      modifier = modifier.fillMaxWidth(),
      colors =
          CardDefaults.cardColors(
              containerColor = CountriesTheme.colors.errorContainer.copy(alpha = 0.1f),
              contentColor = CountriesTheme.colors.onErrorContainer),
      border = null) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.Medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
              Icon(
                  imageVector = Icons.Default.ErrorOutline,
                  contentDescription = stringResource(Res.string.error),
                  tint = CountriesTheme.colors.error,
                  modifier = Modifier.size(Spacing.Icon.Medium))

              Text(
                  text = errorMessage,
                  style = CountryAppTextStyles.ErrorMessage,
                  color = CountriesTheme.colors.onSurface,
                  modifier = Modifier.weight(1f))

              TextButton(
                  onClick = onRetry,
                  colors =
                      ButtonDefaults.textButtonColors(
                          contentColor = CountriesTheme.colors.primary)) {
                    Text(text = retryButtonText, style = CountriesTheme.typography.labelMedium)
                  }
            }
      }
}

/**
 * Error view specifically for empty states
 *
 * @param message The message to display
 * @param onAction Callback when action button is clicked
 * @param modifier Modifier for the error view
 * @param actionText Text for the action button
 */
@Composable
fun EmptyStateView(
    message: String,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    actionText: String = stringResource(Res.string.try_again)
) {
  Column(
      modifier = modifier.fillMaxWidth().padding(Spacing.ExtraLarge),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(Spacing.Medium)) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = stringResource(Res.string.no_data_available),
            tint = CountriesTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(Spacing.Icon.ExtraLarge))

        Text(
            text = stringResource(Res.string.no_results_found_title),
            style = CountriesTheme.typography.headlineSmall,
            color = CountriesTheme.colors.onSurface,
            textAlign = TextAlign.Center)

        Text(
            text = message,
            style = CountriesTheme.typography.bodyMedium,
            color = CountriesTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth())

        if (onAction != null) {
          Spacer(modifier = Modifier.height(Spacing.Small))

          OutlinedButton(
              onClick = onAction,
              colors =
                  ButtonDefaults.outlinedButtonColors(
                      contentColor = CountriesTheme.colors.primary)) {
                Text(text = actionText, style = CountriesTheme.typography.labelLarge)
              }
        }
      }
}

/** Get the appropriate icon for the error type */
@Composable
private fun getErrorIcon(errorType: ErrorType): ImageVector {
  return when (errorType) {
    ErrorType.NETWORK -> Icons.Default.WifiOff
    ErrorType.GENERAL -> Icons.Default.ErrorOutline
    ErrorType.NO_DATA -> Icons.Default.ErrorOutline
    ErrorType.TIMEOUT -> Icons.Default.ErrorOutline
  }
}

/** Get the appropriate title for the error type */
@Composable
private fun getErrorTitle(errorType: ErrorType): String {
  return when (errorType) {
    ErrorType.NETWORK -> stringResource(Res.string.network_error)
    ErrorType.GENERAL -> stringResource(Res.string.something_went_wrong)
    ErrorType.NO_DATA -> stringResource(Res.string.no_data_available)
    ErrorType.TIMEOUT -> stringResource(Res.string.request_timed_out)
  }
}

/** Preview versions for development */
@Composable
private fun ErrorViewPreview() {
  // This would be used in a preview annotation if needed
}
