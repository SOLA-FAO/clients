/*
 * Copyright 2012 Food and Agriculture Organization of the United Nations (FAO).
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
