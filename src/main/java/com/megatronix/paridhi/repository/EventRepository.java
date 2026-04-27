package com.megatronix.paridhi.repository;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.EventType;
import com.megatronix.paridhi.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository< Event, Long > {

	List< Event > findByDomain ( Domain domain );

	List< Event > findByIsEventRegistrationOpen ( boolean isEventRegistrationOpen );

	List< Event > findByEventType ( EventType eventType );
}
