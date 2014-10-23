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
package org.sola.clients.swing.gis.data;

import java.util.HashMap;
import org.geotools.feature.SchemaException;
import org.sola.clients.swing.gis.layer.PojoForPublicDisplayLayer;
import org.sola.common.StringUtility;
import org.sola.webservices.spatial.ResultForNavigationInfo;

/**
 * The data source handles feature requests for layers of type public display.
 * These layers need to set the name last part as an extra parameter to the
 * query in the server.
 *
 * @author Elton Manoku
 */
public class PojoPublicDisplayFeatureSource extends PojoFeatureSource {

    public static final String PARAM_FIRST_PART = "nameLastpart";
    public static final String PARAM_APPLICATION_ID = "appId";
    private java.util.Map<String, String> filterParams = new HashMap<String, String>();

    public PojoPublicDisplayFeatureSource(
            PojoDataAccess dataSource, PojoForPublicDisplayLayer layer) throws SchemaException {
        super(dataSource, layer);
    }

    @Override
    protected ResultForNavigationInfo getResultForNavigation(
            double west, double south, double east, double north) {
        return this.getDataSource().GetQueryDataForPublicDisplay(
                this.getSchema().getTypeName(), west, south, east, north,
                this.getLayer().getSrid(), this.getLayer().getMapControl().getPixelResolution(),
                filterParams);
    }

    public String getNameLastPart() {
        return filterParams.get(PARAM_FIRST_PART);
    }

    public void setNameLastPart(String nameLastPart) {
        if (StringUtility.isEmpty(nameLastPart)) {
            filterParams.remove(PARAM_FIRST_PART);
        } else {
            filterParams.put(PARAM_FIRST_PART, nameLastPart);
        }
    }

    public String getApplicationId() {
        return filterParams.get(PARAM_APPLICATION_ID);
    }

    public void setApplicationId(String appId) {
        if (StringUtility.isEmpty(appId)) {
            filterParams.remove(PARAM_APPLICATION_ID);
        } else {
            filterParams.put(PARAM_APPLICATION_ID, appId);
        }
    }
}
