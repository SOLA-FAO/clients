/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
import java.util.logging.Level;
import org.sola.common.MappingManager;
import org.sola.clients.swing.gis.beans.CadastreChangeBean;
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
import org.sola.webservices.transferobjects.cadastre.CadastreChangeTO;

/**
 *
 * @author manoku
 */
public class PojoDataAccess {

    private WSManager wsManager = null;
    private HashMap<String, ConfigMapLayerTO> mapLayerInfoList = null;
    private MapDefinitionTO mapDefinition = null;
    private static PojoDataAccess poJoDataAccess = null;

    public PojoDataAccess() {
        this.wsManager = WSManager.getInstance();
    }

    public static PojoDataAccess getInstance(){
        if (poJoDataAccess==null){
            poJoDataAccess = new PojoDataAccess();
        }
        return poJoDataAccess;
    }
    
    public MapDefinitionTO getMapDefinition() {
        if (this.mapDefinition == null) {
            this.mapDefinition = getSpatialService().getMapDefinition();
        }
        return this.mapDefinition;
    }

    public HashMap<String, ConfigMapLayerTO> getMapLayerInfoList() {
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
        try {
            return getSpatialService().getSpatialForNavigation(spatialQueryInfo);
        } catch (WebServiceClientException ex) {
            LogUtility.log(ex.getMessageCode(), Level.SEVERE);
            MessageUtility.displayMessage(ex.getMessageCode(),
                    ex.getErrorNumber(), ex.getMessageParameters());
        }
        return null;
    }

    public List<ResultForSelectionInfo> Select(List<QueryForSelect> queries) {
        try {
            return getSearchService().select(queries);
        } catch (WebServiceClientException ex) {
            LogUtility.log("Error trying to select from gis component.", ex);
            MessageUtility.displayMessage(ex.getMessageCode(),
                    ex.getErrorNumber(), ex.getMessageParameters());
        }
        return null;
    }
    
    public WSManager getWSManager(){
        return wsManager;
    }
    
    public CadastreClient getCadastreService(){
        return getInstance().getWSManager().getCadastreService();
    }

    public SearchClient getSearchService(){
        return getInstance().getWSManager().getSearchService();
    }

    public SpatialClient getSpatialService(){
        return getInstance().getWSManager().getSpatialService();
    }

    //    public List<CadastreObjectTO> getCadastreObjectsByBaUnit(String baUnitId){
//        return getCadastreService().getCadastreObjectsByBaUnit(baUnitId);
//    }
//    
//    public CadastreObjectTO getCadastreObjectByPoint(double x, double y, int srid){
//        return getCadastreService().getCadastreObjectByPoint(x, y, srid);
//    }
//    
//    public List<CadastreObjectTO> getCadastreObjectsByService(String serviceId){
//        return getInstance().getWSManager().getCadastreService().getCadastreObjectsByService(
//                serviceId);
//    }
//    
//    public void saveCadastreChange(CadastreChangeTO cadastreChangeTO){
//        getInstance().getWSManager().getCadastreService().saveCadastreChange(cadastreChangeTO);
//    }

    public CadastreChangeBean getCadastreChange(String serviceId){
        CadastreChangeTO cadastreChangeTO = 
                getInstance().getWSManager().getCadastreService().getCadastreChange(serviceId);
        CadastreChangeBean cadastreChangeBean = new CadastreChangeBean();
        MappingManager.getMapper().map(cadastreChangeTO, cadastreChangeBean);
        return cadastreChangeBean;
    }
}
