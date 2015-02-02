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
package org.sola.clients.beans.administrative;

import org.sola.clients.beans.AbstractTransactionedBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.NotifiablePartyForBaUnitTO;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Contains properties and methods to manage <b>Notation</b> object of the
 * domain model. Could be populated from the {@link BaUnitNotationTO} object.
 */
public class NotifiablePartyForBaUnitBean extends AbstractTransactionedBean {

    public static final String APPLICATION_ID_PROPERTY = "applicationId";
    public static final String BAUNIT_ID_PROPERTY = "baunitId";
    public static final String BAUNIT_NAME_PROPERTY = "baunitName";
    public static final String STATUS_PROPERTY = "statusParty";
    public static final String PARTY_ID_PROPERTY = "partyId";
    public static final String TARGET_PARTY_ID_PROPERTY = "targetPartyId";
    public static final String NOTIFIABLE_PARTY_PROPERTY = "notifiableParty";
    private String partyId;
    private String targetPartyId;
    private String baunitName;
    private String statusParty;
    private String baunitId;
    private String applicationId;
    private String groupId;

    /**
     * Returns notifiable party by parameters.
     */
    public static NotifiablePartyForBaUnitBean getNotifiableParty(String partyId, String targetPartyId, String banunitName, String application) {
        NotifiablePartyForBaUnitTO partyTO = WSManager.getInstance().getAdministrative().getNotifiableParty(partyId, targetPartyId, banunitName, application);
        return TypeConverters.TransferObjectToBean(partyTO, NotifiablePartyForBaUnitBean.class, null);
    }

    /**
     * Saves notifiable party into the database.
     *
     * @throws Exception
     */
    public boolean saveNotifiableParty() {
        NotifiablePartyForBaUnitTO notifiableParty = TypeConverters.BeanToTrasferObject(this, NotifiablePartyForBaUnitTO.class);
        notifiableParty = WSManager.getInstance().getAdministrative().saveNotifiableParty(notifiableParty);
        TypeConverters.TransferObjectToBean(notifiableParty, NotifiablePartyForBaUnitBean.class, this);
        return true;
    }
    
    
    /** Removes party. */
    public static void remove(String partyId, String targetPartyId, String banunitName, String application) {
        if(partyId == null || partyId.length()<1){
            return;
        }
        NotifiablePartyForBaUnitTO notifiableParty = WSManager.getInstance().getAdministrative().getNotifiableParty(partyId, targetPartyId, banunitName, application);
        notifiableParty.setEntityAction(EntityAction.DELETE);
        WSManager.getInstance().getAdministrative().saveNotifiableParty(notifiableParty);
    }


    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {

        String oldValue = this.applicationId;
        this.applicationId = applicationId;
        propertySupport.firePropertyChange(APPLICATION_ID_PROPERTY, oldValue, applicationId);
    }

    public String getBaunitId() {
        return baunitId;
    }

    public void setBaunitId(String baunitId) {
        String oldValue = this.baunitId;
        this.baunitId = baunitId;
        propertySupport.firePropertyChange(BAUNIT_ID_PROPERTY, oldValue, baunitId);
    }

    public String getBaunitName() {
        return baunitName;
    }

    public void setBaunitName(String baunitName) {
        String oldValue = this.baunitName;
        this.baunitName = baunitName;
        propertySupport.firePropertyChange(BAUNIT_NAME_PROPERTY, oldValue, baunitName);
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        String oldValue = this.partyId;
        this.partyId = partyId;
        propertySupport.firePropertyChange(PARTY_ID_PROPERTY, oldValue, partyId);
    }

    public String getStatusParty() {
        return statusParty;
    }

    public void setStatusParty(String statusParty) {
        String oldValue = this.statusParty;
        this.statusParty = statusParty;
        propertySupport.firePropertyChange(STATUS_PROPERTY, oldValue, statusParty);
    }

    public String getTargetPartyId() {
        return targetPartyId;
    }

    public void setTargetPartyId(String targetPartyId) {
        String oldValue = this.targetPartyId;
        this.targetPartyId = targetPartyId;
        propertySupport.firePropertyChange(TARGET_PARTY_ID_PROPERTY, oldValue, targetPartyId);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
