package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;

import com.atguigu.gmall.index.annotation.GuliCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;

import com.atguigu.gmall.pmsInterface.entity.CategoryEntity;
import com.atguigu.gmall.pmsInterface.vo.IndexVO;
import org.apache.commons.lang.StringUtils;

import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Time;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IndexServiceImpl implements IndexService {
   @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX="index:cates:";
    @Override
    public List<CategoryEntity> queryLevel1() {

        Resp<List<CategoryEntity>> listResp = gmallPmsClient.queryCategories(1, null);
        List<CategoryEntity> categoryEntities = listResp.getData();
        return categoryEntities;
    }

    @GuliCache(prefix = "index:cates")
    @Override
    public List<IndexVO> querySubLevelsByPid(Long pid) {
      /*      //通过Redisson的方式来获取锁
        RLock lock = redissonClient.getLock("lock");
        lock.lock();*/

        //1.判断是否有缓存
      /* String JsonStr= (String) redisTemplate.opsForValue().get(KEY_PREFIX + pid);
       //缓存中有的话直接序列化
       if(!JsonStr.isEmpty()){
           List<IndexVO> indexVOS = JSON.parseArray(JsonStr, IndexVO.class);
           return indexVOS;
       }*/
        //查询数据库
        Resp<List<IndexVO>> listResp = gmallPmsClient.getLevelsByPid(pid);
        List<IndexVO> indexVOS = listResp.getData();
        //查询之后放入缓存
       /* redisTemplate.opsForValue().set(KEY_PREFIX+pid,JSON.toJSONString(indexVOS));
       * lock.unlock();*/
        return indexVOS;
    }

    @Override
    public synchronized void testLock() {
        //通过Redisson来获取锁
        RLock lock = redissonClient.getLock("lock");
        lock.lock(10, TimeUnit.SECONDS);

        //判断是否有缓存
        String value = redisTemplate.opsForValue().get("num");
        if(StringUtils.isEmpty(value)){
           return;
        }
        //如果有就转换成int类型
        int num = Integer.parseInt(value);
        redisTemplate.opsForValue().set("num",String.valueOf(++num));
        lock.unlock();
















        /*//1.从redis中获取锁
        String value= UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", value,2, TimeUnit.MILLISECONDS);
        *//*但是可能还会出现一个问题是业务逻辑还未执行完毕，锁已经过期，并且自动释放，这样会导致在业务逻辑执行完之后删除锁的时候误删了别的线程的锁
        * 所以要给锁设置一个唯一的值，在删除锁的时候进行判断*//*
        if (lock){
            //查询redis中num的值
            String num = (String) redisTemplate.opsForValue().get("num");
            //判断该值是不是为空，若是为空的话直接返回
            if(num==null){
                return ;
            }
            int i = Integer.parseInt(num);
            this.redisTemplate.opsForValue().set("num", String.valueOf(++i),20+(new Random()).nextInt(20),TimeUnit.HOURS);
            //如果所有的数据都在同一时间过期的话，会造成数据雪崩的现象，这样请求都会转发到数据库中去，所以要给他们设置不同的随机时间
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //lua脚本来保证原子性
           *//* String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            this.redisTemplate.execute(new DefaultRedisScript<>(script), Arrays.asList("lock"),value);*//*
            //释放锁之前进行判断,这种智能降低因为锁的过期时间过短造成的误删锁的问题
            if(StringUtils.equals(redisTemplate.opsForValue().get("key"),value)){
                redisTemplate.delete("lock");
            }

        }else{
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            testLock();
        }*/
    }

    @Override
    public String testLatch() throws InterruptedException {
        RCountDownLatch latch = redissonClient.getCountDownLatch("anyCountDownLatch");
        String value = redisTemplate.opsForValue().get("n");
        int n = Integer.parseInt(value);
        latch.trySetCount(n);


            latch.await();

        return"结束";

    }

    @Override
    public String testOut() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("anyCountDownLatch");
        latch.countDown();

        String value = redisTemplate.opsForValue().get("n");
        int n = Integer.parseInt(value);

        redisTemplate.opsForValue().set("n",String.valueOf(n--));
        return "减一减一";
    }
}
