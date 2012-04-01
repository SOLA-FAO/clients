/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;

/**
 * Implementation of {@link ObservableList} with support of <code>Serialization</code>.
 */
public class SolaObservableList<E> extends AbstractList<E>
        implements ObservableList<E>, Serializable {

    private final boolean supportsElementPropertyChanged = false;
    private List<E> list;
    private transient List<ObservableListListener> listeners;

    public SolaObservableList() {
        this(new ArrayList<E>());
    }
    
    public SolaObservableList(List<E> list) {
        this.list = list;
        listeners = new CopyOnWriteArrayList<ObservableListListener>();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public E set(int index, E element) {
        E oldValue = list.set(index, element);

        for (ObservableListListener listener : listeners) {
            listener.listElementReplaced(this, index, oldValue);
        }

        return oldValue;
    }

    @Override
    public void add(int index, E element) {
        list.add(index, element);
        modCount++;

        for (ObservableListListener listener : listeners) {
            listener.listElementsAdded(this, index, 1);
        }
    }

    @Override
    public E remove(int index) {
        E oldValue = list.remove(index);
        modCount++;

        for (ObservableListListener listener : listeners) {
            listener.listElementsRemoved(this, index,
                    java.util.Collections.singletonList(oldValue));
        }

        return oldValue;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size(), c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (list.addAll(index, c)) {
            modCount++;

            for (ObservableListListener listener : listeners) {
                listener.listElementsAdded(this, index, c.size());
            }
        }

        return false;
    }

    @Override
    public void clear() {
        List<E> dup = new ArrayList<E>(list);
        list.clear();
        modCount++;

        if (!dup.isEmpty()) {
            for (ObservableListListener listener : listeners) {
                listener.listElementsRemoved(this, 0, dup);
            }
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
    public void removeObservableListListener(
            ObservableListListener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean supportsElementPropertyChanged() {
        return supportsElementPropertyChanged;
    }
}
