jMCS    ![JMMC logo](doc/JMMC-logo.jpg)
====

Java framework from [JMMC](http://www.jmmc.fr), to homogenize your GUI across all the 3 main desktop OS, and further integrates your app to them.

Its primary goal is to centralize all GUI apps shared functionalities (e.g menubar handling, about box window, ...) in order to provide end users with a more consistent, feature-reach, desktop-class application family, as integrated as possible across Linux, Mac OS X and Windows, while freeing you developers of this tedious work !

The ultimate goal is to leverage your end users knowledge of their favorite platform, to let them fill right at home while using your applications, thus improving the perceived quality of your products while factorizing and sharing your development efforts.

Your app then feels better to the end user (by truly respecting its platform of choice), and we free you of all those nasty details !

Documentation
=============

The `javadoc` is included in our [releases](https://github.com/JMMC-OpenDev/jMCS_old/releases).

But let's get started by browsing next sections to further discover what jMCS provides:
* [Application Description](doc/Application-Description.md)
* [Application Startup](doc/Application-Startup.md)
* [GUI Facilities](doc/GUI-Facilities.md)
* [jMCS Developer Documentation](doc/jMCS-Developer-Documentation.md)
* [JMMC logo.jpg](doc/JMMC-logo.jpg)
* [Logging Facilities](doc/Logging-Facilities.md)
* [Menu Bar Description](doc/Menu-Bar-Description.md)
* [Networking Facilities](doc/Networking-Facilities.md)
* [others software](doc/others-software.md)
* [User Preferences Facilities](doc/User-Preferences-Facilities.md)
* [Utility Classes](doc/Utility-Classes.md)


License
=======

BSD 3-Clause : see [LICENSE.txt](../master/LICENSE.txt)

Goodies are also greatly appreciated if you feel like rewarding us for the job :)

Build
=====

jMCS uses `maven` to build from sources. Please type the following commands:

```
git clone https://github.com/JMMC-OpenDev/jmcs.git


# first time only: install parent-pom and missing libraries in maven repositories:
cd jMCS/parent-pom
mvn -Dassembly.skipAssembly -Djarsigner.skip=true clean install
cd ..
mvn process-resources

# build jMCS jar files
mvn clean install

# build testgui jar files (if needed)
cd testgui
mvn clean install

```

Jar files are then available in `target` directory !
You also get the option to use prepared jar under the 'Download' section of prepared [releases](releases).


Notes
=====
To skip tests, just run:
```
# build jMCS jar files without any test:
mvn clean install -DskipTests

# check updates on dependencies:
mvn versions:display-dependency-updates
```

Requirements:
- OpenJDK 8+
- Maven 3.6+

See [JMMC Java Build](https://github.com/JMMC-OpenDev/jmmc-java-build)
See [CI nightly builds](https://github.com/JMMC-OpenDev/jmmc-java-build/actions/workflows/build.yml)

