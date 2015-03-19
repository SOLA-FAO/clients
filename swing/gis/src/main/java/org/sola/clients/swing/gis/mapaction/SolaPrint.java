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
package org.sola.clients.swing.gis.mapaction;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import org.geotools.swing.extended.Map;
import org.geotools.swing.mapaction.extended.Print;
import org.geotools.swing.mapaction.extended.print.PrintLayout;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.security.SecurityBean;
/**
 * This map action extends the Print map action that handles the print of the map according to a
 * layout. The user name and date is added in the layout and also it logs against the application
 * (if the application is present) the action of printing.
 *
 * @author Elton Manoku
 */
public class SolaPrint extends Print {

    private final static String FIELD_USER = "{userName}";
    private final static String FIELD_DATE = "{date}";
    
    private String applicationId;

    /**
     * Constructor of the map action.
     * 
     * @param map  The map control with which the map action will interact 
     */
    public SolaPrint(Map map) {
        super(map);
    }

    /**
     * Sets the application id if the map action is found in a bundle where the application is
     * known. This application id is used to log the action of printing against the application.
     *
     * @param applicationId
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * This method calls the start screen for the print action. 
     * If it is needed to completely change the way the print action starts you should rewrite this
     * method. Checkout super.onClick() for an example how you can rewrite it.
     */
    @Override
    public void onClick() {
        super.onClick();
    }

    
    
    
    /**
     * Additionally to the standard functionality of printing, it supplies the values of user and
     * date to the layout so it can print them as well. Also if the print succeeds it logs against
     * the application the action of printing if the application id is present.
     *
     * @param layout
     * @param scale
     * @return
     */
    @Override
    protected String print(
            PrintLayout layout, double scale, java.util.Map<String, Object> extraFields) {
        if (extraFields == null) {
            extraFields = new HashMap<String, Object>();
        }
        extraFields.put(FIELD_USER, SecurityBean.getCurrentUser().getFullUserName());
        extraFields.put(FIELD_DATE,
                DateFormat.getInstance().format(Calendar.getInstance().getTime()));
        String printoutLocation = super.print(layout, scale, extraFields);
        ApplicationServiceBean serviceBean = new ApplicationServiceBean();
        serviceBean.setRequestTypeCode(RequestTypeBean.CODE_CADASTRE_PRINT);
        if (this.applicationId != null) {
            serviceBean.setApplicationId(this.applicationId);
        }

        serviceBean.saveInformationService();

        return printoutLocation;
    }
}
