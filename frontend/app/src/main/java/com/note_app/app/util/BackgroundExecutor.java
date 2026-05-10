package com.note_app.app.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecutor {

    public interface Task<T> {
        T run() throws Exception;
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(Throwable error);
    }

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    public static <T> void run(Task<T> task, Callback<T> callback) {
        EXECUTOR.submit(() -> {
            try {
                T result = task.run();
                MAIN.post(() -> callback.onSuccess(result));
            } catch (Throwable t) {
                MAIN.post(() -> callback.onError(t));
            }
        });
    }
}
