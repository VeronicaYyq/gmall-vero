package com.atguigu.gmall.index.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GuliCache;
import org.apache.commons.lang.StringUtils;
import org.apache.naming.SelectorContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.apache.naming.SelectorContext.prefix;

@Aspect
@Component
public class GuliCacheAspect {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    //拦截所有带这个注解的方法
    @Around("@annotation(com.atguigu.gmall.index.annotation.GuliCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        //获取方法上的注解（切点指的是目标方法）joinPoint.getSignature()是获得方法上的签名
        MethodSignature signature =(MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        GuliCache annotation = method.getAnnotation(GuliCache.class);
        String prefix = annotation.prefix();

        //获取key(查看是否有缓存,若是有缓存的话直接返回，若是没有的话直接从数据库查询)
        String arg = joinPoint.getArgs().toString();
        String key= SelectorContext.prefix +arg;
        result = cacheHit(method, key);
        if(result!=null){
           return result;
        }

        //添加 分布式锁（）
        RLock lock = redissonClient.getLock("lock"+arg);
        //不是将所有的请求进行拦截，假设是需要拦截id为3的，那么就不会对于其他的进行拦截
        lock.lock();
        //再次获取缓存中的数据，防止在加锁之前，就已经有了缓存
        result = cacheHit(method, key);
        if(result!=null){
            lock.unlock();
            return result;
        }
        //执行目标方法joinPoint.getArgs()是目标方法所需要的参数
        result = joinPoint.proceed(joinPoint.getArgs());

        //放入缓存
        redisTemplate.opsForValue().set(key,JSON.toJSONString(result),30, TimeUnit.DAYS);

        lock.unlock();
        return result;
    }

    public Object cacheHit(Method method, String key) {
        Object result;
        String s = redisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank(s)){
            return JSON.parseObject(s, method.getReturnType());
        }
        return null;
    }


}
