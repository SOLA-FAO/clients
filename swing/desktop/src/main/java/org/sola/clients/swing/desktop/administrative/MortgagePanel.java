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

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.validation.groups.Default;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.administrative.validation.MortgageValidationGroup;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.controls.BrowseControlListener;
import org.sola.clients.swing.common.utils.BindingTools;
import org.sola.clients.swing.desktop.MainForm;
import org.sola.clients.swing.desktop.party.QuickSearchPartyForm;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.clients.swing.ui.renderers.FormattersFactory;
import org.sola.clients.swing.ui.renderers.SimpleComboBoxRenderer;
import org.sola.clients.swing.ui.source.DocumentsManagementPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Used to create and manage mortgages. {@link RrrBean} is used to bind the data
 * on the form.
 */
public class MortgagePanel extends ContentPanel {

    private ApplicationBean appBean;
    private ApplicationServiceBean appService;
    private RrrBean.RRR_ACTION rrrAction;
    public static final String UPDATED_RRR = "updatedRRR";

    private DocumentsManagementPanel createDocumentsPanel() {
        if (rrrBean == null) {
            rrrBean = new RrrBean();
        }
        if (appBean == null) {
            appBean = new ApplicationBean();
        }

        boolean allowEdit = true;
        if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            allowEdit = false;
        }

        DocumentsManagementPanel panel = new DocumentsManagementPanel(
                rrrBean.getSourceList(), appBean, allowEdit);
        return panel;
    }

    private RrrBean CreateRrrBean() {
        if (rrrBean == null) {
            rrrBean = new RrrBean();
        }
        return rrrBean;
    }

    public MortgagePanel(RrrBean rrrBean, ApplicationBean applicationBean,
            ApplicationServiceBean applicationService, RrrBean.RRR_ACTION rrrAction) {
        this.appBean = applicationBean;
        this.appService = applicationService;
        this.rrrAction = rrrAction;
        prepareRrrBean(rrrBean, rrrAction);

        initComponents();

        headerPanel.setTitleText(rrrBean.getRrrType().getDisplayValue());
        customizeForm(rrrAction);
        saveRrrState();
    }

    private void prepareRrrBean(RrrBean rrrBean, RrrBean.RRR_ACTION rrrAction) {
        if (rrrBean == null) {
            this.rrrBean = new RrrBean();
            this.rrrBean.setStatusCode(StatusConstants.PENDING);
        } else {
            this.rrrBean = rrrBean.makeCopyByAction(rrrAction);
        }
    }

    private void customizeForm(RrrBean.RRR_ACTION rrrAction) {
        if (rrrAction == RrrBean.RRR_ACTION.NEW) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_CREATE_AND_CLOSE).getMessage());
        }
        if (rrrAction == RrrBean.RRR_ACTION.CANCEL) {
            btnSave.setText(MessageUtility.getLocalizedMessage(
                    ClientMessage.GENERAL_LABELS_EXTINGUISH_AND_CLOSE).getMessage());
        }

        if (rrrAction != RrrBean.RRR_ACTION.EDIT && rrrAction != RrrBean.RRR_ACTION.VIEW
                && appService != null) {
            // Set default noation text from the selected application service
            txtNotationText.setText(appService.getRequestType().getNotationTemplate());
        }

        if (rrrAction == RrrBean.RRR_ACTION.VIEW) {
            btnSave.setVisible(false);
            txtNotationText.setEnabled(false);
            txtRegDatetime.setEditable(false);
            txtNotationText.setEditable(false);
            txtAmount.setEditable(false);
            txtExpiryDate.setEditable(false);
            txtInterestRate.setEditable(false);
            txtRanking.setEditable(false);
            cbxType.setEditable(false);
            browseLender.setEnabled(false);
        } else {
            browseLender.addBrowseControlEventListener(new BrowseControlListener() {

                @Override
                public void deleteButtonClicked(MouseEvent e) {
                }

                @Override
                public void browseButtonClicked(MouseEvent e) {
                    openLenderForm();
                }

                @Override
                public void controlClicked(MouseEvent e) {
                }

                @Override
                public void textClicked(MouseEvent e) {
                }
            });
        }
    }

    private void openLenderForm() {
        QuickSearchPartyForm searchForm = new QuickSearchPartyForm(this, true);
        searchForm.getSearchParams().setRoleTypeCode("bank");
        searchForm.setLocationRelativeTo(this);

        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getNewValue() != null) {
                    rrrBean.setFirstRightholder((PartySummaryBean) e.getNewValue());
                    BindingTools.resetBinding(bindingGroup, "lenderGoup");
                }
            }
        };
        searchForm.addPropertyChangeListener(QuickSearchPartyForm.SELECTED_PARTY, listener);
        searchForm.setVisible(true);
        searchForm.removePropertyChangeListener(QuickSearchPartyForm.SELECTED_PARTY, listener);
    }

    private boolean saveRrr() {
        if (rrrBean.validate(true, Default.class, MortgageValidationGroup.class).size() > 0) {
            return false;
        }
        firePropertyChange(UPDATED_RRR, null, rrrBean);
        close();
        return true;
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        rrrBean = CreateRrrBean();
        mortgageTypeListBean = new org.sola.clients.beans.referencedata.MortgageTypeListBean();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 32767));
        jLabel14 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel10 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtRegDatetime = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtExpiryDate = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbxType = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        browseLender = new org.sola.clients.swing.common.controls.BrowseControl();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JFormattedTextField();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtRanking = new javax.swing.JFormattedTextField();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtInterestRate = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        txtNotationText = new javax.swing.JTextField();
        groupPanel1 = new org.sola.clients.swing.ui.GroupPanel();
        documentsManagementPanel1 = createDocumentsPanel();

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        setHelpTopic(bundle.getString("MortgagePanel.helpTopic")); // NOI18N
        setName("Form"); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("MortgagePanel.headerPanel.titleText")); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/save.png"))); // NOI18N
        btnSave.setText(bundle.getString("MortgagePanel.btnSave.text")); // NOI18N
        btnSave.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar1.add(jSeparator1);

        filler1.setName("filler1"); // NOI18N
        jToolBar1.add(filler1);

        jLabel14.setText(bundle.getString("MortgagePanel.jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        jToolBar1.add(jLabel14);

        lblStatus.setFont(LafManager.getInstance().getLabFontBold());
        lblStatus.setName("lblStatus"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${status.displayValue}"), lblStatus, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jToolBar1.add(lblStatus);

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jPanel10.setName("jPanel10"); // NOI18N

        jPanel8.setName("jPanel8"); // NOI18N
        jPanel8.setLayout(new java.awt.GridLayout(2, 3, 15, 0));

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel13.setText(bundle.getString("MortgagePanel.jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        txtRegDatetime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtRegDatetime.setName("txtRegDatetime"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"), txtRegDatetime, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addContainerGap(112, Short.MAX_VALUE))
            .addComponent(txtRegDatetime, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRegDatetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel2);

        jPanel5.setName("jPanel5"); // NOI18N

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel3.setText(bundle.getString("MortgagePanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtExpiryDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));
        txtExpiryDate.setName("txtExpiryDate"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${expirationDate}"), txtExpiryDate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap(122, Short.MAX_VALUE))
            .addComponent(txtExpiryDate, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtExpiryDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel5);

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel2.setText(bundle.getString("MortgagePanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        cbxType.setName("cbxType"); // NOI18N
        cbxType.setRenderer(new SimpleComboBoxRenderer("getDisplayValue"));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${mortgageTypeListBean}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mortgageTypeListBean, eLProperty, cbxType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${mortgageType}"), cbxType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap(172, Short.MAX_VALUE))
            .addComponent(cbxType, 0, 214, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel1);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel6.setText(bundle.getString("MortgagePanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        browseLender.setBrowseButtonTooltip(bundle.getString("MortgagePanel.browseLender.browseButtonTooltip")); // NOI18N
        browseLender.setDeleteButtonIcon(null);
        browseLender.setDeleteButtonTooltip(bundle.getString("MortgagePanel.browseLender.deleteButtonTooltip")); // NOI18N
        browseLender.setDisplayDeleteButton(false);
        browseLender.setName("browseLender"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${firstRightHolder.name}"), browseLender, org.jdesktop.beansbinding.BeanProperty.create("text"), "lenderGoup");
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addContainerGap(163, Short.MAX_VALUE))
            .addComponent(browseLender, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseLender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel3);

        jPanel6.setName("jPanel6"); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel4.setText(bundle.getString("MortgagePanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        txtAmount.setFormatterFactory(FormattersFactory.getInstance().getDecimalFormatterFactory());
        txtAmount.setName("txtAmount"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${mortgageAmount}"), txtAmount, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addContainerGap())
            .addComponent(txtAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel6);

        jPanel9.setName("jPanel9"); // NOI18N
        jPanel9.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel4.setName("jPanel4"); // NOI18N

        jLabel1.setText(bundle.getString("MortgagePanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtRanking.setFormatterFactory(FormattersFactory.getInstance().getIntegerFormatterFactory());
        txtRanking.setName("txtRanking"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${mortgageRanking}"), txtRanking, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(57, Short.MAX_VALUE))
            .addComponent(txtRanking, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRanking, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel4);

        jPanel7.setName("jPanel7"); // NOI18N

        jLabel5.setText(bundle.getString("MortgagePanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txtInterestRate.setFormatterFactory(FormattersFactory.getInstance().getDecimalFormatterFactory());
        txtInterestRate.setName("txtInterestRate"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${mortgageInterestRate}"), txtInterestRate, org.jdesktop.beansbinding.BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addContainerGap(33, Short.MAX_VALUE))
            .addComponent(txtInterestRate, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInterestRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel9.add(jPanel7);

        jPanel8.add(jPanel9);

        jLabel16.setFont(LafManager.getInstance().getLabFontBold());
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/red_asterisk.gif"))); // NOI18N
        jLabel16.setText(bundle.getString("MortgagePanel.jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        txtNotationText.setName("txtNotationText"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrBean, org.jdesktop.beansbinding.ELProperty.create("${notation.notationText}"), txtNotationText, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        groupPanel1.setName("groupPanel1"); // NOI18N
        groupPanel1.setTitleText(bundle.getString("MortgagePanel.groupPanel1.titleText")); // NOI18N

        documentsManagementPanel1.setName("documentsManagementPanel1"); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(documentsManagementPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                    .addComponent(txtNotationText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(543, 543, 543))
                    .addComponent(groupPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNotationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(groupPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(documentsManagementPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel10);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveRrr();
    }//GEN-LAST:event_btnSaveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.swing.common.controls.BrowseControl browseLender;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cbxType;
    private org.sola.clients.swing.ui.source.DocumentsManagementPanel documentsManagementPanel1;
    private javax.swing.Box.Filler filler1;
    private org.sola.clients.swing.ui.GroupPanel groupPanel1;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblStatus;
    private org.sola.clients.beans.referencedata.MortgageTypeListBean mortgageTypeListBean;
    private org.sola.clients.beans.administrative.RrrBean rrrBean;
    private javax.swing.JFormattedTextField txtAmount;
    private javax.swing.JFormattedTextField txtExpiryDate;
    private javax.swing.JFormattedTextField txtInterestRate;
    public javax.swing.JTextField txtNotationText;
    private javax.swing.JFormattedTextField txtRanking;
    private javax.swing.JFormattedTextField txtRegDatetime;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
