package org.safebabe.mohistpro;

import com.google.common.collect.ImmutableSet;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncScheduler {

    public static ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public static void addTask(Runnable runnable) {
        tasks.add(runnable);
    }

    public static ImmutableSet<Runnable> fetchTasks() {
        ImmutableSet<Runnable> i = ImmutableSet.copyOf(tasks);
        tasks.clear();
        return i;
    }

    public static void runAllTasks() {
        fetchTasks().forEach(Runnable::run);
    }
}
