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

import java.util.List;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.search.PowerOfAttorneySearchParamsTO;
import org.sola.webservices.transferobjects.search.PowerOfAttorneySearchResultTO;

/**
 * Contains methods to search Power of attorney and and holds list of 
 * {@link PowerOfAttorneySearchResultBean}s 
 */
public class PowerOfAttorneySearchResultListBean extends AbstractBindingBean {
    public static final String SELECTED_POWER_OF_ATTORNEY_PROPERTY = "selectedPowerOfAttorney";
    private SolaObservableList<PowerOfAttorneySearchResultBean> powerOfAttorneySearchResultsList;
    private PowerOfAttorneySearchResultBean selectedPowerOfAttorney;
    
    public PowerOfAttorneySearchResultListBean(){
        super();
        powerOfAttorneySearchResultsList = new SolaObservableList<PowerOfAttorneySearchResultBean>();
    }

    public SolaObservableList<PowerOfAttorneySearchResultBean> getPowerOfAttorneySearchResultsList() {
        return powerOfAttorneySearchResultsList;
    }

    public PowerOfAttorneySearchResultBean getSelectedPowerOfAttorney() {
        return selectedPowerOfAttorney;
    }

    public void setSelectedPowerOfAttorney(PowerOfAttorneySearchResultBean selectedPowerOfAttorney) {
        PowerOfAttorneySearchResultBean oldValue = this.selectedPowerOfAttorney;
        this.selectedPowerOfAttorney = selectedPowerOfAttorney;
        propertySupport.firePropertyChange(SELECTED_POWER_OF_ATTORNEY_PROPERTY, oldValue, this.selectedPowerOfAttorney);
    }
    
    
    /** Runs Power of attorney search with a given search criteria. */
    public void search(PowerOfAttorneySearchParamsBean params) {
        powerOfAttorneySearchResultsList.clear();
        PowerOfAttorneySearchParamsTO paramsTO = TypeConverters.BeanToTrasferObject(params,
                PowerOfAttorneySearchParamsTO.class);

        List<PowerOfAttorneySearchResultTO> searchPowerOfAttorneyTO =
                WSManager.getInstance().getSearchService().searchPowerOfAttorney(paramsTO);
        TypeConverters.TransferObjectListToBeanList(searchPowerOfAttorneyTO,
                PowerOfAttorneySearchResultBean.class, (List) powerOfAttorneySearchResultsList);
    }

    /** Opens attachment of selected document. */
    public void openAttachment() {
        if (selectedPowerOfAttorney != null && selectedPowerOfAttorney.getArchiveDocumentId() != null) {
            DocumentBean.openDocument(selectedPowerOfAttorney.getArchiveDocumentId());
        }
    }
}
