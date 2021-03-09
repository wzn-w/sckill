package com.killserver2.service;

import java.util.List;
import java.util.Map;

public interface IItemService {

    /**
     * 减库存
     * @return
     */
    int reduceItem(String code) throws Exception;

    Map<String, Object> selectItem(String code) throws InterruptedException;

    List<Map<String, Object>> selectItemCodes();

    /**
     * 生成订单
     */
    int itemKillSuccess(String code) throws InterruptedException;
}
