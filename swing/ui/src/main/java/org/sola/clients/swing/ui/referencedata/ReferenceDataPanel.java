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
package org.sola.clients.swing.ui.referencedata;

import java.awt.Component;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.referencedata.ChecklistGroupBean;
import org.sola.clients.beans.referencedata.ChecklistItemBean;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.webservices.transferobjects.AbstractCodeTO;

/**
 * Used to manage beans, inheriting from {@link AbstractCodeBean}.
 */
public class ReferenceDataPanel extends javax.swing.JPanel {

    private AbstractCodeBean referenceDataBean;
    private Class<? extends AbstractCodeBean> refDataClass;
    private Class<? extends AbstractCodeTO> refDataTOClass;

    /**
     * Default constructor.
     */
    public ReferenceDataPanel() {
        initComponents();
    }

    /**
     * Creates panel with predefined parameters.
     *
     * @param refDataClass Class of reference data bean to create and bind on
     * the panel.
     * @param refDataTOClass Class of reference data transfer object to be used
     * for saving on the server side.
     */
    public <T extends AbstractCodeBean, S extends AbstractCodeTO> ReferenceDataPanel(Class<T> refDataClass, Class<S> refDataTOClass) {
        this.refDataClass = refDataClass;
        this.refDataTOClass = refDataTOClass;
        initComponents();
        setupRefDataBean(null);
    }

    /**
     * Setup reference data bean object, used to bind data on the form.
     */
    private void setupRefDataBean(AbstractCodeBean referenceDataBean) {
        txtCode.setEnabled(referenceDataBean == null);

        if (referenceDataBean != null) {
            this.referenceDataBean = referenceDataBean;
        } else {
            try {
                this.referenceDataBean = refDataClass.newInstance();
            } catch (Exception ex) {
            }
        }

        descriptionValues.loadLocalizedValues(this.referenceDataBean.getDescription());
        displayValues.loadLocalizedValues(this.referenceDataBean.getDisplayValue());

        if (!this.referenceDataBean.getClass().isAssignableFrom(ChecklistItemBean.class)) {
            // Only display the Display Order fields if this is a ChecklistItemBean.
            lblDisplayOrder.setVisible(false);
            txtDisplayOrder.setVisible(false);
        }
        if (this.referenceDataBean.getClass().isAssignableFrom(ChecklistGroupBean.class)) {
            // Display an additional panel to allow the user to select the checklist items
            // show be linked with the Checklist Group. 
            addChildSelectionPanel(new ChecklistItemSelectionPanel(((ChecklistGroupBean) this.referenceDataBean).getChecklistItemList()));
        } else {
            pnlDynamic.remove(pnlChildren);
        }

        firePropertyChange("referenceDataBean", null, this.referenceDataBean);
    }

    /**
     * Adds the child selection panel to pnlChildren with the appropriate
     * GUI layout.
     *
     * @param selectionPanel
     */
    public void addChildSelectionPanel(Component selectionPanel) {
        javax.swing.GroupLayout pnlChildrenLayout = new javax.swing.GroupLayout(pnlChildren);
        pnlChildren.setLayout(pnlChildrenLayout);
        pnlChildrenLayout.setHorizontalGroup(
                pnlChildrenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(selectionPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, pnlChildren.getWidth(), Short.MAX_VALUE)
        );
        pnlChildrenLayout.setVerticalGroup(
                pnlChildrenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(selectionPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, pnlChildren.getHeight(), Short.MAX_VALUE)
        );
    }

    /**
     * Returns reference data bean, bound on the panel.
     */
    public AbstractCodeBean getReferenceDataBean() {
        return referenceDataBean;
    }

    /**
     * Sets reference data bean, to bind on the panel.
     */
    public <T extends AbstractCodeBean, S extends AbstractCodeTO> void
            setReferenceDataBean(Class<T> refDataClass,
                    Class<S> refDataTOClass, AbstractCodeBean referenceDataBean) {
        this.refDataTOClass = refDataTOClass;
        this.refDataClass = refDataClass;
        setupRefDataBean(referenceDataBean);
    }

    /**
     * Validates reference data object.
     */
    public boolean validateRefData(boolean showMessage) {
        return referenceDataBean.validate(showMessage).size() < 1;
    }

    /**
     * Calls saving procedure of reference data object.
     */
    public boolean save(boolean showMessage) {
        referenceDataBean.setDisplayValue(displayValues.buildMultilingualString());
        referenceDataBean.setDescription(descriptionValues.buildMultilingualString());
        if (validateRefData(showMessage)) {
            AbstractCodeBean.saveRefData(referenceDataBean, refDataTOClass);
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        displayValues = new org.sola.clients.beans.system.LocalizedValuesListBean();
        descriptionValues = new org.sola.clients.beans.system.LocalizedValuesListBean();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        lblDisplayOrder = new javax.swing.JLabel();
        txtDisplayOrder = new javax.swing.JFormattedTextField();
        jPanel8 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        pnlDynamic = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableDisplayValues = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        pnlChildren = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableDescription = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jLabel2 = new javax.swing.JLabel();

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.GridLayout(1, 0, 15, 5));

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/referencedata/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("ReferenceDataPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtCode.setName("txtCode"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${referenceDataBean.code}"), txtCode, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtCode)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel3.setText(bundle.getString("ReferenceDataPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtStatus.setName("txtStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${referenceDataBean.status}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtStatus)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel5.add(jPanel2);

        jPanel7.setName("jPanel7"); // NOI18N

        lblDisplayOrder.setText(bundle.getString("ReferenceDataPanel.lblDisplayOrder.text")); // NOI18N
        lblDisplayOrder.setName("lblDisplayOrder"); // NOI18N

        txtDisplayOrder.setFormatterFactory(FormattersFactory.getInstance().getIntegerFormatterFactory());
        txtDisplayOrder.setText(bundle.getString("ReferenceDataPanel.txtDisplayOrder.text")); // NOI18N
        txtDisplayOrder.setName("txtDisplayOrder"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${referenceDataBean.displayOrder}"), txtDisplayOrder, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblDisplayOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtDisplayOrder)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(lblDisplayOrder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDisplayOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel7);

        jPanel8.setName("jPanel8"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 52, Short.MAX_VALUE)
        );

        jPanel5.add(jPanel8);

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.GridLayout(2, 0, 0, 6));

        pnlDynamic.setName("pnlDynamic"); // NOI18N
        pnlDynamic.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel9.setName("jPanel9"); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel4.setText(bundle.getString("ReferenceDataPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableDisplayValues.setName("tableDisplayValues"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${localizedValues}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, displayValues, eLProperty, tableDisplayValues);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${language.displayValue}"));
        columnBinding.setColumnName("Language.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${localizedValue}"));
        columnBinding.setColumnName("Localized Value");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(tableDisplayValues);
        if (tableDisplayValues.getColumnModel().getColumnCount() > 0) {
            tableDisplayValues.getColumnModel().getColumn(0).setPreferredWidth(150);
            tableDisplayValues.getColumnModel().getColumn(0).setMaxWidth(150);
            tableDisplayValues.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ReferenceDataPanel.tableDisplayValues.columnModel.title0_1")); // NOI18N
            tableDisplayValues.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ReferenceDataPanel.tableDisplayValues.columnModel.title1_1")); // NOI18N
        }

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
        );

        pnlDynamic.add(jPanel9);

        pnlChildren.setName("pnlChildren"); // NOI18N

        javax.swing.GroupLayout pnlChildrenLayout = new javax.swing.GroupLayout(pnlChildren);
        pnlChildren.setLayout(pnlChildrenLayout);
        pnlChildrenLayout.setHorizontalGroup(
            pnlChildrenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 242, Short.MAX_VALUE)
        );
        pnlChildrenLayout.setVerticalGroup(
            pnlChildrenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 176, Short.MAX_VALUE)
        );

        pnlDynamic.add(pnlChildren);

        jPanel6.add(pnlDynamic);

        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tableDescription.setName("tableDescription"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${localizedValues}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, descriptionValues, eLProperty, tableDescription);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${language.displayValue}"));
        columnBinding.setColumnName("Language.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${localizedValue}"));
        columnBinding.setColumnName("Localized Value");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane2.setViewportView(tableDescription);
        if (tableDescription.getColumnModel().getColumnCount() > 0) {
            tableDescription.getColumnModel().getColumn(0).setPreferredWidth(150);
            tableDescription.getColumnModel().getColumn(0).setMaxWidth(150);
            tableDescription.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ReferenceDataPanel.tableDescription.columnModel.title0_1")); // NOI18N
            tableDescription.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ReferenceDataPanel.tableDescription.columnModel.title1_1")); // NOI18N
            tableDescription.getColumnModel().getColumn(1).setCellRenderer(new TableCellTextAreaRenderer());
        }

        jLabel2.setText(bundle.getString("ReferenceDataPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.system.LocalizedValuesListBean descriptionValues;
    private org.sola.clients.beans.system.LocalizedValuesListBean displayValues;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDisplayOrder;
    private javax.swing.JPanel pnlChildren;
    private javax.swing.JPanel pnlDynamic;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableDescription;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableDisplayValues;
    private javax.swing.JTextField txtCode;
    private javax.swing.JFormattedTextField txtDisplayOrder;
    private javax.swing.JTextField txtStatus;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
