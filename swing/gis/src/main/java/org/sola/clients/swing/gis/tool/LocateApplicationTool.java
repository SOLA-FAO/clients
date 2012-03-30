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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.tool.extended.ExtendedEditGeometryTool;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This tool it is used in the control bundle used in the application form. It is used to define
 * the application location.
 * 
 * @author Elton Manoku
 */
public class LocateApplicationTool extends ExtendedEditGeometryTool {

    private String toolName = "locate";
    private String layerName = "Current application location";
    String toolTip =  MessageUtility.getLocalizedMessage(
                            GisMessage.CADASTRE_TOOLTIP_ADD_LOCATION).getMessage();
    
    public LocateApplicationTool() {
        this.setToolName(toolName);
        this.setLayerName(layerName);
        this.setToolTip(toolTip);
        this.setIconImage("resources/application-location-add.png");
        this.setGeometryType(Geometries.POINT);
    }

    /**
     * Gets the geometry of the application location. It is a multipoint.
     * @return 
     */
    public Geometry getLocationGeometry() {
        Geometry result = null;
        SimpleFeatureIterator iterator = this.layer.getFeatureCollection().features();
        while (iterator.hasNext()) {
            SimpleFeature currentFeature = iterator.next();
            Point geom = (Point) currentFeature.getDefaultGeometry();
            if (geom == null) {
                continue;
            }
            if (result == null) {
                result = this.layer.getGeometryFactory().createMultiPoint(new Point[]{geom});
            } else {
                result = result.union(geom);
            }
        }
        return result;
    }

    /**
     * Sets the location of the application
     * 
     * @param existingLocation If null nothing is displayed
     */
    public void setLocationGeometry(Geometry existingLocation) {
        this.layer.removeFeatures();
        if (existingLocation != null) {

            for (int pointInd = 0; pointInd < existingLocation.getNumPoints(); pointInd++) {
                this.layer.addFeature(null,
                        (Geometry) existingLocation.getGeometryN(pointInd).clone(),
                        null);
            }
        }
    }
}