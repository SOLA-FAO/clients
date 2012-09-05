/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
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
