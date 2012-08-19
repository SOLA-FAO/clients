package org.sola.clients.swing.ui.source;

import org.sola.clients.beans.source.PowerOfAttorneyBean;

/**
 * Used for viewing Power of Attorney in read only mode.
 */
public class PowerOfAttorneyViewPanel extends javax.swing.JPanel {

    private PowerOfAttorneyBean powerOfAttorney;

    /**
     * Default constructor.
     */
    public PowerOfAttorneyViewPanel() {
        this(null);
    }

    /**
     * Panel constructor with provided {@link PowerOfAttorneyBean} to show on
     * the panel.
     */
    public PowerOfAttorneyViewPanel(PowerOfAttorneyBean powerOfAttorney) {
        if (powerOfAttorney == null) {
            this.powerOfAttorney = new PowerOfAttorneyBean();
        } else {
            this.powerOfAttorney = powerOfAttorney;
        }
        initComponents();
        documentViewerPanel.setSourceBean(this.powerOfAttorney.getSource());
    }

    public PowerOfAttorneyBean getPowerOfAttorney() {
        return powerOfAttorney;
    }

    private void setupPowerOfAttorney(PowerOfAttorneyBean powerOfAttorney) {
        PowerOfAttorneyBean oldValue = this.powerOfAttorney;
        if (powerOfAttorney == null) {
            this.powerOfAttorney = new PowerOfAttorneyBean();
        } else {
            this.powerOfAttorney = powerOfAttorney;
        }
        documentViewerPanel.setSourceBean(this.powerOfAttorney.getSource());
        firePropertyChange("powerOfAttorney", oldValue, this.powerOfAttorney);
    }

    public void setPowerOfAttorney(PowerOfAttorneyBean powerOfAttorney) {
        setupPowerOfAttorney(powerOfAttorney);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        documentViewerPanel = new org.sola.clients.swing.ui.source.DocumentViewerPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtAttorneyName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtPersonName = new javax.swing.JTextField();

        jPanel3.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("PowerOfAttorneyViewPanel.jLabel1.text")); // NOI18N

        txtAttorneyName.setBackground(new java.awt.Color(255, 255, 255));
        txtAttorneyName.setEditable(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${powerOfAttorney.attorneyName}"), txtAttorneyName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 158, Short.MAX_VALUE))
            .addComponent(txtAttorneyName)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAttorneyName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel1);

        jLabel2.setText(bundle.getString("PowerOfAttorneyViewPanel.jLabel2.text")); // NOI18N

        txtPersonName.setBackground(new java.awt.Color(255, 255, 255));
        txtPersonName.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${powerOfAttorney.personName}"), txtPersonName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 168, Short.MAX_VALUE))
            .addComponent(txtPersonName)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPersonName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(documentViewerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(documentViewerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.ui.source.DocumentViewerPanel documentViewerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField txtAttorneyName;
    private javax.swing.JTextField txtPersonName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
