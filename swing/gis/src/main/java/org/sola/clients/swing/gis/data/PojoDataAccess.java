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
package org.sola.clients.swing.gis.data;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.extended.util.CRSUtility;
import org.geotools.swing.extended.util.GeometryUtility;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.gis.beans.TransactionCadastreChangeBean;
import org.sola.clients.swing.gis.beans.TransactionCadastreRedefinitionBean;
import org.sola.clients.swing.gis.beans.TransactionStateLandBean;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.*;
import org.sola.services.boundary.wsclients.exception.WebServiceClientException;
import org.sola.webservices.search.ConfigMapLayerTO;
import org.sola.webservices.search.CrsTO;
import org.sola.webservices.search.MapDefinitionTO;
import org.sola.webservices.search.QueryForSelect;
import org.sola.webservices.search.ResultForSelectionInfo;
import org.sola.webservices.spatial.QueryForNavigation;
import org.sola.webservices.spatial.QueryForPublicDisplayMap;
import org.sola.webservices.spatial.ResultForNavigationInfo;
import org.sola.webservices.transferobjects.transaction.TransactionCadastreChangeTO;
import org.sola.webservices.transferobjects.transaction.TransactionCadastreRedefinitionTO;
import org.sola.webservices.transferobjects.transaction.TransactionStateLandTO;

/**
 *
 * This class is a singletone class. It handles the communication with the services for all needs
 * that gis component has for information from server. It uses Sola infrastructure to achieve this.
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
     * It gets the singletone instance of itself. Use this instead of using the constructor to get a
     * reference to the object.
     */
    public static PojoDataAccess getInstance() {
        if (poJoDataAccess == null) {
            poJoDataAccess = new PojoDataAccess();
        }
        return poJoDataAccess;
    }

    /**
     * Gets the map definition from the server. This object is used to define startup parameters for
     * the map control
     *
     * @return
     */
    public MapDefinitionTO getMapDefinition() {
        if (this.mapDefinition == null) {
            this.resetMapDefinition();
        }
        return this.mapDefinition;
    }

    /**
     * It resets the map definition.
     */
    public void resetMapDefinition(){
        this.mapDefinition = getSearchService().getMapDefinition();
        this.mapLayerInfoList = null;
        CRSUtility.getInstance().clearCRSList();
        for (CrsTO crsTO:this.mapDefinition.getCrsList()){
            CRSUtility.getInstance().setCRS(crsTO.getSrid(), crsTO.getWkt());
        }
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
     *
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
     * Gets the list of features and other relevant information for the given extent
     * for a layer of type pojo_public_display
     *
     * @param name Name of layer
     * @param west West coordinate
     * @param south South coordinate
     * @param east East coordinate
     * @param north North coordinate
     * @param srid Srid of the map control
     * @param pixelTolerance the pixel tolerance. It is not used at the moment
     * @param nameLastPart The name last part of a cadastre object that is used
     * in filtering the features.
     * @return
     */
    public ResultForNavigationInfo GetQueryDataForPublicDisplay(String name,
            double west, double south, double east, double north, int srid,
            double pixelTolerance, String nameLastPart) {
        ConfigMapLayerTO configMapLayer = this.getMapLayerInfoList().get(name);
        QueryForPublicDisplayMap spatialQueryInfo = new QueryForPublicDisplayMap();
        spatialQueryInfo.setQueryName(configMapLayer.getPojoQueryName());
        spatialQueryInfo.setWest(west);
        spatialQueryInfo.setSouth(south);
        spatialQueryInfo.setEast(east);
        spatialQueryInfo.setNorth(north);
        spatialQueryInfo.setSrid(srid);
        spatialQueryInfo.setPixelResolution(pixelTolerance);        
        spatialQueryInfo.setNameLastPart(nameLastPart);
        return getSpatialService().getSpatialForPublicDisplay(spatialQueryInfo);
    }

    /**
     * Gets a list of selected set of features for each query defined in the parameter.
     *
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
     * Gets a reference to the Sola Web service manager. This is used to finally communicate with
     * the web services
     *
     * @return
     */
    public WSManager getWSManager() {
        return wsManager;
    }

    /**
     * Gets a reference to the cadastre web service
     *
     * @return
     */
    public CadastreClient getCadastreService() {
        return getInstance().getWSManager().getCadastreService();
    }

    /**
     * Gets a reference to the search web service
     *
     * @return
     */
    public SearchClient getSearchService() {
        return getInstance().getWSManager().getSearchService();
    }

    /**
     * Gets a reference to the spatial web service
     *
     * @return
     */
    public SpatialClient getSpatialService() {
        return getInstance().getWSManager().getSpatialService();
    }

    /**
     * Gets a cadastre change transaction
     *
     * @param serviceId The service id which initializes the transaction
     * @return
     */
    public TransactionCadastreChangeBean getTransactionCadastreChange(String serviceId) {
        TransactionCadastreChangeTO objTO =
                getInstance().getCadastreService().getTransactionCadastreChange(serviceId);
        TransactionCadastreChangeBean transactionBean = new TransactionCadastreChangeBean();
        if (objTO == null) {
            transactionBean.setFromServiceId(serviceId);
        } else {
            transactionBean = TypeConverters.TransferObjectToBean(objTO, 
                    TransactionCadastreChangeBean.class, null);
        }
        return transactionBean;
    }

    /**
     * Gets a cadastre change transaction
     *
     * @param serviceId The service id which initializes the transaction
     * @return
     */
    public TransactionCadastreChangeBean getTransactionCadastreChangeById(String id) {
        TransactionCadastreChangeTO objTO =
                getInstance().getCadastreService().getTransactionCadastreChangeById(id);
        TransactionCadastreChangeBean transactionBean = new TransactionCadastreChangeBean();
        if (objTO == null) {
            transactionBean.setId(id);
        } else {
            transactionBean = TypeConverters.TransferObjectToBean(objTO, 
                    TransactionCadastreChangeBean.class, null);
        }
        return transactionBean;
    }

    /**
     * Gets a cadastre redefinition transaction
     *
     * @param serviceId The service id which initializes the transaction
     * @return
     */
    public TransactionCadastreRedefinitionBean getTransactionCadastreRedefinition(
            String serviceId) {
        TransactionCadastreRedefinitionTO objTO =
                getInstance().getCadastreService().getTransactionCadastreRedefinition(serviceId);
        TransactionCadastreRedefinitionBean transactionBean =
                new TransactionCadastreRedefinitionBean();
        if (objTO == null) {
            transactionBean.setFromServiceId(serviceId);
        } else {
             transactionBean = TypeConverters.TransferObjectToBean(objTO, 
                     TransactionCadastreRedefinitionBean.class, null);
        }
        return transactionBean;
    }
    
    /**
     * Gets the extent of the public display map
     * 
     * @param nameLastPart
     * @return 
     */
    public ReferencedEnvelope getExtentOfPublicDisplay(String nameLastPart){
        byte[] e = getSearchService().getExtentOfPublicDisplayMap(nameLastPart);
        if (e == null){
            return null;
        }
        Geometry extent = GeometryUtility.getGeometryFromWkb(e);
        return JTS.toEnvelope(extent);
    }
    
        /**
     * Gets a state land transaction
     *
     * @param serviceId The service id which initializes the transaction
     * @return
     */
    public TransactionStateLandBean getStateLandChange(
            String serviceId) {
        TransactionStateLandTO objTO =
                getInstance().getCadastreService().getStateLandChange(serviceId);
        TransactionStateLandBean transactionBean =
                new TransactionStateLandBean();
        if (objTO == null) {
            transactionBean.setFromServiceId(serviceId);
        } else {
             transactionBean = TypeConverters.TransferObjectToBean(objTO, 
                     TransactionStateLandBean.class, null);
        }
        return transactionBean;
    }
}
