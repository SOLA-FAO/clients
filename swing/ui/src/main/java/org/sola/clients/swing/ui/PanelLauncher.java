/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.swing.ui;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.sola.clients.beans.cache.CacheManager;
import org.sola.clients.beans.system.ConfigPanelLauncherBean;
import org.sola.clients.swing.common.tasks.SolaTask;
import org.sola.clients.swing.common.tasks.TaskManager;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * Encapsulates the logic required to determine which panel to create to for a
 * service or RRR. Also determines the constructor to use based on the
 * constructor arguments indicated.
 *
 * PanelLauncher has been setup as a singleton with some static helper methods
 * to make it simpler to use.
 *
 * NOTE: The constructorArgs array boxes primitive types, so you must ensure the
 * panel class constructor is declared with boxed data types instead of
 * primitive data types (e.g. the constructor should use Boolean instead of
 * boolean).
 *
 * @author soladev
 */
public final class PanelLauncher {

    private PanelLauncher() {
    }

    private static class PanelLauncherHolder {

        private static final PanelLauncher INSTANCE = new PanelLauncher();
    }

    /**
     * Returns a singleton instance of {@link PanelLauncher}.
     */
    public static PanelLauncher getInstance() {
        return PanelLauncherHolder.INSTANCE;
    }

    /**
     * Returns the bean matching the panel launcher code
     */
    private ConfigPanelLauncherBean getBean(String panelLauncherCode) {
        return CacheManager.getBeanByCode(CacheManager.getPanelLauncherConfiguration(), panelLauncherCode);
    }

    /**
     * Creates a panel based on the panelLauncherCode using the constructor
     * arguments supplied. If the panel cannot be instantiated, any errors are
     * written to the SOLA Log and NULL is returned.
     *
     * NOTE: The constructorArgs array boxes primitive types, so you must ensure
     * the panel class constructor is declared with boxed data types instead of
     * primitive data types (e.g. the constructor should use Boolean instead of
     * boolean).
     *
     * @param panelLauncherCode The type of panel to launch
     * @param constructorArgs The arguments to pass to the panel used to display
     * the service
     */
    private ContentPanel createPanel(String panelLauncherCode, Object... constructorArgs) {
        ContentPanel panel = null;
        ConfigPanelLauncherBean bean = getBean(panelLauncherCode);
        if (bean != null && bean.getPanelClass() != null) {
            try {
                // Use reflection to create a class from the class name
                Class<?> panelClass = Class.forName(bean.getPanelClass());
                if (constructorArgs != null && constructorArgs.length > 0) {
                    List<Class<?>> constructorClasses = new ArrayList<Class<?>>();
                    List<Object> arguments = new ArrayList<Object>();
                    for (Object arg : constructorArgs) {
                        // Determine the class of each constructor argument. Note that primitive
                        // types are boxed, so boolean becomes Boolean. If the arg is null then
                        // it will not be possible to determine the type of the arg, so exclude
                        // it from the constructorClassses list. Note that this will require
                        // the panel being launched to have overloaded constructor(s) that 
                        // omit the nullable parameters. 
                        if (arg != null) {
                            constructorClasses.add(arg.getClass());
                            arguments.add(arg);
                        }
                    }
                    // Retrieve the constructor from the class that matches the argument types.
                    // Primitive types are boxed, so you must ensure the panel class construtor
                    // is declared with boxed data types (i.e. nullable datatypes) instead of 
                    // primitive (non-nullable) data types. e.g. use Boolean instead of boolean 
                    // when declaring the readOnly parameter in the constructor arguments. 
                    Constructor<?> constructor = panelClass.getConstructor(constructorClasses.toArray(new Class<?>[constructorClasses.size()]));
                    panel = (ContentPanel) constructor.newInstance(arguments.toArray(new Object[arguments.size()]));
                } else {
                    // No constructor arguments, - use the nullary constructor
                    panel = (ContentPanel) panelClass.newInstance();
                }
            } catch (Exception ex) {
                LogUtility.log("Unable to initialize panel for code " + panelLauncherCode
                        + ". Ensure an overloaded constructor exists that excludes nullable parameters.", ex);
                panel = null;
            }
        }
        return panel;
    }

    /**
     * Launches the panel/form for the panel launcher code using a SolaTask
     * worker thread.
     *
     * NOTE: The constructorArgs array boxes primitive types, so you must ensure
     * the panel class constructor is declared with boxed data types instead of
     * primitive data types (e.g. the constructor should use Boolean instead of
     * boolean).
     *
     * @param panelLauncherCode The code identifying the panel to launch
     * @param mainPanel The main content panel to show the service panel/form in
     * @param panelListener Property Change Listener that is registered on the
     * new panel to listen for property changes such as the closing of the
     * panel. Can be null.
     * @param taskDone A runnable class that defines any actions to execute at
     * the completion of loading the new panel. Can be null if no actions should
     * occur
     * @param constructorArgs The arguments to pass to the constructor for the
     * form. Null if the nullary constructor is to be used.
     */
    public static void launch(final String panelLauncherCode,
            final MainContentPanel mainPanel,
            final PropertyChangeListener panelListener,
            final Runnable taskDone,
            final Object... constructorArgs) {
        final boolean result[] = {false};
        final ConfigPanelLauncherBean bean = getInstance().getBean(panelLauncherCode);
        if (bean != null) {
            SolaTask t = new SolaTask<Void, Void>() {
                @Override
                public Void doTask() {
                    if (bean.getMessageCode() != null) {
                        setMessage(MessageUtility.getLocalizedMessageText(bean.getMessageCode()));
                    }
                    if (mainPanel.isPanelOpened(bean.getCardName())) {
                        mainPanel.showPanel(bean.getCardName());
                        result[0] = true;
                    } else {
                        ContentPanel panel = getInstance().createPanel(panelLauncherCode, constructorArgs);
                        if (panel != null) {
                            if (panelListener != null) {
                                panel.addPropertyChangeListener(panelListener);
                            }
                            mainPanel.addPanel(panel, bean.getCardName(), true);
                            result[0] = true;
                        }
                    }
                    return null;
                }

                @Override
                protected void taskDone() {
                    if (result[0]) {
                        // Check if additional steps need to be executed 
                        if (taskDone != null) {
                            taskDone.run();
                        }
                    } else {
                        // The panel could not be created. Check the panel launcher configuration
                        MessageUtility.displayMessage(ClientMessage.ERR_UNABLE_TO_LAUNCH_PANEL,
                                new String[]{bean.getDisplayValue()});
                    }
                }
            };
            TaskManager.getInstance().runTask(t);
        } else {
            // The service could not be started. Check it is mapped by the service launcher
            MessageUtility.displayMessage(ClientMessage.ERR_MISSING_PANEL_LAUNCH_CODE,
                    new String[]{panelLauncherCode});
        }
    }

    /**
     * Determines if the Panel Launch Code is part of the specified Launch Group
     *
     * @param launchGroupCode
     * @param panelLauncherCode
     * @return true if the code is part of the group, false otherwise.
     */
    public static boolean isLaunchGroup(String launchGroupCode, String panelLauncherCode) {
        boolean result = false;
        if (panelLauncherCode != null && launchGroupCode != null) {
            result = launchGroupCode.equals(getInstance().getBean(panelLauncherCode).getLaunchGroupCode());
        }
        return result;
    }
}
