package com.megatronix.paridhi.model;

import com.megatronix.paridhi.constant.Domain;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static com.megatronix.paridhi.constant.AppConstant.Storage.DEFAULT_EVENT_POSTER_FILE_ID;
import static com.megatronix.paridhi.constant.AppConstant.Storage.DEFAULT_EVENT_POSTER_URL;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table ( name = "event_combos" )
public class EventCombo extends Auditable {

	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	@Column ( name = "event_combo_id", updatable = false, nullable = false )
	private Long eventComboId;

	@Enumerated ( EnumType.STRING )
	@Column ( name = "domain", nullable = false )
	private Domain domain;

	@Column ( name = "event_combo_name", nullable = false )
	private String eventComboName;

	@Column ( name = "event_combo_description", length = 2000, nullable = false )
	private String eventComboDescription;

	@ManyToMany (
		cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
		}
	)
	@JoinTable (
		name = "event_combo_mappings",
		joinColumns = @JoinColumn ( name = "event_combo_id" ),
		inverseJoinColumns = @JoinColumn ( name = "event_id" )
	)
	@Builder.Default
	private Set< Event > events = new HashSet<>();

	@Column ( name = "event_combo_poster_url", nullable = false )
	@Builder.Default
	private String eventComboPosterUrl = DEFAULT_EVENT_POSTER_URL;

	@Column ( name = "event_combo_poster_file_id", nullable = false )
	@Builder.Default
	private String eventComboPosterFileId = DEFAULT_EVENT_POSTER_FILE_ID;

	@Column ( name = "event_combo_registration_fee", nullable = false )
	private BigDecimal eventComboRegistrationFee;

	@Column ( name = "is_event_combo_registration_open", nullable = false )
	@Builder.Default
	private boolean isEventComboRegistrationOpen = false;

	// ==========================================
	// JPA HELPER METHODS for Many-To-Many
	// ==========================================

	public void addEvent ( Event event ) {
		this.events.add(event);
	}

	public void removeEvent ( Event event ) {
		this.events.remove(event);
	}
}
