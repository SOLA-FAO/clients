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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.validation.groups.Default;
import net.sf.jasperreports.engine.JasperPrint;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.RrrReportBean;
import org.sola.clients.beans.administrative.validation.LeaseValidationGroup;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.RrrSubTypeListBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.reports.ReportManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.party.PartyPanelForm;
import org.sola.clients.swing.desktop.party.PartySearchPanelForm;
import org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.MainContentPanel;
import org.sola.clients.swing.common.utils.FormattersFactory;
import org.sola.clients.swing.ui.administrative.ConditionsPanel;
import org.sola.clients.swing.ui.reports.FreeTextDialog;
import org.sola.clients.swing.ui.reports.ReportViewerForm;
import org.sola.clients.swing.ui.security.SecurityClassificationDialog;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Form for managing simple ownership right. {@link RrrBean} is used to bind the
 * data on the form.
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

    private ConditionsPanel createConditionsPanel() {
        return new ConditionsPanel(rrrBean, rrrAction);
    }

    private RrrSubTypeListBean createRrrSubTypes() {
        RrrSubTypeListBean list = new RrrSubTypeListBean(true);
        list.setRrrTypeFilter(rrrBean.getTypeCode(), rrrBean.getRrrSubTypeCode());
        return list;
    }

    public LeasePanel(BaUnitBean baUnit, RrrBean rrrBean, RrrBean.RRR_ACTION rrrAction) {
        this(baUnit, rrrBean, null, null, rrrAction);
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

    private void postInit() {
        customizeForm();
        customizeOwnerButtons(null);
        saveRrrState();
    }

    private void customizeForm() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle");
        String refNum = rrrBean.getReferenceNum() == null ? "" : rrrBean.getReferenceNum(); 
        String title = String.format(bundle.getString("LeasePanel.headerPanel.titleText"),
                rrrBean.getRrrType().getDisplayValue(), rrrBean.getFirstRightHolder() == null
                ? refNum : rrrBean.getFirstRightHolder().getFullName());
        txtStatus.setEnabled(false);
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
            title = String.format(bundle.getString("LeasePanel.headerPanel.titleText.newRrr"), rrrBean.getRrrType().getDisplayValue());
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

        this.setBreadCrumbTitle(this.getBreadCrumbPath(), title);

        boolean enabled = rrrAction != RrrBean.RRR_ACTION.VIEW;

        btnSave.setEnabled(enabled);
        txtNotationText.setEnabled(enabled);
        txtRegDatetime.setEditable(enabled);
        btnRegDate.setEnabled(enabled);
        txtNotationText.setEnabled(enabled);
        txtExpirationDate.setEnabled(enabled);
        btnExpDate.setEnabled(enabled);
        txtRent.setEnabled(enabled);
        txtDueDate.setEnabled(enabled);
        btnDueDate.setEnabled(enabled);

        btnPrintDraftLease.setEnabled(enabled);
        btnPrintDraftOffer.setEnabled(enabled);
        btnPrintLease.setEnabled(enabled);
        btnPrintOffer.setEnabled(enabled);
        btnPrintRejection.setEnabled(enabled);
        cbxRrrSubType.setEnabled(enabled);

        if (!rrrSubTypes.hasRrrSubTypes()) {
            // Hide the purpose panel and put it at the end of the panel list. 
            pnlTop.remove(pnlPurpose);
            pnlTop.add(pnlPurpose);
            pnlPurpose.setVisible(false);
        }

        // Hide the Lease Print buttons if this isn't a lease
        boolean isPrintVisible = RrrBean.CODE_LEASE.equals(rrrBean.getTypeCode());
        btnPrintDraftLease.setVisible(isPrintVisible);
        btnPrintDraftOffer.setVisible(isPrintVisible);
        btnPrintLease.setVisible(isPrintVisible);
        btnPrintOffer.setVisible(isPrintVisible);
        btnPrintRejection.setVisible(isPrintVisible);
        sep1.setVisible(isPrintVisible);
        sep2.setVisible(isPrintVisible);
        sep3.setVisible(isPrintVisible);

        // Configure Security button
        btnSecurity.setVisible(btnSave.isEnabled()
                && SecurityBean.isInRole(RolesConstants.CLASSIFICATION_CHANGE_CLASS));

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

    private RrrReportBean prepareReportBean() {
        RrrReportBean reportBean = new RrrReportBean(baUnit, rrrBean, applicationBean, appService);
        String warnings = "";
        String warning;

        if (applicationBean == null || applicationBean.isNew()) {
            warnings = warnings + MessageUtility.getLocalizedMessageText(ClientMessage.APPLICATION_NOT_FOUND);
        }

        if (reportBean.getRrrRegNumber().isEmpty()) {
            warning = MessageUtility.getLocalizedMessageText(
                    ClientMessage.BAUNIT_RRR_NO_REGISTRATION_NUMBER,
                    new Object[]{rrrBean.getRrrType().getDisplayValue()});
            if (warnings.isEmpty()) {
                warnings = "- " + warning;
            } else {
                warnings = warnings + "\n- " + warning;
            }
        }

        if (reportBean.getBaUnit().getCadastreObjectFilteredList().size() < 1) {
            warning = MessageUtility.getLocalizedMessageText(ClientMessage.BAUNIT_HAS_NO_PARCELS);
            if (warnings.isEmpty()) {
                warnings = "- " + warning;
            } else {
                warnings = warnings + "\n- " + warning;
            }
        }

        if (!warnings.isEmpty()) {
            warnings = MessageUtility.getLocalizedMessageText(ClientMessage.BAUNIT_RRR_REPORT_WARNINGS)
                    + "\n\n" + warnings;
            if (JOptionPane.showConfirmDialog(MainForm.getInstance(), warnings, "",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                return reportBean;
            } else {
                return null;
            }
        }
        return reportBean;
    }

    private void printRejectionLetter() {
        final RrrReportBean reportBean = prepareReportBean();
        if (reportBean != null) {
            // Show free text form
            FreeTextDialog form = new FreeTextDialog(
                    MessageUtility.getLocalizedMessageText(ClientMessage.BAUNIT_LEASE_REJECTION_REASON_TITLE),
                    null, MainForm.getInstance(), true);
            WindowUtility.centerForm(form);

            form.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals(FreeTextDialog.TEXT_TO_SAVE)) {
                        reportBean.setFreeText((String) evt.getNewValue());
                    }
                }
            });
            form.setVisible(true);
            showReport(ReportManager.getLeaseRejectionReport(reportBean));
        }
    }

    private void printOfferLetter(boolean isDraft) {
        final RrrReportBean reportBean = prepareReportBean();
        if (reportBean != null) {
            showReport(ReportManager.getLeaseOfferReport(reportBean, isDraft));
        }
    }

    private void printLease(boolean isDraft) {
        final RrrReportBean reportBean = prepareReportBean();
        if (reportBean != null) {
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

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    private void configureSecurity() {
        SecurityClassificationDialog form = new SecurityClassificationDialog(rrrBean,
                MainForm.getInstance(), true);
        WindowUtility.centerForm(form);
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
        rrrSubTypes = createRrrSubTypes();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        btnSecurity = new javax.swing.JButton();
        sep1 = new javax.swing.JToolBar.Separator();
        btnPrintDraftOffer = new javax.swing.JButton();
        btnPrintDraftLease = new javax.swing.JButton();
        sep2 = new javax.swing.JToolBar.Separator();
        btnPrintOffer = new javax.swing.JButton();
        btnPrintLease = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        sep3 = new javax.swing.JToolBar.Separator();
        btnPrintRejection = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel11 = new javax.swing.JPanel();
        pnlTop = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtRegistrationNumber = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtRegDatetime = new org.sola.clients.swing.common.controls.WatermarkDate();
        btnRegDate = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtExpirationDate = new org.sola.clients.swing.common.controls.WatermarkDate();
        btnExpDate = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        pnlPurpose = new javax.swing.JPanel();
        lblRrrSubType = new javax.swing.JLabel();
        cbxRrrSubType = new javax.swing.JComboBox();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtRent = new javax.swing.JFormattedTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtDueDate = new org.sola.clients.swing.common.controls.WatermarkDate();
        btnDueDate = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtNotationText = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        jToolBar2 = new javax.swing.JToolBar();
        btnViewOwner = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnAddOwner = new javax.swing.JButton();
        btnEditOwner = new javax.swing.JButton();
        btnRemoveOwner = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnSelectExisting = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableOwners = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        groupPanel2 = new org.sola.clients.swing.ui.GroupPanel();
        jPanel3 = new javax.swing.JPanel();
        documentsManagementPanel = createDocumentsPanel();
        jPanel12 = new javax.swing.JPanel();
        conditionsPanel1 = createConditionsPanel();

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

        setHeaderPanel(headerPanel);

        headerPanel.setTitleText(bundle.getString("LeasePanel.headerPanel.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/confirm-close.png"))); // NOI18N
        btnSave.setText(bundle.getString("SimpleOwhershipPanel.btnSave.text")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnSecurity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/lock.png"))); // NOI18N
        btnSecurity.setText(bundle.getString("LeasePanel.btnSecurity.text")); // NOI18N
        btnSecurity.setFocusable(false);
        btnSecurity.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSecurity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSecurityActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSecurity);
        jToolBar1.add(sep1);

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
        jToolBar1.add(sep2);

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
        jToolBar1.add(sep3);

        btnPrintRejection.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/print.png"))); // NOI18N
        btnPrintRejection.setText(bundle.getString("LeasePanel.btnPrintRejection.text")); // NOI18N
        btnPrintRejection.setFocusable(false);
        btnPrintRejection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintRejectionActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrintRejection);

        pnlTop.setLayout(new java.awt.GridLayout(2, 4, 15, 0));

        jLabel8.setText(bundle.getString("LeasePanel.jLabel8.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${notation.referenceNr}"), txtRegistrationNumber, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtRegistrationNumber)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRegistrationNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel13);

        jLabel2.setText(bundle.getString("SimpleOwhershipPanel.jLabel2.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"), txtRegDatetime, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnRegDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnRegDate.setText(bundle.getString("LeasePanel.btnRegDate.text")); // NOI18N
        btnRegDate.setBorder(null);
        btnRegDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(txtRegDatetime, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRegDate))
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegDate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel5);

        jLabel5.setText(bundle.getString("LeasePanel.jLabel5.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${expirationDate}"), txtExpirationDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnExpDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnExpDate.setText(bundle.getString("LeasePanel.btnExpDate.text")); // NOI18N
        btnExpDate.setBorder(null);
        btnExpDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExpDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(txtExpirationDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExpDate))
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtExpirationDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExpDate))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel7);

        jLabel7.setText(bundle.getString("LeasePanel.jLabel7.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), txtStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
            .addComponent(txtStatus)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel4);

        lblRrrSubType.setText(bundle.getString("LeasePanel.lblRrrSubType.text")); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrSubTypes}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrSubTypes, eLProperty, cbxRrrSubType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${rrrSubType}"), cbxRrrSubType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout pnlPurposeLayout = new javax.swing.GroupLayout(pnlPurpose);
        pnlPurpose.setLayout(pnlPurposeLayout);
        pnlPurposeLayout.setHorizontalGroup(
            pnlPurposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblRrrSubType, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
            .addComponent(cbxRrrSubType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlPurposeLayout.setVerticalGroup(
            pnlPurposeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPurposeLayout.createSequentialGroup()
                .addComponent(lblRrrSubType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRrrSubType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pnlTop.add(pnlPurpose);

        jLabel6.setText(bundle.getString("LeasePanel.jLabel6.text")); // NOI18N

        txtRent.setFormatterFactory(FormattersFactory.getInstance().getMoneyFormatterFactory());
        txtRent.setText(bundle.getString("LeasePanel.txtRent.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${amount}"), txtRent, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtRent, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel8);

        jLabel4.setText(bundle.getString("LeasePanel.jLabel4.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${dueDate}"), txtDueDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        btnDueDate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/calendar.png"))); // NOI18N
        btnDueDate.setText(bundle.getString("LeasePanel.btnDueDate.text")); // NOI18N
        btnDueDate.setBorder(null);
        btnDueDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDueDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 59, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(txtDueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDueDate))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDueDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDueDate))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTop.add(jPanel6);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 153, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 52, Short.MAX_VALUE)
        );

        pnlTop.add(jPanel15);

        jPanel1.setLayout(new java.awt.GridLayout(3, 1, 15, 6));

        txtNotationText.setColumns(20);
        txtNotationText.setLineWrap(true);
        txtNotationText.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${notation.notationText}"), txtNotationText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(txtNotationText);

        jLabel3.setText(bundle.getString("SimpleOwhershipPanel.jLabel3.text")); // NOI18N

        groupPanel1.setTitleText(bundle.getString("SimpleOwhershipPanel.groupPanel1.titleText")); // NOI18N

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);

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
        jToolBar2.add(jSeparator6);

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
        jToolBar2.add(jSeparator7);

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

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3)
            .addComponent(groupPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel14);

        tableOwners.setComponentPopupMenu(popUpOwners);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredRightHolderList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, eLProperty, tableOwners);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${fullName}"));
        columnBinding.setColumnName("Full Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(tableOwners);
        if (tableOwners.getColumnModel().getColumnCount() > 0) {
            tableOwners.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("LeasePanel.tableOwners.columnModel.title0")); // NOI18N
        }

        groupPanel2.setTitleText(bundle.getString("SimpleOwhershipPanel.groupPanel2.titleText")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
            .addComponent(groupPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(groupPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(documentsManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(documentsManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel3);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("LeasePanel.jPanel11.TabConstraints.tabTitle"), jPanel11); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(conditionsPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(conditionsPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("LeasePanel.jPanel12.TabConstraints.tabTitle"), jPanel12); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

    private void btnRegDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegDateActionPerformed
        showCalendar(txtRegDatetime);
    }//GEN-LAST:event_btnRegDateActionPerformed

    private void btnExpDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExpDateActionPerformed
        showCalendar(txtExpirationDate);
    }//GEN-LAST:event_btnExpDateActionPerformed

    private void btnDueDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDueDateActionPerformed
        showCalendar(txtDueDate);
    }//GEN-LAST:event_btnDueDateActionPerformed

    private void btnSecurityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSecurityActionPerformed
        configureSecurity();
    }//GEN-LAST:event_btnSecurityActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddOwner;
    private javax.swing.JButton btnDueDate;
    private javax.swing.JButton btnEditOwner;
    private javax.swing.JButton btnExpDate;
    private javax.swing.JButton btnPrintDraftLease;
    private javax.swing.JButton btnPrintDraftOffer;
    private javax.swing.JButton btnPrintLease;
    private javax.swing.JButton btnPrintOffer;
    private javax.swing.JButton btnPrintRejection;
    private javax.swing.JButton btnRegDate;
    private javax.swing.JButton btnRemoveOwner;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSecurity;
    private javax.swing.JButton btnSelectExisting;
    private javax.swing.JButton btnViewOwner;
    private javax.swing.JComboBox cbxRrrSubType;
    private org.sola.clients.swing.ui.administrative.ConditionsPanel conditionsPanel1;
    private org.sola.clients.swing.desktop.source.DocumentsManagementExtPanel documentsManagementPanel;
    private javax.swing.Box.Filler filler1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel2;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JLabel lblRrrSubType;
    private javax.swing.JMenuItem menuAddOwner;
    private javax.swing.JMenuItem menuEditOwner;
    private javax.swing.JMenuItem menuRemoveOwner;
    private javax.swing.JMenuItem menuViewOwner;
    private javax.swing.JPanel pnlPurpose;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPopupMenu popUpOwners;
    private org.sola.clients.beans.administrative.RrrBean rrrBean;
    private org.sola.clients.beans.referencedata.RrrSubTypeListBean rrrSubTypes;
    private javax.swing.JToolBar.Separator sep1;
    private javax.swing.JToolBar.Separator sep2;
    private javax.swing.JToolBar.Separator sep3;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableOwners;
    private org.sola.clients.swing.common.controls.WatermarkDate txtDueDate;
    private org.sola.clients.swing.common.controls.WatermarkDate txtExpirationDate;
    private javax.swing.JTextArea txtNotationText;
    private org.sola.clients.swing.common.controls.WatermarkDate txtRegDatetime;
    private javax.swing.JTextField txtRegistrationNumber;
    private javax.swing.JFormattedTextField txtRent;
    private javax.swing.JTextField txtStatus;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
