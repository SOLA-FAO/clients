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
package org.sola.clients.swing.desktop.application;

import java.awt.ComponentOrientation;
import java.awt.Frame;
import java.util.Locale;
import javax.swing.ImageIcon;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.controls.SolaList;

/**
 * Popup window to select property object from the list.
 */
public class PropertiesList extends javax.swing.JDialog {

    public static final String SELECTED_PROPERTY = "selectedProperty";
    
    private SolaList propertyList;
    private ApplicationPropertyBean selectedProperty;
    
    /** Creates new form PropertiesList */
    public PropertiesList() {
        this(new SolaList());
    }

    public PropertiesList(SolaList<ApplicationPropertyBean> propertyList) {
        super((Frame)null, true);
        this.propertyList = propertyList;
        initComponents();
        this.setIconImage(new ImageIcon(PropertiesList.class.getResource("/images/sola/logo_icon.jpg")).getImage());
    }
    
    public ObservableList<ApplicationPropertyBean> getPropertyList() {
        return propertyList.getFilteredList();
    }

    public ApplicationPropertyBean getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(ApplicationPropertyBean selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        scrollPropertyDetails = new javax.swing.JScrollPane();
        tabPropertyDetails = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        setTitle(bundle.getString("PropertiesList.title")); // NOI18N
        setName("Form"); // NOI18N

        scrollPropertyDetails.setFont(new java.awt.Font("Tahoma 12 12", 0, 12)); // NOI18N
        scrollPropertyDetails.setName("scrollPropertyDetails"); // NOI18N
        scrollPropertyDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabPropertyDetails.setName("tabPropertyDetails"); // NOI18N
        tabPropertyDetails.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${propertyList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tabPropertyDetails);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${area}"));
        columnBinding.setColumnName("Area");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalValue}"));
        columnBinding.setColumnName("Total Value");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedProperty}"), tabPropertyDetails, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tabPropertyDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        tabPropertyDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablePropertiesMouseCliecked(evt);
            }
        });
        scrollPropertyDetails.setViewportView(tabPropertyDetails);
        tabPropertyDetails.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertiesList.tabPropertyDetails.columnModel.title0")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertiesList.tabPropertyDetails.columnModel.title1")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertiesList.tabPropertyDetails.columnModel.title2")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertiesList.tabPropertyDetails.columnModel.title3")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablePropertiesMouseCliecked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablePropertiesMouseCliecked
        if(selectedProperty!=null){
            firePropertyChange(SELECTED_PROPERTY, null, selectedProperty);
        }
    }//GEN-LAST:event_tablePropertiesMouseCliecked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPropertyDetails;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabPropertyDetails;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
