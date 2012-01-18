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
package org.sola.clients.swing.desktop.administrative;

import java.awt.BorderLayout;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperPrint;
import org.jdesktop.application.Action;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.clients.swing.desktop.DesktopApplication;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.administrative.BaUnitNotationBean;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.swing.ui.application.ApplicationDocumentsForm;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.swing.gis.ui.controlsbundle.ControlsBundleForBaUnit;
import org.sola.clients.beans.referencedata.RrrTypeActionConstants;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.beans.referencedata.RrrTypeListBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.ui.renderers.LockCellRenderer;
import org.sola.clients.swing.ui.renderers.SimpleComboBoxRenderer;
import org.sola.clients.swing.ui.renderers.TableCellListRenderer;
import org.sola.clients.swing.ui.source.DocumentsPanel;
import org.sola.clients.beans.source.SourceBean;
import org.sola.clients.beans.source.SourceListBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.desktop.ReportViewerForm;
import org.sola.common.RolesConstants;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;

/**
 * This form is used to manage property object ({@codeBaUnit}). 
 * {@link BaUnitBean} is used to bind data on the form.
 */
public class PropertyForm extends javax.swing.JFrame {

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

    /** Creates {@link BaUnitBean} used to bind form components. */
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

    /** Creates documents table to show paper title documents. */
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
     * Form constructor. Creates form in read only mode.
     * @param nameFirstPart First part of the property code.
     * @param nameLastPart Last part of the property code.
     */
    public PropertyForm(String nameFirstPart, String nameLastPart) {
        this(null, null, nameFirstPart, nameLastPart, true);
    }

    /** 
     * Form constructor.
     * @param applicationBean {@link ApplicationBean} instance, used to get data
     * on BaUnit and provide list of documents.
     * @param applicationService {@link ApplicationServiceBean} instance, used 
     * to determine what actions should be taken on this form.
     * @param nameFirstPart First part of the property code.
     * @param nameLastPart Last part of the property code.
     */
    public PropertyForm(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService,
            String nameFirstPart, String nameLastPart, boolean readOnly) {

        this.readOnly = readOnly || !SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE);
        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        this.nameFirstPart = nameFirstPart;
        this.nameLastPart = nameLastPart;

        initComponents();
        customizeForm();
    }

    /** 
     * Form constructor.
     * @param applicationBean {@link ApplicationBean} instance, used to get 
     * list of documents.
     * @param applicationService {@link ApplicationServiceBean} instance, used 
     * to determine what actions should be taken on this form.
     * @param BaUnitBean Instance of {@link BaUnitBean}, used to bind data on the form.
     */
    public PropertyForm(ApplicationBean applicationBean,
            ApplicationServiceBean applicationService, BaUnitBean baUnitBean, boolean readOnly) {
        this.baUnitBean1 = baUnitBean;
        this.readOnly = readOnly || !SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE);
        this.applicationBean = applicationBean;
        this.applicationService = applicationService;
        if (baUnitBean != null) {
            this.nameFirstPart = baUnitBean.getNameFirstpart();
            this.nameLastPart = baUnitBean.getNameLastpart();
        }
        initComponents();
        customizeForm();
    }

    /** Applies customization of component L&F. */
    private void customizeComponents() {

//    BUTTONS   
        LafManager.getInstance().setBtnProperties(btnAddNotation);
        LafManager.getInstance().setBtnProperties(btnAddParcel);
        LafManager.getInstance().setBtnProperties(btnChangeRight);
        LafManager.getInstance().setBtnProperties(btnCreateRight);
        LafManager.getInstance().setBtnProperties(btnEditRight);
        LafManager.getInstance().setBtnProperties(btnExtinguish);
        LafManager.getInstance().setBtnProperties(btnLinkPaperTitle);
        LafManager.getInstance().setBtnProperties(btnRemoveNotation);
        LafManager.getInstance().setBtnProperties(btnRemoveParcel);
        LafManager.getInstance().setBtnProperties(btnRemoveRight);
        LafManager.getInstance().setBtnProperties(btnSave);
        LafManager.getInstance().setBtnProperties(btnViewPaperTitle);

//    COMBOBOXES
        LafManager.getInstance().setCmbProperties(cbxRightType);


//    LABELS    
        LafManager.getInstance().setLabProperties(jLabel1);
        LafManager.getInstance().setLabProperties(jLabel15);
        LafManager.getInstance().setLabProperties(jLabel16);
        LafManager.getInstance().setLabProperties(jLabel2);
        LafManager.getInstance().setLabProperties(jLabel4);
        LafManager.getInstance().setLabProperties(jLabel5);

//    TXT FIELDS
        LafManager.getInstance().setTxtProperties(txtEstateType);
        LafManager.getInstance().setTxtProperties(txtFirstPart);
        LafManager.getInstance().setTxtProperties(txtLastPart);
        LafManager.getInstance().setTxtProperties(txtName);
        LafManager.getInstance().setTxtProperties(txtNotationText);

//    TABBED PANELS
        LafManager.getInstance().setTabProperties(jTabbedPane1);

    }

    /** 
     * Runs form customization, to restrict certain actions, bind listeners on 
     * the {@link BaUnitBean} and other components.
     */
    private void customizeForm() {
        customizeComponents();
        this.setIconImage(new ImageIcon(PropertyForm.class.getResource("/images/sola/logo_icon.jpg")).getImage());

        if (applicationBean != null && applicationService != null) {
            this.setTitle(String.format("Service: %s  Application: #%s",
                    applicationService.getRequestType().getDisplayValue(),
                    applicationBean.getNr()));
        }

        if (nameFirstPart != null && nameLastPart != null) {
            this.setTitle(String.format("%s Property: %s ", this.getTitle(),
                    nameFirstPart.concat(nameLastPart)));
        }

        btnSave.setEnabled(!readOnly);
        customizeRightsButtons(null);
        customizeNotationButtons(null);
        customizeRightTypesList();
        customizeParcelButtons(null);
        customizePaperTitleButtons(null);
        customizePrintButton();

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
    }

    /** Enables print button if row version of {@link BaUnitBean} > 0 . */
    private void customizePrintButton() {
        btnPrintBaUnit.getAction().setEnabled(baUnitBean1.getRowVersion() > 0);
    }

    /** Enables or disables paper title buttons, depending on the form state. */
    private void customizePaperTitleButtons(SourceBean source) {
        if (source != null && source.getArchiveDocument() != null) {
            btnViewPaperTitle.getAction().setEnabled(true);
        } else {
            btnViewPaperTitle.getAction().setEnabled(false);
        }
        btnLinkPaperTitle.getAction().setEnabled(!readOnly);
    }

    /** Enables or disables notation buttons, depending on the form state. */
    private void customizeNotationButtons(BaUnitNotationBean notation) {
        if (notation == null || !notation.getStatusCode().equals(StatusConstants.PENDING)
                || notation.getBaUnitId() == null
                || !notation.getBaUnitId().equals(baUnitBean1.getId())
                || notation.isLocked() || readOnly) {
            btnRemoveNotation.getAction().setEnabled(false);
        } else {
            btnRemoveNotation.getAction().setEnabled(true);
        }
        btnAddNotation.getAction().setEnabled(!readOnly);
    }

    /** 
     * Enables or disables parcel buttons, depending on the form state and 
     * selection in the list of parcel. 
     */
    private void customizeParcelButtons(CadastreObjectBean cadastreBean) {
        if (cadastreBean == null || !cadastreBean.getStatusCode().equals(StatusConstants.PENDING)
                || cadastreBean.isLocked() || readOnly) {
            btnRemoveParcel.getAction().setEnabled(false);
        } else {
            btnRemoveParcel.getAction().setEnabled(true);
        }
        btnAddParcel.getAction().setEnabled(!readOnly);
    }

    /** Enables or disables combobox list of right types, depending on the form state.*/
    private void customizeRightTypesList() {
        cbxRightType.setSelectedIndex(-1);
        rrrTypes.setSelectedRrrType(null);

        if (!readOnly && isActionAllowed(RrrTypeActionConstants.NEW)) {
            cbxRightType.setEnabled(true);

            // Restrict selection of right type by application service
            if (applicationService != null && applicationService.getRequestType() != null) {
                rrrTypes.setSelectedRightByCode(applicationService.getRequestType().getRrrTypeCode());
                if (rrrTypes.getSelectedRrrType() != null) {
                    cbxRightType.setEnabled(false);
                }
            }
        } else {
            cbxRightType.setEnabled(false);
            customizeCreateRightButton(rrrTypes.getSelectedRrrType());
        }
    }

    /** 
     * Enables or disables button for creating new right, depending on 
     * the form state. 
     */
    private void customizeCreateRightButton(RrrTypeBean rrrTypeBean) {
        if (rrrTypeBean != null && !readOnly && isActionAllowed(RrrTypeActionConstants.NEW)) {
            btnCreateRight.getAction().setEnabled(true);
        } else {
            btnCreateRight.getAction().setEnabled(false);
        }
    }

    /** 
     * Enables or disables buttons for managing list of rights, depending on the 
     * form state, selected right and it's state. 
     */
    private void customizeRightsButtons(RrrBean rrrBean) {
        btnEditRight.getAction().setEnabled(false);
        btnRemoveRight.getAction().setEnabled(false);
        btnChangeRight.getAction().setEnabled(false);
        btnExtinguish.getAction().setEnabled(false);

        if (rrrBean != null && !rrrBean.isLocked() && !readOnly) {
            boolean isPending = rrrBean.getStatusCode().equals(StatusConstants.PENDING);

            // Control pending state and allowed types of RRR for edit/remove buttons
            if (isPending && isRightTypeAllowed(rrrBean.getTypeCode())) {
                btnEditRight.getAction().setEnabled(true);
                btnRemoveRight.getAction().setEnabled(true);
            }

            // Control the record state, duplication of pending records,
            // allowed action and allowed type of RRR.
            if (!isPending && !baUnitBean1.isPendingRrrExists(rrrBean)
                    && isRightTypeAllowed(rrrBean.getTypeCode())) {
                if (isActionAllowed(RrrTypeActionConstants.VARY)) {
                    btnChangeRight.getAction().setEnabled(true);
                }
                if (isActionAllowed(RrrTypeActionConstants.CANCEL)) {
                    btnExtinguish.getAction().setEnabled(true);
                }
            }
        }
    }

    /** Checks if certain action is allowed on the form. */
    private boolean isActionAllowed(String action) {
        boolean result = true;
        if (applicationService != null && applicationService.getRequestType() != null
                && applicationService.getRequestType().getRrrTypeActionCode() != null) {
            result = applicationService.getRequestType().getRrrTypeActionCode().equalsIgnoreCase(action);
        }
        return result;
    }

    /** Checks what type of rights are allowed to create/manage on the form. */
    private boolean isRightTypeAllowed(String rrrTypeCode) {
        boolean result = true;
        if (rrrTypeCode != null && applicationService != null
                && applicationService.getRequestType() != null
                && applicationService.getRequestType().getRrrTypeCode() != null) {
            result = applicationService.getRequestType().getRrrTypeCode().equalsIgnoreCase(rrrTypeCode);
        }
        return result;
    }

    /** Returns {@link BaUnitBean} by first and last name part. */
    private BaUnitBean getBaUnit(String nameFirstPart, String nameLastPart) {
        BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().GetBaUnitByCode(nameFirstPart, nameLastPart);
        return TypeConverters.TransferObjectToBean(baUnitTO, BaUnitBean.class, null);
    }

    /** Opens {@link ReportViewerForm} to display report.*/
    private void showReport(JasperPrint report) {
        ReportViewerForm form = new ReportViewerForm(report);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }

    /** Open form to add new parcel. */
    @Action
    public void addParcel() {

        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getNewValue() != null) {
                    baUnitBean1.getCadastreObjectList().addAsNew((CadastreObjectBean) e.getNewValue());
                }
            }
        };

        CreateParcelForm parcelForm = new CreateParcelForm(this, true);

        parcelForm.setLocationRelativeTo(this);
        parcelForm.addPropertyChangeListener(CreateParcelForm.SELECTED_PARCEL, listener);
        parcelForm.setVisible(true);
        parcelForm.removePropertyChangeListener(CreateParcelForm.SELECTED_PARCEL, listener);

    }

    /** Removes selected parcel from the list of parcels. */
    @Action
    public void removeParcel() {
        if (baUnitBean1.getSelectedParcel() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            baUnitBean1.removeSelectedParcel();
        }
    }

    /** Removes selected right from the list of rights. */
    @Action
    public void removeRight() {
        if (baUnitBean1.getSelectedRight() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            baUnitBean1.removeSelectedRight();
        }
    }

    /** Opens appropriate right form for editing. */
    @Action
    public void editRight() {

        if (baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.EDIT);
        }
    }

    /** Opens appropriate right form to extinguish selected right. */
    @Action
    public void extinguishRight() {

        if (baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.CANCEL);
        }
    }

    /** Opens appropriate right form to create new right. */
    @Action
    public void createRight() {
        openRightForm(null, RrrBean.RRR_ACTION.NEW);
    }

    /** Opens appropriate right form to vary selected right. */
    @Action
    public void varyRight() {
        if (baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.VARY);
        }
    }

    /** Adds new notation on the BaUnit. */
    @Action
    public void addNotation() {
        if (baUnitBean1.addBaUnitNotation(txtNotationText.getText())) {
            txtNotationText.setText(null);
        }
    }

    /** Removes selected notation. */
    @Action
    public void removeNotation() {
        if (MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            baUnitBean1.removeSelectedBaUnitNotation();
        }
    }

    /** Opens paper title attachment. */
    @Action
    public void viewDocument() {

        if (documentsPanel1.getSourceListBean().getSelectedSource() != null) {
            documentsPanel1.getSourceListBean().getSelectedSource().openDocument();
        }
    }

    /** Prints BA unit certificate. */
    @Action
    public void print() {
        showReport(ReportManager.getBaUnitReport(getBaUnit(
                baUnitBean1.getNameFirstpart(), baUnitBean1.getNameLastpart())));
    }

    /** Links document as a paper title on the BaUnit object. */
    @Action
    public void linkDocument() {
        openDocumentsForm();
    }

    /** 
     * Opens right form, depending on given {@link RrrBean} and action. 
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
        Window form = null;

        if (rrrBean.getRrrType().getCode().equals("mortgage")) {
            form = new MortgageForm(this, true, rrrBean, applicationBean, applicationService, action);
        } else if (rrrBean.getRrrType().getCode().toLowerCase().contains("ownership")) {
            form = new OwnershipForm(this, true, rrrBean, applicationBean, applicationService, action);
        } else {
            form = new SimpleRightForm(this, true, rrrBean, applicationBean, applicationService, action);
        }

        form.addPropertyChangeListener(SimpleRightForm.UPDATED_RRR, rightFormListener);
        form.setVisible(true);
    }

    /** Opens form to select or create document to be used as a paper title document. */
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
        DesktopApplication.getApplication().show(applicationDocumentsForm);
        applicationDocumentsForm.removePropertyChangeListener(
                SourceListBean.SELECTED_SOURCE_PROPERTY, listener);
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
        menuEditRight = new javax.swing.JMenuItem();
        menuRemoveRight = new javax.swing.JMenuItem();
        popupNotations = new javax.swing.JPopupMenu();
        menuRemoveNotation = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
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
        mapPanel = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFirstPart = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtLastPart = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtEstateType = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        documentsPanel1 = createDocumentsPanel();
        jToolBar4 = new javax.swing.JToolBar();
        btnViewPaperTitle = new javax.swing.JButton();
        btnLinkPaperTitle = new javax.swing.JButton();
        jToolBar5 = new javax.swing.JToolBar();
        btnPrintBaUnit = new javax.swing.JButton();

        popupParcels.setName("popupParcels"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(PropertyForm.class, this);
        menuAddParcel.setAction(actionMap.get("addParcel")); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        menuAddParcel.setText(bundle.getString("PropertyForm.menuAddParcel.text")); // NOI18N
        menuAddParcel.setName("menuAddParcel"); // NOI18N
        popupParcels.add(menuAddParcel);

        menuRemoveParcel.setAction(actionMap.get("removeParcel")); // NOI18N
        menuRemoveParcel.setText(bundle.getString("PropertyForm.menuRemoveParcel.text")); // NOI18N
        menuRemoveParcel.setName("menuRemoveParcel"); // NOI18N
        popupParcels.add(menuRemoveParcel);

        popupRights.setName("popupRights"); // NOI18N

        menuVaryRight.setAction(actionMap.get("varyRight")); // NOI18N
        menuVaryRight.setText(bundle.getString("PropertyForm.menuVaryRight.text")); // NOI18N
        menuVaryRight.setName("menuVaryRight"); // NOI18N
        popupRights.add(menuVaryRight);

        menuExtinguishRight.setAction(actionMap.get("extinguishRight")); // NOI18N
        menuExtinguishRight.setText(bundle.getString("PropertyForm.menuExtinguishRight.text")); // NOI18N
        menuExtinguishRight.setName("menuExtinguishRight"); // NOI18N
        popupRights.add(menuExtinguishRight);

        menuEditRight.setAction(actionMap.get("editRight")); // NOI18N
        menuEditRight.setText(bundle.getString("PropertyForm.menuEditRight.text")); // NOI18N
        menuEditRight.setName("menuEditRight"); // NOI18N
        popupRights.add(menuEditRight);

        menuRemoveRight.setAction(actionMap.get("removeRight")); // NOI18N
        menuRemoveRight.setText(bundle.getString("PropertyForm.menuRemoveRight.text")); // NOI18N
        menuRemoveRight.setName("menuRemoveRight"); // NOI18N
        popupRights.add(menuRemoveRight);

        popupNotations.setName("popupNotations"); // NOI18N

        menuRemoveNotation.setAction(actionMap.get("removeNotation")); // NOI18N
        menuRemoveNotation.setText(bundle.getString("PropertyForm.menuRemoveNotation.text")); // NOI18N
        menuRemoveNotation.setName("menuRemoveNotation"); // NOI18N
        popupNotations.add(menuRemoveNotation);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

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
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${selectedParcel}"), tableParcels, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableParcels);
        tableParcels.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyForm.tableParcels.columnModel.title0")); // NOI18N
        tableParcels.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyForm.tableParcels.columnModel.title1")); // NOI18N
        tableParcels.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyForm.tableParcels.columnModel.title3")); // NOI18N
        tableParcels.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyForm.tableParcels.columnModel.title2")); // NOI18N
        tableParcels.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("PropertyForm.tableParcels.columnModel.title4")); // NOI18N
        tableParcels.getColumnModel().getColumn(5).setPreferredWidth(40);
        tableParcels.getColumnModel().getColumn(5).setMaxWidth(40);
        tableParcels.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("PropertyForm.tableParcels.columnModel.title5")); // NOI18N
        tableParcels.getColumnModel().getColumn(5).setCellRenderer(new LockCellRenderer());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnAddParcel.setAction(actionMap.get("addParcel")); // NOI18N
        btnAddParcel.setName("btnAddParcel"); // NOI18N
        jToolBar1.add(btnAddParcel);

        btnRemoveParcel.setAction(actionMap.get("removeParcel")); // NOI18N
        btnRemoveParcel.setName("btnRemoveParcel"); // NOI18N
        jToolBar1.add(btnRemoveParcel);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                        .add(20, 20, 20))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("PropertyForm.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

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
        tableRights.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyForm.tableRights.columnModel.title0")); // NOI18N
        tableRights.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyForm.tableRights.columnModel.title1")); // NOI18N
        tableRights.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyForm.tableRights.columnModel.title2")); // NOI18N
        tableRights.getColumnModel().getColumn(3).setPreferredWidth(40);
        tableRights.getColumnModel().getColumn(3).setMaxWidth(40);
        tableRights.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyForm.tableRights.columnModel.title3")); // NOI18N
        tableRights.getColumnModel().getColumn(3).setCellRenderer(new LockCellRenderer());

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        jLabel16.setText(bundle.getString("PropertyForm.jLabel16.text")); // NOI18N
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

        btnCreateRight.setAction(actionMap.get("createRight")); // NOI18N
        btnCreateRight.setName("btnCreateRight"); // NOI18N
        jToolBar2.add(btnCreateRight);

        btnChangeRight.setAction(actionMap.get("varyRight")); // NOI18N
        btnChangeRight.setName("btnChangeRight"); // NOI18N
        jToolBar2.add(btnChangeRight);

        btnExtinguish.setAction(actionMap.get("extinguishRight")); // NOI18N
        btnExtinguish.setName("btnExtinguish"); // NOI18N
        jToolBar2.add(btnExtinguish);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar2.add(jSeparator1);

        btnEditRight.setAction(actionMap.get("editRight")); // NOI18N
        btnEditRight.setName("btnEditRight"); // NOI18N
        jToolBar2.add(btnEditRight);

        btnRemoveRight.setAction(actionMap.get("removeRight")); // NOI18N
        btnRemoveRight.setName("btnRemoveRight"); // NOI18N
        jToolBar2.add(btnRemoveRight);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jToolBar2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jToolBar2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("PropertyForm.jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

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
        tableOwnership.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyForm.tableOwnership.columnModel.title0")); // NOI18N
        tableOwnership.getColumnModel().getColumn(0).setCellRenderer(new TableCellListRenderer("getName", "getLastName"));
        tableOwnership.getColumnModel().getColumn(1).setPreferredWidth(70);
        tableOwnership.getColumnModel().getColumn(1).setMaxWidth(70);
        tableOwnership.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyForm.tableOwnership.columnModel.title1")); // NOI18N
        tableOwnership.getColumnModel().getColumn(2).setPreferredWidth(130);
        tableOwnership.getColumnModel().getColumn(2).setMaxWidth(130);
        tableOwnership.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyForm.tableOwnership.columnModel.title2")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("PropertyForm.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

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
        tableNotations.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("PropertyForm.tableNotations.columnModel.title0")); // NOI18N
        tableNotations.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("PropertyForm.tableNotations.columnModel.title1")); // NOI18N
        tableNotations.getColumnModel().getColumn(2).setPreferredWidth(180);
        tableNotations.getColumnModel().getColumn(2).setMaxWidth(180);
        tableNotations.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("PropertyForm.tableNotations.columnModel.title2")); // NOI18N
        tableNotations.getColumnModel().getColumn(3).setPreferredWidth(40);
        tableNotations.getColumnModel().getColumn(3).setMaxWidth(40);
        tableNotations.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("PropertyForm.tableNotations.columnModel.title3")); // NOI18N
        tableNotations.getColumnModel().getColumn(3).setCellRenderer(new LockCellRenderer());

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        filler4.setName("filler4"); // NOI18N
        jToolBar3.add(filler4);

        btnAddNotation.setAction(actionMap.get("addNotation")); // NOI18N
        btnAddNotation.setName("btnAddNotation"); // NOI18N
        jToolBar3.add(btnAddNotation);

        btnRemoveNotation.setAction(actionMap.get("removeNotation")); // NOI18N
        btnRemoveNotation.setName("btnRemoveNotation"); // NOI18N
        jToolBar3.add(btnRemoveNotation);

        txtNotationText.setMinimumSize(new java.awt.Dimension(150, 20));
        txtNotationText.setName("txtNotationText"); // NOI18N

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel15.setText(bundle.getString("PropertyForm.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel15)
                        .add(9, 9, 9)
                        .add(txtNotationText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
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
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("PropertyForm.jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        mapPanel.setName("mapPanel"); // NOI18N

        org.jdesktop.layout.GroupLayout mapPanelLayout = new org.jdesktop.layout.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 716, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 231, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(bundle.getString("PropertyForm.mapPanel.TabConstraints.tabTitle"), mapPanel); // NOI18N

        btnSave.setText(bundle.getString("PropertyForm.btnSave.text")); // NOI18N
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        jPanel8.setName("jPanel8"); // NOI18N

        jLabel1.setText(bundle.getString("PropertyForm.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtFirstPart.setEnabled(false);
        txtFirstPart.setName("txtFirstPart"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${nameFirstpart}"), txtFirstPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel2.setText(bundle.getString("PropertyForm.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtLastPart.setEnabled(false);
        txtLastPart.setName("txtLastPart"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${nameLastpart}"), txtLastPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel4.setText(bundle.getString("PropertyForm.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(bundle.getString("PropertyForm.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txtEstateType.setEditable(false);
        txtEstateType.setEnabled(false);
        txtEstateType.setName("txtEstateType"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${estateType}"), txtEstateType, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtName.setName("txtName"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitBean1, org.jdesktop.beansbinding.ELProperty.create("${name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel1)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(txtFirstPart)
                    .add(txtLastPart, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4))
                .add(10, 10, 10)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                    .add(txtEstateType, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtEstateType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel4))
                        .add(18, 18, 18)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel5)
                            .add(txtName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtFirstPart)
                            .add(jLabel1))
                        .add(18, 18, 18)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtLastPart, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel2))))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("PropertyForm.jPanel5.border.title"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        documentsPanel1.setName("documentsPanel1"); // NOI18N

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);
        jToolBar4.setName("jToolBar4"); // NOI18N

        btnViewPaperTitle.setAction(actionMap.get("viewDocument")); // NOI18N
        btnViewPaperTitle.setName("btnViewPaperTitle"); // NOI18N
        jToolBar4.add(btnViewPaperTitle);

        btnLinkPaperTitle.setAction(actionMap.get("linkDocument")); // NOI18N
        btnLinkPaperTitle.setName("btnLinkPaperTitle"); // NOI18N
        jToolBar4.add(btnLinkPaperTitle);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, documentsPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jToolBar4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jToolBar4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(documentsPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);
        jToolBar5.setName("jToolBar5"); // NOI18N

        btnPrintBaUnit.setAction(actionMap.get("print")); // NOI18N
        btnPrintBaUnit.setFont(new java.awt.Font("Tahoma", 0, 12));
        btnPrintBaUnit.setText(bundle.getString("PropertyForm.btnPrintBaUnit.text")); // NOI18N
        btnPrintBaUnit.setFocusable(false);
        btnPrintBaUnit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPrintBaUnit.setName("btnPrintBaUnit"); // NOI18N
        btnPrintBaUnit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar5.add(btnPrintBaUnit);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, btnSave, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
                    .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .add(jToolBar5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jToolBar5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnSave)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
    boolean result = false;
    if (this.baUnitID != null && !this.baUnitID.equals("")) {
        result = baUnitBean1.saveBaUnit(applicationService.getId());
    } else {
        result = baUnitBean1.createBaUnit(applicationService.getId());
    }
    if (result) {
        JOptionPane.showMessageDialog(this, "Changes successfully saved.");
        this.dispose();
    } else {
        JOptionPane.showMessageDialog(this, "Failed to save application.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}//GEN-LAST:event_btnSaveActionPerformed

    private void tableRightsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableRightsMouseClicked
        if (evt.getClickCount() > 1 && baUnitBean1.getSelectedRight() != null) {
            openRightForm(baUnitBean1.getSelectedRight(), RrrBean.RRR_ACTION.VIEW);
        }
    }//GEN-LAST:event_tableRightsMouseClicked

private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
// TODO add your handling code here:
    if (this.mapControl == null) {
        this.mapControl = new ControlsBundleForBaUnit();
        this.mapControl.setCadastreObjects(baUnitBean1.getId());
        if (applicationBean != null) {
            this.mapControl.setApplicationId(this.applicationBean.getId());
        }
        this.mapPanel.setLayout(new BorderLayout());
        this.mapPanel.add(this.mapControl, BorderLayout.CENTER);
    }
}//GEN-LAST:event_formComponentShown
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.administrative.BaUnitBean baUnitBean1;
    private org.sola.clients.beans.referencedata.RrrTypeListBean baUnitRrrTypes;
    private javax.swing.JButton btnAddNotation;
    private javax.swing.JButton btnAddParcel;
    private javax.swing.JButton btnChangeRight;
    private javax.swing.JButton btnCreateRight;
    private javax.swing.JButton btnEditRight;
    private javax.swing.JButton btnExtinguish;
    private javax.swing.JButton btnLinkPaperTitle;
    private javax.swing.JButton btnPrintBaUnit;
    private javax.swing.JButton btnRemoveNotation;
    private javax.swing.JButton btnRemoveParcel;
    private javax.swing.JButton btnRemoveRight;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnViewPaperTitle;
    private javax.swing.JComboBox cbxRightType;
    private org.sola.clients.swing.ui.source.DocumentsPanel documentsPanel1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JMenuItem menuAddParcel;
    private javax.swing.JMenuItem menuEditRight;
    private javax.swing.JMenuItem menuExtinguishRight;
    private javax.swing.JMenuItem menuRemoveNotation;
    private javax.swing.JMenuItem menuRemoveParcel;
    private javax.swing.JMenuItem menuRemoveRight;
    private javax.swing.JMenuItem menuVaryRight;
    private javax.swing.JPopupMenu popupNotations;
    private javax.swing.JPopupMenu popupParcels;
    private javax.swing.JPopupMenu popupRights;
    private org.sola.clients.beans.referencedata.RrrTypeListBean rrrTypes;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableNotations;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableOwnership;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableParcels;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRights;
    private javax.swing.JTextField txtEstateType;
    private javax.swing.JTextField txtFirstPart;
    private javax.swing.JTextField txtLastPart;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNotationText;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
