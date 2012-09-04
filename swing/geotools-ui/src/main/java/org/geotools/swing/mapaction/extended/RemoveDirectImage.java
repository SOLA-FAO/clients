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
package org.geotools.swing.mapaction.extended;

import org.geotools.swing.extended.Map;
import org.geotools.swing.extended.util.Messaging;
import org.geotools.swing.tool.extended.AddDirectImageTool;

/**
 * This is a command that is used to reset the adding of a direct image in the map.
 * It requires that in the toolbar is also added 
 * a tool {@see org.geotools.swing.tool.extended.AddDirectImageTool}.
 * 
 * @author Elton Manoku
 */
public class RemoveDirectImage extends ExtendedAction{
    
    /**
     * Creates the map action.
     * 
     * @param mapControl The map control with which the map action will interact
     */
    public RemoveDirectImage(Map mapControl) {        
        super(mapControl, "image-remove", Messaging.getInstance().getMessageText(
                Messaging.Ids.REMOVE_DIRECT_IMAGE_TOOLTIP.toString()), 
                "resources/image-remove.png");
    }

    @Override
    public void onClick(){
        this.getAddTool().reset();
    }

    private AddDirectImageTool getAddTool() {
        ExtendedAction selectAction =
                this.getMapControl().getMapActionByName(AddDirectImageTool.NAME);
        return (AddDirectImageTool) selectAction.getAttachedTool();
    }
}
