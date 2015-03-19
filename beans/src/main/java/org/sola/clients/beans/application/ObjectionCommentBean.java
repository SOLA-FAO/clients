/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.util.Date;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.webservices.transferobjects.casemanagement.ObjectionCommentTO;

/**
 * Represents objection comment object. Could be populated from the
 * {@link ObjectionCommentTO} object.<br />
 * For more information see data dictionary <b>Application</b> schema.
 */
public class ObjectionCommentBean extends AbstractIdBean {

    public static final String COMMENT_PROPERTY = "comment";
    public static final String COMMENT_DATE_PROPERTY = "commentDate";
    public static final String COMMENT_BY_PROPERTY = "commentBy";
    public static final String DISPLAY_USER_NAME_PROPERTY = "displayUserName";

    private String objectionId;
    private String commentBy;
    private String comment;
    private Date commentDate;
    private String userName; 

    public ObjectionCommentBean() {
        super();
    }

    public String getObjectionId() {
        return objectionId;
    }

    public void setObjectionId(String objectionId) {
        this.objectionId = objectionId;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String value) {
        String oldValue = this.commentBy;
        this.commentBy = value;
        propertySupport.firePropertyChange(COMMENT_BY_PROPERTY, oldValue, value);
        propertySupport.firePropertyChange(COMMENT_BY_PROPERTY, oldValue, value);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String value) {
        String oldValue = this.comment;
        this.comment = value;
        propertySupport.firePropertyChange(COMMENT_PROPERTY, oldValue, value);
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date value) {
        Date oldValue = this.commentDate;
        this.commentDate = value;
        propertySupport.firePropertyChange(DISPLAY_USER_NAME_PROPERTY, oldValue, value);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
