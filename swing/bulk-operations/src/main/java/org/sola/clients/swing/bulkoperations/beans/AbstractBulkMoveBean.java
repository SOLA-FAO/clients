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
