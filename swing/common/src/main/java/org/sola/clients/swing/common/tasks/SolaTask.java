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
        } catch (Exception e) {
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
        } catch (Exception e) {
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

            Exception exception;
            
            @Override
            protected T doInBackground() throws Exception {
                try {
                    exception = null;
                    return doTask();
                } catch (Exception e) {
                    exception = e;
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    propertySupport.firePropertyChange(REMOVE_TASK, false, true);
                    if(exception != null){
                        try {
                            propertySupport.firePropertyChange(EXCEPTION_RISED, null, exception);
                        } catch (Exception e) {
                        }
                        taskFailed(exception);
                        return;
                    }
                    taskDone();
                } catch (Exception e) {
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