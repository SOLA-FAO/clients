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
package org.sola.clients.swing.gis.ui.controlsbundle;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.sola.clients.swing.gis.Messaging;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.mapaction.LocateApplicationRemove;
import org.sola.clients.swing.gis.tool.LocateApplicationTool;
import org.sola.common.messaging.GisMessage;

/**
 * A control bundle that is used in the application form to define the application location.
 * 
 * @author Elton Manoku
 */
public final class ControlsBundleForApplicationLocation extends SolaControlsBundle {

    LocateApplicationTool locationTool = null;
    WKBWriter wkbWritter = null;
    WKBReader wkbReader = null;

    public ControlsBundleForApplicationLocation() {
        super();
        this.Setup(PojoDataAccess.getInstance());
    }

    @Override
    public void Setup(PojoDataAccess pojoDataAccess) {
        super.Setup(pojoDataAccess);
        locationTool = new LocateApplicationTool();
        this.getMap().addTool(locationTool, this.getToolbar(), true);
        this.getMap().addMapAction(
                new LocateApplicationRemove(this), this.getToolbar(), true);

        //Initialize a writter that can be used when saving the location
        wkbWritter = new WKBWriter();
        wkbReader = new WKBReader();

    }

    /**
     * @return the applicationLocation
     */
    public byte[] getApplicationLocation() {
        byte[] applicationLocation = null;
        Geometry locationGeom = this.locationTool.getLocationGeometry();
        if (locationGeom != null) {
            applicationLocation = this.wkbWritter.write(locationGeom);
        }
        return applicationLocation;
    }

    /**
     * @param applicationLocation the applicationLocation to set
     */
    public void setApplicationLocation(byte[] applicationLocation) {
        //Add the location of the application if it exists
        if (applicationLocation != null) {
            try {
                Geometry geom = this.wkbReader.read(applicationLocation);
                this.locationTool.setLocationGeometry(geom);
                ReferencedEnvelope envelope = JTS.toEnvelope(geom);
                envelope.expandBy(2000);
                this.getMap().setDisplayArea(envelope);
            } catch (ParseException ex) {
                Messaging.getInstance().show(GisMessage.LOCATE_ERROR_APPLICATION);
                org.sola.common.logging.LogUtility.log(
                        GisMessage.LOCATE_ERROR_APPLICATION, ex);
            }
        } else {
            this.locationTool.setLocationGeometry(null);
            this.getMap().zoomToFullExtent();
        }
    }
}
