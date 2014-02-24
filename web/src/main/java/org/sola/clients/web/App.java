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
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.sola.services.casemanagement.boundry.Address;
import org.sola.services.casemanagement.boundry.Application;
import org.sola.services.casemanagement.boundry.Party;
import org.sola.services.casemanagement.boundry.RequestType;
import org.sola.services.casemanagement.boundry.ServicesInApplication;
import org.sola.services.casemanagement.boundry.ServicesInApplicationPK;
import org.sola.services.casemanagement.boundry.TestWSService;

/**
 *
 * @author solovov
 */
@ManagedBean
@SessionScoped
public class App {

    /** Creates a new instance of App */
    private TestWSService wsService = new TestWSService();

    public App() {
        init();
    }

    private void init() {
        wsService.getTestWSPort();
        services = wsService.getTestWSPort().getRequestTypes();
        selectedServices = new LinkedList<RequestType>();
        agents = wsService.getTestWSPort().getApplicants();

        String addressID = UUID.randomUUID().toString();
        contactAddress = new Address();
        contactAddress.setId(addressID);

        contactPerson = new Party();
        contactPerson.setId(UUID.randomUUID().toString());
        contactPerson.setAddressId(addressID);

        communicationWays = new LinkedList<String>();
        communicationWays.add("eMail");
        communicationWays.add("phone");
        communicationWays.add("fax");
        communicationWays.add("post");
        communicationWays.add("courier");
        communicationWay = "phone";
    }
    private List<Party> agents;
    private List<String> selectedItems; // + getter + setter
    private List<RequestType> services;
    private List<RequestType> selectedServices;
    private List<String> communicationWays;
    private String servicesValue;
    private String selectedServicesValue;
    private Party contactPerson;
    private String communicationWay;
    private Address contactAddress;
    private String selectedAgent;

    public String getSelectedAgent() {
        return selectedAgent;
    }

    public void setSelectedAgent(String selectedAgent) {
        this.selectedAgent = selectedAgent;
    }

    public Address getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(Address contactAddress) {
        this.contactAddress = contactAddress;
    }

    public Party getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Party contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getCommunicationWay() {
        return communicationWay;
    }

    public void setCommunicationWay(String communicationWay) {
        this.communicationWay = communicationWay;
    }

    public List<String> getCommunicationWays() {
        return communicationWays;
    }

    public void setCommunicationWays(List<String> communicationWays) {
        this.communicationWays = communicationWays;
    }

    public String getSelectedServicesValue() {
        return selectedServicesValue;
    }

    public void setSelectedServicesValue(String selectedServicesValue) {
        this.selectedServicesValue = selectedServicesValue;
    }

    public String getServicesValue() {
        return servicesValue;
    }

    public void setServicesValue(String servicesValue) {
        this.servicesValue = servicesValue;
    }

    public List<RequestType> getSelectedServices() {
        return selectedServices;
    }

    public List<RequestType> getServices() {
        return services;
    }

    public List<String> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(List<String> selectedItems) {
        this.selectedItems = selectedItems;
    }

    /**
     * @return the agents
     */
    public List<Party> getAgents() {
        return agents;
    }

    public void addService() {
        if (servicesValue != null && !servicesValue.equals("") && selectedServices != null) {

            RequestType itemService = getItemService(services, servicesValue);
            if (itemService != null) {
                selectedServices.add(itemService);
                services.remove(itemService);
            }
        }
        selectedServicesValue = "";
    }

    private RequestType getItemService(List<RequestType> lst, String code) {
        if (code != null && !code.equals("") && lst != null) {
            for (RequestType itemService : lst) {
                if (itemService.getCode().equals(code)) {
                    return itemService;
                }
            }
        }
        return null;
    }

    public void removeService() {
        if (selectedServicesValue != null && !selectedServicesValue.equals("") && services != null) {

            RequestType itemService = getItemService(selectedServices, selectedServicesValue);

            if (itemService != null) {
                selectedServices.remove(itemService);
                services.add(itemService);
            }
        }
        selectedServicesValue = "";
    }

    public void moveServiceUp() {
        if (selectedServices != null && !selectedServicesValue.equals("")) {
            RequestType itemService = getItemService(selectedServices, selectedServicesValue);
            int i = 0;

            for (Iterator iter = selectedServices.iterator(); iter.hasNext();) {
                if (iter.next() == itemService && i > 0) {
                    Collections.swap(selectedServices, i, i - 1);
                    break;
                }
                i += 1;
            }
        }
    }

    public void moveServiceDown() {
        if (selectedServices != null && !selectedServicesValue.equals("")) {
            RequestType itemService = getItemService(selectedServices, selectedServicesValue);
            int i = 0;
            int size = selectedServices.size();

            for (Iterator iter = selectedServices.iterator(); iter.hasNext();) {
                if (iter.next() == itemService && i + 1 < size) {
                    Collections.swap(selectedServices, i, i + 1);
                    break;
                }
                i += 1;
            }
        }
    }
    private static final String DATE_FORMAT_NOW = "MMM d, yyyy HH:mm";

    public String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public String getUserName() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Principal user = ctx.getExternalContext().getUserPrincipal();
        if (user != null) {
            return user.getName();
        } else {
            return "";
        }
    }

    public void submit() throws DatatypeConfigurationException {
        try {
            // Do validation here
            //
            boolean hasErrors = false;

            if (contactPerson.getName() == null || contactPerson.getName().equals("")) {
                FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fill in contact person name", "Field is Empty");
                FacesContext.getCurrentInstance().addMessage("Field is Empty", fm);
                hasErrors = true;
            }

            if (contactPerson.getLastName() == null || contactPerson.getLastName().equals("")) {
                FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fill in contact person last name", "Field is Empty");
                FacesContext.getCurrentInstance().addMessage("Field is Empty", fm);
                hasErrors = true;
            }

            if (contactAddress.getDescription() == null || contactAddress.getDescription().equals("")) {
                FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fill in contact person address", "Field is Empty");
                FacesContext.getCurrentInstance().addMessage("Field is Empty", fm);
                hasErrors = true;
            }

            if (selectedServices.size() <= 0) {
                FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Select service(s)", "Field is Empty");
                FacesContext.getCurrentInstance().addMessage("Field is Empty", fm);
                hasErrors = true;
            }

            if (hasErrors) {
                return;
            }

            String applicationID = UUID.randomUUID().toString();

            // Services in application
            List<ServicesInApplication> appServices = new LinkedList<ServicesInApplication>();
            int i = 1;
            for (RequestType servicesInApplication : selectedServices) {
                ServicesInApplication tmpService = new ServicesInApplication();
                ServicesInApplicationPK key = new ServicesInApplicationPK();

                key.setApplicationId(applicationID);
                key.setRequestTypeCode(servicesInApplication.getCode());

                tmpService.setServiceOrder(i);
                tmpService.setServicesInApplicationPK(key);
                appServices.add(tmpService);
                i += 1;
            }

            // Application date
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(Calendar.getInstance().getTime());
            XMLGregorianCalendar appDate1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

            // Application
            Application app = new Application();
            app.setId(applicationID);
            app.setApplicantId(selectedAgent);
            app.setLodgingDatetime(appDate1);
            app.setContactPersonId(contactPerson.getId());

            wsService.getTestWSPort().insertApplication(app, contactPerson, contactAddress, appServices);

            init();

            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("Receipt.xhtml?id=" + app.getId());
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception e) {
        }
    }

    public void cancel() throws IOException {
        //response.sendRedirect("");
        init();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/solaweb/index.xhtml");
    }
}
