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
import java.awt.image.WritableRaster;

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
    /**
     * DOCUMENT ME!
     */
    private static final Logger logger = LoggerFactory.getLogger(ImageCanvas.class.getName());
    /** float value formatter used by wedge rendering */
    public static final NumberFormat floatFormatter = new DecimalFormat("0.00E0");
    // Define constant to place differnet members of plot
    /**
     * DOCUMENT ME!
     */
    private static final int leftInset = 20;
    /**
     * DOCUMENT ME!
     */
    private static final int rightInset = 40;
    /**
     * DOCUMENT ME!
     */
    private static final int topInset = 10;
    /**
     * DOCUMENT ME!
     */
    private static final int bottomInset = 25;
    /**
     * DOCUMENT ME!
     */
    private static final int wedgeWidth = 10;
    /**
     * DOCUMENT ME!
     */
    private static final int wedgeImageDist = 10;
    /**
     * DOCUMENT ME!
     */
    private static final int wedgeLegendDist = 10;
    /** float data array */
    private float[] dataArray;
    /**
     * DOCUMENT ME!
     */
    private transient Image image_;
    /**
     * DOCUMENT ME!
     */
    private transient WritableRaster imageRaster_;
    /**
     * DOCUMENT ME!
     */
    private transient Image wedge_;
    /**
     * DOCUMENT ME!
     */
    private IndexColorModel colorModel_;
    /**
     * DOCUMENT ME!
     */
    private int w_;
    /**
     * DOCUMENT ME!
     */
    private int h_;
    /**
     * DOCUMENT ME!
     */
    private int canvasWidth_;
    /**
     * DOCUMENT ME!
     */
    private int canvasHeight_;
    /**
     * DOCUMENT ME!
     */
    private int mouseX_;
    /**
     * DOCUMENT ME!
     */
    private int mouseY_;
    /**
     * DOCUMENT ME!
     */
    private int mousePixel_;
    /**
     * Minimum Float value
     */
    private float minValue_;
    /**
     * Maximum Float value
     */
    private float maxValue_;
    /**
     * Float value to Pixel conversion factor
     */
    private float normalisePixelCoefficient_;
    /**
     * DOCUMENT ME!
     */
    private boolean antiAliasing = false;
    /** draw ticks flag */
    private boolean drawTicks = true;
    /**
     * DOCUMENT ME!
     */
    private ObservableImage observe_;

    /**
     * Creates a new instance of ImageCanvas
     */
    public ImageCanvas() {
        image_ = null;
        imageRaster_ = null;
        // set default properties
        colorModel_ = ColorModels.getDefaultColorModel();
        antiAliasing = false;

        setColorModel(colorModel_);
        w_ = 0;
        h_ = 0;
        canvasWidth_ = 500;
        canvasHeight_ = 500;

        observe_ = new ObservableImage();
        this.addMouseMotionListener(this);
    }

    public boolean isAntiAliasing() {
        return antiAliasing;
    }

    public void setAntiAliasing(boolean antiAliasing) {
        this.antiAliasing = antiAliasing;
    }

    public boolean isDrawTicks() {
        return drawTicks;
    }

    public void setDrawTicks(boolean drawTicks) {
        this.drawTicks = drawTicks;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseMoved(MouseEvent e) {
        mouseX_ = ((e.getX() - leftInset) * w_) / canvasWidth_;
        mouseY_ = ((e.getY() - topInset) * h_) / canvasHeight_;

        if ((mouseX_ >= 0) && (mouseY_ >= 0) && (mouseX_ < w_) && (mouseY_ < h_)) {
            // first band = Red. => buggy with RGB rendering
            mousePixel_ = imageRaster_.getSample(mouseX_, mouseY_, 0);
            observe_.setChanged();
        }

        observe_.notifyObservers();
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param observer DOCUMENT ME!
     */
    public void addObserver(Observer observer) {
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
     * DOCUMENT ME!
     */
    private void buildWedge() {
        int wedgeSize = colorModel_.getMapSize();
        wedge_ = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(1, wedgeSize, colorModel_,
                generateWedge(0, wedgeSize, wedgeSize), 0, 1));
    }

    /**
     * DOCUMENT ME!
     *
     * @param w DOCUMENT ME!
     * @param h DOCUMENT ME!
     * @param array DOCUMENT ME!
     */
    public void initImage(int width, int height, float[] array) {

        // search min and max of input array

        final int size = array.length;

        float aMin = array[0];
        float aMax = array[0];

        for (int i = 1; i < size; i++) {
            float a = array[i];

            if (aMin > a) {
                aMin = a;
            }

            if (aMax < a) {
                aMax = a;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("min = " + aMin + ", max = " + aMax);
        }

        initImage(width, height, array, aMin, aMax);
    }

    /**
     * DOCUMENT ME!
     *
     * @param w DOCUMENT ME!
     * @param h DOCUMENT ME!
     * @param array DOCUMENT ME!
     */
    public void initImage(int width, int height, float[] array, final float min, final float max) {
        this.w_ = width;
        this.h_ = height;

        if (logger.isDebugEnabled()) {
            logger.debug("initImage: using array of size  " + width + "x" + height
                    + " array : nb of point is " + array.length);
        }

        this.minValue_ = min;
        this.maxValue_ = max;
        this.dataArray = array;

        // rebuild image :
        buildImage();

        // set new canvas dimension
        final Dimension d = new Dimension(width, height);
        setMinimumSize(d);
        setPreferredSize(d);
    }

    /**
     * DOCUMENT ME!
     */
    private void buildImage() {
        // build image with RGB linear LUT interpolation :
        if (this.dataArray != null) {
            this.normalisePixelCoefficient_ = ImageUtils.computeScalingFactor(this.minValue_, this.maxValue_, colorModel_.getMapSize());

            final BufferedImage bi = ImageUtils.createImage(this.w_, this.h_, this.dataArray, this.minValue_, colorModel_, normalisePixelCoefficient_);

            this.imageRaster_ = bi.getRaster();

            this.image_ = bi;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param min DOCUMENT ME!
     * @param max DOCUMENT ME!
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private int[] generateWedge(int min, int max, int size) {
        int[] pixels = new int[size];

        for (int i = 0; i < size; i++) {
            pixels[size - i - 1] = ((i * (max - min)) / size);
        }

        return pixels;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getCanvasDimension() {
        Dimension canvasDim = new Dimension();
        canvasDim.setSize(canvasHeight_, canvasWidth_);

        return canvasDim;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getImageDimension() {
        Dimension imageDim = new Dimension();
        imageDim.setSize(h_, w_);

        return imageDim;
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // set antiAliasing
        if (antiAliasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

                    // draw vertical tics
                    for (int i = 0; i < h_; i++) {
                        int y = topInset + ((canvasHeight_ * i) / h_) + (canvasHeight_ / (2 * h_));
                        g2d.drawLine(leftInset - 2, y, leftInset, y);
                        g2d.drawString("" + i, leftInset - 20, y + 4);
                    }

                    // draw horizontal tics
                    for (int i = 0; i < w_; i++) {
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

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Override
    public Dimension getPreferredSize() {
        return this.getCanvasDimension();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Override
    public Dimension getMinimumSize() {
        return this.getCanvasDimension();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
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
}
