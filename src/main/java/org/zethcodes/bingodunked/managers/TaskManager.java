package org.zethcodes.bingodunked.managers;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    public static TaskManager instance;
    private List<Integer> taskIds = new ArrayList<>();

    public TaskManager()
    {
        instance = this;
    }

    public void cancelAllTasks()
    {
        for (Integer taskId : taskIds) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        taskIds.clear();
    }

    public void addTaskId(int taskId)
    {
        taskIds.add(taskId);
    }
}
