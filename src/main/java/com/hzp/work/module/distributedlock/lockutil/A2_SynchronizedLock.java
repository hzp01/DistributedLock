package com.hzp.work.module.distributedlock.lockutil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class A2_SynchronizedLock {
    @Autowired
    private A1_NormalBusiness normalBusiness;

    /**
     * 一般业务逻辑，查询库存、创建订单、减少库存
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean order(Integer id) {
        synchronized (this) {
            return normalBusiness.order(id);
        }
    }
}
