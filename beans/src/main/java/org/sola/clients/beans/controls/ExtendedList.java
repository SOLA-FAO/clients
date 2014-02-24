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
package org.sola.clients.beans.controls;

import java.beans.*;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.jdesktop.beansbinding.PropertyResolutionException;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

/**
 * Provides observable list, together with filtered list excluding beans marked
 * for removal as well as beans with a given status.
 */
public class ExtendedList<E> extends AbstractList<E> implements ObservableList<E>, Serializable {

    private class ListElementListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            filterElement((E) evt.getSource());
        }
    }
    private final boolean supportsElementPropertyChanged;
    private List<E> list;
    private List<E> newItemsList;
    private final FilteredList<E> filteredList;
    private transient List<ObservableListListener> listeners;
    private ExtendedListFilter filter;
    private String filterExpression;
    private PropertyChangeListener elementListener;

    /**
     * Default class constructor
     */
    public ExtendedList() {
        this(new ArrayList<E>(), null);
    }

    /**
     * Class constructor.
     *
     * @param filter Filer instance.
     */
    public ExtendedList(ExtendedListFilter filter) {
        this(new ArrayList<E>(), filter);
    }

    /**
     * Class constructor.
     *
     * @param list Initial unfiltered list
     */
    public ExtendedList(List<E> list) {
        this(list, null);
    }

    /**
     * Class constructor.
     *
     * @param list Initial unfiltered list
     * @param filter Filer instance.
     */
    public ExtendedList(List<E> list, ExtendedListFilter filter) {
        if (list == null) {
            list = new ArrayList<E>();
        }
        this.list = list;
        this.newItemsList = new ArrayList<E>();
        listeners = new CopyOnWriteArrayList<ObservableListListener>();
        this.supportsElementPropertyChanged = false;
        filteredList = new FilteredList(this);
        this.filter = filter;
        initElementListener();
    }

    /**
     * Creates new instance of element property change listener.
     */
    private void initElementListener() {
        if (supportsElementPropertyChanged) {
            elementListener = new ListElementListener();
        }
    }

    /**
     * Returns filter expression, used to filter items. If filter expression is
     * set together with {@link ExtendedListFilter} instance, both expression
     * and filter will be evaluated.
     */
    public String getFilterExpression() {
        return filterExpression;
    }

    /**
     * Sets filter expression, used to filter items. OGNL language is used for
     * building expressions read more at <a
     * href="http://commons.apache.org/ognl/">
     * http://commons.apache.org/ognl/</a>.<br /> If filter expression is set
     * together with {@link ExtendedListFilter} instance, both expression and
     * filter will be evaluated.
     */
    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
        filter();
    }

    /**
     * Returns {@link ExtendedListFilter} implementation instance, used to
     * filter elements in the list.
     */
    public ExtendedListFilter getFilter() {
        return filter;
    }

    /**
     * Sets {@link ExtendedListFilter} implementation instance, used to to
     * filter elements in the list.
     */
    public void setFilter(ExtendedListFilter filter) {
        this.filter = filter;
        filter();
    }

    /**
     * Returns filtered list.
     */
    public ObservableList<E> getFilteredList() {
        return filteredList;
    }

    /**
     * Returns real index of the element in the list using {@code equals} method
     * and reference address of the object.
     *
     * @param element Element of the list.
     */
    public int getRealIndex(E element) {
        return getRealIndex(list.indexOf(element));
    }

    /**
     * Returns real index of the element in the list using {@code equals} method
     * and reference address of the object.
     *
     * @param element Index of the element in the list.
     */
    public int getRealIndex(int index) {
        return getRealIndex(index, this.list);
    }

    /**
     * Static method to returns real index of the element in the list using {@code equals}
     * method and reference address of the object.
     *
     * @param element Index of the element in the list.
     * @param list List to use for searching
     */
    public static int getRealIndex(int index, List list) {
        if (index > -1 && index < list.size()) {
            Object element = list.get(index);
            int i = 0;

            for (Object bean : list) {
                if (bean == element) {
                    index = i;
                    break;
                }
                i += 1;
            }
        }
        return index;
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    private E set(int index, E element, boolean fireFilteredListEvent) {
        E oldValue = list.set(index, element);

        removePropertyChangeListener(oldValue);
        addPropertyChangeListener(element);

        for (ObservableListListener listener : listeners) {
            listener.listElementReplaced(this, index, oldValue);
        }

        if (fireFilteredListEvent) {
            filteredList.set(filteredList.indexOf(oldValue), element, false);
        }
        return oldValue;
    }

    @Override
    public E set(int index, E element) {
        return this.set(index, element, true);
    }

    private static BeanInfo getBeanInfo(Object object) {
        assert object != null;
        try {
            return Introspector.getBeanInfo(object.getClass());
        } catch (IntrospectionException ie) {
            throw new PropertyResolutionException(
                    "Exception while introspecting " + object.getClass().getName(),
                    ie);
        }
    }

    private static EventSetDescriptor getEventSetDescriptor(Object object) {
        assert object != null;

        EventSetDescriptor[] eds = getBeanInfo(object).getEventSetDescriptors();

        for (EventSetDescriptor ed : eds) {
            if (ed.getListenerType() == PropertyChangeListener.class) {
                return ed;
            }
        }

        return null;
    }

    private static Object invokeMethod(Method method, Object object,
            Object... args) {
        Exception reason = null;

        try {
            return method.invoke(object, args);
        } catch (IllegalArgumentException ex) {
            reason = ex;
        } catch (IllegalAccessException ex) {
            reason = ex;
        } catch (InvocationTargetException ex) {
            reason = ex;
        }

        throw new PropertyResolutionException("Exception invoking method "
                + method + " on " + object, reason);
    }

    /**
     * Removes property change listener for the list element.
     */
    private void removePropertyChangeListener(E element) {
        if (element != null && elementListener != null && supportsElementPropertyChanged) {
            EventSetDescriptor ed = getEventSetDescriptor(element);
            Method removePCMethod = null;

            if ((ed == null) || ((removePCMethod = ed.getRemoveListenerMethod()) == null)) {
                return;
            }
            invokeMethod(removePCMethod, element, elementListener);
        }
    }

    /**
     * Adds property change listener for the list element to trigger filtering
     * check.
     */
    private void addPropertyChangeListener(E element) {
        if (element != null && elementListener != null && supportsElementPropertyChanged) {
            EventSetDescriptor ed = getEventSetDescriptor(element);
            Method addPCMethod = null;

            if ((ed == null) || ((addPCMethod = ed.getAddListenerMethod()) == null)) {
                return;
            }
            invokeMethod(addPCMethod, element, elementListener);
        }
    }

    private void add(int index, E element, boolean fireFilteredListEvent) {
        if (element == null) {
            return;
        }

        list.add(index, element);
        addPropertyChangeListener(element);

        modCount++;

        for (ObservableListListener listener : listeners) {
            listener.listElementsAdded(this, index, 1);
        }
        if (fireFilteredListEvent) {
            if (index >= filteredList.size()) {
                index = filteredList.size();
            }
            filteredList.add(index, element, false);
        }
    }

    @Override
    public void add(int index, E element) {
        this.add(index, element, true);
    }

    /**
     * Adds new element into the list and tracks it as newly added. This helps
     * to take a decision whether to remove element from list or not.
     *
     * @param element Object to add.
     */
    public void addAsNew(E element) {
        if (!isNewlyAdded(element)) {
            newItemsList.add(element);
            this.add(element);
        }
    }

    /**
     * Removes element from the list of newly added elements.
     */
    private void removeFromNewItemsList(E element) {
        if (isNewlyAdded(element)) {
            newItemsList.remove(getRealIndex(newItemsList.indexOf(element), newItemsList));
        }
    }

    /**
     * Checks element existence in the list of newly added elements.
     */
    public boolean isNewlyAdded(E element) {
        for (E bean : newItemsList) {
            if (bean == element) {
                return true;
            }
        }
        return false;
    }

    private E remove(int index, boolean fireFilteredListEvent) {
        E oldValue = list.remove(getRealIndex(index));
        removePropertyChangeListener(oldValue);
        removeFromNewItemsList(oldValue);
        modCount++;

        for (ObservableListListener listener : listeners) {
            listener.listElementsRemoved(this, index,
                    java.util.Collections.singletonList(oldValue));
        }

        if (fireFilteredListEvent) {
            filteredList.remove(filteredList.indexOf(oldValue), false);
        }
        return oldValue;
    }

    @Override
    public E remove(int index) {
        return this.remove(index, true);
    }

    @Override
    public boolean remove(Object o) {
        return this.remove(list.indexOf(o)) != null;
    }

    /**
     * Checks given element of the list against filtering conditions. If element
     * doesn't conform to the filer criteria, it will be removed from the
     * filtered list. If element conforms to the filter criteria and not in the
     * filtered list, it will be added.
     *
     * @param element Element object to refresh
     * @see #filter()
     * @see #filterElement(int)
     */
    public void filterElement(E element) {
        filterElement(getRealIndex(element));
    }

    /**
     * Checks given element of the list against filtering conditions. If element
     * doesn't conform to the filer criteria, it will be removed from the
     * filtered list. If element conforms to the filter criteria and not in the
     * filtered list, it will be added.
     *
     * @param index Index of the element to refresh.
     * @see #filter()
     * @see #filterElement(java.lang.Object)
     */
    public void filterElement(int index) {
        if (index > -1) {
            E element = list.get(index);
            boolean isAllowedByFilter = filteredList.isAllowedByFilter(element);
            int indexInFilteredList = filteredList.indexOf(element);

            if (isAllowedByFilter && indexInFilteredList < 0) {
                filteredList.add(filteredList.size(), element, false);
            } else if (!isAllowedByFilter && indexInFilteredList > -1) {
                filteredList.remove(indexInFilteredList, false);
            }
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (list.addAll(index, c)) {
            filteredList.addAll(filteredList.size(), c);
            modCount++;

            for (ObservableListListener listener : listeners) {
                listener.listElementsAdded(this, index, c.size());
            }
        }
        return false;
    }

    private void clear(boolean fireFilteredListEvent) {
        List<E> dup = new ArrayList<E>(list);
        initElementListener();

        list.clear();

        if (fireFilteredListEvent) {
            filteredList.clear(false);
        }

        modCount++;

        if (!dup.isEmpty()) {
            for (ObservableListListener listener : listeners) {
                listener.listElementsRemoved(this, 0, dup);
            }
        }
    }

    @Override
    public void clear() {
        this.clear(true);
    }

    /**
     * Forcibly applies filtering conditions to all elements of the list.
     *
     * @see #filterElement(java.lang.Object)
     */
    public void filter() {
        for (int i = 0; i < list.size(); i++) {
            filterElement(i);
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    private void fireElementChanged(int index) {
        for (ObservableListListener listener : listeners) {
            listener.listElementPropertyChanged(this, index);
        }
    }

    @Override
    public void addObservableListListener(ObservableListListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeObservableListListener(ObservableListListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean supportsElementPropertyChanged() {
        return supportsElementPropertyChanged;
    }

    /**
     * Observable list class with support of filtering.
     */
    private class FilteredList<E> extends AbstractList<E> implements ObservableList<E>, Serializable {

        private final boolean supportsElementPropertyChanged;
        private List<E> list;
        private ExtendedList<E> parentList;
        private transient List<ObservableListListener> listeners;

        /**
         * Class constructor to create new list.
         *
         * @param parentList Unfiltered parent list, holding all elements.
         */
        public FilteredList(ExtendedList<E> parentList) {
            this.list = new ArrayList<E>();
            this.parentList = parentList;
            listeners = new CopyOnWriteArrayList<ObservableListListener>();
            this.supportsElementPropertyChanged = false;
            // Populate from the parent list if there are any rows
            for (E item : parentList) {
                add(list.size(), item, false);
            }
        }

        @Override
        public E get(int index) {
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }

        private E set(int index, E element, boolean fireMainListEvent) {
            E oldValue = null;

            if (index > -1 && element != null && isAllowedByFilter(element)) {
                oldValue = list.set(index, element);

                for (ObservableListListener listener : listeners) {
                    listener.listElementReplaced(this, index, oldValue);
                }
            }

            if (fireMainListEvent) {
                parentList.set(parentList.indexOf(oldValue), element, false);
            }
            return oldValue;
        }

        @Override
        public E set(int index, E element) {
            return this.set(index, element, true);
        }

        private void add(int index, E element, boolean fireMainListEvent) {
            if (element != null && isAllowedByFilter(element)) {
                list.add(index, element);
                modCount++;

                for (ObservableListListener listener : listeners) {
                    listener.listElementsAdded(this, index, 1);
                }
            }

            if (fireMainListEvent) {
                if (index >= parentList.size()) {
                    index = parentList.size();
                }
                parentList.add(index, element, false);
            }
        }

        @Override
        public void add(int index, E element) {
            this.add(index, element, true);
        }

        /**
         * Removes element from the filtered list at specified position.
         *
         * @param index Index of element in the list
         * @param fireMainListEvent Boolean value indicating whether to trigger
         * removal event on the the parent list or not.
         */
        private E remove(int index, boolean fireMainListEvent) {
            E oldValue = null;

            if (index > -1) {
                oldValue = list.remove(getRealIndex(index, list));
                modCount++;

                for (ObservableListListener listener : listeners) {
                    listener.listElementsRemoved(this, index,
                            java.util.Collections.singletonList(oldValue));
                }
            }
            if (fireMainListEvent) {
                parentList.remove(parentList.indexOf(oldValue), false);
            }
            return oldValue;
        }

        @Override
        public E remove(int index) {
            return this.remove(index, true);
        }

        @Override
        public boolean remove(Object o) {
            return this.remove(list.indexOf(o)) != null;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return addAll(size(), c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            for (E item : c) {
                this.add(item);
            }
            return false;
        }

        /**
         * Removes all elements from the list.
         */
        private void clear(boolean fireMainListEvent) {
            List<E> dup = new ArrayList<E>(list);
            list.clear();
            if (fireMainListEvent) {
                parentList.clear(false);
            }

            modCount++;

            if (!dup.isEmpty()) {
                for (ObservableListListener listener : listeners) {
                    listener.listElementsRemoved(this, 0, dup);
                }
            }
        }

        @Override
        public void clear() {
            this.clear(true);
        }

        /**
         * Checks filter criteria against the given element. Returns true if
         * element conforms to the criteria.
         */
        private boolean isAllowedByFilter(E element) {
            boolean result = true;
            // Evaluate against filter instatce
            if (filter != null) {
                result = filter.isAllowedByFilter(element);
            }
            // Evaluate against fileter expression
            if (filterExpression != null && filterExpression.length() > 0) {
                try {
                    Object expr = Ognl.parseExpression(filterExpression);
                    OgnlContext ctx = new OgnlContext();
                    result = (Boolean) Ognl.getValue(expr, ctx, element);
                } catch (OgnlException ex) {
                    throw new RuntimeException(String.format("Exception occured, while "
                            + "evaluating filter expression - %s", ex.getMessage()));
                }

            }
            return result;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return list.containsAll(c);
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return list.toArray(a);
        }

        @Override
        public Object[] toArray() {
            return list.toArray();
        }

        private void fireElementChanged(int index) {
            for (ObservableListListener listener : listeners) {
                listener.listElementPropertyChanged(this, index);
            }
        }

        @Override
        public void addObservableListListener(ObservableListListener listener) {
            listeners.add(listener);
        }

        @Override
        public void removeObservableListListener(
                ObservableListListener listener) {
            listeners.remove(listener);
        }

        @Override
        public boolean supportsElementPropertyChanged() {
            return supportsElementPropertyChanged;
        }
    }
}
