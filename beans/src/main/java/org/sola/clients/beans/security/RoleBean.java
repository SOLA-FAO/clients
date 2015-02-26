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
package org.sola.clients.beans.security;

import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.security.RoleTO;

/**
 * Represents role, which could be assigned to the group of users. Could be
 * populated from the {@link RoleTO} object.<br /> For more information see data
 * dictionary <b>approle</b> table in the <b>System</b> schema. <br />This bean
 * is used as a part of {@link GroupBean}.
 */
public class RoleBean extends AbstractCodeBean {

    private transient String plainDisplayValue;

    public RoleBean() {
        super();
    }

    /**
     * Saves role.
     */
    public void save() {
        RoleTO roleTO = TypeConverters.BeanToTrasferObject(this, RoleTO.class);
        WSManager.getInstance().getAdminService().saveRole(roleTO);
        TypeConverters.TransferObjectToBean(roleTO, RoleBean.class, this);
    }

    /**
     * Processes the translated display value for the role and removes any
     * reference to a role group
     *
     * @return
     */
    public String getPlainDisplayValue() {
        String displayValue = this.getTranslatedDisplayValue();
        if (displayValue != null) {
            int idx = displayValue.indexOf("-");
            if (idx >= 0) {
                displayValue = displayValue.substring(idx + 1).trim();
            }
        }
        return displayValue;
    }

    /**
     * Returns true if the role is a security role. All security roles must have
     * a code that begins with 2 numeric digits (used as a display order)
     * followed by the text SEC_
     *
     * @return
     */
    public boolean isSecurityRole() {
        return getCode().matches("^\\d\\dSEC_.*");
    }

    @Override
    public String toString() {
        return getPlainDisplayValue();
    }
}
