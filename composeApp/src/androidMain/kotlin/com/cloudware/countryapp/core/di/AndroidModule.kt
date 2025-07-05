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
fun androidModule(context: Context) =
    DI.Module("android") {
      // Bind Platform implementation with context
      bindSingleton<Platform> { getPlatform(instance<Context>()) }

      // Bind Android context first
      bindSingleton<Context> { context }
    }

/** Creates the complete DI container for Android with context */
fun createAndroidDI(context: Context): DI = DI {

  // Import Android-specific module
  import(androidModule(context = context))

  // Import all common modules
  import(coreModule)
  import(networkModule)
  import(domainModule)
  import(dataModule)
  import(presentationModule)
}
