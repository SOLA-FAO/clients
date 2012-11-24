/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.geotools.swing.extended.util.GeometryUtility;
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
    private static String SPATIAL_UNIT_TEMPORARY_TYPE = "cadastre_object";
    private String nameLastPart;
    private SpatialAttributeBean nameFirstPart;
    private SpatialAttributeBean officialArea;
    private boolean generateFirstPart = false;
    private String cadastreObjectTypeCode = "parcel";

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
    public List<SpatialUnitTemporaryBean> getBeans(SpatialSourceBean fromSource) {
        List<SpatialUnitTemporaryBean> beans = new ArrayList<SpatialUnitTemporaryBean>();
        List<SpatialAttributeBean> onlyAttributes = new ArrayList<SpatialAttributeBean>();
        onlyAttributes.add(officialArea);
        if (!isGenerateFirstPart()) {
            onlyAttributes.add(nameFirstPart);
        }

        for (SpatialSourceObjectBean sourceObject : fromSource.getFeatures(onlyAttributes)) {
            SpatialUnitTemporaryBean bean = new  SpatialUnitTemporaryBean();
            bean.setTypeCode(SPATIAL_UNIT_TEMPORARY_TYPE);
            bean.setCadastreObjectTypeCode(cadastreObjectTypeCode);
            bean.setGeom(sourceObject.getTheGeom());
            
            bean.setOfficialArea(BigDecimal.valueOf(
                    Double.valueOf(sourceObject.getFieldsWithValues().get(
                        officialArea.getName()).toString())));
            bean.setNameLastpart(getNameLastPart());
            if (!isGenerateFirstPart()){
                bean.setNameFirstpart(
                        sourceObject.getFieldsWithValues().get(nameFirstPart.getName()).toString());
            }
            beans.add(bean);
        }
        return beans;
    }
}
