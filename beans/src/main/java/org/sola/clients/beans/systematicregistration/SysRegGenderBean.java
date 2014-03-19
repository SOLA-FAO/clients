/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
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
