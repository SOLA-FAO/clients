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
package org.sola.clients.swing.ui.administrative;

import java.net.URL;
import java.util.List;
import javax.swing.ImageIcon;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.administrative.BaUnitSearchResultBean;
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
 * This dialog form is used to assign properties from one team/property manager
 * to another
 */
public class PropertyAssignmentDialog extends javax.swing.JDialog {

    public static final String ASSIGNMENT_CHANGED = "assignmentChanged";
    private List<BaUnitSearchResultBean> properties;
    private BaUnitBean baUnit;

    /**
     * Default constructor
     *
     * @param properties List of properties to assign
     */
    public PropertyAssignmentDialog(List<BaUnitSearchResultBean> properties,
            java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.properties = properties;
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
    public PropertyAssignmentDialog(BaUnitBean baUnit,
            java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.baUnit = baUnit;
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

        if (baUnit == null && (properties == null || properties.size() < 1)) {
            btnAssign.setEnabled(false);
            return;
        }
        
        // Set the default team based on the team set on the job/property
        if (baUnit != null && baUnit.getPropertyManager() != null) {
            teamListBean.setSelectedPartyById(baUnit.getPropertyManager().getId());
        }
        
        boolean allowAssign = SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE, 
                RolesConstants.ADMINISTRATIVE_ASSIGN_TEAM); 
        btnAssign.setEnabled(allowAssign);
        cbxTeam.setEnabled(allowAssign);
    }

    /**
     * Assign team to properties
     */
    private void assign() {

        final String teamId = teamListBean.getSelectedPartySummaryBean() == null
                || teamListBean.getSelectedPartySummaryBean().getEntityAction() == EntityAction.DISASSOCIATE
                ? null : teamListBean.getSelectedPartySummaryBean().getId();

        final PropertyAssignmentDialog dialog = this;

        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_ASSIGN_PROP_MANAGER));
                if (baUnit != null) {
                    baUnit.assignTeam(teamId);
                } else {
                    BaUnitBean.assignTeam(properties, teamId);
                }
                return null;
            }

            @Override
            protected void taskDone() {
                MessageUtility.displayMessage(ClientMessage.BAUNIT_PROP_MANAGER_ASSIGNED);
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

        teamListBean = createPartySummaryList();
        jToolBar1 = new javax.swing.JToolBar();
        btnAssign = new javax.swing.JButton();
        pnlTeam = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbxTeam = new javax.swing.JComboBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/administrative/Bundle"); // NOI18N
        setTitle(bundle.getString("ApplicationAssignmentDialog.title")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAssign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        btnAssign.setText(bundle.getString("PropertyAssignmentDialog.btnAssign.text")); // NOI18N
        btnAssign.setFocusable(false);
        btnAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAssign);

        jLabel2.setText(bundle.getString("PropertyAssignmentDialog.jLabel2.text")); // NOI18N

        cbxTeam.setEnabled(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${partySummaryList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, teamListBean, eLProperty, cbxTeam);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, teamListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedPartySummaryBean}"), cbxTeam, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout pnlTeamLayout = new javax.swing.GroupLayout(pnlTeam);
        pnlTeam.setLayout(pnlTeamLayout);
        pnlTeamLayout.setHorizontalGroup(
            pnlTeamLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
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
                .addComponent(pnlTeam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel pnlTeam;
    private org.sola.clients.beans.party.PartySummaryListBean teamListBean;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
