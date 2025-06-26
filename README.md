# KNZWapp

An Android mobile app designed for travellers to Kanazawa, Japan. Users can record travel notes with photos and location, search for nearby spots, view the weather, and log in via fingerprint or username password.

## APIs
Firebase API - Firebase Authentication and Realtime database
Google API - Places SDK for Android and Places API
OpenWeatherMap API

## Sensors
GPS / Location
Camera
Biometric Authentication

## API key
API keys are stored using `gradle.properties` and injected via `manifestPlaceholders`.
`google-services.json` and `gradle.properties` are excluded from version control via `.gitignore`.

In gradle.properties, set your API keys:
MAPS_API_KEY=YOUR_MAPS_API_KEY
PLACES_API_KEY=YOUR_PLACES_API_KEY
WEATHER_API_KEY=YOUR_WEATHER_API_KEY

## Features
Login via Firebase Auth and BiometricPrompt
Capture travel memories with image and GPS
Find nearby spots like food and bus stations using Google Places API
Show some tourist spots and weather in Kanazawa
Save and sync travel notes via Firebase Realtime Database
