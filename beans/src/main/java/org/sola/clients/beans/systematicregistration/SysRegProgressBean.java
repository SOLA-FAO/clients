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
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.SysRegManagementParamsTO;
import org.sola.webservices.transferobjects.administrative.SysRegProgressTO;

/**
 * Contains summary properties of the LodgementView object. Could be populated
 * from the {@link LodgementViewTO} object.<br /> For more information see UC
 * <b>Lodgement Report</b> schema.
 */
public class SysRegProgressBean extends AbstractIdBean {
    
    private ObservableList<SysRegProgressBean> progressList;
    private String block;
    private BigDecimal TotAppLod;
    private BigDecimal TotParcLoaded;
    private BigDecimal TotRecObj;
    private BigDecimal TotSolvedObj;
    private BigDecimal TotAppPDisp;
    private BigDecimal TotPrepCertificate;
    private BigDecimal TotIssuedCertificate;
    
     public SysRegProgressBean() {
        super();
        progressList = ObservableCollections.observableList(new LinkedList<SysRegProgressBean>());
    }

    public BigDecimal getTotAppLod() {
        return TotAppLod;
    }

    public void setTotAppLod(BigDecimal TotAppLod) {
        this.TotAppLod = TotAppLod;
    }

    public BigDecimal getTotAppPDisp() {
        return TotAppPDisp;
    }

    public void setTotAppPDisp(BigDecimal TotAppPDisp) {
        this.TotAppPDisp = TotAppPDisp;
    }

    public BigDecimal getTotIssuedCertificate() {
        return TotIssuedCertificate;
    }

    public void setTotIssuedCertificate(BigDecimal TotIssuedCertificate) {
        this.TotIssuedCertificate = TotIssuedCertificate;
    }

    public BigDecimal getTotParcLoaded() {
        return TotParcLoaded;
    }

    public void setTotParcLoaded(BigDecimal TotParcLoaded) {
        this.TotParcLoaded = TotParcLoaded;
    }

    public BigDecimal getTotPrepCertificate() {
        return TotPrepCertificate;
    }

    public void setTotPrepCertificate(BigDecimal TotPrepCertificate) {
        this.TotPrepCertificate = TotPrepCertificate;
    }

    public BigDecimal getTotRecObj() {
        return TotRecObj;
    }

    public void setTotRecObj(BigDecimal TotRecObj) {
        this.TotRecObj = TotRecObj;
    }

    public BigDecimal getTotSolvedObj() {
        return TotSolvedObj;
    }

    public void setTotSolvedObj(BigDecimal TotSolvedObj) {
        this.TotSolvedObj = TotSolvedObj;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public ObservableList<SysRegProgressBean> getProgressList() {
        return progressList;
    }

    public void setProgressList(ObservableList<SysRegProgressBean> progressList) {
        this.progressList = progressList;
    }
    
        /**
     * Returns collection of {@link ApplicationBean} objects. This method is
     * used by Jasper report designer to extract properties of application bean
     * to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        SysRegStatusBean bean = new SysRegStatusBean();
        collection.add(bean);
        return collection;
    }

    //      /** Passes from date and to date search criteria. */
    public void passParameter(SysRegManagementParamsBean params) {
        SysRegManagementParamsTO paramsTO = TypeConverters.BeanToTrasferObject(params,
                SysRegManagementParamsTO.class);
           
        List<SysRegProgressTO> progressViewTO =
                WSManager.getInstance().getAdministrative().getSysRegProgress(paramsTO);
        TypeConverters.TransferObjectListToBeanList(progressViewTO,
                SysRegProgressBean.class, (List) this.getProgressList());
    }
}
