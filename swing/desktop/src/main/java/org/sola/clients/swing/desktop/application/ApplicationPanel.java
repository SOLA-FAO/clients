/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
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
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.party.PartySummaryListBean;
import org.sola.clients.beans.referencedata.*;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.converters.DateConverter;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.ReportViewerForm;
import org.sola.clients.swing.desktop.administrative.PropertyPanel;
import org.sola.clients.swing.desktop.cadastre.CadastreTransactionMapPanel;
import org.sola.clients.swing.desktop.cadastre.MapPanelForm;
import org.sola.clients.swing.desktop.source.DocumentSearchDialog;
import org.sola.clients.swing.desktop.source.DocumentSearchPanel;
import org.sola.clients.swing.desktop.source.TransactionedDocumentsPanel;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForApplicationLocation;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.*;
import org.sola.clients.swing.ui.source.DocumentPanel;
import org.sola.clients.swing.ui.source.FileBrowserForm;
import org.sola.clients.swing.ui.validation.ValidationResultForm;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.casemanagement.ApplicationTO;

/**
 * This form is used to create new application or edit existing one. <p>The following list of beans
 * is used to bind the data on the form:<br />
 * {@link ApplicationBean}, <br />{@link RequestTypeListBean}, <br />
 * {@link PartySummaryListBean}, <br />{@link CommunicationTypeListBean}, <br />
 * {@link SourceTypeListBean}, <br />{@link ApplicationDocumentsHelperBean}</p>
 */
public class ApplicationPanel extends ContentPanel {

    private ControlsBundleForApplicationLocation mapControl = null;
    public static final String APPLICATION_SAVED_PROPERTY = "applicationSaved";
    private String applicationID;

    /**
     * This method is used by the form designer to create {@link ApplicationBean}. It uses
     * <code>applicationId</code> parameter passed to the form constructor.<br />
     * <code>applicationId</code> should be initialized before
     * {@link ApplicationForm#initComponents} method call.
     */
    private ApplicationBean getApplicationBean() {
        ApplicationBean applicationBean = new ApplicationBean();

        if (applicationID != null && !applicationID.equals("")) {
            ApplicationTO applicationTO = WSManager.getInstance().getCaseManagementService().getApplication(applicationID);
            TypeConverters.TransferObjectToBean(applicationTO, ApplicationBean.class, applicationBean);
        }

        applicationBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ApplicationBean.APPLICATION_PROPERTY)) {
                    firePropertyChange(ApplicationBean.APPLICATION_PROPERTY, evt.getOldValue(), evt.getNewValue());
                }
            }
        });
        return applicationBean;
    }

    private CommunicationTypeListBean createCommunicationTypes() {
        if (communicationTypes == null) {
            String communicationCode = null;
            if (appBean != null && appBean.getContactPerson() != null
                    && appBean.getContactPerson().getPreferredCommunicationCode() != null) {
                communicationCode = appBean.getContactPerson().getPreferredCommunicationCode();

            }
            communicationTypes = new CommunicationTypeListBean(true, communicationCode);
        }
        return communicationTypes;
    }

    /**
     * Default constructor to create new application.
     */
    public ApplicationPanel() {
        this(null);
    }

    /**
     * This constructor is used to open existing application for editing.
     *
     * @param applicationId ID of application to open.
     */
    public ApplicationPanel(String applicationId) {
        this.applicationID = applicationId;
        initComponents();
        postInit();
    }

    /**
     * Runs post initialization actions to customize form elements.
     */
    private void postInit() {

        addDocumentPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DocumentPanel.UPDATED_SOURCE)
                        && evt.getNewValue() != null) {
                    appBean.getSourceList().addAsNew((SourceBean) evt.getNewValue());
                }
            }
        });

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
                    customizeDocumentsButtons();
                } else if (evt.getPropertyName().equals(ApplicationBean.SELECTED_PROPPERTY_PROPERTY)) {
                    customizePropertyButtons();
                } else if (evt.getPropertyName().equals(ApplicationBean.SELECTED_SOURCE_PROPERTY)) {
                    customizeDocumentsButtons();
                }
            }
        });

        customizeServicesButtons();
        customizeApplicationForm();
        customizePropertyButtons();
        customizeDocumentsButtons();
    }

    /**
     * Applies customization of form, based on Application status.
     */
    private void customizeApplicationForm() {
        if (appBean != null && !appBean.isNew()) {
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle");
            pnlHeader.setTitleText(bundle.getString("ApplicationPanel.pnlHeader.titleText") + " #" + appBean.getNr());
            applicationDocumentsHelper.updateCheckList(appBean.getServiceList(), appBean.getSourceList());
            appBean.loadApplicationLogList();
            if (appBean.getContactPerson() != null
                    && appBean.getContactPerson().getPreferredCommunicationCode() == null) {
                cbxCommunicationWay.setSelectedIndex(-1);
            }
            tabbedControlMain.addTab(bundle.getString("ApplicationPanel.validationPanel.TabConstraints.tabTitle"), validationPanel);
            tabbedControlMain.addTab(bundle.getString("ApplicationPanel.historyPanel.TabConstraints.tabTitle"), historyPanel);
            btnValidate.setEnabled(true);
        } else {
            cbxAgents.requestFocus(true);
            tabbedControlMain.removeTabAt(tabbedControlMain.indexOfComponent(historyPanel));
            tabbedControlMain.removeTabAt(tabbedControlMain.indexOfComponent(validationPanel));
            btnValidate.setEnabled(false);
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
        btnPrintStatusReport.setEnabled(appBean.getRowVersion() > 0);

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
            btnDeleteDoc.setEnabled(editAllowed);
            btnAddExistingDocument.setEnabled(editAllowed);
            btnCalculateFee.setEnabled(editAllowed);
            btnPrintFee.setEnabled(editAllowed);
            btnValidate.setEnabled(editAllowed);
            cbxPaid.setEnabled(editAllowed);
            addDocumentPanel.setAllowEditing(editAllowed);
            txtFirstName.setEditable(editAllowed);
            txtLastName.setEditable(editAllowed);
            txtAddress.setEditable(editAllowed);
            txtEmail.setEditable(editAllowed);
            txtPhone.setEditable(editAllowed);
            txtFax.setEditable(editAllowed);
            cbxCommunicationWay.setEnabled(editAllowed);
            cbxAgents.setEnabled(editAllowed);
            txtFirstPart.setEditable(editAllowed);
            txtLastPart.setEditable(editAllowed);
            txtArea.setEditable(editAllowed);
            txtValue.setEditable(editAllowed);
        } else {
            if (!SecurityBean.isInRole(RolesConstants.APPLICATION_CREATE_APPS)) {
                btnSave.setEnabled(false);
            }
        }
        saveAppState();
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
     * Disables or enables buttons, related to the documents list management.
     */
    private void customizeDocumentsButtons() {
        SourceBean selectedDocument = appBean.getSelectedSource();
        boolean enablePropertyButtons = appBean.isEditingAllowed();

        btnDeleteDoc.setEnabled(false);
        btnOpenAttachment.setEnabled(false);

        if (enablePropertyButtons && selectedDocument != null) {
            if (selectedDocument != null) {
                btnDeleteDoc.setEnabled(true);
                if (selectedDocument.getArchiveDocumentId() != null && selectedDocument.getArchiveDocumentId().length() > 0) {
                    btnOpenAttachment.setEnabled(true);
                }
            }
        }
    }

    /**
     * This method is used by the form designer to create the list of agents.
     */
    private PartySummaryListBean createPartySummaryList() {
        PartySummaryListBean agentsList = new PartySummaryListBean();
        agentsList.FillAgents(true);
        return agentsList;
    }

    private void openPropertyForm(final BaUnitBean baUnitBean, final boolean readOnly) {
        if (baUnitBean != null) {
            SolaTask t = new SolaTask<Void, Void>() {

                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTY));
                    ApplicationBean applicationBean = appBean.copy();
                    PropertyPanel propertyPnl = new PropertyPanel(applicationBean,
                            applicationBean.getSelectedService(), baUnitBean, readOnly);
                    getMainContentPanel().addPanel(propertyPnl, MainContentPanel.CARD_PROPERTY_PANEL, true);
                    return null;
                }

                @Override
                protected void taskDone() {
                    if (!appBean.getSelectedService().getRequestTypeCode().equalsIgnoreCase(RequestTypeBean.CODE_NEW_DIGITAL_TITLE)) {
                        ((PropertyPanel) getMainContentPanel().getPanel(MainContentPanel.CARD_PROPERTY_PANEL)).showPriorTitileMessage();
                    }
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    private void openPropertyForm(final ApplicationPropertyBean applicationProperty, final boolean readOnly) {
        if (applicationProperty != null) {
            SolaTask t = new SolaTask<Void, Void>() {

                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PROPERTY));
                    ApplicationBean applicationBean = appBean.copy();
                    PropertyPanel propertyPnl = new PropertyPanel(applicationBean,
                            applicationBean.getSelectedService(), applicationProperty.getNameFirstpart(),
                            applicationProperty.getNameLastpart(), readOnly);
                    getMainContentPanel().addPanel(propertyPnl, MainContentPanel.CARD_PROPERTY_PANEL, true);
                    return null;
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    /**
     * Opens dialog form to display status change result for application or service.
     */
    private void openValidationResultForm(List<ValidationResultBean> validationResultList,
            boolean isSuccess, String message) {
        ValidationResultForm resultForm = new ValidationResultForm(
                null, true, validationResultList, isSuccess, message);
        resultForm.setLocationRelativeTo(this);
        resultForm.setVisible(true);
    }

    /**
     * Checks if there are any changes on the form before proceeding with action.
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

    private void launchService(final boolean readOnly) {
        if (appBean.getSelectedService() != null) {
            String requestType = appBean.getSelectedService().getRequestTypeCode();

            // Determine what form to start for selected service

            // Power of attorney or other type document registration
            if (requestType.equalsIgnoreCase(RequestTypeBean.CODE_REG_POWER_OF_ATTORNEY)
                    || requestType.equalsIgnoreCase(RequestTypeBean.CODE_REG_STANDARD_DOCUMENT)) {
                // Run registration/cancelation Power of attorney
                SolaTask t = new SolaTask<Void, Void>() {

                    @Override
                    public Void doTask() {
                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_DOCREGISTRATION));
                        TransactionedDocumentsPanel form = new TransactionedDocumentsPanel(
                                appBean, appBean.getSelectedService());
                        getMainContentPanel().addPanel(form, MainContentPanel.CARD_TRANSACTIONED_DOCUMENT, true);
                        return null;
                    }
                };
                TaskManager.getInstance().runTask(t);
            } // Document copy request
            else if (requestType.equalsIgnoreCase(RequestTypeBean.CODE_DOCUMENT_COPY)) {
                SolaTask t = new SolaTask<Void, Void>() {

                    @Override
                    public Void doTask() {
                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_DOCUMENTSEARCH));
                        if (!getMainContentPanel().isPanelOpened(MainContentPanel.CARD_DOCUMENT_SEARCH)) {
                            DocumentSearchPanel documentSearchPanel = new DocumentSearchPanel();
                            getMainContentPanel().addPanel(documentSearchPanel, MainContentPanel.CARD_DOCUMENT_SEARCH);
                        }
                        getMainContentPanel().showPanel(MainContentPanel.CARD_DOCUMENT_SEARCH);
                        return null;
                    }
                };
                TaskManager.getInstance().runTask(t);
            } // Cadastre print
            else if (requestType.equalsIgnoreCase(RequestTypeBean.CODE_CADASTRE_PRINT)) {
                SolaTask t = new SolaTask<Void, Void>() {

                    @Override
                    public Void doTask() {
                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_MAP));
                        if (!getMainContentPanel().isPanelOpened(MainContentPanel.CARD_MAP)) {
                            MapPanelForm mapPanel = new MapPanelForm();
                            getMainContentPanel().addPanel(mapPanel, MainContentPanel.CARD_MAP);
                        }
                        getMainContentPanel().showPanel(MainContentPanel.CARD_MAP);
                        return null;
                    }
                };
                TaskManager.getInstance().runTask(t);
            } // Service enquiry (application status report)
            else if (requestType.equalsIgnoreCase(RequestTypeBean.CODE_SERVICE_ENQUIRY)) {
                SolaTask t = new SolaTask<Void, Void>() {

                    @Override
                    public Void doTask() {
                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_APPSEARCH));
                        if (!getMainContentPanel().isPanelOpened(MainContentPanel.CARD_APPSEARCH)) {
                            ApplicationSearchPanel searchApplicationPanel = new ApplicationSearchPanel();
                            getMainContentPanel().addPanel(searchApplicationPanel, MainContentPanel.CARD_APPSEARCH);
                        }
                        getMainContentPanel().showPanel(MainContentPanel.CARD_APPSEARCH);
                        return null;
                    }
                };
                TaskManager.getInstance().runTask(t);
            } // Cadastre change services
            else if (requestType.equalsIgnoreCase(RequestTypeBean.CODE_CADASTRE_CHANGE)
                    || requestType.equalsIgnoreCase(RequestTypeBean.CODE_CADASTRE_REDEFINITION)) {

                if (appBean.getPropertyList().getFilteredList().size() == 1) {
                    SolaTask t = new SolaTask<Void, Void>() {

                        @Override
                        public Void doTask() {
                            setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_CADASTRE_CHANGE));
                            CadastreTransactionMapPanel form = new CadastreTransactionMapPanel(
                                    appBean,
                                    appBean.getSelectedService(),
                                    appBean.getPropertyList().getFilteredList().get(0));
                            getMainContentPanel().addPanel(form, MainContentPanel.CARD_CADASTRECHANGE, true);
                            return null;
                        }
                    };
                    TaskManager.getInstance().runTask(t);

                } else if (appBean.getPropertyList().getFilteredList().size() > 1) {
                    PropertiesList propertyListForm = new PropertiesList(appBean.getPropertyList());
                    propertyListForm.setLocationRelativeTo(this);

                    propertyListForm.addPropertyChangeListener(new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName().equals(PropertiesList.SELECTED_PROPERTY)
                                    && evt.getNewValue() != null) {
                                final ApplicationPropertyBean property =
                                        (ApplicationPropertyBean) evt.getNewValue();
                                ((JDialog) evt.getSource()).dispose();

                                SolaTask t = new SolaTask<Void, Void>() {

                                    @Override
                                    public Void doTask() {
                                        setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_DOCREGISTRATION));
                                        CadastreTransactionMapPanel form = new CadastreTransactionMapPanel(
                                                appBean,
                                                appBean.getSelectedService(), property);
                                        getMainContentPanel().addPanel(
                                                form, MainContentPanel.CARD_CADASTRECHANGE, true);
                                        return null;
                                    }
                                };
                                TaskManager.getInstance().runTask(t);
                            }
                        }
                    });
                    propertyListForm.setVisible(true);

                } else {
                    CadastreTransactionMapPanel form = new CadastreTransactionMapPanel(
                            appBean, appBean.getSelectedService(), null);
                    getMainContentPanel().addPanel(
                            form, MainContentPanel.CARD_CADASTRECHANGE, true);
                }

            } else {

                // Try to get BA Units, craeted through the service
                List<BaUnitBean> baUnitsList = BaUnitBean.getBaUnitsByServiceId(appBean.getSelectedService().getId());

                if (baUnitsList != null && baUnitsList.size() > 0) {
                    if (baUnitsList.size() > 1) {
                        // Show BA Unit Selection Form
                        BaUnitsListPanel baUnitListPanel = new BaUnitsListPanel(baUnitsList);
                        baUnitListPanel.addPropertyChangeListener(new PropertyChangeListener() {

                            @Override
                            public void propertyChange(PropertyChangeEvent evt) {
                                if (evt.getPropertyName().equals(BaUnitsListPanel.SELECTED_BAUNIT_PROPERTY)
                                        && evt.getNewValue() != null) {
                                    BaUnitBean baUnitBean = (BaUnitBean) evt.getNewValue();
                                    openPropertyForm(baUnitBean, readOnly);
                                    ((ContentPanel) evt.getSource()).close();
                                }
                            }
                        });
                        getMainContentPanel().addPanel(baUnitListPanel, MainContentPanel.CARD_BAUNIT_SELECT_PANEL, true);
                    } else {
                        openPropertyForm(baUnitsList.get(0), readOnly);
                    }
                } else {

                    // Open property form for new title registration
                    if (requestType.equalsIgnoreCase(RequestTypeBean.CODE_NEW_APARTMENT)
                            || requestType.equalsIgnoreCase(RequestTypeBean.CODE_NEW_FREEHOLD)
                            || requestType.equalsIgnoreCase(RequestTypeBean.CODE_NEW_STATE)) {
                        if (!readOnly) {
                            // Open empty property form
                            openPropertyForm(new BaUnitBean(), readOnly);
                        }
                    } else {

                        // Open property form for existing title changes
                        if (appBean.getPropertyList().getFilteredList().size() == 1) {
                            openPropertyForm(appBean.getPropertyList().getFilteredList().get(0), readOnly);
                        } else if (appBean.getPropertyList().getFilteredList().size() > 1) {
                            PropertiesList propertyListForm = new PropertiesList(appBean.getPropertyList());
                            propertyListForm.setLocationRelativeTo(this);

                            propertyListForm.addPropertyChangeListener(new PropertyChangeListener() {

                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    if (evt.getPropertyName().equals(PropertiesList.SELECTED_PROPERTY)
                                            && evt.getNewValue() != null) {
                                        ApplicationPropertyBean property = (ApplicationPropertyBean) evt.getNewValue();
                                        ((JDialog) evt.getSource()).dispose();
                                        openPropertyForm(property, readOnly);
                                    }
                                }
                            });

                            propertyListForm.setVisible(true);
                        } else {
                            MessageUtility.displayMessage(ClientMessage.APPLICATION_PROPERTY_LIST_EMPTY);
                        }
                    }
                }
            }

        } else {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_SELECT_SERVICE);
        }
    }

    private boolean saveApplication() {
        appBean.setLocation(this.mapControl.getApplicationLocation());
        if (applicationID != null && !applicationID.equals("")) {
            return appBean.saveApplication();
        } else {
            return appBean.lodgeApplication();
        }
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
                MessageUtility.displayMessage(ClientMessage.APPLICATION_SUCCESSFULLY_SAVED);
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

    private void openSeachDocuments() {
        DocumentSearchDialog form = new DocumentSearchDialog(null, true);
        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(org.sola.clients.swing.ui.source.DocumentSearchPanel.SELECT_SOURCE)) {
                    appBean.getSourceList().addAsNew((SourceBean) evt.getNewValue());
                    MessageUtility.displayMessage(ClientMessage.SOURCE_ADDED);
                }
            }
        });
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }

    /**
     * Designer generated code
     */
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
        communicationTypes = createCommunicationTypes();
        popupApplicationActions = new javax.swing.JPopupMenu();
        menuApprove = new javax.swing.JMenuItem();
        menuCancel = new javax.swing.JMenuItem();
        menuWithdraw = new javax.swing.JMenuItem();
        menuLapse = new javax.swing.JMenuItem();
        menuRequisition = new javax.swing.JMenuItem();
        menuResubmit = new javax.swing.JMenuItem();
        menuDispatch = new javax.swing.JMenuItem();
        menuArchive = new javax.swing.JMenuItem();
        pnlHeader = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnCalculateFee = new javax.swing.JButton();
        btnValidate = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnPrintFee = new javax.swing.JButton();
        btnPrintStatusReport = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        dropDownButton1 = new org.sola.clients.swing.common.controls.DropDownButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel22 = new javax.swing.JPanel();
        tabbedControlMain = new javax.swing.JTabbedPane();
        contactPanel = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtFirstName = new javax.swing.JTextField();
        labName = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        labLastName = new javax.swing.JLabel();
        txtLastName = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        txtAddress = new javax.swing.JTextField();
        labAddress = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        labPhone = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        labFax = new javax.swing.JLabel();
        txtFax = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        labEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        labPreferredWay = new javax.swing.JLabel();
        cbxCommunicationWay = new javax.swing.JComboBox();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel23 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        labAgents = new javax.swing.JLabel();
        cbxAgents = new javax.swing.JComboBox();
        jPanel15 = new javax.swing.JPanel();
        labStatus = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        jPanel25 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtAppNumber = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        labDate = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jPanel26 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtCompleteBy = new javax.swing.JFormattedTextField();
        servicesPanel = new javax.swing.JPanel();
        scrollFeeDetails1 = new javax.swing.JScrollPane();
        tabServices = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        tbServices = new javax.swing.JToolBar();
        btnAddService = new javax.swing.JButton();
        btnRemoveService = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnUPService = new javax.swing.JButton();
        btnDownService = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnViewService = new javax.swing.JButton();
        btnStartService = new javax.swing.JButton();
        btnCompleteService = new javax.swing.JButton();
        btnRevertService = new javax.swing.JButton();
        btnCancelService = new javax.swing.JButton();
        propertyPanel = new javax.swing.JPanel();
        tbPropertyDetails = new javax.swing.JToolBar();
        btnRemoveProperty = new javax.swing.JButton();
        btnVerifyProperty = new javax.swing.JButton();
        scrollPropertyDetails = new javax.swing.JScrollPane();
        tabPropertyDetails = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel20 = new javax.swing.JPanel();
        propertypartPanel = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        labFirstPart = new javax.swing.JLabel();
        txtFirstPart = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        txtLastPart = new javax.swing.JTextField();
        labLastPart = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        labArea = new javax.swing.JLabel();
        txtArea = new javax.swing.JTextField();
        jPanel19 = new javax.swing.JPanel();
        labValue = new javax.swing.JLabel();
        txtValue = new javax.swing.JTextField();
        jPanel21 = new javax.swing.JPanel();
        btnAddProperty = new javax.swing.JButton();
        documentPanel = new javax.swing.JPanel();
        scrollDocuments = new javax.swing.JScrollPane();
        tabDocuments = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        labDocRequired = new javax.swing.JLabel();
        scrollDocRequired = new javax.swing.JScrollPane();
        tblDocTypesHelper = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel1 = new javax.swing.JPanel();
        addDocumentPanel = new org.sola.clients.swing.ui.source.DocumentPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddExistingDocument = new javax.swing.JButton();
        btnDeleteDoc = new javax.swing.JButton();
        btnOpenAttachment = new javax.swing.JButton();
        mapPanel = new javax.swing.JPanel();
        feesPanel = new javax.swing.JPanel();
        scrollFeeDetails = new javax.swing.JScrollPane();
        tabFeeDetails = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel2 = new javax.swing.JPanel();
        formTxtServiceFee = new javax.swing.JFormattedTextField();
        formTxtTaxes = new javax.swing.JFormattedTextField();
        formTxtFee = new javax.swing.JFormattedTextField();
        formTxtPaid = new javax.swing.JFormattedTextField();
        cbxPaid = new javax.swing.JCheckBox();
        labTotalFee3 = new javax.swing.JLabel();
        labTotalFee2 = new javax.swing.JLabel();
        labTotalFee = new javax.swing.JLabel();
        labTotalFee1 = new javax.swing.JLabel();
        labFixedFee = new javax.swing.JLabel();
        validationPanel = new javax.swing.JPanel();
        validationsPanel = new javax.swing.JScrollPane();
        tabValidations = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        historyPanel = new javax.swing.JPanel();
        actionLogPanel = new javax.swing.JScrollPane();
        tabActionLog = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        popUpServices.setName("popUpServices"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        menuAddService.setText(bundle.getString("ApplicationPanel.menuAddService.text")); // NOI18N
        menuAddService.setName("menuAddService"); // NOI18N
        menuAddService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuAddService);

        menuRemoveService.setText(bundle.getString("ApplicationPanel.menuRemoveService.text")); // NOI18N
        menuRemoveService.setName("menuRemoveService"); // NOI18N
        menuRemoveService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuRemoveService);

        jSeparator3.setName("jSeparator3"); // NOI18N
        popUpServices.add(jSeparator3);

        menuMoveServiceUp.setText(bundle.getString("ApplicationPanel.menuMoveServiceUp.text")); // NOI18N
        menuMoveServiceUp.setName("menuMoveServiceUp"); // NOI18N
        menuMoveServiceUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuMoveServiceUpActionPerformed(evt);
            }
        });
        popUpServices.add(menuMoveServiceUp);

        menuMoveServiceDown.setText(bundle.getString("ApplicationPanel.menuMoveServiceDown.text")); // NOI18N
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
        menuViewService.setText(bundle.getString("ApplicationPanel.menuViewService.text")); // NOI18N
        menuViewService.setName("menuViewService"); // NOI18N
        menuViewService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuViewService);

        menuStartService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/start.png"))); // NOI18N
        menuStartService.setText(bundle.getString("ApplicationPanel.menuStartService.text")); // NOI18N
        menuStartService.setName("menuStartService"); // NOI18N
        menuStartService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuStartServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuStartService);

        menuCompleteService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm.png"))); // NOI18N
        menuCompleteService.setText(bundle.getString("ApplicationPanel.menuCompleteService.text")); // NOI18N
        menuCompleteService.setName("menuCompleteService"); // NOI18N
        menuCompleteService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCompleteServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuCompleteService);

        menuRevertService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/revert.png"))); // NOI18N
        menuRevertService.setText(bundle.getString("ApplicationPanel.menuRevertService.text")); // NOI18N
        menuRevertService.setName("menuRevertService"); // NOI18N
        menuRevertService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRevertServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuRevertService);

        menuCancelService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/cancel.png"))); // NOI18N
        menuCancelService.setText(bundle.getString("ApplicationPanel.menuCancelService.text")); // NOI18N
        menuCancelService.setName("menuCancelService"); // NOI18N
        menuCancelService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCancelServiceActionPerformed(evt);
            }
        });
        popUpServices.add(menuCancelService);

        popupApplicationActions.setName("popupApplicationActions"); // NOI18N

        menuApprove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/approve.png"))); // NOI18N
        menuApprove.setText(bundle.getString("ApplicationPanel.menuApprove.text")); // NOI18N
        menuApprove.setName("menuApprove"); // NOI18N
        menuApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuApproveActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuApprove);

        menuCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/reject.png"))); // NOI18N
        menuCancel.setText(bundle.getString("ApplicationPanel.menuCancel.text")); // NOI18N
        menuCancel.setName("menuCancel"); // NOI18N
        menuCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCancelActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuCancel);

        menuWithdraw.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/withdraw.png"))); // NOI18N
        menuWithdraw.setText(bundle.getString("ApplicationPanel.menuWithdraw.text")); // NOI18N
        menuWithdraw.setName("menuWithdraw"); // NOI18N
        menuWithdraw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuWithdrawActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuWithdraw);

        menuLapse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lapse.png"))); // NOI18N
        menuLapse.setText(bundle.getString("ApplicationPanel.menuLapse.text")); // NOI18N
        menuLapse.setName("menuLapse"); // NOI18N
        menuLapse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLapseActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuLapse);

        menuRequisition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/requisition.png"))); // NOI18N
        menuRequisition.setText(bundle.getString("ApplicationPanel.menuRequisition.text")); // NOI18N
        menuRequisition.setName("menuRequisition"); // NOI18N
        menuRequisition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRequisitionActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuRequisition);

        menuResubmit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/resubmit.png"))); // NOI18N
        menuResubmit.setText(bundle.getString("ApplicationPanel.menuResubmit.text")); // NOI18N
        menuResubmit.setName("menuResubmit"); // NOI18N
        menuResubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuResubmitActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuResubmit);

        menuDispatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/envelope.png"))); // NOI18N
        menuDispatch.setText(bundle.getString("ApplicationPanel.menuDispatch.text")); // NOI18N
        menuDispatch.setName("menuDispatch"); // NOI18N
        menuDispatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDispatchActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuDispatch);

        menuArchive.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/archive.png"))); // NOI18N
        menuArchive.setText(bundle.getString("ApplicationPanel.menuArchive.text")); // NOI18N
        menuArchive.setName("menuArchive"); // NOI18N
        menuArchive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuArchiveActionPerformed(evt);
            }
        });
        popupApplicationActions.add(menuArchive);

        setHeaderPanel(pnlHeader);
        setHelpTopic(bundle.getString("ApplicationPanel.helpTopic")); // NOI18N
        setMinimumSize(new java.awt.Dimension(660, 458));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(660, 458));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        pnlHeader.setName("pnlHeader"); // NOI18N
        pnlHeader.setTitleText(bundle.getString("ApplicationPanel.pnlHeader.titleText")); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        LafManager.getInstance().setBtnProperties(btnSave);
        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("ApplicationPanel.btnSave.text")); // NOI18N
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar3.add(btnSave);

        LafManager.getInstance().setBtnProperties(btnCalculateFee);
        btnCalculateFee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calculate.png"))); // NOI18N
        btnCalculateFee.setText(bundle.getString("ApplicationPanel.btnCalculateFee.text")); // NOI18N
        btnCalculateFee.setName("btnCalculateFee"); // NOI18N
        btnCalculateFee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateFeeActionPerformed(evt);
            }
        });
        jToolBar3.add(btnCalculateFee);

        btnValidate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/validation.png"))); // NOI18N
        btnValidate.setText(bundle.getString("ApplicationPanel.btnValidate.text")); // NOI18N
        btnValidate.setName("btnValidate"); // NOI18N
        btnValidate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        btnValidate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnValidateActionPerformed(evt);
            }
        });
        jToolBar3.add(btnValidate);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar3.add(jSeparator6);

        btnPrintFee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintFee.setText(bundle.getString("ApplicationPanel.btnPrintFee.text")); // NOI18N
        btnPrintFee.setName("btnPrintFee"); // NOI18N
        btnPrintFee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintFeeActionPerformed(evt);
            }
        });
        jToolBar3.add(btnPrintFee);

        btnPrintStatusReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintStatusReport.setText(bundle.getString("ApplicationPanel.btnPrintStatusReport.text")); // NOI18N
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

        dropDownButton1.setText(bundle.getString("ApplicationPanel.dropDownButton1.text")); // NOI18N
        dropDownButton1.setComponentPopupMenu(popupApplicationActions);
        dropDownButton1.setFocusable(false);
        dropDownButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dropDownButton1.setName("dropDownButton1"); // NOI18N
        dropDownButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(dropDownButton1);

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(642, 352));

        jPanel22.setName("jPanel22"); // NOI18N
        jPanel22.setPreferredSize(new java.awt.Dimension(640, 435));
        jPanel22.setRequestFocusEnabled(false);

        tabbedControlMain.setName("tabbedControlMain"); // NOI18N
        tabbedControlMain.setPreferredSize(new java.awt.Dimension(440, 370));
        tabbedControlMain.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        contactPanel.setName("contactPanel"); // NOI18N
        contactPanel.setPreferredSize(new java.awt.Dimension(645, 331));
        contactPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        contactPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contactPanelMouseClicked(evt);
            }
        });

        jPanel12.setName("jPanel12"); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel3.setName("jPanel3"); // NOI18N

        txtFirstName.setName("txtFirstName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.name}"), txtFirstName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        LafManager.getInstance().setTxtProperties(txtFirstName);

        LafManager.getInstance().setLabProperties(labName);
        labName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labName.setLabelFor(txtFirstName);
        labName.setText(bundle.getString("ApplicationPanel.labName.text")); // NOI18N
        labName.setIconTextGap(1);
        labName.setName("labName"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(labName, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(189, Short.MAX_VALUE))
            .addComponent(txtFirstName, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(labName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel3);

        jPanel4.setName("jPanel4"); // NOI18N

        LafManager.getInstance().setLabProperties(labLastName);
        labLastName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labLastName.setText(bundle.getString("ApplicationPanel.labLastName.text")); // NOI18N
        labLastName.setIconTextGap(1);
        labLastName.setName("labLastName"); // NOI18N

        txtLastName.setName("txtLastName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.lastName}"), txtLastName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtLastName.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastName.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(labLastName)
                .addContainerGap(236, Short.MAX_VALUE))
            .addComponent(txtLastName, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(labLastName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel4);

        jPanel5.setName("jPanel5"); // NOI18N

        txtAddress.setName("txtAddress"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.address.description}"), txtAddress, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtAddress.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtAddress.setHorizontalAlignment(JTextField.LEADING);

        LafManager.getInstance().setLabProperties(labAddress);
        labAddress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labAddress.setText(bundle.getString("ApplicationPanel.labAddress.text")); // NOI18N
        labAddress.setIconTextGap(1);
        labAddress.setName("labAddress"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(labAddress)
                .addContainerGap())
            .addComponent(txtAddress)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(labAddress)
                .addGap(4, 4, 4)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setName("jPanel11"); // NOI18N
        jPanel11.setLayout(new java.awt.GridLayout(2, 2, 15, 0));

        jPanel7.setName("jPanel7"); // NOI18N

        LafManager.getInstance().setLabProperties(labPhone);
        labPhone.setText(bundle.getString("ApplicationPanel.labPhone.text")); // NOI18N
        labPhone.setName("labPhone"); // NOI18N

        txtPhone.setName("txtPhone"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.phone}"), txtPhone, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtPhone.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtPhone.setHorizontalAlignment(JTextField.LEADING);
        txtPhone.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPhoneFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(labPhone)
                .addContainerGap(266, Short.MAX_VALUE))
            .addComponent(txtPhone, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(labPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel7);

        jPanel8.setName("jPanel8"); // NOI18N

        LafManager.getInstance().setLabProperties(labFax);
        labFax.setText(bundle.getString("ApplicationPanel.labFax.text")); // NOI18N
        labFax.setName("labFax"); // NOI18N

        txtFax.setName("txtFax"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.fax}"), txtFax, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtFax.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFax.setHorizontalAlignment(JTextField.LEADING);
        txtFax.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtFaxFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtFax, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(labFax)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(labFax, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel8);

        jPanel9.setName("jPanel9"); // NOI18N

        LafManager.getInstance().setLabProperties(labEmail);
        labEmail.setText(bundle.getString("ApplicationPanel.labEmail.text")); // NOI18N
        labEmail.setName("labEmail"); // NOI18N

        txtEmail.setName("txtEmail"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.email}"), txtEmail, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtEmail.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtEmail.setHorizontalAlignment(JTextField.LEADING);
        txtEmail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEmailFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(labEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(160, Short.MAX_VALUE))
            .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(labEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel9);

        jPanel10.setName("jPanel10"); // NOI18N

        LafManager.getInstance().setLabProperties(labPreferredWay);
        labPreferredWay.setText(bundle.getString("ApplicationPanel.labPreferredWay.text")); // NOI18N
        labPreferredWay.setName("labPreferredWay"); // NOI18N

        LafManager.getInstance().setCmbProperties(cbxCommunicationWay);
        cbxCommunicationWay.setMaximumRowCount(9);
        cbxCommunicationWay.setName("cbxCommunicationWay"); // NOI18N
        cbxCommunicationWay.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${communicationTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, communicationTypes, eLProperty, cbxCommunicationWay);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.preferredCommunication}"), cbxCommunicationWay, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbxCommunicationWay.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(labPreferredWay)
                .addContainerGap(141, Short.MAX_VALUE))
            .addComponent(cbxCommunicationWay, 0, 330, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(labPreferredWay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxCommunicationWay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel10);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
        );

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("ApplicationPanel.groupPanel1.titleText")); // NOI18N

        jPanel23.setName("jPanel23"); // NOI18N
        jPanel23.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        jPanel14.setMinimumSize(new java.awt.Dimension(28, 20));
        jPanel14.setName("jPanel14"); // NOI18N

        LafManager.getInstance().setLabProperties(labAgents);
        labAgents.setText(bundle.getString("ApplicationPanel.labAgents.text")); // NOI18N
        labAgents.setIconTextGap(1);
        labAgents.setName("labAgents"); // NOI18N

        LafManager.getInstance().setCmbProperties(cbxAgents);
        cbxAgents.setName("cbxAgents"); // NOI18N
        cbxAgents.setPreferredSize(new java.awt.Dimension(28, 30));
        cbxAgents.setRenderer(new SimpleComboBoxRenderer("getFullName"));
        cbxAgents.setRequestFocusEnabled(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${partySummaryList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySummaryList, eLProperty, cbxAgents);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${agent}"), cbxAgents, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbxAgents.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbxAgents, 0, 330, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(labAgents)
                .addContainerGap(267, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(labAgents)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxAgents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.add(jPanel14);

        jPanel15.setName("jPanel15"); // NOI18N

        LafManager.getInstance().setLabProperties(labStatus);
        labStatus.setText(bundle.getString("ApplicationPanel.labStatus.text")); // NOI18N
        labStatus.setName("labStatus"); // NOI18N

        txtStatus.setEditable(false);
        txtStatus.setName("txtStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtStatus.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtStatus.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(labStatus)
                .addContainerGap(265, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(labStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jPanel23.add(jPanel15);

        jPanel25.setName("jPanel25"); // NOI18N
        jPanel25.setLayout(new java.awt.GridLayout(1, 3, 15, 0));

        jPanel24.setEnabled(false);
        jPanel24.setFocusable(false);
        jPanel24.setName("jPanel24"); // NOI18N
        jPanel24.setRequestFocusEnabled(false);

        jLabel1.setText(bundle.getString("ApplicationPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtAppNumber.setEditable(false);
        txtAppNumber.setEnabled(false);
        txtAppNumber.setFocusable(false);
        txtAppNumber.setName("txtAppNumber"); // NOI18N
        txtAppNumber.setRequestFocusEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${nr}"), txtAppNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtAppNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
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
        labDate.setText(bundle.getString("ApplicationPanel.labDate.text")); // NOI18N
        labDate.setName("labDate"); // NOI18N

        txtDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        txtDate.setText(bundle.getString("ApplicationPanel.txtDate.text")); // NOI18N
        txtDate.setEnabled(false);
        txtDate.setFocusable(false);
        txtDate.setName(bundle.getString("ApplicationPanel.txtDate.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${lodgingDatetime}"), txtDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(labDate, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
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

        jPanel26.setEnabled(false);
        jPanel26.setFocusable(false);
        jPanel26.setName("jPanel26"); // NOI18N
        jPanel26.setRequestFocusEnabled(false);

        jLabel2.setText(bundle.getString("ApplicationPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtCompleteBy.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        txtCompleteBy.setText(bundle.getString("ApplicationPanel.txtCompleteBy.text")); // NOI18N
        txtCompleteBy.setEnabled(false);
        txtCompleteBy.setFocusable(false);
        txtCompleteBy.setName(bundle.getString("ApplicationPanel.txtCompleteBy.name")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${expectedCompletionDate}"), txtCompleteBy, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
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

        javax.swing.GroupLayout contactPanelLayout = new javax.swing.GroupLayout(contactPanel);
        contactPanel.setLayout(contactPanelLayout);
        contactPanelLayout.setHorizontalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        contactPanelLayout.setVerticalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.contactPanel.TabConstraints.tabTitle"), contactPanel); // NOI18N

        servicesPanel.setName("servicesPanel"); // NOI18N

        scrollFeeDetails1.setBackground(new java.awt.Color(255, 255, 255));
        scrollFeeDetails1.setName("scrollFeeDetails1"); // NOI18N
        scrollFeeDetails1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabServices.setComponentPopupMenu(popUpServices);
        tabServices.setName("tabServices"); // NOI18N
        tabServices.setNextFocusableComponent(btnSave);
        tabServices.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        tabServices.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${serviceList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabServices);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${serviceOrder}"));
        columnBinding.setColumnName("Service Order");
        columnBinding.setColumnClass(Integer.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${requestType.displayValue}"));
        columnBinding.setColumnName("Request Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"));
        columnBinding.setColumnName("Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${selectedService}"), tabServices, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        scrollFeeDetails1.setViewportView(tabServices);
        tabServices.getColumnModel().getColumn(0).setMinWidth(70);
        tabServices.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabServices.getColumnModel().getColumn(0).setMaxWidth(70);
        tabServices.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationPanel.tabFeeDetails1.columnModel.title0")); // NOI18N
        tabServices.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationPanel.tabFeeDetails1.columnModel.title1")); // NOI18N
        tabServices.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationPanel.tabFeeDetails1.columnModel.title2")); // NOI18N

        tbServices.setFloatable(false);
        tbServices.setRollover(true);
        tbServices.setName("tbServices"); // NOI18N

        btnAddService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddService.setText(bundle.getString("ApplicationPanel.btnAddService.text")); // NOI18N
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
        btnRemoveService.setText(bundle.getString("ApplicationPanel.btnRemoveService.text")); // NOI18N
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
        btnUPService.setText(bundle.getString("ApplicationPanel.btnUPService.text")); // NOI18N
        btnUPService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUPService.setName("btnUPService"); // NOI18N
        btnUPService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUPServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnUPService);

        btnDownService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/down.png"))); // NOI18N
        btnDownService.setText(bundle.getString("ApplicationPanel.btnDownService.text")); // NOI18N
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

        btnViewService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewService.setText(bundle.getString("ApplicationPanel.btnViewService.text")); // NOI18N
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

        btnStartService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/start.png"))); // NOI18N
        btnStartService.setText(bundle.getString("ApplicationPanel.btnStartService.text")); // NOI18N
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
        btnCompleteService.setText(bundle.getString("ApplicationPanel.btnCompleteService.text")); // NOI18N
        btnCompleteService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCompleteService.setName("btnCompleteService"); // NOI18N
        btnCompleteService.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompleteServiceActionPerformed(evt);
            }
        });
        tbServices.add(btnCompleteService);

        btnRevertService.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/revert.png"))); // NOI18N
        btnRevertService.setText(bundle.getString("ApplicationPanel.btnRevertService.text")); // NOI18N
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
        btnCancelService.setText(bundle.getString("ApplicationPanel.btnCancelService.text")); // NOI18N
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
                    .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addComponent(tbServices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE))
                .addContainerGap())
        );
        servicesPanelLayout.setVerticalGroup(
            servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(servicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbServices, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.servicesPanel.TabConstraints.tabTitle"), servicesPanel); // NOI18N

        propertyPanel.setName("propertyPanel"); // NOI18N
        propertyPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        propertyPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                propertyPanelMouseClicked(evt);
            }
        });

        tbPropertyDetails.setFloatable(false);
        tbPropertyDetails.setRollover(true);
        tbPropertyDetails.setToolTipText(bundle.getString("ApplicationPanel.tbPropertyDetails.toolTipText")); // NOI18N
        tbPropertyDetails.setName("tbPropertyDetails"); // NOI18N

        btnRemoveProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveProperty.setText(bundle.getString("ApplicationPanel.btnRemoveProperty.text")); // NOI18N
        btnRemoveProperty.setName("btnRemoveProperty"); // NOI18N
        btnRemoveProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemovePropertyActionPerformed(evt);
            }
        });
        tbPropertyDetails.add(btnRemoveProperty);

        btnVerifyProperty.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/verify.png"))); // NOI18N
        btnVerifyProperty.setText(bundle.getString("ApplicationPanel.btnVerifyProperty.text")); // NOI18N
        btnVerifyProperty.setName("btnVerifyProperty"); // NOI18N
        btnVerifyProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyPropertyActionPerformed(evt);
            }
        });
        tbPropertyDetails.add(btnVerifyProperty);

        scrollPropertyDetails.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        scrollPropertyDetails.setName("scrollPropertyDetails"); // NOI18N
        scrollPropertyDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabPropertyDetails.setName("tabPropertyDetails"); // NOI18N
        tabPropertyDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredPropertyList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabPropertyDetails, "");
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
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${verifiedExists}"));
        columnBinding.setColumnName("Verified Exists");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${verifiedLocation}"));
        columnBinding.setColumnName("Verified Location");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${selectedProperty}"), tabPropertyDetails, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        scrollPropertyDetails.setViewportView(tabPropertyDetails);
        tabPropertyDetails.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationPanel.tabPropertyDetails.columnModel.title0")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationPanel.tabPropertyDetails.columnModel.title1")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationPanel.tabPropertyDetails.columnModel.title2")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationPanel.tabPropertyDetails.columnModel.title3")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ApplicationPanel.tabPropertyDetails.columnModel.title4")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("ApplicationPanel.tabPropertyDetails.columnModel.title6")); // NOI18N

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel20.setName("jPanel20"); // NOI18N

        propertypartPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        propertypartPanel.setName("propertypartPanel"); // NOI18N
        propertypartPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        propertypartPanel.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        jPanel16.setName("jPanel16"); // NOI18N

        LafManager.getInstance().setLabProperties(labFirstPart);
        labFirstPart.setText(bundle.getString("ApplicationPanel.labFirstPart.text")); // NOI18N
        labFirstPart.setName("labFirstPart"); // NOI18N

        LafManager.getInstance().setTxtProperties(txtFirstPart);
        txtFirstPart.setText(bundle.getString("ApplicationPanel.txtFirstPart.text")); // NOI18N
        txtFirstPart.setName("txtFirstPart"); // NOI18N
        txtFirstPart.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFirstPart.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(labFirstPart)
                .addContainerGap(60, Short.MAX_VALUE))
            .addComponent(txtFirstPart, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(labFirstPart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtFirstPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );

        propertypartPanel.add(jPanel16);

        jPanel17.setName("jPanel17"); // NOI18N

        txtLastPart.setText(bundle.getString("ApplicationPanel.txtLastPart.text")); // NOI18N
        txtLastPart.setName("txtLastPart"); // NOI18N
        txtLastPart.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastPart.setHorizontalAlignment(JTextField.LEADING);

        LafManager.getInstance().setLabProperties(labLastPart);
        labLastPart.setText(bundle.getString("ApplicationPanel.labLastPart.text")); // NOI18N
        labLastPart.setName("labLastPart"); // NOI18N

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(labLastPart)
                .addContainerGap(61, Short.MAX_VALUE))
            .addComponent(txtLastPart, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(labLastPart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLastPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        propertypartPanel.add(jPanel17);

        jPanel18.setName("jPanel18"); // NOI18N

        LafManager.getInstance().setLabProperties(labArea);
        labArea.setText(bundle.getString("ApplicationPanel.labArea.text")); // NOI18N
        labArea.setName("labArea"); // NOI18N

        txtArea.setText(bundle.getString("ApplicationPanel.txtArea.text")); // NOI18N
        txtArea.setName("txtArea"); // NOI18N
        txtArea.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtArea.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(labArea)
                .addContainerGap(56, Short.MAX_VALUE))
            .addComponent(txtArea, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(labArea)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        propertypartPanel.add(jPanel18);

        jPanel19.setName("jPanel19"); // NOI18N

        LafManager.getInstance().setLabProperties(labValue);
        labValue.setText(bundle.getString("ApplicationPanel.labValue.text")); // NOI18N
        labValue.setName("labValue"); // NOI18N

        txtValue.setText(bundle.getString("ApplicationPanel.txtValue.text")); // NOI18N
        txtValue.setName("txtValue"); // NOI18N
        txtValue.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtValue.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(labValue)
                .addContainerGap(78, Short.MAX_VALUE))
            .addComponent(txtValue, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(labValue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        propertypartPanel.add(jPanel19);

        jPanel21.setName("jPanel21"); // NOI18N
        jPanel21.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 18));

        LafManager.getInstance().setBtnProperties(btnAddProperty);
        btnAddProperty.setText(bundle.getString("ApplicationPanel.btnAddProperty.text")); // NOI18N
        btnAddProperty.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddProperty.setName("btnAddProperty"); // NOI18N
        btnAddProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPropertyActionPerformed(evt);
            }
        });
        jPanel21.add(btnAddProperty);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(propertypartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                    .addComponent(propertypartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout propertyPanelLayout = new javax.swing.GroupLayout(propertyPanel);
        propertyPanel.setLayout(propertyPanelLayout);
        propertyPanelLayout.setHorizontalGroup(
            propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, propertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbPropertyDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        propertyPanelLayout.setVerticalGroup(
            propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbPropertyDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.propertyPanel.TabConstraints.tabTitle"), propertyPanel); // NOI18N

        documentPanel.setName("documentPanel"); // NOI18N
        documentPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        documentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                documentPanelMouseClicked(evt);
            }
        });

        scrollDocuments.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        scrollDocuments.setName("scrollDocuments"); // NOI18N
        scrollDocuments.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabDocuments.setName("tabDocuments"); // NOI18N
        tabDocuments.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${sourceFilteredList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabDocuments);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sourceType.displayValue}"));
        columnBinding.setColumnName("Source Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${acceptance}"));
        columnBinding.setColumnName("Acceptance");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${laNr}"));
        columnBinding.setColumnName("La Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${referenceNr}"));
        columnBinding.setColumnName("Reference Nr");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${recordation}"));
        columnBinding.setColumnName("Recordation");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${submission}"));
        columnBinding.setColumnName("Submission");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${archiveDocumentId}"));
        columnBinding.setColumnName("Archive Document Id");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${selectedSource}"), tabDocuments, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tabDocuments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabDocumentsMouseClicked(evt);
            }
        });
        scrollDocuments.setViewportView(tabDocuments);
        tabDocuments.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationPanel.tabDocuments.columnModel.title0_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(0).setCellRenderer(new ReferenceCodeCellConverter(CacheManager.getSourceTypesMap()));
        tabDocuments.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationPanel.tabDocuments.columnModel.title1_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationPanel.tabDocuments.columnModel.title2_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationPanel.tabDocuments.columnModel.title3_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ApplicationPanel.tabDocuments.columnModel.title4_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("ApplicationPanel.tabDocuments.columnModel.title5")); // NOI18N
        tabDocuments.getColumnModel().getColumn(6).setPreferredWidth(30);
        tabDocuments.getColumnModel().getColumn(6).setMaxWidth(30);
        tabDocuments.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("ApplicationPanel.tabDocuments.columnModel.title6")); // NOI18N
        tabDocuments.getColumnModel().getColumn(6).setCellRenderer(new AttachedDocumentCellRenderer());

        labDocRequired.setBackground(new java.awt.Color(255, 255, 204));
        labDocRequired.setText(bundle.getString("ApplicationPanel.labDocRequired.text")); // NOI18N
        labDocRequired.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));
        labDocRequired.setName("labDocRequired"); // NOI18N
        labDocRequired.setOpaque(true);

        scrollDocRequired.setBackground(new java.awt.Color(255, 255, 255));
        scrollDocRequired.setName("scrollDocRequired"); // NOI18N
        scrollDocRequired.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tblDocTypesHelper.setBackground(new java.awt.Color(255, 255, 255));
        tblDocTypesHelper.setGridColor(new java.awt.Color(255, 255, 255));
        tblDocTypesHelper.setName("tblDocTypesHelper"); // NOI18N
        tblDocTypesHelper.setOpaque(false);
        tblDocTypesHelper.setShowHorizontalLines(false);
        tblDocTypesHelper.setShowVerticalLines(false);
        tblDocTypesHelper.getTableHeader().setResizingAllowed(false);
        tblDocTypesHelper.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${checkList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, applicationDocumentsHelper, eLProperty, tblDocTypesHelper);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${isInList}"));
        columnBinding.setColumnName("Is In List");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${displayValue}"));
        columnBinding.setColumnName("Display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        scrollDocRequired.setViewportView(tblDocTypesHelper);
        tblDocTypesHelper.getColumnModel().getColumn(0).setMinWidth(20);
        tblDocTypesHelper.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblDocTypesHelper.getColumnModel().getColumn(0).setMaxWidth(20);
        tblDocTypesHelper.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationPanel.tblDocTypesHelper.columnModel.title0_1")); // NOI18N
        tblDocTypesHelper.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationPanel.tblDocTypesHelper.columnModel.title1_1")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setName("jPanel1"); // NOI18N

        addDocumentPanel.setName("addDocumentPanel"); // NOI18N
        addDocumentPanel.setOkButtonText(bundle.getString("ApplicationPanel.addDocumentPanel.okButtonText")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addDocumentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addDocumentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 116, Short.MAX_VALUE))
        );

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setToolTipText(bundle.getString("ApplicationPanel.jToolBar1.toolTipText")); // NOI18N
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddExistingDocument.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddExistingDocument.setText(bundle.getString("ApplicationPanel.btnAddExistingDocument.text")); // NOI18N
        btnAddExistingDocument.setFocusable(false);
        btnAddExistingDocument.setName(bundle.getString("ApplicationPanel.btnAddExistingDocument.name")); // NOI18N
        btnAddExistingDocument.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddExistingDocument.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddExistingDocumentActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAddExistingDocument);

        btnDeleteDoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnDeleteDoc.setText(bundle.getString("ApplicationPanel.btnDeleteDoc.text")); // NOI18N
        btnDeleteDoc.setName("btnDeleteDoc"); // NOI18N
        btnDeleteDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteDocActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDeleteDoc);

        btnOpenAttachment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/document-view.png"))); // NOI18N
        btnOpenAttachment.setText(bundle.getString("ApplicationPanel.btnOpenAttachment.text")); // NOI18N
        btnOpenAttachment.setActionCommand(bundle.getString("ApplicationPanel.btnOpenAttachment.actionCommand")); // NOI18N
        btnOpenAttachment.setName("btnOpenAttachment"); // NOI18N
        btnOpenAttachment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenAttachmentActionPerformed(evt);
            }
        });
        jToolBar1.add(btnOpenAttachment);

        javax.swing.GroupLayout documentPanelLayout = new javax.swing.GroupLayout(documentPanel);
        documentPanel.setLayout(documentPanelLayout);
        documentPanelLayout.setHorizontalGroup(
            documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(documentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(labDocRequired, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollDocRequired, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
                .addContainerGap())
        );
        documentPanelLayout.setVerticalGroup(
            documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(documentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(documentPanelLayout.createSequentialGroup()
                        .addComponent(labDocRequired, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollDocRequired, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
                    .addGroup(documentPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.documentPanel.TabConstraints.tabTitle"), documentPanel); // NOI18N

        mapPanel.setName("mapPanel"); // NOI18N

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 635, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.mapPanel.TabConstraints.tabTitle"), mapPanel); // NOI18N

        feesPanel.setName("feesPanel"); // NOI18N
        feesPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        feesPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                feesPanelMouseClicked(evt);
            }
        });

        scrollFeeDetails.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        scrollFeeDetails.setName("scrollFeeDetails"); // NOI18N

        tabFeeDetails.setColumnSelectionAllowed(true);
        tabFeeDetails.setName("tabFeeDetails"); // NOI18N
        tabFeeDetails.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${serviceList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabFeeDetails);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${requestType.displayValue}"));
        columnBinding.setColumnName("Request Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${baseFee}"));
        columnBinding.setColumnName("Base Fee");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${areaFee}"));
        columnBinding.setColumnName("Area Fee");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${valueFee}"));
        columnBinding.setColumnName("Value Fee");
        columnBinding.setColumnClass(java.math.BigDecimal.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${expectedCompletionDate}"));
        columnBinding.setColumnName("Expected Completion Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        scrollFeeDetails.setViewportView(tabFeeDetails);
        tabFeeDetails.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabFeeDetails.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationPanel.tabFeeDetails.columnModel.title0")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationPanel.tabFeeDetails.columnModel.title1_1")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationPanel.tabFeeDetails.columnModel.title2_2")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationPanel.tabFeeDetails.columnModel.title3")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ApplicationPanel.tabFeeDetails.columnModel.title4")); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        formTxtServiceFee.setEditable(false);
        formTxtServiceFee.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtServiceFee.setInheritsPopupMenu(true);
        formTxtServiceFee.setName("formTxtServiceFee"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${servicesFee}"), formTxtServiceFee, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtServiceFee.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtServiceFee.setHorizontalAlignment(JFormattedTextField.LEADING);

        formTxtTaxes.setEditable(false);
        formTxtTaxes.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtTaxes.setInheritsPopupMenu(true);
        formTxtTaxes.setName("formTxtTaxes"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${tax}"), formTxtTaxes, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtTaxes.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtTaxes.setHorizontalAlignment(JFormattedTextField.LEADING);

        formTxtFee.setEditable(false);
        formTxtFee.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtFee.setName("formTxtFee"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${totalFee}"), formTxtFee, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtFee.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtFee.setHorizontalAlignment(JFormattedTextField.LEADING);

        formTxtPaid.setEditable(false);
        formTxtPaid.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtPaid.setName("formTxtPaid"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${totalAmountPaid}"), formTxtPaid, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtPaid.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtPaid.setHorizontalAlignment(JFormattedTextField.LEADING);

        cbxPaid.setText(bundle.getString("ApplicationPanel.cbxPaid.text")); // NOI18N
        cbxPaid.setActionCommand(bundle.getString("ApplicationPanel.cbxPaid.actionCommand")); // NOI18N
        cbxPaid.setMargin(new java.awt.Insets(2, 0, 2, 2));
        cbxPaid.setName("cbxPaid"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${feePaid}"), cbxPaid, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        labTotalFee3.setText(bundle.getString("ApplicationPanel.labTotalFee3.text")); // NOI18N
        labTotalFee3.setName("labTotalFee3"); // NOI18N

        LafManager.getInstance().setLabProperties(labTotalFee2);
        labTotalFee2.setText(bundle.getString("ApplicationPanel.labTotalFee2.text")); // NOI18N
        labTotalFee2.setName("labTotalFee2"); // NOI18N

        LafManager.getInstance().setLabProperties(labTotalFee);
        labTotalFee.setText(bundle.getString("ApplicationPanel.labTotalFee.text")); // NOI18N
        labTotalFee.setName("labTotalFee"); // NOI18N

        LafManager.getInstance().setLabProperties(labTotalFee1);
        labTotalFee1.setText(bundle.getString("ApplicationPanel.labTotalFee1.text")); // NOI18N
        labTotalFee1.setName("labTotalFee1"); // NOI18N

        labFixedFee.setBackground(new java.awt.Color(255, 255, 255));
        LafManager.getInstance().setLabProperties(labFixedFee);
        labFixedFee.setText(bundle.getString("ApplicationPanel.labFixedFee.text")); // NOI18N
        labFixedFee.setName("labFixedFee"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(formTxtServiceFee, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addComponent(formTxtTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(labFixedFee, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labTotalFee1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(formTxtFee)
                    .addComponent(labTotalFee, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(formTxtPaid)
                    .addComponent(labTotalFee2, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                .addGap(22, 22, 22)
                .addComponent(labTotalFee3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxPaid)
                .addGap(46, 46, 46))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {formTxtFee, formTxtPaid, formTxtServiceFee, formTxtTaxes});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(formTxtTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labTotalFee, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labFixedFee)
                                .addComponent(labTotalFee1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labTotalFee2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(formTxtFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(27, 27, 27)
                            .addComponent(formTxtServiceFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cbxPaid)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(formTxtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labTotalFee3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout feesPanelLayout = new javax.swing.GroupLayout(feesPanel);
        feesPanel.setLayout(feesPanelLayout);
        feesPanelLayout.setHorizontalGroup(
            feesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(feesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(feesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollFeeDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addGroup(feesPanelLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 17, Short.MAX_VALUE)))
                .addContainerGap())
        );
        feesPanelLayout.setVerticalGroup(
            feesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, feesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollFeeDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.feesPanel.TabConstraints.tabTitle"), feesPanel); // NOI18N

        validationPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        validationPanel.setName("validationPanel"); // NOI18N
        validationPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        validationsPanel.setBackground(new java.awt.Color(255, 255, 255));
        validationsPanel.setName("validationsPanel"); // NOI18N
        validationsPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabValidations.setName("tabValidations"); // NOI18N

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
        tabValidations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationPanel.tabValidations.columnModel.title1")); // NOI18N
        tabValidations.getColumnModel().getColumn(0).setCellRenderer(new TableCellTextAreaRenderer());
        tabValidations.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabValidations.getColumnModel().getColumn(1).setMaxWidth(100);
        tabValidations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationPanel.tabValidations.columnModel.title2")); // NOI18N
        tabValidations.getColumnModel().getColumn(2).setPreferredWidth(45);
        tabValidations.getColumnModel().getColumn(2).setMaxWidth(45);
        tabValidations.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationPanel.tabValidations.columnModel.title3")); // NOI18N
        tabValidations.getColumnModel().getColumn(2).setCellRenderer(new ViolationCellRenderer());
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
                .addComponent(validationsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.validationPanel.TabConstraints.tabTitle"), validationPanel); // NOI18N

        historyPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        historyPanel.setName("historyPanel"); // NOI18N
        historyPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        historyPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                historyPanelMouseClicked(evt);
            }
        });

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
        tabActionLog.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationPanel.tabActionLog.columnModel.title0")); // NOI18N
        tabActionLog.getColumnModel().getColumn(0).setCellRenderer(new DateTimeRenderer());
        tabActionLog.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationPanel.tabActionLog.columnModel.title1_1")); // NOI18N
        tabActionLog.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationPanel.tabActionLog.columnModel.title2_1")); // NOI18N
        tabActionLog.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationPanel.tabActionLog.columnModel.title3_1")); // NOI18N
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
                .addComponent(actionLogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.historyPanel.TabConstraints.tabTitle"), historyPanel); // NOI18N

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedControlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedControlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel22);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Validates user's data input and calls save operation on the {@link ApplicationBean}.
     */
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveApplication(false);
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnAddPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPropertyActionPerformed
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
        txtFirstPart.requestFocus();
    }//GEN-LAST:event_btnAddPropertyActionPerformed

    /**
     * Opens {@link FileBrowserForm} to select digital copy of the document and get it attached.
     */
    private void tabDocumentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabDocumentsMouseClicked
        if (evt.getClickCount() == 2) {
            openAttachment();
        }
    }//GEN-LAST:event_tabDocumentsMouseClicked

    /**
     * Removes attached digital copy from selected document.
     */
    private void txtEmailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEmailFocusLost
        // Verify the email address is valid
        if (appBean.getContactPerson().getEmail() == null
                || !appBean.getContactPerson().getEmail().equals(txtEmail.getText())) {
            txtEmail.setText(appBean.getContactPerson().getEmail());
        }
    }//GEN-LAST:event_txtEmailFocusLost

    private void txtPhoneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPhoneFocusLost
        // Verify the phone number is valid
        if (appBean.getContactPerson().getPhone() == null
                || !appBean.getContactPerson().getPhone().equals(txtPhone.getText())) {
            txtPhone.setText(appBean.getContactPerson().getPhone());
        }
    }//GEN-LAST:event_txtPhoneFocusLost

    private void txtFaxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFaxFocusLost
        // Verify the fax number is valid
        if (appBean.getContactPerson().getFax() == null
                || !appBean.getContactPerson().getFax().equals(txtFax.getText())) {
            txtFax.setText(appBean.getContactPerson().getFax());
        }
    }//GEN-LAST:event_txtFaxFocusLost

    private void contactPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contactPanelMouseClicked
        cbxAgents.requestFocus(false);
        txtFirstName.requestFocus();
    }//GEN-LAST:event_contactPanelMouseClicked

    private void propertyPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_propertyPanelMouseClicked
        cbxAgents.requestFocus(false);
        txtFirstPart.requestFocus();
    }//GEN-LAST:event_propertyPanelMouseClicked

    private void documentPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_documentPanelMouseClicked
        cbxAgents.requestFocus(false);
    }//GEN-LAST:event_documentPanelMouseClicked

    private void feesPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_feesPanelMouseClicked
        cbxAgents.requestFocus(false);
        formTxtServiceFee.requestFocus(true);
    }//GEN-LAST:event_feesPanelMouseClicked

    private void historyPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historyPanelMouseClicked
        cbxAgents.requestFocus(false);
    }//GEN-LAST:event_historyPanelMouseClicked

    private void btnCalculateFeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateFeeActionPerformed
        calculateFee();
    }//GEN-LAST:event_btnCalculateFeeActionPerformed

    private void btnValidateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValidateActionPerformed
        validateApplication();
    }//GEN-LAST:event_btnValidateActionPerformed

    private void btnPrintFeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintFeeActionPerformed
        printReceipt();
    }//GEN-LAST:event_btnPrintFeeActionPerformed

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

    private void btnAddServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddServiceActionPerformed
        addService();
    }//GEN-LAST:event_btnAddServiceActionPerformed

    private void btnRemoveServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveServiceActionPerformed
        removeService();
    }//GEN-LAST:event_btnRemoveServiceActionPerformed

    private void btnUPServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUPServiceActionPerformed
        moveServiceUp();
    }//GEN-LAST:event_btnUPServiceActionPerformed

    private void btnDownServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownServiceActionPerformed
        moveServiceDown();
    }//GEN-LAST:event_btnDownServiceActionPerformed

    private void btnViewServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewServiceActionPerformed
        viewService();
    }//GEN-LAST:event_btnViewServiceActionPerformed

    private void btnStartServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartServiceActionPerformed
        startService();
    }//GEN-LAST:event_btnStartServiceActionPerformed

    private void btnCancelServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelServiceActionPerformed
        cancelService();
    }//GEN-LAST:event_btnCancelServiceActionPerformed

    private void btnCompleteServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompleteServiceActionPerformed
        completeService();
    }//GEN-LAST:event_btnCompleteServiceActionPerformed

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

    private void btnRevertServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRevertServiceActionPerformed
        revertService();
    }//GEN-LAST:event_btnRevertServiceActionPerformed

    private void menuRevertServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRevertServiceActionPerformed
        revertService();
    }//GEN-LAST:event_menuRevertServiceActionPerformed

    private void btnRemovePropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemovePropertyActionPerformed
        removeSelectedProperty();
    }//GEN-LAST:event_btnRemovePropertyActionPerformed

    private void btnVerifyPropertyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyPropertyActionPerformed
        verifySelectedProperty();
    }//GEN-LAST:event_btnVerifyPropertyActionPerformed

    private void btnDeleteDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteDocActionPerformed
        removeSelectedSource();
    }//GEN-LAST:event_btnDeleteDocActionPerformed

    private void btnOpenAttachmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenAttachmentActionPerformed
        openAttachment();
    }//GEN-LAST:event_btnOpenAttachmentActionPerformed

    private void btnAddExistingDocumentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddExistingDocumentActionPerformed
        openSeachDocuments();
    }//GEN-LAST:event_btnAddExistingDocumentActionPerformed

    /**
     * Opens attached digital copy of the selected document
     */
    private void openAttachment() {
        if (appBean.getSelectedSource() != null
                && appBean.getSelectedSource().getArchiveDocument() != null) {
            SolaTask t = new SolaTask<Void, Void>() {

                @Override
                public Void doTask() {
                    setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_DOCUMENT_OPENING));
                    DocumentBean.openDocument(appBean.getSelectedSource().getArchiveDocument().getId(),
                            appBean.getSelectedSource().getArchiveDocument().getFileName());
                    return null;
                }
            };
            TaskManager.getInstance().runTask(t);
        }
    }

    /**
     * Initializes map control to display application location.
     */
    private void formComponentShown(java.awt.event.ComponentEvent evt) {
        if (this.mapControl == null) {
            this.mapControl = new ControlsBundleForApplicationLocation();
            this.mapControl.setApplicationLocation(appBean.getLocation());
            this.mapControl.setApplicationId(appBean.getId());
            this.mapPanel.setLayout(new BorderLayout());
            this.mapPanel.add(this.mapControl, BorderLayout.CENTER);
        }
    }

    /**
     * Clears fields on the <b>Properties</b> tab, after the new property is added into the list.
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

            SolaTask<List<ValidationResultBean>, List<ValidationResultBean>> t =
                    new SolaTask<List<ValidationResultBean>, List<ValidationResultBean>>() {

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
                        }
                    };
            TaskManager.getInstance().runTask(t);
        }
    }

    private void addService() {
        ServiceListForm serviceListForm = new ServiceListForm(appBean);
        serviceListForm.setLocationRelativeTo(this);
        serviceListForm.setVisible(true);
        btnCalculateFee.setEnabled(true);
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
     * Moves selected application service down in the services list. Calls {@link ApplicationBean#moveServiceDown()}
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
        launchService(false);
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
     * Removes selected property object from the properties list. Calls {@link ApplicationBean#removeSelectedProperty()}
     */
    private void removeSelectedProperty() {
        appBean.removeSelectedProperty();
    }

    /**
     * Verifies selected property object to check existence. Calls {@link ApplicationBean#verifyProperty()}
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

    private void removeSelectedSource() {
        if (appBean.getSelectedSource() != null) {
            //if (MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            appBean.removeSelectedSource();
            //}
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
     * Calculates fee for the application. Calls {@link ApplicationBean#calculateFee()}
     */
    private void calculateFee() {
        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_APP_CALCULATINGFEE));
                appBean.calculateFee();
                tabbedControlMain.setSelectedIndex(tabbedControlMain.indexOfComponent(feesPanel));
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    /**
     * Prints payment receipt.
     */
    private void printReceipt() {
        if (applicationID == null || applicationID.equals("")) {
            if (MessageUtility.displayMessage(ClientMessage.CHECK_NOT_LODGED_RECEIPT) == MessageUtility.BUTTON_TWO) {
                return;
            }
        }
        showReport(ReportManager.getApplicationFeeReport(appBean));
    }

    /**
     * Allows to overview service.
     */
    private void viewService() {
        launchService(true);
    }

    private void printStatusReport() {
        if (appBean.getRowVersion() > 0
                && ApplicationServiceBean.saveInformationService(RequestTypeBean.CODE_SERVICE_ENQUIRY)) {
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane actionLogPanel;
    private org.sola.clients.swing.ui.source.DocumentPanel addDocumentPanel;
    public org.sola.clients.beans.application.ApplicationBean appBean;
    private org.sola.clients.beans.application.ApplicationDocumentsHelperBean applicationDocumentsHelper;
    private javax.swing.JButton btnAddExistingDocument;
    private javax.swing.JButton btnAddProperty;
    private javax.swing.JButton btnAddService;
    private javax.swing.JButton btnCalculateFee;
    private javax.swing.JButton btnCancelService;
    private javax.swing.JButton btnCompleteService;
    private javax.swing.JButton btnDeleteDoc;
    private javax.swing.JButton btnDownService;
    private javax.swing.JButton btnOpenAttachment;
    private javax.swing.JButton btnPrintFee;
    private javax.swing.JButton btnPrintStatusReport;
    private javax.swing.JButton btnRemoveProperty;
    private javax.swing.JButton btnRemoveService;
    private javax.swing.JButton btnRevertService;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnStartService;
    private javax.swing.JButton btnUPService;
    private javax.swing.JButton btnValidate;
    private javax.swing.JButton btnVerifyProperty;
    private javax.swing.JButton btnViewService;
    private javax.swing.JComboBox cbxAgents;
    public javax.swing.JComboBox cbxCommunicationWay;
    private javax.swing.JCheckBox cbxPaid;
    private org.sola.clients.beans.referencedata.CommunicationTypeListBean communicationTypes;
    public javax.swing.JPanel contactPanel;
    public javax.swing.JPanel documentPanel;
    private org.sola.clients.swing.common.controls.DropDownButton dropDownButton1;
    public javax.swing.JPanel feesPanel;
    private javax.swing.JFormattedTextField formTxtFee;
    private javax.swing.JFormattedTextField formTxtPaid;
    private javax.swing.JFormattedTextField formTxtServiceFee;
    private javax.swing.JFormattedTextField formTxtTaxes;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    public javax.swing.JPanel historyPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel labAddress;
    private javax.swing.JLabel labAgents;
    private javax.swing.JLabel labArea;
    private javax.swing.JLabel labDate;
    private javax.swing.JLabel labDocRequired;
    private javax.swing.JLabel labEmail;
    private javax.swing.JLabel labFax;
    private javax.swing.JLabel labFirstPart;
    private javax.swing.JLabel labFixedFee;
    private javax.swing.JLabel labLastName;
    private javax.swing.JLabel labLastPart;
    private javax.swing.JLabel labName;
    private javax.swing.JLabel labPhone;
    private javax.swing.JLabel labPreferredWay;
    private javax.swing.JLabel labStatus;
    private javax.swing.JLabel labTotalFee;
    private javax.swing.JLabel labTotalFee1;
    private javax.swing.JLabel labTotalFee2;
    private javax.swing.JLabel labTotalFee3;
    private javax.swing.JLabel labValue;
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
    private javax.swing.JMenuItem menuRemoveService;
    private javax.swing.JMenuItem menuRequisition;
    private javax.swing.JMenuItem menuResubmit;
    private javax.swing.JMenuItem menuRevertService;
    private javax.swing.JMenuItem menuStartService;
    private javax.swing.JMenuItem menuViewService;
    private javax.swing.JMenuItem menuWithdraw;
    private org.sola.clients.beans.party.PartySummaryListBean partySummaryList;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JPopupMenu popUpServices;
    private javax.swing.JPopupMenu popupApplicationActions;
    public javax.swing.JPanel propertyPanel;
    private javax.swing.JPanel propertypartPanel;
    private javax.swing.JScrollPane scrollDocRequired;
    private javax.swing.JScrollPane scrollDocuments;
    private javax.swing.JScrollPane scrollFeeDetails;
    private javax.swing.JScrollPane scrollFeeDetails1;
    private javax.swing.JScrollPane scrollPropertyDetails;
    private javax.swing.JPanel servicesPanel;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabActionLog;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabDocuments;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabFeeDetails;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabPropertyDetails;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabServices;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabValidations;
    public javax.swing.JTabbedPane tabbedControlMain;
    private javax.swing.JToolBar tbPropertyDetails;
    private javax.swing.JToolBar tbServices;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblDocTypesHelper;
    public javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtAppNumber;
    private javax.swing.JTextField txtArea;
    private javax.swing.JFormattedTextField txtCompleteBy;
    private javax.swing.JFormattedTextField txtDate;
    public javax.swing.JTextField txtEmail;
    public javax.swing.JTextField txtFax;
    public javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtFirstPart;
    public javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtLastPart;
    public javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JTextField txtValue;
    public javax.swing.JPanel validationPanel;
    private org.sola.clients.beans.validation.ValidationResultListBean validationResultListBean;
    private javax.swing.JScrollPane validationsPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
