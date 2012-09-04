/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotools.swing.mapaction.extended.ui;

import java.util.List;
import java.util.Map;
import org.geotools.swing.mapaction.extended.print.PrintLayout;

/**
 * This interface is used by the implementers of the Print Form. Print form is the form that is
 * called when the print map action is clicked.
 *
 * @author Elton Manoku
 */
public interface IPrintUi {

    /**
     * Gets the map scale that will be used for the print
     *
     * @return
     */
    Integer getScale();

    /**
     * Sets the scale that will be used for the print
     *
     * @param scale
     */
    void setScale(Integer scale);

    /**
     * Gets the selected print layout
     *
     * @return
     */
    PrintLayout getPrintLayout();

    /**
     * Sets the list of the available print layouts
     *
     * @param printLayoutList
     */
    void setPrintLayoutList(List<PrintLayout> printLayoutList);

    /**
     * Sets the visibility of the component
     *
     * @param visible
     */
    void setVisibility(boolean visible);

    /**
     * Gets extra fields that can be used in the final print
     *
     * @return
     */
    Map<String, Object> getExtraFields();
}
