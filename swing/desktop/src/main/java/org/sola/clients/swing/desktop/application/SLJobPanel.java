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
package org.sola.clients.swing.desktop.application;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.swing.JTextField;
import net.sf.jasperreports.engine.JasperPrint;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationDocumentsHelperBean;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.party.PartySummaryListBean;
import org.sola.clients.beans.referencedata.*;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.system.PanelLauncherGroupBean;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.controls.AutoCompletion;
import org.sola.clients.swing.common.controls.TextSearch;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.SLDashBoardPanel;
import org.sola.clients.swing.desktop.administrative.PropertyHelper;
import org.sola.clients.swing.desktop.administrative.PropertyPanel;
import org.sola.clients.swing.desktop.reports.SysRegCertParamsForm;
import org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForApplicationLocation;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.PanelLauncher;
import org.sola.clients.swing.ui.renderers.*;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.clients.swing.ui.security.SecurityClassificationDialog;
import org.sola.clients.swing.ui.validation.ValidationResultForm;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.casemanagement.ApplicationTO;

/**
 * This form is used to create new application or edit existing one.
 * <p>
 * The following list of beans is used to bind the data on the form:<br />
 * {@link ApplicationBean}, <br />{@link RequestTypeListBean}, <br />
 * {@link PartySummaryListBean}, <br />{@link CommunicationTypeListBean}, <br />
 * {@link SourceTypeListBean}, <br />{@link ApplicationDocumentsHelperBean}</p>
 */
public class SLJobPanel extends ContentPanel {

    private ControlsBundleForApplicationLocation mapControl = null;
    public static final String APPLICATION_SAVED_PROPERTY = "applicationSaved";
    private String applicationID;
    ApplicationPropertyBean property;
    boolean saveInProgress = false;

    /**
     * This method is used by the form designer to create
     * {@link ApplicationBean}. It uses <code>applicationId</code> parameter
     * passed to the form constructor.<br />
     * <code>applicationId</code> should be initialized before
     * {@link ApplicationForm#initComponents} method call.
     */
    private ApplicationBean getApplicationBean() {
        if (appBean == null) {
            if (applicationID != null && !applicationID.equals("")) {
                ApplicationTO applicationTO = WSManager.getInstance().getCaseManagementService().getApplication(applicationID);
                appBean = TypeConverters.TransferObjectToBean(applicationTO, ApplicationBean.class, null);
            } else {
                appBean = new ApplicationBean();
            }
        }

        appBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ApplicationBean.APPLICATION_PROPERTY)) {
                    firePropertyChange(ApplicationBean.APPLICATION_PROPERTY, evt.getOldValue(), evt.getNewValue());
                }
            }
        });
        return appBean;
    }

    private DocumentsManagementExtPanel createDocumentsPanel() {
        if (documentsPanel == null) {
            if (appBean != null) {
                documentsPanel = new DocumentsManagementExtPanel(
                        appBean.getSourceList(), null, appBean.isEditingAllowed());
            } else {
                documentsPanel = new DocumentsManagementExtPanel();
            }
        }
        return documentsPanel;
    }

    /**
     * Default constructor to create new application.
     */
    public SLJobPanel() {
        this((String) null);
    }

    /**
     * This constructor is used to open existing application for editing.
     *
     * @param applicationId ID of application to open.
     */
    public SLJobPanel(String applicationId) {
        this.applicationID = applicationId;
        initComponents();
        postInit();
    }

    /**
     * This constructor is used to open existing application for editing.
     *
     * @param application {@link ApplicationBean} to show on the form.
     */
    public SLJobPanel(ApplicationBean application) {
        this.appBean = application;
        initComponents();
        postInit();
    }

    public ApplicationPropertyBean getProperty() {
        if (property == null) {
            property = new ApplicationPropertyBean();
        }
        return property;
    }

    /**
     * Runs post initialization actions to customize form elements.
     */
    private void postInit() {
        appBean.getSourceFilteredList().addObservableListListener(new ObservableListListener() {
            @Override
            public void listElementsAdded(ObservableList ol, int i, int i1) {
                applicationDocumentsHelper.verifyCheckList(appBean.getSourceList().getFilteredList());
            }

            @Override
            public void listElementsRemoved(ObservableList ol, int i, List list) {
                applicationDocumentsHelper.verifyCheckList(appBean.getSourceList().getFilteredList());
            }

            @Override
            public void listElementReplaced(ObservableList ol, int i, Object o) {
            }

            @Override
            public void listElementPropertyChanged(ObservableList ol, int i) {
            }
        });

        appBean.getServiceList().addObservableListListener(new ObservableListListener() {
            @Override
            public void listElementsAdded(ObservableList ol, int i, int i1) {
                applicationDocumentsHelper.updateCheckList(appBean.getServiceList(), appBean.getSourceList());
            }

            @Override
            public void listElementsRemoved(ObservableList ol, int i, List list) {
                applicationDocumentsHelper.updateCheckList(appBean.getServiceList(), appBean.getSourceList());
            }

            @Override
            public void listElementReplaced(ObservableList ol, int i, Object o) {
                customizeServicesButtons();
            }

            @Override
            public void listElementPropertyChanged(ObservableList ol, int i) {
                customizeServicesButtons();
            }
        });

        appBean.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ApplicationBean.SELECTED_SERVICE_PROPERTY)) {
                    customizeServicesButtons();
                } else if (evt.getPropertyName().equals(ApplicationBean.STATUS_TYPE_PROPERTY)) {
                    customizeApplicationForm();
                    customizeServicesButtons();
                    customizePropertyButtons();
                } else if (evt.getPropertyName().equals(ApplicationBean.SELECTED_PROPPERTY_PROPERTY)) {
                    customizePropertyButtons();
                } else if (evt.getPropertyName().equals(ApplicationBean.SELECTED_CADASTRE_OBJECT)) {
                    customizeParcelsButtons();
                }
            }
        });

        cadastreObjectSearch.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(TextSearch.ITEM_SELECTED)) {
                    customizeAddParcelButton();
                }
            }
        });

        customizeServicesButtons();
        customizeApplicationForm();
        customizePropertyButtons();
        customizeParcelsButtons();
        customizeAddParcelButton();
    }

    /**
     * Applies customization of form, based on Application status.
     */
    private void customizeApplicationForm() {
        if (appBean != null && !appBean.isNew()) {
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle");
            pnlHeader.setTitleText(bundle.getString("SLJobPanel.pnlHeader.titleText") + " #" + appBean.getNr());
            applicationDocumentsHelper.updateCheckList(appBean.getServiceList(), appBean.getSourceList());
            if (!saveInProgress) {
                // Don't retrieve the application log from the service while a save is in progress
                appBean.loadApplicationLogList();
            }
            tabbedControlMain.addTab(bundle.getString("SLJobPanel.validationPanel.TabConstraints.tabTitle"), validationPanel);
            tabbedControlMain.addTab(bundle.getString("SLJobPanel.historyPanel.TabConstraints.tabTitle"), historyPanel);
            btnValidate.setEnabled(true);
        } else {
            tabbedControlMain.removeTabAt(tabbedControlMain.indexOfComponent(historyPanel));
            tabbedControlMain.removeTabAt(tabbedControlMain.indexOfComponent(validationPanel));
            btnValidate.setEnabled(false);
        }
        btnAssignApp.setEnabled(false);

        if (!SecurityBean.isInRole(RolesConstants.GIS_VIEW_MAP)) {
            // User does not have rights to view the map
            tabbedControlMain.removeTabAt(tabbedControlMain.indexOfComponent(mapPanel));
        }

        menuApprove.setEnabled(appBean.canApprove()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_APPROVE));
        menuCancel.setEnabled(appBean.canCancel()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_REJECT));
        menuArchive.setEnabled(appBean.canArchive()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_ARCHIVE));
        menuDispatch.setEnabled(appBean.canDespatch()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_DISPATCH));
        menuRequisition.setEnabled(appBean.canRequisition()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_REQUISITE));
        menuResubmit.setEnabled(appBean.canResubmit()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_RESUBMIT));
        menuLapse.setEnabled(appBean.canLapse()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_WITHDRAW));
        menuWithdraw.setEnabled(appBean.canWithdraw()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_WITHDRAW));
        btnPrintStatusReport.setEnabled(appBean.getRowVersion() > 0
                && SecurityBean.isInRole(RolesConstants.APPLICATION_PRINT_STATUS_REPORT));

        if (btnValidate.isEnabled()) {
            btnValidate.setEnabled(appBean.canValidate()
                    && SecurityBean.isInRole(RolesConstants.APPLICATION_VALIDATE));
        }

        if (appBean.getStatusCode() != null) {
            boolean editAllowed = appBean.isEditingAllowed()
                    && SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS);
            btnSave.setEnabled(editAllowed);
            btnAddProperty.setEnabled(editAllowed);
            btnRemoveProperty.setEnabled(editAllowed);
            btnVerifyProperty.setEnabled(editAllowed);

            btnValidate.setEnabled(editAllowed);
            txtFirstPart.setEditable(editAllowed);
            txtLastPart.setEditable(editAllowed);
            txtArea.setEditable(editAllowed);
            txtValue.setEditable(editAllowed);
            btnCertificate.setEnabled(false);
            documentsPanel.setAllowEdit(editAllowed);
            cadastreObjectSearch.setEnabled(editAllowed);
            if (appBean.getStatusCode().equals("approved")) {
                btnCertificate.setEnabled(true);
            }

            txtDescription.setEnabled(editAllowed);
            btnAssignApp.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_ASSIGN_TO_OTHERS,
                    RolesConstants.APPLICATION_ASSIGN_TO_YOURSELF));

        } else {
            if (!SecurityBean.isInRole(RolesConstants.APPLICATION_CREATE_APPS)) {
                btnSave.setEnabled(false);
            }
            btnCertificate.setEnabled(false);
        }

        btnSecurity.setEnabled(btnSave.isEnabled());
        btnSecurity.setVisible(SecurityBean.isInRole(RolesConstants.CLASSIFICATION_CHANGE_CLASS));

        saveAppState();
    }

    /**
     * Disables or enables parcel remove button.
     */
    private void customizeParcelsButtons() {
        boolean enabled = appBean.getSelectedCadastreObject() != null && appBean.isEditingAllowed();
        btnRemoveParcel.setEnabled(enabled);
        menuRemoveParcel.setEnabled(btnRemoveParcel.isEnabled());
    }

    /**
     * Disables or enables add parcel button.
     */
    private void customizeAddParcelButton() {
        boolean enabled = cadastreObjectSearch.getSelectedObject() != null && appBean.isEditingAllowed();
        btnAddParcel.setEnabled(enabled);
    }

    /**
     * Disables or enables buttons, related to the services list management.
     */
    private void customizeServicesButtons() {
        ApplicationServiceBean selectedService = appBean.getSelectedService();
        boolean servicesManagementAllowed = appBean.isManagementAllowed();
        boolean enableServicesButtons = appBean.isEditingAllowed();

        if (enableServicesButtons) {
            if (applicationID != null && applicationID.length() > 0) {
                enableServicesButtons = SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS);
            } else {
                enableServicesButtons = SecurityBean.isInRole(RolesConstants.APPLICATION_CREATE_APPS);
            }
        }

        // Customize services list buttons
        btnAddService.setEnabled(enableServicesButtons);
        btnRemoveService.setEnabled(false);
        btnUPService.setEnabled(false);
        btnDownService.setEnabled(false);

        if (enableServicesButtons) {
            if (selectedService != null) {
                if (selectedService.isNew()) {
                    btnRemoveService.setEnabled(true);
                    btnUPService.setEnabled(true);
                    btnDownService.setEnabled(true);
                } else {
                    btnRemoveService.setEnabled(false);
                    btnUPService.setEnabled(selectedService.isManagementAllowed());
                    btnDownService.setEnabled(selectedService.isManagementAllowed());
                }

                if (btnUPService.isEnabled()
                        && appBean.getServiceList().indexOf(selectedService) == 0) {
                    btnUPService.setEnabled(false);
                }
                if (btnDownService.isEnabled()
                        && appBean.getServiceList().indexOf(selectedService) == appBean.getServiceList().size() - 1) {
                    btnDownService.setEnabled(false);
                }
            }
        }

        // Customize service management buttons
        btnCompleteService.setEnabled(false);
        btnCancelService.setEnabled(false);
        btnStartService.setEnabled(false);
        btnViewService.setEnabled(false);
        btnRevertService.setEnabled(false);

        if (servicesManagementAllowed) {
            if (selectedService != null) {
                btnViewService.setEnabled(!selectedService.isNew());
                btnCancelService.setEnabled(selectedService.isManagementAllowed()
                        && SecurityBean.isInRole(RolesConstants.APPLICATION_SERVICE_CANCEL));
                btnStartService.setEnabled(selectedService.isManagementAllowed()
                        && SecurityBean.isInRole(RolesConstants.APPLICATION_SERVICE_START));

                String serviceStatus = selectedService.getStatusCode();

                if (serviceStatus != null && serviceStatus.equals(StatusConstants.COMPLETED)) {
                    btnCompleteService.setEnabled(false);
                    btnRevertService.setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_SERVICE_REVERT));
                } else {
                    btnCompleteService.setEnabled(selectedService.isManagementAllowed()
                            && SecurityBean.isInRole(RolesConstants.APPLICATION_SERVICE_COMPLETE));
                    btnRevertService.setEnabled(false);
                }
            }
        }

        menuAddService.setEnabled(btnAddService.isEnabled());
        menuRemoveService.setEnabled(btnRemoveService.isEnabled());
        menuMoveServiceUp.setEnabled(btnUPService.isEnabled());
        menuMoveServiceDown.setEnabled(btnDownService.isEnabled());
        menuViewService.setEnabled(btnViewService.isEnabled());
        menuStartService.setEnabled(btnStartService.isEnabled());
        menuCompleteService.setEnabled(btnCompleteService.isEnabled());
        menuRevertService.setEnabled(btnRevertService.isEnabled());
        menuCancelService.setEnabled(btnCancelService.isEnabled());
    }

    /**
     * Disables or enables buttons, related to the property list management.
     */
    private void customizePropertyButtons() {
        boolean enable = false;
        if (appBean.isEditingAllowed() && appBean.getSelectedProperty() != null) {
            enable = true;
        }
        btnRemoveProperty.setEnabled(enable);
        btnVerifyProperty.setEnabled(enable);
    }

    /**
     * This method is used by the form designer to create the list of agents.
     */
    private PartySummaryListBean createPartySummaryList() {
        PartySummaryListBean agentsList = new PartySummaryListBean();
        agentsList.loadParties(PartyRoleTypeBean.ROLE_PROPERTY_MANAGER,
                true, appBean.getAgentId());
        return agentsList;
    }

    /**
     * Opens dialog form to display status change result for application or
     * service.
     */
    private void openValidationResultForm(List<ValidationResultBean> validationResultList,
            boolean isSuccess, String message) {
        ValidationResultForm resultForm = new ValidationResultForm(
                null, true, validationResultList, isSuccess, message);
        resultForm.setLocationRelativeTo(this);
        resultForm.setVisible(true);
    }

    /**
     * Checks if there are any changes on the form before proceeding with
     * action.
     */
    private boolean checkSaveBeforeAction() {
        if (MainForm.checkBeanState(appBean)) {
            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_SAVE_BEFORE_ACTION)
                    == MessageUtility.BUTTON_ONE) {
                if (checkApplication()) {
                    saveApplication();
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates application
     */
    private void validateApplication() {
        if (!checkSaveBeforeAction()) {
            return;
        }

        if (appBean.getId() != null) {
            SolaTask t = new SolaTask() {
                @Override
                public Boolean doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_APP_VALIDATING));
                    validationResultListBean.setValidationResultList(appBean.validate());
                    tabbedControlMain.setSelectedIndex(tabbedControlMain.indexOfComponent(validationPanel));
                    return true;
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    private void launchService(final ApplicationServiceBean service, final boolean readOnly) {
        if (service != null) {

            String requestType = service.getRequestTypeCode();
            final String servicePanelCode = service.getRequestType().getServicePanelCode();

            // Create property change listener to refresh the state of the appBean
            // when the service form is closed. 
            final PropertyChangeListener refreshAppBeanOnClose = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(ContentPanel.CONTENT_PANEL_CLOSED)) {
                        appBean.reload();
                        customizeApplicationForm();
                        saveAppState();
                    }
                }
            };

            if (servicePanelCode == null) {
                // No service panel code for the request type. Log a message and continue
                // in case this service is a workflow step that has no associated panel.
                LogUtility.log("No service panel code for request type " + requestType);
            } else if (PanelLauncher.isLaunchGroup(PanelLauncherGroupBean.CODE_NULL_CONSTRUCTOR, servicePanelCode)) {
                // Panels with no constructor arguments
                PanelLauncher.launch(servicePanelCode, getMainContentPanel(), null, null);
            } else if (PanelLauncher.isLaunchGroup(PanelLauncherGroupBean.CODE_DOCUMENT_SERVICES, servicePanelCode)) {
                // Transactioned Document Services
                PanelLauncher.launch(servicePanelCode, getMainContentPanel(), null, null, appBean, service);
            } else if (PanelLauncher.isLaunchGroup(PanelLauncherGroupBean.CODE_CADASTRE_SERVICES, servicePanelCode)) {
                // Cadastre Transaction Services
                ApplicationPropertyBean propertyBean = null;
                if (appBean.getPropertyList().getFilteredList().size() > 0) {
                    propertyBean = appBean.getPropertyList().getFilteredList().get(0);
                }
                PanelLauncher.launch(servicePanelCode, getMainContentPanel(), refreshAppBeanOnClose, null,
                        appBean, service, propertyBean);
            } else if (PanelLauncher.isLaunchGroup(PanelLauncherGroupBean.CODE_PROPERTY_SERVICES, servicePanelCode)
                    || PanelLauncher.isLaunchGroup(PanelLauncherGroupBean.CODE_NEW_PROPERTY_SERVICES, servicePanelCode)) {

                // Property related service
                final boolean isNewPropertyService = PanelLauncher.isLaunchGroup(PanelLauncherGroupBean.CODE_NEW_PROPERTY_SERVICES, servicePanelCode);
                final BaUnitBean result[] = {null};
                final ContentPanel window = this;

                SolaTask t = new SolaTask<Void, Void>() {
                    @Override
                    public Void doTask() {
                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTY));
                        // Determine the property to process for this service using the PropertyHelper
                        result[0] = PropertyHelper.getBaUnitBeanForService(appBean, service, window, isNewPropertyService);
                        return null;
                    }

                    @Override
                    protected void taskDone() {
                        if (result[0] != null) {
                            launchPropertyPanel(result[0], isNewPropertyService, service, servicePanelCode,
                                    refreshAppBeanOnClose, readOnly);
                        }
                    }
                };
                TaskManager.getInstance().runTask(t);
            } else {
                MessageUtility.displayMessage(ClientMessage.APPLICATION_UNKNOWN_LAUNCH_PANEL);
            }
        } else {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_SELECT_SERVICE);
        }
    }

    /**
     * Opens the property panel for a Property Service.
     *
     * @param propBean
     * @param isNewPropertyService
     * @param service
     * @param servicePanelCode
     * @param refreshAppBeanOnClose
     * @param readOnly
     */
    private void launchPropertyPanel(BaUnitBean propBean, boolean isNewPropertyService,
            final ApplicationServiceBean service, final String servicePanelCode,
            final PropertyChangeListener refreshAppBeanOnClose, final boolean readOnly) {

        if (isNewPropertyService) {
            // This is a new property service. Create task to execute after the panel is opened.
            Runnable postOpen = new Runnable() {
                @Override
                public void run() {
                    if (!service.getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_NEW_DIGITAL_TITLE)
                            && getMainContentPanel().getPanel(MainContentPanel.CARD_PROPERTY_PANEL) != null) {
                        ((PropertyPanel) getMainContentPanel().getPanel(MainContentPanel.CARD_PROPERTY_PANEL)).showPriorTitileMessage();
                    }
                }
            };
            PanelLauncher.launch(servicePanelCode, getMainContentPanel(), refreshAppBeanOnClose, postOpen,
                    appBean.copy(), service, propBean, readOnly);
        } else if (propBean.isNew()) {
            // The property does not exist so launch Property Details using the Name Parts
            PanelLauncher.launch(servicePanelCode, getMainContentPanel(), refreshAppBeanOnClose, null,
                    appBean.copy(), service, propBean.getNameFirstpart(),
                    propBean.getNameLastpart(), readOnly);
        } else {
            // Launch Property Details using the propBean details.
            PanelLauncher.launch(servicePanelCode, getMainContentPanel(), refreshAppBeanOnClose, null,
                    appBean.copy(), service, propBean, readOnly);
        }
    }

    private boolean saveApplication() {
        appBean.setLocation(this.mapControl.getApplicationLocation());
        boolean isSuccess = false;
        //appBean.getAgent().setTypeCode(PartyTypeBean.CODE_NATURAL_PERSON);
        if (applicationID != null && !applicationID.equals("")) {
            isSuccess = appBean.saveApplication();
        } else {
            isSuccess = appBean.lodgeApplication();
        }
        if (isSuccess) {
            markDashboardForRefresh();
        }
        return isSuccess;
    }

    private boolean checkApplication() {
        if (appBean.validate(true).size() > 0) {
            return false;
        }

        if (applicationDocumentsHelper.isAllItemsChecked() == false) {
            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_NOTALL_DOCUMENT_REQUIRED) == MessageUtility.BUTTON_TWO) {
                return false;
            }
        }

        // Check how many properties needed 
        int nrPropRequired = 0;

        for (Iterator<ApplicationServiceBean> it = appBean.getServiceList().iterator(); it.hasNext();) {
            ApplicationServiceBean appService = it.next();
            for (Iterator<RequestTypeBean> it1 = CacheManager.getRequestTypes().iterator(); it1.hasNext();) {
                RequestTypeBean requestTypeBean = it1.next();
                if (requestTypeBean.getCode().equals(appService.getRequestTypeCode())) {
                    if (requestTypeBean.getNrPropertiesRequired() > nrPropRequired) {
                        nrPropRequired = requestTypeBean.getNrPropertiesRequired();
                    }
                    break;
                }
            }
        }

        String[] params = {"" + nrPropRequired};
        if (appBean.getPropertyList().size() < nrPropRequired) {
            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_ATLEAST_PROPERTY_REQUIRED, params) == MessageUtility.BUTTON_TWO) {
                return false;
            }
        }
        return true;
    }

    private void saveApplication(final boolean closeOnSave) {

        if (!checkApplication()) {
            return;
        }
        saveInProgress = true;

        SolaTask<Void, Void> t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SAVING));
                saveApplication();
                if (closeOnSave) {
                    close();
                }
                return null;
            }

            @Override
            public void taskDone() {
                saveInProgress = false;
//                MessageUtility.displayMessage(ClientMessage.APPLICATION_SUCCESSFULLY_SAVED);
                customizeApplicationForm();
                saveAppState();

                if (applicationID == null || applicationID.equals("")) {
                    showReport(ReportManager.getLodgementNoticeReport(appBean));
                    applicationID = appBean.getId();
                }
                firePropertyChange(APPLICATION_SAVED_PROPERTY, false, true);
            }
        };

        TaskManager.getInstance().runTask(t);

    }

    private void markDashboardForRefresh() {
        if (getMainContentPanel().isPanelOpened(MainContentPanel.CARD_DASHBOARD)) {
            SLDashBoardPanel dashBoard = (SLDashBoardPanel) getMainContentPanel().getPanel(MainContentPanel.CARD_DASHBOARD);
            if (dashBoard != null) {
                dashBoard.setAutoRefresh(true);
            }
        }
    }

    private void addParcel() {
        if (cadastreObjectSearch.getSelectedObject() != null) {
            appBean.addCadastreObject((CadastreObjectBean) ((CadastreObjectBean) cadastreObjectSearch.getSelectedObject()).copy());
        }
    }

    private void removeSelectedParcel() {
        appBean.removeSelectedCadastreObject();
    }

    private void openSysRegCertParamsForm(String nr) {
        SysRegCertParamsForm certificateGenerator = new SysRegCertParamsForm(null, true, nr, null);
        certificateGenerator.setVisible(true);
    }

    /**
     * Initializes map control to display application location.
     */
    private void formComponentShown(java.awt.event.ComponentEvent evt) {
        if (this.mapControl == null && SecurityBean.isInRole(RolesConstants.GIS_VIEW_MAP)) {
            this.mapControl = new ControlsBundleForApplicationLocation();
            this.mapControl.setApplicationLocation(appBean.getLocation());
            this.mapControl.setApplicationId(appBean.getId());
            this.mapPanel.setLayout(new BorderLayout());
            this.mapPanel.add(this.mapControl, BorderLayout.CENTER);
        }
    }

    /**
     * Clears fields on the <b>Properties</b> tab, after the new property is
     * added into the list.
     */
    private void clearPropertyFields() {
        txtFirstPart.setText(null);
        txtLastPart.setText(null);
        txtArea.setText(null);
        txtValue.setText(null);
    }

    /**
     * Opens {@link ReportViewerForm} to display report.
     */
    private void showReport(JasperPrint report) {
        ReportViewerForm form = new ReportViewerForm(report);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }

    private void takeActionAgainstApplication(final String actionType) {
        String msgCode = ClientMessage.APPLICATION_ACTION_WARNING_SOFT;
        if (ApplicationActionTypeBean.WITHDRAW.equals(actionType)
                || ApplicationActionTypeBean.ARCHIVE.equals(actionType)
                || ApplicationActionTypeBean.LAPSE.equals(actionType)
                || ApplicationActionTypeBean.CANCEL.equals(actionType)
                || ApplicationActionTypeBean.APPROVE.equals(actionType)) {
            msgCode = ClientMessage.APPLICATION_ACTION_WARNING_STRONG;
        }
        String localizedActionName = CacheManager.getBeanByCode(
                CacheManager.getApplicationActionTypes(), actionType).getDisplayValue();
        if (MessageUtility.displayMessage(msgCode, new String[]{localizedActionName}) == MessageUtility.BUTTON_ONE) {

            if (!checkSaveBeforeAction()) {
                return;
            }

            SolaTask<List<ValidationResultBean>, List<ValidationResultBean>> t
                    = new SolaTask<List<ValidationResultBean>, List<ValidationResultBean>>() {
                        @Override
                        public List<ValidationResultBean> doTask() {
                            setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_APP_TAKE_ACTION));
                            boolean displayValidationResultFormInSuccess = true;
                            List<ValidationResultBean> result = null;
                            if (ApplicationActionTypeBean.VALIDATE.equals(actionType)) {
                                displayValidationResultFormInSuccess = false;
                                validationResultListBean.setValidationResultList(appBean.validate());
                            } else if (ApplicationActionTypeBean.WITHDRAW.equals(actionType)) {
                                result = appBean.withdraw();
                            } else if (ApplicationActionTypeBean.CANCEL.equals(actionType)) {
                                result = appBean.reject();
                            } else if (ApplicationActionTypeBean.ARCHIVE.equals(actionType)) {
                                result = appBean.archive();
                            } else if (ApplicationActionTypeBean.DISPATCH.equals(actionType)) {
                                result = appBean.despatch();
                            } else if (ApplicationActionTypeBean.LAPSE.equals(actionType)) {
                                result = appBean.lapse();
                            } else if (ApplicationActionTypeBean.REQUISITION.equals(actionType)) {
                                result = appBean.requisition();
                            } else if (ApplicationActionTypeBean.RESUBMIT.equals(actionType)) {
                                result = appBean.resubmit();
                            } else if (ApplicationActionTypeBean.APPROVE.equals(actionType)) {
                                result = appBean.approve();
                            }

                            if (displayValidationResultFormInSuccess) {
                                return result;
                            }
                            return null;
                        }

                        @Override
                        public void taskDone() {
                            List<ValidationResultBean> result = get();

                            if (result != null) {
                                String message = MessageUtility.getLocalizedMessage(
                                        ClientMessage.APPLICATION_ACTION_SUCCESS,
                                        new String[]{appBean.getNr()}).getMessage();
                                openValidationResultForm(result, true, message);
                            }
                            saveAppState();
                            markDashboardForRefresh();
                        }
                    };
            TaskManager.getInstance().runTask(t);
        }
    }

    private void addService() {
        ServiceListForm serviceListForm = new ServiceListForm(appBean);
        serviceListForm.setLocationRelativeTo(this);
        serviceListForm.setVisible(true);
    }

    /**
     * Removes selected service from the services list.
     */
    private void removeService() {
        if (appBean.getSelectedService() != null) {
            appBean.removeSelectedService();
            applicationDocumentsHelper.updateCheckList(appBean.getServiceList(), appBean.getSourceList());
        }
    }

    /**
     * Moves selected service up in the list of services.
     */
    private void moveServiceUp() {
        ApplicationServiceBean asb = appBean.getSelectedService();
        if (asb != null) {
            Integer order = (Integer) (tabServices.getValueAt(tabServices.getSelectedRow(), 0));
            if (appBean.moveServiceUp()) {
                tabServices.setValueAt(order - 1, tabServices.getSelectedRow() - 1, 0);
                tabServices.setValueAt(order, tabServices.getSelectedRow(), 0);
                tabServices.getSelectionModel().setSelectionInterval(tabServices.getSelectedRow() - 1, tabServices.getSelectedRow() - 1);
            }
        } else {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_SELECT_SERVICE);

        }
    }

    /**
     * Moves selected application service down in the services list. Calls
     * {@link ApplicationBean#moveServiceDown()}
     */
    private void moveServiceDown() {
        ApplicationServiceBean asb = appBean.getSelectedService();
        if (asb != null) {
            Integer order = (Integer) (tabServices.getValueAt(tabServices.getSelectedRow(), 0));
            //            lstSelectedServices.setSelectedIndex(lstSelectedServices.getSelectedIndex() - 1);
            if (appBean.moveServiceDown()) {
                tabServices.setValueAt(order + 1, tabServices.getSelectedRow() + 1, 0);
                tabServices.setValueAt(order, tabServices.getSelectedRow(), 0);
                tabServices.getSelectionModel().setSelectionInterval(tabServices.getSelectedRow() + 1, tabServices.getSelectedRow() + 1);
            }
        } else {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_SELECT_SERVICE);
        }
    }

    /**
     * Launches selected service.
     */
    private void startService() {
        final ApplicationServiceBean selectedService = appBean.getSelectedService();

        if (selectedService != null) {

            SolaTask t = new SolaTask<Void, Void>() {
                List<ValidationResultBean> result;

                @Override
                protected Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SERVICE_STARTING));
                    result = selectedService.start();
                    return null;
                }

                @Override
                protected void taskDone() {
                    appBean.reload();
                    customizeApplicationForm();
                    saveAppState();
                    markDashboardForRefresh();
                    launchService(appBean.getServiceById(selectedService.getId()), false);
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    /**
     * Calls "complete method for the selected service. "
     */
    private void completeService() {
        final ApplicationServiceBean selectedService = appBean.getSelectedService();

        if (selectedService != null) {

            final String serviceName = selectedService.getRequestType().getDisplayValue();

            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_SERVICE_COMPLETE_WARNING,
                    new String[]{serviceName}) == MessageUtility.BUTTON_ONE) {

                if (!checkSaveBeforeAction()) {
                    return;
                }

                SolaTask t = new SolaTask<Void, Void>() {
                    List<ValidationResultBean> result;

                    @Override
                    protected Void doTask() {
                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SERVICE_COMPLETING));
                        result = selectedService.complete();
                        return null;
                    }

                    @Override
                    protected void taskDone() {
                        String message = MessageUtility.getLocalizedMessage(
                                ClientMessage.APPLICATION_SERVICE_COMPLETE_SUCCESS,
                                new String[]{serviceName}).getMessage();

                        appBean.reload();
                        customizeApplicationForm();
                        saveAppState();
                        markDashboardForRefresh();
                        if (result != null) {
                            openValidationResultForm(result, true, message);
                        }
                    }
                };
                TaskManager.getInstance().runTask(t);
            }
        }
    }

    private void revertService() {
        final ApplicationServiceBean selectedService = appBean.getSelectedService();

        if (selectedService != null) {

            final String serviceName = selectedService.getRequestType().getDisplayValue();

            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_SERVICE_REVERT_WARNING,
                    new String[]{serviceName}) == MessageUtility.BUTTON_ONE) {

                if (!checkSaveBeforeAction()) {
                    return;
                }

                SolaTask t = new SolaTask<Void, Void>() {
                    List<ValidationResultBean> result;

                    @Override
                    protected Void doTask() {
                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SERVICE_REVERTING));
                        result = selectedService.revert();
                        return null;
                    }

                    @Override
                    protected void taskDone() {
                        String message = MessageUtility.getLocalizedMessage(
                                ClientMessage.APPLICATION_SERVICE_REVERT_SUCCESS,
                                new String[]{serviceName}).getMessage();

                        appBean.reload();
                        customizeApplicationForm();
                        saveAppState();
                        markDashboardForRefresh();
                        if (result != null) {
                            openValidationResultForm(result, true, message);
                        }
                    }
                };
                TaskManager.getInstance().runTask(t);
            }
        }
    }

    private void cancelService() {
        final ApplicationServiceBean selectedService = appBean.getSelectedService();

        if (selectedService != null) {

            final String serviceName = selectedService.getRequestType().getDisplayValue();
            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_SERVICE_CANCEL_WARNING,
                    new String[]{serviceName}) == MessageUtility.BUTTON_ONE) {

                if (!checkSaveBeforeAction()) {
                    return;
                }

                SolaTask t = new SolaTask<Void, Void>() {
                    List<ValidationResultBean> result;

                    @Override
                    protected Void doTask() {
                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_SERVICE_CANCELING));
                        result = selectedService.cancel();
                        return null;
                    }

                    @Override
                    protected void taskDone() {
                        String message;

                        message = MessageUtility.getLocalizedMessage(
                                ClientMessage.APPLICATION_SERVICE_CANCEL_SUCCESS,
                                new String[]{serviceName}).getMessage();
                        appBean.reload();
                        customizeApplicationForm();
                        saveAppState();
                        markDashboardForRefresh();
                        if (result != null) {
                            openValidationResultForm(result, true, message);
                        }
                    }
                };
                TaskManager.getInstance().runTask(t);
            }
        }
    }

    /**
     * Removes selected property object from the properties list. Calls
     * {@link ApplicationBean#removeSelectedProperty()}
     */
    private void removeSelectedProperty() {
        appBean.removeSelectedProperty();
    }

    /**
     * Verifies selected property object to check existence. Calls
     * {@link ApplicationBean#verifyProperty()}
     */
    private void verifySelectedProperty() {
        if (appBean.getSelectedProperty() == null) {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_SELECT_PROPERTY_TOVERIFY);
            return;
        }

        if (appBean.verifyProperty()) {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_PROPERTY_VERIFIED);
        }
    }

    private void approveApplication() {
        takeActionAgainstApplication(ApplicationActionTypeBean.APPROVE);
    }

    private void rejectApplication() {
        takeActionAgainstApplication(ApplicationActionTypeBean.CANCEL);
    }

    private void withdrawApplication() {
        takeActionAgainstApplication(ApplicationActionTypeBean.WITHDRAW);
    }

    private void requisitionApplication() {
        takeActionAgainstApplication(ApplicationActionTypeBean.REQUISITION);
    }

    private void archiveApplication() {
        takeActionAgainstApplication(ApplicationActionTypeBean.ARCHIVE);
    }

    private void dispatchApplication() {
        takeActionAgainstApplication(ApplicationActionTypeBean.DISPATCH);
    }

    private void lapseApplication() {
        takeActionAgainstApplication(ApplicationActionTypeBean.LAPSE);
    }

    private void resubmitApplication() {
        takeActionAgainstApplication(ApplicationActionTypeBean.RESUBMIT);
    }

    private void saveAppState() {
        MainForm.saveBeanState(appBean);
    }

    /**
     * Allows to overview service.
     */
    private void viewService() {
        launchService(appBean.getSelectedService(), true);
    }

    private void printStatusReport() {
        if (appBean.getRowVersion() > 0) {
            showReport(ReportManager.getApplicationStatusReport(appBean));
        }
    }

    @Override
    protected boolean panelClosing() {

        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(appBean)) {
            saveApplication(true);
            return false;
        }
        return true;
    }

    public void addProperty() {
        if (txtFirstPart.getText() == null || txtFirstPart.getText().equals("")
                || txtLastPart.getText() == null || txtLastPart.getText().equals("")) {
            MessageUtility.displayMessage(ClientMessage.CHECK_FIRST_LAST_PROPERTY);
            return;
        }

        BigDecimal area = null;
        BigDecimal value = null;

        try {
            area = new BigDecimal(txtArea.getText());
        } catch (Exception e) {
        }

        try {
            value = new BigDecimal(txtValue.getText());
        } catch (Exception e) {
        }
        appBean.addProperty(txtFirstPart.getText(), txtLastPart.getText(), area, value);
        clearPropertyFields();
        verifySelectedProperty();
        txtFirstPart.requestFocus();
    }

    /**
     * Displays the ApplicationAssignmentDialog to allow the user to reassign
     * the Job
     */
    private void assignJob() {
        ApplicationAssignmentDialog form = new ApplicationAssignmentDialog(appBean, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.setVisible(true);
    }

    private void configureSecurity() {
        SecurityClassificationDialog form = new SecurityClassificationDialog(appBean, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        form.setVisible(true);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        appBean = getApplicationBean();
        partySummaryList = createPartySummaryList();
        applicationDocumentsHelper = new org.sola.clients.beans.application.ApplicationDocumentsHelperBean();
        validationResultListBean = new org.sola.clients.beans.validation.ValidationResultListBean();
        popUpServices = new javax.swing.JPopupMenu();
        menuAddService = new javax.swing.JMenuItem();
        menuRemoveService = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuMoveServiceUp = new javax.swing.JMenuItem();
        menuMoveServiceDown = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuViewService = new javax.swing.JMenuItem();
        menuStartService = new javax.swing.JMenuItem();
        menuCompleteService = new javax.swing.JMenuItem();
        menuRevertService = new javax.swing.JMenuItem();
        menuCancelService = new javax.swing.JMenuItem();
        popupApplicationActions = new javax.swing.JPopupMenu();
        menuApprove = new javax.swing.JMenuItem();
        menuCancel = new javax.swing.JMenuItem();
        menuWithdraw = new javax.swing.JMenuItem();
        menuLapse = new javax.swing.JMenuItem();
        menuRequisition = new javax.swing.JMenuItem();
        menuResubmit = new javax.swing.JMenuItem();
        menuDispatch = new javax.swing.JMenuItem();
        menuArchive = new javax.swing.JMenuItem();
        landUseTypeListBean1 = new org.sola.clients.beans.referencedata.LandUseTypeListBean();
        popUpParcels = new javax.swing.JPopupMenu();
        menuRemoveParcel = new org.sola.clients.swing.common.menuitems.MenuRemove();
        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnValidate = new javax.swing.JButton();
        btnAssignApp = new javax.swing.JButton();
        btnSecurity = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnPrintStatusReport = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnCertificate = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        dropDownButton1 = new org.sola.clients.swing.common.controls.DropDownButton();
        tabbedControlMain = new javax.swing.JTabbedPane();
        contactPanel = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtAppNumber = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        labDate = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jPanel14 = new javax.swing.JPanel();
        labAgents = new javax.swing.JLabel();
        txtAgent = new javax.swing.JTextField();
        jPanel30 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtAssignedTo = new javax.swing.JTextField();
        jPanel26 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCompleteBy = new javax.swing.JFormattedTextField();
        jPanel15 = new javax.swing.JPanel();
        labStatus = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        jPanel29 = new javax.swing.JPanel();
        jPanel31 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescription = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        servicesPanel = new javax.swing.JPanel();
        scrollFeeDetails1 = new javax.swing.JScrollPane();
        tabServices = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        tbServices = new javax.swing.JToolBar();
        btnViewService = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        btnAddService = new javax.swing.JButton();
        btnRemoveService = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnUPService = new javax.swing.JButton();
        btnDownService = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnStartService = new javax.swing.JButton();
        btnCompleteService = new javax.swing.JButton();
        btnRevertService = new javax.swing.JButton();
        btnCancelService = new javax.swing.JButton();
        documentPanel = new javax.swing.JPanel();
        documentsPanel = createDocumentsPanel();
        propertyPanel = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        scrollPropertyDetails = new javax.swing.JScrollPane();
        tabPropertyDetails = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        tbPropertyDetails = new javax.swing.JToolBar();
        btnAddProperty = new javax.swing.JButton();
        btnRemoveProperty = new javax.swing.JButton();
        btnVerifyProperty = new javax.swing.JButton();
        jPanel22 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        labFirstPart = new javax.swing.JLabel();
        txtFirstPart = new javax.swing.JTextField();
        jPanel21 = new javax.swing.JPanel();
        labLastPart = new javax.swing.JLabel();
        txtLastPart = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        labValue = new javax.swing.JLabel();
        txtValue = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        labArea = new javax.swing.JLabel();
        txtArea = new javax.swing.JTextField();
        jPanel27 = new javax.swing.JPanel();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableParcels = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar1 = new javax.swing.JToolBar();
        cadastreObjectSearch = new org.sola.clients.swing.ui.cadastre.CadastreObjectSearch2();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 0), new java.awt.Dimension(3, 32767));
        btnAddParcel = new org.sola.clients.swing.common.buttons.BtnAdd();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        btnRemoveParcel = new org.sola.clients.swing.common.buttons.BtnRemove();
        mapPanel = new javax.swing.JPanel();
        validationPanel = new javax.swing.JPanel();
        validationsPanel = new javax.swing.JScrollPane();
        tabValidations = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        historyPanel = new javax.swing.JPanel();
        actionLogPanel = new javax.swing.JScrollPane();
        tabActionLog = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        popUpServices.setName("popUpServices"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        menuAddService.setText(bundle.getString("SLJobPanel.menuAddService.text")); // NOI18N
        menuAddService.setName("menuAddService"); // NOI18N
        menuAddService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuAddService);

        menuRemoveService.setText(bundle.getString("SLJobPanel.menuRemoveService.text")); // NOI18N
        menuRemoveService.setName("menuRemoveService"); // NOI18N
        menuRemoveService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuRemoveService);

        jSeparator3.setName("jSeparator3"); // NOI18N
        popUpServices.add(jSeparator3);

        menuMoveServiceUp.setText(bundle.getString("SLJobPanel.menuMoveServiceUp.text")); // NOI18N
        menuMoveServiceUp.setName("menuMoveServiceUp"); // NOI18N
        menuMoveServiceUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMoveServiceUpActionPerformed(evt);
            }
        });
        popUpServices.add(menuMoveServiceUp);

        menuMoveServiceDown.setText(bundle.getString("SLJobPanel.menuMoveServiceDown.text")); // NOI18N
        menuMoveServiceDown.setName("menuMoveServiceDown"); // NOI18N
        menuMoveServiceDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMoveServiceDownActionPerformed(evt);
            }
        });
        popUpServices.add(menuMoveServiceDown);

        jSeparator4.setName("jSeparator4"); // NOI18N
        popUpServices.add(jSeparator4);

        menuViewService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewService.setText(bundle.getString("SLJobPanel.menuViewService.text")); // NOI18N
        menuViewService.setName("menuViewService"); // NOI18N
        menuViewService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuViewService);

        menuStartService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/start.png"))); // NOI18N
        menuStartService.setText(bundle.getString("SLJobPanel.menuStartService.text")); // NOI18N
        menuStartService.setName("menuStartService"); // NOI18N
        menuStartService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuStartServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuStartService);

        menuCompleteService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm.png"))); // NOI18N
        menuCompleteService.setText(bundle.getString("SLJobPanel.menuCompleteService.text")); // NOI18N
        menuCompleteService.setName("menuCompleteService"); // NOI18N
        menuCompleteService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCompleteServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuCompleteService);

        menuRevertService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/revert.png"))); // NOI18N
        menuRevertService.setText(bundle.getString("SLJobPanel.menuRevertService.text")); // NOI18N
        menuRevertService.setName("menuRevertService"); // NOI18N
        menuRevertService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRevertServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuRevertService);

        menuCancelService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/cancel.png"))); // NOI18N
        menuCancelService.setText(bundle.getString("SLJobPanel.menuCancelService.text")); // NOI18N
        menuCancelService.setName("menuCancelService"); // NOI18N
        menuCancelService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCancelServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuCancelService);

        popupApplicationActions.setName("popupApplicationActions"); // NOI18N

        menuApprove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/approve.png"))); // NOI18N
        menuApprove.setText(bundle.getString("SLJobPanel.menuApprove.text")); // NOI18N
        menuApprove.setName("menuApprove"); // NOI18N
        menuApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuApproveActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuApprove);

        menuCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/reject.png"))); // NOI18N
        menuCancel.setText(bundle.getString("SLJobPanel.menuCancel.text")); // NOI18N
        menuCancel.setName("menuCancel"); // NOI18N
        menuCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCancelActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuCancel);

        menuWithdraw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/withdraw.png"))); // NOI18N
        menuWithdraw.setText(bundle.getString("SLJobPanel.menuWithdraw.text")); // NOI18N
        menuWithdraw.setName("menuWithdraw"); // NOI18N
        menuWithdraw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuWithdrawActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuWithdraw);

        menuLapse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lapse.png"))); // NOI18N
        menuLapse.setText(bundle.getString("SLJobPanel.menuLapse.text")); // NOI18N
        menuLapse.setName("menuLapse"); // NOI18N
        menuLapse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLapseActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuLapse);

        menuRequisition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/requisition.png"))); // NOI18N
        menuRequisition.setText(bundle.getString("SLJobPanel.menuRequisition.text")); // NOI18N
        menuRequisition.setName("menuRequisition"); // NOI18N
        menuRequisition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRequisitionActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuRequisition);

        menuResubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/resubmit.png"))); // NOI18N
        menuResubmit.setText(bundle.getString("SLJobPanel.menuResubmit.text")); // NOI18N
        menuResubmit.setName("menuResubmit"); // NOI18N
        menuResubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuResubmitActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuResubmit);

        menuDispatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/envelope.png"))); // NOI18N
        menuDispatch.setText(bundle.getString("SLJobPanel.menuDispatch.text")); // NOI18N
        menuDispatch.setName("menuDispatch"); // NOI18N
        menuDispatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDispatchActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuDispatch);

        menuArchive.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/archive.png"))); // NOI18N
        menuArchive.setText(bundle.getString("SLJobPanel.menuArchive.text")); // NOI18N
        menuArchive.setName("menuArchive"); // NOI18N
        menuArchive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuArchiveActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuArchive);

        popUpParcels.setName(bundle.getString("SLJobPanel.popUpParcels.name")); // NOI18N

        menuRemoveParcel.setText(bundle.getString("SLJobPanel.menuRemoveParcel.text")); // NOI18N
        menuRemoveParcel.setName(bundle.getString("ApplicationPanel.menuRemoveParcel.name")); // NOI18N
        menuRemoveParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveParcelActionPerformed(evt);
            }
        });
        popUpParcels.add(menuRemoveParcel);

        setHeaderPanel(pnlHeader);
        setHelpTopic(bundle.getString("SLJobPanel.helpTopic")); // NOI18N
        setMinimumSize(new java.awt.Dimension(660, 458));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(660, 458));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("SLJobPanel.pnlHeader.titleText")); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        LafManager.getInstance().setBtnProperties(btnSave);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("SLJobPanel.btnSave.text")); // NOI18N
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar3.add(btnSave);

        btnValidate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/validation.png"))); // NOI18N
        btnValidate.setText(bundle.getString("SLJobPanel.btnValidate.text")); // NOI18N
        btnValidate.setName("btnValidate"); // NOI18N
        btnValidate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        btnValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidateActionPerformed(evt);
            }
        });
        jToolBar3.add(btnValidate);

        btnAssignApp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        btnAssignApp.setText(bundle.getString("SLJobPanel.btnAssignApp.text")); // NOI18N
        btnAssignApp.setFocusable(false);
        btnAssignApp.setName("btnAssignApp"); // NOI18N
        btnAssignApp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAssignApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignAppActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAssignApp);

        btnSecurity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lock.png"))); // NOI18N
        btnSecurity.setText(bundle.getString("SLJobPanel.btnSecurity.text")); // NOI18N
        btnSecurity.setFocusable(false);
        btnSecurity.setName("btnSecurity"); // NOI18N
        btnSecurity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSecurityActionPerformed(evt);
            }
        });
        jToolBar3.add(btnSecurity);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar3.add(jSeparator6);

        btnPrintStatusReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintStatusReport.setText(bundle.getString("SLJobPanel.btnPrintStatusReport.text")); // NOI18N
        btnPrintStatusReport.setFocusable(false);
        btnPrintStatusReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPrintStatusReport.setName("btnPrintStatusReport"); // NOI18N
        btnPrintStatusReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrintStatusReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintStatusReportActionPerformed(evt);
            }
        });
        jToolBar3.add(btnPrintStatusReport);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jToolBar3.add(jSeparator5);

        btnCertificate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/approve1.png"))); // NOI18N
        btnCertificate.setText(bundle.getString("SLJobPanel.btnCertificate.text")); // NOI18N
        btnCertificate.setFocusable(false);
        btnCertificate.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        btnCertificate.setName(bundle.getString("ApplicationPanel.btnCertificate.name")); // NOI18N
        btnCertificate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCertificate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCertificateActionPerformed(evt);
            }
        });
        jToolBar3.add(btnCertificate);

        jSeparator7.setName(bundle.getString("ApplicationPanel.jSeparator7.name")); // NOI18N
        jToolBar3.add(jSeparator7);

        dropDownButton1.setText(bundle.getString("SLJobPanel.dropDownButton1.text")); // NOI18N
        dropDownButton1.setComponentPopupMenu(popupApplicationActions);
        dropDownButton1.setFocusable(false);
        dropDownButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dropDownButton1.setName("dropDownButton1"); // NOI18N
        dropDownButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(dropDownButton1);

        tabbedControlMain.setName("tabbedControlMain"); // NOI18N
        tabbedControlMain.setPreferredSize(new java.awt.Dimension(440, 370));
        tabbedControlMain.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        contactPanel.setName("contactPanel"); // NOI18N
        contactPanel.setPreferredSize(new java.awt.Dimension(645, 331));
        contactPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        jPanel25.setName("jPanel25"); // NOI18N
        jPanel25.setLayout(new java.awt.GridLayout(2, 4, 15, 0));

        jPanel24.setEnabled(false);
        jPanel24.setFocusable(false);
        jPanel24.setName("jPanel24"); // NOI18N
        jPanel24.setRequestFocusEnabled(false);

        jLabel1.setText(bundle.getString("SLJobPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtAppNumber.setEditable(false);
        txtAppNumber.setEnabled(false);
        txtAppNumber.setFocusable(false);
        txtAppNumber.setName("txtAppNumber"); // NOI18N
        txtAppNumber.setRequestFocusEnabled(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${nr}"), txtAppNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtAppNumber)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAppNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel25.add(jPanel24);

        jPanel13.setEnabled(false);
        jPanel13.setFocusable(false);
        jPanel13.setName("jPanel13"); // NOI18N
        jPanel13.setRequestFocusEnabled(false);

        LafManager.getInstance().setLabProperties(labDate);
        labDate.setText(bundle.getString("SLJobPanel.labDate.text")); // NOI18N
        labDate.setName("labDate"); // NOI18N

        txtDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        txtDate.setText(bundle.getString("SLJobPanel.txtDate.text")); // NOI18N
        txtDate.setEnabled(false);
        txtDate.setFocusable(false);
        txtDate.setName(bundle.getString("ApplicationPanel.txtDate.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${lodgingDatetime}"), txtDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtDate)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(labDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel25.add(jPanel13);

        jPanel14.setMinimumSize(new java.awt.Dimension(28, 20));
        jPanel14.setName("jPanel14"); // NOI18N

        LafManager.getInstance().setLabProperties(labAgents);
        labAgents.setText(bundle.getString("SLJobPanel.labAgents.text")); // NOI18N
        labAgents.setIconTextGap(1);
        labAgents.setName("labAgents"); // NOI18N

        txtAgent.setEnabled(false);
        txtAgent.setName("txtAgent"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${agent.fullName}"), txtAgent, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labAgents, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
            .addComponent(txtAgent)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(labAgents)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtAgent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel25.add(jPanel14);

        jPanel30.setName("jPanel30"); // NOI18N

        jLabel5.setText(bundle.getString("SLJobPanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txtAssignedTo.setEnabled(false);
        txtAssignedTo.setName("txtAssignedTo"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${assignee.fullUserName}"), txtAssignedTo, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
            .addComponent(txtAssignedTo)
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtAssignedTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel25.add(jPanel30);

        jPanel26.setEnabled(false);
        jPanel26.setFocusable(false);
        jPanel26.setName("jPanel26"); // NOI18N
        jPanel26.setRequestFocusEnabled(false);

        jLabel2.setText(bundle.getString("SLJobPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtCompleteBy.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        txtCompleteBy.setText(bundle.getString("SLJobPanel.txtCompleteBy.text")); // NOI18N
        txtCompleteBy.setEnabled(false);
        txtCompleteBy.setFocusable(false);
        txtCompleteBy.setName(bundle.getString("ApplicationPanel.txtCompleteBy.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${expectedCompletionDate}"), txtCompleteBy, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtCompleteBy)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCompleteBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel25.add(jPanel26);

        jPanel15.setName("jPanel15"); // NOI18N

        LafManager.getInstance().setLabProperties(labStatus);
        labStatus.setText(bundle.getString("SLJobPanel.labStatus.text")); // NOI18N
        labStatus.setName("labStatus"); // NOI18N

        txtStatus.setEnabled(false);
        txtStatus.setName("txtStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtStatus.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtStatus.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtStatus)
            .addComponent(labStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(labStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel25.add(jPanel15);

        jPanel29.setName("jPanel29"); // NOI18N

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel25.add(jPanel29);

        jPanel31.setName("jPanel31"); // NOI18N

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel25.add(jPanel31);

        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtDescription.setColumns(20);
        txtDescription.setLineWrap(true);
        txtDescription.setRows(5);
        txtDescription.setName("txtDescription"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${description}"), txtDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(txtDescription);

        jLabel6.setText(bundle.getString("SLJobPanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
            .addComponent(jScrollPane2)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout contactPanelLayout = new javax.swing.GroupLayout(contactPanel);
        contactPanel.setLayout(contactPanelLayout);
        contactPanelLayout.setHorizontalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE))
                .addContainerGap())
        );
        contactPanelLayout.setVerticalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("SLJobPanel.contactPanel.TabConstraints.tabTitle"), contactPanel); // NOI18N

        servicesPanel.setName("servicesPanel"); // NOI18N

        scrollFeeDetails1.setBackground(new java.awt.Color(255, 255, 255));
        scrollFeeDetails1.setName("scrollFeeDetails1"); // NOI18N
        scrollFeeDetails1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabServices.setComponentPopupMenu(popUpServices);
        tabServices.setName("tabServices"); // NOI18N
        tabServices.setNextFocusableComponent(btnSave);
        tabServices.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        tabServices.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${serviceList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabServices);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${serviceOrder}"));
        columnBinding.setColumnName("Service Order");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${requestType.displayValue}"));
        columnBinding.setColumnName("Request Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${concatenatedName}"));
        columnBinding.setColumnName("Concatenated Name");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${selectedService}"), tabServices, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tabServices.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabServicesMouseClicked(evt);
            }
        });
        scrollFeeDetails1.setViewportView(tabServices);
        if (tabServices.getColumnModel().getColumnCount() > 0) {
            tabServices.getColumnModel().getColumn(0).setMinWidth(70);
            tabServices.getColumnModel().getColumn(0).setPreferredWidth(70);
            tabServices.getColumnModel().getColumn(0).setMaxWidth(70);
            tabServices.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLJobPanel.tabFeeDetails1.columnModel.title0")); // NOI18N
            tabServices.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLJobPanel.tabFeeDetails1.columnModel.title1")); // NOI18N
            tabServices.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLJobPanel.tabServices.columnModel.title3")); // NOI18N
            tabServices.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLJobPanel.tabFeeDetails1.columnModel.title2")); // NOI18N
        }

        tbServices.setFloatable(false);
        tbServices.setRollover(true);
        tbServices.setName("tbServices"); // NOI18N

        btnViewService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewService.setText(bundle.getString("SLJobPanel.btnViewService.text")); // NOI18N
        btnViewService.setFocusable(false);
        btnViewService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewService.setName("btnViewService"); // NOI18N
        btnViewService.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnViewService);

        jSeparator9.setName("jSeparator9"); // NOI18N
        tbServices.add(jSeparator9);

        btnAddService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddService.setText(bundle.getString("SLJobPanel.btnAddService.text")); // NOI18N
        btnAddService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddService.setName("btnAddService"); // NOI18N
        btnAddService.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        btnAddService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnAddService);

        btnRemoveService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveService.setText(bundle.getString("SLJobPanel.btnRemoveService.text")); // NOI18N
        btnRemoveService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveService.setName("btnRemoveService"); // NOI18N
        btnRemoveService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnRemoveService);

        jSeparator1.setName("jSeparator1"); // NOI18N
        tbServices.add(jSeparator1);

        btnUPService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/up.png"))); // NOI18N
        btnUPService.setText(bundle.getString("SLJobPanel.btnUPService.text")); // NOI18N
        btnUPService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUPService.setName("btnUPService"); // NOI18N
        btnUPService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUPServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnUPService);

        btnDownService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/down.png"))); // NOI18N
        btnDownService.setText(bundle.getString("SLJobPanel.btnDownService.text")); // NOI18N
        btnDownService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDownService.setName("btnDownService"); // NOI18N
        btnDownService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnDownService);

        jSeparator2.setName("jSeparator2"); // NOI18N
        tbServices.add(jSeparator2);

        btnStartService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/start.png"))); // NOI18N
        btnStartService.setText(bundle.getString("SLJobPanel.btnStartService.text")); // NOI18N
        btnStartService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStartService.setName("btnStartService"); // NOI18N
        btnStartService.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        btnStartService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnStartService);

        btnCompleteService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm.png"))); // NOI18N
        btnCompleteService.setText(bundle.getString("SLJobPanel.btnCompleteService.text")); // NOI18N
        btnCompleteService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCompleteService.setName("btnCompleteService"); // NOI18N
        btnCompleteService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompleteServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnCompleteService);

        btnRevertService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/revert.png"))); // NOI18N
        btnRevertService.setText(bundle.getString("SLJobPanel.btnRevertService.text")); // NOI18N
        btnRevertService.setFocusable(false);
        btnRevertService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRevertService.setName("btnRevertService"); // NOI18N
        btnRevertService.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRevertService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRevertServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnRevertService);

        btnCancelService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/cancel.png"))); // NOI18N
        btnCancelService.setText(bundle.getString("SLJobPanel.btnCancelService.text")); // NOI18N
        btnCancelService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCancelService.setName("btnCancelService"); // NOI18N
        btnCancelService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnCancelService);

        javax.swing.GroupLayout servicesPanelLayout = new javax.swing.GroupLayout(servicesPanel);
        servicesPanel.setLayout(servicesPanelLayout);
        servicesPanelLayout.setHorizontalGroup(
            servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, servicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                    .addComponent(tbServices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE))
                .addContainerGap())
        );
        servicesPanelLayout.setVerticalGroup(
            servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(servicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbServices, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("SLJobPanel.servicesPanel.TabConstraints.tabTitle"), servicesPanel); // NOI18N

        documentPanel.setName("documentPanel"); // NOI18N
        documentPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        documentsPanel.setName(bundle.getString("ApplicationPanel.documentsPanel.name")); // NOI18N

        javax.swing.GroupLayout documentPanelLayout = new javax.swing.GroupLayout(documentPanel);
        documentPanel.setLayout(documentPanelLayout);
        documentPanelLayout.setHorizontalGroup(
            documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(documentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                .addContainerGap())
        );
        documentPanelLayout.setVerticalGroup(
            documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(documentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(documentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("SLJobPanel.documentPanel.TabConstraints.tabTitle"), documentPanel); // NOI18N

        propertyPanel.setName("propertyPanel"); // NOI18N
        propertyPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        jPanel28.setName(bundle.getString("ApplicationPanel.jPanel28.name")); // NOI18N
        jPanel28.setLayout(new java.awt.GridLayout(2, 1, 0, 6));

        jPanel23.setName(bundle.getString("ApplicationPanel.jPanel23.name")); // NOI18N

        scrollPropertyDetails.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        scrollPropertyDetails.setName("scrollPropertyDetails"); // NOI18N
        scrollPropertyDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabPropertyDetails.setName("tabPropertyDetails"); // NOI18N
        tabPropertyDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        tabPropertyDetails.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredPropertyList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabPropertyDetails);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"));
        columnBinding.setColumnName("Name Firstpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"));
        columnBinding.setColumnName("Name Lastpart");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${area}"));
        columnBinding.setColumnName("Area");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${totalValue}"));
        columnBinding.setColumnName("Total Value");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${verifiedLocation}"));
        columnBinding.setColumnName("Verified Location");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${verifiedExists}"));
        columnBinding.setColumnName("Verified Exists");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${selectedProperty}"), tabPropertyDetails, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        scrollPropertyDetails.setViewportView(tabPropertyDetails);
        if (tabPropertyDetails.getColumnModel().getColumnCount() > 0) {
            tabPropertyDetails.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLJobPanel.tabPropertyDetails.columnModel.title0")); // NOI18N
            tabPropertyDetails.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLJobPanel.tabPropertyDetails.columnModel.title1")); // NOI18N
            tabPropertyDetails.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLJobPanel.tabPropertyDetails.columnModel.title2")); // NOI18N
            tabPropertyDetails.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLJobPanel.tabPropertyDetails.columnModel.title3")); // NOI18N
            tabPropertyDetails.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLJobPanel.tabPropertyDetails.columnModel.title6")); // NOI18N
            tabPropertyDetails.getColumnModel().getColumn(4).setCellRenderer(new ExistingObjectCellRenderer());
            tabPropertyDetails.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("SLJobPanel.tabPropertyDetails.columnModel.title4")); // NOI18N
            tabPropertyDetails.getColumnModel().getColumn(5).setCellRenderer(new ExistingObjectCellRenderer());
        }

        tbPropertyDetails.setFloatable(false);
        tbPropertyDetails.setRollover(true);
        tbPropertyDetails.setToolTipText(bundle.getString("SLJobPanel.tbPropertyDetails.toolTipText")); // NOI18N
        tbPropertyDetails.setName("tbPropertyDetails"); // NOI18N

        LafManager.getInstance().setBtnProperties(btnAddProperty);
        btnAddProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddProperty.setText(bundle.getString("SLJobPanel.btnAddProperty.text")); // NOI18N
        btnAddProperty.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddProperty.setName("btnAddProperty"); // NOI18N
        btnAddProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPropertyActionPerformed(evt);
            }
        });
        tbPropertyDetails.add(btnAddProperty);

        btnRemoveProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveProperty.setText(bundle.getString("SLJobPanel.btnRemoveProperty.text")); // NOI18N
        btnRemoveProperty.setName("btnRemoveProperty"); // NOI18N
        btnRemoveProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemovePropertyActionPerformed(evt);
            }
        });
        tbPropertyDetails.add(btnRemoveProperty);

        btnVerifyProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/document-task.png"))); // NOI18N
        btnVerifyProperty.setText(bundle.getString("SLJobPanel.btnVerifyProperty.text")); // NOI18N
        btnVerifyProperty.setName("btnVerifyProperty"); // NOI18N
        btnVerifyProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyPropertyActionPerformed(evt);
            }
        });
        tbPropertyDetails.add(btnVerifyProperty);

        jPanel22.setName(bundle.getString("ApplicationPanel.jPanel22.name")); // NOI18N
        jPanel22.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        jPanel20.setName(bundle.getString("ApplicationPanel.jPanel20.name")); // NOI18N

        LafManager.getInstance().setLabProperties(labFirstPart);
        labFirstPart.setText(bundle.getString("SLJobPanel.labFirstPart.text")); // NOI18N
        labFirstPart.setName("labFirstPart"); // NOI18N

        LafManager.getInstance().setTxtProperties(txtFirstPart);
        txtFirstPart.setText(bundle.getString("SLJobPanel.txtFirstPart.text")); // NOI18N
        txtFirstPart.setName("txtFirstPart"); // NOI18N
        txtFirstPart.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFirstPart.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtFirstPart, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
            .addComponent(labFirstPart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(labFirstPart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFirstPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel22.add(jPanel20);

        jPanel21.setName(bundle.getString("ApplicationPanel.jPanel21.name")); // NOI18N

        LafManager.getInstance().setLabProperties(labLastPart);
        labLastPart.setText(bundle.getString("SLJobPanel.labLastPart.text")); // NOI18N
        labLastPart.setName("labLastPart"); // NOI18N

        txtLastPart.setText(bundle.getString("SLJobPanel.txtLastPart.text")); // NOI18N
        txtLastPart.setName("txtLastPart"); // NOI18N
        txtLastPart.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastPart.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtLastPart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
            .addComponent(labLastPart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(labLastPart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLastPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel22.add(jPanel21);

        jPanel17.setName("jPanel17"); // NOI18N

        LafManager.getInstance().setLabProperties(labValue);
        labValue.setText(bundle.getString("SLJobPanel.labValue.text")); // NOI18N
        labValue.setName("labValue"); // NOI18N

        txtValue.setText(bundle.getString("SLJobPanel.txtValue.text")); // NOI18N
        txtValue.setName("txtValue"); // NOI18N
        txtValue.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtValue.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtValue, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
            .addComponent(labValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(labValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel22.add(jPanel17);

        jPanel16.setName("jPanel16"); // NOI18N

        LafManager.getInstance().setLabProperties(labArea);
        labArea.setText(bundle.getString("SLJobPanel.labArea.text")); // NOI18N
        labArea.setName("labArea"); // NOI18N

        txtArea.setText(bundle.getString("SLJobPanel.txtArea.text")); // NOI18N
        txtArea.setName("txtArea"); // NOI18N
        txtArea.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtArea.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtArea, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
            .addComponent(labArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(labArea)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel22.add(jPanel16);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
            .addComponent(tbPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbPropertyDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
        );

        jPanel28.add(jPanel23);

        jPanel27.setName(bundle.getString("ApplicationPanel.jPanel27.name")); // NOI18N

        groupPanel2.setName(bundle.getString("ApplicationPanel.groupPanel2.name")); // NOI18N
        groupPanel2.setTitleText(bundle.getString("SLJobPanel.groupPanel2.titleText")); // NOI18N

        jScrollPane1.setName(bundle.getString("ApplicationPanel.jScrollPane1.name")); // NOI18N

        tableParcels.setComponentPopupMenu(popUpParcels);
        tableParcels.setName(bundle.getString("ApplicationPanel.tableParcels.name")); // NOI18N
        tableParcels.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cadastreObjectFilteredList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tableParcels);
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
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${selectedCadastreObject}"), tableParcels, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableParcels);
        if (tableParcels.getColumnModel().getColumnCount() > 0) {
            tableParcels.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLJobPanel.tableParcels.columnModel.title0")); // NOI18N
            tableParcels.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLJobPanel.tableParcels.columnModel.title1")); // NOI18N
            tableParcels.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLJobPanel.tableParcels.columnModel.title4")); // NOI18N
            tableParcels.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLJobPanel.tableParcels.columnModel.title2")); // NOI18N
            tableParcels.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("SLJobPanel.tableParcels.columnModel.title3")); // NOI18N
        }

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName(bundle.getString("ApplicationPanel.jToolBar1.name")); // NOI18N

        cadastreObjectSearch.setAutoSelect(false);
        cadastreObjectSearch.setMaximumSize(new java.awt.Dimension(200, 32767));
        cadastreObjectSearch.setMinimumSize(new java.awt.Dimension(200, 28));
        cadastreObjectSearch.setName(bundle.getString("ApplicationPanel.cadastreObjectSearch.name")); // NOI18N
        cadastreObjectSearch.setPreferredSize(new java.awt.Dimension(200, 28));
        cadastreObjectSearch.setWatermarkText(bundle.getString("SLJobPanel.cadastreObjectSearch.watermarkText")); // NOI18N
        jToolBar1.add(cadastreObjectSearch);

        filler1.setName(bundle.getString("ApplicationPanel.filler1.name")); // NOI18N
        jToolBar1.add(filler1);

        btnAddParcel.setText(bundle.getString("SLJobPanel.btnAddParcel.text")); // NOI18N
        btnAddParcel.setName(bundle.getString("ApplicationPanel.btnAddParcel.name")); // NOI18N
        btnAddParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddParcel);

        jSeparator8.setName(bundle.getString("ApplicationPanel.jSeparator8.name")); // NOI18N
        jToolBar1.add(jSeparator8);

        filler2.setName(bundle.getString("ApplicationPanel.filler2.name")); // NOI18N
        jToolBar1.add(filler2);

        btnRemoveParcel.setText(bundle.getString("SLJobPanel.btnRemoveParcel.text")); // NOI18N
        btnRemoveParcel.setName(bundle.getString("ApplicationPanel.btnRemoveParcel.name")); // NOI18N
        btnRemoveParcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveParcelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRemoveParcel);

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(groupPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addComponent(groupPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
        );

        jPanel28.add(jPanel27);

        javax.swing.GroupLayout propertyPanelLayout = new javax.swing.GroupLayout(propertyPanel);
        propertyPanel.setLayout(propertyPanelLayout);
        propertyPanelLayout.setHorizontalGroup(
            propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        propertyPanelLayout.setVerticalGroup(
            propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, propertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("SLJobPanel.propertyPanel.TabConstraints.tabTitle"), propertyPanel); // NOI18N

        mapPanel.setName("mapPanel"); // NOI18N

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 635, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 352, Short.MAX_VALUE)
        );

        tabbedControlMain.addTab(bundle.getString("SLJobPanel.mapPanel.TabConstraints.tabTitle"), mapPanel); // NOI18N

        validationPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        validationPanel.setName("validationPanel"); // NOI18N
        validationPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        validationsPanel.setBackground(new java.awt.Color(255, 255, 255));
        validationsPanel.setName("validationsPanel"); // NOI18N
        validationsPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabValidations.setName("tabValidations"); // NOI18N
        tabValidations.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${validationResutlList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, validationResultListBean, eLProperty, tabValidations);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${feedback}"));
        columnBinding.setColumnName("Feedback");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${severity}"));
        columnBinding.setColumnName("Severity");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${successful}"));
        columnBinding.setColumnName("Successful");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        validationsPanel.setViewportView(tabValidations);
        if (tabValidations.getColumnModel().getColumnCount() > 0) {
            tabValidations.getColumnModel().getColumn(0).setPreferredWidth(280);
            tabValidations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLJobPanel.tabValidations.columnModel.title1")); // NOI18N
            tabValidations.getColumnModel().getColumn(0).setCellRenderer(new TableCellTextAreaRenderer());
            tabValidations.getColumnModel().getColumn(1).setPreferredWidth(100);
            tabValidations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLJobPanel.tabValidations.columnModel.title2")); // NOI18N
            tabValidations.getColumnModel().getColumn(2).setPreferredWidth(50);
            tabValidations.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLJobPanel.tabValidations.columnModel.title3")); // NOI18N
            tabValidations.getColumnModel().getColumn(2).setCellRenderer(new ViolationCellRenderer());
        }
        tabValidations.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout validationPanelLayout = new javax.swing.GroupLayout(validationPanel);
        validationPanel.setLayout(validationPanelLayout);
        validationPanelLayout.setHorizontalGroup(
            validationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validationsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                .addContainerGap())
        );
        validationPanelLayout.setVerticalGroup(
            validationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validationsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("SLJobPanel.validationPanel.TabConstraints.tabTitle"), validationPanel); // NOI18N

        historyPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        historyPanel.setName("historyPanel"); // NOI18N
        historyPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        actionLogPanel.setBorder(null);
        actionLogPanel.setName("actionLogPanel"); // NOI18N
        actionLogPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabActionLog.setName("tabActionLog"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${appLogList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabActionLog);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${changeTime}"));
        columnBinding.setColumnName("Change Time");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${userFullname}"));
        columnBinding.setColumnName("User Fullname");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${notation}"));
        columnBinding.setColumnName("Notation");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        actionLogPanel.setViewportView(tabActionLog);
        if (tabActionLog.getColumnModel().getColumnCount() > 0) {
            tabActionLog.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SLJobPanel.tabActionLog.columnModel.title0")); // NOI18N
            tabActionLog.getColumnModel().getColumn(0).setCellRenderer(new DateTimeRenderer());
            tabActionLog.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SLJobPanel.tabActionLog.columnModel.title1_1")); // NOI18N
            tabActionLog.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SLJobPanel.tabActionLog.columnModel.title2_1")); // NOI18N
            tabActionLog.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SLJobPanel.tabActionLog.columnModel.title3_1")); // NOI18N
        }
        tabActionLog.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout historyPanelLayout = new javax.swing.GroupLayout(historyPanel);
        historyPanel.setLayout(historyPanelLayout);
        historyPanelLayout.setHorizontalGroup(
            historyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(actionLogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                .addContainerGap())
        );
        historyPanelLayout.setVerticalGroup(
            historyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(actionLogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("SLJobPanel.historyPanel.TabConstraints.tabTitle"), historyPanel); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedControlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedControlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Validates user's data input and calls save operation on the
     * {@link ApplicationBean}.
     */
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveApplication(false);
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidateActionPerformed
        validateApplication();
    }//GEN-LAST:event_btnValidateActionPerformed

    private void btnPrintStatusReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintStatusReportActionPerformed
        printStatusReport();
    }//GEN-LAST:event_btnPrintStatusReportActionPerformed

    private void menuApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuApproveActionPerformed
        approveApplication();
    }//GEN-LAST:event_menuApproveActionPerformed

    private void menuCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCancelActionPerformed
        rejectApplication();
    }//GEN-LAST:event_menuCancelActionPerformed

    private void menuWithdrawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuWithdrawActionPerformed
        withdrawApplication();
    }//GEN-LAST:event_menuWithdrawActionPerformed

    private void menuLapseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLapseActionPerformed
        lapseApplication();
    }//GEN-LAST:event_menuLapseActionPerformed

    private void menuRequisitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRequisitionActionPerformed
        requisitionApplication();
    }//GEN-LAST:event_menuRequisitionActionPerformed

    private void menuResubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuResubmitActionPerformed
        resubmitApplication();
    }//GEN-LAST:event_menuResubmitActionPerformed

    private void menuDispatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDispatchActionPerformed
        dispatchApplication();
    }//GEN-LAST:event_menuDispatchActionPerformed

    private void menuArchiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuArchiveActionPerformed
        archiveApplication();
    }//GEN-LAST:event_menuArchiveActionPerformed

    private void menuAddServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddServiceActionPerformed
        addService();
    }//GEN-LAST:event_menuAddServiceActionPerformed

    private void menuRemoveServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveServiceActionPerformed
        removeService();
    }//GEN-LAST:event_menuRemoveServiceActionPerformed

    private void menuMoveServiceUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMoveServiceUpActionPerformed
        moveServiceUp();
    }//GEN-LAST:event_menuMoveServiceUpActionPerformed

    private void menuMoveServiceDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuMoveServiceDownActionPerformed
        moveServiceDown();
    }//GEN-LAST:event_menuMoveServiceDownActionPerformed

    private void menuViewServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewServiceActionPerformed
        viewService();
    }//GEN-LAST:event_menuViewServiceActionPerformed

    private void menuStartServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuStartServiceActionPerformed
        startService();
    }//GEN-LAST:event_menuStartServiceActionPerformed

    private void menuCompleteServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCompleteServiceActionPerformed
        completeService();
    }//GEN-LAST:event_menuCompleteServiceActionPerformed

    private void menuCancelServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCancelServiceActionPerformed
        cancelService();
    }//GEN-LAST:event_menuCancelServiceActionPerformed

    private void menuRevertServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRevertServiceActionPerformed
        revertService();
    }//GEN-LAST:event_menuRevertServiceActionPerformed

    private void btnCertificateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCertificateActionPerformed
        openSysRegCertParamsForm(appBean.getNr());
    }//GEN-LAST:event_btnCertificateActionPerformed

    private void menuRemoveParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveParcelActionPerformed
        removeSelectedParcel();
    }//GEN-LAST:event_menuRemoveParcelActionPerformed

    private void btnRemoveParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveParcelActionPerformed
        removeSelectedParcel();
    }//GEN-LAST:event_btnRemoveParcelActionPerformed

    private void btnAddParcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddParcelActionPerformed
        addParcel();
    }//GEN-LAST:event_btnAddParcelActionPerformed

    private void btnAddPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPropertyActionPerformed
        addProperty();
    }//GEN-LAST:event_btnAddPropertyActionPerformed

    private void btnVerifyPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyPropertyActionPerformed
        verifySelectedProperty();
    }//GEN-LAST:event_btnVerifyPropertyActionPerformed

    private void btnRemovePropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemovePropertyActionPerformed
        removeSelectedProperty();
    }//GEN-LAST:event_btnRemovePropertyActionPerformed

    private void btnCancelServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelServiceActionPerformed
        cancelService();
    }//GEN-LAST:event_btnCancelServiceActionPerformed

    private void btnRevertServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRevertServiceActionPerformed
        revertService();
    }//GEN-LAST:event_btnRevertServiceActionPerformed

    private void btnCompleteServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompleteServiceActionPerformed
        completeService();
    }//GEN-LAST:event_btnCompleteServiceActionPerformed

    private void btnStartServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartServiceActionPerformed
        startService();
    }//GEN-LAST:event_btnStartServiceActionPerformed

    private void btnViewServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewServiceActionPerformed
        viewService();
    }//GEN-LAST:event_btnViewServiceActionPerformed

    private void btnDownServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownServiceActionPerformed
        moveServiceDown();
    }//GEN-LAST:event_btnDownServiceActionPerformed

    private void btnUPServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUPServiceActionPerformed
        moveServiceUp();
    }//GEN-LAST:event_btnUPServiceActionPerformed

    private void btnRemoveServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveServiceActionPerformed
        removeService();
    }//GEN-LAST:event_btnRemoveServiceActionPerformed

    private void btnAddServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddServiceActionPerformed
        addService();
    }//GEN-LAST:event_btnAddServiceActionPerformed

    private void btnAssignAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignAppActionPerformed
        assignJob();
    }//GEN-LAST:event_btnAssignAppActionPerformed

    private void btnSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSecurityActionPerformed
        configureSecurity();
    }//GEN-LAST:event_btnSecurityActionPerformed

    private void tabServicesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabServicesMouseClicked
        if (evt.getClickCount() == 2) {
            if (btnStartService.isEnabled()) {
                startService();
            } else if (btnViewService.isEnabled()) {
                viewService();
            }
        }
    }//GEN-LAST:event_tabServicesMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane actionLogPanel;
    public org.sola.clients.beans.application.ApplicationBean appBean;
    private org.sola.clients.beans.application.ApplicationDocumentsHelperBean applicationDocumentsHelper;
    private org.sola.clients.swing.common.buttons.BtnAdd btnAddParcel;
    private javax.swing.JButton btnAddProperty;
    private javax.swing.JButton btnAddService;
    private javax.swing.JButton btnAssignApp;
    private javax.swing.JButton btnCancelService;
    private javax.swing.JButton btnCertificate;
    private javax.swing.JButton btnCompleteService;
    private javax.swing.JButton btnDownService;
    private javax.swing.JButton btnPrintStatusReport;
    private org.sola.clients.swing.common.buttons.BtnRemove btnRemoveParcel;
    private javax.swing.JButton btnRemoveProperty;
    private javax.swing.JButton btnRemoveService;
    private javax.swing.JButton btnRevertService;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSecurity;
    private javax.swing.JButton btnStartService;
    private javax.swing.JButton btnUPService;
    private javax.swing.JButton btnValidate;
    private javax.swing.JButton btnVerifyProperty;
    private javax.swing.JButton btnViewService;
    private org.sola.clients.swing.ui.cadastre.CadastreObjectSearch2 cadastreObjectSearch;
    public javax.swing.JPanel contactPanel;
    public javax.swing.JPanel documentPanel;
    private org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel documentsPanel;
    private org.sola.clients.swing.common.controls.DropDownButton dropDownButton1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    public javax.swing.JPanel historyPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel labAgents;
    private javax.swing.JLabel labArea;
    private javax.swing.JLabel labDate;
    private javax.swing.JLabel labFirstPart;
    private javax.swing.JLabel labLastPart;
    private javax.swing.JLabel labStatus;
    private javax.swing.JLabel labValue;
    private org.sola.clients.beans.referencedata.LandUseTypeListBean landUseTypeListBean1;
    public javax.swing.JPanel mapPanel;
    private javax.swing.JMenuItem menuAddService;
    private javax.swing.JMenuItem menuApprove;
    private javax.swing.JMenuItem menuArchive;
    private javax.swing.JMenuItem menuCancel;
    private javax.swing.JMenuItem menuCancelService;
    private javax.swing.JMenuItem menuCompleteService;
    private javax.swing.JMenuItem menuDispatch;
    private javax.swing.JMenuItem menuLapse;
    private javax.swing.JMenuItem menuMoveServiceDown;
    private javax.swing.JMenuItem menuMoveServiceUp;
    private org.sola.clients.swing.common.menuitems.MenuRemove menuRemoveParcel;
    private javax.swing.JMenuItem menuRemoveService;
    private javax.swing.JMenuItem menuRequisition;
    private javax.swing.JMenuItem menuResubmit;
    private javax.swing.JMenuItem menuRevertService;
    private javax.swing.JMenuItem menuStartService;
    private javax.swing.JMenuItem menuViewService;
    private javax.swing.JMenuItem menuWithdraw;
    private org.sola.clients.beans.party.PartySummaryListBean partySummaryList;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JPopupMenu popUpParcels;
    private javax.swing.JPopupMenu popUpServices;
    private javax.swing.JPopupMenu popupApplicationActions;
    public javax.swing.JPanel propertyPanel;
    private javax.swing.JScrollPane scrollFeeDetails1;
    private javax.swing.JScrollPane scrollPropertyDetails;
    private javax.swing.JPanel servicesPanel;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabActionLog;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabPropertyDetails;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabServices;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabValidations;
    public javax.swing.JTabbedPane tabbedControlMain;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableParcels;
    private javax.swing.JToolBar tbPropertyDetails;
    private javax.swing.JToolBar tbServices;
    private javax.swing.JTextField txtAgent;
    private javax.swing.JTextField txtAppNumber;
    private javax.swing.JTextField txtArea;
    private javax.swing.JTextField txtAssignedTo;
    private javax.swing.JFormattedTextField txtCompleteBy;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JTextField txtFirstPart;
    private javax.swing.JTextField txtLastPart;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtValue;
    public javax.swing.JPanel validationPanel;
    private org.sola.clients.beans.validation.ValidationResultListBean validationResultListBean;
    private javax.swing.JScrollPane validationsPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
