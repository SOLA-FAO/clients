/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sola.clients.beans.systematicregistration;

import java.util.List;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.SysRegPubDisOwnerNameTO;

/**
 *
 * @author RizzoM
 */
public class OwnerNameListingListBean extends AbstractBindingBean {

    public static final String SELECTED_PARCEL_PROPERTY = "selectedParcels";
    private SolaObservableList<OwnerNameListingBean> parcels;
    private OwnerNameListingBean selectedParcels;

    public OwnerNameListingListBean() {
        super();
        parcels = new SolaObservableList<OwnerNameListingBean>();
    }

    public SolaObservableList<OwnerNameListingBean> getOwnerNameListing() {
        return parcels;
    }

    public OwnerNameListingBean getSelectedParcels() {
        return selectedParcels;
    }

    public void setSelectedParcels(OwnerNameListingBean selectedParcels) {
        OwnerNameListingBean oldValue = this.selectedParcels;
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
        OwnerNameListingListBean bean = new OwnerNameListingListBean();
        collection.add(bean);
        return collection;
    }

    //      /** Passes from date and to date search criteria. */
    public void passParameter(String params) {

        List<SysRegPubDisOwnerNameTO> sysRegListingTO =
                WSManager.getInstance().getAdministrative().getSysRegPubDisOwnerNameByLocation(params);

        TypeConverters.TransferObjectListToBeanList(sysRegListingTO,
                OwnerNameListingBean.class, (List) parcels);
    }
}