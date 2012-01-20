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
package org.sola.clients.swing.desktop;

import java.awt.event.WindowEvent;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.sola.clients.swing.desktop.application.ApplicationPanel;
import org.sola.clients.swing.desktop.application.ApplicationSearchPanel;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.desktop.administrative.BaUnitSearchPanel;
import org.sola.clients.swing.desktop.source.DocumentSearchPanel;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleViewer;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.party.PartySearchPanel;
import org.sola.common.RolesConstants;
import org.sola.common.help.HelpUtility;
import org.sola.common.logging.LogUtility;

/**
 * The main form of the application.
 */
public class MainForm extends FrameView {

    Object foreFont = LafManager.getInstance().getForeFont();
    Object labFont = LafManager.getInstance().getLabFont();
    Object bgFont = LafManager.getInstance().getBgFont();
    Object txtFont = LafManager.getInstance().getTxtFont();
    Object txtAreaFont = LafManager.getInstance().getTxtAreaFont();
    Object btnFont = LafManager.getInstance().getBtnFont();
    Object tabFont = LafManager.getInstance().getTabFont();
    Object cmbFont = LafManager.getInstance().getCmbFont();
    Object btnBackground = LafManager.getInstance().getBtnBackground();

    /** Main form constructor. Initializes resources, help context and tasks. */
    public MainForm(SingleFrameApplication app) {
        super(app);
        URL imgURL = this.getClass().getResource("/images/sola/logo_icon.jpg");
        this.getFrame().setIconImage(new ImageIcon(imgURL).getImage());

        initComponents();
        /**
         * Get an instance of HelpUtility and obtain <code>ActionListener<code/>s for the Help Menu Item 
         * and the Help Button components. Register them on the the help menu item and the Help Button
         * 
         */
        HelpUtility hu = HelpUtility.getInstance();
        jmiContextHelp.addActionListener(hu.getHelpListener(jmiContextHelp, "overview"));

        this.getFrame().addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                postInit();
            }
        });
    }

    /** 
     * Runs post initialization tasks. Enables or disables toolbar buttons and 
     * menu items depending on user rights. Loads various data after the 
     * form has been opened. It helps to display form with no significant delays. 
     */
    private void postInit() {
        customizeComponents();
        // Customize buttons
        btnNewApplication.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_CREATE_APPS));
        btnOpenMap.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.GIS_VIEW_MAP));
        btnSearchApplications.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_VIEW_APPS));
        btnShowDashboard.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_VIEW_APPS));
        btnManageParties.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.PARTY_SAVE));

        // Load dashboard
        openDashBoard();

        txtUserName.setText(SecurityBean.getCurrentUser().getUserName());

        jMenuItem3.setVisible(false);
        jMenuItem4.setVisible(false);
        jMenuItem5.setVisible(false);
        jMenuItem6.setVisible(false);

        // Enable/disable toolbar and main menu based on users access
    }

    /** Applies customization of component L&F. */
    private void customizeComponents() {
        //    BUTTONS   
        LafManager.getInstance().setBtnProperties(btnNewApplication);
        LafManager.getInstance().setBtnProperties(btnOpenMap);
        LafManager.getInstance().setBtnProperties(btnSearchApplications);
        LafManager.getInstance().setBtnProperties(btnShowDashboard);

//    LABELS    
        LafManager.getInstance().setLabProperties(labStatus);
        LafManager.getInstance().setLabProperties(txtUserName);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        applicationsMain = new javax.swing.JToolBar();
        btnShowDashboard = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnNewApplication = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnSearchApplications = new javax.swing.JButton();
        btnOpenBaUnitSearch = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnManageParties = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnOpenMap = new javax.swing.JButton();
        pnlContent = new org.sola.clients.swing.ui.MainContentPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem menuExitItem = new javax.swing.JMenuItem();
        menuView = new javax.swing.JMenu();
        menuLanguage = new javax.swing.JMenu();
        menuLangEN = new javax.swing.JMenuItem();
        menuLangIT = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        menuLogLevel = new javax.swing.JMenu();
        menuAllLogLevel = new javax.swing.JMenuItem();
        menuDefaultLogLevel = new javax.swing.JMenuItem();
        menuOffLogLevel = new javax.swing.JMenuItem();
        javax.swing.JMenu homeDashboard = new javax.swing.JMenu();
        menuDashboardItem = new javax.swing.JMenuItem();
        menuReports = new javax.swing.JMenu();
        menuApplications = new javax.swing.JMenu();
        menuNewApplication = new javax.swing.JMenuItem();
        menuRegistration = new javax.swing.JMenu();
        menuMap = new javax.swing.JMenu();
        menuShowMap = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jmiContextHelp = new javax.swing.JMenuItem();
        menuSearch = new javax.swing.JMenu();
        menuSearchApplication = new javax.swing.JMenuItem();
        menuBaUnitSearch = new javax.swing.JMenuItem();
        menuDocumentSearch = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        labStatus = new javax.swing.JLabel();
        taskPanel1 = new org.sola.clients.swing.common.tasks.TaskPanel();
        txtUserName = new javax.swing.JLabel();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(700, 550));
        mainPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        applicationsMain.setFloatable(false);
        applicationsMain.setRollover(true);
        applicationsMain.setFont(new java.awt.Font("Tahoma", 0, 12));
        applicationsMain.setMaximumSize(new java.awt.Dimension(32769, 32769));
        applicationsMain.setMinimumSize(new java.awt.Dimension(90, 45));
        applicationsMain.setName("applicationsMain"); // NOI18N
        applicationsMain.setPreferredSize(new java.awt.Dimension(980, 45));
        applicationsMain.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(MainForm.class, this);
        btnShowDashboard.setAction(actionMap.get("openDashBoard")); // NOI18N
        btnShowDashboard.setFont(UIManager.getFont(btnFont));
        btnShowDashboard.setFocusable(false);
        btnShowDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnShowDashboard.setName("btnShowDashboard"); // NOI18N
        btnShowDashboard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(btnShowDashboard);

        jSeparator2.setName("jSeparator2"); // NOI18N
        applicationsMain.add(jSeparator2);

        btnNewApplication.setAction(actionMap.get("openNewApplicationForm")); // NOI18N
        btnNewApplication.setFont(UIManager.getFont(btnFont));
        btnNewApplication.setFocusable(false);
        btnNewApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNewApplication.setName("btnNewApplication"); // NOI18N
        btnNewApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(btnNewApplication);

        jSeparator4.setName("jSeparator4"); // NOI18N
        applicationsMain.add(jSeparator4);

        btnSearchApplications.setAction(actionMap.get("searchApplications")); // NOI18N
        btnSearchApplications.setFont(UIManager.getFont(btnFont));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        btnSearchApplications.setText(bundle.getString("MainForm.btnSearchApplications.text")); // NOI18N
        btnSearchApplications.setFocusable(false);
        btnSearchApplications.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSearchApplications.setName("btnSearchApplications"); // NOI18N
        btnSearchApplications.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(btnSearchApplications);

        btnOpenBaUnitSearch.setAction(actionMap.get("searchBaUnit")); // NOI18N
        btnOpenBaUnitSearch.setText(bundle.getString("MainForm.btnOpenBaUnitSearch.text_1")); // NOI18N
        btnOpenBaUnitSearch.setFocusable(false);
        btnOpenBaUnitSearch.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenBaUnitSearch.setName("btnOpenBaUnitSearch"); // NOI18N
        btnOpenBaUnitSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(btnOpenBaUnitSearch);

        jButton1.setAction(actionMap.get("searchDocuments")); // NOI18N
        jButton1.setText(bundle.getString("MainForm.jButton1.text_1")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(jButton1);

        jSeparator3.setName("jSeparator3"); // NOI18N
        applicationsMain.add(jSeparator3);

        btnManageParties.setAction(actionMap.get("manageParties")); // NOI18N
        btnManageParties.setText(bundle.getString("MainForm.btnManageParties.text_1")); // NOI18N
        btnManageParties.setFocusable(false);
        btnManageParties.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnManageParties.setName("btnManageParties"); // NOI18N
        btnManageParties.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(btnManageParties);

        jSeparator1.setName("jSeparator1"); // NOI18N
        applicationsMain.add(jSeparator1);

        btnOpenMap.setAction(actionMap.get("openMap")); // NOI18N
        btnOpenMap.setFont(UIManager.getFont(btnFont));
        btnOpenMap.setText(bundle.getString("MainForm.btnOpenMap.text")); // NOI18N
        btnOpenMap.setFocusable(false);
        btnOpenMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenMap.setName("btnOpenMap"); // NOI18N
        btnOpenMap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(btnOpenMap);

        pnlContent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        pnlContent.setName("pnlContent"); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(applicationsMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
            .add(pnlContent, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(applicationsMain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlContent, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
        );

        menuBar.setFont(new java.awt.Font("Tahoma", 0, 12));
        menuBar.setName("menuBar"); // NOI18N
        menuBar.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        fileMenu.setText(bundle.getString("MainForm.fileMenu.text_1")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        menuExitItem.setAction(actionMap.get("quit")); // NOI18N
        menuExitItem.setText(bundle.getString("MainForm.menuExitItem.text")); // NOI18N
        menuExitItem.setText(bundle.getString("MainForm.menuExitItem.text")); // NOI18N
        menuExitItem.setToolTipText(bundle.getString("MainForm.menuExitItem.toolTipText")); // NOI18N
        menuExitItem.setName("menuExitItem"); // NOI18N
        fileMenu.add(menuExitItem);

        menuBar.add(fileMenu);

        menuView.setText(bundle.getString("MainForm.menuView.text_1")); // NOI18N
        menuView.setName("menuView"); // NOI18N

        menuLanguage.setText(bundle.getString("MainForm.menuLanguage.text_1")); // NOI18N
        menuLanguage.setName("menuLanguage"); // NOI18N

        menuLangEN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/flags/en.jpg"))); // NOI18N
        menuLangEN.setText(bundle.getString("MainForm.menuLangEN.text_1")); // NOI18N
        menuLangEN.setName("menuLangEN"); // NOI18N
        menuLanguage.add(menuLangEN);

        menuLangIT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/flags/it.jpg"))); // NOI18N
        menuLangIT.setText(bundle.getString("MainForm.menuLangIT.text_1")); // NOI18N
        menuLangIT.setName("menuLangIT"); // NOI18N
        menuLanguage.add(menuLangIT);

        menuView.add(menuLanguage);

        jMenu1.setText(bundle.getString("MainForm.jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem1.addActionListener(new LNFSetter("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel","green"));
        jMenuItem1.setText(bundle.getString("MainForm.jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenu1.add(jMenuItem1);

        jMenuItem2.addActionListener(new LNFSetter("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel","autumn"));
        jMenuItem2.setText(bundle.getString("MainForm.jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenu1.add(jMenuItem2);

        jMenuItem3.addActionListener(new LNFSetter("javax.swing.plaf.metal.MetalLookAndFeel", "default"));
        jMenuItem3.setText(bundle.getString("MainForm.jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenu1.add(jMenuItem3);

        jMenuItem4.addActionListener(new LNFSetter("com.sun.java.swing.plaf.windows.WindowsLookAndFeel", "default"));
        jMenuItem4.setText(bundle.getString("MainForm.jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenu1.add(jMenuItem4);

        jMenuItem5.addActionListener(new LNFSetter("com.sun.java.swing.plaf.motif.MotifLookAndFeel", "default"));
        jMenuItem5.setText(bundle.getString("MainForm.jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        jMenu1.add(jMenuItem5);

        String defaultLookAndFeel = UIManager.getSystemLookAndFeelClassName();
        jMenuItem6.addActionListener(new LNFSetter(defaultLookAndFeel,  "default"));
        jMenuItem6.setText(bundle.getString("MainForm.jMenuItem6.text")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        jMenu1.add(jMenuItem6);

        menuView.add(jMenu1);

        menuLogLevel.setText(bundle.getString("MainForm.menuLogLevel.text")); // NOI18N
        menuLogLevel.setName("menuLogLevel"); // NOI18N

        menuAllLogLevel.setAction(actionMap.get("setAllLogLevel")); // NOI18N
        menuAllLogLevel.setText(bundle.getString("MainForm.menuAllLogLevel.text")); // NOI18N
        menuAllLogLevel.setActionCommand(bundle.getString("MainForm.menuAllLogLevel.actionCommand")); // NOI18N
        menuAllLogLevel.setName("menuAllLogLevel"); // NOI18N
        menuLogLevel.add(menuAllLogLevel);

        menuDefaultLogLevel.setAction(actionMap.get("setDefaultLogLevel")); // NOI18N
        menuDefaultLogLevel.setText(bundle.getString("MainForm.menuDefaultLogLevel.text")); // NOI18N
        menuDefaultLogLevel.setActionCommand(bundle.getString("MainForm.menuDefaultLogLevel.actionCommand")); // NOI18N
        menuDefaultLogLevel.setName("menuDefaultLogLevel"); // NOI18N
        menuLogLevel.add(menuDefaultLogLevel);

        menuOffLogLevel.setAction(actionMap.get("setOffLogLevel")); // NOI18N
        menuOffLogLevel.setText(bundle.getString("MainForm.menuOffLogLevel.text")); // NOI18N
        menuOffLogLevel.setActionCommand(bundle.getString("MainForm.menuOffLogLevel.actionCommand")); // NOI18N
        menuOffLogLevel.setName("menuOffLogLevel"); // NOI18N
        menuLogLevel.add(menuOffLogLevel);

        menuView.add(menuLogLevel);

        menuBar.add(menuView);

        homeDashboard.setText(bundle.getString("MainForm.homeDashboard.text_1")); // NOI18N
        homeDashboard.setName("homeDashboard"); // NOI18N

        menuDashboardItem.setAction(actionMap.get("openDashBoard")); // NOI18N
        menuDashboardItem.setText(bundle.getString("MainForm.menuDashboardItem.text_1")); // NOI18N
        menuDashboardItem.setName("menuDashboardItem"); // NOI18N
        homeDashboard.add(menuDashboardItem);

        menuReports.setText(bundle.getString("MainForm.menuReports.text_1")); // NOI18N
        menuReports.setName("menuReports"); // NOI18N
        homeDashboard.add(menuReports);

        menuBar.add(homeDashboard);

        menuApplications.setText(bundle.getString("MainForm.menuApplications.text_1")); // NOI18N
        menuApplications.setName("menuApplications"); // NOI18N

        menuNewApplication.setAction(actionMap.get("openNewApplicationForm")); // NOI18N
        menuNewApplication.setText(bundle.getString("MainForm.menuNewApplication.text")); // NOI18N
        menuNewApplication.setName("menuNewApplication"); // NOI18N
        menuApplications.add(menuNewApplication);

        menuBar.add(menuApplications);

        menuRegistration.setText(bundle.getString("MainForm.menuRegistration.text_1")); // NOI18N
        menuRegistration.setName("menuRegistration"); // NOI18N
        menuBar.add(menuRegistration);

        menuMap.setText(bundle.getString("MainForm.menuMap.text_1")); // NOI18N
        menuMap.setName("menuMap"); // NOI18N

        menuShowMap.setAction(actionMap.get("openMap")); // NOI18N
        menuShowMap.setName("menuShowMap"); // NOI18N
        menuShowMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuShowMapActionPerformed(evt);
            }
        });
        menuMap.add(menuShowMap);

        menuBar.add(menuMap);

        helpMenu.setText(bundle.getString("MainForm.helpMenu.text_1")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        jmiContextHelp.setText(bundle.getString("MainForm.jmiContextHelp.text_1")); // NOI18N
        jmiContextHelp.setName("jmiContextHelp"); // NOI18N
        helpMenu.add(jmiContextHelp);

        menuBar.add(helpMenu);

        menuSearch.setText(bundle.getString("MainForm.menuSearch.text")); // NOI18N
        menuSearch.setName("menuSearch"); // NOI18N

        menuSearchApplication.setAction(actionMap.get("searchApplications")); // NOI18N
        menuSearchApplication.setText(bundle.getString("MainForm.menuSearchApplication.text_1")); // NOI18N
        menuSearchApplication.setName("menuSearchApplication"); // NOI18N
        menuSearch.add(menuSearchApplication);

        menuBaUnitSearch.setAction(actionMap.get("searchBaUnit")); // NOI18N
        menuBaUnitSearch.setText(bundle.getString("MainForm.menuBaUnitSearch.text")); // NOI18N
        menuBaUnitSearch.setName("menuBaUnitSearch"); // NOI18N
        menuSearch.add(menuBaUnitSearch);

        menuDocumentSearch.setAction(actionMap.get("searchDocuments")); // NOI18N
        menuDocumentSearch.setText(bundle.getString("MainForm.menuDocumentSearch.text")); // NOI18N
        menuDocumentSearch.setName("menuDocumentSearch"); // NOI18N
        menuSearch.add(menuDocumentSearch);

        menuBar.add(menuSearch);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(1024, 24));

        labStatus.setFont(new java.awt.Font("Tahoma", 0, 12));
        labStatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labStatus.setText(bundle.getString("MainForm.labStatus.text_1")); // NOI18N
        labStatus.setName("labStatus"); // NOI18N

        taskPanel1.setName("taskPanel1"); // NOI18N

        txtUserName.setFont(new java.awt.Font("Tahoma", 1, 12));
        txtUserName.setText(bundle.getString("MainForm.txtUserName.text")); // NOI18N
        txtUserName.setName("txtUserName"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(labStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtUserName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .add(52, 52, 52)
                .add(taskPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 373, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(4, 4, 4)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(taskPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtUserName)
                        .add(labStatus)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void menuShowMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuShowMapActionPerformed
    }//GEN-LAST:event_menuShowMapActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar applicationsMain;
    private javax.swing.JButton btnManageParties;
    private javax.swing.JButton btnNewApplication;
    private javax.swing.JButton btnOpenBaUnitSearch;
    private javax.swing.JButton btnOpenMap;
    private javax.swing.JButton btnSearchApplications;
    private javax.swing.JButton btnShowDashboard;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JMenuItem jmiContextHelp;
    private javax.swing.JLabel labStatus;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem menuAllLogLevel;
    private javax.swing.JMenu menuApplications;
    private javax.swing.JMenuItem menuBaUnitSearch;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuDashboardItem;
    private javax.swing.JMenuItem menuDefaultLogLevel;
    private javax.swing.JMenuItem menuDocumentSearch;
    private javax.swing.JMenuItem menuLangEN;
    private javax.swing.JMenuItem menuLangIT;
    private javax.swing.JMenu menuLanguage;
    private javax.swing.JMenu menuLogLevel;
    private javax.swing.JMenu menuMap;
    private javax.swing.JMenuItem menuNewApplication;
    private javax.swing.JMenuItem menuOffLogLevel;
    private javax.swing.JMenu menuRegistration;
    private javax.swing.JMenu menuReports;
    private javax.swing.JMenu menuSearch;
    private javax.swing.JMenuItem menuSearchApplication;
    private javax.swing.JMenuItem menuShowMap;
    private javax.swing.JMenu menuView;
    private org.sola.clients.swing.ui.MainContentPanel pnlContent;
    private javax.swing.JPanel statusPanel;
    private org.sola.clients.swing.common.tasks.TaskPanel taskPanel1;
    private javax.swing.JLabel txtUserName;
    // End of variables declaration//GEN-END:variables

    /** Shows {@link AboutForm}. */
    @Action
    public void showAboutBox() {
        JFrame mainFrame = DesktopApplication.getApplication().getMainFrame();
        AboutForm aboutBox = new AboutForm(mainFrame);
        aboutBox.setLocationRelativeTo(mainFrame);
        DesktopApplication.getApplication().show(aboutBox);
    }

    /** Opens and embeds dashboard into the main form. */
    @Action
    public void openDashBoard() {
        if (!pnlContent.isPanelOpened(MainContentPanel.CARD_DASHBOARD)) {
            DashBoardPanel dashBoard = new DashBoardPanel();
            pnlContent.addPanel(dashBoard, MainContentPanel.CARD_DASHBOARD);
        }
        pnlContent.showPanel(MainContentPanel.CARD_DASHBOARD);
    }

    class LNFSetter implements ActionListener {

        String theLNFName;
        String whichButton;

//    LoginForm loginform;
        LNFSetter(String lnfName, String theme) {
            theLNFName = lnfName;
            whichButton = theme;

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                LafManager.getInstance().setProperties(whichButton);
                UIManager.setLookAndFeel(theLNFName);

                SwingUtilities.updateComponentTreeUI(mainPanel);
                SwingUtilities.updateComponentTreeUI(menuBar);
                SwingUtilities.updateComponentTreeUI(statusPanel);
                openDashBoard();

            } catch (Exception evt) {
                JOptionPane.showMessageDialog(null, "setLookAndFeel didn't work: " + evt, "UI Failure",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /** Opens empty {@link ApplicationPanel} to create new application. */
    @Action
    public void openNewApplicationForm() {
        if (pnlContent.isPanelOpened(MainContentPanel.CARD_APPLICATION)) {
            pnlContent.closePanel(MainContentPanel.CARD_APPLICATION);
        }
        ApplicationPanel applicationPanel = new ApplicationPanel();
        pnlContent.addPanel(applicationPanel, MainContentPanel.CARD_APPLICATION);
        pnlContent.showPanel(MainContentPanel.CARD_APPLICATION);
    }

    /** Opens map. */
    @Action
    public void openMap() {
        if (!pnlContent.isPanelOpened(MainContentPanel.CARD_MAP)) {
            ControlsBundleViewer mapCtrl = new ControlsBundleViewer();
            pnlContent.addPanel(mapCtrl, MainContentPanel.CARD_MAP);
            mapCtrl.getMap().zoomToFullExtent();
        }
        pnlContent.showPanel(MainContentPanel.CARD_MAP);
    }

    /** Opens {@link ApplicationSearchForm}. */
    @Action
    public void searchApplications() {
        if (!pnlContent.isPanelOpened(MainContentPanel.CARD_APPSEARCH)) {
            ApplicationSearchPanel searchApplicationPanel = new ApplicationSearchPanel();
            pnlContent.addPanel(searchApplicationPanel, MainContentPanel.CARD_APPSEARCH);
        }
        pnlContent.showPanel(MainContentPanel.CARD_APPSEARCH);
    }

    @Action
    public void manageParties() {
        if (!pnlContent.isPanelOpened(MainContentPanel.CARD_PERSONS)) {
            PartySearchPanel partySearchPanel = new PartySearchPanel();
            pnlContent.addPanel(partySearchPanel, MainContentPanel.CARD_PERSONS);
        }
        pnlContent.showPanel(MainContentPanel.CARD_PERSONS);
    }

    @Action
    public void searchBaUnit() {
        if (!pnlContent.isPanelOpened(MainContentPanel.CARD_BAUNIT_SEARCH)) {
            BaUnitSearchPanel baUnitSearchPanel = new BaUnitSearchPanel();
            pnlContent.addPanel(baUnitSearchPanel, MainContentPanel.CARD_BAUNIT_SEARCH);
        }
        pnlContent.showPanel(MainContentPanel.CARD_BAUNIT_SEARCH);
    }

    @Action
    public void searchDocuments() {
        if (!pnlContent.isPanelOpened(MainContentPanel.CARD_DOCUMENT_SEARCH)) {
            DocumentSearchPanel documentSearchPanel = new DocumentSearchPanel();
            pnlContent.addPanel(documentSearchPanel, MainContentPanel.CARD_DOCUMENT_SEARCH);
        }
        pnlContent.showPanel(MainContentPanel.CARD_DOCUMENT_SEARCH);
    }

    @Action
    public void setAllLogLevel() {
         LogUtility.setLogLevel(Level.ALL);
    }

    @Action
    public void setDefaultLogLevel() {
        LogUtility.setLogLevel(Level.INFO);
    }

    @Action
    public void setOffLogLevel() {
        LogUtility.setLogLevel(Level.OFF);
    }
}
