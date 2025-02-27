# WeatherAlarm

WeatherAlarm is an Android application that allows users to set alarms that use Text-to-Speech (TTS) to read out the weather conditions for the day. The app automatically detects the user's location and fetches weather data from OpenWeatherMap to provide a spoken weather forecast when the alarm goes off.

## Features
- Set alarms for different times of the day
- Alarms are listed and sorted by trigger time
- Text-to-Speech (TTS) reads the weather conditions aloud
- Provides weather details such as temperature, wind speed, and chance of rain
- Automatically retrieves user location
- Uses OpenWeatherMap API for weather data

## Setup Instructions
### Prerequisites
- Android Studio installed
- An OpenWeatherMap API key

### Installation
1. Clone this repository:
   ```sh
   git clone https://github.com/yourusername/weatheralarm.git
   cd weatheralarm
   ```
2. Open the project in Android Studio.
3. Add your OpenWeatherMap API key:
   - Navigate to the `local.properties` file in the project root (create it if it doesn’t exist).
   - Add the following line, replacing `YOUR_API_KEY` with your actual OpenWeatherMap API key:
     ```properties
     OPENWEATHER_API_KEY=YOUR_API_KEY
     ```
4. Sync the project with Gradle.
5. Build and run the app on an Android device or emulator.

## Usage Instructions
1. Launch the WeatherAlarm app.
2. Grant necessary permissions for location and notifications.
3. Tap the `Add Alarm` button to create a new alarm.
4. Set the desired time and enable the weather forecast option.
5. Save the alarm – it will appear in the list, sorted by time.
6. When the alarm triggers, the app will read the weather conditions aloud using TTS.

## Technologies Used
- Java
- Gradle
- OpenWeatherMap API
- Android.TTS (Text-to-Speech)

## License
This project is licensed under the MIT License.
