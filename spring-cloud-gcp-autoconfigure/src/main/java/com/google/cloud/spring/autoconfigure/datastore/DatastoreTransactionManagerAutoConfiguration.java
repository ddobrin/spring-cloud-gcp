/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.autoconfigure.datastore;

import com.google.cloud.spring.data.datastore.core.DatastoreTransactionManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Auto-configuration for {@link DatastoreTransactionManager}.
 *
 * @since 1.1
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DatastoreTransactionManager.class)
@ConditionalOnProperty(value = "spring.cloud.gcp.datastore.enabled", matchIfMissing = true)
@AutoConfigureBefore(TransactionAutoConfiguration.class)
public class DatastoreTransactionManagerAutoConfiguration {

  /** Configuration class. */
  @Configuration(proxyBeanMethods = false)
  static class DatastoreTransactionManagerConfiguration {

    private final DatastoreProvider datastore;

    private final TransactionManagerCustomizers transactionManagerCustomizers;

    DatastoreTransactionManagerConfiguration(
        DatastoreProvider datastore,
        ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
      this.datastore = datastore;
      this.transactionManagerCustomizers = transactionManagerCustomizers.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public DatastoreTransactionManager datastoreTransactionManager() {
      DatastoreTransactionManager transactionManager =
          new DatastoreTransactionManager(this.datastore);
      if (this.transactionManagerCustomizers != null) {
        this.transactionManagerCustomizers.customize(transactionManager);
      }
      return transactionManager;
    }
  }
}
