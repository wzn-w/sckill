package com.killserver1.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.killserver1.service.IItemService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品接口
 */
@RestController
public class ItemController {

    @Autowired
    IItemService itemService;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 创建令牌桶实例， 每秒生成 200个令牌，放行 200个请求
     */
    RateLimiter rateLimiter = RateLimiter.create(200);

    /**
     * 查询商品
     * @param code 商品code
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("getItem")
    public Map<String, Object> getItem(@RequestParam("code") String code) throws InterruptedException {
        Map<String, Object> item = itemService.selectItem(code);
        return item;
    }

    /**
     * 秒杀接口
     * @param code
     * @return
     * @throws Exception
     */
    @RequestMapping("/startItemKill")
    public Map<String, Integer> getItemKill(@RequestParam("code") String code) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        // 获得令牌后才能继续访问
        double acquire = rateLimiter.acquire();
        System.out.println("请求等待时长: " + acquire);
        int status = itemService.reduceItem(code);
        map.put("status", status);
        return map;
    }
}
