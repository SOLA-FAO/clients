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

import org.sola.clients.beans.AbstractBindingBean;

/** 
 * Holds source id, related to the request type ({@link RequestTypeBean}). 
 */
public class RequestTypeSourceTypeBean extends AbstractBindingBean {
    public static final String REQUEST_TYPE_CODE_PROPERTY = "requestTypeCode";
    public static final String SOURCE_TYPE_CODE_PROPERTY = "sourceTypeCode";
    
    private String requestTypeCode;
    private String sourceTypeCode;
    
    public RequestTypeSourceTypeBean(){
        super();
    }

    public RequestTypeSourceTypeBean(String sourceTypeCode){
        super();
        this.sourceTypeCode = sourceTypeCode;
    }
    
    public String getRequestTypeCode() {
        return requestTypeCode;
    }

    public void setRequestTypeCode(String requestTypeCode) {
        String oldValue = this.requestTypeCode;
        this.requestTypeCode = requestTypeCode;
        propertySupport.firePropertyChange(REQUEST_TYPE_CODE_PROPERTY, oldValue, this.requestTypeCode);
    }

    public String getSourceTypeCode() {
        return sourceTypeCode;
    }

    public void setSourceTypeCode(String sourceTypeCode) {
        String oldValue = this.sourceTypeCode;
        this.sourceTypeCode = sourceTypeCode;
        propertySupport.firePropertyChange(SOURCE_TYPE_CODE_PROPERTY, oldValue, this.sourceTypeCode);
    }
}