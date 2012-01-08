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
package org.sola.clients.swing.desktop.application;

import org.sola.clients.swing.ui.application.ApplicationDetailsPanel;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.util.Locale;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.UIManager;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.security.UserSummaryBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/** This dialog form is used to assign application to the user or unassign it from him.
 * <br />{@link UsersListBean} is used to bind the data on the form.
 */
public class ApplicationAssignmentForm extends javax.swing.JDialog {

    private String applicationId;
    Object foreFont = LafManager.getInstance().getForeFont();
    Object labFont = LafManager.getInstance().getLabFont();
    Object bgFont = LafManager.getInstance().getBgFont();
    Object txtFont = LafManager.getInstance().getTxtFont();
    Object txtAreaFont = LafManager.getInstance().getTxtAreaFont();
    Object btnFont = LafManager.getInstance().getBtnFont();
    Object tabFont = LafManager.getInstance().getTabFont();
    Object cmbFont = LafManager.getInstance().getCmbFont();
    Object btnBackground = LafManager.getInstance().getBtnBackground();

    /** This method is used by the dialog designer to create application details component. 
     * It uses <code>applicationId</code> parameter passed to the dialog constructor.<br />
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

    /** Initializes components.
     * @param parent Parent component.
     * @param modal Boolean value indicating modal state of the dialog.
     * @param applicationId ID of application to display.
     */
    public ApplicationAssignmentForm(java.awt.Frame parent, boolean modal, String applicationId) {
        super(parent, modal);
        this.applicationId = applicationId;
        initComponents();
        this.setIconImage(new ImageIcon(ApplicationAssignmentForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());

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

    /** Enables or disables button, depending on user rights. */
    private void customizeForm() {
        if (pnlApplicationDetails.getApplicationBean().getAssigneeId() != null
                && !pnlApplicationDetails.getApplicationBean().getAssigneeId().equals("")) {
            if (pnlApplicationDetails.getApplicationBean().getAssigneeId().equals(SecurityBean.getCurrentUser().getId())) {
                btnAssign.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_YOURSELF));
            } else {
                btnAssign.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_UNASSIGN_FROM_OTHERS));
            }
        } else {
            if (usersListBean1.getSelectedUser() != null) {
                if (usersListBean1.getSelectedUser().getId().equals(SecurityBean.getCurrentUser().getId())) {
                    btnAssign.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_YOURSELF));
                } else {
                    btnAssign.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_OTHERS));
                }
            }
        }
    }

    /** Designer generated code */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        usersListBean1 = new org.sola.clients.beans.security.UserSearchResultListBean();
        assignPanel = new javax.swing.JPanel();
        btnAssign = new javax.swing.JButton();
        cbxUsers = new javax.swing.JComboBox();
        labAssignto = new javax.swing.JLabel();
        pnlApplicationDetails = createAppDetailsPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(ApplicationAssignmentForm.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        assignPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        assignPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        assignPanel.setName("assignPanel"); // NOI18N

        btnAssign.setBackground(UIManager.getColor(btnBackground));
        btnAssign.setFont(UIManager.getFont(cmbFont));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        btnAssign.setText(bundle.getString("ApplicationDetailsForm.btnAssign.text")); // NOI18N
        btnAssign.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAssign.setName("btnAssign"); // NOI18N
        btnAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignActionPerformed(evt);
            }
        });

        cbxUsers.setFont(UIManager.getFont(cmbFont)); // NOI18N
        cbxUsers.setForeground(resourceMap.getColor("cbxUsers.foreground")); // NOI18N
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

        labAssignto.setFont(resourceMap.getFont("labAssignto.font")); // NOI18N
        labAssignto.setText(bundle.getString("ApplicationDetailsForm.labAssignto.text")); // NOI18N
        labAssignto.setName("labAssignto"); // NOI18N
        labAssignto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labAssigntoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout assignPanelLayout = new javax.swing.GroupLayout(assignPanel);
        assignPanel.setLayout(assignPanelLayout);
        assignPanelLayout.setHorizontalGroup(
            assignPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(assignPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labAssignto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAssign, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(477, Short.MAX_VALUE))
        );
        assignPanelLayout.setVerticalGroup(
            assignPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(assignPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(labAssignto)
                .addComponent(cbxUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(btnAssign))
        );

        pnlApplicationDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pnlApplicationDetails.setName("pnlApplicationDetails"); // NOI18N
        pnlApplicationDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlApplicationDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(assignPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(assignPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlApplicationDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void labAssigntoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labAssigntoMouseClicked
        // TODO add your handling code here:
}//GEN-LAST:event_labAssigntoMouseClicked

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

    /** Assigns application to the selected user.*/
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
                this.dispose();
            }
        }
    }

    /** Unassigns application from the selected user.*/
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
                this.dispose();
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel assignPanel;
    private javax.swing.JButton btnAssign;
    private javax.swing.JComboBox cbxUsers;
    private javax.swing.JLabel labAssignto;
    private org.sola.clients.swing.ui.application.ApplicationDetailsPanel pnlApplicationDetails;
    private org.sola.clients.beans.security.UserSearchResultListBean usersListBean1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
