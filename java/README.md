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

Signer's client lib
---------------------

The samples use a client lib which encapsulates the API calls to Signer. This library
supports **Java 8 or greater**. The lib should be **referenced as a dependency**, as can
be seen in the file [build.gradle](console/build.gradle) of each sample:

	repositories {
		mavenCentral()
		maven {
			url  "http://dl.bintray.com/lacunasoftware/maven" 
		}
	} 

	dependencies {
		compile("com.lacunasoftware.signer:signer-client:1.0.1")
	}

If you project uses Maven, please refer to the file [pom.xml](console/pom.xml) instead:

	<dependencies>
		...
		<dependency>
			<groupId>com.lacunasoftware.signer</groupId>
			<artifactId>signer-client</artifactId>
			<version>1.0.1</version>
		</dependency>
		...
	</dependencies>
	...
	<repositories>
		<repository>
			<id>lacuna.repository</id>
			<name>lacuna repository</name>
			<url>http://dl.bintray.com/lacunasoftware/maven</url>
		</repository>
	</repositories>
	
**NOTE:**
> If you are willing to not use `GET` and ` POST` requests available in Lacuna's Signer libraries and decided to use Jackson library for data serialization:
> be sure that your request is not sending our models' parameters that were initialized with `null` values following the example bellow.
```java
 	ObjectMapper objectMapper = new ObjectMapper();
	//Responsible for generating Json strings without null parameters presented in Signer models
	objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); 
	String Json = objectMapper.writeValueAsString(request);
```

If your project uses another tool for dependency resolution (e.g. Ivy), please visit the
[package page on BinTray](https://bintray.com/lacunasoftware/maven/signer-client) and click on
the link "SET ME UP!".
