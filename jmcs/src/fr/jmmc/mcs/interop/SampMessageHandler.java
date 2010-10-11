/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: SampMessageHandler.java,v 1.7 2010-10-11 14:14:59 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2010/10/07 14:59:56  bourgesl
 * added TODO processCall default impl
 *
 * Revision 1.5  2010/10/05 14:52:31  bourgesl
 * removed SampException in several method signatures
 *
 * Revision 1.4  2010/10/05 10:17:56  bourgesl
 * fixed warnings / javadoc
 * fixed exception handling / logs
 * fixed member visibility
 *
 * Revision 1.3  2010/10/05 09:44:19  bourgesl
 * fixed warnings / javadoc
 *
 * Revision 1.2  2010/09/24 12:06:16  lafrasse
 * Added mType facilities and SampCapability support.
 *
 * Revision 1.1  2010/09/14 14:29:49  lafrasse
 * First SAMP manager implementation.
 *
 ******************************************************************************/
package fr.jmmc.mcs.interop;

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
 * @author lafrasse
 */
public abstract class SampMessageHandler extends AbstractMessageHandler
{

    /* members */
    /** Store the handled mType */
    private final String _mType;

    /**
     * Generic constructor taking its mType as a String (e.g for private message)
     * 
     * @param mType single MType which this handler can process
     */
    public SampMessageHandler(final String mType)
    {
        super(mType);

        _mType = mType;

        SampManager.registerCapability(this);
    }

    /** 
     * Dedicated constructor taking a public or private SAMP capability
     * @param capability public or private SAMP capability 
     */
    public SampMessageHandler(final SampCapability capability)
    {
        this(capability.mType());
    }

    /**
     * Return the currently handled mType as a String
     * @return the currently handled mType as a String 
     */
    public final String handledMType()
    {
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
    public final Map<?,?> processCall(final HubConnection connection, final String senderId, final Message message) throws SampException {

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
