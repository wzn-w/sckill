package com.killserver2.mapper;


import com.killserver2.entity.ItemKillSuccess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ItemMapper {

    /**
     * 减库存
     * @return
     */
    int reduceItem(@Param("code") String code);

    /**
     * 加库存， 取消订单场景
     */
    int addItem(@Param("code") String code);

    /**
     * 查询商品数据
     * @return
     */
    Map<String, Object> selectItem(@Param("code") String code);

    /**
     * 查询所有商品的编号
     */
    List<Map<String, Object>> selectItemCodes();

    /**
     * 根据itemId查询item_kill的id
     */
    Map<String, Object> selectItemKillId(@Param("itemId") Integer itemId);

    /**
     * 抢购成功， 生成订单
     */
    int insertItemSuccess(ItemKillSuccess itemKillSuccess);
}
