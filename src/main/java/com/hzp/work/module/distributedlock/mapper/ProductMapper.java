package com.hzp.work.module.distributedlock.mapper;

import com.hzp.work.module.distributedlock.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductMapper {
    @Select("select * from product where id = #{id}")
    Product selectById(@Param("id")Integer id);

    @Update("update product set num = num - 1 where id = #{id}")
    int update(@Param("id")Integer id);

    /*** mysql悲观锁 ***/
    @Select("select * from product where id = #{id} for update")
    Product selectByIdPessimismLock(@Param("id") Integer id);

    /*** mysql乐观锁,这里的version可以直接用num代替，效果一样, 这个业务场景里采用num>0效果更好 ***/
    @Update("update product set num = num - 1, version = version + 1 where id = #{id} and version = #{version}")
    int updateOptimisticLock(@Param("id") Integer id, @Param("version") Integer version);
}
