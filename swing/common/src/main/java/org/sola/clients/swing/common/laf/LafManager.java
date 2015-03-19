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
package org.sola.clients.swing.common.laf;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author rizzom
 */
/**
 * Provides methods to manage look and feel settings.
 */
public class LafManager {

    public static final String STATE_LAND_THEME = "state_land";
    public static final String ADMIN_THEME = "admin";
    public static final String GREEN_THEME = "green";

    public Object getBgFont() {
        return bgFont;
    }

    public void setBgFont(Object bgFont) {
        this.bgFont = bgFont;
    }

    public Object getBtnBackground() {
        return btnBackground;
    }

    public void setBtnBackground(Object btnBackground) {
        this.btnBackground = btnBackground;
    }

    public Object getBtnFont() {
        return btnFont;
    }

    public void setBtnFont(Object btnFont) {
        this.btnFont = btnFont;
    }

    public Object getBtnDarkShadow() {
        return btnDarkShadow;
    }

    public Object getBtnDisabledText() {
        return btnDisabledText;
    }

    public Object getBtnForeground() {
        return btnForeground;
    }

    public Object getBtnHighlight() {
        return btnHighlight;
    }

    public Object getBtnLight() {
        return btnLight;
    }

    public Object getBtnSelect() {
        return btnSelect;
    }

    public Object getBtnShadow() {
        return btnShadow;
    }

    public Object getCmbFont() {
        return cmbFont;
    }

    public void setCmbFont(Object cmbFont) {
        this.cmbFont = cmbFont;
    }

    public Object getForeFont() {
        return foreFont;
    }

    public void setForeFont(Object foreFont) {
        this.foreFont = foreFont;
    }

    public Object getLabFont() {
        return labFont;
    }

    public Object getTxtFieldBg() {
        return txtFieldBg;
    }

    public Font getLabFontBold() {
        return Font.decode("AppleGothic-BOLD-12");
    }

    public void setLabFont(Object labFont) {
        this.labFont = labFont;
    }

    public Object getTabFont() {
        return tabFont;
    }

    public void setTabFont(Object tabFont) {
        this.tabFont = tabFont;
    }

    public Object getTxtAreaFont() {
        return txtAreaFont;
    }

    public void setTxtAreaFont(Object txtAreaFont) {
        this.txtAreaFont = txtAreaFont;
    }

    public Object getTxtFont() {
        return txtFont;
    }

    public void setTxtFont(Object txtFont) {
        this.txtFont = txtFont;
    }

    public void setLabProperties(JLabel label) {
        label.setFont(UIManager.getFont(labFont));
        label.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        label.setHorizontalAlignment(JLabel.LEADING);
    }

    public void setTxtProperties(JTextField txt) {
        txt.setFont(UIManager.getFont(txtFont));
        txt.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txt.setHorizontalAlignment(JTextField.LEADING);
    }

    public void setFormattedTxtProperties(JFormattedTextField txt) {
        txt.setFont(UIManager.getFont(txtFont));
        txt.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        txt.setHorizontalAlignment(JTextField.LEADING);
    }

    public void setTxtAreaProperties(JTextArea txtarea) {
        txtarea.setFont(UIManager.getFont(txtAreaFont));
        txtarea.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    public void setCmbProperties(JComboBox combo) {
        combo.setFont(UIManager.getFont(cmbFont));
        combo.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    public void setChkProperties(JCheckBox check) {
        check.setFont(UIManager.getFont(labFont));
        check.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    public void setBtnProperties(JButton button) {
        button.setFont(UIManager.getFont(btnFont));
        button.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
        button.setHorizontalAlignment(JButton.CENTER);
    }

    public void setTabProperties(JTabbedPane tab) {
        tab.setFont(UIManager.getFont(tabFont));
        tab.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    public void setListProperties(JList list) {
        list.setFont(UIManager.getFont(listFont));
        list.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    public void setRadioProperties(JRadioButton radio) {
        radio.setFont(UIManager.getFont(radioFont));
        radio.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    public void setPassProperties(JPasswordField password) {
        password.setFont(UIManager.getFont(passwordFont));
        password.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
    }

    private LafManager() {
    }
    private static LafManager laf = null;

    public static LafManager getInstance() {
        if (laf == null) {
            laf = new LafManager();
        }
        return laf;
    }
    private Object foreFont = "nimbusBase";
    private Object labFont = "Label.font";
    private Object bgFont = "PasswordField.background";
    private Object txtFont = "TextField.font";
    private Object txtAreaFont = "TextArea.font";
    private Object btnFont = "Button.font";
    private Object tabFont = "TabbedPane.font";
    private Object cmbFont = "ComboBox.font";
    private Object listFont = "List.font";
    private Object radioFont = "RadioButton.font";
    private Object passwordFont = "PasswordField.font";
    private Object btnBackground = "Button.background";
    private Object btnSelect = "Button.select";
    private Object btnDarkShadow = "Button.darkShadow";
    private Object btnDisabledText = "Button.disabledText";
    private Object btnForeground = "Button.foreground";
    private Object btnHighlight = "Button.highlight";
    private Object btnLight = "Button.light";
    private Object btnShadow = "Button.shadow";
    private Object txtFieldBg = "TextField.background";

    /**
     * sets {@link Look and feel} settings accordingly.
     */
    public void setProperties(final String theme) {
        UIDefaults defaults = UIManager.getDefaults();
        final Object painterScrollbar = defaults.get("ScrollBar:ScrollBarThumb[Disabled].backgroundPainter");

        if (STATE_LAND_THEME.equals(theme) || ADMIN_THEME.equals(theme)) {
            try {

                UIManager.installLookAndFeel("AdvancedNimbus", AdvancedNimbusLookAndFeel.class.getName());
                UIManager.setLookAndFeel(new AdvancedNimbusLookAndFeel() {

                    @Override
                    public UIDefaults getDefaults() {
                        UIDefaults ret = super.getDefaults();
                        ret.put("nimbusBase", new Color(239, 239, 233));
                        ret.put("control", new Color(245, 250, 240));
                        ret.put("info", new Color(242, 242, 189));
                        ret.put("nimbusAlertYellow", new Color(255, 220, 35));
                        ret.put("nimbusDisabledText", new Color(50, 50, 50));
                        ret.put("nimbusFocus", new Color(239, 239, 233));
                        ret.put("nimbusGreen", new Color(154, 177, 95));
                        ret.put("nimbusBlueGrey", new Color(154, 177, 95));
                        ret.put("nimbusInfoBlue", new Color(154, 177, 95));
                        ret.put("nimbusLightBackground", new Color(255, 255, 255));
                        ret.put("nimbusOrange", new Color(33, 124, 149));
                        ret.put("nimbusRed", new Color(140, 60, 34));
                        ret.put("nimbusSelectedText", new Color(0, 0, 0));
                        ret.put("nimbusSelectionBackground", new Color(181, 181, 150));

                        //      ####  Secondary Colors   #####
                        ret.put("activeCaption", new Color(181, 181, 150));
                        ret.put("background", new Color(239, 239, 233));
                        ret.put("controlDkShadow", new Color(181, 181, 150));
                        ret.put("controlShadow", new Color(239, 239, 233));
                        ret.put("inactiveCaption", new Color(181, 181, 150));
                        ret.put("infoText", new Color(0, 0, 0));
                        ret.put("menu", new Color(239, 239, 233));
                        ret.put("menuText", new Color(0, 0, 0));
                        ret.put("nimbusBorder", new Color(255, 255, 255));
                        ret.put("nimbusSelection", new Color(154, 177, 95));
                        ret.put("scrollbar", new Color(154, 177, 95));
                        ret.put("textBackground", new Color(239, 239, 233));
                        ret.put("textHighlight", new Color(255, 255, 255));
                        ret.put("textHighlightText", new Color(255, 255, 255));
                        ret.put("textInactiveText", new Color(142, 143, 145));
                        ret.put("paleSolaGrey", new Color(239, 239, 233));
                        ret.put("SolaGrey", new Color(181, 181, 150));
                        ret.put("SolaGroup", new Color(181, 181, 150));
                        ret.put("SolaHeader", new Color(154, 177, 95));

//      ####  Background/Foreground Colors   #####         
                        ret.put("PasswordField.background", new Color(236, 247, 235));
                        ret.put("Table.alternateRowColor", new Color(255, 255, 255));
                        ret.put("List.background", new Color(255, 255, 255));
                        ret.put("Table.dropLineColor", new Color(255, 255, 255));
                        ret.put("MenuItem.background", new Color(255, 255, 255));
                        ret.put("List.foreground", new Color(0, 102, 51));
                        ret.put("List[Selected].textBackground", new Color(154, 177, 95));
                        ret.put("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter", new FillPainter(new Color(154, 177, 95)));
                        ret.put("MenuBar:Menu[Enabled].textForeground", new Color(255, 255, 255));
                        ret.put("MenuBar[Enabled].backgroundPainter", new FillPainter(new Color(181, 181, 150)));
                        ret.put("MenuBar:Menu[Selected].backgroundPainter", new FillPainter(new Color(154, 177, 95)));

                        //      #### FONTS  ####   
                        ret.put("TextField.font", Font.decode("AppleGothic"));
                        ret.put("TextArea.font", Font.decode("AppleGothic"));
                        ret.put("PasswordField.font", Font.decode("AppleGothic"));
                        ret.put("ComboBox.font", Font.decode("AppleGothic"));
                        ret.put("Button.font", Font.decode("AppleGothic"));
                        ret.put("TabbedPane.font", Font.decode("AppleGothic"));
                        ret.put("Table.font", Font.decode("AppleGothic"));
                        ret.put("Label.font", Font.decode("AppleGothic"));
                        ret.put("List.font", Font.decode("AppleGothic"));
                        ret.put("RadioButton.font", Font.decode("AppleGothic"));
                        ret.put("RootPaneUI", NimbusRootPaneUI.class.getName());

                        URL imgURL = null;
                        String loginTitle = "";
                        if (ADMIN_THEME.equals(theme)) {
                            // Use the icon for the admin theme 
                            imgURL = this.getClass().getResource("/images/common/sola_icon_admin.png");
                            loginTitle = MessageUtility.getLocalizedMessageText(ClientMessage.SECURITY_LOGIN_TITLE_ADMIN);
                        } else {
                            // Use the icon for the registry theme 
                            imgURL = this.getClass().getResource("/images/common/sola_icon_state_land.png");
                            loginTitle = MessageUtility.getLocalizedMessageText(ClientMessage.SECURITY_LOGIN_TITLE_STATE_LAND);
                        }
                        ret.put("solaTitleBarIcon", new ImageIcon(imgURL));
                        ret.put("solaLoginTitle", loginTitle);
                        
                        // Indicates this is a custom LAF for sola.
                        ret.put("solaLAF", true); 
                        return ret;
                    }
                });

            } catch (Exception ex) {
                throw new Error(ex);
            }
            JFrame.setDefaultLookAndFeelDecorated(true);
        }

        if (GREEN_THEME.equals(theme)) {
//      #### Primary Colors  ####        
            UIManager.put("nimbusBase", new Color(107, 160, 35)); /*
             * DEFAULT COLOR USED (51,98,140) 107,142,35 ALEX 185,208,196
             */

            UIManager.put("control", new Color(245, 250, 240)); /*
             * DEFAULT BACKGROUND (214,217,223) OUR OLD [226,244,224
             */

            UIManager.put("info", new Color(242, 242, 189)); /*
             * TOOL TIP INFO (242,242,189)
             */

            UIManager.put("nimbusAlertYellow", new Color(255, 220, 35));  /*
             * ALERT TRIANGLE (255,220,35)
             */

            UIManager.put("nimbusDisabledText", new Color(50, 50, 50));	/*
             * (142,143,145)
             */

            UIManager.put("nimbusFocus", new Color(107, 142, 35));	 /*
             * (115,164,209)
             */

            UIManager.put("nimbusGreen", new Color(176, 179, 50));	 /*
             * (176,179,50)
             */

            UIManager.put("nimbusInfoBlue", new Color(47, 190, 180));	 /*
             * (47,92,180)
             */

            UIManager.put("nimbusLightBackground", new Color(255, 255, 255));  /*
             * (255,255,255)
             */

            UIManager.put("nimbusOrange", new Color(191, 120, 4));	  /*
             * (191,98,4)
             */

            UIManager.put("nimbusRed", new Color(140, 60, 34));	 /*
             * (169,46,34)
             */

            UIManager.put("nimbusSelectedText", new Color(255, 255, 255));  /*
             * (255,255,255)
             */

            UIManager.put("nimbusSelectionBackground", new Color(57, 105, 138)); /*
             * SELECTED ELEMENT (57,105,138)
             */
//        UIManager.put("text", new Color(0,0,0));  /*(0,0,0)*/
//      ####  Secondary Colors   #####

            UIManager.put("activeCaption", new Color(186, 190, 198)); 	/*
             * (186,190,198)
             */

            UIManager.put("background", new Color(214, 217, 223));           /*
             * (214,217,223)
             */

            UIManager.put("controlDkShadow", new Color(164, 171, 184)); 	/*
             * (164,171,184)
             */
//         UIManager.put("controlHighlight", new Color(233,236,242)); 	/*(233,236,242)*/  
//         UIManager.put("controlLHighligh", new Color(247,248,250)); 	/*(247,248,250)*/  

            UIManager.put("controlShadow", new Color(204, 211, 224));        /*
             * (204,211,224)
             */
//         UIManager.put("controlText", new Color(186,190,198));          /*(186,190,198)*/ 
//         UIManager.put("desktop", new Color(61,96,121));                /*(61,96,121)*/ 

            UIManager.put("inactiveCaption", new Color(189, 193, 200)); 	/*
             * (189,193,200)
             */

            UIManager.put("infoText", new Color(0, 0, 0));                   /*
             * (0,0,0)
             */

            UIManager.put("menu", new Color(237, 239, 242));                 /*
             * (237,239,242)
             */

            UIManager.put("menuText", new Color(0, 0, 0));                   /*
             * (0,0,0)
             */

            UIManager.put("nimbusBlueGrey", new Color(105, 145, 85));        /*
             * BORDER, CAPTION (169,176,190)
             */

            UIManager.put("nimbusBorder", new Color(146, 151, 161)); 	/*
             * BORDERs OF SECTIONs(146,151,161)
             */

            UIManager.put("nimbusSelection", new Color(124, 205, 124));          /*
             * SELECTED ELEMENT (57,105,138)
             */

            UIManager.put("scrollbar", new Color(205, 208, 213));            /*
             * (205,208,213)
             */

            UIManager.put("textBackground", new Color(57, 105, 138));        /*
             * (57,105,138)
             */
//         UIManager.put("textForeground", new Color(0,0,0));             /*(0,0,0)*/ 

            UIManager.put("textHighlight", new Color(255, 255, 255));         /*
             * (57,105,138)
             */

            UIManager.put("textHighlightText", new Color(255, 255, 255));  	/*
             * (255,255,255)
             */

            UIManager.put("textInactiveText", new Color(142, 143, 145));     /*
             * (142,143,145)
             */

//            GROUP PANEL BACKGROUND
            UIManager.put("SolaGroup", new Color(153, 153, 153));
//            HEADER PANEL BACKGROUND        
            UIManager.put("SolaHeader", new Color(51, 153, 0));

//      ####  Background Colors   #####         
            UIManager.put("PasswordField.background", new Color(236, 247, 235));   /*
             * (231,245,162) ALEX[200,244,200]
             */

            UIManager.put("Table.background", new Color(226, 244, 224));
            UIManager.put("Table.alternateRowColor", new Color(226, 244, 224));     /*
             * 236,247,235
             */

            UIManager.put("List.background", new Color(185, 227, 185));
            UIManager.put("Table.dropLineColor", new Color(166, 212, 150));
            UIManager.put("List.foreground", new Color(0, 102, 51));
            UIManager.put("TextField.font", Font.decode("AppleGothic"));
            UIManager.put("TextArea.font", Font.decode("AppleGothic"));
            UIManager.put("PasswordField.font", Font.decode("AppleGothic"));
            UIManager.put("ComboBox.font", Font.decode("AppleGothic"));
            UIManager.put("Button.font", Font.decode("AppleGothic"));
            UIManager.put("TabbedPane.font", Font.decode("AppleGothic"));
            UIManager.put("Table.font", Font.decode("AppleGothic"));
            UIManager.put("Label.font", Font.decode("AppleGothic"));
            UIManager.put("List.font", Font.decode("AppleGothic"));
            UIManager.put("RadioButton.font", Font.decode("AppleGothic"));

            // Set the default image Icon
            URL imgURL = this.getClass().getResource("/images/common/sola_icon_default.jpg");
            UIManager.put("solaTitleBarIcon", new ImageIcon(imgURL));
            
            // Indicates this is not a custom LAF for sola.
            UIManager.put("solaLAF", false); 
            

            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(LafManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(LafManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(LafManager.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedLookAndFeelException ex) {
                        Logger.getLogger(LafManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }

        }

    }

    public void setTitleTextProperties(String titleText) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
