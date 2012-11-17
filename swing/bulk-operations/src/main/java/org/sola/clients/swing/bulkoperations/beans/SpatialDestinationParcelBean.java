/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.ArrayList;
import java.util.List;
import org.sola.clients.swing.gis.beans.CadastreObjectBean;
import org.sola.clients.swing.gis.beans.SpatialBean;

/**
 *
 * @author Elton Manoku
 */
public class SpatialDestinationParcelBean extends SpatialDestinationBean {

    private static String PROPERTY_NAME_LAST_PART = "nameLastPart";
    private static String PROPERTY_NAME_FIRST_PART = "nameFirstPart";
    private static String PROPERTY_OFFICIAL_AREA = "officialArea";
    private static String PROPERTY_GENERATE_FIRST_PART = "generateFirstPart";
    private String nameLastPart;
    private SpatialAttributeBean nameFirstPart;
    private SpatialAttributeBean officialArea;
    private boolean generateFirstPart = false;

    public SpatialDestinationParcelBean() {
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

    @Override
    public <T extends SpatialBean> List<T> getBeans(SpatialSourceBean fromSource) {
        List<T> beans = new ArrayList<T>();
        List<SpatialAttributeBean> onlyAttributes = new ArrayList<SpatialAttributeBean>();
        onlyAttributes.add(officialArea);
        if (!isGenerateFirstPart()) {
            onlyAttributes.add(nameFirstPart);
        }

        for (SpatialSourceObjectBean sourceObject : fromSource.getFeatures(onlyAttributes)) {
            CadastreObjectBean parcel = new CadastreObjectBean();
            parcel.setGeomPolygon(sourceObject.getTheGeom());
            parcel.setOfficialArea(
                    Double.valueOf(
                    sourceObject.getFieldsWithValues().get(officialArea.getName()).toString()));
            parcel.setNameLastpart(getNameLastPart());
            if (!isGenerateFirstPart()){
                parcel.setNameFirstpart(
                        sourceObject.getFieldsWithValues().get(nameFirstPart.getName()).toString());
            }
            beans.add((T)parcel);
        }
        return beans;
    }
}
