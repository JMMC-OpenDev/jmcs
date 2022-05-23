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
package fr.jmmc.jmcs.network.interop;

import fr.jmmc.jmcs.util.MCSExceptionHandler;
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

        /*
        x-samp.mostly-harmless : A subscribing client can use this to note that an MType it subscribes to is known 
        to be (in)capable of causing security breaches if invoked by a potentially untrusted client. 
        A value of "1" means that it is safe for untrusted invocation, and "0" means it is unsafe. 
        Any other value (or absence of the key) makes no comment. 
        Hub implementations may use this information to modify their policies about whether untrusted 
        or marginally trusted clients are blocked from sending the MType(s) so annotated to that client, 
        but they should not rely on its presence (should fall back to some default policy if it is not supplied). 
         */
        final Object subMap = getSubscriptions().get(mType);
        if (subMap instanceof Map) {
            ((Map) subMap).put("x-samp.mostly-harmless", "1"); // safe messages
        }

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
     * @throws SampException if any error occurred while message processing
     */
    @Override
    public final Map<?, ?> processCall(final HubConnection connection, final String senderId, final Message message) throws SampException {

        // TODO: handle SampException to display any error message or at least in status bar ...
        try {
            processMessage(senderId, message);

        } catch (SampException se) {
            throw se;
        } catch (Throwable th) {
            // TODO: only runtime exception ?
            MCSExceptionHandler.runExceptionHandler(th);
        }

        return null;
    }

    /**
     * Implements message processing : must be implemented in child classes
     *
     * @param senderId public ID of sender client
     * @param message message with MType this handler is subscribed to
     * @throws SampException if any error occurred while message processing
     */
    protected abstract void processMessage(final String senderId, final Message message) throws SampException;
}
