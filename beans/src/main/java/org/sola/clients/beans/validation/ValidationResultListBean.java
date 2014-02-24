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
package org.sola.clients.beans.validation;

import java.util.LinkedList;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;

/**
 * Holds the list of validation results
 */
public class ValidationResultListBean extends AbstractBindingBean{
    
    public static final String SELECTED_VALIDATION_RESULT = "selectedValidationResult";
            
    private ObservableList<ValidationResultBean> validationResutlList;
    private ValidationResultBean selectedValidationResult;
    
    public ValidationResultListBean(){
        super();
        validationResutlList = ObservableCollections.observableList(new LinkedList<ValidationResultBean>());
    }

    public ValidationResultBean getSelectedValidationResult() {
        return selectedValidationResult;
    }

    public void setSelectedValidationResult(ValidationResultBean selectedValidationResult) {
        this.selectedValidationResult = selectedValidationResult;
        propertySupport.firePropertyChange(SELECTED_VALIDATION_RESULT, null, selectedValidationResult);
    }

    public ObservableList<ValidationResultBean> getValidationResutlList() {
        return validationResutlList;
    }
    
    public void setValidationResultList(ObservableList<ValidationResultBean> list) {
        validationResutlList.clear();
        if(list!=null){
            for(ValidationResultBean bean: list){
                validationResutlList.add(bean);
            }
        }
    }
    
}
