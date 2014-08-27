/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.source.PowerOfAttorneyBean;
import org.sola.clients.beans.system.LanguageBean;
import org.sola.clients.beans.system.LanguageListBean;
import org.sola.clients.swing.common.DefaultExceptionHandler;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.ui.localization.LocalizationManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.common.utils.LocalizationTools;
import org.sola.clients.swing.desktop.administrative.BaUnitSearchPanel;
import org.sola.clients.swing.desktop.administrative.RightsExportForm;
import org.sola.clients.swing.desktop.application.ApplicationSearchPanel;
import org.sola.clients.swing.desktop.application.SLJobPanel;
import org.sola.clients.swing.desktop.cadastre.MapPanelForm;
import org.sola.clients.swing.desktop.cadastre.MapPublicDisplayPanel;
import org.sola.clients.swing.desktop.cadastre.MapSpatialUnitEditPanel;
import org.sola.clients.swing.desktop.cadastre.MapSpatialUnitGroupEditPanel;
import org.sola.clients.swing.desktop.party.PartySearchPanelForm;
import org.sola.clients.swing.desktop.reports.LodgementReportParamsForm;
import org.sola.clients.swing.desktop.reports.SysRegCertParamsForm;
import org.sola.clients.swing.desktop.reports.SysRegListingParamsForm;
import org.sola.clients.swing.desktop.reports.SysRegManagementParamsForm;
import org.sola.clients.swing.desktop.source.DocumentSearchForm;
import org.sola.clients.swing.desktop.source.PowerOfAttorneyViewForm;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.help.HelpUtility;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Main form of the application.
 */
public class MainForm extends javax.swing.JFrame {

    public static final String MAIN_FORM_HEIGHT = "mainFormHeight";
    public static final String MAIN_FORM_WIDTH = "mainFormWitdh";
    public static final String MAIN_FORM_TOP = "mainFormTop";
    public static final String MAIN_FORM_LEFT = "mainFormLeft";
    private ApplicationSearchPanel searchApplicationPanel;
    private DocumentSearchForm searchDocPanel;
    private PartySearchPanelForm searchPartyPanel;
    private BaUnitSearchPanel searchBaUnitPanel;
    private static MainForm INSTANCE;
    // Create a variable holding the listener
    KeyAdapter keyAdapterAppSearch = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                launchAppSearchMethod(searchApplicationPanel);
            }
        }
    };
    // Create a variable holding the listener
    KeyAdapter keyAdapterDocSearch = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                launchDocSearchMethod(searchDocPanel);
            }
        }
    };
    // Create a variable holding the listener
    KeyAdapter keyAdapterBaUnitSearch = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                launchBaUnitSearchMethod(searchBaUnitPanel);
            }
        }
    };
    // Create a variable holding the listener
    KeyAdapter keyAdapterPartySearch = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                launchPartySearchMethod(searchPartyPanel);
            }
        }
    };

    public ApplicationSearchPanel getSearchApplicationPanel() {
        return searchApplicationPanel;
    }

    public void setSearchApplicationPanel(ApplicationSearchPanel searchApplicationPanel) {
        this.searchApplicationPanel = searchApplicationPanel;
    }

    public BaUnitSearchPanel getSearchBaUnitPanel() {
        return searchBaUnitPanel;
    }

    public void setSearchBaUnitPanel(BaUnitSearchPanel searchBaUnitPanel) {
        this.searchBaUnitPanel = searchBaUnitPanel;
    }

    public DocumentSearchForm getSearchDocPanel() {
        return searchDocPanel;
    }

    public void setSearchDocPanel(DocumentSearchForm searchDocPanel) {
        this.searchDocPanel = searchDocPanel;
    }

    public PartySearchPanelForm getSearchPartyPanel() {
        return searchPartyPanel;
    }

    public void setSearchPartyPanel(PartySearchPanelForm searchPartyPanel) {
        this.searchPartyPanel = searchPartyPanel;
    }

    /**
     * Returns a singleton instance of {@link MainForm}.
     */
    public static MainForm getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainForm();
        }
        return INSTANCE;
    }

    /**
     * Default constructor.
     */
    private MainForm() {
        URL imgURL = this.getClass().getResource("/images/sola/logo_icon.jpg");
        this.setIconImage(new ImageIcon(imgURL).getImage());

        initComponents();
        LocalizationTools.setOrientation(this);
        this.setTitle("SOLA Desktop - " + LocalizationManager.getVersionNumber());
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                postInit();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                preClose();
            }
        });
    }

    /**
     * Runs post initialization tasks. Enables or disables toolbar buttons and
     * menu items depending on user rights. Loads various data after the form
     * has been opened. It helps to display form with no significant delays.
     */
    private void postInit() {
        // Set screen size and location 
        configureForm();
        loadLanguages();

        // Customize buttons
        btnNewApplication.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_CREATE_APPS));
        btnOpenMap.setEnabled(SecurityBean.isInRole(RolesConstants.GIS_VIEW_MAP));
        btnSearchApplications.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_VIEW_APPS));
        btnShowDashboard.setEnabled(SecurityBean.isInRole(RolesConstants.DASHBOARD_VIEW_ASSIGNED_APPS,
                RolesConstants.DASHBOARD_VIEW_UNASSIGNED_APPS));
        btnManageParties.setEnabled(SecurityBean.isInRole(RolesConstants.PARTY_SEARCH));
        btnOpenBaUnitSearch.setEnabled(SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SEARCH));
        btnDocumentSearch.setEnabled(SecurityBean.isInRole(RolesConstants.SOURCE_SEARCH));
        btnSetPassword.setEnabled(SecurityBean.isInRole(RolesConstants.ADMIN_CHANGE_PASSWORD));

        menuSearchApplication.setEnabled(btnSearchApplications.isEnabled());
        menuNewApplication.setEnabled(btnNewApplication.isEnabled());
        menuBaUnitSearch.setEnabled(btnOpenBaUnitSearch.isEnabled());
        menuPersons.setEnabled(btnManageParties.isEnabled());
        menuShowMap.setEnabled(btnOpenMap.isEnabled());
        menuLodgementReport.setEnabled(SecurityBean.isInRole(RolesConstants.REPORTS_VIEW));
        menuDocumentSearch.setEnabled(btnDocumentSearch.isEnabled());
        menuFlushCache.setVisible(SecurityBean.isInRole(RolesConstants.ADMIN_MANAGE_REFDATA));

        if (SecurityBean.isPasswordChangeReqd(false)) {
            // Load the user profile page
            showPasswordPanel();
        } else {
            if (btnShowDashboard.isEnabled()) {
                // Load dashboard
                openDashBoard(true);
            }
        }

        txtUserName.setText(SecurityBean.getCurrentUser().getUserName());
    }

    /**
     * Sets the screen size and location based on the settings stored in the
     * users preferences.
     */
    private void configureForm() {

        int height = this.getHeight();
        int width = this.getWidth();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = ((dim.width) / 2) - (width / 2);
        int y = ((dim.height) / 2) - (height / 2);

        if (WindowUtility.hasUserPreferences()) {
            // Set the size of the screen
            Preferences prefs = WindowUtility.getUserPreferences();
            height = Integer.parseInt(prefs.get(MAIN_FORM_HEIGHT, Integer.toString(height)));
            width = Integer.parseInt(prefs.get(MAIN_FORM_WIDTH, Integer.toString(width)));
            y = Integer.parseInt(prefs.get(MAIN_FORM_TOP, Integer.toString(y)));
            x = Integer.parseInt(prefs.get(MAIN_FORM_LEFT, Integer.toString(x)));

            // Check if the screen sizes are within the bounds of the users 
            // physical screen. e.g. may have been using dual monitor. 
            if (height > dim.height || height < 50) {
                height = dim.height - 50 - y;
            }
            if (width > dim.width || width < 200) {
                width = dim.width - 20 - x;
            }
            if (y + 10 > dim.height || y + height - 100 < 0) {
                y = 5;
            }
            if (x + 10 > dim.width || x + width - 100 < 0) {
                x = 10;
            }
            this.setSize(width, height);
        }
        this.setLocation(x, y);
    }

    /**
     * Captures the screen size and location and saves them as the users
     * preference just before the screen is is closed.
     */
    private void preClose() {
        if (WindowUtility.hasUserPreferences()) {
            Preferences prefs = WindowUtility.getUserPreferences();
            prefs.put(MAIN_FORM_HEIGHT, Integer.toString(this.getHeight()));
            prefs.put(MAIN_FORM_WIDTH, Integer.toString(this.getWidth()));
            prefs.put(MAIN_FORM_TOP, Integer.toString(this.getY()));
            prefs.put(MAIN_FORM_LEFT, Integer.toString(this.getX()));
        }
    }

    private void setAllLogLevel() {
        LogUtility.setLogLevel(Level.ALL);
    }

    private void setDefaultLogLevel() {
        LogUtility.setLogLevel(Level.INFO);
    }

    private void setOffLogLevel() {
        LogUtility.setLogLevel(Level.OFF);
    }

    private void openMap() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_MAP));
                if (!pnlContent.isPanelOpened(MainContentPanel.CARD_MAP)) {
                    MapPanelForm mapPanel = new MapPanelForm();
                    pnlContent.addPanel(mapPanel, MainContentPanel.CARD_MAP);
                }
                pnlContent.showPanel(MainContentPanel.CARD_MAP);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void openMapPublicDisplay() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_MAP));
                if (!pnlContent.isPanelOpened(MapPublicDisplayPanel.PANEL_NAME)) {
                    MapPublicDisplayPanel mapPanel = new MapPublicDisplayPanel();
                    pnlContent.addPanel(mapPanel, MapPublicDisplayPanel.PANEL_NAME);
                }
                pnlContent.showPanel(MapPublicDisplayPanel.PANEL_NAME);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void openMapSpatialUnitGroupEditor() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_MAP));
                if (!pnlContent.isPanelOpened(MapSpatialUnitGroupEditPanel.PANEL_NAME)) {
                    MapSpatialUnitGroupEditPanel mapPanel = new MapSpatialUnitGroupEditPanel();
                    pnlContent.addPanel(mapPanel, MapSpatialUnitGroupEditPanel.PANEL_NAME);
                }
                pnlContent.showPanel(MapSpatialUnitGroupEditPanel.PANEL_NAME);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void openMapSpatialUnitEditor() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_MAP));
                if (!pnlContent.isPanelOpened(MapSpatialUnitEditPanel.PANEL_NAME)) {
                    MapSpatialUnitEditPanel mapPanel = new MapSpatialUnitEditPanel();
                    pnlContent.addPanel(mapPanel, MapSpatialUnitEditPanel.PANEL_NAME);
                }
                pnlContent.showPanel(MapSpatialUnitEditPanel.PANEL_NAME);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void searchApplications() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_APPSEARCH));
                if (!pnlContent.isPanelOpened(MainContentPanel.CARD_APPSEARCH)) {
                    ApplicationSearchPanel searchApplicationPanel = new ApplicationSearchPanel();
                    setSearchApplicationPanel(searchApplicationPanel);
                    pnlContent.addPanel(searchApplicationPanel, MainContentPanel.CARD_APPSEARCH);

                }

                pnlContent.showPanel(MainContentPanel.CARD_APPSEARCH);
                return null;
            }

            @Override
            protected void taskDone() {
                addKeyListeners(MainContentPanel.CARD_APPSEARCH);
            }
        };
        TaskManager.getInstance().runTask(t);

    }

    private void searchBaUnit() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTYSEARCH));
                if (!pnlContent.isPanelOpened(MainContentPanel.CARD_BAUNIT_SEARCH)) {
                    BaUnitSearchPanel baUnitSearchPanel = new BaUnitSearchPanel();
                    setSearchBaUnitPanel(baUnitSearchPanel);
                    pnlContent.addPanel(baUnitSearchPanel, MainContentPanel.CARD_BAUNIT_SEARCH);
                }
                pnlContent.showPanel(MainContentPanel.CARD_BAUNIT_SEARCH);
                return null;
            }

            @Override
            protected void taskDone() {
                addKeyListeners(MainContentPanel.CARD_BAUNIT_SEARCH);
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void searchDocuments() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_DOCUMENTSEARCH));
                if (!pnlContent.isPanelOpened(MainContentPanel.CARD_DOCUMENT_SEARCH)) {
                    DocumentSearchForm documentSearchPanel = new DocumentSearchForm();
                    setSearchDocPanel(documentSearchPanel);
                    pnlContent.addPanel(documentSearchPanel, MainContentPanel.CARD_DOCUMENT_SEARCH);
                }
                pnlContent.showPanel(MainContentPanel.CARD_DOCUMENT_SEARCH);
                return null;
            }

            @Override
            protected void taskDone() {
                addKeyListeners(MainContentPanel.CARD_DOCUMENT_SEARCH);
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void openSearchParties() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSONSEARCH));
                if (!pnlContent.isPanelOpened(MainContentPanel.CARD_SEARCH_PERSONS)) {
                    PartySearchPanelForm partySearchPanelForm = new PartySearchPanelForm();
                    pnlContent.addPanel(partySearchPanelForm, MainContentPanel.CARD_SEARCH_PERSONS, true);
                    setSearchPartyPanel(partySearchPanelForm);
                } else {
                    pnlContent.showPanel(MainContentPanel.CARD_SEARCH_PERSONS);
                }
                return null;
            }

            @Override
            protected void taskDone() {
                addKeyListeners(MainContentPanel.CARD_SEARCH_PERSONS);
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void addKeyListeners(final String card) {

        if (card.contentEquals(MainContentPanel.CARD_APPSEARCH)) {
            pnlContent.addKeyListener(keyAdapterAppSearch);
            pnlContent.removeKeyListener(keyAdapterDocSearch);
            pnlContent.removeKeyListener(keyAdapterBaUnitSearch);
            pnlContent.removeKeyListener(keyAdapterPartySearch);
        }
        if (card.contentEquals(MainContentPanel.CARD_DOCUMENT_SEARCH)) {
            pnlContent.addKeyListener(keyAdapterDocSearch);
            pnlContent.removeKeyListener(keyAdapterAppSearch);
            pnlContent.removeKeyListener(keyAdapterBaUnitSearch);
            pnlContent.removeKeyListener(keyAdapterPartySearch);
        }
        if (card.contentEquals(MainContentPanel.CARD_BAUNIT_SEARCH)) {
            pnlContent.addKeyListener(keyAdapterBaUnitSearch);
            pnlContent.removeKeyListener(keyAdapterDocSearch);
            pnlContent.removeKeyListener(keyAdapterAppSearch);
            pnlContent.removeKeyListener(keyAdapterPartySearch);

        }
        if (card.contentEquals(MainContentPanel.CARD_SEARCH_PERSONS)) {
            pnlContent.addKeyListener(keyAdapterPartySearch);
            pnlContent.removeKeyListener(keyAdapterDocSearch);
            pnlContent.removeKeyListener(keyAdapterBaUnitSearch);
            pnlContent.removeKeyListener(keyAdapterAppSearch);

        }
        pnlContent.setFocusable(true);
        pnlContent.requestFocusInWindow();

    }

    public void launchAppSearchMethod(final ApplicationSearchPanel panel) {
        panel.clickFind();
    }

    public void launchDocSearchMethod(DocumentSearchForm panel) {
        panel.clickFind();
    }

    public void launchBaUnitSearchMethod(BaUnitSearchPanel panel) {
        panel.clickFind();
    }

    public void launchPartySearchMethod(PartySearchPanelForm panel) {
        panel.clickFind();
    }

    /**
     * Opens dashboard panel.
     *
     * @param forceRefresh Indicates whether to fetch applications upon opening.
     */
    public void openDashBoard(boolean forceRefresh) {
        if (!pnlContent.isPanelOpened(MainContentPanel.CARD_DASHBOARD)) {
            SLDashBoardPanel dashBoard = new SLDashBoardPanel(forceRefresh);
            pnlContent.addPanel(dashBoard, MainContentPanel.CARD_DASHBOARD);
        }
        pnlContent.showPanel(MainContentPanel.CARD_DASHBOARD);
    }

    public MainContentPanel getMainContentPanel() {
        return pnlContent;
    }

    private void showAboutBox() {
        AboutForm aboutBox = new AboutForm(this);
        aboutBox.setLocationRelativeTo(this);
        aboutBox.setVisible(true);
    }

    private void setLanguage(String code, String country) {
        LocalizationManager.setLanguage(code, country);
        MessageUtility.displayMessage(ClientMessage.GENERAL_UPDATE_LANG);
    }

    /**
     * Calls {@link AbstractBindingBean#saveStateHash()} method to make a hash
     * of object's state
     */
    public static void saveBeanState(AbstractBindingBean bean) {
        try {
            bean.saveStateHash();
        } catch (IOException ex) {
            DefaultExceptionHandler.handleException(ex);
        } catch (NoSuchAlgorithmException ex) {
            DefaultExceptionHandler.handleException(ex);
        }
    }

    /**
     * Calls {@link AbstractBindingBean#hasChanges()} method to detect if there
     * are any changes on the provided bean. <br /> Note, to check for the
     * changes, you should call {@link AbstractBindingBean#saveStateHash()}
     * before calling this method.
     */
    public static boolean checkBeanState(AbstractBindingBean bean) {
        try {
            return bean.hasChanges();
        } catch (IOException ex) {
            DefaultExceptionHandler.handleException(ex);
            return true;
        } catch (NoSuchAlgorithmException ex) {
            DefaultExceptionHandler.handleException(ex);
            return true;
        }
    }

    /**
     * Calls
     * {@link MainForm#checkBeanState(org.sola.clients.beans.AbstractBindingBean)}
     * method to detect if there are any changes on the provided bean. If it
     * returns true, warning message is shown and the result of user selection
     * is returned. If user clicks <b>Yes</b> button to confirm saving changes,
     * true is returned.
     */
    public static boolean checkSaveBeforeClose(AbstractBindingBean bean) {
        boolean hasChanges = false;
        if (checkBeanState(bean)) {
            if (MessageUtility.displayMessage(ClientMessage.GENERAL_FORM_CHANGES_WARNING) == MessageUtility.BUTTON_ONE) {
                hasChanges = true;
            } else {
                hasChanges = false;
            }
        }
        return hasChanges;
    }

    /**
     * Opens Application form and shows provided application.
     *
     * @param app Application to show on the form.
     */
    public void openApplicationForm(final ApplicationBean app) {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_APP));
                SLJobPanel form = new SLJobPanel(app);
                getMainContentPanel().addPanel(form, MainContentPanel.CARD_APPLICATION, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Opens Application form and shows provided application.
     *
     * @param ID Application ID to load application by and show on the form.
     */
    public void openApplicationForm(final String id) {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_APP));
                SLJobPanel form = new SLJobPanel(id);
                getMainContentPanel().addPanel(form, MainContentPanel.CARD_APPLICATION, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Opens Application form to create new application.
     */
    public void openApplicationForm() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_APPNEW));
                SLJobPanel applicationPanel = new SLJobPanel();
                getMainContentPanel().addPanel(applicationPanel, MainContentPanel.CARD_APPLICATION, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void showRightsExportPanel() {
        if (getMainContentPanel().isPanelOpened(MainContentPanel.CARD_RIGHT_EXPORT)) {
            getMainContentPanel().showPanel(MainContentPanel.CARD_RIGHT_EXPORT);
        } else {
            RightsExportForm form = new RightsExportForm();
            getMainContentPanel().addPanel(form, MainContentPanel.CARD_RIGHT_EXPORT, true);
        }
    }

    /**
     * Opens {@link PowerOfAttorneyViewForm} form and shows provided document.
     *
     * @param powerOfAttorney Power of attorney to show on the form.
     */
    public void openDocumentViewForm(final PowerOfAttorneyBean powerOfAttorney) {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_FORM_OPENING));
                PowerOfAttorneyViewForm form = new PowerOfAttorneyViewForm(powerOfAttorney);
                getMainContentPanel().addPanel(form, MainContentPanel.CARD_VIEW_POWER_OF_ATTORNEY, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Reloads main form
     */
    private void reloadMainForm() {
        INSTANCE = new MainForm();
        this.dispose();

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                MainForm.getInstance().setVisible(true);
            }
        });
    }

    private void openLodgementReportParamsForm() {
        LodgementReportParamsForm reportDateChooser = new LodgementReportParamsForm(this, true);
        reportDateChooser.setVisible(true);
    }

    private void openSysRegListingParamsForm(String report) {
        SysRegListingParamsForm reportDateChooser = new SysRegListingParamsForm(this, true, report);
        reportDateChooser.setVisible(true);
    }

    private void openSysRegCertificatesParamsForm() {
        SysRegCertParamsForm certificateGenerator = new SysRegCertParamsForm(null, true);
        certificateGenerator.setVisible(true);
    }

    private void openSysRegManagementParamsForm(String whichReport) {
        SysRegManagementParamsForm managementGenerator = new SysRegManagementParamsForm(this, true, whichReport);
        managementGenerator.setVisible(true);
    }

    private void editPassword() {
        showPasswordPanel();
    }

    /**
     * Shows password panel.
     */
    private void showPasswordPanel() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
//                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_MAP));
                if (!pnlContent.isPanelOpened(MainContentPanel.CARD_USER_PROFILE)) {
                    UserProfileForm panel = new UserProfileForm(SecurityBean.getCurrentUser().getUserName());
                    pnlContent.addPanel(panel, MainContentPanel.CARD_USER_PROFILE);
                }
                pnlContent.showPanel(MainContentPanel.CARD_USER_PROFILE);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void loadLanguages() {
        LanguageListBean langs = new LanguageListBean(false, false);
        menuLanguage.removeAll();

        for (LanguageBean lang : langs.getLanguages()) {
            final String langCode = lang.getLanguageCode();
            final String localeCode = lang.getCode();

            JCheckBoxMenuItem menuLang = new JCheckBoxMenuItem();
            menuLang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/flags/" + langCode + ".png")));
            menuLang.setText(lang.getDisplayValue());
            if (LocalizationManager.getLanguage().equalsIgnoreCase(langCode)) {
                menuLang.setSelected(true);
            }
            //menuLang.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm.png")));
            menuLang.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ((JCheckBoxMenuItem) evt.getSource()).setSelected(true);
                    selectLanguage(localeCode);
                }
            });
            menuLanguage.add(menuLang);
        }
    }

    private void selectLanguage(String locale) {
        if (LocalizationManager.getLocaleCode().equals(locale)) {
            return;
        }

        if (MessageUtility.displayMessage(ClientMessage.CONFIRM_CHANGE_LANGUAGE) == MessageUtility.BUTTON_ONE) {
            LocalizationManager.setLanguage(LocalizationManager.getLangCode(locale),
                    LocalizationManager.getCountryCode(locale));
            reloadMainForm();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        applicationsMain = new javax.swing.JToolBar();
        btnShowDashboard = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnNewApplication = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnSearchApplications = new javax.swing.JButton();
        btnOpenBaUnitSearch = new javax.swing.JButton();
        btnDocumentSearch = new javax.swing.JButton();
        btnManageParties = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnOpenMap = new javax.swing.JButton();
        btnSetPassword = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        labStatus = new javax.swing.JLabel();
        taskPanel1 = new org.sola.clients.swing.common.tasks.TaskPanel();
        txtUserName = new javax.swing.JLabel();
        pnlContent = new org.sola.clients.swing.ui.MainContentPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem menuExitItem = new javax.swing.JMenuItem();
        menuFlushCache = new javax.swing.JMenuItem();
        menuSpatialEditMap = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuLanguage = new javax.swing.JMenu();
        menuLogLevel = new javax.swing.JMenu();
        menuAllLogLevel = new javax.swing.JMenuItem();
        menuDefaultLogLevel = new javax.swing.JMenuItem();
        menuOffLogLevel = new javax.swing.JMenuItem();
        menuApplications = new javax.swing.JMenu();
        menuNewApplication = new javax.swing.JMenuItem();
        menuSearch = new javax.swing.JMenu();
        menuSearchApplication = new javax.swing.JMenuItem();
        menuBaUnitSearch = new javax.swing.JMenuItem();
        menuDocumentSearch = new javax.swing.JMenuItem();
        menuPersons = new javax.swing.JMenuItem();
        menuMap = new javax.swing.JMenu();
        menuShowMap = new javax.swing.JMenuItem();
        menuReportsDesktop = new javax.swing.JMenu();
        menuLodgementReport = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jmiContextHelp = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        setTitle(bundle.getString("MainForm.title")); // NOI18N

        applicationsMain.setFloatable(false);
        applicationsMain.setRollover(true);
        applicationsMain.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        applicationsMain.setMaximumSize(new java.awt.Dimension(32769, 32769));
        applicationsMain.setMinimumSize(new java.awt.Dimension(90, 45));
        applicationsMain.setPreferredSize(new java.awt.Dimension(980, 45));

        btnShowDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/home.png"))); // NOI18N
        btnShowDashboard.setText(bundle.getString("MainForm.btnShowDashboard.text")); // NOI18N
        btnShowDashboard.setFocusable(false);
        btnShowDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnShowDashboard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnShowDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowDashboardActionPerformed(evt);
            }
        });
        applicationsMain.add(btnShowDashboard);
        applicationsMain.add(jSeparator2);

        btnNewApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/new.png"))); // NOI18N
        btnNewApplication.setText(bundle.getString("MainForm.btnNewApplication.text")); // NOI18N
        btnNewApplication.setFocusable(false);
        btnNewApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNewApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewApplicationActionPerformed(evt);
            }
        });
        applicationsMain.add(btnNewApplication);
        applicationsMain.add(jSeparator4);

        btnSearchApplications.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        btnSearchApplications.setText(bundle.getString("MainForm.btnSearchApplications.text")); // NOI18N
        btnSearchApplications.setFocusable(false);
        btnSearchApplications.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSearchApplications.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSearchApplications.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchApplicationsActionPerformed(evt);
            }
        });
        applicationsMain.add(btnSearchApplications);

        btnOpenBaUnitSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        btnOpenBaUnitSearch.setText(bundle.getString("MainForm.btnOpenBaUnitSearch.text")); // NOI18N
        btnOpenBaUnitSearch.setFocusable(false);
        btnOpenBaUnitSearch.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenBaUnitSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenBaUnitSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenBaUnitSearchActionPerformed(evt);
            }
        });
        applicationsMain.add(btnOpenBaUnitSearch);

        btnDocumentSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        btnDocumentSearch.setText(bundle.getString("MainForm.btnDocumentSearch.text")); // NOI18N
        btnDocumentSearch.setFocusable(false);
        btnDocumentSearch.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDocumentSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDocumentSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDocumentSearchActionPerformed(evt);
            }
        });
        applicationsMain.add(btnDocumentSearch);

        btnManageParties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        btnManageParties.setText(bundle.getString("MainForm.btnManageParties.text")); // NOI18N
        btnManageParties.setFocusable(false);
        btnManageParties.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnManageParties.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnManageParties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManagePartiesActionPerformed(evt);
            }
        });
        applicationsMain.add(btnManageParties);
        applicationsMain.add(jSeparator1);

        btnOpenMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/network.png"))); // NOI18N
        btnOpenMap.setText(bundle.getString("MainForm.btnOpenMap.text")); // NOI18N
        btnOpenMap.setFocusable(false);
        btnOpenMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenMap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenMapActionPerformed(evt);
            }
        });
        applicationsMain.add(btnOpenMap);

        btnSetPassword.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lock--pencil.png"))); // NOI18N
        btnSetPassword.setText(bundle.getString("MainForm.btnSetPassword.text")); // NOI18N
        btnSetPassword.setFocusable(false);
        btnSetPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSetPassword.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSetPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSetPasswordActionPerformed(evt);
            }
        });
        applicationsMain.add(btnSetPassword);

        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusPanel.setPreferredSize(new java.awt.Dimension(1024, 24));

        labStatus.setFont(LafManager.getInstance().getLabFontBold());
        labStatus.setText(bundle.getString("MainForm.labStatus.text")); // NOI18N

        txtUserName.setText(bundle.getString("MainForm.txtUserName.text")); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taskPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
            .addComponent(txtUserName, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
            .addComponent(taskPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
        );

        menuBar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        fileMenu.setText(bundle.getString("MainForm.fileMenu.text")); // NOI18N

        menuExitItem.setText(bundle.getString("MainForm.menuExitItem.text")); // NOI18N
        menuExitItem.setToolTipText(bundle.getString("MainForm.menuExitItem.toolTipText")); // NOI18N
        menuExitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitItemActionPerformed(evt);
            }
        });
        fileMenu.add(menuExitItem);

        menuFlushCache.setText(bundle.getString("MainForm.menuFlushCache.text")); // NOI18N
        menuFlushCache.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFlushCacheActionPerformed(evt);
            }
        });
        fileMenu.add(menuFlushCache);

        menuSpatialEditMap.setText(bundle.getString("MainForm.menuSpatialEditMap.text")); // NOI18N
        menuSpatialEditMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSpatialEditMapActionPerformed(evt);
            }
        });
        fileMenu.add(menuSpatialEditMap);

        menuBar.add(fileMenu);

        menuView.setText(bundle.getString("MainForm.menuView.text")); // NOI18N

        menuLanguage.setText(bundle.getString("MainForm.menuLanguage.text")); // NOI18N
        menuView.add(menuLanguage);

        menuLogLevel.setText(bundle.getString("MainForm.menuLogLevel.text")); // NOI18N

        menuAllLogLevel.setText(bundle.getString("MainForm.menuAllLogLevel.text")); // NOI18N
        menuAllLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAllLogLevelActionPerformed(evt);
            }
        });
        menuLogLevel.add(menuAllLogLevel);

        menuDefaultLogLevel.setText(bundle.getString("MainForm.menuDefaultLogLevel.text")); // NOI18N
        menuDefaultLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDefaultLogLevelActionPerformed(evt);
            }
        });
        menuLogLevel.add(menuDefaultLogLevel);

        menuOffLogLevel.setText(bundle.getString("MainForm.menuOffLogLevel.text")); // NOI18N
        menuOffLogLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOffLogLevelActionPerformed(evt);
            }
        });
        menuLogLevel.add(menuOffLogLevel);

        menuView.add(menuLogLevel);

        menuBar.add(menuView);

        menuApplications.setText(bundle.getString("MainForm.menuApplications.text")); // NOI18N

        menuNewApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/new.png"))); // NOI18N
        menuNewApplication.setText(bundle.getString("MainForm.menuNewApplication.text")); // NOI18N
        menuNewApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewApplicationActionPerformed(evt);
            }
        });
        menuApplications.add(menuNewApplication);

        menuBar.add(menuApplications);

        menuSearch.setText(bundle.getString("MainForm.menuSearch.text")); // NOI18N

        menuSearchApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        menuSearchApplication.setText(bundle.getString("MainForm.menuSearchApplication.text")); // NOI18N
        menuSearchApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSearchApplicationActionPerformed(evt);
            }
        });
        menuSearch.add(menuSearchApplication);

        menuBaUnitSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        menuBaUnitSearch.setText(bundle.getString("MainForm.menuBaUnitSearch.text")); // NOI18N
        menuBaUnitSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBaUnitSearchActionPerformed(evt);
            }
        });
        menuSearch.add(menuBaUnitSearch);

        menuDocumentSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        menuDocumentSearch.setText(bundle.getString("MainForm.menuDocumentSearch.text")); // NOI18N
        menuDocumentSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDocumentSearchActionPerformed(evt);
            }
        });
        menuSearch.add(menuDocumentSearch);

        menuPersons.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        menuPersons.setText(bundle.getString("MainForm.menuPersons.text")); // NOI18N
        menuPersons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPersonsActionPerformed(evt);
            }
        });
        menuSearch.add(menuPersons);

        menuBar.add(menuSearch);

        menuMap.setText(bundle.getString("MainForm.menuMap.text")); // NOI18N

        menuShowMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/network.png"))); // NOI18N
        menuShowMap.setText(bundle.getString("MainForm.menuShowMap.text")); // NOI18N
        menuShowMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuShowMapActionPerformed(evt);
            }
        });
        menuMap.add(menuShowMap);

        menuBar.add(menuMap);

        menuReportsDesktop.setText(bundle.getString("MainForm.menuReportsDesktop.text_1")); // NOI18N

        menuLodgementReport.setText(bundle.getString("MainForm.menuLodgementReportDesktop.text_1")); // NOI18N
        menuLodgementReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLodgementReportActionPerformed(evt);
            }
        });
        menuReportsDesktop.add(menuLodgementReport);
        menuLodgementReport.getAccessibleContext().setAccessibleName(bundle.getString("MainForm.menuLodgementReport.AccessibleContext.accessibleName")); // NOI18N

        menuBar.add(menuReportsDesktop);

        helpMenu.setText(bundle.getString("MainForm.helpMenu.text")); // NOI18N

        aboutMenuItem.setText(bundle.getString("MainForm.aboutMenuItem.text")); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jmiContextHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/help.png"))); // NOI18N
        jmiContextHelp.setText(bundle.getString("MainForm.jmiContextHelp.text")); // NOI18N
        jmiContextHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiContextHelpActionPerformed(evt);
            }
        });
        helpMenu.add(jmiContextHelp);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 990, Short.MAX_VALUE)
            .addComponent(applicationsMain, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 990, Short.MAX_VALUE)
            .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(applicationsMain, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuShowMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuShowMapActionPerformed
        openMap();
    }//GEN-LAST:event_menuShowMapActionPerformed

    private void menuExitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuExitItemActionPerformed

    private void menuAllLogLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAllLogLevelActionPerformed
        setAllLogLevel();
    }//GEN-LAST:event_menuAllLogLevelActionPerformed

    private void menuDefaultLogLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDefaultLogLevelActionPerformed
        setDefaultLogLevel();
    }//GEN-LAST:event_menuDefaultLogLevelActionPerformed

    private void menuOffLogLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOffLogLevelActionPerformed
        setOffLogLevel();
    }//GEN-LAST:event_menuOffLogLevelActionPerformed

    private void menuNewApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewApplicationActionPerformed
        openApplicationForm();
    }//GEN-LAST:event_menuNewApplicationActionPerformed

    private void menuSearchApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSearchApplicationActionPerformed
        searchApplications();
    }//GEN-LAST:event_menuSearchApplicationActionPerformed

    private void menuBaUnitSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBaUnitSearchActionPerformed
        searchBaUnit();
    }//GEN-LAST:event_menuBaUnitSearchActionPerformed

    private void menuDocumentSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDocumentSearchActionPerformed
        searchDocuments();
    }//GEN-LAST:event_menuDocumentSearchActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        showAboutBox();
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void btnShowDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowDashboardActionPerformed
        openDashBoard(true);
    }//GEN-LAST:event_btnShowDashboardActionPerformed

    private void btnNewApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewApplicationActionPerformed
        openApplicationForm();
    }//GEN-LAST:event_btnNewApplicationActionPerformed

    private void btnSearchApplicationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchApplicationsActionPerformed
        searchApplications();
    }//GEN-LAST:event_btnSearchApplicationsActionPerformed

    private void btnDocumentSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDocumentSearchActionPerformed
        searchDocuments();
    }//GEN-LAST:event_btnDocumentSearchActionPerformed

    private void btnManagePartiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManagePartiesActionPerformed
        openSearchParties();
    }//GEN-LAST:event_btnManagePartiesActionPerformed

    private void openSysRegGenderReport(java.awt.event.ActionEvent evt) {
        SysRegManagementParamsForm managementGenerator = new SysRegManagementParamsForm(this, true, "sysRegGenderBean");
        managementGenerator.clickView(evt);
    }

    private void btnOpenMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenMapActionPerformed
        openMap();
    }//GEN-LAST:event_btnOpenMapActionPerformed

    private void btnOpenBaUnitSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenBaUnitSearchActionPerformed
        searchBaUnit();
    }//GEN-LAST:event_btnOpenBaUnitSearchActionPerformed

    private void menuLodgementReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLodgementReportActionPerformed
        openLodgementReportParamsForm();
    }//GEN-LAST:event_menuLodgementReportActionPerformed

    private void menuPersonsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPersonsActionPerformed
        openSearchParties();
    }//GEN-LAST:event_menuPersonsActionPerformed

    private void btnSetPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSetPasswordActionPerformed
        editPassword();
    }//GEN-LAST:event_btnSetPasswordActionPerformed

    private void jmiContextHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiContextHelpActionPerformed
        HelpUtility.getInstance().showTopic("overview");
    }//GEN-LAST:event_jmiContextHelpActionPerformed

    private void menuFlushCacheActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFlushCacheActionPerformed
        // Clear the local cache and the server cache. 
        CacheManager.clear();
        if (WSManager.getInstance().getAdminService().flushCache()) {
            MessageUtility.displayMessage(ClientMessage.ADMIN_FLUSH_CACHE);
        }
    }//GEN-LAST:event_menuFlushCacheActionPerformed

    private void menuSpatialEditMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSpatialEditMapActionPerformed
       openMapSpatialUnitEditor();
    }//GEN-LAST:event_menuSpatialEditMapActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar applicationsMain;
    private javax.swing.JButton btnDocumentSearch;
    private javax.swing.JButton btnManageParties;
    private javax.swing.JButton btnNewApplication;
    private javax.swing.JButton btnOpenBaUnitSearch;
    private javax.swing.JButton btnOpenMap;
    private javax.swing.JButton btnSearchApplications;
    private javax.swing.JButton btnSetPassword;
    private javax.swing.JButton btnShowDashboard;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JMenuItem jmiContextHelp;
    private javax.swing.JLabel labStatus;
    private javax.swing.JMenuItem menuAllLogLevel;
    private javax.swing.JMenu menuApplications;
    private javax.swing.JMenuItem menuBaUnitSearch;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuDefaultLogLevel;
    private javax.swing.JMenuItem menuDocumentSearch;
    private javax.swing.JMenuItem menuFlushCache;
    private javax.swing.JMenu menuLanguage;
    private javax.swing.JMenuItem menuLodgementReport;
    private javax.swing.JMenu menuLogLevel;
    private javax.swing.JMenu menuMap;
    private javax.swing.JMenuItem menuNewApplication;
    private javax.swing.JMenuItem menuOffLogLevel;
    private javax.swing.JMenuItem menuPersons;
    private javax.swing.JMenu menuReportsDesktop;
    private javax.swing.JMenu menuSearch;
    private javax.swing.JMenuItem menuSearchApplication;
    private javax.swing.JMenuItem menuShowMap;
    private javax.swing.JMenuItem menuSpatialEditMap;
    private javax.swing.JMenu menuView;
    private org.sola.clients.swing.ui.MainContentPanel pnlContent;
    private javax.swing.JPanel statusPanel;
    private org.sola.clients.swing.common.tasks.TaskPanel taskPanel1;
    private javax.swing.JLabel txtUserName;
    // End of variables declaration//GEN-END:variables
}
