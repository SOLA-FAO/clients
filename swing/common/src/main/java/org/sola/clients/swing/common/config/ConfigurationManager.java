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
package org.sola.clients.swing.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import javax.swing.JOptionPane;

/** Manages various configuration parameters. */
public class ConfigurationManager {

    private static Properties wsConfig;
    private static Properties envConfig;

    /** 
     * Returns environmental parameters such as running mode 
     * ({@link EnvConfig#SOLA_RUNNING_MODE}). 
     */
    private static Properties getEnvConfig() {
        if (envConfig == null) {
            envConfig = new Properties();
            // TODO: implement loading of evironment parameters
            if (System.getProperty(EnvConfig.SOLA_RUNNING_MODE.toString()) != null) {
                envConfig.setProperty(EnvConfig.SOLA_RUNNING_MODE.toString(),
                        System.getProperty(EnvConfig.SOLA_RUNNING_MODE.toString()));
            } else {
                // Set default to debug. change in production environment to RUN
                envConfig.setProperty(EnvConfig.SOLA_RUNNING_MODE.toString(),
                        EnvRunningModes.DEBUG.toString());
            }
        }
        return envConfig;
    }

    /** 
     * Returns Web-services configuration parameters, such as WSDL location of 
     * the service. Tries to load them from different sources in a certain priority order.
     */
    public static HashMap<String, String> getWSConfig() {

        HashMap<String, String> config = new HashMap<String, String>();

        if (wsConfig == null) {
            try {
                wsConfig = new Properties();
                String folderName = System.getProperty("user.home") + "/sola";
                String fileName = folderName + "/solaws.conf";
                File file = new File(fileName);

                // Load from system properties
                for (Enumeration it = System.getProperties().keys(); it.hasMoreElements();) {
                    Object systemProp = it.nextElement();
                    if (systemProp != null && systemProp.toString().startsWith("jnlp.SOLA_WS")) {
                        wsConfig.setProperty(systemProp.toString().replaceAll("jnlp.", ""), System.getProperty(systemProp.toString()));
                    }
                }

                if (wsConfig.size() == 0) {
                    // Load from embedded config file
                    // TODO: Remove loading from embedded config in final deployment scenario
                    wsConfig.load(ConfigurationManager.class.getResourceAsStream("/config/wsconfig.properties"));

                    if (wsConfig.size() == 0) {
                        // Load from local config file
                        if (file.exists()) {
                            // Load config
                            FileInputStream inputConfigStream = new FileInputStream(fileName);
                            wsConfig.load(inputConfigStream);
                            inputConfigStream.close();
                        } else {
                            // TODO: Open popup form to update/enter new settings
                            // Get new properties and save to local file
                            updateWSLocalConfigFile(fileName, folderName);
                        }
                        // Check config
                        if (wsConfig.size() < 1) {
                            // Not all arguments have been passed
                            int userSelectedOption = JOptionPane.showConfirmDialog(null,
                                    "There are some missing configuration values. Do you want to setup new values in local config file?",
                                    "", JOptionPane.YES_NO_OPTION);

                            while (userSelectedOption == JOptionPane.YES_OPTION) {
                                // TODO: Open popup form to unpdate/enter new settings
                                updateWSLocalConfigFile(fileName, folderName);
                                loadWSConfigFromLocalConfigFile(fileName);

                                if (wsConfig.size() < 1) {
                                    userSelectedOption = JOptionPane.showConfirmDialog(null,
                                            "There are some missing configuration values. Do you want to setup new values in local config file?",
                                            "", JOptionPane.YES_NO_OPTION);
                                } else {
                                    userSelectedOption = JOptionPane.NO_OPTION;
                                }
                            }
                        }
                    }
                } else {
                    // Save to local config if it exists
                    if (file.exists()) {
                        updateWSLocalConfigFile(fileName, folderName);
                    }
                }

                if (wsConfig.size() == 0) {
                    throw new Exception("No configuration settings provided");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Can't initialize configuration settings. " + ex.getMessage());
            }
        }

        if (wsConfig != null && wsConfig.size() > 0) {
            for (Entry<Object, Object> entry : wsConfig.entrySet()) {
                config.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        return config;
    }

    /** 
     * This method is used by {@link #getWSConfig()} to load configuration 
     * values from the file on the local drive.
     * @param filePath The full path to the configuration file.
     */
    private static void loadWSConfigFromLocalConfigFile(String filePath) {
        try {
            FileInputStream inputConfigStream = null;
            inputConfigStream = new FileInputStream(filePath);
            wsConfig.load(inputConfigStream);
            inputConfigStream.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Can't initialize configuration settings. " + ex.getMessage());
        }
    }

    /** 
     * Updates local configuration file with new values.
     * @param filePath The full path to the configuration file.
     * @param folderName The full path to the folder with configuration file.
     */
    private static void updateWSLocalConfigFile(String filePath, String folderName) {
        try {
            File folder = new File(folderName);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            FileOutputStream out = null;
            out = new FileOutputStream(filePath);
            wsConfig.store(out, null);
            out.close();

        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Can't initialize configuration settings. " + ex.getMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Can't initialize configuration settings. " + ex.getMessage());
        }

    }

    /** 
     * Returns environmental configuration value.
     * @param key The key of the value to return.
     */
    public static String getEnvConfigValue(EnvConfig key) {
        return getEnvConfig().getProperty(key.toString());
    }
}
