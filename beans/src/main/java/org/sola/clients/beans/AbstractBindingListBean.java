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

import java.util.List;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Used to create binding beans list.
 */
public abstract class AbstractBindingListBean extends AbstractBindingBean {

    public AbstractBindingListBean() {
        super();
    }

    /** 
     * Loads list of reference data beans.
     * @param loadToList List instance to load objects in.
     * @param loadFromList List instance to load objects from.
     * @param createDummy Indicates whether to add empty object on the list.
     */
    protected <T extends AbstractCodeBean> void loadCodeList(Class<T> codeBeanClass, 
            List<T> loadToList, List<T> loadFromList, boolean createDummy) {
        if (loadToList == null || loadFromList == null) {
            return;
        }

        loadToList.clear();

        for (T codeBean : loadFromList) {
            loadToList.add(codeBean);
        }

        if (createDummy) {

            try {
                T dummy = (T) codeBeanClass.newInstance();
                
                if (dummy != null) {
                    dummy.setDisplayValue(" ");
                    dummy.setEntityAction(EntityAction.DISASSOCIATE);
                    loadToList.add(0, dummy);
                }
            } catch (Exception ex) {
            }
        }
    }
}
