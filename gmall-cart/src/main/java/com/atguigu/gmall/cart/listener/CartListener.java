package com.atguigu.gmall.cart.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.pmsInterface.entity.SkuInfoEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class CartListener {
    @Autowired
    private CartService cartService;

    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String PRICE_PREFIX= "cart:currentPrice:";

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "gmall.cart.queue",durable = "true"),
            exchange = @Exchange(
                    value = "gmall.cart.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),key={"cart.update"}
    ))

    public void listenerCart(Map<String,Object> map){
        Long skuId= (Long) map.get("skuId");
        BigDecimal price = new BigDecimal(map.get("price").toString());
        if(map.get("skuId")!=null) {

            redisTemplate.opsForValue().set(PRICE_PREFIX+skuId.toString(),price.toString());
        }
    }
}
