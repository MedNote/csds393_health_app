package com.mednote.cwru.util.helpers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceHelper {

    private final ExecutorService executorService;
    private static ExecutorServiceHelper instance;

    private ExecutorServiceHelper() {
        executorService = Executors.newFixedThreadPool(5);
    }

    public static ExecutorServiceHelper getInstance() {
        if (instance == null) {
            instance = new ExecutorServiceHelper();
        }
        return instance;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void execute(Runnable runnable) {
        executorService.execute(runnable);
    }
}
