/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.geotools.map.extended.layer;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DirectLayer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.swing.extended.exception.DirectImageNotValidFileException;
import org.geotools.swing.extended.util.ImageUtility;

/**
 * This layer is used to draw any given image in the map. Next to the message is
 * given also the coordinates of the boundingbox where the image will appear.
 *
 * @author Elton Manoku
 */
public class DirectImageLayer extends DirectLayer {

    private ReferencedEnvelope bounds;
    private File rasterFile = null;
    private BufferedImage loadedBufferedImage = null;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private int imageWidth;
    private int imageHeight;
    private static final int MAX_PIXEL_HEIGHT = 2000;
    private static final int MAX_PIXEL_WIDTH = 2000;

    /**
     * Constructor of the layer.
     */
    public DirectImageLayer() {
    }

    /**
     * Sets the raster file. If set to null the current image will disappear.
     * This is the way to remove the image.
     *
     * @param rasterFile The File object containing a reference to the raster
     * file.
     * @throws IOException It is thrown if any IO error occurs.
     * @throws DirectImageNotValidFileException It is thrown if the File is not
     * image.
     */
    public void setRasterFile(File rasterFile)
            throws IOException, DirectImageNotValidFileException {
        this.rasterFile = rasterFile;
        if (this.rasterFile != null) {
            // #285 Subsample large images to avoid Java Heap error
            this.loadedBufferedImage = ImageUtility.subsampleImage(
                    ImageIO.createImageInputStream(this.rasterFile),
                    MAX_PIXEL_WIDTH, MAX_PIXEL_HEIGHT, null);
            if (this.loadedBufferedImage == null) {
                throw new DirectImageNotValidFileException();
            }
            this.imageWidth = this.loadedBufferedImage.getWidth();
            this.imageHeight = this.loadedBufferedImage.getHeight();
        } else {
            this.loadedBufferedImage = null;
        }
    }

    /**
     * Sets the maximum X coordinate of the bounding box where the image will be
     * located.
     *
     * @param maxX
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * Sets the maximum Y coordinate of the bounding box where the image will be
     * located.
     *
     * @param maxY
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    /**
     * Sets the minimum X coordinate of the bounding box where the image will be
     * located.
     *
     * @param minX
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * Sets the minimum Y coordinate of the bounding box where the image will be
     * located.
     *
     * @param minY
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * It draws the image in the map if an image is provided with the method
     * setRasterFile(File rasterFile).
     * <br/>It is called directly from the drawing engine.
     *
     * @param graphics
     * @param map
     * @param viewport
     */
    @Override
    public void draw(Graphics2D graphics, MapContent map, MapViewport viewport) {
        this.bounds = viewport.getBounds();
        if (this.loadedBufferedImage != null) {
            Point2D pixelStartPoint = new Point2D.Double(this.minX, this.maxY);
            Point2D pixelEndPoint = new Point2D.Double(this.maxX, this.minY);
            viewport.getWorldToScreen().transform(pixelStartPoint, pixelStartPoint);
            viewport.getWorldToScreen().transform(pixelEndPoint, pixelEndPoint);
            graphics.drawImage(
                    this.loadedBufferedImage,
                    (int) pixelStartPoint.getX(), (int) pixelStartPoint.getY(),
                    (int) pixelEndPoint.getX(), (int) pixelEndPoint.getY(),
                    0, 0, this.imageWidth, this.imageHeight, null);
        }
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return this.bounds;
    }
}
