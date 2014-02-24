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
package org.sola.clients.beans;

import javax.swing.JOptionPane;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.RegistrationStatusTypeBean;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Abstract bean for all beans having registration status.
 *
 * @see RegistrationStatusTypeBean
 */
public abstract class AbstractTransactionedBean extends AbstractIdBean {

    public static final String STATUS_CODE_PROPERTY = "statusCode";
    public static final String STATUS_PROPERTY = "status";
    public static final String IS_LOCKED_PROPERTY = "isLocked";
    private RegistrationStatusTypeBean status;
    private boolean locked;

    public AbstractTransactionedBean() {
        super();
    }

    public String getStatusCode() {
        if (status != null) {
            return status.getCode();
        } else {
            return null;
        }
    }

    public void setStatusCode(String statusCode) {
        String oldValue = null;
        if (status != null) {
            oldValue = status.getCode();
        }
        if (WSManager.getInstance().getReferenceDataService() != null) {
            setStatus(CacheManager.getBeanByCode(
                    CacheManager.getRegistrationStatusTypes(), statusCode));
            propertySupport.firePropertyChange(STATUS_CODE_PROPERTY, oldValue, statusCode);
        }
    }

    public RegistrationStatusTypeBean getStatus() {
        return status;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        boolean oldValue = this.locked;
        this.locked = locked;
        propertySupport.firePropertyChange(IS_LOCKED_PROPERTY, oldValue, locked);
    }

    public void setStatus(RegistrationStatusTypeBean status) {
        if (this.status == null) {
            this.status = new RegistrationStatusTypeBean();
        }
        this.setJointRefDataBean(this.status, status, STATUS_PROPERTY);
    }
}
