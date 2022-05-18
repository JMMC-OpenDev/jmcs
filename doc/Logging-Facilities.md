jMCS logging architecture rely on [SLF4J](http://www.slf4j.org) and [Logback](http://logback.qos.ch), properly initialized at boostrap time.
You can dynamically change log outputs by using our dedicated panel, available from every jMCS-based application `Help` menu.

Your application can easily leverage those frameworks (see Logback dedicated [documentation](http://logback.qos.ch/manual/architecture.html) for more details).

Here is a minimal example :
```
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YourGreatClass {

    /** Here you create a dedicated logger for YourGreatClass */
    private static final Logger _logger = LoggerFactory.getLogger(YourGreatClass.class.getName());

    public YourGreatClass(String[] args) {
        // You send a debug message
        _logger.debug("Constructor called");
    }

    protected void doJob() {
        // You send an info message
        _logger.info("Job starting.");
        try {
            // ...
        } catch(Exception e) {
            // You send a warning message
            _logger.warn("Job failed");
            return;
        }
        _logger.info("Job done.");
    }

    public static void main(String[] args) {
        new YourGreatClass().doJob();
    }
}
```

You can customize your application logging configuration in a file named `LogbackConfiguration.xml`, in your `resource` directory.

Here is a minimal template :
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>

<!-- YourApp log configuration -->
<included>

    <!-- Define the minimum output level for all loggers in classes belonging to the 'com.yourcompany.yourapp' package -->
    <logger name="com.yourcompany.yourapp" level="INFO"/>

    <!-- Define a specific level that will only output WARN (or stronger) messages for YourGreatClass. -->
    <logger name="com.yourcompany.yourapp.yourgreatclass" level="WARN"/>

</included>
```