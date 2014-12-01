/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.swing.desktop.administrative;

import org.sola.clients.swing.ui.administrative.PropertyAssignmentDialog;
import org.sola.clients.swing.desktop.cadastre.CreateParcelDialog;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import net.sf.jasperreports.engine.JasperPrint;
import org.sola.clients.beans.administrative.*;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.cadastre.SpatialValueAreaBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.party.PartySummaryListBean;
import org.sola.clients.beans.referencedata.*;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceListBean;
import org.sola.clients.beans.system.PanelLauncherGroupBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.cadastre.SLParcelPanel;
import org.sola.clients.swing.desktop.cadastre.AddParcelDialog;
import org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForBaUnit;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.PanelLauncher;
import org.sola.clients.swing.ui.cadastre.CadastreObjectsDialog;
import org.sola.clients.swing.ui.renderers.*;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.clients.swing.ui.security.SecurityClassificationDialog;
import org.sola.clients.swing.ui.source.DocumentsPanel;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.EntityTable;
import org.sola.webservices.transferobjects.administrative.BaUnitAreaTO;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;
import org.sola.webservices.transferobjects.cadastre.SpatialValueAreaTO;

/**
 * This form is used to manage property object ({
 *
 * @codeBaUnit}). {@link BaUnitBean} is used to bind data on the form.
 */
public class SLPropertyPanel extends ContentPanel {

    /**
     * Listens for events of different right forms, to add created right into
     * the list of rights or update existing one.
     */
    private class RightFormListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(SimpleRightPanel.UPDATED_RRR)
                    && evt.getNewValue() != null) {
                // Add new RRR
                baUnitBean1.addRrr((RrrBean) evt.getNewValue());
                tableRights.clearSelection();
            }
        }
    }
    private ApplicationBean applicationBean;
    private ApplicationServiceBean applicationService;
    private String baUnitID;
    private String nameFirstPart;
    private String nameLastPart;
    private ControlsBundleForBaUnit mapControl = null;
    private boolean readOnly = false;
    java.util.ResourceBundle resourceBundle;
    private PropertyChangeListener propertyRelationshipListener;
    public BaUnitBean whichBaUnitSelected;

    /**
     * Creates {@link BaUnitBean} used to bind form components.
     */
    private BaUnitBean createBaUnitBean() {
        if (baUnitBean1 == null) {
            BaUnitBean baUnitBean = null;
            if (nameFirstPart != null && nameLastPart != null) {
                // Get BA Unit
                baUnitBean = getBaUnit(nameFirstPart, nameLastPart);
                if (baUnitBean != null) {
                    baUnitID = baUnitBean.getId();
                }
            }
            if (baUnitBean == null) {
                baUnitBean = new BaUnitBean();
                if (nameFirstPart != null && nameLastPart != null) {
                    baUnitBean.setNameFirstpart(nameFirstPart);
                    baUnitBean.setNameLastpart(nameLastPart);
                }
            }
            baUnitBean1 = baUnitBean;
            if (baUnitBean1.getStatusCode() != null && !baUnitBean1.getStatusCode().equals(StatusConstants.CURRENT)
                    && !baUnitBean1.getStatusCode().equals(StatusConstants.PENDING)) {
                readOnly = true;
            }
        }
        return baUnitBean1;
    }

    /**
     * Creates {@link BaUnitAreaBean} used to bind form components.
     */
    private BaUnitAreaBean createBaUnitAreaBean() {
        if (baUnitAreaBean1 == null) {
            BaUnitAreaBean baUnitAreaBean = null;
            baUnitAreaBean = getBaUnitArea(baUnitBean1.getId());
            if (baUnitAreaBean == null) {
                baUnitAreaBean = new BaUnitAreaBean();
            }
            baUnitAreaBean1 = baUnitAreaBean;
        }

        return baUnitAreaBean1;

    }

    /**
     * Creates documents table to show documents linked to the SL Property
     */
    private DocumentsManagementExtPanel createDocumentsPanel() {
        DocumentsManagementExtPanel panel;
        if (baUnitBean1 != null) {
            panel = new DocumentsManagementExtPanel(baUnitBean1.getSourceList(),
                    applicationBean, !readOnly);
        } else {
            panel = new DocumentsManagementExtPanel();
        }
        return panel;
    }

    /**
     * Creates documents table to show paper title documents.
     */
    private DocumentsPanel createPropertyDocsPanel() {
        DocumentsPanel panel;
        if (baUnitBean1 != null) {
            panel = new DocumentsPanel(baUnitBean1.getSourceList());
        } else {
            panel = new DocumentsPanel();
        }
        return panel;
    }

    private PartySummaryListBean createPartySummaryList() {
        PartySummaryListBean agentsList = new PartySummaryListBean();
        agentsList.loadParties(PartyRoleTypeBean.ROLE_PROPERTY_MANAGER,
                true, (String) null);
        return agentsList;
    }

    /**
     * Form constructor. Creates and open form in read only mode.
     *
     * @param nameFirstPart First part of the property code.
     * @param nameLastPart Last part of the property code.
     */
    public SLPropertyPanel(String nameFirstPart, String nameLastPart, boolean readOnly) {
        this(null, null, nameFirstPart, nameLastPart, readOnly);
    }

    /**
     * Form constructor.
     *
     * @param applicationBean {@link ApplicationBean} instance, used to get data
     * on BaUnit and provide list of documents.
     * @param applicationService {@link ApplicationServiceBean} instance, used
     * to determine what actions should be taken on this form.
     * @param nameFirstPart First part of the property code.
     * @param nameLastPart Last part of the property code.
     * @param readOnly If true, opens form in read only mode.
     */
    public SLPropertyPanel(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService,
            String nameFirstPart, String nameLastPart, Boolean readOnly) {
        this.readOnly = readOnly || !SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE);
        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        this.nameFirstPart = nameFirstPart;
        this.nameLastPart = nameLastPart;
        resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle");
        initComponents();
        postInit();

    }

    /**
     * Form constructor.
     *
     * @param applicationBean {@link ApplicationBean} instance, used to get list
     * of documents.
     * @param applicationService {@link ApplicationServiceBean} instance, used
     * to determine what actions should be taken on this form.
     * @param BaUnitBean Instance of {@link BaUnitBean}, used to bind data on
     * the form.
     * @param readOnly If true, opens form in read only mode.
     */
    public SLPropertyPanel(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService,
            BaUnitBean baUnitBean, Boolean readOnly) {
        this.baUnitBean1 = baUnitBean;
        this.readOnly = readOnly || !SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE);
        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        if (baUnitBean != null) {
            this.nameFirstPart = baUnitBean.getNameFirstpart();
            this.nameLastPart = baUnitBean.getNameLastpart();
        }

        resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle");
        initComponents();
        postInit();

    }

    /**
     * Makes post initialization tasks.
     */
    private void postInit() {

        if (BaUnitSummaryBean.TYPE_STATE_LAND.equals(baUnitBean1.getTypeCode())) {
            //State Land property, setup tabs as required.
            tabsMain.removeTabAt(tabsMain.indexOfComponent(pnlGeneral));
            tabsMain.removeTabAt(tabsMain.indexOfComponent(pnlOwnership));
            tabsMain.removeTabAt(tabsMain.indexOfComponent(pnlNotations));

        } else {
            //Other property, configure tabs and default to readOnly
            this.readOnly = true;
            tabsMain.removeTabAt(tabsMain.indexOfComponent(pnlSLGeneral));
            tabsMain.removeTabAt(tabsMain.indexOfComponent(pnlNotes));
            tabsMain.removeTabAt(tabsMain.indexOfComponent(pnlValuations));
            btnAssign.setVisible(false);
        }

        customizeForm();

        rrrTypes.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RrrTypeListBean.SELECTED_RRR_TYPE_PROPERTY)) {
                    customizeCreateRightButton((RrrTypeBean) evt.getNewValue());
                }
            }
        });

        baUnitBean1.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BaUnitBean.SELECTED_RIGHT_PROPERTY)) {
                    customizeRightsButtons((RrrBean) evt.getNewValue());
                } else if (evt.getPropertyName().equals(BaUnitBean.SELECTED_HISTORIC_RIGHT_PROPERTY)) {
                    customizeHistoricRightsViewButton();
                } else if (evt.getPropertyName().equals(BaUnitBean.SELECTED_BA_UNIT_NOTATION_PROPERTY)) {
                    customizeNotationButtons((BaUnitNotationBean) evt.getNewValue());
                } else if (evt.getPropertyName().equals(BaUnitBean.SELECTED_PARCEL_PROPERTY)) {
                    customizeParcelButtons((CadastreObjectBean) evt.getNewValue());
                } else if (evt.getPropertyName().equals(BaUnitBean.ROW_VERSION_PROPERTY)) {
                    customizePrintButton();
                } else if (evt.getPropertyName().equals(BaUnitBean.SELECTED_PARENT_BA_UNIT_PROPERTY)) {
                    customizeParentPropertyButtons();
                } else if (evt.getPropertyName().equals(BaUnitBean.SELECTED_CHILD_BA_UNIT_PROPERTY)) {
                    customizeChildPropertyButtons();
                }

            }
        });

        propertyDocsPanel.getSourceListBean().addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(SourceListBean.SELECTED_SOURCE_PROPERTY)) {
                    customizePaperTitleButtons((SourceBean) evt.getNewValue());
                }
            }
        });

        saveBaUnitState();
    }

    /**
     * Runs form customization, to restrict certain actions, bind listeners on
     * the {@link BaUnitBean} and other components.
     */
    private void customizeForm() {

        txtSLLastPart.setEnabled(false);
        txtSLStatus.setEnabled(false);
        txtSLLandUse.setEnabled(false);
        txtPropertyManager.setEnabled(false);
        String title;

        if (nameFirstPart != null && nameLastPart != null) {
            title = String.format(resourceBundle.getString("SLPropertyPanel.existingProperty.Text"), baUnitBean1.getDisplayName());
        } else {
            title = resourceBundle.getString("SLPropertyPanel.newProperty.Text");
        }

        if (applicationBean != null && applicationService != null) {
            title = String.format("%s > %s", title,
                    String.format(resourceBundle.getString("SLPropertyPanel.applicationInfo.Text"),
                            applicationService.getRequestType().getDisplayValue()));
        }
        this.setBreadCrumbTitle(this.getBreadCrumbPath(), title);

        boolean editProperty = SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE) && !readOnly;
        btnSave.setEnabled(editProperty);
        txtSLDescription.setEnabled(editProperty);
        txtSLArea.setEnabled(editProperty);

        btnAssign.setEnabled(SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE,
                RolesConstants.ADMINISTRATIVE_ASSIGN_TEAM) && !baUnitBean1.isNew());

        // Make security button invisible unless the user has permission to set the classification level.
        btnSecurity.setVisible(SecurityBean.isInRole(RolesConstants.CLASSIFICATION_CHANGE_CLASS));

        if (!SecurityBean.isInRole(RolesConstants.GIS_VIEW_MAP)) {
            // User does not have rights to view the map
            tabsMain.removeTabAt(tabsMain.indexOfComponent(pnlMap));
        }
        customizeRightsButtons(null);
        customizeNotationButtons(null);
        customizeRightTypesList();
        customizeParcelButtons(null);
        customizePrintButton();
        customizeParentPropertyButtons();
        customizeChildPropertyButtons();
        customizeTerminationButton();
        customizeHistoricRightsViewButton();
        customizePaperTitleButtons(null);

    }

    /**
     * Opens the property relationship panel so the user can associate this
     * property with another property.
     */
    private void openPropertyRelationshipPanel() {
        if (baUnitBean1 == null) {
            return;
        }
        if (getMainContentPanel() != null) {
            if (propertyRelationshipListener == null) {
                propertyRelationshipListener = new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(SLPropertyRelationshipPanel.SELECTED_RESULT_PROPERTY)) {
                            addParentProperty((Object[]) evt.getNewValue(), true);
                        }
                    }
                };
            }
        }

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTYLINK));

                SLPropertyRelationshipPanel propRelPanel = new SLPropertyRelationshipPanel();
                propRelPanel.addPropertyChangeListener(propertyRelationshipListener);
                getMainContentPanel().addPanel(propRelPanel, MainContentPanel.CARD_PROPERTY_RELATIONSHIP, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Populates rights, parcels and parent Properties lists from provided
     * result object.
     *
     * @param selectedResult Array of selected result from the wizard form.
     * First item of array contains selected {@link BaUnitBean}, second item
     * contains {@link BaUnitRelTypeBean}.
     */
    private boolean addParentProperty(Object[] selectedResult, boolean showMessage) {
        if (selectedResult == null) {
            return false;
        }

        BaUnitSummaryBean selectedBaUnit = (BaUnitSummaryBean) selectedResult[0];
        BaUnitRelTypeBean baUnitRelType = (BaUnitRelTypeBean) selectedResult[1];
        //this.whichBaUnitSelected = selectedBaUnit;
        // Check if relation type duplicates a relationship already on the list
//        for (RelatedBaUnitInfoBean parent : baUnitBean1.getFilteredParentBaUnits()) {
//            if (parent.getRelationCode() != null
//                    && parent.getRelationCode().equals(baUnitRelType.getCode())) {
//                MessageUtility.displayMessage(ClientMessage.BAUNIT_WRONG_RELATION_TYPE);
//                return false;
//            }
//        }

        // Check if baUnit is already related as a parent baUnit with the same relationship type
        for (RelatedBaUnitInfoBean parent : baUnitBean1.getFilteredParentBaUnits()) {
            if (parent.getRelatedBaUnitId() != null && parent.getRelatedBaUnitId().equals(selectedBaUnit.getId())
                    && parent.getRelationCode() != null && parent.getRelationCode().equals(baUnitRelType.getCode())) {
                if (showMessage) {
                    MessageUtility.displayMessage(ClientMessage.BAUNIT_HAS_SELECTED_PARENT_BA_UNIT);
                }
                return false;
            }
        }

        // Create relation
        RelatedBaUnitInfoBean relatedBuUnit = new RelatedBaUnitInfoBean();
        relatedBuUnit.setBaUnitId(baUnitBean1.getId());
        relatedBuUnit.setBaUnitRelType(baUnitRelType);
        relatedBuUnit.setRelatedBaUnit(selectedBaUnit);
        relatedBuUnit.setRelatedBaUnitId(selectedBaUnit.getId());
        baUnitBean1.getParentBaUnits().addAsNew(relatedBuUnit);

        if (showMessage) {
            tabsMain.setSelectedIndex(tabsMain.indexOfComponent(pnlRelationships));
        }
        return true;
    }

    /**
     * Enables or disables "open", "add" and "remove" buttons for the parent
     * Properties list.
     */
    private void customizeParentPropertyButtons() {
        boolean enabled = !readOnly;
        if (baUnitBean1 == null || (baUnitBean1.getStatusCode() != null
                && !baUnitBean1.getStatusCode().equals(StatusConstants.PENDING))) {
            enabled = false;
        }

        btnViewParent.setEnabled(baUnitBean1.getSelectedParentBaUnit() != null);
        btnAddParent.setEnabled(enabled);
        btnRemoveParent.setEnabled(enabled && btnViewParent.isEnabled());

        menuViewParentBaUnit.setEnabled(btnViewParent.isEnabled());
        menuAddParentBaUnit.setEnabled(btnAddParent.isEnabled());
        menuRemoveParentBaUnit.setEnabled(btnRemoveParent.isEnabled());
    }

    /**
     * Enables or disables "open" button for the child Properties list.
     */
    private void customizeChildPropertyButtons() {
        btnViewChild.setEnabled(baUnitBean1.getSelectedChildBaUnit() != null);
        menuViewChildBaUnit.setEnabled(btnViewChild.isEnabled());
    }

    /**
     * Enables or disables print button if row version of {@link BaUnitBean} > 0
     * .
     */
    private void customizePrintButton() {
        btnPrintBaUnit.setEnabled(baUnitBean1.getRowVersion() > 0);
    }

    private void customizeHistoricRightsViewButton() {
        btnViewHistoricRight.setEnabled(baUnitBean1.getSelectedHistoricRight() != null);
    }

    /**
     * Enables or disables notation buttons, depending on the form state.
     */
    private void customizeNotationButtons(BaUnitNotationBean notation) {
        boolean canAdd = !readOnly || SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_NOTATION_SAVE);
        boolean canView = notation != null;
        boolean canEdit = canAdd && canView;
        boolean canRemove = canEdit && !notation.getStatusCode().equals(StatusConstants.COMPLETED);

        btnAddNotation.setEnabled(canAdd);
        btnViewNotation.setEnabled(canView);
        btnEditNotation.setEnabled(canEdit);
        btnRemoveNotation.setEnabled(canRemove);
        menuAddNotation.setEnabled(btnAddNotation.isEnabled());
        menuViewNotation.setEnabled(btnViewNotation.isEnabled());
        menuEditNotation.setEnabled(btnEditNotation.isEnabled());
        menuRemoveNotation.setEnabled(btnRemoveNotation.isEnabled());
    }

    /**
     * Enables or disables termination button, depending on the form state.
     */
    private void customizeTerminationButton() {
        boolean enabled = !readOnly;

        // Check BaUnit status to be current
        if (baUnitBean1.getStatusCode() == null || !baUnitBean1.getStatusCode().equals(StatusConstants.CURRENT)) {
            enabled = false;
        }

        // Check RequestType to have cancel action.
        if (applicationService == null || applicationService.getRequestType() == null
                || applicationService.getRequestType().getTypeActionCode() == null
                || !applicationService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_DISPOSE_PROPERTY)) {
            enabled = false;
        }

        // Determine what should be shown on the button, terminate or cancelling of termination.
        if (baUnitBean1.getPendingActionCode() != null && applicationService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_DISPOSE_PROPERTY)) {
            // Show cancel
            btnTerminate.setIcon(new ImageIcon(getClass().getResource("/images/common/undo.png")));
            btnTerminate.setText(resourceBundle.getString("SLPropertyPanel.btnTerminate.text2"));
        } else {
            // Show terminate
            btnTerminate.setIcon(new ImageIcon(getClass().getResource("/images/common/stop.png")));
            btnTerminate.setText(resourceBundle.getString("SLPropertyPanel.btnTerminate.text"));
        }
        btnTerminate.setEnabled(enabled);
    }

    /**
     * Enables or disables parcel buttons, depending on the form state and
     * selection in the list of parcel.
     */
    private void customizeParcelButtons(CadastreObjectBean cadastreBean) {
        if (cadastreBean == null || cadastreBean.isLocked() || readOnly) {
            btnRemoveParcel.setEnabled(false);
            btnEditParcel.setEnabled(false);
        } else {
            btnRemoveParcel.setEnabled(true);
            btnEditParcel.setEnabled(true);
        }
        btnViewParcel.setEnabled(cadastreBean != null);
        btnAddParcel.setEnabled(!readOnly);
        menuAddParcel.setEnabled(btnAddParcel.isEnabled());
        menuRemoveParcel.setEnabled(btnRemoveParcel.isEnabled());
        menuViewParcel.setEnabled(btnViewParcel.isEnabled());
        menuEditParcel.setEnabled(btnEditParcel.isEnabled());
    }

    /**
     * Enables or disables combobox list of right types, depending on the form
     * state.
     */
    private void customizeRightTypesList() {
        cbxRightType.setSelectedIndex(-1);
        rrrTypes.setSelectedRrrType(null);

        if (!readOnly && isActionAllowed(RrrTypeActionConstants.NEW)) {
            cbxRightType.setEnabled(true);

            // Restrict selection of right type by application service
            if (applicationService != null && applicationService.getRequestType() != null
                    && applicationService.getRequestType().getRrrTypeCode() != null) {
                rrrTypes.setSelectedRightByCode(applicationService.getRequestType().getRrrTypeCode());
                if (rrrTypes.getSelectedRrrType() != null) {
                    cbxRightType.setEnabled(false);
                }
            }
        } else {
            cbxRightType.setEnabled(false);
        }
        customizeCreateRightButton(rrrTypes.getSelectedRrrType());
    }

    /**
     * Enables or disables button for creating new right, depending on the form
     * state.
     */
    private void customizeCreateRightButton(RrrTypeBean rrrTypeBean) {
        if (rrrTypeBean != null && rrrTypeBean.getCode() != null
                && !readOnly && isActionAllowed(RrrTypeActionConstants.NEW)) {
            btnCreateRight.setEnabled(true);
        } else {
            btnCreateRight.setEnabled(false);
        }
    }

    /**
     * Enables or disables buttons for managing list of rights, depending on the
     * form state, selected right and it's state.
     */
    private void customizeRightsButtons(RrrBean rrrBean) {
        btnEditRight.setEnabled(false);
        btnRemoveRight.setEnabled(false);
        btnChangeRight.setEnabled(false);
        btnExtinguish.setEnabled(false);
        btnViewRight.setEnabled(rrrBean != null);

        if (rrrBean != null && !rrrBean.isLocked() && !readOnly) {
            boolean isPending = rrrBean.getStatusCode().equals(StatusConstants.PENDING);

            // Control pending state and allowed types of RRR for edit/remove buttons
            if (isPending && isRightTypeAllowed(rrrBean.getTypeCode())) {
                btnEditRight.setEnabled(true);
                btnRemoveRight.setEnabled(true);
            }

            // Control the record state, duplication of pending records,
            // allowed action and allowed type of RRR.
            if (rrrBean.getStatusCode().equals(StatusConstants.CURRENT)
                    && !baUnitBean1.isPendingRrrExists(rrrBean)
                    && isRightTypeAllowed(rrrBean.getTypeCode())) {
                if (isActionAllowed(RrrTypeActionConstants.VARY)) {
                    btnChangeRight.setEnabled(true);
                }
                if (isActionAllowed(RrrTypeActionConstants.CANCEL)) {
                    btnExtinguish.setEnabled(true);
                }
            }
        }

        menuEditRight.setEnabled(btnEditRight.isEnabled());
        menuRemoveRight.setEnabled(btnRemoveRight.isEnabled());
        menuVaryRight.setEnabled(btnChangeRight.isEnabled());
        menuExtinguishRight.setEnabled(btnExtinguish.isEnabled());
        menuViewRight.setEnabled(btnViewRight.isEnabled());
    }

    /**
     * Enables or disables paper title buttons, depending on the form state.
     */
    private void customizePaperTitleButtons(SourceBean source) {
        if (source != null && source.getArchiveDocument() != null) {
            btnViewPropDoc.setEnabled(true);
            menuViewPropertyDocument.setEnabled(true);
        } else {
            btnViewPropDoc.setEnabled(false);
            menuViewPropertyDocument.setEnabled(false);
        }
    }

    /**
     * Checks if certain action is allowed on the form.
     */
    private boolean isActionAllowed(String action) {
        boolean result = true;

        if (RrrTypeActionConstants.CANCEL.equalsIgnoreCase(action)) {
            // Default to false if the action is cancel as cannot have cancel and vary/new actions
            // supported by the same service.
            result = false;
        }

        if (applicationService != null && applicationService.getRequestType() != null
                && applicationService.getRequestType().getTypeActionCode() != null) {
            result = applicationService.getRequestType().getTypeActionCode().equalsIgnoreCase(action);
        }
        return result;
    }

    /**
     * Checks what type of rights are allowed to create/manage on the form.
     */
    private boolean isRightTypeAllowed(String rrrTypeCode) {
        boolean result = true;

        if (rrrTypeCode != null && applicationService != null
                && applicationService.getRequestType() != null
                && applicationService.getRequestType().getRrrTypeCode() != null) {
            result = applicationService.getRequestType().getRrrTypeCode().equalsIgnoreCase(rrrTypeCode);
        }
        return result;
    }

    /**
     * Returns {@link BaUnitBean} by first and last name part.
     */
    private BaUnitBean getBaUnit(String nameFirstPart, String nameLastPart) {
        BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().getBaUnitByCode(nameFirstPart, nameLastPart);
        return TypeConverters.TransferObjectToBean(baUnitTO, BaUnitBean.class, null);
    }

    /**
     * Returns {@link BaUnitBean} by first and last name part.
     */
    private BaUnitBean getBaUnit(String id) {
        BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().getBaUnitById(id);
        return TypeConverters.TransferObjectToBean(baUnitTO, BaUnitBean.class, null);
    }

    /**
     * Returns {@link BaUnitBean} by first and last name part.
     */
    private BaUnitBean getBaUnitWithCadObject(String nameFirstPart, String nameLastPart, String colist) {
        BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().getBaUnitWithCadObject(nameFirstPart, nameLastPart, colist);
        return TypeConverters.TransferObjectToBean(baUnitTO, BaUnitBean.class, null);
    }

    /**
     * Returns {@link BaUnitBean} by first and last name part.
     */
    private SpatialValueAreaBean getSpatialValueArea(String colist) {
        SpatialValueAreaTO spatialValueAreaTO = WSManager.getInstance().getCadastreService().getSpatialValueArea(colist);
        return TypeConverters.TransferObjectToBean(spatialValueAreaTO, SpatialValueAreaBean.class, null);
    }

    /**
     * Returns {@link BaUnitAreaBean} by first and last name part.
     */
    private BaUnitAreaBean getBaUnitArea(String baUnitId) {
        BaUnitAreaTO baUnitAreaTO = WSManager.getInstance().getAdministrative().getBaUnitAreas(baUnitId);
        return TypeConverters.TransferObjectToBean(baUnitAreaTO, BaUnitAreaBean.class, null);
    }

    /**
     * Opens {@link ReportViewerForm} to display report.
     */
    private void showReport(JasperPrint report) {
        ReportViewerForm form = new ReportViewerForm(report);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }

    /**
     * Opens paper title attachment.
     */
    private void viewDocument() {
        if (propertyDocsPanel.getSourceListBean().getSelectedSource() != null) {
            propertyDocsPanel.getSourceListBean().getSelectedSource().openDocument();
        }
    }

    /**
     * Calculates the area for the property based on the official areas of the
     * linked parcels
     */
    private void setPropertyArea() {
        BigDecimal propertyArea = BigDecimal.ZERO;
        for (CadastreObjectBean parcel : baUnitBean1.getCadastreObjectFilteredList()) {
            if (parcel.getOfficialAreaSize() != null) {
                propertyArea = propertyArea.add(parcel.getOfficialAreaSize());
            }
        }
        txtSLArea.setValue(propertyArea);
        // Force the area to be accepted by the FormattedTextField
        txtSLArea.transferFocus();
    }

    /**
     * Open form to add new parcel or search for existing one.\
     *
     * @param isNew Opens {@link CreateParcelDialog} if true, otherwise opens
     * {@link AddParcelDialog}
     */
    private void addParcel() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getNewValue() != null) {
                    CadastreObjectBean bean = (CadastreObjectBean) e.getNewValue();
                    baUnitBean1.addParcel(bean);
                    if (bean.getGeomPolygon() != null) {
                        // Locate any underlying titles and add them to this state land property
                        addUnderlyingTitles(bean.getId());
                    }
                    // Increase the area of the property by the official area of the parcel
                    setPropertyArea();
                }
            }
        };
        JDialog form = new AddParcelDialog(this.applicationBean, null, true);
        form.setLocationRelativeTo(this);
        form.addPropertyChangeListener(AddParcelDialog.SELECTED_PARCEL, listener);
        form.setVisible(true);
        form.removePropertyChangeListener(AddParcelDialog.SELECTED_PARCEL, listener);
    }

    /**
     * Locates the titles underlying the state land parcel and adds them as
     * parent properties to the state land property.
     *
     * @param parcelId
     */
    private void addUnderlyingTitles(final String parcelId) {
        final List<BaUnitSummaryBean> underlyingTitles = new ArrayList<BaUnitSummaryBean>();
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_GET_UNDERLYING_TITLES));
                TypeConverters.TransferObjectListToBeanList(
                        WSManager.getInstance().getSearchService().getUnderlyingTitles(parcelId),
                        BaUnitSummaryBean.class, (List) underlyingTitles);
                return null;
            }

            @Override
            protected void taskDone() {
                BaUnitRelTypeBean underlyingTitleRel = CacheManager.getBeanByCode(CacheManager.getBaUnitRelTypes(),
                        BaUnitRelTypeBean.CODE_UNDERLYING_TITLE);
                for (BaUnitSummaryBean bean : underlyingTitles) {
                    Object[] params = new Object[]{bean, underlyingTitleRel};
                    addParentProperty(params, false);
                }
            }
        };
        TaskManager.getInstance().runTask(t);

    }

    private void editParcel() {
        if (baUnitBean1.getSelectedParcel() != null) {
            PropertyChangeListener listener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(SLParcelPanel.SAVE_PARCEL)
                            && evt.getNewValue() != null) {
                        int idx = baUnitBean1.getCadastreObjectList().getRealIndex(baUnitBean1.getSelectedParcel());
                        baUnitBean1.getCadastreObjectList().set(idx, (CadastreObjectBean) evt.getNewValue());
                        tableParcels.clearSelection();
                    }
                }
            };
            openParcelPanel(baUnitBean1.getSelectedParcel(), listener, false);
        }
    }

    private void viewParcel() {
        if (baUnitBean1.getSelectedParcel() != null) {
            openParcelPanel(baUnitBean1.getSelectedParcel(), null, true);
        }
    }

    /**
     * Removes selected parcel from the list of parcels.
     */
    private void removeParcel() {
        if (baUnitBean1.getSelectedParcel() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            baUnitBean1.removeSelectedParcel();
        }
        setPropertyArea();
    }

    /**
     * Opens the SLParcelPanel to allowing viewing/editing of a parcel.
     *
     * @param parcel The CadastreObjectBean to view/edit or null if the parcel
     * is to be created
     * @param listener A PropertyChangeListenter used to trigger actions when
     * the SLParcelPanel is closed. null if no listener is required.
     * @param panelReadOnly Flag to indicate if the ParcelPanel should be
     * displayed read only (true) or not.
     */
    private void openParcelPanel(final CadastreObjectBean parcel,
            final PropertyChangeListener listener, final boolean panelReadOnly) {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PARCEL));
                SLParcelPanel panel = new SLParcelPanel(parcel, baUnitBean1, panelReadOnly);
                if (listener != null) {
                    panel.addPropertyChangeListener(listener);
                }
                getMainContentPanel().addPanel(panel, MainContentPanel.CARD_SLPPARCEL_PANEL, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Removes selected right from the list of rights.
     */
    private void removeRight() {
        if (baUnitBean1.getSelectedRight() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            baUnitBean1.removeSelectedRight();
        }
    }

    /**
     * Opens appropriate right form for editing.
     */
    private void editRight() {

        if (baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.EDIT);
        }
    }

    /**
     * Opens appropriate right form to extinguish selected right.
     */
    private void extinguishRight() {

        if (baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.CANCEL);
        }
    }

    /**
     * Opens appropriate right form to create new right.
     */
    private void createRight() {
        openRightForm(null, RrrBean.RRR_ACTION.NEW);
    }

    /**
     * Opens appropriate right form to vary selected right.
     */
    private void varyRight() {
        if (baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.VARY);
        }
    }

    /**
     * Adds new notation on the BaUnit.
     */
    private void addNotation() {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(NotationPanel.SAVE_NOTATION)
                        && evt.getNewValue() != null) {
                    baUnitBean1.getBaUnitNotationList().addAsNew((BaUnitNotationBean) evt.getNewValue());
                }
            }
        };
        openNotationPanel(null, listener, false);
    }

    /**
     * Removes selected notation.
     */
    private void removeNotation() {
        if (MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            BaUnitNotationBean notation = baUnitBean1.getSelectedBaUnitNotation();
            if (notation != null) {
                baUnitBean1.removeSelectedBaUnitNotation();
                if (applicationBean == null) {
                    // Explicilty save the notation if this action is occuring outside of a transaction. 
                    notation.save();
                }
            }
        }
    }

    /**
     * Opens the NotationPanel to allow editing of selected notation.
     */
    private void editNotation() {
        if (baUnitBean1.getSelectedBaUnitNotation() != null) {
            PropertyChangeListener listener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(NotationPanel.SAVE_NOTATION)
                            && evt.getNewValue() != null) {
                        int idx = baUnitBean1.getBaUnitNotationList().getRealIndex(baUnitBean1.getSelectedBaUnitNotation());
                        baUnitBean1.getBaUnitNotationList().set(idx, (BaUnitNotationBean) evt.getNewValue());
                        tableNotes.clearSelection();
                    }
                }
            };
            openNotationPanel(baUnitBean1.getSelectedBaUnitNotation(), listener, false);
        }
    }

    /**
     * Allows user to view notation details.
     */
    private void viewNotation() {
        if (baUnitBean1.getSelectedBaUnitNotation() != null) {
            openNotationPanel(baUnitBean1.getSelectedBaUnitNotation(), null, true);
        }
    }

    /**
     * Opens the NotationPanel to allowing viewing/editing of a notation.
     *
     * @param notationBean The notation to view/edit or null if the notation is
     * to be created
     * @param listener A PropertyChangeListenter used to trigger actions when
     * the NotationPanel is closed. null if no listener is required.
     * @param panelReadOnly Flag to indicate if the NotationPanel should be
     * displayed read only (true) or not.
     */
    private void openNotationPanel(final BaUnitNotationBean notationBean,
            final PropertyChangeListener listener, final boolean panelReadOnly) {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_NOTATION));
                NotationPanel panel = new NotationPanel(notationBean, baUnitBean1, applicationBean, panelReadOnly);
                if (listener != null) {
                    panel.addPropertyChangeListener(listener);
                }
                getMainContentPanel().addPanel(panel, MainContentPanel.CARD_NOTATION_PANEL, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Prints BA unit certificate.
     */
    private void print() {
        if (ApplicationServiceBean.saveInformationService(RequestTypeBean.CODE_TITLE_SERACH)) {
            showReport(ReportManager.getBaUnitReport(getBaUnit(
                    baUnitBean1.getNameFirstpart(), baUnitBean1.getNameLastpart())));
        }
    }

    /**
     * Opens right form, depending on given {@link RrrBean} and action.
     *
     * @param rrrBean {@link RrrBean} instance to figure out what form to open
     * and pass this bean as a parameter.
     * @param action {@link RrrBean#RRR_ACTION} is passed to the right form for
     * further form customization.
     */
    private void openRightForm(RrrBean rrrBean, RrrBean.RRR_ACTION action) {
        if (action == RrrBean.RRR_ACTION.NEW && rrrTypes.getSelectedRrrType() == null) {
            return;
        }

        if (rrrBean == null) {
            rrrBean = new RrrBean();
            rrrBean.setTypeCode(rrrTypes.getSelectedRrrType().getCode());
            rrrBean.setPrimary(rrrTypes.getSelectedRrrType().isPrimary());
        }

        // Determine the panel to open for the Rrr
        String rrrPanelCode = rrrBean.getRrrType().getRrrPanelCode();

        if (PanelLauncher.isLaunchGroup(PanelLauncherGroupBean.CODE_LEASE_RRR, rrrPanelCode)) {
            // Lease RRR requires additional constructor argument
            PanelLauncher.launch(rrrPanelCode, getMainContentPanel(), new RightFormListener(), null,
                    baUnitBean1, rrrBean, applicationBean, applicationService, action);
        } else {
            PanelLauncher.launch(rrrPanelCode, getMainContentPanel(), new RightFormListener(), null,
                    baUnitBean1, rrrBean, applicationBean, applicationService, action);
        }
    }

    private void saveBaUnit(final boolean showMessage, final boolean closeOnSave) {

        if (baUnitBean1.validate(true).size() > 0) {
            return;
        }

        if (!baUnitBean1.isValid()) {
            return;
        }

        if (txtSLArea.isEditable()) {

            if (baUnitAreaBean1 == null) {
                return;
            } else {
                java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle");

                if (baUnitAreaBean1.getSize() == null) {
                    MessageUtility.displayMessage(ClientMessage.CHECK_BAUNITAREA_VALUE,
                            new Object[]{bundle.getString("PropertyPanel.labArea.text")});
                    txtSLArea.setText(null);
                    txtSLArea.requestFocus();
                    return;
                }

                baUnitAreaBean1.setTypeCode("officialArea");
                baUnitAreaBean1.setBaUnitId(baUnitBean1.getId());
            }
        }

        SolaTask<Void, Void> t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SAVING));

                if (baUnitID != null && !baUnitID.equals("")) {
                    baUnitBean1.saveBaUnit(applicationService.getId());
                } else {
                    baUnitBean1.createBaUnit(applicationService.getId());
                }
                if (closeOnSave) {
                    close();
                }

                if ((!txtSLArea.getText().equals(null)) && txtSLArea.getText() != "" && (!txtSLArea.getText().isEmpty())) {
                    baUnitAreaBean1.createBaUnitArea(baUnitBean1.getId());
                }
                return null;
            }

            @Override
            public void taskDone() {
                if (showMessage) {
                    MessageUtility.displayMessage(ClientMessage.BAUNIT_SAVED);
                }
                saveBaUnitState();

            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void saveBaUnit() {
        if (baUnitBean1.validate(true).size() > 0) {
            return;
        }

        if (baUnitID != null && !baUnitID.equals("")) {
            baUnitBean1.saveBaUnit(applicationService.getId());
        } else {
            baUnitBean1.createBaUnit(applicationService.getId());
        }
        saveBaUnitState();
    }

    private String getTerminateMessage() {
        String whichMessage = ClientMessage.BAUNIT_CONFIRM_TERMINATION;
        for (int i = 0; i < baUnitBean1.getRrrFilteredList().size(); i++) {
            if (baUnitBean1.getRrrFilteredList().get(i).getStatus().getCode().equals(StatusConstants.CURRENT)) {
                whichMessage = ClientMessage.BAUNIT_CURRENT_RRR_EXIST_CONFIRM_TERMINATION;
                return whichMessage;
            }
        }

        return whichMessage;
    }

    private void terminateBaUnit() {
        if (baUnitBean1.getPendingActionCode() != null && baUnitBean1.getPendingActionCode().equals(TypeActionBean.CODE_CANCEL)) {
            saveBaUnit();
            baUnitBean1.cancelBaUnitTermination();
            MessageUtility.displayMessage(ClientMessage.BAUNIT_TERMINATION_CANCELED);
            customizeForm();
            saveBaUnitState();
        } else {
            String whichMessage = getTerminateMessage();
            if (MessageUtility.displayMessage(whichMessage) == MessageUtility.BUTTON_ONE) {
                saveBaUnit();
                baUnitBean1.terminateBaUnit(applicationService.getId());
                MessageUtility.displayMessage(ClientMessage.BAUNIT_TERMINATED);
                customizeForm();
                saveBaUnitState();
            }
        }
    }

    /**
     * Opens property form in read only mode for a given BaUnit.
     */
    private void openPropertyForm(final RelatedBaUnitInfoBean relatedBaUnit) {
        if (relatedBaUnit != null && relatedBaUnit.getRelatedBaUnit() != null) {
            SolaTask t = new SolaTask<Void, Void>() {

                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTY));
                    SLPropertyPanel propertyPnl = new SLPropertyPanel(
                            relatedBaUnit.getRelatedBaUnit().getNameFirstpart(),
                            relatedBaUnit.getRelatedBaUnit().getNameLastpart(), true);
                    getMainContentPanel().addPanel(propertyPnl,
                            MainContentPanel.CARD_PROPERTY_PANEL + "_"
                            + relatedBaUnit.getRelatedBaUnit().getId(), true);
                    return null;
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    private void saveBaUnitState() {
        MainForm.saveBeanState(baUnitBean1);
    }

    private void openApplicationParcelsForm() {
        CadastreObjectsDialog form = new CadastreObjectsDialog(
                applicationBean.getCadastreObjectFilteredList(), MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(CadastreObjectsDialog.SELECT_CADASTRE_OBJECT)) {
                    baUnitBean1.getCadastreObjectList().addAsNew((CadastreObjectBean) evt.getNewValue());
                }
            }
        });
        form.setVisible(true);
    }

    @Override
    protected boolean panelClosing() {
        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(baUnitBean1)) {
            saveBaUnit(true, true);
            return false;
        }
        return true;
    }

    private void assignProperty() {
        PropertyAssignmentDialog form = new PropertyAssignmentDialog(baUnitBean1, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.setVisible(true);
    }

    private void configureSecurity() {
        SecurityClassificationDialog form = new SecurityClassificationDialog(baUnitBean1, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        if (!btnSave.isEnabled()) {
            form.setSaveChanges(EntityTable.BAUNIT);
        }
        form.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        baUnitBean1 = createBaUnitBean();
        baUnitRrrTypes = new org.sola.clients.beans.referencedata.RrrTypeListBean();
        rrrTypes = new org.sola.clients.beans.referencedata.RrrTypeListBean();
        popupParcels = new javax.swing.JPopupMenu();
        menuViewParcel = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        menuAddParcel = new javax.swing.JMenuItem();
        menuEditParcel = new javax.swing.JMenuItem();
        menuRemoveParcel = new javax.swing.JMenuItem();
        popupRights = new javax.swing.JPopupMenu();
        menuVaryRight = new javax.swing.JMenuItem();
        menuExtinguishRight = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuViewRight = new javax.swing.JMenuItem();
        menuEditRight = new javax.swing.JMenuItem();
        menuRemoveRight = new javax.swing.JMenuItem();
        popupNotations = new javax.swing.JPopupMenu();
        menuViewNotation = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        menuAddNotation = new javax.swing.JMenuItem();
        menuEditNotation = new javax.swing.JMenuItem();
        menuRemoveNotation = new javax.swing.JMenuItem();
        popupParentBaUnits = new javax.swing.JPopupMenu();
        menuViewParentBaUnit = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuAddParentBaUnit = new javax.swing.JMenuItem();
        menuRemoveParentBaUnit = new javax.swing.JMenuItem();
        popupChildBaUnits = new javax.swing.JPopupMenu();
        menuViewChildBaUnit = new javax.swing.JMenuItem();
        baUnitAreaBean1 = createBaUnitAreaBean();
        popupPropertyDocuments = new javax.swing.JPopupMenu();
        menuViewPropertyDocument = new javax.swing.JMenuItem();
        propertyManagerList = createPartySummaryList();
        jToolBar5 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnAssign = new javax.swing.JButton();
        btnSecurity = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnTerminate = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnPrintBaUnit = new javax.swing.JButton();
        tabsMain = new javax.swing.JTabbedPane();
        pnlSLGeneral = new javax.swing.JPanel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtSLLastPart = new javax.swing.JTextField();
        areaPanel = new javax.swing.JPanel();
        labArea = new javax.swing.JLabel();
        txtSLArea = new javax.swing.JFormattedTextField();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtSLLandUse = new javax.swing.JTextField();
        jPanel23 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtPropertyManager = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtSLStatus = new javax.swing.JTextField();
        jPanel27 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        documentsPanel1 = createDocumentsPanel();
        jPanel25 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtSLDescription = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        pnlGeneral = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtPropFirstpart = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtPropLastpart = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtPropArea = new javax.swing.JFormattedTextField();
        jPanel20 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtPropStatus = new javax.swing.JTextField();
        jPanel21 = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnViewPropDoc = new org.sola.clients.swing.common.buttons.BtnView();
        propertyDocsPanel = createPropertyDocsPanel();
        jPanel24 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        txtPropDescription = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        groupPanel5 = new org.sola.clients.swing.ui.GroupPanel();
        pnlNotes = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableNotes = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar3 = new javax.swing.JToolBar();
        btnViewNotation = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnAddNotation = new javax.swing.JButton();
        btnEditNotation = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemoveNotation = new javax.swing.JButton();
        pnlParcels = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableParcels = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnViewParcel = new org.sola.clients.swing.common.buttons.BtnView();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        btnAddParcel = new javax.swing.JButton();
        btnEditParcel = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemoveParcel = new javax.swing.JButton();
        pnlInterests = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableRights = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar2 = new javax.swing.JToolBar();
        btnViewRight = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jLabel16 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        cbxRightType = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        btnCreateRight = new javax.swing.JButton();
        btnChangeRight = new javax.swing.JButton();
        btnExtinguish = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnEditRight = new javax.swing.JButton();
        btnRemoveRight = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jToolBar8 = new javax.swing.JToolBar();
        btnViewHistoricRight = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        tableRightsHistory = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        pnlOwnership = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tableOwnership = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        pnlNotations = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTableWithDefaultStyles1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        pnlRelationships = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jToolBar6 = new javax.swing.JToolBar();
        btnViewParent = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnAddParent = new javax.swing.JButton();
        btnRemoveParent = new javax.swing.JButton();
        groupPanel4 = new org.sola.clients.swing.ui.GroupPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tableParentBaUnits = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel5 = new javax.swing.JPanel();
        jToolBar7 = new javax.swing.JToolBar();
        btnViewChild = new javax.swing.JButton();
        groupPanel3 = new org.sola.clients.swing.ui.GroupPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableChildBaUnits = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        pnlValuations = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jToolBar10 = new javax.swing.JToolBar();
        jButton2 = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        tblValuations = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        pnlMap = new javax.swing.JPanel();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();

        popupParcels.setName("popupParcels"); // NOI18N

        menuViewParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        menuViewParcel.setText(bundle.getString("SLPropertyPanel.menuViewParcel.text")); // NOI18N
        menuViewParcel.setName("menuViewParcel"); // NOI18N
        menuViewParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewParcelActionPerformed(evt);
            }
        });
        popupParcels.add(menuViewParcel);

        jSeparator11.setName("jSeparator11"); // NOI18N
        popupParcels.add(jSeparator11);

        menuAddParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAddParcel.setText(bundle.getString("SLPropertyPanel.menuAddParcel.text")); // NOI18N
        menuAddParcel.setName("menuAddParcel"); // NOI18N
        menuAddParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddParcelActionPerformed(evt);
            }
        });
        popupParcels.add(menuAddParcel);

        menuEditParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditParcel.setText(bundle.getString("SLPropertyPanel.menuEditParcel.text")); // NOI18N
        menuEditParcel.setName("menuEditParcel"); // NOI18N
        menuEditParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditParcelActionPerformed(evt);
            }
        });
        popupParcels.add(menuEditParcel);

        menuRemoveParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveParcel.setText(bundle.getString("PropertyPanel.btnRemoveParcel.text")); // NOI18N
        menuRemoveParcel.setToolTipText(bundle.getString("SLPropertyPanel.menuRemoveParcel.toolTipText")); // NOI18N
        menuRemoveParcel.setName("menuRemoveParcel"); // NOI18N
        menuRemoveParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveParcelActionPerformed(evt);
            }
        });
        popupParcels.add(menuRemoveParcel);

        popupRights.setName("popupRights"); // NOI18N

        menuVaryRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/vary.png"))); // NOI18N
        menuVaryRight.setText(bundle.getString("SLPropertyPanel.menuVaryRight.text")); // NOI18N
        menuVaryRight.setName("menuVaryRight"); // NOI18N
        menuVaryRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuVaryRightActionPerformed(evt);
            }
        });
        popupRights.add(menuVaryRight);

        menuExtinguishRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/flag.png"))); // NOI18N
        menuExtinguishRight.setText(bundle.getString("SLPropertyPanel.menuExtinguishRight.text")); // NOI18N
        menuExtinguishRight.setToolTipText(bundle.getString("SLPropertyPanel.menuExtinguishRight.toolTipText")); // NOI18N
        menuExtinguishRight.setName("menuExtinguishRight"); // NOI18N
        menuExtinguishRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExtinguishRightActionPerformed(evt);
            }
        });
        popupRights.add(menuExtinguishRight);

        jSeparator5.setName("jSeparator5"); // NOI18N
        popupRights.add(jSeparator5);

        menuViewRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewRight.setText(bundle.getString("SLPropertyPanel.menuViewRight.text")); // NOI18N
        menuViewRight.setName("menuViewRight"); // NOI18N
        popupRights.add(menuViewRight);

        menuEditRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/document--pencil.png"))); // NOI18N
        menuEditRight.setText(bundle.getString("SLPropertyPanel.menuEditRight.text")); // NOI18N
        menuEditRight.setName("menuEditRight"); // NOI18N
        menuEditRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditRightActionPerformed(evt);
            }
        });
        popupRights.add(menuEditRight);

        menuRemoveRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveRight.setText(bundle.getString("SLPropertyPanel.menuRemoveRight.text")); // NOI18N
        menuRemoveRight.setName("menuRemoveRight"); // NOI18N
        menuRemoveRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveRightActionPerformed(evt);
            }
        });
        popupRights.add(menuRemoveRight);

        popupNotations.setName("popupNotations"); // NOI18N

        menuViewNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewNotation.setText(bundle.getString("SLPropertyPanel.menuViewNotation.text")); // NOI18N
        menuViewNotation.setName("menuViewNotation"); // NOI18N
        menuViewNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewNotationActionPerformed(evt);
            }
        });
        popupNotations.add(menuViewNotation);

        jSeparator8.setName("jSeparator8"); // NOI18N
        popupNotations.add(jSeparator8);

        menuAddNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAddNotation.setText(bundle.getString("SLPropertyPanel.menuAddNotation.text")); // NOI18N
        menuAddNotation.setName("menuAddNotation"); // NOI18N
        menuAddNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddNotationActionPerformed(evt);
            }
        });
        popupNotations.add(menuAddNotation);

        menuEditNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditNotation.setText(bundle.getString("SLPropertyPanel.menuEditNotation.text")); // NOI18N
        menuEditNotation.setName("menuEditNotation"); // NOI18N
        menuEditNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditNotationActionPerformed(evt);
            }
        });
        popupNotations.add(menuEditNotation);

        menuRemoveNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveNotation.setText(bundle.getString("SLPropertyPanel.menuRemoveNotation.text")); // NOI18N
        menuRemoveNotation.setName("menuRemoveNotation"); // NOI18N
        menuRemoveNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveNotationActionPerformed(evt);
            }
        });
        popupNotations.add(menuRemoveNotation);

        popupParentBaUnits.setName("popupParentBaUnits"); // NOI18N

        menuViewParentBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewParentBaUnit.setText(bundle.getString("SLPropertyPanel.menuViewParentBaUnit.text")); // NOI18N
        menuViewParentBaUnit.setName("menuViewParentBaUnit"); // NOI18N
        menuViewParentBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewParentBaUnitActionPerformed(evt);
            }
        });
        popupParentBaUnits.add(menuViewParentBaUnit);

        jSeparator2.setName("jSeparator2"); // NOI18N
        popupParentBaUnits.add(jSeparator2);

        menuAddParentBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAddParentBaUnit.setText(bundle.getString("SLPropertyPanel.menuAddParentBaUnit.text")); // NOI18N
        menuAddParentBaUnit.setName("menuAddParentBaUnit"); // NOI18N
        menuAddParentBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddParentBaUnitActionPerformed(evt);
            }
        });
        popupParentBaUnits.add(menuAddParentBaUnit);

        menuRemoveParentBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveParentBaUnit.setText(bundle.getString("SLPropertyPanel.menuRemoveParentBaUnit.text")); // NOI18N
        menuRemoveParentBaUnit.setName("menuRemoveParentBaUnit"); // NOI18N
        menuRemoveParentBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveParentBaUnitActionPerformed(evt);
            }
        });
        popupParentBaUnits.add(menuRemoveParentBaUnit);

        popupChildBaUnits.setName("popupChildBaUnits"); // NOI18N

        menuViewChildBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewChildBaUnit.setText(bundle.getString("SLPropertyPanel.menuViewChildBaUnit.text")); // NOI18N
        menuViewChildBaUnit.setName("menuViewChildBaUnit"); // NOI18N
        menuViewChildBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewChildBaUnitActionPerformed(evt);
            }
        });
        popupChildBaUnits.add(menuViewChildBaUnit);

        popupPropertyDocuments.setName("popupPropertyDocuments"); // NOI18N

        menuViewPropertyDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewPropertyDocument.setText(bundle.getString("SLPropertyPanel.menuViewPropertyDocument.text")); // NOI18N
        menuViewPropertyDocument.setName("menuViewPropertyDocument"); // NOI18N
        menuViewPropertyDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewPropertyDocumentActionPerformed(evt);
            }
        });
        popupPropertyDocuments.add(menuViewPropertyDocument);

        setHeaderPanel(headerPanel);
        setHelpTopic(bundle.getString("SLPropertyPanel.helpTopic")); // NOI18N
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);
        jToolBar5.setName("jToolBar5"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("SLPropertyPanel.btnSave.text")); // NOI18N
        btnSave.setToolTipText(bundle.getString("SLPropertyPanel.btnSave.toolTipText")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar5.add(btnSave);

        btnAssign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        btnAssign.setText(bundle.getString("SLPropertyPanel.btnAssign.text")); // NOI18N
        btnAssign.setFocusable(false);
        btnAssign.setName("btnAssign"); // NOI18N
        btnAssign.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignActionPerformed(evt);
            }
        });
        jToolBar5.add(btnAssign);

        btnSecurity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lock.png"))); // NOI18N
        btnSecurity.setText(bundle.getString("SLPropertyPanel.btnSecurity.text")); // NOI18N
        btnSecurity.setFocusable(false);
        btnSecurity.setName("btnSecurity"); // NOI18N
        btnSecurity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSecurityActionPerformed(evt);
            }
        });
        jToolBar5.add(btnSecurity);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar5.add(jSeparator6);

        btnTerminate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/stop.png"))); // NOI18N
        btnTerminate.setText(bundle.getString("SLPropertyPanel.btnTerminate.text")); // NOI18N
        btnTerminate.setFocusable(false);
        btnTerminate.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnTerminate.setName("btnTerminate"); // NOI18N
        btnTerminate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTerminate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTerminateActionPerformed(evt);
            }
        });
        jToolBar5.add(btnTerminate);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jToolBar5.add(jSeparator4);

        btnPrintBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintBaUnit.setText(bundle.getString("SLPropertyPanel.btnPrintBaUnit.text")); // NOI18N
        btnPrintBaUnit.setFocusable(false);
        btnPrintBaUnit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPrintBaUnit.setName("btnPrintBaUnit"); // NOI18N
        btnPrintBaUnit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintBaUnitActionPerformed(evt);
            }
        });
        jToolBar5.add(btnPrintBaUnit);

        tabsMain.setName("tabsMain"); // NOI18N
        tabsMain.setPreferredSize(new java.awt.Dimension(494, 300));

        pnlSLGeneral.setName("pnlSLGeneral"); // NOI18N

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("SLPropertyPanel.groupPanel1.titleText")); // NOI18N

        jPanel13.setName("jPanel13"); // NOI18N
        jPanel13.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridLayout(2, 2, 15, 0));

        jPanel9.setName("jPanel9"); // NOI18N

        jLabel2.setText(bundle.getString("SLPropertyPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtSLLastPart.setEditable(false);
        txtSLLastPart.setName("txtSLLastPart"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${displayName}"), txtSLLastPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(txtSLLastPart)
            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtSLLastPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel9);

        areaPanel.setName(bundle.getString("PropertyPanel.areaPanel.name_1")); // NOI18N

        labArea.setText(bundle.getString("SLPropertyPanel.labArea.text")); // NOI18N
        labArea.setName(bundle.getString("PropertyPanel.labArea.name")); // NOI18N

        txtSLArea.setFormatterFactory(FormattersFactory.getInstance().getMetricAreaFormatterFactory());
        txtSLArea.setText(bundle.getString("SLPropertyPanel.txtSLArea.text")); // NOI18N
        txtSLArea.setName("txtSLArea"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitAreaBean1, org.jdesktop.beansbinding.ELProperty.create("${size}"), txtSLArea, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout areaPanelLayout = new org.jdesktop.layout.GroupLayout(areaPanel);
        areaPanel.setLayout(areaPanelLayout);
        areaPanelLayout.setHorizontalGroup(
            areaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, labArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(txtSLArea)
        );
        areaPanelLayout.setVerticalGroup(
            areaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(areaPanelLayout.createSequentialGroup()
                .add(labArea)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtSLArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(areaPanel);

        jPanel11.setName("jPanel11"); // NOI18N

        jLabel4.setText(bundle.getString("SLPropertyPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        txtSLLandUse.setEditable(false);
        txtSLLandUse.setName("txtSLLandUse"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${landUseType.displayValue}"), txtSLLandUse, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(txtSLLandUse)
            .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtSLLandUse, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel11);

        jPanel23.setName("jPanel23"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel23Layout = new org.jdesktop.layout.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 140, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 51, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel23);

        jPanel13.add(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridLayout(2, 2, 15, 0));

        jPanel22.setName("jPanel22"); // NOI18N

        jLabel10.setText(bundle.getString("SLPropertyPanel.jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        txtPropertyManager.setEnabled(false);
        txtPropertyManager.setName("txtPropertyManager"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${propertyManager.fullName}"), txtPropertyManager, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel22Layout = new org.jdesktop.layout.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(txtPropertyManager)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel22Layout.createSequentialGroup()
                .add(jLabel10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(txtPropertyManager, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.add(jPanel22);

        jPanel6.setName("jPanel6"); // NOI18N

        jLabel7.setText(bundle.getString("SLPropertyPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtSLStatus.setEditable(false);
        txtSLStatus.setName("txtSLStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${stateLandStatus.displayValue}"), txtSLStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(txtSLStatus)
            .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtSLStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel6);

        jPanel27.setName("jPanel27"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel27Layout = new org.jdesktop.layout.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 140, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 51, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel27);

        jPanel26.setName("jPanel26"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel26Layout = new org.jdesktop.layout.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 140, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 51, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel26);

        jPanel13.add(jPanel2);

        documentsPanel1.setName("documentsPanel1"); // NOI18N

        jPanel25.setName("jPanel25"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        txtSLDescription.setColumns(20);
        txtSLDescription.setLineWrap(true);
        txtSLDescription.setRows(5);
        txtSLDescription.setName("txtSLDescription"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${description}"), txtSLDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane5.setViewportView(txtSLDescription);

        jLabel5.setText(bundle.getString("SLPropertyPanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel25Layout = new org.jdesktop.layout.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane5)
            .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel25Layout.createSequentialGroup()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 63, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout pnlSLGeneralLayout = new org.jdesktop.layout.GroupLayout(pnlSLGeneral);
        pnlSLGeneral.setLayout(pnlSLGeneralLayout);
        pnlSLGeneralLayout.setHorizontalGroup(
            pnlSLGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSLGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlSLGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, documentsPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .add(groupPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                    .add(jPanel25, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlSLGeneralLayout.setVerticalGroup(
            pnlSLGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSLGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(groupPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(documentsPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlSLGeneral.TabConstraints.tabTitle"), pnlSLGeneral); // NOI18N

        pnlGeneral.setName("pnlGeneral"); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel10.setName("jPanel10"); // NOI18N

        jLabel3.setText(bundle.getString("SLPropertyPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtPropFirstpart.setEnabled(false);
        txtPropFirstpart.setName("txtPropFirstpart"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"), txtPropFirstpart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(txtPropFirstpart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtPropFirstpart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel10);

        jPanel18.setName("jPanel18"); // NOI18N

        jLabel6.setText(bundle.getString("SLPropertyPanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtPropLastpart.setEnabled(false);
        txtPropLastpart.setName("txtPropLastpart"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"), txtPropLastpart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel18Layout = new org.jdesktop.layout.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(txtPropLastpart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel18Layout.createSequentialGroup()
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtPropLastpart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel18);

        jPanel3.add(jPanel4);

        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel19.setName("jPanel19"); // NOI18N

        jLabel8.setText(bundle.getString("SLPropertyPanel.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        txtPropArea.setFormatterFactory(FormattersFactory.getInstance().getMetricAreaFormatterFactory());
        txtPropArea.setText(bundle.getString("SLPropertyPanel.txtPropArea.text")); // NOI18N
        txtPropArea.setEnabled(false);
        txtPropArea.setName("txtPropArea"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitAreaBean1, org.jdesktop.beansbinding.ELProperty.create("${size}"), txtPropArea, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel19Layout = new org.jdesktop.layout.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
            .add(txtPropArea)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel19Layout.createSequentialGroup()
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtPropArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel19);

        jPanel20.setName("jPanel20"); // NOI18N

        jLabel9.setText(bundle.getString("SLPropertyPanel.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        txtPropStatus.setEnabled(false);
        txtPropStatus.setName("txtPropStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtPropStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel20Layout = new org.jdesktop.layout.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
            .add(txtPropStatus)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel20Layout.createSequentialGroup()
                .add(jLabel9)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtPropStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.add(jPanel20);

        jPanel3.add(jPanel7);

        jPanel21.setName("jPanel21"); // NOI18N

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);
        jToolBar4.setName("jToolBar4"); // NOI18N

        btnViewPropDoc.setName("btnViewPropDoc"); // NOI18N
        btnViewPropDoc.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewPropDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewPropDocActionPerformed(evt);
            }
        });
        jToolBar4.add(btnViewPropDoc);

        propertyDocsPanel.setComponentPopupMenu(popupPropertyDocuments);
        propertyDocsPanel.setName("propertyDocsPanel"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel21Layout = new org.jdesktop.layout.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(propertyDocsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel21Layout.createSequentialGroup()
                .add(jToolBar4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(propertyDocsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 156, Short.MAX_VALUE))
        );

        jPanel24.setName("jPanel24"); // NOI18N

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        txtPropDescription.setColumns(20);
        txtPropDescription.setRows(5);
        txtPropDescription.setEnabled(false);
        txtPropDescription.setName("txtPropDescription"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${description}"), txtPropDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane7.setViewportView(txtPropDescription);

        jLabel1.setText(bundle.getString("SLPropertyPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel24Layout = new org.jdesktop.layout.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane7)
            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel24Layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
        );

        groupPanel5.setName("groupPanel5"); // NOI18N
        groupPanel5.setTitleText(bundle.getString("SLPropertyPanel.groupPanel5.titleText")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlGeneralLayout = new org.jdesktop.layout.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel24, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(groupPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(groupPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlGeneral.TabConstraints.tabTitle"), pnlGeneral); // NOI18N

        pnlNotes.setName("pnlNotes"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tableNotes.setComponentPopupMenu(popupNotations);
        tableNotes.setName("tableNotes"); // NOI18N
        tableNotes.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitFilteredNotationList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableNotes);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${referenceNr}"));
        columnBinding.setColumnName("Reference Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notationText}"));
        columnBinding.setColumnName("Notation Text");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notationDate}"));
        columnBinding.setColumnName("Notation Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${changeUser}"));
        columnBinding.setColumnName("Change User");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedBaUnitNotation}"), tableNotes, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(tableNotes);
        if (tableNotes.getColumnModel().getColumnCount() > 0) {
            tableNotes.getColumnModel().getColumn(0).setPreferredWidth(100);
            tableNotes.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title0")); // NOI18N
            tableNotes.getColumnModel().getColumn(1).setPreferredWidth(400);
            tableNotes.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title1")); // NOI18N
            tableNotes.getColumnModel().getColumn(2).setPreferredWidth(120);
            tableNotes.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title3")); // NOI18N
            tableNotes.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            tableNotes.getColumnModel().getColumn(3).setPreferredWidth(120);
            tableNotes.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLPropertyPanel.tableNotations.columnModel.title4")); // NOI18N
            tableNotes.getColumnModel().getColumn(4).setPreferredWidth(100);
            tableNotes.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title2")); // NOI18N
        }

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        btnViewNotation.setName("btnViewNotation"); // NOI18N
        btnViewNotation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewNotationActionPerformed(evt);
            }
        });
        jToolBar3.add(btnViewNotation);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jToolBar3.add(jSeparator7);

        btnAddNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddNotation.setText(bundle.getString("SLPropertyPanel.btnAddNotation.text")); // NOI18N
        btnAddNotation.setName("btnAddNotation"); // NOI18N
        btnAddNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNotationActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAddNotation);

        btnEditNotation.setName("btnEditNotation"); // NOI18N
        btnEditNotation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditNotationActionPerformed(evt);
            }
        });
        jToolBar3.add(btnEditNotation);

        btnRemoveNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveNotation.setText(bundle.getString("SLPropertyPanel.btnRemoveNotation.text")); // NOI18N
        btnRemoveNotation.setName("btnRemoveNotation"); // NOI18N
        btnRemoveNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveNotationActionPerformed(evt);
            }
        });
        jToolBar3.add(btnRemoveNotation);

        org.jdesktop.layout.GroupLayout pnlNotesLayout = new org.jdesktop.layout.GroupLayout(pnlNotes);
        pnlNotes.setLayout(pnlNotesLayout);
        pnlNotesLayout.setHorizontalGroup(
            pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlNotesLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                    .add(jToolBar3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlNotesLayout.setVerticalGroup(
            pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlNotesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlNotes.TabConstraints.tabTitle"), pnlNotes); // NOI18N

        pnlParcels.setName("pnlParcels"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableParcels.setComponentPopupMenu(popupParcels);
        tableParcels.setName("tableParcels"); // NOI18N
        tableParcels.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cadastreObjectFilteredList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableParcels);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stateLandStatusType.displayValue}"));
        columnBinding.setColumnName("State Land Status Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedParcel}"), tableParcels, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableParcels.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableParcelsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableParcels);
        if (tableParcels.getColumnModel().getColumnCount() > 0) {
            tableParcels.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableParcels.columnModel.title0")); // NOI18N
            tableParcels.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tableParcels.columnModel.title1")); // NOI18N
            tableParcels.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.tableParcels.columnModel.title6")); // NOI18N
            tableParcels.getColumnModel().getColumn(2).setCellRenderer(new AreaCellRenderer());
            tableParcels.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLPropertyPanel.tableParcels.columnModel.title8")); // NOI18N
            tableParcels.getColumnModel().getColumn(4).setPreferredWidth(180);
            tableParcels.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLPropertyPanel.tableParcels.columnModel.title7")); // NOI18N
            tableParcels.getColumnModel().getColumn(4).setCellRenderer(new CellDelimitedListRenderer("; ", false));
            tableParcels.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("SLPropertyPanel.tableParcels.columnModel.title3")); // NOI18N
        }

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnViewParcel.setName("btnViewParcel"); // NOI18N
        btnViewParcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnViewParcel);

        jSeparator9.setName("jSeparator9"); // NOI18N
        jToolBar1.add(jSeparator9);

        btnAddParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddParcel.setText(bundle.getString("SLPropertyPanel.btnAddParcel.text")); // NOI18N
        btnAddParcel.setName("btnAddParcel"); // NOI18N
        btnAddParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddParcel);

        btnEditParcel.setName("btnEditParcel"); // NOI18N
        btnEditParcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEditParcel);

        btnRemoveParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveParcel.setText(bundle.getString("SLPropertyPanel.btnRemoveParcel.text")); // NOI18N
        btnRemoveParcel.setToolTipText(bundle.getString("SLPropertyPanel.btnRemoveParcel.toolTipText")); // NOI18N
        btnRemoveParcel.setName("btnRemoveParcel"); // NOI18N
        btnRemoveParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveParcel);

        org.jdesktop.layout.GroupLayout pnlParcelsLayout = new org.jdesktop.layout.GroupLayout(pnlParcels);
        pnlParcels.setLayout(pnlParcelsLayout);
        pnlParcelsLayout.setHorizontalGroup(
            pnlParcelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlParcelsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlParcelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlParcelsLayout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(pnlParcelsLayout.createSequentialGroup()
                        .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                        .add(20, 20, 20))))
        );
        pnlParcelsLayout.setVerticalGroup(
            pnlParcelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlParcelsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlParcels.TabConstraints.tabTitle"), pnlParcels); // NOI18N

        pnlInterests.setName("pnlInterests"); // NOI18N

        jPanel16.setName(bundle.getString("PropertyPanel.jPanel16.name")); // NOI18N
        jPanel16.setLayout(new java.awt.GridLayout(2, 1, 0, 10));

        jPanel15.setName(bundle.getString("PropertyPanel.jPanel15.name_3")); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tableRights.setComponentPopupMenu(popupRights);
        tableRights.setName("tableRights"); // NOI18N
        tableRights.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrFilteredList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableRights);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rrrType.displayValue}"));
        columnBinding.setColumnName("Rrr Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${referenceNum}"));
        columnBinding.setColumnName("Reference Num");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"));
        columnBinding.setColumnName("Registration Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${concatenatedName}"));
        columnBinding.setColumnName("Concatenated Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedRight}"), tableRights, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableRights.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableRightsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableRights);
        if (tableRights.getColumnModel().getColumnCount() > 0) {
            tableRights.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableRights.columnModel.title0")); // NOI18N
            tableRights.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tableRights.columnModel.title3")); // NOI18N
            tableRights.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.tableRights.columnModel.title1")); // NOI18N
            tableRights.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            tableRights.getColumnModel().getColumn(3).setPreferredWidth(360);
            tableRights.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLPropertyPanel.tableRights.columnModel.title4_1")); // NOI18N
            tableRights.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLPropertyPanel.tableRights.columnModel.title2")); // NOI18N
        }

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnViewRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewRight.setText(bundle.getString("SLPropertyPanel.btnViewRight.text")); // NOI18N
        btnViewRight.setFocusable(false);
        btnViewRight.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewRight.setName("btnViewRight"); // NOI18N
        btnViewRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewRightActionPerformed(evt);
            }
        });
        jToolBar2.add(btnViewRight);

        jSeparator10.setName("jSeparator10"); // NOI18N
        jToolBar2.add(jSeparator10);

        jLabel16.setText(bundle.getString("SLPropertyPanel.jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        jToolBar2.add(jLabel16);

        filler1.setName("filler1"); // NOI18N
        jToolBar2.add(filler1);

        cbxRightType.setLightWeightPopupEnabled(false);
        cbxRightType.setMaximumSize(new java.awt.Dimension(170, 20));
        cbxRightType.setName("cbxRightType"); // NOI18N
        cbxRightType.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrTypeBeanList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrTypes, eLProperty, cbxRightType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrTypes, org.jdesktop.beansbinding.ELProperty.create("${selectedRrrType}"), cbxRightType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jToolBar2.add(cbxRightType);

        filler2.setName("filler2"); // NOI18N
        jToolBar2.add(filler2);

        btnCreateRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/create.png"))); // NOI18N
        btnCreateRight.setText(bundle.getString("SLPropertyPanel.btnCreateRight.text")); // NOI18N
        btnCreateRight.setName("btnCreateRight"); // NOI18N
        btnCreateRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateRightActionPerformed(evt);
            }
        });
        jToolBar2.add(btnCreateRight);

        btnChangeRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/vary.png"))); // NOI18N
        btnChangeRight.setText(bundle.getString("SLPropertyPanel.btnChangeRight.text")); // NOI18N
        btnChangeRight.setName("btnChangeRight"); // NOI18N
        btnChangeRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeRightActionPerformed(evt);
            }
        });
        jToolBar2.add(btnChangeRight);

        btnExtinguish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/flag.png"))); // NOI18N
        btnExtinguish.setText(bundle.getString("SLPropertyPanel.btnExtinguish.text")); // NOI18N
        btnExtinguish.setToolTipText(bundle.getString("SLPropertyPanel.btnExtinguish.toolTipText")); // NOI18N
        btnExtinguish.setName("btnExtinguish"); // NOI18N
        btnExtinguish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExtinguishActionPerformed(evt);
            }
        });
        jToolBar2.add(btnExtinguish);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar2.add(jSeparator1);

        btnEditRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditRight.setText(bundle.getString("SLPropertyPanel.btnEditRight.text")); // NOI18N
        btnEditRight.setName("btnEditRight"); // NOI18N
        btnEditRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditRightActionPerformed(evt);
            }
        });
        jToolBar2.add(btnEditRight);

        btnRemoveRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveRight.setText(bundle.getString("SLPropertyPanel.btnRemoveRight.text")); // NOI18N
        btnRemoveRight.setName("btnRemoveRight"); // NOI18N
        btnRemoveRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveRightActionPerformed(evt);
            }
        });
        jToolBar2.add(btnRemoveRight);

        org.jdesktop.layout.GroupLayout jPanel15Layout = new org.jdesktop.layout.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
            .add(jToolBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel15Layout.createSequentialGroup()
                .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
        );

        jPanel16.add(jPanel15);

        jPanel17.setName(bundle.getString("PropertyPanel.jPanel17.name")); // NOI18N

        jToolBar8.setFloatable(false);
        jToolBar8.setRollover(true);
        jToolBar8.setName(bundle.getString("PropertyPanel.jToolBar8.name")); // NOI18N

        btnViewHistoricRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewHistoricRight.setText(bundle.getString("SLPropertyPanel.btnViewHistoricRight.text")); // NOI18N
        btnViewHistoricRight.setFocusable(false);
        btnViewHistoricRight.setName(bundle.getString("PropertyPanel.btnViewHistoricRight.name")); // NOI18N
        btnViewHistoricRight.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewHistoricRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewHistoricRightActionPerformed(evt);
            }
        });
        jToolBar8.add(btnViewHistoricRight);

        jScrollPane8.setName(bundle.getString("PropertyPanel.jScrollPane8.name")); // NOI18N

        tableRightsHistory.setName(bundle.getString("PropertyPanel.tableRightsHistory.name")); // NOI18N
        tableRightsHistory.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrHistoricList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableRightsHistory);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rrrType.displayValue}"));
        columnBinding.setColumnName("Rrr Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${referenceNum}"));
        columnBinding.setColumnName("Reference Num");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"));
        columnBinding.setColumnName("Registration Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${concatenatedName}"));
        columnBinding.setColumnName("Concatenated Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedHistoricRight}"), tableRightsHistory, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableRightsHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableRightsHistoryMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(tableRightsHistory);
        if (tableRightsHistory.getColumnModel().getColumnCount() > 0) {
            tableRightsHistory.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableRightsHistory.columnModel.title0")); // NOI18N
            tableRightsHistory.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tableRightsHistory.columnModel.title3")); // NOI18N
            tableRightsHistory.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.tableRightsHistory.columnModel.title1")); // NOI18N
            tableRightsHistory.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            tableRightsHistory.getColumnModel().getColumn(3).setPreferredWidth(360);
            tableRightsHistory.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLPropertyPanel.tableRightsHistory.columnModel.title4")); // NOI18N
            tableRightsHistory.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLPropertyPanel.tableRightsHistory.columnModel.title2")); // NOI18N
        }

        groupPanel2.setName("groupPanel2"); // NOI18N
        groupPanel2.setTitleText(bundle.getString("SLPropertyPanel.groupPanel2.titleText")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel17Layout = new org.jdesktop.layout.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
            .add(groupPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17Layout.createSequentialGroup()
                .add(groupPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
        );

        jPanel16.add(jPanel17);

        org.jdesktop.layout.GroupLayout pnlInterestsLayout = new org.jdesktop.layout.GroupLayout(pnlInterests);
        pnlInterests.setLayout(pnlInterestsLayout);
        pnlInterestsLayout.setHorizontalGroup(
            pnlInterestsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlInterestsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlInterestsLayout.setVerticalGroup(
            pnlInterestsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlInterestsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel16, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlInterests.TabConstraints.tabTitle"), pnlInterests); // NOI18N

        pnlOwnership.setName("pnlOwnership"); // NOI18N

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        tableOwnership.setName("tableOwnership"); // NOI18N
        tableOwnership.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrSharesList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableOwnership);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rrrShare.rightHolderList}"));
        columnBinding.setColumnName("Rrr Share.right Holder List");
        columnBinding.setColumnClass(org.sola.clients.beans.controls.SolaList.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rrrShare.share}"));
        columnBinding.setColumnName("Rrr Share.share");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status}"));
        columnBinding.setColumnName("Status");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane9.setViewportView(tableOwnership);
        if (tableOwnership.getColumnModel().getColumnCount() > 0) {
            tableOwnership.getColumnModel().getColumn(0).setPreferredWidth(360);
            tableOwnership.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableOwnership.columnModel.title0")); // NOI18N
            tableOwnership.getColumnModel().getColumn(0).setCellRenderer(new TableCellListRenderer("getName", "getLastName"));
            tableOwnership.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tableOwnership.columnModel.title2")); // NOI18N
            tableOwnership.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.tableOwnership.columnModel.title1")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout pnlOwnershipLayout = new org.jdesktop.layout.GroupLayout(pnlOwnership);
        pnlOwnership.setLayout(pnlOwnershipLayout);
        pnlOwnershipLayout.setHorizontalGroup(
            pnlOwnershipLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlOwnershipLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlOwnershipLayout.setVerticalGroup(
            pnlOwnershipLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlOwnershipLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlOwnership.TabConstraints.tabTitle"), pnlOwnership); // NOI18N

        pnlNotations.setName("pnlNotations"); // NOI18N

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        jTableWithDefaultStyles1.setName("jTableWithDefaultStyles1"); // NOI18N
        jTableWithDefaultStyles1.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${allBaUnitNotationList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, jTableWithDefaultStyles1);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${referenceNr}"));
        columnBinding.setColumnName("Reference Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notationText}"));
        columnBinding.setColumnName("Notation Text");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${changeTime}"));
        columnBinding.setColumnName("Change Time");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane10.setViewportView(jTableWithDefaultStyles1);
        if (jTableWithDefaultStyles1.getColumnModel().getColumnCount() > 0) {
            jTableWithDefaultStyles1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.jTableWithDefaultStyles1.columnModel.title0")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(1).setPreferredWidth(360);
            jTableWithDefaultStyles1.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.jTableWithDefaultStyles1.columnModel.title1")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.jTableWithDefaultStyles1.columnModel.title2")); // NOI18N
            jTableWithDefaultStyles1.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            jTableWithDefaultStyles1.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLPropertyPanel.jTableWithDefaultStyles1.columnModel.title3")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout pnlNotationsLayout = new org.jdesktop.layout.GroupLayout(pnlNotations);
        pnlNotations.setLayout(pnlNotationsLayout);
        pnlNotationsLayout.setHorizontalGroup(
            pnlNotationsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlNotationsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlNotationsLayout.setVerticalGroup(
            pnlNotationsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlNotationsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlNotations.TabConstraints.tabTitle"), pnlNotations); // NOI18N

        pnlRelationships.setName("pnlRelationships"); // NOI18N

        jPanel14.setName("jPanel14"); // NOI18N
        jPanel14.setLayout(new java.awt.GridLayout(2, 1, 15, 6));

        jPanel8.setName("jPanel8"); // NOI18N

        jToolBar6.setFloatable(false);
        jToolBar6.setRollover(true);
        jToolBar6.setName("jToolBar6"); // NOI18N

        btnViewParent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewParent.setText(bundle.getString("SLPropertyPanel.btnViewParent.text")); // NOI18N
        btnViewParent.setToolTipText(bundle.getString("SLPropertyPanel.btnViewParent.toolTipText")); // NOI18N
        btnViewParent.setFocusable(false);
        btnViewParent.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewParent.setName("btnViewParent"); // NOI18N
        btnViewParent.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewParentActionPerformed(evt);
            }
        });
        jToolBar6.add(btnViewParent);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar6.add(jSeparator3);

        btnAddParent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddParent.setText(bundle.getString("SLPropertyPanel.btnAddParent.text")); // NOI18N
        btnAddParent.setFocusable(false);
        btnAddParent.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddParent.setName("btnAddParent"); // NOI18N
        btnAddParent.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddParentActionPerformed(evt);
            }
        });
        jToolBar6.add(btnAddParent);

        btnRemoveParent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveParent.setText(bundle.getString("SLPropertyPanel.btnRemoveParent.text")); // NOI18N
        btnRemoveParent.setFocusable(false);
        btnRemoveParent.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveParent.setName("btnRemoveParent"); // NOI18N
        btnRemoveParent.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveParentActionPerformed(evt);
            }
        });
        jToolBar6.add(btnRemoveParent);

        groupPanel4.setName("groupPanel4"); // NOI18N
        groupPanel4.setTitleText(bundle.getString("SLPropertyPanel.groupPanel4.titleText")); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        tableParentBaUnits.setComponentPopupMenu(popupParentBaUnits);
        tableParentBaUnits.setName("tableParentBaUnits"); // NOI18N
        tableParentBaUnits.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredParentBaUnits}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableParentBaUnits);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.displayName}"));
        columnBinding.setColumnName("Related Ba Unit.display Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.baUnitType.displayValue}"));
        columnBinding.setColumnName("Related Ba Unit.ba Unit Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baUnitRelType.displayValue}"));
        columnBinding.setColumnName("Ba Unit Rel Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.description}"));
        columnBinding.setColumnName("Related Ba Unit.description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.status.displayValue}"));
        columnBinding.setColumnName("Related Ba Unit.status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedParentBaUnit}"), tableParentBaUnits, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableParentBaUnits.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableParentBaUnitsMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tableParentBaUnits);
        if (tableParentBaUnits.getColumnModel().getColumnCount() > 0) {
            tableParentBaUnits.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableParentBaUnits.columnModel.title2")); // NOI18N
            tableParentBaUnits.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tableParentBaUnits.columnModel.title1_1")); // NOI18N
            tableParentBaUnits.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.tableParentBaUnits.columnModel.title0_1")); // NOI18N
            tableParentBaUnits.getColumnModel().getColumn(3).setPreferredWidth(260);
            tableParentBaUnits.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLPropertyPanel.tableParentBaUnits.columnModel.title3")); // NOI18N
            tableParentBaUnits.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLPropertyPanel.tableParentBaUnits.columnModel.title4")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(groupPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel8Layout.createSequentialGroup()
                .add(groupPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel8);

        jPanel5.setName("jPanel5"); // NOI18N

        jToolBar7.setFloatable(false);
        jToolBar7.setRollover(true);
        jToolBar7.setName("jToolBar7"); // NOI18N

        btnViewChild.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewChild.setText(bundle.getString("SLPropertyPanel.btnViewChild.text")); // NOI18N
        btnViewChild.setFocusable(false);
        btnViewChild.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewChild.setName("btnViewChild"); // NOI18N
        btnViewChild.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewChild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewChildActionPerformed(evt);
            }
        });
        jToolBar7.add(btnViewChild);

        groupPanel3.setName("groupPanel3"); // NOI18N
        groupPanel3.setTitleText(bundle.getString("SLPropertyPanel.groupPanel3.titleText")); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tableChildBaUnits.setComponentPopupMenu(popupChildBaUnits);
        tableChildBaUnits.setName("tableChildBaUnits"); // NOI18N
        tableChildBaUnits.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredChildBaUnits}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableChildBaUnits);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.displayName}"));
        columnBinding.setColumnName("Related Ba Unit.display Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.baUnitType.displayValue}"));
        columnBinding.setColumnName("Related Ba Unit.ba Unit Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baUnitRelType.displayValue}"));
        columnBinding.setColumnName("Ba Unit Rel Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.description}"));
        columnBinding.setColumnName("Related Ba Unit.description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.status.displayValue}"));
        columnBinding.setColumnName("Related Ba Unit.status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedChildBaUnit}"), tableChildBaUnits, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableChildBaUnits.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableChildBaUnitsMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tableChildBaUnits);
        if (tableChildBaUnits.getColumnModel().getColumnCount() > 0) {
            tableChildBaUnits.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title2")); // NOI18N
            tableChildBaUnits.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title4")); // NOI18N
            tableChildBaUnits.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title3")); // NOI18N
            tableChildBaUnits.getColumnModel().getColumn(3).setPreferredWidth(260);
            tableChildBaUnits.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title5")); // NOI18N
            tableChildBaUnits.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title6")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
            .add(groupPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane3)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(groupPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel5);

        org.jdesktop.layout.GroupLayout pnlRelationshipsLayout = new org.jdesktop.layout.GroupLayout(pnlRelationships);
        pnlRelationships.setLayout(pnlRelationshipsLayout);
        pnlRelationshipsLayout.setHorizontalGroup(
            pnlRelationshipsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRelationshipsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlRelationshipsLayout.setVerticalGroup(
            pnlRelationshipsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRelationshipsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlRelationships.TabConstraints.tabTitle"), pnlRelationships); // NOI18N

        pnlValuations.setName("pnlValuations"); // NOI18N

        jPanel12.setName("jPanel12"); // NOI18N

        jToolBar10.setFloatable(false);
        jToolBar10.setRollover(true);
        jToolBar10.setName("jToolBar10"); // NOI18N

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        jButton2.setText(bundle.getString("SLPropertyPanel.jButton2.text")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar10.add(jButton2);

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        tblValuations.setName("tblValuations"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${valuationList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tblValuations);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nr}"));
        columnBinding.setColumnName("Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baUnitBasic.displayName}"));
        columnBinding.setColumnName("Ba Unit Basic.display Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baUnitBasic.area}"));
        columnBinding.setColumnName("Ba Unit Basic.area");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${amount}"));
        columnBinding.setColumnName("Amount");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${type.displayValue}"));
        columnBinding.setColumnName("Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${type.description}"));
        columnBinding.setColumnName("Type.description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedValuation}"), tblValuations, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane11.setViewportView(tblValuations);
        if (tblValuations.getColumnModel().getColumnCount() > 0) {
            tblValuations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tblValuations.columnModel.title0")); // NOI18N
            tblValuations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tblValuations.columnModel.title4")); // NOI18N
            tblValuations.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.tblValuations.columnModel.title5")); // NOI18N
            tblValuations.getColumnModel().getColumn(2).setCellRenderer(new AreaCellRenderer());
            tblValuations.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLPropertyPanel.tblValuations.columnModel.title1")); // NOI18N
            tblValuations.getColumnModel().getColumn(3).setCellRenderer(new MoneyCellRenderer());
            tblValuations.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLPropertyPanel.tblValuations.columnModel.title2")); // NOI18N
            tblValuations.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("SLPropertyPanel.tblValuations.columnModel.title3")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                    .add(jToolBar10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout pnlValuationsLayout = new org.jdesktop.layout.GroupLayout(pnlValuations);
        pnlValuations.setLayout(pnlValuationsLayout);
        pnlValuationsLayout.setHorizontalGroup(
            pnlValuationsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlValuationsLayout.setVerticalGroup(
            pnlValuationsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlValuations.TabConstraints.tabTitle"), pnlValuations); // NOI18N

        pnlMap.setName("pnlMap"); // NOI18N
        pnlMap.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                pnlMapComponentShown(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlMapLayout = new org.jdesktop.layout.GroupLayout(pnlMap);
        pnlMap.setLayout(pnlMapLayout);
        pnlMapLayout.setHorizontalGroup(
            pnlMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 627, Short.MAX_VALUE)
        );
        pnlMapLayout.setVerticalGroup(
            pnlMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 460, Short.MAX_VALUE)
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlMap.TabConstraints.tabTitle"), pnlMap); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("SLPropertyPanel.headerPanel.titleText")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
            .add(jToolBar5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(tabsMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(headerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tabsMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
    saveBaUnit(true, false);
    customizeForm();
}//GEN-LAST:event_btnSaveActionPerformed

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    if (this.mapControl == null) {
        this.mapControl = new ControlsBundleForBaUnit();
        if (applicationBean != null) {
            this.mapControl.setApplicationId(this.applicationBean.getId());
        }
        this.pnlMap.setLayout(new BorderLayout());
        this.pnlMap.add(this.mapControl, BorderLayout.CENTER);
    }
}//GEN-LAST:event_formComponentShown

    private void menuViewParentBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewParentBaUnitActionPerformed
        btnViewParentActionPerformed(evt);
    }//GEN-LAST:event_menuViewParentBaUnitActionPerformed

    private void menuAddParentBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddParentBaUnitActionPerformed
        openPropertyRelationshipPanel();
    }//GEN-LAST:event_menuAddParentBaUnitActionPerformed

    private void menuRemoveParentBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveParentBaUnitActionPerformed
        btnRemoveParentActionPerformed(evt);
    }//GEN-LAST:event_menuRemoveParentBaUnitActionPerformed

    private void menuViewChildBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewChildBaUnitActionPerformed
        btnViewChildActionPerformed(evt);
    }//GEN-LAST:event_menuViewChildBaUnitActionPerformed

    private void btnTerminateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTerminateActionPerformed
        terminateBaUnit();
    }//GEN-LAST:event_btnTerminateActionPerformed

    private void btnPrintBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintBaUnitActionPerformed
        print();
    }//GEN-LAST:event_btnPrintBaUnitActionPerformed

    private void menuAddParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddParcelActionPerformed
        addParcel();
    }//GEN-LAST:event_menuAddParcelActionPerformed

    private void menuRemoveParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveParcelActionPerformed
        removeParcel();
    }//GEN-LAST:event_menuRemoveParcelActionPerformed

    private void menuVaryRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuVaryRightActionPerformed
        varyRight();
    }//GEN-LAST:event_menuVaryRightActionPerformed

    private void menuExtinguishRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExtinguishRightActionPerformed
        extinguishRight();
    }//GEN-LAST:event_menuExtinguishRightActionPerformed

    private void menuEditRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditRightActionPerformed
        editRight();
    }//GEN-LAST:event_menuEditRightActionPerformed

    private void menuRemoveRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveRightActionPerformed
        removeRight();
    }//GEN-LAST:event_menuRemoveRightActionPerformed

    private void menuRemoveNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveNotationActionPerformed
        removeNotation();
    }//GEN-LAST:event_menuRemoveNotationActionPerformed

    private void pnlMapComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_pnlMapComponentShown
        if (this.mapControl != null) {
            this.mapControl.setCadastreObjects(baUnitBean1.getCadastreObjectList());
        }
    }//GEN-LAST:event_pnlMapComponentShown

    private void btnViewChildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewChildActionPerformed
        openPropertyForm(baUnitBean1.getSelectedChildBaUnit());
    }//GEN-LAST:event_btnViewChildActionPerformed

    private void btnRemoveParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveParentActionPerformed
        baUnitBean1.removeSelectedParentBaUnit();
    }//GEN-LAST:event_btnRemoveParentActionPerformed

    private void btnAddParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddParentActionPerformed
        openPropertyRelationshipPanel();
    }//GEN-LAST:event_btnAddParentActionPerformed

    private void btnViewParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewParentActionPerformed
        openPropertyForm(baUnitBean1.getSelectedParentBaUnit());
    }//GEN-LAST:event_btnViewParentActionPerformed

    private void btnRemoveNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveNotationActionPerformed
        removeNotation();
    }//GEN-LAST:event_btnRemoveNotationActionPerformed

    private void btnAddNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNotationActionPerformed
        addNotation();
    }//GEN-LAST:event_btnAddNotationActionPerformed

    private void tableRightsHistoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableRightsHistoryMouseClicked
        if (evt.getClickCount() > 1 && baUnitBean1.getSelectedHistoricRight() != null) {
            openRightForm(baUnitBean1.getSelectedHistoricRight(), RrrBean.RRR_ACTION.VIEW);
        }
    }//GEN-LAST:event_tableRightsHistoryMouseClicked

    private void btnViewHistoricRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewHistoricRightActionPerformed
        if (baUnitBean1.getSelectedHistoricRight() != null) {
            openRightForm(baUnitBean1.getSelectedHistoricRight(), RrrBean.RRR_ACTION.VIEW);
        }
    }//GEN-LAST:event_btnViewHistoricRightActionPerformed

    private void btnRemoveRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveRightActionPerformed
        removeRight();
    }//GEN-LAST:event_btnRemoveRightActionPerformed

    private void btnEditRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditRightActionPerformed
        editRight();
    }//GEN-LAST:event_btnEditRightActionPerformed

    private void btnViewRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewRightActionPerformed
        if (baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.VIEW);
        }
    }//GEN-LAST:event_btnViewRightActionPerformed

    private void btnExtinguishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExtinguishActionPerformed
        extinguishRight();
    }//GEN-LAST:event_btnExtinguishActionPerformed

    private void btnChangeRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeRightActionPerformed
        varyRight();
    }//GEN-LAST:event_btnChangeRightActionPerformed

    private void btnCreateRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateRightActionPerformed
        createRight();
    }//GEN-LAST:event_btnCreateRightActionPerformed

    private void tableRightsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableRightsMouseClicked
        if (evt.getClickCount() > 1 && baUnitBean1.getSelectedRight() != null) {
            if (btnEditRight.isEnabled()) {
                editRight();
            } else {
                openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.VIEW);
            }
        }
    }//GEN-LAST:event_tableRightsMouseClicked

    private void btnRemoveParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveParcelActionPerformed
        removeParcel();
    }//GEN-LAST:event_btnRemoveParcelActionPerformed

    private void btnAddParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddParcelActionPerformed
        addParcel();
    }//GEN-LAST:event_btnAddParcelActionPerformed

    private void btnEditNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditNotationActionPerformed
        editNotation();
    }//GEN-LAST:event_btnEditNotationActionPerformed

    private void menuAddNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddNotationActionPerformed
        addNotation();
    }//GEN-LAST:event_menuAddNotationActionPerformed

    private void menuEditNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditNotationActionPerformed
        editNotation();
    }//GEN-LAST:event_menuEditNotationActionPerformed

    private void btnViewNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewNotationActionPerformed
        viewNotation();
    }//GEN-LAST:event_btnViewNotationActionPerformed

    private void menuViewNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewNotationActionPerformed
        viewNotation();
    }//GEN-LAST:event_menuViewNotationActionPerformed

    private void btnEditParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditParcelActionPerformed
        editParcel();
    }//GEN-LAST:event_btnEditParcelActionPerformed

    private void btnViewParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewParcelActionPerformed
        viewParcel();
    }//GEN-LAST:event_btnViewParcelActionPerformed

    private void menuViewParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewParcelActionPerformed
        viewParcel();
    }//GEN-LAST:event_menuViewParcelActionPerformed

    private void menuEditParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditParcelActionPerformed
        editParcel();
    }//GEN-LAST:event_menuEditParcelActionPerformed

    private void btnViewPropDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewPropDocActionPerformed
        viewDocument();
    }//GEN-LAST:event_btnViewPropDocActionPerformed

    private void menuViewPropertyDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewPropertyDocumentActionPerformed
        viewDocument();
    }//GEN-LAST:event_menuViewPropertyDocumentActionPerformed

    private void btnAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignActionPerformed
        assignProperty();
    }//GEN-LAST:event_btnAssignActionPerformed

    private void btnSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSecurityActionPerformed
        configureSecurity();
    }//GEN-LAST:event_btnSecurityActionPerformed

    private void tableParentBaUnitsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableParentBaUnitsMouseClicked
        if (evt.getClickCount() == 2 && baUnitBean1.getSelectedParentBaUnit() != null) {
            openPropertyForm(baUnitBean1.getSelectedParentBaUnit());
        }
    }//GEN-LAST:event_tableParentBaUnitsMouseClicked

    private void tableChildBaUnitsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableChildBaUnitsMouseClicked
        if (evt.getClickCount() == 2 && baUnitBean1.getSelectedChildBaUnit() != null) {
            openPropertyForm(baUnitBean1.getSelectedChildBaUnit());
        }
    }//GEN-LAST:event_tableChildBaUnitsMouseClicked

    private void tableParcelsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableParcelsMouseClicked
        if (evt.getClickCount() == 2 && baUnitBean1.getSelectedParcel() != null) {
            if (btnEditParcel.isEnabled()) {
                editParcel();
            } else {
                viewParcel();
            }
        }
    }//GEN-LAST:event_tableParcelsMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (baUnitBean1.getSelectedValuation() != null) {
            openValuation(baUnitBean1.getSelectedValuation(), true);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel areaPanel;
    private org.sola.clients.beans.administrative.BaUnitAreaBean baUnitAreaBean1;
    private org.sola.clients.beans.administrative.BaUnitBean baUnitBean1;
    private org.sola.clients.beans.referencedata.RrrTypeListBean baUnitRrrTypes;
    private javax.swing.JButton btnAddNotation;
    private javax.swing.JButton btnAddParcel;
    private javax.swing.JButton btnAddParent;
    private javax.swing.JButton btnAssign;
    private javax.swing.JButton btnChangeRight;
    private javax.swing.JButton btnCreateRight;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEditNotation;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEditParcel;
    private javax.swing.JButton btnEditRight;
    private javax.swing.JButton btnExtinguish;
    private javax.swing.JButton btnPrintBaUnit;
    private javax.swing.JButton btnRemoveNotation;
    private javax.swing.JButton btnRemoveParcel;
    private javax.swing.JButton btnRemoveParent;
    private javax.swing.JButton btnRemoveRight;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSecurity;
    private javax.swing.JButton btnTerminate;
    private javax.swing.JButton btnViewChild;
    private javax.swing.JButton btnViewHistoricRight;
    private org.sola.clients.swing.common.buttons.BtnView btnViewNotation;
    private org.sola.clients.swing.common.buttons.BtnView btnViewParcel;
    private javax.swing.JButton btnViewParent;
    private org.sola.clients.swing.common.buttons.BtnView btnViewPropDoc;
    private javax.swing.JButton btnViewRight;
    private javax.swing.JComboBox cbxRightType;
    private org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel documentsPanel1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    private org.sola.clients.swing.ui.GroupPanel groupPanel3;
    private org.sola.clients.swing.ui.GroupPanel groupPanel4;
    private org.sola.clients.swing.ui.GroupPanel groupPanel5;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel16;
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
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles jTableWithDefaultStyles1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar10;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToolBar jToolBar6;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JToolBar jToolBar8;
    private javax.swing.JLabel labArea;
    private javax.swing.JMenuItem menuAddNotation;
    private javax.swing.JMenuItem menuAddParcel;
    private javax.swing.JMenuItem menuAddParentBaUnit;
    private javax.swing.JMenuItem menuEditNotation;
    private javax.swing.JMenuItem menuEditParcel;
    private javax.swing.JMenuItem menuEditRight;
    private javax.swing.JMenuItem menuExtinguishRight;
    private javax.swing.JMenuItem menuRemoveNotation;
    private javax.swing.JMenuItem menuRemoveParcel;
    private javax.swing.JMenuItem menuRemoveParentBaUnit;
    private javax.swing.JMenuItem menuRemoveRight;
    private javax.swing.JMenuItem menuVaryRight;
    private javax.swing.JMenuItem menuViewChildBaUnit;
    private javax.swing.JMenuItem menuViewNotation;
    private javax.swing.JMenuItem menuViewParcel;
    private javax.swing.JMenuItem menuViewParentBaUnit;
    private javax.swing.JMenuItem menuViewPropertyDocument;
    private javax.swing.JMenuItem menuViewRight;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlInterests;
    private javax.swing.JPanel pnlMap;
    private javax.swing.JPanel pnlNotations;
    private javax.swing.JPanel pnlNotes;
    private javax.swing.JPanel pnlOwnership;
    private javax.swing.JPanel pnlParcels;
    private javax.swing.JPanel pnlRelationships;
    private javax.swing.JPanel pnlSLGeneral;
    private javax.swing.JPanel pnlValuations;
    private javax.swing.JPopupMenu popupChildBaUnits;
    private javax.swing.JPopupMenu popupNotations;
    private javax.swing.JPopupMenu popupParcels;
    private javax.swing.JPopupMenu popupParentBaUnits;
    private javax.swing.JPopupMenu popupPropertyDocuments;
    private javax.swing.JPopupMenu popupRights;
    private org.sola.clients.swing.ui.source.DocumentsPanel propertyDocsPanel;
    private org.sola.clients.beans.party.PartySummaryListBean propertyManagerList;
    private org.sola.clients.beans.referencedata.RrrTypeListBean rrrTypes;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableChildBaUnits;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableNotes;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableOwnership;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableParcels;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableParentBaUnits;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRights;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRightsHistory;
    private javax.swing.JTabbedPane tabsMain;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblValuations;
    private javax.swing.JFormattedTextField txtPropArea;
    private javax.swing.JTextArea txtPropDescription;
    private javax.swing.JTextField txtPropFirstpart;
    private javax.swing.JTextField txtPropLastpart;
    private javax.swing.JTextField txtPropStatus;
    private javax.swing.JTextField txtPropertyManager;
    private javax.swing.JFormattedTextField txtSLArea;
    private javax.swing.JTextArea txtSLDescription;
    private javax.swing.JTextField txtSLLandUse;
    private javax.swing.JTextField txtSLLastPart;
    private javax.swing.JTextField txtSLStatus;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private void openValuation(final ValuationBean selectedValuation, final boolean b) {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                if (selectedValuation != null) {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_OBJECTION));
                    ValuationPanel panel = new ValuationPanel(selectedValuation, applicationBean, applicationService, b);
                    panel.addPropertyChangeListener(new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName().equals(ValuationPanel.VALUATION_SAVED)) {
                                ((ValuationBean) evt.getNewValue()).saveItem();
                                tblValuations.clearSelection();
                            }
                        }
                    });
                    getMainContentPanel().addPanel(panel, MainContentPanel.CARD_VALUATION_PANEL, true);
                }
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

}
