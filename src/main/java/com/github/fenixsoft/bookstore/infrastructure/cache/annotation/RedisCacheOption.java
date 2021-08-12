package com.github.fenixsoft.bookstore.infrastructure.cache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCacheOption {
    RedisCacheable[] cacheable() default {};

    RedisCachePut[] cachePut() default {};

    RedisCacheEvict[] cacheEvict() default {};
}
