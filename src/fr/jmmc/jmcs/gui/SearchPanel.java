/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.gui;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Search Pane
 * @author Slvain LAFRASSE
 */
public class SearchPanel extends JFrame {

    JTextField _searchField;
    JCheckBox _regexpCheckBox;

    public SearchPanel() {
        super("Find");

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel findLabel = new JLabel("Find:");
        panel.add(findLabel);

        _searchField = new JTextField();
        panel.add(_searchField);
        _searchField.addActionListener(null);

        _regexpCheckBox = new JCheckBox("Use Regular Expression");
        panel.add(_regexpCheckBox);

        JButton previousButton = new JButton("Previous");
        panel.add(previousButton);

        JButton nextButton = new JButton("Next");
        getRootPane().setDefaultButton(nextButton);
        panel.add(nextButton);

        layout.setHorizontalGroup(
                layout.createSequentialGroup().addComponent(findLabel).addGroup(layout.createParallelGroup().addComponent(_searchField).addComponent(_regexpCheckBox)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(previousButton).addComponent(nextButton)));

        layout.setVerticalGroup(
                layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(findLabel).addComponent(_searchField).addComponent(previousButton)).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(_regexpCheckBox).addComponent(nextButton)));

        getContentPane().add(panel);
        pack();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        WindowCenterer.centerOnMainScreen(this);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SearchPanel();
    }
}
