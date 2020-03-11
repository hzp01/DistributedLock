package com.hzp.work.module.distributedlock.LockMethodUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class _8RedisLock5UUID {
    @Autowired
    private _1NormalBusiness normalBusiness;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final String LOCK_KEY = "lock";

    @Transactional(rollbackFor = Exception.class)
    public Boolean order(Integer id) {
        /**  解决分布式高并发：redisLock方式五:设置锁的值为唯一码如UUID、setnx锁、try catch中执行业务逻辑，finally通过判断唯一码删除该线程的锁；*/
        // 缺点：超卖问题，锁失效：设置锁失效时间太短，线程执行业务逻辑时，锁已经失效，多个线程可以同时执行业务逻辑出现超卖
        // 解决：动态设置失效时间，可以自动续长
        String uuid = UUID.randomUUID().toString();
        Boolean redisLock = stringRedisTemplate.opsForValue().setIfAbsent(LOCK_KEY, uuid, 30, TimeUnit.SECONDS);
        if (!redisLock) {
            return false;
        }
        try {
            normalBusiness.order(id);
        } catch (Exception e) {
            throw new RuntimeException("执行业务逻辑异常！");
        } finally {
            if (stringRedisTemplate.opsForValue().get(LOCK_KEY).equals(uuid)) {
                stringRedisTemplate.delete("lock");
            }
        }
        return true;
    }
}
