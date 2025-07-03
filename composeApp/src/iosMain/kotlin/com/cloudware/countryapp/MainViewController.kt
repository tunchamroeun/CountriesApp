package com.cloudware.countryapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.cloudware.countryapp.core.di.DIContainer
import com.cloudware.countryapp.core.di.createIOSDI
import platform.UIKit.UIViewController

/** Main view controller for iOS app Configures Compose UI with proper iOS lifecycle handling */
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
        // Wrap the app with Surface to handle iOS safe areas properly
        Surface(
            modifier = Modifier.fillMaxSize().safeDrawingPadding(),
            color = MaterialTheme.colorScheme.background) {
              App()
            }
      }
}

/**
 * Alternative main view controller with custom configuration Can be used if specific iOS
 * configurations are needed
 */
fun MainViewControllerWithConfig(configure: () -> Unit = {}): UIViewController {
  return ComposeUIViewController(
      configure = {
        // Initialize DI container with iOS-specific configuration
        if (!DIContainer.isInitialized()) {
          DIContainer.initialize(createIOSDI())
        }

        configure()
      }) {
        Surface(
            modifier = Modifier.fillMaxSize().safeDrawingPadding(),
            color = MaterialTheme.colorScheme.background) {
              App()
            }
      }
}
