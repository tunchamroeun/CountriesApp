package com.cloudware.countryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.cloudware.countryapp.core.di.DIContainer
import com.cloudware.countryapp.core.di.createAndroidDI

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    // Enable edge-to-edge for modern Android UI
    enableEdgeToEdge()

    super.onCreate(savedInstanceState)

    try {
      // Initialize DI container with Android-specific configuration
      if (!DIContainer.isInitialized()) {
        DIContainer.initialize(createAndroidDI(this.applicationContext))
      }

      setContent { App() }
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

@Preview(showBackground = true)
@Composable
fun AppAndroidPreview() {
  App()
}
