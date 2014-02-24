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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractVersionedBean;
import org.sola.clients.beans.administrative.validation.RrrShareCheck;
import org.sola.clients.beans.controls.SolaList;
import org.sola.clients.beans.party.PartySummaryBean;
import org.sola.clients.beans.validation.Localized;
import org.sola.clients.beans.validation.NoDuplicates;
import org.sola.common.messaging.ClientMessage;
import org.sola.webservices.transferobjects.EntityAction;
import org.sola.webservices.transferobjects.administrative.RrrShareTO;

/**
 * Contains properties and methods to manage <b>RRR share</b> object of the 
 * domain model. Could be populated from the {@link RrrShareTO} object.
 */
@RrrShareCheck
public class RrrShareBean extends AbstractVersionedBean {

    public static final String SELECTED_RIGHTHOLDER_PROPERTY = "selectedRightHolder";
    private String rrrId;
    @NotNull(message = ClientMessage.CHECK_NOTNULL_NOMINATOR, payload = Localized.class)
    @Min(value = 1, message = ClientMessage.CHECK_MIN_NOMINATOR, payload = Localized.class)
    private Short nominator;
    @Min(value = 1, message = ClientMessage.CHECK_MIN_DENOMINATOR, payload = Localized.class)
    @NotNull(message = ClientMessage.CHECK_NOTNULL_DENOMINATOR, payload = Localized.class)
    private Short denominator;
    private SolaList<PartySummaryBean> rightHolderList;
    private transient PartySummaryBean selectedRightHolder;

    public RrrShareBean() {
        super();
        rightHolderList = new SolaList();
    }

    public Short getDenominator() {
        return denominator;
    }

    public void setDenominator(Short denominator) {
        this.denominator = denominator;
    }

    public String getShare() {
        String share = "";
        if (nominator != null) {
            share = nominator.toString();
        }
        if (denominator != null) {
            share += "/" + denominator.toString();
        }
        return share;
    }

    public Short getNominator() {
        return nominator;
    }

    public void setNominator(Short nominator) {
        this.nominator = nominator;
    }

    public SolaList<PartySummaryBean> getRightHolderList() {
        return rightHolderList;
    }

    @NoDuplicates(message = ClientMessage.CHECK_NODUPLI_RIGHTHOLDERLIST, payload = Localized.class)
    @Size(min = 1, message = ClientMessage.CHECK_SIZE_FILTERRIGHTHOLDER, payload = Localized.class)
    public ObservableList<PartySummaryBean> getFilteredRightHolderList() {
        return rightHolderList.getFilteredList();
    }

    /** Returns read only string combining list of right holders.*/
    public String getRightHoldersStringList() {
        String result = "";
        for (PartySummaryBean rightHolder : getFilteredRightHolderList()) {
            if (result.length() > 0) {
                result = result + "\r\n";
            }
            result = result + "- " + rightHolder.getName();
            if (rightHolder.getLastName() != null && rightHolder.getLastName().length() > 0) {
                result = result + " " + rightHolder.getLastName();
            }
            result = result + ";";
        }
        return result;
    }

    public void setRightHolderList(SolaList<PartySummaryBean> rightHolderList) {
        this.rightHolderList = rightHolderList;
    }

    public void removeSelectedRightHolder() {
        if (selectedRightHolder != null && rightHolderList != null) {
            rightHolderList.safeRemove(selectedRightHolder, EntityAction.DELETE);
        }
    }

    public String getRrrId() {
        return rrrId;
    }

    public void setRrrId(String rrrId) {
        this.rrrId = rrrId;
    }

    public PartySummaryBean getSelectedRightHolder() {
        return selectedRightHolder;
    }

    public void setSelectedRightHolder(PartySummaryBean selectedRightHolder) {
        PartySummaryBean oldValue = this.selectedRightHolder;
        this.selectedRightHolder = selectedRightHolder;
        propertySupport.firePropertyChange(SELECTED_RIGHTHOLDER_PROPERTY, oldValue, this.selectedRightHolder);
    }

    public void addOrUpdateRightholder(PartySummaryBean rightholder) {
        if (rightholder != null && rightHolderList != null) {
            if (rightHolderList.contains(rightholder)) {
                rightHolderList.set(rightHolderList.indexOf(rightholder), rightholder);
            } else {
                rightHolderList.addAsNew(rightholder);
            }
        }
    }
}
