/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.beans;

import java.util.List;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.transaction.TransactionBulkOperationSpatialTO;

/**
 * The transaction that sends the spatial objects to the server.
 *
 * @author Elton Manoku
 */
public class TransactionBulkOperationSpatial extends TransactionBulkOperation {
    
    private boolean generateFirstPart = false;
    private List<SpatialUnitTemporaryBean> spatialUnitTemporaryList;

    public boolean isGenerateFirstPart() {
        return generateFirstPart;
    }

    public void setGenerateFirstPart(boolean generateFirstPart) {
        this.generateFirstPart = generateFirstPart;
    }
    
    public List<SpatialUnitTemporaryBean> getSpatialUnitTemporaryList() {
        return spatialUnitTemporaryList;
    }

    public void setSpatialUnitTemporaryList(List<SpatialUnitTemporaryBean> spatialUnitTemporaryList) {
        this.spatialUnitTemporaryList = spatialUnitTemporaryList;
    }
    public TransactionBulkOperationSpatialTO getTO() {
        return TypeConverters.BeanToTrasferObject(this, TransactionBulkOperationSpatialTO.class);
    }
    
    @Override
    public List<ValidationResultBean> save() {
        return TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getBulkOperationsService().saveTransactionBulkOperationSpatial(
                this.getTO()), ValidationResultBean.class, null);
    }
    
}
