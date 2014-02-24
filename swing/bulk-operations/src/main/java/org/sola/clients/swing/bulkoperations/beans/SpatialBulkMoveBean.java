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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotNull;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.swing.gis.beans.TransactionCadastreChangeBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;

/**
 * It is used to upload spatial features from a spatial dataset like a shapefile
 * to cadastre schema. Depending in the destination, the features are uploaded
 * as cadastre objects or spatial units.
 * 
* @author Elton Manoku
 */
public class SpatialBulkMoveBean extends AbstractBulkMoveBean {

    public static final String PROPERTY_SOURCE = "source";
    public static final String PROPERTY_DESTINATION = "destination";
    private SpatialSourceBean source = new SpatialSourceShapefileBean();
    private SpatialDestinationBean destination = new SpatialDestinationCadastreObjectBean();
    private TransactionCadastreChangeBean transactionCadastreChange = null;

    @NotNull(message = "Source must be present")
    public SpatialSourceBean getSource() {
        return source;
    }

    public void setSource(SpatialSourceBean source) {
        SpatialSourceBean old = this.source;
        this.source = source;
        propertySupport.firePropertyChange(PROPERTY_SOURCE, old, source);
    }

    @NotNull(message = "Destination must be present")
    public SpatialDestinationBean getDestination() {
        return destination;
    }

    public void setDestination(SpatialDestinationBean value) {
        SpatialDestinationBean old = this.destination;
        this.destination = value;
        propertySupport.firePropertyChange(PROPERTY_DESTINATION, old, value);
    }

    /**
     * Gets the list of beans that are derived from the source features.
     * 
     * @return 
     */
    public List<SpatialUnitTemporaryBean> getBeans() {
        return getDestination().getBeans(getSource());
    }

    public TransactionCadastreChangeBean getTransactionCadastreChange() {
        return transactionCadastreChange;
    }

    @Override
    public TransactionBulkOperationSpatial getTransaction() {
        return (TransactionBulkOperationSpatial) super.getTransaction();
    }

    @Override
    public void reset() {
        super.reset();
        transactionCadastreChange = null;
    }

    @Override
    public void sendToServer() {
        reset();
        setTransaction(new TransactionBulkOperationSpatial());
        if (getDestination().getClass().equals(SpatialDestinationCadastreObjectBean.class)) {
            getTransaction().setGenerateFirstPart(
                    ((SpatialDestinationCadastreObjectBean) getDestination()).isGenerateFirstPart());
        }
        getTransaction().setSpatialUnitTemporaryList(getBeans());
        getValidationResults().addAll(getTransaction().save());
        if (getDestination().getClass().equals(SpatialDestinationCadastreObjectBean.class)
                && hasValidationProblems()) {
            transactionCadastreChange =
                    PojoDataAccess.getInstance().getTransactionCadastreChangeById(
                    getTransaction().getId());
        }
    }
    
    /**
     * True if there are problems found with the bulk move process.
     * It checks if there is any unsuccessful validation rule found.
     * @return 
     */
    public boolean hasValidationProblems(){
        for(ValidationResultBean validationResult: getValidationResults()){
            if (!validationResult.isSuccessful()){
                return true;
            }
        }
        return false;
    }

    @Override
    public <T extends AbstractBindingBean> Set<ConstraintViolation<T>> validate(
            boolean showMessage, Class<?>... group) {
        Set<ConstraintViolation<T>> violations = super.validate(showMessage, group);
        if (getSource() != null) {
            violations.addAll((Collection) getSource().validate(showMessage, group));
        }
        if (getDestination() != null) {
            violations.addAll((Collection) getDestination().validate(showMessage, group));
        }
        return violations;
    }
}
