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
package org.sola.clients.swing.common.tasks;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.TaskService;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 * This singleton class provides methods to add new tasks to the 
 * {@link TaskMonitor}. It holds <code>TaskMonitor</code> instance and allows to
 * check for the running tasks.
 */
public class TaskManager {

    private ApplicationContext appContext;
    private TaskMonitor taskMonitor;
    private TaskService taskService;

    /** 
     * Creates class instance and initializes {@link ApplicationContext}, 
     * {@link TaskMonitor} and {@link TaskService} variables.
     */
    private TaskManager() {
        appContext = Application.getInstance().getContext();
        taskMonitor = appContext.getTaskMonitor();
        taskService = appContext.getTaskService();
    }

    /** Returns the instance of the class. */
    public static TaskManager getInstance() {
        return TaskManagerHolder.INSTANCE;
    }

    /** Class instance holder */
    private static class TaskManagerHolder {

        private static final TaskManager INSTANCE = new TaskManager();
    }

    /** 
     * Runs new task.
     * @param task New task to run.
     */
    public boolean runTask(Task task) {
        if (task == null) {
            return false;
        }
        int tasksNum = getActiveTasks();

        if (tasksNum > 0) {
            MessageUtility.displayMessage(ClientMessage.GENERAL_ACTIVE_TASKS_EXIST,
                    new Object[]{tasksNum});
            return false;
        }
        
        task.addTaskListener(new DefaultTaskListener());

        taskService.execute(task);
        taskMonitor.setForegroundTask(task);

        return true;
    }

    /** Returns number of active tasks.*/
    public int getActiveTasks() {
        return taskMonitor.getTasks().size();
    }
}
