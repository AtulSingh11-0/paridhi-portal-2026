package com.megatronix.paridhi.security;

import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
	private static final Logger log = LoggerFactory.getLogger(JwtService.class);
	private final BlacklistedTokenRepository blacklistedTokenRepository;

	@Value ( "${jwt.secret}" )
	private String secretKey;

	@Value ( "${jwt.expiration}" )
	private long expirationTime;

	public JwtService ( BlacklistedTokenRepository blacklistedTokenRepository ) {
		this.blacklistedTokenRepository = blacklistedTokenRepository;
	}

	public String extractUsername ( String token ) {
		return extractClaim(token, Claims::getSubject);
	}

	public < T > T extractClaim ( String token, Function< Claims, T > claimsResolver ) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateToken ( User user ) {
		List<String> roles = user.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.toList();

		Map< String, Object > claims = Map.of(
			"userId", user.getUserId(),
			"name", user.getName(),
			"isProfileCreated", user.isProfileCreated(),
			"isVerified", user.isVerified(),
			"isLoggedIn", user.isLoggedIn(),
			"roles", roles
		);
		return generateToken(claims, user);
	}

	public String generateToken ( Map< String, Object > extraClaims, UserDetails userDetails ) {
		Instant now = Instant.now();
		return Jwts
			.builder()
			.claims(extraClaims)
			.subject(userDetails.getUsername())
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusMillis(expirationTime)))
			.signWith(getSigninKey())
			.compact();
	}

	public boolean isTokenValid ( String token, UserDetails userDetails ) {
		final String username = extractUsername(token);

		return (
			username.equals(userDetails.getUsername())
				&& ! isTokenExpired(token)
				&& ! isTokenBlacklisted(token)
		);
	}

	public boolean isTokenBlacklisted ( String token ) {
		return blacklistedTokenRepository.existsByToken(token);
	}

	public Instant getExpirationDateFromToken ( String token ) {
		return extractExpiration(token).toInstant();
	}

	public boolean isTokenExpired ( String token ) {
		try {
			return extractExpiration(token).before(Date.from(Instant.now()));
		} catch ( Exception e ) {
			return true;
		}
	}

	private Date extractExpiration ( String token ) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims ( String token ) {
		try {
			return Jwts
				.parser()
				.verifyWith(getSigninKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch ( ExpiredJwtException e ) {
			log.warn("Token is expired, extracting claims from the exception payload.");
			return e.getClaims();
		}
	}

	private SecretKey getSigninKey () {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}