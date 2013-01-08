/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.List;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Abstract transaction that handles only the bulk operations. Every bulk operation
 * transaction must inherit from this class.
 * 
 * @author Elton Manoku
 */
public abstract class TransactionBulkOperation extends AbstractIdBean {
    
    public TransactionBulkOperation(){
        super();
    }

    /**
     * It saves the transaction in the server. It returns back the list of 
     * validation rules if any.
     * 
     * @return 
     */
    public abstract List<ValidationResultBean> save();

    /**
     * It rejects/ rolls back the transaction.
     * 
     * @return 
     */
    public boolean reject() {
        return WSManager.getInstance().getBulkOperationsService().rejectTransaction(
                getId(), getRowVersion());
    }
}
