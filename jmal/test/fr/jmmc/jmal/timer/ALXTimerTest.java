/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.mcs.timer;

import fr.jmmc.jmal.ALX;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ivoa.util.timer.TimerFactory.UNIT;

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

        // Fastest : M5
        // slowest : O5
//        testProfiler("M5");

        testTimers("O5");
    }

    /**
     * Use this test with Netbeans Profiler enabled
     * @param sptype spectral type
     */
    private static void testProfiler(final String sptype)
    {

        final int N = 50000;

        for (int n = 0; n < 10; n++) {
            // repeat tests n times :

            for (int i = 0; i < N; i++) {

                // do something : use ALX
                try {
                    ALX.ld2ud(1d, sptype);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "test fail", e);
                }

            }

            for (int i = 0; i < N; i++) {

                // do something : use ALX
                try {
                    ALX.ld2ud(1d, sptype);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "test fail", e);
                }

            }

            // pause for 10 ms :
            try {
                Thread.sleep(10l);
            } catch (InterruptedException ex) {
                log.log(Level.SEVERE, "interrupted", ex);
            }
        }
    }

    /**
     * Use this test to have micro benchmarks
     * @param sptype spectral type
     */
    private static void testTimers(final String sptype)
    {

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
                    ALX.ld2ud(1d, sptype);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "test fail", e);
                }

                TimerFactory.getSimpleTimer("ALX.ld2ud(ns)", UNIT.ns).addNanoSeconds(start, System.nanoTime());
            }

            for (int i = 0; i < N; i++) {

                start = System.nanoTime();

                // do something : use ALX
                try {
                    ALX.ld2ud(1d, sptype);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "test fail", e);
                }

                TimerFactory.getTimer("ALX.ld2ud(ms)", UNIT.ms, threshold).addMilliSeconds(start, System.nanoTime());
            }

            if (!TimerFactory.isEmpty()) {
                log.warning("TimerFactory : statistics : " + TimerFactory.dumpTimers());
            }

            // pause for 10 ms :
            try {
                Thread.sleep(10l);
            } catch (InterruptedException ex) {
                log.log(Level.SEVERE, "interrupted", ex);
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
