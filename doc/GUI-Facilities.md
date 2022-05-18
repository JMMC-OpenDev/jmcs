jMCS provides lots of GUI pain savers, such as action management, various elaborated graphical components, utility classes and generic windows, described hereafter.

### <a name="AboutBox"></a> About Box : the window giving application detailled information to end user
An about box window is already provided and linked to the appropriate menu item.
It contains your logo, the application name and version number, plus a customizable description text, and a standard invite to use acknowledgement in end-user publications. This is followed by a list of your external dependencies (if provided in the [ApplicationData.xml](Application Description) file).

### Status Bar : a way to display ongoing process state to end user
This singleton can be used to _discreetly_ provide the end-user with ongoing process status feedback. Simply add an instance of this class (that extends JPanel) to your main window's bottom.

You can then easily set its message from anywhere in your application with `StatusBar.show("Insert your message here...");`. End-users can also review StatusBar messages history from the provided log window in the `Help` menu.

### Search Field : a textfield dedicated to searching tasks
This widget (based on [work](http://elliotth.blogspot.com/2004/09/cocoa-like-search-field-for-java.html) from [Elliott Hughes](mailto:enh@jessies.org)) can easily be used with an `ActionListener` to perform searches throughout your data or using webservices.

`SearchField` *SHOULD ALWAYS* be prefered to a standard one, as its shape is easily associated with searching capabilities by end-users.

### WindowUtils : a way to center windows on complex screen setup
You can use this static utility class to:
   * simply center a given Window on the *main screen* of an end-user complex screen setup (e.g multiple monitor system);
   * install standard keyboard shortcuts to close a JDialog, JFrame and Window;
   * remember window sizes.

### <a name="HelpWindow"></a> Help Window : how to harness the shared HTML-based help browser
   1. Add the !HelpSet JAR file generation from a documentation module reference in the Makefile (man target) by invocking `jmcsHTML2HelpSet JMMC-MAN-2600-0001` for example;
   1. Check that this JAR is also installed by the Makefile;
   1. Check that this JAR is also referenced in the JNLP (if any).

Please note that their was some [known bug](http://forums.sun.com/thread.jspa?messageID=10522645) preventing the load of !HelpSets embedded in JAR file launched from JNLP under JVM 1.5.0_16, that was worked around using a dedicated method found in `HelpView.java`

### File Choosers : how to restrict available file types based on file extensions
jMCS offers a shared `FileFilterRepository` class to achieve this. This singleton registers file extensions with their corresponding MIME types, so you can later retrieve them to pass the corresponding [FileFilter](http://java.sun.com/javase/6/docs/api/javax/swing/filechooser/FileFilter.html) instance to your [JFileChooser](http://java.sun.com/javase/6/docs/api/javax/swing/JFileChooser.html).
Here is the code you should add to use it:
```
...
    protected class OpenFileAction extends RegisteredAction
    {
        /** Class Path. This name is used to register to the ActionRegistrar */
        public final static String _classPath="fr.jmmc.package.OpenFileAction";
        /** Action name. This name is used to register to the ActionRegistrar */
        public final static String _actionName="openFile";

        /** Class logger */
        static java.util.logging.Logger _logger = java.util.logging.Logger.getLogger(classPath);

        FileFilterRepository _fileFilterRepository = FileFilterRepository.getInstance();
        String               _scvotMimeType        = "application/x-searchcal+votable+xml";

        public OpenFileAction()
        {
            super(_classPath,_actionName);
            ...

            _fileFilterRepository.put(_scvotMimeType, "scvot", "SearchCal VOTables (SCVOT)");
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            // JFileChooser only allowing selection of '.scvot' files
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(_fileFilterRepository.get(_scvotMimeType));
            ...
        }
    }
...
```