/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.swing.common.controls;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.sola.clients.swing.common.LocalizationManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Allows to select different languages to change language of the application.
 */
public class LanguageCombobox extends JComboBox {

    /**
     * ComboBox renderer class. Displays flags for each language.
     */
    private class ComboBoxRenderer extends JLabel implements ListCellRenderer {

        private Font uhOhFont;

        public ComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
            setPreferredSize(new Dimension(20, 20));
        }

        /*
         * This method finds the image and text corresponding to the selected
         * value and returns the label, set up to display the text and image.
         */
        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)
            int selectedIndex = ((Integer) value).intValue();

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            //Set the icon and text.  If icon was null, say so.
            ImageIcon icon = languageIcons[selectedIndex];
            String language = languageStrings[selectedIndex];
            setIcon(icon);
            if (icon != null) {
                setText(language);
                setFont(list.getFont());
            } else {
                setUhOhText(language + " (no image available)", list.getFont());
            }

            return this;
        }

        //Set the font and text when no image was found.
        protected void setUhOhText(String uhOhText, Font normalFont) {
            if (uhOhFont == null) { //lazily create this font
                uhOhFont = normalFont.deriveFont(Font.ITALIC);
            }
            setFont(uhOhFont);
            setText(uhOhText);
        }
    }
    private boolean showMessage = true;
    public boolean confirmedChange = false;
    private String[] languageStrings = {"English", "Italian", "नेपाली"};
    private String[] languageIconNames = {"en.jpg", "it.jpg", "np.png"};
    private ImageIcon[] languageIcons;
    private static final Map<String, Integer> languagesMap = Collections.unmodifiableMap(new HashMap(2, 1.0f) {
        {
            put("en", 0);
            put("it", 1);
            put("np", 2);
        }
    });

    /**
     * Class constructor.
     *
     * @param applicationMainClass The main class of application, where this
     * control is used. Application class needed to pick up and save preferred
     * setting of the language.
     */
    public LanguageCombobox() {
        super();
        setModel(new javax.swing.DefaultComboBoxModel(new Integer[]{0, 1, 2}));
        addLanguageIcons();
        setRenderer(new ComboBoxRenderer());
        setMaximumRowCount(4);
        revalidate();

    }

    private void addLanguageIcons() {

        languageIcons = new ImageIcon[languageStrings.length];
        Integer[] intArray = new Integer[languageStrings.length];

        for (int i = 0; i < languageStrings.length; i++) {
            intArray[i] = new Integer(i);
            languageIcons[i] = createImageIcon(languageIconNames[i]);
            if (languageIcons[i] != null) {
                languageIcons[i].setDescription(languageStrings[i]);
            }
        }

        String selectedLanguage = LocalizationManager.getLanguage();

        if (selectedLanguage != null && !selectedLanguage.equals("")
                && languagesMap != null && languagesMap.containsKey(selectedLanguage)) {
            showMessage = false;
            setSelectedIndex(languagesMap.get(selectedLanguage));
            showMessage = true;
        }
    }

    @Override
    protected void selectedItemChanged() {
        super.selectedItemChanged();

        if (getSelectedItem() != null) {
            int language = (Integer) getSelectedItem();

            if ("italian".equalsIgnoreCase(languageStrings[language])) {
                LocalizationManager.setLanguage("it", "IT");
            } else if ("english".equalsIgnoreCase(languageStrings[language])) {
                LocalizationManager.setLanguage("en", "US");
            } else if ("नेपाली".equalsIgnoreCase(languageStrings[language])) {
                LocalizationManager.setLanguage("np", "NP");
            }
            if (showMessage) {
                LocalizationManager.loadLanguage();
                if (!this.confirmedChange) {
                    MessageUtility.displayMessage(ClientMessage.GENERAL_UPDATE_LANG);
                    LocalizationManager.restartApplication();
                }
            }
        }
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String name) {
        URL imgURL = LanguageCombobox.class.getResource("/images/flags/" + name);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + name);
            return null;
        }
    }
}
