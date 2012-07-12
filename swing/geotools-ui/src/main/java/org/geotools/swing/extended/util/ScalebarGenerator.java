/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.extended.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;

/**
 * This utility generates an image presenting a scalebar of the map based in the give scale.<br/> It
 * can be customized to apply different colors for segments (even, uneven) height, margins, text
 * colors. <br/> The scalebar switches as needed to measure from mm up to km depending in the scale
 * and required width.
 *
 * @author Elton Manoku
 */
public class ScalebarGenerator {

    private final static String TEMPORARY_IMAGE_FILE_LOCATION =
            System.getProperty("user.home") + File.separator + "sola";
    private final static String TEMPORARY_IMAGE_FILE = "scalebar";
    private final static String IMAGE_FORMAT = "png";

    private enum segmentMeasureUnitType {

        m,
        km,
        cm,
        mm
    }
    private int height = 40;
    private int numberOfSegments = 3;
    private int lrbMargin = 15;
    private int topMargin = 15;
    private Color colorBorderSegment = Color.DARK_GRAY;
    private Color colorSegmentEven = Color.BLACK;
    private Color colorSegmentUneven = Color.BLUE;
    private Color colorText = Color.RED;
    private Font textFont = new Font(Font.SANS_SERIF, Font.BOLD, 10);
    private segmentMeasureUnitType segmentMeasureUnit = segmentMeasureUnitType.m;
    private boolean drawScaleText = true;

    /**
     * Constructor
     */
    public ScalebarGenerator() {
    }

    /**
     * A color for the border of the segment.
     *
     * @param colorBorderSegment
     */
    public void setColorBorderSegment(Color colorBorderSegment) {
        this.colorBorderSegment = colorBorderSegment;
    }

    /**
     * Set color for the even segments.
     *
     * @param colorSegmentEven
     */
    public void setColorSegmentEven(Color colorSegmentEven) {
        this.colorSegmentEven = colorSegmentEven;
    }

    /**
     * Set color for the uneven segments.
     *
     * @param colorSegmentUneven
     */
    public void setColorSegmentUneven(Color colorSegmentUneven) {
        this.colorSegmentUneven = colorSegmentUneven;
    }

    /**
     * Sets color for the texts used.
     *
     * @param colorText
     */
    public void setColorText(Color colorText) {
        this.colorText = colorText;
    }

    /**
     * Controls whether the scale is displayed on top of the scale bar or not
     *
     * @param drawScaleText If false, the scale text will not be displayed.
     */
    public void setDrawScaleText(boolean drawScaleText) {
        this.drawScaleText = drawScaleText;
    }

    /**
     * Sets the height of the entire image. The height of the scale bar is calculated from height -
     * (lrbMargin + topMargin). The default image height is 40. The default scale bar height is 10.
     *
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the margin for the left, right and bottom of the control. The margin is the distance
     * from the edge of the image to the scale bar. This margin must be big enough to allow display
     * of the distance text. The default value is 15.
     *
     * @param margin The size of the margin to set around the left, right and bottom of the scalebar
     * image.
     */
    public void setLrbMargin(int lrbMargin) {
        this.lrbMargin = lrbMargin;
    }

    /**
     * Sets the size of the margin above the scale bar. Default value is 15. This value can be reset
     * to 2 if the scale text is not displayed.
     *
     * @param topMargin The size of the margin above the scale bar.
     */
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    /**
     * Sets the number of segments to be displayed in the scalebar.
     *
     * @param numberOfSegments
     */
    public void setNumberOfSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
    }

    /**
     * Sets the font of the texts used.
     *
     * @param textFont
     */
    public void setTextFont(Font textFont) {
        this.textFont = textFont;
    }

    /**
     * Generate the image of the scalebar. If the scale is small (e.g. &lt; 0.01), the scale bar
     * will not be generated.
     *
     * Note that due to different screen resolutions, generating a 100% accurate scale bar for on
     * screen display is next to impossible. The scale should be used as indicative only.
     *
     * @param scale The scale for which to generate the image
     * @param width The initial width of the scalebar in points. There are approx 2.83 points per
     * mm. The real width will change to round the measurement of the segments.
     * @param dpi DPI used on the display device. e.g. for a standard printout, 72 is suitable. For
     * screen display, 96 is better.
     * @return
     */
    public BufferedImage getImage(Double scale, double width, double dpi) {

        double extentWidth = 2.54 * scale / 100;
        double screenWidth = dpi;
        double widthInMeters = extentWidth * width / screenWidth;
        if (widthInMeters < 0.001) {
            // Scale is too small to draw a reasonable scalebar. 
            return null;
        }

        // Uses the widthInMeters to determine the appropriate unit of measure and rounding
        // factor to use for the scale bar
        double rounding = 1;
        this.segmentMeasureUnit = segmentMeasureUnitType.m;
        if (widthInMeters > 1000) {
            rounding = 500;
            this.segmentMeasureUnit = segmentMeasureUnitType.km;
        } else if (widthInMeters > 100) {
            rounding = 50;
        } else if (widthInMeters > 10) {
            rounding = 5;
        } else if (widthInMeters < 0.005) {
            rounding = 0.0005;
            this.segmentMeasureUnit = segmentMeasureUnitType.mm;
        } else if (widthInMeters < 0.05) {
            rounding = 0.005;
            this.segmentMeasureUnit = segmentMeasureUnitType.mm;
        } else if (widthInMeters < 0.5) {
            rounding = 0.05;
            this.segmentMeasureUnit = segmentMeasureUnitType.cm;
        } else if (widthInMeters < 1) {
            rounding = 0.5;
            this.segmentMeasureUnit = segmentMeasureUnitType.cm;
        }
        double segmentInMeters =
                (double) Math.ceil((widthInMeters / this.numberOfSegments) / rounding) * rounding;

        double finalWidthInMeters = segmentInMeters * this.numberOfSegments;
        int scalebarDrawingWidth = (int) Math.round(finalWidthInMeters * screenWidth / extentWidth);
        int segmentDrawingWidth = scalebarDrawingWidth / this.numberOfSegments;
        int finalScalebarWidth = scalebarDrawingWidth + (this.lrbMargin * 2);

        BufferedImage bi = new BufferedImage(
                finalScalebarWidth, height, BufferedImage.TYPE_INT_ARGB);

        int segmentHeight = this.height - (this.lrbMargin + this.topMargin);
        Graphics2D g2D = (Graphics2D) bi.getGraphics();
        g2D.setColor(this.colorBorderSegment);
        g2D.fillRect(this.lrbMargin - 1, this.topMargin - 1,
                (this.numberOfSegments * segmentDrawingWidth) + 2,
                segmentHeight + 2);
        for (int segmentInd = 0; segmentInd < this.numberOfSegments; segmentInd++) {
            int x = this.lrbMargin + segmentInd * segmentDrawingWidth;
            int y = this.topMargin;
            if (segmentInd % 2 == 0) {
                g2D.setColor(this.colorSegmentEven);
            } else {
                g2D.setColor(this.colorSegmentUneven);
            }
            g2D.fillRect(x, y, segmentDrawingWidth, segmentHeight);
            this.drawSegmentText(g2D,
                    segmentInd * segmentInMeters,
                    this.lrbMargin + segmentInd * segmentDrawingWidth,
                    this.height);
        }
        this.drawSegmentText(g2D,
                this.numberOfSegments * segmentInMeters,
                this.lrbMargin + this.numberOfSegments * segmentDrawingWidth,
                this.height);

        if (scale != null && drawScaleText) {
            String scaleText;
            DecimalFormat df = new DecimalFormat("#,###,###");
            df.setRoundingMode(RoundingMode.HALF_UP);
            if (scale < 1) {
                df = new DecimalFormat("0.##");
            }
            scaleText = df.format(scale);
            scaleText = String.format("1: %s", scaleText);
            this.drawText(g2D, scaleText, finalScalebarWidth / 2, this.topMargin - 2);
        }

        return bi;
    }

    /**
     * Generate the image of the scalebar, stores it in a temporary location and returns the file
     * location.
     *
     * @param scale The scale for which to generate the image
     * @param width The width of the scalebar. The real width will change to round the measurement
     * of the segments.
     * @param dpi DPI used
     * @return The absolute path of the image
     * @throws IOException
     */
    public String getImageAsFileLocation(
            Double scale, double width, double dpi) throws IOException {
        File location = new File(TEMPORARY_IMAGE_FILE_LOCATION);
        if (!location.exists()) {
            location.mkdirs();
        }
        String pathToResult = TEMPORARY_IMAGE_FILE_LOCATION + File.separator
                + TEMPORARY_IMAGE_FILE + "." + IMAGE_FORMAT;
        File outputFile = new File(pathToResult);
        BufferedImage bufferedImage = this.getImage(scale, width, dpi);
        ImageIO.write(bufferedImage, IMAGE_FORMAT, outputFile);
        return pathToResult;
    }

    private void drawText(Graphics2D g2D, String txt, int x, int y) {
        g2D.setFont(this.textFont);
        FontMetrics fontMetrics = g2D.getFontMetrics();
        int txtWidth = fontMetrics.stringWidth(txt);
        x = x - txtWidth / 2;
        g2D.setColor(this.colorText);
        g2D.drawString(txt, x, y);
    }

    private void drawSegmentText(Graphics2D g2D, double segmentMeasureInMeters, int x, int y) {
        String measureToWrite = "";
        if (this.segmentMeasureUnit == segmentMeasureUnitType.km) {
            if ((double) segmentMeasureInMeters / 1000 < 10) {
                DecimalFormat decFormat = new DecimalFormat("#.#");
                measureToWrite = decFormat.format(((double) segmentMeasureInMeters / 1000));
            } else {
                DecimalFormat decFormat = new DecimalFormat("#");
                measureToWrite = decFormat.format(((double) segmentMeasureInMeters / 1000));
            }
        } else if (this.segmentMeasureUnit == segmentMeasureUnitType.cm) {
            DecimalFormat decFormat = new DecimalFormat("#");
            measureToWrite = decFormat.format(((double) segmentMeasureInMeters * 100));
        } else if (this.segmentMeasureUnit == segmentMeasureUnitType.mm) {
            if ((double) segmentMeasureInMeters * 1000 < 1) {
                DecimalFormat decFormat = new DecimalFormat("#.#");
                measureToWrite = decFormat.format(((double) segmentMeasureInMeters * 1000));
            } else {
                DecimalFormat decFormat = new DecimalFormat("#");
                measureToWrite = decFormat.format(((double) segmentMeasureInMeters * 1000));
            }
        } else {
            DecimalFormat decFormat = new DecimalFormat("#");
            measureToWrite = decFormat.format(segmentMeasureInMeters);
        }
        measureToWrite = String.format("%s %s", measureToWrite, this.segmentMeasureUnit);
        this.drawText(g2D, measureToWrite, x, y);
    }
}
