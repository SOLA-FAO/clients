/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
import org.sola.common.MappingManager;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.clients.swing.gis.to.CadastreChangeExtraTO;
import org.sola.webservices.transferobjects.cadastre.CadastreChangeTO;

/**
 *
 * @author manoku
 */
public class CadastreChangeBean extends AbstractGisBean{
    
    private String fromServiceId;
    private List<CadastreObjectBean> newCadastreObjectList = new ArrayList<CadastreObjectBean>();
    private List<String> targetCadastreObjectIdList = new ArrayList<String>();
    private List<SurveyPointBean> surveyPointList = new ArrayList<SurveyPointBean>();
    List<String> sourceIdList = new ArrayList<String>();

    public List<CadastreObjectBean> getNewCadastreObjectList() {
        return newCadastreObjectList;
    }

    public void setNewCadastreObjectList(List<CadastreObjectBean> newCadastreObjectList) {
        this.newCadastreObjectList = newCadastreObjectList;
    }

    public String getFromServiceId() {
        return fromServiceId;
    }

    public void setFromServiceId(String fromServiceId) {
        this.fromServiceId = fromServiceId;
    }

    public List<String> getSourceIdList() {
        return sourceIdList;
    }

    public void setSourceIdList(List<String> sourceIdList) {
        this.sourceIdList = sourceIdList;
    }

    public List<SurveyPointBean> getSurveyPointList() {
        return surveyPointList;
    }

    public void setSurveyPointList(List<SurveyPointBean> surveyPointList) {
        this.surveyPointList = surveyPointList;
    }

    public List<String> getTargetCadastreObjectIdList() {
        return targetCadastreObjectIdList;
    }

    public void setTargetCadastreObjectIdList(List<String> targetCadastreObjectIdList) {
        this.targetCadastreObjectIdList = targetCadastreObjectIdList;
    }

    
    public CadastreChangeTO getTO(){
        CadastreChangeExtraTO to = new CadastreChangeExtraTO();
        MappingManager.getMapper().map(this, to);
        return to;
    }
    
    public List<ValidationResultBean> save(){        
        return TypeConverters.TransferObjectListToBeanList(
                PojoDataAccess.getInstance().getCadastreService().saveCadastreChange(this.getTO()), 
                ValidationResultBean.class, null);
    }
}
