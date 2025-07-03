## Project Overview

This is a Kotlin Multiplatform (KMP) project for a SpaceX data platform using:

- **Architecture**: Decompose + MVIKotlin (MVI pattern) + Domain Driven Design
- **Networking**: Apollo GraphQL Client (https://countries.trevorblades.com/graphql)
- **UI**: Compose Multiplatform
- **DI**: Kodein-DI
- **Localization**: Compose Resources for i18n
- **Platforms**: Android, iOS
- **Data Strategy**: GraphQL-only with Apollo (no local database caching)

## Module Structure

```
CountriesApp/
├── composeApp/              # Shared KMP module
│   ├── commonMain/          # Shared business logic & UI
│   ├── androidMain/         # Android-specific implementations
│   └── iosMain/             # iOS-specific implementations
├── androidApp/              # Android application module
└── iosApp/                  # iOS application module
```

## Package Structure (Domain Driven Design)

```
com.cloudware.countryapp/
├── core/                    # Core utilities and extensions
│   ├── di/                  # Dependency injection modules
│   │   ├── CoreModule.kt
│   │   ├── NetworkModule.kt
│   │   └── AppModule.kt
│   ├── navigation/          # Navigation configuration
│   │   └── RootComponent.kt
│   └── utils/               # Common utilities
│       ├── CoroutineDispatchers.kt
│       └── Extensions.kt
│
├── domain/                  # Domain layer (Business logic)
│   ├── model/               # Domain models
│   │   ├── Country.kt
│   │   ├── Language.kt
│   │   └── Continent.kt
│   ├── repository/          # Repository interfaces
│   │   └── CountryRepository.kt
│   └── usecase/             # Use cases
│       ├── GetCountriesUseCase.kt
│       ├── GetCountryDetailsUseCase.kt
│       └── SearchCountriesUseCase.kt
│
├── data/                    # Data layer
│   ├── remote/              # Remote data sources
│   │   ├── CountryRemoteDataSource.kt
│   │   └── GraphQLClient.kt
│   ├── repository/          # Repository implementations
│   │   └── CountryRepositoryImpl.kt
│   └── mapper/              # Data mappers
│       └── CountryMapper.kt
│
├── presentation/            # Presentation layer
│   ├── theme/               # Design system
│   │   ├── Theme.kt
│   │   ├── Color.kt
│   │   ├── Typography.kt
│   │   └── Spacing.kt
│   ├── components/          # Reusable UI components
│   │   ├── CountryCard.kt
│   │   ├── SearchBar.kt
│   │   └── ErrorView.kt
│   └── features/            # Feature modules
│       ├── countries/       # Countries list feature
│       │   ├── CountriesComponent.kt
│       │   ├── CountriesStore.kt
│       │   ├── CountriesScreen.kt
│       │   └── components/
│       ├── details/         # Country details feature
│       │   ├── DetailsComponent.kt
│       │   ├── DetailsStore.kt
│       │   ├── DetailsScreen.kt
│       │   └── components/
│       └── search/          # Search feature
│           ├── SearchComponent.kt
│           ├── SearchStore.kt
│           └── SearchScreen.kt
│
└── App.kt                   # Application entry point
```

## Feature Module Structure (MVIKotlin Pattern)

Each feature follows this structure:

```
feature/
├── FeatureComponent.kt      # Decompose Component interface & implementation
├── FeatureStore.kt          # MVIKotlin Store (Intent, State, Label, Executor, Reducer)
├── FeatureScreen.kt         # Compose UI
└── components/              # Feature-specific UI components
```

### Component Structure (Decompose)
```kotlin
interface FeatureComponent {
    val model: Value<Model>
    
    fun onIntent(intent: Intent)
    
    data class Model(
        val state: FeatureStore.State
    )
}

class DefaultFeatureComponent(
    componentContext: ComponentContext,
    private val store: FeatureStore
) : FeatureComponent, ComponentContext by componentContext
```

### Store Structure (MVIKotlin)
```kotlin
interface FeatureStore : Store<Intent, State, Label> {
    sealed interface Intent {
        // User intents
    }
    
    data class State(
        // UI state
    )
    
    sealed interface Label {
        // Side effects
    }
}
```

## Dependency Injection Structure (Kodein-DI)

```kotlin
// Core module
val coreModule = DI.Module("core") {
    bindSingleton { CoroutineDispatchers() }
}

// Network module
val networkModule = DI.Module("network") {
    bindSingleton { ApolloClient.Builder()... }
}

// Domain module
val domainModule = DI.Module("domain") {
    bindFactory { GetCountriesUseCase(instance()) }
    bindFactory { GetCountryDetailsUseCase(instance()) }
}

// Data module
val dataModule = DI.Module("data") {
    bindSingleton<CountryRepository> { CountryRepositoryImpl(instance()) }
}

// Presentation module
val presentationModule = DI.Module("presentation") {
    bindFactory { params -> CountriesStore(instance(), instance()) }
}
```

## Navigation Structure (Decompose)

```kotlin
sealed class Configuration : Parcelable {
    @Parcelize
    object Countries : Configuration()
    
    @Parcelize
    data class Details(val countryCode: String) : Configuration()
    
    @Parcelize
    object Search : Configuration()
}

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val navigation = StackNavigation<Configuration>()
    
    val stack = childStack(
        source = navigation,
        initialConfiguration = Configuration.Countries,
        handleBackButton = true,
        childFactory = ::child
    )
}
```

## Resource Structure

```
composeResources/
├── drawable/                # Vector drawables
├── font/                    # Custom fonts
├── strings/                 # Localization
│   ├── strings.xml         # Default (English)
│   ├── strings-es.xml      # Spanish
│   └── strings-fr.xml      # French
└── values/                  # Other resources
```

## Testing Structure

```
src/
├── commonTest/             # Shared tests
│   ├── domain/
│   │   └── usecase/
│   ├── data/
│   │   └── repository/
│   └── presentation/
│       └── store/
├── androidTest/            # Android-specific tests
└── iosTest/                # iOS-specific tests
```