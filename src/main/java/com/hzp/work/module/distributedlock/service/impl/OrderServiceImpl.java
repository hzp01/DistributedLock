package com.hzp.work.module.distributedlock.service.impl;

import com.hzp.work.module.distributedlock.lockutil.*;
import com.hzp.work.module.distributedlock.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private A1_NormalBusiness normalBusiness;
    @Autowired
    private A2_SynchronizedLock synchronizedLock;
    @Autowired
    private A3_MysqlOptimisticLock mysqlOptimisticLock;
    @Autowired
    private A4_RedisLock1_NoFinally redisLockNoFinally;
    @Autowired
    private A5_RedisLock2_OnlyFinally redisLockOnlyFinally;
    @Autowired
    private A6_RedisLock3_AddExpire redisLockAddExpire;
    @Autowired
    private A7_RedisLock4_AtomExpire redisLockAtomExpire;
    @Autowired
    private A8_RedisLock5_UUID redisLockUUID;

    @Override
    @Transactional
    public Boolean order(Integer id) {
        /** 普通业务逻辑：查询库存、创建订单、减少库存；*/
        // 缺点：无法解决多线程高并发问题
//        return normalBusiness.order(id);
        /** 解决高并发：悲观锁synchronized；*/
        // 缺点：无法解决分布式问题
//        return synchronizedLock.order(id);
        /**  解决分布式高并发：mysql乐观锁；*/
        // 缺点：请求失败率太高，失败没有重试机制
//        return mysqlOptimisticLock.order(id);
        /**  解决分布式高并发：redisLock方式一:设锁、普通业务、删锁；*/
        // 缺点：业务异常导致无法删除出现死锁;
        // 解决：加finally
//        return redisLockNoFinally.order(id);
        /**  解决分布式高并发：redisLock方式二:设锁、try catch普通业务、finally删锁；*/
        // 缺点：死锁问题；服务宕机：获取锁的服务删除锁之前宕机，锁无法释放，出现死锁
        // 解决：加失效时间
//        return redisLockOnlyFinally.order(id);
        /**  解决分布式高并发：redisLock方式三:设锁、加失效时间、try catch普通业务、finally删锁；*/
        // 缺点：死锁问题；服务宕机，原子操作：设置锁失效时间之前宕机，锁没有释放，出现死锁
        // 解决：需要set锁和expire失效时间保证原子性
//        return redisLockAddExpire.order(id);
        /**  解决分布式高并发：redisLock方式四:setnx锁、try catch中执行业务逻辑，finally删除锁；*/
        // 缺点：释放别人锁：设置锁失效时间太短，线程A锁执行业务逻辑时，锁失效，线程B获取锁时，线程A此时删除了B的锁，出现超卖
        // 解决：为锁设置唯一码，保证线程删除的是自己的锁
//        return redisLockAtomExpire.order(id);
        /**  解决分布式高并发：redisLock方式五:设置锁的值为唯一码如UUID、setnx锁、try catch中执行业务逻辑，finally通过判断唯一码删除该线程的锁；*/
        // 缺点：超卖问题，锁失效：设置锁失效时间太短，线程执行业务逻辑时，锁已经失效，多个线程可以同时执行业务逻辑出现超卖
        // 解决：动态设置失效时间，可以自动续长
        return redisLockUUID.order(id);
        /**  解决分布式高并发：redisLock方式六:获取分布式锁redisson、上锁lock（默认时间30s）、try catch中执行业务逻辑、finally删除锁unlock；*/
        // 缺点：超卖问题，redis主从切换：redis主节点set锁、同步从节点，同步前主节点挂掉了，出现超卖
        // 解决：zk
    }

}
