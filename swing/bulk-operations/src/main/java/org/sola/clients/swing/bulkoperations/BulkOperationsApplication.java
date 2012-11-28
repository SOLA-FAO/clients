/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.bulkoperations.spatialobjects.ImportSpatialPanel;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.LocalizationManager;
import org.sola.clients.swing.ui.DesktopClientExceptionHandler;
import org.sola.clients.swing.ui.security.LoginForm;
import org.sola.clients.swing.ui.security.LoginPanel;
import org.sola.common.RolesConstants;
import org.sola.common.logging.LogUtility;

/**
 *
 * @author Elton Manoku
 */
public class BulkOperationsApplication {

    private static void displayControlsBundleForm(Component ctrl) {
        JDialog controlContainer = new JDialog();
        //controlContainer.setAlwaysOnTop(true);
        controlContainer.setModal(true);
        ctrl.setPreferredSize(new Dimension(800, 600));
        controlContainer.getContentPane().add(ctrl);
        controlContainer.pack();
        controlContainer.setVisible(true);
    }
    
    public static void main(String[] args) {
        System.out.println("Test import panel");

       SecurityBean.authenticate("test", "test".toCharArray(), getWSConfig());

        ImportSpatialPanel ctrl = new ImportSpatialPanel();
        displayControlsBundleForm(ctrl);
    
    }
    
    private static HashMap<String, String> getWSConfig() {
        HashMap<String, String> wsConfig = new HashMap<String, String>();
        wsConfig.put("SOLA_WS_CASE_MANAGEMENT_SERVICE_URL", "http://localhost:8080/sola/webservices/casemanagement-service?wsdl");
        wsConfig.put("SOLA_WS_REFERENCE_DATA_SERVICE_URL", "http://localhost:8080/sola/webservices/referencedata-service?wsdl");
        wsConfig.put("SOLA_WS_ADMIN_SERVICE_URL", "http://localhost:8080/sola/webservices/admin-service?wsdl");
        wsConfig.put("SOLA_WS_CADASTRE_SERVICE_URL", "http://localhost:8080/sola/webservices/cadastre-service?wsdl");
        wsConfig.put("SOLA_WS_SEARCH_SERVICE_URL", "http://localhost:8080/sola/webservices/search-service?wsdl");
        wsConfig.put("SOLA_WS_DIGITAL_ARCHIVE_URL", "http://localhost:8080/sola/webservices/digitalarchive-service?wsdl");
        wsConfig.put("SOLA_WS_SPATIAL_SERVICE_URL", "http://localhost:8080/sola/webservices/spatial-service?wsdl");
        wsConfig.put("SOLA_WS_ADMINISTRATIVE_SERVICE_URL", "http://localhost:8080/sola/webservices/administrative-service?wsdl");
        return wsConfig;
    }
    
    /** Main method to run the application. 
     * @param args Array of input parameters.
     */
    public static void mainM(String[] args) {
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
                LocalizationManager.loadLanguage(BulkOperationsApplication.class);
                LogUtility.initialize(BulkOperationsApplication.class);
                LafManager.getInstance().setProperties("green");

                final LoginForm loginForm = new LoginForm(BulkOperationsApplication.class);
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
