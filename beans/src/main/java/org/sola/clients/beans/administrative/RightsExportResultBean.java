/*
 * Copyright 2013 Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.beans.administrative;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.referencedata.IdTypeBean;

/**
 *
 */
public class RightsExportResultBean extends AbstractBindingBean {
    public static final String IS_CHECKED_PROPERTY = "checked";
    
    private boolean checked;
    private String baUnitId;
    private String nameFirstPart;
    private String nameLastPart;
    private BigDecimal area;
    private String rightId;
    private String rightType;
    private Date registrationDate;
    private Date expirationDate;
    private BigDecimal amount;
    private String owners;
    private String applicantId;
    private String applicantName;
    private String applicantLastName;
    private String applicantAddress;
    private String applicantPhone;
    private String applicantMobile;
    private String applicantEmail;
    private String applicantIdNumber;
    private String applicantIdTypeCode;
    // private boolean isActive;
    // private gender;
    // private Date dateOfBirth;
    // private previousLease number
    // private startDate
    
    public RightsExportResultBean(){
        super();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        boolean oldValue = this.checked;
        this.checked = checked;
        propertySupport.firePropertyChange(IS_CHECKED_PROPERTY, oldValue, this.checked);
    }

    public BigDecimal getAmount() {
        if(amount == null){
            return new BigDecimal(0);
        } else {
            return amount;
        }
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getApplicantAddress() {
        return applicantAddress;
    }

    public void setApplicantAddress(String applicantAddress) {
        this.applicantAddress = applicantAddress;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantIdNumber() {
        return applicantIdNumber;
    }

    public void setApplicantIdNumber(String applicantIdNumber) {
        this.applicantIdNumber = applicantIdNumber;
    }

    public String getApplicantIdTypeName() {
        String idTypeName = "";
        if(getApplicantIdTypeCode()!=null){
            IdTypeBean idBean = CacheManager.getBeanByCode(CacheManager.getIdTypes(), getApplicantIdTypeCode());
            if(idBean!=null){
                idTypeName = idBean.getDisplayValue();
            }
        }
        return idTypeName;
    }
    
    public String getApplicantIdTypeCode() {
        return applicantIdTypeCode;
    }

    public void setApplicantIdTypeCode(String applicantIdTypeCode) {
        this.applicantIdTypeCode = applicantIdTypeCode;
    }

    public String getApplicantLastName() {
        return applicantLastName;
    }

    public void setApplicantLastName(String applicantLastName) {
        this.applicantLastName = applicantLastName;
    }

    public String getApplicantMobile() {
        return applicantMobile;
    }

    public void setApplicantMobile(String applicantMobile) {
        this.applicantMobile = applicantMobile;
    }

    public String getApplicantFullName() {
        String fullName = "";
        if(getApplicantName()!=null){
            fullName = getApplicantName();
        }
        if(getApplicantLastName()!=null && !getApplicantLastName().isEmpty()){
            fullName = fullName + " " + getApplicantLastName();
        }
        return fullName;
    }
    
    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }
    
    public String getAreaFormatted(){
        String areaFormatted = "";
        if(getArea()!=null){
            areaFormatted = "with an area of " + getArea().toPlainString() + " m2";
        }
        return areaFormatted;
    }
    
    public BigDecimal getArea() {
        if(area == null){
            return new BigDecimal(0);
        } else {
            return area;
        }
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        this.baUnitId = baUnitId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPropCode(){
        String propCode ="";
        if(getNameFirstPart()!=null){
            propCode = getNameFirstPart();
        }
        if(getNameLastPart()!=null && !getNameLastPart().isEmpty()){
            propCode = propCode + "/" + getNameLastPart();
        }
        return propCode;
    }
    
    public String getNameFirstPart() {
        return nameFirstPart;
    }

    public void setNameFirstPart(String nameFirstPart) {
        this.nameFirstPart = nameFirstPart;
    }

    public String getNameLastPart() {
        return nameLastPart;
    }

    public void setNameLastPart(String nameLastPart) {
        this.nameLastPart = nameLastPart;
    }

    public String getOwnersFormatted(){
        String formattedOwners = getOwners();

        if(formattedOwners!=null){
            String[] tmpOwners = formattedOwners.split(",");
            int lng = tmpOwners.length;
            
            for(int i = 0; i<lng; i++){
                if(i==0){
                    formattedOwners = tmpOwners[i];
                } else if(i == 3) {
                    if(lng>4){
                        formattedOwners = formattedOwners + ", " + tmpOwners[i] + ", and others";
                    } else {
                        formattedOwners = formattedOwners + ", and " + tmpOwners[i];
                    }
                    break;
                } else {
                    formattedOwners = formattedOwners + ", " + tmpOwners[i];
                }
            }
        }
        return formattedOwners;
    }
    
    public String getOwners() {
        return owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRightType() {
        return rightType;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    public String getRightId() {
        return rightId;
    }

    public void setRightId(String rightId) {
        this.rightId = rightId;
    }
}
