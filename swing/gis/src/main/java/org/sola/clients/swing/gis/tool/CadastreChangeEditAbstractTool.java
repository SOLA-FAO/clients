/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.tool;

import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.geotools.swing.tool.extended.ExtendedEditGeometryTool;

/**
 * An abstract class, that is used as a parent from editing tools that if activated, must reset
 * the irregular boundary definition procedure.
 * 
 * @author Elton Manoku
 */
public abstract class CadastreChangeEditAbstractTool extends ExtendedEditGeometryTool {

    @Override
    public void onSelectionChanged(boolean selected) {
        super.onSelectionChanged(selected);
        if (selected) {
            ExtendedAction action =
                    this.getMapControl().getMapActionByName(CadastreBoundarySelectTool.NAME);
            if (action != null) {
                ((CadastreBoundarySelectTool) action.getAttachedTool()).clearSelection();
                this.getMapControl().refresh();
            }
        }
    }
}
