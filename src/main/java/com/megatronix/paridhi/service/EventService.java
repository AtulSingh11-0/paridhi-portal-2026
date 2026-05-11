package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.EventType;
import com.megatronix.paridhi.dto.request.EventRequest;
import com.megatronix.paridhi.dto.response.*;
import com.megatronix.paridhi.exception.EventNotFoundException;
import com.megatronix.paridhi.model.Event;
import com.megatronix.paridhi.model.EventCombo;
import com.megatronix.paridhi.model.Team;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.EventComboRepository;
import com.megatronix.paridhi.repository.EventRepository;
import com.megatronix.paridhi.repository.TeamRepository;
import com.megatronix.paridhi.util.AccessControlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.megatronix.paridhi.constant.AppConstant.Common.*;
import static com.megatronix.paridhi.constant.AppConstant.Resource.EVENT;
import static com.megatronix.paridhi.constant.AppConstant.Storage.*;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.NOT_FOUND;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.TEAM_SIZE;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.*;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.*;
import static com.megatronix.paridhi.util.LoggingUtil.logError;
import static com.megatronix.paridhi.util.LoggingUtil.logOperation;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
public class EventService {

	private static final Logger log = LoggerFactory.getLogger(EventService.class);

	private final TeamRepository teamRepository;
	private final EventRepository eventRepository;
	private final EventComboRepository eventComboRepository;
	private final ImageStorageService imageStorageService;

	public EventService (
		TeamRepository teamRepository,
		EventRepository eventRepository,
		EventComboRepository eventComboRepository,
		ImageStorageService imageStorageService
	) {
		this.teamRepository = teamRepository;
		this.eventRepository = eventRepository;
		this.eventComboRepository = eventComboRepository;
		this.imageStorageService = imageStorageService;
	}

	/*
	 * public methods — doesn't require authentication
	 */

	@Transactional ( readOnly = true )
	public List< CategorizedEventAndEventComboResponse > getAllEventsAndCombos () {
		// step-1: fetch all events and combos from the database
		List< Event > events = eventRepository.findAll();
		List< EventCombo > combos = eventComboRepository.findAll();

		if ( log.isDebugEnabled() ) {
			log.debug("getAllEventsAndCombos: fetched {} events and {} combos", events.size(), combos.size());
		}

		// step-2: group events by domain using a case-insensitive sorted map
		Map< String, List< EventPublicResponse > > eventsByDomain = events.stream()
			.map(EventPublicResponse::fromEvent)
			.collect(Collectors.groupingBy(
				e -> e.domain().name(),
				() -> new TreeMap<>(CASE_INSENSITIVE_ORDER),
				toList()
			));

		// step-3: group combos by domain using a case-insensitive sorted map
		Map< String, List< EventComboPublicResponse > > combosByDomain = combos.stream()
			.map(this::apply)
			.collect(Collectors.groupingBy(
				c -> c.domain().name(),
				() -> new TreeMap<>(CASE_INSENSITIVE_ORDER),
				toList()
			));

		// step-4: merge all domain keys and build unified response
		Set< String > allDomains = new TreeSet<>(CASE_INSENSITIVE_ORDER);
		allDomains.addAll(eventsByDomain.keySet());
		allDomains.addAll(combosByDomain.keySet());

		return allDomains.stream()
			.map(domain -> new CategorizedEventAndEventComboResponse(
				domain,
				eventsByDomain.getOrDefault(domain, List.of()),
				combosByDomain.getOrDefault(domain, List.of())
			))
			.toList();
	}

	@Transactional ( readOnly = true )
	public List< CategorizedEventResponse > getAllEvents () {
		// step-1: fetch all events from the database
		List< Event > events = eventRepository.findAll();

		if ( log.isInfoEnabled() ) {
			logOperation(log, READ, EVENT, format(EVENTS_RETRIEVED, events.size()));
		}

		// step-2: group events by domain and return
		return groupEventsByDomain(events);
	}

	@Transactional ( readOnly = true )
	public List< EventPublicResponse > getEventsByDomain ( Domain domain ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching events for domain: {}", domain);
		}

		// step-1: fetch events by domain from the database
		List< Event > events = eventRepository.findByDomain(domain);

		if ( log.isInfoEnabled() ) {
			logOperation(log, READ, EVENT, format(EVENTS_RETRIEVED, events.size()));
		}

		// step-2: map to public DTOs and return
		return events.stream()
			.map(EventPublicResponse::fromEvent)
			.toList();
	}

	@Transactional ( readOnly = true )
	public List< CategorizedEventResponse > getEventsByRegistration ( boolean isEventRegistrationOpen, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching events by registration status: {}", isEventRegistrationOpen ? OPEN : CLOSED);
		}

		// step-1: fetch events filtered by registration status
		List< Event > events = eventRepository.findByIsEventRegistrationOpen(isEventRegistrationOpen);

		if ( log.isInfoEnabled() ) {
			logOperation(log, READ, EVENT, user, format(EVENTS_RETRIEVED, events.size()));
		}

		// step-2: group and return
		return groupEventsByDomain(events);
	}

	@Transactional ( readOnly = true )
	public List< CategorizedEventResponse > getEventsByType ( EventType eventType ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching events by type: {}", eventType);
		}

		// step-1: fetch events filtered by type
		List< Event > events = eventRepository.findByEventType(eventType);

		if ( log.isInfoEnabled() ) {
			logOperation(log, READ, EVENT, format(EVENTS_RETRIEVED, events.size()));
		}

		// step-2: group and return
		return groupEventsByDomain(events);
	}

	@Transactional ( readOnly = true )
	public EventPublicResponse getEventById ( Long eventId ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching event with ID: {}", eventId);
		}

		// step-1: fetch event by ID — throws EventNotFoundException if missing
		var event = fetchEventById(eventId, null);

		// step-2: audit log the successful retrieval
		logOperation(log, READ, EVENT, format(EVENT_RETRIEVED, event.getEventName(), eventId));

		// step-3: map and return the public DTO
		return EventPublicResponse.fromEvent(event);
	}

	/*
	 * protected methods — requires authentication
	 */

	@Transactional
	public EventAdminResponse createEvent ( EventRequest request, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to create event: '{}' for domain: {}", request.name(), request.domain());
		}

		// step-1: security — only ADMIN/SUPERADMIN may create events
		AccessControlUtil.requireAdminAccess(log, user, EVENT_CREATE, EVENT);

		// step-2: validate player count bounds before persisting
		validatePlayerLimits(request);

		// step-3: build and persist the event entity
		var event = Event.builder()
			.domain(request.domain())
			.eventName(request.name())
			.eventType(request.type())
			.eventDate(request.date())
			.eventDescription(request.description())
			.eventVenue(request.venue())
			.eventCoordinatorDetails(request.coordinatorDetails())
			.eventRuleBookUrl(request.ruleBookUrl())
			.isTeamEvent(request.isTeamEvent())
			.eventMinPlayers(request.minPlayers())
			.eventMaxPlayers(request.maxPlayers())
			.eventRegistrationFee(request.registrationFee())
			.build();

		var savedEvent = eventRepository.save(event);

		// step-4: audit log the successful creation
		logOperation(log, EVENT_CREATE, EVENT, user,
			format(EVENT_CREATED, savedEvent.getEventName(), savedEvent.getEventId(), user.getEmail(), user.getUserId()));

		return EventAdminResponse.fromEvent(savedEvent);
	}

	@Transactional
	public EventAdminResponse updateEvent ( Long eventId, EventRequest request, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update event ID: {}", eventId);
		}

		// step-1: security — only ADMIN/SUPERADMIN may update events
		AccessControlUtil.requireAdminAccess(log, user, EVENT_UPDATE, EVENT);

		// step-2: validate player count bounds before applying update
		validatePlayerLimits(request);

		// step-3: fetch the existing event — throws EventNotFoundException if missing
		var existingEvent = fetchEventById(eventId, user);

		// step-4: apply updates to all event fields
		existingEvent.setDomain(request.domain());
		existingEvent.setEventName(request.name());
		existingEvent.setEventType(request.type());
		existingEvent.setEventDate(request.date());
		existingEvent.setEventDescription(request.description());
		existingEvent.setEventVenue(request.venue());
		existingEvent.setEventCoordinatorDetails(request.coordinatorDetails());
		existingEvent.setEventRuleBookUrl(request.ruleBookUrl());
		existingEvent.setEventMinPlayers(request.minPlayers());
		existingEvent.setEventMaxPlayers(request.maxPlayers());
		existingEvent.setEventRegistrationFee(request.registrationFee());

		var updatedEvent = eventRepository.save(existingEvent);

		// step-5: audit log the successful update
		logOperation(log, EVENT_UPDATE, EVENT, user,
			format(EVENT_UPDATED, updatedEvent.getEventName(), eventId, user.getEmail(), user.getUserId()));

		return EventAdminResponse.fromEvent(updatedEvent);
	}

	@Transactional
	public EventAdminResponse updateEventImage ( Long eventId, MultipartFile file, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update image for event ID: {} — file: '{}' ({} bytes)",
				eventId, file.getOriginalFilename(), file.getSize());
		}

		// step-1: security — only ADMIN/SUPERADMIN may update event images
		AccessControlUtil.requireAdminAccess(log, user, EVENT_IMAGE_UPDATE, EVENT_IMAGE);

		// step-2: fetch the existing event
		var existingEvent = fetchEventById(eventId, user);

		// step-3: best-effort cleanup of the previous poster image (non-fatal)
		if ( existingEvent.getEventPosterUrl() != null && existingEvent.getEventPosterFileId() != null ) {
			try {
				imageStorageService.deleteFile(existingEvent.getEventPosterFileId());
			} catch ( Exception e ) {
				logError(log, EVENT_IMAGE_UPDATE, IMAGE_STORAGE, user,
					format(EVENT_IMAGE_DELETE_FAILED, eventId, e.getMessage()), e);
				// continue with image update even if old image cleanup fails
			}
		}

		// step-4: upload the new image and persist the updated URLs
		ImageUploadResult imageDetails = imageStorageService.uploadFile(file, "event");
		existingEvent.setEventPosterUrl(imageDetails.fileUrl());
		existingEvent.setEventPosterFileId(imageDetails.fileId());

		var updatedEvent = eventRepository.save(existingEvent);

		// step-5: audit log the successful image update
		logOperation(log, EVENT_IMAGE_UPDATE, EVENT_IMAGE, user,
			format(EVENT_IMAGE_UPDATED, updatedEvent.getEventName(), eventId, imageDetails.fileId(), user.getEmail(), user.getUserId()));

		return EventAdminResponse.fromEvent(updatedEvent);
	}

	@Transactional
	public void deleteEvent ( Long eventId, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to delete event ID: {}", eventId);
		}

		// step-1: security — only ADMIN/SUPERADMIN may delete events
		AccessControlUtil.requireAdminAccess(log, user, EVENT_DELETE, EVENT);

		// step-2: fetch the event to be deleted
		var existingEvent = fetchEventById(eventId, user);

		// step-3: cascade delete all teams registered for this event
		deleteEventTeams(existingEvent, eventId, user);

		// step-4: update or cascade-delete event combos that reference this
		removeEventFromCombos(existingEvent, eventId, user);

		// step-5: best-effort cleanup of the event poster image (non-fatal)
		deleteEventPoster(existingEvent, eventId, user);

		// step-6: delete the event from the database and audit log
		eventRepository.delete(existingEvent);
		logOperation(log, EVENT_DELETE, EVENT, user,
			format(EVENT_DELETED, existingEvent.getEventName(), eventId, user.getEmail(), user.getUserId()));
	}

	@Transactional
	public EventAdminResponse toggleRegistrationStatus ( Long eventId, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to toggle registration status for event ID: {}", eventId);
		}

		// step-1: security — only ADMIN/SUPERADMIN may toggle registration
		AccessControlUtil.requireAdminAccess(log, user, EVENT_TOGGLE_REGISTRATION, EVENT);

		// step-2: fetch the existing event
		var existingEvent = fetchEventById(eventId, user);

		// step-3: flip registration status and persist
		boolean previousStatus = existingEvent.isEventRegistrationOpen();
		existingEvent.setEventRegistrationOpen(! previousStatus);
		var updatedEvent = eventRepository.save(existingEvent);

		// step-4: audit log the toggle result (old -> new status)
		logOperation(log, EVENT_TOGGLE_REGISTRATION, EVENT, user,
			format(EVENT_REGISTRATION_TOGGLED,
				updatedEvent.getEventName(), eventId,
				previousStatus ? OPEN : CLOSED,
				! previousStatus ? OPEN : CLOSED,
				user.getEmail(), user.getUserId()));

		return EventAdminResponse.fromEvent(updatedEvent);
	}

	/*
	 * private methods — used internally only
	 */

	private List< CategorizedEventResponse > groupEventsByDomain ( List< Event > events ) {
		return events.stream()
			.collect(Collectors.groupingBy(
				e -> e.getDomain().name(),
				TreeMap::new,
				Collectors.mapping(EventPublicResponse::fromEvent, toList())
			))
			.entrySet().stream()
			.map(entry -> new CategorizedEventResponse(entry.getKey(), entry.getValue()))
			.toList();
	}

	private Event fetchEventById ( Long eventId, User user ) {
		return eventRepository.findById(eventId)
			.orElseThrow(() -> {
				logError(log, READ, EVENT, user, format(EVENT_NOT_FOUND, eventId), null);
				return new EventNotFoundException(format(NOT_FOUND, EVENT));
			});
	}

	private Instant getEventComboDate ( EventCombo combo ) {
		return combo.getEvents().stream()
			.map(Event::getEventDate)
			.min(Instant::compareTo)
			.orElse(Instant.now());
	}

	private String getEventComboVenue ( EventCombo combo ) {
		return combo.getEvents().stream()
			.map(Event::getEventVenue)
			.filter(venue -> venue != null && ! venue.isBlank())
			.findFirst()
			.orElse("TBD");
	}

	private void validatePlayerLimits ( EventRequest request ) {
		if ( request.minPlayers() > request.maxPlayers() ) {
			throw new IllegalArgumentException(
				format(TEAM_SIZE, request.minPlayers(), request.maxPlayers(), request.name()));
		}
	}

	private void deleteEventTeams ( Event existingEvent, Long eventId, User user ) {
		List< Team > teams = teamRepository.findByEvent(existingEvent);
		if ( ! teams.isEmpty() ) {
			teamRepository.deleteAll(teams);
			if ( log.isInfoEnabled() ) {
				logOperation(log, EVENT_DELETE, EVENT, user,
					format(EVENT_TEAMS_DELETED, teams.size(), existingEvent.getEventName(), eventId));
			}
		}
	}

	private void removeEventFromCombos ( Event existingEvent, Long eventId, User user ) {
		// step-1: find all combos that include this event
		Set< EventCombo > combos = eventComboRepository.findByEvents_EventId(eventId);
		if ( ! combos.isEmpty() ) {
			// step-2: remove the event from each combo's event list
			for ( EventCombo combo : combos ) {
				combo.getEvents().remove(existingEvent);
				// step-3: if combo has fewer than 2 events after removal, delete it — otherwise save the updated combo
				if ( combo.getEvents().size() < 2 ) {
					// step-3a: combo is invalid with fewer than 2 events — delete it
					eventComboRepository.delete(combo);
					if ( log.isInfoEnabled() ) {
						logOperation(log, EVENT_DELETE, AppConstant.Resource.COMBO, user,
							format(EVENT_COMBO_DELETED, combo.getEventComboId()));
					}
				} else {
					// step-3b: combo still has enough events — just save the updated reference list
					eventComboRepository.save(combo);
				}
			}
			if ( log.isInfoEnabled() ) {
				logOperation(log, EVENT_DELETE, AppConstant.Resource.COMBO, user,
					format(EVENT_COMBOS_UPDATED, combos.size(), existingEvent.getEventName(), eventId));
			}
		}
	}

	private void deleteEventPoster ( Event existingEvent, Long eventId, User user ) {
		if ( existingEvent.getEventPosterUrl() != null && existingEvent.getEventPosterFileId() != null ) {
			try {
				imageStorageService.deleteFile(existingEvent.getEventPosterFileId());
			} catch ( Exception e ) {
				logError(log, EVENT_DELETE, IMAGE_STORAGE, user,
					format(EVENT_IMAGE_DELETE_FAILED, eventId, e.getMessage()), e);
			}
		}
	}

	private EventComboPublicResponse apply ( EventCombo combo ) {
		return EventComboPublicResponse.fromCombo(combo, getEventComboDate(combo), getEventComboVenue(combo));
	}
}
