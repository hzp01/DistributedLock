package com.hzp.work.module.distributedlock.LockMethodUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class _5RedisLock2OnlyFinally {
    @Autowired
    private _1NormalBusiness normalBusiness;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional(rollbackFor = Exception.class)
    public Boolean order(Integer id) {
        /**  解决分布式高并发：redisLock方式二:设锁、try catch普通业务、finally删锁；*/
        // 缺点：死锁问题；服务宕机：获取锁的服务删除锁之前宕机，锁无法释放，出现死锁
        // 解决：加失效时间
        Boolean redisLock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "lockValue");
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
