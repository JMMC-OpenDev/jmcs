/*******************************************************************************
 *                 jMCS project ( http://www.jmmc.fr/dev/jmcs )
 *******************************************************************************
 * Copyright (c) 2013, CNRS. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the CNRS nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.component;

import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.Bootstrapper;
import fr.jmmc.jmcs.data.preference.Preferences;
import fr.jmmc.jmcs.data.preference.PreferencesException;
import fr.jmmc.jmcs.gui.util.SwingUtils;
import fr.jmmc.jmcs.gui.util.WindowUtils;
import fr.jmmc.jmcs.service.BrowserLauncher;
import fr.jmmc.jmcs.util.JVMUtils;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create resize-able, best-sized window able to display either plain text or HTML.
 *
 * @author Sylvain LAFRASE
 */
public class ResizableTextViewFactory {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(ResizableTextViewFactory.class.getName());
    // Constants
    private static final int MARGIN = 35;
    private static final int MINIMUM_WIDTH = 400;
    private static final int MAXIMUM_WIDTH = 1000;
    private static final int MINIMUM_HEIGHT = 300;
    private static final int MAXIMUM_HEIGHT = 750;
    private static final int BUTTON_HEIGHT = 20;

    /**
     * Create a window containing the given plain text with the given title.
     * @param text plain text to show.
     * @param title window title
     * @param modal true to make the window modal, false otherwise.
     */
    public static void createTextWindow(final String text, final String title, final boolean modal) {
        createTextWindow(text, title, modal, 0);
    }

    /**
     * Create a window containing the given plain text with the given title.
     * @param text plain text to show.
     * @param title window title
     * @param modal true to make the window modal, false otherwise.
     * @param timeoutMillis timeout in milliseconds to wait before the window is hidden (auto-hide)
     */
    public static void createTextWindow(final String text, final String title, final boolean modal, final int timeoutMillis) {
        SwingUtils.invokeAndWaitEDT(new Runnable() {
            @Override
            public void run() {
                final JDialog dialog = new JDialog(App.getExistingFrame(), title, modal);
                final JEditorPane editorPane = startLayout(dialog);

                // if modal, blocks until the dialog is closed:
                finishLayout(editorPane, dialog, text, modal, timeoutMillis, null, null);
            }
        });
    }

    /**
     * Create a window containing the given HTML text with the given title.
     * @param html HTML text to show.
     * @param title window title
     * @param modal true to make the window modal, false otherwise.
     */
    public static void createHtmlWindow(final String html, final String title, final boolean modal) {
        createHtmlWindow(html, title, modal, 0);
    }

    /**
     * Create a window containing the given HTML text with the given title.
     * @param html HTML text to show.
     * @param title window title
     * @param modal true to make the window modal, false otherwise.
     * @param timeoutMillis timeout in milliseconds to wait before the window is hidden (auto-hide)
     */
    public static void createHtmlWindow(final String html, final String title, final boolean modal, final int timeoutMillis) {
        createHtmlWindow(html, title, modal, timeoutMillis, null, null);
    }

    /**
     * Create a window containing the given HTML text with the given title.
     * @param html HTML text to show.
     * @param title window title
     * @param modal true to make the window modal, false otherwise.
     * @param timeoutMillis timeout in milliseconds to wait before the window is hidden (auto-hide)
     * @param preferences null or reference to the dedicated Preferences singleton
     * @param dismissablePreferenceName null or preference name to store
     */
    public static void createHtmlWindow(final String html, final String title, final boolean modal,
                                        final int timeoutMillis, final Preferences preferences, final String dismissablePreferenceName) {
        if (preferences != null && dismissablePreferenceName != null && DismissableMessagePane.getPreferenceState(preferences, dismissablePreferenceName)) {
            //nop
        } else if (Bootstrapper.isHeadless()) {
            _logger.info("[Headless] Html Message: {}", html);
        } else {
            SwingUtils.invokeAndWaitEDT(new Runnable() {
                @Override
                public void run() {
                    final JDialog dialog = new JDialog(App.getExistingFrame(), title, modal);
                    final JEditorPane editorPane = startLayout(dialog);
                    editorPane.setContentType("text/html");
                    editorPane.addHyperlinkListener(new HyperlinkListener() {
                        @Override
                        public void hyperlinkUpdate(HyperlinkEvent event) {
                            // When a link is clicked
                            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

                                // Get the clicked URL
                                final URL url = event.getURL();

                                // If it is valid
                                if (url != null) {
                                    // Get it in the good format
                                    final String clickedURL = url.toExternalForm();

                                    if (SystemUtils.JAVA_VERSION_FLOAT >= 1.7f) {
                                        // Open the url in web browser
                                        BrowserLauncher.openURL(clickedURL);
                                    }
                                } else { // Assume it was an anchor
                                    String anchor = event.getDescription();
                                    editorPane.scrollToReference(anchor);
                                }
                            }
                        }
                    });

                    // if modal, blocks until the dialog is closed:
                    finishLayout(editorPane, dialog, html, modal, timeoutMillis, preferences, dismissablePreferenceName);
                }
            });
        }
    }

    /**
     * Initialize the frame layout and return the editor pane
     * @param dialog frame to layout
     * @return editor pane
     */
    private static JEditorPane startLayout(final JDialog dialog) {
        dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setAlwaysOnTop(true);

        final JEditorPane editorPane = new JEditorPane();
        // Use default fonts (hi-dpi) if no font defined in html:
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editorPane.setEditable(false);
        editorPane.setMargin(new Insets(5, 5, 5, 5));
        return editorPane;
    }

    /**
     * Finish the frame layout (editor pane) using the given text to display
     * @param editorPane editor pane to use
     * @param dialog frame to layout
     * @param text text to display
     * @param modal true to make the window modal, false otherwise.
     * @param timeoutMillis timeout in milliseconds to wait before the window is hidden (auto-hide)
     * @param preferences null or reference to the dedicated Preferences singleton
     * @param dismissablePreferenceName null or preference name to store
     */
    private static void finishLayout(final JEditorPane editorPane, final JDialog dialog, final String text,
                                     final boolean modal, final int timeoutMillis, final Preferences preferences,
                                     final String dismissablePreferenceName) {

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(editorPane);
        scrollPane.setBorder(null);

        // Window layout
        final Container contentPane = dialog.getContentPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        editorPane.setText(text);
        editorPane.setCaretPosition(0); // Move back focus at the top of the content
        editorPane.setSize(MINIMUM_WIDTH, Integer.MAX_VALUE);

        if (modal) {
            final JButton button = new JButton("OK");
            button.addActionListener(new CloseWindowAction(dialog));

            if (preferences != null && dismissablePreferenceName != null) {
                JPanel p = new JPanel();
                final JButton button2 = new JButton(DismissableMessagePane.DO_NOT_SHOW_THIS_MESSAGE_AGAIN);
                button2.addActionListener(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DismissableMessagePane.setPreferenceState(preferences, dismissablePreferenceName, true);
                        try {
                            preferences.saveToFile();
                        } catch (PreferencesException ex) {
                            _logger.warn("Can't store on disk following preferences " + dismissablePreferenceName, ex);
                        }
                    }
                });
                button2.addActionListener(new CloseWindowAction(dialog));

                p.add(button2);
                p.add(button);
                contentPane.add(p, BorderLayout.SOUTH);
            } else {
                contentPane.add(button, BorderLayout.SOUTH);
            }

            // Set as default button with focus activated
            dialog.getRootPane().setDefaultButton(button);

            SwingUtils.invokeLaterEDT(new Runnable() {
                @Override
                public void run() {
                    button.requestFocusInWindow();
                }
            });
        }

        // Sizing
        dialog.pack();
        final int minimumEditorPaneWidth = editorPane.getWidth() + MARGIN;
        final int minimumEditorPaneHeight = editorPane.getMinimumSize().height + MARGIN;
        final int finalWidth = Math.max(Math.min(minimumEditorPaneWidth, MAXIMUM_WIDTH), MINIMUM_WIDTH);
        int finalHeight = Math.max(Math.min(minimumEditorPaneHeight, MAXIMUM_HEIGHT), MINIMUM_HEIGHT);
        if (modal) {
            finalHeight += BUTTON_HEIGHT; // For button height
        }
        dialog.setPreferredSize(new Dimension(finalWidth, finalHeight));

        WindowUtils.setClosingKeyboardShortcuts(dialog);
        dialog.pack();
        WindowUtils.centerOnMainScreen(dialog);

        if (timeoutMillis > 0) {
            // Use Timer to wait before closing this dialog :
            final Timer timer = new Timer(timeoutMillis, new CloseWindowAction(dialog));

            // timer runs only once :
            timer.setRepeats(false);
            timer.start();
        }

        // Show it and if modal, waits until dialog is not visible or disposed:
        dialog.setVisible(true);
    }

    /** Action to close the given window by sending a window closing event */
    private final static class CloseWindowAction implements ActionListener {

        /** window to close */
        private final Window _window;

        CloseWindowAction(final Window window) {
            _window = window;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            _logger.debug("CloseWindowAction called.");

            if (_window.isVisible()) {
                // trigger standard closing action (@see JFrame.setDefaultCloseOperation)
                // i.e. hide or dispose the window:
                _window.dispatchEvent(new WindowEvent(_window, WindowEvent.WINDOW_CLOSING));
            }
        }
    }

    /**
     * Test code
     * @param args unused arguments
     */
    public static void main(String[] args) {
        final int autoHideDelay = 2000; // 2s

        // TEXT Windows:
        final String text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum congue tincidunt justo. Etiam massa arcu, vestibulum pulvinar accumsan ut, ullamcorper sed sapien. Quisque ullamcorper felis eget turpis mattis vestibulum. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Cras et turpis justo, sed lacinia libero. Sed in tellus eget libero posuere euismod. In nulla mi, semper a condimentum quis, tincidunt eget magna. Etiam tristique venenatis ante eu interdum. Phasellus ultrices rhoncus urna, ac pretium ante ultricies condimentum. Vestibulum et turpis ac felis pulvinar rhoncus nec a nulla. Proin eu ante eu leo fringilla ornare in a massa. Morbi varius porttitor nibh ac elementum. Cras sed neque massa, sed vulputate magna. Ut viverra velit magna, sagittis tempor nibh.";
        ResizableTextViewFactory.createTextWindow(text, "Text", true);
        System.out.println("modal dialog passed.");

        ResizableTextViewFactory.createHtmlWindow("Test Modal Text Window with timeout: wait the dialog to auto-hide", "Text", true, autoHideDelay);
        System.out.println("modal dialog passed.");

        // HTML Windows:
        final String html = "<html><head><title>D??claration universelle des droits de l'homme</title></head><body><h1>D??claration universelle des droits de l'homme</h1><h2>Pr??ambule</h2><p><em>Consid??rant</em> que la reconnaissance de la dignit?? inh??rente ?? tous les membres de la famille humaine et de leurs droits ??gaux et inali??nables constitue le fondement de la libert??, de la justice et de la paix dans le monde.</p><p><em>Consid??rant</em> que la m??connaissance et le m??pris des droits de l'homme ont conduit ?? des actes de barbarie qui r??voltent la conscience de l'humanit?? et que l'av??nement d'un monde o?? les ??tres humains seront libres de parler et de croire, lib??r??s de la terreur et de la mis??re, a ??t?? proclam?? comme la plus haute aspiration de l'homme.</p><p><em>Consid??rant</em> qu'il est essentiel que les droits de l'homme soient prot??g??s par un r??gime de droit pour que l'homme ne soit pas contraint, en supr??me recours, ?? la r??volte contre la tyrannie et l'oppression.</p><p><em>Consid??rant</em> qu'il est essentiel d'encourager le d??veloppement de relations amicales entre nations.</p><p><em>Consid??rant</em> que dans la Charte les peuples des Nations Unies ont proclam?? ?? nouveau leur foi dans les droits fondamentaux de l'homme, dans la dignit?? et la valeur de la personne humaine, dans l'??galit?? des droits des hommes et des femmes, et qu'ils se sont d??clar??s r??solus ?? favoriser le progr??s social et ?? instaurer de meilleures conditions de vie dans une libert?? plus grande.</p><p><em>Consid??rant</em> que les Etats Membres se sont engag??s ?? assurer, en coop??ration avec l'Organisation des Nations Unies, le respect universel et effectif des droits de l'homme et des libert??s fondamentales.</p><p><em>Consid??rant</em> qu'une conception commune de ces droits et libert??s est de la plus haute importance pour remplir pleinement cet engagement.</p><p><strong>L'Assembl??e G??n??rale proclame la pr??sente D??claration universelle des droits de l'homme</strong> comme l'id??al commun ?? atteindre par tous les peuples et toutes les nations afin que tous les individus et tous les organes de la soci??t??, ayant cette D??claration constamment ?? l'esprit, s'efforcent, par l'enseignement et l'??ducation, de d??velopper le respect de ces droits et libert??s et d'en assurer, par des mesures progressives d'ordre national et international, la reconnaissance et l'application universelles et effectives, tant parmi les populations des Etats Membres eux-m??mes que parmi celles des territoires plac??s sous leur juridiction.</p><h2>Article premier</h2><p>Tous les ??tres humains naissent libres et ??gaux en dignit?? et en droits. Ils sont dou??s de raison et de conscience et doivent agir les uns envers les autres dans un esprit de fraternit??.<p/><h2>Article 2</h2><p>1.Chacun peut se pr??valoir de tous les droits et de toutes les libert??s proclam??s dans la pr??sente D??claration, sans distinction aucune, notamment de race, de couleur, de sexe, de langue, de religion, d'opinion politique ou de toute autre opinion, d'origine nationale ou sociale, de fortune, de naissance ou de toute autre situation.<br>2.De plus, il ne sera fait aucune distinction fond??e sur le statut politique, juridique ou international du pays ou du territoire dont une personne est ressortissante, que ce pays ou territoire soit ind??pendant, sous tutelle, non autonome ou soumis ?? une limitation quelconque de souverainet??.</p><h2>Article 3</h2><p>Tout individu a droit ?? la vie, ?? la libert?? et ?? la s??ret?? de sa personne.<p/><h2>Article 4</h2><p>Nul ne sera tenu en esclavage ni en servitude; l'esclavage et la traite des esclaves sont interdits sous toutes leurs formes.</p><h2>Article 5</h2><p>Nul ne sera soumis ?? la torture, ni ?? des peines ou traitements cruels, inhumains ou d??gradants.<p/><h2>Article 6</h2><p>Chacun a le droit ?? la reconnaissance en tous lieux de sa personnalit?? juridique.<p/><h2>Article 7</h2><p>Tous sont ??gaux devant la loi et ont droit sans distinction ?? une ??gale protection de la loi. Tous ont droit ?? une protection ??gale contre toute discrimination qui violerait la pr??sente D??claration et contre toute provocation ?? une telle discrimination.</p><h2>Article 8</h2><p>Toute personne a droit ?? un recours effectif devant les juridictions nationales comp??tentes contre les actes violant les droits fondamentaux qui lui sont reconnus par la constitution ou par la loi.<p/><h2>Article 9</h2><p>Nul ne peut ??tre arbitrairement arr??t??, d??tenu ou exil??.<p/><h2>Article 10</h2><p>Toute personne a droit, en pleine ??galit??, ?? ce que sa cause soit entendue ??quitablement et publiquement par un tribunal ind??pendant et impartial, qui d??cidera, soit de ses droits et obligations, soit du bien-fond?? de toute accusation en mati??re p??nale dirig??e contre elle.<p/><h2>Article 11</h2><p>1. Toute personne accus??e d'un acte d??lictueux est pr??sum??e innocente jusqu'?? ce que sa culpabilit?? ait ??t?? l??galement ??tablie au cours d'un proc??s public o?? toutes les garanties n??cessaires ?? sa d??fense lui auront ??t?? assur??es.<br>2. Nul ne sera condamn?? pour des actions ou omissions qui, au moment o?? elles ont ??t?? commises, ne constituaient pas un acte d??lictueux d'apr??s le droit national ou international. De m??me, il ne sera inflig?? aucune peine plus forte que celle qui ??tait applicable au moment o?? l'acte d??lictueux a ??t?? commis.<p/><h2>Article 12</h2><p>Nul ne sera l'objet d'immixtions arbitraires dans sa vie priv??e, sa famille, son domicile ou sa correspondance, ni d'atteintes ?? son honneur et ?? sa r??putation. Toute personne a droit ?? la protection de la loi contre de telles immixtions ou de telles atteintes.<p/><h2>Article 13</h2><p>1. Toute personne a le droit de circuler librement et de choisir sa r??sidence ?? l'int??rieur d'un Etat.<br>2. Toute personne a le droit de quitter tout pays, y compris le sien, et de revenir dans son pays.<p/><h2>Article 14</h2><p>1. Devant la pers??cution, toute personne a le droit de chercher asile et de b??n??ficier de l'asile en d'autres pays.<br>2. Ce droit ne peut ??tre invoqu?? dans le cas de poursuites r??ellement fond??es sur un crime de droit commun ou sur des agissements contraires aux buts et aux principes des Nations Unies.<p/><h2>Article 15</h2><p>1. Tout individu a droit ?? une nationalit??.<br>2. Nul ne peut ??tre arbitrairement priv?? de sa nationalit??, ni du droit de changer de nationalit??.<p/><h2>Article 16</h2><p>1. A partir de l'??ge nubile, l'homme et la femme, sans aucune restriction quant ?? la race, la nationalit?? ou la religion, ont le droit de se marier et de fonder une famille. Ils ont des droits ??gaux au regard du mariage, durant le mariage et lors de sa dissolution.<br> 2. Le mariage ne peut ??tre conclu qu'avec le libre et plein consentement des futurs ??poux.<br>3. La famille est l'??l??ment naturel et fondamental de la soci??t?? et a droit ?? la protection de la soci??t?? et de l'Etat.<p/><h2>Article 17</h2><p>1. Toute personne, aussi bien seule qu'en collectivit??, a droit ?? la propri??t??.<br /> 2. Nul ne peut ??tre arbitrairement priv?? de sa propri??t??.<p/><h2>Article 18</h2><p>Toute personne a droit ?? la libert?? de pens??e, de conscience et de religion ; ce droit implique la libert?? de changer de religion ou de conviction ainsi que la libert?? de manifester sa religion ou sa conviction seule ou en commun, tant en public qu'en priv??, par l'enseignement, les pratiques, le culte et l'accomplissement des rites.</p><h2>Article 19</h2><p>Tout individu a droit ?? la libert?? d'opinion et d'expression, ce qui implique le droit de ne pas ??tre inqui??t?? pour ses opinions et celui de chercher, de recevoir et de r??pandre, sans consid??rations de fronti??res, les informations et les id??es par quelque moyen d'expression que ce soit.</p><h2>Article 20</h2><p>1. Toute personne a droit ?? la libert?? de r??union et d'association pacifiques.<br>2. Nul ne peut ??tre oblig?? de faire partie d'une association.<p/><h2>Article 21</h2><p>1. Toute personne a le droit de prendre part ?? la direction des affaires publiques de son pays, soit directement, soit par l'interm??diaire de repr??sentants librement choisis.<br> 2. Toute personne a droit ?? acc??der, dans des conditions d'??galit??, aux fonctions publiques de son pays.<br /> 3. La volont?? du peuple est le fondement de l'autorit?? des pouvoirs publics ; cette volont?? doit s'exprimer par des ??lections honn??tes qui doivent avoir lieu p??riodiquement, au suffrage universel ??gal et au vote secret ou suivant une proc??dure ??quivalente assurant la libert?? du vote.<p/><h2>Article 22</h2><p>Toute personne, en tant que membre de la soci??t??, a droit ?? la s??curit?? sociale ; elle est fond??e ?? obtenir la satisfaction des droits ??conomiques, sociaux et culturels indispensables ?? sa dignit?? et au libre d??veloppement de sa personnalit??, gr??ce ?? l'effort national et ?? la coop??ration internationale, compte tenu de l'organisation et des ressources de chaque pays.</p><h2>Article 23</h2><p>1. Toute personne a droit au travail, au libre choix de son travail, ?? des conditions ??quitables et satisfaisantes de travail et ?? la protection contre le ch??mage.<br> 2. Tous ont droit, sans aucune discrimination, ?? un salaire ??gal pour un travail ??gal.<br> 3. Quiconque travaille a droit ?? une r??mun??ration ??quitable et satisfaisante lui assurant ainsi qu'?? sa famille une existence conforme ?? la dignit?? humaine et compl??t??e, s'il y a lieu, par tous autres moyens de protection sociale.<br>4. Toute personne a le droit de fonder avec d'autres des syndicats et de s'affilier ?? des syndicats pour la d??fense de ses int??r??ts.<p/><h2>Article 24</h2><p>Toute personne a droit au repos et aux loisirs et notamment ?? une limitation raisonnable de la dur??e du travail et ?? des cong??s pay??s p??riodiques.<p/><h2>Article 25</h2><p>1. Toute personne a droit ?? un niveau de vie suffisant pour assurer sa sant??, son bien-??tre et ceux de sa famille, notamment pour l'alimentation, l'habillement, le logement, les soins m??dicaux ainsi que pour les services sociaux n??cessaires ; elle a droit ?? la s??curit?? en cas de ch??mage, de maladie, d'invalidit??, de veuvage, de vieillesse ou dans les autres cas de perte de ses moyens de subsistance par suite de circonstances ind??pendantes de sa volont??.<br>2. La maternit?? et l'enfance ont droit ?? une aide et ?? une assistance sp??ciales. Tous les enfants, qu'ils soient n??s dans le mariage ou hors mariage, jouissent de la m??me protection sociale.<p/><h2>Article 26</h2><p>1. Toute personne a droit ?? l'??ducation. L'??ducation doit ??tre gratuite, au moins en ce qui concerne l'enseignement ??l??mentaire et fondamental. L'enseignement ??l??mentaire est obligatoire. L'enseignement technique et professionnel doit ??tre g??n??ralis?? ; l'acc??s aux ??tudes sup??rieures doit ??tre ouvert en pleine ??galit?? ?? tous en fonction de leur m??rite.<br> 2. L'??ducation doit viser au plein ??panouissement de la personnalit?? humaine et au renforcement du respect des droits de l'homme et des libert??s fondamentales. Elle doit favoriser la compr??hension, la tol??rance et l'amiti?? entre toutes les nations et tous les groupes raciaux ou religieux, ainsi que le d??veloppement des activit??s des Nations Unies pour le maintien de la paix.<br>3. Les parents ont, par priorit??, le droit de choisir le genre d'??ducation ?? donner ?? leurs enfants.<p/><h2>Article 27</h2><p>1. Toute personne a le droit de prendre part librement ?? la vie culturelle de la communaut??, de jouir des arts et de participer au progr??s scientifique et aux bienfaits qui en r??sultent.<br>2. Chacun a droit ?? la protection des int??r??ts moraux et mat??riels d??coulant de toute production scientifique, litt??raire ou artistique dont il est l'auteur.<p/><h2>Article 28</h2><p>Toute personne a droit ?? ce que r??gne, sur le plan social et sur le plan international, un ordre tel que les droits et libert??s ??nonc??s dans la pr??sente D??claration puissent y trouver plein effet.<p/><h2>Article 29</h2><p>1. L'individu a des devoirs envers la communaut?? dans laquelle seule le libre et plein d??veloppement de sa personnalit?? est possible.<br> 2. Dans l'exercice de ses droits et dans la jouissance de ses libert??s, chacun n'est soumis qu'aux limitations ??tablies par la loi exclusivement en vue d'assurer la reconnaissance et le respect des droits et libert??s d'autrui et afin de satisfaire aux justes exigences de la morale, de l'ordre public et du bien-??tre g??n??ral dans une soci??t?? d??mocratique.<br>3. Ces droits et libert??s ne pourront, en aucun cas, s'exercer contrairement aux buts et aux principes des Nations Unies.<p/><h2>Article 30</h2><p>Aucune disposition de la pr??sente D??claration ne peut ??tre interpr??t??e comme impliquant pour un Etat, un groupement ou un individu un droit quelconque de se livrer ?? une activit?? ou d'accomplir un acte visant ?? la destruction des droits et libert??s qui y sont ??nonc??s.<p/></body></html>";
        ResizableTextViewFactory.createHtmlWindow(html, "HTML", false);

        ResizableTextViewFactory.createHtmlWindow("<html><head><title>Test Modal HTML Window</title></head><body><h1>D??claration universelle des droits de l'homme</h1>"
                + "<p>Test Modal HTML Window: click on button to close this window</p></body></html>", "HTML", true);
        System.out.println("modal dialog passed.");

        ResizableTextViewFactory.createHtmlWindow("<html><head><title>Test Modal HTML Window with timeout</title></head><body><h1>D??claration universelle des droits de l'homme</h1>"
                + "<p>Test Modal HTML Window with timeout: wait the dialog to auto-hide</p></body></html>", "HTML", true, autoHideDelay);
        System.out.println("modal dialog passed.");

        JVMUtils.showUnsupportedJdkWarning();
        System.out.println("modal dialog passed.");

        System.out.println("That's all Folks !");

        try {
            Thread.sleep(30 * 1000L);
        } catch (InterruptedException ex) {
            // nop
        }

        System.exit(0);
    }
}
