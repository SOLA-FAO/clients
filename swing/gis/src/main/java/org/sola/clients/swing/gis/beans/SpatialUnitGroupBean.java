/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.beans;

import com.vividsolutions.jts.geom.Geometry;
import java.util.UUID;
import org.geotools.swing.extended.util.GeometryUtility;

/**
 *
 * @author Elton Manoku
 */
public class SpatialUnitGroupBean extends SpatialBean {

    public static String LABEL_PROPERTY = "label";
    private String id;
    private Integer hierarchyLevel;
    private String label;
    private byte[] geom;

    public SpatialUnitGroupBean(){
        super();
        generateId();
    }
    
    
    /** 
     * Generates new ID for the cadastre object 
     */
    public final void generateId(){
        setId(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public Integer getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(Integer hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        String oldValue = this.label;
        this.label = label;
        propertySupport.firePropertyChange(LABEL_PROPERTY, oldValue, label);        
    }

    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] geom) {
        this.geom = geom.clone();
        super.setFeatureGeom(GeometryUtility.getGeometryFromWkb(geom));
    }

    @Override
    public void setFeatureGeom(Geometry geometryValue) {
        super.setFeatureGeom(geometryValue);
        this.geom = GeometryUtility.getWkbFromGeometry(geometryValue);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", getHierarchyLevel(), getLabel());
    }

}
