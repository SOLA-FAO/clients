/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
