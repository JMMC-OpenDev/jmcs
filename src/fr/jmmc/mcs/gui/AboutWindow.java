/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: AboutWindow.java,v 1.1 2006-11-18 22:51:33 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package jmmc.mcs.gui;

import jmmc.mcs.log.MCSLogger;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;


/**
 * About window.
 */
public class AboutWindow extends JFrame
{
    /**
     * Constructor.
     */
    public AboutWindow(String name, String version, String greetings,
        String copyright)
    {
        try
        {
            setTitle("About " + name + "...");

            // Window size
            setSize(480, 360);
            setResizable(false);

            // Window screen position (centered)
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize  = getSize();
            setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel versionLabel = new JLabel("Version : " + version);
            panel.add(versionLabel);

            JTextArea greetingsTextArea = new JTextArea(greetings, 3, 40);
            greetingsTextArea.setEditable(false);
            panel.add(greetingsTextArea);

            JLabel copyrightLabel = new JLabel(copyright);
            panel.add(copyrightLabel);

            Container contentPane = getContentPane();
            contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
            contentPane.add(panel);

            // Set the GUI up
            pack();
            setVisible(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
/*___oOo___*/
