package com.example.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

//执行job任务
public class MyJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("哦 我是分布式定时任务 quart:\t"+Thread.currentThread().getName());
    }
}
