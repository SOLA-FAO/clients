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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.sola.clients.swing.desktop.application.ApplicationForm;
import org.sola.clients.swing.desktop.application.ApplicationSearchForm;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleViewer;
import org.sola.common.RolesConstants;
import org.sola.common.help.HelpUtility;

/**
 * The main form of the application.
 */
public class MainForm extends FrameView {

    private ApplicationForm applicationForm;
    private ApplicationSearchForm searchApplicationForm;
    
    Object foreFont =  LafManager.getInstance().getForeFont();
    Object labFont =  LafManager.getInstance().getLabFont();
    Object bgFont =    LafManager.getInstance().getBgFont();
    Object txtFont =   LafManager.getInstance().getTxtFont();
    Object txtAreaFont =   LafManager.getInstance().getTxtAreaFont();
    Object btnFont =   LafManager.getInstance().getBtnFont();
    Object tabFont =   LafManager.getInstance().getTabFont();
    Object cmbFont =   LafManager.getInstance().getCmbFont();
    Object btnBackground =   LafManager.getInstance().getBtnBackground();
    
    
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
        // Customize buttons
        btnNewApplication.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_CREATE_APPS));
        btnOpenMap.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.GIS_VIEW_MAP));
        btnSearchApplications.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_VIEW_APPS));
        btnShowDashboard.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_VIEW_APPS));
        
        // Load dashboard
        DashBoardPanel dashBoard = new DashBoardPanel(this.getFrame());
        changingPanel.setViewportView(dashBoard);
        
        txtUserName.setText(SecurityBean.getCurrentUser().getUserName());
        
        jMenuItem3.setVisible(false);
        jMenuItem4.setVisible(false);
        jMenuItem5.setVisible(false);
        jMenuItem6.setVisible(false);

        // Enable/disable toolbar and main menu based on users access
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        applicationsMain = new javax.swing.JToolBar();
        btnShowDashboard = new javax.swing.JButton();
        btnNewApplication = new javax.swing.JButton();
        btnSearchApplications = new javax.swing.JButton();
        btnOpenMap = new javax.swing.JButton();
        changingPanel = new javax.swing.JScrollPane();
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
        javax.swing.JMenu homeDashboard = new javax.swing.JMenu();
        menuDashboardItem = new javax.swing.JMenuItem();
        menuApplications = new javax.swing.JMenu();
        menuNewApplication = new javax.swing.JMenuItem();
        menuSearchApplication = new javax.swing.JMenuItem();
        menuMap = new javax.swing.JMenu();
        menuShowMap = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jmiContextHelp = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        labStatus = new javax.swing.JLabel();
        taskPanel1 = new org.sola.clients.swing.common.tasks.TaskPanel();
        txtUserName = new javax.swing.JLabel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(700, 550));
        mainPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        applicationsMain.setFloatable(false);
        applicationsMain.setRollover(true);
        applicationsMain.setMaximumSize(new java.awt.Dimension(32769, 32769));
        applicationsMain.setMinimumSize(new java.awt.Dimension(980, 45));
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

        btnNewApplication.setAction(actionMap.get("openNewApplicationForm")); // NOI18N
        btnNewApplication.setFont(UIManager.getFont(btnFont));
        btnNewApplication.setFocusable(false);
        btnNewApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNewApplication.setName("btnNewApplication"); // NOI18N
        btnNewApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(btnNewApplication);

        btnSearchApplications.setAction(actionMap.get("searchApplications")); // NOI18N
        btnSearchApplications.setFont(UIManager.getFont(btnFont));
        btnSearchApplications.setFocusable(false);
        btnSearchApplications.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSearchApplications.setName("btnSearchApplications"); // NOI18N
        btnSearchApplications.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(btnSearchApplications);

        btnOpenMap.setAction(actionMap.get("openMap")); // NOI18N
        btnOpenMap.setFont(UIManager.getFont(btnFont));
        btnOpenMap.setFocusable(false);
        btnOpenMap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenMap.setName("btnOpenMap"); // NOI18N
        btnOpenMap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        applicationsMain.add(btnOpenMap);

        changingPanel.setAutoscrolls(true);
        changingPanel.setName("changingPanel"); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(applicationsMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1006, Short.MAX_VALUE)
            .add(changingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1006, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(applicationsMain, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))
        );

        menuBar.setName("menuBar"); // NOI18N
        menuBar.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
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

        menuBar.add(menuView);

        homeDashboard.setText(bundle.getString("MainForm.homeDashboard.text_1")); // NOI18N
        homeDashboard.setName("homeDashboard"); // NOI18N

        menuDashboardItem.setAction(actionMap.get("openDashBoard")); // NOI18N
        menuDashboardItem.setText(bundle.getString("MainForm.menuDashboardItem.text_1")); // NOI18N
        menuDashboardItem.setName("menuDashboardItem"); // NOI18N
        homeDashboard.add(menuDashboardItem);

        menuBar.add(homeDashboard);

        menuApplications.setText(bundle.getString("MainForm.menuApplications.text_1")); // NOI18N
        menuApplications.setName("menuApplications"); // NOI18N

        menuNewApplication.setAction(actionMap.get("openNewApplicationForm")); // NOI18N
        menuNewApplication.setText(bundle.getString("MainForm.menuNewApplication.text")); // NOI18N
        menuNewApplication.setName("menuNewApplication"); // NOI18N
        menuApplications.add(menuNewApplication);

        menuSearchApplication.setAction(actionMap.get("searchApplications")); // NOI18N
        menuSearchApplication.setText(bundle.getString("MainForm.menuSearchApplication.text_1")); // NOI18N
        menuSearchApplication.setName("menuSearchApplication"); // NOI18N
        menuApplications.add(menuSearchApplication);

        menuBar.add(menuApplications);

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
                .add(7, 7, 7)
                .add(labStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtUserName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 170, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 388, Short.MAX_VALUE)
                .add(taskPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(3, 3, 3))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(4, 4, 4)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(taskPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(labStatus)
                        .add(txtUserName)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void menuShowMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuShowMapActionPerformed
    }//GEN-LAST:event_menuShowMapActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar applicationsMain;
    private javax.swing.JButton btnNewApplication;
    private javax.swing.JButton btnOpenMap;
    private javax.swing.JButton btnSearchApplications;
    private javax.swing.JButton btnShowDashboard;
    private javax.swing.JScrollPane changingPanel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jmiContextHelp;
    private javax.swing.JLabel labStatus;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenu menuApplications;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuDashboardItem;
    private javax.swing.JMenuItem menuLangEN;
    private javax.swing.JMenuItem menuLangIT;
    private javax.swing.JMenu menuLanguage;
    private javax.swing.JMenu menuMap;
    private javax.swing.JMenuItem menuNewApplication;
    private javax.swing.JMenuItem menuSearchApplication;
    private javax.swing.JMenuItem menuShowMap;
    private javax.swing.JMenu menuView;
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
        DashBoardPanel dashBoard = new DashBoardPanel(this.getFrame());
        changingPanel.setViewportView(dashBoard);
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
    
    /** Opens empty {@link ApplicationForm} to create new application. */
    @Action
    public void openNewApplicationForm() {
        if (applicationForm == null || !applicationForm.isVisible()) {
            applicationForm = new ApplicationForm();
        }
        applicationForm.setVisible(true);
    }

    /** Opens map. */
    @Action
    public void openMap() {
        //Set up the content pane.
        ControlsBundleViewer mapCtrl = new ControlsBundleViewer();
        changingPanel.setViewportView(mapCtrl);
        mapCtrl.getMap().zoomToFullExtent();
    }

    /** Opens {@link ApplicationSearchForm}. */
    @Action
    public void searchApplications() {
        if (searchApplicationForm == null || !searchApplicationForm.isVisible()) {
            searchApplicationForm = new ApplicationSearchForm();
        }
        searchApplicationForm.setVisible(true);
    }
}
