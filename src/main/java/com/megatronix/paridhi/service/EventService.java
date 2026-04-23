package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.*;
import com.megatronix.paridhi.dto.request.EventRequest;
import com.megatronix.paridhi.dto.response.EventAdminResponse;
import com.megatronix.paridhi.dto.response.EventPublicResponse;
import com.megatronix.paridhi.dto.response.ImageUploadResult;
import com.megatronix.paridhi.exception.EventNotFoundException;
import com.megatronix.paridhi.exception.ForbiddenAccessException;
import com.megatronix.paridhi.model.Event;
import com.megatronix.paridhi.model.EventCombo;
import com.megatronix.paridhi.model.Team;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.EventComboRepository;
import com.megatronix.paridhi.repository.EventRepository;
import com.megatronix.paridhi.repository.TeamRepository;
import com.megatronix.paridhi.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {
	private static final Logger log = LoggerFactory.getLogger(EventService.class);
	private final TeamRepository teamRepository;
	private final EventRepository eventRepository;
	private final EventComboRepository eventComboRepository;
	private final ImageStorageService imageStorageService;

	/*
	 * public methods - doesn't require authentication
	 */

	public List< EventPublicResponse > getAllEvents () {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			"Fetching all events"
		);

		// fetch all events from the database
		List< Event > events = eventRepository.findAll();

		// log the successful retrieval of events
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			AppConstant.SUCCESSFULLY_RETRIEVED + events.size() + AppConstant.EVENT
		);

		// map events to EventPublicResponse DTOs and return
		return events.stream()
			.map(EventPublicResponse::fromEvent)
			.toList();
	}

	public List< EventPublicResponse > getEventsByDomain ( Domain domain ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			"Fetching events by domain: " + domain
		);

		// fetch events by domain from the database
		List< Event > events = eventRepository.findByDomain(domain);

		// log the successful retrieval of events
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			AppConstant.SUCCESSFULLY_RETRIEVED + events.size() + " events for domain: " + domain
		);

		// map events to EventPublicResponse DTOs and return
		return events.stream()
			.map(EventPublicResponse::fromEvent)
			.toList();
	}

	public List< EventPublicResponse > getEventsByRegistration ( boolean isEventRegistrationOpen ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			"Fetching events by registration status: " + ( isEventRegistrationOpen ? AppConstant.OPEN : AppConstant.CLOSED )
		);

		// fetch events by registration status from the database
		List< Event > events = eventRepository.findByIsEventRegistrationOpen(isEventRegistrationOpen);

		// log the successful retrieval of events
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			AppConstant.SUCCESSFULLY_RETRIEVED + events.size() + " events with registration status: " +
				( isEventRegistrationOpen ? AppConstant.OPEN : AppConstant.CLOSED )
		);

		// map events to EventPublicResponse DTOs and return
		return events.stream()
			.map(EventPublicResponse::fromEvent)
			.toList();
	}

	public List< EventPublicResponse > getEventsByType ( EventType eventType ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			"Fetching events by type: " + eventType
		);

		// fetch events by type from the database
		List< Event > events = eventRepository.findByEventType(eventType);

		// log the successful retrieval of events
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			AppConstant.SUCCESSFULLY_RETRIEVED + events.size() + " events of type: " + eventType
		);

		// map events to EventPublicResponse DTOs and return
		return events.stream()
			.map(EventPublicResponse::fromEvent)
			.toList();
	}

	public EventPublicResponse getEventById ( Long eventId ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			"Fetching event with ID: " + eventId
		);

		// find event by ID
		var event = fetchEventById(eventId, null);

		// log the successful retrieval of the event
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.EVENT,
			null,
			"Successfully retrieved event ID: " + eventId + " - " + event.getEventName()
		);

		// map event to EventPublicResponse DTO and return
		return EventPublicResponse.fromEvent(event);
	}

	/*
	 * protected methods - requires authentication
	 */

	@Transactional
	public EventAdminResponse createEvent ( EventRequest request, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.CREATE,
			AppConstant.EVENT,
			user,
			"Creating new event: " + request.name() + " for domain: " + request.domain()
		);

		// validate user access
		checkUserAccess(user, MessageConstant.Operation.CREATE);

		// validate the min and max player limit for that event
		validatePlayerLimits(request);

		// create event builder object
		var event = Event.builder()
			.domain(request.domain())
			.eventName(request.name())
			.eventType(request.type())
			.eventDate(request.date())
			.eventDescription(request.description())
			.eventVenue(( request.venue() ))
			.eventCoordinatorDetails(request.coordinatorDetails())
			.eventRuleBookUrl(request.ruleBookUrl())
			.isTeamEvent(request.isTeamEvent())
			.eventMinPlayers(request.minPlayers())
			.eventMaxPlayers(request.maxPlayers())
			.eventRegistrationFee(request.registrationFee())
			.eventPrizePool(( request.prizePool() ))
			.build();

		// save event to database
		var savedEvent = eventRepository.save(event);

		// log the successful creation of the event
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.CREATE,
			AppConstant.EVENT,
			user,
			"Successfully created event ID: " + savedEvent.getEventId() + " - " + savedEvent.getEventName()
		);

		// return EventAdminResponse object
		return EventAdminResponse.fromEvent(savedEvent);
	}

	@Transactional
	public EventAdminResponse updateEvent ( Long eventId, EventRequest request, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.EVENT,
			user,
			"Updating event with ID: " + eventId
		);

		// validate user access
		checkUserAccess(user, MessageConstant.Operation.UPDATE);

		// validate the min and max player limit for that event
		validatePlayerLimits(request);

		// find event by ID
		var existingEvent = fetchEventById(eventId, user);

		// update event fields
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
		existingEvent.setEventPrizePool(request.prizePool());

		// save updated event to database
		var updatedEvent = eventRepository.save(existingEvent);

		// log the successful update of the event
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.EVENT,
			user,
			"Successfully updated event ID: " + eventId + " - " + updatedEvent.getEventName()
		);

		// return EventAdminResponse object
		return EventAdminResponse.fromEvent(updatedEvent);
	}

	@Transactional
	public EventAdminResponse updateEventImage ( Long eventId, MultipartFile file, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.EVENT_IMAGE,
			user,
			"Updating image for event with ID: " + eventId + " - File: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)"
		);

		// validate user access
		checkUserAccess(user, "update image of");

		// find event by ID
		var existingEvent = fetchEventById(eventId, user);

		// clean up previous image if it exists
		if ( existingEvent.getEventPosterUrl() != null && existingEvent.getEventPosterFileId() != null ) {
			try {
				imageStorageService.deleteFile(existingEvent.getEventPosterFileId());
			} catch ( Exception e ) {
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.DELETE,
					AppConstant.IMAGE_STORAGE,
					user,
					"Failed to delete previous image for event ID: " + eventId + " - Error: " + e.getMessage(),
					e
				);
				// continue with image update even if cleanup fails
			}
		}

		ImageUploadResult imageDetails = imageStorageService.uploadFile(file, "event");

		// update event image URL
		existingEvent.setEventPosterUrl(imageDetails.fileUrl());
		existingEvent.setEventPosterFileId(imageDetails.fileId());

		// save updated event to database
		var updatedEvent = eventRepository.save(existingEvent);

		// log the successful update of the event image
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.EVENT_IMAGE,
			user,
			"Successfully updated image for event ID: " + eventId +
				" - New image file ID: " + imageDetails.fileId()
		);

		// return an EventAdminResponse object
		return EventAdminResponse.fromEvent(updatedEvent);
	}

	@Transactional
	public void deleteEvent ( Long eventId, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.DELETE,
			AppConstant.EVENT,
			user,
			"Deleting event with ID: " + eventId
		);

		// validate user access
		checkUserAccess(user, MessageConstant.Operation.DELETE);

		// find event by ID
		var existingEvent = fetchEventById(eventId, user);

		// handle teams that reference this event
		List< Team > teams = teamRepository.findByEvent(existingEvent);
		if ( ! teams.isEmpty() ) {
			LoggingUtil.logOperation(
				log,
				MessageConstant.Operation.DELETE,
				AppConstant.EVENT,
				user,
				"Deleting " + teams.size() + " teams associated with event: " + existingEvent.getEventName()
			);
			teamRepository.deleteAll(teams);
		}

		// handle many-to-many relationship with combo
		Set< EventCombo > combos = eventComboRepository.findByEvents_EventId(eventId);
		if ( ! combos.isEmpty() ) {
			LoggingUtil.logOperation(
				log,
				MessageConstant.Operation.UPDATE,
				AppConstant.EVENT_COMBO,
				user,
				"Updating " + combos.size() + " combos that contain event ID: " + eventId
			);

			// remove the event from each combo
			for ( EventCombo combo : combos ) {
				combo.getEvents().remove(existingEvent);
				// if the combo has fewer than 2 events, delete it
				if ( combo.getEvents().size() < 2 ) {
					eventComboRepository.delete(combo);
					LoggingUtil.logOperation(
						log,
						MessageConstant.Operation.DELETE,
						AppConstant.EVENT_COMBO,
						user,
						"Deleting combo ID: " + combo.getEventComboId() + " as it has fewer than 2 events"
					);
				} else {
					// save the updated combo
					eventComboRepository.save(combo);
				}
			}
		}

		// delete image from cloudinary if exists
		if ( existingEvent.getEventPosterUrl() != null && existingEvent.getEventPosterFileId() != null ) {
			try {
				imageStorageService.deleteFile(existingEvent.getEventPosterFileId());
			} catch ( Exception e ) {
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.DELETE,
					AppConstant.IMAGE_STORAGE,
					user,
					"Failed to delete image for event ID: " + eventId + " - Error: " + e.getMessage(),
					e
				);
				// continue with event deletion even if image deletion fails
			}
		}

		// delete event from database
		eventRepository.delete(existingEvent);

		// log the successful deletion of the event
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.DELETE,
			AppConstant.EVENT,
			user,
			"Successfully deleted event ID: " + eventId + " - " + existingEvent.getEventName()
		);
	}

	@Transactional
	public EventAdminResponse toggleRegistrationStatus ( Long eventId, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.EVENT,
			user,
			"Toggling registration status for event with ID: " + eventId
		);

		// validate user access
		checkUserAccess(user, "toggle registration status of");

		// find event by ID
		var existingEvent = fetchEventById(eventId, user);

		// get current status for logging
		boolean currentStatus = existingEvent.isEventRegistrationOpen();
		boolean newStatus = ! currentStatus;

		// update registration status
		existingEvent.setEventRegistrationOpen(newStatus);
		var updatedEvent = eventRepository.save(existingEvent);

		// log the successful update of registration status
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.EVENT,
			user,
			"Successfully updated registration status for event ID: " + eventId +
				" - Name: " + updatedEvent.getEventName() +
				" - Status: " + ( currentStatus ? "OPEN -> CLOSED" : "CLOSED -> OPEN" )
		);

		// return updated event
		return EventAdminResponse.fromEvent(updatedEvent);
	}

	/*
	 * private methods - is used internally only
	 */

	private void checkUserAccess ( User user, String methodType ) {
		// check if user is null
		if ( user == null ) {
			LoggingUtil.logSecurity(
				log,
				AppConstant.ACCESS_DENIED,
				null,
				AppConstant.FAILED,
				String.format(MessageConstant.ErrorTemplate.AUTHENTICATION_REQUIRED, methodType + " " + AppConstant.EVENT)
			);
			throw new ForbiddenAccessException(MessageConstant.UserMessage.ACCESS_DENIED);
		}

		// check if user has ROLE_USER
		if ( user.getRole().equals(Role.ROLE_USER) ) {
			LoggingUtil.logSecurity(
				log,
				AppConstant.ACCESS_DENIED,
				user,
				AppConstant.FAILED,
				String.format(MessageConstant.ErrorTemplate.NOT_AUTHORIZED, methodType + " " + AppConstant.EVENT)
			);
			throw new ForbiddenAccessException(MessageConstant.UserMessage.ACCESS_DENIED);
		}

		// log the successful authorization
		LoggingUtil.logSecurity(
			log,
			AppConstant.ACCESS_GRANTED,
			user,
			AppConstant.SUCCESS,
			String.format(MessageConstant.SuccessTemplate.AUTHORIZED, methodType + " " + AppConstant.EVENT)
		);
	}

	private Event fetchEventById ( Long eventId, User user ) {
		return eventRepository.findById(eventId)
			.orElseThrow(() -> {
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.READ,
					AppConstant.EVENT,
					user,
					String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT),
					null
				);
				return new EventNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT));
			});
	}

	private void validatePlayerLimits ( EventRequest request ) {
		if ( request.minPlayers() > request.maxPlayers() ) {
			throw new IllegalArgumentException(String.format(MessageConstant.ErrorTemplate.TEAM_SIZE,
				request.minPlayers(), request.maxPlayers(), request.name()));
		}
	}
}
