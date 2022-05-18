In order to benefit from most jMCS functionalities, your applications shall derive from the `App` class.

This class formalize applications life cycle, divided in 4 sequential steps : initialization, GUI setup, execution and quitting:
* Your `initServices()` method shall contain all your application initialization code, like specific command-line option parsing, core services initialization, ...
* Your `setupGui()` method shall contain all MVC objects allocation and initialization, GUI setup, ...
* Your `execute()` method shall in fact contain no code at all, as long as your application GUI fully implement the reflex approach (e.g !Observer/Observable and MVC patterns, ...) and has already been initialized in `setupGui()`, but is provided to handle specific action once application initialization is done (e.g network connection setup, automatic query launch, ...)
* Your `cleanup()` method shall contain all application end-of-life management (closing network connection, flushing caches, ...)

Once your App-derived class is instantiated at runtime in your `main()` method, jMCS `Bootstrapper.launch(YourApp)` will properly bring your application to life by :
   1. Initializing jMCS and App internal mechanisms ([ApplicationData.xml](Application Description) file loading and parsing, [logger](Logging Facilities) setup, command line option parsing, shared functionalities setup, ...);
   1. Displaying a splash screen (for at least 2.5 seconds), in order to let the end user know which application is about to start;
   1. Calling your `initServices()` method to start your application core codes;
   1. Calling your `setupGui()` method to properly create your application GUI in EDT while the splash screen is shown;
   1. Discarding the splash screen;
   1. Calling your `execute()` method at last, so you can set your GUI as the frontmost window for the end user to freely use it.

Here is a minimal jMCS-compliant application template :
```
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.Bootstrapper;

public class YourMainClass extends App {

    public YourMainClass(String[] args) {
        super(args); // MANDATORY App internal initialization
    }

    protected void initServices() {
        // Add your application initialization code here...
    }

    protected void setupGui() {
        // Add your application GUI creation here...
    }

    protected void execute() {
        // Add your application execution code here...
    }

    protected void cleanup() {
        // Add your application cleanup code here...
    }

    public static void main(String[] args) {
        Bootstrapper.launchApp(new YourMainClass(args));
    }
}
```