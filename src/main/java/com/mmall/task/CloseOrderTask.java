package com.mmall.task;

import com.mmall.common.Consts;
import com.mmall.service.IOrderService;
import com.mmall.utility.PropertiesUtil;
import com.mmall.utility.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

/**
 * 只考虑定时关闭订单，无分布式
 */
//    @Scheduled(cron="0 */1 * * * ?")//每一分钟（每个一分钟的整数倍）
//    public void closeOrderTaskV1(){
//        log.info("关闭订单定时任务启动");
//        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
//        iOrderService.closeOrder(hour);
//        log.info("关闭订单定时任务结束");
//    }

    /**
     * 考虑redis分布式情况----》v2
     */
//    @Scheduled(cron="0 */1 * * * ?")
//    public void closeOrderTaskV2(){
//        log.info("关闭订单定时任务启动");
//        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","50"));
//        Long setnxResult = RedisShardedPoolUtil.setnx(Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
//        if(setnxResult!=null&&setnxResult.intValue()==1){
//            //如果返回值是1，代表设置成功，获取锁
//            closeOrder(Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
//        }else{
//            log.info("没有获得分布式锁:{}",Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
//        }
//        log.info("关闭订单定时任务结束");
//    }
//
//    private void closeOrder(String lockName){
//        RedisShardedPoolUtil.expire(lockName,50);//有效期50s，防止死锁
//        log.info("获取:{},ThreadName:{}",Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
//        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
//        iOrderService.closeOrder(hour);
//        RedisShardedPoolUtil.del(Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
//        log.info("释放:{},ThreadName:{}",Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
//        log.info("==============================");
//    }

    /**
     * 创建锁后，如果突然关闭tomcat，锁变成永久---》死锁，v3解决这个问题
     */
    @Scheduled(cron="0 */1 * * * ?")
    public void closeOrderTaskV3(){
        log.info("关闭订单定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
        Long setnxResult = RedisShardedPoolUtil.setnx(Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnxResult!=null&&setnxResult.intValue()==1){
            //如果返回值是1，代表设置成功，获取锁
            closeOrder(Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }else{
            //未获取到锁，继续判断，判断时间戳，看是否可以重置并获取锁
            String lockValueStr = RedisShardedPoolUtil.get(Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if(lockValueStr!=null&&System.currentTimeMillis()>Long.parseLong(lockValueStr)){
                //锁是已经失效了
                String getSetResult = RedisShardedPoolUtil.getSet(Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
                //再次用时间戳getSet
                //返回给定的key旧值，-》旧值判断，是否可以锁
                //当key没有旧值，即key不存在时，返回nil
                //这里我们set了一个新的value值，获取旧值
                if(getSetResult==null||(getSetResult!=null&& StringUtils.equals(getSetResult,lockValueStr))){
                    closeOrder(Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }else{
                    log.info("没有获得分布式锁:{}",Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
            }else{
                log.info("没有获得分布式锁:{}",Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }
        log.info("关闭订单定时任务结束");
    }

    private void closeOrder(String lockName){
        RedisShardedPoolUtil.expire(lockName,5);//有效期5s，防止死锁
        log.info("获取:{},ThreadName:{}",Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放:{},ThreadName:{}",Consts.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("==============================");
    }

}
