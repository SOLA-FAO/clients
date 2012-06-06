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
package org.sola.clients.swing.desktop.administrative;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import net.sf.jasperreports.engine.JasperPrint;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.administrative.BaUnitNotationBean;
import org.sola.clients.beans.administrative.RelatedBaUnitInfoBean;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.*;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceListBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.ReportViewerForm;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForBaUnit;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.application.ApplicationDocumentsForm;
import org.sola.clients.swing.ui.renderers.LockCellRenderer;
import org.sola.clients.swing.ui.renderers.SimpleComboBoxRenderer;
import org.sola.clients.swing.ui.renderers.TableCellListRenderer;
import org.sola.clients.swing.ui.source.DocumentsPanel;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;

/**
 * This form is used to manage property object ({@codeBaUnit}).
 * {@link BaUnitBean} is used to bind data on the form.
 */
public class PropertyPanel extends ContentPanel {

    /**
     * Listens for events of different right forms, to add created right into
     * the list of rights or update existing one.
     */
    private class RightFormListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // Add new RRR
            if (evt.getNewValue() != null) {
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
    private ApplicationDocumentsForm applicationDocumentsForm;
    private boolean readOnly = false;
    java.util.ResourceBundle resourceBundle;
    private PropertyChangeListener newPropertyWizardListener;

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
     * Creates documents table to show paper title documents.
     */
    private DocumentsPanel createDocumentsPanel() {
        DocumentsPanel panel;
        if (baUnitBean1 != null) {
            panel = new DocumentsPanel(baUnitBean1.getSourceList());
        } else {
            panel = new DocumentsPanel();
        }
        return panel;
    }

    /**
     * Form constructor. Creates and open form in read only mode.
     *
     * @param nameFirstPart First part of the property code.
     * @param nameLastPart Last part of the property code.
     */
    public PropertyPanel(String nameFirstPart, String nameLastPart) {
        this(null, null, nameFirstPart, nameLastPart, true);
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
    public PropertyPanel(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService,
            String nameFirstPart, String nameLastPart, boolean readOnly) {
        this.readOnly = readOnly || !SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE);
        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        this.nameFirstPart = nameFirstPart;
        this.nameLastPart = nameLastPart;
        resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle");
        initComponents();
        portInit();
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
    public PropertyPanel(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService,
            BaUnitBean baUnitBean, boolean readOnly) {
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
        portInit();
    }

    /**
     * Makes post initialization tasks.
     */
    private void portInit() {
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

        documentsPanel1.getSourceListBean().addPropertyChangeListener(new PropertyChangeListener() {

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

        if (nameFirstPart != null && nameLastPart != null) {
            headerPanel.setTitleText(String.format(
                    resourceBundle.getString("PropertyPanel.existingProperty.Text"),
                    nameFirstPart, nameLastPart));
        } else {
            headerPanel.setTitleText(resourceBundle.getString("PropertyPanel.newProperty.Text"));
        }

        if (applicationBean != null && applicationService != null) {
            headerPanel.setTitleText(String.format("%s, %s",
                    headerPanel.getTitleText(),
                    String.format(resourceBundle.getString("PropertyPanel.applicationInfo.Text"),
                    applicationService.getRequestType().getDisplayValue(), applicationBean.getNr())));
        }

        btnSave.setEnabled(!readOnly);
        txtName.setEditable(!readOnly);
        customizeRightsButtons(null);
        customizeNotationButtons(null);
        customizeRightTypesList();
        customizeParcelButtons(null);
        customizePaperTitleButtons(null);
        customizePrintButton();
        customizeParentPropertyButtons();
        customizeChildPropertyButtons();
        customizeTerminationButton();
    }

    /**
     * Shows {@link NewPropertyWizardPanel} to select parent property.
     */
    private void showNewTitleWizard(boolean showMessage) {
        if (baUnitBean1 == null || (baUnitBean1.getStatusCode() != null
                && !baUnitBean1.getStatusCode().equals(StatusConstants.PENDING))) {
            return;
        }

        if (!showMessage || (showMessage && MessageUtility.displayMessage(ClientMessage.BAUNIT_SELECT_EXISTING_PROPERTY) == MessageUtility.BUTTON_ONE)) {
            // Open selection form
            if (getMainContentPanel() != null) {
                if (newPropertyWizardListener == null) {
                    newPropertyWizardListener = new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName().equals(NewPropertyWizardPanel.SELECTED_RESULT_PROPERTY)) {
                                if (addParentProperty((Object[]) evt.getNewValue())) {
//                                        && MessageUtility.displayMessage(ClientMessage.BAUNIT_SELECT_EXISTING_PROPERTY_AGAIN) == MessageUtility.BUTTON_ONE) {
//                                    showNewTitleWizard(false);
                                }
                            }
                        }
                    };
                }

                SolaTask t = new SolaTask<Void, Void>() {

                    @Override
                    public Void doTask() {
                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTYLINK));
                        boolean allowSelection = true;
                        if (applicationService != null) {
                            allowSelection = !applicationService.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_NEW_DIGITAL_TITLE);
                        }
                        NewPropertyWizardPanel newPropertyWizardPanel = new NewPropertyWizardPanel(applicationBean, allowSelection);
                        newPropertyWizardPanel.addPropertyChangeListener(newPropertyWizardListener);
                        getMainContentPanel().addPanel(newPropertyWizardPanel, MainContentPanel.CARD_NEW_PROPERTY_WIZARD, true);
                        return null;
                    }
                };
                TaskManager.getInstance().runTask(t);
            }
        }
    }

    public void showPriorTitileMessage() {
        if ((baUnitBean1.getNameFirstpart() == null || baUnitBean1.getNameFirstpart().length() < 1)
                && (baUnitBean1.getNameLastpart() == null || baUnitBean1.getNameLastpart().length() < 1)) {
            showNewTitleWizard(true);
        }
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

        tabsMain.setSelectedIndex(tabsMain.indexOfComponent(pnlPriorProperties));
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

    /**
     * Enables or disables paper title buttons, depending on the form state.
     */
    private void customizePaperTitleButtons(SourceBean source) {
        if (source != null && source.getArchiveDocument() != null) {
            btnViewPaperTitle.setEnabled(true);
        } else {
            btnViewPaperTitle.setEnabled(false);
        }
        btnLinkPaperTitle.setEnabled(!readOnly);
    }

    /**
     * Enables or disables notation buttons, depending on the form state.
     */
    private void customizeNotationButtons(BaUnitNotationBean notation) {
        if (notation == null || !notation.getStatusCode().equals(StatusConstants.PENDING)
                || notation.getBaUnitId() == null
                || !notation.getBaUnitId().equals(baUnitBean1.getId())
                || notation.isLocked() || readOnly) {
            btnRemoveNotation.setEnabled(false);
        } else {
            btnRemoveNotation.setEnabled(true);
        }
        btnAddNotation.setEnabled(!readOnly);
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
                || !applicationService.getRequestType().getTypeActionCode().equals(TypeActionBean.CODE_CANCEL)) {
            enabled = false;
        }

        // Determine what should be shown on the button, terminate or cancelling of termination.
        if (baUnitBean1.getPendingActionCode() != null && baUnitBean1.getPendingActionCode().equals(TypeActionBean.CODE_CANCEL)) {
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
     * Opens {@link ReportViewerForm} to display report.
     */
    private void showReport(JasperPrint report) {
        ReportViewerForm form = new ReportViewerForm(report);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }

    /**
     * Open form to add new parcel.
     */
    private void addParcel() {

        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getNewValue() != null) {
                    baUnitBean1.getCadastreObjectList().addAsNew((CadastreObjectBean) e.getNewValue());
                }
            }
        };

        CreateParcelForm parcelForm = new CreateParcelForm(null, true);

        parcelForm.setLocationRelativeTo(this);
        parcelForm.addPropertyChangeListener(CreateParcelForm.SELECTED_PARCEL, listener);
        parcelForm.setVisible(true);
        parcelForm.removePropertyChangeListener(CreateParcelForm.SELECTED_PARCEL, listener);

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
        if (baUnitBean1.addBaUnitNotation(txtNotationText.getText())) {
            txtNotationText.setText(null);
        }
    }

    /**
     * Removes selected notation.
     */
    private void removeNotation() {
        if (MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            baUnitBean1.removeSelectedBaUnitNotation();
        }
    }

    /**
     * Opens paper title attachment.
     */
    private void viewDocument() {

        if (documentsPanel1.getSourceListBean().getSelectedSource() != null) {
            documentsPanel1.getSourceListBean().getSelectedSource().openDocument();
        }
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
     * Links document as a paper title on the BaUnit object.
     */
    private void linkDocument() {
        openDocumentsForm();
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
        if (action == RrrBean.RRR_ACTION.NEW
                && rrrTypes.getSelectedRrrType() == null) {
            return;
        }

        if (rrrBean == null) {
            rrrBean = new RrrBean();
            rrrBean.setTypeCode(rrrTypes.getSelectedRrrType().getCode());
        }

        RightFormListener rightFormListener = new RightFormListener();
        ContentPanel panel;
        String cardName = MainContentPanel.CARD_SIMPLE_RIGHT;
        String rrrCode = rrrBean.getRrrType().getCode();

        if (rrrCode.equals(RrrBean.CODE_MORTGAGE)) {
            panel = new MortgagePanel(rrrBean, applicationBean, applicationService, action);
            cardName = MainContentPanel.CARD_MORTGAGE;
        } else if (rrrCode.equalsIgnoreCase(RrrBean.CODE_AGRI_ACTIVITY)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_COMMON_OWNERSHIP)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_CUSTOMARY_TYPE)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_FIREWOOD)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_FISHING)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_GRAZING)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_LEASE)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_OCCUPATION)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_OWNERSHIP_ASSUMED)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_SUPERFICIES)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_TENANCY)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_USUFRUCT)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_WATERRIGHTS)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_ADMIN_PUBLIC_SERVITUDE)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_MONUMENT)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_LIFE_ESTATE)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_CAVEAT)){
            panel = new SimpleOwhershipPanel(rrrBean, applicationBean, applicationService, action);
            cardName = MainContentPanel.CARD_SIMPLE_OWNERSHIP;
        } else if (rrrCode.equalsIgnoreCase(RrrBean.CODE_OWNERSHIP)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_STATE_OWNERSHIP)
                || rrrCode.equalsIgnoreCase(RrrBean.CODE_APARTMENT)) {
            panel = new OwnershipPanel(rrrBean, applicationBean, applicationService, action);
            cardName = MainContentPanel.CARD_OWNERSHIP;
        } else {
            panel = new SimpleRightPanel(rrrBean, applicationBean, applicationService, action);
        }

        panel.addPropertyChangeListener(SimpleRightPanel.UPDATED_RRR, rightFormListener);
        getMainContentPanel().addPanel(panel, cardName, true);
    }

    private void saveBaUnit(final boolean showMessage, final boolean closeOnSave) {
        if (baUnitBean1.validate(true).size() > 0) {
            return;
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

    private void terminateBaUnit(){
        if (baUnitBean1.getPendingActionCode() != null && baUnitBean1.getPendingActionCode().equals(TypeActionBean.CODE_CANCEL)) {
            saveBaUnit();
            baUnitBean1.cancelBaUnitTermination();
            MessageUtility.displayMessage(ClientMessage.BAUNIT_TERMINATION_CANCELED);
            customizeForm();
            saveBaUnitState();
        } else {
            if (MessageUtility.displayMessage(ClientMessage.BAUNIT_CONFIRM_TERMINATION) == MessageUtility.BUTTON_ONE) {
                saveBaUnit();
                baUnitBean1.terminateBaUnit(applicationService.getId());
                MessageUtility.displayMessage(ClientMessage.BAUNIT_TERMINATED);
                customizeForm();
                saveBaUnitState();
            }
        }
    }
    
    /**
     * Opens form to select or create document to be used as a paper title
     * document.
     */
    private void openDocumentsForm() {
        if (applicationDocumentsForm != null) {
            applicationDocumentsForm.dispose();
        }

        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                SourceBean document = null;
                if (e.getPropertyName().equals(ApplicationDocumentsForm.SELECTED_SOURCE)
                        && e.getNewValue() != null) {
                    document = (SourceBean) e.getNewValue();
                    baUnitBean1.createPaperTitle(document);
                }
            }
        };

        applicationDocumentsForm = new ApplicationDocumentsForm(applicationBean, null, true);
        applicationDocumentsForm.setLocationRelativeTo(this);
        applicationDocumentsForm.addPropertyChangeListener(
                SourceListBean.SELECTED_SOURCE_PROPERTY, listener);
        applicationDocumentsForm.setVisible(true);
        applicationDocumentsForm.removePropertyChangeListener(
                SourceListBean.SELECTED_SOURCE_PROPERTY, listener);
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
                    PropertyPanel propertyPnl = new PropertyPanel(
                            relatedBaUnit.getRelatedBaUnit().getNameFirstpart(),
                            relatedBaUnit.getRelatedBaUnit().getNameLastpart());
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
        menuRemoveNotation = new javax.swing.JMenuItem();
        popupParentBaUnits = new javax.swing.JPopupMenu();
        menuOpenParentBaUnit = new javax.swing.JMenuItem();
        menuAddParentBaUnit = new javax.swing.JMenuItem();
        menuRemoveParentBaUnit = new javax.swing.JMenuItem();
        popupChildBaUnits = new javax.swing.JPopupMenu();
        menuOpenChildBaUnit = new javax.swing.JMenuItem();
        jToolBar5 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnTerminate = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnPrintBaUnit = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabsMain = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFirstPart = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtLastPart = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtEstateType = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtBaUnitStatus = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jToolBar4 = new javax.swing.JToolBar();
        btnViewPaperTitle = new javax.swing.JButton();
        btnLinkPaperTitle = new javax.swing.JButton();
        documentsPanel1 = createDocumentsPanel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableParcels = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddParcel = new javax.swing.JButton();
        btnRemoveParcel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
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
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tableOwnership = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableNotations = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar3 = new javax.swing.JToolBar();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnAddNotation = new javax.swing.JButton();
        btnRemoveNotation = new javax.swing.JButton();
        txtNotationText = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        pnlPriorProperties = new javax.swing.JPanel();
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
        mapPanel = new javax.swing.JPanel();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();

        popupParcels.setName("popupParcels"); // NOI18N

        menuAddParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        menuAddParcel.setText(bundle.getString("PropertyPanel.menuAddParcel.text")); // NOI18N
        menuAddParcel.setName("menuAddParcel"); // NOI18N
        menuAddParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddParcelActionPerformed(evt);
            }
        });
        popupParcels.add(menuAddParcel);

        menuRemoveParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveParcel.setText(bundle.getString("PropertyPanel.btnRemoveParcel.text")); // NOI18N
        menuRemoveParcel.setToolTipText(bundle.getString("PropertyPanel.menuRemoveParcel.toolTipText")); // NOI18N
        menuRemoveParcel.setName("menuRemoveParcel"); // NOI18N
        menuRemoveParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveParcelActionPerformed(evt);
            }
        });
        popupParcels.add(menuRemoveParcel);

        popupRights.setName("popupRights"); // NOI18N

        menuVaryRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/vary.png"))); // NOI18N
        menuVaryRight.setText(bundle.getString("PropertyPanel.menuVaryRight.text")); // NOI18N
        menuVaryRight.setName("menuVaryRight"); // NOI18N
        menuVaryRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuVaryRightActionPerformed(evt);
            }
        });
        popupRights.add(menuVaryRight);

        menuExtinguishRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/flag.png"))); // NOI18N
        menuExtinguishRight.setText(bundle.getString("PropertyPanel.menuExtinguishRight.text")); // NOI18N
        menuExtinguishRight.setToolTipText(bundle.getString("PropertyPanel.menuExtinguishRight.toolTipText")); // NOI18N
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
        menuViewRight.setText(bundle.getString("PropertyPanel.menuViewRight.text")); // NOI18N
        menuViewRight.setName("menuViewRight"); // NOI18N
        popupRights.add(menuViewRight);

        menuEditRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/document--pencil.png"))); // NOI18N
        menuEditRight.setText(bundle.getString("PropertyPanel.menuEditRight.text")); // NOI18N
        menuEditRight.setName("menuEditRight"); // NOI18N
        menuEditRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditRightActionPerformed(evt);
            }
        });
        popupRights.add(menuEditRight);

        menuRemoveRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveRight.setText(bundle.getString("PropertyPanel.menuRemoveRight.text")); // NOI18N
        menuRemoveRight.setName("menuRemoveRight"); // NOI18N
        menuRemoveRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveRightActionPerformed(evt);
            }
        });
        popupRights.add(menuRemoveRight);

        popupNotations.setName("popupNotations"); // NOI18N

        menuRemoveNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveNotation.setText(bundle.getString("PropertyPanel.menuRemoveNotation.text")); // NOI18N
        menuRemoveNotation.setName("menuRemoveNotation"); // NOI18N
        menuRemoveNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveNotationActionPerformed(evt);
            }
        });
        popupNotations.add(menuRemoveNotation);

        popupParentBaUnits.setName("popupParentBaUnits"); // NOI18N

        menuOpenParentBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuOpenParentBaUnit.setText(bundle.getString("PropertyPanel.menuOpenParentBaUnit.text")); // NOI18N
        menuOpenParentBaUnit.setName("menuOpenParentBaUnit"); // NOI18N
        menuOpenParentBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenParentBaUnitActionPerformed(evt);
            }
        });
        popupParentBaUnits.add(menuOpenParentBaUnit);

        menuAddParentBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAddParentBaUnit.setText(bundle.getString("PropertyPanel.menuAddParentBaUnit.text")); // NOI18N
        menuAddParentBaUnit.setName("menuAddParentBaUnit"); // NOI18N
        menuAddParentBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddParentBaUnitActionPerformed(evt);
            }
        });
        popupParentBaUnits.add(menuAddParentBaUnit);

        menuRemoveParentBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveParentBaUnit.setText(bundle.getString("PropertyPanel.menuRemoveParentBaUnit.text")); // NOI18N
        menuRemoveParentBaUnit.setName("menuRemoveParentBaUnit"); // NOI18N
        menuRemoveParentBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveParentBaUnitActionPerformed(evt);
            }
        });
        popupParentBaUnits.add(menuRemoveParentBaUnit);

        popupChildBaUnits.setName("popupChildBaUnits"); // NOI18N

        menuOpenChildBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuOpenChildBaUnit.setText(bundle.getString("PropertyPanel.menuOpenChildBaUnit.text")); // NOI18N
        menuOpenChildBaUnit.setName("menuOpenChildBaUnit"); // NOI18N
        menuOpenChildBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenChildBaUnitActionPerformed(evt);
            }
        });
        popupChildBaUnits.add(menuOpenChildBaUnit);

        setHeaderPanel(headerPanel);
        setHelpTopic(bundle.getString("PropertyPanel.helpTopic")); // NOI18N
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
        btnSave.setText(bundle.getString("PropertyPanel.btnSave.text")); // NOI18N
        btnSave.setToolTipText(bundle.getString("PropertyPanel.btnSave.toolTipText")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar5.add(btnSave);

        btnTerminate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/stop.png"))); // NOI18N
        btnTerminate.setText(bundle.getString("PropertyPanel.btnTerminate.text")); // NOI18N
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
        btnPrintBaUnit.setText(bundle.getString("PropertyPanel.btnPrintBaUnit.text")); // NOI18N
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

        jScrollPane6.setBorder(null);
        jScrollPane6.setName("jScrollPane6"); // NOI18N

        tabsMain.setName("tabsMain"); // NOI18N
        tabsMain.setPreferredSize(new java.awt.Dimension(494, 300));

        jPanel7.setName("jPanel7"); // NOI18N

        jPanel13.setName("jPanel13"); // NOI18N
        jPanel13.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        jPanel10.setName("jPanel10"); // NOI18N

        jLabel1.setText(bundle.getString("PropertyPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtFirstPart.setEditable(false);
        txtFirstPart.setName("txtFirstPart"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"), txtFirstPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jLabel1)
                .addContainerGap(84, Short.MAX_VALUE))
            .add(txtFirstPart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtFirstPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel10);

        jPanel9.setName("jPanel9"); // NOI18N

        jLabel2.setText(bundle.getString("PropertyPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtLastPart.setEditable(false);
        txtLastPart.setName("txtLastPart"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"), txtLastPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jLabel2)
                .addContainerGap(85, Short.MAX_VALUE))
            .add(txtLastPart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtLastPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel9);

        jPanel11.setName("jPanel11"); // NOI18N

        jLabel4.setText(bundle.getString("PropertyPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        txtEstateType.setEditable(false);
        txtEstateType.setName("txtEstateType"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${estateType}"), txtEstateType, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jLabel4)
                .addContainerGap(72, Short.MAX_VALUE))
            .add(txtEstateType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtEstateType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel11);

        jPanel6.setName("jPanel6"); // NOI18N

        jLabel7.setText(bundle.getString("PropertyPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtBaUnitStatus.setEditable(false);
        txtBaUnitStatus.setName("txtBaUnitStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtBaUnitStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jLabel7)
                .addContainerGap(101, Short.MAX_VALUE))
            .add(txtBaUnitStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtBaUnitStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel13.add(jPanel6);

        jPanel12.setName("jPanel12"); // NOI18N

        jLabel5.setText(bundle.getString("PropertyPanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txtName.setName("txtName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(jLabel5)
                .addContainerGap(517, Short.MAX_VALUE))
            .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);
        jToolBar4.setName("jToolBar4"); // NOI18N

        btnViewPaperTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewPaperTitle.setText(bundle.getString("PropertyPanel.btnViewPaperTitle.text")); // NOI18N
        btnViewPaperTitle.setName("btnViewPaperTitle"); // NOI18N
        btnViewPaperTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewPaperTitleActionPerformed(evt);
            }
        });
        jToolBar4.add(btnViewPaperTitle);

        btnLinkPaperTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/document-link.png"))); // NOI18N
        btnLinkPaperTitle.setText(bundle.getString("PropertyPanel.btnLinkPaperTitle.text")); // NOI18N
        btnLinkPaperTitle.setName("btnLinkPaperTitle"); // NOI18N
        btnLinkPaperTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLinkPaperTitleActionPerformed(evt);
            }
        });
        jToolBar4.add(btnLinkPaperTitle);

        documentsPanel1.setName("documentsPanel1"); // NOI18N

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("PropertyPanel.groupPanel1.titleText")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, documentsPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, groupPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jToolBar4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(groupPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jToolBar4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(documentsPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        tabsMain.addTab(bundle.getString("PropertyPanel.jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableParcels.setComponentPopupMenu(popupParcels);
        tableParcels.setName("tableParcels"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cadastreObjectFilteredList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableParcels);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
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
        tableParcels.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title0")); // NOI18N
        tableParcels.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title1")); // NOI18N
        tableParcels.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title3")); // NOI18N
        tableParcels.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title2")); // NOI18N
        tableParcels.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title4")); // NOI18N
        tableParcels.getColumnModel().getColumn(5).setPreferredWidth(40);
        tableParcels.getColumnModel().getColumn(5).setMaxWidth(40);
        tableParcels.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("PropertyPanel.tableParcels.columnModel.title5")); // NOI18N
        tableParcels.getColumnModel().getColumn(5).setCellRenderer(new LockCellRenderer());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddParcel.setText(bundle.getString("PropertyPanel.btnAddParcel.text")); // NOI18N
        btnAddParcel.setName("btnAddParcel"); // NOI18N
        btnAddParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddParcel);

        btnRemoveParcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveParcel.setText(bundle.getString("PropertyPanel.btnRemoveParcel.text")); // NOI18N
        btnRemoveParcel.setToolTipText(bundle.getString("PropertyPanel.btnRemoveParcel.toolTipText")); // NOI18N
        btnRemoveParcel.setName("btnRemoveParcel"); // NOI18N
        btnRemoveParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveParcel);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                        .add(20, 20, 20))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("PropertyPanel.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tableRights.setComponentPopupMenu(popupRights);
        tableRights.setName("tableRights"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrFilteredList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableRights);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rrrType.displayValue}"));
        columnBinding.setColumnName("Rrr Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"));
        columnBinding.setColumnName("Registration Date");
        columnBinding.setColumnClass(java.util.Date.class);
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
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedRight}"), tableRights, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tableRights.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableRightsMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableRights);
        tableRights.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyPanel.tableRights.columnModel.title0")); // NOI18N
        tableRights.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableRights.columnModel.title1")); // NOI18N
        tableRights.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableRights.columnModel.title2")); // NOI18N
        tableRights.getColumnModel().getColumn(3).setPreferredWidth(40);
        tableRights.getColumnModel().getColumn(3).setMaxWidth(40);
        tableRights.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.tableRights.columnModel.title3")); // NOI18N
        tableRights.getColumnModel().getColumn(3).setCellRenderer(new LockCellRenderer());

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        jLabel16.setText(bundle.getString("PropertyPanel.jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        jToolBar2.add(jLabel16);

        filler1.setName("filler1"); // NOI18N
        jToolBar2.add(filler1);

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
        btnCreateRight.setText(bundle.getString("PropertyPanel.btnCreateRight.text")); // NOI18N
        btnCreateRight.setName("btnCreateRight"); // NOI18N
        btnCreateRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateRightActionPerformed(evt);
            }
        });
        jToolBar2.add(btnCreateRight);

        btnChangeRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/vary.png"))); // NOI18N
        btnChangeRight.setText(bundle.getString("PropertyPanel.btnChangeRight.text")); // NOI18N
        btnChangeRight.setName("btnChangeRight"); // NOI18N
        btnChangeRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeRightActionPerformed(evt);
            }
        });
        jToolBar2.add(btnChangeRight);

        btnExtinguish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/flag.png"))); // NOI18N
        btnExtinguish.setText(bundle.getString("PropertyPanel.btnExtinguish.text")); // NOI18N
        btnExtinguish.setToolTipText(bundle.getString("PropertyPanel.btnExtinguish.toolTipText")); // NOI18N
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
        btnViewRight.setText(bundle.getString("PropertyPanel.btnViewRight.text")); // NOI18N
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
        btnEditRight.setText(bundle.getString("PropertyPanel.btnEditRight.text")); // NOI18N
        btnEditRight.setName("btnEditRight"); // NOI18N
        btnEditRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditRightActionPerformed(evt);
            }
        });
        jToolBar2.add(btnEditRight);

        btnRemoveRight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveRight.setText(bundle.getString("PropertyPanel.btnRemoveRight.text")); // NOI18N
        btnRemoveRight.setName("btnRemoveRight"); // NOI18N
        btnRemoveRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveRightActionPerformed(evt);
            }
        });
        jToolBar2.add(btnRemoveRight);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jToolBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("PropertyPanel.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel3.setName("jPanel3"); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        tableOwnership.setName("tableOwnership"); // NOI18N

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
        jScrollPane5.setViewportView(tableOwnership);
        tableOwnership.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyPanel.tableOwnership.columnModel.title0")); // NOI18N
        tableOwnership.getColumnModel().getColumn(0).setCellRenderer(new TableCellListRenderer("getName", "getLastName"));
        tableOwnership.getColumnModel().getColumn(1).setPreferredWidth(70);
        tableOwnership.getColumnModel().getColumn(1).setMaxWidth(70);
        tableOwnership.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableOwnership.columnModel.title1")); // NOI18N
        tableOwnership.getColumnModel().getColumn(2).setPreferredWidth(130);
        tableOwnership.getColumnModel().getColumn(2).setMaxWidth(130);
        tableOwnership.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableOwnership.columnModel.title2")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("PropertyPanel.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tableNotations.setComponentPopupMenu(popupNotations);
        tableNotations.setName("tableNotations"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${allBaUnitNotationList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, eLProperty, tableNotations);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${referenceNr}"));
        columnBinding.setColumnName("Reference Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notationText}"));
        columnBinding.setColumnName("Notation Text");
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
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedBaUnitNotation}"), tableNotations, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(tableNotations);
        tableNotations.getColumnModel().getColumn(0).setMinWidth(80);
        tableNotations.getColumnModel().getColumn(0).setPreferredWidth(80);
        tableNotations.getColumnModel().getColumn(0).setMaxWidth(80);
        tableNotations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title0")); // NOI18N
        tableNotations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title1")); // NOI18N
        tableNotations.getColumnModel().getColumn(2).setPreferredWidth(180);
        tableNotations.getColumnModel().getColumn(2).setMaxWidth(180);
        tableNotations.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title2")); // NOI18N
        tableNotations.getColumnModel().getColumn(3).setPreferredWidth(40);
        tableNotations.getColumnModel().getColumn(3).setMaxWidth(40);
        tableNotations.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.tableNotations.columnModel.title3")); // NOI18N
        tableNotations.getColumnModel().getColumn(3).setCellRenderer(new LockCellRenderer());

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        filler4.setName("filler4"); // NOI18N
        jToolBar3.add(filler4);

        btnAddNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddNotation.setText(bundle.getString("PropertyPanel.btnAddNotation.text")); // NOI18N
        btnAddNotation.setName("btnAddNotation"); // NOI18N
        btnAddNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNotationActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAddNotation);

        btnRemoveNotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveNotation.setText(bundle.getString("PropertyPanel.btnRemoveNotation.text")); // NOI18N
        btnRemoveNotation.setName("btnRemoveNotation"); // NOI18N
        btnRemoveNotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveNotationActionPerformed(evt);
            }
        });
        jToolBar3.add(btnRemoveNotation);

        txtNotationText.setMinimumSize(new java.awt.Dimension(150, 20));
        txtNotationText.setName("txtNotationText"); // NOI18N

        jLabel15.setText(bundle.getString("PropertyPanel.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel15)
                        .add(9, 9, 9)
                        .add(txtNotationText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jToolBar3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 139, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jToolBar3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtNotationText, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel15)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("PropertyPanel.jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        pnlPriorProperties.setName("pnlPriorProperties"); // NOI18N

        jPanel14.setName("jPanel14"); // NOI18N
        jPanel14.setLayout(new java.awt.GridLayout(2, 1, 0, 15));

        jPanel8.setName("jPanel8"); // NOI18N

        jToolBar6.setFloatable(false);
        jToolBar6.setRollover(true);
        jToolBar6.setName("jToolBar6"); // NOI18N

        jLabel6.setFont(LafManager.getInstance().getLabFontBold());
        jLabel6.setText(bundle.getString("PropertyPanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        jToolBar6.add(jLabel6);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar6.add(jSeparator3);

        btnOpenParent.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenParent.setText(bundle.getString("PropertyPanel.btnOpenParent.text")); // NOI18N
        btnOpenParent.setToolTipText(bundle.getString("PropertyPanel.btnOpenParent.toolTipText")); // NOI18N
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
        btnAddParent.setText(bundle.getString("PropertyPanel.btnAddParent.text")); // NOI18N
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
        btnRemoveParent.setText(bundle.getString("PropertyPanel.btnRemoveParent.text")); // NOI18N
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
        tableParentBaUnits.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyPanel.tableParentBaUnits.columnModel.title0_1")); // NOI18N
        tableParentBaUnits.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableParentBaUnits.columnModel.title1_1")); // NOI18N
        tableParentBaUnits.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableParentBaUnits.columnModel.title2_1")); // NOI18N
        tableParentBaUnits.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.jTable1.columnModel.title4")); // NOI18N
        tableParentBaUnits.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("PropertyPanel.jTable1.columnModel.title3_1")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jToolBar6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel8);

        jPanel5.setName("jPanel5"); // NOI18N

        jToolBar7.setFloatable(false);
        jToolBar7.setRollover(true);
        jToolBar7.setName("jToolBar7"); // NOI18N

        jLabel3.setFont(LafManager.getInstance().getLabFontBold());
        jLabel3.setText(bundle.getString("PropertyPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        jToolBar7.add(jLabel3);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jToolBar7.add(jSeparator2);

        btnOpenChild.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenChild.setText(bundle.getString("PropertyPanel.btnOpenChild.text")); // NOI18N
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
        tableChildBaUnits.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyPanel.tableChildBaUnits.columnModel.title0_1")); // NOI18N
        tableChildBaUnits.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyPanel.tableChildBaUnits.columnModel.title1_1")); // NOI18N
        tableChildBaUnits.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyPanel.tableChildBaUnits.columnModel.title2_1")); // NOI18N
        tableChildBaUnits.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyPanel.tableChildBaUnits.columnModel.title3_1")); // NOI18N
        tableChildBaUnits.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("PropertyPanel.tableChildBaUnits.columnModel.title4")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jToolBar7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
            .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jToolBar7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
        );

        jPanel14.add(jPanel5);

        org.jdesktop.layout.GroupLayout pnlPriorPropertiesLayout = new org.jdesktop.layout.GroupLayout(pnlPriorProperties);
        pnlPriorProperties.setLayout(pnlPriorPropertiesLayout);
        pnlPriorPropertiesLayout.setHorizontalGroup(
            pnlPriorPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPriorPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlPriorPropertiesLayout.setVerticalGroup(
            pnlPriorPropertiesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPriorPropertiesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabsMain.addTab(bundle.getString("PropertyPanel.pnlPriorProperties.TabConstraints.tabTitle"), pnlPriorProperties); // NOI18N

        mapPanel.setName("mapPanel"); // NOI18N
        mapPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                mapPanelComponentShown(evt);
            }
        });

        org.jdesktop.layout.GroupLayout mapPanelLayout = new org.jdesktop.layout.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 594, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 325, Short.MAX_VALUE)
        );

        tabsMain.addTab(bundle.getString("PropertyPanel.mapPanel.TabConstraints.tabTitle"), mapPanel); // NOI18N

        jScrollPane6.setViewportView(tabsMain);

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("PropertyPanel.headerPanel.titleText")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(headerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
            .add(jToolBar5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(headerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToolBar5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
    saveBaUnit(true, false);
    customizeForm();
}//GEN-LAST:event_btnSaveActionPerformed

    private void tableRightsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableRightsMouseClicked
        if (evt.getClickCount() > 1 && baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.VIEW);
        }
    }//GEN-LAST:event_tableRightsMouseClicked

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
    if (this.mapControl == null) {
        this.mapControl = new ControlsBundleForBaUnit();
        //this.mapControl.setCadastreObjects(baUnitBean1.getCadastreObjectList());
        if (applicationBean != null) {
            this.mapControl.setApplicationId(this.applicationBean.getId());
        }
        this.mapPanel.setLayout(new BorderLayout());
        this.mapPanel.add(this.mapControl, BorderLayout.CENTER);
    }
}//GEN-LAST:event_formComponentShown

    private void btnAddParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddParentActionPerformed
        showNewTitleWizard(false);
    }//GEN-LAST:event_btnAddParentActionPerformed

    private void btnRemoveParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveParentActionPerformed
        baUnitBean1.removeSelectedParentBaUnit();
    }//GEN-LAST:event_btnRemoveParentActionPerformed

    private void btnOpenParentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenParentActionPerformed
        openPropertyForm(baUnitBean1.getSelectedParentBaUnit());
    }//GEN-LAST:event_btnOpenParentActionPerformed

    private void btnOpenChildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenChildActionPerformed
        openPropertyForm(baUnitBean1.getSelectedChildBaUnit());
    }//GEN-LAST:event_btnOpenChildActionPerformed

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

    private void btnViewRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewRightActionPerformed
        if (baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.VIEW);
        }
    }//GEN-LAST:event_btnViewRightActionPerformed

    private void btnPrintBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintBaUnitActionPerformed
        print();
    }//GEN-LAST:event_btnPrintBaUnitActionPerformed

    private void btnViewPaperTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewPaperTitleActionPerformed
        viewDocument();
    }//GEN-LAST:event_btnViewPaperTitleActionPerformed

    private void btnLinkPaperTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLinkPaperTitleActionPerformed
        linkDocument();
    }//GEN-LAST:event_btnLinkPaperTitleActionPerformed

    private void menuAddParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddParcelActionPerformed
        addParcel();
    }//GEN-LAST:event_menuAddParcelActionPerformed

    private void btnAddParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddParcelActionPerformed
        addParcel();
    }//GEN-LAST:event_btnAddParcelActionPerformed

    private void btnRemoveParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveParcelActionPerformed
        removeParcel();
    }//GEN-LAST:event_btnRemoveParcelActionPerformed

    private void menuRemoveParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveParcelActionPerformed
        removeParcel();
    }//GEN-LAST:event_menuRemoveParcelActionPerformed

    private void menuVaryRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuVaryRightActionPerformed
        varyRight();
    }//GEN-LAST:event_menuVaryRightActionPerformed

    private void btnChangeRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeRightActionPerformed
        varyRight();
    }//GEN-LAST:event_btnChangeRightActionPerformed

    private void menuExtinguishRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExtinguishRightActionPerformed
        extinguishRight();
    }//GEN-LAST:event_menuExtinguishRightActionPerformed

    private void btnExtinguishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExtinguishActionPerformed
        extinguishRight();
    }//GEN-LAST:event_btnExtinguishActionPerformed

    private void menuEditRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditRightActionPerformed
        editRight();
    }//GEN-LAST:event_menuEditRightActionPerformed

    private void btnEditRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditRightActionPerformed
        editRight();
    }//GEN-LAST:event_btnEditRightActionPerformed

    private void menuRemoveRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveRightActionPerformed
        removeRight();
    }//GEN-LAST:event_menuRemoveRightActionPerformed

    private void btnRemoveRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveRightActionPerformed
        removeRight();
    }//GEN-LAST:event_btnRemoveRightActionPerformed

    private void btnCreateRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateRightActionPerformed
        createRight();
    }//GEN-LAST:event_btnCreateRightActionPerformed

    private void menuRemoveNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveNotationActionPerformed
        removeNotation();
    }//GEN-LAST:event_menuRemoveNotationActionPerformed

    private void btnRemoveNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveNotationActionPerformed
        removeNotation();
    }//GEN-LAST:event_btnRemoveNotationActionPerformed

    private void btnAddNotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNotationActionPerformed
        addNotation();
    }//GEN-LAST:event_btnAddNotationActionPerformed

    private void mapPanelComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_mapPanelComponentShown
        this.mapControl.setCadastreObjects(baUnitBean1.getCadastreObjectList());
    }//GEN-LAST:event_mapPanelComponentShown

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.administrative.BaUnitBean baUnitBean1;
    private org.sola.clients.beans.referencedata.RrrTypeListBean baUnitRrrTypes;
    private javax.swing.JButton btnAddNotation;
    private javax.swing.JButton btnAddParcel;
    private javax.swing.JButton btnAddParent;
    private javax.swing.JButton btnChangeRight;
    private javax.swing.JButton btnCreateRight;
    private javax.swing.JButton btnEditRight;
    private javax.swing.JButton btnExtinguish;
    private javax.swing.JButton btnLinkPaperTitle;
    private javax.swing.JButton btnOpenChild;
    private javax.swing.JButton btnOpenParent;
    private javax.swing.JButton btnPrintBaUnit;
    private javax.swing.JButton btnRemoveNotation;
    private javax.swing.JButton btnRemoveParcel;
    private javax.swing.JButton btnRemoveParent;
    private javax.swing.JButton btnRemoveRight;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnTerminate;
    private javax.swing.JButton btnViewPaperTitle;
    private javax.swing.JButton btnViewRight;
    private javax.swing.JComboBox cbxRightType;
    private org.sola.clients.swing.ui.source.DocumentsPanel documentsPanel1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler4;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToolBar jToolBar6;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JMenuItem menuAddParcel;
    private javax.swing.JMenuItem menuAddParentBaUnit;
    private javax.swing.JMenuItem menuEditRight;
    private javax.swing.JMenuItem menuExtinguishRight;
    private javax.swing.JMenuItem menuOpenChildBaUnit;
    private javax.swing.JMenuItem menuOpenParentBaUnit;
    private javax.swing.JMenuItem menuRemoveNotation;
    private javax.swing.JMenuItem menuRemoveParcel;
    private javax.swing.JMenuItem menuRemoveParentBaUnit;
    private javax.swing.JMenuItem menuRemoveRight;
    private javax.swing.JMenuItem menuVaryRight;
    private javax.swing.JMenuItem menuViewRight;
    private javax.swing.JPanel pnlPriorProperties;
    private javax.swing.JPopupMenu popupChildBaUnits;
    private javax.swing.JPopupMenu popupNotations;
    private javax.swing.JPopupMenu popupParcels;
    private javax.swing.JPopupMenu popupParentBaUnits;
    private javax.swing.JPopupMenu popupRights;
    private org.sola.clients.beans.referencedata.RrrTypeListBean rrrTypes;
    private javax.swing.JTable tableChildBaUnits;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableNotations;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableOwnership;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableParcels;
    private javax.swing.JTable tableParentBaUnits;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRights;
    private javax.swing.JTabbedPane tabsMain;
    private javax.swing.JTextField txtBaUnitStatus;
    private javax.swing.JTextField txtEstateType;
    private javax.swing.JTextField txtFirstPart;
    private javax.swing.JTextField txtLastPart;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNotationText;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
