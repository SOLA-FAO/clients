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
package org.sola.clients.beans.source;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.validation.Localized;
import org.sola.common.messaging.ClientMessage;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.casemanagement.PowerOfAttorneyTO;

/**
 * Represents Power of attorney object.
 */
public class PowerOfAttorneyBean extends AbstractIdBean {
    public static final String SOURCE_PROPERTY = "source";
    public static final String PERSON_NAME_PROPERTY = "personName";
    public static final String ATTORNEY_NAME_PROPERTY = "attorneyName";
    
    @NotNull(payload=Localized.class, message=ClientMessage.SOURCE_MUST_BE_NOT_NULL)
    private SourceBean source;
    @NotEmpty(payload=Localized.class, message=ClientMessage.SOURCE_PERSON_NAME_NOT_NULL)
    private String personName;
    @NotEmpty(payload=Localized.class, message=ClientMessage.SOURCE_ATTORNEY_NAME_NOT_NULL)
    private String attorneyName;
    
    /** Default constructor. */
    public PowerOfAttorneyBean(){
        super();
    }
    
    /** 
     * Creates new instance of {@link PowerOfAttorneyBean} with a given source. 
     * @param source {@link SourceBean} to be attached to the created instance.
     */
    public PowerOfAttorneyBean(SourceBean source){
        super();
        this.source = source;
    }

    public String getAttorneyName() {
        return attorneyName;
    }

    public void setAttorneyName(String attorneyName) {
        String oldValue = this.attorneyName;
        this.attorneyName = attorneyName;
        propertySupport.firePropertyChange(ATTORNEY_NAME_PROPERTY, oldValue, this.attorneyName);
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        String oldValue = this.personName;
        this.personName = personName;
        propertySupport.firePropertyChange(PERSON_NAME_PROPERTY, oldValue, this.personName);
    }

    public SourceBean getSource() {
        return source;
    }

    public void setSource(SourceBean source) {
        SourceBean oldValue = this.source;
        this.source = source;
        propertySupport.firePropertyChange(SOURCE_PROPERTY, oldValue, this.source);
    }
    
    /**
     * Static method to attach Power of attorney to the transaction.
     *
     * @param serviceId Application service id, bound to transaction.
     * @param powerOfAttorney Power of attorney object, containing source.
     */
    public static PowerOfAttorneyBean attachToTransaction(PowerOfAttorneyBean powerOfAttorney, String serviceId) {
        PowerOfAttorneyBean result = null;
        PowerOfAttorneyTO to = WSManager.getInstance().getCaseManagementService()
                .attachPowerOfAttorneyToTransaction(serviceId, TypeConverters
                .BeanToTrasferObject(powerOfAttorney, PowerOfAttorneyTO.class));
        if (to != null) {
            result = new PowerOfAttorneyBean();
            TypeConverters.TransferObjectToBean(to, PowerOfAttorneyBean.class, result);
        }
        return result;
    }
    
    /** Returns Power of attorney object by provided id.*/
    public static PowerOfAttorneyBean getPowerOfAttorney(String id) {
        return TypeConverters.TransferObjectToBean(
                WSManager.getInstance().getCaseManagementService().getPowerOfAttorneyById(id),
                PowerOfAttorneyBean.class, null);
    }
}
