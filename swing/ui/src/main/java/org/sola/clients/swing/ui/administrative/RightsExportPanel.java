/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.ui.administrative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.filechooser.FileFilter;
import org.sola.clients.beans.administrative.RightsExportResultBean;
import org.sola.clients.beans.administrative.RightsExportResultListBean;
import org.sola.clients.beans.referencedata.RrrTypeListBean;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.FileUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Panel to search rights and export results into CSV file.
 */
public class RightsExportPanel extends javax.swing.JPanel {

    private RrrTypeListBean createRightTypes() {
        if (rrrTypes == null) {
            rrrTypes = new RrrTypeListBean(true);
        }
        return rrrTypes;
    }

    /**
     * Default constructor
     */
    public RightsExportPanel() {
        initComponents();
        postInit();
    }

    private void postInit() {
        rightsExportResults.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RightsExportResultListBean.LIST_ITEM_CHECKED)) {
                    customizeExportSelectedButtons((Boolean) evt.getNewValue());
                }
            }
        });
        customizeExportAllButtons(false);
        customizeExportSelectedButtons(false);
    }

    private void customizeExportSelectedButtons(boolean checked) {
        btnExportSelected.setEnabled(checked);
    }

    private void customizeExportAllButtons(boolean enabled) {
        btnExportAll.setEnabled(enabled);
    }

    private void clean() {
        cbxRightType.setSelectedIndex(0);
        txtDateFrom.setValue(null);
        txtDateTo.setValue(null);
        rightsExportResults.getRightsList().clear();
        customizeExportAllButtons(false);
        customizeExportSelectedButtons(false);
        lblRecords.setText("0");
    }

    private void search() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SEARCH_RIGHTS));
                rightsExportResults.search(rightsExportParams);
                return null;
            }

            @Override
            public void taskDone() {
                if (rightsExportResults.getRightsList().size() < 1) {
                    customizeExportAllButtons(false);
                    customizeExportSelectedButtons(false);
                    lblRecords.setText("0");
                    MessageUtility.displayMessage(ClientMessage.SEARCH_NO_RESULTS);
                } else {
                    lblRecords.setText(String.valueOf(rightsExportResults.getRightsList().size()));
                    customizeExportAllButtons(true);
                }
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void export(final List<RightsExportResultBean> list) {
        if (list == null || list.size() < 1) {
            return;
        }

        JFileChooser jfc = new JFileChooser();
        jfc.removeChoosableFileFilter(jfc.getFileFilter());
        jfc.setAcceptAllFileFilterUsed(false);
        jfc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                String extension = FileUtility.getFileExtension(f);
                if (extension != null) {
                    if (extension.equalsIgnoreCase(FileUtility.csv)) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "CSV files";
            }
        });

        int result = jfc.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        
        final File file;
        if(jfc.getSelectedFile().getPath().endsWith("." + FileUtility.csv)){
            file = jfc.getSelectedFile();
        } else {
            file = new File(jfc.getSelectedFile().getPath() + "." + FileUtility.csv);
        }

        SolaTask t = new SolaTask<Boolean, Void>() {
            Exception ex = null;

            @Override
            public Boolean doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_EXPORT_RIGHTS));
                try {
                    if (!rightsExportResults.exportToCsv(file, list)) {
                        return false;
                    }
                } catch (Exception e) {
                    ex = e;
                    return false;
                }
                return true;
            }

            @Override
            public void taskDone() {
                if (ex!=null) {
                    MessageUtility.displayMessage(ClientMessage.BAUNIT_RIGHTS_EXPORT_FAILED, 
                            new Object[]{ex.getMessage()});
                } else if (get()) {
                    lblRecords.setText(String.valueOf(rightsExportResults.getRightsList().size()));
                    MessageUtility.displayMessage(ClientMessage.BAUNIT_RIGHTS_EXPORT_SUCCESS);
                }
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        rightsExportParams = new org.sola.clients.beans.administrative.RightsExportParamsBean();
        rightsExportResults = new org.sola.clients.beans.administrative.RightsExportResultListBean();
        rrrTypes = createRightTypes();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbxRightType = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnDateFrom = new javax.swing.JButton();
        txtDateFrom = new org.sola.clients.swing.common.controls.WatermarkDate();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btnDateTo = new javax.swing.JButton();
        txtDateTo = new org.sola.clients.swing.common.controls.WatermarkDate();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableRightResults = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnSearch = new javax.swing.JButton();
        btnClean = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnExportSelected = new javax.swing.JButton();
        btnExportAll = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jLabel4 = new javax.swing.JLabel();
        lblRecords = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel2.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/administrative/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("RightsExportPanel.jLabel1.text")); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrTypeBeanList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrTypes, eLProperty, cbxRightType);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rightsExportParams, org.jdesktop.beansbinding.ELProperty.create("${rightType}"), cbxRightType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
            .addComponent(cbxRightType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRightType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel3);

        jLabel2.setText(bundle.getString("RightsExportPanel.jLabel2.text")); // NOI18N

        btnDateFrom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnDateFrom.setText(bundle.getString("RightsExportPanel.btnDateFrom.text")); // NOI18N
        btnDateFrom.setBorder(null);
        btnDateFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDateFromActionPerformed(evt);
            }
        });

        txtDateFrom.setText(bundle.getString("RightsExportPanel.txtDateFrom.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rightsExportParams, org.jdesktop.beansbinding.ELProperty.create("${dateFrom}"), txtDateFrom, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(txtDateFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDateFrom))
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDateFrom)
                    .addComponent(txtDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel4);

        jLabel3.setText(bundle.getString("RightsExportPanel.jLabel3.text")); // NOI18N

        btnDateTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnDateTo.setText(bundle.getString("RightsExportPanel.btnDateTo.text")); // NOI18N
        btnDateTo.setBorder(null);
        btnDateTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDateToActionPerformed(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rightsExportParams, org.jdesktop.beansbinding.ELProperty.create("${dateTo}"), txtDateTo, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(txtDateTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDateTo))
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDateTo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel5);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 134, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel9);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rightsList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rightsExportResults, eLProperty, tableRightResults);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${checked}"));
        columnBinding.setColumnName("Checked");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${propCode}"));
        columnBinding.setColumnName("Prop Code");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${applicantFullName}"));
        columnBinding.setColumnName("Applicant Full Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${area}"));
        columnBinding.setColumnName("Area");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ownersFormatted}"));
        columnBinding.setColumnName("Owners Formatted");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"));
        columnBinding.setColumnName("Registration Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${expirationDate}"));
        columnBinding.setColumnName("Expiration Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rightsExportResults, org.jdesktop.beansbinding.ELProperty.create("${selectedRight}"), tableRightResults, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(tableRightResults);
        if (tableRightResults.getColumnModel().getColumnCount() > 0) {
            tableRightResults.getColumnModel().getColumn(0).setMaxWidth(25);
            tableRightResults.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("RightsExportPanel.tableRightResults.columnModel.title0_1")); // NOI18N
            tableRightResults.getColumnModel().getColumn(1).setPreferredWidth(100);
            tableRightResults.getColumnModel().getColumn(1).setMaxWidth(200);
            tableRightResults.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("RightsExportPanel.tableRightResults.columnModel.title4")); // NOI18N
            tableRightResults.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("RightsExportPanel.tableRightResults.columnModel.title1_1")); // NOI18N
            tableRightResults.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableRightResults.getColumnModel().getColumn(3).setMaxWidth(150);
            tableRightResults.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("RightsExportPanel.tableRightResults.columnModel.title2_1")); // NOI18N
            tableRightResults.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("RightsExportPanel.tableRightResults.columnModel.title3_1")); // NOI18N
            tableRightResults.getColumnModel().getColumn(5).setPreferredWidth(100);
            tableRightResults.getColumnModel().getColumn(5).setMaxWidth(120);
            tableRightResults.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("RightsExportPanel.tableRightResults.columnModel.title6")); // NOI18N
            tableRightResults.getColumnModel().getColumn(6).setPreferredWidth(100);
            tableRightResults.getColumnModel().getColumn(6).setMaxWidth(120);
            tableRightResults.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("RightsExportPanel.tableRightResults.columnModel.title5")); // NOI18N
        }

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        btnSearch.setText(bundle.getString("RightsExportPanel.btnSearch.text")); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSearch);

        btnClean.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/eraser.png"))); // NOI18N
        btnClean.setText(bundle.getString("RightsExportPanel.btnClean.text")); // NOI18N
        btnClean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCleanActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClean);
        jToolBar1.add(jSeparator2);

        btnExportSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/export.png"))); // NOI18N
        btnExportSelected.setText(bundle.getString("RightsExportPanel.btnExportSelected.text")); // NOI18N
        btnExportSelected.setFocusable(false);
        btnExportSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportSelectedActionPerformed(evt);
            }
        });
        jToolBar1.add(btnExportSelected);

        btnExportAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/export2.png"))); // NOI18N
        btnExportAll.setText(bundle.getString("RightsExportPanel.btnExportAll.text")); // NOI18N
        btnExportAll.setFocusable(false);
        btnExportAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportAllActionPerformed(evt);
            }
        });
        jToolBar1.add(btnExportAll);
        jToolBar1.add(jSeparator1);
        jToolBar1.add(filler1);

        jLabel4.setText(bundle.getString("RightsExportPanel.jLabel4.text")); // NOI18N
        jToolBar1.add(jLabel4);

        lblRecords.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRecords.setText(bundle.getString("RightsExportPanel.lblRecords.text")); // NOI18N
        jToolBar1.add(lblRecords);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 581, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnCleanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCleanActionPerformed
        clean();
    }//GEN-LAST:event_btnCleanActionPerformed

    private void btnExportSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportSelectedActionPerformed
        export(rightsExportResults.getSelectedRightsList());
    }//GEN-LAST:event_btnExportSelectedActionPerformed

    private void btnExportAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportAllActionPerformed
        export(rightsExportResults.getRightsList());
    }//GEN-LAST:event_btnExportAllActionPerformed

    private void btnDateFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDateFromActionPerformed
        showCalendar(txtDateFrom);
    }//GEN-LAST:event_btnDateFromActionPerformed

    private void btnDateToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDateToActionPerformed
        showCalendar(txtDateTo);
    }//GEN-LAST:event_btnDateToActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClean;
    private javax.swing.JButton btnDateFrom;
    private javax.swing.JButton btnDateTo;
    private javax.swing.JButton btnExportAll;
    private javax.swing.JButton btnExportSelected;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cbxRightType;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblRecords;
    private org.sola.clients.beans.administrative.RightsExportParamsBean rightsExportParams;
    private org.sola.clients.beans.administrative.RightsExportResultListBean rightsExportResults;
    private org.sola.clients.beans.referencedata.RrrTypeListBean rrrTypes;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRightResults;
    private org.sola.clients.swing.common.controls.WatermarkDate txtDateFrom;
    private org.sola.clients.swing.common.controls.WatermarkDate txtDateTo;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
