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
package org.sola.clients.swing.common.tasks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import javax.swing.SwingWorker.StateValue;
import org.sola.clients.swing.common.DefaultExceptionHandler;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This singleton class provides methods to run new tasks and monitor them to
 * indicate long running processes.
 */
public class TaskManager {

    private HashMap<String, SolaTask> tasks;
    protected final PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

    /**
     * Class constructor.
     */
    private TaskManager() {
        tasks = new HashMap<String, SolaTask>();
    }

    /**
     * Returns the instance of the class.
     */
    public static TaskManager getInstance() {
        return TaskManagerHolder.INSTANCE;
    }

    /**
     * Class instance holder
     */
    private static class TaskManagerHolder {

        private static final TaskManager INSTANCE = new TaskManager();
    }

    /**
     * Runs new task.
     *
     * @param task Task to run.
     */
    public boolean runTask(SolaTask task) {
        if (task == null || isTaskRunning(task.getId())) {
            return false;
        }

        if (getNumberOfActiveTasks() > 0) {
            MessageUtility.displayMessage(ClientMessage.GENERAL_ACTIVE_TASKS_EXIST,
                    new Object[]{getNumberOfActiveTasks()});
            return false;
        }

        task.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                handleTaskEvents(evt);
            }
        });
        tasks.put(task.getId(), task);
        task.execute();
        return true;
    }

    private void handleTaskEvents(PropertyChangeEvent evt) {
        String taskId = null;
        if (evt.getSource() instanceof SolaTask) {
            taskId = ((SolaTask) evt.getSource()).getId();
        }

        if (evt.getPropertyName().equals(SolaTask.EVENT_STATE)) {
            StateValue state = (StateValue) evt.getNewValue();
            if (state.name().equalsIgnoreCase(SolaTask.TASK_DONE)) {
                tasks.remove(taskId);
            }
        }

        if (evt.getPropertyName().equals(SolaTask.REMOVE_TASK)) {
            tasks.remove(taskId);
        }

        if (evt.getPropertyName().equals(SolaTask.EXCEPTION_RISED)) {
            if (Throwable.class.isAssignableFrom(evt.getNewValue().getClass())) {
                DefaultExceptionHandler.handleException((Throwable) evt.getNewValue());
            }
            if (taskId != null) {
                tasks.remove(taskId);
            }
        }

        propertySupport.firePropertyChange(evt);
    }

    /**
     * Returns number of active tasks.
     */
    public int getNumberOfActiveTasks() {
        return tasks.size();
    }

    /**
     * Returns true if task is already running.
     *
     * @param taskId The ID of the task to check.
     */
    public boolean isTaskRunning(String taskId) {
        return tasks.containsKey(taskId);
    }

    /**
     * Registers property change listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes property change listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
}
