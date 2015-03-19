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
package org.sola.clients.swing.ui.cadastre;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.address.AddressBean;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.referencedata.CadastreObjectTypeBean;
import org.sola.clients.beans.referencedata.StateLandStatusTypeBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.swing.ui.address.AddressDialog;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.ui.renderers.SimpleComboBoxRenderer;
import org.sola.common.WindowUtility;

/**
 * Parcel panel to create and manage parcel objects
 */
public class ParcelPanel extends javax.swing.JPanel {

    private boolean readOnly = true;

    private CadastreObjectBean createCadastreBean() {
        if (cadastreObjectBean1 == null) {
            cadastreObjectBean1 = new CadastreObjectBean();
            cadastreObjectBean1.setTypeCode(CadastreObjectTypeBean.CODE_STATE_LAND);
            cadastreObjectBean1.setStatusCode(StatusConstants.PENDING);
            cadastreObjectBean1.setStateLandStatusCode(StateLandStatusTypeBean.CODE_PROPOSED);
        }
        return cadastreObjectBean1;
    }

    public ParcelPanel() {
        this(null, false);
    }

    public ParcelPanel(CadastreObjectBean cadastreObject) {
        this(cadastreObject, false);
    }

    public ParcelPanel(CadastreObjectBean cadastreObject, boolean readOnly) {
        cadastreObjectBean1 = cadastreObject == null ? null : (CadastreObjectBean) cadastreObject.copy();
        this.readOnly = readOnly;
        initComponents();
        postInit();
    }

    private void postInit() {
        cadastreObjectBean1.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CadastreObjectBean.SELECTED_ADDRESS_PROPERTY)) {
                    customizeAddressButtons();
                }
            }
        });
        customizeForm();
    }

    private void customizeForm() {
        boolean enabled = !readOnly;
        boolean editable = enabled && StatusConstants.PENDING.equals(cadastreObjectBean1.getStatusCode());

        txtParcelFirstPart.setEnabled(editable);
        txtParcelLastPart.setEnabled(editable);
        txtArea.setEnabled(editable);

        cbxLandUse.setEnabled(enabled);
        txtDescription.setEnabled(enabled);
        cbxStateLandStatusType.setEnabled(enabled);
        txtStatus.setEnabled(false);

        // Determine if the State Land Status or the Parcel Status panel should be displayed.
        // Remove then add to ensure the panel is now at the end of the panel list before
        // making it invisible. 
        if (CadastreObjectTypeBean.CODE_STATE_LAND.equals(cadastreObjectBean1.getTypeCode())) {
            pnlTop.remove(pnlParcelStatus);
            pnlTop.add(pnlParcelStatus);
            pnlParcelStatus.setVisible(false);
        } else {
            pnlTop.remove(pnlSLStatus);
            pnlTop.add(pnlSLStatus);
            pnlSLStatus.setVisible(false);
        }

        customizeAddressButtons();
    }

    private void customizeAddressButtons() {
        boolean selected = cadastreObjectBean1.getSelectedAddress() != null;
        boolean enabled = selected && !readOnly;
        btnViewAddress.setEnabled(selected);
        btnAddAddress.setEnabled(!readOnly);
        btnEditAddress.setEnabled(enabled);
        btnRemoveAddress.setEnabled(enabled);

        menuAdd1.setEnabled(btnAddAddress.isEnabled());
        menuRemove1.setEnabled(btnRemoveAddress.isEnabled());
        menuEdit1.setEnabled(btnEditAddress.isEnabled());
        menuView1.setEnabled(btnViewAddress.isEnabled());
    }

    public CadastreObjectBean getCadastreObject() {
        return cadastreObjectBean1;
    }

    private void addAddress() {
        AddressDialog form = new AddressDialog(null, null, true, readOnly);
        WindowUtility.centerForm(form);
        WindowUtility.addEscapeListener(form, false);
        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(AddressDialog.ADDRESS_SAVED)) {
                    cadastreObjectBean1.addAddress((AddressBean) evt.getNewValue());
                }
            }
        });
        form.setVisible(true);
    }

    private void editAddress() {
        if (cadastreObjectBean1.getSelectedAddress() == null) {
            return;
        }
        AddressDialog form = new AddressDialog(
                (AddressBean) cadastreObjectBean1.getSelectedAddress().copy(),
                null, true, readOnly);
        WindowUtility.centerForm(form);
        WindowUtility.addEscapeListener(form, false);
        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(AddressDialog.ADDRESS_SAVED)) {
                    cadastreObjectBean1.getSelectedAddress().copyFromObject((AddressBean) evt.getNewValue());
                }
            }
        });
        form.setVisible(true);
    }

    private void viewAddress() {
        if (cadastreObjectBean1.getSelectedAddress() == null) {
            return;
        }
        AddressDialog form = new AddressDialog(
                (AddressBean) cadastreObjectBean1.getSelectedAddress().copy(),
                null, true, true);
        WindowUtility.centerForm(form);
        WindowUtility.addEscapeListener(form, false);
        form.setVisible(true);
    }

    private void removeAddress() {
        cadastreObjectBean1.removeSelectedAddress();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        cadastreObjectBean1 = createCadastreBean();
        cadastreObjectTypeListBean1 = new org.sola.clients.beans.referencedata.CadastreObjectTypeListBean();
        landUseTypeListBean1 = new org.sola.clients.beans.referencedata.LandUseTypeListBean();
        popUpAddresses = new javax.swing.JPopupMenu();
        menuView1 = new org.sola.clients.swing.common.menuitems.MenuView();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuAdd1 = new org.sola.clients.swing.common.menuitems.MenuAdd();
        menuEdit1 = new org.sola.clients.swing.common.menuitems.MenuEdit();
        menuRemove1 = new org.sola.clients.swing.common.menuitems.MenuRemove();
        stateLandStatusTypeList = new org.sola.clients.beans.referencedata.StateLandStatusTypeListBean();
        pnlTop = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        txtParcelFirstPart = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtParcelLastPart = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtArea = new javax.swing.JFormattedTextField();
        pnlParcelStatus = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        pnlSLStatus = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        cbxStateLandStatusType = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        labLandUse = new javax.swing.JLabel();
        cbxLandUse = new javax.swing.JComboBox();
        jPanel12 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel10 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnViewAddress = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAddAddress = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnEditAddress = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemoveAddress = new org.sola.clients.swing.common.buttons.BtnRemove();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableWithDefaultStyles1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/cadastre/Bundle"); // NOI18N
        popUpAddresses.setName(bundle.getString("ParcelPanel.popUpAddresses.name")); // NOI18N

        menuView1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuView1.setName("menuView1"); // NOI18N
        menuView1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuView1ActionPerformed(evt);
            }
        });
        popUpAddresses.add(menuView1);

        jSeparator2.setName("jSeparator2"); // NOI18N
        popUpAddresses.add(jSeparator2);

        menuAdd1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAdd1.setName(bundle.getString("ParcelPanel.menuAdd1.name")); // NOI18N
        menuAdd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAdd1ActionPerformed(evt);
            }
        });
        popUpAddresses.add(menuAdd1);

        menuEdit1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEdit1.setName(bundle.getString("ParcelPanel.menuEdit1.name")); // NOI18N
        menuEdit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEdit1ActionPerformed(evt);
            }
        });
        popUpAddresses.add(menuEdit1);

        menuRemove1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemove1.setName(bundle.getString("ParcelPanel.menuRemove1.name")); // NOI18N
        menuRemove1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemove1ActionPerformed(evt);
            }
        });
        popUpAddresses.add(menuRemove1);

        setName("Form"); // NOI18N

        pnlTop.setName(bundle.getString("ParcelPanel.jPanel1.name")); // NOI18N
        pnlTop.setLayout(new java.awt.GridLayout(2, 4, 15, 0));

        jPanel2.setName(bundle.getString("ParcelPanel.jPanel2.name")); // NOI18N

        jLabel17.setText(bundle.getString("ParcelPanel.jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        txtParcelFirstPart.setName("txtParcelFirstPart"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"), txtParcelFirstPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(txtParcelFirstPart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
            .add(jLabel17, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jLabel17)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtParcelFirstPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel2);

        jPanel3.setName(bundle.getString("ParcelPanel.jPanel3.name")); // NOI18N

        jLabel7.setText(bundle.getString("ParcelPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtParcelLastPart.setName("txtParcelLastPart"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"), txtParcelLastPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(txtParcelLastPart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
            .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtParcelLastPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel3);

        jPanel7.setName(bundle.getString("ParcelPanel.jPanel7.name")); // NOI18N

        jLabel1.setText(bundle.getString("ParcelPanel.jLabel1.text")); // NOI18N
        jLabel1.setName(bundle.getString("ParcelPanel.jLabel1.name")); // NOI18N

        txtArea.setFormatterFactory(FormattersFactory.getInstance().getMetricAreaFormatterFactory());
        txtArea.setText(bundle.getString("ParcelPanel.txtArea.text")); // NOI18N
        txtArea.setName(bundle.getString("ParcelPanel.txtArea.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${officialAreaSize}"), txtArea, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(txtArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnlTop.add(jPanel7);

        pnlParcelStatus.setName("pnlParcelStatus"); // NOI18N

        jLabel3.setText(bundle.getString("ParcelPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtStatus.setEnabled(false);
        txtStatus.setName("txtStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout pnlParcelStatusLayout = new org.jdesktop.layout.GroupLayout(pnlParcelStatus);
        pnlParcelStatus.setLayout(pnlParcelStatusLayout);
        pnlParcelStatusLayout.setHorizontalGroup(
            pnlParcelStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
            .add(txtStatus)
        );
        pnlParcelStatusLayout.setVerticalGroup(
            pnlParcelStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlParcelStatusLayout.createSequentialGroup()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 17, Short.MAX_VALUE))
        );

        pnlTop.add(pnlParcelStatus);

        pnlSLStatus.setName(bundle.getString("ParcelPanel.jPanel4.name")); // NOI18N

        jLabel10.setText(bundle.getString("ParcelPanel.jLabel10.text")); // NOI18N
        jLabel10.setToolTipText(bundle.getString("ParcelPanel.jLabel10.toolTipText")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        cbxStateLandStatusType.setName("cbxStateLandStatusType"); // NOI18N
        cbxStateLandStatusType.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${stateLandStatusTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, stateLandStatusTypeList, eLProperty, cbxStateLandStatusType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${stateLandStatusType}"), cbxStateLandStatusType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout pnlSLStatusLayout = new org.jdesktop.layout.GroupLayout(pnlSLStatus);
        pnlSLStatus.setLayout(pnlSLStatusLayout);
        pnlSLStatusLayout.setHorizontalGroup(
            pnlSLStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbxStateLandStatusType, 0, 118, Short.MAX_VALUE)
            .add(jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlSLStatusLayout.setVerticalGroup(
            pnlSLStatusLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSLStatusLayout.createSequentialGroup()
                .add(jLabel10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbxStateLandStatusType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );

        pnlTop.add(pnlSLStatus);

        jPanel6.setName(bundle.getString("ParcelPanel.jPanel6.name")); // NOI18N

        labLandUse.setText(bundle.getString("ParcelPanel.labLandUse.text")); // NOI18N
        labLandUse.setName(bundle.getString("ParcelPanel.labLandUse.name")); // NOI18N

        cbxLandUse.setName(bundle.getString("ParcelPanel.cbxLandUse.name")); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${landUseTypeList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, landUseTypeListBean1, eLProperty, cbxLandUse);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${landUseType}"), cbxLandUse, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cbxLandUse, 0, 118, Short.MAX_VALUE)
            .add(labLandUse, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(labLandUse)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbxLandUse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel6);

        jPanel12.setName("jPanel12"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 118, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 57, Short.MAX_VALUE)
        );

        pnlTop.add(jPanel12);

        jPanel5.setName(bundle.getString("ParcelPanel.jPanel5.name")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 118, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 57, Short.MAX_VALUE)
        );

        pnlTop.add(jPanel5);

        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setLayout(new java.awt.GridLayout(2, 1, 15, 3));

        jPanel8.setName("jPanel8"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        txtDescription.setName("txtDescription"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${description}"), txtDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(txtDescription);

        jLabel2.setText(bundle.getString("ParcelPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        groupPanel1.setName(bundle.getString("ParcelPanel.groupPanel1.name")); // NOI18N
        groupPanel1.setTitleText(bundle.getString("ParcelPanel.groupPanel1.titleText")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
            .add(groupPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel8Layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(groupPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(3, 3, 3))
        );

        jPanel9.add(jPanel8);

        jPanel10.setName("jPanel10"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName(bundle.getString("ParcelPanel.jToolBar1.name")); // NOI18N

        btnViewAddress.setName("btnViewAddress"); // NOI18N
        btnViewAddress.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewAddressActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewAddress);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        btnAddAddress.setName(bundle.getString("ParcelPanel.btnAdd1.name")); // NOI18N
        btnAddAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddAddressActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddAddress);

        btnEditAddress.setName(bundle.getString("ParcelPanel.btnEdit1.name")); // NOI18N
        btnEditAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditAddressActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEditAddress);

        btnRemoveAddress.setName(bundle.getString("ParcelPanel.btnRemove1.name")); // NOI18N
        btnRemoveAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAddressActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveAddress);

        jScrollPane1.setName(bundle.getString("ParcelPanel.jScrollPane1.name")); // NOI18N

        jTableWithDefaultStyles1.setComponentPopupMenu(popUpAddresses);
        jTableWithDefaultStyles1.setName(bundle.getString("ParcelPanel.jTableWithDefaultStyles1.name")); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${addressFilteredList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, eLProperty, jTableWithDefaultStyles1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedAddress}"), jTableWithDefaultStyles1, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTableWithDefaultStyles1);
        if (jTableWithDefaultStyles1.getColumnModel().getColumnCount() > 0) {
            jTableWithDefaultStyles1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ParcelPanel.jTableWithDefaultStyles1.columnModel.title0_1")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(0).setCellRenderer(new org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer());
        }

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel10);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlTop, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnlTop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddAddressActionPerformed
        addAddress();
    }//GEN-LAST:event_btnAddAddressActionPerformed

    private void btnEditAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditAddressActionPerformed
        editAddress();
    }//GEN-LAST:event_btnEditAddressActionPerformed

    private void btnRemoveAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAddressActionPerformed
        removeAddress();
    }//GEN-LAST:event_btnRemoveAddressActionPerformed

    private void menuAdd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAdd1ActionPerformed
        addAddress();
    }//GEN-LAST:event_menuAdd1ActionPerformed

    private void menuEdit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEdit1ActionPerformed
        editAddress();
    }//GEN-LAST:event_menuEdit1ActionPerformed

    private void menuRemove1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemove1ActionPerformed
        removeAddress();
    }//GEN-LAST:event_menuRemove1ActionPerformed

    private void btnViewAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewAddressActionPerformed
        viewAddress();
    }//GEN-LAST:event_btnViewAddressActionPerformed

    private void menuView1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuView1ActionPerformed
        viewAddress();
    }//GEN-LAST:event_menuView1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnAdd btnAddAddress;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEditAddress;
    private org.sola.clients.swing.common.buttons.BtnRemove btnRemoveAddress;
    private org.sola.clients.swing.common.buttons.BtnView btnViewAddress;
    private org.sola.clients.beans.cadastre.CadastreObjectBean cadastreObjectBean1;
    private org.sola.clients.beans.referencedata.CadastreObjectTypeListBean cadastreObjectTypeListBean1;
    private javax.swing.JComboBox cbxLandUse;
    private javax.swing.JComboBox cbxStateLandStatusType;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles jTableWithDefaultStyles1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel labLandUse;
    private org.sola.clients.beans.referencedata.LandUseTypeListBean landUseTypeListBean1;
    private org.sola.clients.swing.common.menuitems.MenuAdd menuAdd1;
    private org.sola.clients.swing.common.menuitems.MenuEdit menuEdit1;
    private org.sola.clients.swing.common.menuitems.MenuRemove menuRemove1;
    private org.sola.clients.swing.common.menuitems.MenuView menuView1;
    private javax.swing.JPanel pnlParcelStatus;
    private javax.swing.JPanel pnlSLStatus;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPopupMenu popUpAddresses;
    private org.sola.clients.beans.referencedata.StateLandStatusTypeListBean stateLandStatusTypeList;
    private javax.swing.JFormattedTextField txtArea;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtParcelFirstPart;
    private javax.swing.JTextField txtParcelLastPart;
    private javax.swing.JTextField txtStatus;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
