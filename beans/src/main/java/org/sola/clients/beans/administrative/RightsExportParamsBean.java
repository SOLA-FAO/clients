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
