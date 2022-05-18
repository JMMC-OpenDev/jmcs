jMCS provides several services and utilities freely available to you, detailed below.

### BrowserLauncher
This singleton can be used to open a URL in the user's default web browser.

### JnlpStarter
This singleton can be used to start a Java !WebStart application at a given URL.

### RecentFilesManager
This singleton can be used to manage recently opened files. Any added file is automatically displayed (and remembered) in the corresponding 'File' menu.

### XslTransform
This singleton can be used to apply an XSLT script (stored in a Jar) to an XML document (stored in a String).

### JAXB
[JAXB](https://jaxb.java.net) let developers map Java classes to XML representations. jMCS provide a wrapper around it to simplify its usage:
```
// Start JAXB with package path
final JAXBFactory jaxbFactory = JAXBFactory.getInstance("com.example.file.model");

// Try to load file.xml resource
final URL fileURL = ResourceUtils.getResource("com/example/data/file.xml");
YourDataModelClass yourDataModelObject;
try {
    yourDataModelObject = (YourDataModelClass) JAXBUtils.loadObject(fileURL, jaxbFactory);
} catch (IOException ioe) {
    throw new IllegalStateException("Load failure on " + fileURL, ioe);
}

// TODO : manipulate your data model here...

// Serialize data model to XML
final StringWriter stringWriter = new StringWriter(4096); // 4K buffer
JAXBUtils.saveObject(stringWriter, yourDataModelObject, jaxbFactory);
```

### CollectionUtils
This static class provides various facilities to deal with Java collections, such as:
   * human readable `toString()` implementations;
   * `null`-aware `isEmpty()` checkers.

### CommandLineUtils
This static class provides methods to launch a command-line path application in background.

### FileUtils
This static class provides numerous methods to efficiently handle files:
   * manipulate pathes/extensions;
   * create/read/write/copy/zip/checksum files.

### ImageUtils
This static class provides helpers to load `ImageIcon` from Jars and scale them.

### IntrospectionUtils
This static class provides numerous helpers to handle most common task with introspection load `ImageIcon` from Jars and scale them.

### MCSExceptionHandler
This class provides Java Thread uncaught exception handlers

### NumberUtils
This class mainly handles `double` number comparisons with absolute error.

### ResourceUtils
This static class facilitates the work to get resources files from inside Jars.

### StringUtils
This static class facilitate provides numerous helpers to deal with `String`, such as trimming, accent, …

### UrlUtils
This static class deals with URL validation, encoding, and so on.

### Concurrency, Runner and Timer
TBD