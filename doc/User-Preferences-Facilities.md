jMCS provides an elaborated "key-value" mechanism to manage application preferences. To take advantage of it, your preference data handling class shall derive from our `Preferences` class.

You will then get the following benefits:
* Automatic preference file loading at first use (stored in the end-user expected, host operating system specific, default location), by just providing the preference filename through `getPreferenceFilename()` (shall be in the reversed-URL form, like `"com.organisation_name.application_name"`);
* Automatic fallback to default values if no preference file found at launch time, by using the mandatory `setDefaultPreferences()` method to define each of your application default values and keys;
* Automatic preference file versioning handling, by using `getPreferencesVersionNumber()` : each time you change your preference file structure (preference keys, value units, ...), simply increment the returned integer value, and provide the corresponding conversion code in your `updatePreferencesVersion()` method to handle this automatically for the end-user (so the user doesn't loose any prior settings while upgrading your application regularly).
* Freely provided `saveToFile()` method, handling automatic serialization of your current preferenced values;

The following data types can be used as values : String, Boolean, Integer, Double, Color and List<String> objects. Each values shall be identified using an unique string key, in the form of `"abc.def.foo.bar"`. Values related to a same preference group (e.g `"abc.def.foo.LOW"`, `"abc.def.foo.MEDIUM"`, `"abc.def.foo.HIGH"`) can be ordered through a specified integer index if needed.

For convenience, we advice making your Preferences implementation a singleton, in order to easily access it from anywhere in your application code.

For example:
```
import fr.jmmc.jmcs.data.preference.Preferences;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import java.util.Arrays;

public class PrefTest extends Preferences {

    /** Singleton instance */
    private static PrefTest _instance = null;

    /** @return the singleton instance. */
    public static synchronized PrefTest getInstance() {
        // DO NOT MODIFY !!!
        if (_instance == null) {
            _instance = new PrefTest();
        }
        return _instance;
        // DO NOT MODIFY !!!
    }

    private PrefTest() {
        super();
    }

    @Override
    protected String getPreferenceFilename() {
        return "fr.jmmc.test.properties";
    }

    @Override
    protected int getPreferencesVersionNumber() {
        return 1;
    }

    @Override
    public void setDefaultPreferences() throws PreferencesException {
        setDefaultPreference("myBoolean", false);
        setDefaultPreference("myInteger", 0);
        setDefaultPreference("myDouble", 3.14159);
        setDefaultPreference("myString", "test");
        setDefaultPreference("myStrList", Arrays.asList(new String[]{"jmmc", "", "guillaume", "sylvain", "laurent"}));
    }

    public static void main(String[] args) {
        final Preferences prefs = PrefTest.getInstance();
        try {
            prefs.setPreference("myInteger", 13);
        } catch (PreferencesException ex) {
            System.out.println("ex = " + ex);
        }
        String currentPrefs = prefs.dumpCurrentProperties();
        System.out.println("Current Preferences Dump :\n" + currentPrefs);
    }
}
```