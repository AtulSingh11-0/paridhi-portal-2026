package com.megatronix.paridhi.model;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.EventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.type.SqlTypes.JSON;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table ( name = "events" )
public class Event extends Auditable {

	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	@Column ( name = "event_id", updatable = false, nullable = false )
	private Long eventId;

	@Enumerated ( EnumType.STRING )
	@Column ( name = "domain", nullable = false )
	private Domain domain;

	@Column ( name = "event_name", nullable = false )
	private String eventName;

	@Enumerated ( EnumType.STRING )
	@Column ( name = "event_type", nullable = false )
	private EventType eventType;

	@Column ( name = "event_date", nullable = false )
	private Instant eventDate;

	@Column ( name = "event_description", length = 2000, nullable = false )
	private String eventDescription;

	@Column ( name = "event_venue", nullable = false )
	private String eventVenue;

	@JdbcTypeCode ( JSON )
	@Column ( name = "event_coordinator_details", columnDefinition = "json", nullable = false )
	@Builder.Default
	private List< String > eventCoordinatorDetails = new ArrayList<>();

	@Column ( name = "event_poster_url", nullable = false )
	@Builder.Default
	private String eventPosterUrl = AppConstant.DEFAULT_EVENT_POSTER_URL; // ImageKit URL

	@Column ( name = "event_poster_file_id", nullable = false )
	@Builder.Default
	private String eventPosterFileId = AppConstant.DEFAULT_EVENT_POSTER_FILE_ID; // ImageKit File ID for deletion

	@Column ( name = "event_rule_book_url" )
	private String eventRuleBookUrl; // PDF URL

	@Column ( name = "is_team_event", nullable = false )
	private boolean isTeamEvent;

	@Column ( name = "event_min_players", nullable = false )
	private Integer eventMinPlayers;

	@Column ( name = "event_max_players", nullable = false )
	private Integer eventMaxPlayers;

	@Column ( name = "event_registration_fee", nullable = false )
	private BigDecimal eventRegistrationFee;

	@Column ( name = "is_event_registration_open", nullable = false )
	@Builder.Default
	private boolean isEventRegistrationOpen = false;

	@PrePersist
	@PreUpdate
	private void validatePlayers () {
		if ( ! this.isTeamEvent ) {
			this.eventMaxPlayers = 1;
			this.eventMinPlayers = 1;
		}
	}
}
