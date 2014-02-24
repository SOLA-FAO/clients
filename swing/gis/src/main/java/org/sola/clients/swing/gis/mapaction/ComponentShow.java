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
package org.sola.clients.swing.gis.mapaction;

import java.awt.Component;
import org.geotools.swing.extended.Map;
import org.geotools.swing.mapaction.extended.ExtendedAction;

/**
 * It is an abstract map action which can be used to show/hide a gui component.
 *
 * @author Elton Manoku
 */
public abstract class ComponentShow extends ExtendedAction {

    private Component componentToShow;

    /**
     * Constructor
     * 
     * @param mapCtrl The map control with which the map action will interact 
     * @param componentToShow The component to hide/show
     * @param name The name
     * @param tooltip Tooltip to be shown on mouse over
     * @param iconResource The resource of the icon to be displayed
     */
    public ComponentShow(Map mapCtrl, Component componentToShow,
            String name, String tooltip, String iconResource) {
        super(mapCtrl, name, tooltip, iconResource);
        this.componentToShow = componentToShow;
    }

    /**
     * It shows / hides the component
     */
    @Override
    public void onClick() {
        this.componentToShow.setVisible(!this.componentToShow.isVisible());
    }
}
