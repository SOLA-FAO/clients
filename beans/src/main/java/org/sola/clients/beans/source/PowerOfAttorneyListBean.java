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
