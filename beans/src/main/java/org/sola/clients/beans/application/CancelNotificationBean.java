/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
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

import org.sola.clients.beans.application.*;
import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.webservices.transferobjects.casemanagement.CancelNotificationTO;

import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Contains properties and methods to manage <b>Notation</b> object of the
 * domain model. Could be populated from the {@link BaUnitNotationTO} object.
 */
public class CancelNotificationBean extends AbstractTransactionedBean {

    
    private String partyName;
    private String partyLastName;
    private String targetpartyName;
    private String targetpartyLastName;
    private String partyId;
    private String targetPartyId;
    private String baunitName;
    private String serviceId;

    public String getBaunitName() {
        return baunitName;
    }

    public void setBaunitName(String baunitName) {
        this.baunitName = baunitName;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPartyLastName() {
        return partyLastName;
    }

    public void setPartyLastName(String partyLastName) {
        this.partyLastName = partyLastName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTargetPartyId() {
        return targetPartyId;
    }

    public void setTargetPartyId(String targetPartyId) {
        this.targetPartyId = targetPartyId;
    }

    public String getTargetpartyLastName() {
        return targetpartyLastName;
    }

    public void setTargetpartyLastName(String targetpartyLastName) {
        this.targetpartyLastName = targetpartyLastName;
    }

    public String getTargetpartyName() {
        return targetpartyName;
    }

    public void setTargetpartyName(String targetpartyName) {
        this.targetpartyName = targetpartyName;
    }

    /**
     * Returns notifiable party by parameters.
     */
    public static CancelNotificationBean getCancelNotification(String partyId, String targetPartyId, String banunitName, String application, String service) {
        CancelNotificationTO cancelNotificationTO = WSManager.getInstance().getCaseManagementService().getCancelNotification(partyId, targetPartyId, banunitName, application, service);
        
        return TypeConverters.TransferObjectToBean(cancelNotificationTO, CancelNotificationBean.class, null);
    }
    
}
