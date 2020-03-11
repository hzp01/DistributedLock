package com.hzp.work.module.distributedlock.lockutil;

import com.hzp.work.module.distributedlock.entity.Product;
import com.hzp.work.module.distributedlock.entity.Record;
import com.hzp.work.module.distributedlock.mapper.ProductMapper;
import com.hzp.work.module.distributedlock.mapper.RecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class A1_NormalBusiness {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RecordMapper recordMapper;

    /**
     * 一般业务逻辑，查询库存、创建订单、减少库存
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean order(Integer id) {
        // 查询库存
        Product product = productMapper.selectById(id);
        int num = product.getNum();
        log.info("查询的数量为：{}", num);
        if (num <= 0) {
            return false;
        }

        // 创建订单
        Record record = Record.builder().uid(UUID.randomUUID().toString()).pid(id).build();
        int insertResult = recordMapper.insert(record);
        log.info("创建订单的结果为：{}", insertResult);
        if (insertResult != 1) {
            throw new RuntimeException("创建订单失败");
        } else {
            // 减少库存
            int updateResult = productMapper.update(id);
            log.info("减少库存的结果为：{}", updateResult);
            if (updateResult != 1) {
                throw new RuntimeException("减少库存失败");
            }
        }
        return true;
    }
}
