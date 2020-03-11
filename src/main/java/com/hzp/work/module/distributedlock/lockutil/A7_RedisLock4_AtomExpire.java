package com.hzp.work.module.distributedlock.lockutil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class A7_RedisLock4_AtomExpire {
    @Autowired
    private A1_NormalBusiness normalBusiness;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public Boolean order(Integer id) {
        /**  解决分布式高并发：redisLock方式四:setnx锁、try catch中执行业务逻辑，finally删除锁；*/
        // 缺点：释放别人锁：设置锁失效时间太短，线程A锁执行业务逻辑时，锁失效，线程B获取锁时，线程A此时删除了B的锁，出现超卖
        // 解决：为锁设置唯一码，保证线程删除的是自己的锁
        Boolean redisLock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "lockValue", 30, TimeUnit.SECONDS);
        if (!redisLock) {
            return false;
        }
        try {
            normalBusiness.order(id);
        } catch (Exception e) {
            throw new RuntimeException("执行业务逻辑异常！");
        } finally {
            stringRedisTemplate.delete("lock");
        }
        return true;
    }
}
