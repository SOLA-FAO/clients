/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import org.geotools.geometry.jts.Geometries;
import org.geotools.swing.extended.Map;
import org.geotools.swing.tool.extended.ExtendedEditGeometryTool;
import org.sola.clients.swing.gis.layer.PojoBaseLayer;
import org.sola.clients.swing.gis.layer.SpatialUnitGroupLayer;

/**
 * Tool that is used for adding or editing a spatial unit group.
 *
 * @author Elton Manoku
 */
public class SpatialUnitGroupEdit extends ExtendedEditGeometryTool {

    private String toolName = "SpatialUnitGroupEdit";
    private static java.util.ResourceBundle resource =
            java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/tool/resources/strings");
    private final static String SPATIAL_UNIT_GROUP_LAYER_PREFIX = "sug_";

    public SpatialUnitGroupEdit(SpatialUnitGroupLayer layer) {
        this.setToolName(toolName);
        this.setIconImage("resources/spatial_unit_group_edit.png");
        this.setToolTip(resource.getString(
                String.format("%s.tooltip", this.getClass().getSimpleName())));
        this.setGeometryType(Geometries.POLYGON);
        this.layer = layer;
    }

    /**
     * It is overridden to define the target snapped layers during editing.
     * @param mapControl 
     */
    @Override
    public void setMapControl(Map mapControl) {
        super.setMapControl(mapControl);
        for (String layerName : this.getMapControl().getSolaLayers().keySet()) {
            if (layerName.startsWith(SPATIAL_UNIT_GROUP_LAYER_PREFIX)) {
                this.getTargetSnappingLayers().add(
                        (PojoBaseLayer) this.getMapControl().getSolaLayers().get(layerName));
            }
        }

    }
}
