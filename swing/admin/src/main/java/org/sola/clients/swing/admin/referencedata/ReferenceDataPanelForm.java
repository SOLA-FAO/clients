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
package org.sola.clients.swing.admin.referencedata;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.referencedata.ReferenceDataPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.AbstractCodeTO;

/**
 * Holds instance of {@link ReferenceDataPanel}
 */
public class ReferenceDataPanelForm extends ContentPanel {

    public static final String REFDATA_SAVED_PROPERTY = "RefDataSaved";
    
    private boolean saveOnAction;
    private boolean closeOnSave;
    private AbstractCodeBean refDataBean;
    private ResourceBundle resourceBundle;
    private String headerTitle;
    
    /** Default constructor. */
    public ReferenceDataPanelForm() {
        initComponents();
    }
   
    /** 
     * Form constructor. 
     * @param refDataClass Class of the reference data object bean.
     * @param refDataTOClass Transfer object class, related to reference data object bean.
     * @param refDataBean The instance of reference data object to show on the panel.
     * @param headerTitle The name of reference data type to use as a header.
     * @param saveOnAction If <code>true</code>, reference data object will be saved into database. 
     * If <code>false</code>, it will be validated and validation result returned as a value of 
     * {@link ReferenceDataPanelForm.REFDATA_SAVED_PROPERTY} property change event.
     * @param closeOnSave Indicates whether to close the form upon save action takes place.
     */
    public <T extends AbstractCodeBean, S extends AbstractCodeTO> 
            ReferenceDataPanelForm(Class<T> refDataClass, Class<S> refDataTOClass, 
            AbstractCodeBean refDataBean, String headerTitle, boolean saveOnAction, boolean closeOnSave) {
        this.refDataBean = refDataBean;
        this.headerTitle = headerTitle;
        this.saveOnAction = saveOnAction;
        this.closeOnSave = closeOnSave;
        resourceBundle = ResourceBundle.getBundle("org/sola/clients/swing/admin/referencedata/Bundle"); 
        
        initComponents();
        setRefDataBean(refDataClass, refDataTOClass, this.refDataBean);
    }
    
    public boolean isCloseOnSave() {
        return closeOnSave;
    }

    public void setCloseOnSave(boolean closeOnSave) {
        this.closeOnSave = closeOnSave;
        customizePanel();
    }

    public AbstractCodeBean getRefDataBean() {
        return referenceDataPanel.getReferenceDataBean();
    }

    public final <T extends AbstractCodeBean, S extends AbstractCodeTO> void 
            setRefDataBean(Class<T> refDataClass, Class<S> refDataTOClass, AbstractCodeBean refDataBean) {
        this.refDataBean = refDataBean;
        referenceDataPanel.setReferenceDataBean(refDataClass, refDataTOClass, refDataBean);
        customizePanel();
    }

    public boolean isSaveOnAction() {
        return saveOnAction;
    }

    public void setSaveOnAction(boolean saveOnAction) {
        this.saveOnAction = saveOnAction;
    }
    
    private void customizePanel() {
        if (refDataBean != null) {
            headerPanel.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    refDataBean.getTranslatedDisplayValue()));
        } else {
            headerPanel.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    resourceBundle.getString("ReferenceDataManagementPanel.NewItem.text")));
        }

        if (closeOnSave) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_SAVE_AND_CLOSE).getMessage());
        } else {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_SAVE).getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        referenceDataPanel = new org.sola.clients.swing.ui.referencedata.ReferenceDataPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);

        headerPanel.setName("headerPanel"); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/referencedata/Bundle"); // NOI18N
        headerPanel.setTitleText(bundle.getString("ReferenceDataPanelForm.headerPanel.titleText")); // NOI18N

        referenceDataPanel.setName("referenceDataPanel"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("ReferenceDataPanelForm.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(referenceDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(referenceDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        boolean isSaved = false;
        
        if(saveOnAction){
            if(referenceDataPanel.save(true)){
                MessageUtility.displayMessage(ClientMessage.ADMIN_REFDATA_SAVED, 
                        new String[]{referenceDataPanel.getReferenceDataBean().getTranslatedDisplayValue()});
                isSaved = true;
            }
        } else {
            if(referenceDataPanel.validateRefData(true)){
                isSaved = true;
            }
        }
        
        if(isSaved){
            firePropertyChange(REFDATA_SAVED_PROPERTY, false, true);
            if(closeOnSave){
                close();
            } else {
                customizePanel();
            }
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.swing.ui.referencedata.ReferenceDataPanel referenceDataPanel;
    // End of variables declaration//GEN-END:variables
}
