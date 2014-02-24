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
package org.sola.clients.beans.application;

import org.sola.clients.beans.AbstractBindingBean;

/** 
 * A simple bean class to create documents check list, to guide the user which 
 * documents should be put into the application documents list. 
 */
public class ApplicationDocumentCheckerBean extends AbstractBindingBean {
    
    public static final String DISPLAY_VALUE_PROPERTY = "displayValue";
    public static final String SOURCE_TYPE_CODE_PROPERTY = "sourceTypeCode";
    public static final String IS_IN_LIST_PROPERTY = "isInList";
    
    private String displayValue;
    private String sourceTypeCode;
    private boolean isInList;
    
    /** 
     * Creates object's instance.
     * @param displayValue Document type name.
     * @param sourceTypeCode Document type code.
     * @param isInList Boolean value indicating if this type of document was added 
     * into the application documents list.
     */
    public ApplicationDocumentCheckerBean(String displayValue, String sourceTypeCode, boolean isInList)
    {
        this.displayValue = displayValue;
        this.sourceTypeCode = sourceTypeCode;
        this.isInList = isInList;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String value) {
        String old= displayValue;
        displayValue = value;
        propertySupport.firePropertyChange(DISPLAY_VALUE_PROPERTY, old, value);
    }

    public boolean isIsInList() {
        return isInList;
    }

    public void setIsInList(boolean value) {
        boolean old= isInList;
        isInList = value;
        propertySupport.firePropertyChange(IS_IN_LIST_PROPERTY, old, value);
    }

    public String getSourceTypeCode() {
        return sourceTypeCode;
    }

    public void setSourceTypeCode(String value) {
        String old= sourceTypeCode;
        sourceTypeCode = value;
        propertySupport.firePropertyChange(SOURCE_TYPE_CODE_PROPERTY, old, value);
    }
    
}
