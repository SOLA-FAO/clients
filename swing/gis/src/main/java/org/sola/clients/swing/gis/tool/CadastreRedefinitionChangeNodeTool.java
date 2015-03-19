/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.gis.tool;

import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.tool.extended.ExtendedEditGeometryTool;
import org.sola.clients.swing.gis.layer.CadastreRedefinitionNodeLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * The tool is used during the redefine cadastre process. It is used as an alternative
 * to interactively change a node that has been targeted for change.
 * The node should be first selected by using the Add node tool or Select node tool.
 * 
 * @author Elton Manoku
 */
public class CadastreRedefinitionChangeNodeTool extends ExtendedEditGeometryTool {
    
    private final static String NAME = "cadastre-redefinition-change-node";
    private String toolTip = MessageUtility.getLocalizedMessage(
            GisMessage.CADASTRE_REDEFINITION_CHANGE_NODE).getMessage();

    /**
     * 
     * @param nodeLayer The layer where the nodes are managed.
     */
    public CadastreRedefinitionChangeNodeTool(
            CadastreRedefinitionNodeLayer nodeLayer) {
        this.setToolName(NAME);
        this.setIconImage("resources/node-linking.png");
        this.setToolTip(toolTip);
        this.setGeometryType(Geometries.POINT);
        this.layer = nodeLayer;
    }

}
