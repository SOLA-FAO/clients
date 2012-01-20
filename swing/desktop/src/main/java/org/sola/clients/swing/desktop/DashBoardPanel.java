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

import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.jdesktop.application.Action;
import org.sola.clients.swing.desktop.application.ApplicationAssignmentPanel;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationSummaryBean;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import java.util.Locale;
import org.jdesktop.application.Task;
import org.sola.clients.swing.desktop.application.ApplicationPanel;
import org.sola.clients.beans.application.ApplicationSearchResultBean;
import org.sola.clients.beans.application.ApplicationSearchResultsListBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.common.RolesConstants;

/** 
 * This panel displays assigned and unassigned applications.<br /> 
 * {@link ApplicationSummaryListBean} is used to bind the data on the panel.
 */
public class DashBoardPanel extends ContentPanel {

    private class AssignmentPanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(ApplicationBean.ASSIGNEE_ID_PROPERTY)) {
                if (getMainContentPanel() != null && getMainContentPanel().isPanelOpened(MainContentPanel.CARD_APPASSIGNMENT)) {
                    getMainContentPanel().closePanel(MainContentPanel.CARD_APPASSIGNMENT);
                }
                refreshApplications();
            }
        }
    }
    private AssignmentPanelListener assignmentPanelListener;

    /** 
     * Panel constructor.
     * @param mainForm Parent form.
     */
    public DashBoardPanel() {
        assignmentPanelListener = new AssignmentPanelListener();
        initComponents();
        postInit();
    }

    /** Runs post initialization tasks to add listeners. */
    private void postInit() {

        customizeComponents();
        setHeaderPanel(headerPanel1);
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

    /** Applies customization of component L&F. */
    private void customizeComponents() {

//    BUTTONS   
        LafManager.getInstance().setBtnProperties(btnAssignApplication);
        LafManager.getInstance().setBtnProperties(btnOpenAssignedApplication);
        LafManager.getInstance().setBtnProperties(btnOpenUnassignedApplication);
        LafManager.getInstance().setBtnProperties(btnRefreshAssigned);
        LafManager.getInstance().setBtnProperties(btnRefreshUnassigned);
        LafManager.getInstance().setBtnProperties(btnUnassignApplication);

//    LABELS    
        LafManager.getInstance().setLabProperties(jLabel1);
        LafManager.getInstance().setLabProperties(jLabel2);
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
        btnOpenAssignedApplication.getAction().setEnabled(isEditEnabled);
    }

    /** Enables or disables toolbar buttons for unassigned applications list. */
    private void customizeUnassignedAppButtons(ApplicationSearchResultBean app) {
        boolean isAssignEnabled = true;
        boolean isEditEnabled = true;

        if (app == null) {
            isAssignEnabled = false;
            isEditEnabled = false;
        } else {
            if (SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_YOURSELF)
                    || SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_OTHERS)) {
                isAssignEnabled = true;
            } else {
                isAssignEnabled = false;
            }
            isEditEnabled = SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS);
        }

        btnAssignApplication.getAction().setEnabled(isAssignEnabled);
        btnOpenUnassignedApplication.getAction().setEnabled(isEditEnabled);
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

        if (getMainContentPanel() != null) {
            if (getMainContentPanel().isPanelOpened(MainContentPanel.CARD_APPASSIGNMENT)) {
                getMainContentPanel().closePanel(MainContentPanel.CARD_APPASSIGNMENT);
            }
            ApplicationAssignmentPanel panel = new ApplicationAssignmentPanel(appBean.getId());
            panel.addPropertyChangeListener(ApplicationBean.ASSIGNEE_ID_PROPERTY, assignmentPanelListener);
            getMainContentPanel().addPanel(panel, MainContentPanel.CARD_APPASSIGNMENT);
            getMainContentPanel().showPanel(MainContentPanel.CARD_APPASSIGNMENT);
        }
    }

    /** 
     * Opens application form. 
     * @param appBean Selected application summary bean.
     */
    private void openApplication(final ApplicationSummaryBean appBean) {
        if (appBean == null || getMainContentPanel() == null) {
            return;
        }

        Task t = new Task(DesktopApplication.getApplication()) {

            @Override
            protected Object doInBackground() throws Exception {
                setMessage("Opening application form.");
                PropertyChangeListener listener = new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent e) {
                        refreshApplications();
                    }
                };

                if (getMainContentPanel() != null) {
                    ApplicationPanel applicationPanel = new ApplicationPanel(appBean.getId());
                    applicationPanel.addPropertyChangeListener(ApplicationBean.APPLICATION_PROPERTY, listener);
                    getMainContentPanel().addPanel(applicationPanel, MainContentPanel.CARD_APPLICATION, true);
                }
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
        menuOpenUnassignedApplication = new javax.swing.JMenuItem();
        menuRefreshUnassignApplication = new javax.swing.JMenuItem();
        popUpAssignedApplications = new javax.swing.JPopupMenu();
        menuUnassignApplication = new javax.swing.JMenuItem();
        menuOpenAssignedApplication = new javax.swing.JMenuItem();
        menuRefreshAssignApplication = new javax.swing.JMenuItem();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        unassignedScrollPanel = new javax.swing.JScrollPane();
        tbUnassigned = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        tbUnassignedApplications = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAssignApplication = new javax.swing.JButton();
        btnOpenUnassignedApplication = new javax.swing.JButton();
        btnRefreshUnassigned = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tbAssignedApplications = new javax.swing.JToolBar();
        jLabel2 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnUnassignApplication = new javax.swing.JButton();
        btnOpenAssignedApplication = new javax.swing.JButton();
        btnRefreshAssigned = new javax.swing.JButton();
        inprogressScrollPanel = new javax.swing.JScrollPane();
        tbAssigned = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        headerPanel1 = new org.sola.clients.swing.ui.HeaderPanel();

        popUpUnassignedApplications.setName("popUpUnassignedApplications"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(org.sola.clients.swing.desktop.DesktopApplication.class).getContext().getActionMap(DashBoardPanel.class, this);
        menuAssignApplication.setAction(actionMap.get("assignApplication")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        menuAssignApplication.setText(bundle.getString("DashBoardPanel.menuAssignApplication.text")); // NOI18N
        menuAssignApplication.setName("menuAssignApplication"); // NOI18N
        popUpUnassignedApplications.add(menuAssignApplication);

        menuOpenUnassignedApplication.setAction(actionMap.get("editUnassignedApplication")); // NOI18N
        menuOpenUnassignedApplication.setLabel(bundle.getString("DashBoardPanel.menuOpenUnassignedApplication.label")); // NOI18N
        menuOpenUnassignedApplication.setName("menuOpenUnassignedApplication"); // NOI18N
        popUpUnassignedApplications.add(menuOpenUnassignedApplication);

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

        menuOpenAssignedApplication.setAction(actionMap.get("editAssignedApplication")); // NOI18N
        menuOpenAssignedApplication.setText(bundle.getString("DashBoardPanel.menuOpenAssignedApplication.text")); // NOI18N
        menuOpenAssignedApplication.setToolTipText(bundle.getString("DashBoardPanel.menuOpenAssignedApplication.toolTipText")); // NOI18N
        menuOpenAssignedApplication.setName("menuOpenAssignedApplication"); // NOI18N
        popUpAssignedApplications.add(menuOpenAssignedApplication);

        menuRefreshAssignApplication.setAction(actionMap.get("refreshUnassignedApplications")); // NOI18N
        menuRefreshAssignApplication.setText(bundle.getString("DashBoardPanel.menuRefreshAssignApplication.text")); // NOI18N
        menuRefreshAssignApplication.setToolTipText(bundle.getString("DashBoardPanel.menuRefreshAssignApplication.toolTipText")); // NOI18N
        menuRefreshAssignApplication.setName("menuRefreshAssignApplication"); // NOI18N
        popUpAssignedApplications.add(menuRefreshAssignApplication);

        setMinimumSize(new java.awt.Dimension(354, 249));
        setName("Form"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridLayout(2, 1, 0, 20));

        jPanel1.setName("jPanel1"); // NOI18N

        unassignedScrollPanel.setName("unassignedScrollPanel"); // NOI18N
        unassignedScrollPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tbUnassigned.setComponentPopupMenu(popUpUnassignedApplications);
        tbUnassigned.setGridColor(new java.awt.Color(135, 127, 115));
        tbUnassigned.setName("tbUnassigned"); // NOI18N
        tbUnassigned.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbUnassigned.setShowVerticalLines(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${applicationSearchResultsList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, unassignedAppListBean, eLProperty, tbUnassigned);
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
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, unassignedAppListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedApplication}"), tbUnassigned, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
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
        tbUnassigned.getColumnModel().getColumn(3).setCellRenderer(new org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer());
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
        btnAssignApplication.setFont(new java.awt.Font("Arial", 0, 12));
        btnAssignApplication.setText(bundle.getString("DashBoardPanel.btnAssignApplication.text")); // NOI18N
        btnAssignApplication.setFocusable(false);
        btnAssignApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAssignApplication.setName("btnAssignApplication"); // NOI18N
        btnAssignApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbUnassignedApplications.add(btnAssignApplication);

        btnOpenUnassignedApplication.setAction(actionMap.get("editUnassignedApplication")); // NOI18N
        btnOpenUnassignedApplication.setFont(new java.awt.Font("Arial", 0, 12));
        btnOpenUnassignedApplication.setText(bundle.getString("DashBoardPanel.btnOpenUnassignedApplication.text")); // NOI18N
        btnOpenUnassignedApplication.setFocusable(false);
        btnOpenUnassignedApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenUnassignedApplication.setName("btnOpenUnassignedApplication"); // NOI18N
        btnOpenUnassignedApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbUnassignedApplications.add(btnOpenUnassignedApplication);

        btnRefreshUnassigned.setAction(actionMap.get("refreshUnassignedApplications")); // NOI18N
        btnRefreshUnassigned.setFont(new java.awt.Font("Arial", 0, 12));
        btnRefreshUnassigned.setText(bundle.getString("DashBoardPanel.btnRefreshUnassigned.text")); // NOI18N
        btnRefreshUnassigned.setFocusable(false);
        btnRefreshUnassigned.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRefreshUnassigned.setName("btnRefreshUnassigned"); // NOI18N
        btnRefreshUnassigned.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbUnassignedApplications.add(btnRefreshUnassigned);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tbUnassignedApplications, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
            .add(unassignedScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(tbUnassignedApplications, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(unassignedScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

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

        btnOpenAssignedApplication.setAction(actionMap.get("editAssignedApplication")); // NOI18N
        btnOpenAssignedApplication.setFont(new java.awt.Font("Arial", 0, 12));
        btnOpenAssignedApplication.setText(bundle.getString("DashBoardPanel.btnOpenAssignedApplication.text")); // NOI18N
        btnOpenAssignedApplication.setFocusable(false);
        btnOpenAssignedApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenAssignedApplication.setName("btnOpenAssignedApplication"); // NOI18N
        btnOpenAssignedApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbAssignedApplications.add(btnOpenAssignedApplication);

        btnRefreshAssigned.setAction(actionMap.get("refreshAssignedApplications")); // NOI18N
        btnRefreshAssigned.setFont(new java.awt.Font("Arial", 0, 12));
        btnRefreshAssigned.setFocusable(false);
        btnRefreshAssigned.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRefreshAssigned.setName("btnRefreshAssigned"); // NOI18N
        btnRefreshAssigned.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbAssignedApplications.add(btnRefreshAssigned);

        inprogressScrollPanel.setName("inprogressScrollPanel"); // NOI18N
        inprogressScrollPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tbAssigned.setComponentPopupMenu(popUpAssignedApplications);
        tbAssigned.setGridColor(new java.awt.Color(135, 127, 115));
        tbAssigned.setName("tbAssigned"); // NOI18N
        tbAssigned.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbAssigned.setShowVerticalLines(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${applicationSearchResultsList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, assignedAppListBean, eLProperty, tbAssigned);
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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${assigneeName}"));
        columnBinding.setColumnName("Assignee Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status}"));
        columnBinding.setColumnName("Status");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, assignedAppListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedApplication}"), tbAssigned, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
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
        tbAssigned.getColumnModel().getColumn(3).setCellRenderer(new org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer());
        tbAssigned.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("DashBoard.tbAssigned.columnModel.title3")); // NOI18N
        tbAssigned.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title5")); // NOI18N
        tbAssigned.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("DashBoard.tbAssigned.columnModel.title4")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tbAssignedApplications, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
            .add(inprogressScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(tbAssignedApplications, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(inprogressScrollPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel2);

        headerPanel1.setName("headerPanel1"); // NOI18N
        headerPanel1.setTitleText(bundle.getString("DashBoardPanel.headerPanel1.titleText")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(headerPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void tbUnassignedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbUnassignedMouseClicked
        if (evt.getClickCount() == 2 && btnOpenUnassignedApplication.getAction().isEnabled()) {
            editUnassignedApplication();
        }
    }//GEN-LAST:event_tbUnassignedMouseClicked

    private void tbAssignedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAssignedMouseClicked
        if (evt.getClickCount() == 2 && btnOpenAssignedApplication.getAction().isEnabled()) {
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
    private javax.swing.JButton btnOpenAssignedApplication;
    private javax.swing.JButton btnOpenUnassignedApplication;
    private javax.swing.JButton btnRefreshAssigned;
    private javax.swing.JButton btnRefreshUnassigned;
    private javax.swing.JButton btnUnassignApplication;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel1;
    private javax.swing.JScrollPane inprogressScrollPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JMenuItem menuAssignApplication;
    private javax.swing.JMenuItem menuOpenAssignedApplication;
    private javax.swing.JMenuItem menuOpenUnassignedApplication;
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
