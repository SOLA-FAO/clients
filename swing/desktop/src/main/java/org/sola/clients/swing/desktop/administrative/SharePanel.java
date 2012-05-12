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
package org.sola.clients.swing.desktop.administrative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.RrrShareBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.party.PartyPanelForm;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.FormattersFactory;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Form for managing ownership shares
 */
public class SharePanel extends ContentPanel {

    private class RightHolderFormListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PartyPanelForm.PARTY_SAVED)) {
                rrrShareBean.addOrUpdateRightholder((PartySummaryBean) ((PartyPanelForm) evt.getSource()).getParty());
                tableOwners.clearSelection();
            }
        }
    }
    
    private RrrBean.RRR_ACTION rrrAction;
    public static final String UPDATED_RRR_SHARE = "updatedRrrShare";

    public SharePanel(RrrShareBean rrrShareBean, RrrBean.RRR_ACTION rrrAction) {
        this.rrrAction = rrrAction;
        prepareRrrShareBean(rrrShareBean);

        initComponents();

        customizeForm(rrrAction);
        customizeOwnersButtons(null);
        saveRrrShareState();
    }

    private RrrShareBean CreateRrrShareBean() {
        if (rrrShareBean == null) {
            rrrShareBean = new RrrShareBean();
        }
        return rrrShareBean;
    }

    private void prepareRrrShareBean(RrrShareBean rrrShareBean) {
        if (rrrShareBean == null) {
            this.rrrShareBean = new RrrShareBean();
        } else {
            this.rrrShareBean = rrrShareBean.copy();
        }
    }

    private void customizeForm(RrrBean.RRR_ACTION rrrAction) {
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
        } else if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            btnSave.setEnabled(false);
            txtNominator.setEditable(false);
            txtDenominator.setEditable(false);
            btnAddOwner.setEnabled(false);
            btnRemoveOwner.setEnabled(false);
        }

        rrrShareBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RrrShareBean.SELECTED_RIGHTHOLDER_PROPERTY)) {
                    customizeOwnersButtons((PartySummaryBean) evt.getNewValue());
                }
            }
        });
    }

    /**
     * Enables or disables rightholders buttons, depending on selection in the
     * list of rightholders and user rights.
     */
    private void customizeOwnersButtons(PartySummaryBean party) {
        boolean isReadOnly = rrrAction == RrrBean.RRR_ACTION.VIEW;

        btnAddOwner.setEnabled(!isReadOnly);
        btnEditOwner.setEnabled(party != null && !isReadOnly);
        btnRemoveOwner.setEnabled(party != null && !isReadOnly);
        btnViewOwner.setEnabled(party != null);

        menuAddOwner.setEnabled(btnAddOwner.isEnabled());
        menuEditOwner.setEnabled(btnEditOwner.isEnabled());
        menuRemoveOwner.setEnabled(btnRemoveOwner.isEnabled());
        menuViewOwner.setEnabled(btnViewOwner.isEnabled());
    }

    private void openRightHolderForm(final PartySummaryBean partySummaryBean, final boolean isReadOnly) {
        final RightHolderFormListener listener = new RightHolderFormListener();

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
                PartyPanelForm partyForm;

                if (partySummaryBean != null) {
                    partyForm = new PartyPanelForm(true, partySummaryBean, isReadOnly, true);
                } else {
                    partyForm = new PartyPanelForm(true, null, isReadOnly, true);
                }
                partyForm.addPropertyChangeListener(listener);
                getMainContentPanel().addPanel(partyForm, MainContentPanel.CARD_PERSON, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private boolean saveRrrShare() {
        if (rrrShareBean.validate(true).size() < 1) {
            firePropertyChange(UPDATED_RRR_SHARE, null, rrrShareBean);
            close();
            return true;
        }
        return false;
    }

    private void saveRrrShareState() {
        MainForm.saveBeanState(rrrShareBean);
    }

    @Override
    protected boolean panelClosing() {
        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(rrrShareBean)) {
            return saveRrrShare();
        }
        return true;
    }
    
    private void viewOwner() {
        if (rrrShareBean.getSelectedRightHolder() != null) {
            openRightHolderForm(rrrShareBean.getSelectedRightHolder(), true);
        }
    }

    private void removeOwner() {
        if (rrrShareBean.getSelectedRightHolder() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            rrrShareBean.removeSelectedRightHolder();
        }
    }

    private void addOwner() {
        openRightHolderForm(null, false);
    }

    private void editOwner() {
        if (rrrShareBean.getSelectedRightHolder() != null) {
            openRightHolderForm(rrrShareBean.getSelectedRightHolder(), false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        rrrShareBean = CreateRrrShareBean();
        popupOwners = new javax.swing.JPopupMenu();
        menuAddOwner = new javax.swing.JMenuItem();
        menuEditOwner = new javax.swing.JMenuItem();
        menuRemoveOwner = new javax.swing.JMenuItem();
        menuViewOwner = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDenominator = new javax.swing.JFormattedTextField();
        txtNominator = new javax.swing.JFormattedTextField();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddOwner = new javax.swing.JButton();
        btnEditOwner = new javax.swing.JButton();
        btnRemoveOwner = new javax.swing.JButton();
        btnViewOwner = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableOwners = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar2 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();

        popupOwners.setName("popupOwners"); // NOI18N

        menuAddOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        menuAddOwner.setText(bundle.getString("SharePanel.menuAddOwner.text")); // NOI18N
        menuAddOwner.setName("menuAddOwner"); // NOI18N
        menuAddOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddOwnerActionPerformed(evt);
            }
        });
        popupOwners.add(menuAddOwner);

        menuEditOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditOwner.setText(bundle.getString("SharePanel.menuEditOwner.text")); // NOI18N
        menuEditOwner.setName("menuEditOwner"); // NOI18N
        menuEditOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditOwnerActionPerformed(evt);
            }
        });
        popupOwners.add(menuEditOwner);

        menuRemoveOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveOwner.setText(bundle.getString("SharePanel.menuRemoveOwner.text")); // NOI18N
        menuRemoveOwner.setName("menuRemoveOwner"); // NOI18N
        menuRemoveOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveOwnerActionPerformed(evt);
            }
        });
        popupOwners.add(menuRemoveOwner);

        menuViewOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewOwner.setText(bundle.getString("SharePanel.menuViewOwner.text")); // NOI18N
        menuViewOwner.setName("menuViewOwner"); // NOI18N
        menuViewOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewOwnerActionPerformed(evt);
            }
        });
        popupOwners.add(menuViewOwner);

        setHeaderPanel(headerPanel);
        setHelpTopic(bundle.getString("SharePanel.helpTopic")); // NOI18N
        setName("Form"); // NOI18N

        jLabel1.setFont(LafManager.getInstance().getLabFontBold());
        jLabel1.setText(bundle.getString("SharePanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(bundle.getString("SharePanel.jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(bundle.getString("SharePanel.jLabel2.toolTipText")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtDenominator.setFormatterFactory(FormattersFactory.getInstance().getShortFormatterFactory());
        txtDenominator.setName("txtDenominator"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrShareBean, org.jdesktop.beansbinding.ELProperty.create("${denominator}"), txtDenominator, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        txtNominator.setFormatterFactory(FormattersFactory.getInstance().getShortFormatterFactory());
        txtNominator.setName("txtNominator"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrShareBean, org.jdesktop.beansbinding.ELProperty.create("${nominator}"), txtNominator, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("SharePanel.headerPanel.titleText")); // NOI18N

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("SharePanel.groupPanel1.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddOwner.setText(bundle.getString("SharePanel.btnAddOwner.text")); // NOI18N
        btnAddOwner.setName("btnAddOwner"); // NOI18N
        btnAddOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddOwnerActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddOwner);

        btnEditOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditOwner.setText(bundle.getString("SharePanel.btnEditOwner.text")); // NOI18N
        btnEditOwner.setFocusable(false);
        btnEditOwner.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditOwner.setName("btnEditOwner"); // NOI18N
        btnEditOwner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditOwnerActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEditOwner);

        btnRemoveOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveOwner.setText(bundle.getString("SharePanel.btnRemoveOwner.text")); // NOI18N
        btnRemoveOwner.setName("btnRemoveOwner"); // NOI18N
        btnRemoveOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveOwnerActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveOwner);

        btnViewOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewOwner.setText(bundle.getString("SharePanel.btnViewOwner.text")); // NOI18N
        btnViewOwner.setName("btnViewOwner"); // NOI18N
        btnViewOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewOwnerActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewOwner);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableOwners.setComponentPopupMenu(popupOwners);
        tableOwners.setName("tableOwners"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredRightHolderList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrShareBean, eLProperty, tableOwners);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${lastName}"));
        columnBinding.setColumnName("Last Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrShareBean, org.jdesktop.beansbinding.ELProperty.create("${selectedRightHolder}"), tableOwners, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableOwners);
        tableOwners.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SharePanel.tableOwners.columnModel.title0")); // NOI18N
        tableOwners.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SharePanel.tableOwners.columnModel.title1")); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("SharePanel.btnSave.text")); // NOI18N
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSave);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNominator, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDenominator, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 177, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtNominator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDenominator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveRrrShare();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddOwnerActionPerformed
        addOwner();
    }//GEN-LAST:event_btnAddOwnerActionPerformed

    private void btnRemoveOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveOwnerActionPerformed
        removeOwner();
    }//GEN-LAST:event_btnRemoveOwnerActionPerformed

    private void btnEditOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditOwnerActionPerformed
        editOwner();
    }//GEN-LAST:event_btnEditOwnerActionPerformed

    private void btnViewOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewOwnerActionPerformed
        viewOwner();
    }//GEN-LAST:event_btnViewOwnerActionPerformed

    private void menuAddOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddOwnerActionPerformed
        addOwner();
    }//GEN-LAST:event_menuAddOwnerActionPerformed

    private void menuEditOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditOwnerActionPerformed
        editOwner();
    }//GEN-LAST:event_menuEditOwnerActionPerformed

    private void menuRemoveOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveOwnerActionPerformed
        removeOwner();
    }//GEN-LAST:event_menuRemoveOwnerActionPerformed

    private void menuViewOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewOwnerActionPerformed
        viewOwner();
    }//GEN-LAST:event_menuViewOwnerActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddOwner;
    private javax.swing.JButton btnEditOwner;
    private javax.swing.JButton btnRemoveOwner;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnViewOwner;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JMenuItem menuAddOwner;
    private javax.swing.JMenuItem menuEditOwner;
    private javax.swing.JMenuItem menuRemoveOwner;
    private javax.swing.JMenuItem menuViewOwner;
    private javax.swing.JPopupMenu popupOwners;
    private org.sola.clients.beans.administrative.RrrShareBean rrrShareBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableOwners;
    private javax.swing.JFormattedTextField txtDenominator;
    private javax.swing.JFormattedTextField txtNominator;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
