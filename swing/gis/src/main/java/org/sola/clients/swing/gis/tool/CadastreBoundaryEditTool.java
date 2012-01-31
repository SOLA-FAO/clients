/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import org.geotools.geometry.jts.Geometries;
import org.geotools.map.extended.layer.ExtendedLayerGraphics;
import org.geotools.swing.tool.extended.ExtendedDrawToolWithSnapping;
import org.sola.clients.swing.gis.layer.CadastreBoundaryPointLayer;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * It handles the editing of a selected boundary. The boundary is already defined 
 * by the points found in the pointLayer.
 * Do not forget to set the targetLayer as well.
 * @author Elton Manoku
 */
public class CadastreBoundaryEditTool extends ExtendedDrawToolWithSnapping {
    private String toolName = "cadastre-boundary-edit";
    private String toolTip = MessageUtility.getLocalizedMessage(
                            GisMessage.CADASTRE_BOUNDARY_EDIT_TOOL_TOOLTIP).getMessage();
    
    private ExtendedLayerGraphics targetLayer;
    private CadastreBoundaryPointLayer pointLayer;

    public CadastreBoundaryEditTool(CadastreBoundaryPointLayer pointLayer) {
        this.setToolName(toolName);
        this.setGeometryType(Geometries.LINESTRING);
        this.setToolTip(toolTip);
        this.pointLayer = pointLayer;
        this.getTargetSnappingLayers().add(this.pointLayer);
    }

    public void setTargetLayer(ExtendedLayerGraphics targetLayer) {
        this.targetLayer = targetLayer;
    }

}
