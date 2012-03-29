/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sola.clients.swing.desktop.administrative;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.sola.clients.beans.administrative.BaUnitBean;
import org.sola.clients.beans.administrative.BaUnitSearchResultListBean;
import org.sola.clients.beans.administrative.RrrBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationPropertyBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.referencedata.StatusConstants;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.ContentPanel;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.BaUnitTO;

/**
 * Allows to search and/or select BA unit for the new title registration.
 */
public class NewPropertyWizardPanel extends ContentPanel {

    public static final String SELECTED_RESULT_PROPERTY = "selectedResult";
    
    private ApplicationPropertyBean selectedApplicationProperty;
    private BaUnitBean baUnitBean;
    private java.util.ResourceBundle resourceBundle;
    private final static String CARD_SEARCH = "cardSearch";
    private final static String CARD_BAUNIT = "cardBaUnit";
    private boolean allowSelection;

    /** 
     * Class constructor. 
     * @param applicationBean Application instance used to pick up property list.
     * @param allowSelection Defines if selection of parcels and rights is allowed.
     */
    public NewPropertyWizardPanel(ApplicationBean applicationBean, boolean allowSelection) {
        this.applicationBean = applicationBean;
        this.allowSelection = allowSelection;
        resourceBundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle");

        initComponents();

        showCard(CARD_SEARCH);
        customizeAppPropertySelectButton();
        btnSelectFromSearch.setEnabled(false);
        baUnitSearchPanel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BaUnitSearchResultListBean.SELECTED_BAUNIT_SEARCH_RESULT_PROPERTY)) {
                    btnSelectFromSearch.setEnabled(evt.getNewValue() != null);
                }
            }
        });
        baUnitRelTypeListBean.makePriorTitleDefault();
        tableCurrentParcels.setEnabled(allowSelection);
        tableNewParcels.setEnabled(allowSelection);
        tableRights.setEnabled(allowSelection);
        int tabIndex = tabsPropertySelection.indexOfComponent(pnlApplicationProperty);
        tabsPropertySelection.setTitleAt(tabIndex, String.format("%s #%s",
                tabsPropertySelection.getTitleAt(tabIndex), applicationBean.getNr()));
    }

    /** Creates {@link ApplicationBean} instance to bind on the form. */
    private ApplicationBean createApplicationBean() {
        if (applicationBean == null) {
            applicationBean = new ApplicationBean();
        }
        return applicationBean;
    }

    public ApplicationPropertyBean getSelectedApplicationProperty() {
        return selectedApplicationProperty;
    }

    public void setSelectedApplicationProperty(ApplicationPropertyBean selectedApplicationProperty) {
        this.selectedApplicationProperty = selectedApplicationProperty;
        customizeAppPropertySelectButton();
    }

    private void customizeAppPropertySelectButton() {
        btnSelectFromApplication.setEnabled(selectedApplicationProperty != null);
    }

    public BaUnitBean getBaUnitBean() {
        if (baUnitBean == null) {
            baUnitBean = new BaUnitBean();
            baUnitBean.filterCurrentRecords();
            firePropertyChange("baUnitBean", null, this.baUnitBean);
        }
        return baUnitBean;
    }

    public void setBaUnitBean(BaUnitBean baUnitBean) {
        if (baUnitBean != null) {
            this.baUnitBean = baUnitBean;
            this.baUnitBean.filterCurrentRecords();
            firePropertyChange("baUnitBean", null, this.baUnitBean);
        }
    }

    /** Sets {@link BaUnitBean} by first and last name part. */
    private void setupBaUnit(final String nameFirstPart, final String nameLastPart) {
        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage("Getting property object.");
                BaUnitTO baUnitTO = WSManager.getInstance().getAdministrative().GetBaUnitByCode(nameFirstPart, nameLastPart);
                BaUnitBean baUnitBean1 = TypeConverters.TransferObjectToBean(baUnitTO, BaUnitBean.class, null);
                if (baUnitBean1 != null) {
                    setBaUnitBean(baUnitBean1);
                    showCard(CARD_BAUNIT);
                } else {
                    MessageUtility.displayMessage(ClientMessage.BAUNIT_NOT_FOUND);
                }
                return null;
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void showCard(String cardName) {
        if (cardName.equals(CARD_BAUNIT)) {
            headerPanel.setTitleText(resourceBundle.getString("NewPropertyWizardPanel.headerPanel.titleText2"));
        } else {
            headerPanel.setTitleText(resourceBundle.getString("NewPropertyWizardPanel.headerPanel.titleText"));
        }
        ((CardLayout) pnlCards.getLayout()).show(pnlCards, cardName);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        applicationBean = createApplicationBean();
        baUnitRelTypeListBean = new org.sola.clients.beans.referencedata.BaUnitRelTypeListBean();
        headerPanel = new org.sola.clients.swing.ui.HeaderPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        pnlCards = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableRights = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tableNewParcels = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tableCurrentParcels = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        jLabel3 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        btnFinish = new javax.swing.JButton();
        btnBackToSelection = new javax.swing.JButton();
        cbxRelationType = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tabsPropertySelection = new javax.swing.JTabbedPane();
        pnlApplicationProperty = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableApplicationProperty = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        btnSelectFromApplication = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        baUnitSearchPanel = new org.sola.clients.swing.ui.administrative.BaUnitSearchPanel();
        btnSelectFromSearch = new javax.swing.JButton();

        setCloseOnHide(true);
        setHeaderPanel(headerPanel);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/administrative/Bundle"); // NOI18N
        setHelpTopic(bundle.getString("NewPropertyWizardPanel.helpTopic")); // NOI18N

        headerPanel.setName("headerPanel"); // NOI18N
        headerPanel.setTitleText(bundle.getString("NewPropertyWizardPanel.headerPanel.titleText_1")); // NOI18N

        jScrollPane5.setBorder(null);
        jScrollPane5.setName("jScrollPane5"); // NOI18N

        pnlCards.setName("pnlCards"); // NOI18N
        pnlCards.setPreferredSize(new java.awt.Dimension(494, 335));
        pnlCards.setLayout(new java.awt.CardLayout());

        jPanel7.setName("jPanel7"); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setLayout(new java.awt.GridLayout(3, 1, 0, 12));

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tableRights.setName("tableRights"); // NOI18N

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitBean.rrrFilteredList}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableRights);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${selected}"));
        columnBinding.setColumnName("Selected");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rrrType.displayValue}"));
        columnBinding.setColumnName("Rrr Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${registrationDate}"));
        columnBinding.setColumnName("Registration Date");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane2.setViewportView(tableRights);
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("org/sola/clients/swing/desktop/application/Bundle"); // NOI18N
        tableRights.getColumnModel().getColumn(0).setPreferredWidth(30);
        tableRights.getColumnModel().getColumn(0).setMaxWidth(30);
        tableRights.getColumnModel().getColumn(0).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableRights.columnModel.title0")); // NOI18N
        tableRights.getColumnModel().getColumn(1).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableRights.columnModel.title1")); // NOI18N
        tableRights.getColumnModel().getColumn(2).setPreferredWidth(120);
        tableRights.getColumnModel().getColumn(2).setMaxWidth(120);
        tableRights.getColumnModel().getColumn(2).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableRights.columnModel.title2")); // NOI18N

        jLabel1.setText(bundle.getString("NewPropertyWizardPanel.jLabel1.text_1")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel2);

        jPanel4.setName("jPanel4"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tableNewParcels.setName("tableNewParcels"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitBean.filteredNewCadastreObjectList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableNewParcels);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${selected}"));
        columnBinding.setColumnName("Selected");
        columnBinding.setColumnClass(Boolean.class);
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
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane4.setViewportView(tableNewParcels);
        tableNewParcels.getColumnModel().getColumn(0).setPreferredWidth(30);
        tableNewParcels.getColumnModel().getColumn(0).setMaxWidth(30);
        tableNewParcels.getColumnModel().getColumn(0).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableNewParcels.columnModel.title0")); // NOI18N
        tableNewParcels.getColumnModel().getColumn(1).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableNewParcels.columnModel.title1")); // NOI18N
        tableNewParcels.getColumnModel().getColumn(2).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableNewParcels.columnModel.title2")); // NOI18N
        tableNewParcels.getColumnModel().getColumn(3).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableNewParcels.columnModel.title3")); // NOI18N

        jLabel2.setText(bundle.getString("NewPropertyWizardPanel.jLabel2.text_1")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel4);

        jPanel5.setName("jPanel5"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tableCurrentParcels.setName("tableCurrentParcels"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitBean.cadastreObjectFilteredList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, tableCurrentParcels);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${selected}"));
        columnBinding.setColumnName("Selected");
        columnBinding.setColumnClass(Boolean.class);
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
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane3.setViewportView(tableCurrentParcels);
        tableCurrentParcels.getColumnModel().getColumn(0).setPreferredWidth(30);
        tableCurrentParcels.getColumnModel().getColumn(0).setMaxWidth(30);
        tableCurrentParcels.getColumnModel().getColumn(0).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableCurrentParcels.columnModel.title0")); // NOI18N
        tableCurrentParcels.getColumnModel().getColumn(1).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableCurrentParcels.columnModel.title1")); // NOI18N
        tableCurrentParcels.getColumnModel().getColumn(2).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableCurrentParcels.columnModel.title2")); // NOI18N
        tableCurrentParcels.getColumnModel().getColumn(3).setHeaderValue(bundle1.getString("NewPropertyWizardPanel.tableCurrentParcels.columnModel.title3")); // NOI18N

        jLabel3.setText(bundle.getString("NewPropertyWizardPanel.jLabel3.text_1")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel5);

        jPanel8.setName("jPanel8"); // NOI18N

        btnFinish.setText(bundle.getString("NewPropertyWizardPanel.btnFinish.text_1")); // NOI18N
        btnFinish.setName("btnFinish"); // NOI18N
        btnFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishActionPerformed(evt);
            }
        });

        btnBackToSelection.setText(bundle.getString("NewPropertyWizardPanel.btnBackToSelection.text_1")); // NOI18N
        btnBackToSelection.setName("btnBackToSelection"); // NOI18N
        btnBackToSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackToSelectionActionPerformed(evt);
            }
        });

        cbxRelationType.setEnabled(false);
        cbxRelationType.setName("cbxRelationType"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitRelTypes}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitRelTypeListBean, eLProperty, cbxRelationType);
        bindingGroup.addBinding(jComboBoxBinding);
        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitRelTypeListBean, org.jdesktop.beansbinding.ELProperty.create("${selectedBaUnitRelType}"), cbxRelationType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jLabel4.setText(bundle.getString("NewPropertyWizardPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(cbxRelationType, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBackToSelection, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFinish, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addContainerGap())
        );

        jPanel8Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnBackToSelection, btnFinish});

        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFinish)
                    .addComponent(btnBackToSelection)
                    .addComponent(cbxRelationType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        pnlCards.add(jPanel7, "cardBaUnit");

        jPanel1.setName("jPanel1"); // NOI18N

        tabsPropertySelection.setName("tabsPropertySelection"); // NOI18N

        pnlApplicationProperty.setName("pnlApplicationProperty"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tableApplicationProperty.setName("tableApplicationProperty"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${filteredPropertyList}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, applicationBean, eLProperty, tableApplicationProperty);
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
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedApplicationProperty}"), tableApplicationProperty, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(tableApplicationProperty);
        tableApplicationProperty.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("NewPropertyWizardPanel.tableApplicationProperty.columnModel.title0")); // NOI18N
        tableApplicationProperty.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("NewPropertyWizardPanel.tableApplicationProperty.columnModel.title1")); // NOI18N
        tableApplicationProperty.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("NewPropertyWizardPanel.tableApplicationProperty.columnModel.title2")); // NOI18N
        tableApplicationProperty.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("NewPropertyWizardPanel.tableApplicationProperty.columnModel.title3")); // NOI18N

        btnSelectFromApplication.setText(bundle1.getString("NewPropertyWizardPanel.btnSelectFromApplication.text")); // NOI18N
        btnSelectFromApplication.setName("btnSelectFromApplication"); // NOI18N
        btnSelectFromApplication.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectFromApplicationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlApplicationPropertyLayout = new javax.swing.GroupLayout(pnlApplicationProperty);
        pnlApplicationProperty.setLayout(pnlApplicationPropertyLayout);
        pnlApplicationPropertyLayout.setHorizontalGroup(
            pnlApplicationPropertyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlApplicationPropertyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlApplicationPropertyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                    .addComponent(btnSelectFromApplication, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        pnlApplicationPropertyLayout.setVerticalGroup(
            pnlApplicationPropertyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlApplicationPropertyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSelectFromApplication)
                .addContainerGap())
        );

        tabsPropertySelection.addTab(bundle1.getString("NewPropertyWizardPanel.pnlApplicationProperty.TabConstraints.tabTitle"), pnlApplicationProperty); // NOI18N

        jPanel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        baUnitSearchPanel.setName("baUnitSearchPanel"); // NOI18N
        baUnitSearchPanel.setShowOpenButton(false);

        btnSelectFromSearch.setText(bundle1.getString("NewPropertyWizardPanel.btnSelectFromSearch.text")); // NOI18N
        btnSelectFromSearch.setName("btnSelectFromSearch"); // NOI18N
        btnSelectFromSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectFromSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(baUnitSearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                    .addComponent(btnSelectFromSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(baUnitSearchPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSelectFromSearch)
                .addContainerGap())
        );

        tabsPropertySelection.addTab(bundle1.getString("NewPropertyWizardPanel.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsPropertySelection)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsPropertySelection)
        );

        pnlCards.add(jPanel1, "cardSearch");

        jScrollPane5.setViewportView(pnlCards);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelectFromApplicationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectFromApplicationActionPerformed
        if (selectedApplicationProperty != null) {
            setupBaUnit(selectedApplicationProperty.getNameFirstpart(),
                    selectedApplicationProperty.getNameLastpart());
        }
    }//GEN-LAST:event_btnSelectFromApplicationActionPerformed

    private void btnSelectFromSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectFromSearchActionPerformed
        if (baUnitSearchPanel.getSelectedSearchResult() != null) {
            setupBaUnit(baUnitSearchPanel.getSelectedSearchResult().getNameFirstPart(),
                    baUnitSearchPanel.getSelectedSearchResult().getNameLastPart());
        }
    }//GEN-LAST:event_btnSelectFromSearchActionPerformed

    private void btnBackToSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackToSelectionActionPerformed
        showCard(CARD_SEARCH);
    }//GEN-LAST:event_btnBackToSelectionActionPerformed

    private void btnFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishActionPerformed
        if (getBaUnitBean().getSelectedCadastreObjects().size() < 1
                && getBaUnitBean().getSelectedNewCadastreObjects().size() < 1
                && getBaUnitBean().getSelectedRrrs(false).size() < 1) {
            if(allowSelection && MessageUtility.displayMessage(ClientMessage.BAUNIT_NOTHING_SELECTED)
                    != MessageUtility.BUTTON_ONE){
                return;
            }
        }

        if(baUnitRelTypeListBean.getSelectedBaUnitRelType() == null){
            MessageUtility.displayMessage(ClientMessage.BAUNIT_SELECT_RELATION_TYPE);
            return;
        }
        
        if (getBaUnitBean().getSelectedCadastreObjects().size() > 0) {
            MessageUtility.displayMessage(ClientMessage.BAUNIT_EXISTING_PARCELS_SELECTED);
        }

        BaUnitBean selectedBaUnit = getBaUnitBean().copy();
        selectedBaUnit.getRrrList().clear();
        selectedBaUnit.getRrrList().addAll(getBaUnitBean().getSelectedRrrs(true));
        selectedBaUnit.getCadastreObjectList().clear();
        selectedBaUnit.getCadastreObjectList().addAll(getBaUnitBean().getSelectedCadastreObjects());
        selectedBaUnit.getCadastreObjectList().addAll(getBaUnitBean().getSelectedNewCadastreObjects());
        
        for (RrrBean rrrBean : selectedBaUnit.getRrrList()) {
            rrrBean.setStatusCode(StatusConstants.PENDING);
        }
        
        Object[] result = new Object[]{selectedBaUnit, baUnitRelTypeListBean.getSelectedBaUnitRelType()};
        getMainContentPanel().closePanel(this);
        firePropertyChange(SELECTED_RESULT_PROPERTY, null, result);
    }//GEN-LAST:event_btnFinishActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.application.ApplicationBean applicationBean;
    private org.sola.clients.beans.referencedata.BaUnitRelTypeListBean baUnitRelTypeListBean;
    private org.sola.clients.swing.ui.administrative.BaUnitSearchPanel baUnitSearchPanel;
    private javax.swing.JButton btnBackToSelection;
    private javax.swing.JButton btnFinish;
    private javax.swing.JButton btnSelectFromApplication;
    private javax.swing.JButton btnSelectFromSearch;
    private javax.swing.JComboBox cbxRelationType;
    private org.sola.clients.swing.ui.HeaderPanel headerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPanel pnlApplicationProperty;
    private javax.swing.JPanel pnlCards;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableApplicationProperty;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableCurrentParcels;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableNewParcels;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tableRights;
    private javax.swing.JTabbedPane tabsPropertySelection;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
