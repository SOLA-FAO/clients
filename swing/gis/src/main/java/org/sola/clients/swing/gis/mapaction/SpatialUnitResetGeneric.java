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

import org.geotools.swing.mapaction.extended.ExtendedAction;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForSpatialUnitGenericEditor;

/**
 * This map action is used in the control bundle used in the application form. It is used to reset
 * the application location.
 *
 * @author Elton Manoku
 */
public final class SpatialUnitResetGeneric extends ExtendedAction {

    private final static String MAPACTION_NAME = "SpatialUnitResetGeneric";
    private ControlsBundleForSpatialUnitGenericEditor controlsBundle;
    
    /**
     * Constructor of the map action that is used to reset all editing actions
     * that are not yet sent to server.
     * 
     * @param mapControl  The map control with which the map action will interact 
     */
    public SpatialUnitResetGeneric(ControlsBundleForSpatialUnitGenericEditor mapControl) {
        super(mapControl.getMap(), MAPACTION_NAME,
                ((Messaging)Messaging.getInstance()).getMapActionString(
                String.format("%s.tooltip",MAPACTION_NAME)),
                "resources/reset.png");
        this.controlsBundle = mapControl;
    }

    @Override
    public void onClick() {
        this.controlsBundle.getLayer().removeAllBeans();
    }
}