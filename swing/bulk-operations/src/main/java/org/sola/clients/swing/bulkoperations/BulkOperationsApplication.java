/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.LocalizationManager;
import org.sola.clients.swing.ui.DesktopClientExceptionHandler;
import org.sola.clients.swing.ui.security.LoginForm;
import org.sola.clients.swing.ui.security.LoginPanel;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.logging.LogUtility;

/**
 * This is the singletone starting class of the bulk operations application.
 *
 * @author Elton Manoku
 */
public class BulkOperationsApplication {

    /**
     * Main method to run the application.
     *
     * @param args Array of input parameters.
     */
    public static void main(String[] args) {
        // #321 Set class to record user preferences for
        WindowUtility.setMainAppClass(BulkOperationsApplication.class);
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
                LogUtility.initialize(BulkOperationsApplication.class);

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
                                // Check user to have external access roles
                                if (!SecurityBean.isInRole(RolesConstants.BULK_APPLICATION)) {
                                    JOptionPane.showMessageDialog(loginForm,
                                            "You don't have rights to use this application.");
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
