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
package org.sola.clients.beans.source;

import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * Holds the list of {@link PowerOfAttorneyBean} objects.
 */
public class PowerOfAttorneyListBean extends AbstractBindingBean {
    
    public static final String SELECTED_POWER_OF_ATTORNEY_PROPERTY = "selectedPowerOfAttorney";
    private SolaList<PowerOfAttorneyBean> powerOfAttorneyList;
    private PowerOfAttorneyBean selectedPowerOfAttorney;
    
    public PowerOfAttorneyListBean(){
        super();
        powerOfAttorneyList = new SolaList();
    }

    public SolaList<PowerOfAttorneyBean> getPowerOfAttorneyList() {
        return powerOfAttorneyList;
    }
    
    public ObservableList<PowerOfAttorneyBean> getFilteredPowerOfAttorneyList() {
        return powerOfAttorneyList.getFilteredList();
    }

    public PowerOfAttorneyBean getSelectedPowerOfAttorney() {
        return selectedPowerOfAttorney;
    }

    public void setSelectedPowerOfAttorney(PowerOfAttorneyBean selectedPowerOfAttorney) {
        PowerOfAttorneyBean oldValue = this.selectedPowerOfAttorney;
        this.selectedPowerOfAttorney = selectedPowerOfAttorney;
        propertySupport.firePropertyChange(SELECTED_POWER_OF_ATTORNEY_PROPERTY, oldValue, this.selectedPowerOfAttorney);
    }
    
    /** 
     * Loads power of attorney, attached to the transaction. 
     * @param serviceId Application service id, bound to transaction.
     */
    public void loadPowerOfAttorneyByService(String serviceId) {
        ArrayList<PowerOfAttorneyBean> tmpPowerOfAttorneyList = new ArrayList<PowerOfAttorneyBean>();
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCaseManagementService().getPowerOfAttorneyByServiceId(serviceId),
                PowerOfAttorneyBean.class, (List) tmpPowerOfAttorneyList);
        powerOfAttorneyList.clear();
        if (tmpPowerOfAttorneyList != null) {
            for (PowerOfAttorneyBean powerOfAttorneyBean : tmpPowerOfAttorneyList) {
                powerOfAttorneyList.addAsNew(powerOfAttorneyBean);
            }
        }
    }
    
    public void removeSelectedPowerOfAttorney(){
        if(selectedPowerOfAttorney!=null){
            powerOfAttorneyList.safeRemove(selectedPowerOfAttorney, EntityAction.DELETE);
        }
    }
}
