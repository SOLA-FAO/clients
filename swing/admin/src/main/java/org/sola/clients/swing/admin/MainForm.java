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

import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperPrint;
import org.jdesktop.application.Action;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.referencedata.BaUnitRelTypeBean;
import org.sola.clients.beans.referencedata.BaUnitTypeBean;
import org.sola.clients.beans.referencedata.BrSeverityTypeBean;
import org.sola.clients.beans.referencedata.BrTechnicalTypeBean;
import org.sola.clients.beans.referencedata.BrValidationTargetTypeBean;
import org.sola.clients.beans.referencedata.CommunicationTypeBean;
import org.sola.clients.beans.referencedata.GenderTypeBean;
import org.sola.clients.beans.referencedata.IdTypeBean;
import org.sola.clients.beans.referencedata.MortgageTypeBean;
import org.sola.clients.beans.referencedata.PartyRoleTypeBean;
import org.sola.clients.beans.referencedata.PartyTypeBean;
import org.sola.clients.beans.referencedata.RegistrationStatusTypeBean;
import org.sola.clients.beans.referencedata.RequestCategoryTypeBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.referencedata.RrrGroupTypeBean;
import org.sola.clients.beans.referencedata.TypeActionBean;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.beans.referencedata.ServiceActionTypeBean;
import org.sola.clients.beans.referencedata.ServiceStatusTypeBean;
import org.sola.clients.beans.referencedata.SourceTypeBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.admin.referencedata.ReferenceDataManagementPanel;
import org.sola.clients.swing.admin.security.GroupsManagementPanel;
import org.sola.clients.swing.admin.security.RolesManagementPanel;
import org.sola.clients.swing.admin.security.UsersManagementPanel;
import org.sola.clients.swing.admin.system.BrManagementPanel;
import org.sola.clients.swing.common.LafManager;
import org.sola.common.RolesConstants;

/**
 * Main form of the Admin application.
 */
public class MainForm extends javax.swing.JFrame {

    /** Creates new form MainForm */
    public MainForm() {
        initComponents();
        
        URL imgURL = this.getClass().getResource("/images/common/admin.png");
        this.setIconImage(new ImageIcon(imgURL).getImage());
        lblUserName.setText(SecurityBean.getCurrentUser().getUserName());
        customizeComponents() ;
        customizeForm();
    }
    
    
    
       /** Applies customization of component L&F. */
    private void customizeComponents() {
    
     
//    BUTTONS   
    LafManager.getInstance().setBtnProperties(btnGISSettings);
    LafManager.getInstance().setBtnProperties(btnGroups);
    LafManager.getInstance().setBtnProperties(btnLanguage);
    LafManager.getInstance().setBtnProperties(btnRoles);
    LafManager.getInstance().setBtnProperties(btnSystemSettings);
    LafManager.getInstance().setBtnProperties(btnUsers);
    
//    LABELS    
    LafManager.getInstance().setLabProperties(jLabel1);
    LafManager.getInstance().setLabProperties(lblUserName);
    
    }

    
    
    /** Customizes main form regarding user access rights. */
    private void customizeForm(){
        boolean hasSecurityRole = SecurityBean.isInRole(RolesConstants.ADMIN_MANAGE_SECURITY);
        boolean hasRefdataRole = SecurityBean.isInRole(RolesConstants.ADMIN_MANAGE_REFDATA);
        boolean hasSettingsRole = SecurityBean.isInRole(RolesConstants.ADMIN_MANAGE_SETTINGS);
        boolean hasBRRole = SecurityBean.isInRole(RolesConstants.ADMIN_MANAGE_BR);
        
        btnRoles.getAction().setEnabled(hasSecurityRole);
        btnUsers.getAction().setEnabled(hasSecurityRole);
        btnGroups.getAction().setEnabled(hasSecurityRole);
        
        btnSystemSettings.getAction().setEnabled(hasSettingsRole);
        btnGISSettings.getAction().setEnabled(hasSettingsRole);
        btnLanguage.getAction().setEnabled(hasSettingsRole);
        btnBr.getAction().setEnabled(hasBRRole);
        
        menuRefData.setEnabled(hasRefdataRole);
    }
    
    /** Opens reference data management panel for different reference data type.*/
    private <T extends AbstractCodeBean> void openReferenceDataPanel(
            Class<T> refDataClass, String headerTitle){
        ReferenceDataManagementPanel panel = new ReferenceDataManagementPanel(refDataClass, headerTitle);
        mainScrollPane.setViewportView(panel);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainToolbar = new javax.swing.JToolBar();
        btnRoles = new javax.swing.JButton();
        btnGroups = new javax.swing.JButton();
        btnUsers = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnLanguage = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnSystemSettings = new javax.swing.JButton();
        btnGISSettings = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnBr = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        mainScrollPane = new javax.swing.JScrollPane();
        statusPanel = new javax.swing.JPanel();
        taskPanel1 = new org.sola.clients.swing.common.tasks.TaskPanel();
        jLabel1 = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        mainMenu = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuExit = new javax.swing.JMenuItem();
        menuSecurity = new javax.swing.JMenu();
        menuRoles = new javax.swing.JMenuItem();
        menuGroups = new javax.swing.JMenuItem();
        menuUsers = new javax.swing.JMenuItem();
        menuRefData = new javax.swing.JMenu();
        menuApplications = new javax.swing.JMenu();
        menuRequestCategory = new javax.swing.JMenuItem();
        menuRequestTypes = new javax.swing.JMenuItem();
        menuTypeActions = new javax.swing.JMenuItem();
        menuServiceActionTypes = new javax.swing.JMenuItem();
        menuServiceStatusTypes = new javax.swing.JMenuItem();
        menuAdministrative = new javax.swing.JMenu();
        menuBaUnitType = new javax.swing.JMenuItem();
        menuBaUnitRelationTypes = new javax.swing.JMenuItem();
        menuMortgageTypes = new javax.swing.JMenuItem();
        menuRrrGroupTypes = new javax.swing.JMenuItem();
        menuRrrTypes = new javax.swing.JMenuItem();
        menuSources = new javax.swing.JMenu();
        menuSourceTypes = new javax.swing.JMenuItem();
        menuParty = new javax.swing.JMenu();
        menuCommunicationType = new javax.swing.JMenuItem();
        menuIdTypes = new javax.swing.JMenuItem();
        menuGenders = new javax.swing.JMenuItem();
        menuPartyRoleType = new javax.swing.JMenuItem();
        menuPartyType = new javax.swing.JMenuItem();
        menuSystem = new javax.swing.JMenu();
        menuBRSeverityType = new javax.swing.JMenuItem();
        menuBRValidationTargetType = new javax.swing.JMenuItem();
        menuBRTechnicalType = new javax.swing.JMenuItem();
        menuTransaction = new javax.swing.JMenu();
        menuRegistrationStatusType = new javax.swing.JMenuItem();
        menuReports = new javax.swing.JMenu();
        menuLodgementReport = new javax.swing.JMenuItem();
        menuTimeReport = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/Bundle"); // NOI18N
        setTitle(bundle.getString("MainForm.title")); // NOI18N

        mainToolbar.setFloatable(false);
        mainToolbar.setRollover(true);
        mainToolbar.setName("mainToolbar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(MainForm.class, this);
        btnRoles.setAction(actionMap.get("manageRoles")); // NOI18N
        btnRoles.setText(bundle.getString("MainForm.btnRoles.text")); // NOI18N
        btnRoles.setFocusable(false);
        btnRoles.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRoles.setName("btnRoles"); // NOI18N
        btnRoles.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(btnRoles);

        btnGroups.setAction(actionMap.get("manageGroups")); // NOI18N
        btnGroups.setText(bundle.getString("MainForm.btnGroups.text")); // NOI18N
        btnGroups.setFocusable(false);
        btnGroups.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGroups.setName("btnGroups"); // NOI18N
        btnGroups.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(btnGroups);

        btnUsers.setAction(actionMap.get("manageUsers")); // NOI18N
        btnUsers.setText(bundle.getString("MainForm.btnUsers.text")); // NOI18N
        btnUsers.setFocusable(false);
        btnUsers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUsers.setName("btnUsers"); // NOI18N
        btnUsers.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(btnUsers);

        jSeparator1.setName("jSeparator1"); // NOI18N
        mainToolbar.add(jSeparator1);

        btnLanguage.setAction(actionMap.get("manageLanguages")); // NOI18N
        btnLanguage.setText(bundle.getString("MainForm.btnLanguage.text")); // NOI18N
        btnLanguage.setFocusable(false);
        btnLanguage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLanguage.setName("btnLanguage"); // NOI18N
        btnLanguage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(btnLanguage);

        jSeparator2.setName("jSeparator2"); // NOI18N
        mainToolbar.add(jSeparator2);

        btnSystemSettings.setAction(actionMap.get("manageSystemSettings")); // NOI18N
        btnSystemSettings.setText(bundle.getString("MainForm.btnSystemSettings.text")); // NOI18N
        btnSystemSettings.setFocusable(false);
        btnSystemSettings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSystemSettings.setName("btnSystemSettings"); // NOI18N
        btnSystemSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(btnSystemSettings);

        btnGISSettings.setAction(actionMap.get("manageGisSettings")); // NOI18N
        btnGISSettings.setText(bundle.getString("MainForm.btnGISSettings.text")); // NOI18N
        btnGISSettings.setFocusable(false);
        btnGISSettings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGISSettings.setName("btnGISSettings"); // NOI18N
        btnGISSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(btnGISSettings);

        jSeparator3.setName("jSeparator3"); // NOI18N
        mainToolbar.add(jSeparator3);

        btnBr.setAction(actionMap.get("manageBr")); // NOI18N
        btnBr.setText(bundle.getString("MainForm.btnBr.text")); // NOI18N
        btnBr.setFocusable(false);
        btnBr.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBr.setName("btnBr"); // NOI18N
        btnBr.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mainToolbar.add(btnBr);

        mainPanel.setName("mainPanel"); // NOI18N

        mainScrollPane.setName("mainScrollPane"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
        );

        statusPanel.setName("statusPanel"); // NOI18N

        taskPanel1.setName("taskPanel1"); // NOI18N

        jLabel1.setText(bundle.getString("MainForm.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        lblUserName.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblUserName.setText(bundle.getString("MainForm.lblUserName.text")); // NOI18N
        lblUserName.setName("lblUserName"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 228, Short.MAX_VALUE)
                .addComponent(taskPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(taskPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(lblUserName)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainMenu.setName("mainMenu"); // NOI18N

        menuFile.setText(bundle.getString("MainForm.menuFile.text")); // NOI18N
        menuFile.setName("menuFile"); // NOI18N

        menuExit.setAction(actionMap.get("exit")); // NOI18N
        menuExit.setText(bundle.getString("MainForm.menuExit.text")); // NOI18N
        menuExit.setName("menuExit"); // NOI18N
        menuFile.add(menuExit);

        mainMenu.add(menuFile);

        menuSecurity.setText(bundle.getString("MainForm.menuSecurity.text")); // NOI18N
        menuSecurity.setName("menuSecurity"); // NOI18N

        menuRoles.setAction(actionMap.get("manageRoles")); // NOI18N
        menuRoles.setText(bundle.getString("MainForm.menuRoles.text")); // NOI18N
        menuRoles.setName("menuRoles"); // NOI18N
        menuSecurity.add(menuRoles);

        menuGroups.setAction(actionMap.get("manageGroups")); // NOI18N
        menuGroups.setText(bundle.getString("MainForm.menuGroups.text")); // NOI18N
        menuGroups.setName("menuGroups"); // NOI18N
        menuSecurity.add(menuGroups);

        menuUsers.setAction(actionMap.get("manageUsers")); // NOI18N
        menuUsers.setText(bundle.getString("MainForm.menuUsers.text")); // NOI18N
        menuUsers.setName("menuUsers"); // NOI18N
        menuSecurity.add(menuUsers);

        mainMenu.add(menuSecurity);

        menuRefData.setText(bundle.getString("MainForm.menuRefData.text")); // NOI18N
        menuRefData.setName("menuRefData"); // NOI18N

        menuApplications.setText(bundle.getString("MainForm.menuApplications.text")); // NOI18N
        menuApplications.setName("menuApplications"); // NOI18N

        menuRequestCategory.setAction(actionMap.get("manageRequestCategories")); // NOI18N
        menuRequestCategory.setText(bundle.getString("MainForm.menuRequestCategory.text")); // NOI18N
        menuRequestCategory.setName("menuRequestCategory"); // NOI18N
        menuApplications.add(menuRequestCategory);

        menuRequestTypes.setAction(actionMap.get("manageRequestTypes")); // NOI18N
        menuRequestTypes.setText(bundle.getString("MainForm.menuRequestTypes.text")); // NOI18N
        menuRequestTypes.setName("menuRequestTypes"); // NOI18N
        menuApplications.add(menuRequestTypes);

        menuTypeActions.setAction(actionMap.get("manageTypeActions")); // NOI18N
        menuTypeActions.setText(bundle.getString("MainForm.menuTypeActions.text")); // NOI18N
        menuTypeActions.setName("menuTypeActions"); // NOI18N
        menuApplications.add(menuTypeActions);

        menuServiceActionTypes.setAction(actionMap.get("manageServiceActionTypes")); // NOI18N
        menuServiceActionTypes.setText(bundle.getString("MainForm.menuServiceActionTypes.text")); // NOI18N
        menuServiceActionTypes.setName("menuServiceActionTypes"); // NOI18N
        menuApplications.add(menuServiceActionTypes);

        menuServiceStatusTypes.setAction(actionMap.get("manageServiceStatusTypes")); // NOI18N
        menuServiceStatusTypes.setText(bundle.getString("MainForm.menuServiceStatusTypes.text")); // NOI18N
        menuServiceStatusTypes.setName("menuServiceStatusTypes"); // NOI18N
        menuApplications.add(menuServiceStatusTypes);

        menuRefData.add(menuApplications);

        menuAdministrative.setText(bundle.getString("MainForm.menuAdministrative.text")); // NOI18N
        menuAdministrative.setName("menuAdministrative"); // NOI18N

        menuBaUnitType.setAction(actionMap.get("manageBAUnitType")); // NOI18N
        menuBaUnitType.setText(bundle.getString("MainForm.menuBaUnitType.text")); // NOI18N
        menuBaUnitType.setName("menuBaUnitType"); // NOI18N
        menuAdministrative.add(menuBaUnitType);

        menuBaUnitRelationTypes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuBaUnitRelationTypes.setText(bundle.getString("MainForm.menuBaUnitRelationTypes.text")); // NOI18N
        menuBaUnitRelationTypes.setName("menuBaUnitRelationTypes"); // NOI18N
        menuBaUnitRelationTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBaUnitRelationTypesActionPerformed(evt);
            }
        });
        menuAdministrative.add(menuBaUnitRelationTypes);

        menuMortgageTypes.setAction(actionMap.get("manageMortgageTypes")); // NOI18N
        menuMortgageTypes.setText(bundle.getString("MainForm.menuMortgageTypes.text")); // NOI18N
        menuMortgageTypes.setName("menuMortgageTypes"); // NOI18N
        menuAdministrative.add(menuMortgageTypes);

        menuRrrGroupTypes.setAction(actionMap.get("manageRrrGroupTypes")); // NOI18N
        menuRrrGroupTypes.setText(bundle.getString("MainForm.menuRrrGroupTypes.text")); // NOI18N
        menuRrrGroupTypes.setName("menuRrrGroupTypes"); // NOI18N
        menuAdministrative.add(menuRrrGroupTypes);

        menuRrrTypes.setAction(actionMap.get("manageRrrTypes")); // NOI18N
        menuRrrTypes.setText(bundle.getString("MainForm.menuRrrTypes.text")); // NOI18N
        menuRrrTypes.setName("menuRrrTypes"); // NOI18N
        menuAdministrative.add(menuRrrTypes);

        menuRefData.add(menuAdministrative);

        menuSources.setText(bundle.getString("MainForm.menuSources.text")); // NOI18N
        menuSources.setName("menuSources"); // NOI18N

        menuSourceTypes.setAction(actionMap.get("manageSourceTypes")); // NOI18N
        menuSourceTypes.setText(bundle.getString("MainForm.menuSourceTypes.text")); // NOI18N
        menuSourceTypes.setName("menuSourceTypes"); // NOI18N
        menuSources.add(menuSourceTypes);

        menuRefData.add(menuSources);

        menuParty.setText(bundle.getString("MainForm.menuParty.text")); // NOI18N
        menuParty.setName("menuParty"); // NOI18N

        menuCommunicationType.setAction(actionMap.get("manageCommunicationTypes")); // NOI18N
        menuCommunicationType.setText(bundle.getString("MainForm.menuCommunicationType.text")); // NOI18N
        menuCommunicationType.setName("menuCommunicationType"); // NOI18N
        menuParty.add(menuCommunicationType);

        menuIdTypes.setAction(actionMap.get("manageIdTypes")); // NOI18N
        menuIdTypes.setText(bundle.getString("MainForm.menuIdTypes.text")); // NOI18N
        menuIdTypes.setName("menuIdTypes"); // NOI18N
        menuParty.add(menuIdTypes);

        menuGenders.setAction(actionMap.get("manageGender")); // NOI18N
        menuGenders.setText(bundle.getString("MainForm.menuGenders.text")); // NOI18N
        menuGenders.setName("menuGenders"); // NOI18N
        menuParty.add(menuGenders);

        menuPartyRoleType.setAction(actionMap.get("managePartyRoleTypes")); // NOI18N
        menuPartyRoleType.setText(bundle.getString("MainForm.menuPartyRoleType.text")); // NOI18N
        menuPartyRoleType.setName("menuPartyRoleType"); // NOI18N
        menuParty.add(menuPartyRoleType);

        menuPartyType.setAction(actionMap.get("managePartyTypes")); // NOI18N
        menuPartyType.setText(bundle.getString("MainForm.menuPartyType.text")); // NOI18N
        menuPartyType.setName("menuPartyType"); // NOI18N
        menuParty.add(menuPartyType);

        menuRefData.add(menuParty);

        menuSystem.setText(bundle.getString("MainForm.menuSystem.text")); // NOI18N
        menuSystem.setName("menuSystem"); // NOI18N

        menuBRSeverityType.setAction(actionMap.get("manageBRSeverityTypes")); // NOI18N
        menuBRSeverityType.setText(bundle.getString("MainForm.menuBRSeverityType.text")); // NOI18N
        menuBRSeverityType.setName("menuBRSeverityType"); // NOI18N
        menuSystem.add(menuBRSeverityType);

        menuBRValidationTargetType.setAction(actionMap.get("manageBRValidationTargetTypes")); // NOI18N
        menuBRValidationTargetType.setText(bundle.getString("MainForm.menuBRValidationTargetType.text")); // NOI18N
        menuBRValidationTargetType.setName("menuBRValidationTargetType"); // NOI18N
        menuSystem.add(menuBRValidationTargetType);

        menuBRTechnicalType.setAction(actionMap.get("manageBRTechnicalTypes")); // NOI18N
        menuBRTechnicalType.setText(bundle.getString("MainForm.menuBRTechnicalType.text")); // NOI18N
        menuBRTechnicalType.setName("menuBRTechnicalType"); // NOI18N
        menuSystem.add(menuBRTechnicalType);

        menuRefData.add(menuSystem);

        menuTransaction.setText(bundle.getString("MainForm.menuTransaction.text")); // NOI18N
        menuTransaction.setName("menuTransaction"); // NOI18N

        menuRegistrationStatusType.setAction(actionMap.get("manageRegistrationStatusTypes")); // NOI18N
        menuRegistrationStatusType.setText(bundle.getString("MainForm.menuRegistrationStatusType.text")); // NOI18N
        menuRegistrationStatusType.setName("menuRegistrationStatusType"); // NOI18N
        menuTransaction.add(menuRegistrationStatusType);

        menuRefData.add(menuTransaction);

        mainMenu.add(menuRefData);

        menuReports.setText(bundle.getString("MainForm.menuReports.text_1")); // NOI18N
        menuReports.setName("menuReports"); // NOI18N

        menuLodgementReport.setText(bundle.getString("MainForm.menuLodgementReport.text_1")); // NOI18N
        menuLodgementReport.setName("menuLodgementReport"); // NOI18N
        menuLodgementReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLodgementReportActionPerformed(evt);
            }
        });
        menuReports.add(menuLodgementReport);

        menuTimeReport.setText(bundle.getString("MainForm.menuTimeReport.text_1")); // NOI18N
        menuTimeReport.setName("menuTimeReport"); // NOI18N
        menuTimeReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuTimeReportActionPerformed(evt);
            }
        });
        menuReports.add(menuTimeReport);

        mainMenu.add(menuReports);

        menuHelp.setText(bundle.getString("MainForm.menuHelp.text")); // NOI18N
        menuHelp.setName("menuHelp"); // NOI18N
        mainMenu.add(menuHelp);

        setJMenuBar(mainMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE)
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 /** Opens {@link ReportViewerForm} to display report.*/
    private void showReport(JasperPrint report) {
        ReportViewerForm form = new ReportViewerForm(report);
        form.setVisible(true);
    }
    private void menuLodgementReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLodgementReportActionPerformed

        showReport(ReportManager.getBrReport());     }//GEN-LAST:event_menuLodgementReportActionPerformed

    private void menuTimeReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuTimeReportActionPerformed

        showReport(ReportManager.getBrValidaction());     }//GEN-LAST:event_menuTimeReportActionPerformed

    private void menuBaUnitRelationTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBaUnitRelationTypesActionPerformed
        openReferenceDataPanel(BaUnitRelTypeBean.class, menuBaUnitRelationTypes.getText());
    }//GEN-LAST:event_menuBaUnitRelationTypesActionPerformed

    /** Opens roles management panel. */
    @Action
    public void manageRoles() {
        RolesManagementPanel panel = new RolesManagementPanel();
        mainScrollPane.setViewportView(panel);
    }

    /** Opens groups management panel. */
    @Action
    public void manageGroups() {
        GroupsManagementPanel groupManagementPanel = new GroupsManagementPanel();
        mainScrollPane.setViewportView(groupManagementPanel);
    }

    /** Opens users management panel. */
    @Action
    public void manageUsers() {
        UsersManagementPanel panel = new UsersManagementPanel();
        mainScrollPane.setViewportView(panel);
    }

    @Action
    public void manageLanguages() {
        JOptionPane.showMessageDialog(this, "Not yet implemented.");
    }

    @Action
    public void manageSystemSettings() {
        JOptionPane.showMessageDialog(this, "Not yet implemented.");
    }

    @Action
    public void manageGisSettings() {
        JOptionPane.showMessageDialog(this, "Not yet implemented.");
    }

    @Action
    public void manageCommunicationTypes() {
        openReferenceDataPanel(CommunicationTypeBean.class, 
                menuCommunicationType.getText());
    }

    @Action
    public void manageBAUnitType() {
        openReferenceDataPanel(BaUnitTypeBean.class, menuBaUnitType.getText());
    }

    @Action
    public void manageIdTypes() {
        openReferenceDataPanel(IdTypeBean.class, menuIdTypes.getText());
    }

    @Action
    public void manageGender() {
        openReferenceDataPanel(GenderTypeBean.class, menuGenders.getText());
    }

    @Action
    public void managePartyRoleTypes() {
        openReferenceDataPanel(PartyRoleTypeBean.class, menuPartyRoleType.getText());
    }

    @Action
    public void managePartyTypes() {
        openReferenceDataPanel(PartyTypeBean.class, menuPartyType.getText());
    }

    @Action
    public void manageMortgageTypes() {
        openReferenceDataPanel(MortgageTypeBean.class, menuMortgageTypes.getText());
    }

    @Action
    public void manageRrrGroupTypes() {
        openReferenceDataPanel(RrrGroupTypeBean.class, menuRrrGroupTypes.getText());
    }

    @Action
    public void manageRrrTypes() {
        openReferenceDataPanel(RrrTypeBean.class, menuRrrTypes.getText());
    }

    @Action
    public void manageSourceTypes() {
        openReferenceDataPanel(SourceTypeBean.class, menuSourceTypes.getText());
    }

    @Action
    public void manageRequestTypes() {
        openReferenceDataPanel(RequestTypeBean.class, menuRequestTypes.getText());
    }

    @Action
    public void manageTypeActions() {
        openReferenceDataPanel(TypeActionBean.class, menuTypeActions.getText());
    }

    @Action
    public void manageServiceActionTypes() {
        openReferenceDataPanel(ServiceActionTypeBean.class, menuServiceActionTypes.getText());
    }

    @Action
    public void manageServiceStatusTypes() {
        openReferenceDataPanel(ServiceStatusTypeBean.class, menuServiceStatusTypes.getText());
    }

    @Action
    public void exit() {
        System.exit(0);
    }

    @Action
    public void manageRequestCategories() {
        openReferenceDataPanel(RequestCategoryTypeBean.class, menuRequestCategory.getText());
    }

    @Action
    public void manageRegistrationStatusTypes() {
        openReferenceDataPanel(RegistrationStatusTypeBean.class, menuRegistrationStatusType.getText());
    }

    @Action
    public void manageBRSeverityTypes() {
        openReferenceDataPanel(BrSeverityTypeBean.class, menuBRSeverityType.getText());
    }

    @Action
    public void manageBRValidationTargetTypes() {
        openReferenceDataPanel(BrValidationTargetTypeBean.class, menuBRValidationTargetType.getText());
    }

    @Action
    public void manageBRTechnicalTypes() {
        openReferenceDataPanel(BrTechnicalTypeBean.class, menuBRTechnicalType.getText());
    }

    @Action
    public void manageBr() {
        BrManagementPanel panel = new BrManagementPanel();
        mainScrollPane.setViewportView(panel);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBr;
    private javax.swing.JButton btnGISSettings;
    private javax.swing.JButton btnGroups;
    private javax.swing.JButton btnLanguage;
    private javax.swing.JButton btnRoles;
    private javax.swing.JButton btnSystemSettings;
    private javax.swing.JButton btnUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane mainScrollPane;
    private javax.swing.JToolBar mainToolbar;
    private javax.swing.JMenu menuAdministrative;
    private javax.swing.JMenu menuApplications;
    private javax.swing.JMenuItem menuBRSeverityType;
    private javax.swing.JMenuItem menuBRTechnicalType;
    private javax.swing.JMenuItem menuBRValidationTargetType;
    private javax.swing.JMenuItem menuBaUnitRelationTypes;
    private javax.swing.JMenuItem menuBaUnitType;
    private javax.swing.JMenuItem menuCommunicationType;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuGenders;
    private javax.swing.JMenuItem menuGroups;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuIdTypes;
    private javax.swing.JMenuItem menuLodgementReport;
    private javax.swing.JMenuItem menuMortgageTypes;
    private javax.swing.JMenu menuParty;
    private javax.swing.JMenuItem menuPartyRoleType;
    private javax.swing.JMenuItem menuPartyType;
    private javax.swing.JMenu menuRefData;
    private javax.swing.JMenuItem menuRegistrationStatusType;
    private javax.swing.JMenu menuReports;
    private javax.swing.JMenuItem menuRequestCategory;
    private javax.swing.JMenuItem menuRequestTypes;
    private javax.swing.JMenuItem menuRoles;
    private javax.swing.JMenuItem menuRrrGroupTypes;
    private javax.swing.JMenuItem menuRrrTypes;
    private javax.swing.JMenu menuSecurity;
    private javax.swing.JMenuItem menuServiceActionTypes;
    private javax.swing.JMenuItem menuServiceStatusTypes;
    private javax.swing.JMenuItem menuSourceTypes;
    private javax.swing.JMenu menuSources;
    private javax.swing.JMenu menuSystem;
    private javax.swing.JMenuItem menuTimeReport;
    private javax.swing.JMenu menuTransaction;
    private javax.swing.JMenuItem menuTypeActions;
    private javax.swing.JMenuItem menuUsers;
    private javax.swing.JPanel statusPanel;
    private org.sola.clients.swing.common.tasks.TaskPanel taskPanel1;
    // End of variables declaration//GEN-END:variables
}
