This folder contains **PHP sample projects** demonstrating the usage of [Signer](https://www.dropsigner.com/).

For other languages, please visit the [repository root](https://github.com/LacunaSoftware/SignerSamples).

## Default sample

A sample **console application** can be fond in the in the index.php file

You can user the following tool to run the application:

**Using [Composer](http://getcomposer.org)**

1. [Download the project](https://github.com/LacunaSoftware/SignerSamples/archive/master.zip)
   or clone the repository

1. In a command prompt, navigate to the folder `console` and run the command
   `composer install`.
   

Opening the samples
----------------------

The samples are located in the folder Scenarios, and you can test each one by calling them in the index.php file.

Running samples
----------------------

Use the command prompt and run the command bellow to execute the example:

php index.php


Signer's client lib
---------------------

The recommended way to install **Signer client lib** is through [Composer](http://getcomposer.org):

    "require": {
        "lacuna/signer-client" : "1.2.1"
    }

This library depends on the GuzzleHttp package, which in turn requires PHP 5.5 or greater.

