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
package fr.jmmc.jmcs.gui.util;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.OpenFilesHandler;
import com.apple.eawt.PreferencesHandler;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.apple.eawt.QuitStrategy;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import fr.jmmc.jmcs.gui.action.internal.InternalActionFactory;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mac OS X adapter.
 * 
 * @author Brice COLUCCI, Sylvain LAFRASSE, Laurent BOURGES.
 */
public class MacOSXAdapter implements AboutHandler, PreferencesHandler, QuitHandler, OpenFilesHandler {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(MacOSXAdapter.class.getName());
    /** pseudo-singleton model; no point in making multiple instances */
    private static MacOSXAdapter _instance;
    /** application */
    private static Application _application;
    /* members */
    /** flag to prevent handleQuit() to be called twice (known Apple bug) */
    private boolean alreadyQuitting = false;
    /** Store a proxy to the shared ActionRegistrar facility */
    private ActionRegistrar _registrar = null;
    /** reference to the app frame where the existing quit, about, prefs code is */
    private JFrame mainAppFrame;

    /**
     * Creates a new OSXAdapter object.
     *
     * @param mainFrame application main frame
     */
    private MacOSXAdapter(final JFrame mainFrame) {
        mainAppFrame = mainFrame;
        _registrar = ActionRegistrar.getInstance();
    }

    /** Handle about action */
    @Override
    public void handleAbout(AboutEvent ae) {
        if (mainAppFrame != null) {
            InternalActionFactory.showAboutBoxAction().actionPerformed(null);
        } else {
            throw new IllegalStateException("handleAbout: MyApp instance detached from listener");
        }
    }

    /** Show the user preferences */
    @Override
    public void handlePreferences(PreferencesEvent ae) {
        if (mainAppFrame != null) {
            AbstractAction preferenceAction = _registrar.getPreferenceAction();
            if (preferenceAction != null) {
                preferenceAction.actionPerformed(null);
            }
        } else {
            throw new IllegalStateException("handlePreferences: MyApp instance detached from listener");
        }
    }

    /** Handle quit action */
    @Override
    public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
        if (mainAppFrame != null) {
            /* You MUST setHandled(false) if you want to delay or cancel the quit.
             * This is important for cross-platform development -- have a universal quit
             * routine that chooses whether or not to quit, so the functionality is identical
             * on all platforms.  This example simply cancels the AppleEvent-based quit and
             * defers to that universal method. */
            ActionEvent evt = new ActionEvent(response, 0, null);
            _registrar.getQuitAction().actionPerformed(evt);
        } else {
            throw new IllegalStateException("handleQuitRequestWith: MyApp instance detached from listener");
        }
    }

    /** Handle the open action */
    @Override
    public void openFiles(OpenFilesEvent e) {
        if (mainAppFrame != null) {
            final int FIRST_FILE_INDEX = 0;
            final String firstFilePath = e.getFiles().get(FIRST_FILE_INDEX).getAbsolutePath();
            if (logger.isInfoEnabled()) {
                logger.info("Should open '{}' file.", firstFilePath);
            }
            _registrar.getOpenAction().actionPerformed(new ActionEvent(_registrar, 0, firstFilePath));
        } else {
            throw new IllegalStateException("openFiles: MyApp instance detached from listener");
        }
    }

    /**
     * Register this adapter
     *
     * @param mainFrame main application frame
     */
    public static void registerMacOSXApplication(final JFrame mainFrame) {

        if (_application == null) {
            _application = Application.getApplication();
        }

        if (_instance == null) {
            _instance = new MacOSXAdapter(mainFrame);
        }

        // Link 'About...' menu entry
        _application.setAboutHandler(_instance);

        // Set up quitiing behaviour
        _application.setQuitHandler(_instance);
        _application.disableSuddenTermination();
        _application.setQuitStrategy(QuitStrategy.SYSTEM_EXIT_0);

        // Set up double-clicked file opening handler
        _application.setOpenFileHandler(_instance);

        // Link 'Preferences' menu entry (if any)
        AbstractAction preferenceAction = ActionRegistrar.getInstance().getPreferenceAction();
        if (preferenceAction == null) {
            _application.setPreferencesHandler(null);
        } else {
            _application.setPreferencesHandler(_instance);
        }
    }
}
