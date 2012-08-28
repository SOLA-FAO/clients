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
package org.sola.clients.swing.desktop.party;

import java.util.ResourceBundle;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Used to create or edit party object.
 */
public class PartyPanelForm extends ContentPanel {

    public static final String PARTY_SAVED = "partySaved";
    private boolean savePartyOnAction;
    private boolean readOnly;
    private boolean closeOnSave;
    private PartyBean partyBean;
    private ResourceBundle resourceBundle;

    /**
     * Default form constructor.
     */
    public PartyPanelForm() {
        this(true);
    }

    /**
     * Form constructor.
     *
     * @param savePartyOnAction If
     * <code>true</code>, party will be saved into database. If
     * <code>false</code>, party will be validated and validation result
     * returned as a value of
     * {@link PartyPanel.PARTY_SAVED} property change event.
     */
    public PartyPanelForm(boolean savePartyOnAction) {
        this(savePartyOnAction, null, false, false);
    }

    /**
     * Form constructor.
     *
     * @param savePartyOnAction If
     * <code>true</code>, party will be saved into database. If
     * <code>false</code>, party will be validated and validation result
     * returned as a value of
     * {@link PartyPanel.PARTY_SAVED} property change event.
     * @param partyBean The party bean instance to show on the panel.
     * @param readOnly Indicates whether to display provided {@link PartyBean}
     * in read only mode or not.
     * @param closeOnSave Indicates whether to close the form upon save action
     * takes place.
     */
    public PartyPanelForm(boolean savePartyOnAction, PartyBean partyBean, boolean readOnly, boolean closeOnSave) {
        this.readOnly = readOnly;
        this.partyBean = partyBean;
        this.savePartyOnAction = savePartyOnAction;
        this.closeOnSave = closeOnSave;
        resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/party/Bundle");

        initComponents();
        customizePanel();
        savePartyState();
    }

    /**
     * Form constructor.
     *
     * @param savePartyOnAction If
     * <code>true</code>, party will be saved into database. If
     * <code>false</code>, party will be validated and validation result
     * returned as a value of
     * {@link PartyPanel.PARTY_SAVED} property change event.
     * @param partySummaryBean The party summary bean instance to retrieve
     * actual {@link PartyBean} to show on the panel.
     * @param readOnly Indicates whether to display provided {@link PartyBean}
     * in read only mode or not.
     * @param closeOnSave Indicates whether to close the form upon save action
     * takes place.
     */
    public PartyPanelForm(boolean savePartyOnAction, PartySummaryBean partySummaryBean,
            boolean readOnly, boolean closeOnSave) {
        this.savePartyOnAction = savePartyOnAction;
        this.closeOnSave = closeOnSave;
        this.readOnly = readOnly;
        resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/party/Bundle");
        if (partySummaryBean != null) {
            this.partyBean = partySummaryBean.getPartyBean();
        }

        initComponents();
        customizePanel();
        savePartyState();
    }

    public boolean isCloseOnSave() {
        return closeOnSave;
    }

    public void setCloseOnSave(boolean closeOnSave) {
        this.closeOnSave = closeOnSave;
        customizePanel();
    }

    public boolean isSavePartyOnAction() {
        return savePartyOnAction;
    }

    public void setSavePartyOnAction(boolean savePartyOnAction) {
        this.savePartyOnAction = savePartyOnAction;
    }

    public PartyBean getParty() {
        return partyPanel.getPartyBean();
    }

    public void setParty(PartyBean partyBean) {
        this.partyBean = partyBean;
        partyPanel.setPartyBean(partyBean);
        customizePanel();
        savePartyState();
    }

    private org.sola.clients.swing.ui.party.PartyPanel createPartyPanel() {
        org.sola.clients.swing.ui.party.PartyPanel panel;
        panel = new org.sola.clients.swing.ui.party.PartyPanel(partyBean, readOnly);
        return panel;
    }

    private void customizePanel() {
        if (!readOnly) {
            this.readOnly = !SecurityBean.isInRole(RolesConstants.PARTY_SAVE);
        }

        btnSave.setEnabled(!readOnly);

        if (partyBean != null) {
            headerPanel.setTitleText(String.format(resourceBundle.getString("PartyPanelForm.headerPanel.titleText2"),
                    partyBean.getName(), partyBean.getLastName() == null ? "" : partyBean.getLastName()));
        } else {
            headerPanel.setTitleText(resourceBundle.getString("PartyPanelForm.headerPanel.titleText"));
        }

        if (closeOnSave) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_SAVE_AND_CLOSE).getMessage());
            
            partyPanel.jPanel1.setVisible(false);
            partyPanel.groupPanel1.setVisible(false);
            
        } else {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_SAVE).getMessage());
        }
    }

    private void saveParty(final boolean allowClose) {
        if (savePartyOnAction) {
            SolaTask<Boolean, Boolean> t = new SolaTask<Boolean, Boolean>() {

                @Override
                public Boolean doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SAVING));
                    return partyPanel.saveParty();
                }

                @Override
                public void taskDone() {
                    if (get() != null && get()) {
                        customizePanel();
                        firePropertyChange(PARTY_SAVED, false, true);
                        if (closeOnSave || allowClose) {
                            close();
                        } else {
                            MessageUtility.displayMessage(ClientMessage.PARTY_SAVED);
                            savePartyState();
                        }
                    }
                }
            };
            TaskManager.getInstance().runTask(t);
        } else {
            if (partyPanel.validateParty(true)) {
                customizePanel();
                firePropertyChange(PARTY_SAVED, false, true);
                if (closeOnSave && allowClose) {
                    close();
                }
            }
        }
    }

    private void savePartyState() {
        MainForm.saveBeanState(partyPanel.getPartyBean());
    }

    @Override
    protected boolean panelClosing() {
        if (btnSave.isEnabled() && savePartyOnAction && MainForm.checkSaveBeforeClose(partyPanel.getPartyBean())) {
            saveParty(true);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        partyPanel = createPartyPanel();

        setHeaderPanel(headerPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/party/Bundle"); // NOI18N
        setHelpTopic(bundle.getString("PartyPanelForm.helpTopic")); // NOI18N
        setName("Form"); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("PartyPanelForm.headerPanel.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("PartyPanelForm.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        partyPanel.setName("partyPanel"); // NOI18N
        jScrollPane1.setViewportView(partyPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveParty(false);
    }//GEN-LAST:event_btnSaveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.swing.ui.party.PartyPanel partyPanel;
    // End of variables declaration//GEN-END:variables
}
