/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.tool.extended.ui;

import java.awt.Component;
import java.awt.Dialog;
import javax.swing.JDialog;

/**
 * Singletone class that provides utilities related with user interface.
 * @author Elton Manoku
 */
public class UiUtil {

    private static UiUtil singleInstance = new UiUtil();

    /**
     * Gets the singletone instance
     */
    public static UiUtil getInstance() {
        return singleInstance;
    }

    /**
     * Gets a modalform showing the component passed as parameter. Also a title can be provided.
     */
    public JDialog getDialog(String title, Component componentToShow) {
        JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setPreferredSize(componentToShow.getPreferredSize());
        dialog.getContentPane().add(componentToShow);
        dialog.setTitle(title);
        dialog.pack();
        return dialog;
    }
}
