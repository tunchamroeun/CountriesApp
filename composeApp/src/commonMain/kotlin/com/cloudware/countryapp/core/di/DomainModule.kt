package com.cloudware.countryapp.core.di

import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

/** Domain module for dependency injection Provides domain layer dependencies including use cases */
val domainModule =
    DI.Module("domain") {

      // Use case singletons - bind as singletons for reuse
      bindSingleton<GetCountriesUseCase> { GetCountriesUseCase(instance(), instance()) }
      bindSingleton<GetCountryDetailsUseCase> { GetCountryDetailsUseCase(instance(), instance()) }
      bindSingleton<SearchCountriesUseCase> { SearchCountriesUseCase(instance(), instance()) }
    }
