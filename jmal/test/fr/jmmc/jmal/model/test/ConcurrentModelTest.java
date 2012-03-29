/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.model.test;

import fr.jmmc.jmal.image.ColorModels;
import fr.jmmc.jmal.image.ColorScale;
import fr.jmmc.jmal.model.ModelUVMapService;
import fr.jmmc.jmal.model.ImageMode;
import fr.jmmc.jmal.model.targetmodel.Model;
import fr.jmmc.jmcs.App;
import fr.jmmc.jmcs.jaxb.JAXBFactory;
import fr.jmmc.jmcs.jaxb.XmlBindException;
import fr.jmmc.jmcs.util.FileUtils;
import fr.jmmc.jmcs.util.concurrent.ParallelJobExecutor;
import java.awt.geom.Rectangle2D;
import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import javax.xml.bind.JAXBException;
import org.ivoa.util.timer.TimerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Benchmark on ModelUVMapService.computeUVMap()
 *
 * @author bourgesl
 */
public class ConcurrentModelTest {

    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentModelTest.class.getName());
    /** package name for JAXB generated code */
    private final static String TM_JAXB_PATH = "fr.jmmc.jmal.model.targetmodel";
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

        final boolean testSlow = true;

        if (testSlow) {
            testConcurrency("fr/jmmc/jmal/model/test/modelSlow.xml");
        } else {
            testConcurrency("fr/jmmc/jmal/model/test/modelFast.xml");
        }

        System.exit(0);
    }

    /**
     * Use this test to have micro benchmarks
     *
     * @param fileName absolute file path containing target models
     */
    private static void testConcurrency(final String fileName) {

        final boolean testLargeScale = true;

        final int maxImageSize;
        final int minImageSize;

        if (testLargeScale) {
            maxImageSize = 2560;
            minImageSize = 256;
        } else {
            maxImageSize = 512;
            minImageSize = 64;
        }

        // prepare variables:
        final double uvMax = 100d;

        final Rectangle2D.Double uvRect = new Rectangle2D.Double();
        uvRect.setFrameFromDiagonal(-uvMax, -uvMax, uvMax, uvMax);

        ImageMode mode = ImageMode.AMP;

        final Model model = (Model) loadObject(fileName);

        final List<Model> models = model.getModels();

        logger.info("loaded models: {}", models);

        if (models.isEmpty()) {
            return;
        }

        final int nCpu = Runtime.getRuntime().availableProcessors();

        // WARMUP:
        performBenchmark(models, uvRect, mode, minImageSize, maxImageSize, nCpu, 5, "WARMUP");

        if (!TimerFactory.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Benchmark WARMUP statistics: {}", TimerFactory.dumpTimers());
            }
            TimerFactory.resetTimers();
        }

        // BENCHMARKS:
        final int PASS = 4;
        final int midPASS = PASS / 2;
        final int N = 20;

        for (int n = 0; n < PASS; n++) {
            logger.info("starting Pass[{}]", (n + 1));

            // repeat test PASS x N times :
            performBenchmark(models, uvRect, mode, minImageSize, maxImageSize, nCpu, N, "NORMAL");

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
            TimerFactory.resetTimers();
        }
        TimerFactory.onExit();
    }

    /**
     * Perform benchmark
     *
     * @param models list of models
     * @param uvRect UV frequency area in rad-1
     * @param mode image mode (amplitude or phase)
     * @param minImageSize minimum image size
     * @param maxImageSize maximum image size
     * @param nCpu number of CPU
     * @param N number of tests to perform
     * @param benchType benchmark type
     */
    private static void performBenchmark(final List<Model> models, final Rectangle2D.Double uvRect, final ImageMode mode,
                                         final int minImageSize, final int maxImageSize,
                                         final int nCpu, final int N, final String benchType) {

        final int modelSize = models.size();

        /** disable threshold timers */
        final double threshold = 0d;
        long start;
        String key;

        int imageSize, nJob;

        for (imageSize = maxImageSize; imageSize >= minImageSize; imageSize -= (imageSize > 1024) ? 2 * minImageSize : minImageSize) {

            for (nJob = nCpu; nJob >= 1; nJob /= 2) {

                key = "compute[" + imageSize + "/" + nJob + "][" + (imageSize * imageSize * modelSize) + "]";

                logger.info("starting {} benchmark[{}]", benchType, key);

                // before each pass:
                cleanup();

                for (int i = 0; i < N; i++) {

                    start = System.nanoTime();

                    // test code:
                    computeModel(nJob, models, uvRect, mode, imageSize);

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
     * @param models list of models
     * @param uvRect UV frequency area in rad-1
     * @param mode image mode (amplitude or phase)
     * @param imageSize number of pixels for both width and height of the generated image
     */
    private static void computeModel(final int maxParallelJob,
                                     final List<Model> models, final Rectangle2D.Double uvRect, final ImageMode mode, final int imageSize) {

        ParallelJobExecutor.getInstance().setMaxParallelJob(maxParallelJob);

        ModelUVMapService.computeUVMap(models, uvRect, mode, imageSize, colorModel, ColorScale.LINEAR);
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
    private ConcurrentModelTest() {
        //no-op
    }

    /**
     * Protected load method used by ConfigurationManager.initialize to load the aspro configuration files
     *
     * @param uri relative URI of the document to load (class loader)
     * @return unmarshalled object
     *
     * @throws IllegalStateException if the file is not found or an I/O exception occured
     * @throws IllegalArgumentException if the load operation failed
     * @throws XmlBindException if a JAXBException was caught while creating an unmarshaller
     */
    private static Object loadObject(final String uri)
            throws IllegalStateException, IllegalArgumentException, XmlBindException {

        logger.info("loading file: {}", uri);

        Object result = null;

        try {
            final JAXBFactory jf = JAXBFactory.getInstance(TM_JAXB_PATH);

            // use the class loader resource resolver
            final URL url = FileUtils.getResource(uri);

            if (logger.isInfoEnabled()) {
                logger.info("loading url: {}", url);
            }

            // Note : use input stream to avoid JNLP offline bug with URL (Unknown host exception)
            result = jf.createUnMarshaller().unmarshal(new BufferedInputStream(url.openStream()));

        } catch (IOException ioe) {
            throw new IllegalStateException("Load failure on " + uri, ioe);
        } catch (JAXBException je) {
            throw new IllegalArgumentException("Load failure on " + uri, je);
        }

        return result;
    }
}
