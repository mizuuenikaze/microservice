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
import org.springframework.security.core.userdetails.UserDetails;

import com.muk.services.exchange.ServiceConstants;

@Configuration
@EnableCaching
public class CachingConfig extends CachingConfigurerSupport {

	@Bean(destroyMethod = "close")
	public javax.cache.CacheManager ehCacheManager() {
		final CachingProvider provider = Caching.getCachingProvider();
		final javax.cache.CacheManager cacheManager = provider.getCacheManager();

		final CacheConfiguration<String, UserDetails> cacheConfiguration = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, UserDetails.class, ResourcePoolsBuilder.heap(10)).build();
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