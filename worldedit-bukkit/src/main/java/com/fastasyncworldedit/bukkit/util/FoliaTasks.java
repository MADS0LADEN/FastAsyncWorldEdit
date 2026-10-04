package com.fastasyncworldedit.bukkit.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Integer task ids for Folia {@link ScheduledTask}s, matching {@code BukkitScheduler}'s int ids.
 */
public final class FoliaTasks {

    private static final AtomicInteger IDS = new AtomicInteger();
    private static final ConcurrentHashMap<Integer, ScheduledTask> TASKS = new ConcurrentHashMap<>();

    private FoliaTasks() {
    }

    public static int track(ScheduledTask task) {
        int id = IDS.incrementAndGet();
        TASKS.put(id, task);
        return id;
    }

    public static void cancel(int id) {
        ScheduledTask task = TASKS.remove(id);
        if (task != null) {
            task.cancel();
        }
    }
}
