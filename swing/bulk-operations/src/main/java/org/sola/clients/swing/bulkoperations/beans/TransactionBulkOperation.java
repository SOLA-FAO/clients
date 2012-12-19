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
 *
 * @author Elton Manoku
 */
public abstract class TransactionBulkOperation extends AbstractIdBean {
    
    public TransactionBulkOperation(){
        super();
    }

    public abstract List<ValidationResultBean> save();

    public boolean reject() {
        return WSManager.getInstance().getBulkOperationsService().rejectTransaction(
                getId(), getRowVersion());
    }
}
