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
package org.sola.clients.beans.validation;

import java.lang.reflect.Method;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.sola.clients.beans.AbstractCodeBean;
import org.sola.clients.beans.AbstractIdBean;
import org.sola.clients.beans.AbstractVersionedBean;

/**
 * Validates the list for duplicated objects
 */
public class ListDuplicationValidator implements ConstraintValidator<NoDuplicates, List> {

    private String methodName;

    @Override
    public void initialize(NoDuplicates constraint) {
        methodName = constraint.methodName();
    }

    @Override
    public boolean isValid(List value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        int i = 0;
        int i1 = 0;

        for (Object object : value) {
            Object objectValue = null;
            if (methodName != null && methodName.length() > 0) {
                try {
                    Method method = object.getClass().getMethod(methodName);
                    objectValue = method.invoke(object);
                } catch (Exception ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            } else {
                if (AbstractIdBean.class.isAssignableFrom(object.getClass())) {
                    // Search by AbstractIdBean
                    objectValue = ((AbstractIdBean) object).getId();
                } else if (AbstractCodeBean.class.isAssignableFrom(object.getClass())) {
                    // Search by AbstractCodeBean
                    objectValue = ((AbstractCodeBean) object).getCode();
                } else if (AbstractVersionedBean.class.isAssignableFrom(object.getClass())) {
                    // Search by AbstractVersionedBean
                    objectValue = ((AbstractVersionedBean) object).getRowId();
                }
            }
            
            i1=0;
            for (Object object1 : value) {
                Object objectValue1 = null;
                if (methodName != null && methodName.length() > 0) {
                    try {
                        Method method = object1.getClass().getMethod(methodName);
                        objectValue1 = method.invoke(object1);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex.getMessage());
                    }
                } else {
                    if (AbstractIdBean.class.isAssignableFrom(object1.getClass())) {
                        // Search by AbstractIdBean
                        objectValue1 = ((AbstractIdBean) object1).getId();
                    } else if (AbstractCodeBean.class.isAssignableFrom(object1.getClass())) {
                        // Search by AbstractCodeBean
                        objectValue1 = ((AbstractCodeBean) object1).getCode();
                    } else if (AbstractVersionedBean.class.isAssignableFrom(object1.getClass())) {
                        // Search by AbstractVersionedBean
                        objectValue1 = ((AbstractVersionedBean) object1).getRowId();
                    }
                }

                if (objectValue1 != null && objectValue != null && i != i1) {
                    if (objectValue.equals(objectValue1)) {
                        return false;
                    }
                }
                i1 += 1;
            }
            i += 1;
        }
        return true;
    }
}