package com.github.fenixsoft.bookstore.infrastructure.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheable {
    int DEFAULT_TTL = 4 * 60 * 60;
    int NULL_TTL = 5 * 60;

    String value();

    int[] key_args() default {};

    RedisAction action() default RedisAction.REDIS_FIRST;

    int ttl() default DEFAULT_TTL;

    boolean sync() default false;

    boolean cacheNull() default true;

    int nullTtl() default NULL_TTL;

    enum RedisAction {
        REDIS_FIRST,
        REDIS_STAB
    }
}

