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
package org.sola.clients.swing.desktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationSearchResultBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.party.PartySummaryListBean;
import org.sola.clients.beans.referencedata.PartyRoleTypeBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.EntityAction;

/**
 * This dialog form is used to assign application to the user or unassign it
 * from him. <br />{@link UsersListBean} is used to bind the data on the form.
 */
public class ApplicationAssignmentDialog extends javax.swing.JDialog {

    public static final String ASSIGNMENT_CHANGED = "assignmentChanged";
    private List<ApplicationSearchResultBean> applications;
    private ApplicationBean app;

    /**
     * Default constructor
     *
     * @param applications List of applications to assign or unassign
     */
    public ApplicationAssignmentDialog(List<ApplicationSearchResultBean> applications,
            java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.applications = applications;
        initComponents();
        customizeForm();
    }

    /**
     * Supports re-assignment of a individual application
     *
     * @param app
     * @param parent
     * @param modal
     */
    public ApplicationAssignmentDialog(ApplicationBean app,
            java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.app = app;
        initComponents();
        customizeForm();
    }

    private PartySummaryListBean createPartySummaryList() {
        PartySummaryListBean teamList = new PartySummaryListBean();
        teamList.loadParties(PartyRoleTypeBean.ROLE_TEAM,
                true, (String) null);
        return teamList;
    }

    private void customizeForm() {

        URL imgURL = this.getClass().getResource("/images/sola/logo_icon.jpg");
        this.setIconImage(new ImageIcon(imgURL).getImage());

        WindowUtility.addEscapeListener(this, false);

        teamListBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PartySummaryListBean.SELECTED_PARTYSUMMARY_PROPERTY)
                        && SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_OTHERS)) {
                    // Only filter the users list if the current user has permission to assign 
                    // the job to a different user
                    PartySummaryBean party = (PartySummaryBean) evt.getNewValue();
                    List<String> list = null;
                    if (party != null && party.getEntityAction() != EntityAction.DISASSOCIATE) {
                        list = new ArrayList();
                        list.add(party.getId());
                    }
                    usersList.filterUsersByTeamIds(list);
                    // Set the current user as the default if they are part of the selected team. 
                    usersList.setSelectedUserById(SecurityBean.getCurrentUser().getId());
                }
            }
        });

        cbxUsers.setEnabled(false);

        if (app == null && (applications == null || applications.size() < 1)) {
            btnAssign.setEnabled(false);
            return;
        }

        boolean allowAssignTeam = SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_YOURSELF,
                RolesConstants.APPLICATION_ASSIGN_TO_OTHERS);
        boolean allowAssignUser = SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_OTHERS);

        usersList.setSelectedUserById(SecurityBean.getCurrentUser().getId());
        cbxUsers.setEnabled(allowAssignUser);
        cbxTeam.setEnabled(allowAssignTeam);
        btnAssign.setEnabled(allowAssignTeam || allowAssignUser);

        // User cannot reassign the application to another user. Setup the teams so that they 
        // can only select a team they are part of. 
        if (!allowAssignUser && SecurityBean.getCurrentUser().getTeamIds() != null) {
            teamListBean.filterParties(SecurityBean.getCurrentUser().getTeamIds());
            teamListBean.setSelectedPartyById(SecurityBean.getCurrentUser().getTeamIds().get(0));
        }

        // Reset the default team based on the team previously set for the job
        if (app != null && app.getAgentId() != null) {
            teamListBean.setSelectedPartyById(app.getAgentId());
        }
        if (applications != null && applications.size() == 1
                && applications.get(0).getAgentId() != null) {
            teamListBean.setSelectedPartyById(applications.get(0).getAgentId());
        }
    }

    /**
     * Assign applications
     */
    private void assign() {
        if (usersList.getSelectedUser() == null) {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_NOSEL_USER);
            return;
        }
        final String teamId = teamListBean.getSelectedPartySummaryBean() == null
                || teamListBean.getSelectedPartySummaryBean().getEntityAction() == EntityAction.DISASSOCIATE
                ? null : teamListBean.getSelectedPartySummaryBean().getId();

        final ApplicationAssignmentDialog dialog = this;

        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_ASSIGN_JOB));
                if (app != null) {
                    app.assignUser(usersList.getSelectedUser().getId(), teamId);
                } else {
                    for (ApplicationSearchResultBean app : applications) {
                        ApplicationBean.assignUser(app, usersList.getSelectedUser().getId(), teamId);
                    }
                }
                return null;
            }

            @Override
            protected void taskDone() {
                MessageUtility.displayMessage(ClientMessage.APPLICATION_ASSIGNED);
                dialog.firePropertyChange(ASSIGNMENT_CHANGED, false, true);
                dialog.dispose();
            }

        };
        TaskManager.getInstance().runTask(t);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        usersList = new org.sola.clients.beans.security.UserSearchResultListBean();
        teamListBean = createPartySummaryList();
        jToolBar1 = new javax.swing.JToolBar();
        btnAssign = new javax.swing.JButton();
        pnlAssignee = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbxUsers = new javax.swing.JComboBox();
        pnlTeam = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbxTeam = new javax.swing.JComboBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        setTitle(bundle.getString("ApplicationAssignmentDialog.title")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAssign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        btnAssign.setText(bundle.getString("ApplicationAssignmentDialog.btnAssign.text")); // NOI18N
        btnAssign.setFocusable(false);
        btnAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAssign);

        jLabel1.setText(bundle.getString("ApplicationAssignmentDialog.jLabel1.text")); // NOI18N

        cbxUsers.setEnabled(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${users}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, usersList, eLProperty, cbxUsers);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, usersList, org.jdesktop.beansbinding.ELProperty.create("${selectedUser}"), cbxUsers, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout pnlAssigneeLayout = new javax.swing.GroupLayout(pnlAssignee);
        pnlAssignee.setLayout(pnlAssigneeLayout);
        pnlAssigneeLayout.setHorizontalGroup(
            pnlAssigneeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbxUsers, 0, 252, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlAssigneeLayout.setVerticalGroup(
            pnlAssigneeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAssigneeLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText(bundle.getString("ApplicationAssignmentDialog.jLabel2.text")); // NOI18N

        cbxTeam.setEnabled(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${partySummaryList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, teamListBean, eLProperty, cbxTeam);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, teamListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedPartySummaryBean}"), cbxTeam, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout pnlTeamLayout = new javax.swing.GroupLayout(pnlTeam);
        pnlTeam.setLayout(pnlTeamLayout);
        pnlTeamLayout.setHorizontalGroup(
            pnlTeamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cbxTeam, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlTeamLayout.setVerticalGroup(
            pnlTeamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTeamLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTeam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlAssignee, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlAssignee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignActionPerformed
        assign();
    }//GEN-LAST:event_btnAssignActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAssign;
    private javax.swing.JComboBox cbxTeam;
    private javax.swing.JComboBox cbxUsers;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel pnlAssignee;
    private javax.swing.JPanel pnlTeam;
    private org.sola.clients.beans.party.PartySummaryListBean teamListBean;
    private org.sola.clients.beans.security.UserSearchResultListBean usersList;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
