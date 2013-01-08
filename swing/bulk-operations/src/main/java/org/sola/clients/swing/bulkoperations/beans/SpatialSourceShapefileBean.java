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
