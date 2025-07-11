import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.apollo)
}

kotlin {
  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
  }

  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libs.androidx.activity.compose)
      implementation(libs.androidx.core.ktx)
    }
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.materialIconsExtended)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(libs.androidx.lifecycle.runtimeCompose)
      // Apollo
      implementation(libs.apollo.runtime)
      // Decompose
      implementation(libs.decompose)
      implementation(libs.decompose.extensions)
      // MVI
      implementation(libs.mvikotlin)
      implementation(libs.essenty.lifecycle.coroutines)
      implementation(libs.mvikotlin.main)
      implementation(libs.mvikotlin.extensions.coroutines)
      implementation(libs.mvikotlin.logging)
      implementation(libs.mvikotlin.timetravel)
      implementation(libs.kodein.di)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
    }

    androidUnitTest.dependencies {
      // JUnit is only available on JVM/Android targets
      implementation(libs.kotlin.testJunit)
    }

    androidInstrumentedTest.dependencies {
      // JUnit is only available on JVM/Android targets
      implementation(libs.kotlin.testJunit)
    }
  }
}

android {
  namespace = "com.cloudware.countryapp"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    // Apollo
    applicationId = "com.cloudware.countryapp"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
  buildTypes { getByName("release") { isMinifyEnabled = false } }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  lint {
    disable += "NullSafeMutableLiveData"
    // Suppress Kotlin version compatibility warnings during linting
    disable += "KotlinPropertyAccess"
    checkReleaseBuilds = false
    abortOnError = false
  }
}

dependencies { debugImplementation(compose.uiTooling) }

apollo {
  service("service") {
    packageName.set("com.cloudware.countryapp")
    introspection {
      endpointUrl.set("https://countries.trevorblades.com/graphql")
      schemaFile.set(file("src/commonMain/graphql/schema.graphqls"))
    }
  }
}
