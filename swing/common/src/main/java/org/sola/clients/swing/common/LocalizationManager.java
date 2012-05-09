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
package org.sola.clients.swing.common;

import java.io.File;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Provides methods to manage languages and locales settings.
 */
public class LocalizationManager {

    private static final String LANGUAGE = "language";
    private static final String COUNTRY = "country";

    /** 
     * Loads default language and country codes and sets {@link Locale} settings
     * accordingly. 
     */
    public static void loadLanguage(Class<?> applicationMainClass) {
        Preferences prefs = Preferences.userNodeForPackage(applicationMainClass);
        Locale defaultLocale = Locale.getDefault(Locale.Category.FORMAT);

        String language = prefs.get(LANGUAGE, "en");
        String country = prefs.get(COUNTRY, "US");
        
        if(defaultLocale.getLanguage().equalsIgnoreCase(language)){
            // Override country code from local settings
            country = defaultLocale.getCountry();
        }
        Locale loc = new Locale(language, country);
        Locale.setDefault(loc);

    }

    /** 
     * Returns preference language code. If language is not set, <b>en</b> is returned by default.
     * @return Two letters language code.
     */
    public static String getLanguage(Class<?> applicationMainClass) {
        Preferences prefs = Preferences.userNodeForPackage(applicationMainClass);
        return prefs.get(LANGUAGE, "en");
    }

    /** Sets selected language and stores it in the user's preferences. 
     * @param language Two letters language name in lowercase.
     * @param country Two letters country name in uppercase.
     */
    public static void setLanguage(Class<?> applicationMainClass, String language, String country) {
        Preferences prefs = Preferences.userNodeForPackage(applicationMainClass);

        prefs.put(LANGUAGE, language);
        prefs.put(COUNTRY, country);
        try {
            prefs.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }

    }

    /** Restarts application. */
    public static boolean restartApplication(Class<?> applicationMainClass) {
        String javaBin = System.getProperty("java.home") + "/bin/java";
        File jarFile;
        try {
            jarFile = new File(applicationMainClass.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
        } catch (Exception e) {
            return false;
        }

        /* is it a jar file? */
        if (!jarFile.getName().endsWith(".jar")) {
            return false;   //no, it's a .class probably  
        }
        String toExec[] = new String[]{javaBin, "-jar", jarFile.getPath()};
        try {
            Process p = Runtime.getRuntime().exec(toExec);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        System.exit(0);
        return true;
    }
}
