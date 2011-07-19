/*   Swing Hacks
 *   Tips and Tools for Killer GUIs
 *   By Joshua Marinacci, Chris Adamson
 *   First Edition June 2005
 *   Series: Hacks
 *   ISBN: 0-596-00907-0
 *   http://www.oreilly.com/catalog/swinghks/
 */
package fr.jmmc.mcs.gui;

import java.awt.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class CheckBoxJList extends JList implements ListSelectionListener
{
    /**
     * DOCUMENT ME!
     */
    static Color listForeground;

    /**
     * DOCUMENT ME!
     */
    static Color listBackground;

    /**
     * DOCUMENT ME!
     */
    static Color listSelectionForeground;

    /**
     * DOCUMENT ME!
     */
    static Color listSelectionBackground;

    static
    {
        UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
        listForeground              = uid.getColor("List.foreground");
        listBackground              = uid.getColor("List.background");
        listSelectionForeground     = uid.getColor("List.selectionForeground");
        listSelectionBackground     = uid.getColor("List.selectionBackground");
    }

    /**
     * DOCUMENT ME!
     */
    HashSet selectionCache = new HashSet();

    /**
     * DOCUMENT ME!
     */
    int toggleIndex = -1;

    /**
     * DOCUMENT ME!
     */
    boolean toggleWasSelected;

    /**
     * Creates a new CheckBoxJList object.
     */
    public CheckBoxJList()
    {
        super();
        setCellRenderer(new CheckBoxListCellRenderer());
        addListSelectionListener(this);
    }

    // ListSelectionListener implementation
    /**
     * DOCUMENT ME!
     *
     * @param lse DOCUMENT ME!
     */
    public void valueChanged(ListSelectionEvent lse)
    {
        //System.out.println(lse);
        if (! lse.getValueIsAdjusting())
        {
            removeListSelectionListener(this);

            // remember everything selected as a result of this action
            HashSet newSelections = new HashSet();
            int     size          = getModel().getSize();

            for (int i = 0; i < size; i++)
            {
                if (getSelectionModel().isSelectedIndex(i))
                {
                    newSelections.add(new Integer(i));
                }
            }

            // GM Clear all because the cache contains more elements than its model
            if(selectionCache.size()>size){                
                selectionCache.clear();
                getSelectionModel().clearSelection();
            }
            
            // turn on everything that was previously selected
            Iterator it = selectionCache.iterator();

            while (it.hasNext())
            {
                int index = ((Integer) it.next()).intValue();
                //System.out.println("adding " + index);
                getSelectionModel().addSelectionInterval(index, index);
            }

            // add or remove the delta
            it = newSelections.iterator();

            while (it.hasNext())
            {
                Integer nextInt = (Integer) it.next();
                int     index   = nextInt.intValue();

                if (selectionCache.contains(nextInt))
                {
                    getSelectionModel().removeSelectionInterval(index, index);
                }
                else
                {
                    getSelectionModel().addSelectionInterval(index, index);
                }
            }

            // save selections for next time
            selectionCache.clear();

            for (int i = 0; i < size; i++)
            {
                if (getSelectionModel().isSelectedIndex(i))
                {
                    //System.out.println("caching " + i);
                    selectionCache.add(new Integer(i));
                }
            }

            addListSelectionListener(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args)
    {
        JList            list     = new CheckBoxJList();
        DefaultListModel defModel = new DefaultListModel();
        list.setModel(defModel);

        String[] listItems = 
            {
                "Chris", "Joshua", "Daniel", "Michael", "Don", "Kimi", "Kelly",
                "Keagan"
            };
        Iterator it = Arrays.asList(listItems).iterator();

        while (it.hasNext())
        {
            defModel.addElement(it.next());
        }

        // show list
        JScrollPane scroller = new JScrollPane(list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JFrame      frame    = new JFrame("Checkbox JList");
        frame.getContentPane().add(scroller);
        frame.pack();
        frame.setVisible(true);
    }

    class CheckBoxListCellRenderer extends JComponent
        implements ListCellRenderer
    {
        DefaultListCellRenderer defaultComp;
        JCheckBox               checkbox;

        public CheckBoxListCellRenderer()
        {
            setLayout(new BorderLayout());
            defaultComp     = new DefaultListCellRenderer();
            checkbox        = new JCheckBox();
            add(checkbox, BorderLayout.WEST);
            add(defaultComp, BorderLayout.CENTER);
        }

        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus)
        {
            defaultComp.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
            /*
               checkbox.setSelected (isSelected);
               checkbox.setForeground (isSelected ?
                                       listSelectionForeground :
                                       listForeground);
               checkbox.setBackground (isSelected ?
                                       listSelectionBackground :
                                       listBackground);
             */
            checkbox.setSelected(isSelected);

            Component[] comps = getComponents();

            for (int i = 0; i < comps.length; i++)
            {
                comps[i].setForeground(listForeground);
                comps[i].setBackground(listBackground);
            }

            return this;
        }
    }
}
