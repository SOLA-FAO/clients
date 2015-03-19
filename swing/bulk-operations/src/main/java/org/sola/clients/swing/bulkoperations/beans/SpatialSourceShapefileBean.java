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

import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.util.GeometryUtility;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.sola.common.logging.LogUtility;

/**
 * The spatial source of type Shapefile. It expects a shapefile as a source of
 * the features.
 * @author Elton Manoku
 */
public class SpatialSourceShapefileBean extends SpatialSourceBean {

    @NotNull(message = "Source is not yet selected")
    private SimpleFeatureSource featureSource;

    public SpatialSourceShapefileBean() {
        super();
        setCode("shp");
        setDisplayValue("Shapefile");
    }

    @Override
    protected void loadAttributes() {
        if (getSourceFile() == null) {
            return;
        }
        getAttributes().clear();
        try {
            FileDataStore store = FileDataStoreFinder.getDataStore(getSourceFile());
            featureSource = store.getFeatureSource();
            setFeaturesNumber(featureSource.getFeatures().size());
        } catch (IOException ex) {
            throw new RuntimeException("Error opening file.", ex);
        }

        for (AttributeDescriptor attributeDescriptor :
                featureSource.getSchema().getAttributeDescriptors()) {
            String bindingName = attributeDescriptor.getType().getBinding().getSimpleName();
            if (featureSource.getSchema().getGeometryDescriptor().equals(attributeDescriptor)) {
                setGeometryType(bindingName);
                continue;
            }
            SpatialAttributeBean attributeBean = new SpatialAttributeBean();
            attributeBean.setName(attributeDescriptor.getLocalName());
            attributeBean.setDataType(bindingName);
            getAttributes().add(attributeBean);
        }
    }

    @Override
    protected List<SpatialSourceObjectBean> getFeatures(List<SpatialAttributeBean> onlyAttributes) {
        List<SpatialSourceObjectBean> spatialObjectList = new ArrayList<SpatialSourceObjectBean>();
        try {
            SimpleFeature feature;
            SimpleFeatureIterator iterator = featureSource.getFeatures().features();
            while (iterator.hasNext()) {
                feature = iterator.next();
                SpatialSourceObjectBean spatialObject = new SpatialSourceObjectBean();
                Geometry geometry =(Geometry)feature.getDefaultGeometry();
                if (geometry.getGeometryType().toLowerCase().startsWith("multi")
                        && isIfMultiUseFirstGeometry()){
                    geometry = geometry.getGeometryN(0);
                }
                geometry.setSRID(getSrid());
                spatialObject.setTheGeom(GeometryUtility.getWkbFromGeometry(geometry));
                for(SpatialAttributeBean attribute: onlyAttributes){
                    spatialObject.getFieldsWithValues().put(
                            attribute.getName(),
                            feature.getAttribute(attribute.getName()));
                }
                spatialObjectList.add(spatialObject);
            }
            iterator.close();
        } catch (IOException ex) {
             LogUtility.log("Error retrieving features from shapefile.", ex);
             throw new RuntimeException("Error retrieving features from shapefile.", ex);
        }
        return spatialObjectList;
    }

    @Override
    public ReferencedEnvelope getExtent() {
        try {
        return featureSource.getFeatures().getBounds();
        } catch (IOException ex) {
             LogUtility.log("Error retrieving features from shapefile.", ex);
             throw new RuntimeException("Error retrieving features from shapefile.", ex);
        }        
    }
        
}
