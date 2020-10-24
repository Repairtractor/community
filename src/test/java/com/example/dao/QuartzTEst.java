package com.example.dao;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QuartzTEst {
    @Autowired
    private Scheduler scheduler;

    @Test
    public void test(){
        try {
            boolean job = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));
            System.out.println(job);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
