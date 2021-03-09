package com.killserver2.util.Receiver;


import com.killserver2.mapper.ItemMapper;
import com.killserver2.service.IItemService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Component
/**
 * 监听的队列名称 TestDirectQueue
 */


@RabbitListener(queues = "TestDirectQueue")
public class StockDirectReceiver {

    @Autowired
    ItemMapper itemMapper;

    @Autowired
    IItemService itemService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitHandler
    public void process(Map testMessage) throws InterruptedException {
        System.out.println("DirectReceiver消费者收到消息  : " + testMessage.toString());
        String code = (String)testMessage.get("code");
        addOrderAndReduceStock(code);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean addOrderAndReduceStock(String code) throws InterruptedException {
        int res1 = itemMapper.reduceItem(code);
        int res2 = itemService.itemKillSuccess(code);
        return res1 > 0 && res2 > 0;
    }
}
