/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.sola.common.MappingManager;
import org.sola.clients.swing.gis.beans.TransactionCadastreChangeBean;
import org.sola.clients.swing.gis.beans.TransactionCadastreRedefinitionBean;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.CadastreClient;
import org.sola.services.boundary.wsclients.SearchClient;
import org.sola.services.boundary.wsclients.SpatialClient;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.services.boundary.wsclients.exception.WebServiceClientException;
import org.sola.webservices.spatial.ConfigMapLayerTO;
import org.sola.webservices.spatial.MapDefinitionTO;
import org.sola.webservices.spatial.QueryForNavigation;
import org.sola.webservices.search.QueryForSelect;
import org.sola.webservices.spatial.ResultForNavigationInfo;
import org.sola.webservices.search.ResultForSelectionInfo;
import org.sola.webservices.transferobjects.transaction.TransactionCadastreChangeTO;
import org.sola.webservices.transferobjects.transaction.TransactionCadastreRedefinitionTO;

/**
 *
 * This class is a singletone class. It handles the communication with the services for all needs
 * that gis component has for information from server.
 * It uses Sola infrastructure to achieve this.
 * 
 * @author Elton Manoku
 */
public class PojoDataAccess {

    private WSManager wsManager = null;
    private HashMap<String, ConfigMapLayerTO> mapLayerInfoList = null;
    private MapDefinitionTO mapDefinition = null;
    private static PojoDataAccess poJoDataAccess = null;

    /**
     * Constructor. Use the singletone method instead of this constructor.
     */
    public PojoDataAccess() {
        this.wsManager = WSManager.getInstance();
    }

    /**
     * It gets the singletone instance of itself. Use this instead of using the constructor to 
     * get a reference to the object.
     */
    public static PojoDataAccess getInstance(){
        if (poJoDataAccess==null){
            poJoDataAccess = new PojoDataAccess();
        }
        return poJoDataAccess;
    }
    
    /**
     * Gets the map definition from the server. This object is used to define startup 
     * parameters for the map control
     * 
     * @return 
     */
    public MapDefinitionTO getMapDefinition() {
        if (this.mapDefinition == null) {
            this.mapDefinition = getSpatialService().getMapDefinition();
        }
        return this.mapDefinition;
    }

    /**
     * Gets the list of layer definitions from server
     */
    public Map<String, ConfigMapLayerTO> getMapLayerInfoList() {
        if (this.mapLayerInfoList == null) {
            List<ConfigMapLayerTO> configMapLayerList = new ArrayList<ConfigMapLayerTO>();
            configMapLayerList = this.getMapDefinition().getLayers();
            this.mapLayerInfoList = new HashMap<String, ConfigMapLayerTO>();
            for (ConfigMapLayerTO configMapLayer : configMapLayerList) {
                this.mapLayerInfoList.put(configMapLayer.getId(), configMapLayer);
            }
        }
        return this.mapLayerInfoList;
    }

    /**
     * Gets the list of features and other relevant information for the given extent
     * @param name Name of layer
     * @param west West coordinate
     * @param south South coordinate
     * @param east East coordinate
     * @param north North coordinate
     * @param srid Srid of the map control
     * @param pixelTolerance the pixel tolerance. It is not used at the moment
     * @return 
     */
    public ResultForNavigationInfo GetQueryData(String name,
            double west, double south, double east, double north, int srid,
            double pixelTolerance) {
        ConfigMapLayerTO configMapLayer = this.getMapLayerInfoList().get(name);
        QueryForNavigation spatialQueryInfo = new QueryForNavigation();
        spatialQueryInfo.setQueryName(configMapLayer.getPojoQueryName());
        spatialQueryInfo.setWest(west);
        spatialQueryInfo.setSouth(south);
        spatialQueryInfo.setEast(east);
        spatialQueryInfo.setNorth(north);
        spatialQueryInfo.setSrid(srid);
        spatialQueryInfo.setPixelResolution(pixelTolerance);
            return getSpatialService().getSpatialForNavigation(spatialQueryInfo);
    }

    /**
     * Gets a list of selected set of features for each query defined in the parameter.
     * @param queries List of queries used for selecting features
     * @return 
     */
    public List<ResultForSelectionInfo> Select(List<QueryForSelect> queries) {
        try {
            return getSearchService().select(queries);
        } catch (WebServiceClientException ex) {
            LogUtility.log(ex.getMessageCode(), ex);
            MessageUtility.displayMessage(ex.getMessageCode(),
                    ex.getErrorNumber(), ex.getMessageParameters());
        }
        return null;
    }
    
    /**
     * Gets a reference to the Sola Web service manager. This is used to finally 
     * communicate with the web services
     * @return 
     */
    public WSManager getWSManager(){
        return wsManager;
    }
    
    /**
     * Gets a reference to the cadastre web service
     * @return 
     */
    public CadastreClient getCadastreService(){
        return getInstance().getWSManager().getCadastreService();
    }

    /**
     * Gets a reference to the search web service
     * @return 
     */
    public SearchClient getSearchService(){
        return getInstance().getWSManager().getSearchService();
    }

    /**
     * Gets a reference to the spatial web service
     * @return 
     */
    public SpatialClient getSpatialService(){
        return getInstance().getWSManager().getSpatialService();
    }

    /**
     * Gets a cadastre change transaction
     * @param serviceId The service id which initializes the transaction
     * @return 
     */
    public TransactionCadastreChangeBean getTransactionCadastreChange(String serviceId){
        TransactionCadastreChangeTO objTO = 
                getInstance().getCadastreService().getTransactionCadastreChange(serviceId);
        TransactionCadastreChangeBean transactionBean = new TransactionCadastreChangeBean();
        if (objTO == null){
            transactionBean.setFromServiceId(serviceId);
        }else{
            MappingManager.getMapper().map(objTO, transactionBean);            
        }
        return transactionBean;
    }

    /**
     * Gets a cadastre redefinition transaction
     * @param serviceId The service id which initializes the transaction
     * @return 
     */
    public TransactionCadastreRedefinitionBean getTransactionCadastreRedefinition(
            String serviceId){
        TransactionCadastreRedefinitionTO objTO = 
                getInstance().getCadastreService().getTransactionCadastreRedefinition(serviceId);
        TransactionCadastreRedefinitionBean transactionBean = 
                new TransactionCadastreRedefinitionBean();
        if (objTO == null){
            transactionBean.setFromServiceId(serviceId);
        }else{
            MappingManager.getMapper().map(objTO, transactionBean);            
        }
        return transactionBean;
    }
}
