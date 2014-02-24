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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.validation.ValidationResultBean;

/**
 * This class is used as the abstract class for all bulk move beans. It encapsulates 
 * common functionality and properties used in all bulk move beans.
 * @author Elton Manoku
 */
public abstract class AbstractBulkMoveBean extends AbstractBindingBean {

    private ObservableList<ValidationResultBean> validationResults =
            new SolaObservableList<ValidationResultBean>();
    private TransactionBulkOperation transaction = null;

    public TransactionBulkOperation getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionBulkOperation transaction) {
        this.transaction = transaction;
    }

    public ObservableList<ValidationResultBean> getValidationResults() {
        return validationResults;
    }

    /**
     * It resets the bulk operation by setting the associated transaction to null
     * and clearing validation result list.
     */
    public void reset() {
        this.transaction = null;
        validationResults.clear();
    }
    
    /**
     * It starts the process of bulk upload. After the method is finished,
     * the transaction property must be present and the validation result list
     * is populated with validation rules. <br/>
     * For each kind of bulk move, it has to be implemented.
     */
    public abstract void sendToServer();
}
