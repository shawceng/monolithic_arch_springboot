/*
 * Copyright 2012-2020. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. More information from:
 *
 *        https://github.com/fenixsoft
 */

package com.github.fenixsoft.bookstore.domain.account;

import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCacheEvict;
import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCacheable;
import com.sun.xml.bind.v2.model.core.ID;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 用户对象数据仓库
 *
 * @author icyfenix@gmail.com
 * @date 2020/3/6 23:10
 **/
@Mapper
public interface AccountRepository {

    List<Account> findAll();

    @RedisCacheable("USER:{}")
    Account findByUsername(String username);

    /**
     * 判断唯一性，用户名、邮箱、电话不允许任何一个重复
     */
    boolean existsByUsernameOrEmailOrTelephone(String username, String email, String telephone);

    /**
     * 判断唯一性，用户名、邮箱、电话不允许任何一个重复
     */
    Collection<Account> findByUsernameOrEmailOrTelephone(String username, String email, String telephone);

    /**
     * 判断存在性，用户名存在即为存在
     */
    @RedisCacheable("USER:{}")
    boolean existsByUsername(String username);


    // 覆盖以下父类中需要处理缓存失效的方法
    // 父类取不到CacheConfig的配置信息，所以不能抽象成一个通用的父类接口中完成
    void save(Account entity);

    void update(Account entity);

    <S extends Account> Iterable<S> saveAll(Iterable<S> entities);

    @RedisCacheable("USER:{}")
    Account findById(ID id);

    @RedisCacheable("USER:{}")
    boolean existsById(Integer id);

    @RedisCacheable("USER:{}")
    void deleteById(Integer id);

    @RedisCacheEvict("{id}")
    void delete(Account entity);

    void deleteAll(Iterable<? extends Account> entities);

    void deleteAll();
}
