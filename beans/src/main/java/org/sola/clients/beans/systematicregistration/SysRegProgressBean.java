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
    private String TotParcLoaded;
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

    public String getTotParcLoaded() {
        return TotParcLoaded;
    }

    public void setTotParcLoaded(String TotParcLoaded) {
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
