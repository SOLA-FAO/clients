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
package org.sola.clients.swing.admin.referencedata;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.*;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.AbstractCodeTO;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.referencedata.*;

/**
 * Allows to manage different reference data tables.
 */
public class ReferenceDataManagementPanel extends ContentPanel {

    public static final String SELECTED_REF_DATA = "selectedRefData";
    private Class<? extends AbstractCodeBean> refDataClass;
    private Class<? extends AbstractCodeTO> refDataTOClass;
    private ObservableList<? extends AbstractCodeBean> refDataList;
    private AbstractCodeBean selectedRefData;

    /** Default panel constructor. */
    public ReferenceDataManagementPanel() {
        initComponents();
    }

    /** 
     * Creates new instance of panel with predefined parameters.
     * @param refDataClass Type of reference data to load.
     * @param headerTitle Reference data name to put on the title of the panel.
     */
    public <T extends AbstractCodeBean> ReferenceDataManagementPanel(
            Class<T> refDataClass, String headerTitle) {
        this.refDataClass = refDataClass;
        initRefDataList();

        initComponents();

        headerPanel.setTitleText(headerTitle);
        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(SELECTED_REF_DATA)) {
                    customizeRefDataButtons((AbstractCodeBean) evt.getNewValue());
                }
            }
        });

        customizeRefDataButtons(null);
    }

    /** Returns selected reference data object. */
    public AbstractCodeBean getSelectedRefData() {
        return selectedRefData;
    }

    /** Sets selected reference data object. */
    public void setSelectedRefData(AbstractCodeBean selectedRefData) {
        AbstractCodeBean oldValue = this.selectedRefData;
        this.selectedRefData = selectedRefData;
        firePropertyChange(SELECTED_REF_DATA, oldValue, this.selectedRefData);
    }

    /** Returns title header for the panel. */
    public String getHeaderTitle() {
        return headerPanel.getTitleText();
    }

    /** Sets title header for the panel. */
    public void setHeaderTitle(String headerTitle) {
        headerPanel.setTitleText(headerTitle);
    }

    /** Returns list of reference data objects, bound on the form. */
    public List<? extends AbstractCodeBean> getRefDataList() {
        return refDataList;
    }

    // Methods 
    /** Enables/disables reference data buttons. */
    private void customizeRefDataButtons(AbstractCodeBean refDataBean) {
        btnEditRefData.setEnabled(refDataBean != null);
        btnRemoveRefData.setEnabled(refDataBean != null);
        menuEditRefData.setEnabled(btnAddRefData.isEnabled());
        menuRemoveRefData.setEnabled(btnRemoveRefData.isEnabled());
    }

    /** Shows the panel with selected reference data object. */
    private void showRefData(final AbstractCodeBean refDataBean) {
        if (refDataClass == RequestTypeBean.class) {
            RequestTypePanelForm panel = new RequestTypePanelForm((RequestTypeBean) refDataBean, 
                    true, refDataBean != null);
            panel.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ReferenceDataPanelForm.REFDATA_SAVED_PROPERTY)) {
                        if (refDataBean == null) {
                            ((RequestTypePanelForm) evt.getSource()).setRequestTypeBean(null);
                        }
                        initRefDataList();
                    }
                }
            });
            getMainContentPanel().addPanel(panel, MainContentPanel.CARD_ADMIN_REFDATA_REQUEST_TYPE, true);
            
        } else if (refDataClass == RrrTypeBean.class) {
            RrrTypePanelForm panel = new RrrTypePanelForm((RrrTypeBean) refDataBean, true, refDataBean != null);
            panel.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ReferenceDataPanelForm.REFDATA_SAVED_PROPERTY)) {
                        if (refDataBean == null) {
                            ((RrrTypePanelForm) evt.getSource()).setRrrTypeBean(null);
                        }
                        initRefDataList();
                    }
                }
            });
            getMainContentPanel().addPanel(panel, MainContentPanel.CARD_ADMIN_REFDATA, true);
            
        } else {
            ReferenceDataPanelForm panel = new ReferenceDataPanelForm(refDataClass, 
                    refDataTOClass, refDataBean, headerPanel.getTitleText(), true, refDataBean != null);
            panel.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ReferenceDataPanelForm.REFDATA_SAVED_PROPERTY)) {
                        if (refDataBean == null) {
                            ((ReferenceDataPanelForm) evt.getSource()).setRefDataBean(refDataClass, refDataTOClass, null);
                        }
                        initRefDataList();
                    }
                }
            });
            getMainContentPanel().addPanel(panel, MainContentPanel.CARD_ADMIN_REFDATA, true);
        }
    }
   
    /** Loads reference data list, related to provided reference data type.*/
    public final void initRefDataList() {
        createRefDataList();
        // PARTY
        if (refDataClass == CommunicationTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getCommunicationTypes(null),
                    CommunicationTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.COMMUNICATION_TYPES_KEY);
            refDataTOClass = CommunicationTypeTO.class;
        } else if (refDataClass == IdTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getIdTypes(null),
                    IdTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.ID_TYPE_CODES_KEY);
            refDataTOClass = IdTypeTO.class;
        } else if (refDataClass == GenderTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getGenderTypes(null),
                    GenderTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.GENDER_TYPES_KEY);
            refDataTOClass = GenderTypeTO.class;
        } else if (refDataClass == PartyTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getPartyTypes(null),
                    PartyTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.PARTY_TYPE_CODES_KEY);
            refDataTOClass = PartyTypeTO.class;
        } else if (refDataClass == PartyRoleTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getPartyRoles(null),
                    PartyRoleTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.PARTY_ROLE_TYPE_CODES_KEY);
            refDataTOClass = PartyRoleTypeTO.class;
        } // ADMINISTRATIVE
        else if (refDataClass == BaUnitTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getBaUnitTypes(null),
                    BaUnitTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.BA_UNIT_TYPE_CODES_KEY);
            refDataTOClass = BaUnitTypeTO.class;
        } else if (refDataClass == MortgageTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getMortgageTypes(null),
                    MortgageTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.MORTGAGE_TYPE_CODES_KEY);
            refDataTOClass = MortgageTypeTO.class;
        } else if (refDataClass == RrrGroupTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getRrrGroupTypes(null),
                    RrrGroupTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.RRR_GROUP_TYPE_CODES_KEY);
            refDataTOClass = RrrGroupTypeTO.class;
        } else if (refDataClass == RrrTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getRrrTypes(null),
                    RrrTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.RRR_TYPE_CODES_KEY);
            refDataTOClass = RrrTypeTO.class;
        } else if (refDataClass == SourceBaUnitRelationTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getSourceBaUnitRelationTypes(null),
                    SourceBaUnitRelationTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.SOURCE_BA_UNIT_RELATION_TYPE_CODES_KEY);
            refDataTOClass = SourceBaUnitRelationTypeTO.class;
        } else if (refDataClass == BaUnitRelTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getBaUnitRelTypes(null),
                    BaUnitRelTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.BA_UNIT_REL_TYPE_KEY);
            refDataTOClass = BaUnitRelTypeTO.class;
        } // SOURCE
        else if (refDataClass == SourceTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getSourceTypes(null),
                    SourceTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.SOURCE_TYPES_KEY);
            refDataTOClass = SourceTypeTO.class;
        } // APPLICATION
        else if (refDataClass == TypeActionBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getTypeActions(null),
                    TypeActionBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.TYPE_ACTIONS_KEY);
            refDataTOClass = TypeActionTO.class;
        } else if (refDataClass == ServiceStatusTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getServiceStatusTypes(null),
                    ServiceStatusTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.SERVICE_STATUS_TYPE_CODES_KEY);
            refDataTOClass = ServiceStatusTypeTO.class;
        } else if (refDataClass == ServiceActionTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getServiceActionTypes(null),
                    ServiceActionTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.SERVICE_ACTION_TYPE_CODES_KEY);
            refDataTOClass = ServiceActionTypeTO.class;
        } else if (refDataClass == RequestTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getRequestTypes(null),
                    RequestTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.REQUEST_TYPES_KEY);
            refDataTOClass = RequestTypeTO.class;
        } else if (refDataClass == RequestCategoryTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getRequestCategoryTypes(null),
                    RequestCategoryTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.REQUEST_CATEGORY_TYPE_KEY);
            refDataTOClass = RequestCategoryTypeTO.class;
        } // SYSTEM
        else if (refDataClass == BrSeverityTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getBrSeverityTypes(null),
                    BrSeverityTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.BR_SEVERITY_TYPE_KEY);
            refDataTOClass = BrSeverityTypeTO.class;
        } else if (refDataClass == BrValidationTargetTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getBrValidationTargetTypes(null),
                    BrValidationTargetTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.BR_VALIDATION_TARGET_TYPE_KEY);
            refDataTOClass = BrValidationTargetTypeTO.class;
        } else if (refDataClass == BrTechnicalTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getBrTechnicalTypes(null),
                    BrTechnicalTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.BR_TECHNICAL_TYPE_KEY);
            refDataTOClass = BrTechnicalTypeTO.class;
        } // TRANSACTION
        else if (refDataClass == RegistrationStatusTypeBean.class) {
            TypeConverters.TransferObjectListToBeanList(WSManager.getInstance().getReferenceDataService().getRegistrationStatusTypes(null),
                    RegistrationStatusTypeBean.class, (List) refDataList);
            CacheManager.remove(CacheManager.REGISTRATION_STATUS_TYPE_CODES_KEY);
            refDataTOClass = RegistrationStatusTypeTO.class;
        }
    }

    /** 
     * Creates new instance of the reference data list if it is null. *
     * @param refDataBeanClass Class type used to create list.
     */
    private <T extends AbstractCodeBean> void createRefDataList() {
        if (refDataList == null) {
            refDataList = ObservableCollections.observableList(new ArrayList<T>());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        popupRefData = new javax.swing.JPopupMenu();
        menuAddRefData = new javax.swing.JMenuItem();
        menuEditRefData = new javax.swing.JMenuItem();
        menuRemoveRefData = new javax.swing.JMenuItem();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        pnlRefDataList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableRefData = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        toolbarRefData = new javax.swing.JToolBar();
        btnAddRefData = new javax.swing.JButton();
        btnEditRefData = new javax.swing.JButton();
        btnRemoveRefData = new javax.swing.JButton();

        popupRefData.setName("popupRefData"); // NOI18N

        menuAddRefData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/referencedata/Bundle"); // NOI18N
        menuAddRefData.setText(bundle.getString("ReferenceDataManagementPanel.menuAddRefData.text")); // NOI18N
        menuAddRefData.setName("menuAddRefData"); // NOI18N
        menuAddRefData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddRefDataActionPerformed(evt);
            }
        });
        popupRefData.add(menuAddRefData);

        menuEditRefData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditRefData.setText(bundle.getString("ReferenceDataManagementPanel.menuEditRefData.text")); // NOI18N
        menuEditRefData.setName("menuEditRefData"); // NOI18N
        menuEditRefData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditRefDataActionPerformed(evt);
            }
        });
        popupRefData.add(menuEditRefData);

        menuRemoveRefData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveRefData.setText(bundle.getString("ReferenceDataManagementPanel.menuRemoveRefData.text")); // NOI18N
        menuRemoveRefData.setName("menuRemoveRefData"); // NOI18N
        menuRemoveRefData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveRefDataActionPerformed(evt);
            }
        });
        popupRefData.add(menuRemoveRefData);

        setHeaderPanel(headerPanel);
        setMinimumSize(new java.awt.Dimension(200, 200));
        setPreferredSize(new java.awt.Dimension(400, 250));

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("ReferenceDataManagementPanel.headerPanel.titleText")); // NOI18N

        pnlRefDataList.setName("pnlRefDataList"); // NOI18N
        pnlRefDataList.setPreferredSize(new java.awt.Dimension(300, 200));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableRefData.setComponentPopupMenu(popupRefData);
        tableRefData.setName("tableRefData"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${refDataList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableRefData);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${code}"));
        columnBinding.setColumnName("Code");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${translatedDisplayValue}"));
        columnBinding.setColumnName("Translated Display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${translatedDescription}"));
        columnBinding.setColumnName("Translated Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status}"));
        columnBinding.setColumnName("Status");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedRefData}"), tableRefData, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableRefData);
        tableRefData.getColumnModel().getColumn(0).setPreferredWidth(120);
        tableRefData.getColumnModel().getColumn(0).setMaxWidth(200);
        tableRefData.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ReferenceDataManagementPanel.tableRefData.columnModel.title0_1")); // NOI18N
        tableRefData.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableRefData.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ReferenceDataManagementPanel.tableRefData.columnModel.title1_1")); // NOI18N
        tableRefData.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ReferenceDataManagementPanel.tableRefData.columnModel.title2_1")); // NOI18N
        tableRefData.getColumnModel().getColumn(2).setCellRenderer(new TableCellTextAreaRenderer());
        tableRefData.getColumnModel().getColumn(3).setPreferredWidth(60);
        tableRefData.getColumnModel().getColumn(3).setMaxWidth(60);
        tableRefData.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ReferenceDataManagementPanel.tableRefData.columnModel.title3_1")); // NOI18N

        toolbarRefData.setFloatable(false);
        toolbarRefData.setRollover(true);
        toolbarRefData.setName("toolbarRefData"); // NOI18N

        btnAddRefData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddRefData.setText(bundle.getString("ReferenceDataManagementPanel.btnAddRefData.text")); // NOI18N
        btnAddRefData.setFocusable(false);
        btnAddRefData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddRefData.setName("btnAddRefData"); // NOI18N
        btnAddRefData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddRefData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddRefDataActionPerformed(evt);
            }
        });
        toolbarRefData.add(btnAddRefData);

        btnEditRefData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditRefData.setText(bundle.getString("ReferenceDataManagementPanel.btnEditRefData.text")); // NOI18N
        btnEditRefData.setFocusable(false);
        btnEditRefData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditRefData.setName("btnEditRefData"); // NOI18N
        btnEditRefData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditRefData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditRefDataActionPerformed(evt);
            }
        });
        toolbarRefData.add(btnEditRefData);

        btnRemoveRefData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveRefData.setText(bundle.getString("ReferenceDataManagementPanel.btnRemoveRefData.text")); // NOI18N
        btnRemoveRefData.setFocusable(false);
        btnRemoveRefData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveRefData.setName("btnRemoveRefData"); // NOI18N
        btnRemoveRefData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveRefData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveRefDataActionPerformed(evt);
            }
        });
        toolbarRefData.add(btnRemoveRefData);

        javax.swing.GroupLayout pnlRefDataListLayout = new javax.swing.GroupLayout(pnlRefDataList);
        pnlRefDataList.setLayout(pnlRefDataListLayout);
        pnlRefDataListLayout.setHorizontalGroup(
            pnlRefDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbarRefData, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
        );
        pnlRefDataListLayout.setVerticalGroup(
            pnlRefDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRefDataListLayout.createSequentialGroup()
                .addComponent(toolbarRefData, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pnlRefDataList, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlRefDataList, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddRefDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddRefDataActionPerformed
        addRefData();
    }//GEN-LAST:event_btnAddRefDataActionPerformed

    private void btnEditRefDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditRefDataActionPerformed
        editRefData();
    }//GEN-LAST:event_btnEditRefDataActionPerformed

    private void btnRemoveRefDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveRefDataActionPerformed
        removeRefData();
    }//GEN-LAST:event_btnRemoveRefDataActionPerformed

    private void menuAddRefDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddRefDataActionPerformed
        addRefData();
    }//GEN-LAST:event_menuAddRefDataActionPerformed

    private void menuEditRefDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditRefDataActionPerformed
        editRefData();
    }//GEN-LAST:event_menuEditRefDataActionPerformed

    private void menuRemoveRefDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveRefDataActionPerformed
        removeRefData();
    }//GEN-LAST:event_menuRemoveRefDataActionPerformed

    private void addRefData() {
        showRefData(null);
    }

    private void editRefData() {
        showRefData(selectedRefData);
    }

    private void removeRefData() {
        if (selectedRefData != null && MessageUtility.displayMessage(
                ClientMessage.ADMIN_CONFIRM_DELETE_REFDATA,
                new String[]{selectedRefData.getTranslatedDisplayValue()}) == MessageUtility.BUTTON_ONE) {
            selectedRefData.setEntityAction(EntityAction.DELETE);
            AbstractCodeBean.saveRefData(selectedRefData, refDataTOClass);
            initRefDataList();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddRefData;
    private javax.swing.JButton btnEditRefData;
    private javax.swing.JButton btnRemoveRefData;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menuAddRefData;
    private javax.swing.JMenuItem menuEditRefData;
    private javax.swing.JMenuItem menuRemoveRefData;
    private javax.swing.JPanel pnlRefDataList;
    private javax.swing.JPopupMenu popupRefData;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRefData;
    private javax.swing.JToolBar toolbarRefData;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
