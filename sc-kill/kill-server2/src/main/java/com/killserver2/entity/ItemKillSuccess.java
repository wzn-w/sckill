package com.killserver2.entity;

import java.math.BigDecimal;
import java.util.Date;

public class ItemKillSuccess {

    private String code;
    private Integer itemId;
    private Integer killId;
    private String userId;
    private Integer status;
    private Date createTime;
    private BigDecimal price;

    public ItemKillSuccess(String code, Integer itemId, Integer killId, String userId, Integer status, Date createTime, BigDecimal price) {
        this.code = code;
        this.itemId = itemId;
        this.killId = killId;
        this.userId = userId;
        this.status = status;
        this.createTime = createTime;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public ItemKillSuccess setCode(String code) {
        this.code = code;
        return this;
    }

    public Integer getItemId() {
        return itemId;
    }

    public ItemKillSuccess setItemId(Integer itemId) {
        this.itemId = itemId;
        return this;
    }

    public Integer getKillId() {
        return killId;
    }

    public ItemKillSuccess setKillId(Integer killId) {
        this.killId = killId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public ItemKillSuccess setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public ItemKillSuccess setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public ItemKillSuccess setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public  BigDecimal getPrice() {
        return price;
    }

    public ItemKillSuccess setPrice( BigDecimal price) {
        this.price = price;
        return this;
    }
}
