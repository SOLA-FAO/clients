/*
 * Copyright 2013 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sola.clients.beans.administrative;

import java.util.Date;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.referencedata.RrrTypeBean;

/**
 * Parameters used to query list of land rights for further export.
 */
public class RightsExportParamsBean extends AbstractBindingBean {
    public static final String DATE_FROM_PROPERTY = "dateFrom";
    public static final String DATE_TO_PROPERTY = "dateTo";
    public static final String RIGHT_TYPE_PROPERTY = "rightType";
    
    private Date dateFrom;
    private Date dateTo;
    private RrrTypeBean rightType;
    
    public RightsExportParamsBean(){
        super();
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        Date oldValue = this.dateFrom;
        this.dateFrom = dateFrom;
        propertySupport.firePropertyChange(DATE_FROM_PROPERTY, oldValue, this.dateFrom);
    }

    public Date getDateTo() {
        return this.dateTo;
    }

    public void setDateTo(Date dateTo) {
        Date oldValue = this.dateTo;
        this.dateTo = dateTo;
        propertySupport.firePropertyChange(DATE_TO_PROPERTY, oldValue, this.dateTo);
    }

    public String getRightTypeCode(){
        if(getRightType()==null){
            return null;
        } else {
            return getRightType().getCode();
        }
    }
    
    public RrrTypeBean getRightType() {
        return rightType;
    }

    public void setRightType(RrrTypeBean rightType) {
        RrrTypeBean oldValue = this.rightType;
        this.rightType = rightType;
        propertySupport.firePropertyChange(RIGHT_TYPE_PROPERTY, oldValue, this.rightType);
    }
}
