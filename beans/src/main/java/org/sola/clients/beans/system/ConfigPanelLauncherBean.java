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
package org.sola.clients.beans.system;

import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.webservices.admin.ConfigPanelLauncherTO;

/**
 * Represents reference data object of the <b>config_panel_launcher</b> table.
 * Can be populated from the {@link ConfigPanelLauncherTO} object.<br />
 * For more information see data dictionary <b>system</b> schema.
 */
public class ConfigPanelLauncherBean extends AbstractCodeBean {

    public static final String PANEL_CLASS_PROPERTY = "panelClass";
    public static final String CARD_NAME_PROPERTY = "cardName";
    public static final String MESSAGE_CODE_PROPERTY = "messageCode";
    public static final String LAUNCH_GROUP_CODE_PROPERTY = "launchGroupCode";
    public static final String LAUNCH_GROUP_PROPERTY = "launchGroup";
    private String panelClass;
    private String cardName;
    private String messageCode;
    PanelLauncherGroupBean launchGroup;

    public ConfigPanelLauncherBean() {
        super();
    }

    public String getPanelClass() {
        return panelClass;
    }

    public void setPanelClass(String value) {
        String old = panelClass;
        panelClass = value;
        propertySupport.firePropertyChange(PANEL_CLASS_PROPERTY, old, value);
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String value) {
        String old = cardName;
        cardName = value;
        propertySupport.firePropertyChange(CARD_NAME_PROPERTY, old, value);
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String value) {
        String old = messageCode;
        messageCode = value;
        propertySupport.firePropertyChange(MESSAGE_CODE_PROPERTY, old, value);
    }

    public String getLaunchGroupCode() {
        if (launchGroup != null) {
            return launchGroup.getCode();
        } else {
            return null;
        }
    }

    public void setLaunchGroupCode(String groupCode) {
        String oldValue = null;
        if (launchGroup != null) {
            oldValue = launchGroup.getCode();
        }
        setLaunchGroup(CacheManager.getBeanByCode(CacheManager.getPanelLauncherGroups(), groupCode));
        propertySupport.firePropertyChange(LAUNCH_GROUP_CODE_PROPERTY, oldValue, groupCode);
    }

    public PanelLauncherGroupBean getLaunchGroup() {
        if (launchGroup == null) {
            launchGroup = new PanelLauncherGroupBean();
        }
        return launchGroup;
    }

    public void setLaunchGroup(PanelLauncherGroupBean group) {
        if (this.launchGroup == null) {
            this.launchGroup = new PanelLauncherGroupBean();
        }
        this.setJointRefDataBean(this.launchGroup, group, LAUNCH_GROUP_PROPERTY);
    }

}
