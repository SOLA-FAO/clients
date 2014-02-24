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
package org.sola.clients.swing.gis.tool;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.extended.ExtendedTool;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.beans.CadastreObjectTargetBean;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.layer.CadastreChangeTargetCadastreObjectLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTO;

/**
 * Tool used during the cadastre change to select target cadastre objects.
 *
 * @author MariaPaola Rizzo
 */
public class CadastreChangeSelectCadastreObjectTool
        extends ExtendedTool implements TargetCadastreObjectTool {

    public final static String NAME = "select-parcel";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_CHANGE_TOOLTIP_SELECT_PARCEL).getMessage();
    private CadastreChangeTargetCadastreObjectLayer targetParcelsLayer = null;
    private PojoDataAccess dataAccess;
    private String cadastreObjectType;

    /**
     * Constructor
     *
     * @param dataAccess The data access that handles communication with the web services
     */
    public CadastreChangeSelectCadastreObjectTool(PojoDataAccess dataAccess) {
        this.setToolName(NAME);
        this.setIconImage(String.format("resources/%s.png", NAME));
        this.setToolTip(toolTip);
        this.dataAccess = dataAccess;
    }

    /**
     * Gets the target cadastre object layer
     *
     * @return
     */
    public CadastreChangeTargetCadastreObjectLayer getTargetParcelsLayer() {
        return targetParcelsLayer;
    }

    /**
     * Sets the target cadastre object layer
     *
     * @param targetParcelsLayer
     */
    public void setTargetParcelsLayer(CadastreChangeTargetCadastreObjectLayer targetParcelsLayer) {
        this.targetParcelsLayer = targetParcelsLayer;
    }

    @Override
    public void setCadastreObjectType(String cadastreObjectType) {
        this.cadastreObjectType = cadastreObjectType;
    }

    /**
     * The action of this tool. If a cadastre object is already selected it will be unselected,
     * otherwise it will be selected.
     *
     * @param ev
     */
    @Override
    public void onMouseClicked(MapMouseEvent ev) {
        DirectPosition2D pos = ev.getWorldPos();
        CadastreObjectTO cadastreObject =
                this.dataAccess.getCadastreService().getCadastreObjectByPoint(
                pos.x, pos.y, this.getMapControl().getSrid(), this.cadastreObjectType);
        if (cadastreObject == null) {
            Messaging.getInstance().show(GisMessage.PARCEL_TARGET_NOT_FOUND);
            return;
        }
        CadastreObjectTargetBean bean = this.getBeanFromCadastreObjectId(
                cadastreObject.getId());
        if (bean != null) {
            this.targetParcelsLayer.getBeanList().remove(bean);
        } else {
            bean = new CadastreObjectTargetBean();
            bean.setCadastreObjectId(cadastreObject.getId());
            bean.setGeomPolygonCurrent(cadastreObject.getGeomPolygon());
            this.targetParcelsLayer.getBeanList().add(bean);
        }
    }

    /**
     * Gets the CadastreObjectTargetBean matching the cadastre object id
     *
     * @param id The id of the cadastre object
     * @return An instance of CadastreObjectTargetBean with the cadastreObjectId matching the id or
     * if not found returns null.
     */
    private CadastreObjectTargetBean getBeanFromCadastreObjectId(String id) {
        for (CadastreObjectTargetBean bean : targetParcelsLayer.getBeanList()) {
            if (bean.getCadastreObjectId().equals(id)) {
                return bean;
            }
        }
        return null;
    }
}
