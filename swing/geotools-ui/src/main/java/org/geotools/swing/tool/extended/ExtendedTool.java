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

package org.geotools.swing.tool.extended;


import org.geotools.swing.tool.CursorTool;
import org.geotools.swing.extended.Map;
import org.geotools.swing.mapaction.extended.ExtendedAction;

/**
 * Basic tool that implements common functionality for all tools that can be used 
 * with the map control.
 * 
 * @author Elton Manoku
 */
public abstract class ExtendedTool extends CursorTool {

    private String toolName = null;
    private String toolTip = "";
    private String iconImage = "resources/blank.png";
    private ExtendedAction actionContainer;
    
    /**
     * Set the map pane that this cursor tool is associated with
     * @param pane the map pane
     * 
     * @throws IllegalArgumentException if mapPane is null
     */
    public void setMapControl(Map mapControl) {
        if (mapControl == null) {
            throw new IllegalArgumentException("mapControl is null. Give it....");
        }
        this.setMapPane(mapControl);
    }



    /**
     * Get the map pane that this tool is servicing
     *
     * @return the map pane
     */
    public Map getMapControl() {
        return (Map)this.getMapPane();
    }

    /**
     * @return the toolName
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * @param toolName the toolName to set
     */
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    /**
     * @return the toolTip
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * @param toolTip the toolTip to set
     */
    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    /**
     * @return the iconImage
     */
    public String getIconImage() {
        return iconImage;
    }

    /**
     * @param iconImage the iconImage to set
     */
    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }

    public ExtendedAction getActionContainer() {
        return actionContainer;
    }

    public void setActionContainer(ExtendedAction actionContainer) {
        this.actionContainer = actionContainer;
    }
    
    
    /**
     * This is called if a tool is made active or not active. It can be overridden by
     * subtypes to take certain actions.
     * @param isActive True if the tool is selected.
     */
    public void onSelectionChanged(boolean selected){
    }
}
