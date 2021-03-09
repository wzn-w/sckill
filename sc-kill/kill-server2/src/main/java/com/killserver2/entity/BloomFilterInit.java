package com.killserver2.entity;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.killserver2.mapper.ItemMapper;
import com.killserver2.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class BloomFilterInit {

    @Autowired
    IItemService itemService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ItemMapper itemMapper;

    //预计要插入的数据
    private static int size = 1000000;
    //期望的误判率
    private static double fpp = 0.0001;
    public static BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), size, fpp);

    /**
     * 初始化bloomFilter， redis中的库存信息
     */
    @PostConstruct
    public void bloomFilterInit() throws InterruptedException {
        List<Map<String, Object>> list = itemService.selectItemCodes();
        for (Map<String, Object> map : list){
            String code = (String) map.get("code");
            // 将所有商品编号加入 bloomFilter
            bloomFilter.put(code);
            // 将所有商品的库存存入redis
            Map<String, Object> item = itemMapper.selectItem(code);
            Integer total = (Integer)item.get("total");
            System.out.println("bloom " + total);
            redisTemplate.opsForValue().set(code + "stock", total);
        }
    }
}
