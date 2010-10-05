/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampMessageHandler.java,v 1.2 2010-09-24 12:06:16 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/09/14 14:29:49  lafrasse
 * First SAMP manager implementation.
 *
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

    /** Store the handled mType */
    String _mType = null;

    /** Generic constructor taking its mType as a String (e.g for private message) */
    public SampMessageHandler(String mType) throws SampException
    {
        super(mType);

        _mType = mType;

        SampManager.registerCapability(this);
    }

    /** Dedicated constructor taking a public SAMP capability */
    public SampMessageHandler(SampCapability capability) throws SampException
    {
        this(capability.mType());
    }

    /** Return the currently handled mType as a String */
    public String handledMType()
    {
        return _mType;
    }
}
