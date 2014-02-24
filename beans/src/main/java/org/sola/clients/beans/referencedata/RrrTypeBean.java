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
package org.sola.clients.beans.referencedata;

import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.validation.CodeBeanNotEmpty;
import org.sola.webservices.transferobjects.referencedata.RrrTypeTO;

/** 
 * Represents reference data object of the <b>rrr_type</b> table. 
 * Could be populated from the {@link RrrTypeTO} object.<br />
 * For more information see data dictionary <b>Administrative</b> schema.
 */
public class RrrTypeBean extends AbstractCodeBean {
    public static final String RRR_GROUP_TYPE_PROPERTY = "rrrGroupType";
    public static final String RRR_GROUP_TYPE_CODE_PROPERTY = "rrrGroupTypeCode";
    public static final String IS_PRIMARY_TYPE_PROPERTY = "primary";
    public static final String PARTY_REQUIRED_PROPERTY = "partyRequired";
    public static final String SHARE_CHECK_PROPERTY = "shareCheck";
    
    @CodeBeanNotEmpty(message="Select group type.")
    private RrrGroupTypeBean rrrGroupType;
    private boolean primary;
    private boolean partyRequired;
    private boolean shareCheck;
    
    public RrrTypeBean() {
        super();
        rrrGroupType = new RrrGroupTypeBean();
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean value) {
        boolean old = primary;
        primary = value;
        propertySupport.firePropertyChange(IS_PRIMARY_TYPE_PROPERTY, old, value);
    }

    public boolean isPartyRequired() {
        return partyRequired;
    }

    public void setPartyRequired(boolean value) {
        boolean old = partyRequired;
        partyRequired = value;
        propertySupport.firePropertyChange(PARTY_REQUIRED_PROPERTY, old, value);
    }

    public RrrGroupTypeBean getRrrGroupType() {
        return rrrGroupType;
    }

    public void setRrrGroupType(RrrGroupTypeBean value) {
        rrrGroupType = value;
        propertySupport.firePropertyChange(RRR_GROUP_TYPE_PROPERTY, null, value);
    }

    public String getRrrGroupTypeCode() {
        if(rrrGroupType!=null){
            return rrrGroupType.getCode();
        }else{
            return null;
        }
    }

    public void setRrrGroupTypeCode(String value) {
        String old=null;
        if(rrrGroupType!=null){
            old = rrrGroupType.getCode();
        }
        setRrrGroupType(CacheManager.getBeanByCode(
                CacheManager.getRrrGroupTypes(), value));
        propertySupport.firePropertyChange(RRR_GROUP_TYPE_CODE_PROPERTY, old, value);
    }

    public boolean isShareCheck() {
        return shareCheck;
    }

    public void setShareCheck(boolean value) {
        boolean old = shareCheck;
        shareCheck = value;
        propertySupport.firePropertyChange(SHARE_CHECK_PROPERTY, old, value);
    }

}

