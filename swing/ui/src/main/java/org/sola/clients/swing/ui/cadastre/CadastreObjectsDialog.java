package org.sola.clients.swing.ui.cadastre;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;
import org.sola.clients.beans.cadastre.CadastreObjectBean;

/**
 * Shows list of cadastre objects and allows to select one.
 */
public class CadastreObjectsDialog extends javax.swing.JDialog {
    private final String SELECTED_CADASTRE_OBJECT = "selectedCadastreObject";
    public static final String SELECT_CADASTRE_OBJECT = "selectCadastreObject";
    
    private List<CadastreObjectBean> cadastreObjects = ObservableCollections.observableList(new ArrayList<CadastreObjectBean>());
    private CadastreObjectBean selectedCadastreObject;
    
    /**
     * Creates new form CadastreObjectsDialog
     */
    public CadastreObjectsDialog(List<CadastreObjectBean> cadastreObjects, 
            java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.cadastreObjects = cadastreObjects;
        initComponents();
        postInit();
    }
    
    private void postInit(){
        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(SELECTED_CADASTRE_OBJECT)){
                    customizeSelectButtons();
                }
            }
        });
        customizeSelectButtons();
    }

    private void customizeSelectButtons(){
        boolean enabled = selectedCadastreObject != null;
        btnSelect.setEnabled(enabled);
        menuSelect1.setEnabled(btnSelect.isEnabled());
    }
    
    public List<CadastreObjectBean> getCadastreObjects() {
        return cadastreObjects;
    }

    public CadastreObjectBean getSelectedCadastreObject() {
        return selectedCadastreObject;
    }

    public void setSelectedCadastreObject(CadastreObjectBean selectedCadastreObject) {
        this.selectedCadastreObject = selectedCadastreObject;
        firePropertyChange(SELECTED_CADASTRE_OBJECT, null, this.selectedCadastreObject);
    }

    private void select(){
        if(selectedCadastreObject!=null){
            firePropertyChange(SELECT_CADASTRE_OBJECT, null, selectedCadastreObject);
            this.dispose();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popupCadastreObjects = new javax.swing.JPopupMenu();
        menuSelect1 = new org.sola.clients.swing.common.menuitems.MenuSelect();
        jToolBar1 = new javax.swing.JToolBar();
        btnSelect = new org.sola.clients.swing.common.buttons.BtnSelect();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableWithDefaultStyles1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        menuSelect1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSelect1ActionPerformed(evt);
            }
        });

        popupCadastreObjects.add(menuSelect1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/cadastre/Bundle"); // NOI18N
        setTitle(bundle.getString("CadastreObjectsDialog.title")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSelect.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSelect);

        jTableWithDefaultStyles1.setComponentPopupMenu(popupCadastreObjects);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cadastreObjects}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, jTableWithDefaultStyles1);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${cadastreObjectType.displayValue}"));
        columnBinding.setColumnName("Cadastre Object Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sourceReference}"));
        columnBinding.setColumnName("Source Reference");
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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedCadastreObject}"), jTableWithDefaultStyles1, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTableWithDefaultStyles1);
        jTableWithDefaultStyles1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("CadastreObjectsDialog.jTableWithDefaultStyles1.columnModel.title0_1")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("CadastreObjectsDialog.jTableWithDefaultStyles1.columnModel.title1_1")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("CadastreObjectsDialog.jTableWithDefaultStyles1.columnModel.title6")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("CadastreObjectsDialog.jTableWithDefaultStyles1.columnModel.title7")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("CadastreObjectsDialog.jTableWithDefaultStyles1.columnModel.title2_1")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("CadastreObjectsDialog.jTableWithDefaultStyles1.columnModel.title4")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("CadastreObjectsDialog.jTableWithDefaultStyles1.columnModel.title3_1")); // NOI18N
        jTableWithDefaultStyles1.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("CadastreObjectsDialog.jTableWithDefaultStyles1.columnModel.title5")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        select();
    }//GEN-LAST:event_btnSelectActionPerformed

    private void menuSelect1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSelect1ActionPerformed
        select();
    }//GEN-LAST:event_menuSelect1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnSelect btnSelect;
    private javax.swing.JScrollPane jScrollPane1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles jTableWithDefaultStyles1;
    private javax.swing.JToolBar jToolBar1;
    private org.sola.clients.swing.common.menuitems.MenuSelect menuSelect1;
    private javax.swing.JPopupMenu popupCadastreObjects;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
