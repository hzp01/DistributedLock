package com.hzp.work.module.distributedlock.entity;

import lombok.Data;

@Data
public class Product {
    public Integer id;
    public String name;
    public Integer num;
    public Integer version;
}
