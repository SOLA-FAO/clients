/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.swing.desktop.application;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.util.Locale;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.security.UserSummaryBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.application.ApplicationDetailsPanel;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This dialog form is used to assign application to the user or unassign it
 * from him. <br />{@link UsersListBean} is used to bind the data on the form.
 */
public class ApplicationAssignmentPanel extends ContentPanel {

    private String applicationId;

    /**
     * This method is used by the dialog designer to create application details
     * component. It uses
     * <code>applicationId</code> parameter passed to the dialog constructor.<br
     * />
     * <code>applicationId</code> should be initialized before
     * {@link ApplicationAssignmentForm#initComponents} method call.
     */
    private ApplicationDetailsPanel createAppDetailsPanel() {
        ApplicationDetailsPanel panel;
        if (applicationId != null) {
            panel = new ApplicationDetailsPanel(applicationId);
        } else {
            panel = new ApplicationDetailsPanel();
        }
        return panel;
    }

    /**
     * Initializes components.
     *
     * @param parent Parent component.
     * @param modal Boolean value indicating modal state of the dialog.
     * @param applicationId ID of application to display.
     */
    public ApplicationAssignmentPanel(String applicationId) {
        super();
        this.applicationId = applicationId;
        initComponents();

        if (pnlApplicationDetails.getApplicationBean().getAssigneeId() != null
                && !pnlApplicationDetails.getApplicationBean().getAssigneeId().equals("")) {
            btnAssign.setText("Unassign");
            usersListBean1.setSelectedUserById(pnlApplicationDetails.getApplicationBean().getAssigneeId());
        } else {
            btnAssign.setText("Assign");
            usersListBean1.setSelectedUserById(SecurityBean.getCurrentUser().getId());
        }
        customizeForm();
    }

    /**
     * Enables or disables button, depending on user rights.
     */
    private void customizeForm() {
        cbxUsers.setEnabled(false);
        if (pnlApplicationDetails.getApplicationBean().getAssigneeId() != null
                && !pnlApplicationDetails.getApplicationBean().getAssigneeId().equals("")) {
            if (usersListBean1.getSelectedUser().getId().equals(SecurityBean.getCurrentUser().getId())) {
                btnAssign.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_YOURSELF));
            } else {
                btnAssign.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_OTHERS));
            }
        } else {
            cbxUsers.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_OTHERS));
            btnAssign.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_OTHERS));

            if (usersListBean1.getSelectedUser() != null && !btnAssign.isEnabled()
                    && usersListBean1.getSelectedUser().getId().equals(SecurityBean.getCurrentUser().getId())) {
                btnAssign.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_YOURSELF));
            }
        }
    }

    /**
     * Designer generated code
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        usersListBean1 = new org.sola.clients.beans.security.UserSearchResultListBean();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlApplicationDetails = createAppDetailsPanel();
        btnAssign = new javax.swing.JButton();
        cbxUsers = new javax.swing.JComboBox();
        labAssignto = new javax.swing.JLabel();

        setHeaderPanel(headerPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        setHelpTopic(bundle.getString("ApplicationAssignmentPanel.helpTopic")); // NOI18N
        setName("Form"); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("ApplicationAssignmentPanel.headerPanel.titleText")); // NOI18N

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        pnlApplicationDetails.setName("pnlApplicationDetails"); // NOI18N
        pnlApplicationDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        jScrollPane1.setViewportView(pnlApplicationDetails);

        btnAssign.setText(bundle.getString("ApplicationAssignmentPanel.btnAssign.text")); // NOI18N
        btnAssign.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAssign.setName("btnAssign"); // NOI18N
        btnAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignActionPerformed(evt);
            }
        });

        cbxUsers.setEnabled(false);
        cbxUsers.setName("cbxUsers"); // NOI18N
        cbxUsers.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        cbxUsers.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof UserSummaryBean) {
                    UserSummaryBean usr = (UserSummaryBean)value;
                    if(usr != null){
                        setText(usr.getFullUserName());
                    }else{
                        setText("");
                    }
                }
                else{
                    setText("");
                }
                return this;
            }
        });

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${users}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, usersListBean1, eLProperty, cbxUsers, "");
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, usersListBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedUser}"), cbxUsers, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        labAssignto.setFont(LafManager.getInstance().getLabFontBold());
        labAssignto.setText(bundle.getString("ApplicationAssignmentPanel.labAssignto.text")); // NOI18N
        labAssignto.setName("labAssignto"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 826, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labAssignto)
                .addGap(18, 18, 18)
                .addComponent(cbxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(420, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 826, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labAssignto)
                            .addComponent(cbxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAssign))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignActionPerformed
        if (!pnlApplicationDetails.getApplicationBean().isFeePaid()) {
            MessageUtility.displayMessage(ClientMessage.CHECK_FEES_NOT_PAID);
            return;
        }
        if (pnlApplicationDetails.getApplicationBean().getAssigneeId() != null
                && !pnlApplicationDetails.getApplicationBean().getAssigneeId().equals("")) {
            unassign();
        } else {
            assign();
        }
}//GEN-LAST:event_btnAssignActionPerformed

    /**
     * Assigns application to the selected user.
     */
    private void assign() {
        if (usersListBean1.getSelectedUser() == null) {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_NOSEL_USER);
            return;
        }
        if (MessageUtility.displayMessage(ClientMessage.APPLICATION_ASSIGN,
                new String[]{usersListBean1.getSelectedUser().getFullUserName()})
                == MessageUtility.BUTTON_ONE) {

            String old = pnlApplicationDetails.getApplicationBean().getAssigneeId();

            if (pnlApplicationDetails.getApplicationBean().assignUser(
                    usersListBean1.getSelectedUser().getId())) {

                MessageUtility.displayMessage(ClientMessage.APPLICATION_ASSIGNED);
                this.firePropertyChange(ApplicationBean.ASSIGNEE_ID_PROPERTY, old,
                        usersListBean1.getSelectedUser().getId());
            }
        }
    }

    /**
     * Unassigns application from the selected user.
     */
    private void unassign() {
        if (usersListBean1.getSelectedUser() == null) {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_NOSEL_USER);
            return;
        }

        if (MessageUtility.displayMessage(ClientMessage.APPLICATION_UNASSIGN,
                new String[]{usersListBean1.getSelectedUser().getFullUserName()})
                == MessageUtility.BUTTON_ONE) {

            String old = pnlApplicationDetails.getApplicationBean().getAssigneeId();

            if (pnlApplicationDetails.getApplicationBean().assignUser(null)) {
                MessageUtility.displayMessage(ClientMessage.APPLICATION_UNASSIGNED);
                this.firePropertyChange(ApplicationBean.ASSIGNEE_ID_PROPERTY, old, null);
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAssign;
    private javax.swing.JComboBox cbxUsers;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labAssignto;
    private org.sola.clients.swing.ui.application.ApplicationDetailsPanel pnlApplicationDetails;
    private org.sola.clients.beans.security.UserSearchResultListBean usersListBean1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
