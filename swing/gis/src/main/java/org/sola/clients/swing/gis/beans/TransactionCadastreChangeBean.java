/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.clients.swing.gis.beans;

import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.converters.TypeConverters;
import org.sola.clients.beans.validation.ValidationResultBean;
import org.sola.common.mapping.MappingManager;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.to.TransactionCadastreChangeExtraTO;
import org.sola.webservices.transferobjects.transaction.TransactionCadastreChangeTO;

/**
 * Data bean representing a transaction of cadastre change.
 * 
 * @author Elton Manoku
 */
public class TransactionCadastreChangeBean extends TransactionBean{
    
    private List<CadastreObjectBean> cadastreObjectList = new ArrayList<CadastreObjectBean>();
    private List<CadastreObjectTargetBean> cadastreObjectTargetList = 
            new ArrayList<CadastreObjectTargetBean>();
    private List<SurveyPointBean> surveyPointList = new ArrayList<SurveyPointBean>();

    /**
     * Gets list of new cadastre objects
     */
    public List<CadastreObjectBean> getCadastreObjectList() {
        return cadastreObjectList;
    }

    /**
     * Sets list of new cadastre objects
     */
    public void setCadastreObjectList(List<CadastreObjectBean> cadastreObjectList) {
        this.cadastreObjectList = cadastreObjectList;
    }

    /**
     * Gets list of target cadastre objects
     */
    public List<CadastreObjectTargetBean> getCadastreObjectTargetList() {
        return cadastreObjectTargetList;
    }

    /**
     * Sets list of target cadastre objects
     */
    public void setCadastreObjectTargetList(List<CadastreObjectTargetBean> cadastreObjectTargetList) {
        this.cadastreObjectTargetList = cadastreObjectTargetList;
    }
    
    /**
     * Gets list of survey points added in transaction
     */
    public List<SurveyPointBean> getSurveyPointList() {
        return surveyPointList;
    }

    /**
     * Sets list of survey points added in transaction
     */
    public void setSurveyPointList(List<SurveyPointBean> surveyPointList) {
        this.surveyPointList = surveyPointList;
    }
     
    @Override
    public TransactionCadastreChangeTO getTO(){
        TransactionCadastreChangeExtraTO to = new TransactionCadastreChangeExtraTO();
        MappingManager.getMapper().map(this, to);
        return to;
    }
    
    @Override
    public List<ValidationResultBean> save(){
        return TypeConverters.TransferObjectListToBeanList(
                PojoDataAccess.getInstance().getCadastreService().saveTransactionCadastreChange(
                this.getTO()), ValidationResultBean.class, null);
    }

}
