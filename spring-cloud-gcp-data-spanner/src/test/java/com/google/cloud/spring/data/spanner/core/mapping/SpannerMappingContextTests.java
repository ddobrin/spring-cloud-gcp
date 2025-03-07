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

package com.google.cloud.spring.data.spanner.core.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests for the Spanner mapping context. */
@ExtendWith(SpringExtension.class)
class SpannerMappingContextTests {

  @Test
  void testNullSetFieldNamingStrategy() {
    SpannerMappingContext context = new SpannerMappingContext();

    context.setFieldNamingStrategy(null);
    assertThat(context.getFieldNamingStrategy())
        .isEqualTo(PropertyNameFieldNamingStrategy.INSTANCE);
  }

  @Test
  void testSetFieldNamingStrategy() {
    SpannerMappingContext context = new SpannerMappingContext();
    FieldNamingStrategy strat = mock(FieldNamingStrategy.class);
    context.setFieldNamingStrategy(strat);
    assertThat(context.getFieldNamingStrategy()).isSameAs(strat);
  }

  @Test
  void testApplicationContextPassing() {
    SpannerPersistentEntityImpl mockEntity = mock(SpannerPersistentEntityImpl.class);
    SpannerMappingContext context = createSpannerMappingContextWith(mockEntity);
    ApplicationContext applicationContext = mock(ApplicationContext.class);
    context.setApplicationContext(applicationContext);

    context.createPersistentEntity(ClassTypeInformation.from(Object.class));

    verify(mockEntity, times(1)).setApplicationContext(applicationContext);
  }

  @Test
  void testApplicationContextIsNotSet() {
    SpannerPersistentEntityImpl mockEntity = mock(SpannerPersistentEntityImpl.class);
    SpannerMappingContext context = createSpannerMappingContextWith(mockEntity);

    context.createPersistentEntity(ClassTypeInformation.from(Object.class));

    verifyNoMoreInteractions(mockEntity);
  }

  @Test
  void testGetInvalidSpannerEntity() {
    SpannerPersistentEntityImpl mockEntity = mock(SpannerPersistentEntityImpl.class);
    SpannerMappingContext context = createSpannerMappingContextWith(mockEntity);

    assertThatThrownBy(() -> context.getPersistentEntityOrFail(Integer.class))
        .isInstanceOf(SpannerDataException.class);
  }

  private SpannerMappingContext createSpannerMappingContextWith(
      SpannerPersistentEntityImpl mockEntity) {
    return new SpannerMappingContext() {
      @Override
      @SuppressWarnings("unchecked")
      protected SpannerPersistentEntityImpl constructPersistentEntity(
          TypeInformation typeInformation) {
        return mockEntity;
      }
    };
  }
}
