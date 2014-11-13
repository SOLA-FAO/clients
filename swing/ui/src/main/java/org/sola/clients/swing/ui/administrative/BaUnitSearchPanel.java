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
package org.sola.clients.swing.ui.administrative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import org.sola.clients.beans.administrative.BaUnitSearchParamsBean;
import org.sola.clients.beans.administrative.BaUnitSearchResultBean;
import org.sola.clients.beans.administrative.BaUnitSearchResultListBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.referencedata.RrrTypeBean;
import org.sola.clients.beans.security.SecurityBean;
import org.sola.clients.swing.common.LafManager;
import org.sola.clients.swing.common.controls.CalendarForm;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.clients.swing.ui.renderers.CellDelimitedListRenderer;
import org.sola.common.RolesConstants;
import org.sola.common.WindowUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to search BA Units.
 */
public class BaUnitSearchPanel extends javax.swing.JPanel {

    private class AssignmentPanelListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals(PropertyAssignmentDialog.ASSIGNMENT_CHANGED)) {
                executeSearch(searchParamsSL, lblSLSearchResultCount, searchResultsSL);
            }
        }
    }
    private AssignmentPanelListener listener = new AssignmentPanelListener();

    public static final String OPEN_BAUNIT_SEARCH_RESULT = "openBaUnit";
    public static final String SELECT_BAUNIT_SEARCH_RESULT = "selectBaUnit";
    public static final String TAB_STATE_LAND = "tabStateLand";
    public static final String TAB_PROPERTY = "tabProperty";
    public static final String TAB_APPLICATION = "tabApplication";
    private boolean defaultActionOpen = true;

    /**
     * Default constructor.
     */
    public BaUnitSearchPanel() {
        this(null);
    }

    public BaUnitSearchPanel(ApplicationBean appBean) {
        initComponents();
        searchParamsSL.setSearchType(BaUnitSearchParamsBean.SEARCH_TYPE_STATE_LAND);

        baUnitSearchResults.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BaUnitSearchResultListBean.SELECTED_BAUNIT_SEARCH_RESULT_PROPERTY)) {
                    btnOpenBaUnit.setEnabled(evt.getNewValue() != null);
                    btnSelectBaUnit.setEnabled(evt.getNewValue() != null);
                    menuOpenBaUnit.setEnabled(evt.getNewValue() != null);
                    menuSelectBaUnit.setEnabled(evt.getNewValue() != null);
                }
            }
        });

        searchResultsSL.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BaUnitSearchResultListBean.SELECTED_BAUNIT_SEARCH_RESULT_PROPERTY)
                        || evt.getPropertyName().equals(BaUnitSearchResultListBean.BAUNIT_CHECKED_PROPERTY)) {
                    customizeSLButtons(evt.getNewValue() != null);
                }
            }
        });

        searchParamsSL.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BaUnitSearchParamsBean.RRR_TYPE_PROPERTY)) {
                    rrrSubTypeListBean.setRrrTypeFilter(((RrrTypeBean) evt.getNewValue()).getCode(), null);
                }
            }
        });

        applicationResults.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BaUnitSearchResultListBean.SELECTED_BAUNIT_SEARCH_RESULT_PROPERTY)) {
                    btnAppSelect.setEnabled(evt.getNewValue() != null);
                    menuAppSelect.setEnabled(evt.getNewValue() != null);
                }
            }
        });

        // Clear the RrrSubType combo box
        rrrSubTypeListBean.clearAllCodes();
        setupAppTab(appBean);
        customizeButtons();
    }

    /**
     * Configure the Application Tab for display if an applicationBean has been
     * provided otherwise hide the tab.
     *
     * @param appBean
     */
    private void setupAppTab(ApplicationBean appBean) {
        if (appBean == null) {
            hideTabs(TAB_APPLICATION);
        } else {
            tabMain.setTitleAt(0, tabMain.getTitleAt(0) + appBean.getNr());
            BaUnitSearchParamsBean appParams = new BaUnitSearchParamsBean();
            appParams.setSearchType(BaUnitSearchParamsBean.SEARCH_TYPE_STATE_LAND);
            appParams.setApplicationId(appBean.getId());
            applicationResults.search(appParams);
        }
    }

    private void customizeButtons() {
        btnOpenBaUnit.setEnabled(false);
        btnSLOpen.setEnabled(false);
        btnSelectBaUnit.setEnabled(false);
        btnSLSelect.setEnabled(false);
        btnSLAssign.setEnabled(false);
        menuOpenBaUnit.setEnabled(false);
        menuSelectBaUnit.setEnabled(false);
        menuSLOpen.setEnabled(false);
        menuSLSelect.setEnabled(false);
        menuSLAssign.setEnabled(false);
        btnAppSelect.setEnabled(false);
        menuAppSelect.setEnabled(false);

        showSelectButtons(false);
        showOpenButtons(true);
    }

    public void showSelectButtons(boolean show) {
        btnSelectBaUnit.setVisible(show);
        btnSLSelect.setVisible(show);
        menuSelectBaUnit.setVisible(show);
        menuSLSelect.setVisible(show);
    }

    public void showOpenButtons(boolean show) {
        btnOpenBaUnit.setVisible(show);
        btnSLOpen.setVisible(show);
        menuOpenBaUnit.setVisible(show);
        menuSLOpen.setVisible(show);
        btnSLAssign.setVisible(show);
        menuSLAssign.setVisible(show);
    }

    private void customizeSLButtons(boolean selected) {
        btnSLOpen.setEnabled(selected);
        if (searchResultsSL.hasChecked() && SecurityBean.isInRole(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE,
                RolesConstants.ADMINISTRATIVE_ASSIGN_TEAM)) {
            btnSLAssign.setEnabled(true);
        } else {
            btnSLAssign.setEnabled(false);
        }
        btnSLSelect.setEnabled(btnSLOpen.isEnabled());
        menuSLOpen.setEnabled(btnSLOpen.isEnabled());
        menuSLSelect.setEnabled(btnSLOpen.isEnabled());
        menuSLAssign.setEnabled(btnSLAssign.isEnabled());
    }

    private void showCalendar(JFormattedTextField dateField) {
        CalendarForm calendar = new CalendarForm(null, true, dateField);
        calendar.setVisible(true);
    }

    /**
     * Sets the default action to use when the user double clicks on a selected
     * row. If true, the open action will be triggered. If false the select
     * action will be triggered
     *
     * @param open
     */
    public void setDefaultActionOpen(boolean open) {
        defaultActionOpen = open;
    }

    /**
     * Selects the a tab to use for searching and disables the remaining tabs.
     *
     * @param tab Tab to select
     */
    public void selectAndEnableTab(String tab) {
        tabMain.setEnabled(false);
        if (TAB_STATE_LAND.equals(tab)) {
            tabMain.setSelectedComponent(tabStateLand);
        } else if (TAB_PROPERTY.equals(tab)) {
            tabMain.setSelectedComponent(tabProperty);
        } else if (TAB_APPLICATION.equals(tab)) {
            tabMain.setSelectedComponent(tabApplication);
        }
    }

    /**
     * Removes the specified tabs from the tab control.
     *
     * @param tabs Array of tabs to remove
     */
    public void hideTabs(String... tabs) {
        if (tabs == null) {
            return;
        }
        for (String tab : tabs) {
            if (TAB_STATE_LAND.equals(tab) && tabMain.indexOfComponent(tabStateLand) >= 0) {
                tabMain.removeTabAt(tabMain.indexOfComponent(tabStateLand));
            } else if (TAB_PROPERTY.equals(tab) && tabMain.indexOfComponent(tabProperty) >= 0) {
                tabMain.removeTabAt(tabMain.indexOfComponent(tabProperty));
            } else if (TAB_APPLICATION.equals(tab) && tabMain.indexOfComponent(tabApplication) >= 0) {
                tabMain.removeTabAt(tabMain.indexOfComponent(tabApplication));
            }
        }
    }

    /**
     * Executes the search for the SL or general property record.
     *
     * @param params
     * @param lblResultCount
     * @param results
     */
    private void executeSearch(final BaUnitSearchParamsBean params,
            final JLabel lblResultCount, final BaUnitSearchResultListBean results) {

        SolaTask t = new SolaTask<Void, Void>() {
            @Override
            public Void doTask() {
                setMessage(MessageUtility.getLocalizedMessageText(ClientMessage.PROGRESS_MSG_PROPERTY_SEARCHING));
                results.search(params);
                return null;
            }

            @Override
            public void taskDone() {
                if (lblResultCount != null) {
                    lblResultCount.setText(Integer.toString(results.getBaUnitSearchResults().size()));
                    if (results.getBaUnitSearchResults().size() < 1) {
                        MessageUtility.displayMessage(ClientMessage.SEARCH_NO_RESULTS);
                    } else if (results.getBaUnitSearchResults().size() > 100) {
                        MessageUtility.displayMessage(ClientMessage.SEARCH_TOO_MANY_RESULTS, new String[]{"100"});
                    }
                }
            }
        };
        TaskManager.getInstance().runTask(t);
    }

    private void openBaUnit(BaUnitSearchResultBean selectedResult) {
        if (selectedResult != null) {
            firePropertyChange(OPEN_BAUNIT_SEARCH_RESULT, null, selectedResult);
        }
    }

    private void selectBaUnit(BaUnitSearchResultBean selectedResult) {
        if (selectedResult != null) {
            firePropertyChange(SELECT_BAUNIT_SEARCH_RESULT, null, selectedResult);
        }
    }

    public void clickFind() {
        //btnSearchActionPerformed(null);
    }

    /**
     * Opens property assignment form with selected properties.
     *
     * @param propList Selected properties to assign.
     */
    private void assignProperty(final List<BaUnitSearchResultBean> propList) {
        if (propList == null || propList.size() < 1) {
            return;
        }
        PropertyAssignmentDialog form = new PropertyAssignmentDialog(propList, WindowUtility.getTopFrame(), true);
        WindowUtility.centerForm(form);
        form.addPropertyChangeListener(listener);
        form.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        baUnitSearchResults = new org.sola.clients.beans.administrative.BaUnitSearchResultListBean();
        baUnitSearchParams = new org.sola.clients.beans.administrative.BaUnitSearchParamsBean();
        popUpSearchResults = new javax.swing.JPopupMenu();
        menuOpenBaUnit = new javax.swing.JMenuItem();
        menuSelectBaUnit = new javax.swing.JMenuItem();
        searchParamsSL = new org.sola.clients.beans.administrative.BaUnitSearchParamsBean();
        searchResultsSL = new org.sola.clients.beans.administrative.BaUnitSearchResultListBean();
        landUseTypeListBean = new org.sola.clients.beans.referencedata.LandUseTypeListBean();
        popUpSLSearchResults = new javax.swing.JPopupMenu();
        menuSLOpen = new javax.swing.JMenuItem();
        menuSLSelect = new javax.swing.JMenuItem();
        menuSLAssign = new javax.swing.JMenuItem();
        rrrTypeListBean = new org.sola.clients.beans.referencedata.RrrTypeListBean(true);
        rrrSubTypeListBean = new org.sola.clients.beans.referencedata.RrrSubTypeListBean(true);
        jPanel24 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        applicationResults = new org.sola.clients.beans.administrative.BaUnitSearchResultListBean();
        popUpAppResults = new javax.swing.JPopupMenu();
        menuAppSelect = new javax.swing.JMenuItem();
        tabMain = new javax.swing.JTabbedPane();
        tabApplication = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        btnAppSelect = new org.sola.clients.swing.common.buttons.BtnSelect();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblApplicationProperty = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        tabStateLand = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtSLRefNum = new javax.swing.JTextField();
        jPanel21 = new javax.swing.JPanel();
        txtDescription = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtSLParcelNum = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtSLPlanNum = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtSLInterestRefNum = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        cbxRrrType = new javax.swing.JComboBox();
        jPanel12 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtSLRightholder = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtSLLocality = new javax.swing.JTextField();
        jPanel23 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        cbxSLPurpose = new javax.swing.JComboBox();
        jPanel22 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtSLPropertyManager = new javax.swing.JTextField();
        jPanel26 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        cbxRrrSubType = new javax.swing.JComboBox();
        jPanel19 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtSLDocRef = new javax.swing.JTextField();
        jToolBar2 = new javax.swing.JToolBar();
        btnSLSearch = new org.sola.clients.swing.common.buttons.BtnSearch();
        btnSLClear = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnSLOpen = new org.sola.clients.swing.common.buttons.BtnOpen();
        btnSLSelect = new org.sola.clients.swing.common.buttons.BtnSelect();
        btnSLAssign = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel15 = new javax.swing.JLabel();
        lblSLSearchResultCount = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSLProperty = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();
        tabProperty = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNameFirstPart = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtNameLastPart = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtRightholder = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        txtLocality = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtParcelNum = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtPlanNum = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSearch = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnOpenBaUnit = new javax.swing.JButton();
        btnSelectBaUnit = new org.sola.clients.swing.common.buttons.BtnSelect();
        separator1 = new javax.swing.JToolBar.Separator();
        lblSearchResult = new javax.swing.JLabel();
        lblSearchResultCount = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSearchResults = new org.sola.clients.swing.common.controls.JTableWithDefaultStyles();

        popUpSearchResults.setName("popUpSearchResults"); // NOI18N

        menuOpenBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/sola/clients/swing/ui/administrative/Bundle"); // NOI18N
        menuOpenBaUnit.setText(bundle.getString("BaUnitSearchPanel.menuOpenBaUnit.text")); // NOI18N
        menuOpenBaUnit.setName("menuOpenBaUnit"); // NOI18N
        menuOpenBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenBaUnitActionPerformed(evt);
            }
        });
        popUpSearchResults.add(menuOpenBaUnit);

        menuSelectBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/select.png"))); // NOI18N
        menuSelectBaUnit.setText(bundle.getString("BaUnitSearchPanel.menuSelectBaUnit.text")); // NOI18N
        menuSelectBaUnit.setName("menuSelectBaUnit"); // NOI18N
        menuSelectBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSelectBaUnitActionPerformed(evt);
            }
        });
        popUpSearchResults.add(menuSelectBaUnit);

        popUpSLSearchResults.setName("popUpSLSearchResults"); // NOI18N

        menuSLOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        menuSLOpen.setText(bundle.getString("BaUnitSearchPanel.menuSLOpen.text")); // NOI18N
        menuSLOpen.setName("menuSLOpen"); // NOI18N
        menuSLOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSLOpenActionPerformed(evt);
            }
        });
        popUpSLSearchResults.add(menuSLOpen);

        menuSLSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/select.png"))); // NOI18N
        menuSLSelect.setText(bundle.getString("BaUnitSearchPanel.menuSLSelect.text")); // NOI18N
        menuSLSelect.setName("menuSLSelect"); // NOI18N
        menuSLSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSLSelectActionPerformed(evt);
            }
        });
        popUpSLSearchResults.add(menuSLSelect);

        menuSLAssign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        menuSLAssign.setText(bundle.getString("BaUnitSearchPanel.menuSLAssign.text")); // NOI18N
        menuSLAssign.setName("menuSLAssign"); // NOI18N
        menuSLAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSLAssignActionPerformed(evt);
            }
        });
        popUpSLSearchResults.add(menuSLAssign);

        jPanel24.setName("jPanel24"); // NOI18N

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel25.setName("jPanel25"); // NOI18N

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        popUpAppResults.setName("popUpAppResults"); // NOI18N

        menuAppSelect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/select.png"))); // NOI18N
        menuAppSelect.setText(bundle.getString("BaUnitSearchPanel.menuAppSelect.text")); // NOI18N
        menuAppSelect.setName("menuAppSelect"); // NOI18N
        menuAppSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAppSelectActionPerformed(evt);
            }
        });
        popUpAppResults.add(menuAppSelect);

        tabMain.setName("tabMain"); // NOI18N

        tabApplication.setName("tabApplication"); // NOI18N

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setName("jToolBar3"); // NOI18N

        btnAppSelect.setName("btnAppSelect"); // NOI18N
        btnAppSelect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAppSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAppSelectActionPerformed(evt);
            }
        });
        jToolBar3.add(btnAppSelect);

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblApplicationProperty.setName("tblApplicationProperty"); // NOI18N
        tblApplicationProperty.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitSearchResults}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, applicationResults, eLProperty, tblApplicationProperty);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stateLandName}"));
        columnBinding.setColumnName("State Land Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${landUseType.displayValue}"));
        columnBinding.setColumnName("Land Use Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${propertyManager}"));
        columnBinding.setColumnName("Property Manager");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rightholders}"));
        columnBinding.setColumnName("Rightholders");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${parcels}"));
        columnBinding.setColumnName("Parcels");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${locality}"));
        columnBinding.setColumnName("Locality");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stateLandStatus.displayValue}"));
        columnBinding.setColumnName("State Land Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, applicationResults, org.jdesktop.beansbinding.ELProperty.create("${selectedBaUnitSearchResult}"), tblApplicationProperty, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblApplicationProperty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblApplicationPropertyMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblApplicationProperty);
        if (tblApplicationProperty.getColumnModel().getColumnCount() > 0) {
            tblApplicationProperty.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblApplicationProperty.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("BaUnitSearchPanel.tblApplicationProperty.columnModel.title0")); // NOI18N
            tblApplicationProperty.getColumnModel().getColumn(1).setPreferredWidth(30);
            tblApplicationProperty.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("BaUnitSearchPanel.tblApplicationProperty.columnModel.title1")); // NOI18N
            tblApplicationProperty.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("BaUnitSearchPanel.tblApplicationProperty.columnModel.title2")); // NOI18N
            tblApplicationProperty.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("BaUnitSearchPanel.tblApplicationProperty.columnModel.title3")); // NOI18N
            tblApplicationProperty.getColumnModel().getColumn(3).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblApplicationProperty.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("BaUnitSearchPanel.tblApplicationProperty.columnModel.title4")); // NOI18N
            tblApplicationProperty.getColumnModel().getColumn(4).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblApplicationProperty.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("BaUnitSearchPanel.tblApplicationProperty.columnModel.title5")); // NOI18N
            tblApplicationProperty.getColumnModel().getColumn(5).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblApplicationProperty.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("BaUnitSearchPanel.tblApplicationProperty.columnModel.title6")); // NOI18N
            tblApplicationProperty.getColumnModel().getColumn(7).setPreferredWidth(30);
            tblApplicationProperty.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("BaUnitSearchPanel.tblApplicationProperty.columnModel.title7")); // NOI18N
        }

        javax.swing.GroupLayout tabApplicationLayout = new javax.swing.GroupLayout(tabApplication);
        tabApplication.setLayout(tabApplicationLayout);
        tabApplicationLayout.setHorizontalGroup(
            tabApplicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabApplicationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabApplicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabApplicationLayout.setVerticalGroup(
            tabApplicationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabApplicationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3)
                .addContainerGap())
        );

        tabMain.addTab(bundle.getString("BaUnitSearchPanel.tabApplication.TabConstraints.tabTitle"), tabApplication); // NOI18N

        tabStateLand.setName("tabStateLand"); // NOI18N

        jPanel10.setName("jPanel10"); // NOI18N
        jPanel10.setLayout(new java.awt.GridLayout(1, 2, 15, 0));

        jPanel11.setName("jPanel11"); // NOI18N
        jPanel11.setLayout(new java.awt.GridLayout(3, 2, 15, 0));

        jPanel14.setName("jPanel14"); // NOI18N

        jLabel7.setText(bundle.getString("BaUnitSearchPanel.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        txtSLRefNum.setName("txtSLRefNum"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${nameLastPart}"), txtSLRefNum, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(txtSLRefNum)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSLRefNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel14);

        jPanel21.setName("jPanel21"); // NOI18N

        txtDescription.setName("txtDescription"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${description}"), txtDescription, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jLabel19.setText(bundle.getString("BaUnitSearchPanel.jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtDescription)
            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel11.add(jPanel21);

        jPanel16.setName("jPanel16"); // NOI18N

        jLabel11.setText(bundle.getString("BaUnitSearchPanel.jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        txtSLParcelNum.setName("txtSLParcelNum"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${parcelNumber}"), txtSLParcelNum, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(txtSLParcelNum)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSLParcelNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel16);

        jPanel17.setName("jPanel17"); // NOI18N

        jLabel12.setText(bundle.getString("BaUnitSearchPanel.jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        txtSLPlanNum.setName("txtSLPlanNum"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${planNumber}"), txtSLPlanNum, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(txtSLPlanNum)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSLPlanNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel17);

        jPanel18.setName("jPanel18"); // NOI18N

        jLabel13.setText(bundle.getString("BaUnitSearchPanel.jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        txtSLInterestRefNum.setName("txtSLInterestRefNum"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${interestRefNum}"), txtSLInterestRefNum, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtSLInterestRefNum)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSLInterestRefNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel18);

        jPanel20.setName("jPanel20"); // NOI18N

        jLabel20.setText(bundle.getString("BaUnitSearchPanel.jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        cbxRrrType.setName("cbxRrrType"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrTypeBeanList}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrTypeListBean, eLProperty, cbxRrrType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${rrrType}"), cbxRrrType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbxRrrType, 0, 139, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRrrType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.add(jPanel20);

        jPanel10.add(jPanel11);

        jPanel12.setName("jPanel12"); // NOI18N
        jPanel12.setLayout(new java.awt.GridLayout(3, 2, 15, 0));

        jPanel15.setName("jPanel15"); // NOI18N

        jLabel8.setText(bundle.getString("BaUnitSearchPanel.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        txtSLRightholder.setName("txtSLRightholder"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${ownerName}"), txtSLRightholder, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtSLRightholder)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSLRightholder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel15);

        jPanel13.setName("jPanel13"); // NOI18N

        jLabel6.setText(bundle.getString("BaUnitSearchPanel.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtSLLocality.setName("txtSLLocality"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${locality}"), txtSLLocality, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(txtSLLocality)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSLLocality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel13);

        jPanel23.setName("jPanel23"); // NOI18N

        jLabel10.setText(bundle.getString("BaUnitSearchPanel.jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        cbxSLPurpose.setName("cbxSLPurpose"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${landUseTypeList}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, landUseTypeListBean, eLProperty, cbxSLPurpose);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${landUseType}"), cbxSLPurpose, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(cbxSLPurpose, 0, 139, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxSLPurpose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel23);

        jPanel22.setName("jPanel22"); // NOI18N

        jLabel9.setText(bundle.getString("BaUnitSearchPanel.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        txtSLPropertyManager.setName("txtSLPropertyManager"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${propertyManager}"), txtSLPropertyManager, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtSLPropertyManager)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSLPropertyManager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel22);

        jPanel26.setName("jPanel26"); // NOI18N

        jLabel21.setText(bundle.getString("BaUnitSearchPanel.jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        cbxRrrSubType.setName("cbxRrrSubType"); // NOI18N

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${rrrSubTypes}");
        jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rrrSubTypeListBean, eLProperty, cbxRrrSubType);
        bindingGroup.addBinding(jComboBoxBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${rrrSubType}"), cbxRrrSubType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(cbxRrrSubType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxRrrSubType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel26);

        jPanel19.setName("jPanel19"); // NOI18N

        jLabel14.setText(bundle.getString("BaUnitSearchPanel.jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        txtSLDocRef.setName("txtSLDocRef"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchParamsSL, org.jdesktop.beansbinding.ELProperty.create("${documentNumber}"), txtSLDocRef, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(txtSLDocRef)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSLDocRef, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.add(jPanel19);

        jPanel10.add(jPanel12);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setName("jToolBar2"); // NOI18N

        btnSLSearch.setName("btnSLSearch"); // NOI18N
        btnSLSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSLSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSLSearchActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSLSearch);

        btnSLClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/eraser.png"))); // NOI18N
        btnSLClear.setText(bundle.getString("BaUnitSearchPanel.btnSLClear.text")); // NOI18N
        btnSLClear.setFocusable(false);
        btnSLClear.setName("btnSLClear"); // NOI18N
        btnSLClear.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSLClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSLClearActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSLClear);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jToolBar2.add(jSeparator1);

        btnSLOpen.setName("btnSLOpen"); // NOI18N
        btnSLOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSLOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSLOpenActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSLOpen);

        btnSLSelect.setName("btnSLSelect"); // NOI18N
        btnSLSelect.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSLSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSLSelectActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSLSelect);

        btnSLAssign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/assign.png"))); // NOI18N
        btnSLAssign.setText(bundle.getString("BaUnitSearchPanel.btnSLAssign.text")); // NOI18N
        btnSLAssign.setFocusable(false);
        btnSLAssign.setName("btnSLAssign"); // NOI18N
        btnSLAssign.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSLAssign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSLAssignActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSLAssign);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jToolBar2.add(jSeparator2);

        jLabel15.setText(bundle.getString("BaUnitSearchPanel.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        jToolBar2.add(jLabel15);

        lblSLSearchResultCount.setText(bundle.getString("BaUnitSearchPanel.lblSLSearchResultCount.text")); // NOI18N
        lblSLSearchResultCount.setName("lblSLSearchResultCount"); // NOI18N
        jToolBar2.add(lblSLSearchResultCount);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblSLProperty.setComponentPopupMenu(popUpSLSearchResults);
        tblSLProperty.setName("tblSLProperty"); // NOI18N
        tblSLProperty.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitSearchResults}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchResultsSL, eLProperty, tblSLProperty);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${checked}"));
        columnBinding.setColumnName("Checked");
        columnBinding.setColumnClass(Boolean.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stateLandName}"));
        columnBinding.setColumnName("State Land Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${landUseType.displayValue}"));
        columnBinding.setColumnName("Land Use Type.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${propertyManager}"));
        columnBinding.setColumnName("Property Manager");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rightholders}"));
        columnBinding.setColumnName("Rightholders");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${parcels}"));
        columnBinding.setColumnName("Parcels");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${locality}"));
        columnBinding.setColumnName("Locality");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${stateLandStatus.displayValue}"));
        columnBinding.setColumnName("State Land Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, searchResultsSL, org.jdesktop.beansbinding.ELProperty.create("${selectedBaUnitSearchResult}"), tblSLProperty, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblSLProperty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSLPropertyMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblSLProperty);
        if (tblSLProperty.getColumnModel().getColumnCount() > 0) {
            tblSLProperty.getColumnModel().getColumn(0).setMinWidth(25);
            tblSLProperty.getColumnModel().getColumn(0).setPreferredWidth(25);
            tblSLProperty.getColumnModel().getColumn(0).setMaxWidth(25);
            tblSLProperty.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("BaUnitSearchPanel.jTableWithDefaultStyles1.columnModel.title7_1")); // NOI18N
            tblSLProperty.getColumnModel().getColumn(1).setPreferredWidth(30);
            tblSLProperty.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("BaUnitSearchPanel.jTableWithDefaultStyles1.columnModel.title0_2")); // NOI18N
            tblSLProperty.getColumnModel().getColumn(2).setPreferredWidth(30);
            tblSLProperty.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("BaUnitSearchPanel.jTableWithDefaultStyles1.columnModel.title6_1")); // NOI18N
            tblSLProperty.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("BaUnitSearchPanel.jTableWithDefaultStyles1.columnModel.title8")); // NOI18N
            tblSLProperty.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("BaUnitSearchPanel.jTableWithDefaultStyles1.columnModel.title3_1")); // NOI18N
            tblSLProperty.getColumnModel().getColumn(4).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblSLProperty.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("BaUnitSearchPanel.jTableWithDefaultStyles1.columnModel.title4")); // NOI18N
            tblSLProperty.getColumnModel().getColumn(5).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblSLProperty.getColumnModel().getColumn(6).setHeaderValue(bundle.getString("BaUnitSearchPanel.jTableWithDefaultStyles1.columnModel.title5")); // NOI18N
            tblSLProperty.getColumnModel().getColumn(6).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblSLProperty.getColumnModel().getColumn(7).setHeaderValue(bundle.getString("BaUnitSearchPanel.jTableWithDefaultStyles1.columnModel.title6")); // NOI18N
            tblSLProperty.getColumnModel().getColumn(8).setPreferredWidth(30);
            tblSLProperty.getColumnModel().getColumn(8).setHeaderValue(bundle.getString("BaUnitSearchPanel.jTableWithDefaultStyles1.columnModel.title7")); // NOI18N
        }

        javax.swing.GroupLayout tabStateLandLayout = new javax.swing.GroupLayout(tabStateLand);
        tabStateLand.setLayout(tabStateLandLayout);
        tabStateLandLayout.setHorizontalGroup(
            tabStateLandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabStateLandLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabStateLandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabStateLandLayout.setVerticalGroup(
            tabStateLandLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabStateLandLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabMain.addTab(bundle.getString("BaUnitSearchPanel.tabStateLand.TabConstraints.tabTitle"), tabStateLand); // NOI18N

        tabProperty.setName("tabProperty"); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridLayout(2, 4, 15, 0));

        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText(bundle.getString("BaUnitSearchPanel.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        txtNameFirstPart.setName("txtNameFirstPart"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitSearchParams, org.jdesktop.beansbinding.ELProperty.create("${nameFirstPart}"), txtNameFirstPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtNameFirstPart)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNameFirstPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel1);

        jPanel3.setName("jPanel3"); // NOI18N

        jLabel3.setText(bundle.getString("BaUnitSearchPanel.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtNameLastPart.setName("txtNameLastPart"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitSearchParams, org.jdesktop.beansbinding.ELProperty.create("${nameLastPart}"), txtNameLastPart, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtNameLastPart, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtNameLastPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel3);

        jPanel2.setName("jPanel2"); // NOI18N

        jLabel2.setText(bundle.getString("BaUnitSearchPanel.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        txtRightholder.setName("txtRightholder"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitSearchParams, org.jdesktop.beansbinding.ELProperty.create("${ownerName}"), txtRightholder, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtRightholder, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRightholder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel2);

        jPanel5.setName("jPanel5"); // NOI18N

        jLabel16.setText(bundle.getString("BaUnitSearchPanel.jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        txtLocality.setName("txtLocality"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitSearchParams, org.jdesktop.beansbinding.ELProperty.create("${locality}"), txtLocality, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(txtLocality)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLocality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel5);

        jPanel6.setName("jPanel6"); // NOI18N

        jLabel4.setText(bundle.getString("BaUnitSearchPanel.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        txtParcelNum.setName("txtParcelNum"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitSearchParams, org.jdesktop.beansbinding.ELProperty.create("${parcelNumber}"), txtParcelNum, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(txtParcelNum)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtParcelNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel6);

        jPanel9.setName("jPanel9"); // NOI18N

        jLabel5.setText(bundle.getString("BaUnitSearchPanel.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txtPlanNum.setName("txtPlanNum"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitSearchParams, org.jdesktop.beansbinding.ELProperty.create("${planNumber}"), txtPlanNum, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addComponent(txtPlanNum)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlanNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel9);

        jPanel8.setName("jPanel8"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 139, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 52, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel8);

        jPanel7.setName("jPanel7"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 139, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 52, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel7);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/search.png"))); // NOI18N
        btnSearch.setText(bundle.getString("BaUnitSearchPanel.btnSearch.text")); // NOI18N
        btnSearch.setName("btnSearch"); // NOI18N
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSearch);

        btnClear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/eraser.png"))); // NOI18N
        btnClear.setText(bundle.getString("BaUnitSearchPanel.btnClear.text")); // NOI18N
        btnClear.setName(bundle.getString("BaUnitSearchPanel.btnClear.name")); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClear);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jToolBar1.add(jSeparator3);

        btnOpenBaUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/folder-open-document.png"))); // NOI18N
        btnOpenBaUnit.setText(bundle.getString("BaUnitSearchPanel.btnOpenBaUnit.text")); // NOI18N
        btnOpenBaUnit.setFocusable(false);
        btnOpenBaUnit.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpenBaUnit.setName("btnOpenBaUnit"); // NOI18N
        btnOpenBaUnit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenBaUnitActionPerformed(evt);
            }
        });
        jToolBar1.add(btnOpenBaUnit);

        btnSelectBaUnit.setName("btnSelectBaUnit"); // NOI18N
        btnSelectBaUnit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSelectBaUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectBaUnitActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSelectBaUnit);

        separator1.setName("separator1"); // NOI18N
        jToolBar1.add(separator1);

        lblSearchResult.setText(bundle.getString("BaUnitSearchPanel.lblSearchResult.text")); // NOI18N
        lblSearchResult.setName("lblSearchResult"); // NOI18N
        jToolBar1.add(lblSearchResult);

        lblSearchResultCount.setFont(LafManager.getInstance().getLabFontBold());
        lblSearchResultCount.setText(bundle.getString("BaUnitSearchPanel.lblSearchResultCount.text")); // NOI18N
        lblSearchResultCount.setName("lblSearchResultCount"); // NOI18N
        jToolBar1.add(lblSearchResultCount);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblSearchResults.setComponentPopupMenu(popUpSearchResults);
        tblSearchResults.setName("tblSearchResults"); // NOI18N
        tblSearchResults.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${baUnitSearchResults}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitSearchResults, eLProperty, tblSearchResults);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${rightholders}"));
        columnBinding.setColumnName("Rightholders");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${locality}"));
        columnBinding.setColumnName("Locality");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${parcels}"));
        columnBinding.setColumnName("Parcels");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${description}"));
        columnBinding.setColumnName("Description");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${registrationStatus.displayValue}"));
        columnBinding.setColumnName("Registration Status.display Value");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, baUnitSearchResults, org.jdesktop.beansbinding.ELProperty.create("${selectedBaUnitSearchResult}"), tblSearchResults, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        tblSearchResults.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSearchResultsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblSearchResults);
        if (tblSearchResults.getColumnModel().getColumnCount() > 0) {
            tblSearchResults.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblSearchResults.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("BaUnitSearchPanel.tblSearchResults.columnModel.title0")); // NOI18N
            tblSearchResults.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("BaUnitSearchPanel.tableSearchResults.columnModel.title3")); // NOI18N
            tblSearchResults.getColumnModel().getColumn(1).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblSearchResults.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("BaUnitSearchPanel.tableSearchResults.columnModel.title5")); // NOI18N
            tblSearchResults.getColumnModel().getColumn(2).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblSearchResults.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("BaUnitSearchPanel.tableSearchResults.columnModel.title5_1")); // NOI18N
            tblSearchResults.getColumnModel().getColumn(3).setCellRenderer(new CellDelimitedListRenderer("::::"));
            tblSearchResults.getColumnModel().getColumn(4).setHeaderValue(bundle.getString("BaUnitSearchPanel.tableSearchResults.columnModel.title6")); // NOI18N
            tblSearchResults.getColumnModel().getColumn(5).setPreferredWidth(30);
            tblSearchResults.getColumnModel().getColumn(5).setHeaderValue(bundle.getString("BaUnitSearchPanel.tableSearchResults.columnModel.title4")); // NOI18N
        }

        javax.swing.GroupLayout tabPropertyLayout = new javax.swing.GroupLayout(tabProperty);
        tabProperty.setLayout(tabPropertyLayout);
        tabPropertyLayout.setHorizontalGroup(
            tabPropertyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPropertyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabPropertyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE))
                .addContainerGap())
        );
        tabPropertyLayout.setVerticalGroup(
            tabPropertyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPropertyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabMain.addTab(bundle.getString("BaUnitSearchPanel.tabProperty.TabConstraints.tabTitle"), tabProperty); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabMain)
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        executeSearch(baUnitSearchParams, lblSearchResultCount, baUnitSearchResults);
    }//GEN-LAST:event_btnSearchActionPerformed

    private void tblSearchResultsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSearchResultsMouseClicked
        if (evt.getClickCount() == 2) {
            if (defaultActionOpen) {
                openBaUnit(baUnitSearchResults.getSelectedBaUnitSearchResult());
            } else {
                selectBaUnit(baUnitSearchResults.getSelectedBaUnitSearchResult());
            }
        }
    }//GEN-LAST:event_tblSearchResultsMouseClicked

    private void btnOpenBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenBaUnitActionPerformed
        openBaUnit(baUnitSearchResults.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_btnOpenBaUnitActionPerformed

    private void menuOpenBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenBaUnitActionPerformed
        openBaUnit(baUnitSearchResults.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_menuOpenBaUnitActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        baUnitSearchParams.clear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnSLSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSLSearchActionPerformed
        executeSearch(searchParamsSL, lblSLSearchResultCount, searchResultsSL);
    }//GEN-LAST:event_btnSLSearchActionPerformed

    private void btnSLClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSLClearActionPerformed
        searchParamsSL.clear();
    }//GEN-LAST:event_btnSLClearActionPerformed

    private void btnSLOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSLOpenActionPerformed
        openBaUnit(searchResultsSL.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_btnSLOpenActionPerformed

    private void btnSLSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSLSelectActionPerformed
        selectBaUnit(searchResultsSL.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_btnSLSelectActionPerformed

    private void btnSelectBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectBaUnitActionPerformed
        selectBaUnit(baUnitSearchResults.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_btnSelectBaUnitActionPerformed

    private void tblSLPropertyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSLPropertyMouseClicked
        if (evt.getClickCount() == 2) {
            if (defaultActionOpen) {
                openBaUnit(searchResultsSL.getSelectedBaUnitSearchResult());
            } else {
                selectBaUnit(searchResultsSL.getSelectedBaUnitSearchResult());
            }
        }
    }//GEN-LAST:event_tblSLPropertyMouseClicked

    private void menuSelectBaUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSelectBaUnitActionPerformed
        selectBaUnit(baUnitSearchResults.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_menuSelectBaUnitActionPerformed

    private void menuSLOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSLOpenActionPerformed
        openBaUnit(searchResultsSL.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_menuSLOpenActionPerformed

    private void menuSLSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSLSelectActionPerformed
        selectBaUnit(searchResultsSL.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_menuSLSelectActionPerformed

    private void btnSLAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSLAssignActionPerformed
        assignProperty(searchResultsSL.getChecked(true));
    }//GEN-LAST:event_btnSLAssignActionPerformed

    private void menuSLAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSLAssignActionPerformed
        assignProperty(searchResultsSL.getChecked(true));
    }//GEN-LAST:event_menuSLAssignActionPerformed

    private void btnAppSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAppSelectActionPerformed
        selectBaUnit(applicationResults.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_btnAppSelectActionPerformed

    private void menuAppSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAppSelectActionPerformed
        selectBaUnit(applicationResults.getSelectedBaUnitSearchResult());
    }//GEN-LAST:event_menuAppSelectActionPerformed

    private void tblApplicationPropertyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblApplicationPropertyMouseClicked
        if (evt.getClickCount() == 2) {
            if (defaultActionOpen) {
                openBaUnit(applicationResults.getSelectedBaUnitSearchResult());
            } else {
                selectBaUnit(applicationResults.getSelectedBaUnitSearchResult());
            }
        }
    }//GEN-LAST:event_tblApplicationPropertyMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.sola.clients.beans.administrative.BaUnitSearchResultListBean applicationResults;
    private org.sola.clients.beans.administrative.BaUnitSearchParamsBean baUnitSearchParams;
    private org.sola.clients.beans.administrative.BaUnitSearchResultListBean baUnitSearchResults;
    private org.sola.clients.swing.common.buttons.BtnSelect btnAppSelect;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnOpenBaUnit;
    private javax.swing.JButton btnSLAssign;
    private javax.swing.JButton btnSLClear;
    private org.sola.clients.swing.common.buttons.BtnOpen btnSLOpen;
    private org.sola.clients.swing.common.buttons.BtnSearch btnSLSearch;
    private org.sola.clients.swing.common.buttons.BtnSelect btnSLSelect;
    private javax.swing.JButton btnSearch;
    private org.sola.clients.swing.common.buttons.BtnSelect btnSelectBaUnit;
    private javax.swing.JComboBox cbxRrrSubType;
    private javax.swing.JComboBox cbxRrrType;
    private javax.swing.JComboBox cbxSLPurpose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private org.sola.clients.beans.referencedata.LandUseTypeListBean landUseTypeListBean;
    private javax.swing.JLabel lblSLSearchResultCount;
    private javax.swing.JLabel lblSearchResult;
    private javax.swing.JLabel lblSearchResultCount;
    private javax.swing.JMenuItem menuAppSelect;
    private javax.swing.JMenuItem menuOpenBaUnit;
    private javax.swing.JMenuItem menuSLAssign;
    private javax.swing.JMenuItem menuSLOpen;
    private javax.swing.JMenuItem menuSLSelect;
    private javax.swing.JMenuItem menuSelectBaUnit;
    private javax.swing.JPopupMenu popUpAppResults;
    private javax.swing.JPopupMenu popUpSLSearchResults;
    private javax.swing.JPopupMenu popUpSearchResults;
    private org.sola.clients.beans.referencedata.RrrSubTypeListBean rrrSubTypeListBean;
    private org.sola.clients.beans.referencedata.RrrTypeListBean rrrTypeListBean;
    private org.sola.clients.beans.administrative.BaUnitSearchParamsBean searchParamsSL;
    private org.sola.clients.beans.administrative.BaUnitSearchResultListBean searchResultsSL;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JPanel tabApplication;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JPanel tabProperty;
    private javax.swing.JPanel tabStateLand;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblApplicationProperty;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblSLProperty;
    private org.sola.clients.swing.common.controls.JTableWithDefaultStyles tblSearchResults;
    private javax.swing.JTextField txtDescription;
    private javax.swing.JTextField txtLocality;
    private javax.swing.JTextField txtNameFirstPart;
    private javax.swing.JTextField txtNameLastPart;
    private javax.swing.JTextField txtParcelNum;
    private javax.swing.JTextField txtPlanNum;
    private javax.swing.JTextField txtRightholder;
    private javax.swing.JTextField txtSLDocRef;
    private javax.swing.JTextField txtSLInterestRefNum;
    private javax.swing.JTextField txtSLLocality;
    private javax.swing.JTextField txtSLParcelNum;
    private javax.swing.JTextField txtSLPlanNum;
    private javax.swing.JTextField txtSLPropertyManager;
    private javax.swing.JTextField txtSLRefNum;
    private javax.swing.JTextField txtSLRightholder;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
