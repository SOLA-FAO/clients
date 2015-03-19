/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.extended.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.renderer.lite.StreamingRenderer;

/**
 * It is a generator of an image from the current status of the map layers.
 * <br/> In the sides of the image generated, are added the coordinates of the
 * extent.
 *
 * @author Elton Manoku
 */
public class MapImageGenerator {

    private final static String TEMPORARY_IMAGE_FILE_LOCATION =
            System.getProperty("user.home") + File.separator + "sola";
    private final static String TEMPORARY_IMAGE_FILE = "map";
    private MapContent mapContent;
    private Color textColor = Color.RED;
    private Font textFont = new Font(Font.SANS_SERIF, Font.BOLD, 10);
    private String textInTheMapCenter = null;
    private boolean drawCoordinatesInTheSides = true;

    /**
     * Constructor of the generator.
     *
     * @param mapContent The map content used as a source for generating the
     * image
     */
    public MapImageGenerator(MapContent mapContent) {
        this.mapContent = mapContent;
    }

    public MapContent getMapContent() {
        return mapContent;
    }

    /**
     * Gets the color of the text used in the image
     *
     * @return
     */
    public Color getTextColor() {
        return textColor;
    }

    /**
     * Sets the color of the text used in the image
     *
     * @param textColor
     */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    /**
     * Gets the font of the text used in the image
     *
     * @return
     */
    public Font getTextFont() {
        return textFont;
    }

    /**
     * Sets the font of the text used in the image
     *
     * @param textFont
     */
    public void setTextFont(Font textFont) {
        this.textFont = textFont;
    }

    public String getTextInTheMapCenter() {
        return textInTheMapCenter;
    }

    public void setTextInTheMapCenter(String textInTheMapCenter) {
        this.textInTheMapCenter = textInTheMapCenter;
    }

    public boolean GetDrawCoordinatesInTheSides() {
        return drawCoordinatesInTheSides;
    }

    public void setDrawCoordinatesInTheSides(boolean drawCoordinatesInTheSides) {
        this.drawCoordinatesInTheSides = drawCoordinatesInTheSides;
    }

    /**
     * It generates the image.
     *
     * @param imageWidth The image width
     * @param imageHeight The image height
     * @param scale The scale of the map
     * @param dpi The dpi used
     * @return An buffered image
     */
    public BufferedImage getImage(double imageWidth, double imageHeight, double scale, int dpi) {
        return getImage(this.mapContent.getViewport().getBounds(), imageWidth, imageHeight, scale, dpi);
    }

    /**
     * It generates the image.
     *
     * @param initialExtent The initial extent of the map
     * @param imageWidth The image width
     * @param imageHeight The image height
     * @param scale The scale of the map
     * @param dpi The dpi used
     * @return An buffered image
     */
    public BufferedImage getImage(ReferencedEnvelope initialExtent, double imageWidth, double imageHeight, double scale, int dpi) {
        double centerX = initialExtent.getMedian(0);
        double centerY = initialExtent.getMedian(1);
        double dotPerCm = dpi / 2.54;
        double extentWidth = imageWidth * scale / (dotPerCm * 100);
        double extentHeight = imageHeight * scale / (dotPerCm * 100);
        ReferencedEnvelope extent = new ReferencedEnvelope(
                centerX - extentWidth / 2, centerX + extentWidth / 2,
                centerY - extentHeight / 2, centerY + extentHeight / 2,
                initialExtent.getCoordinateReferenceSystem());
        return this.getImage((int) Math.round(imageWidth), extent);
    }
    
    /**
     * It generates the image. The height of the image is calculated.
     *
     * @param imageWidth The width of the image
     * @param extent The extent of the map to fit the image
     * @return
     */
    public BufferedImage getImage(int imageWidth, ReferencedEnvelope extent) {
        int imageHeight = (int) (imageWidth * (extent.getHeight() / extent.getWidth()));
        BufferedImage bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        StreamingRenderer renderer = new StreamingRenderer();
        renderer.setMapContent(this.mapContent);
        Rectangle rectangle = new Rectangle(imageWidth, imageHeight);

        //Save the current viewport
        MapViewport mapViewportOriginal = this.mapContent.getViewport();

        //Define a new viewport 
        MapViewport mapViewport = new MapViewport(extent, true);
        mapViewport.setScreenArea(rectangle);
        mapViewport.setCoordinateReferenceSystem(this.mapContent.getViewport().getCoordinateReferenceSystem());

        //Set the new viewport
        renderer.getMapContent().setViewport(mapViewport);

        // Render map according to the new viewport. Use the viewport 
        // worldToScreen transformation to ensure the image is rendered 
        // with the same orientation as the map. 
        renderer.paint(graphics, rectangle, extent, mapViewport.getWorldToScreen());
        //Set the previous viewport back
        this.mapContent.setViewport(mapViewportOriginal);

        if (GetDrawCoordinatesInTheSides()) {
            graphics.setColor(Color.BLACK);
            graphics.drawRect(0, 0, imageWidth - 1, imageHeight - 1);
            graphics.setFont(this.textFont);
            graphics.setColor(this.textColor);

            this.drawText(graphics, String.format("%s N", (int) extent.getMaxY()),
                    imageWidth / 2, 10, true);
            this.drawText(graphics, String.format("%s N", (int) extent.getMinY()),
                    imageWidth / 2, imageHeight - 3, true);
            AffineTransform originalTransform = graphics.getTransform();
            graphics.rotate(-Math.PI / 2, 10, imageHeight / 2);
            this.drawText(graphics, String.format("%s E", (int) extent.getMinX()),
                    10, imageHeight / 2, false);
            graphics.setTransform(originalTransform);
            graphics.rotate(Math.PI / 2, imageWidth - 10, imageHeight / 2);
            this.drawText(graphics, String.format("%s E", (int) extent.getMaxX()),
                    imageWidth - 100, imageHeight / 2, false);

            graphics.setTransform(originalTransform);

        }
        if (this.textInTheMapCenter != null) {
            this.drawText(graphics, textInTheMapCenter, imageWidth / 2, imageHeight / 2, true);
        }

        return bi;
    }

    /**
     * It generates the image and it saves it in a temporary file. The file is
     * map.<format>
     *
     * @param imageWidth The width in pixels/pointers of the image
     * @param imageHeight The height in pixels/pointers of the image
     * @param scale The desired scale
     * @param dpi The dpi of the target. If it is a pdf generation it is the dpi
     * of the pdf generator
     * @param imageFormat Acceptable formats for the image. Potential values can
     * be jpg, png, bmp
     * @return The absolute path where the image is stored
     * @throws IOException
     */
    public String getImageAsFileLocation(double imageWidth, double imageHeight, double scale,
            int dpi, String imageFormat) throws IOException {
        return this.getImageAsFileLocation(this.mapContent.getViewport().getBounds(), 
                imageWidth, imageHeight, scale, dpi, imageFormat, TEMPORARY_IMAGE_FILE);
    }

    /**
     * It generates the image and it saves it in a temporary file.
     *
     * @param imageWidth The width in pixels/pointers of the image
     * @param imageHeight The height in pixels/pointers of the image
     * @param scale The desired scale
     * @param dpi The dpi of the target. If it is a pdf generation it is the dpi
     * of the pdf generator
     * @param imageFormat Acceptable formats for the image. Potential values can
     * be jpg, png, bmp
     * @param fileNameWithoutExtension The filename with the extension and
     * without folder
     * @return The absolute path where the image is stored
     * @throws IOException
     */
    public String getImageAsFileLocation(
            ReferencedEnvelope initialExtent, double imageWidth, double imageHeight, double scale,
            int dpi, String imageFormat, String fileNameWithoutExtension) throws IOException {
        String pathToResult = getFullpathOfMapImage(fileNameWithoutExtension, imageFormat);
        File outputFile = new File(pathToResult);
        BufferedImage bufferedImage = this.getImage(initialExtent, imageWidth, imageHeight, scale, dpi);
        ImageIO.write(bufferedImage, imageFormat, outputFile);
        return pathToResult;
    }

    /**
     * It gets the full path of the map image given the name of the file without extension.
     * This method makes also the folder where the image is saved if it does not exist.
     * @param fileNameWithoutExtension
     * @param imageFormat
     * @return 
     */
    public String getFullpathOfMapImage(String fileNameWithoutExtension, String imageFormat){
        File location = new File(TEMPORARY_IMAGE_FILE_LOCATION);
        if (!location.exists()) {
            location.mkdirs();
        }
        return TEMPORARY_IMAGE_FILE_LOCATION + File.separator + fileNameWithoutExtension + "." + imageFormat;
    }
    
    private void drawText(Graphics2D graphics, String txt, int x, int y, boolean center) {
        if (center) {
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int txtWidth = fontMetrics.stringWidth(txt);
            x = x - txtWidth / 2;
        }
        graphics.drawString(txt, x, y);
    }
}
