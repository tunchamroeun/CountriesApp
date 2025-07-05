package com.cloudware.countryapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.cloudware.countryapp.core.navigation.RootComponent
import com.cloudware.countryapp.core.navigation.RootStore
import com.cloudware.countryapp.core.utils.backAnimation
import com.cloudware.countryapp.presentation.features.countries.CountriesScreen
import com.cloudware.countryapp.presentation.features.details.DetailsScreen
import com.cloudware.countryapp.presentation.features.search.SearchScreen
import com.cloudware.countryapp.presentation.theme.CountriesTheme

/**
 * Main application composable function.
 *
 * This is the entry point for the Countries App UI. It sets up the theme, initializes the
 * navigation structure, and provides the base surface for the entire application. This function
 * should be called from platform-specific entry points (MainActivity for Android,
 * MainViewController for iOS).
 */
@Composable
fun App(component: RootComponent, modifier: Modifier = Modifier) {
  CountriesTheme { AppContent(component = component, modifier = modifier) }
}

/**
 * Main application content that handles dependency injection and navigation setup.
 *
 * This composable:
 * - Retrieves dependencies from the DI container
 * - Creates and manages the root component lifecycle
 * - Sets up the navigation structure using Decompose
 * - Handles proper lifecycle management for the component tree
 */
@Composable
private fun AppContent(component: RootComponent, modifier: Modifier) {
  Children(
      stack = component.stack,
      modifier = modifier.fillMaxSize(),
      animation =
          backAnimation(
              backHandler = component.backHandler,
              onBack = { component.onIntent(RootStore.Intent.GoBack) },
          )) {
        when (val instance = it.instance) {
          is RootComponent.Child.CountriesChild -> {
            CountriesScreen(
                component = instance.component,
                onSearchClicked = instance.component::onSearchClicked)
          }

          is RootComponent.Child.DetailsChild -> {
            DetailsScreen(component = instance.component)
          }

          is RootComponent.Child.SearchChild -> {
            SearchScreen(
                component = instance.component,
                onBackClicked = { /* Navigation handled by component */ })
          }
        }
      }
}
