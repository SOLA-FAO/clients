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
package org.sola.clients.swing.admin.system;

import java.util.concurrent.TimeUnit;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.services.boundary.wsclients.exception.WebServiceClientException;

/**
 *
 * @author Elton Manoku
 */
public class ConsolidationConsolidatePanel extends ContentPanel {

    public static final String PANEL_NAME = "ConsolidationConsolidatePanel";
    private String processName = "consolidate";
    private static final int PROGRESS_CHECK_INTERVAL_SECONDS = 1;

    /**
     * Creates new form ConsolidationExtractPanel
     */
    public ConsolidationConsolidatePanel() {
        initComponents();
        txtBackupPath.setText(WSManager.getInstance().getAdminService().getSetting("path-to-backup", ""));
    }

    private void run() {

        txtStatus.setText(MessageUtility.getLocalizedMessageText(
                ClientMessage.ADMIN_CONSOLIDATION_RUNNING));

        SolaTask taskConsolidate = new SolaTask<Void, Void>() {
            boolean failed = false;

            @Override
            public Void doTask() {
                txtLog.setText("");
                try {
                    WSManager.getInstance().getAdminService().setProcessProgress(
                            processName, WSManager.getInstance().getAdminService().getProcessProgress(
                            processName, false) + 10);
                    String extractedFile = "";
                    if (chkRestoreExtractedFile.isSelected()){
                        extractedFile = txtSourceInServer.getText();                        
                    }
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.ADMIN_CONSOLIDATION_RUNNING));
                    WSManager.getInstance().getAdminService().consolidationConsolidate(
                            extractedFile, chkMergeConsolidationSchema.isSelected());
                } catch (WebServiceClientException ex) {
                    failed = true;
                    txtStatus.setText(ex.getMessage());
                }
                return null;
            }

            @Override
            protected void taskDone() {
                super.taskDone();
                if (failed) {
                    txtStatus.setText(String.format("%s:%s",txtStatus.getText(), 
                            MessageUtility.getLocalizedMessageText(ClientMessage.ADMIN_CONSOLIDATION_FAILED)));
                }else{
                    txtStatus.setText(MessageUtility.getLocalizedMessageText(
                            ClientMessage.ADMIN_CONSOLIDATION_DONE));                    
                }
                // It can be that even the process if finished, still the progress bar is not yet 100 percent.
                // This makes sure. The number 10000 is a number which is the infinite max the 
                // progress can have
                WSManager.getInstance().getAdminService().setProcessProgress(processName, 10000);
            }
        };
        TaskManager.getInstance().runTask(taskConsolidate);

        SolaTask taskProcessProgress = new SolaTask<Void, Void>() {
            Integer progressValue = 0;

            @Override
            public Void doTask() {
                while (progressValue < 100) {
                    progressValue = WSManager.getInstance().getAdminService().getProcessProgress(
                            processName, true);
                    progressBarProcess.setValue(progressValue);
                    try {
                        TimeUnit.SECONDS.sleep(PROGRESS_CHECK_INTERVAL_SECONDS);
                    } catch (InterruptedException ex) {
                    }
                }
                return null;
            }

            @Override
            protected void taskDone() {
                super.taskDone();
            }
        };
        int maximumAllowedNumberOfTasks = TaskManager.getInstance().getMaximumAllowedNumberOfTasks();
        if (maximumAllowedNumberOfTasks <= TaskManager.getInstance().getNumberOfActiveTasks()) {
            TaskManager.getInstance().setMaximumAllowedNumberOfTasks(maximumAllowedNumberOfTasks + 1);
        }
        TaskManager.getInstance().runTask(taskProcessProgress);
        TaskManager.getInstance().setMaximumAllowedNumberOfTasks(maximumAllowedNumberOfTasks);
    }

    /**
     * It retrieves the log of the process.
     * 
     */
    private void showLog() {

        SolaTask showLog = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.ADMIN_CONSOLIDATION_CONSOLIDATE));

                txtLog.setText(
                        WSManager.getInstance().getAdminService().getProcessLog(processName));
                return null;
            }

            @Override
            protected void taskDone() {
                super.taskDone();
            }
        };


        int maximumAllowedNumberOfTasks = TaskManager.getInstance().getMaximumAllowedNumberOfTasks();
        if (maximumAllowedNumberOfTasks <= TaskManager.getInstance().getNumberOfActiveTasks()) {
            TaskManager.getInstance().setMaximumAllowedNumberOfTasks(
                    TaskManager.getInstance().getNumberOfActiveTasks());
        }
        TaskManager.getInstance().runTask(showLog);
        TaskManager.getInstance().setMaximumAllowedNumberOfTasks(maximumAllowedNumberOfTasks);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();
        btnStart = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLog = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        progressBarProcess = new javax.swing.JProgressBar();
        cmdShowLog = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        txtSourceInServer = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        chkRestoreExtractedFile = new javax.swing.JCheckBox();
        chkMergeConsolidationSchema = new javax.swing.JCheckBox();
        txtBackupPath = new javax.swing.JTextField();

        setHeaderPanel(pnlHeader);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/system/Bundle"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("ConsolidationConsolidatePanel.pnlHeader.titleText")); // NOI18N

        btnStart.setText(bundle.getString("ConsolidationConsolidatePanel.btnStart.text")); // NOI18N
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        jLabel2.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel2.text")); // NOI18N

        txtLog.setEditable(false);
        txtLog.setColumns(20);
        txtLog.setRows(5);
        txtLog.setText(bundle.getString("ConsolidationConsolidatePanel.txtLog.text")); // NOI18N
        jScrollPane1.setViewportView(txtLog);

        jLabel4.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel4.text")); // NOI18N

        cmdShowLog.setText(bundle.getString("ConsolidationConsolidatePanel.cmdShowLog.text")); // NOI18N
        cmdShowLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdShowLogActionPerformed(evt);
            }
        });

        jLabel6.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel6.text")); // NOI18N

        txtStatus.setEditable(false);
        txtStatus.setText(bundle.getString("ConsolidationConsolidatePanel.txtStatus.text")); // NOI18N

        txtSourceInServer.setText(bundle.getString("ConsolidationConsolidatePanel.txtSourceInServer.text")); // NOI18N

        jLabel7.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel7.text")); // NOI18N

        jLabel1.setText(bundle.getString("ConsolidationConsolidatePanel.jLabel1.text")); // NOI18N

        chkRestoreExtractedFile.setSelected(true);
        chkRestoreExtractedFile.setText(bundle.getString("ConsolidationConsolidatePanel.chkRestoreExtractedFile.text")); // NOI18N
        chkRestoreExtractedFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkRestoreExtractedFileActionPerformed(evt);
            }
        });

        chkMergeConsolidationSchema.setSelected(true);
        chkMergeConsolidationSchema.setText(bundle.getString("ConsolidationConsolidatePanel.chkMergeConsolidationSchema.text")); // NOI18N

        txtBackupPath.setEditable(false);
        txtBackupPath.setText(bundle.getString("ConsolidationConsolidatePanel.txtBackupPath.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnStart)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progressBarProcess, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkMergeConsolidationSchema)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkRestoreExtractedFile)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBackupPath, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSourceInServer, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(546, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmdShowLog))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStatus)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSourceInServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel1)
                    .addComponent(chkRestoreExtractedFile)
                    .addComponent(txtBackupPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(chkMergeConsolidationSchema))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnStart)
                        .addComponent(jLabel2))
                    .addComponent(progressBarProcess, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdShowLog)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        if (!chkRestoreExtractedFile.isSelected() && !chkMergeConsolidationSchema.isSelected()) {
            MessageUtility.displayMessage(ClientMessage.ADMIN_CONSOLIDATION_NO_ACTION_SELECTED);
            return;
        }
        if (chkRestoreExtractedFile.isSelected() && txtSourceInServer.getText().isEmpty()) {
            MessageUtility.displayMessage(ClientMessage.ADMIN_CONSOLIDATION_CONSOLIDATE_FILE_MISSING);
            return;
        }
        run();
    }//GEN-LAST:event_btnStartActionPerformed

    private void cmdShowLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdShowLogActionPerformed
        showLog();
    }//GEN-LAST:event_cmdShowLogActionPerformed

    private void chkRestoreExtractedFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkRestoreExtractedFileActionPerformed
        txtSourceInServer.setEnabled(chkRestoreExtractedFile.isSelected());
        if (!txtSourceInServer.isEnabled()){
            txtSourceInServer.setText("");
        }
    }//GEN-LAST:event_chkRestoreExtractedFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStart;
    private javax.swing.JCheckBox chkMergeConsolidationSchema;
    private javax.swing.JCheckBox chkRestoreExtractedFile;
    private javax.swing.JButton cmdShowLog;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JProgressBar progressBarProcess;
    private javax.swing.JTextField txtBackupPath;
    private javax.swing.JTextArea txtLog;
    private javax.swing.JTextField txtSourceInServer;
    private javax.swing.JTextField txtStatus;
    // End of variables declaration//GEN-END:variables
}
