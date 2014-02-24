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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.clients.web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.sola.services.casemanagement.boundry.Address;
import org.sola.services.casemanagement.boundry.TestWSService;
import org.sola.services.casemanagement.boundry.Application;
import org.sola.services.casemanagement.boundry.Party;
import org.sola.services.casemanagement.boundry.RequestType;
import org.sola.services.casemanagement.boundry.ServicesInApplication;

/**
 *
 * @author solovov
 */
@ManagedBean
@RequestScoped
public class AppReceipt {

    /** Creates a new instance of AppReceipt */
    public AppReceipt() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String id = req.getParameter("id");

        if (id == null || id.equals("")) {
            return;
        }

        application = wsService.getTestWSPort().getApplication(id);
        agent = wsService.getTestWSPort().getPartyByID(application.getApplicantId());
        contactPerson = wsService.getTestWSPort().getPartyByID(application.getContactPersonId());
        contactPersonAddress = wsService.getTestWSPort().getAddress(contactPerson.getAddressId());
        List<RequestType> allServices = wsService.getTestWSPort().getRequestTypes();
        List<ServicesInApplication> applicationServices = wsService.getTestWSPort().getApplicationServices(application.getId());
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy HH:mm");
        appDate = sdf.format(application.getLodgingDatetime().toGregorianCalendar().getInstance().getTime());

        services = new LinkedList<RequestType>();

        for (ServicesInApplication applicationService : applicationServices) {
            for (RequestType service : allServices) {
                if (applicationService.getServicesInApplicationPK().getRequestTypeCode().
                        equals(service.getCode())) {
                        services.add(service);
                        break;
                }
            }
        }
    }

    private TestWSService wsService = new TestWSService();
    private Application application;
    private Party agent;
    private Party contactPerson;
    private Address contactPersonAddress;
    private List<RequestType> services;
    private String appDate;

    public String getAppDate() {
        return appDate;
    }

    public String getSrvCout(){
        return Integer.toString(services.size());
    }

    public Party getAgent() {
        return agent;
    }

    public Party getContactPerson() {
        return contactPerson;
    }

    public Address getContactPersonAddress() {
        return contactPersonAddress;
    }

    public List<RequestType> getServices() {
        return services;
    }

    public Application getApplication() {
        return application;
    }

}
