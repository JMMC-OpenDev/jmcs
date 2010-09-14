/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampMessageHandler.java,v 1.1 2010-09-14 14:29:49 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 ******************************************************************************/
package fr.jmmc.mcs.interop;

import java.util.*;

import java.util.logging.*;

import org.astrogrid.samp.*;
import org.astrogrid.samp.client.*;


/**
 * SampMessageHandler class.
 *
 * Conveniently provides automatic registration of your message handler.
 *
 * @author lafrasse
 */
public abstract class SampMessageHandler extends AbstractMessageHandler
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger("fr.jmmc.mcs.interop.JmcsMessageHandler");

    /** Constructor */
    public SampMessageHandler(String mType) throws SampException
    {
        super(mType);
        SampManager.registerCapability(this);
    }
}
