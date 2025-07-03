package com.cloudware.countryapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.net.toUri

class AndroidPlatform(private val context: Context) : Platform {
  override val name: String = "Android ${Build.VERSION.SDK_INT}"
  override val isAndroid: Boolean = true
  override val isIOS: Boolean = false

  override fun getDeviceLocale(): String {
    // API 24+ is guaranteed since minSdk is 24
    return context.resources.configuration.locales[0].language
  }

  override fun isNetworkAvailable(): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    return try {
      // API 23+ is guaranteed since minSdk is 24
      val network = connectivityManager.activeNetwork ?: return false
      val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
      capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
          capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
          capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } catch (e: Exception) {
      // Return false if there's any exception accessing network state
      false
    }
  }

  override fun shareText(text: String, subject: String?) {
    val intent =
        Intent(Intent.ACTION_SEND).apply {
          type = "text/plain"
          putExtra(Intent.EXTRA_TEXT, text)
          subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    try {
      context.startActivity(
          Intent.createChooser(intent, "Share Country Info").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          })
    } catch (e: Exception) {
      // Handle case where no sharing apps are available
      e.printStackTrace()
    }
  }

  override fun dialPhoneNumber(phoneNumber: String) {
    val intent =
        Intent(Intent.ACTION_DIAL).apply {
          data = "tel:$phoneNumber".toUri()
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    try {
      context.startActivity(intent)
    } catch (e: Exception) {
      // Handle case where dialer is not available
      e.printStackTrace()
    }
  }

  override fun openMaps(countryName: String) {
    // Try Google Maps first, fallback to generic geo intent
    val mapsIntent =
        Intent(Intent.ACTION_VIEW).apply {
          data = "geo:0,0?q=$countryName".toUri()
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    try {
      context.startActivity(mapsIntent)
    } catch (e: Exception) {
      // Handle case where maps app is not available
      e.printStackTrace()
    }
  }

  override fun openUrl(url: String) {
    val intent =
        Intent(Intent.ACTION_VIEW).apply {
          data = url.toUri()
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    try {
      context.startActivity(intent)
    } catch (e: Exception) {
      // Handle case where browser is not available
      e.printStackTrace()
    }
  }

  override fun getAppVersion(): String {
    return try {
      val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        "${packageInfo.versionName} (${packageInfo.longVersionCode})"
      } else {
        @Suppress("DEPRECATION") "${packageInfo.versionName} (${packageInfo.versionCode})"
      }
    } catch (e: PackageManager.NameNotFoundException) {
      "Unknown"
    }
  }
}

actual fun getPlatform(): Platform {
  // Note: This requires context to be passed. In a real app, you would typically
  // get this from a dependency injection framework or application context
  throw IllegalStateException("AndroidPlatform requires context. Use getPlatform(context) instead.")
}

/** Android-specific function to get platform with context */
fun getPlatform(context: Context): Platform = AndroidPlatform(context)
