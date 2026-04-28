package com.megatronix.paridhi.service;

import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static java.time.Duration.ofHours;
import static java.time.Duration.ofMinutes;

@Service
public class RateLimitingService {
	private static final Logger log = LoggerFactory.getLogger(RateLimitingService.class);
	private final ProxyManager< byte[] > proxyManager;
	private final StringRedisTemplate stringRedisTemplate;

	public RateLimitingService ( ProxyManager< byte[] > proxyManager, StringRedisTemplate stringRedisTemplate ) {
		this.proxyManager = proxyManager;
		this.stringRedisTemplate = stringRedisTemplate;
	}

	public boolean isBlocked ( String clientKey ) {
		return Boolean.TRUE.equals(stringRedisTemplate.hasKey("penalty:" + clientKey));
	}

	public void applyProgressivePenalty ( String clientKey ) {
		String strikeKey = "strikes:" + clientKey;
		String penaltyKey = "penalty:" + clientKey;

		// increment strikes, if doesnt exist then start from 1
		Long strikes = stringRedisTemplate.opsForValue().increment(strikeKey);
		if ( strikes == null ) strikes = 1L;

		// ensure the strike record resets after 48 hours of good behavior
		stringRedisTemplate.expire(strikeKey, ofHours(48));

		Duration penaltyDuration;

		// progressing ban logic
		if ( strikes == 1 ) penaltyDuration = ofMinutes(15);
		else if ( strikes == 2 ) penaltyDuration = ofMinutes(30);
		else if ( strikes == 3 ) penaltyDuration = ofHours(1);
		else if ( strikes == 4 ) penaltyDuration = ofHours(6);
		else penaltyDuration = ofHours(72);

		log.warn("Applying Strike {} to client {}. Banned for {} minutes.", strikes, clientKey, penaltyDuration.toMinutes());
		stringRedisTemplate.opsForValue().set(penaltyKey, "true", penaltyDuration);
	}

	/*public void applyPenalty ( String ipAddress ) {
		stringRedisTemplate.opsForValue().set("penalty:" + ipAddress, "true", ofMinutes(PENALTY_DURATION_MINUTES));
	}*/

	public Bucket resolveBucket ( String clientKey ) {
		// dynamic rate limiting based on trust tier
		int capacity;
		if ( ( clientKey.startsWith("user:") ) ) {
			capacity = 20; // trusted users get 20 requests per minute
		} else if ( clientKey.startsWith("visitor:") ) {
			capacity = 10; // anonymous visitors get 10 requests per minute
		} else {
			capacity = 5; // default for unknown clients
		}

		BucketConfiguration configuration = BucketConfiguration.builder()
			.addLimit(
				BandwidthBuilder.builder()
					.capacity(capacity)
					.refillGreedy(capacity, ofMinutes(1))
					.build())
			.build();

		return proxyManager.getProxy(clientKey.getBytes(), () -> configuration);
	}
}
