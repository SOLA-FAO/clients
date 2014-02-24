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

import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.clients.beans.application.ApplicationServiceBean;
import org.sola.clients.beans.cadastre.CadastreObjectBean;
import org.sola.common.DateUtility;
import org.sola.common.StringUtility;

/**
 * Provides data for different rrr reports.
 */
public class RrrReportBean extends AbstractBindingBean {

    private ApplicationBean application;
    private ApplicationServiceBean service;
    private RrrBean rrr;
    private BaUnitBean baUnit;
    private String freeText;
    
    /** Default constructor. */
    public RrrReportBean() {
        super();
    }
    
    /** Class constructor with initial values for BaUnit, RRR, Application and ApplicationService object. */
    public RrrReportBean(BaUnitBean baUnit, RrrBean rrr, 
            ApplicationBean application, ApplicationServiceBean service) {
        super();
        this.baUnit = baUnit;
        this.rrr = rrr;
        this.application = application;
        this.service = service;
    }

    public String getFreeText() {
        if(freeText == null){
            freeText = "";
        }
        return freeText;
    }

    public void setFreeText(String freeText) {
        if(freeText == null){
            freeText = "";
        }
        this.freeText = freeText;
    }

    public BaUnitBean getBaUnit() {
        if (baUnit == null) {
            baUnit = new BaUnitBean();
        }
        return baUnit;
    }

    public void setBaUnit(BaUnitBean baUnit) {
        if (baUnit == null) {
            baUnit = new BaUnitBean();
        }
        this.baUnit = baUnit;
    }

    public ApplicationBean getApplication() {
        if (application == null) {
            application = new ApplicationBean();
        }
        return application;
    }

    public void setApplication(ApplicationBean application) {
        if (application == null) {
            application = new ApplicationBean();
        }
        this.application = application;
    }

    public RrrBean getRrr() {
        if (rrr == null) {
            rrr = new RrrBean();
        }
        return rrr;
    }

    public void setRrr(RrrBean rrr) {
        if (rrr == null) {
            rrr = new RrrBean();
        }
        this.rrr = rrr;
    }

    public ApplicationServiceBean getService() {
        if (service == null) {
            service = new ApplicationServiceBean();
        }
        return service;
    }

    public void setService(ApplicationServiceBean service) {
        if (service == null) {
            service = new ApplicationServiceBean();
        }
        this.service = service;
    }

    /**
     * Shortcut for application number.
     */
    public String getApplicationNumber() {
        return StringUtility.empty(getApplication().getNr());
    }

    /**
     * Shortcut for application date, converted to string.
     */
    public String getApplicationDate() {
        return DateUtility.getShortDateString(getApplication().getLodgingDatetime(), true);
    }

    /**
     * Shortcut for applicant's full name.
     */
    public String getApplicantName() {
        if (getApplication().getContactPerson() != null && getApplication().getContactPerson().getFullName() != null) {
            return getApplication().getContactPerson().getFullName();
        }
        return "";
    }

    /**
     * Shortcut for service name.
     */
    public String getServiceName() {
        if (getService() != null && getService().getRequestType() != null
                && getService().getRequestType().getDisplayValue() != null) {
            return getService().getRequestType().getDisplayValue();
        }
        return "";
    }

    /**
     * Shortcut for the first parcels first/last name parts.
     */
    public String getParcelsCodes() {
        String codes = "";
        if (baUnitHasParcels()) {
            for (CadastreObjectBean cadastreObject : getBaUnit().getCadastreObjectFilteredList()) {
                String code = cadastreObject.toString();
                if (!code.isEmpty()) {
                    if (codes.isEmpty()) {
                        codes = code;
                    } else {
                        codes = codes + "; " + code;
                    }
                }
            }
        }
        return codes;
    }

    /**
     * Shortcut for the first parcel first/last name part.
     */
    public String getFirstParcelCode() {
        if (baUnitHasParcels()) {
            return getBaUnit().getCadastreObjectFilteredList().get(0).toString();
        }
        return "";
    }

    /**
     * Shortcut for the first parcel type.
     */
    public String getFirstParcelType() {
        if (baUnitHasParcels()) {
            if (getBaUnit().getCadastreObjectFilteredList().get(0).getCadastreObjectType() != null) {
                return getBaUnit().getCadastreObjectFilteredList().get(0).getCadastreObjectType().toString();
            }
        }
        return "";
    }

    /**
     * Shortcut for the first parcel official area.
     */
    public String getFirstParcelOfficialArea() {
        if (baUnitHasParcels()) {
            if (getBaUnit().getCadastreObjectFilteredList().get(0).getOfficialAreaSize() != null) {
                return getBaUnit().getCadastreObjectFilteredList().get(0).getOfficialAreaSize().toPlainString() + " m2";
            }
        }
        return "";
    }

    /**
     * Shortcut for the first parcel land use.
     */
    public String getFirstParcelLandUse() {
        if (baUnitHasParcels()) {
            if (getBaUnit().getCadastreObjectFilteredList().get(0).getLandUseType() != null) {
                return getBaUnit().getCadastreObjectFilteredList().get(0).getLandUseType().toString();
            }
        }
        return "";
    }

    /**
     * Shortcut for the first parcel address.
     */
    public String getFirstParcelAddress() {
        if (baUnitHasParcels()) {
            return getBaUnit().getCadastreObjectFilteredList().get(0).getAddressString();
        }
        return "";
    }
    
    /**
     * Shortcut for the first parcel map reference number.
     */
    public String getFirstParcelMapRef() {
        if (baUnitHasParcels() && getBaUnit().getCadastreObjectFilteredList().get(0).getSourceReference() != null) {
            return getBaUnit().getCadastreObjectFilteredList().get(0).getSourceReference();
        }
        return "";
    }
    
    /**
     * Shortcut for the RRR registration number.
     */
    public String getRrrRegNumber() {
        if (getRrr().getNr() != null) {
            return getRrr().getNr();
        }
        return "";
    }

    /**
     * Shortcut for the RRR registration date in MEDIUM format without time.
     */
    public String getRrrRegDate() {
        return DateUtility.getMediumDateString(getRrr().getRegistrationDate(), false);
    }
    
    /**
     * Shortcut for the RRR expiration date in MEDIUM format without time.
     */
    public String getRrrExpirationDate() {
        return DateUtility.getMediumDateString(getRrr().getExpirationDate(), false);
    }
    
    /**
     * Shortcut for the RRR payment amount.
     */
    public String getRrrPaymentAmount() {
        if(getRrr().getAmount()!=null){
            return getRrr().getAmount().toPlainString();
        } else {
            return "";
        }
    }
    
    /**
     * Shortcut for the RRR mortgage ranking.
     */
    public String getRrrMortgageRanking() {
        if(getRrr().getMortgageRanking()!=null){
            return getRrr().getMortgageRanking().toString();
        } else {
            return "";
        }
    }
    
    /**
     * Shortcut for the RRR mortgage interest.
     */
    public String getRrrMortgageInterest() {
        if(getRrr().getMortgageInterestRate()!=null){
            return getRrr().getMortgageInterestRate().toPlainString();
        } else {
            return "";
        }
    }
    
    /**
     * Shortcut for the RRR mortgage type.
     */
    public String getRrrMortgageType() {
        if(getRrr().getMortgageType()!=null){
            return getRrr().getMortgageType().toString();
        } else {
            return "";
        }
    }
    
    /**
     * Shortcut for the RRR mortgage lender.
     */
    public String getRrrMortgageLender() {
        if(getRrr().getFirstRightHolder()!=null){
            return getRrr().getFirstRightHolder().getFullName();
        } else {
            return "";
        }
    }
    
    /**
     * Shortcut for the RRR due date in MEDIUM format without time.
     */
    public String getRrrDueDate() {
        return DateUtility.getMediumDateString(getRrr().getDueDate(), false);
    }
    
    /**
     * Returns true if there are parcels on the BaUnit.
     */
    public boolean baUnitHasParcels() {
        if (getBaUnit().getCadastreObjectFilteredList() != null
                && getBaUnit().getCadastreObjectFilteredList().size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
