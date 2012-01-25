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
package org.sola.clients.swing.desktop.application;

import org.jdesktop.application.Action;
import org.sola.clients.swing.ui.validation.ValidationResultForm;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.party.PartySummaryListBean;
import org.sola.clients.beans.referencedata.RequestTypeBean;
import org.sola.clients.beans.digitalarchive.DocumentBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.swing.common.converters.DateConverter;
import org.sola.clients.swing.ui.renderers.SimpleComboBoxRenderer;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForApplicationLocation;
import org.sola.clients.swing.ui.DesktopClientExceptionHandler;
import org.sola.clients.swing.ui.renderers.AttachedDocumentCellRenderer;
import org.sola.clients.swing.ui.renderers.ReferenceCodeCellConverter;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.clients.beans.application.ApplicationDocumentsHelperBean;
import org.sola.clients.beans.referencedata.CommunicationTypeListBean;
import org.sola.clients.beans.referencedata.RequestTypeListBean;
import org.sola.clients.beans.source.SourceTypeListBean;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.casemanagement.ApplicationTO;
import java.util.Locale;
import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.jdesktop.application.Application;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.swing.desktop.DesktopApplication;
import org.sola.clients.swing.desktop.administrative.PropertyPanel;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.referencedata.ApplicationActionTypeBean;
import org.sola.clients.swing.desktop.cadastre.CadastreTransactionMapPanel;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer;
import org.sola.clients.swing.ui.renderers.ViolationCellRenderer;
import org.sola.clients.swing.ui.source.DocumentPanel;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.desktop.source.TransactionedDocumentsForm;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.ui.source.FileBrowserForm;
import org.sola.clients.swing.desktop.ReportViewerForm;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.common.RolesConstants;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;

/**
 * This form is used to create new application or edit existing one.
 * <p>The following list of beans is used to bind the data on the form:<br />
 * {@link ApplicationBean}, <br />{@link RequestTypeListBean}, <br />
 * {@link PartySummaryListBean}, <br />{@link CommunicationTypeListBean}, <br />
 * {@link SourceTypeListBean}, <br />{@link ApplicationDocumentsHelperBean}</p>
 */
public class ApplicationPanel extends ContentPanel {

    private ControlsBundleForApplicationLocation mapControl = null;
    private String applicationID;
    Object foreFont = LafManager.getInstance().getForeFont();
    Object labFont = LafManager.getInstance().getLabFont();
    Object bgFont = LafManager.getInstance().getBgFont();
    Object txtFont = LafManager.getInstance().getTxtFont();
    Object txtAreaFont = LafManager.getInstance().getTxtAreaFont();
    Object btnFont = LafManager.getInstance().getBtnFont();
    Object tabFont = LafManager.getInstance().getTabFont();
    Object cmbFont = LafManager.getInstance().getCmbFont();
    Object btnBackground = LafManager.getInstance().getBtnBackground();

    /** 
     * This method is used by the form designer to create {@link ApplicationBean}. 
     * It uses <code>applicationId</code> parameter passed to the form constructor.<br />
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
            communicationTypes = new CommunicationTypeListBean(true);
        }
        return communicationTypes;
    }

    /** Default constructor to create new application. */
    public ApplicationPanel() {
        this(null);
    }

    /** 
     * This constructor is used to open existing application for editing. 
     * @param applicationId ID of application to open.
     */
    public ApplicationPanel(String applicationId) {
        this.applicationID = applicationId;
        initComponents();
        postInit();
    }

    /** Runs post initialization actions to customize form elements. */
    private void postInit() {
        customizeComponents();

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

    /** Applies customization of component L&F. */
    private void customizeComponents() {

//    BUTTONS   
        LafManager.getInstance().setBtnProperties(btnAddProperty);
        LafManager.getInstance().setBtnProperties(btnAddService);
        LafManager.getInstance().setBtnProperties(btnCalculateFee);
        LafManager.getInstance().setBtnProperties(btnCancelService);
        LafManager.getInstance().setBtnProperties(btnCompleteRevertService);
        LafManager.getInstance().setBtnProperties(btnDeleteDoc);
        LafManager.getInstance().setBtnProperties(btnDownService);
        LafManager.getInstance().setBtnProperties(btnLodge);
        LafManager.getInstance().setBtnProperties(btnOpenAttachment);
        LafManager.getInstance().setBtnProperties(btnPrintFee);
        LafManager.getInstance().setBtnProperties(btnRemoveProperty);
        LafManager.getInstance().setBtnProperties(btnRemoveService);
        LafManager.getInstance().setBtnProperties(btnStartService);
        LafManager.getInstance().setBtnProperties(btnUPService);
        LafManager.getInstance().setBtnProperties(btnValidate);
        LafManager.getInstance().setBtnProperties(btnVerifyProperty);
        LafManager.getInstance().setBtnProperties(btnViewService);

//    COMBOBOXES
        LafManager.getInstance().setCmbProperties(cbxAgents);
        LafManager.getInstance().setCmbProperties(cbxCommunicationWay);


//     CHECKBOXES
        LafManager.getInstance().setChkProperties(cbxPaid);

//    LABELS    
        LafManager.getInstance().setLabProperties(labAddress);
        LafManager.getInstance().setLabProperties(labAgents);
        LafManager.getInstance().setLabProperties(labArea);
        LafManager.getInstance().setLabProperties(labDate);
        LafManager.getInstance().setLabProperties(labDocRequired);
        LafManager.getInstance().setLabProperties(labEmail);
        LafManager.getInstance().setLabProperties(labFax);
        LafManager.getInstance().setLabProperties(labFirstPart);
        LafManager.getInstance().setLabProperties(labFixedFee);
        LafManager.getInstance().setLabProperties(labLastName);
        LafManager.getInstance().setLabProperties(labLastPart);
        LafManager.getInstance().setLabProperties(labName);
        LafManager.getInstance().setLabProperties(labPhone);
        LafManager.getInstance().setLabProperties(labPreferredWay);
        LafManager.getInstance().setLabProperties(labStatus);
        LafManager.getInstance().setLabProperties(labTotalFee);
        LafManager.getInstance().setLabProperties(labTotalFee1);
        LafManager.getInstance().setLabProperties(labTotalFee2);
//    labTotalFee3 HAS ITS OWN CUSTOMIZATION
        LafManager.getInstance().setLabProperties(labTotalFee3);
        labTotalFee3.setFont(labTotalFee3.getFont().deriveFont(labTotalFee3.getFont().getStyle() | java.awt.Font.BOLD, labTotalFee3.getFont().getSize() + 1));
        LafManager.getInstance().setLabProperties(labValue);

//    TXT FIELDS
        LafManager.getInstance().setTxtProperties(txtAddress);
        LafManager.getInstance().setTxtProperties(txtArea);
        LafManager.getInstance().setTxtProperties(txtDate);
        LafManager.getInstance().setTxtProperties(txtEmail);
        LafManager.getInstance().setTxtProperties(txtFax);
        LafManager.getInstance().setTxtProperties(txtFirstName);
        LafManager.getInstance().setTxtProperties(txtFirstPart);
        LafManager.getInstance().setTxtProperties(txtLastName);
        LafManager.getInstance().setTxtProperties(txtLastPart);
        LafManager.getInstance().setTxtProperties(txtPhone);
        LafManager.getInstance().setTxtProperties(txtStatus);
        LafManager.getInstance().setTxtProperties(txtValue);

//    FORMATTED TXT
        LafManager.getInstance().setFormattedTxtProperties(formTxtFee);
        LafManager.getInstance().setFormattedTxtProperties(formTxtPaid);
        LafManager.getInstance().setFormattedTxtProperties(formTxtServiceFee);
        LafManager.getInstance().setFormattedTxtProperties(formTxtTaxes);

//    TABBED PANELS
        LafManager.getInstance().setTabProperties(tabbedControlMain);
    }

    /** Applies customization of form, based on Application status. */
    private void customizeApplicationForm() {
        if (appBean != null && !appBean.isNew()) {
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle");
            pnlHeader.setTitleText(bundle.getString("ApplicationForm.titleApplication") + " #" + appBean.getNr());
            applicationDocumentsHelper.updateCheckList(appBean.getServiceList(), appBean.getSourceList());
            appBean.loadApplicationLogList();
            if (appBean.getContactPerson() != null
                    && appBean.getContactPerson().getPreferredCommunicationCode() == null) {
                cbxCommunicationWay.setSelectedIndex(-1);
            }
        } else {
            cbxAgents.requestFocus(true);
            this.tabbedControlMain.removeTabAt(tabbedControlMain.indexOfComponent(historyPanel));
            this.tabbedControlMain.removeTabAt(tabbedControlMain.indexOfComponent(validationPanel));
            this.btnValidate.getAction().setEnabled(false);
        }

        menuApprove.getAction().setEnabled(appBean.canApprove()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_APPROVE));
        menuCancel.getAction().setEnabled(appBean.canCancel()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_REJECT));
        menuArchive.getAction().setEnabled(appBean.canArchive()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_ARCHIVE));
        menuDispatch.getAction().setEnabled(appBean.canDespatch()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_DESPATCH));
        menuRequisition.getAction().setEnabled(appBean.canRequisition()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_REQUISITE));
        menuResubmit.getAction().setEnabled(appBean.canResubmit()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_RESUBMIT));
        menuLapse.getAction().setEnabled(appBean.canLapse()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_WITHDRAW));
        menuWithdraw.getAction().setEnabled(appBean.canWithdraw()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_WITHDRAW));
        btnPrintStatusReport.getAction().setEnabled(appBean.getRowVersion() > 0);

        if (this.btnValidate.getAction().isEnabled()) {
            this.btnValidate.getAction().setEnabled(appBean.canValidate()
                    && SecurityBean.isInRole(RolesConstants.APPLICATION_VALIDATE));
        }

        if (appBean.getStatusCode() != null) {
            boolean editAllowed = appBean.isEditingAllowed()
                    && SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS);
            btnLodge.setEnabled(editAllowed);
            btnAddProperty.setEnabled(editAllowed);
            btnRemoveProperty.setEnabled(editAllowed);
            btnVerifyProperty.setEnabled(editAllowed);
            btnDeleteDoc.setEnabled(editAllowed);
            btnCalculateFee.setEnabled(editAllowed);
            btnPrintFee.setEnabled(editAllowed);
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
                btnLodge.setEnabled(false);
            }
        }
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
        btnRemoveService.getAction().setEnabled(false);
        btnUPService.getAction().setEnabled(false);
        btnDownService.getAction().setEnabled(false);

        if (enableServicesButtons) {
            if (selectedService != null) {
                if (selectedService.isNew()) {
                    btnRemoveService.getAction().setEnabled(true);
                    btnUPService.getAction().setEnabled(true);
                    btnDownService.getAction().setEnabled(true);
                } else {
                    btnRemoveService.getAction().setEnabled(false);
                    btnUPService.getAction().setEnabled(selectedService.isManagementAllowed());
                    btnDownService.getAction().setEnabled(selectedService.isManagementAllowed());
                }

                if (btnUPService.getAction().isEnabled()
                        && appBean.getServiceList().indexOf(selectedService) == 0) {
                    btnUPService.getAction().setEnabled(false);
                }
                if (btnDownService.getAction().isEnabled()
                        && appBean.getServiceList().indexOf(selectedService) == appBean.getServiceList().size() - 1) {
                    btnDownService.getAction().setEnabled(false);
                }
            }
        }

        // Customize service management buttons
        btnCompleteRevertService.getAction().setEnabled(false);
        btnCancelService.getAction().setEnabled(false);
        btnStartService.getAction().setEnabled(false);
        btnViewService.getAction().setEnabled(false);

        if (servicesManagementAllowed) {
            if (selectedService != null) {
                btnViewService.getAction().setEnabled(true);
                btnCancelService.getAction().setEnabled(selectedService.isManagementAllowed()
                        && SecurityBean.isInRole(RolesConstants.APPLICATION_SERVICE_CANCEL));
                btnStartService.getAction().setEnabled(selectedService.isManagementAllowed()
                        && SecurityBean.isInRole(RolesConstants.APPLICATION_SERVICE_START));

                String serviceStatus = selectedService.getStatusCode();
                ActionMap actionMap = Application.getInstance(DesktopApplication.class).getContext().getActionMap(ApplicationPanel.class, this);

                if (serviceStatus != null && serviceStatus.equals(StatusConstants.COMPLETED)) {
                    btnCompleteRevertService.setAction(actionMap.get("revertService"));
                    btnCompleteRevertService.getAction().setEnabled(SecurityBean.isInRole(RolesConstants.APPLICATION_SERVICE_REVERT));
                } else {
                    btnCompleteRevertService.setAction(actionMap.get("completeService"));
                    btnCompleteRevertService.getAction().setEnabled(selectedService.isManagementAllowed()
                            && SecurityBean.isInRole(RolesConstants.APPLICATION_SERVICE_COMPLETE));
                }
                menuCompleteRevertService.setAction(btnCompleteRevertService.getAction());
            }
        }
    }

    /** 
     * Disables or enables buttons, related to the property list management. 
     */
    private void customizePropertyButtons() {
        ApplicationPropertyBean selectedProperty = appBean.getSelectedProperty();
        boolean enablePropertyButtons = appBean.isEditingAllowed();

        if (enablePropertyButtons && selectedProperty != null) {
            btnRemoveProperty.getAction().setEnabled(true);
            btnVerifyProperty.getAction().setEnabled(true);
        } else {
            btnRemoveProperty.getAction().setEnabled(false);
            btnVerifyProperty.getAction().setEnabled(false);
        }
    }

    /** 
     * Disables or enables buttons, related to the documents list management. 
     */
    private void customizeDocumentsButtons() {
        SourceBean selectedDocument = appBean.getSelectedSource();
        boolean enablePropertyButtons = appBean.isEditingAllowed();

        btnDeleteDoc.getAction().setEnabled(false);
        //  btnRemoveAttachedDocument.getAction().setEnabled(false);
        btnOpenAttachment.getAction().setEnabled(false);

        if (enablePropertyButtons && selectedDocument != null) {
            if (selectedDocument != null) {
                btnDeleteDoc.getAction().setEnabled(true);
                if (selectedDocument.getArchiveDocumentId() != null && selectedDocument.getArchiveDocumentId().length() > 0) {
                    btnOpenAttachment.getAction().setEnabled(true);
                    //btnRemoveAttachedDocument.getAction().setEnabled(true);
                }
            }
        }
    }

    /** This method is used by the form designer to create the list of agents.*/
    private PartySummaryListBean createPartySummaryList() {
        PartySummaryListBean agentsList = new PartySummaryListBean();
        agentsList.FillAgents(true);
        return agentsList;
    }

    private void openPropertyForm(BaUnitBean baUnitBean, boolean readOnly) {
        if (baUnitBean != null) {
            ApplicationBean applicationBean = appBean.copy();
            PropertyPanel propertyPnl = new PropertyPanel(applicationBean,
                    applicationBean.getSelectedService(), baUnitBean, readOnly);
            getMainContentPanel().addPanel(propertyPnl, MainContentPanel.CARD_PROPERTY_PANEL, true);
        }
    }

    private void openPropertyForm(ApplicationPropertyBean applicationProperty, boolean readOnly) {
        if (applicationProperty != null) {
            ApplicationBean applicationBean = appBean.copy();
            PropertyPanel propertyPnl = new PropertyPanel(applicationBean,
                    applicationBean.getSelectedService(), applicationProperty.getNameFirstpart(),
                    applicationProperty.getNameLastpart(), readOnly);
            getMainContentPanel().addPanel(propertyPnl, MainContentPanel.CARD_PROPERTY_PANEL, true);
        }
    }

    /** Opens dialog form to display status change result for application or service. */
    private void openValidationResultForm(List<ValidationResultBean> validationResultList,
            boolean isSuccess, String message) {
        ValidationResultForm resultForm = new ValidationResultForm(
                null, true, validationResultList, isSuccess, message);
        resultForm.setLocationRelativeTo(this);
        resultForm.setVisible(true);
    }

    /** Validates application */
    private boolean validateApplication() {
        if (appBean.getId() != null) {
            validationResultListBean.setValidationResultList(appBean.validate());
            for (ValidationResultBean validationBean : validationResultListBean.getValidationResutlList()) {
                if (!validationBean.isSuccessful() && validationBean.getSeverity().equals("critical")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void launchService(final boolean readOnly) {
        if (appBean.getSelectedService() != null) {
            String requestType = appBean.getSelectedService().getRequestTypeCode();

            // Determine what form to start for selected service
            if (requestType.equalsIgnoreCase(RequestTypeBean.CODE_REG_POWER_OF_ATTORNEY)) {
                // Run registration/cancelation Power of attorney
                TransactionedDocumentsForm form = new TransactionedDocumentsForm(
                        null, true, appBean, appBean.getSelectedService());
                form.setLocationRelativeTo(this);
                form.setVisible(true);

            } else if (requestType.equalsIgnoreCase(RequestTypeBean.CODE_CADASTRE_CHANGE)
                    || requestType.equalsIgnoreCase(RequestTypeBean.CODE_CADASTRE_REDEFINITION)) {

                if (appBean.getPropertyList().getFilteredList().size() == 1) {
                    CadastreTransactionMapPanel form = new CadastreTransactionMapPanel(
                            appBean,
                            appBean.getSelectedService(),
                            appBean.getPropertyList().getFilteredList().get(0));
                    getMainContentPanel().addPanel(form, MainContentPanel.CARD_CADASTRECHANGE, true);

                } else if (appBean.getPropertyList().getFilteredList().size() > 1) {
                    PropertiesList propertyListForm = new PropertiesList(appBean.getPropertyList());
                    propertyListForm.setLocationRelativeTo(this);

                    propertyListForm.addPropertyChangeListener(new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName().equals(PropertiesList.SELECTED_PROPERTY)
                                    && evt.getNewValue() != null) {
                                ApplicationPropertyBean property =
                                        (ApplicationPropertyBean) evt.getNewValue();
                                ((JDialog) evt.getSource()).dispose();
                                CadastreTransactionMapPanel form = new CadastreTransactionMapPanel(
                                        appBean,
                                        appBean.getSelectedService(), property);
                                getMainContentPanel().addPanel(
                                        form, MainContentPanel.CARD_CADASTRECHANGE, true);
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

            } else if (requestType.equalsIgnoreCase(RequestTypeBean.CODE_NEW_APARTMENT)
                    || requestType.equalsIgnoreCase(RequestTypeBean.CODE_NEW_FREEHOLD)
                    || requestType.equalsIgnoreCase(RequestTypeBean.CODE_NEW_OWNERSHIP)
                    || requestType.equalsIgnoreCase(RequestTypeBean.CODE_NEW_STATE)) {

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
                    if (!readOnly) {
                        // Open empty property form
                        openPropertyForm(new BaUnitBean(), readOnly);
                    }
                }

            } else {
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

        } else {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_SELECT_SERVICE);
        }
    }

    /**Designer generated code*/
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        appBean = getApplicationBean();
        requestTypeList = new org.sola.clients.beans.referencedata.RequestTypeListBean();
        partySummaryList = createPartySummaryList();
        communicationTypeList = new org.sola.clients.beans.referencedata.CommunicationTypeListBean()
        ;
        sourceTypeList = new org.sola.clients.beans.source.SourceTypeListBean();
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
        menuCompleteRevertService = new javax.swing.JMenuItem();
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
        btnLodge = new javax.swing.JButton();
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
        jPanel24 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtAppNumber = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        labDate = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        labStatus = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        servicesPanel = new javax.swing.JPanel();
        scrollFeeDetails1 = new javax.swing.JScrollPane();
        tabFeeDetails1 = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        tbServices = new javax.swing.JToolBar();
        btnAddService = new javax.swing.JButton();
        btnRemoveService = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnUPService = new javax.swing.JButton();
        btnDownService = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnViewService = new javax.swing.JButton();
        btnStartService = new javax.swing.JButton();
        btnCancelService = new javax.swing.JButton();
        btnCompleteRevertService = new javax.swing.JButton();
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

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(ApplicationPanel.class, this);
        menuAddService.setAction(actionMap.get("addService")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        menuAddService.setText(bundle.getString("ApplicationPanel.menuAddService.text")); // NOI18N
        menuAddService.setName("menuAddService"); // NOI18N
        popUpServices.add(menuAddService);

        menuRemoveService.setAction(actionMap.get("removeService")); // NOI18N
        menuRemoveService.setText(bundle.getString("ApplicationPanel.menuRemoveService.text")); // NOI18N
        menuRemoveService.setName("menuRemoveService"); // NOI18N
        popUpServices.add(menuRemoveService);

        jSeparator3.setName("jSeparator3"); // NOI18N
        popUpServices.add(jSeparator3);

        menuMoveServiceUp.setAction(actionMap.get("moveServiceUp")); // NOI18N
        menuMoveServiceUp.setText(bundle.getString("ApplicationPanel.menuMoveServiceUp.text")); // NOI18N
        menuMoveServiceUp.setName("menuMoveServiceUp"); // NOI18N
        popUpServices.add(menuMoveServiceUp);

        menuMoveServiceDown.setAction(actionMap.get("moveServiceDown")); // NOI18N
        menuMoveServiceDown.setText(bundle.getString("ApplicationPanel.menuMoveServiceDown.text")); // NOI18N
        menuMoveServiceDown.setName("menuMoveServiceDown"); // NOI18N
        popUpServices.add(menuMoveServiceDown);

        jSeparator4.setName("jSeparator4"); // NOI18N
        popUpServices.add(jSeparator4);

        menuViewService.setAction(actionMap.get("viewService")); // NOI18N
        menuViewService.setText(bundle.getString("ApplicationPanel.menuViewService.text")); // NOI18N
        menuViewService.setName("menuViewService"); // NOI18N
        popUpServices.add(menuViewService);

        menuStartService.setAction(actionMap.get("startService")); // NOI18N
        menuStartService.setText(bundle.getString("ApplicationPanel.menuStartService.text")); // NOI18N
        menuStartService.setName("menuStartService"); // NOI18N
        popUpServices.add(menuStartService);

        menuCompleteRevertService.setAction(actionMap.get("completeService")); // NOI18N
        menuCompleteRevertService.setText(bundle.getString("ApplicationPanel.menuCompleteRevertService.text")); // NOI18N
        menuCompleteRevertService.setName("menuCompleteRevertService"); // NOI18N
        popUpServices.add(menuCompleteRevertService);

        menuCancelService.setAction(actionMap.get("cancelService")); // NOI18N
        menuCancelService.setText(bundle.getString("ApplicationPanel.menuCancelService.text")); // NOI18N
        menuCancelService.setName("menuCancelService"); // NOI18N
        popUpServices.add(menuCancelService);

        popupApplicationActions.setName("popupApplicationActions"); // NOI18N

        menuApprove.setAction(actionMap.get("approveApplication")); // NOI18N
        menuApprove.setFont(new java.awt.Font("Tahoma", 0, 12));
        menuApprove.setText(bundle.getString("ApplicationPanel.menuApprove.text")); // NOI18N
        menuApprove.setName("menuApprove"); // NOI18N
        popupApplicationActions.add(menuApprove);

        menuCancel.setAction(actionMap.get("rejectApplication")); // NOI18N
        menuCancel.setFont(new java.awt.Font("Tahoma", 0, 12));
        menuCancel.setText(bundle.getString("ApplicationPanel.menuCancel.text")); // NOI18N
        menuCancel.setName("menuCancel"); // NOI18N
        popupApplicationActions.add(menuCancel);

        menuWithdraw.setAction(actionMap.get("withdrawApplication")); // NOI18N
        menuWithdraw.setFont(new java.awt.Font("Tahoma", 0, 12));
        menuWithdraw.setText(bundle.getString("ApplicationPanel.menuWithdraw.text")); // NOI18N
        menuWithdraw.setName("menuWithdraw"); // NOI18N
        popupApplicationActions.add(menuWithdraw);

        menuLapse.setAction(actionMap.get("lapseApplication")); // NOI18N
        menuLapse.setFont(new java.awt.Font("Tahoma", 0, 12));
        menuLapse.setText(bundle.getString("ApplicationPanel.menuLapse.text")); // NOI18N
        menuLapse.setName("menuLapse"); // NOI18N
        popupApplicationActions.add(menuLapse);

        menuRequisition.setAction(actionMap.get("requisitionApplication")); // NOI18N
        menuRequisition.setFont(new java.awt.Font("Tahoma", 0, 12));
        menuRequisition.setText(bundle.getString("ApplicationPanel.menuRequisition.text")); // NOI18N
        menuRequisition.setName("menuRequisition"); // NOI18N
        popupApplicationActions.add(menuRequisition);

        menuResubmit.setAction(actionMap.get("resubmitApplication")); // NOI18N
        menuResubmit.setFont(new java.awt.Font("Tahoma", 0, 12));
        menuResubmit.setText(bundle.getString("ApplicationPanel.menuResubmit.text")); // NOI18N
        menuResubmit.setName("menuResubmit"); // NOI18N
        popupApplicationActions.add(menuResubmit);

        menuDispatch.setAction(actionMap.get("despatchApplication")); // NOI18N
        menuDispatch.setFont(new java.awt.Font("Tahoma", 0, 12));
        menuDispatch.setText(bundle.getString("ApplicationPanel.menuDispatch.text")); // NOI18N
        menuDispatch.setName("menuDispatch"); // NOI18N
        popupApplicationActions.add(menuDispatch);

        menuArchive.setAction(actionMap.get("archiveApplication")); // NOI18N
        menuArchive.setFont(new java.awt.Font("Tahoma", 0, 12));
        menuArchive.setText(bundle.getString("ApplicationPanel.menuArchive.text")); // NOI18N
        menuArchive.setName("menuArchive"); // NOI18N
        popupApplicationActions.add(menuArchive);

        setHeaderPanel(pnlHeader);
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

        btnLodge.setFont(UIManager.getFont(btnFont));
        LafManager.getInstance().setBtnProperties(btnLodge);
        btnLodge.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnLodge.setText(bundle.getString("ApplicationPanel.btnLodge.text")); // NOI18N
        btnLodge.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLodge.setName("btnLodge"); // NOI18N
        btnLodge.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        btnLodge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLodgeActionPerformed(evt);
            }
        });
        jToolBar3.add(btnLodge);

        btnCalculateFee.setAction(actionMap.get("calculateFee")); // NOI18N
        btnCalculateFee.setFont(UIManager.getFont(btnCalculateFee));
        LafManager.getInstance().setBtnProperties(btnCalculateFee);
        btnCalculateFee.setText(bundle.getString("ApplicationPanel.btnCalculateFee.text")); // NOI18N
        btnCalculateFee.setToolTipText(bundle.getString("ApplicationForm.btnCalculateFee.tooltiptext")); // NOI18N
        btnCalculateFee.setName("btnCalculateFee"); // NOI18N
        jToolBar3.add(btnCalculateFee);

        btnValidate.setAction(actionMap.get("validactionApplication")); // NOI18N
        btnValidate.setFont(UIManager.getFont(btnFont));
        btnValidate.setText(bundle.getString("ApplicationPanel.btnValidate.text")); // NOI18N
        btnValidate.setToolTipText(bundle.getString("ApplicationForm.btnValidate.tooltiptext")); // NOI18N
        btnValidate.setName("btnValidate"); // NOI18N
        btnValidate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        jToolBar3.add(btnValidate);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar3.add(jSeparator6);

        btnPrintFee.setAction(actionMap.get("printReceipt")); // NOI18N
        btnPrintFee.setFont(UIManager.getFont(btnFont));
        btnPrintFee.setText(bundle.getString("ApplicationPanel.btnPrintFee.text")); // NOI18N
        btnPrintFee.setToolTipText(bundle.getString("ApplicationForm.btnPrintFee.tooltiptext")); // NOI18N
        btnPrintFee.setName("btnPrintFee"); // NOI18N
        jToolBar3.add(btnPrintFee);

        btnPrintStatusReport.setAction(actionMap.get("printStatusReport")); // NOI18N
        btnPrintStatusReport.setText(bundle.getString("ApplicationPanel.btnPrintStatusReport.text")); // NOI18N
        btnPrintStatusReport.setFocusable(false);
        btnPrintStatusReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPrintStatusReport.setName("btnPrintStatusReport"); // NOI18N
        btnPrintStatusReport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
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

        tabbedControlMain.setFont(UIManager.getFont(tabFont));
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

        labName.setFont(UIManager.getFont(labFont));
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
                .addContainerGap(198, Short.MAX_VALUE))
            .addComponent(txtFirstName, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
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

        labLastName.setFont(UIManager.getFont(labFont));
        LafManager.getInstance().setLabProperties(labLastName);
        labLastName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labLastName.setText(bundle.getString("ApplicationPanel.labLastName.text")); // NOI18N
        labLastName.setIconTextGap(1);
        labLastName.setName("labLastName"); // NOI18N

        txtLastName.setFont(UIManager.getFont(txtFont));
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
                .addContainerGap(245, Short.MAX_VALUE))
            .addComponent(txtLastName, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
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

        txtAddress.setFont(UIManager.getFont(txtFont));
        txtAddress.setName("txtAddress"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.address.description}"), txtAddress, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtAddress.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtAddress.setHorizontalAlignment(JTextField.LEADING);

        labAddress.setFont(new java.awt.Font("Tahoma", 0, 12));
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
                .addContainerGap(576, Short.MAX_VALUE))
            .addComponent(txtAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
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

        labPhone.setFont(new java.awt.Font("Tahoma", 0, 12));
        LafManager.getInstance().setLabProperties(labPhone);
        labPhone.setText(bundle.getString("ApplicationPanel.labPhone.text")); // NOI18N
        labPhone.setName("labPhone"); // NOI18N

        txtPhone.setFont(UIManager.getFont(txtFont));
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
                .addContainerGap(270, Short.MAX_VALUE))
            .addComponent(txtPhone, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(labPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel7);

        jPanel8.setName("jPanel8"); // NOI18N

        labFax.setFont(new java.awt.Font("Tahoma", 0, 12));
        LafManager.getInstance().setLabProperties(labFax);
        labFax.setText(bundle.getString("ApplicationPanel.labFax.text")); // NOI18N
        labFax.setName("labFax"); // NOI18N

        txtFax.setFont(UIManager.getFont(txtFont));
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
            .addComponent(txtFax, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
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
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel8);

        jPanel9.setName("jPanel9"); // NOI18N

        labEmail.setFont(new java.awt.Font("Tahoma", 0, 12));
        LafManager.getInstance().setLabProperties(labEmail);
        labEmail.setText(bundle.getString("ApplicationPanel.labEmail.text")); // NOI18N
        labEmail.setName("labEmail"); // NOI18N

        txtEmail.setFont(UIManager.getFont(txtFont));
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
                .addContainerGap(169, Short.MAX_VALUE))
            .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(labEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel9);

        jPanel10.setName("jPanel10"); // NOI18N

        labPreferredWay.setFont(new java.awt.Font("Tahoma", 0, 12));
        LafManager.getInstance().setLabProperties(labPreferredWay);
        labPreferredWay.setText(bundle.getString("ApplicationPanel.labPreferredWay.text")); // NOI18N
        labPreferredWay.setName("labPreferredWay"); // NOI18N

        cbxCommunicationWay.setFont(UIManager.getFont(txtFont));
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
                .addContainerGap(127, Short.MAX_VALUE))
            .addComponent(cbxCommunicationWay, 0, 309, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(labPreferredWay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxCommunicationWay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel10);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("ApplicationPanel.groupPanel1.titleText")); // NOI18N

        jPanel23.setName("jPanel23"); // NOI18N
        jPanel23.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        jPanel14.setName("jPanel14"); // NOI18N

        labAgents.setFont(new java.awt.Font("Tahoma", 0, 12));
        LafManager.getInstance().setLabProperties(labAgents);
        labAgents.setText(bundle.getString("ApplicationPanel.labAgents.text")); // NOI18N
        labAgents.setIconTextGap(1);
        labAgents.setName("labAgents"); // NOI18N

        cbxAgents.setFont(UIManager.getFont(cmbFont));
        LafManager.getInstance().setCmbProperties(cbxAgents);
        cbxAgents.setName("cbxAgents"); // NOI18N
        cbxAgents.setRenderer(new SimpleComboBoxRenderer("getName"));
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
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(labAgents, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
            .addComponent(cbxAgents, 0, 147, Short.MAX_VALUE)
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

        jPanel24.setName("jPanel24"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText(bundle.getString("ApplicationPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtAppNumber.setEditable(false);
        txtAppNumber.setName("txtAppNumber"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${nr}"), txtAppNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(71, Short.MAX_VALUE))
            .addComponent(txtAppNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAppNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.add(jPanel24);

        jPanel13.setName("jPanel13"); // NOI18N

        labDate.setFont(new java.awt.Font("Tahoma", 0, 12));
        LafManager.getInstance().setLabProperties(labDate);
        labDate.setText(bundle.getString("ApplicationPanel.labDate.text")); // NOI18N
        labDate.setName("labDate"); // NOI18N

        txtDate.setEditable(false);
        txtDate.setFont(UIManager.getFont(txtFont));
        txtDate.setName("txtDate"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${lodgingDatetime}"), txtDate, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new DateConverter());
        bindingGroup.addBinding(binding);

        txtDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtDate.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(labDate, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(91, Short.MAX_VALUE))
            .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(labDate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.add(jPanel13);

        jPanel15.setName("jPanel15"); // NOI18N

        labStatus.setFont(new java.awt.Font("Tahoma", 0, 12));
        LafManager.getInstance().setLabProperties(labStatus);
        labStatus.setText(bundle.getString("ApplicationPanel.labStatus.text")); // NOI18N
        labStatus.setName("labStatus"); // NOI18N

        txtStatus.setEditable(false);
        txtStatus.setFont(UIManager.getFont(txtFont));
        txtStatus.setName("txtStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtStatus.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtStatus.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(labStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
            .addComponent(txtStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(labStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel23.add(jPanel15);

        javax.swing.GroupLayout contactPanelLayout = new javax.swing.GroupLayout(contactPanel);
        contactPanel.setLayout(contactPanelLayout);
        contactPanelLayout.setHorizontalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
                .addContainerGap())
        );
        contactPanelLayout.setVerticalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contactPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.contactPanel.TabConstraints.tabTitle"), contactPanel); // NOI18N

        servicesPanel.setName("servicesPanel"); // NOI18N

        scrollFeeDetails1.setBackground(new java.awt.Color(255, 255, 255));
        scrollFeeDetails1.setName("scrollFeeDetails1"); // NOI18N
        scrollFeeDetails1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        tabFeeDetails1.setComponentPopupMenu(popUpServices);
        tabFeeDetails1.setName("tabFeeDetails1"); // NOI18N
        tabFeeDetails1.setNextFocusableComponent(btnLodge);
        tabFeeDetails1.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        tabFeeDetails1.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${serviceList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, eLProperty, tabFeeDetails1);
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
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${selectedService}"), tabFeeDetails1, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        scrollFeeDetails1.setViewportView(tabFeeDetails1);
        tabFeeDetails1.getColumnModel().getColumn(0).setMinWidth(70);
        tabFeeDetails1.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabFeeDetails1.getColumnModel().getColumn(0).setMaxWidth(70);
        tabFeeDetails1.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationDetailsForm.tabFeeDetails1.columnModel.title0")); // NOI18N
        tabFeeDetails1.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails1.columnModel.title1")); // NOI18N
        tabFeeDetails1.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationDetailsPanel.tabFeeDetails1.columnModel.title2")); // NOI18N

        tbServices.setFloatable(false);
        tbServices.setRollover(true);
        tbServices.setName("tbServices"); // NOI18N

        btnAddService.setAction(actionMap.get("addService")); // NOI18N
        btnAddService.setFont(UIManager.getFont(btnFont));
        btnAddService.setText(bundle.getString("ApplicationPanel.btnAddService.text")); // NOI18N
        btnAddService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddService.setName("btnAddService"); // NOI18N
        btnAddService.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        tbServices.add(btnAddService);

        btnRemoveService.setAction(actionMap.get("removeService")); // NOI18N
        btnRemoveService.setFont(UIManager.getFont(btnFont));
        btnRemoveService.setText(bundle.getString("ApplicationPanel.btnRemoveService.text")); // NOI18N
        btnRemoveService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveService.setName("btnRemoveService"); // NOI18N
        tbServices.add(btnRemoveService);

        jSeparator1.setName("jSeparator1"); // NOI18N
        tbServices.add(jSeparator1);

        btnUPService.setAction(actionMap.get("moveServiceUp")); // NOI18N
        btnUPService.setFont(UIManager.getFont(btnFont));
        btnUPService.setText(bundle.getString("ApplicationPanel.btnUPService.text")); // NOI18N
        btnUPService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUPService.setName("btnUPService"); // NOI18N
        tbServices.add(btnUPService);

        btnDownService.setAction(actionMap.get("moveServiceDown")); // NOI18N
        btnDownService.setFont(UIManager.getFont(btnFont));
        btnDownService.setText(bundle.getString("ApplicationPanel.btnDownService.text")); // NOI18N
        btnDownService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDownService.setName("btnDownService"); // NOI18N
        tbServices.add(btnDownService);

        jSeparator2.setName("jSeparator2"); // NOI18N
        tbServices.add(jSeparator2);

        btnViewService.setAction(actionMap.get("viewService")); // NOI18N
        btnViewService.setText(bundle.getString("ApplicationPanel.btnViewService.text")); // NOI18N
        btnViewService.setFocusable(false);
        btnViewService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewService.setName("btnViewService"); // NOI18N
        btnViewService.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbServices.add(btnViewService);

        btnStartService.setAction(actionMap.get("startService")); // NOI18N
        btnStartService.setFont(UIManager.getFont(btnFont));
        btnStartService.setText(bundle.getString("ApplicationPanel.btnStartService.text")); // NOI18N
        btnStartService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStartService.setName("btnStartService"); // NOI18N
        btnStartService.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        tbServices.add(btnStartService);

        btnCancelService.setAction(actionMap.get("cancelService")); // NOI18N
        btnCancelService.setFont(UIManager.getFont(btnFont));
        btnCancelService.setText(bundle.getString("ApplicationPanel.btnCancelService.text")); // NOI18N
        btnCancelService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCancelService.setName("btnCancelService"); // NOI18N
        tbServices.add(btnCancelService);

        btnCompleteRevertService.setAction(actionMap.get("completeService")); // NOI18N
        btnCompleteRevertService.setFont(UIManager.getFont(btnFont));
        btnCompleteRevertService.setText(bundle.getString("ApplicationPanel.btnCompleteRevertService.text")); // NOI18N
        btnCompleteRevertService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCompleteRevertService.setName("btnCompleteRevertService"); // NOI18N
        tbServices.add(btnCompleteRevertService);

        javax.swing.GroupLayout servicesPanelLayout = new javax.swing.GroupLayout(servicesPanel);
        servicesPanel.setLayout(servicesPanelLayout);
        servicesPanelLayout.setHorizontalGroup(
            servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, servicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
                    .addComponent(tbServices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
                .addContainerGap())
        );
        servicesPanelLayout.setVerticalGroup(
            servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(servicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbServices, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
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

        btnRemoveProperty.setAction(actionMap.get("removeSelectedProperty")); // NOI18N
        btnRemoveProperty.setText(bundle.getString("ApplicationPanel.btnRemoveProperty.text")); // NOI18N
        btnRemoveProperty.setToolTipText(bundle.getString("ApplicationPanel.btnRemoveProperty.toolTipText")); // NOI18N
        btnRemoveProperty.setName("btnRemoveProperty"); // NOI18N
        tbPropertyDetails.add(btnRemoveProperty);

        btnVerifyProperty.setAction(actionMap.get("verifySelectedProperty")); // NOI18N
        btnVerifyProperty.setText(bundle.getString("ApplicationPanel.btnVerifyProperty.text")); // NOI18N
        btnVerifyProperty.setToolTipText(bundle.getString("ApplicationPanel.btnVerifyProperty.toolTipText")); // NOI18N
        btnVerifyProperty.setName("btnVerifyProperty"); // NOI18N
        tbPropertyDetails.add(btnVerifyProperty);

        scrollPropertyDetails.setFont(new java.awt.Font("Tahoma", 0, 12));
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
        tabPropertyDetails.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("NewApplication.tabPropertyDetails.columnModel.title6")); // NOI18N

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel20.setName("jPanel20"); // NOI18N

        propertypartPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        propertypartPanel.setName("propertypartPanel"); // NOI18N
        propertypartPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        propertypartPanel.setLayout(new java.awt.GridLayout(1, 4, 15, 0));

        jPanel16.setName("jPanel16"); // NOI18N

        labFirstPart.setFont(UIManager.getFont(labFont));
        LafManager.getInstance().setLabProperties(labFirstPart);
        labFirstPart.setText(bundle.getString("ApplicationPanel.labFirstPart.text")); // NOI18N
        labFirstPart.setName("labFirstPart"); // NOI18N

        txtFirstPart.setFont(UIManager.getFont(txtFont));
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
                .addContainerGap(65, Short.MAX_VALUE))
            .addComponent(txtFirstPart, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
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

        txtLastPart.setFont(UIManager.getFont(txtFont));
        txtLastPart.setText(bundle.getString("ApplicationPanel.txtLastPart.text")); // NOI18N
        txtLastPart.setName("txtLastPart"); // NOI18N
        txtLastPart.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastPart.setHorizontalAlignment(JTextField.LEADING);

        labLastPart.setFont(UIManager.getFont(labFont));
        LafManager.getInstance().setLabProperties(labLastPart);
        labLastPart.setText(bundle.getString("ApplicationPanel.labLastPart.text")); // NOI18N
        labLastPart.setName("labLastPart"); // NOI18N

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(labLastPart)
                .addContainerGap(66, Short.MAX_VALUE))
            .addComponent(txtLastPart, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
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

        labArea.setFont(UIManager.getFont(labFont));
        LafManager.getInstance().setLabProperties(labArea);
        labArea.setText(bundle.getString("ApplicationPanel.labArea.text")); // NOI18N
        labArea.setName("labArea"); // NOI18N

        txtArea.setFont(UIManager.getFont(txtFont));
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
                .addContainerGap(61, Short.MAX_VALUE))
            .addComponent(txtArea, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
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

        labValue.setFont(UIManager.getFont(labFont));
        LafManager.getInstance().setLabProperties(labValue);
        labValue.setText(bundle.getString("ApplicationPanel.labValue.text")); // NOI18N
        labValue.setName("labValue"); // NOI18N

        txtValue.setFont(UIManager.getFont(txtFont));
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
                .addContainerGap(83, Short.MAX_VALUE))
            .addComponent(txtValue, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
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

        btnAddProperty.setBackground(UIManager.getColor(btnBackground));
        btnAddProperty.setFont(UIManager.getFont(btnFont));
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
                .addComponent(propertypartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
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
                    .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tbPropertyDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
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
                .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
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

        scrollDocuments.setFont(new java.awt.Font("Tahoma", 0, 12));
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
        labDocRequired.setFont(UIManager.getFont(labFont));
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
                .addComponent(addDocumentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addDocumentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
        );

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setToolTipText(bundle.getString("ApplicationPanel.jToolBar1.toolTipText")); // NOI18N
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnDeleteDoc.setAction(actionMap.get("removeSelectedSource")); // NOI18N
        btnDeleteDoc.setText(bundle.getString("ApplicationPanel.btnDeleteDoc.text")); // NOI18N
        btnDeleteDoc.setName("btnDeleteDoc"); // NOI18N
        jToolBar1.add(btnDeleteDoc);

        btnOpenAttachment.setAction(actionMap.get("openSourceAttachment")); // NOI18N
        btnOpenAttachment.setText(bundle.getString("ApplicationPanel.btnOpenAttachment.text")); // NOI18N
        btnOpenAttachment.setToolTipText(bundle.getString("ApplicationPanel.btnOpenAttachment.toolTipText")); // NOI18N
        btnOpenAttachment.setActionCommand(bundle.getString("ApplicationPanel.btnOpenAttachment.actionCommand")); // NOI18N
        btnOpenAttachment.setName("btnOpenAttachment"); // NOI18N
        jToolBar1.add(btnOpenAttachment);

        javax.swing.GroupLayout documentPanelLayout = new javax.swing.GroupLayout(documentPanel);
        documentPanel.setLayout(documentPanelLayout);
        documentPanelLayout.setHorizontalGroup(
            documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(documentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
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
                        .addComponent(scrollDocRequired, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                    .addGroup(documentPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.documentPanel.TabConstraints.tabTitle"), documentPanel); // NOI18N

        mapPanel.setName("mapPanel"); // NOI18N

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 654, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 415, Short.MAX_VALUE)
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.mapPanel.TabConstraints.tabTitle"), mapPanel); // NOI18N

        feesPanel.setName("feesPanel"); // NOI18N
        feesPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        feesPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                feesPanelMouseClicked(evt);
            }
        });

        scrollFeeDetails.setFont(new java.awt.Font("Tahoma", 0, 12));
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

        formTxtServiceFee.setFont(UIManager.getFont(txtFont));
        formTxtServiceFee.setEditable(false);
        formTxtServiceFee.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtServiceFee.setName("formTxtServiceFee"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${servicesFee}"), formTxtServiceFee, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtServiceFee.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtServiceFee.setHorizontalAlignment(JFormattedTextField.LEADING);

        formTxtTaxes.setFont(UIManager.getFont(txtFont));
        formTxtTaxes.setEditable(false);
        formTxtTaxes.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtTaxes.setName("formTxtTaxes"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${tax}"), formTxtTaxes, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtTaxes.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtTaxes.setHorizontalAlignment(JFormattedTextField.LEADING);

        formTxtFee.setFont(UIManager.getFont(txtFont));
        formTxtFee.setEditable(false);
        formTxtFee.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        formTxtFee.setName("formTxtFee"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${totalFee}"), formTxtFee, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        formTxtFee.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        formTxtFee.setHorizontalAlignment(JFormattedTextField.LEADING);

        formTxtPaid.setFont(UIManager.getFont(txtFont));
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

        labTotalFee2.setFont(UIManager.getFont(btnFont));
        LafManager.getInstance().setLabProperties(labTotalFee2);
        labTotalFee2.setText(bundle.getString("ApplicationPanel.labTotalFee2.text")); // NOI18N
        labTotalFee2.setName("labTotalFee2"); // NOI18N

        labTotalFee.setFont(UIManager.getFont(labFont));
        LafManager.getInstance().setLabProperties(labTotalFee);
        labTotalFee.setText(bundle.getString("ApplicationPanel.labTotalFee.text")); // NOI18N
        labTotalFee.setName("labTotalFee"); // NOI18N

        labTotalFee1.setFont(UIManager.getFont(btnFont));
        LafManager.getInstance().setLabProperties(labTotalFee1);
        labTotalFee1.setText(bundle.getString("ApplicationPanel.labTotalFee1.text")); // NOI18N
        labTotalFee1.setName("labTotalFee1"); // NOI18N

        labFixedFee.setBackground(new java.awt.Color(255, 255, 255));
        labFixedFee.setFont(UIManager.getFont(btnFont));
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
                        .addComponent(labTotalFee1, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                        .addGap(33, 33, 33))
                    .addComponent(formTxtServiceFee, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(formTxtTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(labFixedFee, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(formTxtFee, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labTotalFee, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(formTxtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labTotalFee2))
                .addGap(18, 18, 18)
                .addComponent(labTotalFee3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxPaid)
                .addGap(54, 54, 54))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {formTxtFee, formTxtPaid, formTxtServiceFee, formTxtTaxes});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labTotalFee3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxPaid)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(labTotalFee2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(formTxtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(formTxtTaxes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(labTotalFee, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labFixedFee))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(formTxtFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(labTotalFee1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(formTxtServiceFee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout feesPanelLayout = new javax.swing.GroupLayout(feesPanel);
        feesPanel.setLayout(feesPanelLayout);
        feesPanelLayout.setHorizontalGroup(
            feesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(feesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(feesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollFeeDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        feesPanelLayout.setVerticalGroup(
            feesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, feesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollFeeDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.feesPanel.TabConstraints.tabTitle"), feesPanel); // NOI18N

        validationPanel.setFont(new java.awt.Font("Tahoma", 0, 12));
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
        tabValidations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationForm.tabValidations.columnModel.title1_1")); // NOI18N
        tabValidations.getColumnModel().getColumn(0).setCellRenderer(new TableCellTextAreaRenderer());
        tabValidations.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabValidations.getColumnModel().getColumn(1).setMaxWidth(100);
        tabValidations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationForm.tabValidations.columnModel.title2_1")); // NOI18N
        tabValidations.getColumnModel().getColumn(2).setPreferredWidth(45);
        tabValidations.getColumnModel().getColumn(2).setMaxWidth(45);
        tabValidations.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationForm.tabValidations.columnModel.title3_1")); // NOI18N
        tabValidations.getColumnModel().getColumn(2).setCellRenderer(new ViolationCellRenderer());
        tabValidations.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout validationPanelLayout = new javax.swing.GroupLayout(validationPanel);
        validationPanel.setLayout(validationPanelLayout);
        validationPanelLayout.setHorizontalGroup(
            validationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validationsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
                .addContainerGap())
        );
        validationPanelLayout.setVerticalGroup(
            validationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validationsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.validationPanel.TabConstraints.tabTitle"), validationPanel); // NOI18N

        historyPanel.setFont(new java.awt.Font("Tahoma", 0, 12));
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
                .addComponent(actionLogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
                .addContainerGap())
        );
        historyPanelLayout.setVerticalGroup(
            historyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(actionLogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationPanel.historyPanel.TabConstraints.tabTitle"), historyPanel); // NOI18N

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedControlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedControlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel22);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    /** Validates user's data input and calls save operation on the {@link ApplicationBean}.*/
    private void btnLodgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLodgeActionPerformed

        String mandatoryFields = "";
        if ("".equals(txtFirstName.getText()) || (txtFirstName.getText().equals(""))) {
            mandatoryFields = mandatoryFields + "\n" + labName.getText().replace(":", "");
        }

        if ("".equals(txtLastName.getText()) || (txtLastName.getText().equals(""))) {
            mandatoryFields = mandatoryFields + "\n" + labLastName.getText().replace(":", "");
        }

        if ("".equals(txtAddress.getText()) || (txtAddress.getText().equals(""))) {
            mandatoryFields = mandatoryFields + "\n" + labAddress.getText().replace(":", "");
        }

        if (!"".equals(mandatoryFields)) {
            String[] mandatoryparams = {"" + mandatoryFields};
            MessageUtility.displayMessage(ClientMessage.CHECK_NOTNULL_FIELDS, mandatoryparams);
            return;
        }

        // Show confirm message
        if (MessageUtility.displayMessage(ClientMessage.APPLICATION_SAVE_CONFIRM) == MessageUtility.BUTTON_ONE) {
            try {
                if (applicationDocumentsHelper.isAllItemsChecked() == false) {
                    if (MessageUtility.displayMessage(ClientMessage.APPLICATION_NOTALL_DOCUMENT_REQUIRED) == MessageUtility.BUTTON_TWO) {
                        return;
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
                        return;
                    }
                }

                appBean.setLocation(this.mapControl.getApplicationLocation());

                if (this.applicationID != null && !this.applicationID.equals("")) {
                    appBean.saveApplication();
                } else {
                    appBean.lodgeApplication();
                }

                MessageUtility.displayMessage(ClientMessage.APPLICATION_SUCCESSFULLY_SAVED);

                if (applicationID == null || applicationID.equals("")) {
                    showReport(ReportManager.getLodgementNoticeReport(appBean));
                }
            } catch (Throwable ex) {
                DesktopClientExceptionHandler.handleException(ex);
            }
        }
}//GEN-LAST:event_btnLodgeActionPerformed

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

    /** Opens {@link FileBrowserForm} to select digital copy of the document and get it attached.*/
    private void tabDocumentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabDocumentsMouseClicked
        if (evt.getClickCount() == 2) {
            openAttachment();
        }
    }//GEN-LAST:event_tabDocumentsMouseClicked

    /** Removes attached digital copy from selected document.*/
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

    /** Opens attached digital copy of the selected document */
    private void openAttachment() {
        if (appBean.getSelectedSource() != null
                && appBean.getSelectedSource().getArchiveDocument() != null) {
            // Try to open attached file
            DocumentBean.openDocument(appBean.getSelectedSource().getArchiveDocument().getId());
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

    /** Clears fields on the <b>Properties</b> tab, after the new property is added into the list.*/
    private void clearPropertyFields() {
        txtFirstPart.setText(null);
        txtLastPart.setText(null);
        txtArea.setText(null);
        txtValue.setText(null);
    }

    /** Opens {@link ReportViewerForm} to display report.*/
    private void showReport(JasperPrint report) {
        ReportViewerForm form = new ReportViewerForm(report);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }

    private void takeActionAgainstApplication(String actionType) {
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
        if (MessageUtility.displayMessage(msgCode,
                new String[]{localizedActionName}) == MessageUtility.BUTTON_ONE) {

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
            } else if (ApplicationActionTypeBean.DESPATCH.equals(actionType)) {
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
                String message = MessageUtility.getLocalizedMessage(
                        ClientMessage.APPLICATION_ACTION_SUCCESS,
                        new String[]{appBean.getNr()}).getMessage();
                openValidationResultForm(result, true, message);
            }
        }
    }

    @Action
    public void addService() {
        ServiceListForm serviceListForm = new ServiceListForm(appBean);
        serviceListForm.setLocationRelativeTo(this);
        serviceListForm.setVisible(true);
        btnCalculateFee.setEnabled(true);
    }

    /** Removes selected service from the services list.*/
    @Action
    public void removeService() {
        if (appBean.getSelectedService() != null) {
            requestTypeList.addRequestType(appBean.getSelectedService().getRequestTypeCode());
            appBean.removeSelectedService();
            applicationDocumentsHelper.updateCheckList(appBean.getServiceList(), appBean.getSourceList());
        }
    }

    /** Moves selected service up in the list of services. */
    @Action
    public void moveServiceUp() {
        ApplicationServiceBean asb = appBean.getSelectedService();
        if (asb != null) {
            Integer order = (Integer) (tabFeeDetails1.getValueAt(tabFeeDetails1.getSelectedRow(), 0));
            if (appBean.moveServiceUp()) {
                tabFeeDetails1.setValueAt(order - 1, tabFeeDetails1.getSelectedRow() - 1, 0);
                tabFeeDetails1.setValueAt(order, tabFeeDetails1.getSelectedRow(), 0);
                tabFeeDetails1.getSelectionModel().setSelectionInterval(tabFeeDetails1.getSelectedRow() - 1, tabFeeDetails1.getSelectedRow() - 1);
            }
        } else {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_SELECT_SERVICE);

        }
    }

    /**
     * Moves selected application service down in the services list. 
     * Calls {@link ApplicationBean#moveServiceDown()}
     */
    @Action
    public void moveServiceDown() {
        ApplicationServiceBean asb = appBean.getSelectedService();
        if (asb != null) {
            Integer order = (Integer) (tabFeeDetails1.getValueAt(tabFeeDetails1.getSelectedRow(), 0));
            //            lstSelectedServices.setSelectedIndex(lstSelectedServices.getSelectedIndex() - 1);
            if (appBean.moveServiceDown()) {
                tabFeeDetails1.setValueAt(order + 1, tabFeeDetails1.getSelectedRow() + 1, 0);
                tabFeeDetails1.setValueAt(order, tabFeeDetails1.getSelectedRow(), 0);
                tabFeeDetails1.getSelectionModel().setSelectionInterval(tabFeeDetails1.getSelectedRow() + 1, tabFeeDetails1.getSelectedRow() + 1);
            }
        } else {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_SELECT_SERVICE);
        }
    }

    /** Launches selected service.*/
    @Action
    public void startService() {
        launchService(false);
    }

    /** Calls "complete method for the selected service. "*/
    @Action
    public void completeService() {
        if (appBean.getSelectedService() != null) {

            String serviceName = appBean.getSelectedService().getRequestType().getDisplayValue();

            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_SERVICE_COMPLETE_WARNING,
                    new String[]{serviceName}) == MessageUtility.BUTTON_ONE) {

                List<ValidationResultBean> result = appBean.getSelectedService().complete();
                String message = MessageUtility.getLocalizedMessage(
                        ClientMessage.APPLICATION_SERVICE_COMPLETE_SUCCESS,
                        new String[]{serviceName}).getMessage();
                appBean.reload();
                customizeApplicationForm();
                openValidationResultForm(result, true, message);
            }
        }
    }

    @Action
    public void revertService() {
        if (appBean.getSelectedService() != null) {

            String serviceName = appBean.getSelectedService().getRequestType().getDisplayValue();

            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_SERVICE_REVERT_WARNING,
                    new String[]{serviceName}) == MessageUtility.BUTTON_ONE) {

                List<ValidationResultBean> result = appBean.getSelectedService().revert();
                String message = MessageUtility.getLocalizedMessage(
                        ClientMessage.APPLICATION_SERVICE_REVERT_SUCCESS,
                        new String[]{serviceName}).getMessage();
                appBean.reload();
                customizeApplicationForm();
                openValidationResultForm(result, true, message);
            }
        }
    }

    @Action
    public void cancelService() {
        if (appBean.getSelectedService() != null) {

            String serviceName = appBean.getSelectedService().getRequestType().getDisplayValue();
            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_SERVICE_CANCEL_WARNING,
                    new String[]{serviceName}) == MessageUtility.BUTTON_ONE) {

                List<ValidationResultBean> result = appBean.getSelectedService().cancel();
                String message = "";

                message = MessageUtility.getLocalizedMessage(
                        ClientMessage.APPLICATION_SERVICE_CANCEL_SUCCESS,
                        new String[]{serviceName}).getMessage();
                appBean.reload();
                customizeApplicationForm();
                openValidationResultForm(result, true, message);
            }
        }
    }

    /**
     * Removes selected property object from the properties list.
     * Calls {@link ApplicationBean#removeSelectedProperty()}
     */
    @Action
    public void removeSelectedProperty() {
        appBean.removeSelectedProperty();
    }

    /**
     * Verifies selected property object to check existence.
     * Calls {@link ApplicationBean#verifyProperty()}
     */
    @Action
    public void verifySelectedProperty() {
        if (appBean.getSelectedProperty() == null) {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_SELECT_PROPERTY_TOVERIFY);
            return;
        }

        if (appBean.verifyProperty()) {
            MessageUtility.displayMessage(ClientMessage.APPLICATION_PROPERTY_VERIFIED);
        }
    }

    @Action
    public void removeSelectedSource() {

        if (appBean.getSelectedSource() != null) {
            if (MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
                appBean.removeSelectedSource();
                applicationDocumentsHelper.verifyCheckList(appBean.getSourceList());
            }
        }
    }

    @Action
    public void removeSourceAttachment() {

        if (appBean.getSelectedSource() != null
                && appBean.getSelectedSource().getArchiveDocument() != null) {
            if (MessageUtility.displayMessage(ClientMessage.APPLICATION_CONFIRM_DOCUMENT_ATTACHMENT_REMOVAL)
                    == MessageUtility.BUTTON_ONE) {
                appBean.getSelectedSource().removeAttachment();
                tabDocuments.clearSelection();
            }
        }
    }

    @Action
    public void openSourceAttachment() {
        openAttachment();
    }

    @Action
    public void approveApplication() {
        this.takeActionAgainstApplication(ApplicationActionTypeBean.APPROVE);
    }

    @Action
    public void rejectApplication() {
        this.takeActionAgainstApplication(ApplicationActionTypeBean.CANCEL);
    }

    @Action
    public void withdrawApplication() {
        this.takeActionAgainstApplication(ApplicationActionTypeBean.WITHDRAW);
    }

    @Action
    public void requisitionApplication() {
        this.takeActionAgainstApplication(ApplicationActionTypeBean.REQUISITION);
    }

    @Action
    public void archiveApplication() {
        this.takeActionAgainstApplication(ApplicationActionTypeBean.ARCHIVE);
    }

    @Action
    public void despatchApplication() {
        this.takeActionAgainstApplication(ApplicationActionTypeBean.DESPATCH);
    }

    @Action
    public void lapseApplication() {
        this.takeActionAgainstApplication(ApplicationActionTypeBean.LAPSE);

    }

    @Action
    public void resubmitApplication() {
        this.takeActionAgainstApplication(ApplicationActionTypeBean.RESUBMIT);
    }

    /** Validates the application.*/
    @Action
    public void validactionApplication() {
        validateApplication();
        tabbedControlMain.setSelectedIndex(tabbedControlMain.indexOfComponent(validationPanel));
    }

    /**
     * Calculates fee for the application.
     * Calls {@link ApplicationBean#calculateFee()}
     */
    @Action
    public void calculateFee() {
        appBean.calculateFee();
        tabbedControlMain.setSelectedIndex(5);
    }

    /**Prints payment receipt.*/
    @Action
    public void printReceipt() {
        if (applicationID == null || applicationID.equals("")) {
            if (MessageUtility.displayMessage(ClientMessage.CHECK_NOT_LODGED_RECEIPT) == MessageUtility.BUTTON_TWO) {
                return;
            }
        }
        showReport(ReportManager.getApplicationFeeReport(appBean));
    }

    /** Allows to overview service. */
    @Action
    public void viewService() {
        launchService(true);
    }

    @Action
    public void printStatusReport() {
        if (appBean.getRowVersion() > 0
                && ApplicationServiceBean.saveInformationService(RequestTypeBean.CODE_SERVICE_ENQUIRY)) {
            showReport(ReportManager.getApplicationStatusReport(appBean));
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane actionLogPanel;
    private org.sola.clients.swing.ui.source.DocumentPanel addDocumentPanel;
    public org.sola.clients.beans.application.ApplicationBean appBean;
    private org.sola.clients.beans.application.ApplicationDocumentsHelperBean applicationDocumentsHelper;
    private javax.swing.JButton btnAddProperty;
    private javax.swing.JButton btnAddService;
    private javax.swing.JButton btnCalculateFee;
    private javax.swing.JButton btnCancelService;
    private javax.swing.JButton btnCompleteRevertService;
    private javax.swing.JButton btnDeleteDoc;
    private javax.swing.JButton btnDownService;
    private javax.swing.JButton btnLodge;
    private javax.swing.JButton btnOpenAttachment;
    private javax.swing.JButton btnPrintFee;
    private javax.swing.JButton btnPrintStatusReport;
    private javax.swing.JButton btnRemoveProperty;
    private javax.swing.JButton btnRemoveService;
    private javax.swing.JButton btnStartService;
    private javax.swing.JButton btnUPService;
    private javax.swing.JButton btnValidate;
    private javax.swing.JButton btnVerifyProperty;
    private javax.swing.JButton btnViewService;
    private javax.swing.JComboBox cbxAgents;
    public javax.swing.JComboBox cbxCommunicationWay;
    private javax.swing.JCheckBox cbxPaid;
    private org.sola.clients.beans.referencedata.CommunicationTypeListBean communicationTypeList;
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
    private javax.swing.JMenuItem menuCompleteRevertService;
    private javax.swing.JMenuItem menuDispatch;
    private javax.swing.JMenuItem menuLapse;
    private javax.swing.JMenuItem menuMoveServiceDown;
    private javax.swing.JMenuItem menuMoveServiceUp;
    private javax.swing.JMenuItem menuRemoveService;
    private javax.swing.JMenuItem menuRequisition;
    private javax.swing.JMenuItem menuResubmit;
    private javax.swing.JMenuItem menuStartService;
    private javax.swing.JMenuItem menuViewService;
    private javax.swing.JMenuItem menuWithdraw;
    private org.sola.clients.beans.party.PartySummaryListBean partySummaryList;
    private org.sola.clients.swing.ui.HeaderPanel pnlHeader;
    private javax.swing.JPopupMenu popUpServices;
    private javax.swing.JPopupMenu popupApplicationActions;
    public javax.swing.JPanel propertyPanel;
    private javax.swing.JPanel propertypartPanel;
    private org.sola.clients.beans.referencedata.RequestTypeListBean requestTypeList;
    private javax.swing.JScrollPane scrollDocRequired;
    private javax.swing.JScrollPane scrollDocuments;
    private javax.swing.JScrollPane scrollFeeDetails;
    private javax.swing.JScrollPane scrollFeeDetails1;
    private javax.swing.JScrollPane scrollPropertyDetails;
    private javax.swing.JPanel servicesPanel;
    private org.sola.clients.beans.source.SourceTypeListBean sourceTypeList;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabActionLog;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabDocuments;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabFeeDetails;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabFeeDetails1;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabPropertyDetails;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tabValidations;
    public javax.swing.JTabbedPane tabbedControlMain;
    private javax.swing.JToolBar tbPropertyDetails;
    private javax.swing.JToolBar tbServices;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblDocTypesHelper;
    public javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtAppNumber;
    private javax.swing.JTextField txtArea;
    private javax.swing.JTextField txtDate;
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
