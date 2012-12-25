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
package org.sola.clients.swing.ui.cadastre;

import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.swing.ui.renderers.SimpleComboBoxRenderer;

/**
 * Parcel panel to create and manage parcel objects
 */
public class ParcelPanel extends javax.swing.JPanel {

    private CadastreObjectBean createCadastreBean(){
        CadastreObjectBean result = new CadastreObjectBean();
        return result;
    }
    
    public ParcelPanel() {
        initComponents();
    }
 
    public CadastreObjectBean getCadastreObject(){
        return cadastreObjectBean1;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        cadastreObjectBean1 = createCadastreBean();
        cadastreObjectTypeListBean1 = new org.sola.clients.beans.referencedata.CadastreObjectTypeListBean();
        landUseTypeListBean1 = new org.sola.clients.beans.referencedata.LandUseTypeListBean();
        jLabel17 = new javax.swing.JLabel();
        txtParcelFirstPart = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtParcelLastPart = new javax.swing.JTextField();
        cbxParcelEstateType = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtParcelSurveyRef = new javax.swing.JTextField();
        cbxLandUse = new javax.swing.JComboBox();
        labLandUse = new javax.swing.JLabel();

        setName("Form"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/cadastre/Bundle"); // NOI18N
        jLabel17.setText(bundle.getString("ParcelPanel.jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        txtParcelFirstPart.setName("txtParcelFirstPart"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"), txtParcelFirstPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel7.setText(bundle.getString("ParcelPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtParcelLastPart.setName("txtParcelLastPart"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"), txtParcelLastPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        cbxParcelEstateType.setName("cbxParcelEstateType"); // NOI18N
        cbxParcelEstateType.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cadastreObjectTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectTypeListBean1, eLProperty, cbxParcelEstateType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${cadastreObjectType}"), cbxParcelEstateType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel10.setText(bundle.getString("ParcelPanel.jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel9.setText(bundle.getString("ParcelPanel.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        txtParcelSurveyRef.setName("txtParcelSurveyRef"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${sourceReference}"), txtParcelSurveyRef, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        cbxLandUse.setName(bundle.getString("ParcelPanel.cbxLandUse.name")); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${landUseTypeList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, landUseTypeListBean1, eLProperty, cbxLandUse);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cadastreObjectBean1, org.jdesktop.beansbinding.ELProperty.create("${landUseType}"), cbxLandUse, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        labLandUse.setText(bundle.getString("ParcelPanel.labLandUse.text")); // NOI18N
        labLandUse.setName(bundle.getString("ParcelPanel.labLandUse.name")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel17)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 157, Short.MAX_VALUE)
                        .add(jLabel7)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 129, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cbxLandUse, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cbxParcelEstateType, 0, 187, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, txtParcelFirstPart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .add(jLabel10)
                                .add(0, 0, Short.MAX_VALUE)))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtParcelSurveyRef, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                            .add(jLabel9)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txtParcelLastPart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(labLandUse)
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jLabel17))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtParcelLastPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtParcelFirstPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel9)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtParcelSurveyRef, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cbxParcelEstateType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 9, Short.MAX_VALUE)
                .add(labLandUse)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cbxLandUse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {jLabel17, jLabel7}, org.jdesktop.layout.GroupLayout.VERTICAL);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.cadastre.CadastreObjectBean cadastreObjectBean1;
    private org.sola.clients.beans.referencedata.CadastreObjectTypeListBean cadastreObjectTypeListBean1;
    private javax.swing.JComboBox cbxLandUse;
    private javax.swing.JComboBox cbxParcelEstateType;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel labLandUse;
    private org.sola.clients.beans.referencedata.LandUseTypeListBean landUseTypeListBean1;
    private javax.swing.JTextField txtParcelFirstPart;
    private javax.swing.JTextField txtParcelLastPart;
    private javax.swing.JTextField txtParcelSurveyRef;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
