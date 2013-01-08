/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.List;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.transaction.TransactionBulkOperationSourceTO;

/**
 * The transaction that sends the documents (sources) to the server.
 * 
 * @author Elton Manoku
 */
public class TransactionBulkOperationSource extends TransactionBulkOperation {
    
    private List<SourceBean> sourceList;

    public List<SourceBean> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<SourceBean> sourceList) {
        this.sourceList = sourceList;
    }

    public TransactionBulkOperationSourceTO getTO() {
        return TypeConverters.BeanToTrasferObject(this, TransactionBulkOperationSourceTO.class);
    }
    
    @Override
    public List<ValidationResultBean> save() {
        return TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getBulkOperationsService().saveTransactionBulkOperationSource(
                this.getTO()), ValidationResultBean.class, null);
    }
}
