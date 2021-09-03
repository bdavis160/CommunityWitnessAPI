# CommunityWitnessAPI
This repo contains a REST API for the Community Witness DB.  


# Running
To build and run the API without installing, run `./gradlew run` from the root directory of this repository.
To run after installation either use start the systemd service by running `systemctl start CommunityWitnessAPI` or run it from the command line
as `org-communitywitness-api`.
To run with a configuration file, specify that file as the first argument to the API, for example `org-communitywitness-api /path/to/my/config.properties`.


# Configuration
There are a variety of user changeable settings, which are all listed and thoroughly described in the default configuration file `CommunityWitness.properties`.
You should start with this configuration then modify it for your use case.


# Documentation
A document going over authentication/authorization and all the API end points can be found in the `docs` folder
in both PDF format for easy viewing and RTF format for future editing.
For documentation about the code itself you can run `./gradlew javadoc` and check the resulting javadocs in the build folders.


# Testing

## Unit tests
To run the unit tests run `./gradlew test` from the root directory of this repository.

## Stress tests
To run the stress tests, first install locust from https://locust.io/ 
then edit `stress-test/locustfile.py` and at the top of the file fill in `WITNESS_API_KEY` and `INVESTIGATOR_API_KEY`
with valid API keys for the witness and investigator user you want to use for stress testing on your system.
Finally run locust from the `stress-test` directory and navigate to its web ui and fill in the parameters it gives you.


# Installing
To build and install the API run `sudo ./gradlew installPackage` from the root directory of this repository.

## Customizing Installation
There following 3 properties control where the package is installed

Property | Default Value | Description 
-------- | ------------- | -----------
`installDir` | `/opt/CommunityWitnessAPI` | The location to store launcher scripts, the application jar, and library jars.
`serviceInstallDir` | `/usr/local/lib/systemd/system` | The location to store the systemd service file. This should be put somewhere systemd will look like /etc/systemd/system or the default value.
`configInstallDir` | `/etc/CommunityWitness` | The location to store the API's configuration file. This can be wherever you like to store configs.
`launcherInstallDir` | `/usr/local/bin` | The location to link the launcher script to. This should be somewhere in $PATH so that shells can find it.

To modify these properties either edit `gradle.properties` or pass them in via the command line flag `-P`, for example by doing `./gradlew -PinstallDir=/my/dir installPackage`.


# Importing `org.communitywitness.common` with gradle
To import the `org.communitywitness.common` as a dependency in another gradle project, you can use this https://github.com/alexvasilkov/GradleGitDependenciesPlugin/tree/master gradle plugin by modifying your gradle setup as follows.

1. Add the following to `settings.gradle`
```
plugins {
	id 'com.alexvasilkov.git-dependencies' version '2.0.4'
}
```

2. Add the following to your `build.gradle` file
```
git {
	implementation 'https://github.com/bdavis160/CommunityWitnessAPI.git', {
		name 'org-communitywitness-common'
		branch 'main'
		projectPath '/org-communitywitness-common'
	}
}
```

If you have any issues with that try looking through the readme for that gradle plugin.
	