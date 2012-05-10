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
package org.sola.clients.swing.gis.beans;

import java.util.ArrayList;
import java.util.List;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.webservices.transferobjects.search.SpatialSearchOptionTO;

/**
 * It is a data bean which defines a search option for the map control. It contains a static method
 * which generates a list of the choices read from the database.
 *
 * @author Elton Manoku
 */
public class SpatialSearchOptionBean {

    private static List<SpatialSearchOptionBean> instanceList = null;
    private String queryName;
    private String title;

    public SpatialSearchOptionBean(String queryName, String title) {
        this.queryName = queryName;
        this.title = title;
    }

    /**
     * Retrieves the options available for the spatial search from the database. Each option
     * is associated with a dynamic query that contains the SQL to execute for the search. 
     * @return
     */
    public static List<SpatialSearchOptionBean> getInstanceList() {
        if (instanceList == null) {
            instanceList = new ArrayList<SpatialSearchOptionBean>();
            List<SpatialSearchOptionTO> searchOptions =
                    PojoDataAccess.getInstance().getSearchService().getSpatialSearchOptions();
            if (searchOptions != null) {
                for (SpatialSearchOptionTO searchOption : searchOptions) {
                    instanceList.add(new SpatialSearchOptionBean(
                            searchOption.getQueryName(), searchOption.getTitle()));
                }
            }
        }
        return instanceList;
    }

    /**
     * Gets the label which is used for the presentation of the choice for the user
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the label
     */
    public void setTitle(String label) {
        this.title = label;
    }

    /**
     * Gets the value. It is the identifier of the choice
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Sets the value
     */
    public void setQueryName(String value) {
        this.queryName = value;
    }

    /**
     * Gets the string presentation of the choice
     */
    @Override
    public String toString() {
        return this.title;
    }
}
