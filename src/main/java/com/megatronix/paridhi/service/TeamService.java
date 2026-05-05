package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.EmailType;
import com.megatronix.paridhi.constant.Position;
import com.megatronix.paridhi.dto.request.EventComboTeamRequest;
import com.megatronix.paridhi.dto.request.TeamRequest;
import com.megatronix.paridhi.dto.response.TeamAdminResponse;
import com.megatronix.paridhi.dto.response.TeamPublicResponse;
import com.megatronix.paridhi.dto.response.UserRegisteredTeamsAdminResponse;
import com.megatronix.paridhi.dto.response.UserRegisteredTeamsPublicResponse;
import com.megatronix.paridhi.exception.*;
import com.megatronix.paridhi.model.Event;
import com.megatronix.paridhi.model.EventCombo;
import com.megatronix.paridhi.model.Team;
import com.megatronix.paridhi.model.Team.Contact;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.EventComboRepository;
import com.megatronix.paridhi.repository.EventRepository;
import com.megatronix.paridhi.repository.MRDRepository;
import com.megatronix.paridhi.repository.TeamRepository;
import com.megatronix.paridhi.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.megatronix.paridhi.constant.AppConstant.Resource.*;
import static com.megatronix.paridhi.constant.EmailType.*;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.*;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.*;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.*;
import static com.megatronix.paridhi.util.AccessControlUtil.requireAdminAccess;
import static com.megatronix.paridhi.util.LoggingUtil.logError;
import static com.megatronix.paridhi.util.LoggingUtil.logOperation;
import static java.lang.String.format;

@Service
public class TeamService {

	private static final Logger log = LoggerFactory.getLogger(TeamService.class);
	private static final String TEAM_SEQ_KEY = "paridhi:team:sequence:2026";

	private final EmailService emailService;
	private final MRDRepository mrdRepository;
	private final TeamRepository teamRepository;
	private final EventRepository eventRepository;
	private final EventComboRepository eventComboRepository;
	private final StringRedisTemplate stringRedisTemplate;

	public TeamService (
		EmailService emailService,
		MRDRepository mrdRepository,
		TeamRepository teamRepository,
		EventRepository eventRepository,
		StringRedisTemplate stringRedisTemplate,
		EventComboRepository eventComboRepository
	) {
		this.emailService = emailService;
		this.mrdRepository = mrdRepository;
		this.teamRepository = teamRepository;
		this.eventRepository = eventRepository;
		this.stringRedisTemplate = stringRedisTemplate;
		this.eventComboRepository = eventComboRepository;
	}

	/*
	 * public methods — doesn't require authentication
	 */

	@Transactional ( readOnly = true )
	public List< UserRegisteredTeamsPublicResponse > getTeamsByUser ( String email ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching all teams for user: {}", email);
		}

		List< String > userGids = mrdRepository.findGidListByUserEmail(email);

		if ( userGids.isEmpty() ) {
			logError(log, READ, TEAM, null, format(TEAMS_NOT_FOUND_FOR_USER, email), null);
			return List.of();
		}

		List< Team > teams = teamRepository.findByGidListWithContacts(userGids);

		Map< String, List< TeamPublicResponse > > gidTeamMap = new TreeMap<>();

		for ( String gid : userGids ) {
			gidTeamMap.put(gid, new ArrayList<>());
		}

		for ( Team team : teams ) {
			for ( String gid : team.getGidList() ) {
				if ( gidTeamMap.containsKey(gid) ) {
					gidTeamMap.get(gid).add(TeamPublicResponse.fromTeam(team));
				}
			}
		}

		logOperation(log, READ, TEAM, null, format(TEAMS_FETCHED, teams.size(), "all user GIDs", email, "N/A"));

		return gidTeamMap.entrySet().stream()
			.map(entry -> UserRegisteredTeamsPublicResponse.of(entry.getKey(), entry.getValue()))
			.toList();
	}

	@Transactional ( readOnly = true )
	public TeamPublicResponse getTeamByTid ( String tid, String email ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching public team with TID: {}", tid);
		}

		// step-1: fetch team safely
		var team = fetchTeamSafely(tid, email, null);

		// step-2: audit log the retrieval
		logOperation(log, READ, TEAM, null, format(TEAM_FETCHED, tid, email, "N/A"));

		return TeamPublicResponse.fromTeam(team);
	}

	@Transactional ( readOnly = true )
	public List< TeamPublicResponse > getTeamsByGid ( String gid, String email ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching teams by GID: {}", gid);
		}

		// step-1: verify the GID belongs to a valid user
		verifyGidOwnership(gid, email, null);

		// step-2: filter teams by GID
		var teams = teamRepository.findAll().stream()
			.filter(team -> team.getGidList().contains(gid))
			.toList();

		// step-3: audit log the retrieval
		logOperation(log, READ, TEAM, null, format(TEAMS_FETCHED, teams.size(), gid, email, "N/A"));

		return teams.stream()
			.map(TeamPublicResponse::fromTeam)
			.toList();
	}

	@Transactional
	public TeamPublicResponse registerTeam ( TeamRequest request, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to register team '{}' for event ID: {}", request.teamName(), request.eventId());
		}

		// step-1: fetch the event
		var event = fetchEventById(request.eventId(), user);

		// step-2: convert contacts to entity format
		List< Contact > contactList = request.contacts().stream()
			.map(contact -> new Contact(contact.name(), contact.phoneNumber()))
			.toList();

		// step-3: process rules and persist
		Team savedTeam = processAndSaveTeam(request.teamName(), event, request.gidList(), contactList, user);

		// step-4: audit log successful registration
		logOperation(log, TEAM_REGISTER, TEAM, user,
			format(TEAM_REGISTERED, savedTeam.getTeamName(), savedTeam.getTid(), event.getEventName(), user.getEmail(), user.getUserId()));

		return TeamPublicResponse.fromTeam(savedTeam);
	}

	@Transactional
	public List< TeamPublicResponse > registerTeamForEventCombo ( EventComboTeamRequest request, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to register team '{}' for combo ID: {}", request.teamName(), request.comboId());
		}

		// step-1: fetch the combo
		var combo = fetchComboById(request.comboId(), user);

		// step-2: guard — check combo registration status
		if ( ! combo.isEventComboRegistrationOpen() ) {
			logError(log, TEAM_COMBO_REGISTER, AppConstant.Resource.COMBO, user, format(TEAM_REGISTRATION_CLOSED, combo.getEventComboName()), null);
			throw new TeamRegistrationException(format(REGISTRATION_CLOSED, COMBO, combo.getEventComboName()));
		}

		// step-3: guard — ensure GIDs are provided for all events in the combo
		Set< Long > comboEventIds = combo.getEvents().stream()
			.map(Event::getEventId)
			.collect(Collectors.toSet());

		if ( ! request.eventGidMap().keySet().containsAll(comboEventIds) ) {
			logError(log, TEAM_COMBO_REGISTER, AppConstant.Resource.COMBO, user, format(TEAM_MISSING_COMBO_GIDS, combo.getEventComboName()), null);
			throw new TeamRegistrationException(format(MISSING_GID_LIST, COMBO, combo.getEventComboName()));
		}

		// step-4: prepare contacts
		List< Contact > contactList = request.contacts().stream()
			.map(contact -> new Contact(contact.name(), contact.phoneNumber()))
			.toList();

		List< TeamPublicResponse > teamResponses = new ArrayList<>();

		// step-5: register a team for each event in the combo
		for ( Event event : combo.getEvents() ) {
			List< String > eventGids = request.eventGidMap().get(event.getEventId());
			Team savedTeam = processAndSaveTeam(request.teamName(), event, eventGids, contactList, user);
			teamResponses.add(TeamPublicResponse.fromTeam(savedTeam));
		}

		if ( teamResponses.isEmpty() ) {
			logError(log, TEAM_COMBO_REGISTER, AppConstant.Resource.COMBO, user, "Failed to register teams for combo: " + combo.getEventComboName(), null);
			throw new TeamRegistrationException("No teams registered for combo: " + combo.getEventComboName());
		}

		// step-6: audit log successful combo registration
		logOperation(log, TEAM_COMBO_REGISTER, TEAM, user,
			format(TEAM_REGISTERED_COMBO, request.teamName(), "MULTIPLE", combo.getEventComboName(), user.getEmail(), user.getUserId()));

		return teamResponses;
	}

	/*
	 * protected methods — requires authentication
	 */

	@Transactional ( readOnly = true )
	public List< TeamAdminResponse > getTeamsByEvent ( Long eventId, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching teams for event ID: {}", eventId);
		}

		// step-1: security — only ADMIN/SUPERADMIN may access
		requireAdminAccess(log, user, READ, TEAM);

		// step-2: fetch the event
		var event = fetchEventById(eventId, user);

		// step-3: retrieve and map teams
		var teams = teamRepository.findByEventWithContacts(event);

		logOperation(log, READ, TEAM, user, format(TEAMS_FETCHED, teams.size(), event.getEventName(), user.getEmail(), user.getUserId()));

		return teams.stream()
			.map(TeamAdminResponse::fromTeam)
			.toList();
	}

	@Transactional ( readOnly = true )
	public List< UserRegisteredTeamsAdminResponse > getAdminTeamsByUserEmail ( String email, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching teams for admin user with email: {}", email);
		}

		// step-1: security — only ADMIN/SUPERADMIN may access
		requireAdminAccess(log, user, READ, TEAM);

		// step-2: get user's GIDs
		var userGids = mrdRepository.findGidListByUserEmail(email);

		if ( userGids.isEmpty() ) {
			logError(log, READ, TEAM, user, format(TEAMS_NOT_FOUND_FOR_USER, email), null);
			throw new UserNotFoundException(format(NOT_FOUND, "GIDs"));
		}

		// step-3: fetch teams mapped to those GIDs
		var teams = teamRepository.findByGidListWithContacts(userGids);

		Map< String, List< TeamAdminResponse > > gidTeamMap = new java.util.TreeMap<>();

		for ( String gid : userGids ) {
			gidTeamMap.put(gid, new ArrayList<>());
		}

		for ( Team team : teams ) {
			for ( String gid : team.getGidList() ) {
				if ( gidTeamMap.containsKey(gid) ) {
					gidTeamMap.get(gid).add(TeamAdminResponse.fromTeam(team));
				}
			}
		}

		logOperation(log, READ, TEAM, user, format(TEAMS_FETCHED, teams.size(), "all user GIDs", user.getEmail(), user.getUserId()));

		return gidTeamMap.entrySet().stream()
			.map(entry -> UserRegisteredTeamsAdminResponse.of(entry.getKey(), entry.getValue()))
			.toList();
	}

	@Transactional ( readOnly = true )
	public TeamAdminResponse getAdminTeamByTid ( String tid, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching team with TID: {}", tid);
		}

		// step-1: security — only ADMIN/SUPERADMIN may access
		requireAdminAccess(log, user, READ, TEAM);

		// step-2: fetch team
		var team = fetchTeamSafely(tid, null, user);

		logOperation(log, READ, TEAM, user, format(TEAM_FETCHED, tid, user.getEmail(), user.getUserId()));

		return TeamAdminResponse.fromTeam(team);
	}

	@Transactional ( readOnly = true )
	public List< TeamAdminResponse > getAdminTeamsByGid ( String gid, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching admin teams by GID: {}", gid);
		}

		// step-1: security — only ADMIN/SUPERADMIN may access
		requireAdminAccess(log, user, READ, TEAM);

		// step-2: verify GID and filter teams
		verifyGidOwnership(gid, null, user);
		var teams = teamRepository.findAll().stream()
			.filter(team -> team.getGidList().contains(gid))
			.toList();

		logOperation(log, READ, TEAM, user, format(TEAMS_FETCHED, teams.size(), gid, user.getEmail(), user.getUserId()));

		return teams.stream()
			.map(TeamAdminResponse::fromTeam)
			.toList();
	}

	@Transactional
	public TeamAdminResponse updatePaymentStatus ( String tid, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update payment status for team TID: {}", tid);
		}

		// step-1: security — only ADMIN/SUPERADMIN may toggle payment
		requireAdminAccess(log, user, TEAM_PAYMENT_UPDATE, TEAM);

		// step-2: fetch and mutate
		Team team = fetchTeamSafely(tid, null, user);
		team.setHasPaid(! team.isHasPaid());

		// step-3: audit log the successful toggle
		logOperation(log, TEAM_PAYMENT_UPDATE, TEAM, user,
			format(TEAM_PAYMENT_UPDATED, tid, team.isHasPaid(), user.getEmail(), user.getUserId()));

		return TeamAdminResponse.fromTeam(team);
	}

	@Transactional
	public TeamAdminResponse updatePlayedStatus ( String tid, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update played status for team TID: {}", tid);
		}

		// step-1: security — only ADMIN/SUPERADMIN may toggle played
		requireAdminAccess(log, user, TEAM_PLAYED_UPDATE, TEAM);

		// step-2: fetch team
		Team team = fetchTeamSafely(tid, null, user);

		// step-3: guard — team must have paid
		if ( ! team.isHasPaid() ) {
			logError(log, TEAM_PLAYED_UPDATE, TEAM, user, format(TEAM_PLAYED_FAILED_NOT_PAID, tid), null);
			throw new IllegalArgumentException("Team must have paid to update played status");
		}

		// step-4: mutate and audit log
		team.setHasPlayed(! team.isHasPlayed());
		logOperation(log, TEAM_PLAYED_UPDATE, TEAM, user,
			format(TEAM_PLAYED_UPDATED, tid, team.isHasPlayed(), user.getEmail(), user.getUserId()));

		return TeamAdminResponse.fromTeam(team);
	}

	@Transactional
	public TeamAdminResponse updateQualifiedStatus ( String tid, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update qualified status for team TID: {}", tid);
		}

		// step-1: security — only ADMIN/SUPERADMIN may toggle qualified
		requireAdminAccess(log, user, TEAM_QUALIFIED_UPDATE, TEAM);

		// step-2: fetch team
		Team team = fetchTeamSafely(tid, null, user);

		// step-3: guard — team must have played
		if ( ! team.isHasPlayed() ) {
			logError(log, TEAM_QUALIFIED_UPDATE, TEAM, user, format(TEAM_QUALIFICATION_FAILED_NOT_PLAYED, tid), null);
			throw new IllegalArgumentException("Team must have played to update qualified status");
		}

		// step-4: mutate and optionally notify
		team.setHasQualified(! team.isHasQualified());
		if ( team.isHasQualified() ) {
			triggerEmailNotification(team, QUALIFICATION, user);
		}

		// step-5: audit log the successful toggle
		logOperation(log, TEAM_QUALIFIED_UPDATE, TEAM, user,
			format(TEAM_QUALIFIED_UPDATED, tid, team.isHasQualified(), user.getEmail(), user.getUserId()));

		return TeamAdminResponse.fromTeam(team);
	}

	@Transactional
	public TeamAdminResponse updatePosition ( String tid, Position position, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update position for team TID: {}", tid);
		}

		// step-1: security — only ADMIN/SUPERADMIN may update position
		requireAdminAccess(log, user, TEAM_POSITION_UPDATE, TEAM);

		// step-2: fetch team
		Team team = fetchTeamSafely(tid, null, user);

		// step-3: guard — team must be qualified to have a position
		if ( ! team.isHasQualified() ) {
			logError(log, TEAM_POSITION_UPDATE, TEAM, user, format(TEAM_POSITION_FAILED_NOT_QUALIFIED, tid), null);
			throw new IllegalArgumentException("Team must be qualified to update position");
		}

		// step-4: apply position update
		Position previousPosition = team.getPosition();
		team.setPosition(position != null ? position : Position.NONE);

		boolean isWinningPosition = position != null && position != Position.NONE;
		if ( isWinningPosition && position != previousPosition ) {
			triggerEmailNotification(team, POSITION, user);
		}

		// step-5: audit log
		logOperation(log, TEAM_POSITION_UPDATE, TEAM, user,
			format(TEAM_POSITION_UPDATED, tid, team.getPosition().name(), user.getEmail(), user.getUserId()));

		return TeamAdminResponse.fromTeam(team);
	}

	/*
	 * private methods — used internally only
	 */

	private Team processAndSaveTeam ( String teamName, Event event, List< String > gidList, List< Contact > contactList, User user ) {
		// guard: is event registration open?
		if ( ! event.isEventRegistrationOpen() ) {
			logError(log, TEAM_REGISTER, TEAM, user, format(TEAM_REGISTRATION_CLOSED, event.getEventName()), null);
			throw new TeamRegistrationException(format(REGISTRATION_CLOSED, EVENT, event.getEventName()));
		}

		// guard: duplicate team name for the same event
		if ( teamRepository.existsByTeamNameAndEvent(teamName, event) ) {
			logError(log, TEAM_REGISTER, TEAM, user, format(TEAM_NAME_EXISTS, teamName, event.getEventName()), null);
			throw new TeamRegistrationException(format("Team name '%s' already exists for event: %s", teamName, event.getEventName()));
		}

		// guard: team size limits
		int teamSize = gidList.size();
		if ( teamSize < event.getEventMinPlayers() || teamSize > event.getEventMaxPlayers() ) {
			logError(log, TEAM_REGISTER, TEAM, user, format(TEAM_SIZE_INVALID, teamSize, event.getEventMinPlayers(), event.getEventMaxPlayers(), event.getEventName()), null);
			throw new TeamRegistrationException(format("Team size '%d' must be between: %d and %d for event: %s", teamSize, event.getEventMinPlayers(), event.getEventMaxPlayers(), event.getEventName()));
		}

		// validate and guard GIDs
		validateGids(gidList, user);

		// guard: ensure none of the GIDs are already registered for this specific event
		Set< String > alreadyRegisteredGids = teamRepository.findByEvent(event).stream()
			.flatMap(team -> team.getGidList().stream())
			.collect(Collectors.toSet());

		for ( String gid : gidList ) {
			if ( alreadyRegisteredGids.contains(gid) ) {
				logError(log, TEAM_REGISTER, TEAM, user, format(TEAM_ALREADY_REGISTERED_GID, gid, event.getEventName()), null);
				throw new TeamRegistrationException(format("GID '%s' is already registered for event: %s", gid, event.getEventName()));
			}
		}

		// assemble and persist team entity
		var team = Team.builder()
			.teamName(teamName)
			.tid(generateTID())
			.event(event)
			.contacts(contactList)
			.gidList(gidList)
			.build();

		Team savedTeam = teamRepository.save(team);

		if ( log.isDebugEnabled() ) {
			log.debug("Persisted team TID {} for event {}", savedTeam.getTid(), event.getEventName());
		}

		triggerEmailNotification(savedTeam, REGISTRATION, user);

		return savedTeam;
	}

	private void validateGids ( List< String > gidList, User user ) {
		// validate that all GIDs exist in MRD
		for ( String gid : gidList ) {
			if ( ! mrdRepository.existsByGid(gid) ) {
				logError(log, TEAM_REGISTER, TEAM, user, format(TEAM_INVALID_GID, gid), null);
				throw new TeamRegistrationException("Invalid GID: " + gid);
			}
		}

		// check for duplicate GIDs in the request
		Set< String > uniqueGids = new HashSet<>(gidList);
		if ( uniqueGids.size() != gidList.size() ) {
			logError(log, TEAM_REGISTER, TEAM, user, TEAM_DUPLICATE_GID, null);
			throw new TeamRegistrationException("Duplicate GIDs are not allowed");
		}
	}

	private Event fetchEventById ( Long eventId, User user ) {
		return eventRepository.findById(eventId)
			.orElseThrow(() -> {
				logError(log, READ, EVENT, user, format(NOT_FOUND_LOG, EVENT, "ID", eventId), null);
				return new EventNotFoundException(format(NOT_FOUND, EVENT));
			});
	}

	private EventCombo fetchComboById ( Long comboId, User user ) {
		return eventComboRepository.findById(comboId)
			.orElseThrow(() -> {
				logError(log, READ, AppConstant.Resource.COMBO, user, format(NOT_FOUND_LOG, AppConstant.Resource.COMBO, "ID", comboId), null);
				return new ComboNotFoundException(format(NOT_FOUND, COMBO));
			});
	}

	private Team fetchTeamSafely ( String tid, String email, User user ) {
		return teamRepository.findByTid(tid)
			.orElseThrow(() -> {
				User auditUser = email != null ? User.builder().email(email).build() : user;
				logError(log, READ, TEAM, auditUser, format(TEAM_NOT_FOUND, tid), null);
				return new TeamNotFoundException(format(NOT_FOUND, TEAM + " with TID: " + tid));
			});
	}


	private void triggerEmailNotification ( Team team, EmailType type, User user ) {
		// fetch emails of all the team members registered with GID
		List< String > teamMemberEmails = mrdRepository.findUserEmailListByGidList(team.getGidList());
		String[] emailArray = teamMemberEmails.toArray(new String[0]);

		type.sendEmail(emailService, emailArray, team, user);
	}

	private void verifyGidOwnership ( String gid, String email, User user ) {
		if ( ! mrdRepository.existsByGid(gid) ) {
			User auditUser = email != null ? User.builder().email(email).build() : user;
			logError(log, READ, TEAM, auditUser, format(NOT_FOUND_LOG, "GID", "ID", gid), null);
			throw new TeamNotFoundException("GID not found: " + gid);
		}
	}

	private String generateTID () {
		Long sequence = stringRedisTemplate.opsForValue().increment(TEAM_SEQ_KEY);
		String paddedSequence = format("%03d", sequence);
		String salt = RandomUtil.generateIdSalt(2);
		return "TD26-" + paddedSequence + "-" + salt;
	}
}
