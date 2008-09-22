/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Actions.java,v 1.2 2008-09-22 16:53:50 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/07/01 08:58:13  lafrasse
 * Added jmcs test application from bcolucci.
 *
 ******************************************************************************/
package fr.jmmc.mcs.modjava;

import fr.jmmc.mcs.util.*;

import javax.swing.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
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
