![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/logo192.png)
# InstaNews
Daily News App!

# Screenshots
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s2.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s6.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s7.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s7.5.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s7.6.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s8.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s9.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s10.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s11.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s12.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s13.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s14.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s15.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s16.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s17.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s18.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s19.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s20.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s21.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s22.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s23.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s24.jpg)
![alt text](https://github.com/Singularity-Coder/InstaNews/blob/master/screenshots/s25.jpg)

## Tech stack & Open-source libraries
- Minimum SDK level 21
- [Java](https://www.java.com/en/) based, [RxAndroid](https://github.com/ReactiveX/RxAndroid) + [LiveData](https://developer.android.com/topic/libraries/architecture/livedatahttps://developer.android.com/topic/libraries/architecture/livedata) for asynchronous.
- Jetpack
  - Lifecycle: Observe Android lifecycles and handle UI states upon the lifecycle changes.
  - ViewModel: Manages UI-related data holder and lifecycle aware. Allows data to survive configuration changes such as screen rotations.
  - DataBinding: Binds UI components in your layouts to data sources in your app using a declarative format rather than programmatically.
  - Room: Constructs Database by providing an abstraction layer over SQLite to allow fluent database access.
- Architecture
  - MVVM Architecture (View - DataBinding - ViewModel - Model)
  - Repository Pattern
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit): Construct the REST APIs and paging network data.
- [gson](https://github.com/google/gson): A Java serialization/deserialization library to convert Java Objects into JSON and back.
- [Material-Components](https://github.com/material-components/material-components-android): Material design components for building ripple animation, and CardView.
- [Glide](https://github.com/bumptech/glide): Loading images from the network.

## Architecture
![alt text](https://github.com/Singularity-Coder/FOLK-Database/blob/master/assets/arch.png)

This App is based on the MVVM architecture and the Repository pattern, which follows the [Google's official architecture guidance](https://developer.android.com/topic/architecture).

The overall architecture of this App is composed of two layers; the UI layer and the data layer. Each layer has dedicated components and they have each different responsibilities.

### Upcoming Features
1. FCM - Remainders (Time for News)
2. Periodic News refreshes using Work Manager
3. Dagger
4. Unit and Espresso Tests
5. Animations
6. Bottom Sheets
7. Webscraping and WebView enhancements - Customise fonts, clean reader view, navigaions, text size
8. Long Press news card to preview like instagram
9. Gestures
10. Foreground Service to detect news from SMS
