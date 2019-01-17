package com.social.preserve.threadpool;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Created by pt198 on 18/09/2018.
 */

public class FixedThreadPool {
    private ExecutorService mExecutor;
    private static final int MAX_THREAD=4;
    private static class SingletonInstance{
        private static final FixedThreadPool INSTANCE=new FixedThreadPool();
    }
    private FixedThreadPool(){
        mExecutor= Executors.newFixedThreadPool(MAX_THREAD);
    }
    public static FixedThreadPool getInstance(){
        return SingletonInstance.INSTANCE;
    }
    public Future<?> submit(Runnable run){
        return mExecutor.submit(run);
    }
    public void execute(Runnable run){
        mExecutor.execute(run);
    }
}
