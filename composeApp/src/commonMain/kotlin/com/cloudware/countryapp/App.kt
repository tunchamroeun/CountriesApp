package com.cloudware.countryapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.create
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.cloudware.countryapp.core.di.DIContainer
import com.cloudware.countryapp.core.navigation.DefaultRootComponent
import com.cloudware.countryapp.core.navigation.RootComponent
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase
import com.cloudware.countryapp.presentation.features.countries.CountriesScreen
import com.cloudware.countryapp.presentation.features.details.DetailsScreen
import com.cloudware.countryapp.presentation.features.search.SearchScreen
import com.cloudware.countryapp.presentation.theme.CountriesTheme
import org.kodein.di.instance

/**
 * Main application composable function.
 *
 * This is the entry point for the Countries App UI. It sets up the theme, initializes the
 * navigation structure, and provides the base surface for the entire application. This function
 * should be called from platform-specific entry points (MainActivity for Android,
 * MainViewController for iOS).
 */
@Composable
fun App() {
  CountriesTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      AppContent()
    }
  }
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
private fun AppContent() {
  // Initialize DI and get dependencies
  val storeFactory by DIContainer.di.instance<StoreFactory>()
  val getCountriesUseCase by DIContainer.di.instance<GetCountriesUseCase>()
  val getCountryDetailsUseCase by DIContainer.di.instance<GetCountryDetailsUseCase>()
  val searchCountriesUseCase by DIContainer.di.instance<SearchCountriesUseCase>()
  val dispatchers by DIContainer.di.instance<CoroutineDispatchers>()

  // Create root component context with proper lifecycle management
  val lifecycle = remember { LifecycleRegistry() }
  val rootComponentContext = remember { DefaultComponentContext(lifecycle = lifecycle) }

  // Create root component
  val rootComponent = remember {
    DefaultRootComponent(
        componentContext = rootComponentContext,
        storeFactory = storeFactory,
        getCountriesUseCase = getCountriesUseCase,
        getCountryDetailsUseCase = getCountryDetailsUseCase,
        searchCountriesUseCase = searchCountriesUseCase,
        dispatchers = dispatchers)
  }

  // Lifecycle management
  LaunchedEffect(lifecycle) { lifecycle.create() }

  DisposableEffect(lifecycle) { onDispose { lifecycle.destroy() } }

  CountriesApp(rootComponent = rootComponent)
}

/**
 * Core application navigation composable.
 *
 * This composable handles the navigation stack and renders the appropriate screen based on the
 * current navigation state. It uses Decompose's Children composable to manage the stack of screens
 * and ensure proper lifecycle management.
 *
 * The navigation supports:
 * - Countries list screen (main screen)
 * - Country details screen (with country code parameter)
 * - Search screen (for finding countries)
 *
 * @param rootComponent The root navigation component that manages the stack
 * @param modifier Modifier for styling the app container
 */
@Composable
private fun CountriesApp(rootComponent: RootComponent, modifier: Modifier = Modifier) {
  val stack by rootComponent.stack.subscribeAsState()

  Children(stack = stack, modifier = modifier.fillMaxSize()) {
    when (val instance = it.instance) {
      is RootComponent.Child.CountriesChild -> {
        CountriesScreen(
            component = instance.component, onSearchClicked = instance.component::onSearchClicked)
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
