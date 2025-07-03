package com.cloudware.countryapp.core.di

import com.cloudware.countryapp.Platform
import com.cloudware.countryapp.getPlatform
import org.kodein.di.DI
import org.kodein.di.bindSingleton

/** iOS-specific dependency injection module Provides platform-specific implementations for iOS */
val iosModule =
    DI.Module("ios") {
      // Bind Platform implementation for iOS
      bindSingleton<Platform> { getPlatform() }
    }

/** Creates the complete DI container for iOS */
fun createIOSDI(): DI = DI {
  // Import iOS-specific module
  import(iosModule)

  // Import all common modules
  import(coreModule)
  import(networkModule)
  import(domainModule)
  import(dataModule)
  import(presentationModule)
}
