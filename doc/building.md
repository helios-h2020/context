# Building and installing the HELIOS ContextExample1 application #

This application  utilizes the HELIOS context module. It implements two example location-based contexts: "at home" and "at work". It can be built using Android Studio (3.5.x).

## Cloning repositories ##

The core context module is included into the repository context.git.  See the documention on the testclient repository on how to clone HELIOS repositories.


## Building the application using Android Studio ##

Steps to build the ContextExample1 application:

  * Start Android Studio 3.5.x IDE
  * Select an option "Open an existing Android Studio project
  * Navigate and select cloned context directory
  * Clean the project. Select from menu: *Build=>Clean project*
  * Build the project. Select from menu: *Build=>Rebuild project*
  * Test using emulator. Select from menu: *Run=>Run 'app'*

The context example1 application requires also the profile module.

## Installing the application to a phone ##

Connect your phone to your computer with the USB cable and allow file transfer in your phone. Go to "context" directory and give a command:

    $ adb install -r -t ./app/build/outputs/apk/debug/app-debug.apk

The option '-r' should be omitted when installing the first
time. After successful install there should be new application "HELIOS LocationContext example1".

## Using the application ##

When the application starts the user can select "Start context updates" to see updates to status of the contexts "at home" and "at work" and the current position coordinates. 