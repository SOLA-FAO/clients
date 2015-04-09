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
package org.sola.clients.swing.ui.localization;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.swing.common.laf.LafManager;
import org.sola.common.WindowUtility;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Provides methods to manage languages and locales settings.
 */
public class LocalizationManager {

    private static final String LANGUAGE = "language";
    private static final String COUNTRY = "country";
    private static final String WEB_START_HOST_PROP = "jnlp.SOLA_WEB_START_HOST";
    // Update to indicate the production host name and IP address. Also
    // update the version number for each production release of SOLA. 
    private static final String PRODUCTION_HOST_NAME = "flossola.org";
    private static final String PRODUCTION_HOST_IP = "46.149.21.37";
    //private static final String PRODUCTION_HOST_NAME = "localhost";
    //private static final String PRODUCTION_HOST_IP = "127.0.0.1";

    /**
     * Loads default language and country codes and sets {@link Locale} settings
     * accordingly.
     */
    public static void loadLanguage() {
        Locale defaultLocale = Locale.getDefault(Locale.Category.FORMAT);

        String language = defaultLocale.getLanguage();
        String country = defaultLocale.getCountry();

        if (WindowUtility.hasUserPreferences()) {
            Preferences prefs = WindowUtility.getUserPreferences();
            language = prefs.get(LANGUAGE, language);

            if (!defaultLocale.getLanguage().equalsIgnoreCase(language)) {
                // Set country code from the preferred settings
                country = prefs.get(COUNTRY, country);
            }
        }

        Locale loc = new Locale(language, country);
        Locale.setDefault(loc);
    }

    /**
     * Returns language code from default locale.
     *
     * @return Two letters language code.
     */
    public static String getLanguage() {
        Locale defaultLocale = Locale.getDefault(Locale.Category.FORMAT);
        return defaultLocale.getLanguage();
    }

    /**
     * Returns locale code from default locale.
     *
     * @return nn-NN pattern.
     */
    public static String getLocaleCode() {
        Locale defaultLocale = Locale.getDefault(Locale.Category.FORMAT);
        return defaultLocale.getLanguage() + "-" + defaultLocale.getCountry();
    }

    /**
     * Sets selected language and stores it in the user's preferences.
     *
     * @param language Two letters language name in lowercase.
     * @param country Two letters country name in uppercase.
     */
    public static void setLanguage(String language, String country) {
        Preferences prefs = WindowUtility.getUserPreferences();
        prefs.put(LANGUAGE, language);
        prefs.put(COUNTRY, country);
        try {
            prefs.flush();
            CacheManager.clear();
            ResourceBundle.clearCache();
            loadLanguage();
        } catch (BackingStoreException ex) {
        }
        // Check if the Font must change due to the change in Language. 
        LafManager.setUiFont(LafManager.getUiFontForLanugage(language));
        LafManager.getInstance().applyTheme();
    }

    /**
     * Determines if the application is connected to the production server or
     * not based on the name of the Service Host. Uses the SOLA_WEB_START_HOST
     * property to make this determination. This property can be set as a
     * startup parameter for the JVM process. If the SOLA_WEB_START_HOST
     * property is not set, the method assumes this is a development version and
     * returns true to indicate a production implementation.
     *
     * @return
     */
    public static boolean isProductionHost() {
        boolean result = false;
        String host = System.getProperty(WEB_START_HOST_PROP);
        LogUtility.log("Host Name = " + (host == null ? "Unknown" : host));
        // If the host variable is not set then this is probably development
        if (host == null || host.equalsIgnoreCase(PRODUCTION_HOST_NAME)
                || host.equals(PRODUCTION_HOST_IP)) {
            result = true;
        }
        return result;
    }

    /**
     * Determines the version number for display based on whether this is a
     * production version of SOLA or a Test version.
     *
     * @return
     */
    public static String getVersionNumber() {
        String result = MessageUtility.getLocalizedMessageText(ClientMessage.ADMIN_TEST_VERSION_NUMBER);
        if (isProductionHost()) {
            result = MessageUtility.getLocalizedMessageText(ClientMessage.ADMIN_PROD_VERSION_NUMBER);
        }
        return result;
    }

    /**
     * Returns language code from provided string. String format must be
     * language-County (e.g. en-US)
     *
     * @param localeString String with language code and country code
     * @return
     */
    public static String getLangCode(String localeString) {
        if (localeString == null || localeString.equals("")) {
            return "";
        }
        String[] codes = localeString.split("-");
        return codes[0];
    }

    /**
     * Returns country code from provided string. String format must be
     * language-County (e.g. en-US)
     *
     * @param localeString String with language code and country code
     * @return
     */
    public static String getCountryCode(String localeString) {
        if (localeString == null || localeString.equals("")) {
            return "";
        }
        String[] codes = localeString.split("-");
        if (codes.length > 1) {
            return codes[1];
        } else {
            return "";
        }
    }
}
