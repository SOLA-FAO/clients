/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.beans.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.search.ApplicationSearchParamsTO;
import org.sola.webservices.transferobjects.search.ApplicationSearchResultTO;

/**
 * Contains methods to search applications and get the list of assigned and
 * unassigned applications.
 */
public class ApplicationSearchResultsListBean extends AbstractBindingBean {
    
private class AppSearchResultListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ApplicationSearchResultBean.CHECKED_PROPERTY)) {
                propertySupport.firePropertyChange(APPLICATION_CHECKED_PROPERTY, false, true);
            }
        }
    }

    public static final String SELECTED_APPLICATION_PROPERTY = "selectedApplication";
    public static final String APPLICATION_CHECKED_PROPERTY = "applicationChecked";
    private SolaObservableList<ApplicationSearchResultBean> applicationSearchResultsList;
    private ApplicationSearchResultBean selectedApplication;
    
    private AppSearchResultListener appSearchResultListener = new AppSearchResultListener();
    
    /**
     * Creates object's instance and initializes collection of {@link ApplicationSearchResultBean}.
     */
    public ApplicationSearchResultsListBean() {
        applicationSearchResultsList = new SolaObservableList<ApplicationSearchResultBean>();
        applicationSearchResultsList.addObservableListListener(new ObservableListListener() {

            @Override
            public void listElementsAdded(ObservableList list, int index, int length) {
                for (int i = index; i < length + index; i++) {
                    ((ApplicationSearchResultBean) list.get(i)).addPropertyChangeListener(appSearchResultListener);
                }
            }

            @Override
            public void listElementsRemoved(ObservableList list, int index, List oldElements) {
                for (Object app : oldElements) {
                    ((ApplicationSearchResultBean) app).removePropertyChangeListener(appSearchResultListener);
                }
            }

            @Override
            public void listElementReplaced(ObservableList list, int index, Object oldElement) {
                ((ApplicationSearchResultBean) oldElement).removePropertyChangeListener(appSearchResultListener);
            }

            @Override
            public void listElementPropertyChanged(ObservableList list, int index) {
            }
        });
    }

    /**
     * Fills application search result list with unassigned applications.
     */
    public void FillUnassigned() {
        applicationSearchResultsList.clear();
        List<ApplicationSearchResultTO> unassignedApplicationsTO =
                WSManager.getInstance().getSearchService().getUnassignedApplications();

        TypeConverters.TransferObjectListToBeanList(unassignedApplicationsTO,
                ApplicationSearchResultBean.class, (List) getApplicationSearchResultsList());
    }

    /**
     * Fills application search result list with assigned applications.
     */
    public void FillAssigned() {
        applicationSearchResultsList.clear();
        List<ApplicationSearchResultTO> assignedApplicationsTO =
                WSManager.getInstance().getSearchService().getAssignedApplications();

        TypeConverters.TransferObjectListToBeanList(assignedApplicationsTO,
                ApplicationSearchResultBean.class, (List) getApplicationSearchResultsList());
    }

    /**
     * Runs application search with a given search criteria.
     */
    public void searchApplications(ApplicationSearchParamsBean params) {
        applicationSearchResultsList.clear();
        ApplicationSearchParamsTO paramsTO = TypeConverters.BeanToTrasferObject(params,
                ApplicationSearchParamsTO.class);

        List<ApplicationSearchResultTO> searchApplicationsTO =
                WSManager.getInstance().getSearchService().searchApplications(paramsTO);
        TypeConverters.TransferObjectListToBeanList(searchApplicationsTO,
                ApplicationSearchResultBean.class, (List) getApplicationSearchResultsList());
    }

    public ObservableList<ApplicationSearchResultBean> getApplicationSearchResultsList() {
        return applicationSearchResultsList;
    }

    public ApplicationSearchResultBean getSelectedApplication() {
        return selectedApplication;
    }

    public void setSelectedApplication(ApplicationSearchResultBean applicationSearchResultBean) {
        selectedApplication = applicationSearchResultBean;
        propertySupport.firePropertyChange(SELECTED_APPLICATION_PROPERTY, null, selectedApplication);
    }

    /**
     * Returns true if there are checked applications on the list. Otherwise
     * false.
     */
    public boolean hasChecked() {
        for (ApplicationSearchResultBean app : getApplicationSearchResultsList()) {
            if (app.isChecked()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns list of checked applications.
     *
     * @param includeSelected Indicates whether to include in the list selected
     * application if there no checked.
     */
    public List<ApplicationSearchResultBean> getChecked(boolean includeSelected) {
        ArrayList<ApplicationSearchResultBean> checkedApps = new ArrayList<ApplicationSearchResultBean>();
        for (ApplicationSearchResultBean app : getApplicationSearchResultsList()) {
            if (app.isChecked()) {
                checkedApps.add(app);
            }
        }
        if (includeSelected && checkedApps.size() < 1 && selectedApplication != null) {
            checkedApps.add(selectedApplication);
        }
        return checkedApps;
    }
}
