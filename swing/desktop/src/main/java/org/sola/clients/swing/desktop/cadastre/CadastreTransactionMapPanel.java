/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.desktop.cadastre;

import java.awt.BorderLayout;
import java.util.List;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.swing.gis.beans.TransactionBean;
import org.sola.clients.swing.ui.validation.ValidationResultForm;
import org.sola.common.messaging.GisMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.swing.gis.beans.TransactionCadastreChangeBean;
import org.sola.clients.swing.gis.beans.TransactionCadastreRedefinitionBean;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForCadastreChange;
import org.sola.clients.swing.ui.source.DocumentsManagementPanel;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForCadastreRedefinition;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForTransaction;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;

/**
 * Used to produce cadastre changes.
 */
public class CadastreTransactionMapPanel extends ContentPanel {

    private ApplicationBean applicationBean;
    private ApplicationServiceBean applicationService;
    private ApplicationPropertyBean applicationProperty;
    private ControlsBundleForTransaction mapControl = null;

    public CadastreTransactionMapPanel(
            ApplicationBean applicationBean,
            ApplicationServiceBean applicationService,
            ApplicationPropertyBean applicationProperty) {

        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        this.applicationProperty = applicationProperty;
        this.initializeMap();

        initComponents();
        customizeForm();
        this.addMapToForm();
    }

    private void initializeMap() {
        if (applicationService.getRequestType().getCode().equals(
                RequestTypeBean.CODE_CADASTRE_CHANGE)) {
            TransactionCadastreChangeBean transactionBean =
                    PojoDataAccess.getInstance().getTransactionCadastreChange(
                    this.applicationService.getId());
            this.mapControl = new ControlsBundleForCadastreChange(
                    this.applicationBean.getNr(), transactionBean, this.getBaUnitId(),
                    this.applicationBean.getLocation());
        } else if (applicationService.getRequestType().getCode().equals(
                RequestTypeBean.CODE_CADASTRE_REDEFINITION)) {
            TransactionCadastreRedefinitionBean transactionBean =
                    PojoDataAccess.getInstance().getTransactionCadastreRedefinition(
                    this.applicationService.getId());
            this.mapControl = new ControlsBundleForCadastreRedefinition(
                    transactionBean, this.getBaUnitId(),
                    this.applicationBean.getLocation());
        }
        this.mapControl.setApplicationId(this.applicationBean.getId());
        this.mapControl.setReadOnly(!this.applicationService.isManagementAllowed());
    }

    private void addMapToForm() {
        this.mapPanel.setLayout(new BorderLayout());
        this.mapPanel.add(this.mapControl, BorderLayout.CENTER);
    }

    private void customizeForm() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/cadastre/Bundle");
        String title = applicationService.getRequestType().getDisplayValue();

        if (applicationBean != null && applicationService != null) {
            if (this.applicationProperty != null) {
                title = String.format(bundle.getString("CadastreTransactionMapPanel.headerPanel.titleText.ApplicationAndProperty"),
                        applicationService.getRequestType().getDisplayValue(),
                        applicationProperty.getNameFirstpart(),
                        applicationProperty.getNameLastpart(),
                        applicationBean.getNr());
            } else {
                title = String.format(bundle.getString("CadastreTransactionMapPanel.headerPanel.titleText.Application"),
                        applicationService.getRequestType().getDisplayValue(),
                        applicationBean.getNr());
            }
        }
        headerPanel.setTitleText(title);
    }

    private String getBaUnitId() {
        String baUnitId = null;
        if (applicationProperty != null) {
            BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().getBaUnitByCode(
                    applicationProperty.getNameFirstpart(),
                    applicationProperty.getNameLastpart());
            if (baUnitTO != null) {
                baUnitId = baUnitTO.getId();
            }
        }
        return baUnitId;
    }

    private DocumentsManagementPanel createDocumentsPanel() {
        if (applicationBean == null) {
            applicationBean = new ApplicationBean();
        }

        boolean allowEdit = true;

        DocumentsManagementPanel panel = new DocumentsManagementPanel(
                this.mapControl.getTransactionBean().getSourceIdList(),
                applicationBean, allowEdit);
        return panel;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mapPanel = new javax.swing.JPanel();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        documentsPanel = createDocumentsPanel();

        setHeaderPanel(headerPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/cadastre/Bundle"); // NOI18N
        setHelpTopic(bundle.getString("CadastreTransactionMapPanel.helpTopic")); // NOI18N
        setName("Form"); // NOI18N

        mapPanel.setName("mapPanel"); // NOI18N

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 672, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 292, Short.MAX_VALUE)
        );

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("CadastreTransactionMapPanel.headerPanel.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("CadastreTransactionMapPanel.btnSave.text")); // NOI18N
        btnSave.setToolTipText(bundle.getString("CadastreTransactionMapPanel.btnSave.toolTipText")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("CadastreTransactionMapPanel.groupPanel1.titleText")); // NOI18N

        documentsPanel.setName("documentsPanel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
                    .addComponent(documentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 672, Short.MAX_VALUE)
                    .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        TransactionBean transactionBean = this.mapControl.getTransactionBean();
        transactionBean.setSourceIdList(this.documentsPanel.getSourceIds(false));
        List<ValidationResultBean> result = transactionBean.save();
        String message = MessageUtility.getLocalizedMessage(
                GisMessage.CADASTRE_CHANGE_SAVED_SUCCESSFULLY).getMessage();
        this.mapControl.refresh(true);
        ValidationResultForm resultForm = new ValidationResultForm(
                null, true, result, true, message);
        resultForm.setLocationRelativeTo(this);
        resultForm.setVisible(true);
}//GEN-LAST:event_btnSaveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private org.sola.clients.swing.ui.source.DocumentsManagementPanel documentsPanel;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel mapPanel;
    // End of variables declaration//GEN-END:variables
}
