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

/**
 * Abstract bean for all beans representing database data tables and having ID 
 * field as a primary key or unique identifier.
 */
public abstract class AbstractIdBean extends AbstractVersionedBean {

    public static final String ID_PROPERTY = "id";
    
    private String id;
    
    public AbstractIdBean() {
        super();
        generateId();
    }

    /** 
     * Generates new ID, row version and row ID. Used to produce new object 
     * instance from existing one. 
     */
    public final void generateId(){
        setId(UUID.randomUUID().toString());
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String value)
    {
        String old = id;
        id = value;
        propertySupport.firePropertyChange(ID_PROPERTY, old, value);
    }
 
    @Override
    public boolean equals(Object aThat)
    {
       if(aThat==null) return  false;
       if ( this == aThat ) return true;
       if ( !(AbstractIdBean.class.isAssignableFrom(aThat.getClass()))) return false;
       
       AbstractIdBean that = (AbstractIdBean)aThat;
       
       if(this.getId() != null && that.getId() != null &&
               this.getId().equals(that.getId()))
           return true;
       else
           return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
