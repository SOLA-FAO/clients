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
package org.sola.clients.swing.ui.party;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import org.sola.clients.beans.party.PartyBean;
import org.sola.clients.beans.party.PartySearchResultListBean;
import org.sola.clients.beans.referencedata.PartyRoleTypeListBean;
import org.sola.clients.beans.referencedata.PartyTypeListBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to search parties and manage them.
 */
public class PartySearchPanel extends JPanel {

    public static final String CREATE_NEW_PARTY_PROPERTY = "createNewParty";
    public static final String EDIT_PARTY_PROPERTY = "editParty";
    public static final String REMOVE_PARTY_PROPERTY = "removeParty";
    public static final String SELECT_PARTY_PROPERTY = "selectParty";
    public static final String VIEW_PARTY_PROPERTY = "viewParty";

    /**
     * Creates new form PartySearchPanel
     */
    public PartySearchPanel() {
        initComponents();

        partySearchResuls.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PartySearchResultListBean.SELECTED_PARTY_SEARCH_RESULT)) {
                    customizePartyButtons();
                }
            }
        });
        customizePartyButtons();
    }

    public boolean isShowViewButton() {
        return btnView.isVisible();
    }

    public void setShowViewButton(boolean isVisible) {
        btnView.setVisible(isVisible);
        menuView.setVisible(isVisible);
        separator1.setVisible(isVisible);
        if (!isVisible && btnSelect.isVisible()) {
            separator1.setVisible(true);
        }
    }

    public boolean isShowSelectButton() {
        return btnSelect.isVisible();
    }

    public void setShowSelectButton(boolean isVisible) {
        btnSelect.setVisible(isVisible);
        menuSelect.setVisible(isVisible);
        separator1.setVisible(isVisible);
        if (!isVisible && btnView.isVisible()) {
            separator1.setVisible(true);
        }
    }

    public boolean isShowAddButton() {
        return btnAddParty.isVisible();
    }

    public void setShowAddButton(boolean isVisible) {
        btnAddParty.setVisible(isVisible);
        menuAdd.setVisible(isVisible);
    }

    public boolean isShowEditButton() {
        return btnEditParty.isVisible();
    }

    public void setShowEditButton(boolean isVisible) {
        btnEditParty.setVisible(isVisible);
        menuEdit.setVisible(isVisible);
    }

    public boolean isShowRemoveButton() {
        return btnRemoveParty.isVisible();
    }

    public void setShowRemoveButton(boolean isVisible) {
        btnRemoveParty.setVisible(isVisible);
        menuRemove.setVisible(isVisible);
    }

    private PartyTypeListBean createPartyTypes() {
        if (partyTypes == null) {
            partyTypes = new PartyTypeListBean(true);
        }
        return partyTypes;
    }

    private PartyRoleTypeListBean createPartyRoleTypes() {
        if (partyRoleTyps == null) {
            partyRoleTyps = new PartyRoleTypeListBean(true);
        }
        return partyRoleTyps;
    }

    /**
     * Enables or disables Party management buttons, based on security rights.
     */
    private void customizePartyButtons() {
        boolean hasPartySaveRole = SecurityBean.isInRole(RolesConstants.PARTY_SAVE);
        boolean enabled = partySearchResuls.getSelectedPartySearchResult() != null;

        btnView.setEnabled(enabled);
        btnSelect.setEnabled(enabled);
        menuView.setEnabled(btnView.isEnabled());
        menuSelect.setEnabled(btnSelect.isEnabled());

        enabled = enabled && hasPartySaveRole;

        if (enabled && partySearchResuls.getSelectedPartySearchResult().isRightHolder()) {
            enabled = SecurityBean.isInRole(RolesConstants.PARTY_RIGHTHOLDERS_SAVE);
        }
        btnAddParty.setEnabled(hasPartySaveRole);
        btnEditParty.setEnabled(enabled);
        btnRemoveParty.setEnabled(enabled);

        menuAdd.setEnabled(btnAddParty.isEnabled());
        menuEdit.setEnabled(btnEditParty.isEnabled());
        menuRemove.setEnabled(btnRemoveParty.isEnabled());
    }

    /**
     * Searches parties with given criteria.
     */
    private void search() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_PERSON_SEARCHING));
                partySearchResuls.search(partySearchParams);
                return null;
            }

            @Override
            public void taskDone() {
                lblSearchResultNumber.setText(Integer.toString(partySearchResuls.getPartySearchResults().size()));
                if (partySearchResuls.getPartySearchResults().size() > 100) {
                    MessageUtility.displayMessage(ClientMessage.SEARCH_TOO_MANY_RESULTS, new String[]{"100"});
                } else if (partySearchResuls.getPartySearchResults().size() < 1) {
                    MessageUtility.displayMessage(ClientMessage.SEARCH_NO_RESULTS);
                }
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void firePartyEvent(String propertyName) {
        if (partySearchResuls.getSelectedPartySearchResult() != null) {
            firePropertyChange(propertyName, null,
                    PartyBean.getParty(partySearchResuls.getSelectedPartySearchResult().getId()));
        }
    }

    private void selectParty() {
        firePartyEvent(SELECT_PARTY_PROPERTY);
    }

    private void viewParty() {
        firePartyEvent(VIEW_PARTY_PROPERTY);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popupParties = new javax.swing.JPopupMenu();
        menuView = new javax.swing.JMenuItem();
        menuSelect = new javax.swing.JMenuItem();
        menuAdd = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenuItem();
        menuRemove = new javax.swing.JMenuItem();
        partyTypes = createPartyTypes();
        partyRoleTyps = createPartyRoleTypes();
        partySearchParams = new org.sola.clients.beans.party.PartySearchParamsBean();
        partySearchResuls = new org.sola.clients.beans.party.PartySearchResultListBean();
        scrlSearchPanel = new javax.swing.JScrollPane();
        pnlSearch = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableSearchResults = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnView = new javax.swing.JButton();
        btnSelect = new javax.swing.JButton();
        separator1 = new javax.swing.JToolBar.Separator();
        btnAddParty = new javax.swing.JButton();
        btnEditParty = new javax.swing.JButton();
        btnRemoveParty = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel5 = new javax.swing.JLabel();
        lblSearchResultNumber = new javax.swing.JLabel();
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
        jLabel4 = new javax.swing.JLabel();
        btnSearch = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        popupParties.setName("popupParties"); // NOI18N

        menuView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/party/Bundle"); // NOI18N
        menuView.setText(bundle.getString("PartySearchPanel.menuView.text")); // NOI18N
        menuView.setName("menuView"); // NOI18N
        menuView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewActionPerformed(evt);
            }
        });
        popupParties.add(menuView);

        menuSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/select.png"))); // NOI18N
        menuSelect.setText(bundle.getString("PartySearchPanel.menuSelect.text")); // NOI18N
        menuSelect.setName("menuSelect"); // NOI18N
        menuSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSelectActionPerformed(evt);
            }
        });
        popupParties.add(menuSelect);

        menuAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAdd.setText(bundle.getString("PartySearchPanel.menuAdd.text")); // NOI18N
        menuAdd.setName("menuAdd"); // NOI18N
        menuAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddActionPerformed(evt);
            }
        });
        popupParties.add(menuAdd);

        menuEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEdit.setText(bundle.getString("PartySearchPanel.menuEdit.text")); // NOI18N
        menuEdit.setName("menuEdit"); // NOI18N
        menuEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditActionPerformed(evt);
            }
        });
        popupParties.add(menuEdit);

        menuRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemove.setText(bundle.getString("PartySearchPanel.menuRemove.text")); // NOI18N
        menuRemove.setName("menuRemove"); // NOI18N
        menuRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveActionPerformed(evt);
            }
        });
        popupParties.add(menuRemove);

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

        tableSearchResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableSearchResultsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableSearchResults);
        tableSearchResults.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PartySearchPanel.tableSearchResults.columnModel.title0_1")); // NOI18N
        tableSearchResults.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PartySearchPanel.tableSearchResults.columnModel.title1_1")); // NOI18N
        tableSearchResults.getColumnModel().getColumn(2).setPreferredWidth(120);
        tableSearchResults.getColumnModel().getColumn(2).setMaxWidth(150);
        tableSearchResults.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PartySearchPanel.tableSearchResults.columnModel.title2_1")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnView.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnView.setText(bundle.getString("PartySearchPanel.btnView.text")); // NOI18N
        btnView.setFocusable(false);
        btnView.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnView.setName("btnView"); // NOI18N
        btnView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnView);

        btnSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/select.png"))); // NOI18N
        btnSelect.setText(bundle.getString("PartySearchPanel.btnSelect.text")); // NOI18N
        btnSelect.setFocusable(false);
        btnSelect.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSelect.setName("btnSelect"); // NOI18N
        btnSelect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSelect);

        separator1.setName("separator1"); // NOI18N
        jToolBar1.add(separator1);

        btnAddParty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddParty.setText(bundle.getString("PartySearchPanel.btnAddParty.text")); // NOI18N
        btnAddParty.setFocusable(false);
        btnAddParty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddParty.setName("btnAddParty"); // NOI18N
        btnAddParty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddParty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPartyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddParty);

        btnEditParty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditParty.setText(bundle.getString("PartySearchPanel.btnEditParty.text")); // NOI18N
        btnEditParty.setFocusable(false);
        btnEditParty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditParty.setName("btnEditParty"); // NOI18N
        btnEditParty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditParty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditPartyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEditParty);

        btnRemoveParty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveParty.setText(bundle.getString("PartySearchPanel.btnRemoveParty.text")); // NOI18N
        btnRemoveParty.setFocusable(false);
        btnRemoveParty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveParty.setName("btnRemoveParty"); // NOI18N
        btnRemoveParty.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveParty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemovePartyActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveParty);

        jSeparator1.setName(bundle.getString("PartySearchPanel.jSeparator1.name")); // NOI18N
        jToolBar1.add(jSeparator1);

        jLabel5.setText(bundle.getString("PartySearchPanel.jLabel5.text")); // NOI18N
        jLabel5.setName(bundle.getString("PartySearchPanel.jLabel5.name")); // NOI18N
        jToolBar1.add(jLabel5);

        lblSearchResultNumber.setFont(LafManager.getInstance().getLabFontBold());
        lblSearchResultNumber.setText(bundle.getString("PartySearchPanel.lblSearchResultNumber.text")); // NOI18N
        lblSearchResultNumber.setName(bundle.getString("PartySearchPanel.lblSearchResultNumber.name")); // NOI18N
        jToolBar1.add(lblSearchResultNumber);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
        );

        jPanel8.setName("jPanel8"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridLayout(1, 4, 10, 0));

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText(bundle.getString("PartySearchPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtName.setName("txtName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySearchParams, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(130, Short.MAX_VALUE))
            .addComponent(txtName)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel2.setText(bundle.getString("PartySearchPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

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
                .addContainerGap(133, Short.MAX_VALUE))
            .addComponent(cbxPartyTypes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxPartyTypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel2);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel3.setText(bundle.getString("PartySearchPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

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
                .addContainerGap(136, Short.MAX_VALUE))
            .addComponent(cbxRoles, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRoles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel3);

        jPanel5.setName("jPanel5"); // NOI18N

        jLabel4.setText(bundle.getString("PartySearchPanel.jLabel4.text")); // NOI18N
        jLabel4.setName(bundle.getString("PartySearchPanel.jLabel4.name")); // NOI18N

        btnSearch.setText(bundle.getString("PartySearchPanel.btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnClear.setText(bundle.getString("PartySearchPanel.btnClear.text")); // NOI18N
        btnClear.setName(bundle.getString("PartySearchPanel.btnClear.name")); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(btnClear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(btnClear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btnClear, btnSearch});

        jPanel4.add(jPanel5);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlSearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrlSearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        search();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        viewParty();
    }//GEN-LAST:event_btnViewActionPerformed

    private void btnSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        selectParty();
    }//GEN-LAST:event_btnSelectActionPerformed

    private void menuViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewActionPerformed
        viewParty();
    }//GEN-LAST:event_menuViewActionPerformed

    private void menuSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSelectActionPerformed
        selectParty();
    }//GEN-LAST:event_menuSelectActionPerformed

    private void btnAddPartyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPartyActionPerformed
        addParty();
    }//GEN-LAST:event_btnAddPartyActionPerformed

    private void menuAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddActionPerformed
        addParty();
    }//GEN-LAST:event_menuAddActionPerformed

    private void btnEditPartyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditPartyActionPerformed
        editParty();
    }//GEN-LAST:event_btnEditPartyActionPerformed

    private void menuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditActionPerformed
        editParty();
    }//GEN-LAST:event_menuEditActionPerformed

    private void btnRemovePartyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemovePartyActionPerformed
        removeParty();
    }//GEN-LAST:event_btnRemovePartyActionPerformed

    private void menuRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveActionPerformed
        removeParty();
    }//GEN-LAST:event_menuRemoveActionPerformed

    private void tableSearchResultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableSearchResultsMouseClicked
        if(evt.getClickCount() > 1 && evt.getButton() == MouseEvent.BUTTON1){
            viewParty();
        }
    }//GEN-LAST:event_tableSearchResultsMouseClicked

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearForm();
    }//GEN-LAST:event_btnClearActionPerformed
    
    private void clearForm() {
        cbxPartyTypes.setSelectedIndex(-1);
        cbxRoles.setSelectedIndex(-1);
        txtName.setText(null);
    }
    
    
    private void addParty() {
        firePropertyChange(CREATE_NEW_PARTY_PROPERTY, false, true);
    }

    private void editParty() {
        firePartyEvent(EDIT_PARTY_PROPERTY);
    }

    private void removeParty() {
        if (partySearchResuls.getSelectedPartySearchResult() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            firePropertyChange(REMOVE_PARTY_PROPERTY, false, true);
            PartyBean.remove(partySearchResuls.getSelectedPartySearchResult().getId());
            search();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddParty;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnEditParty;
    private javax.swing.JButton btnRemoveParty;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSelect;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox cbxPartyTypes;
    private javax.swing.JComboBox cbxRoles;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblSearchResultNumber;
    private javax.swing.JMenuItem menuAdd;
    private javax.swing.JMenuItem menuEdit;
    private javax.swing.JMenuItem menuRemove;
    private javax.swing.JMenuItem menuSelect;
    private javax.swing.JMenuItem menuView;
    private org.sola.clients.beans.referencedata.PartyRoleTypeListBean partyRoleTyps;
    private org.sola.clients.beans.party.PartySearchParamsBean partySearchParams;
    private org.sola.clients.beans.party.PartySearchResultListBean partySearchResuls;
    private org.sola.clients.beans.referencedata.PartyTypeListBean partyTypes;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPopupMenu popupParties;
    private javax.swing.JScrollPane scrlSearchPanel;
    private javax.swing.JToolBar.Separator separator1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableSearchResults;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
