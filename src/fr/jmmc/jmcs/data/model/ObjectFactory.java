
package fr.jmmc.jmcs.data.model;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fr.jmmc.jmcs.data.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.jmmc.jmcs.data.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ApplicationData }
     * 
     */
    public ApplicationData createApplicationData() {
        return new ApplicationData();
    }

    /**
     * Create an instance of {@link Company }
     * 
     */
    public Company createCompany() {
        return new Company();
    }

    /**
     * Create an instance of {@link Program }
     * 
     */
    public Program createProgram() {
        return new Program();
    }

    /**
     * Create an instance of {@link Compilation }
     * 
     */
    public Compilation createCompilation() {
        return new Compilation();
    }

    /**
     * Create an instance of {@link Dependences }
     * 
     */
    public Dependences createDependences() {
        return new Dependences();
    }

    /**
     * Create an instance of {@link Menubar }
     * 
     */
    public Menubar createMenubar() {
        return new Menubar();
    }

    /**
     * Create an instance of {@link ReleaseNotes }
     * 
     */
    public ReleaseNotes createReleaseNotes() {
        return new ReleaseNotes();
    }

    /**
     * Create an instance of {@link Release }
     * 
     */
    public Release createRelease() {
        return new Release();
    }

    /**
     * Create an instance of {@link Menu }
     * 
     */
    public Menu createMenu() {
        return new Menu();
    }

    /**
     * Create an instance of {@link Change }
     * 
     */
    public Change createChange() {
        return new Change();
    }

    /**
     * Create an instance of {@link Prerelease }
     * 
     */
    public Prerelease createPrerelease() {
        return new Prerelease();
    }

    /**
     * Create an instance of {@link Package }
     * 
     */
    public Package createPackage() {
        return new Package();
    }

}
