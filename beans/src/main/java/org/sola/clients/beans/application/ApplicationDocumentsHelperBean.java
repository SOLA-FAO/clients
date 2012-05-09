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

import java.util.Iterator;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.referencedata.ApplicationStatusTypeBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.referencedata.RequestTypeSourceTypeBean;
import org.sola.clients.beans.referencedata.ServiceStatusTypeBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.services.boundary.wsclients.WSManager;
/** 
 * Provides methods and properties to manage documents check list to assist the
 * user while entering application documents.
 */
public class ApplicationDocumentsHelperBean extends AbstractBindingBean {

    private List<RequestTypeBean> requestTypeSourceList;
    private SolaObservableList<ApplicationDocumentCheckerBean> checkList;

    /** 
     * Creates object's instance and initializes documents check list 
     * ({@link SolaObservableList}&lt;{@link ApplicationDocumentCheckerBean}&gt;).
     */
    public ApplicationDocumentsHelperBean() {
        checkList = new SolaObservableList<ApplicationDocumentCheckerBean>();
        if(WSManager.getInstance().getReferenceDataService()!=null){
            requestTypeSourceList = CacheManager.getRequestTypes();
        }
    }

    /**
     * Updates documents check list, based on the application services list and 
     * the list of documents which were already added into the application. 
     * Each application service has the list of required document types.
     * @param applicationServiceList The list of application services.
     * @param applicationSourceList The list of application documents.
     */
    public void updateCheckList(List<ApplicationServiceBean> applicationServiceList,
            List<SourceBean> applicationSourceList) {
        if (applicationServiceList != null && requestTypeSourceList != null && checkList != null) {
            checkList.clear();

            for (Iterator<ApplicationServiceBean> it = applicationServiceList.iterator(); it.hasNext();) {
                ApplicationServiceBean applicationServiceBean = it.next();

                if(applicationServiceBean.getStatusCode() == null || (applicationServiceBean.getStatusCode()!=null &&
                        applicationServiceBean.getStatusCode().equalsIgnoreCase(ServiceStatusTypeBean.STATUS_CODE_CANCELLED))){
                    continue;
                }
                
                for (Iterator<RequestTypeBean> it1 = CacheManager.getRequestTypes().iterator(); it1.hasNext();) {
                    RequestTypeBean requestType = it1.next();

                    if (applicationServiceBean.getRequestTypeCode().equals(requestType.getCode())) {

                        for (Iterator<RequestTypeSourceTypeBean> it2 = requestType.getSourceTypeCodes().iterator(); it2.hasNext();) {
                            RequestTypeSourceTypeBean requestTypeSourceTypeCode = it2.next();

                            if (!isCodeInCheckList(requestTypeSourceTypeCode.getSourceTypeCode())) {
                                checkList.add(new ApplicationDocumentCheckerBean(
                                        CacheManager.getSourceTypesMap().get(requestTypeSourceTypeCode.getSourceTypeCode()).toString(),
                                        requestTypeSourceTypeCode.getSourceTypeCode(), false));
                            }
                        }
                        break;
                    }
                }
            }
            verifyCheckList(applicationSourceList);
        }
    }

    /**
     * Verifies application documents list against the required documents check list.
     * @param applicationSourceList The list of application documents to verify.
     */
    public void verifyCheckList(List<SourceBean> applicationSourceList) {
        if (applicationSourceList != null && checkList != null) {

            for (Iterator<ApplicationDocumentCheckerBean> it = checkList.iterator(); it.hasNext();) {
                ApplicationDocumentCheckerBean applicationDocumentChecker = it.next();
                boolean isInList = false;

                for (Iterator<SourceBean> it1 = applicationSourceList.iterator(); it1.hasNext();) {
                    SourceBean sourceBean = it1.next();

                    if (applicationDocumentChecker.getSourceTypeCode().equals(sourceBean.getTypeCode())) {
                        isInList = true;
                        break;
                    }
                }
                applicationDocumentChecker.setIsInList(isInList);
            }
        }
    }

    public boolean isAllItemsChecked() {
        if (checkList != null) {
            for (Iterator<ApplicationDocumentCheckerBean> it = checkList.iterator(); it.hasNext();) {
                ApplicationDocumentCheckerBean applicationDocumentChecker = it.next();
                if (!applicationDocumentChecker.isIsInList()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isCodeInCheckList(String code) {
        if (checkList != null) {
            for (Iterator<ApplicationDocumentCheckerBean> it = checkList.iterator(); it.hasNext();) {
                ApplicationDocumentCheckerBean applicationDocumentChecker = it.next();
                if (applicationDocumentChecker.getSourceTypeCode().equals(code)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Returns documents check list.*/
    public ObservableList<ApplicationDocumentCheckerBean> getCheckList() {
        return checkList;
    }
}
