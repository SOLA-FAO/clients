/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
import org.sola.webservices.transferobjects.referencedata.RrrSubTypeTO;

/**
 * Represents reference data object of the <b>rrr_sub_type</b> table. Could be
 * populated from the {@link RrrSubTypeTO} object.<br />
 * For more information see data dictionary <b>Administrative</b> schema.
 */
public class RrrSubTypeBean extends AbstractCodeBean {

    public static final String RRR_TYPE_CODE_PROPERTY = "rrrTypeCode";
    public static final String RRR_TYPE_PROPERTY = "rrrType";
    private RrrTypeBean rrrType;

    public RrrSubTypeBean() {
        super();
    }

    public String getRrrTypeCode() {
        if (rrrType != null) {
            return rrrType.getCode();
        } else {
            return null;
        }
    }

    public void setRrrTypeCode(String typeCode) {
        String oldValue = null;
        if (rrrType != null) {
            oldValue = rrrType.getCode();
        }
        setRrrType(CacheManager.getBeanByCode(CacheManager.getRrrTypes(), typeCode));
        propertySupport.firePropertyChange(RRR_TYPE_CODE_PROPERTY, oldValue, typeCode);
    }

    public RrrTypeBean getRrrType() {
        if (rrrType == null) {
            rrrType = new RrrTypeBean();
        }
        return rrrType;
    }

    public void setRrrType(RrrTypeBean rrrType) {
        if (this.rrrType == null) {
            this.rrrType = new RrrTypeBean();
        }
        this.setJointRefDataBean(this.rrrType, rrrType, RRR_TYPE_PROPERTY);
    }

}
