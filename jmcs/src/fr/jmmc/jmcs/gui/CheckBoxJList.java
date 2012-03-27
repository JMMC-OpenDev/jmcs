/*   Swing Hacks
 *   Tips and Tools for Killer GUIs
 *   By Joshua Marinacci, Chris Adamson
 *   First Edition June 2005
 *   Series: Hacks
 *   ISBN: 0-596-00907-0
 *   http://www.oreilly.com/catalog/swinghks/
 */
package fr.jmmc.jmcs.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Custom JList implementation providing multiple selection with checkboxes
 * 
 * @author Guillaume MELLA.
 */
public class CheckBoxJList extends JList implements ListSelectionListener {

    /** list foreground color */
    static Color listForeground;
    /** list background color */
    static Color listBackground;
    /** list selection color */
    static Color listSelectionForeground;
    /** list selection background color */
    static Color listSelectionBackground;

    static {
        UIDefaults uid = UIManager.getLookAndFeel().getDefaults();
        listForeground = uid.getColor("List.foreground");
        listBackground = uid.getColor("List.background");
        listSelectionForeground = uid.getColor("List.selectionForeground");
        listSelectionBackground = uid.getColor("List.selectionBackground");
    }
    private static final long serialVersionUID = 1L;
    /**
     * selection cache
     */
    private Set selectionCache = new HashSet();

    /**
     * Creates a new CheckBoxJList object.
     */
    public CheckBoxJList() {
        super();
        setCellRenderer(new CheckBoxListCellRenderer());
        addListSelectionListener(this);
    }

    /**
     * ListSelectionListener implementation
     *
     * @param lse ListSelectionEvent
     */
    @Override
    public void valueChanged(final ListSelectionEvent lse) {
        //System.out.println(lse);
        if (!lse.getValueIsAdjusting()) {
            removeListSelectionListener(this);

            // remember everything selected as a result of this action
            HashSet newSelections = new HashSet();
            int size = getModel().getSize();

            for (int i = 0; i < size; i++) {
                if (getSelectionModel().isSelectedIndex(i)) {
                    newSelections.add(Integer.valueOf(i));
                }
            }

            // GM Clear all because the cache contains more elements than its model
            if (selectionCache.size() > size) {
                selectionCache.clear();
                getSelectionModel().clearSelection();
            }

            // turn on everything that was previously selected
            Iterator it = selectionCache.iterator();

            while (it.hasNext()) {
                int index = ((Integer) it.next()).intValue();
                //System.out.println("adding " + index);
                getSelectionModel().addSelectionInterval(index, index);
            }

            // add or remove the delta
            it = newSelections.iterator();

            while (it.hasNext()) {
                Integer nextInt = (Integer) it.next();
                int index = nextInt.intValue();

                if (selectionCache.contains(nextInt)) {
                    getSelectionModel().removeSelectionInterval(index, index);
                } else {
                    getSelectionModel().addSelectionInterval(index, index);
                }
            }

            // save selections for next time
            selectionCache.clear();

            for (int i = 0; i < size; i++) {
                if (getSelectionModel().isSelectedIndex(i)) {
                    //System.out.println("caching " + i);
                    selectionCache.add(Integer.valueOf(i));
                }
            }

            addListSelectionListener(this);
        }
    }

    /**
     * Test code
     *
     * @param args unused args
     */
    public static void main(String[] args) {
        JList list = new CheckBoxJList();
        DefaultListModel defModel = new DefaultListModel();
        list.setModel(defModel);

        String[] listItems = {
            "Chris", "Joshua", "Daniel", "Michael", "Don", "Kimi", "Kelly",
            "Keagan"
        };
        Iterator it = Arrays.asList(listItems).iterator();

        while (it.hasNext()) {
            defModel.addElement(it.next());
        }

        // show list
        JScrollPane scroller = new JScrollPane(list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JFrame frame = new JFrame("Checkbox JList");
        frame.getContentPane().add(scroller);
        frame.pack();
        frame.setVisible(true);
    }

    static class CheckBoxListCellRenderer extends JComponent
            implements ListCellRenderer {

        private static final long serialVersionUID = 1L;
        DefaultListCellRenderer defaultComp;
        JCheckBox checkbox;

        public CheckBoxListCellRenderer() {
            setLayout(new BorderLayout());
            defaultComp = new DefaultListCellRenderer();
            checkbox = new JCheckBox();
            add(checkbox, BorderLayout.WEST);
            add(defaultComp, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            defaultComp.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            checkbox.setSelected(isSelected);

            Component[] comps = getComponents();

            for (int i = 0; i < comps.length; i++) {
                comps[i].setForeground(listForeground);
                comps[i].setBackground(listBackground);
            }

            return this;
        }
    }
}
