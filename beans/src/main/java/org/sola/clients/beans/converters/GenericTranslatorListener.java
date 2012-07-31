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
package org.sola.clients.beans.converters;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.dozer.DozerEventListener;
import org.dozer.event.DozerEvent;
import org.sola.clients.beans.AbstractBindingBean;
import org.sola.common.SOLAException;
import org.sola.common.mapping.MappingManager;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.ServiceMessage;
import org.sola.webservices.transferobjects.AbstractTO;

/**
 * Hooks into the Dozer translation process to populate lists through the getter.
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
     * This listener method is triggered before each field in the target object has been assigned
     * its destination value.
     *
     * @param event
     */
    @Override
    public void preWritingDestinationValue(DozerEvent event) {
    }

    /**
     * This listener method is triggered after each field in the target object has been assigned its
     * destination value.
     *
     * @param event
     */
    @Override
    public void postWritingDestinationValue(DozerEvent event) {
    }

    /**
     * This listener method is triggered once at the completion of the mapping process. It checks
     * final object to have List getters and populates list through the getter if relevant setter is
     * missing. This logic is applied only for objects, inherited either from {@link AbstractTO} or {@link AbstractBindingBean}.
     *
     * @param event
     */
    @Override
    public void mappingFinished(DozerEvent event) {
        mapGetterLists(event.getSourceObject(), event.getDestinationObject());
    }

    /**
     * Uses reflection to check for any list methods that are exposed with getters only and ensures
     * they are translated by the Dozer Bean Mapper.
     *
     * @param sourceObject The source object for the mapping
     * @param destinationObject The destination object for the mapping
     * @throws RuntimeException
     * @throws SecurityException
     */
    private void mapGetterLists(Object sourceObject, Object destinationObject) throws RuntimeException, SecurityException {

        if (destinationObject != null && sourceObject != null
                && (AbstractTO.class.isAssignableFrom(destinationObject.getClass())
                || AbstractBindingBean.class.isAssignableFrom(destinationObject.getClass()))) {

            // Only map the getterLists if the source and destination are of different class types. 
            // This check is required because the client uses the mapper to duplciate beans. Some beans
            // have methods that instanitate a version of themselves which can cause nasty never
            // ending loops.
            if (AbstractBindingBean.class.isAssignableFrom(destinationObject.getClass())
                    && AbstractBindingBean.class.isAssignableFrom(sourceObject.getClass())) {
                return;
            }
            if (AbstractTO.class.isAssignableFrom(destinationObject.getClass())
                    && AbstractTO.class.isAssignableFrom(sourceObject.getClass())) {
                return;
            }
            Method[] methods = destinationObject.getClass().getMethods();

            // Loop through the getter methods of destination object
            for (Method destinationGetter : methods) {

                if (destinationGetter.getName().substring(0, 3).equalsIgnoreCase("get")) {
                    Class<?> returnType = destinationGetter.getReturnType();

                    if (AbstractTO.class.isAssignableFrom(returnType)
                            || AbstractBindingBean.class.isAssignableFrom(returnType)) {
                        // Lighthouse #118 - Check  child objects for any nested lists
                        Object childSourceObject = null;
                        Object childDestinationObject = null;
                        Method sourceGetter = null;
                        try {
                            sourceGetter = sourceObject.getClass().getMethod(destinationGetter.getName());
                        } catch (NoSuchMethodException e) {
                            continue;
                        }
                        try {
                            childSourceObject = sourceGetter.invoke(sourceObject);
                            childDestinationObject = destinationGetter.invoke(destinationObject);
                        } catch (Exception e) {
                            throw new SOLAException(ClientMessage.GENERAL_UNEXPECTED,
                                    new Object[]{"Translation error", sourceObject.toString(),
                                        sourceGetter.getName(), e.toString()});
                        }
                        mapGetterLists(childSourceObject, childDestinationObject);

                    } else if (Iterable.class.isAssignableFrom(returnType)) {
                        Method sourceGetter = null;

                        // Get relevant source object getter method
                        try {
                            sourceGetter = sourceObject.getClass().getMethod(destinationGetter.getName());
                        } catch (NoSuchMethodException e) {
                            continue;
                        }

                        // Check for destination setter method to exist
                        try {
                            if (destinationObject.getClass().getMethod("set" + destinationGetter.getName().substring(3)) != null) {
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

                        List sourceList = null;
                        List destinationList = null;
                        try {
                            // Get source and destination lists
                            sourceList = (List) sourceGetter.invoke(sourceObject);
                            destinationList = (List) destinationGetter.invoke(destinationObject);
                        } catch (Exception e) {
                            throw new SOLAException(ClientMessage.GENERAL_UNEXPECTED,
                                    new Object[]{"Translation error", sourceObject.toString(),
                                        sourceGetter.getName(), e.toString()});
                        }

                        if (destinationList == null || sourceList == null) {
                            continue;
                        }

                        destinationList.clear();

                        // Loop through the source list and add items into destination list
                        if (sourceList != null && destinationList != null) {
                            for (Object o : sourceList) {
                                // Call object mapping before adding into the list
                                destinationList.add(MappingManager.getMapper(
                                        new GenericTranslatorListener()).map(o, destinationChildClass));
                            }
                        }

                    }
                }
            }
        }
    }
}
