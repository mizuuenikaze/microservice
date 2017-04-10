/*******************************************************************************
 * Copyright (C)  2017  mizuuenikaze inc
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.muk.services.configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.muk.services.exchange.ServiceConstants;

@Configuration
@EnableCaching
public class CachingConfig extends CachingConfigurerSupport {

	@Bean(destroyMethod = "close")
	public javax.cache.CacheManager ehCacheManager() {
		final CachingProvider provider = Caching.getCachingProvider();
		final javax.cache.CacheManager cacheManager = provider.getCacheManager();

		final CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10)).build();
		cacheManager.createCache(ServiceConstants.CacheNames.userCache,
				Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration));

		return cacheManager;
	}

	@Bean
	@Override
	public CacheManager cacheManager() {
		return new JCacheCacheManager(ehCacheManager());
	}
}