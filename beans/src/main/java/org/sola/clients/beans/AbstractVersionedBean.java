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

import java.util.UUID;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Abstract bean for all beans representing versioned data through the 
 * <code>rowVerion</code> value.
 */
public class AbstractVersionedBean extends AbstractBindingBean {
    
    public static final String ROW_VERSION_PROPERTY = "rowVersion";
    private int rowVersion;
    private String rowId;
    
    public AbstractVersionedBean() {
        super();
    }

    public String getRowId() {
        if(rowId==null || rowId.length()<1){
            generateRowId();
        }
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public int getRowVersion() {
        return rowVersion;
    }

    public void generateRowId(){
        setRowId(UUID.randomUUID().toString());
    }
    
    public boolean isNew() {
        boolean result = true;
        if(rowVersion>0){
            result = false;
        }
        return result;
    }

    public void setRowVersion(int value) {
        int oldValue = value;
        rowVersion = value;
        propertySupport.firePropertyChange(ROW_VERSION_PROPERTY, oldValue, value);
    }
    
    public void resetVersion(){
        setRowVersion(0);
        setRowId(UUID.randomUUID().toString());
        setEntityAction(EntityAction.INSERT);
    }
    
    @Override
    public boolean equals(Object aThat)
    {
       if(aThat==null) return  false;
       if ( this == aThat ) return true;
       if ( !(AbstractVersionedBean.class.isAssignableFrom(aThat.getClass()))) return false;
       
       AbstractVersionedBean that = (AbstractVersionedBean)aThat;
       
       if(this.getRowId() != null && that.getRowId() != null &&
               this.getRowId().equals(that.getRowId()))
           return true;
       else
           return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.rowId != null ? this.rowId.hashCode() : 0);
        return hash;
    }
    
}
