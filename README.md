# ShareSplit - Android Expense Tracking App

ShareSplit is a modern Android application built with Jetpack Compose that helps users track and split group expenses. The app integrates with Google Drive for bill image storage and Firebase for backend services.

## Features

### Core Functionalities
- **User Authentication**: Google Sign-In with Firebase Authentication
- **Group Management**: Create groups, add/remove members via email invitation
- **Expense Tracking**: Add, edit, delete expenses with bill image uploads
- **Google Drive Integration**: Automatic folder creation and file sharing for bill images
- **Expense Splitting**: Equal, percentage, or custom split options
- **Settlements**: Record payments and track balances
- **Push Notifications**: Firebase Cloud Messaging for real-time updates
- **Activity Feed**: Timeline of all group actions
- **Reports & Export**: Generate PDF/CSV summaries

### Technical Features
- **Modern UI**: Material You design with Jetpack Compose
- **MVVM Architecture**: Clean separation of concerns
- **Dependency Injection**: Hilt for dependency management
- **Real-time Updates**: Firestore listeners for live data
- **Image Handling**: Coil for efficient image loading
- **Navigation**: Compose Navigation with bottom navigation

## Tech Stack

- **Frontend**: Kotlin, Jetpack Compose, Material 3
- **Backend**: Firebase (Firestore, Auth, FCM, Storage)
- **Google Services**: Google Drive API, Google Sign-In
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **Image Loading**: Coil
- **Navigation**: Compose Navigation
- **Build System**: Gradle

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (API level 24)
- Google Cloud Console account
- Firebase project

### 1. Firebase Setup

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app to your Firebase project:
   - Package name: `com.sharesplit.app`
   - Download the `google-services.json` file
3. Place `google-services.json` in the `app/` directory
4. Enable the following Firebase services:
   - Authentication (Google Sign-In)
   - Firestore Database
   - Cloud Messaging
   - Storage (optional, for backup)

### 2. Google Cloud Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your Firebase project
3. Enable the Google Drive API
4. Create OAuth 2.0 credentials:
   - Application type: Android
   - Package name: `com.sharesplit.app`
   - SHA-1 certificate fingerprint (get from Android Studio)

### 3. Project Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd Sharesplit
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Build and run the project

### 4. Configuration

#### Firebase Security Rules
Set up Firestore security rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Group members can read/write group data
    match /groups/{groupId} {
      allow read, write: if request.auth != null && 
        request.auth.uid in resource.data.members;
    }
    
    // Group members can read/write expenses
    match /expenses/{expenseId} {
      allow read, write: if request.auth != null && 
        request.auth.uid in get(/databases/$(database)/documents/groups/$(resource.data.groupId)).data.members;
    }
  }
}
```

#### Google Drive Permissions
The app requests the following Google Drive scopes:
- `https://www.googleapis.com/auth/drive.file` - Access to files created by the app
- `https://www.googleapis.com/auth/drive` - Full access to Google Drive

## Project Structure

```
app/src/main/java/com/sharesplit/app/
├── data/
│   ├── model/           # Data models (User, Group, Expense, etc.)
│   └── repository/      # Repository interfaces and implementations
├── di/                  # Dependency injection modules
├── service/             # Firebase services
├── ui/
│   ├── navigation/      # Navigation components
│   ├── screens/         # UI screens
│   └── theme/           # Material theme
├── viewmodel/           # ViewModels
├── MainActivity.kt      # Main activity
└── ShareSplitApplication.kt
```

## Key Components

### Data Models
- `User`: User profile information
- `Group`: Group details with member list
- `Expense`: Expense data with split information
- `Settlement`: Payment records
- `Activity`: Activity feed items

### Repositories
- `AuthRepository`: Authentication operations
- `GroupRepository`: Group management
- `ExpenseRepository`: Expense operations
- `GoogleDriveRepository`: Google Drive file operations

### ViewModels
- `AuthViewModel`: Authentication state management
- `GroupViewModel`: Group operations and state
- `ExpenseViewModel`: Expense management (to be implemented)

## Usage

### Creating a Group
1. Sign in with Google account
2. Navigate to Groups tab
3. Tap the FAB to create a new group
4. Enter group name and description
5. Add member emails
6. The app automatically creates a Google Drive folder and shares it with members

### Adding Expenses
1. Select a group
2. Tap the FAB to add an expense
3. Enter expense details (title, amount, date)
4. Select who paid and how to split
5. Optionally add a bill image (automatically uploaded to Drive)
6. Save the expense

### Viewing Bill Images
1. Tap on an expense with a bill image
2. The image is loaded from Google Drive
3. No local storage is used

## Security Features

- Firebase Authentication with Google Sign-In
- Firestore security rules for data access control
- Google Drive permissions for file access
- No local storage of sensitive data
- Secure token management

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please open an issue on GitHub or contact the development team.

## Future Enhancements

- OCR integration for automatic bill reading
- Multiple currency support
- In-app group chat
- Dark mode improvements
- Offline support
- Advanced expense analytics
- Integration with banking apps 