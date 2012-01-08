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

import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jdesktop.application.Action;
import org.sola.clients.swing.desktop.application.ApplicationAssignmentForm;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationSummaryBean;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;
import org.sola.clients.swing.ui.renderers.ServicesListRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import java.util.Locale;
import org.jdesktop.application.Task;
import org.sola.clients.swing.desktop.application.ApplicationForm;
import org.sola.clients.beans.application.ApplicationSearchResultBean;
import org.sola.clients.beans.application.ApplicationSearchResultsListBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.RolesConstants;

/** 
 * This panel displays assigned and unassigned applications.<br /> 
 * {@link ApplicationSummaryListBean} is used to bind the data on the panel.
 */
public class DashBoardPanel extends javax.swing.JPanel {

    private ApplicationAssignmentForm applicationAssignmentForm;
    private ApplicationForm applicationForm;
    private java.awt.Frame mainForm;

    /** 
     * Panel constructor.
     * @param mainForm Parent form.
     */
    public DashBoardPanel(java.awt.Frame mainForm) {
        this.mainForm = mainForm;
        initComponents();
        postInit();
    }

    /** Runs post initialization tasks to add listeners. */
    private void postInit() {
        btnRefreshAssigned.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_VIEW_APPS));
        btnRefreshUnassigned.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_VIEW_APPS));
        
        refreshApplications();
        customizeAssignedAppButtons(null);
        customizeUnassignedAppButtons(null);

        assignedAppListBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ApplicationSearchResultsListBean.SELECTED_APPLICATION_PROPERTY)) {
                    customizeAssignedAppButtons((ApplicationSearchResultBean) evt.getNewValue());
                }
            }
        });

        unassignedAppListBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ApplicationSearchResultsListBean.SELECTED_APPLICATION_PROPERTY)) {
                    customizeUnassignedAppButtons((ApplicationSearchResultBean) evt.getNewValue());
                }
            }
        });
    }

    /** Enables or disables toolbar buttons for assigned applications list, . */
    private void customizeAssignedAppButtons(ApplicationSearchResultBean app) {
        boolean isUnassignEnabled = true;
        boolean isEditEnabled = true;

        if (app == null) {
            isUnassignEnabled = false;
            isEditEnabled = false;
        } else {
            if (SecurityBean.getCurrentUser().getId().equals(app.getAssigneeId())) {
                isUnassignEnabled = SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_YOURSELF);
            } else {
                isUnassignEnabled = SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_OTHERS);
            }
            isEditEnabled = SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS);
        }

        btnUnassignApplication.getAction().setEnabled(isUnassignEnabled);
        btnEditAssignedApplication.getAction().setEnabled(isEditEnabled);
    }

    /** Enables or disables toolbar buttons for unassigned applications list. */
    private void customizeUnassignedAppButtons(ApplicationSearchResultBean app) {
        boolean isAssignEnabled = true;
        boolean isEditEnabled = true;

        if (app == null) {
            isAssignEnabled = false;
            isEditEnabled = false;
        } else {
            if(SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_YOURSELF) ||
                    SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_OTHERS)){
                isAssignEnabled = true;
            }else{
                isAssignEnabled = false;
            }
            isEditEnabled = SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS);
        }
        
        btnAssignApplication.getAction().setEnabled(isAssignEnabled);
        btnEditUnassignedApplication.getAction().setEnabled(isEditEnabled);
    }

    /** 
     * Opens application assignment form with selected application ID. 
     * @param appBean Selected application summary bean.
     */
    private void openAssignmentForm(ApplicationSummaryBean appBean) {
        if (appBean == null) {
            return;
        }

        if (!appBean.isFeePaid()) {
            MessageUtility.displayMessage(ClientMessage.CHECK_FEES_NOT_PAID);
            return;
        }

        if (applicationAssignmentForm != null) {
            applicationAssignmentForm.dispose();
        }

        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                refreshApplications();
            }
        };

        applicationAssignmentForm = new ApplicationAssignmentForm(mainForm, true, appBean.getId());
        applicationAssignmentForm.setLocationRelativeTo(mainForm);
        applicationAssignmentForm.addPropertyChangeListener(ApplicationBean.ASSIGNEE_ID_PROPERTY, listener);
        DesktopApplication.getApplication().show(applicationAssignmentForm);
        applicationAssignmentForm.removePropertyChangeListener(ApplicationBean.ASSIGNEE_ID_PROPERTY, listener);
    }

    /** 
     * Opens application form. 
     * @param appBean Selected application summary bean.
     */
    private void openApplication(final ApplicationSummaryBean appBean) {
        if (appBean == null) {
            return;
        }

        Task t = new Task(DesktopApplication.getApplication()) {

            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Opening application form.");
                if (applicationForm != null) {
                    applicationForm.dispose();
                }

                PropertyChangeListener listener = new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        refreshApplications();
                    }
                };

                applicationForm = new ApplicationForm(appBean.getId());
                applicationForm.setLocationRelativeTo(mainForm);
                applicationForm.addPropertyChangeListener(ApplicationBean.APPLICATION_PROPERTY, listener);

                DesktopApplication.getApplication().show(applicationForm);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        assignedAppListBean = new org.sola.clients.beans.application.ApplicationSearchResultsListBean();
        unassignedAppListBean = new org.sola.clients.beans.application.ApplicationSearchResultsListBean();
        popUpUnassignedApplications = new javax.swing.JPopupMenu();
        menuAssignApplication = new javax.swing.JMenuItem();
        menuEditUnassignedApplication = new javax.swing.JMenuItem();
        menuRefreshUnassignApplication = new javax.swing.JMenuItem();
        popUpAssignedApplications = new javax.swing.JPopupMenu();
        menuUnassignApplication = new javax.swing.JMenuItem();
        menuEditAssignedApplication = new javax.swing.JMenuItem();
        menuRefreshAssignApplication = new javax.swing.JMenuItem();
        inprogressScrollPanel = new javax.swing.JScrollPane();
        tbAssigned = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        unassignedScrollPanel = new javax.swing.JScrollPane();
        tbUnassigned = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        tbUnassignedApplications = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAssignApplication = new javax.swing.JButton();
        btnEditUnassignedApplication = new javax.swing.JButton();
        btnRefreshUnassigned = new javax.swing.JButton();
        tbAssignedApplications = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnUnassignApplication = new javax.swing.JButton();
        btnEditAssignedApplication = new javax.swing.JButton();
        btnRefreshAssigned = new javax.swing.JButton();

        popUpUnassignedApplications.setName("popUpUnassignedApplications"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(org.sola.clients.swing.desktop.DesktopApplication.class).getContext().getActionMap(DashBoardPanel.class, this);
        menuAssignApplication.setAction(actionMap.get("assignApplication")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        menuAssignApplication.setText(bundle.getString("DashBoardPanel.menuAssignApplication.text")); // NOI18N
        menuAssignApplication.setName("menuAssignApplication"); // NOI18N
        popUpUnassignedApplications.add(menuAssignApplication);

        menuEditUnassignedApplication.setAction(actionMap.get("editUnassignedApplication")); // NOI18N
        menuEditUnassignedApplication.setText(bundle.getString("DashBoardPanel.menuEditUnassignedApplication.text")); // NOI18N
        menuEditUnassignedApplication.setName("menuEditUnassignedApplication"); // NOI18N
        popUpUnassignedApplications.add(menuEditUnassignedApplication);

        menuRefreshUnassignApplication.setAction(actionMap.get("refreshUnassignedApplications")); // NOI18N
        menuRefreshUnassignApplication.setText(bundle.getString("DashBoardPanel.menuRefreshUnassignApplication.text")); // NOI18N
        menuRefreshUnassignApplication.setName("menuRefreshUnassignApplication"); // NOI18N
        popUpUnassignedApplications.add(menuRefreshUnassignApplication);

        popUpAssignedApplications.setName("popUpAssignedApplications"); // NOI18N

        menuUnassignApplication.setAction(actionMap.get("unassignApplication")); // NOI18N
        menuUnassignApplication.setText(bundle.getString("DashBoardPanel.menuUnassignApplication.text")); // NOI18N
        menuUnassignApplication.setToolTipText(bundle.getString("DashBoardPanel.menuUnassignApplication.toolTipText")); // NOI18N
        menuUnassignApplication.setName("menuUnassignApplication"); // NOI18N
        popUpAssignedApplications.add(menuUnassignApplication);

        menuEditAssignedApplication.setAction(actionMap.get("editAssignedApplication")); // NOI18N
        menuEditAssignedApplication.setText(bundle.getString("DashBoardPanel.menuEditAssignedApplication.text")); // NOI18N
        menuEditAssignedApplication.setToolTipText(bundle.getString("DashBoardPanel.menuEditAssignedApplication.toolTipText")); // NOI18N
        menuEditAssignedApplication.setName("menuEditAssignedApplication"); // NOI18N
        popUpAssignedApplications.add(menuEditAssignedApplication);

        menuRefreshAssignApplication.setAction(actionMap.get("refreshUnassignedApplications")); // NOI18N
        menuRefreshAssignApplication.setText(bundle.getString("DashBoardPanel.menuRefreshAssignApplication.text")); // NOI18N
        menuRefreshAssignApplication.setToolTipText(bundle.getString("DashBoardPanel.menuRefreshAssignApplication.toolTipText")); // NOI18N
        menuRefreshAssignApplication.setName("menuRefreshAssignApplication"); // NOI18N
        popUpAssignedApplications.add(menuRefreshAssignApplication);

        setMinimumSize(new java.awt.Dimension(100, 500));
        setName("Form"); // NOI18N

        inprogressScrollPanel.setName("inprogressScrollPanel"); // NOI18N
        inprogressScrollPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tbAssigned.setComponentPopupMenu(popUpAssignedApplications);
        tbAssigned.setGridColor(new java.awt.Color(135, 127, 115));
        tbAssigned.setName("tbAssigned"); // NOI18N
        tbAssigned.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbAssigned.setShowVerticalLines(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${applicationSearchResultsList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, assignedAppListBean, eLProperty, tbAssigned);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nr}"));
        columnBinding.setColumnName("Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${lodgingDatetime}"));
        columnBinding.setColumnName("Lodging Datetime");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${expectedCompletionDate}"));
        columnBinding.setColumnName("Expected Completion Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${serviceList}"));
        columnBinding.setColumnName("Service List");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${agent}"));
        columnBinding.setColumnName("Agent");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${assigneeName}"));
        columnBinding.setColumnName("Assignee Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status}"));
        columnBinding.setColumnName("Status");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, assignedAppListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedApplication}"), tbAssigned, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tbAssigned.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbAssignedMouseClicked(evt);
            }
        });
        inprogressScrollPanel.setViewportView(tbAssigned);
        tbAssigned.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tbAssigned.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title0")); // NOI18N
        tbAssigned.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title1")); // NOI18N
        tbAssigned.getColumnModel().getColumn(1).setCellRenderer(new DateTimeRenderer());
        tbAssigned.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title2")); // NOI18N
        tbAssigned.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
        tbAssigned.getColumnModel().getColumn(3).setMinWidth(180);
        tbAssigned.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("DashBoard.tbAssigned.columnModel.title6")); // NOI18N
        tbAssigned.getColumnModel().getColumn(3).setCellRenderer(new ServicesListRenderer());
        tbAssigned.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("DashBoard.tbAssigned.columnModel.title3")); // NOI18N
        tbAssigned.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title5")); // NOI18N
        tbAssigned.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("DashBoard.tbAssigned.columnModel.title4")); // NOI18N

        unassignedScrollPanel.setName("unassignedScrollPanel"); // NOI18N
        unassignedScrollPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tbUnassigned.setComponentPopupMenu(popUpUnassignedApplications);
        tbUnassigned.setGridColor(new java.awt.Color(135, 127, 115));
        tbUnassigned.setName("tbUnassigned"); // NOI18N
        tbUnassigned.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbUnassigned.setShowVerticalLines(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${applicationSearchResultsList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, unassignedAppListBean, eLProperty, tbUnassigned);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nr}"));
        columnBinding.setColumnName("Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${lodgingDatetime}"));
        columnBinding.setColumnName("Lodging Datetime");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${expectedCompletionDate}"));
        columnBinding.setColumnName("Expected Completion Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${serviceList}"));
        columnBinding.setColumnName("Service List");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${agent}"));
        columnBinding.setColumnName("Agent");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status}"));
        columnBinding.setColumnName("Status");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${feePaid}"));
        columnBinding.setColumnName("Fee Paid");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        jTableBinding.setSourceNullValue("");
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, unassignedAppListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedApplication}"), tbUnassigned, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tbUnassigned.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbUnassignedMouseClicked(evt);
            }
        });
        unassignedScrollPanel.setViewportView(tbUnassigned);
        tbUnassigned.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tbUnassigned.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title0")); // NOI18N
        tbUnassigned.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title1")); // NOI18N
        tbUnassigned.getColumnModel().getColumn(1).setCellRenderer(new DateTimeRenderer());
        tbUnassigned.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("DashBoard.tbUnassigned.columnModel.title3")); // NOI18N
        tbUnassigned.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
        tbUnassigned.getColumnModel().getColumn(3).setMinWidth(180);
        tbUnassigned.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("DashBoard.tbUnassigned.columnModel.title2_1")); // NOI18N
        tbUnassigned.getColumnModel().getColumn(3).setCellRenderer(new ServicesListRenderer());
        tbUnassigned.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("DashBoard.tbUnassigned.columnModel.title2")); // NOI18N
        tbUnassigned.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("DashBoard.tbUnassigned.columnModel.title4")); // NOI18N
        tbUnassigned.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title6")); // NOI18N

        tbUnassignedApplications.setFloatable(false);
        tbUnassignedApplications.setRollover(true);
        tbUnassignedApplications.setName("tbUnassignedApplications"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel1.setText(bundle.getString("DashBoardPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        tbUnassignedApplications.add(jLabel1);

        jSeparator1.setName("jSeparator1"); // NOI18N
        tbUnassignedApplications.add(jSeparator1);

        btnAssignApplication.setAction(actionMap.get("assignApplication")); // NOI18N
        btnAssignApplication.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnAssignApplication.setText(bundle.getString("DashBoardPanel.btnAssignApplication.text")); // NOI18N
        btnAssignApplication.setFocusable(false);
        btnAssignApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAssignApplication.setName("btnAssignApplication"); // NOI18N
        btnAssignApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbUnassignedApplications.add(btnAssignApplication);

        btnEditUnassignedApplication.setAction(actionMap.get("editUnassignedApplication")); // NOI18N
        btnEditUnassignedApplication.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnEditUnassignedApplication.setText(bundle.getString("DashBoardPanel.btnEditUnassignedApplication.text")); // NOI18N
        btnEditUnassignedApplication.setFocusable(false);
        btnEditUnassignedApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditUnassignedApplication.setName("btnEditUnassignedApplication"); // NOI18N
        btnEditUnassignedApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbUnassignedApplications.add(btnEditUnassignedApplication);

        btnRefreshUnassigned.setAction(actionMap.get("refreshUnassignedApplications")); // NOI18N
        btnRefreshUnassigned.setFont(new java.awt.Font("Arial", 0, 12));
        btnRefreshUnassigned.setText(bundle.getString("DashBoardPanel.btnRefreshUnassigned.text")); // NOI18N
        btnRefreshUnassigned.setFocusable(false);
        btnRefreshUnassigned.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRefreshUnassigned.setName("btnRefreshUnassigned"); // NOI18N
        btnRefreshUnassigned.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbUnassignedApplications.add(btnRefreshUnassigned);

        tbAssignedApplications.setFloatable(false);
        tbAssignedApplications.setRollover(true);
        tbAssignedApplications.setName("tbAssignedApplications"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Arial", 1, 12));
        jLabel2.setText(bundle.getString("DashBoardPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        tbAssignedApplications.add(jLabel2);

        jSeparator2.setName("jSeparator2"); // NOI18N
        tbAssignedApplications.add(jSeparator2);

        btnUnassignApplication.setAction(actionMap.get("unassignApplication")); // NOI18N
        btnUnassignApplication.setFont(new java.awt.Font("Arial", 0, 12));
        btnUnassignApplication.setFocusable(false);
        btnUnassignApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUnassignApplication.setName("btnUnassignApplication"); // NOI18N
        btnUnassignApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbAssignedApplications.add(btnUnassignApplication);

        btnEditAssignedApplication.setAction(actionMap.get("editAssignedApplication")); // NOI18N
        btnEditAssignedApplication.setFont(new java.awt.Font("Arial", 0, 12));
        btnEditAssignedApplication.setFocusable(false);
        btnEditAssignedApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditAssignedApplication.setName("btnEditAssignedApplication"); // NOI18N
        btnEditAssignedApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbAssignedApplications.add(btnEditAssignedApplication);

        btnRefreshAssigned.setAction(actionMap.get("refreshAssignedApplications")); // NOI18N
        btnRefreshAssigned.setFont(new java.awt.Font("Arial", 0, 12));
        btnRefreshAssigned.setFocusable(false);
        btnRefreshAssigned.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRefreshAssigned.setName("btnRefreshAssigned"); // NOI18N
        btnRefreshAssigned.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbAssignedApplications.add(btnRefreshAssigned);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, inprogressScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 984, Short.MAX_VALUE)
                    .add(tbUnassignedApplications, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 984, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, tbAssignedApplications, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 984, Short.MAX_VALUE)
                    .add(unassignedScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 984, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(11, 11, 11)
                .add(tbUnassignedApplications, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(unassignedScrollPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 226, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(15, 15, 15)
                .add(tbAssignedApplications, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(inprogressScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void tbUnassignedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbUnassignedMouseClicked
        if (evt.getClickCount() == 2 && btnEditUnassignedApplication.getAction().isEnabled()) {
            editUnassignedApplication();
        }
    }//GEN-LAST:event_tbUnassignedMouseClicked

    private void tbAssignedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAssignedMouseClicked
        if (evt.getClickCount() == 2 && btnEditAssignedApplication.getAction().isEnabled()) {
            openApplication(assignedAppListBean.getSelectedApplication());
        }
    }//GEN-LAST:event_tbAssignedMouseClicked

    /** Refreshes assigned and unassigned application lists. */
    private void refreshApplications() {
        Task t = new Task(DesktopApplication.getApplication()) {

            @Override
            protected Object doInBackground() throws Exception {
                setMessage(MessageUtility.getLocalizedMessage(
                        ClientMessage.APPLICATION_LOADING_UNASSIGNED).getMessage());
                unassignedAppListBean.FillUnassigned();
                setMessage(MessageUtility.getLocalizedMessage(
                        ClientMessage.APPLICATION_LOADING_ASSIGNED).getMessage());
                assignedAppListBean.FillAssigned();
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /** Opens application form for the selected application from unassigned list. */
    @Action
    public void editUnassignedApplication() {
        openApplication(unassignedAppListBean.getSelectedApplication());
    }

    /** Opens form to assign application. */
    @Action
    public void assignApplication() {
        openAssignmentForm(unassignedAppListBean.getSelectedApplication());
    }

    /** Refreshes the list of unassigned applications. */
    @Action
    public void refreshUnassignedApplications() {
        refreshApplications();
    }

    /** Opens form to unassign application. */
    @Action
    public void unassignApplication() {
        openAssignmentForm(assignedAppListBean.getSelectedApplication());
    }

    /** Opens application form for the selected application from assigned list. */
    @Action
    public void editAssignedApplication() {
        openApplication(assignedAppListBean.getSelectedApplication());
    }

    /** Refreshes the list of assigned applications. */
    @Action
    public void refreshAssignedApplications() {
        refreshApplications();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.application.ApplicationSearchResultsListBean assignedAppListBean;
    private javax.swing.JButton btnAssignApplication;
    private javax.swing.JButton btnEditAssignedApplication;
    private javax.swing.JButton btnEditUnassignedApplication;
    private javax.swing.JButton btnRefreshAssigned;
    private javax.swing.JButton btnRefreshUnassigned;
    private javax.swing.JButton btnUnassignApplication;
    private javax.swing.JScrollPane inprogressScrollPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JMenuItem menuAssignApplication;
    private javax.swing.JMenuItem menuEditAssignedApplication;
    private javax.swing.JMenuItem menuEditUnassignedApplication;
    private javax.swing.JMenuItem menuRefreshAssignApplication;
    private javax.swing.JMenuItem menuRefreshUnassignApplication;
    private javax.swing.JMenuItem menuUnassignApplication;
    private javax.swing.JPopupMenu popUpAssignedApplications;
    private javax.swing.JPopupMenu popUpUnassignedApplications;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tbAssigned;
    private javax.swing.JToolBar tbAssignedApplications;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tbUnassigned;
    private javax.swing.JToolBar tbUnassignedApplications;
    private org.sola.clients.beans.application.ApplicationSearchResultsListBean unassignedAppListBean;
    private javax.swing.JScrollPane unassignedScrollPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
