package com.megatronix.paridhi.repository;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.model.EventCombo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EventComboRepository extends JpaRepository< EventCombo, Long > {
	@NonNull
	@Override
	@EntityGraph ( attributePaths = {"events"} )
	List< EventCombo > findAll ();

	@EntityGraph ( attributePaths = {"events"} )
	List< EventCombo > findByDomain ( Domain domain );

	@EntityGraph ( attributePaths = {"events"} )
	List< EventCombo > findByIsEventComboRegistrationOpen ( boolean isEventComboRegistrationOpen );

	@EntityGraph ( attributePaths = {"events"} )
	Set< EventCombo > findByEvents_EventId ( Long eventId );
}
