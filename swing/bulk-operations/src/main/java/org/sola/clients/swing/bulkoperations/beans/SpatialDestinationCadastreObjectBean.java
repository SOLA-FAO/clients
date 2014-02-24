/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.clients.swing.bulkoperations.spatialobjects.SpatialDestinationCadastreObjectPanel;

/**
 * The spatial destination type cadastre object. 
 * This destination type is chosen to convert the source features to 
 * cadastre object complaint beans.
 * 
 * @author Elton Manoku
 */
public class SpatialDestinationCadastreObjectBean extends SpatialDestinationBean {

    private static String PROPERTY_NAME_LAST_PART = "nameLastPart";
    private static String PROPERTY_NAME_FIRST_PART = "nameFirstPart";
    private static String PROPERTY_OFFICIAL_AREA = "officialArea";
    private static String PROPERTY_GENERATE_FIRST_PART = "generateFirstPart";
    private static String PROPERTY_CADASTRE_OBJECT_TYPE_CODE = "cadastreObjectTypeCode";
    private static String PROPERTY_CADASTRE_OBJECT_TYPE = "cadastreObjectType";
    private String nameLastPart;
    private SpatialAttributeBean nameFirstPart;
    private SpatialAttributeBean officialArea;
    private boolean generateFirstPart = false;
    private CadastreObjectTypeBean cadastreObjectType = new CadastreObjectTypeBean();
    private String code = "cadastre_object";
    private String displayValue = "Cadastre object";

    public SpatialDestinationCadastreObjectBean() {
        super();
        setCode(code);
        setDisplayValue(displayValue);
        setCadastreObjectTypeCode(CadastreObjectTypeBean.CODE_PARCEL);
    }

    @Override
    public String getPanelName() {
        return SpatialDestinationCadastreObjectPanel.PANEL_NAME;
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

    @NotNull(message = "Last part is missing")
    public String getNameLastPart() {
        return nameLastPart;
    }

    public void setNameLastPart(String value) {
        String old = this.nameLastPart;
        this.nameLastPart = value;
        propertySupport.firePropertyChange(PROPERTY_NAME_LAST_PART, old, value);
    }

    @NotNull(message = "Attribute to give the offical area value is missing")
    public SpatialAttributeBean getOfficialArea() {
        return officialArea;
    }

    public void setOfficialArea(SpatialAttributeBean value) {
        SpatialAttributeBean old = this.officialArea;
        this.officialArea = value;
        propertySupport.firePropertyChange(PROPERTY_OFFICIAL_AREA, old, value);
    }

    public CadastreObjectTypeBean getCadastreObjectType() {
        return cadastreObjectType;
    }

    public void setCadastreObjectType(CadastreObjectTypeBean cadastreObjectType) {
        this.setJointRefDataBean(
                this.cadastreObjectType, cadastreObjectType, PROPERTY_CADASTRE_OBJECT_TYPE);
    }

    public String getCadastreObjectTypeCode() {
        return cadastreObjectType.getCode();
    }

    public final void setCadastreObjectTypeCode(String value) {
        String old = getCadastreObjectTypeCode();
        setCadastreObjectType(CacheManager.getBeanByCode(
                CacheManager.getCadastreObjectTypes(), value));
        propertySupport.firePropertyChange(PROPERTY_CADASTRE_OBJECT_TYPE_CODE, old, value);
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
            SpatialUnitTemporaryBean bean = new SpatialUnitTemporaryBean();
            bean.setTypeCode(code);
            bean.setCadastreObjectTypeCode(getCadastreObjectTypeCode());
            bean.setGeom(sourceObject.getTheGeom());
            try {
                bean.setOfficialArea(BigDecimal.valueOf(
                        Double.valueOf(sourceObject.getFieldsWithValues().get(
                        officialArea.getName()).toString())));
            } catch (NumberFormatException ex) {
                bean.setOfficialArea(null);
            }
            bean.setNameLastpart(getNameLastPart());
            if (!isGenerateFirstPart()) {
                bean.setNameFirstpart(
                        sourceObject.getFieldsWithValues().get(nameFirstPart.getName()).toString());
            }
            beans.add(bean);
        }
        return beans;
    }
}
