package com.cloudware.countryapp.core.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton

/** Core module for dependency injection Provides core utilities and common dependencies */
val coreModule =
    DI.Module("core") {

      // Coroutine dispatchers for threading management
      bindSingleton { CoroutineDispatchers() }

      // MVIKotlin StoreFactory for creating stores

      bindSingleton<StoreFactory> { LoggingStoreFactory(TimeTravelStoreFactory()) }

      // Add other core dependencies here as needed
      // Example: logging, error handling, etc.
    }
