/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic;

import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Vets;
import org.springframework.samples.petclinic.service.ClinicServiceImpl;
import org.springframework.samples.petclinic.service.OwnerLoaderWriter;
import org.springframework.samples.petclinic.web.VetsAtomView;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.xml.MarshallingView;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.ehcache.CacheManager;

import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;

/**
 * PetClinic Spring Boot Application.
 *
 * @author Fabien Lauf
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableCaching
public class PetClinicApplication {

	/**
	 * {@link BeanNameViewResolver} is used to resolve the Atom and Xml views. So, the
	 * following beans names must match the name of the JSP you want to override and the
	 * file extension will indicate which bean to use for resolving.
	 * @return the atom view
	 */
	@Bean(name = "vets/vetList.atom")
	public VetsAtomView vetsAtomView() {
		return new VetsAtomView();
	}

	@Bean(name = "vets/vetList.xml")
	public MarshallingView vetsXmlView() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(Vets.class);
		return new MarshallingView(marshaller);
	}

  @Autowired
  OwnerLoaderWriter ownerLoaderWriter;

  @Bean
  CacheManager ehcacheManager() {
    return newCacheManagerBuilder()
        .withCache("ownersSearch", newCacheConfigurationBuilder(String.class, Collection.class, heap(100))
            .withLoaderWriter(ownerLoaderWriter).add(WriteBehindConfigurationBuilder
                    .newUnBatchedWriteBehindConfiguration()
                    .concurrencyLevel(1))
            )
        .build(true);
  }

	public static void main(String[] args) throws Exception {
		SpringApplication.run(PetClinicApplication.class, args);
	}

}
