/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sola.clients.beans.source;

/** 
 * Contains properties used as the parameters to search Power of attorney.
 * Could be populated from the {@link PowerOfAttorneySearchParamsTO} object.<br />
 */
public class PowerOfAttorneySearchParamsBean extends SourceSearchParamsBean {
    
    public static final String ATTORNEY_NAME_PROPERTY ="attorneyName";
    public static final String PERSON_NAME_PROPERTY ="personName";
    
    private String attorneyName;
    private String personName;
    
    public PowerOfAttorneySearchParamsBean(){
        super();
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
}
