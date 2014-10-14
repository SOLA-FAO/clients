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
package org.sola.clients.swing.ui.administrative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.administrative.ConditionForRrrBean;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.referencedata.ConditionTypeListBean;
import org.sola.clients.swing.ui.renderers.BooleanCellRenderer2;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.common.WindowUtility;

/**
 *
 * @author solaDev
 */
public class ConditionsPanel extends javax.swing.JPanel {

    RrrBean.RRR_ACTION rrrAction;

    /**
     * Creates new form ConditionsPanel. This constructor is used by the
     * designer when the panel is added to a new form.
     */
    public ConditionsPanel() {
        this.rrrAction = RrrBean.RRR_ACTION.VIEW;
        initComponents();
    }

    /**
     * Constructor
     *
     * @param rrr
     * @param action
     */
    public ConditionsPanel(RrrBean rrr, RrrBean.RRR_ACTION action) {
        this.rrrBean = rrr;
        this.rrrAction = action;
        initComponents();
        postInit();
    }

    private RrrBean createRrrBean() {
        if (this.rrrBean == null) {
            this.rrrBean = new RrrBean();
        }
        return rrrBean;
    }

    private void postInit() {

        // Populate lease conditions list with standard conditions for new RrrBean
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            // SL Customization - don't preload standard conditions
            // rrrBean.addConditions(conditionTypes.getLeaseConditionList());
        }

        conditionTypes.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ConditionTypeListBean.SELECTED_CONDITION_TYPE_PROPERTY)) {
                    customizeAddStandardConditionButton();
                }
            }
        });

        rrrBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RrrBean.SELECTED_CONDITION_PROPERTY)) {
                    customizeLeaseConditionsButtons();
                }
            }
        });

        customizeAddStandardConditionButton();
        customizeLeaseConditionsButtons();
    }

    private void customizeLeaseConditionsButtons() {
        boolean enabled = rrrBean.getSelectedCondition() != null && rrrAction != RrrBean.RRR_ACTION.VIEW;

        btnRemoveCondition.setEnabled(enabled);
        btnEditCondition.setEnabled(enabled);
        menuEditCondition.setEnabled(btnEditCondition.isEnabled());
        menuRemoveCondition.setEnabled(btnRemoveCondition.isEnabled());
    }

    private void customizeAddStandardConditionButton() {
        if (rrrAction != RrrBean.RRR_ACTION.VIEW) {
            return;
        }
        btnAddStandardCond.setEnabled(conditionTypes.getSelectedConditionType() != null);
    }

    private void addCustomCondition() {
        CustomConditionDialog form = new CustomConditionDialog(null, null, true);
        WindowUtility.centerForm(form);
        WindowUtility.addEscapeListener(form, true);
        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CustomConditionDialog.CONDITION_SAVED)) {
                    rrrBean.addRrrCondition((ConditionForRrrBean) evt.getNewValue());
                }
            }
        });
        form.setVisible(true);
    }

    private void editCustomCondition() {

        ConditionForRrrBean copy = (ConditionForRrrBean) rrrBean.getSelectedCondition().copy();
        if (!copy.isCustomCondition()) {
            copy.setCustomConditionName(copy.getDisplayName());
            copy.setCustomConditionText(copy.getConditionType().getDescription());
        }
        CustomConditionDialog form = new CustomConditionDialog(copy, null, true);
        WindowUtility.centerForm(form);
        WindowUtility.addEscapeListener(form, true);

        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CustomConditionDialog.CONDITION_SAVED)) {
                    ConditionForRrrBean cond = (ConditionForRrrBean) evt.getNewValue();
                    if (rrrBean.getSelectedCondition().isCustomCondition()) {
                        rrrBean.getSelectedCondition().setCustomConditionText(cond.getCustomConditionText());
                    }
                    rrrBean.getSelectedCondition().setCustomConditionName(cond.getCustomConditionName());
                }
            }
        });
        form.setVisible(true);
    }

    private void addStandardCondition() {
        rrrBean.addCondition(conditionTypes.getSelectedConditionType());
    }

    private void removeCondition() {
        rrrBean.removeSelectedRrrCondition();
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

        rrrBean = createRrrBean();
        conditionTypes = new ConditionTypeListBean(true);
        leaseConditionsPopUp = new javax.swing.JPopupMenu();
        menuAddCustomCondition = new javax.swing.JMenuItem();
        menuEditCondition = new javax.swing.JMenuItem();
        menuRemoveCondition = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        jComboBox1 = new javax.swing.JComboBox();
        btnAddStandardCond = new org.sola.clients.swing.common.buttons.BtnAdd();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAddCustomCond = new org.sola.clients.swing.common.buttons.BtnAdd();
        btnEditCondition = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemoveCondition = new org.sola.clients.swing.common.buttons.BtnRemove();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableWithDefaultStyles1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        menuAddCustomCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/administrative/Bundle"); // NOI18N
        menuAddCustomCondition.setText(bundle.getString("ConditionsPanel.menuAddCustomCondition.text")); // NOI18N
        menuAddCustomCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddCustomConditionActionPerformed(evt);
            }
        });
        leaseConditionsPopUp.add(menuAddCustomCondition);

        menuEditCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditCondition.setText(bundle.getString("ConditionsPanel.menuEditCondition.text")); // NOI18N
        menuEditCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditConditionActionPerformed(evt);
            }
        });
        leaseConditionsPopUp.add(menuEditCondition);

        menuRemoveCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveCondition.setText(bundle.getString("ConditionsPanel.menuRemoveCondition.text")); // NOI18N
        menuRemoveCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveConditionActionPerformed(evt);
            }
        });
        leaseConditionsPopUp.add(menuRemoveCondition);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${leaseConditionList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, conditionTypes, eLProperty, jComboBox1);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, conditionTypes, org.jdesktop.beansbinding.ELProperty.create("${selectedConditionType}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(jComboBox1);

        btnAddStandardCond.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/select.png"))); // NOI18N
        btnAddStandardCond.setText(bundle.getString("ConditionsPanel.btnAddStandardCond.text")); // NOI18N
        btnAddStandardCond.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddStandardCond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddStandardCondActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddStandardCond);
        jToolBar1.add(jSeparator1);

        btnAddCustomCond.setText(bundle.getString("ConditionsPanel.btnAddCustomCond.text")); // NOI18N
        btnAddCustomCond.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddCustomCond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCustomCondActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddCustomCond);

        btnEditCondition.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditConditionActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEditCondition);

        btnRemoveCondition.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveConditionActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveCondition);

        jTableWithDefaultStyles1.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${conditionsFilteredList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, eLProperty, jTableWithDefaultStyles1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${displayName}"));
        columnBinding.setColumnName("Display Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${conditionText}"));
        columnBinding.setColumnName("Condition Text");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${customCondition}"));
        columnBinding.setColumnName("Custom Condition");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${selectedCondition}"), jTableWithDefaultStyles1, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTableWithDefaultStyles1);
        if (jTableWithDefaultStyles1.getColumnModel().getColumnCount() > 0) {
            jTableWithDefaultStyles1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ConditionsPanel.jTableWithDefaultStyles1.columnModel.title0")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(1).setPreferredWidth(400);
            jTableWithDefaultStyles1.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ConditionsPanel.jTableWithDefaultStyles1.columnModel.title1")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(1).setCellRenderer(new TableCellTextAreaRenderer());
            jTableWithDefaultStyles1.getColumnModel().getColumn(2).setPreferredWidth(50);
            jTableWithDefaultStyles1.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ConditionsPanel.jTableWithDefaultStyles1.columnModel.title2")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(2).setCellRenderer(new BooleanCellRenderer2());
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddCustomCondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCustomCondActionPerformed
        addCustomCondition();
    }//GEN-LAST:event_btnAddCustomCondActionPerformed

    private void btnEditConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditConditionActionPerformed
        editCustomCondition();
    }//GEN-LAST:event_btnEditConditionActionPerformed

    private void btnRemoveConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveConditionActionPerformed
        removeCondition();
    }//GEN-LAST:event_btnRemoveConditionActionPerformed

    private void btnAddStandardCondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStandardCondActionPerformed
        addStandardCondition();
    }//GEN-LAST:event_btnAddStandardCondActionPerformed

    private void menuAddCustomConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddCustomConditionActionPerformed
        addCustomCondition();
    }//GEN-LAST:event_menuAddCustomConditionActionPerformed

    private void menuEditConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditConditionActionPerformed
       editCustomCondition();
    }//GEN-LAST:event_menuEditConditionActionPerformed

    private void menuRemoveConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveConditionActionPerformed
        removeCondition();
    }//GEN-LAST:event_menuRemoveConditionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnAdd btnAddCustomCond;
    private org.sola.clients.swing.common.buttons.BtnAdd btnAddStandardCond;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEditCondition;
    private org.sola.clients.swing.common.buttons.BtnRemove btnRemoveCondition;
    private org.sola.clients.beans.referencedata.ConditionTypeListBean conditionTypes;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles jTableWithDefaultStyles1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPopupMenu leaseConditionsPopUp;
    private javax.swing.JMenuItem menuAddCustomCondition;
    private javax.swing.JMenuItem menuEditCondition;
    private javax.swing.JMenuItem menuRemoveCondition;
    private org.sola.clients.beans.administrative.RrrBean rrrBean;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
