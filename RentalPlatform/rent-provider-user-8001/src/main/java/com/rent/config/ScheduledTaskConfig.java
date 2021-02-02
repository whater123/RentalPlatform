package com.rent.config;

import com.rent.memoryBase.UserWatchedFilter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * corn表达式在linux使用广泛，具体可以参考cron表达式详解以及在线Cron表达式生成器
 * initialDelay：启动后多久开始执行，单位时毫秒
 * fixedRate：下次执行时间，任务开始运行的时候就计时。
 * fixedDelay：下次执行时间，fixedDelay等任务进行完了才开始计时
 * @author w
 */
@Component
public class ScheduledTaskConfig {

//    @Scheduled(cron = "5 0 0 * * ?")
//    public void scheduledTask1(){
//        System.out.println("定时任务1");
//    }

    /**
     * 启动后的10分钟开始执行，之后每10分钟执行一次，清除推荐过滤
     */
    @Scheduled(initialDelay =  1000 * 60 * 10,fixedDelay = 1000 * 60 * 10)
    public void scheduledTask2(){
        UserWatchedFilter.USER_WATCHED.clear();
        System.out.println("定时任务已执行，用户查看记录已清除");
    }

//    @Scheduled(initialDelay =  1000 * 10,fixedRate = 1000 * 5)
//    public void scheduledTask3(){
//        System.out.println("任务3执行时间："+System.currentTimeMillis());
//        System.out.println("定时任务3");
//        try {
//            Thread.sleep(2*1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("任务3结束时间："+System.currentTimeMillis());
//    }
}

