/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: jmcsGenerateHelpsetFromHtml.java,v 1.1 2008-04-29 14:28:58 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import net.sourceforge.jhelpdev.JHelpDevFrame;
import net.sourceforge.jhelpdev.TOCEditorPanel;
import net.sourceforge.jhelpdev.action.CreateAllAction;
import net.sourceforge.jhelpdev.action.CreateMapAction;
import net.sourceforge.jhelpdev.action.OpenConfigAction;
import net.sourceforge.jhelpdev.settings.FileName;


/** Generates a HelpSet file of a HTML folder */
public class jmcsGenerateHelpsetFromHtml
{
    /**
     * Calls jhelpdev software on a HTML folder
     *
     * @param args arg[0] : XML project main file
     */
    public static void main(String[] args)
    {
        // Launch the jhelpdev application
        JHelpDevFrame.main(null);

        // Hide the jhelpdev frame
        JHelpDevFrame.getAJHelpDevToolFrame().setVisible(false);

        // Calls the jhelpdev action to open the project file
        System.out.println("jmcsGenerateHelpsetFromHtml : Opening " + args[0]);
        OpenConfigAction.doIt(new FileName(args[0]));

        // Calls the jhelpdev action to create map files
        System.out.println("jmcsGenerateHelpsetFromHtml : Creating Map...");
        CreateMapAction.doIt();

        // Calls the jhelpdev action to create toc files
        System.out.println(
            "jmcsGenerateHelpsetFromHtml : Creating TOC table...");
        TOCEditorPanel.getTOCTree()
                      .mergeTreeContents(CreateMapAction.getGeneratedRoot());

        // Calls the jhelpdev action to generates helpset (.hs) file
        System.out.println("jmcsGenerateHelpsetFromHtml : Creating HelpSet...");
        CreateAllAction.doIt();

        System.exit(0);
    }
}
/*___oOo___*/
