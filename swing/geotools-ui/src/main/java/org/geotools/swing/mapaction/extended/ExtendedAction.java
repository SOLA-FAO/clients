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

package org.geotools.swing.mapaction.extended;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.geotools.swing.extended.Map;
import org.geotools.swing.tool.extended.ExtendedTool;

/**
 * Superclass for defining common structure and functionality for the Map Actions.
 * Each map action can also be linked to activate a map tool.
 * 
 * @author Elton Manoku
 */
public class ExtendedAction extends AbstractAction{

    private String name;
    private Map mapControl;
    private ExtendedTool attachedTool;
    
    /**
     * Use an other constructor
     */
    public ExtendedAction(){
    }

    /**
     * Use this constructor when there is not tool linked with this action
     * @param mapControl Map control to be used together with the map action
     * @param name Name of the action
     * @param toolTip Tooltip to be shown when mouse over
     * @param iconImage  The icon image to be shown
     */
    public ExtendedAction(Map mapControl, String name, String toolTip, String iconImage) {
        this.init(mapControl, name, toolTip, this.getClass(), iconImage);
    }

    /**
     * Use this constructor when you want to link a tool with this action
     * @param mapControl
     * @param tool 
     */
    public ExtendedAction(Map mapControl, ExtendedTool tool) {
        this.init(mapControl, tool.getToolName(), 
            tool.getToolTip(), tool.getClass(), tool.getIconImage());
        if (tool != null){
            this.attachTool(tool);
        }
    }

    private void init(Map mapControl, String name, String toolTip, 
            Class<?> resourceClass, String iconImage) {
        this.mapControl = mapControl;
        this.putValue(Action.SHORT_DESCRIPTION, toolTip);

        java.net.URL imageURL = null;
        if (name != null && iconImage.equals("resources/blank.png")) {
            iconImage = String.format("resources/%s.png", name);
        }
        if (iconImage != null) {
            if (resourceClass == null){
                resourceClass = this.getClass();
            }
            imageURL = resourceClass.getResource(iconImage);
            if (imageURL != null){
                this.putValue(Action.SMALL_ICON, new ImageIcon(imageURL)); 
            }
        }

        if (imageURL == null && name != null) {
            this.putValue(Action.NAME, name);
        }
        this.name = name;
    }

    /**
     * Gets name of action
     * @return 
     */
    public String getName(){
        return this.name;
    }
    /**
     * Gets the map control
     * @return 
     */
    public Map getMapControl(){
        return this.mapControl;
    }
        
    /**
     * Gets the linked tool. It can also be null
     * @return 
     */
    public ExtendedTool getAttachedTool(){
        return this.attachedTool;
    }
    
    /**
     * It activates the tool attached with the map action.
     * 
     */
    public void activateTool(){
        if (this.getAttachedTool() != null){
            this.getMapControl().setCursorTool(this.getAttachedTool());
        }
    }

    /**
     * Action to be performed. Nor recommended to be overridden. If you like to expose 
     * extra functionality, better override onClick method.
     * @param ev 
     */
    @Override
    public void actionPerformed(ActionEvent ev){
        this.activateTool();
        this.onClick();
    }   
    
    /**
     * This method can be override if other behavior than the standard behavior is expected.
     */
    public void onClick(){
        //This can be overriden. It is called when the actionPerformed is called.
    }

    private void attachTool(ExtendedTool solaTool){
        if (this.getAttachedTool() == null){
            this.attachedTool = solaTool;
           this.attachedTool.setMapControl(this.getMapControl());
        }
    }
}

