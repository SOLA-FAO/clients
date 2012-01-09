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
package org.sola.clients.swing.ui.party;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.jdesktop.application.Action;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.party.PartySearchResultListBean;
import org.sola.clients.beans.referencedata.PartyRoleTypeListBean;
import org.sola.clients.beans.referencedata.PartyTypeListBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to search parties and manage them.
 */
public class PartySearchPanel extends javax.swing.JPanel {
    
    /** {@link PartyPanel} listener to capture creation save and cancel events. */
    private class PartyPanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PartyPanel.CREATED_PARTY)) {
                MessageUtility.displayMessage(ClientMessage.PARTY_CREATED);
            } else if (evt.getPropertyName().equals(PartyPanel.UPDATED_PARTY)) {
                MessageUtility.displayMessage(ClientMessage.PARTY_SAVED);
                showPartySearch();
                search();
            }
            if (evt.getPropertyName().equals(PartyPanel.CANCEL_ACTION)) {
                showPartySearch();
                search();
            }
        }
    }
    
    private ResourceBundle resourceBundle;
    private String headerTitle;
    private final String CARD_SEARCH = "cardSearchPanel";
    private final String CARD_PARTY = "cardPartyPanel";
    
    /** Creates new form PartySearchPanel */
    public PartySearchPanel() {
        resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/party/Bundle");
        initComponents();
        
        headerTitle = pnlHeader.getTitleText();
        PartyPanelListener listener = new PartyPanelListener();
        pnlParty.addPropertyChangeListener(listener);
        
        partySearchResuls.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(PartySearchResultListBean.SELECTED_PARTY_SEARCH_RESULT)){
                    customizePartyButtons();
                }
            }
        });
        showPartySearch();
        customizePartyButtons();
        customizeComponents();
    }
    
    
     /** Applies customization of component L&F. */
    private void customizeComponents() {
 
//    BUTTONS   
    LafManager.getInstance().setBtnProperties(btnAddParty);
    LafManager.getInstance().setBtnProperties(btnEditParty);
    LafManager.getInstance().setBtnProperties(btnRemoveParty);
    LafManager.getInstance().setBtnProperties(btnSearch);
//    COMBOBOXES
    LafManager.getInstance().setCmbProperties(cbxPartyTypes);
    LafManager.getInstance().setCmbProperties(cbxRoles);
    
    
    
//    LABELS    
    LafManager.getInstance().setLabProperties(jLabel1);
    LafManager.getInstance().setLabProperties(jLabel2);
    LafManager.getInstance().setLabProperties(jLabel3);
   
   
//    TXT FIELDS
    LafManager.getInstance().setTxtProperties(txtName);
    }

    
    private PartyTypeListBean createPartyTypes(){
        if(partyTypes == null){
            partyTypes = new PartyTypeListBean(true);
        }
        return partyTypes;
    }
    
    private PartyRoleTypeListBean createPartyRoleTypes(){
        if(partyRoleTyps == null){
            partyRoleTyps = new PartyRoleTypeListBean(true);
        }
        return partyRoleTyps;
    }
    
    /** Enables or disables Party management buttons, based on security rights. */
    private void customizePartyButtons(){
        boolean hasPartySaveRole = SecurityBean.isInRole(RolesConstants.PARTY_SAVE);
        boolean enabled = partySearchResuls.getSelectedPartySearchResult() != null && hasPartySaveRole;
        
        if(enabled && partySearchResuls.getSelectedPartySearchResult().isRightHolder()){
            enabled = SecurityBean.isInRole(RolesConstants.PARTY_RIGHTHOLDERS_SAVE);
        }
        btnAddParty.getAction().setEnabled(hasPartySaveRole);
        btnEditParty.getAction().setEnabled(enabled);
        btnRemoveParty.getAction().setEnabled(enabled);
    }
  
    /** Shows search panel. */
    private void showPartySearch(){
        ((CardLayout)pnlContent.getLayout()).show(pnlContent, CARD_SEARCH);
        pnlHeader.setTitleText(headerTitle);
        customizeComponents();
    }
    
    /** Shows party panel. */
    private void showPartyPanel(PartyBean party){
        ((CardLayout)pnlContent.getLayout()).show(pnlContent, CARD_PARTY);
        pnlParty.setPartyBean(party);
        
        if (party != null) {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    party.getFullName()));
        } else {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    resourceBundle.getString("PartySearchPanel.NewParty")));
        }
        customizeComponents();
    }
    
    /** Searches parties with given criteria. */
    private void search(){
        partySearchResuls.search(partySearchParams);
        if(partySearchResuls.getPartySearchResults().size()>100){
            MessageUtility.displayMessage(ClientMessage.SEARCH_TOO_MANY_RESULTS, new String[]{"100"});
        }else if(partySearchResuls.getPartySearchResults().size()<1){
            MessageUtility.displayMessage(ClientMessage.SEARCH_NO_RESULTS);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popupParties = new javax.swing.JPopupMenu();
        menuAdd = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenuItem();
        menuRemove = new javax.swing.JMenuItem();
        partyTypes = createPartyTypes();
        partyRoleTyps = createPartyRoleTypes();
        partySearchParams = new org.sola.clients.beans.party.PartySearchParamsBean();
        partySearchResuls = new org.sola.clients.beans.party.PartySearchResultListBean();
        pnlContent = new javax.swing.JPanel();
        scrlPartyPanel = new javax.swing.JScrollPane();
        pnlParty = new org.sola.clients.swing.ui.party.PartyPanel();
        scrlSearchPanel = new javax.swing.JScrollPane();
        pnlSearch = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableSearchResults = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddParty = new javax.swing.JButton();
        btnEditParty = new javax.swing.JButton();
        btnRemoveParty = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbxPartyTypes = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cbxRoles = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        btnSearch = new javax.swing.JButton();
        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();

        popupParties.setName("popupParties"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(PartySearchPanel.class, this);
        menuAdd.setAction(actionMap.get("addParty")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/party/Bundle"); // NOI18N
        menuAdd.setText(bundle.getString("PartySearchPanel.menuAdd.text")); // NOI18N
        menuAdd.setName("menuAdd"); // NOI18N
        popupParties.add(menuAdd);

        menuEdit.setAction(actionMap.get("editParty")); // NOI18N
        menuEdit.setText(bundle.getString("PartySearchPanel.menuEdit.text")); // NOI18N
        menuEdit.setName("menuEdit"); // NOI18N
        popupParties.add(menuEdit);

        menuRemove.setAction(actionMap.get("removeParty")); // NOI18N
        menuRemove.setText(bundle.getString("PartySearchPanel.menuRemove.text")); // NOI18N
        menuRemove.setName("menuRemove"); // NOI18N
        popupParties.add(menuRemove);

        pnlContent.setName("pnlContent"); // NOI18N
        pnlContent.setLayout(new java.awt.CardLayout());

        scrlPartyPanel.setBorder(null);
        scrlPartyPanel.setName("scrlPartyPanel"); // NOI18N
        scrlPartyPanel.setPreferredSize(new java.awt.Dimension(300, 425));

        pnlParty.setCloseOnSave(true);
        pnlParty.setName("pnlParty"); // NOI18N
        pnlParty.setPreferredSize(new java.awt.Dimension(400, 440));
        pnlParty.setSavePartyOnAction(true);
        scrlPartyPanel.setViewportView(pnlParty);

        pnlContent.add(scrlPartyPanel, "cardPartyPanel");

        scrlSearchPanel.setBorder(null);
        scrlSearchPanel.setName("scrlSearchPanel"); // NOI18N

        pnlSearch.setMinimumSize(new java.awt.Dimension(300, 300));
        pnlSearch.setName("pnlSearch"); // NOI18N
        pnlSearch.setPreferredSize(new java.awt.Dimension(300, 300));

        jPanel7.setName("jPanel7"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableSearchResults.setComponentPopupMenu(popupParties);
        tableSearchResults.setName("tableSearchResults"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${partySearchResults}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchResuls, eLProperty, tableSearchResults);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${fullName}"));
        columnBinding.setColumnName("Full Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${type.displayValue}"));
        columnBinding.setColumnName("Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rightHolder}"));
        columnBinding.setColumnName("Right Holder");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchResuls, org.jdesktop.beansbinding.ELProperty.create("${selectedPartySearchResult}"), tableSearchResults, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableSearchResults);
        tableSearchResults.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PartySearchPanel.tableSearchResults.columnModel.title0_1")); // NOI18N
        tableSearchResults.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PartySearchPanel.tableSearchResults.columnModel.title1_1")); // NOI18N
        tableSearchResults.getColumnModel().getColumn(2).setPreferredWidth(120);
        tableSearchResults.getColumnModel().getColumn(2).setMaxWidth(150);
        tableSearchResults.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PartySearchPanel.tableSearchResults.columnModel.title2_1")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddParty.setAction(actionMap.get("addParty")); // NOI18N
        btnAddParty.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnAddParty.setText(bundle.getString("PartySearchPanel.btnAddParty.text")); // NOI18N
        btnAddParty.setFocusable(false);
        btnAddParty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddParty.setName("btnAddParty"); // NOI18N
        btnAddParty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnAddParty);

        btnEditParty.setAction(actionMap.get("editParty")); // NOI18N
        btnEditParty.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnEditParty.setText(bundle.getString("PartySearchPanel.btnEditParty.text")); // NOI18N
        btnEditParty.setFocusable(false);
        btnEditParty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditParty.setName("btnEditParty"); // NOI18N
        btnEditParty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnEditParty);

        btnRemoveParty.setAction(actionMap.get("removeParty")); // NOI18N
        btnRemoveParty.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnRemoveParty.setText(bundle.getString("PartySearchPanel.btnRemoveParty.text")); // NOI18N
        btnRemoveParty.setFocusable(false);
        btnRemoveParty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveParty.setName("btnRemoveParty"); // NOI18N
        btnRemoveParty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnRemoveParty);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
        );

        jPanel8.setName("jPanel8"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridLayout(1, 3, 15, 0));

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText(bundle.getString("PartySearchPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtName.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtName.setName("txtName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchParams, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(111, Short.MAX_VALUE))
            .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setText(bundle.getString("PartySearchPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        cbxPartyTypes.setFont(new java.awt.Font("Tahoma", 0, 12));
        cbxPartyTypes.setName("cbxPartyTypes"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${partyTypes}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyTypes, eLProperty, cbxPartyTypes);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchParams, org.jdesktop.beansbinding.ELProperty.create("${partyType}"), cbxPartyTypes, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap(114, Short.MAX_VALUE))
            .addComponent(cbxPartyTypes, 0, 146, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxPartyTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel2);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setText(bundle.getString("PartySearchPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        cbxRoles.setFont(new java.awt.Font("Tahoma", 0, 12));
        cbxRoles.setName("cbxRoles"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${partyRoleTypeList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partyRoleTyps, eLProperty, cbxRoles);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchParams, org.jdesktop.beansbinding.ELProperty.create("${roleType}"), cbxRoles, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(119, Short.MAX_VALUE))
            .addComponent(cbxRoles, 0, 146, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRoles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel3);

        jPanel5.setName("jPanel5"); // NOI18N

        btnSearch.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnSearch.setText(bundle.getString("PartySearchPanel.btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(btnSearch)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlSearchLayout = new javax.swing.GroupLayout(pnlSearch);
        pnlSearch.setLayout(pnlSearchLayout);
        pnlSearchLayout.setHorizontalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlSearchLayout.setVerticalGroup(
            pnlSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSearchLayout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        scrlSearchPanel.setViewportView(pnlSearch);

        pnlContent.add(scrlSearchPanel, "cardSearchPanel");

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("PartySearchPanel.pnlHeader.titleText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    @Action
    public void addParty() {
        showPartyPanel(null);
    }

    @Action
    public void editParty() {
        if(partySearchResuls.getSelectedPartySearchResult()!=null){
            showPartyPanel(PartyBean.getParty(partySearchResuls.getSelectedPartySearchResult().getId()));
        }
    }

    @Action
    public void removeParty() {
        if(partySearchResuls.getSelectedPartySearchResult()!=null && 
                MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE){
            PartyBean.remove(partySearchResuls.getSelectedPartySearchResult().getId());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddParty;
    private javax.swing.JButton btnEditParty;
    private javax.swing.JButton btnRemoveParty;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cbxPartyTypes;
    private javax.swing.JComboBox cbxRoles;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuAdd;
    private javax.swing.JMenuItem menuEdit;
    private javax.swing.JMenuItem menuRemove;
    private org.sola.clients.beans.referencedata.PartyRoleTypeListBean partyRoleTyps;
    private org.sola.clients.beans.party.PartySearchParamsBean partySearchParams;
    private org.sola.clients.beans.party.PartySearchResultListBean partySearchResuls;
    private org.sola.clients.beans.referencedata.PartyTypeListBean partyTypes;
    private javax.swing.JPanel pnlContent;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private org.sola.clients.swing.ui.party.PartyPanel pnlParty;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPopupMenu popupParties;
    private javax.swing.JScrollPane scrlPartyPanel;
    private javax.swing.JScrollPane scrlSearchPanel;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableSearchResults;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
