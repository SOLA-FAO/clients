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
package org.sola.clients.beans.referencedata;

import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.application.ApplicationBean;
import org.sola.webservices.transferobjects.referencedata.ApplicationActionTypeTO;

/** 
 * Represents reference data object of the <b>application_action_type</b> table. 
 * Could be populated from the {@link ApplicationActionTypeTO} object.<br />
 * For more information see data dictionary <b>Application</b> schema.
 * <br />This bean is used as a part of {@link ApplicationBean}.
 */
public class ApplicationActionTypeBean extends AbstractCodeBean {

    public static final String ADD_DOCUMENT = "addDocument";
    public static final String APPROVE = "approve";
    public static final String ARCHIVE = "archive";
    public static final String DISPATCH = "dispatch";
    public static final String WITHDRAW = "withdraw";
    public static final String CANCEL = "cancel";
    public static final String REQUISITION = "requisition";
    public static final String VALIDATE = "validate";
    public static final String LAPSE = "lapse";
    public static final String LODGE = "lodge";
    public static final String ASSIGN = "assign";
    public static final String UNASSIGN = "unAssign";
    public static final String RESUBMIT = "resubmit";
    public static final String VALIDATE_FAILED = "validateFailed";
    public static final String VALIDATE_PASSED = "validatePassed";
    
    public ApplicationActionTypeBean() {
        super();
    }
}
