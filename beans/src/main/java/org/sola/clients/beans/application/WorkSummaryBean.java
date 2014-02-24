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
package org.sola.clients.beans.application;

import org.sola.clients.beans.AbstractBindingBean;

/**
 * Bean containing work summary information for the Lodgement Report
 *
 */
public class WorkSummaryBean extends AbstractBindingBean {

    private String serviceType;
    private String serviceCategory;
    private int inProgressFrom;
    private int onRequisitionFrom;
    private int lodged;
    private int requisitioned;
    private int registered;
    private int cancelled;
    private int withdrawn;
    private int inProgressTo;
    private int onRequisitionTo;
    private int overdue;
    private String overdueApplications;
    private String onRequisitionApplications;

    public WorkSummaryBean() {
        super();
    }

    public int getCancelled() {
        return cancelled;
    }

    public void setCancelled(int cancelled) {
        this.cancelled = cancelled;
    }

    public int getLodged() {
        return lodged;
    }

    public void setLodged(int lodged) {
        this.lodged = lodged;
    }

    public int getOverdue() {
        return overdue;
    }

    public void setOverdue(int overdue) {
        this.overdue = overdue;
    }

    public String getOverdueApplications() {
        return overdueApplications;
    }

    public void setOverdueApplications(String overdueApplications) {
        this.overdueApplications = overdueApplications;
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public int getRequisitioned() {
        return requisitioned;
    }

    public void setRequisitioned(int requisitioned) {
        this.requisitioned = requisitioned;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(int withdrawn) {
        this.withdrawn = withdrawn;
    }

    public String getOnRequisitionApplications() {
        return onRequisitionApplications;
    }

    public void setOnRequisitionApplications(String onRequisitionApplications) {
        this.onRequisitionApplications = onRequisitionApplications;
    }

    public int getInProgressFrom() {
        return inProgressFrom;
    }

    public void setInProgressFrom(int inProgressFrom) {
        this.inProgressFrom = inProgressFrom;
    }

    public int getInProgressTo() {
        return inProgressTo;
    }

    public void setInProgressTo(int inProgressTo) {
        this.inProgressTo = inProgressTo;
    }

    public int getOnRequisitionFrom() {
        return onRequisitionFrom;
    }

    public void setOnRequisitionFrom(int onRequisitionFrom) {
        this.onRequisitionFrom = onRequisitionFrom;
    }

    public int getOnRequisitionTo() {
        return onRequisitionTo;
    }

    public void setOnRequisitionTo(int onRequisitionTo) {
        this.onRequisitionTo = onRequisitionTo;
    }

    /**
     * Returns collection of {@link WorkSummaryBean} objects. This method is
     * used by Jasper report designer to extract properties of application bean
     * to help design a report.
     */
    public static java.util.Collection generateCollection() {
        java.util.Vector collection = new java.util.Vector();
        WorkSummaryBean bean = new WorkSummaryBean();
        collection.add(bean);
        return collection;
    }
}
