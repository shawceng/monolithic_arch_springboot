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

package com.github.fenixsoft.bookstore.domain.payment;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;


/**
 * 支付单数据仓库
 *
 * @author icyfenix@gmail.com
 * @date 2020/3/12 23:25
 **/
@Mapper
public interface PaymentRepository {

    Payment getByPayId(String payId);

    void save(Payment payment);

    Payment findById(Integer id);
}
