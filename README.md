Android MedNote ReadMe
=============================


## Building the MedNote App

First, clone the repo:

`git clone https://github.com/MedNote/csds393_health_app.git`

### Android Studio (Recommended)

(These instructions were tested with Android Studio version 20.3.1 and Later)

* Open Android Studio and select `File->Open...` or from the Android Launcher select `Import project (Eclipse ADT, Gradle, etc.)` and navigate to the root directory of your project.
* Select the directory or drill in and select the file `build.gradle` in the cloned repo.
* Click 'OK' to open the project in Android Studio.
* A Gradle sync should start, but you can force a sync and build the 'app' module as needed.

### Gradle (command line)

* Build the APK: `./gradlew build`

### Eclipse

* Download the latest Android SDK from [Maven Central](http://repo1.maven.org/maven2/io/keen/keen-client-api-android)
  * Note: We publish both an AAR and a JAR; you may use whichever is more convenient based on your infrastructure and needs.


## Running the MedNote App

Connect an Android device to your development machine.

### Android Studio

* Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar
* Select the device you wish to run the app on and click 'OK'

### Gradle

* Install the debug APK on your device `./gradlew installDebug`


## Using the MedNote App

At the Introduction Screen, choose if you want to sign up or log in. If you have created an account previously, just log in with the credentials from before. If it is your first time with the app, continue to signup. Once signed up, proceed with all the necessary information and input all the textboxes to proceed to the next screen. Once logged in, you should see you profile and personal infromation such as Date of Birth. You will also see an option to add note or view note accordingly. If you are a doctor, you will see an option to add note to a patient. If you are a patient, you will see an option to view note from a doctor. Now, you can use these options to communicate with eachother effectively and safely.
