/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.desktop;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.LocalizationManager;
import org.sola.clients.swing.ui.DesktopClientExceptionHandler;
import org.sola.clients.swing.ui.security.LoginForm;
import org.sola.clients.swing.ui.security.LoginPanel;
import org.sola.common.WindowUtility;
import org.sola.common.logging.LogUtility;

/**
 * The main class of the application.
 */
public class DesktopApplication {

    /**
     * Main method to run the application.
     *
     * @param args Array of input parameters.
     */
    public static void main(String[] args) {
        // #321 Set class to record user preferences for
        WindowUtility.setMainAppClass(DesktopApplication.class);
        // Show splash screen
        SplashForm splash = new SplashForm();
        WindowUtility.centerForm(splash);
        splash.setVisible(true);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        splash.setVisible(false);
        splash.dispose();

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Thread.setDefaultUncaughtExceptionHandler(new DesktopClientExceptionHandler());
                LocalizationManager.loadLanguage();
                LogUtility.initialize(DesktopApplication.class);
                
                // Select the Look and Feel Theme based on whether this is 
                // the production version or the test version of SOLA. 
                if (LocalizationManager.isProductionHost()) {
                    LafManager.getInstance().setProperties("green");
                } else {
                    LafManager.getInstance().setProperties("autumn");
                }

                final LoginForm loginForm = new LoginForm();
                loginForm.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(LoginPanel.LOGIN_RESULT)) {
                            if (((Boolean) evt.getNewValue())) {
                                loginForm.dispose();
                                MainForm.getInstance().setVisible(true);
                            }
                        }
                    }
                });
                WindowUtility.centerForm(loginForm);
                loginForm.setVisible(true);
            }
        });
    }
}
