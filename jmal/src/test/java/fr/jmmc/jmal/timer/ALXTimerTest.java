/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.timer;

import fr.jmmc.jmal.SpTypeUtils;
import java.util.Locale;
import fr.jmmc.jmcs.util.timer.TimerFactory;
import fr.jmmc.jmcs.util.timer.TimerFactory.UNIT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple timer tests of SpTypeUtils class
 * @author bourgesl
 */
public class ALXTimerTest {

    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(ALXTimerTest.class.getName());

    /**
     * MicroBenchmarks of SpTypeUtils.ld2ud()
     * @param args
     */
    public static void main(String[] args) {
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
    private static void testProfiler(final String sptype) {

        final int N = 50000;

        for (int n = 0; n < 10; n++) {
            // repeat tests n times :

            for (int i = 0; i < N; i++) {

                // do something : use SpTypeUtils
                try {
                    SpTypeUtils.ld2ud(1d, sptype);
                } catch (Exception e) {
                    logger.error("test fail", e);
                }

            }

            for (int i = 0; i < N; i++) {

                // do something : use SpTypeUtils
                try {
                    SpTypeUtils.ld2ud(1d, sptype);
                } catch (Exception e) {
                    logger.error("test fail", e);
                }

            }

            // pause for 10 ms :
            try {
                Thread.sleep(10l);
            } catch (InterruptedException ex) {
                logger.info("interrupted", ex);
            }
        }
    }

    /**
     * Use this test to have micro benchmarks
     * @param sptype spectral type
     */
    private static void testTimers(final String sptype) {

        /** SpTypeUtils ld2ud - threshold = 0.5 ms */
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

                // do something : use SpTypeUtils
                try {
                    SpTypeUtils.ld2ud(1d, sptype);
                } catch (Exception e) {
                    logger.error("test fail", e);
                }

                TimerFactory.getSimpleTimer("SpTypeUtils.ld2ud(ns)", UNIT.ns).addNanoSeconds(start, System.nanoTime());
            }

            for (int i = 0; i < N; i++) {

                start = System.nanoTime();

                // do something : use SpTypeUtils
                try {
                    SpTypeUtils.ld2ud(1d, sptype);
                } catch (Exception e) {
                    logger.error("test fail", e);
                }

                TimerFactory.getTimer("SpTypeUtils.ld2ud(ms)", UNIT.ms, threshold).addMilliSeconds(start, System.nanoTime());
            }

            if (!TimerFactory.isEmpty()) {
                logger.info("TimerFactory statistics: {}", TimerFactory.dumpTimers());
            }

            // pause for 10 ms :
            try {
                Thread.sleep(10l);
            } catch (InterruptedException ex) {
                logger.error("interrupted", ex);
            }
        }
        TimerFactory.onExit();
    }

    /**
     * Test class
     */
    private ALXTimerTest() {
        //no-op
    }
}
