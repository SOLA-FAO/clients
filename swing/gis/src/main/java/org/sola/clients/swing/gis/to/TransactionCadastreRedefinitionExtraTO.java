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
package org.sola.clients.swing.gis.to;

import java.util.List;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectNodeTargetTO;
import org.sola.webservices.transferobjects.cadastre.CadastreObjectTargetRedefinitionTO;
import org.sola.webservices.transferobjects.transaction.TransactionSourceTO;

/**
 * It extends the TO which is generated from the web service with the set method
 * which disappears during the auto generation from the representation of the TO object in 
 * the server. This method is needed in order to use the generic mapper to translate from 
 * data bean to TO and viceversa.
 * 
 * @author Elton Manoku
 */
public class TransactionCadastreRedefinitionExtraTO 
extends org.sola.webservices.transferobjects.transaction.TransactionCadastreRedefinitionTO{
    
    public void setTransactionSourceList(List<TransactionSourceTO> transactionSourceList) {
        this.transactionSourceList = transactionSourceList;
    }

    public void setCadastreObjectTargetList(
            List<CadastreObjectTargetRedefinitionTO> cadastreObjectTargetList) {
        this.cadastreObjectTargetList = cadastreObjectTargetList;
    }

    public void setCadastreObjectNodeTargetList(
            List<CadastreObjectNodeTargetTO> cadastreObjectNodeTargetList) {
        this.cadastreObjectNodeTargetList = cadastreObjectNodeTargetList;
    }
}
