package com.hzp.work.module.distributedlock.LockMethodUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class _4RedisLock1NoFinally {
    @Autowired
    private _1NormalBusiness normalBusiness;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public Boolean order(Integer id) {
        // 设锁、普通业务、删锁；缺点：业务异常导致无法删除出现死锁，解决：加finally
        Boolean redisLock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "lockValue");
        if (!redisLock) {
            return false;
        }
//            int i = 1/0;
        normalBusiness.order(id);
        stringRedisTemplate.delete("lock");
        return true;
    }
}
