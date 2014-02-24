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
package org.sola.clients.swing.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.*;
import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.common.DefaultExceptionHandler;
import org.sola.clients.swing.ui.validation.ValidationResultForm;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.exception.WebServiceClientException;
import org.sola.services.boundary.wsclients.exception.WebServiceClientExceptionType;

/** Handles all uncaught runtime exceptions in Desktop client application. */
public class DesktopClientExceptionHandler extends DefaultExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace(System.out);
        DesktopClientExceptionHandler.handleException(e);
    }

    /** 
     * Checks exception type and displays appropriate message. 
     * @param t the exception.
     */
    public static void handleException(Throwable t) {
         if (DesktopClientExceptionHandler.hasCause(t, WebServiceClientException.class)){

            WebServiceClientException wex = 
                    DesktopClientExceptionHandler.getCause(t, WebServiceClientException.class);

            if (wex.getType() == WebServiceClientExceptionType.SOLA_VALIDATION_FAILED
                    && wex.getValidationResult() != null) {

                String message = "";

                if (wex.getMessageCode() != null) {
                    message = MessageUtility.getLocalizedMessage(wex.getMessageCode(),
                            wex.getMessageParameters()).getMessage();
                } else {
                    message = MessageUtility.getLocalizedMessage(
                            ClientMessage.ERR_BR_VIOLATION).getMessage();
                }

                ArrayList<ValidationResultBean> results = new ArrayList<ValidationResultBean>();

                TypeConverters.TransferObjectListToBeanList(wex.getValidationResult(),
                        ValidationResultBean.class, (List) results);

                ValidationResultForm resultForm = new ValidationResultForm(
                        null, true, results, false, message);
                
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                int x = ((dim.width) / 2);
                int y = ((dim.height) / 2);
                x = x - (resultForm.getWidth() / 2);
                y = y - (resultForm.getHeight() / 2);
                
                resultForm.setLocation(x, y);
                resultForm.setVisible(true);

            } else {
                DefaultExceptionHandler.handleException(t);
            }
        }else{
            DefaultExceptionHandler.handleException(t);
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
}