package com.megatronix.paridhi.repository;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.model.DomainPoster;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomainPosterRepository extends JpaRepository< DomainPoster, Long > {
	@EntityGraph ( attributePaths = {"createdBy", "updatedBy"} )
	Optional< DomainPoster > findByDomain ( Domain domain );
}
