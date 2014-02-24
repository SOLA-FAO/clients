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
package org.sola.clients.beans.systematicregistration;

import java.util.List;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.SysRegPubDisParcelNameTO;

/**
 *
 * @author RizzoM
 */
public class ParcelNumberListingListBean extends AbstractBindingBean {

    public static final String SELECTED_PARCEL_PROPERTY = "selectedParcels";
    private SolaObservableList<ParcelNumberListingBean> parcels;
    private ParcelNumberListingBean selectedParcels;

    public ParcelNumberListingListBean() {
        super();
        parcels = new SolaObservableList<ParcelNumberListingBean>();
    }

    public SolaObservableList<ParcelNumberListingBean> getParcelNumberListing() {
        return parcels;
    }

    public ParcelNumberListingBean getSelectedParcels() {
        return selectedParcels;
    }

    public void setSelectedParcels(ParcelNumberListingBean selectedParcels) {
        ParcelNumberListingBean oldValue = this.selectedParcels;
        this.selectedParcels = selectedParcels;
        propertySupport.firePropertyChange(SELECTED_PARCEL_PROPERTY, oldValue, this.selectedParcels);
    }

    /**
     * Returns collection of {@link ParcelNumberListingListBean} objects. This
     * method is used by Jasper report designer to extract properties of
     * application bean to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        ParcelNumberListingListBean bean = new ParcelNumberListingListBean();
        collection.add(bean);
        return collection;
    }

    //      /** Passes from date and to date search criteria. */
    public void passParameter(String params) {

        List<SysRegPubDisParcelNameTO> sysRegListingTO =
                WSManager.getInstance().getAdministrative().getSysRegPubDisParcelNameByLocation(params);

        TypeConverters.TransferObjectListToBeanList(sysRegListingTO,
                ParcelNumberListingBean.class, (List) parcels);
    }
    
     /**
     * Withdraws application
     */
    public List<ValidationResultBean> publicDisplay(String params) {
        List<ValidationResultBean> result = TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getAdministrative().publicDisplay(params),
                ValidationResultBean.class, null);
       
        return result;
    }

}
