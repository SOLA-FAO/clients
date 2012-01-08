/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
import java.util.Collections;
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
import org.sola.clients.beans.sorters.ServicesSorterByOrder;
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
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.jdesktop.application.Application;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.clients.swing.desktop.DesktopApplication;
import org.sola.clients.swing.desktop.administrative.PropertyForm;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.referencedata.ApplicationActionTypeBean;
import org.sola.clients.swing.desktop.cadastre.CadastreChangeMapForm;
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
import org.sola.common.RolesConstants;
import org.sola.clients.swing.ui.renderers.DateTimeRenderer;

/**
 * This form is used to create new application or edit existing one.
 * <p>The following list of beans is used to bind the data on the form:<br />
 * {@link ApplicationBean}, <br />{@link RequestTypeListBean}, <br />
 * {@link PartySummaryListBean}, <br />{@link CommunicationTypeListBean}, <br />
 * {@link SourceTypeListBean}, <br />{@link ApplicationDocumentsHelperBean}</p>
 */
public class ApplicationForm extends javax.swing.JFrame {

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

            if (applicationBean.getServiceList() != null) {
                Collections.sort(applicationBean.getServiceList(), new ServicesSorterByOrder());
            }
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

    /** Default constructor to create new application. */
    public ApplicationForm() {
        this(null);
    }

    /** 
     * This constructor is used to open existing application for editing. 
     * @param applicationId ID of application to open.
     */
    public ApplicationForm(String applicationId) {
        this.applicationID = applicationId;

        this.setIconImage(new ImageIcon(ApplicationForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());

        initComponents();
        postInit();
    }

    /** Runs post initialization actions to customize form elements. */
    private void postInit() {
        this.getRootPane().setDefaultButton(btnLodge);

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

    /** Applies customization of form, based on Application status. */
    private void customizeApplicationForm() {
        if (appBean != null && !appBean.isNew()) {
            this.setTitle(this.getTitle() + " #" + appBean.getNr());
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
            cbxCommunicationWay.setSelectedIndex(-1);
        }

        btnApproveApplication.getAction().setEnabled(appBean.canApprove()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_APPROVE));
        btnRejectApplication.getAction().setEnabled(appBean.canCancel()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_REJECT));
        btnArchiveApplication.getAction().setEnabled(appBean.canArchive()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_ARCHIVE));
        btnDespatchApplication.getAction().setEnabled(appBean.canDespatch()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_DESPATCH));
        btnRequisitionApplication.getAction().setEnabled(appBean.canRequisition()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_REQUISITE));
        btnResubmitApplication.getAction().setEnabled(appBean.canResubmit()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_RESUBMIT));
        btnLapseApplication.getAction().setEnabled(appBean.canLapse()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_REJECT));
        btnWithdrawApplication.getAction().setEnabled(appBean.canWithdraw()
                && SecurityBean.isInRole(RolesConstants.APPLICATION_WITHDRAW));

        if (this.btnValidate.getAction().isEnabled()) {
            this.btnValidate.getAction().setEnabled(appBean.canValidate()
                    && SecurityBean.isInRole(RolesConstants.APPLICATION_VALIDATE));
        }

        String applicationStatus = appBean.getStatusCode();

        if (applicationStatus != null) {
            boolean editAllowed = appBean.isEditingAllowed()
                    && SecurityBean.isInRole(RolesConstants.APPLICATION_EDIT_APPS);
            btnLodge.setEnabled(editAllowed);
            btnAddProperty.setEnabled(editAllowed);
            btnRemoveProperty.setEnabled(editAllowed);
            btnVerifyProperty.setEnabled(editAllowed);
            btnDeleteDoc.setEnabled(editAllowed);
            //btnRemoveAttachedDocument.setEnabled(editAllowed);
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
                ActionMap actionMap = Application.getInstance(DesktopApplication.class).getContext().getActionMap(ApplicationForm.class, this);

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

    private void openPropertyForm(ApplicationPropertyBean applicationProperty, boolean readOnly) {
        if (applicationProperty != null) {
            ApplicationBean applicationBean = appBean.copy();
            PropertyForm propertyForm = new PropertyForm(applicationBean,
                    applicationBean.getSelectedService(), applicationProperty, readOnly);
            DesktopApplication.getApplication().show(propertyForm);
            this.dispose();
        }
    }

    /** Opens dialog form to display status change result for application or service. */
    private void openValidationResultForm(List<ValidationResultBean> validationResultList,
            boolean isSuccess, String message) {
        ValidationResultForm resultForm = new ValidationResultForm(
                this, true, validationResultList, isSuccess, message);
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

            // Determine what form to start for selected service
            if (appBean.getSelectedService().getRequestTypeCode().equalsIgnoreCase("regnPowerOfAttorney")) {
                // Run registration/cancelation Power of attorney
                TransactionedDocumentsForm form = new TransactionedDocumentsForm(
                        this, true, appBean, appBean.getSelectedService());
                form.setLocationRelativeTo(this);
                form.setVisible(true);
            } else {
                if (appBean.getSelectedService().getRequestTypeCode().equalsIgnoreCase("cadastreChange")) {
                    if (appBean.getPropertyList().getFilteredList().size() == 1) {
                        CadastreChangeMapForm form = new CadastreChangeMapForm(
                                appBean,
                                appBean.getSelectedService(),
                                appBean.getPropertyList().getFilteredList().get(0));
                        form.setLocationRelativeTo(this);

                        form.setVisible(true);
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
                                    CadastreChangeMapForm form = new CadastreChangeMapForm(
                                            appBean,
                                            appBean.getSelectedService(), property);
                                    form.setVisible(true);
                                }
                            }
                        });
                        propertyListForm.setVisible(true);
                    } else {
                        CadastreChangeMapForm form = new CadastreChangeMapForm(
                                appBean,
                                appBean.getSelectedService(),
                                null);
                        form.setLocationRelativeTo(this);

                        form.setVisible(true);
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
        communicationTypeList = new org.sola.clients.beans.referencedata.CommunicationTypeListBean();
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
        tabbedControlMain = new javax.swing.JTabbedPane();
        contactPanel = new javax.swing.JPanel();
        labName = new javax.swing.JLabel();
        labLastName = new javax.swing.JLabel();
        labAddress = new javax.swing.JLabel();
        txtFirstName = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        labEmail = new javax.swing.JLabel();
        labPhone = new javax.swing.JLabel();
        labFax = new javax.swing.JLabel();
        txtFax = new javax.swing.JTextField();
        txtPhone = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtLastName = new javax.swing.JTextField();
        labPreferredWay = new javax.swing.JLabel();
        cbxCommunicationWay = new javax.swing.JComboBox();
        internalServicesScrollPanel = new javax.swing.JScrollPane();
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
        propertypartPanel = new javax.swing.JPanel();
        labFirstPart = new javax.swing.JLabel();
        txtFirstPart = new javax.swing.JTextField();
        txtLastPart = new javax.swing.JTextField();
        labLastPart = new javax.swing.JLabel();
        labArea = new javax.swing.JLabel();
        labValue = new javax.swing.JLabel();
        btnAddProperty = new javax.swing.JButton();
        txtArea = new javax.swing.JTextField();
        txtValue = new javax.swing.JTextField();
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
        dealingPanel = new javax.swing.JPanel();
        labAgents = new javax.swing.JLabel();
        cbxAgents = new javax.swing.JComboBox();
        labDate = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        labStatus = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        btnLodge = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        btnCalculateFee = new javax.swing.JButton();
        btnPrintFee = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnValidate = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnApproveApplication = new javax.swing.JButton();
        btnDespatchApplication = new javax.swing.JButton();
        btnArchiveApplication = new javax.swing.JButton();
        btnRequisitionApplication = new javax.swing.JButton();
        btnResubmitApplication = new javax.swing.JButton();
        btnWithdrawApplication = new javax.swing.JButton();
        btnLapseApplication = new javax.swing.JButton();
        btnRejectApplication = new javax.swing.JButton();

        popUpServices.setName("popUpServices"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(ApplicationForm.class, this);
        menuAddService.setAction(actionMap.get("addService")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        menuAddService.setText(bundle.getString("ApplicationForm.menuAddService.text")); // NOI18N
        menuAddService.setName("menuAddService"); // NOI18N
        popUpServices.add(menuAddService);

        menuRemoveService.setAction(actionMap.get("removeService")); // NOI18N
        menuRemoveService.setText(bundle.getString("ApplicationForm.menuRemoveService.text")); // NOI18N
        menuRemoveService.setName("menuRemoveService"); // NOI18N
        popUpServices.add(menuRemoveService);

        jSeparator3.setName("jSeparator3"); // NOI18N
        popUpServices.add(jSeparator3);

        menuMoveServiceUp.setAction(actionMap.get("moveServiceUp")); // NOI18N
        menuMoveServiceUp.setText(bundle.getString("ApplicationForm.menuMoveServiceUp.text")); // NOI18N
        menuMoveServiceUp.setName("menuMoveServiceUp"); // NOI18N
        popUpServices.add(menuMoveServiceUp);

        menuMoveServiceDown.setAction(actionMap.get("moveServiceDown")); // NOI18N
        menuMoveServiceDown.setText(bundle.getString("ApplicationForm.menuMoveServiceDown.text")); // NOI18N
        menuMoveServiceDown.setName("menuMoveServiceDown"); // NOI18N
        popUpServices.add(menuMoveServiceDown);

        jSeparator4.setName("jSeparator4"); // NOI18N
        popUpServices.add(jSeparator4);

        menuViewService.setAction(actionMap.get("viewService")); // NOI18N
        menuViewService.setText(bundle.getString("ApplicationForm.menuViewService.text")); // NOI18N
        menuViewService.setName("menuViewService"); // NOI18N
        popUpServices.add(menuViewService);

        menuStartService.setAction(actionMap.get("startService")); // NOI18N
        menuStartService.setText(bundle.getString("ApplicationForm.menuStartService.text")); // NOI18N
        menuStartService.setName("menuStartService"); // NOI18N
        popUpServices.add(menuStartService);

        menuCompleteRevertService.setAction(actionMap.get("completeService")); // NOI18N
        menuCompleteRevertService.setText(bundle.getString("ApplicationForm.menuCompleteRevertService.text")); // NOI18N
        menuCompleteRevertService.setName("menuCompleteRevertService"); // NOI18N
        popUpServices.add(menuCompleteRevertService);

        menuCancelService.setAction(actionMap.get("cancelService")); // NOI18N
        menuCancelService.setText(bundle.getString("ApplicationForm.menuCancelService.text")); // NOI18N
        menuCancelService.setName("menuCancelService"); // NOI18N
        popUpServices.add(menuCancelService);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("ApplicationForm.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(300, 200));
        setName("Form"); // NOI18N
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        tabbedControlMain.setFont(UIManager.getFont(tabFont));
        tabbedControlMain.setName("tabbedControlMain"); // NOI18N
        tabbedControlMain.setPreferredSize(new java.awt.Dimension(832, 562));
        tabbedControlMain.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        contactPanel.setName("contactPanel"); // NOI18N
        contactPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        contactPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contactPanelMouseClicked(evt);
            }
        });

        labName.setFont(new java.awt.Font("Tahoma", 0, 12));
        labName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labName.setLabelFor(txtFirstName);
        labName.setText(bundle.getString("ApplicationForm.labName.text")); // NOI18N
        labName.setIconTextGap(1);
        labName.setName("labName"); // NOI18N

        labLastName.setFont(new java.awt.Font("Tahoma", 0, 12));
        labLastName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labLastName.setText(bundle.getString("ApplicationForm.labLastName.text")); // NOI18N
        labLastName.setIconTextGap(1);
        labLastName.setName("labLastName"); // NOI18N

        labAddress.setFont(new java.awt.Font("Tahoma", 0, 12));
        labAddress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        labAddress.setText(bundle.getString("ApplicationForm.labAddress.text")); // NOI18N
        labAddress.setIconTextGap(1);
        labAddress.setName("labAddress"); // NOI18N

        txtFirstName.setFont(UIManager.getFont(txtFont));
        txtFirstName.setName("txtFirstName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.name}"), txtFirstName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtFirstName.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFirstName.setHorizontalAlignment(JTextField.LEADING);

        txtAddress.setFont(UIManager.getFont(txtFont));
        txtAddress.setName("txtAddress"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.address.description}"), txtAddress, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtAddress.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtAddress.setHorizontalAlignment(JTextField.LEADING);

        labEmail.setFont(new java.awt.Font("Tahoma", 0, 12));
        labEmail.setText(bundle.getString("ApplicationForm.labEmail.text")); // NOI18N
        labEmail.setName("labEmail"); // NOI18N

        labPhone.setFont(new java.awt.Font("Tahoma", 0, 12));
        labPhone.setText(bundle.getString("ApplicationForm.labPhone.text")); // NOI18N
        labPhone.setName("labPhone"); // NOI18N

        labFax.setFont(new java.awt.Font("Tahoma", 0, 12));
        labFax.setText(bundle.getString("ApplicationForm.labFax.text")); // NOI18N
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

        txtLastName.setFont(UIManager.getFont(txtFont));
        txtLastName.setName("txtLastName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.lastName}"), txtLastName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtLastName.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastName.setHorizontalAlignment(JTextField.LEADING);

        labPreferredWay.setFont(new java.awt.Font("Tahoma", 0, 12));
        labPreferredWay.setText(bundle.getString("ApplicationForm.labPreferredWay.text")); // NOI18N
        labPreferredWay.setName("labPreferredWay"); // NOI18N

        cbxCommunicationWay.setFont(UIManager.getFont(txtFont));
        cbxCommunicationWay.setName("cbxCommunicationWay"); // NOI18N
        cbxCommunicationWay.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${communicationTypeList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, communicationTypeList, eLProperty, cbxCommunicationWay);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${contactPerson.preferredCommunication}"), cbxCommunicationWay, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbxCommunicationWay.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout contactPanelLayout = new javax.swing.GroupLayout(contactPanel);
        contactPanel.setLayout(contactPanelLayout);
        contactPanelLayout.setHorizontalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtAddress, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, contactPanelLayout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labName, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(27, 27, 27)
                            .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(labLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(contactPanelLayout.createSequentialGroup()
                            .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtFax)
                                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                                    .addComponent(labPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(labFax, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                            .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(labEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(labPreferredWay, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtPhone, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                                .addComponent(cbxCommunicationWay, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(120, 120, 120))
        );
        contactPanelLayout.setVerticalGroup(
            contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labName)
                    .addComponent(labLastName))
                .addGap(6, 6, 6)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(labAddress)
                .addGap(4, 4, 4)
                .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labPhone)
                    .addComponent(labEmail))
                .addGap(3, 3, 3)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(contactPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(labPreferredWay))
                    .addComponent(labFax, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(contactPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtFax, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbxCommunicationWay, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(259, 259, 259))
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationForm.contactPanel.TabConstraints.tabTitle"), contactPanel); // NOI18N

        internalServicesScrollPanel.setBorder(null);
        internalServicesScrollPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        internalServicesScrollPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        internalServicesScrollPanel.setName("internalServicesScrollPanel"); // NOI18N
        internalServicesScrollPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        servicesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("ApplicationForm.servicesPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 12))); // NOI18N
        servicesPanel.setFont(new java.awt.Font("Tahoma", 0, 12));
        servicesPanel.setName("servicesPanel"); // NOI18N
        servicesPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

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
        btnAddService.setText(bundle.getString("ApplicationForm.btnAddService.text")); // NOI18N
        btnAddService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAddService.setName("btnAddService"); // NOI18N
        btnAddService.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        tbServices.add(btnAddService);

        btnRemoveService.setAction(actionMap.get("removeService")); // NOI18N
        btnRemoveService.setFont(UIManager.getFont(btnFont));
        btnRemoveService.setText(bundle.getString("ApplicationForm.btnRemoveService.text")); // NOI18N
        btnRemoveService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRemoveService.setName("btnRemoveService"); // NOI18N
        tbServices.add(btnRemoveService);

        jSeparator1.setName("jSeparator1"); // NOI18N
        tbServices.add(jSeparator1);

        btnUPService.setAction(actionMap.get("moveServiceUp")); // NOI18N
        btnUPService.setFont(UIManager.getFont(btnFont));
        btnUPService.setText(bundle.getString("ApplicationForm.btnUPService.text")); // NOI18N
        btnUPService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUPService.setName("btnUPService"); // NOI18N
        tbServices.add(btnUPService);

        btnDownService.setAction(actionMap.get("moveServiceDown")); // NOI18N
        btnDownService.setFont(UIManager.getFont(btnFont));
        btnDownService.setText(bundle.getString("ApplicationForm.btnDownService.text")); // NOI18N
        btnDownService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDownService.setName("btnDownService"); // NOI18N
        tbServices.add(btnDownService);

        jSeparator2.setName("jSeparator2"); // NOI18N
        tbServices.add(jSeparator2);

        btnViewService.setAction(actionMap.get("viewService")); // NOI18N
        btnViewService.setText(bundle.getString("ApplicationForm.btnViewService.text")); // NOI18N
        btnViewService.setFocusable(false);
        btnViewService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnViewService.setName("btnViewService"); // NOI18N
        btnViewService.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbServices.add(btnViewService);

        btnStartService.setAction(actionMap.get("startService")); // NOI18N
        btnStartService.setFont(UIManager.getFont(btnFont));
        btnStartService.setText(bundle.getString("ApplicationForm.btnStartService.text")); // NOI18N
        btnStartService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnStartService.setName("btnStartService"); // NOI18N
        btnStartService.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        tbServices.add(btnStartService);

        btnCancelService.setAction(actionMap.get("cancelService")); // NOI18N
        btnCancelService.setFont(UIManager.getFont(btnFont));
        btnCancelService.setText(bundle.getString("ApplicationForm.btnCancelService.text")); // NOI18N
        btnCancelService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCancelService.setName("btnCancelService"); // NOI18N
        tbServices.add(btnCancelService);

        btnCompleteRevertService.setAction(actionMap.get("completeService")); // NOI18N
        btnCompleteRevertService.setFont(UIManager.getFont(btnFont));
        btnCompleteRevertService.setText(bundle.getString("ApplicationForm.btnCompleteRevertService.text")); // NOI18N
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
                    .addComponent(tbServices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE)
                    .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 846, Short.MAX_VALUE))
                .addGap(963, 963, 963))
        );
        servicesPanelLayout.setVerticalGroup(
            servicesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(servicesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbServices, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollFeeDetails1, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addGap(154, 154, 154))
        );

        internalServicesScrollPanel.setViewportView(servicesPanel);

        tabbedControlMain.addTab(bundle.getString("ApplicationForm.internalServicesScrollPanel.TabConstraints.tabTitle"), internalServicesScrollPanel); // NOI18N

        propertyPanel.setName("propertyPanel"); // NOI18N
        propertyPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        propertyPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                propertyPanelMouseClicked(evt);
            }
        });

        tbPropertyDetails.setFloatable(false);
        tbPropertyDetails.setRollover(true);
        tbPropertyDetails.setToolTipText(bundle.getString("ApplicationForm.tbPropertyDetails.toolTipText")); // NOI18N
        tbPropertyDetails.setName("tbPropertyDetails"); // NOI18N

        btnRemoveProperty.setAction(actionMap.get("removeSelectedProperty")); // NOI18N
        btnRemoveProperty.setText(bundle.getString("ApplicationForm.btnRemoveProperty.text")); // NOI18N
        btnRemoveProperty.setToolTipText(bundle.getString("ApplicationForm.btnRemoveProperty.toolTipText")); // NOI18N
        btnRemoveProperty.setName("btnRemoveProperty"); // NOI18N
        tbPropertyDetails.add(btnRemoveProperty);

        btnVerifyProperty.setAction(actionMap.get("verifySelectedProperty")); // NOI18N
        btnVerifyProperty.setText(bundle.getString("ApplicationForm.btnVerifyProperty.text")); // NOI18N
        btnVerifyProperty.setToolTipText(bundle.getString("ApplicationForm.btnVerifyProperty.toolTipText")); // NOI18N
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
        tabPropertyDetails.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationForm.tabPropertyDetails.columnModel.title0")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationForm.tabPropertyDetails.columnModel.title1")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationForm.tabPropertyDetails.columnModel.title2")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationForm.tabPropertyDetails.columnModel.title3")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ApplicationForm.tabPropertyDetails.columnModel.title4")); // NOI18N
        tabPropertyDetails.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("NewApplication.tabPropertyDetails.columnModel.title6")); // NOI18N

        propertypartPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        propertypartPanel.setFont(new java.awt.Font("Tahoma", 0, 12));
        propertypartPanel.setName("propertypartPanel"); // NOI18N
        propertypartPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        labFirstPart.setFont(UIManager.getFont(labFont));
        labFirstPart.setText(bundle.getString("ApplicationForm.labFirstPart.text")); // NOI18N
        labFirstPart.setName("labFirstPart"); // NOI18N

        txtFirstPart.setFont(UIManager.getFont(txtFont));
        txtFirstPart.setText(bundle.getString("ApplicationForm.txtFirstPart.text")); // NOI18N
        txtFirstPart.setName("txtFirstPart"); // NOI18N
        txtFirstPart.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtFirstPart.setHorizontalAlignment(JTextField.LEADING);

        txtLastPart.setFont(UIManager.getFont(txtFont));
        txtLastPart.setText(bundle.getString("ApplicationForm.txtLastPart.text")); // NOI18N
        txtLastPart.setName("txtLastPart"); // NOI18N
        txtLastPart.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtLastPart.setHorizontalAlignment(JTextField.LEADING);

        labLastPart.setFont(UIManager.getFont(labFont));
        labLastPart.setText(bundle.getString("ApplicationForm.labLastPart.text")); // NOI18N
        labLastPart.setName("labLastPart"); // NOI18N

        labArea.setFont(UIManager.getFont(labFont));
        labArea.setText(bundle.getString("ApplicationForm.labArea.text")); // NOI18N
        labArea.setName("labArea"); // NOI18N

        labValue.setFont(UIManager.getFont(labFont));
        labValue.setText(bundle.getString("ApplicationForm.labValue.text")); // NOI18N
        labValue.setName("labValue"); // NOI18N

        btnAddProperty.setBackground(UIManager.getColor(btnBackground));
        btnAddProperty.setFont(UIManager.getFont(btnFont));
        btnAddProperty.setText(bundle.getString("ApplicationForm.btnAddProperty.text")); // NOI18N
        btnAddProperty.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddProperty.setName("btnAddProperty"); // NOI18N
        btnAddProperty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPropertyActionPerformed(evt);
            }
        });

        txtArea.setFont(UIManager.getFont(txtFont));
        txtArea.setText(bundle.getString("ApplicationForm.txtArea.text")); // NOI18N
        txtArea.setName("txtArea"); // NOI18N
        txtArea.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtArea.setHorizontalAlignment(JTextField.LEADING);

        txtValue.setFont(UIManager.getFont(txtFont));
        txtValue.setText(bundle.getString("ApplicationForm.txtValue.text")); // NOI18N
        txtValue.setName("txtValue"); // NOI18N
        txtValue.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtValue.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout propertypartPanelLayout = new javax.swing.GroupLayout(propertypartPanel);
        propertypartPanel.setLayout(propertypartPanelLayout);
        propertypartPanelLayout.setHorizontalGroup(
            propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertypartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFirstPart, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labFirstPart))
                .addGap(18, 18, 18)
                .addGroup(propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtLastPart, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labLastPart))
                .addGap(36, 36, 36)
                .addGroup(propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labArea))
                .addGap(27, 27, 27)
                .addGroup(propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(propertypartPanelLayout.createSequentialGroup()
                        .addComponent(txtValue, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAddProperty))
                    .addComponent(labValue))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        propertypartPanelLayout.setVerticalGroup(
            propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertypartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(propertypartPanelLayout.createSequentialGroup()
                        .addGroup(propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labLastPart)
                            .addComponent(labFirstPart))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLastPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(propertypartPanelLayout.createSequentialGroup()
                        .addGroup(propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labValue)
                            .addComponent(labArea))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(propertypartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddProperty)))
                    .addComponent(txtFirstPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout propertyPanelLayout = new javax.swing.GroupLayout(propertyPanel);
        propertyPanel.setLayout(propertyPanelLayout);
        propertyPanelLayout.setHorizontalGroup(
            propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, propertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(propertypartPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE)
                    .addComponent(tbPropertyDetails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE))
                .addContainerGap())
        );
        propertyPanelLayout.setVerticalGroup(
            propertyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(propertyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(propertypartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbPropertyDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(scrollPropertyDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationForm.propertyPanel.TabConstraints.tabTitle"), propertyPanel); // NOI18N

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
        tabDocuments.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationForm.tabDocuments.columnModel.title0_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(0).setCellRenderer(new ReferenceCodeCellConverter(CacheManager.getSourceTypesMap()));
        tabDocuments.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationForm.tabDocuments.columnModel.title1_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationForm.tabDocuments.columnModel.title2_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationForm.tabDocuments.columnModel.title3_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ApplicationForm.tabDocuments.columnModel.title4_1")); // NOI18N
        tabDocuments.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("ApplicationForm.tabDocuments.columnModel.title5")); // NOI18N
        tabDocuments.getColumnModel().getColumn(6).setPreferredWidth(30);
        tabDocuments.getColumnModel().getColumn(6).setMaxWidth(30);
        tabDocuments.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("ApplicationForm.tabDocuments.columnModel.title6")); // NOI18N
        tabDocuments.getColumnModel().getColumn(6).setCellRenderer(new AttachedDocumentCellRenderer());

        labDocRequired.setBackground(new java.awt.Color(255, 255, 204));
        labDocRequired.setFont(UIManager.getFont(labFont));
        labDocRequired.setText(bundle.getString("ApplicationForm.labDocRequired.text")); // NOI18N
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
        tblDocTypesHelper.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationForm.tblDocTypesHelper.columnModel.title0_1")); // NOI18N
        tblDocTypesHelper.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationForm.tblDocTypesHelper.columnModel.title1_1")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setName("jPanel1"); // NOI18N

        addDocumentPanel.setName("addDocumentPanel"); // NOI18N
        addDocumentPanel.setOkButtonText(bundle.getString("ApplicationForm.addDocumentPanel.okButtonText")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addDocumentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
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
        jToolBar1.setToolTipText(bundle.getString("ApplicationForm.jToolBar1.toolTipText")); // NOI18N
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnDeleteDoc.setAction(actionMap.get("removeSelectedSource")); // NOI18N
        btnDeleteDoc.setText(bundle.getString("ApplicationForm.btnDeleteDoc.text")); // NOI18N
        btnDeleteDoc.setName("btnDeleteDoc"); // NOI18N
        jToolBar1.add(btnDeleteDoc);

        btnOpenAttachment.setAction(actionMap.get("openSourceAttachment")); // NOI18N
        btnOpenAttachment.setText(bundle.getString("ApplicationForm.btnOpenAttachment.text")); // NOI18N
        btnOpenAttachment.setToolTipText(bundle.getString("ApplicationForm.btnOpenAttachment.toolTipText")); // NOI18N
        btnOpenAttachment.setActionCommand(bundle.getString("ApplicationForm.btnOpenAttachment.actionCommand")); // NOI18N
        btnOpenAttachment.setName("btnOpenAttachment"); // NOI18N
        jToolBar1.add(btnOpenAttachment);

        javax.swing.GroupLayout documentPanelLayout = new javax.swing.GroupLayout(documentPanel);
        documentPanel.setLayout(documentPanelLayout);
        documentPanelLayout.setHorizontalGroup(
            documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(documentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(documentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
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
                        .addComponent(scrollDocRequired, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE))
                    .addGroup(documentPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollDocuments, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationForm.documentPanel.TabConstraints.tabTitle"), documentPanel); // NOI18N

        mapPanel.setName("mapPanel"); // NOI18N

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 876, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 401, Short.MAX_VALUE)
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationForm.mapPanel.TabConstraints.tabTitle"), mapPanel); // NOI18N

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
        tabFeeDetails.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationForm.tabFeeDetails.columnModel.title0")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationForm.tabFeeDetails.columnModel.title1_1")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationForm.tabFeeDetails.columnModel.title2_2")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationForm.tabFeeDetails.columnModel.title3")); // NOI18N
        tabFeeDetails.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("ApplicationForm.tabFeeDetails.columnModel.title4")); // NOI18N

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

        cbxPaid.setText(bundle.getString("ApplicationForm.cbxPaid.text")); // NOI18N
        cbxPaid.setActionCommand(bundle.getString("ApplicationForm.cbxPaid.actionCommand")); // NOI18N
        cbxPaid.setMargin(new java.awt.Insets(2, 0, 2, 2));
        cbxPaid.setName("cbxPaid"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${feePaid}"), cbxPaid, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        labTotalFee3.setFont(labTotalFee3.getFont().deriveFont(labTotalFee3.getFont().getStyle() | java.awt.Font.BOLD, labTotalFee3.getFont().getSize()+1));
        labTotalFee3.setText(bundle.getString("ApplicationForm.labTotalFee3.text")); // NOI18N
        labTotalFee3.setName("labTotalFee3"); // NOI18N

        labTotalFee2.setFont(UIManager.getFont(btnFont));
        labTotalFee2.setText(bundle.getString("ApplicationForm.labTotalFee2.text")); // NOI18N
        labTotalFee2.setName("labTotalFee2"); // NOI18N

        labTotalFee.setFont(UIManager.getFont(labFont));
        labTotalFee.setText(bundle.getString("ApplicationForm.labTotalFee.text")); // NOI18N
        labTotalFee.setName("labTotalFee"); // NOI18N

        labTotalFee1.setFont(UIManager.getFont(btnFont));
        labTotalFee1.setText(bundle.getString("ApplicationForm.labTotalFee1.text")); // NOI18N
        labTotalFee1.setName("labTotalFee1"); // NOI18N

        labFixedFee.setBackground(new java.awt.Color(255, 255, 255));
        labFixedFee.setFont(UIManager.getFont(btnFont));
        labFixedFee.setText(bundle.getString("ApplicationForm.labFixedFee.text")); // NOI18N
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
                    .addComponent(scrollFeeDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        feesPanelLayout.setVerticalGroup(
            feesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, feesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollFeeDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationForm.feesPanel.TabConstraints.tabTitle"), feesPanel); // NOI18N

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
                .addComponent(validationsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE)
                .addContainerGap())
        );
        validationPanelLayout.setVerticalGroup(
            validationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validationsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationForm.validationPanel.TabConstraints.tabTitle"), validationPanel); // NOI18N

        historyPanel.setFont(new java.awt.Font("Tahoma", 0, 12));
        historyPanel.setName("historyPanel"); // NOI18N
        historyPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        historyPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                historyPanelMouseClicked(evt);
            }
        });

        actionLogPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("ApplicationForm.actionLogPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
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
        tabActionLog.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("ApplicationForm.tabActionLog.columnModel.title0")); // NOI18N
        tabActionLog.getColumnModel().getColumn(0).setCellRenderer(new DateTimeRenderer());
        tabActionLog.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("ApplicationForm.tabActionLog.columnModel.title1_1")); // NOI18N
        tabActionLog.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("ApplicationForm.tabActionLog.columnModel.title2_1")); // NOI18N
        tabActionLog.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("ApplicationForm.tabActionLog.columnModel.title3_1")); // NOI18N
        tabActionLog.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        javax.swing.GroupLayout historyPanelLayout = new javax.swing.GroupLayout(historyPanel);
        historyPanel.setLayout(historyPanelLayout);
        historyPanelLayout.setHorizontalGroup(
            historyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(historyPanelLayout.createSequentialGroup()
                .addComponent(actionLogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
                .addContainerGap())
        );
        historyPanelLayout.setVerticalGroup(
            historyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, historyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(actionLogPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(158, 158, 158))
        );

        tabbedControlMain.addTab(bundle.getString("ApplicationForm.historyPanel.TabConstraints.tabTitle"), historyPanel); // NOI18N

        dealingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bundle.getString("ApplicationForm.dealingPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        dealingPanel.setName("dealingPanel"); // NOI18N
        dealingPanel.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        labAgents.setFont(new java.awt.Font("Tahoma", 0, 12));
        labAgents.setText(bundle.getString("ApplicationForm.labAgents.text")); // NOI18N
        labAgents.setIconTextGap(1);
        labAgents.setName("labAgents"); // NOI18N

        cbxAgents.setFont(UIManager.getFont(cmbFont));
        cbxAgents.setName("cbxAgents"); // NOI18N
        cbxAgents.setRenderer(new SimpleComboBoxRenderer("getName"));
        cbxAgents.setRequestFocusEnabled(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${partySummaryList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, partySummaryList, eLProperty, cbxAgents);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${agent}"), cbxAgents, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbxAgents.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        labDate.setFont(new java.awt.Font("Tahoma", 0, 12));
        labDate.setText(bundle.getString("ApplicationForm.labDate.text")); // NOI18N
        labDate.setName("labDate"); // NOI18N

        txtDate.setEditable(false);
        txtDate.setFont(UIManager.getFont(txtFont));
        txtDate.setName("txtDate"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${lodgingDatetime}"), txtDate, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new DateConverter());
        bindingGroup.addBinding(binding);

        txtDate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtDate.setHorizontalAlignment(JTextField.LEADING);

        labStatus.setFont(new java.awt.Font("Tahoma", 0, 12));
        labStatus.setText(bundle.getString("ApplicationForm.labStatus.text")); // NOI18N
        labStatus.setName("labStatus"); // NOI18N

        txtStatus.setEditable(false);
        txtStatus.setFont(UIManager.getFont(txtFont));
        txtStatus.setName("txtStatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, appBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtStatus.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txtStatus.setHorizontalAlignment(JTextField.LEADING);

        javax.swing.GroupLayout dealingPanelLayout = new javax.swing.GroupLayout(dealingPanel);
        dealingPanel.setLayout(dealingPanelLayout);
        dealingPanelLayout.setHorizontalGroup(
            dealingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dealingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labAgents, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxAgents, 0, 263, Short.MAX_VALUE)
                .addGap(38, 38, 38)
                .addComponent(labDate, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(labStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        dealingPanelLayout.setVerticalGroup(
            dealingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dealingPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(dealingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxAgents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labAgents)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labStatus)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labDate))
                .addContainerGap())
        );

        btnLodge.setFont(UIManager.getFont(btnFont));
        btnLodge.setText(bundle.getString("ApplicationForm.btnLodge.text")); // NOI18N
        btnLodge.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnLodge.setName("btnLodge"); // NOI18N
        btnLodge.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        btnLodge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLodgeActionPerformed(evt);
            }
        });

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setToolTipText(bundle.getString("ApplicationForm.jToolBar2.toolTipText")); // NOI18N
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnCalculateFee.setAction(actionMap.get("calculateFee")); // NOI18N
        btnCalculateFee.setFont(UIManager.getFont(btnFont));
        btnCalculateFee.setText(bundle.getString("ApplicationForm.btnCalculateFee.text")); // NOI18N
        btnCalculateFee.setName("btnCalculateFee"); // NOI18N
        jToolBar2.add(btnCalculateFee);

        btnPrintFee.setAction(actionMap.get("printReceipt")); // NOI18N
        btnPrintFee.setFont(UIManager.getFont(btnFont));
        btnPrintFee.setText(bundle.getString("ApplicationForm.btnPrintFee.text")); // NOI18N
        btnPrintFee.setName("btnPrintFee"); // NOI18N
        jToolBar2.add(btnPrintFee);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jToolBar2.add(jSeparator6);

        btnValidate.setAction(actionMap.get("validactionApplication")); // NOI18N
        btnValidate.setFont(UIManager.getFont(btnFont));
        btnValidate.setText(bundle.getString("ApplicationForm.btnValidate.text")); // NOI18N
        btnValidate.setName("btnValidate"); // NOI18N
        btnValidate.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        jToolBar2.add(btnValidate);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jToolBar2.add(jSeparator5);

        btnApproveApplication.setAction(actionMap.get("approveApplication")); // NOI18N
        btnApproveApplication.setFont(UIManager.getFont(btnFont));
        btnApproveApplication.setText(bundle.getString("ApplicationForm.btnApproveApplication.text")); // NOI18N
        btnApproveApplication.setName("btnApproveApplication"); // NOI18N
        jToolBar2.add(btnApproveApplication);

        btnDespatchApplication.setAction(actionMap.get("despatchApplication")); // NOI18N
        btnDespatchApplication.setFont(UIManager.getFont(btnFont));
        btnDespatchApplication.setText(bundle.getString("ApplicationForm.btnDespatchApplication.text")); // NOI18N
        btnDespatchApplication.setFocusable(false);
        btnDespatchApplication.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnDespatchApplication.setName("btnDespatchApplication"); // NOI18N
        btnDespatchApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnDespatchApplication);

        btnArchiveApplication.setAction(actionMap.get("archiveApplication")); // NOI18N
        btnArchiveApplication.setFont(UIManager.getFont(btnFont));
        btnArchiveApplication.setText(bundle.getString("ApplicationForm.btnArchiveApplication.text")); // NOI18N
        btnArchiveApplication.setFocusable(false);
        btnArchiveApplication.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnArchiveApplication.setName("btnArchiveApplication"); // NOI18N
        btnArchiveApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnArchiveApplication);

        btnRequisitionApplication.setAction(actionMap.get("requisitionApplication")); // NOI18N
        btnRequisitionApplication.setFont(UIManager.getFont(btnFont));
        btnRequisitionApplication.setText(bundle.getString("ApplicationForm.btnRequisitionApplication.text")); // NOI18N
        btnRequisitionApplication.setFocusable(false);
        btnRequisitionApplication.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnRequisitionApplication.setName("btnRequisitionApplication"); // NOI18N
        btnRequisitionApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnRequisitionApplication);

        btnResubmitApplication.setAction(actionMap.get("resubmitApplication")); // NOI18N
        btnResubmitApplication.setFont(UIManager.getFont(btnFont));
        btnResubmitApplication.setText(bundle.getString("ApplicationForm.btnResubmitApplication.text")); // NOI18N
        btnResubmitApplication.setFocusable(false);
        btnResubmitApplication.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnResubmitApplication.setName("btnResubmitApplication"); // NOI18N
        btnResubmitApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnResubmitApplication);

        btnWithdrawApplication.setAction(actionMap.get("withdrawApplication")); // NOI18N
        btnWithdrawApplication.setFont(UIManager.getFont(btnFont));
        btnWithdrawApplication.setText(bundle.getString("ApplicationForm.btnWithdrawApplication.text")); // NOI18N
        btnWithdrawApplication.setFocusable(false);
        btnWithdrawApplication.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnWithdrawApplication.setName("btnWithdrawApplication"); // NOI18N
        btnWithdrawApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnWithdrawApplication);

        btnLapseApplication.setAction(actionMap.get("lapseApplication")); // NOI18N
        btnLapseApplication.setFont(UIManager.getFont(btnFont));
        btnLapseApplication.setText(bundle.getString("ApplicationForm.btnLapseApplication.text")); // NOI18N
        btnLapseApplication.setFocusable(false);
        btnLapseApplication.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnLapseApplication.setName("btnLapseApplication"); // NOI18N
        btnLapseApplication.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(btnLapseApplication);

        btnRejectApplication.setAction(actionMap.get("rejectApplication")); // NOI18N
        btnRejectApplication.setFont(UIManager.getFont(btnFont));
        btnRejectApplication.setText(bundle.getString("ApplicationForm.btnRejectApplication.text")); // NOI18N
        btnRejectApplication.setName("btnRejectApplication"); // NOI18N
        jToolBar2.add(btnRejectApplication);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 901, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabbedControlMain, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addComponent(btnLodge, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dealingPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dealingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedControlMain, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLodge)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Validates user's data input and calls save operation on the {@link ApplicationBean}.*/
    private void btnLodgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLodgeActionPerformed

        String mandatoryFields = "";
        if ("".equals(txtFirstName.getText()) || (txtFirstName.getText().equals(""))) {
            mandatoryFields = mandatoryFields + "\n" + labName.getText();
        }

        if ("".equals(txtLastName.getText()) || (txtLastName.getText().equals(""))) {
            mandatoryFields = mandatoryFields + "\n" + labLastName.getText();
        }

        if ("".equals(txtAddress.getText()) || (txtAddress.getText().equals(""))) {
            mandatoryFields = mandatoryFields + "\n" + labAddress.getText();
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

                this.dispose();

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
        tabbedControlMain.setSelectedIndex(6);

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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane actionLogPanel;
    private org.sola.clients.swing.ui.source.DocumentPanel addDocumentPanel;
    public org.sola.clients.beans.application.ApplicationBean appBean;
    private org.sola.clients.beans.application.ApplicationDocumentsHelperBean applicationDocumentsHelper;
    private javax.swing.JButton btnAddProperty;
    private javax.swing.JButton btnAddService;
    private javax.swing.JButton btnApproveApplication;
    private javax.swing.JButton btnArchiveApplication;
    private javax.swing.JButton btnCalculateFee;
    private javax.swing.JButton btnCancelService;
    private javax.swing.JButton btnCompleteRevertService;
    private javax.swing.JButton btnDeleteDoc;
    private javax.swing.JButton btnDespatchApplication;
    private javax.swing.JButton btnDownService;
    private javax.swing.JButton btnLapseApplication;
    private javax.swing.JButton btnLodge;
    private javax.swing.JButton btnOpenAttachment;
    private javax.swing.JButton btnPrintFee;
    private javax.swing.JButton btnRejectApplication;
    private javax.swing.JButton btnRemoveProperty;
    private javax.swing.JButton btnRemoveService;
    private javax.swing.JButton btnRequisitionApplication;
    private javax.swing.JButton btnResubmitApplication;
    private javax.swing.JButton btnStartService;
    private javax.swing.JButton btnUPService;
    private javax.swing.JButton btnValidate;
    private javax.swing.JButton btnVerifyProperty;
    private javax.swing.JButton btnViewService;
    private javax.swing.JButton btnWithdrawApplication;
    private javax.swing.JComboBox cbxAgents;
    public javax.swing.JComboBox cbxCommunicationWay;
    private javax.swing.JCheckBox cbxPaid;
    private org.sola.clients.beans.referencedata.CommunicationTypeListBean communicationTypeList;
    public javax.swing.JPanel contactPanel;
    private javax.swing.JPanel dealingPanel;
    public javax.swing.JPanel documentPanel;
    public javax.swing.JPanel feesPanel;
    private javax.swing.JFormattedTextField formTxtFee;
    private javax.swing.JFormattedTextField formTxtPaid;
    private javax.swing.JFormattedTextField formTxtServiceFee;
    private javax.swing.JFormattedTextField formTxtTaxes;
    public javax.swing.JPanel historyPanel;
    public javax.swing.JScrollPane internalServicesScrollPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
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
    private javax.swing.JMenuItem menuCancelService;
    private javax.swing.JMenuItem menuCompleteRevertService;
    private javax.swing.JMenuItem menuMoveServiceDown;
    private javax.swing.JMenuItem menuMoveServiceUp;
    private javax.swing.JMenuItem menuRemoveService;
    private javax.swing.JMenuItem menuStartService;
    private javax.swing.JMenuItem menuViewService;
    private org.sola.clients.beans.party.PartySummaryListBean partySummaryList;
    private javax.swing.JPopupMenu popUpServices;
    public javax.swing.JPanel propertyPanel;
    private javax.swing.JPanel propertypartPanel;
    private org.sola.clients.beans.referencedata.RequestTypeListBean requestTypeList;
    private javax.swing.JScrollPane scrollDocRequired;
    private javax.swing.JScrollPane scrollDocuments;
    private javax.swing.JScrollPane scrollFeeDetails;
    private javax.swing.JScrollPane scrollFeeDetails1;
    private javax.swing.JScrollPane scrollPropertyDetails;
    public javax.swing.JPanel servicesPanel;
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
