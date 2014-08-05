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
package org.sola.clients.beans.administrative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.clients.beans.AbstractBindingListBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.search.BaUnitSearchParamsTO;

/**
 * Holds list of {@link BaUnitSearchResultBean} objects.
 */
public class BaUnitSearchResultListBean extends AbstractBindingListBean {

    private class BaUnitSearchResultListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(BaUnitSearchResultBean.CHECKED_PROPERTY)) {
                propertySupport.firePropertyChange(BAUNIT_CHECKED_PROPERTY, false, true);
            }
        }
    }

    public static final String SELECTED_BAUNIT_SEARCH_RESULT_PROPERTY = "selectedBaUnitSearchResult";
    public static final String BAUNIT_CHECKED_PROPERTY = "baUnitChecked";
    private SolaObservableList<BaUnitSearchResultBean> baUnitSearchResults;
    private BaUnitSearchResultBean selectedBaUnitSearchResult;
    private BaUnitSearchResultListener listener = new BaUnitSearchResultListener();

    public BaUnitSearchResultListBean() {
        super();
        baUnitSearchResults = new SolaObservableList<BaUnitSearchResultBean>();
        // Listen for changes to the beans in the list
        baUnitSearchResults.addObservableListListener(new ObservableListListener() {
            @Override
            public void listElementsAdded(ObservableList list, int index, int length) {
                for (int i = index; i < length + index; i++) {
                    ((BaUnitSearchResultBean) list.get(i)).addPropertyChangeListener(listener);
                }
            }

            @Override
            public void listElementsRemoved(ObservableList list, int index, List oldElements) {
                for (Object app : oldElements) {
                    ((BaUnitSearchResultBean) app).removePropertyChangeListener(listener);
                }
            }

            @Override
            public void listElementReplaced(ObservableList list, int index, Object oldElement) {
                ((BaUnitSearchResultBean) oldElement).removePropertyChangeListener(listener);
            }

            @Override
            public void listElementPropertyChanged(ObservableList list, int index) {
            }
        });
    }

    public ObservableList<BaUnitSearchResultBean> getBaUnitSearchResults() {
        if (baUnitSearchResults == null) {
            baUnitSearchResults = new SolaObservableList<BaUnitSearchResultBean>();
        }
        return baUnitSearchResults;
    }

    public BaUnitSearchResultBean getSelectedBaUnitSearchResult() {
        return selectedBaUnitSearchResult;
    }

    public void setSelectedBaUnitSearchResult(BaUnitSearchResultBean selectedBaUnitSearchResult) {
        this.selectedBaUnitSearchResult = selectedBaUnitSearchResult;
        propertySupport.firePropertyChange(SELECTED_BAUNIT_SEARCH_RESULT_PROPERTY, null, this.selectedBaUnitSearchResult);
    }

    // METHODS
    /**
     * Searches BA units given parameters.
     */
    public void search(BaUnitSearchParamsBean params) {
        getBaUnitSearchResults().clear();
        BaUnitSearchParamsTO searchParams = TypeConverters.BeanToTrasferObject(params, BaUnitSearchParamsTO.class);
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getSearchService().searchBaUnit(searchParams),
                BaUnitSearchResultBean.class, (List) getBaUnitSearchResults());
    }

    /**
     * Loads the list of properties to action for the State Land Dashboard.
     */
    public void loadPropertiesToAction() {
        getBaUnitSearchResults().clear();
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getSearchService().getPropertiesToAction(),
                BaUnitSearchResultBean.class, (List) getBaUnitSearchResults());
    }

    /**
     * Returns true if there are checked properties on the list. Otherwise
     * false.
     */
    public boolean hasChecked() {
        for (BaUnitSearchResultBean prop : getBaUnitSearchResults()) {
            if (prop.isChecked()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns list of checked properties.
     *
     * @param includeSelected Indicates whether to include in the list selected
     * property if there no properties checked.
     */
    public List<BaUnitSearchResultBean> getChecked(boolean includeSelected) {
        List<BaUnitSearchResultBean> checkedProps = new ArrayList<BaUnitSearchResultBean>();
        for (BaUnitSearchResultBean prop : getBaUnitSearchResults()) {
            if (prop.isChecked()) {
                checkedProps.add(prop);
            }
        }
        if (includeSelected && checkedProps.size() < 1 && selectedBaUnitSearchResult != null) {
            checkedProps.add(selectedBaUnitSearchResult);
        }
        return checkedProps;
    }
}
