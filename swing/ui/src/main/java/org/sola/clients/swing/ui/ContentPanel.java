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
package org.sola.clients.swing.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import org.sola.common.help.HelpUtility;

/**
 * Basic class for the panel forms, used to display application logic.
 */
public class ContentPanel extends JPanel {

    private MainContentPanel mainContentPanel;
    private HeaderPanel headerPanel;
    private PropertyChangeListener headerPanelListener;
    private boolean closeOnHide = false;
    private String helpTopic;
    
    public ContentPanel() {
        super();
        headerPanelListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                handleHeaderPanelPropertyChanges(evt);
            }
        };
    }

    /**
     * Returns help topic name, related to this panel.
     */
    public String getHelpTopic() {
        return helpTopic;
    }

    /**
     * Sets help topic name, related to this panel.
     */
    public void setHelpTopic(String helpTopic) {
        this.helpTopic = helpTopic;
    }
    
    public HeaderPanel getHeaderPanel() {
        return headerPanel;
    }

    public void setHeaderPanel(HeaderPanel headerPanel) {
        if (this.headerPanel != null) {
            this.headerPanel.removePropertyChangeListener(headerPanelListener);
        }
        this.headerPanel = headerPanel;
        try {
            this.headerPanel.addPropertyChangeListener(headerPanelListener);
        } catch (Exception e) {
        }
    }

    public MainContentPanel getMainContentPanel() {
        return mainContentPanel;
    }

    public void setMainContentPanel(MainContentPanel mainContentPanel) {
        this.mainContentPanel = mainContentPanel;
    }

    public boolean isCloseOnHide() {
        return closeOnHide;
    }

    public void setCloseOnHide(boolean closeOnHide) {
        this.closeOnHide = closeOnHide;
    }

    // METHODS
    /**
     * Listens to the {@link HeaderPanel} property changes to trap control
     * buttons click.
     */
    private void handleHeaderPanelPropertyChanges(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(HeaderPanel.CLOSE_BUTTON_CLICKED)) {
            if (panelClosing()) {
                close();
            }
        }
        if (evt.getPropertyName().equals(HeaderPanel.HELP_BUTTON_CLICKED)) {
            showHelp();
        }
    }
    
    /** Shows help topic, related to this panel. */
    public void showHelp(){
        if(helpTopic!=null){
            HelpUtility.getInstance().showTopic(helpTopic);
        }
    }
    
    /**
     * Close the panel from the {@link MainContentPanel}
     */
    public void close() {
        if (getMainContentPanel() != null) {
            getMainContentPanel().closePanel(this);
        } else {
            firePropertyChange(HeaderPanel.CLOSE_BUTTON_CLICKED, false, true);
        }
    }

    /**
     * This method is called each time when panel is added to the main content
     * panel.
     */
    protected void panelAdded() {
    }

    /**
     * This method is called each time when panel is going to be closed. If true
     * is returned, panel will be closed, if false, close procedure will be
     * interrupted.
     */
    protected boolean panelClosing() {
        return true;
    }

    /**
     * This method is called each time when panel is shown on the main content
     * panel.
     */
    protected void panelShown() {
    }
}
