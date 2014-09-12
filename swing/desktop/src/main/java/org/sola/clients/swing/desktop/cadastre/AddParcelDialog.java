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
package org.sola.clients.swing.desktop.cadastre;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.ui.cadastre.ParcelPanel;
import org.sola.clients.swing.ui.renderers.AreaCellRenderer;
import org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer;
import org.sola.clients.swing.ui.security.SecurityClassificationDialog;
import org.sola.common.StringUtility;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;

/**
 * Dialog form to create new parcel or search existing one.
 */
public class AddParcelDialog extends javax.swing.JDialog {

    public final static String SELECTED_PARCEL = "selectedParcel";
    public final static String APP_PARCEL_PROPERTY = "selectedAppParcel";
    private CadastreObjectBean selectedAppParcel;

    /**
     * Form constructor.
     */
    public AddParcelDialog(ApplicationBean appBean, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.appBean = appBean;
        initComponents();
        customizeForm();
    }

    private void customizeForm() {
        this.setIconImage(new ImageIcon(AddParcelDialog.class.getResource("/images/sola/logo_icon.jpg")).getImage());
        WindowUtility.addEscapeListener(this, false);
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/cadastre/Bundle");
        if (!StringUtility.isEmpty(appBean.getNr())) {
            String tabTitle = bundle1.getString("AddParcelDialog.tabApplication.TabConstraints.tabTitle");
            tabMain.setTitleAt(tabMain.indexOfComponent(tabApplication), String.format(tabTitle, appBean.getNr()));
        } else {
            tabMain.removeTabAt(tabMain.indexOfComponent(tabApplication));
        }
    }

    private ApplicationBean createAppBean() {
        if (this.appBean == null) {
            this.appBean = new ApplicationBean();
        }
        return this.appBean;
    }

    private ParcelPanel createParcelPanel() {
        return new ParcelPanel(null, false);
    }

    private void validateNewParcel() {
        String parcelId = parcelPanel.getCadastreObject().getId();
        String parcelName = parcelPanel.getCadastreObject().getNameFirstpart() + ' ' + parcelPanel.getCadastreObject().getNameLastpart();
        final List<CadastreObjectBean> searchResult = new ArrayList<CadastreObjectBean>();
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getCadastreService().getCadastreObjectByAllParts(parcelName),
                CadastreObjectBean.class, (List) searchResult);
        for (CadastreObjectBean co : searchResult) {
            if (!co.getId().equals(parcelId)) {
                // Parcel with the same name that is not the current parcel
                MessageUtility.displayMessage(ClientMessage.BAUNIT_PARCEL_EXISTS);
                return;
            }
        }
        if (parcelPanel.getCadastreObject().validate(true).size() <= 0) {
            close(parcelPanel.getCadastreObject());
        }
    }

    private void configureSecurity() {
        SecurityClassificationDialog form = new SecurityClassificationDialog(parcelPanel.getCadastreObject(),
                MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.setVisible(true);
    }

    private void close(CadastreObjectBean selected) {
        if (selected != null) {
            this.firePropertyChange(SELECTED_PARCEL, null, selected.copy());
            this.dispose();
        } else {
            MessageUtility.displayMessage(ClientMessage.BAUNIT_SELECT_PARCEL);
        }
    }

    public CadastreObjectBean getSelectedAppParcel() {
        return selectedAppParcel;
    }

    public void setSelectedAppParcel(CadastreObjectBean selectedAppParcel) {
        CadastreObjectBean old = this.selectedAppParcel;
        this.selectedAppParcel = selectedAppParcel;
        this.firePropertyChange(APP_PARCEL_PROPERTY, old, this.selectedAppParcel);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        appBean = createAppBean();
        tabMain = new javax.swing.JTabbedPane();
        tabApplication = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnAppSelect = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableWithDefaultStyles1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel3 = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnNewClose = new javax.swing.JButton();
        btnNewSecurity = new javax.swing.JButton();
        parcelPanel = createParcelPanel();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSearchSelect = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        txtParcelSearch = new org.sola.clients.swing.ui.cadastre.CadastreObjectSearch2();
        jLabel1 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        setTitle(bundle.getString("AddParcelDialog.title")); // NOI18N
        setName("Form"); // NOI18N

        tabMain.setName("tabMain"); // NOI18N

        tabApplication.setName("tabApplication"); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnAppSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm.png"))); // NOI18N
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/cadastre/Bundle"); // NOI18N
        btnAppSelect.setText(bundle1.getString("AddParcelDialog.btnAppSelect.text")); // NOI18N
        btnAppSelect.setFocusable(false);
        btnAppSelect.setName("btnAppSelect"); // NOI18N
        btnAppSelect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAppSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAppSelectActionPerformed(evt);
            }
        });
        jToolBar2.add(btnAppSelect);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTableWithDefaultStyles1.setName("jTableWithDefaultStyles1"); // NOI18N
        jTableWithDefaultStyles1.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cadastreObjectFilteredList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, jTableWithDefaultStyles1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${officialAreaSize}"));
        columnBinding.setColumnName("Official Area Size");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${landUseType.displayValue}"));
        columnBinding.setColumnName("Land Use Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${addressString}"));
        columnBinding.setColumnName("Address String");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stateLandStatusType.displayValue}"));
        columnBinding.setColumnName("State Land Status Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedAppParcel}"), jTableWithDefaultStyles1, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jTableWithDefaultStyles1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableWithDefaultStyles1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableWithDefaultStyles1);
        if (jTableWithDefaultStyles1.getColumnModel().getColumnCount() > 0) {
            jTableWithDefaultStyles1.getColumnModel().getColumn(0).setHeaderValue(bundle1.getString("AddParcelDialog.jTableWithDefaultStyles1.columnModel.title0")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(1).setHeaderValue(bundle1.getString("AddParcelDialog.jTableWithDefaultStyles1.columnModel.title1")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(2).setHeaderValue(bundle1.getString("AddParcelDialog.jTableWithDefaultStyles1.columnModel.title2")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(2).setCellRenderer(new AreaCellRenderer()	);
            jTableWithDefaultStyles1.getColumnModel().getColumn(3).setHeaderValue(bundle1.getString("AddParcelDialog.jTableWithDefaultStyles1.columnModel.title3")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(4).setPreferredWidth(180);
            jTableWithDefaultStyles1.getColumnModel().getColumn(4).setHeaderValue(bundle1.getString("AddParcelDialog.jTableWithDefaultStyles1.columnModel.title4")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(4).setCellRenderer(new CellDelimitedListRenderer("; ", false) );
            jTableWithDefaultStyles1.getColumnModel().getColumn(5).setHeaderValue(bundle1.getString("AddParcelDialog.jTableWithDefaultStyles1.columnModel.title5")); // NOI18N
        }

        javax.swing.GroupLayout tabApplicationLayout = new javax.swing.GroupLayout(tabApplication);
        tabApplication.setLayout(tabApplicationLayout);
        tabApplicationLayout.setHorizontalGroup(
            tabApplicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabApplicationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabApplicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabApplicationLayout.setVerticalGroup(
            tabApplicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabApplicationLayout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabMain.addTab(bundle1.getString("AddParcelDialog.tabApplication.TabConstraints.tabTitle"), tabApplication); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        btnNewClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm-close.png"))); // NOI18N
        btnNewClose.setText(bundle1.getString("AddParcelDialog.btnNewClose.text")); // NOI18N
        btnNewClose.setFocusable(false);
        btnNewClose.setName("btnNewClose"); // NOI18N
        btnNewClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewCloseActionPerformed(evt);
            }
        });
        jToolBar3.add(btnNewClose);

        btnNewSecurity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lock.png"))); // NOI18N
        btnNewSecurity.setText(bundle1.getString("AddParcelDialog.btnNewSecurity.text")); // NOI18N
        btnNewSecurity.setFocusable(false);
        btnNewSecurity.setName("btnNewSecurity"); // NOI18N
        btnNewSecurity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewSecurityActionPerformed(evt);
            }
        });
        jToolBar3.add(btnNewSecurity);

        parcelPanel.setName("parcelPanel"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(parcelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(parcelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabMain.addTab(bundle1.getString("AddParcelDialog.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSearchSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm.png"))); // NOI18N
        btnSearchSelect.setText(bundle1.getString("AddParcelDialog.btnSearchSelect.text")); // NOI18N
        btnSearchSelect.setName("btnSearchSelect"); // NOI18N
        btnSearchSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchSelectActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSearchSelect);

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel5.setName("jPanel5"); // NOI18N

        txtParcelSearch.setName("txtParcelSearch"); // NOI18N

        jLabel1.setText(bundle1.getString("AddParcelDialog.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtParcelSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtParcelSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.add(jPanel5);

        jPanel8.setName("jPanel8"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 51, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel8);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(353, Short.MAX_VALUE))
        );

        tabMain.addTab(bundle1.getString("AddParcelDialog.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnSearchSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchSelectActionPerformed
    close((CadastreObjectBean) txtParcelSearch.getSelectedObject());
}//GEN-LAST:event_btnSearchSelectActionPerformed

    private void btnAppSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppSelectActionPerformed
        close(getSelectedAppParcel());
    }//GEN-LAST:event_btnAppSelectActionPerformed

    private void btnNewCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewCloseActionPerformed
        validateNewParcel();
    }//GEN-LAST:event_btnNewCloseActionPerformed

    private void btnNewSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewSecurityActionPerformed
        configureSecurity();
    }//GEN-LAST:event_btnNewSecurityActionPerformed

    private void jTableWithDefaultStyles1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableWithDefaultStyles1MouseClicked
        if (evt.getClickCount() == 2) {
            close(getSelectedAppParcel());
        }
    }//GEN-LAST:event_jTableWithDefaultStyles1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.application.ApplicationBean appBean;
    private javax.swing.JButton btnAppSelect;
    private javax.swing.JButton btnNewClose;
    private javax.swing.JButton btnNewSecurity;
    private javax.swing.JButton btnSearchSelect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles jTableWithDefaultStyles1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private org.sola.clients.swing.ui.cadastre.ParcelPanel parcelPanel;
    private javax.swing.JPanel tabApplication;
    private javax.swing.JTabbedPane tabMain;
    private org.sola.clients.swing.ui.cadastre.CadastreObjectSearch2 txtParcelSearch;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
