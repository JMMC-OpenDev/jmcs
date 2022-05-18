jMCS provides networking facilities, such as HTTP client or [SAMP](http://www.ivoa.net/documents/SAMP/) machinery.

### HTTP
This singleton can be used to communicate with any HTTP server. It provides:
   * a way to download data over the web;
   * a way to post data over the web;
   * support for PROXY-based connection;
   * handle multi-threaded work.

### SAMP
SAMP (Simple Application Messaging Protocol) enables software tools to interoperate and exchange data easily. jMCS integrate this technology by default.

To make use of it:
   * In your application main class, you can declare which SAMP message(s) you want to handle by overriding the `declareInteroperability()` method in your App-derived main class. The `SampManager` singleton then automatically calls your code back when such a message is received:
```
// Create SAMP Message handlers
@Override
protected void declareInteroperability() {
    // Add handler to load one new oifits
    new SampMessageHandler(SampCapability.LOAD_FITS_TABLE) {
        @Override
        protected void processMessage(final String senderId, final Message message) throws SampException {
                try {
                    final String url = (String) message.getParam("url");
                    // TODO : open the given URL here...
                } catch (IOException ex) {
                    MessagePane.showErrorMessage("Could not load file from SAMP message : " + message, ex);
                }
            });
        }
    };
}
```

   * While setting up your GUI (`App::setugGui()`), you can add an item in the `Interop` menu to send a SAMP message by creating a `SampCapabilityAction` derived action:
```
// Called to export current data as local file URI to another SAMP application
protected final class SendFileThroughSAMPAction extends SampCapabilityAction {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;

    SendFileThroughSAMPAction(final String classPath, final String fieldName, final SampCapability capability {
        super(classPath, fieldName, capability);
    }

    @Override
    public Map<?, ?> composeMessage() {
        File file = null;
        try {
            file = File.createTempFile("example", "txt");
        } catch (IOException ioe) {
            _logger.warn("Could not save calibrator list to temp file '{}'.", file);
            return null;
        }

        file.deleteOnExit();
        URI uri = file.toURI();

        // TODO : Save your data to the temp file here...

        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("url", uri.toString());
        return parameters;
    }
}
```