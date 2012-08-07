/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.IOException;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.exception.DirectImageNotValidFileException;
import org.geotools.swing.extended.exception.InitializeLayerException;

/**
 * An extended layer to be used to attach an image in the map control.
 * <br/>It uses the {@see org.geotools.map.extended.layer.DirectImageLayer} to load the layer with
 * the image in the map control.
 * @author Elton Manoku
 */
public class ExtendedImageLayer extends ExtendedLayer {
    
    private DirectImageLayer rasterLayer;
    private ExtendedLayerGraphics pointLayer;
    private static final String LAYER_POINT_NAME_POSTFIX = "-POINT";
    private static final String LAYER_POINT_STYLE_RESOURCE = "image-point.xml";
    
    
    /**
     * Constructor of the layer.
     * @param name Name of the layer
     * @param title Title of the layer.
     * @throws InitializeLayerException 
     */
    public ExtendedImageLayer(String name, String title)throws InitializeLayerException{
        this.setLayerName(name);
        this.setTitle(title);
        
         this.rasterLayer = new DirectImageLayer();
         this.pointLayer = new ExtendedLayerGraphics(
                 name + LAYER_POINT_NAME_POSTFIX,
                 Geometries.POINT, LAYER_POINT_STYLE_RESOURCE);
         this.getMapLayers().add(this.rasterLayer);
         this.getMapLayers().addAll(this.pointLayer.getMapLayers());
    }
    
    /**
     * Sets the first orientation point in the map for the image.
     * @param x
     * @param y
     */
    public void setFirstPoint(Double x, Double y){
        Point point = this.pointLayer.getGeometryFactory().createPoint(new Coordinate(x, y));
        this.pointLayer.removeFeatures(false);
        this.pointLayer.addFeature(null, point, null, false);
        this.rasterLayer.setMinX(x);
        this.rasterLayer.setMinY(y);
        this.getMapControl().refresh();
    }
    
    /**
     * Sets the second orientation point in the map for the image.
     * @param x
     * @param y
     */
    public void setSecondPoint(Double x, Double y){
        Point point = this.pointLayer.getGeometryFactory().createPoint(new Coordinate(x, y));
        this.pointLayer.addFeature(null, point, null, false);
        this.rasterLayer.setMaxX(x);
        this.rasterLayer.setMaxY(y);
        this.getMapControl().refresh();
    }

    /**
     * Sets the raster file. If set to null the current image will disappear. This is the way
     * to remove the image.
     * @param rasterFile The File object containing a reference to the raster file.
     * @throws IOException It is thrown if any IO error occurs.
     * @throws DirectImageNotValidFileException It is thrown if the File is not image.
     */
    public void setRasterFile(File rasterFile) 
            throws IOException, DirectImageNotValidFileException {
        this.rasterLayer.setRasterFile(rasterFile);
        if (rasterFile == null){
            this.pointLayer.removeFeatures(false);
        }
    }

    /**
     * Sets the maximum X coordinate of the bounding box where the image will be located.
     * @param maxX 
     */
    public void setMaxX(double maxX) {
        this.rasterLayer.setMaxX(maxX);
    }

    /**
     * Sets the maximum Y coordinate of the bounding box where the image will be located.
     * @param maxY 
     */
    public void setMaxY(double maxY) {
        this.rasterLayer.setMaxY(maxY);
    }

    /**
     * Sets the minimum X coordinate of the bounding box where the image will be located.
     * @param minX 
     */
    public void setMinX(double minX) {
        this.rasterLayer.setMinX(minX);
    }

    /**
     * Sets the minimum Y coordinate of the bounding box where the image will be located.
     * @param minY 
     */
    public void setMinY(double minY) {
        this.rasterLayer.setMinY(minY);
    } 

}
