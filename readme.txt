=======================================
FuseIn 1.1
=======================================

A cloud services integration application written in Java.

---------------------------------------
Dependencies
---------------------------------------

JDK Version

In your terminal, run

	java -version

If your JDK version is not at least 1.7, you need to upgrade it.  
Instructions for doing so can be found at http://openjdk.java.net/install/.
Ensure that you have JDK 1.7 installed before continuing.

----------------------------------------
Testing Information
----------------------------------------
To keep your personal accounts and files safe, we have provided two
test accounts to use.  These accounts will allow you to try each
function with a real account.
Dropbox

Email: testfusein@gmail.com
Password: password.123

Google Drive

Email: testfusein@gmail.com
Password: password.123

----------------------------------------
Installation On Windows
----------------------------------------

!!! UPDATE !!!
There is now GUI support for both Dropbox and Google Drive.

Download gradle-1.11 from http://www.gradle.org/downloads.

Unzip gradle to your desired directory (eg. C:\Program Files).

Add the location of the gradle 'bin' folder to your path.  
To do so, open system properties (using WinKey + Pause). Select the 
"Advanced System Settings" opetion, and then the "Environment variables" button.  
Now, add the path to gradle bin (eg. C:\Program Files\gradle-x.xx\bin) 
to the end of the 'Path' variable under System Properties.  
Make sure to omit quotation marks and to separate path entries with a ";".

Make sure that JAVA_HOME exists in your user variables or system variables.  
JAVA_HOME must be set to the location of your JDK.  If JAVA_HOME does not
exist, click "New" unser System Variables and add
Name: JAVA_HOME
Value: PATH\TO\JDK (eg. C:\Program Files (x86)\java\jdk1.7.0_51)

Now, open a command prompt (type cmd in Start menu), and type

	gradle -v

to verify the installation.

Unzip the FuseIn archive to you desired directory (eg. C:\My Documents).

Now, navigate to the top level fusein directory.  In your command prompt, enter

	gradle installApp

Navigate to the `\build\install\fusein\bin` directory, and run the .bat file:

	fusein.bat

----------------------------------------
KNOWN ISSUES
----------------------------------------

The subfolders of a remote drive may fail to show up in the folder tree.
If this occurs, it is because the tree they are on is collapsed. Double-click
on "someone's Dropbox" to expand the tree.

If a service fails to authenticate (ie. network interruption), these services
will be lost if the configuration properties file is overwritten.

A service with a null authentication token may be added if the authentication
step initially fails.  This results in undefined behaviour until the 
configuration properties file is manually changed.
