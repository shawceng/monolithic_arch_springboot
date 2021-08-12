package com.github.fenixsoft.bookstore.infrastructure.cache;

import com.github.fenixsoft.bookstore.domain.BaseEntity;
import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCacheEvict;
import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCacheOption;
import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCachePut;
import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCacheable;
import com.github.fenixsoft.bookstore.infrastructure.utility.PlaceholderResolver;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class RedisAopAdviceDefine {

    private Logger logger = LoggerFactory.getLogger(RedisAopAdviceDefine.class);

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    @Around("@annotation(redisCacheable)")
    public Object cacheSet(ProceedingJoinPoint joinPoint, RedisCacheable redisCacheable) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        boolean stab = redisCacheable.action() != RedisCacheable.RedisAction.REDIS_FIRST;
        Object[] keyArgs = getKeyArgs(joinPoint.getArgs(), redisCacheable.key_args());
        String key = PlaceholderResolver.resolveByObjects(redisCacheable.value(), keyArgs);

        Class<?> returnType = method.getReturnType();
        Object result = stab ? null : get(key, returnType);
        if (result != null) {
            return result;
        }

        result = joinPoint.proceed();
        if (result != null) {
            redisTemplate.opsForValue().set(key, (Serializable) result, redisCacheable.ttl(), TimeUnit.SECONDS);
        }
        return result;
    }

    @AfterReturning(value = "@annotation(redisCachePut)", returning = "retObj")
    public void cachePut(JoinPoint joinPoint, RedisCachePut redisCachePut, Object retObj) {
        Object[] keyArgs = getKeyArgs(joinPoint.getArgs(), redisCachePut.key_args());

        String key = PlaceholderResolver.resolveByObjects(redisCachePut.value(), keyArgs);

        if (retObj!= null) {
            redisTemplate.opsForValue().set(key, (Serializable) retObj, redisCachePut.ttl(), TimeUnit.SECONDS);
        }
    }

    @AfterReturning(value = "@annotation(redisCacheEvict)")
    public void cacheRemove(JoinPoint joinPoint, RedisCacheEvict redisCacheEvict) {
        Object[] keyArgs = getKeyArgs(joinPoint.getArgs(), redisCacheEvict.key_args());
        String key = PlaceholderResolver.resolveByObjects(redisCacheEvict.value(), keyArgs);

        if (key != null) {
            redisTemplate.delete(key);
        }
    }

    @Around("@annotation(redisCacheOption)")
    public Object cacheOptionAround(ProceedingJoinPoint joinPoint, RedisCacheOption redisCacheOption) throws Throwable {
        RedisCachePut[] redisCachePuts = redisCacheOption.cachePut();
        RedisCacheEvict[] redisCacheEvicts = redisCacheOption.cacheEvict();
        Object result = joinPoint.proceed();

        for (RedisCachePut redisCachePut : redisCachePuts) {
            cachePut(joinPoint, redisCachePut, result);
        }

        for (RedisCacheEvict redisCacheEvict : redisCacheEvicts) {
            cacheRemove(joinPoint, redisCacheEvict);
        }
        return result;
    }

    private Object[] getKeyArgs(Object[] args, int[] keyArgs) {
        Object[] redisKeyArgs;
        int len = keyArgs.length;
        if (len == 0) {
            return args;
        }

        redisKeyArgs = new Object[len];
        for (int i = 0; i < keyArgs.length; i++) {
            redisKeyArgs[i] = args[keyArgs[i]];
        }
        return redisKeyArgs;
    }

    private <T> T get(String key, Class<T> clazz) {
        return (T) redisTemplate.opsForValue().get(key);
    }
}
