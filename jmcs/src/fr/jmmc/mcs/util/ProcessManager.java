/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ProcessManager.java,v 1.1 2007-02-14 10:14:38 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 *
 ******************************************************************************/
package fr.jmmc.mcs.util;


/**
 * This interface expose process activity to interested listener.
 *
 */
public interface ProcessManager {
    public void processStarted();

    public void processStoped();

    public void processTerminated(int returnedValue);

    public void errorOccured(Exception exception);

    public void outputOccured(String line);
}
