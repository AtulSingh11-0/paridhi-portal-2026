package com.megatronix.paridhi.repository;

import com.megatronix.paridhi.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository< BlacklistedToken, Long > {
	boolean existsByToken ( String token );

	List< BlacklistedToken > findByExpiresAtBefore ( Instant now );

	@Modifying
	@Query ( "DELETE FROM BlacklistedToken bt WHERE bt.expiresAt < ?1" )
	void deleteAllExpiredTokens ( Instant now );
}
