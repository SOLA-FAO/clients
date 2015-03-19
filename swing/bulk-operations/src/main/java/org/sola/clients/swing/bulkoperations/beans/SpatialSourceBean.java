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
package org.sola.clients.swing.bulkoperations.beans;

import java.io.File;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.controls.SolaObservableList;

/**
 * Abstract bean that encapsulates common functionality and properties for a 
 * spatial source. <br/>
 * If there will be added a new source type, it has to inherit from this bean.
 * 
 * @author Elton Manoku
 */
public abstract class  SpatialSourceBean extends AbstractCodeBean {
    
    private String geometryType;
    private int featuresNumber;
    private SolaObservableList<SpatialAttributeBean> attributes =
            new SolaObservableList<SpatialAttributeBean>();
    private boolean ifMultiUseFirstGeometry = true;

    @NotNull(message = "Srid is missing")
    private Integer srid = null;
    
    private File sourceFile;
    
    public SpatialSourceBean(){
        super();
        setStatus("c");
    }
    
    public SolaObservableList<SpatialAttributeBean> getAttributes() {
        return attributes;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(String geometryType) {
        this.geometryType = geometryType;
    }

    public Integer getFeaturesNumber() {
        return featuresNumber;
    }

    public void setFeaturesNumber(int featuresNumber) {
        this.featuresNumber = featuresNumber;
    }

    public boolean isIfMultiUseFirstGeometry() {
        return ifMultiUseFirstGeometry;
    }

    public void setIfMultiUseFirstGeometry(boolean ifMultiUseFirstGeometry) {
        this.ifMultiUseFirstGeometry = ifMultiUseFirstGeometry;
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
        //The list of attributes will be populated
        loadAttributes();
    }

    public Integer getSrid() {
        return srid;
    }

    public void setSrid(Integer srid) {
        this.srid = srid;
        System.out.println(srid);
    }
    
    /**
     * It loads attributes for a specific source.
     * For each kind of source type, this method should be overridden.
     */
    protected abstract void loadAttributes();
    
    /**
     * Retrieves the list of features from the source.
     * @return 
     */
    protected abstract List<SpatialSourceObjectBean> getFeatures(
            List<SpatialAttributeBean> onlyAttributes);
    
    /**
     * Retrieves the extent where all the features are found.
     * 
     * @return 
     */
    public abstract ReferencedEnvelope getExtent();
}
