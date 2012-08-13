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
package org.sola.clients.swing.gis;

import java.lang.reflect.Field;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This class overrides the Messaging class of geotools ui in order to override the 
 * show message method of that geotools ui. By doing so, the messages that will be displayed in the
 * geotools ui can be replaced from the messages in the sola projects.
 * 
 * @author Elton Manoku
 */
public class Messaging extends org.geotools.swing.extended.util.Messaging {

    private static java.util.ResourceBundle bundleForLayerTitles = 
                java.util.ResourceBundle.getBundle(
                "org/sola/clients/swing/gis/layer/resources/LayerTitles");

    public Messaging() {
    }
    
    /**
     * This method is called in geotools ui to show messages.
     * @param msg 
     */
    @Override
    public void show(String messageCode, Object... args) {
        String msgSolaCode = this.getSolaMessageCode(messageCode);
        MessageUtility.displayMessage(msgSolaCode, args);
    }

    @Override
    public String getMessageText(String messageCode, Object... args) {
        String msgSolaCode = this.getSolaMessageCode(messageCode);
        return MessageUtility.getLocalizedMessage(msgSolaCode, args).getMessage();
    }
    
    public String getLayerTitle(String layerName){
        String layerTitle = layerName;
        if (bundleForLayerTitles.containsKey(layerName)){
            layerTitle = bundleForLayerTitles.getString(layerName);
        }
        return layerTitle;
    }
    
    private String getSolaMessageCode(String messageId) {
        String solaMsgCode = messageId;
        try {
            Field field = GisMessage.class.getField(messageId);
            solaMsgCode = field.get(null).toString();

        } catch (NoSuchFieldException ex) {
            //To be ignored
        } catch (IllegalAccessException ex) {
            LogUtility.log("Error trying to get message sola code for message id:" + messageId, ex);
        }
        return solaMsgCode;
    }
}
