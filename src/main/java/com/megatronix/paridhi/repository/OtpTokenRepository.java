package com.megatronix.paridhi.repository;

import com.megatronix.paridhi.model.OtpToken;
import com.megatronix.paridhi.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository< OtpToken, Long > {
	Optional< OtpToken > findFirstByUserOrderByCreatedAtDesc ( User user );

	@EntityGraph ( attributePaths = {"user"} )
	Optional< OtpToken > findByOtp ( String otp );

	List< OtpToken > findAllByUser ( User user );

	boolean existsByUserAndIsUsed ( User user, boolean isUsed );

	@Modifying
	@Query ( "DELETE FROM OtpToken o WHERE o.expiryDate < ?1" )
	void deleteAllExpiredTokens ( Instant now );
}
