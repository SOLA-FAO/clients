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

import org.sola.clients.swing.desktop.cadastre.CreateParcelDialog;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import net.sf.jasperreports.engine.JasperPrint;
import org.sola.clients.beans.administrative.*;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.cadastre.SpatialValueAreaBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.*;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.system.PanelLauncherGroupBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.cadastre.SearchParcelDialog;
import org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForBaUnit;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.PanelLauncher;
import org.sola.clients.swing.ui.cadastre.CadastreObjectsDialog;
import org.sola.clients.swing.ui.renderers.*;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
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
    private PropertyChangeListener newPropertyWizardListener;
    public BaUnitBean whichBaUnitSelected;
    private boolean isBtnNext = false;

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

        saveBaUnitState();
    }

    /**
     * Runs form customization, to restrict certain actions, bind listeners on
     * the {@link BaUnitBean} and other components.
     */
    private void customizeForm() {

        if (nameFirstPart != null && nameLastPart != null) {
            headerPanel.setTitleText(String.format(
                    resourceBundle.getString("PropertyPanel.existingProperty.Text"),
                    nameFirstPart, nameLastPart));
            txtArea.setEditable(baUnitBean1.getStatusCode() == null || baUnitBean1.getStatusCode().equalsIgnoreCase(StatusConstants.PENDING));

        } else {
            headerPanel.setTitleText(resourceBundle.getString("PropertyPanel.newProperty.Text"));

            areaPanel.setEnabled(true);
            areaPanel.setEnabled(true);
            txtArea.setEnabled(true);
            txtArea.setEditable(true);
            txtArea.setVisible(true);
            labArea.setEnabled(true);
            labArea.setVisible(true);
        }

        if (baUnitBean1.isNew()) {
            areaPanel.setEnabled(true);
            areaPanel.setEnabled(true);
            txtArea.setEnabled(true);
            txtArea.setEditable(true);
            txtArea.setVisible(true);
            labArea.setEnabled(true);
            labArea.setVisible(true);
        }

        if (applicationBean != null && applicationService != null) {
            headerPanel.setTitleText(String.format("%s, %s",
                    headerPanel.getTitleText(),
                    String.format(resourceBundle.getString("PropertyPanel.applicationInfo.Text"),
                            applicationService.getRequestType().getDisplayValue(), applicationBean.getNr())));
        }

        btnSave.setEnabled(!readOnly);
        txtName.setEditable(!readOnly);

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

    }

    /**
     * Populates rights, parcels and parent Properties lists from provided
     * result object.
     *
     * @param selectedResult Array of selected result from the wizard form.
     * First item of array contains selected {@link BaUnitBean}, second item
     * contains {@link BaUnitRelTypeBean}.
     */
    private boolean addParentProperty(Object[] selectedResult) {
        if (selectedResult == null) {
            return false;
        }

        BaUnitBean selectedBaUnit = (BaUnitBean) selectedResult[0];
        BaUnitRelTypeBean baUnitRelType = (BaUnitRelTypeBean) selectedResult[1];
        this.whichBaUnitSelected = selectedBaUnit;
        // Check relation type to be same as on the list.
        for (RelatedBaUnitInfoBean parent : baUnitBean1.getFilteredParentBaUnits()) {
            if (parent.getRelationCode() != null
                    && !parent.getRelationCode().equals(baUnitRelType.getCode())) {
                MessageUtility.displayMessage(ClientMessage.BAUNIT_WRONG_RELATION_TYPE);
                return false;
            }
        }

        // Check if relation already exists
        for (RelatedBaUnitInfoBean parent : baUnitBean1.getParentBaUnits()) {
            if (parent.getRelatedBaUnitId() != null && parent.getRelatedBaUnitId().equals(selectedBaUnit.getId())) {
                MessageUtility.displayMessage(ClientMessage.BAUNIT_HAS_SELECTED_PARENT_BA_UNIT);
                return false;
            }
        }

        // Go througth the rights and add them, avoiding duplications
        for (RrrBean rrr : selectedBaUnit.getSelectedRrrs(true)) {
            boolean exists = false;
            for (RrrBean currentRrr : baUnitBean1.getRrrList()) {
                if (rrr.getId().equals(currentRrr.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                baUnitBean1.getRrrList().addAsNew(rrr);
            }
        }

        // Go througth the parcels and add them, avoiding duplications
        for (CadastreObjectBean cadastreObject : selectedBaUnit.getSelectedCadastreObjects()) {
            boolean exists = false;
            for (CadastreObjectBean currentCadastreObject : baUnitBean1.getCadastreObjectList()) {
                if (cadastreObject.getId().equals(currentCadastreObject.getId())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                baUnitBean1.getCadastreObjectList().addAsNew(cadastreObject);
            }
        }

        // Create relation
        RelatedBaUnitInfoBean relatedBuUnit = new RelatedBaUnitInfoBean();
        relatedBuUnit.setBaUnitId(baUnitBean1.getId());
        relatedBuUnit.setBaUnitRelType(baUnitRelType);
        relatedBuUnit.setRelatedBaUnit(selectedBaUnit);
        relatedBuUnit.setRelatedBaUnitId(selectedBaUnit.getId());
        baUnitBean1.getParentBaUnits().addAsNew(relatedBuUnit);

        tabsMain.setSelectedIndex(tabsMain.indexOfComponent(pnlRelationships));
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

        btnOpenParent.setEnabled(baUnitBean1.getSelectedParentBaUnit() != null);
        btnAddParent.setEnabled(enabled);
        btnRemoveParent.setEnabled(enabled && btnOpenParent.isEnabled());

        menuOpenParentBaUnit.setEnabled(btnOpenParent.isEnabled());
        menuAddParentBaUnit.setEnabled(btnAddParent.isEnabled());
        menuRemoveParentBaUnit.setEnabled(btnRemoveParent.isEnabled());
    }

    /**
     * Enables or disables "open" button for the child Properties list.
     */
    private void customizeChildPropertyButtons() {
        btnOpenChild.setEnabled(baUnitBean1.getSelectedChildBaUnit() != null);
        menuOpenChildBaUnit.setEnabled(btnOpenChild.isEnabled());
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
        boolean canEdit = canAdd && canView && !notation.getStatusCode().equals(StatusConstants.COMPLETED);
        boolean canRemove = canEdit;

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
                || !applicationService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_CANCEL_PROPERTY)) {
            enabled = false;
        }

        // Determine what should be shown on the button, terminate or cancelling of termination.
        if (baUnitBean1.getPendingActionCode() != null && applicationService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_CANCEL_PROPERTY)) {
            // Show cancel
            btnTerminate.setIcon(new ImageIcon(getClass().getResource("/images/common/undo.png")));
            btnTerminate.setText(resourceBundle.getString("PropertyPanel.btnTerminate.text2"));
        } else {
            // Show terminate
            btnTerminate.setIcon(new ImageIcon(getClass().getResource("/images/common/stop.png")));
            btnTerminate.setText(resourceBundle.getString("PropertyPanel.btnTerminate.text"));
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
        } else {
            btnRemoveParcel.setEnabled(true);
        }
        btnAddParcel.setEnabled(!readOnly);
        btnSearchParcel.setEnabled(!readOnly);
        menuAddParcel.setEnabled(btnAddParcel.isEnabled());
        menuRemoveParcel.setEnabled(btnRemoveParcel.isEnabled());
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
     * Open form to add new parcel or search for existing one.\
     *
     * @param isNew Opens {@link CreateParcelDialog} if true, otherwise opens
     * {@link SearchParcelDialog}
     */
    private void addParcel(boolean isNew) {

        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getNewValue() != null) {
                    baUnitBean1.getCadastreObjectList().addAsNew((CadastreObjectBean) e.getNewValue());
                }
            }
        };

        JDialog form;

        if (isNew) {
            form = new CreateParcelDialog(null, null, true);
        } else {
            form = new SearchParcelDialog(null, true);
        }

        form.setLocationRelativeTo(this);
        form.addPropertyChangeListener(CreateParcelDialog.SELECTED_PARCEL, listener);
        form.setVisible(true);
        form.removePropertyChangeListener(CreateParcelDialog.SELECTED_PARCEL, listener);
    }

    /**
     * Removes selected parcel from the list of parcels.
     */
    private void removeParcel() {
        if (baUnitBean1.getSelectedParcel() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            baUnitBean1.removeSelectedParcel();
        }
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
                        tableNotations.clearSelection();
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

    private void addParentProperty() {
        MessageUtility.displayMessage("Not yet implemented!");
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
        }

        // Determine the panel to open for the Rrr
        String rrrPanelCode = rrrBean.getRrrType().getRrrPanelCode();

        if (PanelLauncher.isLaunchGroup(PanelLauncherGroupBean.CODE_LEASE_RRR, rrrPanelCode)) {
            // Lease RRR requires additional constructor argument
            PanelLauncher.launch(rrrPanelCode, getMainContentPanel(), new RightFormListener(), null,
                    baUnitBean1, rrrBean, applicationBean, applicationService, action);
        } else {
            PanelLauncher.launch(rrrPanelCode, getMainContentPanel(), new RightFormListener(), null,
                    rrrBean, applicationBean, applicationService, action);
        }
    }

    private void saveBaUnit(final boolean showMessage, final boolean closeOnSave) {

        if (baUnitBean1.validate(true).size() > 0) {
            return;
        }

        if (!baUnitBean1.isValid()) {
            return;
        }

        if (txtArea.isEditable() || isBtnNext) {

            if (baUnitAreaBean1 == null) {
                return;
            } else {
                java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle");

                if (baUnitAreaBean1.getSize() == null) {
                    MessageUtility.displayMessage(ClientMessage.CHECK_BAUNITAREA_VALUE,
                            new Object[]{bundle.getString("PropertyPanel.labArea.text")});
                    txtArea.setText(null);
                    txtArea.requestFocus();
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

                if ((!txtArea.getText().equals(null)) && txtArea.getText() != "" && (!txtArea.getText().isEmpty())) {
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        baUnitBean1 = createBaUnitBean();
        baUnitRrrTypes = new org.sola.clients.beans.referencedata.RrrTypeListBean();
        rrrTypes = new org.sola.clients.beans.referencedata.RrrTypeListBean();
        popupParcels = new javax.swing.JPopupMenu();
        menuAddParcel = new javax.swing.JMenuItem();
        menuRemoveParcel = new javax.swing.JMenuItem();
        popupRights = new javax.swing.JPopupMenu();
        menuVaryRight = new javax.swing.JMenuItem();
        menuExtinguishRight = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuViewRight = new javax.swing.JMenuItem();
        menuEditRight = new javax.swing.JMenuItem();
        menuRemoveRight = new javax.swing.JMenuItem();
        popupNotations = new javax.swing.JPopupMenu();
        menuAddNotation = new javax.swing.JMenuItem();
        menuEditNotation = new javax.swing.JMenuItem();
        menuRemoveNotation = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        menuViewNotation = new javax.swing.JMenuItem();
        popupParentBaUnits = new javax.swing.JPopupMenu();
        menuOpenParentBaUnit = new javax.swing.JMenuItem();
        menuAddParentBaUnit = new javax.swing.JMenuItem();
        menuRemoveParentBaUnit = new javax.swing.JMenuItem();
        popupChildBaUnits = new javax.swing.JPopupMenu();
        menuOpenChildBaUnit = new javax.swing.JMenuItem();
        baUnitAreaBean1 = createBaUnitAreaBean();
        jToolBar5 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnTerminate = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnPrintBaUnit = new javax.swing.JButton();
        tabsMain = new javax.swing.JTabbedPane();
        pnlGeneral = new javax.swing.JPanel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtLastPart = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtBaUnitStatus = new javax.swing.JTextField();
        areaPanel = new javax.swing.JPanel();
        labArea = new javax.swing.JLabel();
        txtArea = new javax.swing.JFormattedTextField();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtEstateType = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtName = new javax.swing.JTextArea();
        documentsPanel1 = createDocumentsPanel();
        pnlNotes = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableNotations = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar3 = new javax.swing.JToolBar();
        btnAddNotation = new javax.swing.JButton();
        btnEditNotation = new org.sola.clients.swing.common.buttons.BtnEdit();
        btnRemoveNotation = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnViewNotation = new org.sola.clients.swing.common.buttons.BtnView();
        pnlParcels = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableParcels = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddParcel = new javax.swing.JButton();
        btnAddParcelFromApplication = new javax.swing.JButton();
        btnSearchParcel = new javax.swing.JButton();
        btnRemoveParcel = new javax.swing.JButton();
        pnlInterests = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableRights = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel16 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        cbxRightType = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        btnCreateRight = new javax.swing.JButton();
        btnChangeRight = new javax.swing.JButton();
        btnExtinguish = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnViewRight = new javax.swing.JButton();
        btnEditRight = new javax.swing.JButton();
        btnRemoveRight = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jToolBar8 = new javax.swing.JToolBar();
        jLabel8 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnViewHistoricRight = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        tableRightsHistory = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        pnlRelationships = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jToolBar6 = new javax.swing.JToolBar();
        jLabel6 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnOpenParent = new javax.swing.JButton();
        btnAddParent = new javax.swing.JButton();
        btnRemoveParent = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableParentBaUnits = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jToolBar7 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnOpenChild = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tableChildBaUnits = new javax.swing.JTable();
        pnlNextButton = new javax.swing.JPanel();
        pnlMap = new javax.swing.JPanel();
        pnlHistory = new javax.swing.JPanel();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();

        popupParcels.setName("popupParcels"); // NOI18N

        menuAddParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/new.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        menuAddParcel.setText(bundle.getString("SLPropertyPanel.menuAddParcel.text")); // NOI18N
        menuAddParcel.setName("menuAddParcel"); // NOI18N
        menuAddParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddParcelActionPerformed(evt);
            }
        });
        popupParcels.add(menuAddParcel);

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

        jSeparator8.setName("jSeparator8"); // NOI18N
        popupNotations.add(jSeparator8);

        menuViewNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewNotation.setText(bundle.getString("SLPropertyPanel.menuViewNotation.text")); // NOI18N
        menuViewNotation.setName("menuViewNotation"); // NOI18N
        menuViewNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewNotationActionPerformed(evt);
            }
        });
        popupNotations.add(menuViewNotation);

        popupParentBaUnits.setName("popupParentBaUnits"); // NOI18N

        menuOpenParentBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuOpenParentBaUnit.setText(bundle.getString("SLPropertyPanel.menuOpenParentBaUnit.text")); // NOI18N
        menuOpenParentBaUnit.setName("menuOpenParentBaUnit"); // NOI18N
        menuOpenParentBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenParentBaUnitActionPerformed(evt);
            }
        });
        popupParentBaUnits.add(menuOpenParentBaUnit);

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

        menuOpenChildBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuOpenChildBaUnit.setText(bundle.getString("SLPropertyPanel.menuOpenChildBaUnit.text")); // NOI18N
        menuOpenChildBaUnit.setName("menuOpenChildBaUnit"); // NOI18N
        menuOpenChildBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenChildBaUnitActionPerformed(evt);
            }
        });
        popupChildBaUnits.add(menuOpenChildBaUnit);

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

        pnlGeneral.setName("pnlGeneral"); // NOI18N

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("SLPropertyPanel.groupPanel1.titleText")); // NOI18N

        jPanel13.setName("jPanel13"); // NOI18N
        jPanel13.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridLayout(2, 2, 15, 0));

        jPanel9.setName("jPanel9"); // NOI18N

        jLabel2.setText(bundle.getString("SLPropertyPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtLastPart.setEditable(false);
        txtLastPart.setName("txtLastPart"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"), txtLastPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(txtLastPart)
            .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtLastPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel9);

        jPanel6.setName("jPanel6"); // NOI18N

        jLabel7.setText(bundle.getString("SLPropertyPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtBaUnitStatus.setEditable(false);
        txtBaUnitStatus.setName("txtBaUnitStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtBaUnitStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(txtBaUnitStatus)
            .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtBaUnitStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6);

        areaPanel.setName(bundle.getString("PropertyPanel.areaPanel.name_1")); // NOI18N

        labArea.setText(bundle.getString("SLPropertyPanel.labArea.text")); // NOI18N
        labArea.setName(bundle.getString("PropertyPanel.labArea.name")); // NOI18N

        txtArea.setFormatterFactory(FormattersFactory.getInstance().getMetricAreaFormatterFactory());
        txtArea.setText(bundle.getString("SLPropertyPanel.txtArea.text")); // NOI18N
        txtArea.setName("txtArea"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitAreaBean1, org.jdesktop.beansbinding.ELProperty.create("${size}"), txtArea, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout areaPanelLayout = new org.jdesktop.layout.GroupLayout(areaPanel);
        areaPanel.setLayout(areaPanelLayout);
        areaPanelLayout.setHorizontalGroup(
            areaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, labArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(txtArea)
        );
        areaPanelLayout.setVerticalGroup(
            areaPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(areaPanelLayout.createSequentialGroup()
                .add(labArea)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtArea, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(areaPanel);

        jPanel11.setName("jPanel11"); // NOI18N

        jLabel4.setText(bundle.getString("SLPropertyPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        txtEstateType.setEditable(false);
        txtEstateType.setName("txtEstateType"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${purpose}"), txtEstateType, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(txtEstateType)
            .add(jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtEstateType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel11);

        jPanel13.add(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridLayout(1, 1, 15, 0));

        jPanel12.setName("jPanel12"); // NOI18N

        jLabel5.setText(bundle.getString("SLPropertyPanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        txtName.setColumns(20);
        txtName.setRows(5);
        txtName.setName("txtName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane5.setViewportView(txtName);

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel12);

        jPanel13.add(jPanel2);

        documentsPanel1.setName("documentsPanel1"); // NOI18N

        org.jdesktop.layout.GroupLayout pnlGeneralLayout = new org.jdesktop.layout.GroupLayout(pnlGeneral);
        pnlGeneral.setLayout(pnlGeneralLayout);
        pnlGeneralLayout.setHorizontalGroup(
            pnlGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(documentsPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, groupPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlGeneralLayout.setVerticalGroup(
            pnlGeneralLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(groupPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(documentsPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlGeneral.TabConstraints.tabTitle"), pnlGeneral); // NOI18N

        pnlNotes.setName("pnlNotes"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tableNotations.setComponentPopupMenu(popupNotations);
        tableNotations.setName("tableNotations"); // NOI18N
        tableNotations.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitFilteredNotationList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableNotations);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${referenceNr}"));
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
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedBaUnitNotation}"), tableNotations, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(tableNotations);
        if (tableNotations.getColumnModel().getColumnCount() > 0) {
            tableNotations.getColumnModel().getColumn(0).setPreferredWidth(100);
            tableNotations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title0")); // NOI18N
            tableNotations.getColumnModel().getColumn(1).setPreferredWidth(400);
            tableNotations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title1")); // NOI18N
            tableNotations.getColumnModel().getColumn(2).setPreferredWidth(120);
            tableNotations.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title3")); // NOI18N
            tableNotations.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            tableNotations.getColumnModel().getColumn(3).setResizable(false);
            tableNotations.getColumnModel().getColumn(3).setPreferredWidth(100);
            tableNotations.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title2")); // NOI18N
        }

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

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

        jSeparator7.setName("jSeparator7"); // NOI18N
        jToolBar3.add(jSeparator7);

        btnViewNotation.setName("btnViewNotation"); // NOI18N
        btnViewNotation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewNotationActionPerformed(evt);
            }
        });
        jToolBar3.add(btnViewNotation);

        org.jdesktop.layout.GroupLayout pnlNotesLayout = new org.jdesktop.layout.GroupLayout(pnlNotes);
        pnlNotes.setLayout(pnlNotesLayout);
        pnlNotesLayout.setHorizontalGroup(
            pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlNotesLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
                    .add(jToolBar3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlNotesLayout.setVerticalGroup(
            pnlNotesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlNotesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlNotes.TabConstraints.tabTitle"), pnlNotes); // NOI18N

        pnlParcels.setName("pnlParcels"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableParcels.setComponentPopupMenu(popupParcels);
        tableParcels.setName("tableParcels"); // NOI18N

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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${locked}"));
        columnBinding.setColumnName("Locked");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedParcel}"), tableParcels, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableParcels);
        if (tableParcels.getColumnModel().getColumnCount() > 0) {
            tableParcels.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableParcels.columnModel.title0")); // NOI18N
            tableParcels.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tableParcels.columnModel.title1")); // NOI18N
            tableParcels.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title3")); // NOI18N
            tableParcels.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title2")); // NOI18N
            tableParcels.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title6")); // NOI18N
            tableParcels.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title8")); // NOI18N
            tableParcels.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title7")); // NOI18N
            tableParcels.getColumnModel().getColumn(6).setCellRenderer(new CellDelimitedListRenderer("; ", false));
            tableParcels.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title4")); // NOI18N
            tableParcels.getColumnModel().getColumn(8).setPreferredWidth(40);
            tableParcels.getColumnModel().getColumn(8).setMaxWidth(40);
            tableParcels.getColumnModel().getColumn(8).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title5")); // NOI18N
            tableParcels.getColumnModel().getColumn(8).setCellRenderer(new LockCellRenderer());
        }

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/new.png"))); // NOI18N
        btnAddParcel.setText(bundle.getString("SLPropertyPanel.btnAddParcel.text")); // NOI18N
        btnAddParcel.setName("btnAddParcel"); // NOI18N
        btnAddParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddParcel);

        btnAddParcelFromApplication.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddParcelFromApplication.setText(bundle.getString("SLPropertyPanel.btnAddParcelFromApplication.text_1")); // NOI18N
        btnAddParcelFromApplication.setFocusable(false);
        btnAddParcelFromApplication.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        btnAddParcelFromApplication.setName(bundle.getString("PropertyPanel.btnAddParcelFromApplication.name_1")); // NOI18N
        btnAddParcelFromApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddParcelFromApplicationActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddParcelFromApplication);

        btnSearchParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        btnSearchParcel.setText(bundle.getString("SLPropertyPanel.btnSearchParcel.text_1")); // NOI18N
        btnSearchParcel.setFocusable(false);
        btnSearchParcel.setName(bundle.getString("PropertyPanel.btnSearchParcel.name_1")); // NOI18N
        btnSearchParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSearchParcel);

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
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(pnlParcelsLayout.createSequentialGroup()
                        .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
                        .add(20, 20, 20))))
        );
        pnlParcelsLayout.setVerticalGroup(
            pnlParcelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlParcelsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
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

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrFilteredList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableRights);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rrrType.displayValue}"));
        columnBinding.setColumnName("Rrr Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nr}"));
        columnBinding.setColumnName("Nr");
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
            tableRights.getColumnModel().getColumn(0).setPreferredWidth(100);
            tableRights.getColumnModel().getColumn(0).setMaxWidth(100);
            tableRights.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableRights.columnModel.title0")); // NOI18N
            tableRights.getColumnModel().getColumn(1).setPreferredWidth(100);
            tableRights.getColumnModel().getColumn(1).setMaxWidth(100);
            tableRights.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableRights.columnModel.title3")); // NOI18N
            tableRights.getColumnModel().getColumn(2).setPreferredWidth(120);
            tableRights.getColumnModel().getColumn(2).setMaxWidth(120);
            tableRights.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableRights.columnModel.title1")); // NOI18N
            tableRights.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            tableRights.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.tableRights.columnModel.title4_1")); // NOI18N
            tableRights.getColumnModel().getColumn(4).setPreferredWidth(100);
            tableRights.getColumnModel().getColumn(4).setMaxWidth(100);
            tableRights.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("PropertyPanel.tableRights.columnModel.title2")); // NOI18N
        }

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

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

        btnEditRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/document--pencil.png"))); // NOI18N
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
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
            .add(jToolBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel15Layout.createSequentialGroup()
                .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
        );

        jPanel16.add(jPanel15);

        jPanel17.setName(bundle.getString("PropertyPanel.jPanel17.name")); // NOI18N

        jToolBar8.setFloatable(false);
        jToolBar8.setRollover(true);
        jToolBar8.setName(bundle.getString("PropertyPanel.jToolBar8.name")); // NOI18N

        jLabel8.setText(bundle.getString("SLPropertyPanel.jLabel8.text")); // NOI18N
        jLabel8.setName(bundle.getString("PropertyPanel.jLabel8.name")); // NOI18N
        jToolBar8.add(jLabel8);

        jSeparator6.setName(bundle.getString("PropertyPanel.jSeparator6.name")); // NOI18N
        jToolBar8.add(jSeparator6);

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

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrHistoricList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableRightsHistory);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rrrType.displayValue}"));
        columnBinding.setColumnName("Rrr Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nr}"));
        columnBinding.setColumnName("Nr");
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
            tableRightsHistory.getColumnModel().getColumn(0).setPreferredWidth(100);
            tableRightsHistory.getColumnModel().getColumn(0).setMaxWidth(100);
            tableRightsHistory.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableRightsHistory.columnModel.title0")); // NOI18N
            tableRightsHistory.getColumnModel().getColumn(1).setPreferredWidth(100);
            tableRightsHistory.getColumnModel().getColumn(1).setMaxWidth(100);
            tableRightsHistory.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableRightsHistory.columnModel.title3")); // NOI18N
            tableRightsHistory.getColumnModel().getColumn(2).setPreferredWidth(120);
            tableRightsHistory.getColumnModel().getColumn(2).setMaxWidth(120);
            tableRightsHistory.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableRightsHistory.columnModel.title1")); // NOI18N
            tableRightsHistory.getColumnModel().getColumn(2).setCellRenderer(new DateTimeRenderer());
            tableRightsHistory.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.tableRightsHistory.columnModel.title4")); // NOI18N
            tableRightsHistory.getColumnModel().getColumn(4).setPreferredWidth(100);
            tableRightsHistory.getColumnModel().getColumn(4).setMaxWidth(100);
            tableRightsHistory.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("PropertyPanel.tableRightsHistory.columnModel.title2")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout jPanel17Layout = new org.jdesktop.layout.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel17Layout.createSequentialGroup()
                .add(jToolBar8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
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

        pnlRelationships.setName("pnlRelationships"); // NOI18N

        jPanel14.setName("jPanel14"); // NOI18N
        jPanel14.setLayout(new java.awt.GridLayout(2, 1, 0, 15));

        jPanel8.setName("jPanel8"); // NOI18N

        jToolBar6.setFloatable(false);
        jToolBar6.setRollover(true);
        jToolBar6.setName("jToolBar6"); // NOI18N

        jLabel6.setFont(LafManager.getInstance().getLabFontBold());
        jLabel6.setText(bundle.getString("SLPropertyPanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jToolBar6.add(jLabel6);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar6.add(jSeparator3);

        btnOpenParent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenParent.setText(bundle.getString("SLPropertyPanel.btnOpenParent.text")); // NOI18N
        btnOpenParent.setToolTipText(bundle.getString("SLPropertyPanel.btnOpenParent.toolTipText")); // NOI18N
        btnOpenParent.setFocusable(false);
        btnOpenParent.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenParent.setName("btnOpenParent"); // NOI18N
        btnOpenParent.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenParent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenParentActionPerformed(evt);
            }
        });
        jToolBar6.add(btnOpenParent);

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

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tableParentBaUnits.setComponentPopupMenu(popupParentBaUnits);
        tableParentBaUnits.setName("tableParentBaUnits"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredParentBaUnits}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableParentBaUnits);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.name}"));
        columnBinding.setColumnName("Related Ba Unit.name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.nameFirstpart}"));
        columnBinding.setColumnName("Related Ba Unit.name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.nameLastpart}"));
        columnBinding.setColumnName("Related Ba Unit.name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baUnitRelType.displayValue}"));
        columnBinding.setColumnName("Ba Unit Rel Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.status.displayValue}"));
        columnBinding.setColumnName("Related Ba Unit.status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedParentBaUnit}"), tableParentBaUnits, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(tableParentBaUnits);
        if (tableParentBaUnits.getColumnModel().getColumnCount() > 0) {
            tableParentBaUnits.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableParentBaUnits.columnModel.title0_1")); // NOI18N
            tableParentBaUnits.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tableParentBaUnits.columnModel.title1_1")); // NOI18N
            tableParentBaUnits.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.tableParentBaUnits.columnModel.title2_1")); // NOI18N
            tableParentBaUnits.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.jTable1.columnModel.title4")); // NOI18N
            tableParentBaUnits.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("PropertyPanel.jTable1.columnModel.title3_1")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jToolBar6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel8);

        jPanel5.setName("jPanel5"); // NOI18N

        jToolBar7.setFloatable(false);
        jToolBar7.setRollover(true);
        jToolBar7.setName("jToolBar7"); // NOI18N

        jLabel3.setFont(LafManager.getInstance().getLabFontBold());
        jLabel3.setText(bundle.getString("SLPropertyPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jToolBar7.add(jLabel3);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jToolBar7.add(jSeparator2);

        btnOpenChild.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenChild.setText(bundle.getString("SLPropertyPanel.btnOpenChild.text")); // NOI18N
        btnOpenChild.setFocusable(false);
        btnOpenChild.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenChild.setName("btnOpenChild"); // NOI18N
        btnOpenChild.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenChild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenChildActionPerformed(evt);
            }
        });
        jToolBar7.add(btnOpenChild);

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        tableChildBaUnits.setComponentPopupMenu(popupChildBaUnits);
        tableChildBaUnits.setName("tableChildBaUnits"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredChildBaUnits}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableChildBaUnits);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.name}"));
        columnBinding.setColumnName("Related Ba Unit.name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.nameFirstpart}"));
        columnBinding.setColumnName("Related Ba Unit.name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.nameLastpart}"));
        columnBinding.setColumnName("Related Ba Unit.name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baUnitRelType.displayValue}"));
        columnBinding.setColumnName("Ba Unit Rel Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relatedBaUnit.status.displayValue}"));
        columnBinding.setColumnName("Related Ba Unit.status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedChildBaUnit}"), tableChildBaUnits, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane7.setViewportView(tableChildBaUnits);
        if (tableChildBaUnits.getColumnModel().getColumnCount() > 0) {
            tableChildBaUnits.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title0_1")); // NOI18N
            tableChildBaUnits.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title1_1")); // NOI18N
            tableChildBaUnits.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title2_1")); // NOI18N
            tableChildBaUnits.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title3_1")); // NOI18N
            tableChildBaUnits.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLPropertyPanel.tableChildBaUnits.columnModel.title4")); // NOI18N
        }

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
            .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jToolBar7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel5);

        pnlNextButton.setName(bundle.getString("PropertyPanel.pnlNextButton.name_2")); // NOI18N

        org.jdesktop.layout.GroupLayout pnlNextButtonLayout = new org.jdesktop.layout.GroupLayout(pnlNextButton);
        pnlNextButton.setLayout(pnlNextButtonLayout);
        pnlNextButtonLayout.setHorizontalGroup(
            pnlNextButtonLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        pnlNextButtonLayout.setVerticalGroup(
            pnlNextButtonLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 34, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout pnlRelationshipsLayout = new org.jdesktop.layout.GroupLayout(pnlRelationships);
        pnlRelationships.setLayout(pnlRelationshipsLayout);
        pnlRelationshipsLayout.setHorizontalGroup(
            pnlRelationshipsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRelationshipsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlRelationshipsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlRelationshipsLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(pnlNextButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(61, 61, 61))
                    .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlRelationshipsLayout.setVerticalGroup(
            pnlRelationshipsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRelationshipsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 293, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlNextButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(90, Short.MAX_VALUE))
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlRelationships.TabConstraints.tabTitle"), pnlRelationships); // NOI18N

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
            .add(0, 685, Short.MAX_VALUE)
        );
        pnlMapLayout.setVerticalGroup(
            pnlMapLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 434, Short.MAX_VALUE)
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlMap.TabConstraints.tabTitle"), pnlMap); // NOI18N

        pnlHistory.setName("pnlHistory"); // NOI18N

        org.jdesktop.layout.GroupLayout pnlHistoryLayout = new org.jdesktop.layout.GroupLayout(pnlHistory);
        pnlHistory.setLayout(pnlHistoryLayout);
        pnlHistoryLayout.setHorizontalGroup(
            pnlHistoryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 685, Short.MAX_VALUE)
        );
        pnlHistoryLayout.setVerticalGroup(
            pnlHistoryLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 434, Short.MAX_VALUE)
        );

        tabsMain.addTab(bundle.getString("SLPropertyPanel.pnlHistory.TabConstraints.tabTitle"), pnlHistory); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("SLPropertyPanel.headerPanel.titleText")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
            .add(jToolBar5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
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
                .add(tabsMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 462, Short.MAX_VALUE)
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

    private void menuOpenParentBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenParentBaUnitActionPerformed
        btnOpenParentActionPerformed(evt);
    }//GEN-LAST:event_menuOpenParentBaUnitActionPerformed

    private void menuAddParentBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddParentBaUnitActionPerformed
        btnAddParentActionPerformed(evt);
    }//GEN-LAST:event_menuAddParentBaUnitActionPerformed

    private void menuRemoveParentBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveParentBaUnitActionPerformed
        btnRemoveParentActionPerformed(evt);
    }//GEN-LAST:event_menuRemoveParentBaUnitActionPerformed

    private void menuOpenChildBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenChildBaUnitActionPerformed
        btnOpenChildActionPerformed(evt);
    }//GEN-LAST:event_menuOpenChildBaUnitActionPerformed

    private void btnTerminateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTerminateActionPerformed
        terminateBaUnit();
    }//GEN-LAST:event_btnTerminateActionPerformed

    private void btnPrintBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintBaUnitActionPerformed
        print();
    }//GEN-LAST:event_btnPrintBaUnitActionPerformed

    private void menuAddParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddParcelActionPerformed
        addParcel(true);
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

    private void btnOpenChildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenChildActionPerformed
        openPropertyForm(baUnitBean1.getSelectedChildBaUnit());
    }//GEN-LAST:event_btnOpenChildActionPerformed

    private void btnRemoveParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveParentActionPerformed
        baUnitBean1.removeSelectedParentBaUnit();
    }//GEN-LAST:event_btnRemoveParentActionPerformed

    private void btnAddParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddParentActionPerformed
        addParentProperty();
    }//GEN-LAST:event_btnAddParentActionPerformed

    private void btnOpenParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenParentActionPerformed
        openPropertyForm(baUnitBean1.getSelectedParentBaUnit());
    }//GEN-LAST:event_btnOpenParentActionPerformed

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
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.VIEW);
        }
    }//GEN-LAST:event_tableRightsMouseClicked

    private void btnRemoveParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveParcelActionPerformed
        removeParcel();
    }//GEN-LAST:event_btnRemoveParcelActionPerformed

    private void btnSearchParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchParcelActionPerformed
        addParcel(false);
    }//GEN-LAST:event_btnSearchParcelActionPerformed

    private void btnAddParcelFromApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddParcelFromApplicationActionPerformed
        openApplicationParcelsForm();
    }//GEN-LAST:event_btnAddParcelFromApplicationActionPerformed

    private void btnAddParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddParcelActionPerformed
        addParcel(true);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel areaPanel;
    private org.sola.clients.beans.administrative.BaUnitAreaBean baUnitAreaBean1;
    private org.sola.clients.beans.administrative.BaUnitBean baUnitBean1;
    private org.sola.clients.beans.referencedata.RrrTypeListBean baUnitRrrTypes;
    private javax.swing.JButton btnAddNotation;
    private javax.swing.JButton btnAddParcel;
    private javax.swing.JButton btnAddParcelFromApplication;
    private javax.swing.JButton btnAddParent;
    private javax.swing.JButton btnChangeRight;
    private javax.swing.JButton btnCreateRight;
    private org.sola.clients.swing.common.buttons.BtnEdit btnEditNotation;
    private javax.swing.JButton btnEditRight;
    private javax.swing.JButton btnExtinguish;
    private javax.swing.JButton btnOpenChild;
    private javax.swing.JButton btnOpenParent;
    private javax.swing.JButton btnPrintBaUnit;
    private javax.swing.JButton btnRemoveNotation;
    private javax.swing.JButton btnRemoveParcel;
    private javax.swing.JButton btnRemoveParent;
    private javax.swing.JButton btnRemoveRight;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearchParcel;
    private javax.swing.JButton btnTerminate;
    private javax.swing.JButton btnViewHistoricRight;
    private org.sola.clients.swing.common.buttons.BtnView btnViewNotation;
    private javax.swing.JButton btnViewRight;
    private javax.swing.JComboBox cbxRightType;
    private org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel documentsPanel1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToolBar jToolBar6;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JToolBar jToolBar8;
    private javax.swing.JLabel labArea;
    private javax.swing.JMenuItem menuAddNotation;
    private javax.swing.JMenuItem menuAddParcel;
    private javax.swing.JMenuItem menuAddParentBaUnit;
    private javax.swing.JMenuItem menuEditNotation;
    private javax.swing.JMenuItem menuEditRight;
    private javax.swing.JMenuItem menuExtinguishRight;
    private javax.swing.JMenuItem menuOpenChildBaUnit;
    private javax.swing.JMenuItem menuOpenParentBaUnit;
    private javax.swing.JMenuItem menuRemoveNotation;
    private javax.swing.JMenuItem menuRemoveParcel;
    private javax.swing.JMenuItem menuRemoveParentBaUnit;
    private javax.swing.JMenuItem menuRemoveRight;
    private javax.swing.JMenuItem menuVaryRight;
    private javax.swing.JMenuItem menuViewNotation;
    private javax.swing.JMenuItem menuViewRight;
    private javax.swing.JPanel pnlGeneral;
    private javax.swing.JPanel pnlHistory;
    private javax.swing.JPanel pnlInterests;
    private javax.swing.JPanel pnlMap;
    private javax.swing.JPanel pnlNextButton;
    private javax.swing.JPanel pnlNotes;
    private javax.swing.JPanel pnlParcels;
    private javax.swing.JPanel pnlRelationships;
    private javax.swing.JPopupMenu popupChildBaUnits;
    private javax.swing.JPopupMenu popupNotations;
    private javax.swing.JPopupMenu popupParcels;
    private javax.swing.JPopupMenu popupParentBaUnits;
    private javax.swing.JPopupMenu popupRights;
    private org.sola.clients.beans.referencedata.RrrTypeListBean rrrTypes;
    private javax.swing.JTable tableChildBaUnits;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableNotations;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableParcels;
    private javax.swing.JTable tableParentBaUnits;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRights;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRightsHistory;
    private javax.swing.JTabbedPane tabsMain;
    private javax.swing.JFormattedTextField txtArea;
    private javax.swing.JTextField txtBaUnitStatus;
    private javax.swing.JTextField txtEstateType;
    private javax.swing.JTextField txtLastPart;
    private javax.swing.JTextArea txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
