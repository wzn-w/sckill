package com.killserver2.service.serviceImpl;


import com.killserver2.entity.BloomFilterInit;
import com.killserver2.entity.IdWorker;
import com.killserver2.entity.ItemKillSuccess;
import com.killserver2.mapper.ItemMapper;
import com.killserver2.service.IItemService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ItemServiceImpl implements IItemService {

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RedissonClient redissonClient;

    ReentrantLock reenLock = new ReentrantLock();

    /**
     * 减库存
     * 预减缓存，库存为0时，关闭购买入口，剩下的请求直接返回 商品已售完
     * 异步地更新DB
     * @return
     */
    @Override
    public int reduceItem(String code) throws Exception {
        RLock redissonLock = redissonClient.getLock("lock");
        try{
            // 加分布式锁
            redissonLock.lock();
            Integer stock = (Integer)redisTemplate.opsForValue().get(code + "stock");
            System.out.println("stock " + stock);
            // 已售完
            if (stock < 1){
                System.out.println("商品已售完");
                return 0;
            }
            // 更新缓存
            redisTemplate.opsForValue().set(code + "stock", stock - 1);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 解锁
            redissonLock.unlock();
        }

        // 异步更新DB减库存， 生成订单
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        // 将消息携带绑定键值：TestDirectRouting 发送到交换机TestDirectExchange
        rabbitTemplate.convertAndSend("TestDirectExchange", "TestDirectRouting", map);
        return 1;
    }

    /**
     * 查询商品
     * @return
     */
    @Override
    public Map<String, Object> selectItem(String code) throws InterruptedException {
        Map<String, Object> map = null;
        // 判断布隆过滤器中是否有该 key, 不存在直接返回null,  防止缓存穿透
        if(!BloomFilterInit.bloomFilter.mightContain(code)){
            System.out.println("该key不存在");
            return null;
        }
        //若缓存中存在，直接从缓存中获取
        if(redisTemplate.hasKey(code)){
            map = (Map<String, Object>)redisTemplate.opsForValue().get(code);
            System.out.println("从缓存中获取");
            return map;
        }
        // 获取可重入互斥锁， 包括查看是否空闲，加锁
        if(reenLock.tryLock()){
            //若缓存中不存在则从数据库中读取
            map = itemMapper.selectItem(code);
            System.out.println(map);
            if(map != null){
                //过期时间设置为 200 - 300的随机数
                int timeout = (int)(Math.random() * 100) + 200;
                //将数据库中的信息，加入缓存, 设置过期时间
                redisTemplate.opsForValue().set(code, map, timeout, TimeUnit.SECONDS);
                System.out.println("从数据库中拿");
            }
            reenLock.unlock();
        }else {
            Thread.sleep(500);
            // 等待500毫秒后继续判断
            this.selectItem(code);
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> selectItemCodes() {
        return itemMapper.selectItemCodes();
    }

    /**
     * 生成订单
     * @param code
     * @return
     * @throws InterruptedException
     */
    @Override
    public int itemKillSuccess(String code) throws InterruptedException {
        IdWorker idWorker = new IdWorker(5, 5, 100);

        // 使用雪花算法生成全局唯一id（订单号）
        long id = idWorker.nextId();
        Map<String, Object> item = this.selectItem(code);
        Map<String, Object> itemKill = itemMapper.selectItemKillId((Integer) item.get("id"));
        ItemKillSuccess itemKillSuccess = new ItemKillSuccess(String.valueOf(id), (Integer) item.get("id"), (Integer) itemKill.get("id"), "", 0, new Date(), (BigDecimal)item.get("price"));
        int res = itemMapper.insertItemSuccess(itemKillSuccess);
        return res;
    }
}
