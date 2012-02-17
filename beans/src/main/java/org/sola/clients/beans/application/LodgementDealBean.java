/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.application;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.webservices.transferobjects.casemanagement.LodgementViewTO;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.casemanagement.LodgementViewParamsTO;

/**
 * Contains summary properties of the LodgementView object. Could be populated 
 * from the {@link LodgementViewTO} object.<br /> 
 * For more information see UC <b>Lodgement Report</b> schema.
 */
public class LodgementDealBean extends AbstractIdBean  {

    public static final String TYPE_PROPERTY = "resultType";
    public static final String GROUP_PROPERTY = "resultGroup";
    public static final String TOTAL_PROPERTY = "resultTotal";
    public static final String TOTAL_PERC_PROPERTY = "resultTotalPerc";
    public static final String DAILY_AVG_PROPERTY = "resultDailyAvg";
    public static final String TOTAL_REQUISITION_PROPERTY = "resultTotalReq";
    public static final String REQUISITION_PERC_PROPERTY = "resultReqPerc";
    private ObservableList<LodgementDealBean> lodgementList;
    private String  resultType;
    private String  resultGroup;
    private int resultTotal;
    private BigDecimal  resultTotalPerc;
    private BigDecimal  resultDailyAvg;
    private int resultTotalReq;
    private BigDecimal  resultReqPerc;
    


    public LodgementDealBean() {
//        super();
        lodgementList = ObservableCollections.observableList(new LinkedList<LodgementDealBean>());
    }
    
      
    public ObservableList<LodgementDealBean> getLodgementList() {
        return lodgementList;
    }
    

    public void setLodgementList(ObservableList<LodgementDealBean> lodgementList) {
        this.lodgementList = lodgementList;
    }

    public BigDecimal getResultDailyAvg() {
        return resultDailyAvg;
    }

    public void setResultDailyAvg(BigDecimal resultDailyAvg) {
        this.resultDailyAvg = resultDailyAvg;
    }

    public String getResultGroup() {
        return resultGroup;
    }

    public void setResultGroup(String resultGroup) {
        this.resultGroup = resultGroup;
    }

    public BigDecimal getResultReqPerc() {
        return resultReqPerc;
    }

    public void setResultReqPerc(BigDecimal resultReqPerc) {
        this.resultReqPerc = resultReqPerc;
    }

  

    public BigDecimal getResultTotalPerc() {
        return resultTotalPerc;
    }

    public void setResultTotalPerc(BigDecimal resultTotalPerc) {
        this.resultTotalPerc = resultTotalPerc;
    }

    public int getResultTotal() {
        return resultTotal;
    }

    public void setResultTotal(int resultTotal) {
         int old = resultTotal;
        this.resultTotal = resultTotal;
        propertySupport.firePropertyChange(TOTAL_PROPERTY, old, resultTotal);
//        this.resultTotal = resultTotal;
    }

    public int getResultTotalReq() {
        return resultTotalReq;
    }

    public void setResultTotalReq(int resultTotalReq) {
          int old = resultTotalReq;
        this.resultTotalReq = resultTotalReq;
        propertySupport.firePropertyChange(TOTAL_REQUISITION_PROPERTY, old, resultTotalReq);
//      this.resultTotalReq = resultTotalReq;
    }

   
    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

   
    /** 
     * Returns collection of {@link ApplicationBean} objects. This method is 
     * used by Jasper report designer to extract properties of application bean 
     * to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        LodgementDealBean bean = new LodgementDealBean();
        collection.add(bean);
        return collection;
    }


    //      /** Passes from date and to date search criteria. */
    public void passParameter(LodgementViewParamsBean params) {
//        applicationSearchResultsList.clear();
        LodgementViewParamsTO paramsTO = TypeConverters.BeanToTrasferObject(params,
                LodgementViewParamsTO.class);

        List<LodgementViewTO> lodgementViewTO =
                WSManager.getInstance().getCaseManagementService().getLodgementView(paramsTO);
        TypeConverters.TransferObjectListToBeanList(lodgementViewTO,
                LodgementDealBean.class, (List) getLodgementList());
    }
    
 


}