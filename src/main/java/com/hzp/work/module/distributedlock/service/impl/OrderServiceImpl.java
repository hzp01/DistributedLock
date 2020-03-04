package com.hzp.work.module.distributedlock.service.impl;

import com.hzp.work.module.distributedlock.entity.Product;
import com.hzp.work.module.distributedlock.entity.Record;
import com.hzp.work.module.distributedlock.mapper.ProductMapper;
import com.hzp.work.module.distributedlock.mapper.RecordMapper;
import com.hzp.work.module.distributedlock.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.UUID;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Override
    @Transient
    public Boolean order(Integer id) {
        return pessimismLockOrder(id);
    }

    @Transient
    private Boolean optimisticLockOrder(Integer id) {
        /*** 乐观锁示例,优点类似java的CAS原子操作不加锁，缺点高并发下订单成功率低 ***/
        Product product = productMapper.selectById(id);
        int num = product.getNum();
        log.info("查询的数量为：{}", num);
        if (num <= 0) {
            return false;
        }

        int updateResult = productMapper.updateOptimisticLock(id, product.getVersion());
        if (updateResult == 1) {
            Record record = Record.builder().uid(UUID.randomUUID().toString()).pid(id).build();
            int insertResult = recordMapper.insert(record);
            log.info("版本号为：{}，更新条数为：{}，插入结果为：{}",
                    product.getVersion(), updateResult, insertResult);
        } else {
            throw new RuntimeException("更新优惠券数量失败");
        }
        return true;
    }

    @Transient
    private Boolean pessimismLockOrder(Integer id) {
        // select * from product where id = #{id} for update
        Product product = productMapper.selectById(id);
        int num = product.getNum();
        // 问题点：会查询到同样的数量，会导致破发情况；
        log.info("查询的数量为：{}", num);
        if (num <= 0) {
            return false;
        }

        // update product set num = num - 1 where id = #{id}
        int updateResult = productMapper.update(id);
        if (updateResult == 1) {
            Record record = Record.builder().uid(UUID.randomUUID().toString()).pid(id).build();
            // insert into record(uid, pid) values(#{uid}, #{pid})
            int insertResult = recordMapper.insert(record);
            log.info("当前数量剩余：{}，更新条数为：{}，插入结果为：{}",
                    num, updateResult, insertResult);
        } else {
            throw new RuntimeException("更新优惠券数量失败");
        }
        return true;
    }
}
