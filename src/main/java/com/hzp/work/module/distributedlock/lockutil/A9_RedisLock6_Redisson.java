package com.hzp.work.module.distributedlock.lockutil;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class A9_RedisLock6_Redisson {
    @Autowired
    private A1_NormalBusiness normalBusiness;

    @Autowired
    private RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    public Boolean order(Integer id) {
        /**  解决分布式高并发：redisLock方式六:获取分布式锁redisson、上锁lock（默认时间30s）、try catch中执行业务逻辑、finally删除锁unlock；*/
        // 缺点：超卖问题，redis主从切换：redis主节点set锁、同步从节点，同步前主节点挂掉了，出现超卖
        // 解决：zk
        RLock lock = redissonClient.getLock(id.toString());
        lock.lock(30, TimeUnit.SECONDS);
        try {
            normalBusiness.order(id);
        } catch (Exception e) {
            throw new RuntimeException("执行业务逻辑异常！");
        } finally {
            lock.unlock();
        }
        return true;
    }
}
