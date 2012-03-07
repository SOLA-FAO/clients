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
 * An abstract tool that draws a rectangle in the map.
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
        startDragPos.setLocation(ev.getWorldPos());
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
     *
     * @param ev
     */
    @Override
    public void onMouseReleased(MapMouseEvent ev) {
        if (dragged && !ev.getPoint().equals(startDragPos)) {
            dragged = false;
            Envelope2D env = new Envelope2D();
            env.setFrameFromDiagonal(startDragPos, ev.getWorldPos());
            this.onRectangleFinished(env);
        }
    }
    
    /**
     * It is called after the drawing process is finished.
     * <br/>
     * Override this method to implement functionality at the end of the drawing process.
     * 
     * @param env The envelope drawn
     */
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
