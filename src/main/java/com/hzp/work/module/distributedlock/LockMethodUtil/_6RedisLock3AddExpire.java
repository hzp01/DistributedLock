package com.hzp.work.module.distributedlock.LockMethodUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class _6RedisLock3AddExpire {
    @Autowired
    private _1NormalBusiness normalBusiness;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public Boolean order(Integer id) {
        /**  解决分布式高并发：redisLock方式三:设锁、加失效时间、try catch普通业务、finally删锁；*/
        // 缺点：死锁问题；服务宕机，原子操作：设置锁失效时间之前宕机，锁没有释放，出现死锁
        // 解决：需要set锁和expire失效时间保证原子性
        Boolean redisLock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "lockValue");
        stringRedisTemplate.expire("lock", 30, TimeUnit.SECONDS);
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
