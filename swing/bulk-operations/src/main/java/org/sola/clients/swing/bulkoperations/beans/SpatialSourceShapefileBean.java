/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.io.IOException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.type.AttributeDescriptor;

/**
 *
 * @author Elton Manoku
 */
public class SpatialSourceShapefileBean extends SpatialSourceBean {

    private SimpleFeatureSource featureSource;
    
    public SpatialSourceShapefileBean(){
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
}
