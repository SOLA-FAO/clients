/**
 * ******************************************************************************************
 * Copyright (C) 2015 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.swing.common.laf;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.FontUIResource;
import org.sola.common.WindowUtility;
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

    // Default list of fonts to use for English and common languages. 
    public static final String[] FONTNAME_DEFAULTS = {"AppleGothic", "Arial"};
    // Amharic fonts. Nyala is installed on Windows. Kefa is installed on MacOS.
    // Abyssinica SIL is an open  source font that can be downloaded from 
    // http://scripts.sil.org/cms/scripts/page.php?site_id=nrsi&id=abyssinicasil_download
    // for Windows, MacOS and Linux. Users that do not have the Nyala or Kefa fonts should
    // install Abyssinica SIL. 
    public static final String[] FONTNAME_AMHARIC = {"Nyala", "Kefa", "Abyssinica SIL"};
    public static final int FONTSIZE_NORMAL = 12;
    public static final int FONTSIZE_MEDIUM = 14;
    public static final int FONTSIZE_LARGE = 16;
    public static final int FONTSIZE_EXTRA_LARGE = 24;
    private static final String AMHARIC_LANG_CODE = "am";
    private static final String FONTSIZE_PREF_DEFAULT = "fontsize";
    private static final String FONTSIZE_PREF_AMHARIC = "fontsize.am";

    public static final String UI_PROP_TEXT_FIELD_BACKGROUND = "TextField.background";
    public static final String UI_PROP_BTN_DARK_SHADOW = "Button.darkShadow";
    public static final String UI_PROP_BTN_SHADOW = "Button.shadow";
    public static final String UI_PROP_BTN_BACKGROUND = "Button.background";
    public static final String UI_PROP_BTN_FOREGROUND = "Button.foreground";
    public static final String UI_PROP_BTN_SELECT = "Button.select";
    public static final String UI_PROP_BTN_DISABLED_TEXT = "Button.disabledText";
    public static final String UI_PROP_BTN_HIGHLIGHT = "Button.highlight";
    public static final String UI_PROP_BTN_LIGHT = "Button.light";

    public static String uiFont = FONTNAME_DEFAULTS[0]; // Use the default font

    private static LafManager laf = null;

    private LafManager() {
    }

    private String selectedTheme = GREEN_THEME;

    public static LafManager getInstance() {
        if (laf == null) {
            laf = new LafManager();
        }
        return laf;
    }

    /**
     * Sets the default font for the application
     *
     * @param uiFontStr A font string that must include the font name but can
     * also include the font size and style. See Font.decode for details.
     */
    public static void setUiFont(String uiFontStr) {
        uiFont = uiFontStr;
    }

    /**
     * Sets the uiFont to the specified size. ALso saves the font size as a user
     * preference.
     *
     * @param uiFontSize The size of font. Should be one of the FONTSIZE_
     * constants.
     */
    public static void setUiFontSize(int uiFontSize) {
        Font df = Font.decode(uiFont);
        uiFont = String.format("%s-%d", df.getFontName(), uiFontSize);

        // Capture the font size as a user preference. 
        Preferences prefs = WindowUtility.getUserPreferences();
        if (AMHARIC_LANG_CODE.equals(Locale.getDefault(Locale.Category.FORMAT).getLanguage())) {
            // The font size preference for the Amharic language. 
            prefs.put(FONTSIZE_PREF_AMHARIC, String.valueOf(uiFontSize));
        } else {
            // The font size preference for the default language. 
            prefs.put(FONTSIZE_PREF_DEFAULT, String.valueOf(uiFontSize));
        }
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
        }
    }

    /**
     * *
     * Returns the current uiFont size
     *
     * @return
     */
    public static int getUiFontSize() {
        Font df = Font.decode(uiFont);
        return df.getSize();
    }

    /**
     * Returns a FontUIResource representing the uiFont. The returned value can
     * be adjusted using the deriveFont method to create a Bold, Italic or other
     * styled font.
     *
     * @return
     */
    public static FontUIResource getUiFont() {
        return new FontUIResource(Font.decode(uiFont));
    }

    /**
     * Returns a FontUIResource representing the uiFont with the size of the
     * font adjusted by the sizeOffset. The returned value can be adjusted using
     * the deriveFont method to create a Bold, Italic or other styled font. This
     * method must be used for any component that requires text that is larger
     * or smaller than the base text size as it ensures the component text is
     * resized relatively if the user selects a different base size for the
     * uiFont.
     *
     * @param sizeOffset The number of font points to resize the default font
     * by. Can be negative, but must be more than -1 * fontSize
     * @return
     */
    public static FontUIResource getUiFont(int sizeOffset) {
        Font df = Font.decode(uiFont);
        return new FontUIResource(df.deriveFont(df.getSize() + new Float(sizeOffset)));
    }

    /**
     * Determines the font to use for the language code.
     *
     * @param languageCode
     * @return A font string including the font name and size to use for
     * displaying the language.
     */
    public static String getUiFontForLanugage(String languageCode) {

        // Set the default values for the font name and size
        String font = FONTNAME_DEFAULTS[0];
        String[] langFonts = FONTNAME_DEFAULTS;
        String fontSizePref = FONTSIZE_PREF_DEFAULT;
        int fontSize = FONTSIZE_NORMAL;

        if (AMHARIC_LANG_CODE.equals(languageCode)) {
            // Displaying the Amharic language, so adjust the default 
            // font name and size 
            font = FONTNAME_AMHARIC[0];
            langFonts = FONTNAME_AMHARIC;
            fontSizePref = FONTSIZE_PREF_AMHARIC;
            fontSize = FONTSIZE_MEDIUM;
        }

        if (WindowUtility.hasUserPreferences()) {
            // Check if the user has set a font size preference for 
            // this language/font combination. 
            Preferences prefs = WindowUtility.getUserPreferences();
            fontSize = prefs.getInt(fontSizePref, fontSize);
        }

        // Search the list of fonts suitalble for displaying this language to 
        // find one installed on this computer. If none of the fonts are installed, the
        // default Java font will be used automatically. 
        String[] installedFonts = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        out:
        for (String lf : langFonts) {
            for (String inf : installedFonts) {
                if (lf.equalsIgnoreCase(inf)) {
                    font = lf;
                    System.out.println("Font selected = " + font);
                    break out;
                }
            }
        }
        // Format the font name and size to return a font string that can be 
        // decoded by Font.decode. 
        return String.format("%s-%d", font, fontSize);
    }

    /**
     * Applies the uiFont to all UIDefault font properties to ensure the
     * application uses the correct font for display.
     *
     * @param ret
     */
    public void setUIDefaultFonts(UIDefaults ret) {
        java.util.Enumeration keys = ret.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = ret.get(key);
            if (value != null && (value instanceof javax.swing.plaf.FontUIResource || value instanceof Font)) {
                ret.put(key, new FontUIResource(Font.decode(uiFont)));
            }
        }
        // Set the font for the messages displayed by SOLA
        ret.put("OptionPane.messageFont", new FontUIResource(Font.decode(uiFont)));
        ret.put("OptionPane.buttonFont", new FontUIResource(Font.decode(uiFont)));
        ret.put("OptionPane.font", new FontUIResource(Font.decode(uiFont)));
    }

    /**
     * Sets the specified theme (a.k.a. Look and Feel) for the application. Only
     * use this method when initializing the application or changing the theme.
     * To refresh the application to display a new font, use applyTheme.
     *
     * @param theme
     */
    public void setTheme(String theme) {
        selectedTheme = theme;
        uiFont = getUiFontForLanugage(Locale.getDefault(Locale.Category.FORMAT).getLanguage());
        applyTheme();
    }

    /**
     * Applies / refreshes theme selected at startup.This method must be used if
     * the font is changed.
     */
    public void applyTheme() {
        if (STATE_LAND_THEME.equals(selectedTheme) || ADMIN_THEME.equals(selectedTheme)) {
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
                        setUIDefaultFonts(ret);

                        ret.put("RootPaneUI", NimbusRootPaneUI.class.getName());

                        URL imgURL = null;
                        String loginTitle = "";
                        if (ADMIN_THEME.equals(selectedTheme)) {
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

        if (GREEN_THEME.equals(selectedTheme)) {
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

            setUIDefaultFonts(UIManager.getDefaults());

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
}
