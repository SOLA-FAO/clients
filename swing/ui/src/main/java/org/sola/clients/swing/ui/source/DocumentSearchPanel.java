/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.swing.ui.source;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFormattedTextField;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.referencedata.SourceTypeListBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceSearchParamsBean;
import org.sola.clients.beans.source.SourceSearchResultBean;
import org.sola.clients.beans.source.SourceSearchResultsListBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.renderers.AttachedDocumentCellRenderer;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This panel provides parameterized source (documents) search capabilities.
 * <p>The following list of beans is used to bind the data on the form:<br />
 * {@link SourceSearchResultsListBean},<br />{@link SourceSearchParamsBean}</p>
 */
public class DocumentSearchPanel extends javax.swing.JPanel {

    public static final String SELECTED_SOURCE = "selectedSource";
    public static final String EDIT_SOURCE = "editSource";
    public static final String ATTACH_SOURCE = "attachSource";
    
    /**
     * Default constructor to create form and initialize parameters.
     */
    public DocumentSearchPanel() {
        initComponents();
        customizeButtons();
        cbxSourceType.setSelectedIndex(-1);
        searchResultsList.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(SourceSearchResultsListBean.SELECTED_SOURCE_PROPERTY)) {
                    firePropertyChange(SELECTED_SOURCE, null, evt.getNewValue());
                    customizeButtons();
                }
            }
        });
    }

    private SourceTypeListBean createSourceTypes() {
        if (sourceTypesList == null) {
            sourceTypesList = new SourceTypeListBean(true);
        }
        return sourceTypesList;
    }

    public boolean isShowPrintButton() {
        return btnPrint.isVisible();
    }

    public void setShowPrintButton(boolean showPrintButton) {
        btnPrint.setVisible(showPrintButton);
        menuPrint.setVisible(showPrintButton);
    }
    
    public boolean isShowAttachButton() {
        return btnAttach.isVisible();
    }

    public void setShowAttachButton(boolean showAttachButton) {
        btnAttach.setVisible(showAttachButton);
        menuAttach.setVisible(showAttachButton);
    }

    public boolean isShowEditButton() {
        return btnEdit.isVisible();
    }

    public void setShowEditButton(boolean showEditButton) {
        btnEdit.setVisible(showEditButton);
        menuEdit.setVisible(showEditButton);
    }

    /**
     * Enables or disables printing button.
     */
    private void customizeButtons() {
        boolean enabled = false;
        if (searchResultsList.getSelectedSource() != null
                && searchResultsList.getSelectedSource().getArchiveDocumentId() != null
                && !searchResultsList.getSelectedSource().getArchiveDocumentId().isEmpty()) {
            enabled = true;
        }
        btnPrint.setEnabled(enabled);
        btnOpen.setEnabled(enabled);
        menuOpen.setEnabled(enabled);
        menuPrint.setEnabled(enabled);
        btnEdit.setEnabled(searchResultsList.getSelectedSource() != null
                && SecurityBean.isInRole(RolesConstants.SOURCE_SAVE));
        menuEdit.setEnabled(btnEdit.isEnabled());
        btnAttach.setEnabled(enabled);
        menuAttach.setEnabled(enabled);
    }

    private void clearForm() {
        cbxSourceType.setSelectedIndex(-1);
        txtDateFrom.setValue(null);
        txtDateTo.setValue(null);
        txtLaNr.setText(null);
        txtRefNumber.setText(null);
        txtSubmissionDateFrom.setValue(null);
        txtSubmissionDateTo.setValue(null);
    }

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    public SourceSearchResultBean getSelectedSource() {
        return searchResultsList.getSelectedSource();
    }

    private void print() {
        if (ApplicationServiceBean.saveInformationService(RequestTypeBean.CODE_DOCUMENT_COPY)) {
            openDocument();
        }
    }

    private void openDocument() {
        if(searchResultsList.getSelectedSource().getArchiveDocumentId() == null ||
                searchResultsList.getSelectedSource().getArchiveDocumentId().isEmpty()){
            return;
        }
        
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_OPENING));
                DocumentBean.openDocument(searchResultsList.getSelectedSource().getArchiveDocumentId());
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void fireEditSource() {
        if (searchResultsList.getSelectedSource() != null) {
            firePropertyChange(EDIT_SOURCE, null, SourceBean.getSource(
                    searchResultsList.getSelectedSource().getId()));
        }
    }
    
    private void fireAttach(){
        if(searchResultsList.getSelectedSource().getArchiveDocumentId()!=null &&
                !searchResultsList.getSelectedSource().getArchiveDocumentId().isEmpty()){
            firePropertyChange(ATTACH_SOURCE, null, 
                    SourceBean.getSource(searchResultsList.getSelectedSource().getId()));
        }
    }
    
    public void searchDocuments(){
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_SEARCHING));
                searchResultsList.searchSources(searchParams);
                return null;
            }

            @Override
            public void taskDone() {
                if (searchResultsList.getSourceSearchResultsList().size() > 100) {
                    MessageUtility.displayMessage(ClientMessage.SEARCH_TOO_MANY_RESULTS, new String[]{"100"});
                }
                lblResults.setText(String.format("(%s)", searchResultsList.getSourceSearchResultsList().size()));
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        searchResultsList = new org.sola.clients.beans.source.SourceSearchResultsListBean();
        searchParams = new org.sola.clients.beans.source.SourceSearchParamsBean();
        sourceTypesList = createSourceTypes();
        popUpSearchResults = new javax.swing.JPopupMenu();
        menuOpen = new javax.swing.JMenuItem();
        menuAttach = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenuItem();
        menuPrint = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSearchResults = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel9 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        btnClear = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        txtRefNumber = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtLaNr = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbxSourceType = new javax.swing.JComboBox();
        jPanel11 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        txtSubmissionDateFrom = new javax.swing.JFormattedTextField();
        btnSubmissionDateFrom = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtSubmissionDateTo = new javax.swing.JFormattedTextField();
        btnSubmissionDateTo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtDateFrom = new javax.swing.JFormattedTextField();
        btnDateFrom = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtDateTo = new javax.swing.JFormattedTextField();
        btnDateTo = new javax.swing.JButton();
        jToolBar1 = new javax.swing.JToolBar();
        btnOpen = new javax.swing.JButton();
        btnAttach = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        separatorPrint = new javax.swing.JToolBar.Separator();
        jLabel4 = new javax.swing.JLabel();
        lblResults = new javax.swing.JLabel();

        popUpSearchResults.setName("popUpSearchResults"); // NOI18N

        menuOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/source/Bundle"); // NOI18N
        menuOpen.setText(bundle.getString("DocumentSearchPanel.menuOpen.text")); // NOI18N
        menuOpen.setToolTipText(bundle.getString("DocumentSearchPanel.menuOpen.toolTipText")); // NOI18N
        menuOpen.setName("menuOpen"); // NOI18N
        menuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenActionPerformed(evt);
            }
        });
        popUpSearchResults.add(menuOpen);

        menuAttach.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/attachment.png"))); // NOI18N
        menuAttach.setText(bundle.getString("DocumentSearchPanel.menuAttach.text")); // NOI18N
        menuAttach.setName(bundle.getString("DocumentSearchPanel.menuAttach.name")); // NOI18N
        popUpSearchResults.add(menuAttach);

        menuEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEdit.setText(bundle.getString("DocumentSearchPanel.menuEdit.text")); // NOI18N
        menuEdit.setName(bundle.getString("DocumentSearchPanel.menuEdit.name")); // NOI18N
        menuEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditActionPerformed(evt);
            }
        });
        popUpSearchResults.add(menuEdit);

        menuPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        menuPrint.setText(bundle.getString("DocumentSearchPanel.menuPrint.text")); // NOI18N
        menuPrint.setName("menuPrint"); // NOI18N
        menuPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPrintActionPerformed(evt);
            }
        });
        popUpSearchResults.add(menuPrint);

        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblSearchResults.setComponentPopupMenu(popUpSearchResults);
        tblSearchResults.setName("tblSearchResults"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${sourceSearchResultsList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchResultsList, eLProperty, tblSearchResults);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${typeDisplayValue}"));
        columnBinding.setColumnName("Type Display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${laNr}"));
        columnBinding.setColumnName("La Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${recordation}"));
        columnBinding.setColumnName("Recordation");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${referenceNr}"));
        columnBinding.setColumnName("Reference Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${submission}"));
        columnBinding.setColumnName("Submission");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${statusCode}"));
        columnBinding.setColumnName("Status Code");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${archiveDocumentId}"));
        columnBinding.setColumnName("Archive Document Id");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchResultsList, org.jdesktop.beansbinding.ELProperty.create("${selectedSource}"), tblSearchResults, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblSearchResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSearchResultsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblSearchResults);
        tblSearchResults.getColumnModel().getColumn(0).setMinWidth(120);
        tblSearchResults.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblSearchResults.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("DocumentSeachPanel.tblSearchResults.columnModel.title5_1_1")); // NOI18N
        tblSearchResults.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("DocumentSeachPanel.tblSearchResults.columnModel.title0_1_1")); // NOI18N
        tblSearchResults.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("DocumentSearchPanel.tblSearchResults.columnModel.title2_1")); // NOI18N
        tblSearchResults.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("DocumentSeachPanel.tblSearchResults.columnModel.title1_1_1")); // NOI18N
        tblSearchResults.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("DocumentSeachPanel.tblSearchResults.columnModel.title3_1_1")); // NOI18N
        tblSearchResults.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("DocumentSeachPanel.tblSearchResults.columnModel.title4_1")); // NOI18N
        tblSearchResults.getColumnModel().getColumn(6).setPreferredWidth(30);
        tblSearchResults.getColumnModel().getColumn(6).setMaxWidth(30);
        tblSearchResults.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("DocumentSearchPanel.tblSearchResults.columnModel.title6_1")); // NOI18N
        tblSearchResults.getColumnModel().getColumn(6).setCellRenderer(new AttachedDocumentCellRenderer());

        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setLayout(new java.awt.GridLayout(2, 1));

        jPanel13.setName(bundle.getString("DocumentSearchPanel.jPanel13.name")); // NOI18N

        jLabel9.setText(bundle.getString("DocumentSearchPanel.jLabel9.text")); // NOI18N
        jLabel9.setName(bundle.getString("DocumentSearchPanel.jLabel9.name")); // NOI18N

        btnClear.setText(bundle.getString("DocumentSearchPanel.btnClear.text")); // NOI18N
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClear))
        );

        jPanel9.add(jPanel13);

        jPanel14.setName(bundle.getString("DocumentSearchPanel.jPanel14.name")); // NOI18N

        jLabel10.setText(bundle.getString("DocumentSearchPanel.jLabel10.text")); // NOI18N
        jLabel10.setName(bundle.getString("DocumentSearchPanel.jLabel10.name")); // NOI18N

        btnSearch.setText(bundle.getString("DocumentSearchPanel.btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch))
        );

        jPanel9.add(jPanel14);

        jPanel12.setName("jPanel12"); // NOI18N
        jPanel12.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridLayout(2, 1, 15, 0));

        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel6.setName("jPanel6"); // NOI18N

        txtRefNumber.setName("txtRefNumber"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParams, org.jdesktop.beansbinding.ELProperty.create("${refNumber}"), txtRefNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel8.setText(bundle.getString("DocumentSearchPanel.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtRefNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(7, 7, 7)
                .addComponent(txtRefNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.add(jPanel6);

        jPanel7.setName("jPanel7"); // NOI18N

        jLabel3.setText(bundle.getString("DocumentSearchPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtLaNr.setName("txtLaNr"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParams, org.jdesktop.beansbinding.ELProperty.create("${laNumber}"), txtLaNr, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(70, Short.MAX_VALUE))
            .addComponent(txtLaNr, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLaNr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel10.add(jPanel7);

        jPanel1.add(jPanel10);

        jPanel8.setName("jPanel8"); // NOI18N

        jLabel1.setText(bundle.getString("DocumentSearchPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        cbxSourceType.setName("cbxSourceType"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${sourceTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sourceTypesList, eLProperty, cbxSourceType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParams, org.jdesktop.beansbinding.ELProperty.create("${sourceType}"), cbxSourceType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(205, Short.MAX_VALUE))
            .addComponent(cbxSourceType, 0, 229, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxSourceType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel8);

        jPanel12.add(jPanel1);

        jPanel11.setName("jPanel11"); // NOI18N
        jPanel11.setLayout(new java.awt.GridLayout(2, 2, 15, 0));

        jPanel4.setName("jPanel4"); // NOI18N

        txtSubmissionDateFrom.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtSubmissionDateFrom.setText(bundle.getString("DocumentSearchPanel.txtSubmissionDateFrom.text")); // NOI18N
        txtSubmissionDateFrom.setName("txtSubmissionDateFrom"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParams, org.jdesktop.beansbinding.ELProperty.create("${fromSubmissionDate}"), txtSubmissionDateFrom, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnSubmissionDateFrom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnSubmissionDateFrom.setText(bundle.getString("DocumentSearchPanel.btnSubmissionDateFrom.text")); // NOI18N
        btnSubmissionDateFrom.setBorder(null);
        btnSubmissionDateFrom.setName("btnSubmissionDateFrom"); // NOI18N
        btnSubmissionDateFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmissionDateFromActionPerformed(evt);
            }
        });

        jLabel2.setText(bundle.getString("DocumentSearchPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(txtSubmissionDateFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSubmissionDateFrom))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnSubmissionDateFrom)
                    .addComponent(txtSubmissionDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel4);

        jPanel5.setName("jPanel5"); // NOI18N

        jLabel5.setText(bundle.getString("DocumentSearchPanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txtSubmissionDateTo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtSubmissionDateTo.setText(bundle.getString("DocumentSearchPanel.txtSubmissionDateTo.text")); // NOI18N
        txtSubmissionDateTo.setName("txtSubmissionDateTo"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParams, org.jdesktop.beansbinding.ELProperty.create("${toSubmissionDate}"), txtSubmissionDateTo, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnSubmissionDateTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnSubmissionDateTo.setText(bundle.getString("DocumentSearchPanel.btnSubmissionDateTo.text")); // NOI18N
        btnSubmissionDateTo.setBorder(null);
        btnSubmissionDateTo.setName("btnSubmissionDateTo"); // NOI18N
        btnSubmissionDateTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmissionDateToActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(txtSubmissionDateTo, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSubmissionDateTo))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnSubmissionDateTo)
                    .addComponent(txtSubmissionDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel5);

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel7.setText(bundle.getString("DocumentSearchPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtDateFrom.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtDateFrom.setText(bundle.getString("DocumentSearchPanel.txtDateFrom.text")); // NOI18N
        txtDateFrom.setName("txtDateFrom"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParams, org.jdesktop.beansbinding.ELProperty.create("${fromRecordationDate}"), txtDateFrom, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnDateFrom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnDateFrom.setText(bundle.getString("DocumentSearchPanel.btnDateFrom.text")); // NOI18N
        btnDateFrom.setBorder(null);
        btnDateFrom.setName("btnDateFrom"); // NOI18N
        btnDateFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDateFromActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(txtDateFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDateFrom))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnDateFrom)
                    .addComponent(txtDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel2);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel6.setText(bundle.getString("DocumentSearchPanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtDateTo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtDateTo.setText(bundle.getString("DocumentSearchPanel.txtDateTo.text")); // NOI18N
        txtDateTo.setName("txtDateTo"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParams, org.jdesktop.beansbinding.ELProperty.create("${toRecordationDate}"), txtDateTo, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnDateTo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnDateTo.setText(bundle.getString("DocumentSearchPanel.btnDateTo.text")); // NOI18N
        btnDateTo.setBorder(null);
        btnDateTo.setName("btnDateTo"); // NOI18N
        btnDateTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDateToActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(txtDateTo, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDateTo))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnDateTo)
                    .addComponent(txtDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel3);

        jPanel12.add(jPanel11);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpen.setText(bundle.getString("DocumentSearchPanel.btnOpen.text")); // NOI18N
        btnOpen.setFocusable(false);
        btnOpen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpen.setName("btnOpen"); // NOI18N
        btnOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        jToolBar1.add(btnOpen);

        btnAttach.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/attachment.png"))); // NOI18N
        btnAttach.setText(bundle.getString("DocumentSearchPanel.btnAttach.text")); // NOI18N
        btnAttach.setFocusable(false);
        btnAttach.setName(bundle.getString("DocumentSearchPanel.btnAttach.name")); // NOI18N
        btnAttach.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAttach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttachActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAttach);

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEdit.setText(bundle.getString("DocumentSearchPanel.btnEdit.text")); // NOI18N
        btnEdit.setFocusable(false);
        btnEdit.setName(bundle.getString("DocumentSearchPanel.btnEdit.name")); // NOI18N
        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrint.setText(bundle.getString("DocumentSearchPanel.btnPrint.text")); // NOI18N
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPrint.setName("btnPrint"); // NOI18N
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        separatorPrint.setName("separatorPrint"); // NOI18N
        jToolBar1.add(separatorPrint);

        jLabel4.setText(bundle.getString("DocumentSearchPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        jToolBar1.add(jLabel4);

        lblResults.setFont(LafManager.getInstance().getLabFontBold());
        lblResults.setText(bundle.getString("DocumentSearchPanel.lblResults.text")); // NOI18N
        lblResults.setName("lblResults"); // NOI18N
        jToolBar1.add(lblResults);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        searchDocuments();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnDateFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDateFromActionPerformed
        showCalendar(txtDateFrom);
    }//GEN-LAST:event_btnDateFromActionPerformed

    private void btnSubmissionDateFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmissionDateFromActionPerformed
        showCalendar(txtSubmissionDateFrom);
    }//GEN-LAST:event_btnSubmissionDateFromActionPerformed

    private void btnDateToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDateToActionPerformed
        showCalendar(txtDateTo);
    }//GEN-LAST:event_btnDateToActionPerformed

    private void btnSubmissionDateToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmissionDateToActionPerformed
        showCalendar(txtSubmissionDateTo);
    }//GEN-LAST:event_btnSubmissionDateToActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed

    private void tblSearchResultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSearchResultsMouseClicked
        if (evt.getClickCount() == 2) {
            openDocument();
        }
    }//GEN-LAST:event_tblSearchResultsMouseClicked

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        openDocument();
    }//GEN-LAST:event_btnOpenActionPerformed

    private void menuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenActionPerformed
        openDocument();
    }//GEN-LAST:event_menuOpenActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        print();
    }//GEN-LAST:event_btnPrintActionPerformed

    private void menuPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPrintActionPerformed
        print();
    }//GEN-LAST:event_menuPrintActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        fireEditSource();
    }//GEN-LAST:event_btnEditActionPerformed

    private void menuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditActionPerformed
        fireEditSource();
    }//GEN-LAST:event_menuEditActionPerformed

    private void btnAttachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttachActionPerformed
        fireAttach();
    }//GEN-LAST:event_btnAttachActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAttach;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDateFrom;
    private javax.swing.JButton btnDateTo;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSubmissionDateFrom;
    private javax.swing.JButton btnSubmissionDateTo;
    private javax.swing.JComboBox cbxSourceType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblResults;
    private javax.swing.JMenuItem menuAttach;
    private javax.swing.JMenuItem menuEdit;
    private javax.swing.JMenuItem menuOpen;
    private javax.swing.JMenuItem menuPrint;
    private javax.swing.JPopupMenu popUpSearchResults;
    private org.sola.clients.beans.source.SourceSearchParamsBean searchParams;
    private org.sola.clients.beans.source.SourceSearchResultsListBean searchResultsList;
    private javax.swing.JToolBar.Separator separatorPrint;
    private org.sola.clients.beans.referencedata.SourceTypeListBean sourceTypesList;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblSearchResults;
    private javax.swing.JFormattedTextField txtDateFrom;
    private javax.swing.JFormattedTextField txtDateTo;
    private javax.swing.JTextField txtLaNr;
    private javax.swing.JTextField txtRefNumber;
    private javax.swing.JFormattedTextField txtSubmissionDateFrom;
    private javax.swing.JFormattedTextField txtSubmissionDateTo;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
