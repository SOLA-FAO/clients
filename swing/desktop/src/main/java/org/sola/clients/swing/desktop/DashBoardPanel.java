/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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
import java.util.List;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationSummaryBean;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import java.util.Locale;
import org.sola.clients.swing.desktop.application.ApplicationPanel;
import org.sola.clients.beans.application.ApplicationSearchResultBean;
import org.sola.clients.beans.application.ApplicationSearchResultsListBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.laf.LafManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.application.ApplicationAssignmentDialog;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.BooleanCellRenderer;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;

/**
 * This panel displays assigned and unassigned applications.<br />
 * {@link ApplicationSummaryListBean} is used to bind the data on the panel.
 */
public class DashBoardPanel extends ContentPanel {

    private class AssignmentPanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(ApplicationAssignmentDialog.ASSIGNMENT_CHANGED)) {
                refreshApplications();
            }
        }
    }
    private AssignmentPanelListener assignmentPanelListener;
    private boolean forceRefresh;
    private boolean autoRefresh = false;
    
    /**
     * Panel constructor.
     */
    public DashBoardPanel() {
        this(true);
    }

    /**
     * Panel constructor.
     *
     * @param forceRefresh Indicates whether to fetch applications upon opening.
     */
    public DashBoardPanel(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
        assignmentPanelListener = new AssignmentPanelListener();
        initComponents();
        postInit();
    }
    
    /**
     * Runs post initialization tasks to add listeners.
     */
    private void postInit() {

        setHeaderPanel(headerPanel);
        btnRefreshAssigned.setEnabled(SecurityBean.isInRole(RolesConstants.DASHBOARD_VIEW_ASSIGNED_APPS));
        btnRefreshUnassigned.setEnabled(SecurityBean.isInRole(RolesConstants.DASHBOARD_VIEW_UNASSIGNED_APPS));
        menuRefreshAssignApplication.setEnabled(btnRefreshAssigned.isEnabled());
        menuRefreshUnassignApplication.setEnabled(btnRefreshUnassigned.isEnabled());

        if(forceRefresh){
            refreshApplications();
        }
        
        customizeAssignedAppButtons();
        customizeUnassignedAppButtons();

        assignedAppListBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ApplicationSearchResultsListBean.APPLICATION_CHECKED_PROPERTY)
                        || evt.getPropertyName().equals(ApplicationSearchResultsListBean.SELECTED_APPLICATION_PROPERTY)) {
                    customizeAssignedAppButtons();
                }
            }
        });

        unassignedAppListBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ApplicationSearchResultsListBean.APPLICATION_CHECKED_PROPERTY)
                        || evt.getPropertyName().equals(ApplicationSearchResultsListBean.SELECTED_APPLICATION_PROPERTY)) {
                    customizeUnassignedAppButtons();
                }
            }
        });
    }

    /**
     * Enables or disables toolbar buttons for assigned applications list, .
     */
    private void customizeAssignedAppButtons() {
        boolean isUnassignEnabled;
        boolean isEditEnabled = false;

        if (assignedAppListBean.hasChecked()
                && (SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_YOURSELF)
                || SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_OTHERS))) {
            isUnassignEnabled = true;
        } else {
            isUnassignEnabled = false;
        }

        if (!isUnassignEnabled && SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS, RolesConstants.APPLICATION_VIEW_APPS)) {
            isEditEnabled = assignedAppListBean.getSelectedApplication() != null;
        }

        btnUnassignApplication.setEnabled(isUnassignEnabled);
        btnOpenAssignedApplication.setEnabled(isEditEnabled);
        menuUnassignApplication.setEnabled(isUnassignEnabled);
        menuOpenAssignedApplication.setEnabled(isEditEnabled);
    }

    /**
     * Enables or disables toolbar buttons for unassigned applications list.
     */
    private void customizeUnassignedAppButtons() {
        boolean isAssignEnabled;
        boolean isEditEnabled = false;

        if (unassignedAppListBean.hasChecked()
                && (SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_YOURSELF)
                || SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_OTHERS))) {
            isAssignEnabled = true;
        } else {
            isAssignEnabled = false;
        }

        if (!isAssignEnabled && SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS, RolesConstants.APPLICATION_VIEW_APPS)) {
            isEditEnabled = unassignedAppListBean.getSelectedApplication() != null;
        }

        btnAssignApplication.setEnabled(isAssignEnabled);
        btnOpenUnassignedApplication.setEnabled(isEditEnabled);
        menuAssignApplication.setEnabled(isAssignEnabled);
        menuOpenUnassignedApplication.setEnabled(isEditEnabled);
    }

    /**
     * Opens application assignment form with selected applications.
     *
     * @param appList Selected applications to assign or unassign.
     * @param assign Indicates whether to assign or unassign applications
     */
    private void assignUnassign(final List<ApplicationSearchResultBean> appList, final boolean assign) {
        if (appList == null || appList.size() < 1) {
            return;
        }

        for (ApplicationSearchResultBean app : appList) {
            if (!app.isFeePaid()) {
                MessageUtility.displayMessage(ClientMessage.CHECK_FEES_NOT_PAID, new Object[]{app.getNr()});
                return;
            }
        }

        if (assign) {
            ApplicationAssignmentDialog form = new ApplicationAssignmentDialog(appList, MainForm.getInstance(), true);
            WindowUtility.centerForm(form);
            form.addPropertyChangeListener(assignmentPanelListener);
            form.setVisible(true);
        } else {
            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_CONFIRM_UNASSIGN) == MessageUtility.BUTTON_ONE) {
                for (ApplicationSearchResultBean app : appList) {
                    String assigneeId = app.getAssigneeId();
                    if (assigneeId != null && assigneeId.equals(SecurityBean.getCurrentUser().getId())
                            && !SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_YOURSELF,
                            RolesConstants.APPLICATION_UNASSIGN_FROM_OTHERS)) {
                        // Can't unassign from yourself
                        MessageUtility.displayMessage(ClientMessage.APPLICATION_UNASSIGN_FROM_SELF_FORBIDDEN,
                                new Object[]{app.getNr()});
                        break;
                    }
                    if (assigneeId != null && !assigneeId.equals(SecurityBean.getCurrentUser().getId())
                            && !SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_OTHERS)) {
                        MessageUtility.displayMessage(ClientMessage.APPLICATION_UNASSIGN_FROM_OTHERS_FORBIDDEN,
                                new Object[]{app.getNr()});
                        break;
                    }
                    ApplicationBean.assignUser(app, null, null);
                }
                refreshApplications();
            }
        }
    }

    /**
     * Opens application form.
     *
     * @param appBean Selected application summary bean.
     */
    private void openApplication(final ApplicationSummaryBean appBean) {
        if (appBean == null || getMainContentPanel() == null) {
            return;
        }

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_APP));
                if (getMainContentPanel() != null) {
                    ApplicationPanel applicationPanel = new ApplicationPanel(appBean.getId());
                    getMainContentPanel().addPanel(applicationPanel, MainContentPanel.CARD_APPLICATION, true);
                }
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void autoRefresh(){
        if(autoRefresh){
            setAutoRefresh(false);
            refreshApplications();
        }
    }

    /** Returns autorefresh value. */
    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    /** 
     * Sets autorefresh value. If <code>true</code> is assigned Dashboard will 
     * be refreshed automatically at the time of component shown event. 
     */
    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
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
        lblUnassignedApps = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAssignApplication = new javax.swing.JButton();
        btnOpenUnassignedApplication = new javax.swing.JButton();
        btnRefreshUnassigned = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tbAssignedApplications = new javax.swing.JToolBar();
        lblAssignedApps = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnUnassignApplication = new javax.swing.JButton();
        btnOpenAssignedApplication = new javax.swing.JButton();
        btnRefreshAssigned = new javax.swing.JButton();
        inprogressScrollPanel = new javax.swing.JScrollPane();
        tbAssigned = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();

        popUpUnassignedApplications.setName("popUpUnassignedApplications"); // NOI18N

        menuAssignApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        menuAssignApplication.setText(bundle.getString("DashBoardPanel.menuAssignApplication.text")); // NOI18N
        menuAssignApplication.setName("menuAssignApplication"); // NOI18N
        menuAssignApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAssignApplicationActionPerformed(evt);
            }
        });
        popUpUnassignedApplications.add(menuAssignApplication);

        menuOpenUnassignedApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuOpenUnassignedApplication.setText(bundle.getString("DashBoardPanel.menuOpenUnassignedApplication.text_1")); // NOI18N
        menuOpenUnassignedApplication.setName("menuOpenUnassignedApplication"); // NOI18N
        menuOpenUnassignedApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenUnassignedApplicationActionPerformed(evt);
            }
        });
        popUpUnassignedApplications.add(menuOpenUnassignedApplication);

        menuRefreshUnassignApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        menuRefreshUnassignApplication.setText(bundle.getString("DashBoardPanel.menuRefreshUnassignApplication.text")); // NOI18N
        menuRefreshUnassignApplication.setName("menuRefreshUnassignApplication"); // NOI18N
        menuRefreshUnassignApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRefreshUnassignApplicationActionPerformed(evt);
            }
        });
        popUpUnassignedApplications.add(menuRefreshUnassignApplication);

        popUpAssignedApplications.setName("popUpAssignedApplications"); // NOI18N

        menuUnassignApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/unassign.png"))); // NOI18N
        menuUnassignApplication.setText(bundle.getString("DashBoardPanel.menuUnassignApplication.text")); // NOI18N
        menuUnassignApplication.setToolTipText(bundle.getString("DashBoardPanel.menuUnassignApplication.toolTipText")); // NOI18N
        menuUnassignApplication.setName("menuUnassignApplication"); // NOI18N
        menuUnassignApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuUnassignApplicationActionPerformed(evt);
            }
        });
        popUpAssignedApplications.add(menuUnassignApplication);

        menuOpenAssignedApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuOpenAssignedApplication.setText(bundle.getString("DashBoardPanel.menuOpenAssignedApplication.text")); // NOI18N
        menuOpenAssignedApplication.setToolTipText(bundle.getString("DashBoardPanel.menuOpenAssignedApplication.toolTipText")); // NOI18N
        menuOpenAssignedApplication.setName("menuOpenAssignedApplication"); // NOI18N
        menuOpenAssignedApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenAssignedApplicationActionPerformed(evt);
            }
        });
        popUpAssignedApplications.add(menuOpenAssignedApplication);

        menuRefreshAssignApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        menuRefreshAssignApplication.setText(bundle.getString("DashBoardPanel.menuRefreshAssignApplication.text")); // NOI18N
        menuRefreshAssignApplication.setToolTipText(bundle.getString("DashBoardPanel.menuRefreshAssignApplication.toolTipText")); // NOI18N
        menuRefreshAssignApplication.setName("menuRefreshAssignApplication"); // NOI18N
        menuRefreshAssignApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRefreshAssignApplicationActionPerformed(evt);
            }
        });
        popUpAssignedApplications.add(menuRefreshAssignApplication);

        setHelpTopic("dashboard"); // NOI18N
        setMinimumSize(new java.awt.Dimension(354, 249));
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridLayout(2, 1, 0, 20));

        jPanel1.setName("jPanel1"); // NOI18N

        unassignedScrollPanel.setName("unassignedScrollPanel"); // NOI18N
        unassignedScrollPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tbUnassigned.setColumnSelectionAllowed(true);
        tbUnassigned.setComponentPopupMenu(popUpUnassignedApplications);
        tbUnassigned.setGridColor(new java.awt.Color(135, 127, 115));
        tbUnassigned.setName("tbUnassigned"); // NOI18N
        tbUnassigned.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbUnassigned.setShowVerticalLines(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${applicationSearchResultsList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, unassignedAppListBean, eLProperty, tbUnassigned);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${checked}"));
        columnBinding.setColumnName("Checked");
        columnBinding.setColumnClass(Boolean.class);
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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${contactPerson}"));
        columnBinding.setColumnName("Contact Person");
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
        if (tbUnassigned.getColumnModel().getColumnCount() > 0) {
            tbUnassigned.getColumnModel().getColumn(0).setMinWidth(25);
            tbUnassigned.getColumnModel().getColumn(0).setPreferredWidth(25);
            tbUnassigned.getColumnModel().getColumn(0).setMaxWidth(25);
            tbUnassigned.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title0")); // NOI18N
            tbUnassigned.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title1")); // NOI18N
            tbUnassigned.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title2")); // NOI18N
            tbUnassigned.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            tbUnassigned.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title3")); // NOI18N
            tbUnassigned.getColumnModel().getColumn(3).setCellRenderer(new DateTimeRenderer());
            tbUnassigned.getColumnModel().getColumn(4).setMinWidth(180);
            tbUnassigned.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title4")); // NOI18N
            tbUnassigned.getColumnModel().getColumn(4).setCellRenderer(new org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer());
            tbUnassigned.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title8")); // NOI18N
            tbUnassigned.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title5")); // NOI18N
            tbUnassigned.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title6")); // NOI18N
            tbUnassigned.getColumnModel().getColumn(8).setHeaderValue(bundle.getString("DashBoardPanel.tbUnassigned.columnModel.title7")); // NOI18N
            tbUnassigned.getColumnModel().getColumn(8).setCellRenderer(new BooleanCellRenderer());
        }

        tbUnassignedApplications.setFloatable(false);
        tbUnassignedApplications.setRollover(true);
        tbUnassignedApplications.setName("tbUnassignedApplications"); // NOI18N

        lblUnassignedApps.setText(bundle.getString("DashBoardPanel.lblUnassignedApps.text")); // NOI18N
        lblUnassignedApps.setName("lblUnassignedApps"); // NOI18N
        tbUnassignedApplications.add(lblUnassignedApps);

        jSeparator1.setName("jSeparator1"); // NOI18N
        tbUnassignedApplications.add(jSeparator1);

        btnAssignApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        btnAssignApplication.setText(bundle.getString("DashBoardPanel.btnAssignApplication.text")); // NOI18N
        btnAssignApplication.setToolTipText(bundle.getString("DashBoardPanel.btnAssignApplication.toolTipText")); // NOI18N
        btnAssignApplication.setFocusable(false);
        btnAssignApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAssignApplication.setName("btnAssignApplication"); // NOI18N
        btnAssignApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAssignApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignApplicationActionPerformed(evt);
            }
        });
        tbUnassignedApplications.add(btnAssignApplication);

        btnOpenUnassignedApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenUnassignedApplication.setText(bundle.getString("DashBoardPanel.btnOpenUnassignedApplication.text")); // NOI18N
        btnOpenUnassignedApplication.setFocusable(false);
        btnOpenUnassignedApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenUnassignedApplication.setName("btnOpenUnassignedApplication"); // NOI18N
        btnOpenUnassignedApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenUnassignedApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenUnassignedApplicationActionPerformed(evt);
            }
        });
        tbUnassignedApplications.add(btnOpenUnassignedApplication);

        btnRefreshUnassigned.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        btnRefreshUnassigned.setText(bundle.getString("DashBoardPanel.btnRefreshUnassigned.text")); // NOI18N
        btnRefreshUnassigned.setFocusable(false);
        btnRefreshUnassigned.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRefreshUnassigned.setName("btnRefreshUnassigned"); // NOI18N
        btnRefreshUnassigned.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshUnassigned.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshUnassignedActionPerformed(evt);
            }
        });
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

        lblAssignedApps.setText(bundle.getString("DashBoardPanel.lblAssignedApps.text")); // NOI18N
        lblAssignedApps.setName("lblAssignedApps"); // NOI18N
        tbAssignedApplications.add(lblAssignedApps);

        jSeparator2.setName("jSeparator2"); // NOI18N
        tbAssignedApplications.add(jSeparator2);

        btnUnassignApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/unassign.png"))); // NOI18N
        btnUnassignApplication.setText(bundle.getString("DashBoardPanel.btnUnassignApplication.text")); // NOI18N
        btnUnassignApplication.setToolTipText(bundle.getString("DashBoardPanel.btnUnassignApplication.toolTipText")); // NOI18N
        btnUnassignApplication.setFocusable(false);
        btnUnassignApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUnassignApplication.setName("btnUnassignApplication"); // NOI18N
        btnUnassignApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnUnassignApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnassignApplicationActionPerformed(evt);
            }
        });
        tbAssignedApplications.add(btnUnassignApplication);

        btnOpenAssignedApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenAssignedApplication.setText(bundle.getString("DashBoardPanel.btnOpenAssignedApplication.text")); // NOI18N
        btnOpenAssignedApplication.setFocusable(false);
        btnOpenAssignedApplication.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenAssignedApplication.setName("btnOpenAssignedApplication"); // NOI18N
        btnOpenAssignedApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenAssignedApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenAssignedApplicationActionPerformed(evt);
            }
        });
        tbAssignedApplications.add(btnOpenAssignedApplication);

        btnRefreshAssigned.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        btnRefreshAssigned.setText(bundle.getString("DashBoardPanel.btnRefreshAssigned.text")); // NOI18N
        btnRefreshAssigned.setToolTipText(bundle.getString("DashBoardPanel.btnRefreshAssigned.toolTipText")); // NOI18N
        btnRefreshAssigned.setFocusable(false);
        btnRefreshAssigned.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRefreshAssigned.setName("btnRefreshAssigned"); // NOI18N
        btnRefreshAssigned.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRefreshAssigned.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshAssignedActionPerformed(evt);
            }
        });
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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${checked}"));
        columnBinding.setColumnName("Checked");
        columnBinding.setColumnClass(Boolean.class);
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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${contactPerson}"));
        columnBinding.setColumnName("Contact Person");
        columnBinding.setColumnClass(String.class);
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
        if (tbAssigned.getColumnModel().getColumnCount() > 0) {
            tbAssigned.getColumnModel().getColumn(0).setMinWidth(25);
            tbAssigned.getColumnModel().getColumn(0).setPreferredWidth(25);
            tbAssigned.getColumnModel().getColumn(0).setMaxWidth(25);
            tbAssigned.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title0")); // NOI18N
            tbAssigned.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title1")); // NOI18N
            tbAssigned.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title2")); // NOI18N
            tbAssigned.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            tbAssigned.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title3")); // NOI18N
            tbAssigned.getColumnModel().getColumn(3).setCellRenderer(new DateTimeRenderer());
            tbAssigned.getColumnModel().getColumn(4).setMinWidth(180);
            tbAssigned.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title4")); // NOI18N
            tbAssigned.getColumnModel().getColumn(4).setCellRenderer(new org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer());
            tbAssigned.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title8")); // NOI18N
            tbAssigned.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title5")); // NOI18N
            tbAssigned.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title6")); // NOI18N
            tbAssigned.getColumnModel().getColumn(8).setHeaderValue(bundle.getString("DashBoardPanel.tbAssigned.columnModel.title7")); // NOI18N
        }

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

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("DashBoardPanel.headerPanel.titleText")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(headerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void tbUnassignedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbUnassignedMouseClicked
        if (evt.getClickCount() == 2 && btnOpenUnassignedApplication.isEnabled()) {
            editUnassignedApplication();
        }
    }//GEN-LAST:event_tbUnassignedMouseClicked

    private void tbAssignedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAssignedMouseClicked
        if (evt.getClickCount() == 2 && btnOpenAssignedApplication.isEnabled()) {
            openApplication(assignedAppListBean.getSelectedApplication());
        }
    }//GEN-LAST:event_tbAssignedMouseClicked

    private void btnAssignApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignApplicationActionPerformed
        assignApplication();
    }//GEN-LAST:event_btnAssignApplicationActionPerformed

    private void btnOpenUnassignedApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenUnassignedApplicationActionPerformed
        editUnassignedApplication();
    }//GEN-LAST:event_btnOpenUnassignedApplicationActionPerformed

    private void btnRefreshUnassignedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshUnassignedActionPerformed
        refreshUnassignedApplications();
    }//GEN-LAST:event_btnRefreshUnassignedActionPerformed

    private void btnUnassignApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnassignApplicationActionPerformed
        unassignApplication();
    }//GEN-LAST:event_btnUnassignApplicationActionPerformed

    private void btnOpenAssignedApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenAssignedApplicationActionPerformed
        editAssignedApplication();
    }//GEN-LAST:event_btnOpenAssignedApplicationActionPerformed

    private void btnRefreshAssignedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshAssignedActionPerformed
        refreshAssignedApplications();
    }//GEN-LAST:event_btnRefreshAssignedActionPerformed

    private void menuAssignApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAssignApplicationActionPerformed
        assignApplication();
    }//GEN-LAST:event_menuAssignApplicationActionPerformed

    private void menuOpenUnassignedApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenUnassignedApplicationActionPerformed
        editUnassignedApplication();
    }//GEN-LAST:event_menuOpenUnassignedApplicationActionPerformed

    private void menuRefreshUnassignApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRefreshUnassignApplicationActionPerformed
        refreshUnassignedApplications();
    }//GEN-LAST:event_menuRefreshUnassignApplicationActionPerformed

    private void menuUnassignApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuUnassignApplicationActionPerformed
        unassignApplication();
    }//GEN-LAST:event_menuUnassignApplicationActionPerformed

    private void menuOpenAssignedApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenAssignedApplicationActionPerformed
        editAssignedApplication();
    }//GEN-LAST:event_menuOpenAssignedApplicationActionPerformed

    private void menuRefreshAssignApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRefreshAssignApplicationActionPerformed
        refreshAssignedApplications();
    }//GEN-LAST:event_menuRefreshAssignApplicationActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        autoRefresh();
    }//GEN-LAST:event_formComponentShown

    /**
     * Refreshes assigned and unassigned application lists.
     */
    private void refreshApplications() {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                if (btnRefreshUnassigned.isEnabled()) {
                    setMessage(MessageUtility.getLocalizedMessageText(
                            ClientMessage.APPLICATION_LOADING_UNASSIGNED));
                    unassignedAppListBean.FillUnassigned();
                }
                if (btnRefreshAssigned.isEnabled()) {
                    setMessage(MessageUtility.getLocalizedMessageText(
                            ClientMessage.APPLICATION_LOADING_ASSIGNED));
                    assignedAppListBean.FillAssigned();
                }
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Opens application form for the selected application from unassigned list.
     */
    private void editUnassignedApplication() {
        openApplication(unassignedAppListBean.getSelectedApplication());
    }

    /**
     * Opens form to assign application.
     */
    private void assignApplication() {
        assignUnassign(unassignedAppListBean.getChecked(true), true);
    }

    /**
     * Refreshes the list of unassigned applications.
     */
    private void refreshUnassignedApplications() {
        refreshApplications();
    }

    /**
     * Opens form to unassign application.
     */
    private void unassignApplication() {
        assignUnassign(assignedAppListBean.getChecked(true), false);
    }

    /**
     * Opens application form for the selected application from assigned list.
     */
    private void editAssignedApplication() {
        openApplication(assignedAppListBean.getSelectedApplication());
    }

    /**
     * Refreshes the list of assigned applications.
     */
    private void refreshAssignedApplications() {
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
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JScrollPane inprogressScrollPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JLabel lblAssignedApps;
    private javax.swing.JLabel lblUnassignedApps;
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
