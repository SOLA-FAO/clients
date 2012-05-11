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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.sola.clients.swing.gis.data.PojoDataAccess;
import org.sola.common.MappingManager;
import org.sola.webservices.transferobjects.search.SpatialSearchOptionTO;

/**
 * Bean class used to represent the spatial search option configuration details to the client
 *
 * @author soladev
 */
public class SpatialSearchOptionBean {

    private static List<SpatialSearchOptionBean> instanceList = null;
    private String queryName;
    private String title;
    private String code;
    private String description;
    private int minSearchStrLen;
    private BigDecimal zoomInBuffer;

    public SpatialSearchOptionBean() {
    }

    /**
     * Retrieves the options available for the spatial search from the database. Each option is
     * associated with a dynamic query that contains the SQL to execute for the search.
     *
     * @return
     */
    public static List<SpatialSearchOptionBean> getInstanceList() {
        if (instanceList == null) {
            instanceList = new ArrayList<SpatialSearchOptionBean>();
            List<SpatialSearchOptionTO> searchOptions =
                    PojoDataAccess.getInstance().getSearchService().getSpatialSearchOptions();
            if (searchOptions != null) {
                for (SpatialSearchOptionTO searchOption : searchOptions) {
                    instanceList.add(MappingManager.getMapper().map(searchOption,
                            SpatialSearchOptionBean.class));
                }
            }
        }
        return instanceList;
    }

    /**
     * The title to use for display of the spatial Search option
     */
    public String getTitle() {
        return title;
    }

    /**
     * The title to use for display of the spatial Search option
     */
    public void setTitle(String label) {
        this.title = label;
    }

    /**
     * The name of the dynamic query to execute for this spatial search option.
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * The name of the dynamic query to execute for this spatial search option.
     */
    public void setQueryName(String value) {
        this.queryName = value;
    }

    /**
     * The unique code value for this spatial search option.
     */
    public String getCode() {
        return code;
    }

    /**
     * The unique code value for this spatial search option.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * The description for this spatial search option.
     */
    public String getDescription() {
        return description;
    }

    /**
     * The unique code value for this spatial search option.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The minimum number of characters required before the search should be executed. Usually 3,
     * but can be less if the target object has short labels.
     */
    public int getMinSearchStrLen() {
        return minSearchStrLen;
    }

    /**
     * The minimum number of characters required before the search should be executed. Usually 3,
     * but can be less if the target object has short labels.
     */
    public void setMinSearchStrLen(int minSearchStrLen) {
        this.minSearchStrLen = minSearchStrLen;
    }

    /**
     * The distance to use as a buffer when zooming into the selected object. This unit for this
     * value is dependent on the coordinate system of the map, but usually it will be meters.
     */
    public BigDecimal getZoomInBuffer() {
        return zoomInBuffer;
    }

    /**
     * The distance to use as a buffer when zooming into the selected object. This unit for this
     * value is dependent on the coordinate system of the map, but usually it will be meters.
     */
    public void setZoomInBuffer(BigDecimal zoomInBuffer) {
        this.zoomInBuffer = zoomInBuffer;
    }

    /**
     * Override the toString method to ensure the title property is displayed for the Bean when
     * linked to a list or other control.
     */
    @Override
    public String toString() {
        return this.title;
    }
}
