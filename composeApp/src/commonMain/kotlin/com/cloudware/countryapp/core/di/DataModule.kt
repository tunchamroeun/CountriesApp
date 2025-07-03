package com.cloudware.countryapp.core.di

import com.cloudware.countryapp.data.repository.CountryRepositoryImpl
import com.cloudware.countryapp.domain.repository.CountryRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

/**
 * Data module for dependency injection Provides data layer dependencies including repository
 * implementations
 */
val dataModule =
    DI.Module("data") {

      // Repository implementation - provides data access through repository interface
      bindSingleton<CountryRepository> { CountryRepositoryImpl(remoteDataSource = instance()) }
    }
