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

package com.github.fenixsoft.bookstore.applicaiton;

import com.github.fenixsoft.bookstore.domain.account.Account;
import com.github.fenixsoft.bookstore.domain.account.AccountRepository;
import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCacheEvict;
import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCacheOption;
import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCachePut;
import com.github.fenixsoft.bookstore.infrastructure.cache.annotation.RedisCacheable;
import com.github.fenixsoft.bookstore.infrastructure.utility.Encryption;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.PUT;

/**
 * 用户资源的应用服务接口
 *
 * @author icyfenix@gmail.com
 * @date 2020/3/10 17:46
 **/
@Named
@Transactional
public class AccountApplicationService {

    @Inject
    private AccountRepository repository;

    @Inject
    private Encryption encoder;

    @RedisCacheOption(
            cacheEvict = {
                    @RedisCacheEvict("ALL_USER")
            }
    )
    public void createAccount(Account account) {
        account.setPassword(encoder.encode(account.getPassword()));
        repository.save(account);
    }

    @RedisCacheable("USER:{}")
    public Account findAccountByUsername(String username) {
        return repository.findByUsername(username);
    }

    @RedisCacheOption(
            cacheEvict = {
                    @RedisCacheEvict("USER:{username}"),
                    @RedisCacheEvict("ALL_USER")
            }
    )
    public void updateAccount(Account account) {
        repository.update(account);
    }

}
