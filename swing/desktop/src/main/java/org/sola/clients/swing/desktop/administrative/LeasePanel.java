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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.validation.groups.Default;
import net.sf.jasperreports.engine.JasperPrint;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.administrative.LeaseConditionForRrrBean;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.RrrReportBean;
import org.sola.clients.beans.administrative.validation.LeaseValidationGroup;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.LeaseConditionListBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.ReportViewerForm;
import org.sola.clients.swing.desktop.party.PartyPanelForm;
import org.sola.clients.swing.desktop.party.PartySearchPanelForm;
import org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.ui.renderers.BooleanCellRenderer2;
import org.sola.clients.swing.ui.renderers.FormattersFactory;
import org.sola.clients.swing.ui.reports.FreeTextDialog;
import org.sola.common.StringUtility;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Form for managing simple ownership right. {@link RrrBean} is used to bind the data on the form.
 */
public class LeasePanel extends ContentPanel {

    private ApplicationBean applicationBean;
    private ApplicationServiceBean appService;
    private RrrBean.RRR_ACTION rrrAction;
    private BaUnitBean baUnit;
    public static final String UPDATED_RRR = "updatedRRR";
    
    private DocumentsManagementExtPanel createDocumentsPanel() {
        if (rrrBean == null) {
            rrrBean = new RrrBean();
        }
        if (applicationBean == null) {
            applicationBean = new ApplicationBean();
        }

        boolean allowEdit = true;
        if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            allowEdit = false;
        }

        DocumentsManagementExtPanel panel = new DocumentsManagementExtPanel(
                rrrBean.getSourceList(), applicationBean, allowEdit);
        return panel;
    }

    private RrrBean CreateRrrBean() {
        if (rrrBean == null) {
            rrrBean = new RrrBean();
        }
        return rrrBean;
    }
    
    /**
     * Creates new form SimpleOwhershipPanel
     */
    public LeasePanel(BaUnitBean baUnit, RrrBean rrrBean, ApplicationBean applicationBean, 
            ApplicationServiceBean applicationService, RrrBean.RRR_ACTION rrrAction) {
        this.baUnit = baUnit;
        this.applicationBean = applicationBean;
        this.appService = applicationService;
        this.rrrAction = rrrAction;
        prepareRrrBean(rrrBean, rrrAction);
        initComponents();
        postInit();
    }
    
    private void postInit(){
        // Populate lease conditions list with standard conditions for new RrrBean
        if(rrrAction == RrrBean.RRR_ACTION.NEW){
            rrrBean.addLeaseConditions(leaseConditions.getLeaseConditionList());
        }
        
        leaseConditions.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(LeaseConditionListBean.SELECTED_LEASE_CONDITION_PROPERTY)){
                    customizeAddStandardConditionButton();
                }
            }
        });
        
        rrrBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(RrrBean.SELECTED_LEASE_CONDITION_PROPERTY)){
                    customizeLeaseConditionsButtons();
                }
            }
        });
        
        customizeForm();
        customizeOwnerButtons(null);
        customizeAddStandardConditionButton();
        customizeLeaseConditionsButtons();
        saveRrrState();
    }

    private void customizeLeaseConditionsButtons(){
        boolean enabled = rrrBean.getSelectedLeaseCondition()!=null && rrrAction != RrrBean.RRR_ACTION.VIEW;
        
        btnRemoveCondition.setEnabled(enabled);
        if(enabled){
            btnEditCondition.setEnabled(rrrBean.getSelectedLeaseCondition().isCustomCondition());
        } else {
            btnEditCondition.setEnabled(enabled);
        }
        menuEditCondition.setEnabled(btnEditCondition.isEnabled());
        menuRemoveCondition.setEnabled(btnRemoveCondition.isEnabled());
    }
    
    private void customizeAddStandardConditionButton(){
        if(rrrAction != RrrBean.RRR_ACTION.VIEW){
            return;
        }
        btnAddStandardCondition.setEnabled(leaseConditions.getSelectedLeaseCondition()!=null);
    }
    
    private void customizeForm() {
        headerPanel.setTitleText(rrrBean.getRrrType().getDisplayValue());
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                            ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
        }
        if (rrrAction == RrrBean.RRR_ACTION.CANCEL) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                            ClientMessage.GENERAL_LABELS_TERMINATE_AND_CLOSE).getMessage());
        }

        if (rrrAction != RrrBean.RRR_ACTION.EDIT && rrrAction != RrrBean.RRR_ACTION.VIEW
                && appService != null) {
            // Set default noation text from the selected application service
            txtNotationText.setText(appService.getRequestType().getNotationTemplate());
        }

        boolean enabled = rrrAction != RrrBean.RRR_ACTION.VIEW;

        btnSave.setEnabled(enabled);
        txtNotationText.setEnabled(enabled);
        txtRegDatetime.setEditable(enabled);
        txtNotationText.setEditable(enabled);
        cbxIsPrimary.setEnabled(enabled);
        txtExpirationDate.setEnabled(enabled);
        txtRent.setEnabled(enabled);
        txtDueDate.setEnabled(enabled);
        btnPrintDraftLease.setEnabled(enabled);
        btnPrintDraftOffer.setEnabled(enabled);
        btnPrintLease.setEnabled(enabled);
        btnPrintOffer.setEnabled(enabled);
        btnPrintRejection.setEnabled(enabled);
        btnAddCustomCondition.setEnabled(enabled);
        btnAddStandardCondition.setEnabled(enabled);
        cbxStandardConditions.setEnabled(enabled);
        menuAddCustomCondition.setEnabled(btnAddCustomCondition.isEnabled());
    }
    
    private void prepareRrrBean(RrrBean rrrBean, RrrBean.RRR_ACTION rrrAction) {
        if (rrrBean == null) {
            this.rrrBean = new RrrBean();
            this.rrrBean.setStatusCode(StatusConstants.PENDING);
        } else {
            this.rrrBean = rrrBean.makeCopyByAction(rrrAction);
        }
        this.rrrBean.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(RrrBean.SELECTED_RIGHTHOLDER_PROPERTY)) {
                    customizeOwnerButtons((PartySummaryBean) evt.getNewValue());
                }
            }
        });
    }
    
    private void customizeOwnerButtons(PartySummaryBean owner) {
        boolean isChangesAllowed = false;
        if (rrrAction == RrrBean.RRR_ACTION.VARY || rrrAction == RrrBean.RRR_ACTION.EDIT
                || rrrAction == RrrBean.RRR_ACTION.NEW) {
            isChangesAllowed = true;
        }

        btnAddOwner.setEnabled(isChangesAllowed);
        btnSelectExisting.setEnabled(isChangesAllowed);
        
        if (owner == null) {
            btnRemoveOwner.setEnabled(false);
            btnEditOwner.setEnabled(false);
            btnViewOwner.setEnabled(false);
        } else {
            btnRemoveOwner.setEnabled(isChangesAllowed);
            btnEditOwner.setEnabled(isChangesAllowed);
            btnViewOwner.setEnabled(true);
        }
        
        menuAddOwner.setEnabled(btnAddOwner.isEnabled());
        menuRemoveOwner.setEnabled(btnRemoveOwner.isEnabled());
        menuEditOwner.setEnabled(btnEditOwner.isEnabled());
        menuViewOwner.setEnabled(btnViewOwner.isEnabled());
    }
    
    private boolean saveRrr() {  
        if (rrrBean.validate(true, Default.class, LeaseValidationGroup.class).size() < 1) {
            firePropertyChange(UPDATED_RRR, null, rrrBean);
            close();
            return true;
        }
        return false;
    }
    
    private void saveRrrState() {
        MainForm.saveBeanState(rrrBean);
    }

    @Override
    protected boolean panelClosing() {
        if (btnSave.isEnabled() && MainForm.checkSaveBeforeClose(rrrBean)) {
            return saveRrr();
        }
        return true;
    }
    
    private void viewOwner() {
        if (rrrBean.getSelectedRightHolder() != null) {
            openRightHolderForm(rrrBean.getSelectedRightHolder(), true);
        }
    }

    private void removeOwner() {
        if (rrrBean.getSelectedRightHolder() != null
                && MessageUtility.displayMessage(ClientMessage.CONFIRM_DELETE_RECORD) == MessageUtility.BUTTON_ONE) {
            rrrBean.removeSelectedRightHolder();
        }
    }

    private void addOwner() {
        openRightHolderForm(null, false);
    }

    private void editOwner() {
        if (rrrBean.getSelectedRightHolder() != null) {
            openRightHolderForm(rrrBean.getSelectedRightHolder(), false);
        }
    }
    
    private class RightHolderFormListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(PartyPanelForm.PARTY_SAVED)) {
                rrrBean.addOrUpdateRightholder((PartySummaryBean) ((PartyPanelForm) evt.getSource()).getParty());
                tableOwners.clearSelection();
            }
        }
    }
    
    private void openRightHolderForm(final PartySummaryBean partySummaryBean, final boolean isReadOnly) {
        final RightHolderFormListener listener = new RightHolderFormListener();

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
                PartyPanelForm partyForm;

                if (partySummaryBean != null) {
                    partyForm = new PartyPanelForm(true, partySummaryBean, isReadOnly, true);
                } else {
                    partyForm = new PartyPanelForm(true, null, isReadOnly, true);
                }
                partyForm.addPropertyChangeListener(listener);
                getMainContentPanel().addPanel(partyForm, MainContentPanel.CARD_PERSON, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }
    
    
     private void openSelectRightHolderForm() {
        final RightHolderFormListener listener = new RightHolderFormListener();

        SolaTask t = new SolaTask<Void, Void>() {

            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_OPEN_PERSON));
                PartySearchPanelForm partySearchForm = null;

                partySearchForm = initializePartySearchForm(partySearchForm);

                partySearchForm.addPropertyChangeListener(listener);
                getMainContentPanel().addPanel(partySearchForm, MainContentPanel.CARD_SEARCH_PERSONS, true);
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private PartySearchPanelForm initializePartySearchForm(PartySearchPanelForm partySearchForm) {
        partySearchForm = new PartySearchPanelForm(true, this.rrrBean);
        return partySearchForm;

    }

    private void addCustomCondition(){
        CustomLeaseConditionDialog form = new CustomLeaseConditionDialog(null, MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        
        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(CustomLeaseConditionDialog.LEASE_CONDITION_SAVED)){
                    rrrBean.addLeaseCondition((LeaseConditionForRrrBean)evt.getNewValue());
                }
            }
        });
        form.setVisible(true);
    }
    
    private void editCustomCondition(){
        CustomLeaseConditionDialog form = new CustomLeaseConditionDialog(
                (LeaseConditionForRrrBean)rrrBean.getSelectedLeaseCondition().copy(), 
                MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
        
        form.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals(CustomLeaseConditionDialog.LEASE_CONDITION_SAVED)){
                    LeaseConditionForRrrBean cond = (LeaseConditionForRrrBean)evt.getNewValue();
                    rrrBean.getSelectedLeaseCondition().setCustomConditionText(cond.getCustomConditionText());
                }
            }
        });
        form.setVisible(true);
    }
    
    private void addStandardCondition(){
        rrrBean.addLeaseCondition(leaseConditions.getSelectedLeaseCondition());
    }
    
    private void removeCondition(){
        rrrBean.removeSelectedLeaseCondition();
    }
    
    private RrrReportBean prepareReportBean(){
        RrrReportBean reportBean = new RrrReportBean(baUnit, rrrBean, applicationBean, appService);
        String warnings = "";
        String warning;
        
        if(applicationBean == null || applicationBean.isNew()){
            warnings = warnings + MessageUtility.getLocalizedMessageText(ClientMessage.APPLICATION_NOT_FOUND);
        }
        
        if(reportBean.getRrrRegNumber().isEmpty()){
            warning = MessageUtility.getLocalizedMessageText(
                        ClientMessage.BAUNIT_RRR_NO_REGISTRATION_NUMBER,
                        new Object[]{rrrBean.getRrrType().getDisplayValue()});
            if(warnings.isEmpty()){
                warnings = "- " + warning;
            } else {
                warnings = warnings + "\n- " + warning;
            }
        }

        if(reportBean.getBaUnit().getCadastreObjectFilteredList().size() < 1){
            warning = MessageUtility.getLocalizedMessageText(ClientMessage.BAUNIT_HAS_NO_PARCELS);
            if(warnings.isEmpty()){
                warnings = "- " + warning;
            } else {
                warnings = warnings + "\n- " + warning;
            }
        }
        
        if(!warnings.isEmpty()){
            warnings = MessageUtility.getLocalizedMessageText(ClientMessage.BAUNIT_RRR_REPORT_WARNINGS) +
                    "\n\n" + warnings;
            if(JOptionPane.showConfirmDialog(MainForm.getInstance(), warnings, "", 
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                return reportBean;
            } else {
                return null;
            }
        }
        return reportBean;
    }
    
    private void printRejectionLetter(){
        final RrrReportBean reportBean = prepareReportBean();
        if(reportBean!=null){
            // Show free text form
            FreeTextDialog form = new FreeTextDialog(
                    MessageUtility.getLocalizedMessageText(ClientMessage.BAUNIT_LEASE_REJECTION_REASON_TITLE), 
                    null, MainForm.getInstance(), true);
            WindowUtility.centerForm(form);
            
            form.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if(evt.getPropertyName().equals(FreeTextDialog.TEXT_TO_SAVE)){
                        reportBean.setFreeText((String)evt.getNewValue());
                    }
                }
            });
            form.setVisible(true);
            showReport(ReportManager.getLeaseRejectionReport(reportBean));
        }
    }
    
    private void printOfferLetter(boolean isDraft){
        final RrrReportBean reportBean = prepareReportBean();
        if(reportBean!=null){
            showReport(ReportManager.getLeaseOfferReport(reportBean, isDraft));
        }
    }
    
    private void printLease(boolean isDraft){
        final RrrReportBean reportBean = prepareReportBean();
        if(reportBean!=null){
            showReport(ReportManager.getLeaseReport(reportBean, isDraft));
        }
    }
    
    /**
     * Opens {@link ReportViewerForm} to display report.
     */
    private void showReport(JasperPrint report) {
        ReportViewerForm form = new ReportViewerForm(report);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        rrrBean = CreateRrrBean();
        popUpOwners = new javax.swing.JPopupMenu();
        menuAddOwner = new javax.swing.JMenuItem();
        menuEditOwner = new javax.swing.JMenuItem();
        menuRemoveOwner = new javax.swing.JMenuItem();
        menuViewOwner = new javax.swing.JMenuItem();
        leaseConditions = new org.sola.clients.beans.referencedata.LeaseConditionListBean();
        leaseConditionsPopUp = new javax.swing.JPopupMenu();
        menuAddCustomCondition = new javax.swing.JMenuItem();
        menuEditCondition = new javax.swing.JMenuItem();
        menuRemoveCondition = new javax.swing.JMenuItem();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnPrintDraftOffer = new javax.swing.JButton();
        btnPrintDraftLease = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnPrintOffer = new javax.swing.JButton();
        btnPrintLease = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnPrintRejection = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel11 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtRegDatetime = new javax.swing.JFormattedTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtRegistrationNumber = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtExpirationDate = new javax.swing.JFormattedTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtRent = new javax.swing.JFormattedTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtDueDate = new javax.swing.JFormattedTextField();
        jPanel10 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        cbxIsPrimary = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtNotationText = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnAddOwner = new javax.swing.JButton();
        btnEditOwner = new javax.swing.JButton();
        btnRemoveOwner = new javax.swing.JButton();
        btnViewOwner = new javax.swing.JButton();
        btnSelectExisting = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableOwners = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jPanel3 = new javax.swing.JPanel();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        documentsManagementPanel = createDocumentsPanel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableLeaseConditions = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jToolBar3 = new javax.swing.JToolBar();
        btnAddCustomCondition = new javax.swing.JButton();
        btnEditCondition = new javax.swing.JButton();
        btnRemoveCondition = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(10, 32767));
        cbxStandardConditions = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(8, 0), new java.awt.Dimension(8, 0), new java.awt.Dimension(10, 32767));
        btnAddStandardCondition = new javax.swing.JButton();

        menuAddOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        menuAddOwner.setText(bundle.getString("SimpleOwhershipPanel.menuAddOwner.text")); // NOI18N
        menuAddOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddOwnerActionPerformed(evt);
            }
        });
        popUpOwners.add(menuAddOwner);

        menuEditOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditOwner.setText(bundle.getString("SimpleOwhershipPanel.menuEditOwner.text")); // NOI18N
        menuEditOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditOwnerActionPerformed(evt);
            }
        });
        popUpOwners.add(menuEditOwner);

        menuRemoveOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveOwner.setText(bundle.getString("SimpleOwhershipPanel.menuRemoveOwner.text")); // NOI18N
        menuRemoveOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveOwnerActionPerformed(evt);
            }
        });
        popUpOwners.add(menuRemoveOwner);

        menuViewOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        menuViewOwner.setText(bundle.getString("SimpleOwhershipPanel.menuViewOwner.text")); // NOI18N
        menuViewOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuViewOwnerActionPerformed(evt);
            }
        });
        popUpOwners.add(menuViewOwner);

        menuAddCustomCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        menuAddCustomCondition.setText(bundle.getString("LeasePanel.menuAddCustomCondition.text")); // NOI18N
        menuAddCustomCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAddCustomConditionActionPerformed(evt);
            }
        });
        leaseConditionsPopUp.add(menuAddCustomCondition);

        menuEditCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        menuEditCondition.setText(bundle.getString("LeasePanel.menuEditCondition.text")); // NOI18N
        menuEditCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEditConditionActionPerformed(evt);
            }
        });
        leaseConditionsPopUp.add(menuEditCondition);

        menuRemoveCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        menuRemoveCondition.setText(bundle.getString("LeasePanel.menuRemoveCondition.text")); // NOI18N
        menuRemoveCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRemoveConditionActionPerformed(evt);
            }
        });
        leaseConditionsPopUp.add(menuRemoveCondition);

        setHeaderPanel(headerPanel);

        headerPanel.setTitleText(bundle.getString("SimpleOwhershipPanel.headerPanel.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("SimpleOwhershipPanel.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);
        jToolBar1.add(jSeparator1);

        btnPrintDraftOffer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintDraftOffer.setText(bundle.getString("LeasePanel.btnPrintDraftOffer.text")); // NOI18N
        btnPrintDraftOffer.setFocusable(false);
        btnPrintDraftOffer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintDraftOfferActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrintDraftOffer);

        btnPrintDraftLease.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintDraftLease.setText(bundle.getString("LeasePanel.btnPrintDraftLease.text")); // NOI18N
        btnPrintDraftLease.setFocusable(false);
        btnPrintDraftLease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintDraftLeaseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrintDraftLease);
        jToolBar1.add(jSeparator3);

        btnPrintOffer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintOffer.setText(bundle.getString("LeasePanel.btnPrintOffer.text")); // NOI18N
        btnPrintOffer.setFocusable(false);
        btnPrintOffer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintOfferActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrintOffer);

        btnPrintLease.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintLease.setText(bundle.getString("LeasePanel.btnPrintLease.text")); // NOI18N
        btnPrintLease.setFocusable(false);
        btnPrintLease.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintLeaseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrintLease);
        jToolBar1.add(filler1);
        jToolBar1.add(jSeparator4);

        btnPrintRejection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintRejection.setText(bundle.getString("LeasePanel.btnPrintRejection.text")); // NOI18N
        btnPrintRejection.setFocusable(false);
        btnPrintRejection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintRejectionActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrintRejection);
        jToolBar1.add(jSeparator2);

        jLabel1.setText(bundle.getString("SimpleOwhershipPanel.jLabel1.text")); // NOI18N
        jToolBar1.add(jLabel1);

        lblStatus.setFont(LafManager.getInstance().getLabFontBold());
        lblStatus.setText(bundle.getString("SimpleOwhershipPanel.lblStatus.text")); // NOI18N
        jToolBar1.add(lblStatus);

        jPanel9.setLayout(new java.awt.GridLayout(1, 5, 15, 0));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel2.setText(bundle.getString("SimpleOwhershipPanel.jLabel2.text")); // NOI18N

        txtRegDatetime.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"), txtRegDatetime, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2)
            .addComponent(txtRegDatetime, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel9.add(jPanel5);

        jLabel8.setText(bundle.getString("LeasePanel.jLabel8.text")); // NOI18N

        txtRegistrationNumber.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${nr}"), txtRegistrationNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(0, 4, Short.MAX_VALUE))
            .addComponent(txtRegistrationNumber)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRegistrationNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel13);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel5.setText(bundle.getString("LeasePanel.jLabel5.text")); // NOI18N

        txtExpirationDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtExpirationDate.setText(bundle.getString("LeasePanel.txtExpirationDate.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${expirationDate}"), txtExpirationDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 14, Short.MAX_VALUE))
            .addComponent(txtExpirationDate)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtExpirationDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel9.add(jPanel7);

        jLabel6.setText(bundle.getString("LeasePanel.jLabel6.text")); // NOI18N

        txtRent.setFormatterFactory(FormattersFactory.getInstance().getDecimalFormatterFactory());
        txtRent.setText(bundle.getString("LeasePanel.txtRent.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${amount}"), txtRent, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 43, Short.MAX_VALUE))
            .addComponent(txtRent)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel9.add(jPanel8);

        jLabel4.setText(bundle.getString("LeasePanel.jLabel4.text")); // NOI18N

        txtDueDate.setFormatterFactory(FormattersFactory.getInstance().getDateFormatterFactory());
        txtDueDate.setText(bundle.getString("LeasePanel.txtDueDate.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${dueDate}"), txtDueDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 8, Short.MAX_VALUE))
            .addComponent(txtDueDate)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel9.add(jPanel6);

        jLabel7.setText(bundle.getString("LeasePanel.jLabel7.text")); // NOI18N

        cbxIsPrimary.setText(bundle.getString("SimpleOwhershipPanel.cbxIsPrimary.text")); // NOI18N
        cbxIsPrimary.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cbxIsPrimary.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cbxIsPrimary.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        cbxIsPrimary.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${primary}"), cbxIsPrimary, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(cbxIsPrimary))
                .addGap(0, 26, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxIsPrimary))
        );

        jPanel9.add(jPanel10);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel3.setText(bundle.getString("SimpleOwhershipPanel.jLabel3.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${notation.notationText}"), txtNotationText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(txtNotationText)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNotationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.setLayout(new java.awt.GridLayout(2, 1, 0, 10));

        groupPanel1.setTitleText(bundle.getString("SimpleOwhershipPanel.groupPanel1.titleText")); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

        btnAddOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddOwner.setText(bundle.getString("SimpleOwhershipPanel.btnAddOwner.text")); // NOI18N
        btnAddOwner.setFocusable(false);
        btnAddOwner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddOwnerActionPerformed(evt);
            }
        });
        jToolBar2.add(btnAddOwner);

        btnEditOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditOwner.setText(bundle.getString("SimpleOwhershipPanel.btnEditOwner.text")); // NOI18N
        btnEditOwner.setFocusable(false);
        btnEditOwner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnEditOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditOwnerActionPerformed(evt);
            }
        });
        jToolBar2.add(btnEditOwner);

        btnRemoveOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveOwner.setText(bundle.getString("SimpleOwhershipPanel.btnRemoveOwner.text")); // NOI18N
        btnRemoveOwner.setFocusable(false);
        btnRemoveOwner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRemoveOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveOwnerActionPerformed(evt);
            }
        });
        jToolBar2.add(btnRemoveOwner);

        btnViewOwner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/view.png"))); // NOI18N
        btnViewOwner.setText(bundle.getString("SimpleOwhershipPanel.btnViewOwner.text")); // NOI18N
        btnViewOwner.setFocusable(false);
        btnViewOwner.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnViewOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewOwnerActionPerformed(evt);
            }
        });
        jToolBar2.add(btnViewOwner);

        btnSelectExisting.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        btnSelectExisting.setText(bundle.getString("LeasePanel.btnSelectExisting.text_1")); // NOI18N
        btnSelectExisting.setFocusable(false);
        btnSelectExisting.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSelectExisting.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectExistingActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSelectExisting);

        tableOwners.setComponentPopupMenu(popUpOwners);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredRightHolderList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, eLProperty, tableOwners);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${fullName}"));
        columnBinding.setColumnName("Full Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${selectedRightHolder}"), tableOwners, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableOwners);
        tableOwners.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SimpleOwhershipPanel.tableOwners.columnModel.title0_1")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2);

        groupPanel2.setTitleText(bundle.getString("SimpleOwhershipPanel.groupPanel2.titleText")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(groupPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
            .addComponent(documentsManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(groupPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentsManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("LeasePanel.jPanel11.TabConstraints.tabTitle"), jPanel11); // NOI18N

        tableLeaseConditions.setComponentPopupMenu(leaseConditionsPopUp);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${leaseConditionFilteredList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, eLProperty, tableLeaseConditions);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${conditionText}"));
        columnBinding.setColumnName("Condition Text");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${customCondition}"));
        columnBinding.setColumnName("Custom Condition");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${selectedLeaseCondition}"), tableLeaseConditions, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(tableLeaseConditions);
        tableLeaseConditions.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("LeasePanel.tableLeaseConditions.columnModel.title0_1")); // NOI18N
        tableLeaseConditions.getColumnModel().getColumn(0).setCellRenderer(new org.sola.clients.swing.ui.renderers.TableCellTextAreaRenderer());
        tableLeaseConditions.getColumnModel().getColumn(1).setPreferredWidth(55);
        tableLeaseConditions.getColumnModel().getColumn(1).setMaxWidth(55);
        tableLeaseConditions.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("LeasePanel.tableLeaseConditions.columnModel.title1_1")); // NOI18N
        tableLeaseConditions.getColumnModel().getColumn(1).setCellRenderer(new BooleanCellRenderer2());

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        btnAddCustomCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddCustomCondition.setText(bundle.getString("LeasePanel.btnAddCustomCondition.text")); // NOI18N
        btnAddCustomCondition.setFocusable(false);
        btnAddCustomCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCustomConditionActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAddCustomCondition);

        btnEditCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/pencil.png"))); // NOI18N
        btnEditCondition.setText(bundle.getString("LeasePanel.btnEditCondition.text")); // NOI18N
        btnEditCondition.setFocusable(false);
        btnEditCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditConditionActionPerformed(evt);
            }
        });
        jToolBar3.add(btnEditCondition);

        btnRemoveCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/remove.png"))); // NOI18N
        btnRemoveCondition.setText(bundle.getString("LeasePanel.btnRemoveCondition.text")); // NOI18N
        btnRemoveCondition.setFocusable(false);
        btnRemoveCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveConditionActionPerformed(evt);
            }
        });
        jToolBar3.add(btnRemoveCondition);
        jToolBar3.add(jSeparator5);
        jToolBar3.add(filler3);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${leaseConditionList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, leaseConditions, eLProperty, cbxStandardConditions);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, leaseConditions, org.jdesktop.beansbinding.ELProperty.create("${selectedLeaseCondition}"), cbxStandardConditions, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jToolBar3.add(cbxStandardConditions);
        jToolBar3.add(filler2);

        btnAddStandardCondition.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/add.png"))); // NOI18N
        btnAddStandardCondition.setText(bundle.getString("LeasePanel.btnAddStandardCondition.text")); // NOI18N
        btnAddStandardCondition.setFocusable(false);
        btnAddStandardCondition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddStandardConditionActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAddStandardCondition);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("LeasePanel.jPanel12.TabConstraints.tabTitle"), new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif")), jPanel12); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 737, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddOwnerActionPerformed
        addOwner();
    }//GEN-LAST:event_btnAddOwnerActionPerformed

    private void btnEditOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditOwnerActionPerformed
        editOwner();
    }//GEN-LAST:event_btnEditOwnerActionPerformed

    private void btnRemoveOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveOwnerActionPerformed
        removeOwner();
    }//GEN-LAST:event_btnRemoveOwnerActionPerformed

    private void btnViewOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewOwnerActionPerformed
        viewOwner();
    }//GEN-LAST:event_btnViewOwnerActionPerformed

    private void menuAddOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddOwnerActionPerformed
        addOwner();
    }//GEN-LAST:event_menuAddOwnerActionPerformed

    private void menuEditOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditOwnerActionPerformed
        editOwner();
    }//GEN-LAST:event_menuEditOwnerActionPerformed

    private void menuRemoveOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveOwnerActionPerformed
        removeOwner();
    }//GEN-LAST:event_menuRemoveOwnerActionPerformed

    private void menuViewOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuViewOwnerActionPerformed
        viewOwner();
    }//GEN-LAST:event_menuViewOwnerActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveRrr();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnSelectExistingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectExistingActionPerformed
        openSelectRightHolderForm();
    }//GEN-LAST:event_btnSelectExistingActionPerformed

    private void btnAddCustomConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCustomConditionActionPerformed
        addCustomCondition();
    }//GEN-LAST:event_btnAddCustomConditionActionPerformed

    private void menuAddCustomConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAddCustomConditionActionPerformed
        addCustomCondition();
    }//GEN-LAST:event_menuAddCustomConditionActionPerformed

    private void btnEditConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditConditionActionPerformed
        editCustomCondition();
    }//GEN-LAST:event_btnEditConditionActionPerformed

    private void menuEditConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEditConditionActionPerformed
        editCustomCondition();
    }//GEN-LAST:event_menuEditConditionActionPerformed

    private void btnRemoveConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveConditionActionPerformed
        removeCondition();
    }//GEN-LAST:event_btnRemoveConditionActionPerformed

    private void menuRemoveConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRemoveConditionActionPerformed
        removeCondition();
    }//GEN-LAST:event_menuRemoveConditionActionPerformed

    private void btnAddStandardConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStandardConditionActionPerformed
        addStandardCondition();
    }//GEN-LAST:event_btnAddStandardConditionActionPerformed

    private void btnPrintRejectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintRejectionActionPerformed
        printRejectionLetter();
    }//GEN-LAST:event_btnPrintRejectionActionPerformed

    private void btnPrintDraftOfferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintDraftOfferActionPerformed
        printOfferLetter(true);
    }//GEN-LAST:event_btnPrintDraftOfferActionPerformed

    private void btnPrintOfferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintOfferActionPerformed
        printOfferLetter(false);
    }//GEN-LAST:event_btnPrintOfferActionPerformed

    private void btnPrintDraftLeaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintDraftLeaseActionPerformed
        printLease(true);
    }//GEN-LAST:event_btnPrintDraftLeaseActionPerformed

    private void btnPrintLeaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintLeaseActionPerformed
        printLease(false);
    }//GEN-LAST:event_btnPrintLeaseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCustomCondition;
    private javax.swing.JButton btnAddOwner;
    private javax.swing.JButton btnAddStandardCondition;
    private javax.swing.JButton btnEditCondition;
    private javax.swing.JButton btnEditOwner;
    private javax.swing.JButton btnPrintDraftLease;
    private javax.swing.JButton btnPrintDraftOffer;
    private javax.swing.JButton btnPrintLease;
    private javax.swing.JButton btnPrintOffer;
    private javax.swing.JButton btnPrintRejection;
    private javax.swing.JButton btnRemoveCondition;
    private javax.swing.JButton btnRemoveOwner;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSelectExisting;
    private javax.swing.JButton btnViewOwner;
    private javax.swing.JCheckBox cbxIsPrimary;
    private javax.swing.JComboBox cbxStandardConditions;
    private org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel documentsManagementPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
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
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JLabel lblStatus;
    private org.sola.clients.beans.referencedata.LeaseConditionListBean leaseConditions;
    private javax.swing.JPopupMenu leaseConditionsPopUp;
    private javax.swing.JMenuItem menuAddCustomCondition;
    private javax.swing.JMenuItem menuAddOwner;
    private javax.swing.JMenuItem menuEditCondition;
    private javax.swing.JMenuItem menuEditOwner;
    private javax.swing.JMenuItem menuRemoveCondition;
    private javax.swing.JMenuItem menuRemoveOwner;
    private javax.swing.JMenuItem menuViewOwner;
    private javax.swing.JPopupMenu popUpOwners;
    private org.sola.clients.beans.administrative.RrrBean rrrBean;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableLeaseConditions;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableOwners;
    private javax.swing.JFormattedTextField txtDueDate;
    private javax.swing.JFormattedTextField txtExpirationDate;
    private javax.swing.JTextField txtNotationText;
    private javax.swing.JFormattedTextField txtRegDatetime;
    private javax.swing.JTextField txtRegistrationNumber;
    private javax.swing.JFormattedTextField txtRent;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
