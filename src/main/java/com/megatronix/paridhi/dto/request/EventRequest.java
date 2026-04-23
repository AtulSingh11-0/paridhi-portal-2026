package com.megatronix.paridhi.dto.request;

import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.EventType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record EventRequest(
	@NotNull ( message = "Domain cannot be null" )
	Domain domain,

	@NotBlank ( message = "Event name cannot be empty" )
	@Size ( min = 2, max = 100, message = "Event name must be between 2 and 100 characters" )
	String name,

	@NotNull ( message = "Event type cannot be null" )
	EventType type,

	@NotNull ( message = "Event date cannot be null" )
	Instant date,

	@NotBlank ( message = "Description cannot be empty" )
	@Size ( min = 10, message = "Description must be at least 10 characters" )
	String description,

	@NotBlank ( message = "Venue cannot be empty" )
	String venue,

	@NotNull ( message = "Coordinator details cannot be empty" )
	List< String > coordinatorDetails,

	String ruleBookUrl,

	@NotNull ( message = "Is team event cannot be null" )
	Boolean isTeamEvent,

	@NotNull ( message = "Min players cannot be null" )
	@Min ( value = 1, message = "Min players must be at least 1" )
	Integer minPlayers,

	@NotNull ( message = "Max players cannot be null" )
	@Min ( value = 1, message = "Max players must be at least 1" )
	Integer maxPlayers,

	@NotNull ( message = "Registration fee cannot be null" )
	@Min ( value = 0, message = "Registration fee must be non-negative" )
	BigDecimal registrationFee,

	@Min ( value = 0, message = "Prize pool must be non-negative" )
	BigDecimal prizePool
) {

}
