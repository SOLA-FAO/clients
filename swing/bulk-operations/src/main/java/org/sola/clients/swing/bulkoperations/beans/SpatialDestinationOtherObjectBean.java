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

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.sola.clients.swing.bulkoperations.spatialobjects.SpatialDestinationOtherObjectPanel;

/**
 * The spatial destination type other object. 
 * This destination type is chosen to convert the source features to 
 * other object complaint beans. The other object type can be anything rather than
 * cadastre object.
 * 
 * @author Elton Manoku
 */
public class SpatialDestinationOtherObjectBean extends SpatialDestinationBean {

    private static String PROPERTY_TYPE = "type";
    private static String PROPERTY_LABEL_ATTRIBUTE = "labelAttribute";
    
    private String type;
    private SpatialAttributeBean labelAttribute;
    
    
    private String code = "other_object";
    private String displayValue = "Other object";

    public SpatialDestinationOtherObjectBean() {
        super();
        setCode(code);
        setDisplayValue(displayValue);
    }

    @Override
    public String getPanelName() {
        return SpatialDestinationOtherObjectPanel.PANEL_NAME;
    }

    @NotNull(message = "Attribute to give the label value is missing")
    public SpatialAttributeBean getLabelAttribute() {
        return labelAttribute;
    }

    public void setLabelAttribute(SpatialAttributeBean value) {
        SpatialAttributeBean old = this.labelAttribute;
        this.labelAttribute = value;
        propertySupport.firePropertyChange(PROPERTY_LABEL_ATTRIBUTE, old, value);
    }

    @NotNull(message = "Type of the object is missing")
    public String getType() {
        return type;
    }

    public void setType(String value) {
        String old = this.type;
        this.type = value;
        propertySupport.firePropertyChange(PROPERTY_TYPE, old, value);
    }
    

    @Override
    public List<SpatialUnitTemporaryBean> getBeans(SpatialSourceBean fromSource) {
        List<SpatialUnitTemporaryBean> beans = new ArrayList<SpatialUnitTemporaryBean>();
        List<SpatialAttributeBean> onlyAttributes = new ArrayList<SpatialAttributeBean>();
        onlyAttributes.add(labelAttribute);

        for (SpatialSourceObjectBean sourceObject : fromSource.getFeatures(onlyAttributes)) {
            SpatialUnitTemporaryBean bean = new  SpatialUnitTemporaryBean();
            bean.setTypeCode(getType());
            bean.setLabel(
                    sourceObject.getFieldsWithValues().get(labelAttribute.getName()).toString());
            bean.setGeom(sourceObject.getTheGeom());            
            beans.add(bean);
        }
        return beans;
    }
}
