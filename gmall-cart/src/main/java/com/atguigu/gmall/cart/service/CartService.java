package com.atguigu.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.listener.CartListener;
import com.atguigu.gmall.cart.vo.Cart;
import com.atguigu.gmall.cart.vo.UserInfo;
import com.atguigu.gmall.pmsInterface.entity.SkuInfoEntity;
import com.atguigu.gmall.pmsInterface.entity.SkuSaleAttrValueEntity;

import com.atguigu.gmallsmsinterface.vo.SkuSaleVO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private CartListener cartListener;

    private static final String CART_PREFIX = "gmall:cart:";
    private static final String PRICE_PREFIX= "cart:currentPrice:";


    public String getKey() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userKey = userInfo.getUserKey();
        Long id = userInfo.getId();
        //如果是未登录(获取redis的key)
        String key = CART_PREFIX;
        if (id == null) {
            key= key+userKey;
        } else {
            key =key+ id;
        }
        return key;


    }

    public void insertToCart(Cart cart) {
        //首先判断是否登录(获取userkey)
        String key = getKey();
        //获取用户传输的数量
        Integer num = cart.getCount();
        //获取用户的购物车
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(key);
        //判断是否有该条购物车记录
        if (ops.hasKey(cart.getSkuId().toString())) {
            Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsClient.skuInfoBySkuId(cart.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            //有的话直接增加数量
            String cartStr = ops.get(cart.getSkuId().toString()).toString();
            cart = JSON.parseObject(cartStr, Cart.class);
            cart.setCount(cart.getCount() + num);
            //将数据放回redis中
            //this.redisTemplate.opsForValue().setIfAbsent(PRICE_PREFIX + cart.getSkuId(), skuInfoEntity.getPrice().toString());

        } else {
            //新增一条记录
            Resp<SkuInfoEntity> skuInfoEntityResp = gmallPmsClient.skuInfoBySkuId(cart.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            Resp<List<SkuSaleAttrValueEntity>> listResp = gmallPmsClient.saleInfoBySkuId(cart.getSkuId());
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = listResp.getData();
            Resp<SkuSaleVO> skuSaleVOResp = gmallSmsClient.skuSaleVoByskuId(cart.getSkuId());
            SkuSaleVO skuSaleVO = skuSaleVOResp.getData();
            cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
            cart.setPrice(skuInfoEntity.getPrice());
            this.redisTemplate.opsForValue().setIfAbsent(PRICE_PREFIX + cart.getSkuId(), skuInfoEntity.getPrice().toString());

            //cart.setCurrentPrice(new BigDecimal(s));
            cart.setTitle(skuInfoEntity.getSkuTitle());
            cart.setSkuAttrValue(skuSaleAttrValueEntities);
            cart.setSkuSaleVO(skuSaleVO);

        }
        ops.put(cart.getSkuId().toString(), JSON.toJSONString(cart));
    }


    public List<Cart> queryCarts() {

        //先查询未登录的购物车
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userKey = userInfo.getUserKey();
        Long id = userInfo.getId();
        List<Cart> userKeyCarts = null;

        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(CART_PREFIX+ userKey);
        List<Object> cartlist = ops.values();
        if(!CollectionUtils.isEmpty(cartlist)){
            userKeyCarts = cartlist.stream().map(userKeyCart -> {
                Cart cart = JSON.parseObject(userKeyCart.toString(), Cart.class);
                String currentPrice = redisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId().toString());
                cart.setCurrentPrice(new BigDecimal(currentPrice));
                return cart;
            }).collect(Collectors.toList());
            //未登录的话，直接返回未登录的购物车

        }
        if(id==null){
            return userKeyCarts;
        }

        //查询登录状态下的用户的购物车
        BoundHashOperations<String, Object, Object> idarts = redisTemplate.boundHashOps(CART_PREFIX + id);

            userKeyCarts.forEach(useKeyCart -> {
                Integer count = useKeyCart.getCount();
                String skuId = useKeyCart.getSkuId().toString();
                if (idarts.hasKey(skuId)) {
                    //商品已存在
                    String idCartJson = idarts.get(skuId).toString();
                    Cart idart = JSON.parseObject(idCartJson, Cart.class);
                    useKeyCart.setCount(useKeyCart.getCount() + count);

                    idarts.put(skuId, JSON.toJSONString(idart));
                }
                // 删除未登录状态的购物车
                this.redisTemplate.delete(CART_PREFIX + userKey);
            });


            // 返回登录状态的购物车
            List<Object> userCartJsonList = idarts.values();


                List<Cart>  userCarts= userCartJsonList.stream().map(userCartJson -> {
                    Cart cart = JSON.parseObject(userCartJson.toString(), Cart.class);
                    String price = this.redisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId());
                    cart.setCurrentPrice(new BigDecimal(price));
                    return cart;
                }).collect(Collectors.toList());

            //
            return userCarts;


    }


    public void updateCart(Cart cart) {
        String key = getKey();
        Integer count = cart.getCount();
        BoundHashOperations<String, Object, Object> idarts = redisTemplate.boundHashOps(key);
        String skuId = cart.getSkuId().toString();
        String cartJson= idarts.get(skuId).toString();
        //反序列化
        cart = JSON.parseObject(cartJson, Cart.class);
        cart.setCount(count); // 更新数量
        idarts.put(skuId, JSON.toJSONString(cart));// 保存
    }

    public void deleteCart(Long skuId) {
        String key = getKey();
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(key);
        hashOperations.delete(skuId.toString());
    }
}
