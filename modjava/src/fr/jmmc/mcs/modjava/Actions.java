/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Actions.java,v 1.1 2008-07-01 08:58:13 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.modjava;

import fr.jmmc.mcs.gui.*;

import java.awt.event.ActionEvent;

import javax.swing.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Actions
{
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Action scaction1()
    {
        return new AbstractAction("scaction1")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    System.out.println("scaction1");
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Action scaction2()
    {
        return new AbstractAction("scaction2")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    System.out.println("scaction2");
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Action scaction3()
    {
        return new AbstractAction("scaction3")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    System.out.println("scaction3");
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Action scaction5()
    {
        return new AbstractAction("scaction5")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    System.out.println("scaction5");
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Action mfaction3()
    {
        return new AbstractAction("mfaction3")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    System.out.println("mfaction3");
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Action mfaction4()
    {
        return new AbstractAction("mfaction4")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    System.out.println("mfaction4");
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Action mfaction5()
    {
        return new AbstractAction("mfaction5")
            {
                public void actionPerformed(ActionEvent evt)
                {
                    System.out.println("mfaction5");
                }
            };
    }
}
/*___oOo___*/
