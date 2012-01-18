/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.tool.extended;

import java.awt.geom.Point2D;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.swing.event.MapMouseEvent;

/**
 *
 * @author Elton Manoku
 */
public abstract class ExtendedDrawRectangle extends ExtendedTool{
    private Point2D startDragPos = new DirectPosition2D();
    private boolean dragged = false;
    
        
    /**
     * Records the map position of the mouse event in case this
     * button press is the beginning of a mouse drag
     *
     * @param ev the mouse event
     */
    @Override
    public void onMousePressed(MapMouseEvent ev) {
        startDragPos.setLocation(ev.getMapPosition());
    }

    /**
     * Records that the mouse is being dragged
     *
     * @param ev the mouse event
     */
    @Override
    public void onMouseDragged(MapMouseEvent ev) {
        dragged = true;
    }

    /**
     * If the mouse was dragged, determines the bounds of the
     * box that the user defined and passes this to the mapPane's
     * {@link org.geotools.swing.JMapPane#setDisplayArea(org.opengis.geometry.Envelope) }
     * method
     *
     * @param ev the mouse event
     */
    @Override
    public void onMouseReleased(MapMouseEvent ev) {
        if (dragged && !ev.getPoint().equals(startDragPos)) {
            dragged = false;
            Envelope2D env = new Envelope2D();
            env.setFrameFromDiagonal(startDragPos, ev.getMapPosition());
            this.onRectangleFinished(env);
        }
    }
    
    protected void onRectangleFinished(Envelope2D env){
        
    }
    
    /**
     * Returns true to indicate that this tool draws a box
     * on the map display when the mouse is being dragged
     */
    @Override
    public boolean drawDragBox() {
        return true;
    }
    
}
