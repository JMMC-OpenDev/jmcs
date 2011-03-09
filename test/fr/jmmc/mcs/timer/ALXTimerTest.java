/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ALXTimerTest.java,v 1.1 2011-03-09 10:28:07 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 */
package fr.jmmc.mcs.timer;

import fr.jmmc.mcs.astro.ALX;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import fr.jmmc.mcs.timer.TimerFactory.UNIT;

/**
 * Simple timer tests of ALX class
 * @author bourgesl
 */
public class ALXTimerTest
{

    /** Logger */
    private static final Logger log = Logger.getLogger(ALXTimerTest.class.getName());

    /**
     * MicroBenchmarks of ALX.ld2ud()
     * @param args
     */
    public static void main(String[] args)
    {
        // Set the default locale to en-US locale (for Numerical Fields "." ",")
        Locale.setDefault(Locale.US);

        /** ALX ld2ud - threshold = 0.5 ms */
        final double threshold = 0.5d;

        final int N = 50000;
        long start;

        for (int n = 0; n < 10; n++) {
            // repeat tests n times :

//            if (n < 3) {
            // TimerFactory warmup and reset :
            TimerFactory.resetTimers();
            //          }

            for (int i = 0; i < N; i++) {

                start = System.nanoTime();

                // do something : use ALX
                try {
                    ALX.ld2ud(1d, "A0");
                } catch (Exception e) {
                    log.log(Level.SEVERE, "test fail", e);
                }

                TimerFactory.getSimpleTimer("ALX.ld2ud(ns)", UNIT.ns).addNanoSeconds(start, System.nanoTime());
            }

            for (int i = 0; i < N; i++) {

                start = System.nanoTime();

                // do something : use ALX
                try {
                    ALX.ld2ud(1d, "A0");
                } catch (Exception e) {
                    log.log(Level.SEVERE, "test fail", e);
                }

                TimerFactory.getTimer("ALX.ld2ud(ms)", UNIT.ms, threshold).addMilliSeconds(start, System.nanoTime());
            }

            if (!TimerFactory.isEmpty()) {
                log.warning("TimerFactory : statistics : " + TimerFactory.dumpTimers());
            }
            
            // force gc :
            for (int i = 0; i < 10; i++) {
                System.gc();
            }
        }
        TimerFactory.onExit();
    }

    /**
     * Test class
     */
    private ALXTimerTest()
    {
        //no-op
    }
}
