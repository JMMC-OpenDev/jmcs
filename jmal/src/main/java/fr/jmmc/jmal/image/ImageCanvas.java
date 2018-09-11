/** *****************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ***************************************************************************** */
package fr.jmmc.jmal.image;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Image Canvas
 *
 * @author Laurent BOURGES.
 */
public class ImageCanvas extends Canvas implements MouseMotionListener {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(ImageCanvas.class.getName());
    /** float value formatter used by wedge rendering */
    public static final NumberFormat floatFormatter = new DecimalFormat("0.00E0");
    // Define constant to place different members of plot
    /** left inset */
    private static final int leftInset = 40;
    /** right inset */
    private static final int rightInset = 40;
    /** top inset */
    private static final int topInset = 40;
    /** bottom inset */
    private static final int bottomInset = 40;
    /** width of the wedge */
    private static final int wedgeWidth = 10;
    /** distance between wedge and image */
    private static final int wedgeImageDist = 10;
    /** distance between wedge and its legend */
    private static final int wedgeLegendDist = 10;
    /* members */
    /** antialiasing flag */
    private boolean antiAliasing;
    /** draw ticks flag */
    private boolean drawTicks = true;
    /** color model used by image */
    private IndexColorModel colorModel;
    /** wedge image */
    private transient Image wedge = null;
    /** main image */
    private transient BufferedImage image = null;
    /** observable image instance */
    private ObservableImage observe;
    /** image width */
    private int w;
    /** image height */
    private int h;
    /** canvas width */
    private int canvasWidth;
    /** canvas height */
    private int canvasHeight;
    /** mouse x coordinate */
    private int mouseX;
    /** mouse y coordinate */
    private int mouseY;
    /** mouse pixel value */
    private int mousePixel;
    /* data */
    /** float data array (1D) */
    private float[] data1D = null;
    /** float data array (2D) */
    private float[][] data2D = null;
    /** Minimum Float value */
    private float minValue;
    /** Maximum Float value */
    private float maxValue;
    /** float value to Pixel conversion factor */
    private float normalisePixelCoefficient;

    /**
     * Creates a new instance of ImageCanvas
     */
    public ImageCanvas() {
        // set default properties
        colorModel = ColorModels.getDefaultColorModel();
        antiAliasing = true;

        setColorModel(colorModel);
        w = 0;
        h = 0;
        canvasWidth = 600;
        canvasHeight = 600;

        observe = new ObservableImage();
        this.addMouseMotionListener(this);
    }

    /**
     * Initialize the image with given data
     *
     * @param width image width
     * @param height image height
     * @param array image data
     */
    public void initImage(int width, int height, float[] array) {

        // search min and max of input array
        final int size = array.length;

        float min = array[0];
        float max = array[0];

        float val;
        for (int i = 0; i < size; i++) {
            val = array[i];

            if (min > val) {
                min = val;
            }

            if (max < val) {
                max = val;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("min = {}, max = {}", min, max);
        }

        initImage(width, height, array, min, max);
    }

    /**
     * Initialize the image with given data
     *
     * @param width image width
     * @param height image height
     * @param array image data
     */
    public void initImage(int width, int height, float[][] array) {

        // search min and max of input array
        float min = array[0][0];
        float max = array[0][0];

        float val;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                val = array[j][i];

                if (min > val) {
                    min = val;
                }

                if (max < val) {
                    max = val;
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("min = {}, max = {}", min, max);
        }

        initImage(width, height, array, min, max);
    }

    /**
     * Initialize the image with given data
     *
     * @param width image width
     * @param height image height
     * @param array1D image data
     * @param min minimum value
     * @param max maximum value
     */
    public void initImage(int width, int height, float[] array1D, final float min, final float max) {
        initImage(width, height, array1D, null, min, max);
    }

    /**
     * Initialize the image with given data
     *
     * @param width image width
     * @param height image height
     * @param array2D image data
     * @param min minimum value
     * @param max maximum value
     */
    private void initImage(int width, int height, float[][] array2D, final float min, final float max) {
        initImage(width, height, null, array2D, min, max);
    }

    /**
     * Initialize the image with given data
     *
     * @param width image width
     * @param height image height
     * @param array1D image data
     * @param array2D image data
     * @param min minimum value
     * @param max maximum value
     */
    private void initImage(int width, int height, float[] array1D, float[][] array2D, final float min, final float max) {
        this.w = width;
        this.h = height;

        if (logger.isDebugEnabled()) {
            logger.debug("initImage: using array of size {} x {}", width, height);
        }

        this.minValue = min;
        this.maxValue = max;
        this.data1D = array1D;
        this.data2D = array2D;

        // rebuild image :
        buildImage();

        // set new canvas dimension
        final Dimension d = new Dimension(width, height);
        setMinimumSize(d);
        setPreferredSize(d);
    }

    /**
     * Add an observer
     *
     * @param observer observer to add
     */
    public void addObserver(final Observer observer) {
        observe.addObserver(observer);
    }

    /** Change color model and repaint canvas */
    public void setColorModel(IndexColorModel cm) {
        colorModel = cm;

        buildWedge();
        buildImage();

        repaint();
    }

    /**
     * MouseMotionListener implementation
     *
     * @param me mouse event
     */
    @Override
    public void mouseMoved(MouseEvent me) {
        mouseX = ((me.getX() - leftInset) * w) / canvasWidth;
        mouseY = ((me.getY() - topInset) * h) / canvasHeight;

        if ((mouseX >= 0) && (mouseY >= 0) && (mouseX < w) && (mouseY < h)) {
            if (image != null) {
                // first band = Red. => buggy with RGB rendering
                mousePixel = image.getRaster().getSample(mouseX, mouseY, 0);
                observe.setChanged();
            }
        }

        observe.notifyObservers();
    }

    /**
     * MouseMotionListener implementation
     *
     * @param me mouse event
     */
    @Override
    public void mouseDragged(MouseEvent me) {
    }

    private void buildWedge() {
        final int wedgeSize = colorModel.getMapSize();

        final int[] pixels = new int[wedgeSize];

        for (int i = 0, last = wedgeSize - 1; i <= last; i++) {
            pixels[i] = last - i;
        }

        // max first, min last:
        wedge = Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(1, wedgeSize, colorModel, pixels, 0, 1));
    }

    /**
     * Convert the data to image using the current color model
     */
    private void buildImage() {
        // build image with RGB linear LUT interpolation :
        if (this.data1D != null) {
            this.normalisePixelCoefficient = ImageUtils.computeScalingFactor(this.minValue, this.maxValue, colorModel.getMapSize());
            this.image = ImageUtils.createImage(this.w, this.h, this.data1D, this.minValue, colorModel, normalisePixelCoefficient);

        } else if (this.data2D != null) {
            this.normalisePixelCoefficient = ImageUtils.computeScalingFactor(this.minValue, this.maxValue, colorModel.getMapSize());
            this.image = ImageUtils.createImage(this.w, this.h, this.data2D, this.minValue, colorModel, normalisePixelCoefficient);
        }
    }

    public Dimension getCanvasDimension() {
        Dimension canvasDim = new Dimension();
        canvasDim.setSize(canvasHeight, canvasWidth);

        return canvasDim;
    }

    public Dimension getImageDimension() {
        Dimension imageDim = new Dimension();
        imageDim.setSize(h, w);

        return imageDim;
    }

    /**
     * Paint this component
     *
     * @param g graphics 2D object
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // set antiAliasing
        if (antiAliasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // set quality flags:
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

            // Use bicubic interpolation (slower) for quality:
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        Dimension d = new Dimension(this.getWidth(), this.getHeight());
        canvasWidth = (int) d.getWidth() - leftInset - rightInset - wedgeWidth
                - wedgeImageDist;
        canvasHeight = (int) d.getHeight() - topInset - bottomInset;

        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
        g2d.setColor(Color.BLACK);

        if ((canvasWidth > 0) && (canvasHeight > 0)) {
            if (image != null) {
                // draw image into rect
                g2d.drawImage(image, leftInset + 1, topInset + 1, canvasWidth, canvasHeight, null);

                if (isDrawTicks()) {
                    final int step = 5;

                    // draw vertical tics
                    for (int i = 0; i < h; i += step) {
                        int y = topInset + ((canvasHeight * i) / h) + (canvasHeight / (2 * h));
                        g2d.drawLine(leftInset - 2, y, leftInset, y);
                        g2d.drawString(Integer.toString(i), leftInset - 20, y + 4);
                    }

                    // draw horizontal tics
                    for (int i = 0; i < w; i += step) {
                        int x = leftInset + ((canvasWidth * i) / w) + (canvasWidth / (2 * w));
                        g2d.drawLine(x, topInset + canvasHeight, x, topInset + canvasHeight + 3);
                        g2d.drawString(Integer.toString(i), x - 4, topInset + canvasHeight + 15);
                    }
                }

                g2d.drawRect(leftInset, topInset, canvasWidth + 1, canvasHeight + 1);
            }

            if (wedge != null) {
                g2d.drawRect(leftInset + canvasWidth + wedgeImageDist, topInset + wedgeLegendDist, wedgeWidth + 1,
                        canvasHeight - wedgeLegendDist + 1);
                g2d.drawString(floatFormatter.format(maxValue),
                        leftInset + canvasWidth + wedgeImageDist - 4, topInset + 6);
                g2d.drawString(floatFormatter.format(minValue), leftInset + canvasWidth + wedgeImageDist - 4,
                        topInset + canvasHeight + 15);
                g2d.drawImage(wedge, leftInset + canvasWidth + wedgeImageDist + 1, topInset + wedgeLegendDist + 1,
                        wedgeWidth, canvasHeight - wedgeLegendDist, null);
            }
        }

        observe.notifyImageObservers();
    }

    @Override
    public Dimension getPreferredSize() {
        return this.getCanvasDimension();
    }

    @Override
    public Dimension getMinimumSize() {
        return this.getCanvasDimension();
    }

    @Override
    public Dimension getMaximumSize() {
        return this.getCanvasDimension();
    }

    private static class ObservableImage extends Observable {

        public void notifyImageObservers() {
            setChanged();
            notifyObservers();
        }

        @Override
        public void setChanged() {
            super.setChanged();
        }
    }

    public int getMousePixel() {
        return mousePixel;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public float getNormalisePixelCoefficient() {
        return normalisePixelCoefficient;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public boolean isAntiAliasing() {
        return antiAliasing;
    }

    public void setAntiAliasing(final boolean antiAliasing) {
        this.antiAliasing = antiAliasing;
    }

    public boolean isDrawTicks() {
        return drawTicks;
    }

    public void setDrawTicks(final boolean drawTicks) {
        this.drawTicks = drawTicks;
    }
}
