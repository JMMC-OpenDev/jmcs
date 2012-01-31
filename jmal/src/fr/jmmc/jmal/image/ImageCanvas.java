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
    private IndexColorModel colorModel_;
    /** wedge image */
    private transient Image wedge_ = null;
    /** main image */
    private transient BufferedImage image_ = null;
    /** observable image instance */
    private ObservableImage observe_;
    /** image width */
    private int w_;
    /** image height */
    private int h_;
    /** canvas width */
    private int canvasWidth_;
    /** canvas height */
    private int canvasHeight_;
    /** mouse x coordinate */
    private int mouseX_;
    /** mouse y coordinate */
    private int mouseY_;
    /** mouse pixel value */
    private int mousePixel_;
    /* data */
    /** float data array (1D) */
    private float[] data1D = null;
    /** float data array (2D) */
    private float[][] data2D = null;
    /** Minimum Float value */
    private float minValue_;
    /** Maximum Float value */
    private float maxValue_;
    /** float value to Pixel conversion factor */
    private float normalisePixelCoefficient_;

    /**
     * Creates a new instance of ImageCanvas
     */
    public ImageCanvas() {
        // set default properties
        colorModel_ = ColorModels.getDefaultColorModel();
        antiAliasing = true;

        setColorModel(colorModel_);
        w_ = 0;
        h_ = 0;
        canvasWidth_ = 600;
        canvasHeight_ = 600;

        observe_ = new ObservableImage();
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
            logger.debug("min = " + min + ", max = " + max);
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
            logger.debug("min = " + min + ", max = " + max);
        }
        logger.info("min = " + min + ", max = " + max);

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
        this.w_ = width;
        this.h_ = height;

        if (logger.isDebugEnabled()) {
            logger.debug("initImage: using array of size  " + width + "x" + height);
        }

        this.minValue_ = min;
        this.maxValue_ = max;
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
        observe_.addObserver(observer);
    }

    /** Change color model and repaint canvas */
    public void setColorModel(IndexColorModel cm) {
        colorModel_ = cm;

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
        mouseX_ = ((me.getX() - leftInset) * w_) / canvasWidth_;
        mouseY_ = ((me.getY() - topInset) * h_) / canvasHeight_;

        if ((mouseX_ >= 0) && (mouseY_ >= 0) && (mouseX_ < w_) && (mouseY_ < h_)) {
            if (image_ != null) {
                // first band = Red. => buggy with RGB rendering
                mousePixel_ = image_.getRaster().getSample(mouseX_, mouseY_, 0);
                observe_.setChanged();
            }
        }

        observe_.notifyObservers();
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
        final int wedgeSize = colorModel_.getMapSize();

        final int[] pixels = new int[wedgeSize];

        for (int i = 0, last = wedgeSize - 1; i <= last; i++) {
            pixels[i] = last - i;
        }

        // max first, min last:
        wedge_ = Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(1, wedgeSize, colorModel_, pixels, 0, 1));
    }

    /**
     * Convert the data to image using the current color model
     */
    private void buildImage() {
        // build image with RGB linear LUT interpolation :
        if (this.data1D != null) {
            this.normalisePixelCoefficient_ = ImageUtils.computeScalingFactor(this.minValue_, this.maxValue_, colorModel_.getMapSize());
            this.image_ = ImageUtils.createImage(this.w_, this.h_, this.data1D, this.minValue_, colorModel_, normalisePixelCoefficient_);

        } else if (this.data2D != null) {
            this.normalisePixelCoefficient_ = ImageUtils.computeScalingFactor(this.minValue_, this.maxValue_, colorModel_.getMapSize());
            this.image_ = ImageUtils.createImage(this.w_, this.h_, this.data2D, this.minValue_, colorModel_, normalisePixelCoefficient_);
        }
    }

    public Dimension getCanvasDimension() {
        Dimension canvasDim = new Dimension();
        canvasDim.setSize(canvasHeight_, canvasWidth_);

        return canvasDim;
    }

    public Dimension getImageDimension() {
        Dimension imageDim = new Dimension();
        imageDim.setSize(h_, w_);

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
        canvasWidth_ = (int) d.getWidth() - leftInset - rightInset - wedgeWidth
                - wedgeImageDist;
        canvasHeight_ = (int) d.getHeight() - topInset - bottomInset;

        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, (int) d.getWidth(), (int) d.getHeight());
        g2d.setColor(Color.BLACK);

        if ((canvasWidth_ > 0) && (canvasHeight_ > 0)) {
            if (image_ != null) {
                // draw image into rect
                g2d.drawImage(image_, leftInset + 1, topInset + 1, canvasWidth_, canvasHeight_, null);

                if (isDrawTicks()) {

                    final int step = 32;

                    // draw vertical tics
                    for (int i = 0; i < h_; i += step) {
                        int y = topInset + ((canvasHeight_ * i) / h_) + (canvasHeight_ / (2 * h_));
                        g2d.drawLine(leftInset - 2, y, leftInset, y);
                        g2d.drawString("" + i, leftInset - 20, y + 4);
                    }

                    // draw horizontal tics
                    for (int i = 0; i < w_; i += step) {
                        int x = leftInset + ((canvasWidth_ * i) / w_) + (canvasWidth_ / (2 * w_));
                        g2d.drawLine(x, topInset + canvasHeight_, x, topInset + canvasHeight_ + 3);
                        g2d.drawString("" + i, x - 4, topInset + canvasHeight_ + 15);
                    }
                }

                g2d.drawRect(leftInset, topInset, canvasWidth_ + 1, canvasHeight_ + 1);
            }

            if (wedge_ != null) {
                g2d.drawRect(leftInset + canvasWidth_ + wedgeImageDist, topInset + wedgeLegendDist, wedgeWidth + 1,
                        canvasHeight_ - wedgeLegendDist + 1);
                g2d.drawString(floatFormatter.format(maxValue_),
                        leftInset + canvasWidth_ + wedgeImageDist - 4, topInset + 6);
                g2d.drawString(floatFormatter.format(minValue_), leftInset + canvasWidth_ + wedgeImageDist - 4,
                        topInset + canvasHeight_ + 15);
                g2d.drawImage(wedge_, leftInset + canvasWidth_ + wedgeImageDist + 1, topInset + wedgeLegendDist + 1,
                        wedgeWidth, canvasHeight_ - wedgeLegendDist, null);
            }
        }

        observe_.notifyImageObservers();
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
        return mousePixel_;
    }

    public int getMouseX() {
        return mouseX_;
    }

    public int getMouseY() {
        return mouseY_;
    }

    public float getNormalisePixelCoefficient() {
        return normalisePixelCoefficient_;
    }

    public float getMinValue() {
        return minValue_;
    }

    public float getMaxValue() {
        return maxValue_;
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
