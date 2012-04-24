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

package org.geotools.swing.extended.tool;

import org.geotools.swing.tool.extended.*;
import com.vividsolutions.jts.geom.Geometry;
import java.util.HashMap;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.swing.extended.Map;

/**
 *
 * This is used for testing purposes to show how to extend the {@see ExtendedEditGeometryTool}
 * 
 * @author Elton Manoku
 */
public class ExtendedDrawLinestring extends ExtendedEditGeometryTool{
    private String toolName = "Linestring";
    private String extraFields = "label:\"\",type:0";
    private String layerResourceTest = "linestring.xml";

    public ExtendedDrawLinestring(){
        this.setToolName(toolName);
        this.setLayerName(toolName);
        this.setGeometryType(Geometries.LINESTRING);
        this.setSldResource(layerResourceTest);
        this.setExtraFieldsFormat(extraFields);
    }
    
    @Override
    public void setMapControl(Map mapControl){
        super.setMapControl(mapControl);
        this.layer.setFilterExpressionForSnapping("type=2");
        this.getTargetSnappingLayers().add(this.layer);
    }

   @Override
    public SimpleFeature addFeature(Geometry geometry){
        HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
        fieldsWithValues.put("type", 2);
        fieldsWithValues.put("label", "aha");
        return this.layer.addFeature(null, geometry, fieldsWithValues);
    }
    
}
