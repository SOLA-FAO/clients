/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
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
package org.sola.clients.beans.converters;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.dozer.DozerEventListener;
import org.dozer.event.DozerEvent;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.common.MappingManager;
import org.sola.webservices.transferobjects.AbstractTO;

/**
 * Hooks into the Dozer translation process to populate lists through the
 * getter.
 */
public class GenericTranslatorListener implements DozerEventListener {

    /**
     * This listener method is triggered once before the mapping process begins.
     *
     * @param event
     */
    @Override
    public void mappingStarted(DozerEvent event) {
    }

    /**
     * This listener method is triggered before each field in the target object
     * has been assigned its destination value.
     *
     * @param event
     */
    @Override
    public void preWritingDestinationValue(DozerEvent event) {
    }

    /**
     * This listener method is triggered after each field in the target object
     * has been assigned its destination value.
     *
     * @param event
     */
    @Override
    public void postWritingDestinationValue(DozerEvent event) {
        Object array = event.getFieldMap().getSrcFieldValue(event.getSourceObject());
        if (array != null && array instanceof byte[]) {
            try {
                String name = event.getFieldMap().getDestFieldName();
                name = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method method = event.getDestinationObject().getClass().getMethod(name, byte[].class);
                method.invoke(event.getDestinationObject(), (byte[]) array);
            } catch (Exception e) {
                System.out.println("Error occured while assigning byte array value. " + e.getMessage());
            }
        }
    }

    /**
     * This listener method is triggered once at the completion of the mapping
     * process. It checks final object to have List getters and populates list
     * through the getter if relevant setter is missing. This logic is applied
     * only for objects, inherited either from {@link AbstractTO} or {@link AbstractBindingBean}.
     *
     * @param event
     */
    @Override
    public void mappingFinished(DozerEvent event) {

        // Check for AbstractTO or AbstractBindingBean object type
        if (event.getDestinationObject() != null && event.getSourceObject() != null
                && AbstractTO.class.isAssignableFrom(event.getDestinationObject().getClass())
                || AbstractBindingBean.class.isAssignableFrom(event.getDestinationObject().getClass())) {

            Method[] methods = event.getDestinationObject().getClass().getMethods();

            // Loop through the getter methods of destination object
            for (Method destinationGetter : methods) {

                if (destinationGetter.getName().substring(0, 3).equalsIgnoreCase("get") && 
                        Iterable.class.isAssignableFrom(destinationGetter.getReturnType())) {

                    try {
                        Method sourceGetter;
                        // Get relevant source object getter method
                        try {
                            sourceGetter = event.getSourceObject().getClass().getMethod(destinationGetter.getName());
                        } catch (NoSuchMethodException e) {
                            continue;
                        }

                        // Check for destination setter method to exist
                        try {
                            if (event.getDestinationObject().getClass().getMethod("set" + destinationGetter.getName().substring(3)) != null) {
                                // Skip this method if there is setter.
                                continue;
                            }
                        } catch (NoSuchMethodException e) {
                        }

                        // Get destination list inner type
                        ParameterizedType destinationListType =
                                (ParameterizedType) destinationGetter.getGenericReturnType();
                        Class<?> destinationChildClass =
                                ((Class<?>) destinationListType.getActualTypeArguments()[0]);

                        // Get source and destination lists
                        List sourceList = (List) sourceGetter.invoke(event.getSourceObject());
                        List destinationList = (List) destinationGetter.invoke(event.getDestinationObject());

                        if (destinationList == null || sourceList == null) {
                            continue;
                        }

                        destinationList.clear();

                        // Loop through the source list and add items into destination list
                        if (sourceList != null && destinationList != null) {
                            for (Object o : sourceList) {
                                // Call object mapping before adding into the list
                                MappingManager.setEventListener(new GenericTranslatorListener());
                                destinationList.add(MappingManager.getMapper().map(o, destinationChildClass));
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Unable to translate object " + event.getSourceObject().toString(), e);
                    }
                }
            }
        }
    }
}
