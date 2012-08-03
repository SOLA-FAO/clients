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
package org.sola.clients.swing.gis.beans;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.swing.extended.util.GeometryUtility;

/**
 * It represents a cadastre object during the process of redefinition of the cadastre process.
 * This bean holds the current and changed geometry.
 * 
 * @author Elton Manoku
 */
public class CadastreObjectTargetRedefinitionBean extends CadastreObjectTargetBean {
    private static String FEATURE_GEOM_PROPERTY = "featureGeom";
    private byte[] geomPolygon;

    public byte[] getGeomPolygon() {
        return geomPolygon;
    }

    public void setGeomPolygon(byte[] geomPolygon) {
        setFeatureGeom(GeometryUtility.getGeometryFromWkb(geomPolygon));
    }
   
    @Override
    public void setFeatureGeom(Geometry geometryValue) {
        super.setFeatureGeom((Geometry)geometryValue.clone());
        this.geomPolygon = GeometryUtility.getWkbFromGeometry(geometryValue);
        propertySupport.firePropertyChange(FEATURE_GEOM_PROPERTY, null, geometryValue);
    }
    
    /**
     * Gets the flag if the old geometry of the cadastre object is the same with the new one.
     * This is used to check if there is needed to still keep this bean or not.
     * @return 
     */
    public boolean currentAndNewTheSame(){
        return (getFeatureGeom().equalsTopo(getGeomPolygonCurrentForFeature()));
    }
}
