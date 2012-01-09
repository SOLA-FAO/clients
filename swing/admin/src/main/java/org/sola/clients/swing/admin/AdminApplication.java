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
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.LocalizationManager;
import org.sola.clients.swing.ui.security.LoginPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * The main class of the application.
 */
public class AdminApplication extends SingleFrameApplication {

    /** Listens for {@link LoginForm} {@code login} event.*/
    private class LoginFormListener implements PropertyChangeListener {

        private LoginForm loginForm;
        private SingleFrameApplication app;

        public LoginFormListener(LoginForm loginForm, SingleFrameApplication app) {
            this.loginForm = loginForm;
            this.app = app;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(LoginPanel.LOGIN_RESULT)) {
                if (((Boolean) evt.getNewValue())) {
                    // Check admin roles
                    if(!WSManager.getInstance().getAdminService().isUserAdmin()){
                        MessageUtility.displayMessage(ClientMessage.ADMIN_NO_ADMIN_RIGHTS);
                        loginForm.enableLoginPanel(true);
                    }else{
                        loginForm.dispose();
                        app.show(new MainForm());
                    }
                }
            }
        }
    }
    
    /**
     * Creates and shows the main form of the application.
     */
    @Override
    protected void startup() {
        Thread.setDefaultUncaughtExceptionHandler(new DesktopClientExceptionHandler());
        LocalizationManager.loadLanguage(AdminApplication.class);
        LafManager.getInstance().setProperties("green");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = ((dim.width) / 2);
        int y = ((dim.height) / 2);

        LoginForm loginForm = new LoginForm(this);
        loginForm.addPropertyChangeListener(new LoginFormListener(loginForm, this));
        loginForm.setLocation(x - (loginForm.getWidth() / 2), y - (loginForm.getHeight() / 2));
        loginForm.setVisible(true);
    }

    /** 
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of AdminApplication
     */
    public static AdminApplication getApplication() {
        return Application.getInstance(AdminApplication.class);
    }

    /** Main method to run the application. 
     * @param args Array of input parameters.
     */
    public static void main(String[] args) {
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

        launch(AdminApplication.class, args);
    }
}
