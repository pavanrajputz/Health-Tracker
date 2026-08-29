# Health Tracker App

This is a basic Health Tracker Android application that I created as part of my internship assignment.

The main purpose of this app is to allow users to create an account, enter their health details and calculate their BMI. The app also stores weight history and shows the weight changes using a graph.

## Features

The main features of the app are:

- Email and password registration
- Email and password login
- Google login
- Forgot password
- User details form
- Weight in KG and LBS
- Height in CM and Inches
- Gender selection
- Date of birth
- BMI calculation
- BMI category
- Dashboard
- Update user details
- Weight history
- 7-day weight graph
- Settings screen
- Logout
- Login session persistence
- Firebase user data security

## Technology Used

I used the following technologies for this project:

- Java
- XML
- Android Studio
- Firebase Authentication
- Firebase Firestore
- Google Sign-In
- Material Components

I used Java and XML for the Android application instead of Kotlin.

## How the App Works

The basic flow of the application is:

Login
↓
Create Account / Google Login
↓
User Details
↓
BMI Calculation
↓
Dashboard
↓
Update Details / Weight History / Settings

When a user creates an account, their health details are stored in Firebase Firestore.

The BMI is calculated using:

BMI = weight (kg) / height (m)²

The app also converts KG/LBS and CM/Inches before calculating the BMI.

## BMI Categories

The app displays the BMI category based on the calculated BMI.

- Below 18.5 - Underweight
- 18.5 to 24.9 - Normal Weight
- 25 to 29.9 - Overweight
- 30 and above - Obesity

## Firebase

Firebase is used for authentication and storing user data.

Firebase Authentication is used for:

- Email/password login
- Email/password registration
- Google login
- Password reset

Firestore is used for storing:

- User information
- Weight
- Height
- Gender
- Date of birth
- Weight history

Each user has their own UID and their data is stored using that UID.

## Firestore Structure

The basic structure is:

users
  |
  |-- user UID
       |
       |-- name
       |-- email
       |-- gender
       |-- dateOfBirth
       |-- weight
       |-- weightUnit
       |-- height
       |-- heightUnit
       |
       |-- weightHistory
             |
             |-- weight
             |-- weightKg
             |-- unit
             |-- timestamp

## Security

Firestore security rules are used so that a logged-in user can only access their own user data.

The user UID is checked before allowing read or write operations.

## Libraries Used

Some of the main libraries used in this project are:

- Firebase Authentication
- Firebase Firestore
- Google Play Services Auth
- AndroidX
- Material Components

## Special Configuration

To run this project with Firebase, the Firebase Android application needs to be configured.

The following things are required:

1. Create a Firebase project.
2. Add the Android application in Firebase.
3. Add the package name of the Android project.
4. Add SHA-1 fingerprint for Google Login.
5. Enable Email/Password authentication.
6. Enable Google authentication.
7. Create Firestore Database.
8. Add the `google-services.json` file inside the `app` folder.
9. Sync the project with Gradle.

Google Login will not work correctly if the SHA-1 fingerprint and Google Sign-In configuration are not added.

## How to Run the Project

1. Download or clone the project from GitHub.
2. Open the project in Android Studio.
3. Wait for Gradle sync to finish.
4. Connect an Android phone or start an Android emulator.
5. Make sure Firebase is configured.
6. Check that `google-services.json` is inside the `app` folder.
7. Click the Run button in Android Studio.
8. The application will start on the connected Android device.

## Input Validation

The app checks the user input before saving the data.

For example:

- Email should be in a valid format.
- Password should have the required length.
- Password confirmation should match.
- Weight should be a valid number.
- Height should be a valid number.
- Gender should be selected.
- Date of birth should be selected.

## Weight History

The application stores weight changes in Firestore.

The weight history screen shows the user's recent weight entries and a graph for the past 7 days.

The graph uses KG internally so that KG and LBS values can be compared correctly.

## Project Structure

The project mainly contains:

- Activities for different screens
- XML layouts for UI
- Firebase Authentication code
- Firestore database code
- A custom View for the weight graph
- Drawable and resource files

## Future Improvements

Some features that can be added in the future are:

- More health tracking features
- Better charts
- Notifications
- More detailed user profiles
- More health statistics

## Conclusion

This project helped me understand Android application development using Java and XML along with Firebase Authentication and Firestore.

I also learned about handling user authentication, storing user data, calculating BMI, input validation and displaying data using a graph.
