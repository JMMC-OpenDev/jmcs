/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.modjava;

import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.RegisteredAction;



/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class Actions
{
    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction scaction1;

    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction scaction2;

    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction scaction3;

    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction scaction5;

    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction mfaction3;

    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction mfaction4;

    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction mfaction5;

    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction radio1;

    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction radio2;

    /**
     * DOCUMENT ME!
     */
    public GenericLoggedAction radio3;

    /**
     * Creates a new Actions object.
     */
    public Actions()
    {
        System.out.println("" + ActionRegistrar.getInstance());

        scaction1     = new GenericLoggedAction("scaction1");
        scaction2     = new GenericLoggedAction("scaction2");
        scaction3     = new GenericLoggedAction("scaction3");
        scaction5     = new GenericLoggedAction("scaction5");

        mfaction3     = new GenericLoggedAction("mfaction3");
        mfaction4     = new GenericLoggedAction("mfaction4");
        mfaction5     = new GenericLoggedAction("mfaction5");

        radio1        = new GenericLoggedAction("radio1");
        radio2        = new GenericLoggedAction("radio2");
        radio3        = new GenericLoggedAction("radio3");

        System.out.println("" + ActionRegistrar.getInstance());
    }

    protected class GenericLoggedAction extends RegisteredAction
    {
        String _fieldName = null;

        public GenericLoggedAction(String fieldName)
        {
            super("fr.jmmc.mcs.modjava.Actions", fieldName);

            System.out.println("GenericLoggedAction('" + fieldName + "').");

            _fieldName = fieldName;
        }

        public void actionPerformed(java.awt.event.ActionEvent e)
        {
            System.out.println("GenericLoggedAction.actionPerformed('" +
                _fieldName + "').");
        }
    }
}
/*___oOo___*/
