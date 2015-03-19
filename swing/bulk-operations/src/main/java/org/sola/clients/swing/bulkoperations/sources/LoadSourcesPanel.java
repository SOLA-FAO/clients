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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.bulkoperations.sources;

import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.sola.clients.swing.bulkoperations.beans.SourceBulkMoveBean;
import org.sola.clients.swing.bulkoperations.ValidationResultPanel;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * The panel that handles the bulk operations for loading documents (sources).
 * 
 * @author Elton Manoku
 */
public class LoadSourcesPanel extends ContentPanel {

    private static String PANEL_NAME = "LOAD_SOURCES_PANEL";
    private static java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(
            "org/sola/clients/swing/bulkoperations/sources/Bundle");
    private SourceBulkMoveBean bulkMove = new SourceBulkMoveBean();

    /**
     * Creates new form LoadSourcesPanel
     */
    public LoadSourcesPanel() {
        initComponents();
        this.setName(PANEL_NAME);
        folderChooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return bundle.getString("LoadSourcesPanel.folderChooser.DirectoriesOnlyText");
            }
        });        
        setPostLoadEnabled(false);
    }

    private void setPostLoadEnabled(boolean enable) {
        btnRollback.setEnabled(enable);
        String informationResourceName = "LoadSourcesPanel.lblInformationText.text";
        if (!enable) {
            bulkMove.reset();
        } else {
            informationResourceName = bulkMove.getValidationResults().isEmpty()
                    ? "LoadSourcesPanel.lblInformationText.text.success"
                    : "LoadSourcesPanel.lblInformationText.text.problem";
        }
        lblInformationText.setText(bundle.getString(informationResourceName));
        btnValidations.setEnabled(!bulkMove.getValidationResults().isEmpty());
    }

    private void convertAndSendToServer() {
        setPostLoadEnabled(false);
        if (folderChooser.getSelectedFile() == null){
            folderChooser.setSelectedFile(folderChooser.getCurrentDirectory());
        }
        bulkMove.setBaseFolder(folderChooser.getSelectedFile());

        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(
                        ClientMessage.BULK_OPERATIONS_LOAD_SOURCE_AND_SENDTOSERVER));
                bulkMove.sendToServer();
                return null;
            }

            @Override
            protected void taskDone() {
                super.taskDone();
                setPostLoadEnabled(true);
            }
        };
        TaskManager.getInstance().runTask(t);

    }

    private void rollback() {
        if (bulkMove.getTransaction() != null) {
            bulkMove.getTransaction().reject();
        }
        setPostLoadEnabled(false);
    }

    private void openValidations() {
        ValidationResultPanel validationPanel = new ValidationResultPanel();
        validationPanel.getValidationResultList().addAll(bulkMove.getValidationResults());
        getMainContentPanel().addPanel(validationPanel, validationPanel.getName(), true);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        folderChooser = new javax.swing.JFileChooser();
        btnLoad = new javax.swing.JButton();
        lblInformation = new javax.swing.JLabel();
        lblInformationText = new javax.swing.JLabel();
        btnRollback = new javax.swing.JButton();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        btnValidations = new javax.swing.JButton();

        setHeaderPanel(headerPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/bulkoperations/sources/Bundle"); // NOI18N
        setHelpTopic(bundle.getString("LoadSourcePanel.helptopic")); // NOI18N

        headerPanel.setTitleText(bundle.getString("LoadSourcesPanel.headerPanel.titleText")); // NOI18N

        folderChooser.setAcceptAllFileFilterUsed(false);
        folderChooser.setControlButtonsAreShown(false);
        folderChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        btnLoad.setText(bundle.getString("LoadSourcesPanel.btnLoad.text")); // NOI18N
        btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoadActionPerformed(evt);
            }
        });

        lblInformation.setText(bundle.getString("LoadSourcesPanel.lblInformation.text")); // NOI18N

        lblInformationText.setText(bundle.getString("LoadSourcesPanel.lblInformationText.text")); // NOI18N

        btnRollback.setText(bundle.getString("LoadSourcesPanel.btnRollback.text")); // NOI18N
        btnRollback.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRollbackActionPerformed(evt);
            }
        });

        groupPanel1.setTitleText(bundle.getString("LoadSourcesPanel.groupPanel1.titleText")); // NOI18N

        groupPanel2.setTitleText(bundle.getString("LoadSourcesPanel.groupPanel2.titleText")); // NOI18N

        btnValidations.setText(bundle.getString("LoadSourcesPanel.btnValidations.text")); // NOI18N
        btnValidations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidationsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(btnLoad)
                        .addGap(18, 18, 18)
                        .addComponent(lblInformation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblInformationText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnValidations)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRollback))
                    .addComponent(folderChooser, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(groupPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(folderChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(groupPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLoad)
                    .addComponent(lblInformation)
                    .addComponent(lblInformationText)
                    .addComponent(btnRollback)
                    .addComponent(btnValidations))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoadActionPerformed
        convertAndSendToServer();
    }//GEN-LAST:event_btnLoadActionPerformed

    private void btnRollbackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRollbackActionPerformed
        rollback();
    }//GEN-LAST:event_btnRollbackActionPerformed

    private void btnValidationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidationsActionPerformed
        openValidations();
    }//GEN-LAST:event_btnValidationsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoad;
    private javax.swing.JButton btnRollback;
    private javax.swing.JButton btnValidations;
    private javax.swing.JFileChooser folderChooser;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel lblInformation;
    private javax.swing.JLabel lblInformationText;
    // End of variables declaration//GEN-END:variables
}
