package com.fiskmods.heroes.common;

import java.util.HashMap;
import java.util.Map;

import com.fiskmods.heroes.FiskHeroes;

public class TaskTimer
{
    private static Map<String, TaskTimer> timers = new HashMap<>();

    private long startTime;

    private TaskTimer(String s)
    {
    }

    public static void start(String task)
    {
        get(task).startTime = System.currentTimeMillis();
    }

    public static void stop(String task)
    {
        FiskHeroes.LOGGER.info("Finished task {} in {} ms", task, System.currentTimeMillis() - get(task).startTime);
    }

    private static TaskTimer get(String task)
    {
        TaskTimer timer = timers.get(task);

        if (timer == null)
        {
            timers.put(task, timer = new TaskTimer(task));
        }

        return timer;
    }
}
