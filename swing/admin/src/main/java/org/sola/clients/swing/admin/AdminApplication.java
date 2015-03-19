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
package org.sola.clients.swing.admin;

/*
 *  Copyright (C) 2011 - Food and Agriculture Orgainsation (FAO) of the United Nations (UN)
 *  All rights reserved 
 * 
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *  
 *     1. Redistributions of source code must retain the above copyright notice,this list
 *        of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright notice,this list
 *        of conditions and the following disclaimer in the documentation and/or other
 *        materials provided with the distribution.
 *     3. Neither the name of FAO nor the names of its contributors may be used to endorse or 
 *        promote products derived from this software without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 *  OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import org.sola.clients.swing.ui.security.LoginForm;
import org.sola.clients.swing.ui.DesktopClientExceptionHandler;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.swing.common.laf.LafManager;
import org.sola.clients.swing.ui.localization.LocalizationManager;
import org.sola.clients.swing.ui.security.LoginPanel;
import org.sola.common.WindowUtility;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * The main class of the application.
 */
public class AdminApplication {

    /**
     * Main method to run the application.
     *
     * @param args Array of input parameters.
     */
    public static void main(String[] args) {
        // #321 Set class to record user preferences for
        WindowUtility.setMainAppClass(AdminApplication.class);
        // Show splash screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = ((dim.width) / 2);
        int y = ((dim.height) / 2);

        SplashForm splash = new SplashForm();
        splash.setLocation(x - (splash.getWidth() / 2), y - (splash.getHeight() / 2));
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
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                int x = ((dim.width) / 2);
                int y = ((dim.height) / 2);

                Thread.setDefaultUncaughtExceptionHandler(new DesktopClientExceptionHandler());
                LocalizationManager.loadLanguage();
                LogUtility.initialize(AdminApplication.class);

                // Select the Look and Feel Theme based on whether this is 
                // the production version or the test version of SOLA. 
                if (LocalizationManager.isProductionHost()) {
                    LafManager.getInstance().setProperties(LafManager.ADMIN_THEME);
                } else {
                    LafManager.getInstance().setProperties(LafManager.GREEN_THEME);
                }

                final LoginForm loginForm = new LoginForm();
                loginForm.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(LoginPanel.LOGIN_RESULT)) {
                            if (((Boolean) evt.getNewValue())) {
                                // Check admin roles
                                if (!WSManager.getInstance().getAdminService().isUserAdmin()) {
                                    MessageUtility.displayMessage(ClientMessage.ADMIN_NO_ADMIN_RIGHTS);
                                    loginForm.enableLoginPanel(true);
                                } else {
                                    loginForm.dispose();
                                    MainForm mainForm = new MainForm();
                                    mainForm.setVisible(true);
                                }
                            }
                        }
                    }
                });
                loginForm.setLocation(x - (loginForm.getWidth() / 2), y - (loginForm.getHeight() / 2));
                loginForm.setVisible(true);
            }
        });
    }
}
