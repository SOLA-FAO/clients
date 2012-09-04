/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.awt.Point;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.extended.util.Messaging;

/**
 * It pans the  map. It is based in the {@see org.geotools.swing.tool.PanTool}
 * 
 * @author Elton Manoku
 */
public class ExtendedPan extends ExtendedTool {

    public static final String NAME = "pan";
    private boolean panning = false;
    private Point panePos;
    private String toolTip =  
            Messaging.getInstance().getMessageText(Messaging.Ids.GEOTOOL_TOOLTIP_PAN.toString());
   
    /**
     * Creates the tool.
     */
    public ExtendedPan() {
        this.setToolName(NAME);
        this.setToolTip(toolTip);
    }

    /**
     * Respond to a mouse button press event from the map mapPane. This may
     * signal the start of a mouse drag. Records the event's window position.
     * @param ev the mouse event
     */
    @Override
    public void onMousePressed(MapMouseEvent ev) {
        panePos = ev.getPoint();
        panning = true;
    }

    /**
     * Respond to a mouse dragged event. Calls {@link org.geotools.swing.JMapPane#moveImage()}
     * @param ev the mouse event
     */
    @Override
    public void onMouseDragged(MapMouseEvent ev) {
        if (panning) {
            Point pos = ev.getPoint();
            if (!pos.equals(panePos)) {
                getMapControl().moveImage(pos.x - panePos.x, pos.y - panePos.y);
                panePos = pos;
            }
        }
    }

    /**
     * If this button release is the end of a mouse dragged event, requests the
     * map mapPane to repaint the display
     * @param ev the mouse event
     */
    @Override
    public void onMouseReleased(MapMouseEvent ev) {
        panning = false;
        getMapControl().refresh();
    }
}
