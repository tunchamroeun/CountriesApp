package com.cloudware.countryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.cloudware.countryapp.core.di.DIContainer
import com.cloudware.countryapp.core.di.createAndroidDI
import com.cloudware.countryapp.core.navigation.DefaultRootComponent
import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import com.cloudware.countryapp.domain.usecase.GetCountriesUseCase
import com.cloudware.countryapp.domain.usecase.GetCountryDetailsUseCase
import com.cloudware.countryapp.domain.usecase.SearchCountriesUseCase
import org.kodein.di.instance

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    // Enable edge-to-edge for modern Android UI
    enableEdgeToEdge()

    super.onCreate(savedInstanceState)

    try {
      // Initialize DI container with Android-specific configuration
      if (!DIContainer.isInitialized()) {
        DIContainer.initialize(createAndroidDI(context = this.applicationContext))
      }

      val storeFactory by DIContainer.di.instance<StoreFactory>()
      val getCountriesUseCase by DIContainer.di.instance<GetCountriesUseCase>()
      val getCountryDetailsUseCase by DIContainer.di.instance<GetCountryDetailsUseCase>()
      val searchCountriesUseCase by DIContainer.di.instance<SearchCountriesUseCase>()
      val dispatchers by DIContainer.di.instance<CoroutineDispatchers>()

      val rootComponent =
          DefaultRootComponent(
              componentContext = defaultComponentContext(),
              storeFactory = storeFactory,
              getCountriesUseCase = getCountriesUseCase,
              getCountryDetailsUseCase = getCountryDetailsUseCase,
              searchCountriesUseCase = searchCountriesUseCase,
              dispatchers = dispatchers)

      setContent { App(component = rootComponent) }
    } catch (e: Exception) {
      // Log error and show fallback UI if needed
      // In a real app, you might want to use a crash reporting service
      e.printStackTrace()
      finish()
    }
  }

  override fun onResume() {
    super.onResume()
    // Handle app resume if needed
  }

  override fun onPause() {
    super.onPause()
    // Handle app pause if needed
  }
}
