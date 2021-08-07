# CommunityWitnessAPI
This repo contains a REST API for the Community Witness DB.  


# Running
To build and run the API without installing, run `gradle run` from the root directory of this repository.


# Testing
To run the unit tests run `gradle test` from the root directory of this repository.


# Installing
To build and install the API run `sudo gradle installPackage` from the root directory of this repository.

## Customizing Installation
There following 3 properties control where the package is installed

Property | Default Value | Description 
-------- | ------------- | -----------
`installDir` | `/opt/CommunityWitnessAPI` | The location to store launcher scripts, the application jar, and library jars.
`serviceInstallDir` | `/usr/local/lib/systemd/system/` | The location to store the systemd service file. This should be put somewhere systemd will look like /etc/systemd/system or the default value.
`launcherInstallDir` | `/usr/local/bin` | The location to link the launcher script to. This should be somewhere in $PATH so that shells can find it.

To modify these properties either edit `gradle.properties` or pass them in via the command line flag `-P`, for example by doing `gradle -PinstallDir=/my/dir installPackage`.


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
	