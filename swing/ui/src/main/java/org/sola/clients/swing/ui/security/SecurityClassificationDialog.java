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
package org.sola.clients.swing.ui.security;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.security.RoleListBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityTable;

/**
 * This dialog form is used to configure the security classification and
 * redaction classification for a data record.
 */
public class SecurityClassificationDialog extends javax.swing.JDialog {
    
    public static final String CLASSIFICATION_CHANGED = "classificationChanged";
    private List<AbstractBindingBean> beanList;
    private AbstractBindingBean bean;
    private boolean showRedact = false;
    public EntityTable entityTable = null;

    /**
     * Default constructor
     *
     * @param list List of beans to assign the revised security classification
     * to
     * @param showRedact Flag to indicate if the redact column should be
     * displayed or not
     */
    public SecurityClassificationDialog(List<AbstractBindingBean> list,
            boolean showRedact, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.beanList = list;
        this.entityTable = null;
        this.showRedact = showRedact;
        initComponents();
        customizeForm();
    }

    /**
     * Supports re-classification of an individual bean
     *
     * @param bean
     * @param showRedact Flag to indicate if the redact column should be
     * displayed or not
     * @param parent
     * @param modal
     */
    public SecurityClassificationDialog(AbstractBindingBean bean,
            boolean showRedact, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.bean = bean;
        this.entityTable = null;
        this.showRedact = showRedact;
        initComponents();
        customizeForm();
    }

    /**
     * Loads the security role lists
     *
     * @param filterByClearance If true, only include security roles that the
     * user has clearance for
     * @return
     */
    private RoleListBean createSecurityRolesList(boolean filterByClearance) {
        RoleListBean list = new RoleListBean();
        list.loadSecurityRoles(filterByClearance);
        return list;
    }
    
    private void customizeForm() {
        
        URL imgURL = this.getClass().getResource("/images/sola/logo_icon.jpg");
        this.setIconImage(new ImageIcon(imgURL).getImage());
        
        WindowUtility.addEscapeListener(this, false);
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/security/Bundle");
        if (entityTable == null) {
            btnSave.setText(bundle1.getString("SecurityClassificationDialog.btnSave.close_text"));
            btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm-close.png")));
        } else {
            btnSave.setText(bundle1.getString("SecurityClassificationDialog.btnSave.text"));
            btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png")));
        }

        // Determine the default classificationCode and redactCode for the
        // bean or use the highest codes if there is a list of beans. 
        String classificationCode = null;
        String redactCode = null;
        if (bean != null) {
            classificationCode = bean.getClassificationCode();
            redactCode = bean.getRedactCode();
        } else if (beanList != null && beanList.size() > 0) {
            for (AbstractBindingBean b : beanList) {
                if (b.getClassificationCode() != null
                        && b.getClassificationCode().compareTo(classificationCode) > 0) {
                    classificationCode = b.getClassificationCode();
                }
                if (b.getRedactCode() != null
                        && b.getRedactCode().compareTo(redactCode) > 0) {
                    redactCode = b.getRedactCode();
                }
            }
        }
        
        boolean changeClassification = SecurityBean.isInRole(RolesConstants.CLASSIFICATION_CHANGE_CLASS);
        btnSave.setEnabled(changeClassification);
        cbxClassification.setEnabled(changeClassification);
        // Set the default values for the classification code and redact code. 
        classificationList.setSelectedRole(CacheManager.getBeanByCode(classificationList.getRoleListFiltered(),
                classificationCode));
        
        if (showRedact) {
            // Don't let the user change the redact settings unless they have clearance to do so. No need to
            // check the security classification as the user will not be given access to any records they
            // don't have a classification for. 
            cbxRedact.setEnabled(changeClassification && SecurityBean.hasSecurityClearance(redactCode));

            // Set the default values for the redact code.
            redactList.setSelectedRole(CacheManager.getBeanByCode(redactList.getRoleListFiltered(),
                    redactCode));
        } else {
            this.remove(pnlRedact);
        }
    }

    /**
     * If set to a non null value, the dialog will trigger an update to the
     * entity (or entities) to save changes to the security classification or
     * redact code.
     *
     * @param entityTable Enumeration identifying the SOLA table that must be
     * updated when saving security classification changes. If NULL, the dialog
     * will set the new security classification codes on the entity, but it will
     * not perform an explicity update
     */
    public void setSaveChanges(EntityTable entityTable) {
        this.entityTable = entityTable;
    }

    /**
     * Assign team to properties
     */
    private void save() {
        final String classificationCode = classificationList.getSelectedRole() == null ? null
                : classificationList.getSelectedRole().getCode();
        final String redactCode = redactList.getSelectedRole() == null ? null
                : redactList.getSelectedRole().getCode();
        final List<String> entityIds = new ArrayList<String>();
        
        if (bean != null) {
            bean.setClassificationCode(classificationCode);
            bean.setRedactCode(redactCode);
            if (AbstractIdBean.class.isAssignableFrom(bean.getClass())) {
                entityIds.add(((AbstractIdBean) bean).getId());
                
            }
        } else if (beanList != null && beanList.size() > 0) {
            for (AbstractBindingBean b : beanList) {
                b.setClassificationCode(classificationCode);
                b.setRedactCode(redactCode);
                if (AbstractIdBean.class.isAssignableFrom(b.getClass())) {
                    entityIds.add(((AbstractIdBean) b).getId());
                }
            }
        }
        if (entityTable != null) {
            // Save the changes directly
            final SecurityClassificationDialog dialog = this;
            SolaTask t = new SolaTask<Void, Void>() {
                
                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SAVE_SECURITY_CHANGES));
                    WSManager.getInstance().getAdminService().saveSecurityClassifications(entityIds,
                            entityTable, classificationCode, redactCode);
                    return null;
                }
                
                @Override
                protected void taskDone() {
                    dialog.firePropertyChange(CLASSIFICATION_CHANGED, false, true);
                    dialog.dispose();
                }
                
            };
            TaskManager.getInstance().runTask(t);
            
        } else {
            // Allow the calling form to determine when changes to the classification should be saved. 
            this.firePropertyChange(CLASSIFICATION_CHANGED, false, true);
            this.dispose();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        classificationList = createSecurityRolesList(true);
        redactList = createSecurityRolesList(true);
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        pnlClassification = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbxClassification = new javax.swing.JComboBox();
        pnlRedact = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbxRedact = new javax.swing.JComboBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/administrative/Bundle"); // NOI18N
        setTitle(bundle.getString("ApplicationAssignmentDialog.title")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/security/Bundle"); // NOI18N
        btnSave.setText(bundle1.getString("SecurityClassificationDialog.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        jLabel2.setText(bundle1.getString("SecurityClassificationDialog.jLabel2.text")); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${roleList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, classificationList, eLProperty, cbxClassification);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, classificationList, org.jdesktop.beansbinding.ELProperty.create("${selectedRole}"), cbxClassification, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout pnlClassificationLayout = new javax.swing.GroupLayout(pnlClassification);
        pnlClassification.setLayout(pnlClassificationLayout);
        pnlClassificationLayout.setHorizontalGroup(
            pnlClassificationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
            .addComponent(cbxClassification, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlClassificationLayout.setVerticalGroup(
            pnlClassificationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlClassificationLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxClassification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jLabel1.setText(bundle1.getString("SecurityClassificationDialog.jLabel1.text")); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${roleList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, redactList, eLProperty, cbxRedact);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, redactList, org.jdesktop.beansbinding.ELProperty.create("${selectedRole}"), cbxRedact, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout pnlRedactLayout = new javax.swing.GroupLayout(pnlRedact);
        pnlRedact.setLayout(pnlRedactLayout);
        pnlRedactLayout.setHorizontalGroup(
            pnlRedactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cbxRedact, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlRedactLayout.setVerticalGroup(
            pnlRedactLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRedactLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRedact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlRedact, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlClassification, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlClassification, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlRedact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cbxClassification;
    private javax.swing.JComboBox cbxRedact;
    private org.sola.clients.beans.security.RoleListBean classificationList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel pnlClassification;
    private javax.swing.JPanel pnlRedact;
    private org.sola.clients.beans.security.RoleListBean redactList;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
