package com.megatronix.paridhi.service;

import com.megatronix.paridhi.dto.response.TeamAdminResponse;
import com.megatronix.paridhi.exception.EventNotFoundException;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.EventRepository;
import com.megatronix.paridhi.repository.MRDRepository;
import com.megatronix.paridhi.repository.TeamRepository;
import com.megatronix.paridhi.util.AccessControlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.megatronix.paridhi.constant.AppConstant.Resource.EVENT;
import static com.megatronix.paridhi.constant.AppConstant.Resource.TEAM;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.NOT_FOUND;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.EVENT_NOT_FOUND;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.READ;
import static com.megatronix.paridhi.util.LoggingUtil.logError;
import static com.megatronix.paridhi.util.LoggingUtil.logOperation;
import static java.lang.String.format;

@Service
public class CRDService {

	private static final Logger log = LoggerFactory.getLogger(CRDService.class);
	private final MRDRepository mrdRepository;
	private final TeamRepository teamRepository;
	private final EventRepository eventRepository;

	public CRDService (
		MRDRepository mrdRepository,
		TeamRepository teamRepository,
		EventRepository eventRepository
	) {
		this.teamRepository = teamRepository;
		this.eventRepository = eventRepository;
		this.mrdRepository = mrdRepository;
	}

	/*
	 * protected methods — requires authentication
	 */

	@Transactional ( readOnly = true )
	public List< TeamAdminResponse > getTeamsByEventForPrelims ( Long eventId, User user ) {
		// step-1: security — only ADMIN and above may access prelims data
		AccessControlUtil.requireAdminAccess(log, user, READ, "Prelims Data");

		// step-2: audit log the start of the operation
		logOperation(log, READ, TEAM, user, format("Fetching teams for prelims of event ID: %d", eventId));

		// step-3: fetch the event — throws EventNotFoundException if missing
		var event = eventRepository.findById(eventId)
			.orElseThrow(() -> {
				logError(log, READ, EVENT, user, format(EVENT_NOT_FOUND, eventId), null);
				return new EventNotFoundException(format(NOT_FOUND, EVENT));
			});

		// step-4: fetch all teams for the event (including contacts)
		var teams = teamRepository.findByEventWithContacts(event);

		// step-5: filter teams eligible for prelims
		// eligibility: all members paid for MRD AND the team registration itself is paid AND team is NOT yet qualified
		var prelimsTeams = teams.stream()
			.filter(team -> {
				// check if every team member has a verified and paid MRD record
				boolean allMembersPaid = team.getGidList().stream()
					.allMatch(mrdRepository::existsByGidAndHasPaidTrue);

				return ( allMembersPaid && team.isHasPaid() && ! team.isHasQualified() );
			})
			.toList();

		// step-6: audit log the successful completion
		logOperation(log, READ, TEAM, user,
			format("Fetched %d teams for Prelims of Event: %s (ID: %d)", prelimsTeams.size(), event.getEventName(), eventId));

		return prelimsTeams.stream()
			.map(TeamAdminResponse::fromTeam)
			.toList();
	}

	@Transactional ( readOnly = true )
	public List< TeamAdminResponse > getTeamsByEventForFinals ( Long eventId, User user ) {
		// step-1: security — only ADMIN and above may access finals data
		AccessControlUtil.requireAdminAccess(log, user, READ, "Finals Data");

		// step-2: audit log the start of the operation
		logOperation(log, READ, TEAM, user, format("Fetching teams for finals of event ID: %d", eventId));

		// step-3: fetch the event
		var event = eventRepository.findById(eventId)
			.orElseThrow(() -> {
				logError(log, READ, EVENT, user, format(EVENT_NOT_FOUND, eventId), null);
				return new EventNotFoundException(format(NOT_FOUND, EVENT));
			});

		// step-4: fetch all teams for the event
		var teams = teamRepository.findByEventWithContacts(event);

		// step-5: filter teams eligible for finals
		// eligibility: team has paid AND team has played (prelims) AND team is qualified
		var finalsTeams = teams.stream()
			.filter(team -> ( team.isHasPaid() && team.isHasPlayed() && team.isHasQualified() ))
			.toList();

		// step-6: audit log the successful completion
		logOperation(log, READ, TEAM, user,
			format("Fetched %d teams for Finals of Event: %s (ID: %d)", finalsTeams.size(), event.getEventName(), eventId));

		return finalsTeams.stream()
			.map(TeamAdminResponse::fromTeam)
			.toList();
	}
}
