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
package org.sola.clients.swing.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.*;
import java.util.logging.Level;
import org.sola.common.SOLAException;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.exception.WebServiceClientException;

/** Handles all uncaught runtime exceptions */
public class DefaultExceptionHandler implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace(System.out);
        handleException(e);
    }

    /** 
     * Checks exception type and displays appropriate message. 
     * @param t the exception.
     */
    public static void handleException(Throwable t) {
        if (hasCause(t, WebServiceClientException.class)) {
            WebServiceClientException wex = getCause(t, WebServiceClientException.class);
                MessageUtility.displayMessage(wex.getMessageCode(), wex.getErrorNumber(),
                        wex.getMessageParameters());
        } else if (hasCause(t, SOLAException.class)) {
            SOLAException solaex = getCause(t, SOLAException.class);
            MessageUtility.displayMessage(solaex.getMessage(), solaex.getMessageParameters());
        } else {
            String localMessage = t.getLocalizedMessage();
            try {
                LogUtility.log(getStackTraceAsString(t), Level.SEVERE);
            } catch (Exception logEx) {
                localMessage = "Log error: " + logEx.getLocalizedMessage()
                        + System.getProperty("line.separator") + "Original error: "
                        + t.getLocalizedMessage();
            }
            Object[] parms = {localMessage};
            MessageUtility.displayMessage(ClientMessage.GENERAL_UNEXPECTED, parms);
        }
    }

    /**
     * Formats the stacktrace for an exception into a string
     * @param t The throwable exception
     * @return The stacktrace of the exception formatted as a string
     */
    public static String getStackTraceAsString(Throwable t) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        return result.toString();
    }
    
        /**
     * Loops through the exception hierarchy to determine if it has
     * the specified cause
     * @param t The throwable exception that has been caught
     * @param causeType The type of cause to check for. 
     * @return true if the cause type exists in the exception hierarchy. 
     */
    public static boolean hasCause(Throwable t, Class causeType) {
        return !(getCause(t, causeType) == null);
    }

    /**
     * Loops through the exception hierarchy to get the cause of the specified type
     * @param t The throwable exception that has been caught
     * @param causeType The type of cause to check for. 
     * @return the exception of the specified type or null. 
     */
    public static <T> T getCause(Throwable t, Class<T> causeType) {
        T result = null;
        if (t.getClass() == causeType) {
            result = (T) t;
        } else {
            if (t.getCause() != null) {
                result = getCause(t.getCause(), causeType);
            }
        }
        return result;
    }
}