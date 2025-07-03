package com.cloudware.countryapp.core.di

import com.apollographql.apollo.ApolloClient
import com.cloudware.countryapp.data.remote.CountryRemoteDataSource
import com.cloudware.countryapp.data.remote.GraphQLClient
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

/**
 * Network module for dependency injection Provides network-related dependencies including Apollo
 * GraphQL client
 */
val networkModule =
    DI.Module("network") {

      // Apollo GraphQL client for countries API with proper headers
      bindSingleton<ApolloClient> {
        ApolloClient.Builder()
            .serverUrl("https://countries.trevorblades.com/graphql")
            .addHttpHeader("Content-Type", "application/json")
            .addHttpHeader("Accept", "application/json")
            .addHttpHeader("User-Agent", "CountriesApp/1.0")
            .build()
      }

      // GraphQL client wrapper with enhanced error handling
      bindSingleton<GraphQLClient> { GraphQLClient(apolloClient = instance()) }

      // Remote data source for country operations
      bindSingleton<CountryRemoteDataSource> { CountryRemoteDataSource(graphQLClient = instance()) }
    }
