This folder contains **Java sample projects** demonstrating the usage of [Signer](https://www.dropsigner.com/).

> **Note:** Gradle is the only supported build; there is no Maven build. See [console/README.md](console/README.md) for current instructions.

For other languages, please visit the [repository root](https://github.com/LacunaSoftware/SignerSamples).

## Default sample (Console application)

A sample **console application** can be fond in the folder [console](console/).

You can use the following tool to run the application:

**Using Gradle**

1. [Download the project](https://github.com/LacunaSoftware/SignerSamples/archive/master.zip)
   or clone the repository
   
1. In a command prompt, navigate to the folder `console` and run the command
   `gradlew run` (on Linux `./gradlew run`).
   
> If you are on Linux, you may have to add the execution permission to *gradrew* file by executing
the command `chmod +x gradlew`.

Opening the samples on Eclipse or IDEA
--------------------------------------

To open one of the samples on Eclipse, run `gradlew eclipse` on the sample's folder and then
then import the project from Eclipse.

To open one of the samples on IntelliJ IDEA, run `gradlew idea` on the sample's folder
and then use the "Open" funcionality inside IDEA (works better than "Import").

Signer's client lib
---------------------

The samples use a client lib which encapsulates the API calls to Signer. This library
supports **Java 8 or greater**. The lib should be **referenced as a dependency**, as can
be seen in the file [build.gradle](console/build.gradle) of each sample:

	repositories {
		mavenCentral()
	}

	dependencies {
		compile("com.lacunasoftware.signer:signer-client:2.9.0")
	}

**NOTE:**
>If you wish to send the requests using your own client instead of the client provided in this library, please make sure to adopt
>appropriate handling of null values and datetime serialization (see [RestClient](https://github.com/LacunaSoftware/SignerJavaClient/blob/8b7a536c6758967189d9293ef63a40ebce3ddae1/src/main/java/com/lacunasoftware/signer/javaclient/RestClient.java#L369-L412) for more information). You may accomplish this by using the methods `signerClient.getGson()`
>and `signerClient.getJackson()` to get pre-configured instances of [Gson](https://github.com/google/gson) or [Jackson](https://github.com/FasterXML/jackson) respectively.
