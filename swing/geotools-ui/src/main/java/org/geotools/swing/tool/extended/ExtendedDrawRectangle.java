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
