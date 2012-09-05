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

package org.sola.clients.swing.gis.beans;

import com.vividsolutions.jts.geom.Geometry;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.geotools.swing.extended.util.GeometryUtility;

/**
 * Defines a cadastre object bean.
 * 
 * @author Elton Manoku
 */
public class CadastreObjectBean extends SpatialBean {
    
    public static String NAME_FIRST_PART_PROPERTY = "nameFirstpart";
    
    private String id;
    private String nameFirstpart = "";
    private String nameLastpart = "";
    private String typeCode = "parcel";
    private byte[] geomPolygon;
    private List<SpatialValueAreaBean> spatialValueAreaList = new ArrayList<SpatialValueAreaBean>();

    /**
     * Creates a cadastre object bean
     */
    public CadastreObjectBean(){
        super();
        generateId();
    }

    /** 
     * Generates new ID for the cadastre object 
     */
    public final void generateId(){
        setId(UUID.randomUUID().toString());
    }
    
    @Override
    public void setFeatureGeom(Geometry geometryValue) {
        super.setFeatureGeom(geometryValue);
        this.setGeomPolygon(GeometryUtility.getWkbFromGeometry(geometryValue));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getNameFirstpart() {
        return nameFirstpart;
    }

    /**
     * Sets the Name first part. It fires the change event to notify the corresponding feature
     * for the change.
     * 
     * @param nameFirstpart 
     */
    public void setNameFirstpart(String nameFirstpart) {
        String oldValue = this.nameFirstpart;
        this.nameFirstpart = nameFirstpart;
        propertySupport.firePropertyChange(NAME_FIRST_PART_PROPERTY, oldValue, nameFirstpart);
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastpart) {
        this.nameLastpart = nameLastpart;
    }

    public byte[] getGeomPolygon() {
        return geomPolygon;
    }

    /**
     * Sets the geometry of the cadastre object. If the feature related geometry is not present,
     * it is also set.
     * 
     * @param geomPolygon 
     */
    public void setGeomPolygon(byte[] geomPolygon) {
        this.geomPolygon = geomPolygon.clone();
        if (getFeatureGeom() == null){
            super.setFeatureGeom(GeometryUtility.getGeometryFromWkb(geomPolygon));
        }
    }
   
    public List<SpatialValueAreaBean> getSpatialValueAreaList() {
        return spatialValueAreaList;
    }

    public void setSpatialValueAreaList(List<SpatialValueAreaBean> spatialValueAreaList) {
        this.spatialValueAreaList = spatialValueAreaList;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * Gets the calculated area of the cadastre object. The calculated area is the 
     * area value found in the {@see SpatialValueAreaBean.TYPE_CALCULATED} of the
     * {@see spatialValueAreaList} property.
     * 
     * @return 
     */
    public Double getCalculatedArea() {
        for(SpatialValueAreaBean valueAreaBean: this.getSpatialValueAreaList()){
            if (valueAreaBean.getTypeCode().equals(SpatialValueAreaBean.TYPE_OFFICIAL)){
                return valueAreaBean.getSize().doubleValue();
            }
        }
        return null;
    }

    /**
     * Sets the calculated area of the cadastre object. The calculated area is set in the 
     * {@see SpatialValueAreaBean} of type {@see SpatialValueAreaBean.TYPE_CALCULATED} found
     * in the {@see spatialValueAreaList} property.
     * 
     * @param calculatedArea 
     */
    public void setCalculatedArea(Double calculatedArea) {
        this.setArea(calculatedArea, SpatialValueAreaBean.TYPE_CALCULATED);
    }

    /**
     * Gets the official area of the cadastre object. The official area is the 
     * area value found in the {@see SpatialValueAreaBean.TYPE_OFFICIAL} of the
     * {@see spatialValueAreaList} property.
     * 
     * @return 
     */
    public Double getOfficialArea() {
        for(SpatialValueAreaBean valueAreaBean: this.getSpatialValueAreaList()){
            if (valueAreaBean.getTypeCode().equals(SpatialValueAreaBean.TYPE_OFFICIAL)){
                return valueAreaBean.getSize().doubleValue();
            }
        }
        return null;
    }

    /**
     * Sets the official area of the cadastre object. The official area is set in the 
     * {@see SpatialValueAreaBean} of type {@see SpatialValueAreaBean.TYPE_OFFICIAL} found
     * in the {@see spatialValueAreaList} property.
     * 
     * @param officialArea 
     */
    public void setOfficialArea(Double officialArea) {
        this.setArea(officialArea, SpatialValueAreaBean.TYPE_OFFICIAL);
    }

    /**
     * It sets the area for the cadastre object. The area is stored in the SpatialValueAreaBeans
     * attached to this bean. So for changing the area, first it is located the appropriate
     * SpatialValueAreaBean. if not found it is added one.
     * 
     * @param areaSize The size
     * @param areaType The area type
     */
    private void setArea(Double areaSize, String areaType) {
        SpatialValueAreaBean valueAreaBeanFound = null;
        for(SpatialValueAreaBean valueAreaBean: this.getSpatialValueAreaList()){
            if (valueAreaBean.getTypeCode().equals(areaType)){
                valueAreaBeanFound = valueAreaBean;
                break;
            }
        }
        if (valueAreaBeanFound == null){
            valueAreaBeanFound = new SpatialValueAreaBean();
            valueAreaBeanFound.setTypeCode(areaType);
            this.getSpatialValueAreaList().add(valueAreaBeanFound);
        }
        valueAreaBeanFound.setSize(BigDecimal.valueOf(areaSize));
    }
    
    @Override
    public String toString() {
        return String.format("%s / %s",this.nameFirstpart, this.nameLastpart);
    }    
}
