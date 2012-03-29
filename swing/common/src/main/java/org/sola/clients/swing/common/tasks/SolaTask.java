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
package org.sola.clients.swing.common.tasks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

/**
 * Used to create background tasks, wrapped with exception handling.
 */
public abstract class SolaTask<T, V> {

    private SwingWorker<T, V> task = createTask();
    protected final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    public static final String EXCEPTION_RISED = "solaTaskExceptionRised";
    public static final String EVENT_PROGRESS = "progress";
    public static final String EVENT_MESSAGE = "message";
    public static final String EVENT_STATE = "state";
    public static final String TASK_STARTED = "STARTED";
    public static final String TASK_DONE = "DONE";
    public static final String TASK_PENDING = "PENDING";
    public static final String REMOVE_TASK = "removeTask";
    private int progress;
    private String message;
    private String id = UUID.randomUUID().toString();

    /** Code logic to be executed. */
    protected abstract T doTask();

    /** 
     * Code logic to be executed upon task completion. 
     * This method will be called only if task completed successfully. 
     */
    protected void taskDone() {
    }

    /** 
     * Code logic to be executed in case of exception. 
     * This method is called upon any unhandled exception rise.
     * @param e The exception, thrown from {@link #taskDone()} method.
     */
    protected void taskFailed(Throwable e) {
    }
    
    /** 
     * Executes task. 
     * @see SwingWorker
     */
    public final void execute() {
        try {
            task.execute();
        } catch (Throwable e) {
            propertySupport.firePropertyChange(EXCEPTION_RISED, null, e);
        }
    }

    /** See {@link SwingWorker#isDone()}. */
    public final boolean isDone() {
        return task.isDone();
    }

    /** See {@link SwingWorker#isCancelled()}. */
    public final boolean isCancelled() {
        return task.isCancelled();
    }

    /** See {@link SwingWorker#cancel(boolean)}. */
    public final void cancel(boolean mayInterruptIfRunning) {
        task.cancel(mayInterruptIfRunning);
    }

    /** See {@link SwingWorker#get()}. */
    public final T get() {
        try {
            return task.get();
        } catch (Throwable e) {
            propertySupport.firePropertyChange(EXCEPTION_RISED, null, e);
            return null;
        }
    }

    /** See {@link SwingWorker#getProgress()}. */
    public final int getProgress() {
        return progress;
    }

    /** Sets progress value to be shown on the progress bar. */
    protected final void setProgress(int progress) {
        int oldValue = this.progress;
        this.progress = progress;
        propertySupport.firePropertyChange(EVENT_PROGRESS, oldValue, this.progress);
    }

    /** Returns task ID. */
    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    /** Sets message, describing current process activity. */
    public void setMessage(String message) {
        String oldValue = this.message;
        this.message = message;
        propertySupport.firePropertyChange(EVENT_MESSAGE, oldValue, this.message);
    }

    /** See {@link SwingWorker#getState()}. */
    public final StateValue getState() {
        return task.getState();
    }

    /** Registers property change listener. */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /** Removes property change listener. */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /** Creates actual swing task. */
    private SwingWorker<T, V> createTask() {
        SwingWorker<T, V> swingTask;

        swingTask = new SwingWorker() {

            Throwable exception;
            
            @Override
            protected T doInBackground() throws Exception {
                try {
                    exception = null;
                    return doTask();
                } catch (Throwable e) {
                    exception = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    if(exception != null){
                        propertySupport.firePropertyChange(EXCEPTION_RISED, null, exception);
                        taskFailed(exception);
                        return;
                    }
                    propertySupport.firePropertyChange(REMOVE_TASK, false, true);
                    taskDone();
                } catch (Throwable e) {
                    propertySupport.firePropertyChange(EXCEPTION_RISED, null, e);
                }
            }
        };
        swingTask.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                propertySupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            }
        });
        return swingTask;
    }
}