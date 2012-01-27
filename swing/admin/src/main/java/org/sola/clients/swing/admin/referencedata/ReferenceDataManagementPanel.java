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
package org.sola.clients.swing.admin.referencedata;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.jdesktop.application.Action;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.BaUnitRelTypeBean;
import org.sola.clients.beans.referencedata.BaUnitTypeBean;
import org.sola.clients.beans.referencedata.BrSeverityTypeBean;
import org.sola.clients.beans.referencedata.BrTechnicalTypeBean;
import org.sola.clients.beans.referencedata.BrValidationTargetTypeBean;
import org.sola.clients.beans.referencedata.CommunicationTypeBean;
import org.sola.clients.beans.referencedata.GenderTypeBean;
import org.sola.clients.beans.referencedata.IdTypeBean;
import org.sola.clients.beans.referencedata.MortgageTypeBean;
import org.sola.clients.beans.referencedata.PartyRoleTypeBean;
import org.sola.clients.beans.referencedata.PartyTypeBean;
import org.sola.clients.beans.referencedata.RegistrationStatusTypeBean;
import org.sola.clients.beans.referencedata.RequestCategoryTypeBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.referencedata.RrrGroupTypeBean;
import org.sola.clients.beans.referencedata.TypeActionBean;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.beans.referencedata.ServiceActionTypeBean;
import org.sola.clients.beans.referencedata.ServiceStatusTypeBean;
import org.sola.clients.beans.referencedata.SourceBaUnitRelationTypeBean;
import org.sola.clients.beans.referencedata.SourceTypeBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.ui.referencedata.ReferenceDataPanel;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.AbstractCodeTO;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.referencedata.BaUnitRelTypeTO;
import org.sola.webservices.transferobjects.referencedata.BaUnitTypeTO;
import org.sola.webservices.transferobjects.referencedata.BrSeverityTypeTO;
import org.sola.webservices.transferobjects.referencedata.BrTechnicalTypeTO;
import org.sola.webservices.transferobjects.referencedata.BrValidationTargetTypeTO;
import org.sola.webservices.transferobjects.referencedata.CommunicationTypeTO;
import org.sola.webservices.transferobjects.referencedata.GenderTypeTO;
import org.sola.webservices.transferobjects.referencedata.IdTypeTO;
import org.sola.webservices.transferobjects.referencedata.MortgageTypeTO;
import org.sola.webservices.transferobjects.referencedata.PartyRoleTypeTO;
import org.sola.webservices.transferobjects.referencedata.PartyTypeTO;
import org.sola.webservices.transferobjects.referencedata.RegistrationStatusTypeTO;
import org.sola.webservices.transferobjects.referencedata.RequestCategoryTypeTO;
import org.sola.webservices.transferobjects.referencedata.RequestTypeTO;
import org.sola.webservices.transferobjects.referencedata.RrrGroupTypeTO;
import org.sola.webservices.transferobjects.referencedata.TypeActionTO;
import org.sola.webservices.transferobjects.referencedata.RrrTypeTO;
import org.sola.webservices.transferobjects.referencedata.ServiceActionTypeTO;
import org.sola.webservices.transferobjects.referencedata.ServiceStatusTypeTO;
import org.sola.webservices.transferobjects.referencedata.SourceBaUnitRelationTypeTO;
import org.sola.webservices.transferobjects.referencedata.SourceTypeTO;

/**
 * Allows to manage different reference data tables.
 */
public class ReferenceDataManagementPanel extends javax.swing.JPanel {

    /** {@link ReferenceDataPanel} listener. */
    private class ReferenceDataPanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ReferenceDataPanel.SAVED_REFDATA_PROPERTY)
                    || evt.getPropertyName().equals(ReferenceDataPanel.CREATED_REFDATA_PROPERTY)) {

                if (evt.getPropertyName().equals(ReferenceDataPanel.CREATED_REFDATA_PROPERTY)) {
                    pnlRefData.setReferenceDataBean(null);
                    MessageUtility.displayMessage(ClientMessage.ADMIN_REFDATA_CREATED, new String[]{headerTitle});
                } else if (evt.getPropertyName().equals(ReferenceDataPanel.SAVED_REFDATA_PROPERTY)) {
                    MessageUtility.displayMessage(ClientMessage.ADMIN_OBJECT_SAVED);
                    pnlRefData.setReferenceDataBean(null);
                    showRefDataList();
                    initRefDataList();
                }
            }
            if (evt.getPropertyName().equals(ReferenceDataPanel.CANCEL_ACTION_PROPERTY)) {
                showRefDataList();
                initRefDataList();
            }
        }
    }
    
    public static final String SELECTED_REF_DATA = "selectedRefData";
    private Class<? extends AbstractCodeBean> refDataClass;
    private Class<? extends AbstractCodeTO> refDataTOClass;
    private String headerTitle;
    private ObservableList<? extends AbstractCodeBean> refDataList;
    private AbstractCodeBean selectedRefData;
    private ResourceBundle resourceBundle;

    /** Creates {@link ReferenceDataPanel} instance. */
    private ReferenceDataPanel createRefDataPanel() {
        return new ReferenceDataPanel(refDataClass, refDataTOClass);
    }

    /** Default panel constructor. */
    public ReferenceDataManagementPanel() {
        initComponents();
        customizeComponents();
    }
    
    
    
    
     /** Applies customization of component L&F. */
    private void customizeComponents() {
     
//    BUTTONS   
    LafManager.getInstance().setBtnProperties( btnAddRefData);
    LafManager.getInstance().setBtnProperties(btnClose);
    LafManager.getInstance().setBtnProperties(btnEditRefData);
    LafManager.getInstance().setBtnProperties(btnOK);
    LafManager.getInstance().setBtnProperties(btnRemoveRefData);
    
    }

    
    /** 
     * Creates new instance of panel with predefined parameters.
     * @param refDataClass Type of reference data to load.
     * @param headerTitle Reference data name to put on the title of the panel.
     */
    public <T extends AbstractCodeBean> ReferenceDataManagementPanel(
            Class<T> refDataClass, String headerTitle) {

        resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/referencedata/Bundle");
        this.headerTitle = headerTitle;
        this.refDataClass = refDataClass;
        initRefDataList();

        initComponents();

        ReferenceDataPanelListener listener = new ReferenceDataPanelListener();
        pnlRefData.addPropertyChangeListener(listener);
        pnlRequestType.addPropertyChangeListener(listener);
        pnlRrrType.addPropertyChangeListener(listener);
        this.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(SELECTED_REF_DATA)) {
                    customizeRefDataButtons((AbstractCodeBean) evt.getNewValue());
                }
            }
        });

        customizeRefDataButtons(null);
        showRefDataList();
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
        return headerTitle;
    }

    /** Sets title header for the panel. */
    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    /** Returns list of reference data objects, bound on the form. */
    public List<? extends AbstractCodeBean> getRefDataList() {
        return refDataList;
    }

    // Methods 
    /** Enables/disables reference data buttons. */
    private void customizeRefDataButtons(AbstractCodeBean refDataBean) {
        btnEditRefData.getAction().setEnabled(refDataBean != null);
        btnRemoveRefData.getAction().setEnabled(refDataBean != null);
    }

    /** Shows the panel with reference data list. */
    private void showRefDataList() {
        pnlRefDataList.setVisible(true);
        pnlRefDataManagement.setVisible(false);
        pnlRequestType.setVisible(false);
        pnlRrrType.setVisible(false);
        pnlHeader.setTitleText(headerTitle);
    }

    /** Shows the panel with selected reference data object. */
    private void showRefData(AbstractCodeBean refDataBean) {
        pnlRefDataList.setVisible(false);
        pnlRequestType.setVisible(false);
        pnlRrrType.setVisible(false);
        pnlRefData.setReferenceDataBean(refDataBean);
        pnlRefDataManagement.setVisible(true);
        if (refDataBean != null) {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    refDataBean.getTranslatedDisplayValue()));
        } else {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    resourceBundle.getString("ReferenceDataManagementPanel.NewItem.text")));
        }
    }

    /** Shows request type panel. */
    private void showRequestTypePanel(RequestTypeBean requestTypeBean) {
        pnlRefDataList.setVisible(false);
        pnlRefDataManagement.setVisible(false);
        pnlRrrType.setVisible(false);
        pnlRequestType.setVisible(true);
        pnlRequestType.setRequestTypeBean(requestTypeBean);

        if (requestTypeBean != null) {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    requestTypeBean.getTranslatedDisplayValue()));
        } else {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    resourceBundle.getString("ReferenceDataManagementPanel.NewItem.text")));
        }
    }

    /** Shows RrrType panel. */
    private void showRrrTypePanel(RrrTypeBean rrrTypeBean) {
        pnlRefDataList.setVisible(false);
        pnlRefDataManagement.setVisible(false);
        pnlRequestType.setVisible(false);
        pnlRrrType.setVisible(true);
        pnlRrrType.setRrrTypeBean(rrrTypeBean);

        if (rrrTypeBean != null) {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    rrrTypeBean.getTranslatedDisplayValue()));
        } else {
            pnlHeader.setTitleText(MessageFormat.format("{0} - {1}", headerTitle,
                    resourceBundle.getString("ReferenceDataManagementPanel.NewItem.text")));
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
        }
        else if (refDataClass == BaUnitRelTypeBean.class) {
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
        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlRequestType = new org.sola.clients.swing.ui.referencedata.RequestTypePanel();
        pnlRefDataList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableRefData = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        toolbarRefData = new javax.swing.JToolBar();
        btnAddRefData = new javax.swing.JButton();
        btnEditRefData = new javax.swing.JButton();
        btnRemoveRefData = new javax.swing.JButton();
        pnlRefDataManagement = new javax.swing.JPanel();
        pnlRefData = createRefDataPanel();
        btnOK = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        pnlRrrType = new org.sola.clients.swing.ui.referencedata.RrrTypePanel();

        popupRefData.setName("popupRefData"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(ReferenceDataManagementPanel.class, this);
        menuAddRefData.setAction(actionMap.get("addRefData")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/admin/referencedata/Bundle"); // NOI18N
        menuAddRefData.setText(bundle.getString("ReferenceDataManagementPanel.menuAddRefData.text")); // NOI18N
        menuAddRefData.setName("menuAddRefData"); // NOI18N
        popupRefData.add(menuAddRefData);

        menuEditRefData.setAction(actionMap.get("editRefData")); // NOI18N
        menuEditRefData.setText(bundle.getString("ReferenceDataManagementPanel.menuEditRefData.text")); // NOI18N
        menuEditRefData.setName("menuEditRefData"); // NOI18N
        popupRefData.add(menuEditRefData);

        menuRemoveRefData.setAction(actionMap.get("removeRefData")); // NOI18N
        menuRemoveRefData.setText(bundle.getString("ReferenceDataManagementPanel.menuRemoveRefData.text")); // NOI18N
        menuRemoveRefData.setName("menuRemoveRefData"); // NOI18N
        popupRefData.add(menuRemoveRefData);

        setMinimumSize(new java.awt.Dimension(631, 457));

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("ReferenceDataManagementPanel.pnlHeader.titleText")); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.CardLayout());

        pnlRequestType.setCloseOnSave(true);
        pnlRequestType.setName("pnlRequestType"); // NOI18N
        jPanel1.add(pnlRequestType, "card4");

        pnlRefDataList.setName("pnlRefDataList"); // NOI18N

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

        btnAddRefData.setAction(actionMap.get("addRefData")); // NOI18N
        btnAddRefData.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnAddRefData.setText(bundle.getString("ReferenceDataManagementPanel.btnAddRefData.text")); // NOI18N
        btnAddRefData.setFocusable(false);
        btnAddRefData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddRefData.setName("btnAddRefData"); // NOI18N
        btnAddRefData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarRefData.add(btnAddRefData);

        btnEditRefData.setAction(actionMap.get("editRefData")); // NOI18N
        btnEditRefData.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnEditRefData.setText(bundle.getString("ReferenceDataManagementPanel.btnEditRefData.text")); // NOI18N
        btnEditRefData.setFocusable(false);
        btnEditRefData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEditRefData.setName("btnEditRefData"); // NOI18N
        btnEditRefData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarRefData.add(btnEditRefData);

        btnRemoveRefData.setAction(actionMap.get("removeRefData")); // NOI18N
        btnRemoveRefData.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnRemoveRefData.setText(bundle.getString("ReferenceDataManagementPanel.btnRemoveRefData.text")); // NOI18N
        btnRemoveRefData.setFocusable(false);
        btnRemoveRefData.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveRefData.setName("btnRemoveRefData"); // NOI18N
        btnRemoveRefData.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarRefData.add(btnRemoveRefData);

        javax.swing.GroupLayout pnlRefDataListLayout = new javax.swing.GroupLayout(pnlRefDataList);
        pnlRefDataList.setLayout(pnlRefDataListLayout);
        pnlRefDataListLayout.setHorizontalGroup(
            pnlRefDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbarRefData, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
        );
        pnlRefDataListLayout.setVerticalGroup(
            pnlRefDataListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRefDataListLayout.createSequentialGroup()
                .addComponent(toolbarRefData, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE))
        );

        jPanel1.add(pnlRefDataList, "card3");

        pnlRefDataManagement.setName("pnlRefDataManagement"); // NOI18N

        pnlRefData.setName("pnlRefData"); // NOI18N

        btnOK.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnOK.setText(bundle.getString("ReferenceDataManagementPanel.btnOK.text")); // NOI18N
        btnOK.setName("btnOK"); // NOI18N
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnClose.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnClose.setText(bundle.getString("ReferenceDataManagementPanel.btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlRefDataManagementLayout = new javax.swing.GroupLayout(pnlRefDataManagement);
        pnlRefDataManagement.setLayout(pnlRefDataManagementLayout);
        pnlRefDataManagementLayout.setHorizontalGroup(
            pnlRefDataManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRefDataManagementLayout.createSequentialGroup()
                .addContainerGap(369, Short.MAX_VALUE)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(pnlRefData, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
        );
        pnlRefDataManagementLayout.setVerticalGroup(
            pnlRefDataManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRefDataManagementLayout.createSequentialGroup()
                .addComponent(pnlRefData, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlRefDataManagementLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnClose)
                    .addComponent(btnOK)))
        );

        jPanel1.add(pnlRefDataManagement, "card3");

        pnlRrrType.setName("pnlRrrType"); // NOI18N
        jPanel1.add(pnlRrrType, "card5");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 631, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        pnlRefData.save();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        showRefDataList();
        initRefDataList();
    }//GEN-LAST:event_btnCloseActionPerformed

    /** Shows {@link ReferenceDataPanel} to create new record in the given reference data table.*/
    @Action
    public void addRefData() {
        if (refDataClass == RequestTypeBean.class) {
            showRequestTypePanel(null);
        } else if (refDataClass == RrrTypeBean.class) {
            showRrrTypePanel(null);
        } else {
            showRefData(null);
            btnOK.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_CREATE).getMessage());
        }
    }

    /** Shows {@link ReferenceDataPanel} and passes reference data bean for editing. */
    @Action
    public void editRefData() {
        if (selectedRefData != null) {
            if (refDataClass == RequestTypeBean.class) {
                showRequestTypePanel((RequestTypeBean) selectedRefData);
            } else if (refDataClass == RrrTypeBean.class) {
                showRrrTypePanel((RrrTypeBean) selectedRefData);
            } else {
                showRefData(selectedRefData);
                btnOK.setText(MessageUtility.getLocalizedMessage(
                        ClientMessage.GENERAL_LABELS_SAVE_AND_CLOSE).getMessage());
            }
        }
    }

    /** Removes selected reference data bean. */
    @Action
    public void removeRefData() {
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
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnEditRefData;
    private javax.swing.JButton btnOK;
    private javax.swing.JButton btnRemoveRefData;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem menuAddRefData;
    private javax.swing.JMenuItem menuEditRefData;
    private javax.swing.JMenuItem menuRemoveRefData;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private org.sola.clients.swing.ui.referencedata.ReferenceDataPanel pnlRefData;
    private javax.swing.JPanel pnlRefDataList;
    private javax.swing.JPanel pnlRefDataManagement;
    private org.sola.clients.swing.ui.referencedata.RequestTypePanel pnlRequestType;
    private org.sola.clients.swing.ui.referencedata.RrrTypePanel pnlRrrType;
    private javax.swing.JPopupMenu popupRefData;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRefData;
    private javax.swing.JToolBar toolbarRefData;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
