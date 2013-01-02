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
 *
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

    public void reset() {
        this.transaction = null;
        validationResults.clear();
    }
    
    public abstract void sendToServer();
}
