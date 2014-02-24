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

import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.search.SourceSearchParamsTO;
import org.sola.webservices.transferobjects.search.SourceSearchResultTO;

/**
 * Contains methods to search sources and and holds list of 
 * {@link SourceSearchResultBean}s 
 */
public class SourceSearchResultsListBean extends AbstractBindingBean {
    public static final String SELECTED_SOURCE_PROPERTY = "selectedSource";
    private SolaObservableList<SourceSearchResultBean> sourceSearchResultsList;
    private SourceSearchResultBean selectedSource;

    /** Creates object's instance and initializes collection of {@link SourceSearchResultBean}.*/
    public SourceSearchResultsListBean() {
        sourceSearchResultsList = new SolaObservableList<SourceSearchResultBean>();
    }
    
    /** Runs source search with a given search criteria. */
    public void searchSources(SourceSearchParamsBean params) {
        sourceSearchResultsList.clear();
        SourceSearchParamsTO paramsTO = TypeConverters.BeanToTrasferObject(params,
                SourceSearchParamsTO.class);

        List<SourceSearchResultTO> searchSourcesTO =
                WSManager.getInstance().getSearchService().searchSources(paramsTO);
        TypeConverters.TransferObjectListToBeanList(searchSourcesTO,
                SourceSearchResultBean.class, (List) sourceSearchResultsList);
    }

    /** Opens attachment of selected document. */
    public void openAttachment() {
        if (selectedSource != null && selectedSource.getArchiveDocumentId() != null) {
            DocumentBean.openDocument(selectedSource.getArchiveDocumentId());
        }
    }
    
    public SourceSearchResultBean getSelectedSource() {
        return selectedSource;
    }

    public void setSelectedSource(SourceSearchResultBean selectedSource) {
        this.selectedSource = selectedSource;
        propertySupport.firePropertyChange(SELECTED_SOURCE_PROPERTY, null, selectedSource);
    }

    public ObservableList<SourceSearchResultBean> getSourceSearchResultsList() {
        return sourceSearchResultsList;
    }

    public void setSourceSearchResultsList(SolaObservableList<SourceSearchResultBean> sourceSearchResultsList) {
        this.sourceSearchResultsList = sourceSearchResultsList;
    }
}
