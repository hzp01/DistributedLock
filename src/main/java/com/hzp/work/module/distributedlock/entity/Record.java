package com.hzp.work.module.distributedlock.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Record {
    public Integer id;
    public Integer pid;
    public String uid;
}
