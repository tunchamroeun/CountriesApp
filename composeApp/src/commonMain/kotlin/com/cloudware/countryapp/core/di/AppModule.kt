package com.cloudware.countryapp.core.di

import org.kodein.di.DI

/**
 * Main application module that combines all feature modules This is the central dependency
 * injection configuration for the Countries App
 *
 * Architecture: Domain Driven Design + MVIKotlin + Decompose
 * - Core: Utilities, dispatchers, platform abstractions
 * - Network: Apollo GraphQL client configuration
 * - Domain: Use cases, repository interfaces (when created)
 * - Data: Repository implementations, remote data sources (when created)
 * - Presentation: Stores, components for features (when created)
 */
val appModule =
    DI.Module("app") {

      // Import core infrastructure modules
      import(coreModule)
      import(networkModule)

      // Import domain layer modules
      import(domainModule)

      // Import data layer modules
      import(dataModule)

      // Import presentation layer modules
      import(presentationModule)

      /* Future modules to be imported as they are created:
       *
       * Domain Layer:
       * - domainModule: Use cases, repository interfaces
       *
       * Data Layer:
       * - dataModule: Repository implementations, remote data sources, mappers
       *
       * Presentation Layer:
       * - presentationModule: Stores and components for all features
       * - countriesModule: Countries list feature
       * - detailsModule: Country details feature
       * - searchModule: Search feature
       *
       * Feature-specific modules can be created separately or combined into presentationModule
       * depending on complexity and team preferences.
       */
    }
