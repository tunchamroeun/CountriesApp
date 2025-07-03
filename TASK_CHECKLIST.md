# Countries App - Implementation Task Checklist

## Phase 1: Project Setup & Configuration

### 1.1 Build Configuration
- [x] Set up version catalog in `gradle/libs.versions.toml`
  - [x] Add Kotlin Multiplatform version
  - [x] Add Compose Multiplatform version
  - [x] Add Apollo GraphQL version
  - [x] Add Decompose version
  - [x] Add MVIKotlin version
  - [x] Add Kodein-DI version
- [x] Configure `build.gradle.kts` for multiplatform setup
- [x] Configure `composeApp/build.gradle.kts` with all dependencies
- [x] Set up Android manifest and iOS Info.plist

### 1.2 GraphQL Setup
- [x] Add Apollo GraphQL plugin to build scripts
- [x] Create `graphql/schema.graphqls` from https://countries.trevorblades.com/graphql
- [x] Create `graphql/Countries.graphql` query
- [x] Create `graphql/Country.graphql` query for details
- [x] Generate GraphQL classes using Apollo

## Phase 2: Core Layer Setup

### 2.1 Dependency Injection Structure
- [x] Create `core/di/CoreModule.kt`
  - [x] Configure CoroutineDispatchers
  - [x] Set up common utilities
- [x] Create `core/di/NetworkModule.kt`
  - [x] Configure Apollo GraphQL client
  - [x] Set up HTTP client with proper headers
  - [x] Add GraphQLClient and CountryRemoteDataSource bindings
- [x] Create `core/di/AppModule.kt`
  - [x] Combine all DI modules
  - [x] Set up platform-specific configurations
- [x] Create `core/di/DataModule.kt`
  - [x] Bind repository implementations
  - [x] Configure data layer dependencies

### 2.2 Core Utilities
- [x] Create `core/utils/CoroutineDispatchers.kt`
  - [x] Define IO, Main, Default dispatchers
  - [x] Platform-specific implementations
- [x] Create `core/utils/Extensions.kt`
  - [x] Common extension functions
  - [x] String, List, and other utility extensions

### 2.3 Navigation Setup
- [x] Create `core/navigation/RootComponent.kt`
  - [x] Define Configuration sealed class
  - [x] Set up StackNavigation with Decompose
  - [x] Implement child factory for navigation

## Phase 3: Domain Layer Implementation

### 3.1 Domain Models
- [x] Create `domain/model/Country.kt`
  - [x] Define complete country data structure
  - [x] Include code, name, capital, population, etc.
- [x] Create `domain/model/Language.kt`
  - [x] Define language structure (code, name)
- [x] Create `domain/model/Continent.kt`
  - [x] Define continent structure (code, name)

### 3.2 Repository Interfaces
- [x] Create `domain/repository/CountryRepository.kt`
  - [x] Define `getCountries()` function
  - [x] Define `getCountryDetails(code: String)` function
  - [x] Define `searchCountries(query: String)` function
  - [x] All functions return domain models

### 3.3 Use Cases
- [x] Create `domain/usecase/GetCountriesUseCase.kt`
  - [x] Implement countries list retrieval logic
  - [x] Handle success/error states
- [x] Create `domain/usecase/GetCountryDetailsUseCase.kt`
  - [x] Implement single country details logic
  - [x] Validate country code parameter
- [x] Create `domain/usecase/SearchCountriesUseCase.kt`
  - [x] Implement search functionality
  - [x] Handle empty query states

## Phase 4: Data Layer Implementation ✅

### 4.1 Remote Data Source ✅
- [x] Create `data/remote/CountryRemoteDataSource.kt`
  - [x] Implement GraphQL queries using Apollo
  - [x] Handle network exceptions
  - [x] Return GraphQL generated models
- [x] Create `data/remote/GraphQLClient.kt`
  - [x] Configure Apollo client
  - [x] Set up error handling
  - [x] Configure request/response interceptors

### 4.2 Data Mappers ✅
- [x] Create `data/mapper/CountryMapper.kt`
  - [x] Map GraphQL models to domain models
  - [x] Handle null/optional fields
  - [x] Implement extension functions for mapping

### 4.3 Repository Implementation ✅
- [x] Create `data/repository/CountryRepositoryImpl.kt`
  - [x] Implement CountryRepository interface
  - [x] Use remote data source
  - [x] Map data models to domain models
  - [x] Handle exceptions and return appropriate results

## Phase 5: Presentation Layer Setup

### 5.1 Design System ✅
- [x] Create `presentation/theme/Theme.kt`
  - [x] Define MaterialTheme configuration
  - [x] Set up light/dark theme support
- [x] Create `presentation/theme/Color.kt`
  - [x] Define color palette
  - [x] Primary, secondary, surface colors
- [x] Create `presentation/theme/Typography.kt`
  - [x] Define text styles
  - [x] Heading, body, caption styles
- [x] Create `presentation/theme/Spacing.kt`
  - [x] Define spacing constants
  - [x] Margin, padding, component spacing

### 5.2 Reusable Components ✅
- [x] Create `presentation/components/CountryCard.kt`
  - [x] Design country item for list
  - [x] Include flag, name, capital, population
- [x] Create `presentation/components/SearchBar.kt`
  - [x] Implement search input field
  - [x] Add search icon and clear functionality
- [x] Create `presentation/components/ErrorView.kt`
  - [x] Design error state UI
  - [x] Include retry functionality

## Phase 6: Feature Implementation

### 6.1 Countries List Feature ✅
- [x] Create `presentation/features/countries/CountriesComponent.kt`
  - [x] Define Component interface
  - [x] Implement DefaultCountriesComponent
  - [x] Connect with CountriesStore
- [x] Create `presentation/features/countries/CountriesStore.kt`
  - [x] Define Intent (LoadCountries, SelectCountry, Refresh)
  - [x] Define State (loading, countries, error)
  - [x] Define Label (NavigateToDetails)
  - [x] Implement Executor with GetCountriesUseCase
  - [x] Implement Reducer for state updates
- [x] Create `presentation/features/countries/CountriesScreen.kt`
  - [x] Implement Compose UI
  - [x] LazyColumn for countries list
  - [x] Handle loading, error, and success states
  - [x] Integrate with CountriesComponent

### 6.2 Country Details Feature ✅
- [x] Create `presentation/features/details/DetailsComponent.kt`
  - [x] Define Component interface with country code parameter
  - [x] Implement DefaultDetailsComponent
  - [x] Connect with DetailsStore
- [x] Create `presentation/features/details/DetailsStore.kt`
  - [x] Define Intent (LoadDetails, GoBack, Retry)
  - [x] Define State (loading, country details, error)
  - [x] Implement Executor with GetCountryDetailsUseCase
  - [x] Implement Reducer for state updates
- [x] Create `presentation/features/details/DetailsScreen.kt`
  - [x] Implement detailed country information UI
  - [x] Show flag, name, capital, languages, currencies, phone codes, etc.
  - [x] Add back navigation with arrow icon
  - [x] Handle loading and error states
  - [x] Scrollable content with organized card sections

### 6.3 Search Feature ✅
- [x] Create `presentation/features/search/SearchComponent.kt`
  - [x] Define Component interface
  - [x] Implement DefaultSearchComponent
  - [x] Connect with SearchStore
- [x] Create `presentation/features/search/SearchStore.kt`
  - [x] Define Intent (SearchQuery, ClearSearch, SelectCountry)
  - [x] Define State (query, results, loading, error)
  - [x] Define Label (NavigateToDetails)
  - [x] Implement Executor with SearchCountriesUseCase
  - [x] Add debouncing for search queries
- [x] Create `presentation/features/search/SearchScreen.kt`
  - [x] Implement search UI with SearchBar
  - [x] Show search results in list format
  - [x] Handle empty states and no results

## Phase 7: App Integration

### 7.1 Root App Setup ✅
- [x] Update `App.kt`
  - [x] Initialize DI container
  - [x] Set up RootComponent
  - [x] Apply theme
  - [x] Handle navigation stack
- [x] Update `MainActivity.kt` (Android)
  - [x] Initialize Compose with App
  - [x] Handle Android lifecycle
- [x] Update `MainViewController.kt` (iOS)
  - [x] Initialize Compose with App
  - [x] Handle iOS lifecycle

### 7.2 Resource Localization ✅
- [x] Create `composeResources/values/strings.xml`
  - [x] Add all English strings
  - [x] App name, labels, error messages
- [x] Create `composeResources/values-es/strings.xml`
  - [x] Add Spanish translations
- [x] Create `composeResources/values-fr/strings.xml`
  - [x] Add French translations
- [x] Update UI components to use string resources

## Phase 8: Testing & Quality

### 8.1 Unit Tests
- [x] Test `domain/usecase/` classes
  - [x] Mock repository dependencies
  - [x] Test success and error scenarios
  - [x] GetCountriesUseCase with comprehensive test coverage
  - [x] GetCountryDetailsUseCase with input validation tests
  - [x] SearchCountriesUseCase with query validation tests
- [x] Test `data/mapper/` logic
  - [x] Test domain model structure and properties
  - [x] Test helper methods and edge cases
  - [x] Test null value handling
- [x] Test `presentation/store/` classes
  - [x] Test state transitions for CountriesStore
  - [x] Test intent handling and navigation
  - [x] Mock use case dependencies effectively
  - [x] Partial coverage for SearchStore and DetailsStore

### 8.2 Integration Tests ✅
- [x] Test GraphQL queries against real API
- [x] Test navigation flows
- [x] Test error handling scenarios

### 8.3 UI Tests ✅
- [x] Test Compose screens
- [x] Test user interactions
- [x] Test loading and error states

## Phase 9: Platform-Specific Implementation

### 9.1 Android Specific
- [x] Update `Platform.android.kt`
  - [x] Implement platform-specific functions
- [x] Configure Android resources
- [x] Test on Android devices/emulators

### 9.2 iOS Specific
- [x] Update `Platform.ios.kt`
  - [x] Implement platform-specific functions
- [x] Configure iOS resources
- [x] Test on iOS simulators/devices

## Phase 10: Final Polish

### 10.1 Performance & Optimization ✅
- [x] Add loading states for better UX
- [x] Implement proper error handling  
- [x] Add pull-to-refresh functionality
- [x] Optimize list performance with keys

### 10.2 Code Quality
- [x] Run linting and fix issues ✅
- [x] Add documentation comments
- [ ] Code review and refactoring
- [x] Update README with setup instructions

### 10.3 Deployment Preparation
- [ ] Test on multiple screen sizes
- [ ] Test with slow network conditions
- [ ] Verify offline behavior
- [ ] Prepare for app store submission (if needed)

---

## Development Guidelines Reminder

When implementing each feature:
1. ✅ Create Component interface and implementation first
2. ✅ Define Store with Intent, State, Label clearly
3. ✅ Implement Executor with proper error handling
4. ✅ Create Reducer for state updates
5. ✅ Build Compose UI with proper state handling
6. ✅ Register in DI module
7. ✅ Write unit tests
8. ✅ Test on both platforms

## Key Dependencies to Add

```kotlin
// In libs.versions.toml
kotlin = "1.9.20"
compose = "1.5.4"
apollo = "3.8.2"
decompose = "2.2.0"
mvikotlin = "3.2.1"
kodein = "7.20.2"
``` 