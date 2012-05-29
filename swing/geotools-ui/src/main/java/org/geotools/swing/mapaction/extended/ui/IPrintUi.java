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
   Integer getScale();
   void setScale(Integer scale);
   PrintLayout getPrintLayout();
   void setPrintLayoutList(List<PrintLayout> printLayoutList);
   void setVisibility(boolean visible);
   Map<String, Object> getExtraFields();
}
