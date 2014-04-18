# FuseIn 1.0

A cloud integration application written in Java.

## Dependencies

### JDK Version

In your terminal, run

```sh
java -version
```

If your JDK version is not at least 1.7, you need to upgrade it.  Instructions for doing so can be found [here](http://openjdk.java.net/install/).  Ensure that you have JDK 1.7 installed before continuing.

## Installation

### On Windows

Download gradle-1.11 from [here](http://www.gradle.org/downloads).

Unzip gradle to your desired directory (eg. C:\Progam Files).

Add the location of the gradle 'bin' folder to your path.  To do so, open system properties (using WinKey + Pause), select the 'Advanced' tab, and then the 'Environment variables' button.  Now, add the path to gradle (eg. C:\Program Files\PATH\TO\GRADLE) to the end of the 'Path' variable under System Properties.  Make sure to omit quotation marks and to separate path entries with a ";".

Make sure that JAVA_HOME exists in your user variables or system variables.  JAVA_HOME must be set to the location of your JDK.

Open a command prompt (type cmd in Start menu), and type
```sh
gradle -v
```
to verify the installation.

Now, navigate to the top level fusein directory.  In your command prompt, enter
```sh
gradle installApp
```

Navigate to the `\build\install\fusein\bin` directory, and run the .bat file:
```sh
fusein.bat
```

### On Linux 

Download gradle-1.11 from [here](http://www.gradle.org/downloads).

Unzip gradle to the desired directory.

Open your `.bashrc` file, or, if none exists, make a new one. Enter the following
```sh
export PATH=$PATH:PATH/TO/GRADLE/bin
```

In a new terminal, run the following:
```sh
gradle -v
```
You should now see the gradle version.  If not, troubleshooting information can be found [here](http://www.gradle.org/docs/current/userguide/troubleshooting.html).

Navigate to the FuseIn directory and enter
```sh
gradle installApp
```

Navigate to the install directory at `/build/install/fusein/bin`, and run the script there
```sh
./fusein
```

## Using Eclipse

Go to your FuseIn directory and run:
```sh
gradle elipse
```
This will generate the proper Eclipse project files.

In Eclipse, run `File -> Import`.

In the Import dialog box, select `General -> Existing Projects into Workspace`.

In the Browse window, select your FuseIn root directory.

Click `Finish`.

## Testing

Gradle automates all testing for FuseIn.  In your FuseIn directory run:
```sh
gradle test
```
All unit tests will be run using this command, with output in the terminal window.

Gradle will also generate an easy to read html report.  Navigate to the `/build/reports/test` directory and open the `index.html` file found there.

For running individual tests (and not all tests at once), you may use Eclipse.