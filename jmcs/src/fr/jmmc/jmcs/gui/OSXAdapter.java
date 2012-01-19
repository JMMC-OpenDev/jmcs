/*
 File:                OSXAdapter.java
 Abstract:   A single class with clear, static entry points for
 hooking existing preferences, about, quit functionality
 from an existing Java app into handlers for the Mac OS X
 application menu.  Useful for developers looking to support
 multiple platforms with a single codebase, and support
 Mac OS X features with minimal impact.
 Version:        1.1
 Disclaimer: IMPORTANT:  This Apple software is supplied to you by Apple
 Computer, Inc. ("Apple") in consideration of your agreement to the
 following terms, and your use, installation, modification or
 redistribution of this Apple software constitutes acceptance of these
 terms.  If you do not agree with these terms, please do not use,
 install, modify or redistribute this Apple software.
 In consideration of your agreement to abide by the following terms, and
 subject to these terms, Apple grants you a personal, non-exclusive
 license, under Apple's copyrights in this original Apple software (the
 "Apple Software"), to use, reproduce, modify and redistribute the Apple
 Software, with or without modifications, in source and/or binary forms;
 provided that if you redistribute the Apple Software in its entirety and
 without modifications, you must retain this notice and the following
 text and disclaimers in all such redistributions of the Apple Software.
 Neither the name, trademarks, service marks or logos of Apple Computer,
 Inc. may be used to endorse or promote products derived from the Apple
 Software without specific prior written permission from Apple.  Except
 as expressly stated in this notice, no other rights or licenses, express
 or implied, are granted by Apple herein, including but not limited to
 any patent rights that may be infringed by your derivative works or by
 other works in which the Apple Software may be incorporated.
 The Apple Software is provided by Apple on an "AS IS" basis.  APPLE
 MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 FOR A PARTICULAR PURPOSE, REGARDING THE APPLE SOFTWARE OR ITS USE AND
 OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 IN NO EVENT SHALL APPLE BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 MODIFICATION AND/OR DISTRIBUTION OF THE APPLE SOFTWARE, HOWEVER CAUSED
 AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 STRICT LIABILITY OR OTHERWISE, EVEN IF APPLE HAS BEEN ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 Copyright � 2003-2006 Apple Computer, Inc., All Rights Reserved
 */
package fr.jmmc.jmcs.gui;

import fr.jmmc.jmcs.App;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import fr.jmmc.jmcs.gui.action.ActionRegistrar;
import java.awt.event.ActionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.AbstractAction;

import javax.swing.JFrame;

/**
 * Mac OS X adapter
 * 
 * @author Brice COLUCCI, Sylvain LAFRASSE, Laurent BOURGES.
 */
public class OSXAdapter extends ApplicationAdapter {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(OSXAdapter.class.getName());
    /** pseudo-singleton model; no point in making multiple instances */
    private static OSXAdapter theAdapter;
    /** application */
    private static com.apple.eawt.Application theApplication;
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
    private OSXAdapter(final JFrame mainFrame) {
        mainAppFrame = mainFrame;

        _registrar = ActionRegistrar.getInstance();
    }

    /**
     * Handle about action
     *
     * @param ae application event
     */
    @Override
    public void handleAbout(final ApplicationEvent ae) {
        if (mainAppFrame != null) {
            ae.setHandled(true);
            App.aboutBoxAction().actionPerformed(null);
        } else {
            throw new IllegalStateException("handleAbout: MyApp instance detached from listener");
        }
    }

    /**
     * Show the user preferences
     *
     * @param ae application event
     */
    @Override
    public void handlePreferences(ApplicationEvent ae) {
        if (mainAppFrame != null) {
            ae.setHandled(true);
            AbstractAction preferenceAction = _registrar.getPreferenceAction();
            if (preferenceAction != null) {
                preferenceAction.actionPerformed(null);
            }
        } else {
            throw new IllegalStateException("handlePreferences: MyApp instance detached from listener");
        }
    }

    /**
     * Handle quit action
     *
     * @param ae application event
     */
    @Override
    public void handleQuit(ApplicationEvent ae) {
        if (mainAppFrame != null) {
            /* You MUST setHandled(false) if you want to delay or cancel the quit.
             * This is important for cross-platform development -- have a universal quit
             * routine that chooses whether or not to quit, so the functionality is identical
             * on all platforms.  This example simply cancels the AppleEvent-based quit and
             * defers to that universal method. */
            if (!alreadyQuitting) {
                alreadyQuitting = true; // Prevent handleQuit() to be called twice (known Apple bug)
                _registrar.getQuitAction().actionPerformed(null);
            } else {
                alreadyQuitting = false;
            }

            ae.setHandled(false);
        } else {
            throw new IllegalStateException("handleQuit: MyApp instance detached from listener");
        }
    }

    /**
     * Handle the open action
     *
     * @param ae application event
     */
    @Override
    public void handleOpenFile(ApplicationEvent ae) {
        if (mainAppFrame != null) {
            ae.setHandled(true);

            if (logger.isInfoEnabled()) {
                logger.info("Should open '{}'.", ae.getFilename());
            }

            _registrar.getOpenAction().actionPerformed(new ActionEvent(_registrar, 0, ae.getFilename()));
        } else {
            throw new IllegalStateException("handleOpenFile: MyApp instance detached from listener");
        }
    }

    /**
     * Register this adapter
     *
     * @param mainFrame main application frame
     */
    public static void registerMacOSXApplication(final JFrame mainFrame) {
        if (theApplication == null) {
            theApplication = new com.apple.eawt.Application();
        }

        if (theAdapter == null) {
            theAdapter = new OSXAdapter(mainFrame);
        }

        theApplication.addApplicationListener(theAdapter);
    }

    /**
     * Enable Application Preferences action
     *
     * @param enabled boolean enable preferences action
     */
    public static void enablePrefs(boolean enabled) {
        if (theApplication == null) {
            theApplication = new com.apple.eawt.Application();
        }

        AbstractAction preferenceAction = ActionRegistrar.getInstance().getPreferenceAction();
        if (preferenceAction != null) {
            theApplication.setEnabledPreferencesMenu(enabled);
        } else {
            theApplication.setEnabledPreferencesMenu(!enabled);
        }
    }
}
