jMCS is a collection of APIs and JAR files shared by all JMMC Java-based GUI applications, and also [[others software]].

The general philosophy is :

* your main application class derives from our `App` class, and must be started using `Bootstrapper.launch(YourApp);` in your `static void main(String[] args)` method;
* as much as possible app details are described in a central XML file named `ApplicationData.xml`;
* a large collection of utility singletons and GUI components are available to you;

You can have further details on each main jMCS topic by browsing the following pages :

1. [[Application Startup]]
1. [[Application Description]]
1. [[Menu Bar Description]]
1. [[User Preferences Facilities]]
1. [[GUI Facilities]]
1. [[Logging Facilities]]
1. [[Networking Facilities]]
1. [[Utility Classes]]