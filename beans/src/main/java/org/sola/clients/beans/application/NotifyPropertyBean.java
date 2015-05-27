/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.application;

import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.administrative.BaUnitSummaryBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.referencedata.NotifyRelationshipTypeBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.casemanagement.NotifyPropertyTO;
//import org.sola.webservices.transferobjects.casemanagement.NotifyPropertyTO;

/**
 * Represents objection comment object. Could be populated from the
 * {@link NotifyTO} object.<br /> For more information see data dictionary
 * <b>Application</b> schema.
 */
public class NotifyPropertyBean extends AbstractIdBean {

    public static final String NOTIFY_ID_PROPERTY = "notifyId";
    public static final String BAUNIT_ID_PROPERTY = "baUnitId";
    private String notifyId;
    private String baUnitId;
    private String cancelServiceId;
    private String status;

    public NotifyPropertyBean() {
        super();
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        this.baUnitId = baUnitId;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    public String getCancelServiceId() {
        return cancelServiceId;
    }

    public void setCancelServiceId(String cancelServiceId) {
        this.cancelServiceId = cancelServiceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Saves changes to the saveNotifyPropertyinto the database.
     *
     * @throws Exception
     */
    public boolean saveNotifyProperty(String notifyId, String baUnitId) {
        NotifyPropertyTO notifyProperty = TypeConverters.BeanToTrasferObject(this, NotifyPropertyTO.class);
        notifyProperty = WSManager.getInstance().getCaseManagementService().saveNotifyProperty(notifyProperty, notifyId, baUnitId);
        TypeConverters.TransferObjectToBean(notifyProperty, NotifyPropertyBean.class, this);
        return true;
    }

    /**
     * Saves changes to the saveNotifyPropertyinto the database.
     *
     * @throws Exception
     */
    public boolean cancelNotifyProperty(String notifyId, String baUnitId, NotifyPropertyBean notifiable) {
        System.out.println("BEAN NOTIFIABLE NOTIFY ID   "+notifiable.notifyId);
        System.out.println("BEAN NOTIFY ID   "+notifyId);
        System.out.println("BEAN BA UNIT ID   "+baUnitId);
        System.out.println("BEAN NOTIFY ID   "+notifiable.notifyId);
        NotifyPropertyTO notifyProperty = WSManager.getInstance().getCaseManagementService().getNotifyProperty(notifyId, baUnitId);
        notifyProperty.setCancelServiceId(notifiable.getCancelServiceId());
        notifyProperty.setStatus(notifiable.getStatus());
        notifyProperty.setEntityAction(EntityAction.UPDATE);
        System.out.println("BEAN NOTIFY ID   "+notifyProperty.getNotifyId());
        System.out.println("BEAN BA UNIT ID   "+notifyProperty.getBaUnitId());
        System.out.println("BEAN STATUS   "+notifyProperty.getStatus());
      
        notifyProperty = WSManager.getInstance().getCaseManagementService().saveNotifyProperty(notifyProperty, notifyId, baUnitId);
        
        System.out.println("DOPO BEAN NOTIFY ID   "+notifyProperty.getNotifyId());
        System.out.println("DOPO BA UNIT ID   "+notifyProperty.getBaUnitId());
        System.out.println("DOPO STATUS   "+notifyProperty.getStatus());
        System.out.println("DOPO STATUS   "+notifyProperty.getCancelServiceId());
      
        TypeConverters.TransferObjectToBean(notifyProperty, NotifyPropertyBean.class, this);
        return true;
    }
    
     /**
     * Saves changes to the saveNotifyPropertyinto the database.
     *
     * @throws Exception
     */
    public boolean removeCancelNotification(String notifyId, String baUnitId, NotifyPropertyBean notifiable) {
        NotifyPropertyTO notifyProperty = WSManager.getInstance().getCaseManagementService().getNotifyProperty(notifyId, baUnitId);
        notifyProperty.setCancelServiceId(notifiable.getCancelServiceId());
        notifyProperty.setStatus(notifiable.getStatus());
        notifyProperty.setEntityAction(EntityAction.UPDATE);
        notifyProperty = WSManager.getInstance().getCaseManagementService().saveNotifyProperty(notifyProperty, notifyId, baUnitId);
        TypeConverters.TransferObjectToBean(notifyProperty, NotifyPropertyBean.class, this);
        return true;
    }

    /**
     * Removes notifiable row.
     */
    public static void remove(String notifyId, String baUnitId) {
//        if(notifyId == null || baUnitId == null){
//            return;
//        }
        NotifyPropertyTO notifyProperty = WSManager.getInstance().getCaseManagementService().getNotifyProperty(notifyId, baUnitId);
        notifyProperty.setEntityAction(EntityAction.DELETE);
        WSManager.getInstance().getCaseManagementService().saveNotifyProperty(notifyProperty, notifyId, baUnitId);
    }
    
    
    
}
