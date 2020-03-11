package com.hzp.work.module.distributedlock.mapper;

import com.hzp.work.module.distributedlock.entity.Record;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordMapper {
    @Insert({"insert into record(uid, pid) values(#{uid}, #{pid})"})
//    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Record record);
}
