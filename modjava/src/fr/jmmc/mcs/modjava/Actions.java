/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.modjava;

import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.RegisteredAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing all application actions.
 */
public class Actions {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(Actions.class.getName());

    public GenericLoggedAction scaction1;
    public GenericLoggedAction scaction2;
    public GenericLoggedAction scaction3;
    public GenericLoggedAction scaction5;
    public GenericLoggedAction mfaction3;
    public GenericLoggedAction mfaction4;
    public GenericLoggedAction mfaction5;
    public GenericLoggedAction radio1;
    public GenericLoggedAction radio2;
    public GenericLoggedAction radio3;

    /** Creates a new Actions object. */
    public Actions() {
        _logger.info(ActionRegistrar.getInstance().toString());

        scaction1 = new GenericLoggedAction("scaction1");
        scaction2 = new GenericLoggedAction("scaction2");
        scaction3 = new GenericLoggedAction("scaction3");
        scaction5 = new GenericLoggedAction("scaction5");

        mfaction3 = new GenericLoggedAction("mfaction3");
        mfaction4 = new GenericLoggedAction("mfaction4");
        mfaction5 = new GenericLoggedAction("mfaction5");

        radio1 = new GenericLoggedAction("radio1");
        radio2 = new GenericLoggedAction("radio2");
        radio3 = new GenericLoggedAction("radio3");

        _logger.info(ActionRegistrar.getInstance().toString());
    }

    protected class GenericLoggedAction extends RegisteredAction {

        String _fieldName = null;

        public GenericLoggedAction(String fieldName) {
            super("fr.jmmc.mcs.modjava.Actions", fieldName);
            _logger.info("GenericLoggedAction('" + fieldName + "').");
            _fieldName = fieldName;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            _logger.info("GenericLoggedAction.actionPerformed('" + _fieldName + "').");
        }
    }
}
/*___oOo___*/
