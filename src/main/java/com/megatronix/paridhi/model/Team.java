package com.megatronix.paridhi.model;

import com.megatronix.paridhi.constant.Position;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table ( name = "teams" )
public class Team extends Auditable {

	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	@Column ( name = "team_id", updatable = false, nullable = false )
	private Long teamId;

	@Column ( name = "team_name", nullable = false )
	private String teamName;

	@Column ( name = "tid", unique = true, nullable = false, updatable = false )
	private String tid;

	@ManyToOne ( fetch = FetchType.LAZY )
	@JoinColumn ( name = "event_id", nullable = false )
	private Event event;

	@ElementCollection
	@CollectionTable (
		name = "team_contacts",
		joinColumns = @JoinColumn ( name = "team_id" )
	)
	@Column ( name = "contact_info" )
	@Builder.Default
	private List< Contact > contacts = new ArrayList<>();

	@ElementCollection
	@CollectionTable (
		name = "team_gids",
		joinColumns = @JoinColumn ( name = "team_id" )
	)
	@Column ( name = "gids" )
	@Builder.Default
	private java.util.Set< String > gidList = new java.util.HashSet<>();

	@Builder.Default
	@Column ( name = "has_paid", nullable = false )
	private boolean hasPaid = false;

	@Builder.Default
	@Column ( name = "has_played", nullable = false )
	private boolean hasPlayed = false;

	@Builder.Default
	@Column ( name = "has_qualified", nullable = false )
	private boolean hasQualified = false;

	@Enumerated ( EnumType.STRING )
	@Column ( name = "position" )
	@Builder.Default
	private Position position = Position.NONE;

	@Data
	@Embeddable
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Contact {
		private String name;
		private String phoneNumber;
	}
}