package com.megatronix.paridhi.repository;

import com.megatronix.paridhi.model.PasswordResetToken;
import com.megatronix.paridhi.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository< PasswordResetToken, Long > {
	@EntityGraph ( attributePaths = {"user"} )
	Optional< PasswordResetToken > findByToken ( String token );

	List< PasswordResetToken > findAllByUser ( User user );

	Optional< PasswordResetToken > findFirstByUserOrderByCreatedAtDesc ( User user );

	void deleteByUser ( User user );
}
