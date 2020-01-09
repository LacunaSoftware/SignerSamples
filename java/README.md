This folder contains **Java sample projects** demonstrating the usage of [Signer](https://www.dropsigner.com/).

For other languages, please visit the [repository root](https://github.com/LacunaSoftware/SignerSamples).

## Default sample (Console application)

A sample **console application** can be fond in the folder [console](console/).

You can user the following tools to run the application:

**Using Gradle**

1. [Download the project](https://github.com/LacunaSoftware/SignerSamples/archive/master.zip)
   or clone the repository
   
1. In a command prompt, navigate to the folder `console` and run the command
   `gradlew run` (on Linux `./gradlew run`).
   
> If you are on Linux, you may have to add the execution permission to *gradrew* file by executing
the command `chmod +x gradlew`.

**Using Maven**

1. [Download the project](https://github.com/LacunaSoftware/SignerSamples/archive/master.zip)
   or clone the repository

1. In a command prompt, navigate to the folder `console` and run the command
   `mvn exec:java`. To run this command, it's necessary to have the Apache Maven installed.


Opening the samples on Eclipse or IDEA
--------------------------------------

To open one of the samples on Eclipse, run `gradlew eclipse` on the sample's folder and then
then import the project from Eclipse.

To open one of the samples on IntelliJ IDEA, run `gradlew idea` on the sample's folder
and then use the "Open" funcionality inside IDEA (works better than "Import").