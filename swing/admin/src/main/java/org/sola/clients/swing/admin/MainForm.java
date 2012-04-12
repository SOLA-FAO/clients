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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperPrint;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.referencedata.*;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.admin.referencedata.ReferenceDataManagementPanel;
import org.sola.clients.swing.admin.security.GroupsManagementPanel;
import org.sola.clients.swing.admin.security.RolesManagementPanel;
import org.sola.clients.swing.admin.security.UsersManagementPanel;
import org.sola.clients.swing.admin.system.BrManagementPanel;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.ui.MainContentPanel;
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
        customizeForm();
    }

    /** Customizes main form regarding user access rights. */
    private void customizeForm(){
        boolean hasSecurityRole = SecurityBean.isInRole(RolesConstants.ADMIN_MANAGE_SECURITY);
        boolean hasRefdataRole = SecurityBean.isInRole(RolesConstants.ADMIN_MANAGE_REFDATA);
        boolean hasSettingsRole = SecurityBean.isInRole(RolesConstants.ADMIN_MANAGE_SETTINGS);
        boolean hasBRRole = SecurityBean.isInRole(RolesConstants.ADMIN_MANAGE_BR);
        
        btnRoles.setEnabled(hasSecurityRole);
        btnUsers.setEnabled(hasSecurityRole);
        btnGroups.setEnabled(hasSecurityRole);
        menuRoles.setEnabled(btnRoles.isEnabled());
        menuUsers.setEnabled(btnUsers.isEnabled());
        menuGroups.setEnabled(btnGroups.isEnabled());
        
        btnSystemSettings.setEnabled(hasSettingsRole);
        btnGISSettings.setEnabled(hasSettingsRole);
        btnLanguage.setEnabled(hasSettingsRole);
        btnBr.setEnabled(hasBRRole);
        
        menuRefData.setEnabled(hasRefdataRole);
    }
    
    /** Opens reference data management panel for different reference data type.*/
    private <T extends AbstractCodeBean> void openReferenceDataPanel(
            Class<T> refDataClass, String headerTitle){
        ReferenceDataManagementPanel panel = new ReferenceDataManagementPanel(refDataClass, headerTitle);
        mainContentPanel.addPanel(panel, MainContentPanel.CARD_ADMIN_REFDATA_MANAGE, true);
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
        statusPanel = new javax.swing.JPanel();
        taskPanel1 = new org.sola.clients.swing.common.tasks.TaskPanel();
        jLabel1 = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainContentPanel = new org.sola.clients.swing.ui.MainContentPanel();
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

        btnRoles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/roles.png"))); // NOI18N
        btnRoles.setText(bundle.getString("MainForm.btnRoles.text")); // NOI18N
        btnRoles.setFocusable(false);
        btnRoles.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRoles.setName("btnRoles"); // NOI18N
        btnRoles.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRoles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRolesActionPerformed(evt);
            }
        });
        mainToolbar.add(btnRoles);

        btnGroups.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/group.png"))); // NOI18N
        btnGroups.setText(bundle.getString("MainForm.btnGroups.text")); // NOI18N
        btnGroups.setFocusable(false);
        btnGroups.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGroups.setName("btnGroups"); // NOI18N
        btnGroups.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGroupsActionPerformed(evt);
            }
        });
        mainToolbar.add(btnGroups);

        btnUsers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/user.png"))); // NOI18N
        btnUsers.setText(bundle.getString("MainForm.btnUsers.text")); // NOI18N
        btnUsers.setFocusable(false);
        btnUsers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUsers.setName("btnUsers"); // NOI18N
        btnUsers.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsersActionPerformed(evt);
            }
        });
        mainToolbar.add(btnUsers);

        jSeparator1.setName("jSeparator1"); // NOI18N
        mainToolbar.add(jSeparator1);

        btnLanguage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/language.png"))); // NOI18N
        btnLanguage.setText(bundle.getString("MainForm.btnLanguage.text")); // NOI18N
        btnLanguage.setFocusable(false);
        btnLanguage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLanguage.setName("btnLanguage"); // NOI18N
        btnLanguage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLanguageActionPerformed(evt);
            }
        });
        mainToolbar.add(btnLanguage);

        jSeparator2.setName("jSeparator2"); // NOI18N
        mainToolbar.add(jSeparator2);

        btnSystemSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/settings.png"))); // NOI18N
        btnSystemSettings.setText(bundle.getString("MainForm.btnSystemSettings.text")); // NOI18N
        btnSystemSettings.setFocusable(false);
        btnSystemSettings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSystemSettings.setName("btnSystemSettings"); // NOI18N
        btnSystemSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSystemSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSystemSettingsActionPerformed(evt);
            }
        });
        mainToolbar.add(btnSystemSettings);

        btnGISSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/map-pencil.png"))); // NOI18N
        btnGISSettings.setText(bundle.getString("MainForm.btnGISSettings.text")); // NOI18N
        btnGISSettings.setFocusable(false);
        btnGISSettings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGISSettings.setName("btnGISSettings"); // NOI18N
        btnGISSettings.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGISSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGISSettingsActionPerformed(evt);
            }
        });
        mainToolbar.add(btnGISSettings);

        jSeparator3.setName("jSeparator3"); // NOI18N
        mainToolbar.add(jSeparator3);

        btnBr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/traffic-light.png"))); // NOI18N
        btnBr.setText(bundle.getString("MainForm.btnBr.text")); // NOI18N
        btnBr.setFocusable(false);
        btnBr.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBr.setName("btnBr"); // NOI18N
        btnBr.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnBr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrActionPerformed(evt);
            }
        });
        mainToolbar.add(btnBr);

        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusPanel.setName("statusPanel"); // NOI18N

        taskPanel1.setName("taskPanel1"); // NOI18N

        jLabel1.setFont(LafManager.getInstance().getLabFontBold());
        jLabel1.setText(bundle.getString("MainForm.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(taskPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(lblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(taskPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        mainContentPanel.setName("mainContentPanel"); // NOI18N
        jScrollPane1.setViewportView(mainContentPanel);

        mainMenu.setName("mainMenu"); // NOI18N

        menuFile.setText(bundle.getString("MainForm.menuFile.text")); // NOI18N
        menuFile.setName("menuFile"); // NOI18N

        menuExit.setText(bundle.getString("MainForm.menuExit.text")); // NOI18N
        menuExit.setName("menuExit"); // NOI18N
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuFile.add(menuExit);

        mainMenu.add(menuFile);

        menuSecurity.setText(bundle.getString("MainForm.menuSecurity.text")); // NOI18N
        menuSecurity.setName("menuSecurity"); // NOI18N
        menuSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSecurityActionPerformed(evt);
            }
        });

        menuRoles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/roles.png"))); // NOI18N
        menuRoles.setText(bundle.getString("MainForm.menuRoles.text")); // NOI18N
        menuRoles.setName("menuRoles"); // NOI18N
        menuRoles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRolesActionPerformed(evt);
            }
        });
        menuSecurity.add(menuRoles);

        menuGroups.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/group.png"))); // NOI18N
        menuGroups.setText(bundle.getString("MainForm.menuGroups.text")); // NOI18N
        menuGroups.setName("menuGroups"); // NOI18N
        menuGroups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGroupsActionPerformed(evt);
            }
        });
        menuSecurity.add(menuGroups);

        menuUsers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/user.png"))); // NOI18N
        menuUsers.setText(bundle.getString("MainForm.menuUsers.text")); // NOI18N
        menuUsers.setName("menuUsers"); // NOI18N
        menuSecurity.add(menuUsers);

        mainMenu.add(menuSecurity);

        menuRefData.setText(bundle.getString("MainForm.menuRefData.text")); // NOI18N
        menuRefData.setName("menuRefData"); // NOI18N

        menuApplications.setText(bundle.getString("MainForm.menuApplications.text")); // NOI18N
        menuApplications.setName("menuApplications"); // NOI18N

        menuRequestCategory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuRequestCategory.setText(bundle.getString("MainForm.menuRequestCategory.text")); // NOI18N
        menuRequestCategory.setName("menuRequestCategory"); // NOI18N
        menuRequestCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRequestCategoryActionPerformed(evt);
            }
        });
        menuApplications.add(menuRequestCategory);

        menuRequestTypes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuRequestTypes.setText(bundle.getString("MainForm.menuRequestTypes.text")); // NOI18N
        menuRequestTypes.setName("menuRequestTypes"); // NOI18N
        menuRequestTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRequestTypesActionPerformed(evt);
            }
        });
        menuApplications.add(menuRequestTypes);

        menuTypeActions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuTypeActions.setText(bundle.getString("MainForm.menuTypeActions.text")); // NOI18N
        menuTypeActions.setName("menuTypeActions"); // NOI18N
        menuTypeActions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuTypeActionsActionPerformed(evt);
            }
        });
        menuApplications.add(menuTypeActions);

        menuServiceActionTypes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuServiceActionTypes.setText(bundle.getString("MainForm.menuServiceActionTypes.text")); // NOI18N
        menuServiceActionTypes.setName("menuServiceActionTypes"); // NOI18N
        menuServiceActionTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuServiceActionTypesActionPerformed(evt);
            }
        });
        menuApplications.add(menuServiceActionTypes);

        menuServiceStatusTypes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuServiceStatusTypes.setText(bundle.getString("MainForm.menuServiceStatusTypes.text")); // NOI18N
        menuServiceStatusTypes.setName("menuServiceStatusTypes"); // NOI18N
        menuServiceStatusTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuServiceStatusTypesActionPerformed(evt);
            }
        });
        menuApplications.add(menuServiceStatusTypes);

        menuRefData.add(menuApplications);

        menuAdministrative.setText(bundle.getString("MainForm.menuAdministrative.text")); // NOI18N
        menuAdministrative.setName("menuAdministrative"); // NOI18N

        menuBaUnitType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuBaUnitType.setText(bundle.getString("MainForm.menuBaUnitType.text")); // NOI18N
        menuBaUnitType.setName("menuBaUnitType"); // NOI18N
        menuBaUnitType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBaUnitTypeActionPerformed(evt);
            }
        });
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

        menuMortgageTypes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuMortgageTypes.setText(bundle.getString("MainForm.menuMortgageTypes.text")); // NOI18N
        menuMortgageTypes.setName("menuMortgageTypes"); // NOI18N
        menuMortgageTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMortgageTypesActionPerformed(evt);
            }
        });
        menuAdministrative.add(menuMortgageTypes);

        menuRrrGroupTypes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuRrrGroupTypes.setText(bundle.getString("MainForm.menuRrrGroupTypes.text")); // NOI18N
        menuRrrGroupTypes.setName("menuRrrGroupTypes"); // NOI18N
        menuRrrGroupTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRrrGroupTypesActionPerformed(evt);
            }
        });
        menuAdministrative.add(menuRrrGroupTypes);

        menuRrrTypes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuRrrTypes.setText(bundle.getString("MainForm.menuRrrTypes.text")); // NOI18N
        menuRrrTypes.setName("menuRrrTypes"); // NOI18N
        menuRrrTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRrrTypesActionPerformed(evt);
            }
        });
        menuAdministrative.add(menuRrrTypes);

        menuRefData.add(menuAdministrative);

        menuSources.setText(bundle.getString("MainForm.menuSources.text")); // NOI18N
        menuSources.setName("menuSources"); // NOI18N

        menuSourceTypes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuSourceTypes.setText(bundle.getString("MainForm.menuSourceTypes.text")); // NOI18N
        menuSourceTypes.setName("menuSourceTypes"); // NOI18N
        menuSourceTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSourceTypesActionPerformed(evt);
            }
        });
        menuSources.add(menuSourceTypes);

        menuRefData.add(menuSources);

        menuParty.setText(bundle.getString("MainForm.menuParty.text")); // NOI18N
        menuParty.setName("menuParty"); // NOI18N

        menuCommunicationType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuCommunicationType.setText(bundle.getString("MainForm.menuCommunicationType.text")); // NOI18N
        menuCommunicationType.setName("menuCommunicationType"); // NOI18N
        menuCommunicationType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCommunicationTypeActionPerformed(evt);
            }
        });
        menuParty.add(menuCommunicationType);

        menuIdTypes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuIdTypes.setText(bundle.getString("MainForm.menuIdTypes.text")); // NOI18N
        menuIdTypes.setName("menuIdTypes"); // NOI18N
        menuIdTypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuIdTypesActionPerformed(evt);
            }
        });
        menuParty.add(menuIdTypes);

        menuGenders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuGenders.setText(bundle.getString("MainForm.menuGenders.text")); // NOI18N
        menuGenders.setName("menuGenders"); // NOI18N
        menuGenders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGendersActionPerformed(evt);
            }
        });
        menuParty.add(menuGenders);

        menuPartyRoleType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuPartyRoleType.setText(bundle.getString("MainForm.menuPartyRoleType.text")); // NOI18N
        menuPartyRoleType.setName("menuPartyRoleType"); // NOI18N
        menuPartyRoleType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPartyRoleTypeActionPerformed(evt);
            }
        });
        menuParty.add(menuPartyRoleType);

        menuPartyType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuPartyType.setText(bundle.getString("MainForm.menuPartyType.text")); // NOI18N
        menuPartyType.setName("menuPartyType"); // NOI18N
        menuPartyType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPartyTypeActionPerformed(evt);
            }
        });
        menuParty.add(menuPartyType);

        menuRefData.add(menuParty);

        menuSystem.setText(bundle.getString("MainForm.menuSystem.text")); // NOI18N
        menuSystem.setName("menuSystem"); // NOI18N

        menuBRSeverityType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuBRSeverityType.setText(bundle.getString("MainForm.menuBRSeverityType.text")); // NOI18N
        menuBRSeverityType.setName("menuBRSeverityType"); // NOI18N
        menuBRSeverityType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBRSeverityTypeActionPerformed(evt);
            }
        });
        menuSystem.add(menuBRSeverityType);

        menuBRValidationTargetType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuBRValidationTargetType.setText(bundle.getString("MainForm.menuBRValidationTargetType.text")); // NOI18N
        menuBRValidationTargetType.setName("menuBRValidationTargetType"); // NOI18N
        menuBRValidationTargetType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBRValidationTargetTypeActionPerformed(evt);
            }
        });
        menuSystem.add(menuBRValidationTargetType);

        menuBRTechnicalType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuBRTechnicalType.setText(bundle.getString("MainForm.menuBRTechnicalType.text")); // NOI18N
        menuBRTechnicalType.setName("menuBRTechnicalType"); // NOI18N
        menuBRTechnicalType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBRTechnicalTypeActionPerformed(evt);
            }
        });
        menuSystem.add(menuBRTechnicalType);

        menuRefData.add(menuSystem);

        menuTransaction.setText(bundle.getString("MainForm.menuTransaction.text")); // NOI18N
        menuTransaction.setName("menuTransaction"); // NOI18N

        menuRegistrationStatusType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/book-open.png"))); // NOI18N
        menuRegistrationStatusType.setText(bundle.getString("MainForm.menuRegistrationStatusType.text")); // NOI18N
        menuRegistrationStatusType.setName("menuRegistrationStatusType"); // NOI18N
        menuRegistrationStatusType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRegistrationStatusTypeActionPerformed(evt);
            }
        });
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
            .addComponent(mainToolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
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

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuExitActionPerformed

    private void menuRolesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRolesActionPerformed
        manageRoles();
    }//GEN-LAST:event_menuRolesActionPerformed

    private void btnRolesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRolesActionPerformed
        manageRoles();
    }//GEN-LAST:event_btnRolesActionPerformed

    private void menuGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGroupsActionPerformed
        manageGroups();
    }//GEN-LAST:event_menuGroupsActionPerformed

    private void btnGroupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGroupsActionPerformed
        manageGroups();
    }//GEN-LAST:event_btnGroupsActionPerformed

    private void menuSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSecurityActionPerformed
        manageUsers();
    }//GEN-LAST:event_menuSecurityActionPerformed

    private void btnUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsersActionPerformed
        manageUsers();
    }//GEN-LAST:event_btnUsersActionPerformed

    private void menuRequestCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRequestCategoryActionPerformed
        manageRequestCategories();
    }//GEN-LAST:event_menuRequestCategoryActionPerformed

    private void menuRequestTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRequestTypesActionPerformed
        manageRequestTypes();
    }//GEN-LAST:event_menuRequestTypesActionPerformed

    private void menuTypeActionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuTypeActionsActionPerformed
        manageTypeActions();
    }//GEN-LAST:event_menuTypeActionsActionPerformed

    private void menuServiceActionTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuServiceActionTypesActionPerformed
        manageServiceActionTypes();
    }//GEN-LAST:event_menuServiceActionTypesActionPerformed

    private void menuServiceStatusTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuServiceStatusTypesActionPerformed
        manageServiceStatusTypes();
    }//GEN-LAST:event_menuServiceStatusTypesActionPerformed

    private void menuBaUnitTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBaUnitTypeActionPerformed
        manageBAUnitType();
    }//GEN-LAST:event_menuBaUnitTypeActionPerformed

    private void menuMortgageTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMortgageTypesActionPerformed
        manageMortgageTypes();
    }//GEN-LAST:event_menuMortgageTypesActionPerformed

    private void menuRrrGroupTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRrrGroupTypesActionPerformed
        manageRrrGroupTypes();
    }//GEN-LAST:event_menuRrrGroupTypesActionPerformed

    private void menuRrrTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRrrTypesActionPerformed
        manageRrrTypes();
    }//GEN-LAST:event_menuRrrTypesActionPerformed

    private void menuSourceTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSourceTypesActionPerformed
        manageSourceTypes();
    }//GEN-LAST:event_menuSourceTypesActionPerformed

    private void menuCommunicationTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCommunicationTypeActionPerformed
        manageCommunicationTypes();
    }//GEN-LAST:event_menuCommunicationTypeActionPerformed

    private void menuIdTypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuIdTypesActionPerformed
        manageIdTypes();
    }//GEN-LAST:event_menuIdTypesActionPerformed

    private void menuGendersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGendersActionPerformed
        manageGender();
    }//GEN-LAST:event_menuGendersActionPerformed

    private void menuPartyRoleTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPartyRoleTypeActionPerformed
        managePartyRoleTypes();
    }//GEN-LAST:event_menuPartyRoleTypeActionPerformed

    private void menuPartyTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPartyTypeActionPerformed
        managePartyTypes();
    }//GEN-LAST:event_menuPartyTypeActionPerformed

    private void menuBRSeverityTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBRSeverityTypeActionPerformed
        manageBRSeverityTypes();
    }//GEN-LAST:event_menuBRSeverityTypeActionPerformed

    private void menuBRValidationTargetTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBRValidationTargetTypeActionPerformed
        manageBRValidationTargetTypes();
    }//GEN-LAST:event_menuBRValidationTargetTypeActionPerformed

    private void menuBRTechnicalTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBRTechnicalTypeActionPerformed
        manageBRTechnicalTypes();
    }//GEN-LAST:event_menuBRTechnicalTypeActionPerformed

    private void btnLanguageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLanguageActionPerformed
        manageLanguages();
    }//GEN-LAST:event_btnLanguageActionPerformed

    private void btnSystemSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSystemSettingsActionPerformed
        manageSystemSettings();
    }//GEN-LAST:event_btnSystemSettingsActionPerformed

    private void btnGISSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGISSettingsActionPerformed
        manageGisSettings();
    }//GEN-LAST:event_btnGISSettingsActionPerformed

    private void btnBrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrActionPerformed
        manageBr();
    }//GEN-LAST:event_btnBrActionPerformed

    private void menuRegistrationStatusTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRegistrationStatusTypeActionPerformed
        manageRegistrationStatusTypes();
    }//GEN-LAST:event_menuRegistrationStatusTypeActionPerformed

    /** Opens roles management panel. */
    private void manageRoles() {
        if(mainContentPanel.isPanelOpened(MainContentPanel.CARD_ADMIN_ROLES_MANAGE)){
            mainContentPanel.showPanel(MainContentPanel.CARD_ADMIN_ROLES_MANAGE);
        }else{
            RolesManagementPanel panel = new RolesManagementPanel();
            mainContentPanel.addPanel(panel, MainContentPanel.CARD_ADMIN_ROLES_MANAGE, true);
        }
    }

    /** Opens groups management panel. */
    private void manageGroups() {
        if(mainContentPanel.isPanelOpened(MainContentPanel.CARD_ADMIN_GROUP_MANAGE)){
            mainContentPanel.showPanel(MainContentPanel.CARD_ADMIN_GROUP_MANAGE);
        }else{
            GroupsManagementPanel groupManagementPanel = new GroupsManagementPanel();
            mainContentPanel.addPanel(groupManagementPanel, MainContentPanel.CARD_ADMIN_GROUP_MANAGE, true);
        }
    }

    /** Opens users management panel. */
    private void manageUsers() {
        if(mainContentPanel.isPanelOpened(MainContentPanel.CARD_ADMIN_USER_MANAGE)){
            mainContentPanel.showPanel(MainContentPanel.CARD_ADMIN_USER_MANAGE);
        }else{
            UsersManagementPanel panel = new UsersManagementPanel();
            mainContentPanel.addPanel(panel, MainContentPanel.CARD_ADMIN_USER_MANAGE, true);
        }
    }

    private void manageLanguages() {
        JOptionPane.showMessageDialog(this, "Not yet implemented.");
    }

    private void manageSystemSettings() {
        JOptionPane.showMessageDialog(this, "Not yet implemented.");
    }

    private void manageGisSettings() {
        JOptionPane.showMessageDialog(this, "Not yet implemented.");
    }

    private void manageCommunicationTypes() {
        openReferenceDataPanel(CommunicationTypeBean.class, 
                menuCommunicationType.getText());
    }

    private void manageBAUnitType() {
        openReferenceDataPanel(BaUnitTypeBean.class, menuBaUnitType.getText());
    }

    private void manageIdTypes() {
        openReferenceDataPanel(IdTypeBean.class, menuIdTypes.getText());
    }

    private void manageGender() {
        openReferenceDataPanel(GenderTypeBean.class, menuGenders.getText());
    }

    private void managePartyRoleTypes() {
        openReferenceDataPanel(PartyRoleTypeBean.class, menuPartyRoleType.getText());
    }

    private void managePartyTypes() {
        openReferenceDataPanel(PartyTypeBean.class, menuPartyType.getText());
    }

    private void manageMortgageTypes() {
        openReferenceDataPanel(MortgageTypeBean.class, menuMortgageTypes.getText());
    }

    private void manageRrrGroupTypes() {
        openReferenceDataPanel(RrrGroupTypeBean.class, menuRrrGroupTypes.getText());
    }

    private void manageRrrTypes() {
        openReferenceDataPanel(RrrTypeBean.class, menuRrrTypes.getText());
    }

    private void manageSourceTypes() {
        openReferenceDataPanel(SourceTypeBean.class, menuSourceTypes.getText());
    }

    private void manageRequestTypes() {
        openReferenceDataPanel(RequestTypeBean.class, menuRequestTypes.getText());
    }

    private void manageTypeActions() {
        openReferenceDataPanel(TypeActionBean.class, menuTypeActions.getText());
    }

    private void manageServiceActionTypes() {
        openReferenceDataPanel(ServiceActionTypeBean.class, menuServiceActionTypes.getText());
    }

    private void manageServiceStatusTypes() {
        openReferenceDataPanel(ServiceStatusTypeBean.class, menuServiceStatusTypes.getText());
    }

    private void manageRequestCategories() {
        openReferenceDataPanel(RequestCategoryTypeBean.class, menuRequestCategory.getText());
    }

    private void manageRegistrationStatusTypes() {
        openReferenceDataPanel(RegistrationStatusTypeBean.class, menuRegistrationStatusType.getText());
    }

    private void manageBRSeverityTypes() {
        openReferenceDataPanel(BrSeverityTypeBean.class, menuBRSeverityType.getText());
    }

    private void manageBRValidationTargetTypes() {
        openReferenceDataPanel(BrValidationTargetTypeBean.class, menuBRValidationTargetType.getText());
    }

    private void manageBRTechnicalTypes() {
        openReferenceDataPanel(BrTechnicalTypeBean.class, menuBRTechnicalType.getText());
    }

    private void manageBr() {
        if(mainContentPanel.isPanelOpened(MainContentPanel.CARD_ADMIN_BR_MANAGE)){
            mainContentPanel.showPanel(MainContentPanel.CARD_ADMIN_BR_MANAGE);
        }else{
            BrManagementPanel panel = new BrManagementPanel();
            mainContentPanel.addPanel(panel, MainContentPanel.CARD_ADMIN_BR_MANAGE, true);
        }
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JLabel lblUserName;
    private org.sola.clients.swing.ui.MainContentPanel mainContentPanel;
    private javax.swing.JMenuBar mainMenu;
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
