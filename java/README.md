# Signer samples for Java

This folder contains a **Java sample project** demonstrating the usage of [Signer](https://www.dropsigner.com/).

> **Note:** Gradle is the only supported build; there is no Maven build. See [console/README.md](console/README.md) for current instructions.

For other languages, please visit the [repository root](https://github.com/LacunaSoftware/SignerSamples).

## Console application

A Java 8 **console application** is in the folder [console](console/), with one scenario class
per integration task.

To run it with Gradle:

1. [Download the repository](https://github.com/LacunaSoftware/SignerSamples/archive/master.zip)
   or clone it.

1. In [`App.java`](console/src/main/java/com/lacunasoftware/signer/sample/App.java), set the
   scenario to run.

1. In a command prompt, go to the folder `console` and run `gradlew run` on Windows, or
   `./gradlew run` on Linux and macOS. The wrapper is Gradle 5.2.1, which needs JDK 8 to 11.

> On Linux and macOS, you may have to make the *gradlew* file executable first, with
> `chmod +x gradlew`.

Opening the sample in Eclipse or IntelliJ IDEA
----------------------------------------------

To open the sample in Eclipse, run `gradlew eclipse` in the `console` folder, then import the
project in Eclipse.

To open the sample in IntelliJ IDEA, run `gradlew idea` in the `console` folder, then use IDEA's
"Open" command (it works better than "Import").

Signer's client lib
---------------------

The sample uses a client lib which encapsulates the API calls to Signer. This library supports
**Java 8 or greater**. It is **referenced as a dependency** from Maven Central, as can be seen in
the file [build.gradle](console/build.gradle):

	repositories {
		mavenCentral()
	}

	dependencies {
		implementation 'com.lacunasoftware.signer:signer-client:2.13.0'
	}

**NOTE:**
>If you wish to send the requests using your own client instead of the client provided in this library, please make sure to adopt
>appropriate handling of null values and datetime serialization (see [RestClient](https://github.com/LacunaSoftware/SignerJavaClient/blob/8b7a536c6758967189d9293ef63a40ebce3ddae1/src/main/java/com/lacunasoftware/signer/javaclient/RestClient.java#L369-L412) for more information). You may accomplish this by using the methods `signerClient.getGson()`
>and `signerClient.getJackson()` to get pre-configured instances of [Gson](https://github.com/google/gson) or [Jackson](https://github.com/FasterXML/jackson) respectively.
