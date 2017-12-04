package com.millet.androidlib.Net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池使用单利模式
 * Created by Administrator on 2017/1/12.
 */

public class ExecutorManager {

    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(5);

    private ExecutorManager() {
    }

    public static void execute(Runnable _runnable) {
        if (null == _runnable || null == mExecutorService) return;
        mExecutorService.execute(_runnable);
    }

}
