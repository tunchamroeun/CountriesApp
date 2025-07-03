package com.cloudware.countryapp.core.di

import org.kodein.di.DI

/** Presentation module for dependency injection Provides presentation layer dependencies */
val presentationModule =
    DI.Module("presentation") {

      // Note: Individual feature components will be created in the navigation layer
      // as they require component context and navigation callbacks that are specific
      // to each instance/screen.
    }
