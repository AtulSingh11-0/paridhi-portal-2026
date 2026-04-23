package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.EmailType;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.constant.Position;
import com.megatronix.paridhi.dto.request.EventComboTeamRequest;
import com.megatronix.paridhi.dto.request.TeamRequest;
import com.megatronix.paridhi.dto.response.TeamAdminResponse;
import com.megatronix.paridhi.dto.response.TeamPublicResponse;
import com.megatronix.paridhi.exception.*;
import com.megatronix.paridhi.model.Event;
import com.megatronix.paridhi.model.Team;
import com.megatronix.paridhi.model.Team.Contact;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.EventComboRepository;
import com.megatronix.paridhi.repository.EventRepository;
import com.megatronix.paridhi.repository.MRDRepository;
import com.megatronix.paridhi.repository.TeamRepository;
import com.megatronix.paridhi.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.megatronix.paridhi.constant.EmailType.*;

@Service
public class TeamService {
	private static final Logger log = LoggerFactory.getLogger(TeamService.class);
	private final EmailService emailService;
	private final MRDRepository mrdRepository;
	private final TeamRepository teamRepository;
	private final EventRepository eventRepository;
	private final EventComboRepository eventComboRepository;

	public TeamService ( EmailService emailService, MRDRepository mrdRepository, TeamRepository teamRepository, EventRepository eventRepository, EventComboRepository eventComboRepository ) {
		this.emailService = emailService;
		this.mrdRepository = mrdRepository;
		this.teamRepository = teamRepository;
		this.eventRepository = eventRepository;
		this.eventComboRepository = eventComboRepository;
	}

	/*
	 * public methods - doesn't require authentication
	 */

	@Transactional ( readOnly = true )
	public List< TeamPublicResponse > getTeamsByUser ( String email ) {
		log.info("Fetching teams for user with email: {}", email);

		// get the user's GIDs from MRD
		var userGids = mrdRepository.findGidListByUserEmail(email);

		if ( userGids.isEmpty() ) {
			LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.TEAM, User.builder().email(email).build(), String.format(MessageConstant.ErrorTemplate.NOT_FOUND, "GIDs for user with email: " + email), null);
			throw new UserNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, "GIDs"));
		}

		// return a List of TeamPublicResponse builder object
		return teamRepository.findByGidListWithContacts(userGids).stream().map(TeamPublicResponse::fromTeam).toList();
	}

	@Transactional ( readOnly = true )
	public TeamPublicResponse getTeamByTid ( String tid, String email ) {
		log.info("Fetching admin team with TID: {}", tid);
		var team = fetchTeamSafely(tid, email, null);
		return TeamPublicResponse.fromTeam(team);
	}

	@Transactional ( readOnly = true )
	public List< TeamPublicResponse > getTeamsByGid ( String gid, String email ) {
		log.info("Fetching teams by GID: {}", gid);
		verifyGidOwnership(gid, email, null);
		return teamRepository.findAll().stream().filter(team -> team.getGidList().contains(gid)).map(TeamPublicResponse::fromTeam).toList();
	}

	@Transactional
	public TeamPublicResponse registerTeam ( TeamRequest request, User user ) {
		log.info("Team Registration request: {}", request);

		var event = eventRepository.findById(request.eventId()).orElseThrow(() -> {
			LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.EVENT, user, String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT + " with ID: " + request.eventId()), null);
			return new EventNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT));
		});

		List< Contact > contactList = request.contacts().stream().map(contact -> new Contact(contact.name(), contact.phoneNumber())).toList();

		Team savedTeam = processAndSaveTeam(request.teamName(), event, request.gidList(), contactList);

		return TeamPublicResponse.fromTeam(savedTeam);
	}

	@Transactional
	public List< TeamPublicResponse > registerTeamForEventCombo ( EventComboTeamRequest request, User user ) {
		log.info("Team registration for combo request: {}", request);

		// find the combo
		var combo = eventComboRepository.findById(request.comboId()).orElseThrow(() -> {
			LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.EVENT_COMBO, user, String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT_COMBO + " with ID: " + request.comboId()), null);
			return new ComboNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.COMBO));
		});

		// check if registration for the combo is open or not
		if ( ! combo.isEventComboRegistrationOpen() ) {
			LoggingUtil.logError(log, MessageConstant.Operation.CREATE, AppConstant.EVENT_COMBO, user, String.format(MessageConstant.ErrorTemplate.REGISTRATION_CLOSED, AppConstant.COMBO, combo.getEventComboName()), null);
			throw new TeamRegistrationException("Registration is closed for combo: " + combo.getEventComboName());
		}

		// validate that we have GID mappings for all events in the combo
		Set< Long > comboEventIds = combo.getEvents().stream().map(Event::getEventId).collect(Collectors.toSet());

		if ( ! request.eventGidMap().keySet().containsAll(comboEventIds) ) {
			LoggingUtil.logError(log, MessageConstant.Operation.CREATE, AppConstant.EVENT_COMBO, user, String.format(MessageConstant.ErrorTemplate.MISSING_GID_LIST, AppConstant.COMBO, combo.getEventComboName()), null);
			throw new TeamRegistrationException("You must provide GID list for all events in the combo: " + combo.getEventComboName());
		}

		List< Contact > contactList = request.contacts().stream().map(contact -> new Contact(contact.name(), contact.phoneNumber())).toList();

		// create a list to hold all the team responses
		List< TeamPublicResponse > teamResponses = new ArrayList<>();

		// for each event in the combo, create a team
		for ( Event event : combo.getEvents() ) {
			List< String > eventGids = request.eventGidMap().get(event.getEventId());
			Team savedTeam = processAndSaveTeam(request.teamName(), event, eventGids, contactList);
			teamResponses.add(TeamPublicResponse.fromTeam(savedTeam));
		}

		if ( teamResponses.isEmpty() ) {
			LoggingUtil.logError(log, MessageConstant.Operation.CREATE, AppConstant.EVENT_COMBO, user, "Failed to register teams for combo: " + combo.getEventComboName(), null);
			throw new TeamRegistrationException("No teams registered for combo: " + combo.getEventComboName());
		}

		return teamResponses;
	}

	/*
	 * protected methods - requires authentication
	 */

	@Transactional ( readOnly = true )
	public List< TeamAdminResponse > getTeamsByEvent ( Long eventId, User user ) {
		log.info("Fetching teams for event with ID: {}", eventId);

		// find the event
		var event = eventRepository.findById(eventId).orElseThrow(() -> {
			LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.EVENT, user, String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT), null);
			return new EventNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT));
		});

		// return a List of TeamPublicResponse builder object
		return teamRepository.findByEventWithContacts(event).stream().map(TeamAdminResponse::fromTeam).toList();
	}

	@Transactional ( readOnly = true )
	public List< TeamAdminResponse > getAdminTeamsByUserEmail ( String email, User user ) {
		log.info("Fetching teams for admin user with email: {}", email);

		// get the user's GIDs from MRD
		var userGids = mrdRepository.findGidListByUserEmail(email);

		if ( userGids.isEmpty() ) {
			LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.TEAM, user, String.format(MessageConstant.ErrorTemplate.NOT_FOUND, "GIDs for user with email: " + email), null);
			throw new UserNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, "GIDs"));
		}

		// return a List of TeamAdminResponse builder object
		return teamRepository.findByGidListWithContacts(userGids).stream().map(TeamAdminResponse::fromTeam).toList();
	}

	@Transactional ( readOnly = true )
	public TeamAdminResponse getAdminTeamByTid ( String tid, User user ) {
		log.info("Fetching team with TID: {}", tid);
		var team = fetchTeamSafely(tid, null, user);
		return TeamAdminResponse.fromTeam(team);
	}

	@Transactional ( readOnly = true )
	public List< TeamAdminResponse > getAdminTeamsByGid ( String gid, User user ) {
		log.info("Fetching admin teams by GID: {}", gid);
		verifyGidOwnership(gid, null, user);
		return teamRepository.findAll().stream().filter(team -> team.getGidList().contains(gid)).map(TeamAdminResponse::fromTeam).toList();
	}

	@Transactional
	public TeamAdminResponse updatePaymentStatus ( String tid, User user ) {
		Team team = fetchTeamSafely(tid, null, user);
		team.setHasPaid(! team.isHasPaid());
		return TeamAdminResponse.fromTeam(team);
	}

	@Transactional
	public TeamAdminResponse updatePlayedStatus ( String tid, User user ) {
		Team team = fetchTeamSafely(tid, null, user);

		// check if team has paid
		if ( ! team.isHasPaid() ) {
			log.error("Team must have paid to update played status");
			throw new IllegalArgumentException("Team must have paid to update played status");
		}

		team.setHasPlayed(! team.isHasPlayed());
		return TeamAdminResponse.fromTeam(team);
	}

	@Transactional
	public TeamAdminResponse updateQualifiedStatus ( String tid, User user ) {
		Team team = fetchTeamSafely(tid, null, user);

		// check if team has played
		if ( ! team.isHasPlayed() ) {
			log.error("Team must have played to update qualified status");
			throw new IllegalArgumentException("Team must have played to update qualified status");
		}

		team.setHasQualified(! team.isHasQualified());
		if ( team.isHasQualified() ) {
			triggerEmailNotification(team, QUALIFICATION);
		}
		return TeamAdminResponse.fromTeam(team);
	}

	@Transactional
	public TeamAdminResponse updatePosition ( String tid, Position position, User user ) {
		Team team = fetchTeamSafely(tid, null, user);

		if ( ! team.isHasQualified() ) {
			log.error("Team must be qualified to update position");
			throw new IllegalArgumentException("Team must be qualified to update position");
		}

		Position previousPosition = team.getPosition();
		team.setPosition(position != null ? position : Position.NONE);

		boolean isWinningPosition = position != null && position != Position.NONE;
		if ( isWinningPosition && position != previousPosition ) {
			triggerEmailNotification(team, POSITION);
		}
		return TeamAdminResponse.fromTeam(team);
	}

	/*
	 * private methods - isUsed internally only
	 */

	private Team processAndSaveTeam ( String teamName, Event event, List< String > gidList, List< Contact > contactList ) {
		if ( ! event.isEventRegistrationOpen() ) {
			throw new TeamRegistrationException("Registration is closed for event: " + event.getEventName());
		}

		if ( teamRepository.existsByTeamNameAndEvent(teamName, event) ) {
			throw new TeamRegistrationException(String.format("Team name '%s' already exists for event: %s", teamName, event.getEventName()));
		}

		int teamSize = gidList.size();
		if ( teamSize < event.getEventMinPlayers() || teamSize > event.getEventMaxPlayers() ) {
			throw new TeamRegistrationException(String.format("Team size '%d' must be between: %d and %d for event: %s", teamSize, event.getEventMinPlayers(), event.getEventMaxPlayers(), event.getEventName()));
		}

		validateGids(gidList);

		Set< String > alreadyRegisteredGids = teamRepository.findByEvent(event).stream().flatMap(team -> team.getGidList().stream()).collect(Collectors.toSet());

		for ( String gid : gidList ) {
			if ( alreadyRegisteredGids.contains(gid) ) {
				throw new TeamRegistrationException(String.format("GID '%s' is already registered for event: %s", gid, event.getEventName()));
			}
		}

		var team = Team.builder().teamName(teamName).event(event).contacts(contactList).gidList(gidList).build();

		Team savedTeam = teamRepository.save(team);
		log.info("Created team {} for event {}", savedTeam.getTid(), event.getEventName());

		triggerEmailNotification(savedTeam, REGISTRATION);

		return savedTeam;
	}

	private void validateGids ( List< String > gidList ) {
		// validate that all GIDs exist in MRD
		for ( String gid : gidList ) {
			if ( mrdRepository.existsByGid(gid) ) {
				log.error("Invalid GID: {}", gid);
				throw new TeamRegistrationException("Invalid GID: " + gid);
			}
		}

		// check for duplicate GIDs in the request
		Set< String > uniqueGids = new HashSet<>(gidList);
		if ( uniqueGids.size() != gidList.size() ) {
			log.error("Duplicate GIDs found in the request");
			throw new TeamRegistrationException("Duplicate GIDs are not allowed");
		}
	}

	private Team fetchTeamSafely ( String tid, String email, User user ) {
		return teamRepository.findByTid(tid).orElseThrow(() -> {
			LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.TEAM, email != null ? User.builder().email(email).build() : user, String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.TEAM + " with TID: " + tid), null);
			return new TeamNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.TEAM + " with TID: " + tid));
		});
	}

	private void triggerEmailNotification ( Team team, EmailType type ) {
		// fetch emails of all the team members registered with GID
		List< String > teamMemberEmails = mrdRepository.findUserEmailListByGidList(team.getGidList());
		String[] emailArray = teamMemberEmails.toArray(new String[0]);

		type.sendEmail(emailService, emailArray, team);
	}

	private void verifyGidOwnership ( String gid, String email, User user ) {
		if ( mrdRepository.existsByGid(gid) ) {
			LoggingUtil.logError(log, MessageConstant.Operation.READ, AppConstant.TEAM, email != null ? User.builder().email(email).build() : user, String.format(MessageConstant.ErrorTemplate.NOT_FOUND, "GID: " + gid), null);
			throw new TeamNotFoundException("GID not found: " + gid);
		}
	}
}
