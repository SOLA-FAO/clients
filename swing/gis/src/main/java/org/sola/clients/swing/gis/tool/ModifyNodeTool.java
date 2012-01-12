/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import org.geotools.geometry.Envelope2D;
import org.geotools.swing.tool.extended.ExtendedDrawRectangle;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * It starts the process of changing a node in the parcel layer. <br/>
 * 
 * @author Elton Manoku
 */
public class ModifyNodeTool extends ExtendedDrawRectangle {
    
     private String toolName = "change-node";
     private String toolTip =  MessageUtility.getLocalizedMessage(
                            GisMessage.CADASTRE_TOOLTIP_CHANGE_NODE).getMessage();

    public ModifyNodeTool() {
        this.setToolName(toolName);
        this.setIconImage("resources/change-node.png");
        this.setToolTip(toolTip);
    }

    @Override
    protected void onRectangleFinished(Envelope2D env) {
        
    }
    
}
