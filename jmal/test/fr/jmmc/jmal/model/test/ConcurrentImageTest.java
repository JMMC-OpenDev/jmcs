/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model.test;

import fr.jmmc.jmal.image.ColorModels;
import fr.jmmc.jmal.image.ImageUtils;
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import java.awt.image.IndexColorModel;
import java.util.Locale;
import org.ivoa.util.timer.TimerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Benchmark on ImageUtils.createImage()
 *
 * @author bourgesl
 */
public class ConcurrentImageTest {

    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentImageTest.class.getName());
    /** image color model */
    private final static IndexColorModel colorModel = ColorModels.getColorModel("isophot");

    /**
     * MicroBenchmarks of ModelUVMapService.computeUVMap()
     *
     * @param args
     */
    public static void main(String[] args) {

        // invoke App method to initialize logback now:
        App.isReady();

        logger.warn("\n\nPlease check that the CPU is running at full speed (avoid frequency changes on demand).\n");

        logger.warn("\n\nPlease check that the program is run with following JVM options:\n-Xms384m -Xmx384m (-XX:CompileThreshold=100 -XX:+PrintCompilation)\n");

        // Set the default locale to en-US locale (for Numerical Fields "." ",")
        Locale.setDefault(Locale.US);

        // timer warmup:
        TimerFactory.resetTimers();

        testConcurrency();

        System.exit(0);
    }

    /**
     * Use this test to have micro benchmarks
     */
    private static void testConcurrency() {

        final boolean testLargeScale = false;

        final int maxImageSize;
        final int minImageSize;

        if (testLargeScale) {
            maxImageSize = 2560;
            minImageSize = 256;
        } else {
            maxImageSize = 512;
            minImageSize = 64;
        }

        // gc:
        cleanup();

        final int nCpu = Runtime.getRuntime().availableProcessors();

        // WARMUP:
        performBenchmark(minImageSize, maxImageSize, nCpu, 5, "WARMUP");

        if (!TimerFactory.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Benchmark WARMUP statistics: {}", TimerFactory.dumpTimers());
            }
        }

        TimerFactory.resetTimers();

        // BENCHMARKS:
        final int PASS = 4;
        final int midPASS = PASS / 2;
        final int N = 20;

        for (int n = 0; n < PASS; n++) {
            logger.info("starting Pass[{}]", n);

            // repeat test PASS x N times :
            performBenchmark(minImageSize, maxImageSize, nCpu, N, "NORMAL");

            if (n < midPASS && !TimerFactory.isEmpty()) {
                if (logger.isInfoEnabled()) {
                    logger.info("Benchmark Pass[{}] statistics: {}", (n + 1), TimerFactory.dumpTimers());
                }
                TimerFactory.resetTimers();
            }
        }
        if (!TimerFactory.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Benchmark FINAL statistics: {}", TimerFactory.dumpTimers());
            }
        }
        TimerFactory.onExit();
    }

    /**
     * Perform benchmark
     *
     * @param minImageSize minimum image size
     * @param maxImageSize maximum image size
     * @param nCpu number of CPU
     * @param N number of tests to perform
     * @param benchType benchmark type
     */
    private static void performBenchmark(final int minImageSize, final int maxImageSize,
                                         final int nCpu, final int N, final String benchType) {

        float[][] array;

        /** disable threshold timers */
        final double threshold = 0d;
        long start;
        String key;

        int imageSize, nJob;

        for (imageSize = maxImageSize; imageSize >= minImageSize; imageSize -= (imageSize > 1024) ? 2 * minImageSize : minImageSize) {

            array = createArray(imageSize);

            for (nJob = nCpu; nJob >= 1; nJob /= 2) {

                key = "compute[" + imageSize + "/" + nJob + "][" + (imageSize * imageSize) + "]";

                logger.info("starting {} benchmark[{}]", benchType, key);

                // before each pass:
                cleanup();

                for (int i = 0; i < N; i++) {

                    start = System.nanoTime();

                    // test code:
                    computeImage(nJob, imageSize, array);

                    TimerFactory.getTimer(key, TimerFactory.UNIT.ms, threshold).addMilliSeconds(start, System.nanoTime());

                    // after each test:
                    cleanup();
                }
            }
        }

    }

    /**
     * Compute the image using given parameters
     *
     * @param maxParallelJob maximum number or parallel tasks (>= 1)
     * @param size image size (width = height = size)
     * @param array image data array (2D)
     */
    private static void computeImage(final int maxParallelJob, final int size, final float[][] array) {

        ParallelJobExecutor.getInstance().setMaxParallelJob(maxParallelJob);

        ImageUtils.createImage(size, size, array, 0f, 1f, colorModel);
    }

    /**
     * Cleanup (GC + 50ms pause)
     */
    private static void cleanup() {
        // Perform GC:
        System.gc();

        // pause for 50 ms :
        try {
            Thread.sleep(100l);
        } catch (InterruptedException ex) {
            logger.info("thread interrupted", ex);
        }
    }

    /**
     * Test class
     */
    private ConcurrentImageTest() {
        //no-op
    }

    /**
     * Create the image data
     *
     * @param size image size (width = height = size)
     * @return float[size][size] filled with values between 0.0 and 1.0
     */
    private static float[][] createArray(final int size) {
        float[][] array = new float[size][size];

        final float step = 0.5f / size;

        for (int i, j = 0; j < size; j++) {
            for (i = 0; i < size; i++) {
                array[i][j] = step * (i + j);
            }
        }
        return array;
    }
}
