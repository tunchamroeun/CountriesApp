package com.cloudware.countryapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.cloudware.countryapp.presentation.theme.CountriesTheme
import com.cloudware.countryapp.presentation.theme.CountryAppTextStyles
import com.cloudware.countryapp.presentation.theme.Spacing
import countriesapp.composeapp.generated.resources.*
import countriesapp.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

/**
 * A search bar component with search icon and clear functionality
 *
 * @param query Current search query
 * @param onQueryChange Callback when query changes
 * @param onSearch Callback when search is executed
 * @param onClear Callback when clear button is clicked
 * @param placeholder Placeholder text for the search field
 * @param modifier Modifier for the search bar
 * @param enabled Whether the search bar is enabled
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String = stringResource(Res.string.search_placeholder),
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
  val keyboardController = LocalSoftwareKeyboardController.current
  val focusRequester = remember { FocusRequester() }

  Surface(
      modifier = modifier.fillMaxWidth(),
      color = CountriesTheme.colors.surface,
      shape = CountriesTheme.shapes.large,
      shadowElevation = Spacing.ExtraSmall) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(
                        horizontal = Spacing.SearchBar.HorizontalPadding,
                        vertical = Spacing.SearchBar.VerticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.SearchBar.IconSpacing)) {
              // Search icon
              Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = stringResource(Res.string.search),
                  tint = CountriesTheme.colors.onSurfaceVariant,
                  modifier = Modifier.size(Spacing.Icon.Medium))

              // Search text field
              OutlinedTextField(
                  value = query,
                  onValueChange = onQueryChange,
                  placeholder = {
                    Text(
                        text = placeholder,
                        style = CountryAppTextStyles.SearchPlaceholder,
                        color = CountriesTheme.colors.onSurfaceVariant)
                  },
                  textStyle =
                      CountriesTheme.typography.bodyLarge.copy(
                          color = CountriesTheme.colors.onSurface),
                  singleLine = true,
                  enabled = enabled,
                  colors =
                      OutlinedTextFieldDefaults.colors(
                          focusedBorderColor = CountriesTheme.colors.primary,
                          unfocusedBorderColor = CountriesTheme.colors.outline,
                          disabledBorderColor = CountriesTheme.colors.outlineVariant,
                          focusedContainerColor = CountriesTheme.colors.surface,
                          unfocusedContainerColor = CountriesTheme.colors.surface,
                          disabledContainerColor = CountriesTheme.colors.surfaceVariant),
                  shape = CountriesTheme.shapes.medium,
                  keyboardOptions =
                      KeyboardOptions(
                          keyboardType = KeyboardType.Text,
                          imeAction = ImeAction.Search,
                          capitalization = KeyboardCapitalization.Words),
                  keyboardActions =
                      KeyboardActions(
                          onSearch = {
                            onSearch(query)
                            keyboardController?.hide()
                          }),
                  trailingIcon = {
                    if (query.isNotEmpty()) {
                      IconButton(
                          onClick = {
                            onClear()
                            focusRequester.requestFocus()
                          }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(Res.string.clear_search),
                                tint = CountriesTheme.colors.onSurfaceVariant,
                                modifier = Modifier.size(Spacing.Icon.Medium))
                          }
                    }
                  },
                  modifier = Modifier.weight(1f).focusRequester(focusRequester))
            }
      }
}

/**
 * Compact version of the search bar for smaller spaces
 *
 * @param query Current search query
 * @param onQueryChange Callback when query changes
 * @param onSearch Callback when search is executed
 * @param onClear Callback when clear button is clicked
 * @param placeholder Placeholder text for the search field
 * @param modifier Modifier for the search bar
 * @param enabled Whether the search bar is enabled
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String = stringResource(Res.string.search_placeholder_compact),
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
  val keyboardController = LocalSoftwareKeyboardController.current

  TextField(
      value = query,
      onValueChange = onQueryChange,
      placeholder = {
        Text(
            text = placeholder,
            style = CountryAppTextStyles.SearchPlaceholder,
            color = CountriesTheme.colors.onSurfaceVariant)
      },
      leadingIcon = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(Res.string.search),
            tint = CountriesTheme.colors.onSurfaceVariant,
            modifier = Modifier.size(Spacing.Icon.Medium))
      },
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(onClick = onClear) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(Res.string.clear_search),
                tint = CountriesTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(Spacing.Icon.Medium))
          }
        }
      },
      textStyle =
          CountriesTheme.typography.bodyMedium.copy(color = CountriesTheme.colors.onSurface),
      singleLine = true,
      enabled = enabled,
      colors =
          TextFieldDefaults.colors(
              focusedIndicatorColor = CountriesTheme.colors.primary,
              unfocusedIndicatorColor = CountriesTheme.colors.outline,
              disabledIndicatorColor = CountriesTheme.colors.outlineVariant,
              focusedContainerColor = CountriesTheme.colors.surface,
              unfocusedContainerColor = CountriesTheme.colors.surface,
              disabledContainerColor = CountriesTheme.colors.surfaceVariant),
      shape = CountriesTheme.shapes.medium,
      keyboardOptions =
          KeyboardOptions(
              keyboardType = KeyboardType.Text,
              imeAction = ImeAction.Search,
              capitalization = KeyboardCapitalization.Words),
      keyboardActions =
          KeyboardActions(
              onSearch = {
                onSearch(query)
                keyboardController?.hide()
              }),
      modifier = modifier.fillMaxWidth())
}

/** Preview versions for development */
@Composable
private fun SearchBarPreview() {
  // This would be used in a preview annotation if needed
}
