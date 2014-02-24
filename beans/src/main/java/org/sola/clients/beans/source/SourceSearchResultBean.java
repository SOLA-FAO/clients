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
package org.sola.clients.beans.source;

import org.sola.webservices.transferobjects.search.SourceSearchResultTO;

/**
 * Represents search result of sources. Could be populated 
 * from the {@link SourceSearchResultTO} object.<br /> 
 */
public class SourceSearchResultBean extends SourceSummaryBean {
    private String statusDisplayValue;
    private String typeDisplayValue;
    private String statusCode;
    private String typeCode;
    
    public SourceSearchResultBean(){
        super();
    }

    public String getStatusDisplayValue() {
        return statusDisplayValue;
    }

    public void setStatusDisplayValue(String statusDisplayValue) {
        this.statusDisplayValue = statusDisplayValue;
    }

    public String getTypeDisplayValue() {
        return typeDisplayValue;
    }

    public void setTypeDisplayValue(String typeDisplayValue) {
        this.typeDisplayValue = typeDisplayValue;
    }
    
    @Override
    public String getStatusCode(){
        return statusCode;
    }
    
    @Override
    public void setStatusCode(String statusCode){
        this.statusCode = statusCode;
    }
    
    @Override
    public String getTypeCode() {
        return this.typeCode;
    }

    @Override
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
