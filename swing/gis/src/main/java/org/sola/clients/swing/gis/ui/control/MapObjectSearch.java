/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.control;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.util.Messaging;
import org.sola.clients.swing.common.controls.FreeTextSearch;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.gis.beans.SpatialSearchOptionBean;
import org.sola.clients.swing.gis.beans.SpatialSearchResultBean;
import org.sola.clients.swing.gis.data.PojoFeatureSource;
import org.sola.common.MappingManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.SearchClient;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.search.SpatialSearchResultTO;

/**
 * This control extends the FreeTextSearch functionality by searching in the map control. It is used
 * in the Find tab in the map.
 *
 * @author Elton Manoku
 */
public class MapObjectSearch extends FreeTextSearch {

    private Map map;
    private SpatialSearchOptionBean searchByObject;
    private SolaTask searchTask = null;

    public MapObjectSearch() {
        super();
        this.setHideListIfNotNeeded(false);
        this.setMinimalSearchStringLength(3);
        this.setRefreshTextInSelection(false);
    }

    /**
     * Sets the map control
     *
     * @param map
     */
    public void setMap(Map map) {
        this.map = map;
    }

    /**
     * Executes the spatial search using the search string provided.
     *
     * @param searchString
     * @param listModel
     */
    @Override
    public void onNewSearchString(final String searchString, final DefaultListModel listModel) {

        // No serach is selected
        if (this.searchByObject == null) {
            return;
        }

        // Check if a map search is currently running and if so, cancel it
        if (searchTask != null && TaskManager.getInstance().isTaskRunning(searchTask.getId())) {
            TaskManager.getInstance().removeTask(searchTask);
        }

        final List<SpatialSearchResultTO> searchResults = new ArrayList<SpatialSearchResultTO>();
        final String queryName = this.searchByObject.getQueryName();
        final String searchTitle = this.searchByObject.getTitle();
        listModel.clear();

        searchTask = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                // Perform the search on a background thread
                setMessage(MessageUtility.getLocalizedMessage(ClientMessage.PROGRESS_MSG_MAP_SEARCHING,
                        new String[]{searchTitle}).getMessage());
                try {
                    // Allow a small delay on the background thread so that the tread can be cancelled
                    // before executing the search if the user is still typing. 
                    Thread.sleep(500);
                    searchResults.addAll(WSManager.getInstance().getSearchService().searchSpatialObjects(queryName, searchString));
                } catch (InterruptedException ex) {
                }
                return null;
            }

            @Override
            public void taskDone() {
                // Update the GUI using the primary EDT thread
                if (searchResults.size() > 0) {
                    // Convert the TOs to Beans
                    List<SpatialSearchResultBean> beanList = new ArrayList<SpatialSearchResultBean>();
                    for (SpatialSearchResultTO searchResult : searchResults) {
                        beanList.add(MappingManager.getMapper().map(searchResult,
                                SpatialSearchResultBean.class));

                    }
                    // Sort the beans and then display them in the list. 
                    Collections.sort(beanList);
                    for (SpatialSearchResultBean bean : beanList) {
                        listModel.addElement(bean);
                    }
                }
            }
        };
        TaskManager.getInstance().runTask(searchTask);

    }

    /**
     * Zooms the map to the selected object and highlight it in the map.
     */
    @Override
    public void onSelectionConfirmed() {
        if (this.getSelectedElement() == null) {
            return;
        }
        SpatialSearchResultBean selectedObj =
                (SpatialSearchResultBean) this.getSelectedElement();
        try {
            Geometry geom =
                    PojoFeatureSource.getWkbReader().read(selectedObj.getTheGeom());

            // Select the object on the map
            this.map.clearSelectedFeatures();
            this.map.selectFeature(selectedObj.getId(), geom);

            // Zoom to map to the object
            ReferencedEnvelope boundsToZoom = JTS.toEnvelope(geom);
            boundsToZoom.expandBy(this.searchByObject.getZoomInBuffer().doubleValue());
            this.map.setDisplayArea(boundsToZoom);
        } catch (ParseException ex) {
            Messaging.getInstance().show(GisMessage.LEFT_PANEL_FIND_ERROR);
            org.sola.common.logging.LogUtility.log(GisMessage.LEFT_PANEL_FIND_ERROR, ex);
        }
    }

    /**
     * Sets the search option. The search option is sent to the server as well with the filter, to
     * define what to search.
     *
     * @param searchByObject
     */
    public void setSearchByObject(SpatialSearchOptionBean searchByObject) {
        this.searchByObject = searchByObject;
        if (searchByObject != null) {
            this.setMinimalSearchStringLength(searchByObject.getMinSearchStrLen());
        }
    }

    /**
     * Clear the selected features from the map.
     */
    public void clearSelection() {
        this.map.clearSelectedFeatures();
        this.map.refresh();
    }
}
