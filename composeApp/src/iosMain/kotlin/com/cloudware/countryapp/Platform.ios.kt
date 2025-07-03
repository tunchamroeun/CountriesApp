package com.cloudware.countryapp

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.SystemConfiguration.SCNetworkReachabilityCreateWithName
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsConnectionRequired
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsReachable
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice

@OptIn(ExperimentalForeignApi::class)
class IOSPlatform : Platform {
  override val name: String =
      UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
  override val isAndroid: Boolean = false
  override val isIOS: Boolean = true

  override fun getDeviceLocale(): String {
    // Simplified implementation - return "en" for now
    // In a production app, you would use NSLocale.preferredLanguages.first() or similar
    return "en"
  }

  override fun isNetworkAvailable(): Boolean {
    return memScoped {
      val reachability = SCNetworkReachabilityCreateWithName(null, "8.8.8.8")
      if (reachability == null) return false

      val flags = alloc<platform.SystemConfiguration.SCNetworkReachabilityFlagsVar>()
      val success = SCNetworkReachabilityGetFlags(reachability, flags.ptr)

      if (!success) return false

      val isReachable = (flags.value and kSCNetworkReachabilityFlagsReachable) != 0u
      val needsConnection = (flags.value and kSCNetworkReachabilityFlagsConnectionRequired) != 0u

      isReachable && !needsConnection
    }
  }

  override fun shareText(text: String, subject: String?) {
    // iOS sharing would typically be implemented using UIActivityViewController
    // This is a simplified version - in a real app you'd need platform-specific UI code
    println("iOS Share: $text")
  }

  override fun dialPhoneNumber(phoneNumber: String) {
    val url = NSURL.URLWithString("tel:$phoneNumber")
    if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
      UIApplication.sharedApplication.openURL(url)
    }
  }

  override fun openMaps(countryName: String) {
    val encodedCountry = countryName.replace(" ", "%20")
    val url = NSURL.URLWithString("maps://maps.apple.com/?q=$encodedCountry")
    if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
      UIApplication.sharedApplication.openURL(url)
    } else {
      // Fallback to web maps
      val webUrl = NSURL.URLWithString("https://maps.apple.com/?q=$encodedCountry")
      if (webUrl != null) {
        UIApplication.sharedApplication.openURL(webUrl)
      }
    }
  }

  override fun openUrl(url: String) {
    val nsUrl = NSURL.URLWithString(url)
    if (nsUrl != null && UIApplication.sharedApplication.canOpenURL(nsUrl)) {
      UIApplication.sharedApplication.openURL(nsUrl)
    }
  }

  override fun getAppVersion(): String {
    val bundle = NSBundle.mainBundle
    val version =
        bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"
    val build = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: "Unknown"
    return "$version ($build)"
  }
}

actual fun getPlatform(): Platform = IOSPlatform()
