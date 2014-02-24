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
package org.sola.clients.swing.common.controls;

import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

/** 
 * Allows to display multiple buttons in the way of dropdown list. 
 * The role of dropdown list plays popup menu, which should be assigned through 
 * the <code>componentPopupMenu</code> property.
 */
public class DropDownButton extends JToggleButton {

    private JPopupMenu popUpMenu;

    /** Class constructor. Initializes default settings. */
    public DropDownButton() {
        super();
        this.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/common/dropdown_arrow.png")));
        this.setFocusPainted(false);
        this.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        this.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        this.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);

        this.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                buttonMousePressed(evt);
            }
        });
    }

    /** Keeps horizontal text position always LEADING. */
    @Override
    public final void setHorizontalTextPosition(int textPosition) {
        super.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
    }

    /** Keeps vertical text position always CENTER. */
    @Override
    public final void setVerticalTextPosition(int textPosition) {
        super.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
    }

    /** Shows popup menu if button was pressed. */
    private void buttonMousePressed(java.awt.event.MouseEvent evt) {
        this.setComponentPopupMenu(popUpMenu);
        if (popUpMenu != null) {
            popUpMenu.show(this, 0, this.getHeight());
            int width = this.getWidth();
            if (width < popUpMenu.getWidth()) {
                width = popUpMenu.getWidth();
            }
            popUpMenu.setPopupSize(width, popUpMenu.getHeight());
        }
    }

    /** Returns popup menu, used to display list of choices. */
    @Override
    public JPopupMenu getComponentPopupMenu() {
        return popUpMenu;
    }

    /** Sets popup menu to display list of choices. */
    @Override
    public void setComponentPopupMenu(JPopupMenu popup) {
        JPopupMenu oldPopup = popUpMenu;
        popUpMenu = popup;
        if (popUpMenu != null) {
            popUpMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                }
                @Override
                public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
                }
                @Override
                public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                    setSelected(false);
                }
            });
        }
        firePropertyChange("componentPopupMenu", oldPopup, popup);
    }
}
