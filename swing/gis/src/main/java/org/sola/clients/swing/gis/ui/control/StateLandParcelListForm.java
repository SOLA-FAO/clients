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
package org.sola.clients.swing.gis.ui.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import javax.swing.ImageIcon;
import org.sola.clients.swing.gis.beans.AbstractListSpatialBean;
import org.sola.clients.swing.gis.beans.SpatialBean;
import org.sola.clients.swing.gis.beans.StateLandParcelBean;
import org.sola.clients.swing.gis.beans.StateLandParcelListBean;
import org.sola.clients.swing.ui.renderers.AreaCellRenderer;
import org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer;
import org.sola.common.WindowUtility;

/**
 *
 * @author soladev
 */
public class StateLandParcelListForm extends javax.swing.JDialog {

    private boolean readOnly = false;

    /**
     * Creates new form StateLandParcelListForm
     */
    public StateLandParcelListForm(StateLandParcelListBean listBean,
            boolean readOnly, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.listBean = listBean;
        this.readOnly = readOnly;
        initComponents();
        customizeForm();
    }

    private StateLandParcelListBean createListBean() {
        if (listBean == null) {
            listBean = new StateLandParcelListBean();
        }
        return listBean;
    }

    private void customizeForm() {
        WindowUtility.addEscapeListener(this, false);
        WindowUtility.centerForm(this);
        URL imgURL = this.getClass().getResource("/images/logo_icon.jpg");
        this.setIconImage(new ImageIcon(imgURL).getImage());
        listBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(AbstractListSpatialBean.SELECTED_BEAN_PROPERTY)) {
                    customizeButtons((SpatialBean) evt.getNewValue());
                }
            }
        });
        customizeButtons(null);
    }

    private void customizeButtons(SpatialBean selectedBean) {
        btnEdit1.setEnabled(!readOnly && selectedBean != null);
        btnRemove1.setEnabled(!readOnly && selectedBean != null);
        btnView1.setEnabled(selectedBean != null);
    }

    private void save() {
        clearSelection();
        this.setVisible(false);
    }

    private void showParcel(boolean readOnly) {
        if (listBean.getSelectedBean() != null) {
            StateLandParcelForm form = new StateLandParcelForm(
                    (StateLandParcelBean) listBean.getSelectedBean(), readOnly,
                    WindowUtility.getTopFrame(), true);
            form.setVisible(true);
        }
    }

    private void removeParcel() {
        if (listBean.getSelectedBean() != null) {
            listBean.getBeanList().remove((StateLandParcelBean) listBean.getSelectedBean());
        }
    }

    /**
     * Clears the selected item from the listBean as well as stopping any cell
     * editing that may be occurring.
     */
    private void clearSelection() {
        // If the table is being edited, stop the editing to accept the value entered by the user. 
        if (this.tblParcelList.getCellEditor() != null) {
            this.tblParcelList.getCellEditor().stopCellEditing();
        }
        // Clear the selected feature
        this.tblParcelList.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        listBean = createListBean();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new org.sola.clients.swing.common.buttons.BtnSave();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnView1 = new org.sola.clients.swing.common.buttons.BtnView();
        btnEdit1 = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemove1 = new org.sola.clients.swing.common.buttons.BtnRemove();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnClear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblParcelList = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        setTitle(bundle.getString("StateLandParcelListForm.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/confirm-close.png"))); // NOI18N
        btnSave.setText(bundle.getString("StateLandParcelListForm.btnSave.text")); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);
        jToolBar1.add(jSeparator1);

        btnView1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnView1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnView1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnView1);

        btnEdit1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEdit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEdit1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit1);

        btnRemove1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemove1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemove1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemove1);
        jToolBar1.add(jSeparator2);

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/clear.png"))); // NOI18N
        btnClear.setText(bundle.getString("StateLandParcelListForm.btnClear.text")); // NOI18N
        btnClear.setFocusable(false);
        btnClear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClear);

        tblParcelList.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${beanList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listBean, eLProperty, tblParcelList);
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
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listBean, org.jdesktop.beansbinding.ELProperty.create("${selectedBean}"), tblParcelList, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblParcelList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblParcelListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblParcelList);
        if (tblParcelList.getColumnModel().getColumnCount() > 0) {
            tblParcelList.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("StateLandParcelListForm.tblParcelList.columnModel.title0")); // NOI18N
            tblParcelList.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("StateLandParcelListForm.tblParcelList.columnModel.title1")); // NOI18N
            tblParcelList.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("StateLandParcelListForm.tblParcelList.columnModel.title2")); // NOI18N
            tblParcelList.getColumnModel().getColumn(2).setCellRenderer(new AreaCellRenderer());
            tblParcelList.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("StateLandParcelListForm.tblParcelList.columnModel.title3")); // NOI18N
            tblParcelList.getColumnModel().getColumn(4).setPreferredWidth(180);
            tblParcelList.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("StateLandParcelListForm.tblParcelList.columnModel.title4")); // NOI18N
            tblParcelList.getColumnModel().getColumn(4).setCellRenderer(new CellDelimitedListRenderer("; ", false));
            tblParcelList.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("StateLandParcelListForm.tblParcelList.columnModel.title5")); // NOI18N
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        save();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnEdit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEdit1ActionPerformed
        showParcel(false);
    }//GEN-LAST:event_btnEdit1ActionPerformed

    private void btnView1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnView1ActionPerformed
        showParcel(true);
    }//GEN-LAST:event_btnView1ActionPerformed

    private void btnRemove1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemove1ActionPerformed
        removeParcel();
    }//GEN-LAST:event_btnRemove1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        clearSelection();
    }//GEN-LAST:event_formWindowClosing

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearSelection();
    }//GEN-LAST:event_btnClearActionPerformed

    private void tblParcelListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblParcelListMouseClicked
        if (evt.getClickCount() == 2) {
            if (btnEdit1.isEnabled()) {
                showParcel(false);
            } else {
                showParcel(true);
            }
        }
    }//GEN-LAST:event_tblParcelListMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEdit1;
    private org.sola.clients.swing.common.buttons.BtnRemove btnRemove1;
    private org.sola.clients.swing.common.buttons.BtnSave btnSave;
    private org.sola.clients.swing.common.buttons.BtnView btnView1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.swing.gis.beans.StateLandParcelListBean listBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblParcelList;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
