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
