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
