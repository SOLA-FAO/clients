/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.geotools.ui.maptools;

import com.vividsolutions.jts.geom.Geometry;
import java.io.Serializable;
import java.util.HashMap;
import org.geotools.geometry.jts.Geometries;
import org.opengis.feature.simple.SimpleFeature;
import org.sola.clients.geotools.ui.Map;

/**
 *
 * @author manoku
 */
public class SolaDrawPolygon extends SolaEditGeometryTool{
    private String toolName = "polygon";
    private String extraFields = "label:\"\",type:0";
    private String layerResourceTest = "test_editor_polygon.sld";

    public SolaDrawPolygon(){
        this.setToolName(toolName);
        this.setLayerName(toolName);
        this.setGeometryType(Geometries.POLYGON);
        this.setSldResource(layerResourceTest);
        this.setExtraFieldsFormat(extraFields);
    }
    
    @Override
    public void setMapControl(Map mapControl){
        super.setMapControl(mapControl);
        this.layer.setFilterExpressionForSnapping("type=1");
    }

   @Override
    public SimpleFeature addFeature(Geometry geometry) throws Exception{
        HashMap<String, Object> fieldsWithValues = new HashMap<String, Object>();
        fieldsWithValues.put("type", 2);
        fieldsWithValues.put("label", "aha");
        return this.layer.addFeature(null, geometry, fieldsWithValues);
    }
    
}
