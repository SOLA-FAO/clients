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
package org.sola.clients.beans.utils;

import java.util.Comparator;
import org.sola.clients.beans.administrative.RrrBean;

/**
 * Compares 2 RrrBean by registration date in descending.
 */
public class RrrComparatorByRegistrationDate implements Comparator<RrrBean> {

    @Override
    public int compare(RrrBean o1, RrrBean o2) {
        if(o1.getRegistrationDate() == null && o2.getRegistrationDate()!=null){
            return 11;
        }
        
        if(o1.getRegistrationDate() != null && o2.getRegistrationDate()==null){
            return 1;
        }
        
        if(o1.getRegistrationDate() == null && o2.getRegistrationDate()==null){
            return 0;
        }
        
        return o2.getRegistrationDate().compareTo(o1.getRegistrationDate());
    }
    
}
