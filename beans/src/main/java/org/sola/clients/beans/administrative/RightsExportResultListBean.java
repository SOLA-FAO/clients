/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.administrative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.controls.SolaObservableList;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.common.DateUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.search.RightsExportParamsTO;

/**
 * Holds list of {@link RightsExportResultBean} and allows to do rights search.
 */
public class RightsExportResultListBean extends AbstractBindingBean {

    public static final String LIST_ITEM_CHECKED = "listItemChecked";

    private class RightsListItemListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(RightsExportResultBean.IS_CHECKED_PROPERTY)) {
                if (getRightsList() != null) {
                    for (RightsExportResultBean resultBean : getRightsList()) {
                        if (resultBean.isChecked()) {
                            propertySupport.firePropertyChange(LIST_ITEM_CHECKED, false, true);
                            return;
                        }
                    }
                }
                propertySupport.firePropertyChange(LIST_ITEM_CHECKED, true, false);
            }
        }
    }
    private RightsListItemListener rightsListItemListener = new RightsListItemListener();
    private RightsListListener rightsListListener = new RightsListListener();

    private class RightsListListener implements ObservableListListener {

        @Override
        public void listElementsAdded(ObservableList list, int index, int length) {
            for (int i = index; i < length + index; i++) {
                ((RightsExportResultBean) list.get(i)).addPropertyChangeListener(rightsListItemListener);
            }
        }

        @Override
        public void listElementsRemoved(ObservableList list, int index, List oldElements) {
            for (Object rightExportResult : oldElements) {
                ((RightsExportResultBean) rightExportResult).removePropertyChangeListener(rightsListItemListener);
            }
        }

        @Override
        public void listElementReplaced(ObservableList list, int index, Object oldElement) {
            ((RightsExportResultBean) oldElement).removePropertyChangeListener(rightsListItemListener);
        }

        @Override
        public void listElementPropertyChanged(ObservableList list, int index) {
        }
    }
    public static final String SELECTED_RIGHT_PROPERTY = "selectedRight";
    private SolaObservableList<RightsExportResultBean> rightsList;
    private RightsExportResultBean selectedRight;

    public RightsExportResultListBean() {
        super();
    }

    public SolaObservableList<RightsExportResultBean> getRightsList() {
        if (rightsList == null) {
            rightsList = new SolaObservableList<RightsExportResultBean>();
            rightsList.addObservableListListener(rightsListListener);
        }
        return rightsList;
    }

    public ArrayList<RightsExportResultBean> getSelectedRightsList() {
        ArrayList<RightsExportResultBean> selectedRights = new ArrayList<RightsExportResultBean>();
        if (getRightsList() != null) {
            for (RightsExportResultBean right : getRightsList()) {
                if (right.isChecked()) {
                    selectedRights.add(right);
                }
            }
        }
        return selectedRights;
    }

    public RightsExportResultBean getSelectedRight() {
        return selectedRight;
    }

    public void setSelectedRight(RightsExportResultBean selectedRight) {
        RightsExportResultBean oldValue = this.selectedRight;
        this.selectedRight = selectedRight;
        propertySupport.firePropertyChange(SELECTED_RIGHT_PROPERTY, oldValue, this.selectedRight);
    }

    /**
     * Searches rights by the given parameters.
     */
    public void search(RightsExportParamsBean searchParams) {
        getRightsList().clear();
        RightsExportParamsTO params = TypeConverters.BeanToTrasferObject(searchParams, RightsExportParamsTO.class);
        TypeConverters.TransferObjectListToBeanList(
                WSManager.getInstance().getSearchService().searchRightsForExport(params),
                RightsExportResultBean.class, (List) getRightsList());
    }

    /**
     * Exports all items in the {@link #getRightsList() } into CSV file.
     *
     * @param file CSV file to create with provided list values
     * @return
     * <code>true</code> if export is successful, otherwise returns
     * <code>false</code> or throws an exception.
     */
    public boolean exportAllToCsv(File file) throws Exception {
        return exportToCsv(file, getRightsList());
    }

    /**
     * Exports all items in the {@link #getSelectedRightsList() } into CSV file.
     *
     * @param file CSV file to create with provided list values
     * @return
     * <code>true</code> if export is successful, otherwise returns
     * <code>false</code> or throws an exception.
     */
    public boolean exportSelectedToCsv(File file) throws Exception {
        return exportToCsv(file, getSelectedRightsList());
    }

    /**
     * Exports a given list of {@link RightsExportParamsBean} into CSV file.
     *
     * @param file CSV file to create with provided list values
     * @param list List of {@link RightsExportParamsBean} to export
     * @return
     * <code>true</code> if export is successful, otherwise returns
     * <code>false</code> or throws an exception.
     */
    public boolean exportToCsv(File file, List<RightsExportResultBean> list) throws Exception {
        if (list == null || list.size() < 1) {
            return false;
        }

        BufferedWriter out = null;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            out = new BufferedWriter(fw);
            int i = list.size();
            
            // Add header
            out.write("ApplicantID;"
                        + "ApplicantName;"
                        + "ApplicantLastName;"
                        + "ApplicantFullName;"
                        + "ApplicantAddress;"
                        + "ApplicantPhone;"
                        + "ApplicantMobile;"
                        + "ApplicantEmail;"
                        + "ApplicantIdTypeName;"
                        + "ApplicantIdNumber;"
                        + "BaUnitId;"
                        + "NameFirstPart;"
                        + "NameLastPart;"
                        + "PropCode;"
                        + "Area;"
                        + "AreaFormatted;"
                        + "RightId;"
                        + "RightType;"
                        + "OwnersFormatted;"
                        + "RegistrationDate;"
                        + "ExpirationDate;"
                        + "Amount;");
            out.newLine();
            
            for (RightsExportResultBean right : list) {
                i = i - 1;
                String regDate = "";
                String expDate = "";
                if (right.getRegistrationDate() != null) {
                    regDate = DateUtility.simpleFormat(right.getRegistrationDate(), "yyyy-MM-dd");
                }
                if (right.getExpirationDate() != null) {
                    expDate = DateUtility.simpleFormat(right.getExpirationDate(), "yyyy-MM-dd");
                }
                out.write(formatString(right.getApplicantId()) + ";"
                        + formatString(right.getApplicantName()) + ";"
                        + formatString(right.getApplicantLastName()) + ";"
                        + formatString(right.getApplicantFullName()) + ";"
                        + formatString(right.getApplicantAddress()) + ";"
                        + formatString(right.getApplicantPhone()) + ";"
                        + formatString(right.getApplicantMobile()) + ";"
                        + formatString(right.getApplicantEmail()) + ";"
                        + formatString(right.getApplicantIdTypeName()) + ";"
                        + formatString(right.getApplicantIdNumber()) + ";"
                        + formatString(right.getBaUnitId()) + ";"
                        + formatString(right.getNameFirstPart()) + ";"
                        + formatString(right.getNameLastPart()) + ";"
                        + formatString(right.getPropCode()) + ";"
                        + formatString(right.getArea().toPlainString()) + ";"
                        + formatString(right.getAreaFormatted()) + ";"
                        + formatString(right.getRightId()) + ";"
                        + formatString(right.getRightType()) + ";"
                        + formatString(right.getOwnersFormatted()) + ";"
                        + formatString(regDate) + ";"
                        + formatString(expDate) + ";"
                        + formatString(right.getAmount().toPlainString()) + ";");
                if (i > 0) {
                    out.newLine();
                }
            }
            return true;
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(RightsExportResultListBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private String formatString(String str) {
        if(str == null){
            return "";
        }
        
        if (str.contains("\"") || str.contains(";")) {
            str = str.replace("\"", "\"\"");
            str = "\"" + str + "\"";
        }
        return str;
    }
}
