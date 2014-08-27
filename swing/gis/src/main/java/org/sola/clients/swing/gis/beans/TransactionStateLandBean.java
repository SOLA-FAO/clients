/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.gis.beans;

import java.util.List;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.webservices.transferobjects.transaction.TransactionStateLandTO;

/**
 * Data bean representing a transaction for State Land
 *
 * @author soladev
 */
public class TransactionStateLandBean extends TransactionBean {

    public static final String SELECTED_STATE_LAND_PARCEL_PROPERTY = "selectedStateLandParcelBean";
    private SolaObservableList<StateLandParcelBean> stateLandParcels;
    private StateLandParcelBean selectedStateLandParcelBean;

    public TransactionStateLandBean() {
        super();
        stateLandParcels = new SolaObservableList<StateLandParcelBean>();
    }

    public SolaObservableList<StateLandParcelBean> getStateLandParcels() {
        return stateLandParcels;
    }

    public void setStateLandParcels(SolaObservableList<StateLandParcelBean> stateLandParcels) {
        this.stateLandParcels = stateLandParcels;
    }

    public StateLandParcelBean getSelectedStateLandParcelBean() {
        return selectedStateLandParcelBean;
    }

    public void setSelectedStateLandParcelBean(StateLandParcelBean selectedStateLandParcelBean) {
        this.selectedStateLandParcelBean = selectedStateLandParcelBean;
        propertySupport.firePropertyChange(SELECTED_STATE_LAND_PARCEL_PROPERTY,
                null, this.selectedStateLandParcelBean);
    }

    /**
     * Returns the Transfer Object for this Cadastre Change bean.
     *
     * @return
     */
    @Override
    public TransactionStateLandTO getTO() {
        return TypeConverters.BeanToTrasferObject(this, TransactionStateLandTO.class);
    }

    /**
     * Saves the details of the Cadastre Change to the SOLA database
     *
     * @return
     */
    @Override
    public List<ValidationResultBean> save() {
        return TypeConverters.TransferObjectListToBeanList(
                PojoDataAccess.getInstance().getCadastreService().saveStateLandChange(
                        this.getTO()), ValidationResultBean.class, null);
    }
}
