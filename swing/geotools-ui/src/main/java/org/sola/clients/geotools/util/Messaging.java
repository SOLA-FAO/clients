/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.geotools.util;

/**
 * This class handles showing of the messages. It can be extended by 
 * by the applications to use their library of showing messages.
 * To use the libraries of other applications, override {@code  show(String msg, Object ... args)}
 * and {@code getMessageText(String messageId, Object ... args)}
 *
 * @author Elton Manoku (July 2011)
 *  
 */
public class Messaging {

    /**
     * Constants representing message ids used in the library.
     */
    public static enum Ids {
        ADDING_FEATURE_ERROR,
        MAPCONTROL_MAPCONTEXT_WITHOUT_SRID_ERROR,
        DRAWINGTOOL_GEOMETRY_NOT_VALID_ERROR,
        LAYERGRAPHICS_STARTUP_ERROR,
        SHAPEFILELAYER_FILE_NOT_FOUND_ERROR,
        REMOVE_ALL_FEATURES_ERROR,
        LAYER_NOT_ADDED_ERROR,
        WMSLAYER_NOT_INITIALIZED_ERROR,
        WMSLAYER_LAYER_NOT_FOUND_ERROR,
        UTILITIES_SLD_DOESNOT_EXIST_ERROR,
        UTILITIES_SLD_LOADING_ERROR,
        UTILITIES_COORDSYS_COULDNOT_BE_CREATED_ERROR,
        DRAWINGTOOL_NOT_ENOUGH_POINTS_INFORMATIVE
    };

    private static Messaging messaging = new Messaging();
    
    /**
     * Gets a singletone instance of the Messaging class
     * @return 
     */
    public static Messaging getInstance(){
        return messaging;
    }
    
    public void setMessaging(Messaging messagingLibrary){
        messaging = messagingLibrary;
    }
    /**
     * It shows a message by getting as a parameter a message id or a message text.
     * First it checks if the message id is from the list, if not then it will be considered
     * as message text. 
     * This method can be overridden by other applications to customize way the message is shown.
     * 
     * @param msg The message id or the message text
     * @param args arguments if the message text accepts them
     */
    public void show(String msg, Object ... args) {
        String msgBody = getInstance().getMessageText(msg, args);
        javax.swing.JOptionPane.showMessageDialog(null, msgBody);
    }

    /**
     * Gets the message body based in the id supplied.
     * @param messageId the message id.
     * @return the text message if an id is found. If not then the id is returned.
     */
    private String getMessageText(String messageId) {
        String msgBody = messageId;
        if (messageId.equals(Messaging.Ids.ADDING_FEATURE_ERROR.toString())) {
            msgBody = "Error while adding a feature in the collection.";
        } else if (messageId.equals(Messaging.Ids.MAPCONTROL_MAPCONTEXT_WITHOUT_SRID_ERROR.toString())) {
            msgBody = "The map context of the map control does not have an SRID.";
        } else if (messageId.equals(Messaging.Ids.DRAWINGTOOL_GEOMETRY_NOT_VALID_ERROR.toString())) {
            msgBody = "Geometry is not valid.";
        } else if (messageId.equals(Messaging.Ids.LAYERGRAPHICS_STARTUP_ERROR.toString())) {
            msgBody = "Error while starting up the graphics layer.";
        } else if (messageId.equals(Messaging.Ids.SHAPEFILELAYER_FILE_NOT_FOUND_ERROR.toString())) {
            msgBody = "Shapefile not found.";
        } else if (messageId.equals(Messaging.Ids.REMOVE_ALL_FEATURES_ERROR.toString())) {
            msgBody = "Error while removing all features.";
        } else if (messageId.equals(Messaging.Ids.LAYER_NOT_ADDED_ERROR.toString())) {
            msgBody = "Layer could not be added.";
        } else if (messageId.equals(Messaging.Ids.WMSLAYER_NOT_INITIALIZED_ERROR.toString())) {
            msgBody = "WMS Layer is not initialized.";
        } else if (messageId.equals(Messaging.Ids.WMSLAYER_LAYER_NOT_FOUND_ERROR.toString())) {
            msgBody = "Layer %s not found in the wms server.";
        } else if (messageId.equals(Messaging.Ids.UTILITIES_SLD_DOESNOT_EXIST_ERROR.toString())) {
            msgBody = "SLD Resource %s does not exist.";
        } else if (messageId.equals(Messaging.Ids.UTILITIES_SLD_LOADING_ERROR.toString())) {
            msgBody = "Error while retrieving the SLD style.";
        } else if (messageId.equals(Messaging.Ids.UTILITIES_COORDSYS_COULDNOT_BE_CREATED_ERROR.toString())) {
            msgBody = "Coordinative system for sird %s could not be created.";
        } else if (messageId.equals(Messaging.Ids.DRAWINGTOOL_NOT_ENOUGH_POINTS_INFORMATIVE.toString())) {
            msgBody = "There are not enough points to create the geometry.";
        }
        return msgBody;
    }

    /**
     * It gets the message body with arguments supplied in it. The arguments are replaced 
     * as in a formating String function.
     * @param messageId Id of the message
     * @param args Arguments to be replaced
     * @return It returns the formatted string otherwise the message id
     */
    public String getMessageText(String messageId, Object ... args){
        String msgBody = getInstance().getMessageText(messageId);
        return String.format(msgBody, args);
    }
}
