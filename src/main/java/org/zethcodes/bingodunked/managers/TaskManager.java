package org.zethcodes.bingodunked.managers;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private final List<Integer> taskIds = new ArrayList<>();

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
