package com.megatronix.paridhi.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static java.time.Duration.ofSeconds;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration
@EnableCaching
public class RedisConfig {
	@Value("${spring.data.redis.host:localhost}")
	private String redisHost;

	@Value("${spring.data.redis.port:6379}")
	private int redisPort;

	@Value("${spring.data.redis.password:12345}")
	private String redisPassword;

	@Value("${spring.cache.redis.time-to-live:3600000}")
	private long defaultTtlSeconds;

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
				.serializeKeysWith(
						fromSerializer(new StringRedisSerializer()))
				.entryTtl(ofSeconds(defaultTtlSeconds))
				.disableCachingNullValues();

		return RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(defaultConfig)
				.withCacheConfiguration("events", defaultConfig)
				.withCacheConfiguration("users", defaultConfig)
				.withCacheConfiguration("teams", defaultConfig)
				.build();
	}

	@Bean
	public RedisClient redisClient() {
		RedisURI.Builder builder = RedisURI.builder()
				.withHost(redisHost)
				.withPort(redisPort);

		if (redisPassword != null && !redisPassword.isEmpty()) {
			builder.withPassword(redisPassword.toCharArray());
		}

		return RedisClient.create(builder.build());
	}

	@Bean
	public ProxyManager<byte[]> proxyManager(RedisClient redisClient) {
		return Bucket4jLettuce.casBasedBuilder(redisClient)
				.expirationAfterWrite(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(ofSeconds(10)))
				.build();
	}
}
