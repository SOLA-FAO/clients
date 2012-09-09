/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.desktop.cadastre;

import java.awt.BorderLayout;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.gis.mapaction.SaveTransaction;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForTransaction;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;

/**
 * Used to produce cadastre changes.
 */
public class CadastreTransactionMapPanel extends ContentPanel {

    private ApplicationBean applicationBean;
    private ApplicationServiceBean applicationService;
    private ApplicationPropertyBean applicationProperty;
    private ControlsBundleForTransaction mapControl = null;
    private String targetCadastreObjectType = CadastreObjectTypeBean.CODE_PARCEL;

    /**
     * It initiates the panel with the target cadastre object type as being parcel.
     * 
     * @param applicationBean
     * @param applicationService
     * @param applicationProperty 
     */
    public CadastreTransactionMapPanel(
            ApplicationBean applicationBean,
            ApplicationServiceBean applicationService,
            ApplicationPropertyBean applicationProperty) {
        this(applicationBean, applicationService, 
                applicationProperty, CadastreObjectTypeBean.CODE_PARCEL);
        saveTransactionState();
    }

    /**
     * It initiates the panel with the target cadastre object type as parameter.
     * 
     * @param applicationBean
     * @param applicationService
     * @param applicationProperty
     * @param targetCadastreObjectType 
     */
    public CadastreTransactionMapPanel(
            ApplicationBean applicationBean,
            ApplicationServiceBean applicationService,
            ApplicationPropertyBean applicationProperty,
            String targetCadastreObjectType) {

        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        this.applicationProperty = applicationProperty;
        this.targetCadastreObjectType = targetCadastreObjectType;
        this.initializeMap();

        initComponents();
        customizeForm();
        this.addMapToForm();
        saveTransactionState();
    }

    private void initializeMap() {
        this.mapControl = ControlsBundleForTransaction.getInstance(
                applicationService.getRequestType().getCode(), this.applicationBean, 
                this.applicationService.getId(), this.getBaUnitId(), getTargetCadastreObjectType());
        this.mapControl.setReadOnly(!this.applicationService.isManagementAllowed());
    }
    
    private String getTargetCadastreObjectType(){
        return targetCadastreObjectType;
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
                title = String.format(
                        bundle.getString("CadastreTransactionMapPanel.headerPanel.titleText.ApplicationAndProperty"),
                        applicationService.getRequestType().getDisplayValue(),
                        applicationProperty.getNameFirstpart(),
                        applicationProperty.getNameLastpart(),
                        applicationBean.getNr());
            } else {
                title = String.format(
                        bundle.getString("CadastreTransactionMapPanel.headerPanel.titleText.Application"),
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
     
    
    private void saveTransactionState() {
        MainForm.saveBeanState(this.mapControl.getTransactionBean());
    }
    
    private boolean saveTransaction() {
       SaveTransaction actionSave = new SaveTransaction(this.mapControl);
       actionSave.onClick();
       close();
        saveTransactionState();
        return true;
    }
    @Override
    protected boolean panelClosing() {
        if (MainForm.checkSaveBeforeClose(this.mapControl.getTransactionBean())) {
            return saveTransaction();
        }
        return true;
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mapPanel = new javax.swing.JPanel();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();

        setHeaderPanel(headerPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/cadastre/Bundle"); // NOI18N
        setHelpTopic(bundle.getString("CadastreTransactionMapPanel.helpTopic")); // NOI18N
        setName("Form"); // NOI18N

        mapPanel.setName("mapPanel"); // NOI18N

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 460, Short.MAX_VALUE)
        );

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("CadastreTransactionMapPanel.headerPanel.titleText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
            .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JPanel mapPanel;
    // End of variables declaration//GEN-END:variables
}
