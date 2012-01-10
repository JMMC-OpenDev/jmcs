/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.network.interop;

import java.util.Map;
import org.astrogrid.samp.Message;
import org.astrogrid.samp.client.AbstractMessageHandler;
import org.astrogrid.samp.client.HubConnection;
import org.astrogrid.samp.client.SampException;

/**
 * SampMessageHandler class.
 *
 * Conveniently provides automatic registration of your message handler.
 * 
 * @author Sylvain LAFRASSE, Laurent BOURGES.
 */
public abstract class SampMessageHandler extends AbstractMessageHandler {

    /* members */
    /** Store the handled mType */
    private final String _mType;

    /**
     * Generic constructor taking its mType as a String (e.g for private message)
     * 
     * @param mType single MType which this handler can process
     */
    public SampMessageHandler(final String mType) {
        super(mType);

        _mType = mType;

        SampManager.registerCapability(this);
    }

    /** 
     * Dedicated constructor taking a public or private SAMP capability
     * @param capability public or private SAMP capability 
     */
    public SampMessageHandler(final SampCapability capability) {
        this(capability.mType());
    }

    /**
     * Return the currently handled mType as a String
     * @return the currently handled mType as a String 
     */
    public final String handledMType() {
        return _mType;
    }

    /**
     * Implements message processing : notification only (no response)
     * 
     * This delegates the message processing to @see #processMessage(String, Message)
     * and returns null (equivalent to an empty map)
     *
     * @param connection  hub connection
     * @param senderId  public ID of sender client
     * @param message  message with MType this handler is subscribed to
     * @return null (empty map)
     * @throws SampException if any error occured while message processing
     */
    @Override
    public final Map<?, ?> processCall(final HubConnection connection, final String senderId, final Message message) throws SampException {

        // TODO: handle SampException to display any error message or at least in status bar ...
        
        this.processMessage(senderId, message);

        return null;
    }

    /**
     * Implements message processing : must be implemented in child classes
     *
     * @param senderId public ID of sender client
     * @param message message with MType this handler is subscribed to
     * @throws SampException if any error occured while message processing
     */
    protected abstract void processMessage(final String senderId, final Message message) throws SampException;
}
