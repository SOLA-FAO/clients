/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

/**
 *
 * @author Elton Manoku
 */
public class SpatialDestinationParcelBean extends SpatialDestinationBean{
    
    private static String PROPERTY_NAME_LAST_PART = "nameLastPart";
    private static String PROPERTY_NAME_FIRST_PART = "nameFirstPart";
    private static String PROPERTY_OFFICIAL_AREA = "officialArea";
    private static String PROPERTY_GENERATE_FIRST_PART = "generateFirstPart";
    private String nameLastPart;
    private SpatialAttributeBean nameFirstPart;
    private SpatialAttributeBean officialArea;
    private boolean generateFirstPart = false;
    
    public SpatialDestinationParcelBean(){
        setCode("parcel");
        setDisplayValue("Parcel");
    }

    public boolean isGenerateFirstPart() {
        return generateFirstPart;
    }

    public void setGenerateFirstPart(boolean value) {
        boolean old = this.generateFirstPart;
        this.generateFirstPart = value;
        propertySupport.firePropertyChange(PROPERTY_GENERATE_FIRST_PART, old, value);
    }

    public SpatialAttributeBean getNameFirstPart() {
        return nameFirstPart;
    }

    public void setNameFirstPart(SpatialAttributeBean value) {
        SpatialAttributeBean old = this.nameFirstPart;
        this.nameFirstPart = value;
        propertySupport.firePropertyChange(PROPERTY_NAME_FIRST_PART, old, value);
    }

    public String getNameLastPart() {
        return nameLastPart;
    }

    public void setNameLastPart(String value) {
        String old = this.nameLastPart;
        this.nameLastPart = value;
        propertySupport.firePropertyChange(PROPERTY_NAME_LAST_PART, old, value);
    }

    public SpatialAttributeBean getOfficialArea() {
        return officialArea;
    }

    public void setOfficialArea(SpatialAttributeBean value) {
        SpatialAttributeBean old = this.officialArea;
        this.officialArea = value;
        propertySupport.firePropertyChange(PROPERTY_OFFICIAL_AREA, old, value);
    }
    
}
