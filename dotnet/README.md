# Signer samples for .NET

This folder contains two **.NET projects** demonstrating the usage of
[Signer](https://www.dropsigner.com/): a console app with one scenario per integration task, and a
command-line tool that a coding agent calls to sign the user's pending documents.

For other languages, please visit the [repository root](https://github.com/LacunaSoftware/SignerSamples).

## Console application

A .NET Framework 4.8 **console application** is in the folder [console](console/), with one
scenario class per integration task. Its [README](console/README.md) lists the scenarios and
explains the configuration.

Steps to run a scenario:

1. [Download the repository](https://github.com/LacunaSoftware/SignerSamples/archive/master.zip)
   or clone it.

1. Open `console/Console.sln` in Visual Studio, with the .NET Framework 4.8 targeting pack
   installed.

1. Build the solution. Visual Studio restores the NuGet packages (`packages.config`) if automatic
   package restore is enabled; if it isn't, restore them manually.

1. In `console/Console/Program.cs`, set the scenario to run, then press F5.

Every run creates real documents on the configured Signer instance (the Signer sandbox, by
default), and Signer e-mails the sample participants.

## Skill-friendly signature app

A .NET 10 command-line app in [SkillFriendlySignatureApp](SkillFriendlySignatureApp/), for
Windows. It lists the documents pending signature for a participant on Signer (DropSigner by
default) and signs the ones the user confirms with a certificate from the Windows certificate
store, through PKI SDK. It prints JSON for a coding agent and ships a Claude Code skill that drives
it. Its [README](SkillFriendlySignatureApp/SkillFriendlySignatureApp/README.md) explains the
configuration, the commands and how to install the skill.
