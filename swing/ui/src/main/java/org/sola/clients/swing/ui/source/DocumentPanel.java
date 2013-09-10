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
package org.sola.clients.swing.ui.source;

import java.awt.ComponentOrientation;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import javax.swing.JTextField;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.referencedata.SourceTypeListBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.swing.common.controls.BrowseControlListener;
import org.sola.clients.swing.ui.renderers.FormattersFactory;
import org.sola.clients.swing.ui.renderers.SimpleComboBoxRenderer;
import org.sola.common.RolesConstants;

/**
 * Document panel, used to create or update document. {@link SourceBean} is used
 * to bind data on the panel controls.
 */
public class DocumentPanel extends javax.swing.JPanel {

    public static final String UPDATED_SOURCE = "updatedSource";
    private boolean allowEditing = true;
    private SourceBean document;
    public DocumentBean archiveDocument;

    private SourceTypeListBean createSourceTypeList(){
        if(sourceTypeListBean==null){
            if(document!=null && document.getSourceType()!=null && document.getSourceType().getCode()!=null){
                sourceTypeListBean = new SourceTypeListBean(false, document.getSourceType().getCode());
            } else {
                sourceTypeListBean = new SourceTypeListBean(false);
            }
        }
        return sourceTypeListBean;
    }
    
    public DocumentPanel(SourceBean document, boolean allowEditing) {
        this.document = document;
        this.allowEditing = allowEditing;
        initComponents();
        postInit();
    }

    public DocumentPanel() {
        initComponents();
        postInit();
    }

    public SourceBean getDocument() {
        if (document == null) {
            document = new SourceBean();
        }
        return document;
    }

    public void setDocument(SourceBean document) {
        if (document == null) {
            document = new SourceBean();
        }
        this.document = document;
        firePropertyChange("document", null, this.document);
    }

    public boolean isAllowEditing() {
        return allowEditing;
    }

    public void setAllowEditing(boolean allowEditing) {
        this.allowEditing = allowEditing;
        customizeForm();
    }

    /**
     * Customizes form elements, based on the provided setting.
     */
    private void customizeForm() {
        cbxDocType.setEnabled(allowEditing);
        txtDocRecordDate.setEnabled(allowEditing);
        txtDocRefNumber.setEnabled(allowEditing);
        txtExpiration.setEnabled(allowEditing);
        browseAttachment.setDisplayBrowseButton(allowEditing &&
                SecurityBean.isInRole(RolesConstants.SOURCE_SAVE));
        browseAttachment.setDisplayDeleteButton(allowEditing
                && SecurityBean.isInRole(RolesConstants.SOURCE_SAVE));
        txtOwnerName.setEnabled(allowEditing);
        txtDocAcceptanceDate.setEnabled(allowEditing);
        txtSigningDate.setEnabled(allowEditing);
        txtVersion.setEnabled(allowEditing);
        txtDescription.setEnabled(allowEditing);
    }

    /**
     * Makes post initialization tasks. Binds listener to the browse control,
     * sets text of OK button.
     */
    private void postInit() {
        cbxDocType.setSelectedIndex(-1);
        // Init browse attachment
        browseAttachment.addBrowseControlEventListener(new BrowseControlListener() {

            @Override
            public void deleteButtonClicked(MouseEvent e) {
                getDocument().removeAttachment();
            }

            @Override
            public void browseButtonClicked(MouseEvent e) {
                openAttachmentForm();
            }

            @Override
            public void controlClicked(MouseEvent e) {
            }

            @Override
            public void textClicked(MouseEvent e) {
                DocumentBean.openDocument(getDocument().getArchiveDocument().getId(),
                        getDocument().getArchiveDocument().getFileName());
            }
        });
        customizeForm();
    }

    private void openAttachmentForm() {
        FileBrowserForm fileBrowser = new FileBrowserForm(null, true, FileBrowserForm.AttachAction.CLOSE_WINDOW);
        fileBrowser.setLocationRelativeTo(this);
        fileBrowser.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(FileBrowserForm.ATTACHED_DOCUMENT)) {
                    if (e.getNewValue() != null) {
                        DocumentBean document = (DocumentBean) e.getNewValue();
                        getDocument().setArchiveDocument(document);
                    }
                }
            }
        });
        fileBrowser.setVisible(true);
    }

    public void clearFields() {
        getDocument().clean();
        browseAttachment.setText(null);
        cbxDocType.setSelectedIndex(-1);
    }

    private void fireDocumentChangeEvent() {
        SourceBean updatedSource;
        if (getDocument().isNew()) {
            updatedSource = getDocument().copy();
            if (!(updatedSource.getTypeCode().contentEquals("publicNotification")) && !(updatedSource.getTypeCode().contentEquals("title"))) {
                clearFields();
            }
        } else {
            updatedSource = getDocument();
        }
        firePropertyChange(UPDATED_SOURCE, null, updatedSource);
    }

    public boolean validateDocument(boolean showMessage){
        return getDocument().validate(showMessage).size() < 1;
    }
    
    public boolean saveDocument() {
        if (validateDocument(true)) {
            
           if (!(this.archiveDocument==null)){ 
            if (!this.archiveDocument.getId().equals("")) {
                getDocument().setArchiveDocument(this.archiveDocument);
            }
           } 
            getDocument().save();
            fireDocumentChangeEvent();
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        sourceTypeListBean = createSourceTypeList();
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cbxDocType = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        txtDocRecordDate = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtDocRefNumber = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtLaNumber = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtOwnerName = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        browseAttachment = new org.sola.clients.swing.common.controls.BrowseControl();
        jPanel11 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtSubmissionDate = new javax.swing.JFormattedTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtDocAcceptanceDate = new javax.swing.JFormattedTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtExpiration = new javax.swing.JFormattedTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtSigningDate = new javax.swing.JFormattedTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtVersion = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtDescription = new javax.swing.JTextField();

        setName("Form"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle"); // NOI18N
        jPanel8.setName(bundle.getString("DocumentPanel.jPanel8.name")); // NOI18N
        jPanel8.setLayout(new java.awt.GridLayout(4, 3, 15, 10));

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel4.setText(bundle.getString("DocumentPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        cbxDocType.setFont(new java.awt.Font("Thaoma", 0, 12));
        cbxDocType.setLightWeightPopupEnabled(false);
        cbxDocType.setName("cbxDocType"); // NOI18N
        cbxDocType.setNextFocusableComponent(txtDocRecordDate);
        cbxDocType.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${sourceTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sourceTypeListBean, eLProperty, cbxDocType);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.sourceType}"), cbxDocType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbxDocType.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addContainerGap(121, Short.MAX_VALUE))
            .addComponent(cbxDocType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxDocType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        txtDocRecordDate.setFont(new java.awt.Font("Thaoma", 0, 12));
        txtDocRecordDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtDocRecordDate.setName("txtDocRecordDate"); // NOI18N
        txtDocRecordDate.setNextFocusableComponent(txtDocRefNumber);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.recordation}"), txtDocRecordDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        txtDocRecordDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtDocRecordDate.setHorizontalAlignment(JTextField.LEADING);

        jLabel3.setText(bundle.getString("DocumentPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 122, Short.MAX_VALUE))
            .addComponent(txtDocRecordDate)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDocRecordDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel2);

        jPanel7.setName(bundle.getString("DocumentPanel.jPanel7.name")); // NOI18N

        jLabel2.setText(bundle.getString("DocumentPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtDocRefNumber.setName("txtDocRefNumber"); // NOI18N
        txtDocRefNumber.setNextFocusableComponent(txtOwnerName);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.referenceNr}"), txtDocRefNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtDocRefNumber.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtDocRefNumber.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 95, Short.MAX_VALUE))
            .addComponent(txtDocRefNumber)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDocRefNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel7);

        jPanel6.setName(bundle.getString("DocumentPanel.jPanel6.name")); // NOI18N

        jLabel7.setText(bundle.getString("DocumentPanel.jLabel7.text")); // NOI18N
        jLabel7.setName(bundle.getString("DocumentPanel.jLabel7.name")); // NOI18N

        txtLaNumber.setEnabled(false);
        txtLaNumber.setName(bundle.getString("DocumentPanel.txtLaNumber.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.laNr}"), txtLaNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(0, 76, Short.MAX_VALUE))
            .addComponent(txtLaNumber)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLaNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel6);

        jPanel4.setName(bundle.getString("DocumentPanel.jPanel4.name")); // NOI18N

        jLabel5.setText(bundle.getString("DocumentPanel.jLabel5.text")); // NOI18N
        jLabel5.setName(bundle.getString("DocumentPanel.jLabel5.name")); // NOI18N

        txtOwnerName.setName(bundle.getString("DocumentPanel.txtOwnerName.name")); // NOI18N
        txtOwnerName.setNextFocusableComponent(browseAttachment);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.ownerName}"), txtOwnerName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtOwnerName)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 73, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtOwnerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel4);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel1.setText(bundle.getString("DocumentPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        browseAttachment.setName("browseAttachment"); // NOI18N
        browseAttachment.setNextFocusableComponent(txtDocAcceptanceDate);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.archiveDocument.name}"), browseAttachment, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(85, Short.MAX_VALUE))
            .addComponent(browseAttachment, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseAttachment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel3);

        jPanel11.setName(bundle.getString("DocumentPanel.jPanel11.name")); // NOI18N

        jLabel9.setText(bundle.getString("DocumentPanel.jLabel9.text")); // NOI18N
        jLabel9.setName(bundle.getString("DocumentPanel.jLabel9.name")); // NOI18N

        txtDocRecordDate.setFont(new java.awt.Font("Thaoma", 0, 12));
        txtSubmissionDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtSubmissionDate.setEnabled(false);
        txtSubmissionDate.setName(bundle.getString("DocumentPanel.txtSubmissionDate.name")); // NOI18N
        txtSubmissionDate.setNextFocusableComponent(txtDocRefNumber);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.submission}"), txtSubmissionDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        txtDocRecordDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtDocRecordDate.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(0, 67, Short.MAX_VALUE))
            .addComponent(txtSubmissionDate)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSubmissionDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel11);

        jPanel10.setName(bundle.getString("DocumentPanel.jPanel10.name")); // NOI18N

        jLabel8.setText(bundle.getString("DocumentPanel.jLabel8.text")); // NOI18N
        jLabel8.setName(bundle.getString("DocumentPanel.jLabel8.name")); // NOI18N

        txtDocRecordDate.setFont(new java.awt.Font("Thaoma", 0, 12));
        txtDocAcceptanceDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtDocAcceptanceDate.setName(bundle.getString("DocumentPanel.txtDocAcceptanceDate.name")); // NOI18N
        txtDocAcceptanceDate.setNextFocusableComponent(txtExpiration);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.acceptance}"), txtDocAcceptanceDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        txtDocRecordDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtDocRecordDate.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(0, 63, Short.MAX_VALUE))
            .addComponent(txtDocAcceptanceDate)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDocAcceptanceDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel10);

        jPanel12.setName(bundle.getString("DocumentPanel.jPanel12.name")); // NOI18N

        jLabel10.setText(bundle.getString("DocumentPanel.jLabel10.text")); // NOI18N
        jLabel10.setName(bundle.getString("DocumentPanel.jLabel10.name")); // NOI18N

        txtDocRecordDate.setFont(new java.awt.Font("Thaoma", 0, 12));
        txtExpiration.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtExpiration.setName(bundle.getString("DocumentPanel.txtExpiration.name")); // NOI18N
        txtExpiration.setNextFocusableComponent(txtSigningDate);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.expirationDate}"), txtExpiration, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        txtDocRecordDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtDocRecordDate.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(0, 71, Short.MAX_VALUE))
            .addComponent(txtExpiration)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtExpiration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel12);

        jPanel13.setName(bundle.getString("DocumentPanel.jPanel13.name")); // NOI18N

        jLabel11.setText(bundle.getString("DocumentPanel.jLabel11.text")); // NOI18N
        jLabel11.setName(bundle.getString("DocumentPanel.jLabel11.name")); // NOI18N

        txtStatus.setEnabled(false);
        txtStatus.setName(bundle.getString("DocumentPanel.txtStatus.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addGap(0, 114, Short.MAX_VALUE))
            .addComponent(txtStatus)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel13);

        jPanel14.setName(bundle.getString("DocumentPanel.jPanel14.name")); // NOI18N

        jLabel12.setText(bundle.getString("DocumentPanel.jLabel12.text")); // NOI18N
        jLabel12.setName(bundle.getString("DocumentPanel.jLabel12.name")); // NOI18N

        txtDocRecordDate.setFont(new java.awt.Font("Thaoma", 0, 12));
        txtSigningDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtSigningDate.setName(bundle.getString("DocumentPanel.txtSigningDate.name")); // NOI18N
        txtSigningDate.setNextFocusableComponent(txtVersion);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.signingDate}"), txtSigningDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        txtDocRecordDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtDocRecordDate.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGap(0, 85, Short.MAX_VALUE))
            .addComponent(txtSigningDate)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSigningDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel14);

        jPanel9.setName(bundle.getString("DocumentPanel.jPanel9.name")); // NOI18N

        jLabel13.setText(bundle.getString("DocumentPanel.jLabel13.text")); // NOI18N
        jLabel13.setName(bundle.getString("DocumentPanel.jLabel13.name")); // NOI18N

        txtVersion.setName(bundle.getString("DocumentPanel.txtVersion.name")); // NOI18N
        txtVersion.setNextFocusableComponent(txtDescription);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.version}"), txtVersion, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addGap(0, 110, Short.MAX_VALUE))
            .addComponent(txtVersion)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.add(jPanel9);

        jPanel5.setName(bundle.getString("DocumentPanel.jPanel5.name")); // NOI18N

        jLabel6.setText(bundle.getString("DocumentPanel.jLabel6.text")); // NOI18N
        jLabel6.setName(bundle.getString("DocumentPanel.jLabel6.name")); // NOI18N

        txtDescription.setName(bundle.getString("DocumentPanel.txtDescription.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${document.description}"), txtDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(txtDescription)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public org.sola.clients.swing.common.controls.BrowseControl browseAttachment;
    public javax.swing.JComboBox cbxDocType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private org.sola.clients.beans.referencedata.SourceTypeListBean sourceTypeListBean;
    public javax.swing.JTextField txtDescription;
    public javax.swing.JFormattedTextField txtDocAcceptanceDate;
    public javax.swing.JFormattedTextField txtDocRecordDate;
    public javax.swing.JTextField txtDocRefNumber;
    public javax.swing.JFormattedTextField txtExpiration;
    private javax.swing.JTextField txtLaNumber;
    private javax.swing.JTextField txtOwnerName;
    public javax.swing.JFormattedTextField txtSigningDate;
    private javax.swing.JTextField txtStatus;
    public javax.swing.JFormattedTextField txtSubmissionDate;
    private javax.swing.JTextField txtVersion;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
