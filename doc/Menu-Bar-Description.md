jMCS provides an already populated menu by default, with the following standard functionalities :
* A File menu, with the mandatory Quit menu item that automatically call `App.canBeTerminatedNow()` method (if implemented), to ensure whether your application should exit or not.
* An Edit menu, with fully functional Cut, Copy and Paste items, plus Preferences (disabled if none provided).
* An Help menu, with:
   * A User Manual entry, if your application package contains the [needed documentation format](GUI Facilities#HelpWindow) (automatically disabled otherwise).
   * A way for the end user to provide [feedback to your company](Application Description#FeedbackReport) using a simple dedicated window.
   * A way for the end user to see execution logs in a dedicated window.
   * A way for the end user to put your application [acknowledgement notice](Application Description#AcknowledgementNote) in his clipboard for later pasting in his publication.
   * A way for the end user to get access to your application ['Hot News' RSS feed](Application Description#HotNews) through his computer default Web browser.
   * A way for the end user to get access to your application [release notes](Application Description#ReleaseNotes) using a simple dedicated window.
   * A way for the end user to acces your application [FAQ webpage](Application Description#FAQ) through his computer default Web browser.
   * A way for the end user to view [detailed information about your application](GUI Facilities#AboutBox) using a simple dedicated window.
   * A way for the end user to acknowledge your reliance on jMCS and its dependencies.

All those default menus can of course be extended with your own menus and menu items, using :
* `ApplicationData.xml` to describe your menus and menu items (simple entries, with checkboxes, sub-menus, separators, text description, linked [Action](http://java.sun.com/j2se/1.5.0/docs/api/javax/swing/Action.html)).
* `RegisteredAction` : your actions shall derive from this class in order to be semi-automatically linked to your menu items (using the same action class name and identifier as in `ApplicationData.xml`, thanks to `ActionRegistrar` - a singleton class dedicated to automatically keep track of all your application `RegisteredAction`).
One good practice is to declare `classPath` and `action` as `public static final String` members in your `RegisteredActions`.

Here is an example of how to add menu items insertion and creation in `ApplicationData.xml` :
```
<?xml version="1.0" encoding="UTF-8"?>
<ApplicationData link="http://www.myProgram.com/">
    ...
    <menubar>
        <menu label="File"> <!-- Extending the default File menu -->
            <menu label="Import" classpath="com.yourcompany.example.Actions" action="scaction1" icon="/fr/jmmc/test/import.png"/>
            <menu label="Export" classpath="com.yourcompany.example.Actions" action="scaction1"/>
        </menu>
        <menu label="Menu 1">
            <menu label="Item 1" classpath="com.yourcompany.example.Actions" action="mfaction1" accelerator="shift T" description="Raccourcis n-1 de Model fitting"/>
            <menu/> <!-- menu separator -->
            <menu label="Item 2" checkbox="true" classpath="com.yourcompany.example.Actions" action="mfaction5"/> <!-- checkbox item -->
        </menu>
        <menu label="Menu 2">
            <menu label="Item 1" classpath="com.yourcompany.example.Actions" action="scaction1"/> <!-- sub-menus -->
                <menu label="Item 1.1" classpath="com.yourcompany.example.Actions" action="scaction1"/>
                    <menu label="Item 1.1.1" classpath="com.yourcompany.example.Actions" action="scaction1"/>
                    <menu label="Item 1.1.2" classpath="com.yourcompany.example.Actions" action="scaction1" accelerator="I" checkbox="true"/> <!-- mixing styles -->
                </menu>
                <menu label="SearchCal 12" classpath="com.yourcompany.example.Actions" action="scaction1" accelerator="I"/>
            </menu>
        </menu>
    </menubar>
    ...
</ApplicationData>
```

Another benefit of using our menu bar infrastructure (apart from getting all the previously described functionalities for virtually 'free'), is to transparently handle different hosting platform specificities (e.g Mac OS X, Windows or Linux) in which standard menu bar hierarchy is different. This guarantees the best user experience for each platform user, so they don't have to re-learn application-vendor specific behaviour when they already know their default platform-specific application behavior and shortcuts well.

Note : you can test various "Look & Feel" by starting your application with the following property `-Djmcs.laf.menu=true`, to show a dedicated menu to switch between available LAFs.