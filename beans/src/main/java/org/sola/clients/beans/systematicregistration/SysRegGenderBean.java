/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.systematicregistration;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.webservices.transferobjects.administrative.SysRegGenderTO;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Contains summary properties of the Gender Report object. Could be populated
 * from the {@link GenderTO} object.<br /> For more information see UC
 * <b>Gender Report</b> schema.
 */
public class SysRegGenderBean extends AbstractIdBean {

    private ObservableList<SysRegGenderBean> genderList;
     private String parcel;
    private BigDecimal total;
    private BigDecimal totFem;
    private BigDecimal totMale;
    private BigDecimal totMixed;
    private BigDecimal totJoint;
    private BigDecimal totEntity;
    private BigDecimal totNull;
    
    
    
    public SysRegGenderBean() {
        super();
        genderList = ObservableCollections.observableList(new LinkedList<SysRegGenderBean>());
    }
    
    public ObservableList<SysRegGenderBean> getGenderList() {
        return genderList;
    }

    public void setGenderList(ObservableList<SysRegGenderBean> genderList) {
        this.genderList = genderList;
    }

    public String getParcel() {
        return parcel;
    }

    public void setParcel(String parcel) {
        this.parcel = parcel;
    }

    public BigDecimal getTotEntity() {
        return totEntity;
    }

    public void setTotEntity(BigDecimal totEntity) {
        this.totEntity = totEntity;
    }

    public BigDecimal getTotFem() {
        return totFem;
    }

    public void setTotFem(BigDecimal totFem) {
        this.totFem = totFem;
    }

    public BigDecimal getTotJoint() {
        return totJoint;
    }

    public void setTotJoint(BigDecimal totJoint) {
        this.totJoint = totJoint;
    }

    public BigDecimal getTotMale() {
        return totMale;
    }

    public void setTotMale(BigDecimal totMale) {
        this.totMale = totMale;
    }

    public BigDecimal getTotMixed() {
        return totMixed;
    }

    public void setTotMixed(BigDecimal totMixed) {
        this.totMixed = totMixed;
    }

    public BigDecimal getTotNull() {
        return totNull;
    }

    public void setTotNull(BigDecimal totNull) {
        this.totNull = totNull;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
     /**
     * Returns collection of {@link ApplicationBean} objects. This method is
     * used by Jasper report designer to extract properties of application bean
     * to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        SysRegGenderBean bean = new SysRegGenderBean();
        collection.add(bean);
        return collection;
    }
    
    
       //      /** Passes from date and to date search criteria. */
    public void passParameter(String params) {
           
        List<SysRegGenderTO> genderTO =
                WSManager.getInstance().getAdministrative().getSysRegGender(params);
        TypeConverters.TransferObjectListToBeanList(genderTO,SysRegGenderBean.class, (List) this.getGenderList());
    }
}
