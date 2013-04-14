package org.sola.clients.swing.ui.reports;

public class FreeTextDialog extends javax.swing.JDialog {
    
    public static final String TEXT_TO_SAVE = "textToSave";
    
    /**
     * Default constructor
     */
    public FreeTextDialog(String titleText, String textToEdit,
            java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        if(titleText!=null){
            setTitle(titleText);
        }
        txtFreeText.setText(textToEdit);
    }

    public void setFreeText(String text){
        txtFreeText.setText(text);
    }
    
    public String getFreeText(){
        return txtFreeText.getText();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnSave1 = new org.sola.clients.swing.common.buttons.BtnSave();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtFreeText = new javax.swing.JTextArea();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/reports/Bundle"); // NOI18N
        setTitle(bundle.getString("FreeTextDialog.title")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSave1ActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave1);

        txtFreeText.setColumns(20);
        txtFreeText.setRows(5);
        jScrollPane1.setViewportView(txtFreeText);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSave1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSave1ActionPerformed
        firePropertyChange(TEXT_TO_SAVE, null, txtFreeText.getText());
        this.dispose();
    }//GEN-LAST:event_btnSave1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.buttons.BtnSave btnSave1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextArea txtFreeText;
    // End of variables declaration//GEN-END:variables
}
