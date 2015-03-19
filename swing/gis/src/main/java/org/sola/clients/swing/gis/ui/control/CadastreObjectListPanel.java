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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.swing.gis.ui.control;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.swing.gis.beans.AbstractListSpatialBean;
import org.sola.clients.swing.gis.beans.CadastreObjectListBean;
import org.sola.clients.swing.gis.beans.SpatialBean;

/**
 * A User Interface component that handles the management of the cadastre objects.
 * 
 * @author Elton Manoku
 */
public class CadastreObjectListPanel extends javax.swing.JPanel {

    private CadastreObjectListBean theBean = null;
    private String cadastreObjectType;

    /**
     * This constructor must be used to initialize the bean.
     */
    public CadastreObjectListPanel(CadastreObjectListBean bean) {
        this.theBean = bean;
        initComponents();
        // Add a listner to the bean property of selected bean
        theBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(AbstractListSpatialBean.SELECTED_BEAN_PROPERTY)) {
                    customizeButtons((SpatialBean) evt.getNewValue());
                }
            }
        });
    }
    
      /**
     * This constructor must be used to initialize the bean.
     */
    public CadastreObjectListPanel(CadastreObjectListBean bean, Boolean lastPartEditable) {
        this.theBean = bean;
        final  Boolean lastEditable = lastPartEditable;
        initComponents();
        // Add a listner to the bean property of selected bean
        theBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(AbstractListSpatialBean.SELECTED_BEAN_PROPERTY)) {
                    customizePanel((SpatialBean) evt.getNewValue());
                }
            }
        });
    }
    
    
        
     /**
     * It changes the availability of buttons based in the selected bean
     * @param selectedSource 
     */
    private void customizePanel(SpatialBean selectedSource) {
        cmdRemove.setEnabled(selectedSource != null);
        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${beanList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectListBean, eLProperty, tableCadastreObject);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(true);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${officialArea}"));
        columnBinding.setColumnName("Official Area");
        columnBinding.setColumnClass(Double.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedBean}"), tableCadastreObject, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);
        
    }
   
    /**
     * It changes the availability of buttons based in the selected bean
     * @param selectedSource 
     */
    private void customizeButtons(SpatialBean selectedSource) {
        cmdRemove.setEnabled(selectedSource != null);
    }
    
    /**
     * This constructor is only for the designer.
     */
    public CadastreObjectListPanel() {
        initComponents();
    }

    /**
     * It creates the bean. It is called from the generated code.
     * @return 
     */
    private CadastreObjectListBean createBean() {
        if (this.theBean == null) {
            return new CadastreObjectListBean();
        }
        return this.theBean;
    }

    /**
     * Gets the type of the cadastre objects that will be shown in the list
     * @return 
     */
    public String getCadastreObjectType() {
        return cadastreObjectType;
    }

    /**
     * Sets the type of the cadastre objects that will be shown in the list. Based in the type,
     * different attributes of the cadastre object can be hidden or made visible.
     * 
     * @param cadastreObjectType 
     */
    public void setCadastreObjectType(String cadastreObjectType) {
        this.cadastreObjectType = cadastreObjectType;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        cadastreObjectListBean = createBean();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableCadastreObject = new javax.swing.JTable();
        cmdRemove = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(300, 180));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${beanList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectListBean, eLProperty, tableCadastreObject);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${officialArea}"));
        columnBinding.setColumnName("Official Area");
        columnBinding.setColumnClass(Double.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedBean}"), tableCadastreObject, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableCadastreObject);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/gis/ui/control/Bundle"); // NOI18N
        tableCadastreObject.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("CadastreObjectListPanel.tableCadastreObject.columnModel.title0")); // NOI18N
        tableCadastreObject.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("CadastreObjectListPanel.tableCadastreObject.columnModel.title1")); // NOI18N
        tableCadastreObject.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("CadastreObjectListPanel.tableCadastreObject.columnModel.title2")); // NOI18N

        cmdRemove.setText(bundle.getString("CadastreObjectListPanel.cmdRemove.text")); // NOI18N
        cmdRemove.setEnabled(false);
        cmdRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRemoveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 204, Short.MAX_VALUE)
                        .addComponent(cmdRemove)))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(cmdRemove)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRemoveActionPerformed
        if (theBean.getSelectedBean() != null) {
            theBean.getBeanList().remove(theBean.getSelectedBean());
            theBean.setSelectedBean(null);
        }
    }//GEN-LAST:event_cmdRemoveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.gis.beans.CadastreObjectListBean cadastreObjectListBean;
    private javax.swing.JButton cmdRemove;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tableCadastreObject;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
