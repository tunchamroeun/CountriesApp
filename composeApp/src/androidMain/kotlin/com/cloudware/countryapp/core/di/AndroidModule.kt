package com.cloudware.countryapp.core.di

import android.content.Context
import com.cloudware.countryapp.Platform
import com.cloudware.countryapp.getPlatform
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

/**
 * Android-specific dependency injection module Provides platform-specific implementations that
 * require Android context
 */
val androidModule =
    DI.Module("android") {
      // Bind Platform implementation with context
      bindSingleton<Platform> { getPlatform(instance<Context>()) }
    }

/** Creates the complete DI container for Android with context */
fun createAndroidDI(context: Context): DI = DI {
  // Bind Android context first
  bindSingleton<Context> { context }

  // Import Android-specific module
  import(androidModule)

  // Import all common modules
  import(coreModule)
  import(networkModule)
  import(domainModule)
  import(dataModule)
  import(presentationModule)
}
