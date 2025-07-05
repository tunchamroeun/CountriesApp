package com.cloudware.countryapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackGestureOverlay
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.ApplicationLifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.cloudware.countryapp.core.di.DIContainer
import com.cloudware.countryapp.core.di.createIOSDI
import com.cloudware.countryapp.core.navigation.DefaultRootComponent
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase
import org.kodein.di.instance
import platform.UIKit.UIViewController

/** Main view controller for iOS app Configures Compose UI with proper iOS lifecycle handling */
@OptIn(ExperimentalDecomposeApi::class)
fun MainViewController(): UIViewController {

  return ComposeUIViewController(
      configure = {
        // Initialize DI container with iOS-specific configuration
        if (!DIContainer.isInitialized()) {
          DIContainer.initialize(createIOSDI())
        }

        // Configure iOS-specific behaviors
        // This closure allows for future iOS-specific configurations
      }) {
        val backDispatcher = remember { BackDispatcher() }

        val storeFactory by DIContainer.di.instance<StoreFactory>()
        val getCountriesUseCase by DIContainer.di.instance<GetCountriesUseCase>()
        val getCountryDetailsUseCase by DIContainer.di.instance<GetCountryDetailsUseCase>()
        val searchCountriesUseCase by DIContainer.di.instance<SearchCountriesUseCase>()
        val dispatchers by DIContainer.di.instance<CoroutineDispatchers>()

        val rootComponent =
            DefaultRootComponent(
                componentContext =
                    DefaultComponentContext(
                        lifecycle = ApplicationLifecycle(), backHandler = backDispatcher),
                storeFactory = storeFactory,
                getCountriesUseCase = getCountriesUseCase,
                getCountryDetailsUseCase = getCountryDetailsUseCase,
                searchCountriesUseCase = searchCountriesUseCase,
                dispatchers = dispatchers)
        PredictiveBackGestureOverlay(
            backDispatcher = backDispatcher,
            backIcon = null,
            modifier = Modifier.fillMaxSize(),
        ) {
          App(component = rootComponent)
        }
      }
}

/**
 * Alternative main view controller with custom configuration Can be used if specific iOS
 * configurations are needed
 */
@OptIn(ExperimentalDecomposeApi::class)
fun MainViewControllerWithConfig(configure: () -> Unit = {}): UIViewController {
  return ComposeUIViewController(
      configure = {
        // Initialize DI container with iOS-specific configuration
        if (!DIContainer.isInitialized()) {
          DIContainer.initialize(createIOSDI())
        }

        configure()
      }) {
        val backDispatcher = remember { BackDispatcher() }

        val storeFactory by DIContainer.di.instance<StoreFactory>()
        val getCountriesUseCase by DIContainer.di.instance<GetCountriesUseCase>()
        val getCountryDetailsUseCase by DIContainer.di.instance<GetCountryDetailsUseCase>()
        val searchCountriesUseCase by DIContainer.di.instance<SearchCountriesUseCase>()
        val dispatchers by DIContainer.di.instance<CoroutineDispatchers>()

        val rootComponent =
            DefaultRootComponent(
                componentContext =
                    DefaultComponentContext(
                        lifecycle = ApplicationLifecycle(), backHandler = backDispatcher),
                storeFactory = storeFactory,
                getCountriesUseCase = getCountriesUseCase,
                getCountryDetailsUseCase = getCountryDetailsUseCase,
                searchCountriesUseCase = searchCountriesUseCase,
                dispatchers = dispatchers)
        PredictiveBackGestureOverlay(
            backDispatcher = backDispatcher,
            backIcon = null,
            modifier = Modifier.fillMaxSize(),
        ) {
          App(component = rootComponent)
        }
      }
}
