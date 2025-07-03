# Countries App

A modern Kotlin Multiplatform (KMP) application for exploring countries and their details using GraphQL. Built with Compose Multiplatform for both Android and iOS platforms.

## 📱 Features

- **Countries List**: Browse all countries with essential information
- **Country Details**: View detailed information including capital, languages, currencies, and more
- **Search**: Find countries by name, code, or capital city
- **Multi-language Support**: Available in English, Spanish, and French
- **Platform Actions**: Call country phone codes, view on maps, and share information
- **Pull-to-Refresh**: Refresh data with a simple swipe gesture
- **Modern UI**: Material Design 3 with dark/light theme support

## 🏗️ Architecture

This project follows **Clean Architecture** principles with:

- **Domain Driven Design (DDD)**: Clear separation of business logic
- **MVIKotlin + Decompose**: Unidirectional data flow and navigation
- **GraphQL**: Apollo client for efficient data fetching
- **Dependency Injection**: Kodein-DI for modular architecture

### Layer Structure

```
├── Domain Layer          # Business logic & entities
│   ├── models/          # Pure Kotlin data classes
│   ├── repositories/    # Repository interfaces
│   └── usecases/        # Business logic implementation
│
├── Data Layer           # Data access & external APIs
│   ├── remote/          # GraphQL data sources
│   ├── repositories/    # Repository implementations
│   └── mappers/         # Data transformation
│
├── Presentation Layer   # UI & user interactions
│   ├── features/        # Feature-specific screens
│   ├── components/      # Reusable UI components
│   └── theme/           # Design system
│
└── Core                 # Shared utilities & DI
    ├── di/              # Dependency injection modules
    ├── navigation/      # Navigation configuration
    └── utils/           # Common utilities
```

## 🛠️ Tech Stack

- **Language**: Kotlin (2.2.0)
- **UI Framework**: Compose Multiplatform (1.8.2)
- **Architecture**: MVIKotlin (4.3.0) + Decompose (3.3.0)
- **Networking**: Apollo GraphQL (4.3.1)
- **Dependency Injection**: Kodein-DI (7.25.0)
- **Data Source**: [Countries GraphQL API](https://countries.trevorblades.com/graphql)

## 🚀 Getting Started

### Prerequisites

Before setting up the project, ensure you have the following installed:

#### For Android Development:
- **Android Studio**: Latest stable version (Hedgehog 2023.1.1 or newer)
- **Java Development Kit (JDK)**: Version 11 or higher
- **Android SDK**: API level 24 (minimum) to 35 (target)
- **Git**: For version control

#### For iOS Development (macOS only):
- **Xcode**: Version 15.0 or newer
- **iOS Simulator**: iOS 15.0 or newer
- **Kotlin Multiplatform Mobile Plugin**: Install in Android Studio

#### System Requirements:
- **macOS**: 12.0 or newer (for iOS development)
- **Windows/Linux**: Any recent version (Android development only)
- **RAM**: 8GB minimum, 16GB recommended
- **Disk Space**: 10GB free space for development tools and project

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd CountriesApp
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Choose "Open an Existing Project"
   - Navigate to the cloned directory and select it
   - Wait for initial Gradle sync to complete

3. **Install Dependencies**
   
   The project uses Gradle version catalogs for dependency management. All dependencies will be automatically downloaded during the first build.

4. **Verify Installation**
   
   Run the following command to ensure everything is set up correctly:
   ```bash
   ./gradlew check
   ```

### Building the Project

#### Android
```bash
# Debug build
./gradlew :composeApp:assembleDebug

# Release build
./gradlew :composeApp:assembleRelease

# Run on emulator/device
./gradlew :composeApp:installDebug
```

#### iOS (macOS only)
```bash
# Build iOS framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Open iOS project in Xcode
open iosApp/iosApp.xcodeproj
```

## 🏃‍♂️ Running the App

### Android
1. **Using Android Studio**:
   - Select "app" configuration
   - Choose your device/emulator
   - Click Run (▶️)

2. **Using Command Line**:
   ```bash
   ./gradlew :composeApp:installDebug
   adb shell am start -n com.cloudware.countryapp/.MainActivity
   ```

### iOS (macOS only)
1. **Using Xcode**:
   - Open `iosApp/iosApp.xcodeproj`
   - Select target device/simulator
   - Press ⌘+R to run

2. **Using Command Line**:
   ```bash
   cd iosApp
   xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
   ```

## 🧪 Testing

### Running Tests
```bash
# All tests
./gradlew check

# Unit tests only
./gradlew test

# Android instrumented tests
./gradlew connectedAndroidTest

# Specific test
./gradlew :composeApp:testDebugUnitTest
```

### Test Structure
- **Unit Tests**: Domain layer logic (`commonTest/`)
- **Integration Tests**: Repository and GraphQL integration
- **UI Tests**: Temporarily disabled (KMP Compose testing limitations)

## 📱 Platform-Specific Features

### Android
- Material Design 3 theming
- Edge-to-edge display support
- Android-specific lifecycle management
- Deep linking support (future)

### iOS
- Native iOS navigation feel
- Safe area handling
- iOS-specific lifecycle management
- iOS share sheet integration

## 🌐 API Integration

The app uses the [Countries GraphQL API](https://countries.trevorblades.com/graphql) which provides:
- Comprehensive country data
- Language and currency information
- Continent and regional data
- Real-time updates

### GraphQL Schema
The API schema is defined in `composeApp/src/commonMain/graphql/schema.graphqls` and queries are in:
- `Countries.graphql`: List all countries
- `Country.graphql`: Get specific country details

## 🎨 Design System

The app implements a custom design system based on Material Design 3:

### Colors
- **Primary**: Modern blue palette
- **Surface**: Dynamic surface colors
- **Theme Support**: Light and dark modes

### Typography
- **Headlines**: Inter font family
- **Body**: System default with proper scaling
- **Captions**: Subtle text styling

### Spacing
- **Consistent**: 4dp grid system
- **Responsive**: Adapts to different screen sizes

## 🔧 Development

### Project Structure
```
CountriesApp/
├── composeApp/                    # Shared KMP module
│   ├── src/
│   │   ├── commonMain/           # Shared code
│   │   ├── androidMain/          # Android-specific
│   │   └── iosMain/              # iOS-specific
│   └── build.gradle.kts
├── iosApp/                       # iOS application
├── gradle/                       # Gradle configuration
└── README.md
```

### Key Files
- `App.kt`: Main application entry point
- `RootComponent.kt`: Navigation configuration
- `DIContainer.kt`: Dependency injection setup
- `CountriesRepository.kt`: Data access interface

### Adding New Features
1. Create feature package under `presentation/features/`
2. Define Component interface and implementation
3. Create MVIKotlin Store with Intent, State, Label
4. Implement Compose UI Screen
5. Add navigation Configuration
6. Register in DI module
7. Write unit tests

### Code Style
- Follow Kotlin coding conventions
- Use ktlint for formatting
- Prefer composition over inheritance
- Write meaningful variable names
- Add KDoc comments for public APIs

## 🐛 Troubleshooting

### Common Issues

#### Build Errors
```bash
# Clean and rebuild
./gradlew clean build

# Clear Gradle cache
./gradlew clean --refresh-dependencies
```

#### iOS Build Issues
- Ensure Xcode command line tools are installed
- Verify iOS deployment target compatibility
- Check certificates and provisioning profiles

#### GraphQL Schema Issues
```bash
# Regenerate GraphQL code
./gradlew :composeApp:downloadApolloSchema
./gradlew :composeApp:generateApolloSources
```

#### Dependency Issues
- Check `gradle/libs.versions.toml` for version conflicts
- Ensure internet connection for dependency downloads
- Clear Gradle cache if corruption is suspected

### Getting Help
- Check the [Issues](link-to-issues) page for known problems
- Review the [STRUCTURE.md](STRUCTURE.md) for architecture details
- Consult the [TASK_CHECKLIST.md](TASK_CHECKLIST.md) for development progress

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check existing documentation
- Review the troubleshooting section

---

**Happy coding!** 🚀