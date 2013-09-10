/*
 * Copyright 2013 Food and Agriculture Organization of the United Nations (FAO).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sola.clients.beans.referencedata;

import org.sola.clients.beans.AbstractCodeBean;

/**
 * Represents reference data object of the <b>condition_type</b> table.
 * Could be populated from the {@link ConditionTypeTO} object.<br /> For more
 * information see data dictionary <b>Administrative</b> schema.
 */
public class ConditionTypeBean extends AbstractCodeBean {
    public ConditionTypeBean(){
        super();
    }
}
