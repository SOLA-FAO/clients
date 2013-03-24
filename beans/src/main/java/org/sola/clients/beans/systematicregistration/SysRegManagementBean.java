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
package org.sola.clients.beans.systematicregistration;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.webservices.transferobjects.administrative.SysRegManagementTO;
import org.sola.services.boundary.wsclients.WSManager;
import org.sola.webservices.transferobjects.administrative.SysRegManagementParamsTO;

/**
 * Contains summary properties of the LodgementView object. Could be populated
 * from the {@link LodgementViewTO} object.<br /> For more information see UC
 * <b>Lodgement Report</b> schema.
 */
public class SysRegManagementBean extends AbstractIdBean {

//    public static final String APPLICATION_PROPERTY = "application";
//    public static final String SPATIAL_PROPERTY = "spatial";
//    public static final String COMPLETED_PROPERTY = "completed";
//    public static final String APPROVED_PROPERTY = "approved";
//    public static final String ARCHIVED_PROPERTY = "archived";
//    public static final String PARCEL_NOTIFICATION_PROPERTY = "parcelnotification";
//    public static final String PARCEL_COMPLETION_PROPERTY = "parcelcompletion";
//    public static final String PARCEL_APPROVED_PROPERTY = "parcelapproved";
//    public static final String SIZE_APPROVED_PROPERTY = "sizeapproved";
//    public static final String PARCEL_APPR_RES_PROPERTY = "parcelapprovedresidential";
//    public static final String SIZE_APPR_RES_PROPERTY = "sizeapprovedresidential";
//    public static final String PARCEL_APPR_COM_PROPERTY = "parcelapprovedcommercial";
//    public static final String SIZE_APPR_COM_PROPERTY = "sizeapprovedcommercial";
//    public static final String PARCEL_APPR_IND_PROPERTY = "parcelapprovedindustrial";
//    public static final String SIZE_APPR_IND_PROPERTY = "sizeapprovedindustrial";
//    public static final String PARCEL_APPR_AGR_PROPERTY = "parcelapprovedagricultural";
//    public static final String SIZE_APPR_AGR_PROPERTY = "sizeapprovedagricultural";
//    public static final String OBJECTION_PROPERTY = "objection";
    public static final String COUNTER_PROPERTY = "counter";
    public static final String DESCR_PROPERTY = "descr";
    private ObservableList<SysRegManagementBean> menagementList;
//    private BigDecimal application;
//    private BigDecimal spatial;
//    private BigDecimal completed;
//    private BigDecimal approved;
//    private BigDecimal archived;
//    private BigDecimal parcelnotification;
//    private BigDecimal parcelcompletion;
//    private BigDecimal parcelapproved;
//    private BigDecimal sizeapproved;
//    private BigDecimal parcelapprovedresidential;
//    private BigDecimal sizeapprovedresidential;
//    private BigDecimal parcelapprovedcommercial;
//    private BigDecimal sizeapprovedcommercial;
//    private BigDecimal parcelapprovedindustrial;
//    private BigDecimal sizeapprovedindustrial;
//    private BigDecimal parcelapprovedagricultural;
//    private BigDecimal sizeapprovedagricultural;
//    private BigDecimal objection;
    private BigDecimal counter;
    private String descr;
    private String area;

    public SysRegManagementBean() {
//        super();
        menagementList = ObservableCollections.observableList(new LinkedList<SysRegManagementBean>());
    }

    public ObservableList<SysRegManagementBean> getMenagementList() {
        return menagementList;
    }

    public void setMenagementList(ObservableList<SysRegManagementBean> menagementList) {
        this.menagementList = menagementList;
    }

    public BigDecimal getCounter() {
        return counter;
    }

    public void setCounter(BigDecimal counter) {
        this.counter = counter;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

//    public BigDecimal getApplication() {
//        return application;
//    }
//
//    public void setApplication(BigDecimal application) {
//        this.application = application;
//    }
//
//    public BigDecimal getApproved() {
//        return approved;
//    }
//
//    public void setApproved(BigDecimal approved) {
//        this.approved = approved;
//    }
//
//    public BigDecimal getArchived() {
//        return archived;
//    }
//
//    public void setArchived(BigDecimal archived) {
//        this.archived = archived;
//    }
//
//    public BigDecimal getCompleted() {
//        return completed;
//    }
//
//    public void setCompleted(BigDecimal completed) {
//        this.completed = completed;
//    }
//
//    public BigDecimal getObjection() {
//        return objection;
//    }
//
//    public void setObjection(BigDecimal objection) {
//        this.objection = objection;
//    }
//
//    public BigDecimal getParcelapproved() {
//        return parcelapproved;
//    }
//
//    public void setParcelapproved(BigDecimal parcelapproved) {
//        this.parcelapproved = parcelapproved;
//    }
//
//    public BigDecimal getParcelapprovedagricultural() {
//        return parcelapprovedagricultural;
//    }
//
//    public void setParcelapprovedagricultural(BigDecimal parcelapprovedagricultural) {
//        this.parcelapprovedagricultural = parcelapprovedagricultural;
//    }
//
//    public BigDecimal getParcelapprovedcommercial() {
//        return parcelapprovedcommercial;
//    }
//
//    public void setParcelapprovedcommercial(BigDecimal parcelapprovedcommercial) {
//        this.parcelapprovedcommercial = parcelapprovedcommercial;
//    }
//
//    public BigDecimal getParcelapprovedindustrial() {
//        return parcelapprovedindustrial;
//    }
//
//    public void setParcelapprovedindustrial(BigDecimal parcelapprovedindustrial) {
//        this.parcelapprovedindustrial = parcelapprovedindustrial;
//    }
//
//    public BigDecimal getParcelapprovedresidential() {
//        return parcelapprovedresidential;
//    }
//
//    public void setParcelapprovedresidential(BigDecimal parcelapprovedresidential) {
//        this.parcelapprovedresidential = parcelapprovedresidential;
//    }
//
//    public BigDecimal getParcelcompletion() {
//        return parcelcompletion;
//    }
//
//    public void setParcelcompletion(BigDecimal parcelcompletion) {
//        this.parcelcompletion = parcelcompletion;
//    }
//
//    public BigDecimal getParcelnotification() {
//        return parcelnotification;
//    }
//
//    public void setParcelnotification(BigDecimal parcelnotification) {
//        this.parcelnotification = parcelnotification;
//    }
//
//    public BigDecimal getSizeapproved() {
//        return sizeapproved;
//    }
//
//    public void setSizeapproved(BigDecimal sizeapproved) {
//        this.sizeapproved = sizeapproved;
//    }
//
//    public BigDecimal getSizeapprovedagricultural() {
//        return sizeapprovedagricultural;
//    }
//
//    public void setSizeapprovedagricultural(BigDecimal sizeapprovedagricultural) {
//        this.sizeapprovedagricultural = sizeapprovedagricultural;
//    }
//
//    public BigDecimal getSizeapprovedcommercial() {
//        return sizeapprovedcommercial;
//    }
//
//    public void setSizeapprovedcommercial(BigDecimal sizeapprovedcommercial) {
//        this.sizeapprovedcommercial = sizeapprovedcommercial;
//    }
//
//    public BigDecimal getSizeapprovedindustrial() {
//        return sizeapprovedindustrial;
//    }
//
//    public void setSizeapprovedindustrial(BigDecimal sizeapprovedindustrial) {
//        this.sizeapprovedindustrial = sizeapprovedindustrial;
//    }
//
//    public BigDecimal getSizeapprovedresidential() {
//        return sizeapprovedresidential;
//    }
//
//    public void setSizeapprovedresidential(BigDecimal sizeapprovedresidential) {
//        this.sizeapprovedresidential = sizeapprovedresidential;
//    }
//
//    public BigDecimal getSpatial() {
//        return spatial;
//    }
//
//    public void setSpatial(BigDecimal spatial) {
//        this.spatial = spatial;
//    }
    /**
     * Returns collection of {@link ApplicationBean} objects. This method is
     * used by Jasper report designer to extract properties of application bean
     * to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        SysRegManagementBean bean = new SysRegManagementBean();
        collection.add(bean);
        return collection;
    }

    //      /** Passes from date and to date search criteria. */
    public void passParameter(SysRegManagementParamsBean params) {
//        applicationSearchResultsList.clear();
        SysRegManagementParamsTO paramsTO = TypeConverters.BeanToTrasferObject(params,
                SysRegManagementParamsTO.class);

        List<SysRegManagementTO> managementViewTO =
                WSManager.getInstance().getAdministrative().getSysRegManagement(paramsTO);
        TypeConverters.TransferObjectListToBeanList(managementViewTO,
                SysRegManagementBean.class, (List) this.getMenagementList());
    }
}