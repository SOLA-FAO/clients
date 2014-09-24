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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.sola.clients.beans.administrative.BaUnitSearchResultBean;
import org.sola.clients.beans.administrative.BaUnitSearchResultListBean;
import org.sola.clients.beans.application.ApplicationSummaryBean;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.clients.beans.application.ApplicationSearchResultBean;
import org.sola.clients.beans.application.ApplicationSearchResultsListBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.administrative.PropertyAssignmentDialog;
import org.sola.clients.swing.desktop.administrative.SLPropertyPanel;
import org.sola.clients.swing.desktop.application.ApplicationAssignmentDialog;
import org.sola.clients.swing.desktop.application.SLJobPanel;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer;
import org.sola.common.RolesConstants;
import org.sola.common.StringUtility;
import org.sola.common.WindowUtility;

/**
 * This panel displays assigned and unassigned applications.<br />
 * {@link ApplicationSummaryListBean} is used to bind the data on the panel.
 */
public class SLDashBoardPanel extends ContentPanel {

    private class AssignmentPanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(ApplicationAssignmentDialog.ASSIGNMENT_CHANGED)
                    || e.getPropertyName().equals(PropertyAssignmentDialog.ASSIGNMENT_CHANGED)) {
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
    public SLDashBoardPanel() {
        this(true);
    }

    /**
     * Panel constructor.
     *
     * @param forceRefresh Indicates whether to fetch applications upon opening.
     */
    public SLDashBoardPanel(boolean forceRefresh) {
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
        btnPropertyRefresh.setEnabled(SecurityBean.isInRole(RolesConstants.DASHBOARD_VIEW_UNASSIGNED_APPS));
        menuRefreshAssignApplication.setEnabled(btnRefreshAssigned.isEnabled());
        menuRefreshProperty.setEnabled(btnPropertyRefresh.isEnabled());

        if (forceRefresh) {
            refreshApplications();
        }

        customizeAssignedJobButtons();
        customizePropertyButtons();

        assignedAppListBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ApplicationSearchResultsListBean.APPLICATION_CHECKED_PROPERTY)
                        || evt.getPropertyName().equals(ApplicationSearchResultsListBean.SELECTED_APPLICATION_PROPERTY)) {
                    customizeAssignedJobButtons();
                }
            }
        });

        propertiesToActionListBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BaUnitSearchResultListBean.SELECTED_BAUNIT_SEARCH_RESULT_PROPERTY)
                        || evt.getPropertyName().equals(BaUnitSearchResultListBean.BAUNIT_CHECKED_PROPERTY)) {
                    customizePropertyButtons();
                }
            }
        });
    }

    /**
     * Enables or disables toolbar buttons for assigned applications list, .
     */
    private void customizeAssignedJobButtons() {
        boolean isAssignEnabled = false;
        boolean isEditEnabled = false;

        if (assignedAppListBean.hasChecked()
                && (SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_YOURSELF,
                        RolesConstants.APPLICATION_ASSIGN_TO_OTHERS))) {
            isAssignEnabled = true;
        }

        if (SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS,
                RolesConstants.APPLICATION_VIEW_APPS)) {
            isEditEnabled = assignedAppListBean.getSelectedApplication() != null;
        }

        btnOpenAssignedApplication.setEnabled(isEditEnabled);
        menuOpenAssignedApplication.setEnabled(isEditEnabled);

        btnAssignJob.setEnabled(isAssignEnabled);
        menuAssignJob.setEnabled(isAssignEnabled);
    }

    private void customizePropertyButtons() {
        btnOpenProperty.setEnabled(propertiesToActionListBean.getSelectedBaUnitSearchResult() != null);
        if (propertiesToActionListBean.hasChecked() && SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE,
                RolesConstants.ADMINISTRATIVE_ASSIGN_TEAM)) {
            btnPropertyAssign.setEnabled(true);
        } else {
            btnPropertyAssign.setEnabled(false);
        }

        menuAssignProperty.setEnabled(btnPropertyAssign.isEnabled());
        menuOpenProperty.setEnabled(btnOpenProperty.isEnabled());
    }

    /**
     * Opens application assignment form with selected applications.
     *
     * @param appList Selected applications to assign or unassign.
     */
    private void assignJob(final List<ApplicationSearchResultBean> appList) {
        if (appList == null || appList.size() < 1) {
            return;
        }
        ApplicationAssignmentDialog form = new ApplicationAssignmentDialog(appList, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.addPropertyChangeListener(assignmentPanelListener);
        form.setVisible(true);
    }

    /**
     * Opens application form.
     *
     * @param appBean Selected application summary bean.
     */
    private void openJob(final ApplicationSummaryBean appBean) {
        if (appBean == null || getMainContentPanel() == null) {
            return;
        }

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_APP));
                if (getMainContentPanel() != null) {
                    SLJobPanel applicationPanel = new SLJobPanel(appBean.getId());
                    getMainContentPanel().addPanel(applicationPanel, MainContentPanel.CARD_APPLICATION, true);
                }
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void autoRefresh() {
        if (autoRefresh) {
            setAutoRefresh(false);
            refreshApplications();
        }
    }

    /**
     * Returns autorefresh value.
     */
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

    /**
     * Refreshes assigned and unassigned application lists.
     */
    private void refreshApplications() {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                if (btnRefreshAssigned.isEnabled()) {
                    setMessage(MessageUtility.getLocalizedMessageText(
                            ClientMessage.APPLICATION_LOADING_ASSIGNED));
                    assignedAppListBean.loadAssignedJobs();
                }
                if (btnPropertyRefresh.isEnabled()) {
                    setMessage(MessageUtility.getLocalizedMessageText(
                            ClientMessage.PROGRESS_MSG_PROP_TO_ACTION));
                    propertiesToActionListBean.loadPropertiesToAction();
                }
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Opens application form for the selected application from assigned list.
     */
    private void editAssignedApplication() {
        openJob(assignedAppListBean.getSelectedApplication());
    }

    private void openPropertyForm(final BaUnitSearchResultBean property) {
        if (property != null) {
            SolaTask t = new SolaTask<Void, Void>() {
                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTY));
                    SLPropertyPanel propertyPanel = new SLPropertyPanel(property.getNameFirstpart(), property.getNameLastpart(), true);
                    getMainContentPanel().addPanel(propertyPanel, MainContentPanel.CARD_PROPERTY_PANEL, true);
                    return null;
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    /**
     * Opens property assignment form with selected properties.
     *
     * @param propList Selected properties to assign.
     */
    private void assignProperty(final List<BaUnitSearchResultBean> propList) {
        if (propList == null || propList.size() < 1) {
            return;
        }
        PropertyAssignmentDialog form = new PropertyAssignmentDialog(propList, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.addPropertyChangeListener(assignmentPanelListener);
        form.setVisible(true);
    }

    @Override
    public void setBreadCrumbTitle(String breadCrumbPath, String panelTitle) {
        // Ignore the BreadCrumbPath
        if (StringUtility.isEmpty(panelTitle)) {
            panelTitle = getBreadCrumbTitle();
        }
        if (getHeaderPanel() != null) {
            getHeaderPanel().setTitleText(panelTitle);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        assignedAppListBean = new org.sola.clients.beans.application.ApplicationSearchResultsListBean();
        popUpAssignedApplications = new javax.swing.JPopupMenu();
        menuOpenAssignedApplication = new javax.swing.JMenuItem();
        menuAssignJob = new javax.swing.JMenuItem();
        menuRefreshAssignApplication = new javax.swing.JMenuItem();
        propertiesToActionListBean = new org.sola.clients.beans.administrative.BaUnitSearchResultListBean();
        popUpPropertiesToAction = new javax.swing.JPopupMenu();
        menuOpenProperty = new javax.swing.JMenuItem();
        menuAssignProperty = new javax.swing.JMenuItem();
        menuRefreshProperty = new javax.swing.JMenuItem();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        tbAssignedApplications = new javax.swing.JToolBar();
        btnOpenAssignedApplication = new javax.swing.JButton();
        btnAssignJob = new javax.swing.JButton();
        btnRefreshAssigned = new javax.swing.JButton();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbAssigned = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel1 = new javax.swing.JPanel();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnOpenProperty = new org.sola.clients.swing.common.buttons.BtnOpen();
        btnPropertyAssign = new javax.swing.JButton();
        btnPropertyRefresh = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPropertiesToAction = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();

        popUpAssignedApplications.setName("popUpAssignedApplications"); // NOI18N

        menuOpenAssignedApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/Bundle"); // NOI18N
        menuOpenAssignedApplication.setText(bundle.getString("SLDashBoardPanel.menuOpenAssignedApplication.text")); // NOI18N
        menuOpenAssignedApplication.setToolTipText(bundle.getString("SLDashBoardPanel.menuOpenAssignedApplication.toolTipText")); // NOI18N
        menuOpenAssignedApplication.setName("menuOpenAssignedApplication"); // NOI18N
        menuOpenAssignedApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenAssignedApplicationActionPerformed(evt);
            }
        });
        popUpAssignedApplications.add(menuOpenAssignedApplication);

        menuAssignJob.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/unassign.png"))); // NOI18N
        menuAssignJob.setText(bundle.getString("SLDashBoardPanel.menuAssignJob.text")); // NOI18N
        menuAssignJob.setToolTipText(bundle.getString("SLDashBoardPanel.menuAssignJob.toolTipText")); // NOI18N
        menuAssignJob.setName("menuAssignJob"); // NOI18N
        menuAssignJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAssignJobActionPerformed(evt);
            }
        });
        popUpAssignedApplications.add(menuAssignJob);

        menuRefreshAssignApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        menuRefreshAssignApplication.setText(bundle.getString("SLDashBoardPanel.menuRefreshAssignApplication.text")); // NOI18N
        menuRefreshAssignApplication.setToolTipText(bundle.getString("SLDashBoardPanel.menuRefreshAssignApplication.toolTipText")); // NOI18N
        menuRefreshAssignApplication.setName("menuRefreshAssignApplication"); // NOI18N
        menuRefreshAssignApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRefreshAssignApplicationActionPerformed(evt);
            }
        });
        popUpAssignedApplications.add(menuRefreshAssignApplication);

        popUpPropertiesToAction.setName("popUpPropertiesToAction"); // NOI18N

        menuOpenProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuOpenProperty.setText(bundle.getString("SLDashBoardPanel.menuOpenProperty.text")); // NOI18N
        menuOpenProperty.setName("menuOpenProperty"); // NOI18N
        menuOpenProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenPropertyActionPerformed(evt);
            }
        });
        popUpPropertiesToAction.add(menuOpenProperty);

        menuAssignProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        menuAssignProperty.setText(bundle.getString("SLDashBoardPanel.menuAssignProperty.text")); // NOI18N
        menuAssignProperty.setName("menuAssignProperty"); // NOI18N
        menuAssignProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAssignPropertyActionPerformed(evt);
            }
        });
        popUpPropertiesToAction.add(menuAssignProperty);

        menuRefreshProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        menuRefreshProperty.setText(bundle.getString("SLDashBoardPanel.menuRefreshProperty.text")); // NOI18N
        menuRefreshProperty.setName("menuRefreshProperty"); // NOI18N
        menuRefreshProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRefreshPropertyActionPerformed(evt);
            }
        });
        popUpPropertiesToAction.add(menuRefreshProperty);

        setHelpTopic("dashboard_and_main_menu");
        setMinimumSize(new java.awt.Dimension(354, 249));
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridLayout(2, 1, 0, 6));

        jPanel2.setName("jPanel2"); // NOI18N

        tbAssignedApplications.setFloatable(false);
        tbAssignedApplications.setRollover(true);
        tbAssignedApplications.setName("tbAssignedApplications"); // NOI18N

        btnOpenAssignedApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenAssignedApplication.setText(bundle.getString("SLDashBoardPanel.btnOpenAssignedApplication.text")); // NOI18N
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

        btnAssignJob.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        btnAssignJob.setText(bundle.getString("SLDashBoardPanel.btnAssignJob.text")); // NOI18N
        btnAssignJob.setFocusable(false);
        btnAssignJob.setName("btnAssignJob"); // NOI18N
        btnAssignJob.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAssignJob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignJobActionPerformed(evt);
            }
        });
        tbAssignedApplications.add(btnAssignJob);

        btnRefreshAssigned.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        btnRefreshAssigned.setText(bundle.getString("SLDashBoardPanel.btnRefreshAssigned.text")); // NOI18N
        btnRefreshAssigned.setToolTipText(bundle.getString("SLDashBoardPanel.btnRefreshAssigned.toolTipText")); // NOI18N
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

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("SLDashBoardPanel.groupPanel1.titleText")); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tbAssigned.setName("tbAssigned"); // NOI18N
        tbAssigned.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${applicationSearchResultsList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, assignedAppListBean, eLProperty, tbAssigned);
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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${agent}"));
        columnBinding.setColumnName("Agent");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${assigneeName}"));
        columnBinding.setColumnName("Assignee Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${serviceList}"));
        columnBinding.setColumnName("Service List");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${serviceDescriptions}"));
        columnBinding.setColumnName("Service Descriptions");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${statusBean.displayValue}"));
        columnBinding.setColumnName("Status Bean.display Value");
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
        jScrollPane1.setViewportView(tbAssigned);
        if (tbAssigned.getColumnModel().getColumnCount() > 0) {
            tbAssigned.getColumnModel().getColumn(0).setMinWidth(25);
            tbAssigned.getColumnModel().getColumn(0).setPreferredWidth(25);
            tbAssigned.getColumnModel().getColumn(0).setMaxWidth(25);
            tbAssigned.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLDashBoardPanel.tbAssigned.columnModel.title0")); // NOI18N
            tbAssigned.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLDashBoardPanel.tbAssigned.columnModel.title1")); // NOI18N
            tbAssigned.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLDashBoardPanel.tbAssigned.columnModel.title2")); // NOI18N
            tbAssigned.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            tbAssigned.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLDashBoardPanel.tbAssigned.columnModel.title3")); // NOI18N
            tbAssigned.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLDashBoardPanel.tbAssigned.columnModel.title4_1")); // NOI18N
            tbAssigned.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("SLDashBoardPanel.tbAssigned.columnModel.title6")); // NOI18N
            tbAssigned.getColumnModel().getColumn(6).setPreferredWidth(200);
            tbAssigned.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("SLDashBoardPanel.tbAssigned.columnModel.title5")); // NOI18N
            tbAssigned.getColumnModel().getColumn(6).setCellRenderer(new org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer());
            tbAssigned.getColumnModel().getColumn(7).setPreferredWidth(200);
            tbAssigned.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("SLDashBoardPanel.tbAssigned.columnModel.title8")); // NOI18N
            tbAssigned.getColumnModel().getColumn(7).setCellRenderer(new org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer(";"));
            tbAssigned.getColumnModel().getColumn(8).setHeaderValue(bundle.getString("SLDashBoardPanel.tbAssigned.columnModel.title7_1")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tbAssignedApplications, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
            .add(groupPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(groupPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tbAssignedApplications, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.add(jPanel2);

        jPanel1.setName("jPanel1"); // NOI18N

        groupPanel2.setName("groupPanel2"); // NOI18N
        groupPanel2.setTitleText(bundle.getString("SLDashBoardPanel.groupPanel2.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnOpenProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenProperty.setName("btnOpenProperty"); // NOI18N
        btnOpenProperty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenPropertyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnOpenProperty);

        btnPropertyAssign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        btnPropertyAssign.setText(bundle.getString("SLDashBoardPanel.btnPropertyAssign.text")); // NOI18N
        btnPropertyAssign.setFocusable(false);
        btnPropertyAssign.setName("btnPropertyAssign"); // NOI18N
        btnPropertyAssign.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPropertyAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPropertyAssignActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPropertyAssign);

        btnPropertyRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/refresh.png"))); // NOI18N
        btnPropertyRefresh.setText(bundle.getString("SLDashBoardPanel.btnPropertyRefresh.text")); // NOI18N
        btnPropertyRefresh.setFocusable(false);
        btnPropertyRefresh.setName("btnPropertyRefresh"); // NOI18N
        btnPropertyRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPropertyRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPropertyRefreshActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPropertyRefresh);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblPropertiesToAction.setName("tblPropertiesToAction"); // NOI18N
        tblPropertiesToAction.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitSearchResults}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, propertiesToActionListBean, eLProperty, tblPropertiesToAction);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${checked}"));
        columnBinding.setColumnName("Checked");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stateLandName}"));
        columnBinding.setColumnName("State Land Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${propertyManager}"));
        columnBinding.setColumnName("Property Manager");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${locality}"));
        columnBinding.setColumnName("Locality");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${actionStatus.displayValue}"));
        columnBinding.setColumnName("Action Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notationText}"));
        columnBinding.setColumnName("Notation Text");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${activeJobs}"));
        columnBinding.setColumnName("Active Jobs");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stateLandStatus.displayValue}"));
        columnBinding.setColumnName("State Land Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, propertiesToActionListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedBaUnitSearchResult}"), tblPropertiesToAction, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblPropertiesToAction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPropertiesToActionMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblPropertiesToAction);
        if (tblPropertiesToAction.getColumnModel().getColumnCount() > 0) {
            tblPropertiesToAction.getColumnModel().getColumn(0).setMinWidth(25);
            tblPropertiesToAction.getColumnModel().getColumn(0).setPreferredWidth(25);
            tblPropertiesToAction.getColumnModel().getColumn(0).setMaxWidth(25);
            tblPropertiesToAction.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLDashBoardPanel.jTableWithDefaultStyles1.columnModel.title7_1")); // NOI18N
            tblPropertiesToAction.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLDashBoardPanel.jTableWithDefaultStyles1.columnModel.title0")); // NOI18N
            tblPropertiesToAction.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLDashBoardPanel.jTableWithDefaultStyles1.columnModel.title1")); // NOI18N
            tblPropertiesToAction.getColumnModel().getColumn(3).setPreferredWidth(200);
            tblPropertiesToAction.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLDashBoardPanel.tblPropertiesToAction.columnModel.title8")); // NOI18N
            tblPropertiesToAction.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLDashBoardPanel.jTableWithDefaultStyles1.columnModel.title3")); // NOI18N
            tblPropertiesToAction.getColumnModel().getColumn(5).setPreferredWidth(120);
            tblPropertiesToAction.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("SLDashBoardPanel.jTableWithDefaultStyles1.columnModel.title2")); // NOI18N
            tblPropertiesToAction.getColumnModel().getColumn(6).setPreferredWidth(200);
            tblPropertiesToAction.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("SLDashBoardPanel.jTableWithDefaultStyles1.columnModel.title4_1")); // NOI18N
            tblPropertiesToAction.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("SLDashBoardPanel.tblPropertiesToAction.columnModel.title6")); // NOI18N
            tblPropertiesToAction.getColumnModel().getColumn(7).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblPropertiesToAction.getColumnModel().getColumn(8).setHeaderValue(bundle.getString("SLDashBoardPanel.tblPropertiesToAction.columnModel.title7")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(groupPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane2)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(groupPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel1);

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("SLDashBoardPanel.headerPanel.titleText")); // NOI18N

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
                .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void menuOpenAssignedApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenAssignedApplicationActionPerformed
        editAssignedApplication();
    }//GEN-LAST:event_menuOpenAssignedApplicationActionPerformed

    private void menuRefreshAssignApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRefreshAssignApplicationActionPerformed
        refreshApplications();
    }//GEN-LAST:event_menuRefreshAssignApplicationActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        autoRefresh();
    }//GEN-LAST:event_formComponentShown

    private void btnRefreshAssignedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshAssignedActionPerformed
        refreshApplications();
    }//GEN-LAST:event_btnRefreshAssignedActionPerformed

    private void btnOpenAssignedApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenAssignedApplicationActionPerformed
        editAssignedApplication();
    }//GEN-LAST:event_btnOpenAssignedApplicationActionPerformed

    private void menuAssignJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAssignJobActionPerformed
        assignJob(assignedAppListBean.getChecked(true));
    }//GEN-LAST:event_menuAssignJobActionPerformed

    private void btnAssignJobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignJobActionPerformed
        assignJob(assignedAppListBean.getChecked(true));
    }//GEN-LAST:event_btnAssignJobActionPerformed

    private void btnOpenPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenPropertyActionPerformed
        openPropertyForm(propertiesToActionListBean.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_btnOpenPropertyActionPerformed

    private void btnPropertyRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPropertyRefreshActionPerformed
        refreshApplications();
    }//GEN-LAST:event_btnPropertyRefreshActionPerformed

    private void btnPropertyAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPropertyAssignActionPerformed
        assignProperty(propertiesToActionListBean.getChecked(true));
    }//GEN-LAST:event_btnPropertyAssignActionPerformed

    private void menuOpenPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenPropertyActionPerformed
        openPropertyForm(propertiesToActionListBean.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_menuOpenPropertyActionPerformed

    private void menuAssignPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAssignPropertyActionPerformed
        refreshApplications();
    }//GEN-LAST:event_menuAssignPropertyActionPerformed

    private void menuRefreshPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRefreshPropertyActionPerformed
        assignProperty(propertiesToActionListBean.getChecked(true));
    }//GEN-LAST:event_menuRefreshPropertyActionPerformed

    private void tbAssignedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAssignedMouseClicked
        if (evt.getClickCount() == 2) {
            editAssignedApplication();
        }
    }//GEN-LAST:event_tbAssignedMouseClicked

    private void tblPropertiesToActionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPropertiesToActionMouseClicked
        if (evt.getClickCount() == 2) {
            openPropertyForm(propertiesToActionListBean.getSelectedBaUnitSearchResult());
        }
    }//GEN-LAST:event_tblPropertiesToActionMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.application.ApplicationSearchResultsListBean assignedAppListBean;
    private javax.swing.JButton btnAssignJob;
    private javax.swing.JButton btnOpenAssignedApplication;
    private org.sola.clients.swing.common.buttons.BtnOpen btnOpenProperty;
    private javax.swing.JButton btnPropertyAssign;
    private javax.swing.JButton btnPropertyRefresh;
    private javax.swing.JButton btnRefreshAssigned;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuAssignJob;
    private javax.swing.JMenuItem menuAssignProperty;
    private javax.swing.JMenuItem menuOpenAssignedApplication;
    private javax.swing.JMenuItem menuOpenProperty;
    private javax.swing.JMenuItem menuRefreshAssignApplication;
    private javax.swing.JMenuItem menuRefreshProperty;
    private javax.swing.JPopupMenu popUpAssignedApplications;
    private javax.swing.JPopupMenu popUpPropertiesToAction;
    private org.sola.clients.beans.administrative.BaUnitSearchResultListBean propertiesToActionListBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tbAssigned;
    private javax.swing.JToolBar tbAssignedApplications;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblPropertiesToAction;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
