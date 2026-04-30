package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.Role;
import com.megatronix.paridhi.dto.request.EventComboRequest;
import com.megatronix.paridhi.dto.response.CategorizedEventComboResponse;
import com.megatronix.paridhi.dto.response.EventComboAdminResponse;
import com.megatronix.paridhi.dto.response.EventComboPublicResponse;
import com.megatronix.paridhi.dto.response.ImageUploadResult;
import com.megatronix.paridhi.exception.ComboNotFoundException;
import com.megatronix.paridhi.exception.EventNotFoundException;
import com.megatronix.paridhi.exception.ForbiddenAccessException;
import com.megatronix.paridhi.exception.InvalidDomainException;
import com.megatronix.paridhi.model.Event;
import com.megatronix.paridhi.model.EventCombo;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.EventComboRepository;
import com.megatronix.paridhi.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.megatronix.paridhi.constant.AppConstant.*;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.*;
import static com.megatronix.paridhi.constant.MessageConstant.LogMessage.*;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.*;
import static com.megatronix.paridhi.constant.MessageConstant.UserMessage.ACCESS_DENIED;
import static com.megatronix.paridhi.util.LoggingUtil.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class EventComboService {

	private static final Logger log = LoggerFactory.getLogger(EventComboService.class);

	private final EventComboRepository eventComboRepository;
	private final EventRepository eventRepository;
	private final ImageStorageService imageStorageService;

	/*
	 * public methods — doesn't require authentication
	 */

	@Transactional ( readOnly = true )
	public List< CategorizedEventComboResponse > getAllEventCombos () {
		// step-1: fetch all event combos from the database
		List< EventCombo > combos = eventComboRepository.findAll();

		if ( log.isInfoEnabled() ) {
			logOperation(log, READ, EVENT_COMBO, null, format(COMBOS_RETRIEVED, combos.size()));
		}

		// step-2: group by domain and return
		return groupEventCombosByDomain(combos);
	}

	@Transactional ( readOnly = true )
	public EventComboPublicResponse getEventComboById ( Long eventComboId ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching event combo with ID: {}", eventComboId);
		}

		// step-1: fetch the combo — throws ComboNotFoundException if missing
		var combo = fetchEventComboById(eventComboId, null);

		// step-2: audit log the successful retrieval
		logOperation(log, READ, EVENT_COMBO, null, format(COMBO_RETRIEVED, combo.getEventComboName(), eventComboId));

		// step-3: map to public DTO and return
		return EventComboPublicResponse.fromCombo(combo);
	}

	@Transactional ( readOnly = true )
	public List< EventComboPublicResponse > getEventCombosByDomain ( Domain domain ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching event combos for domain: {}", domain);
		}

		// step-1: fetch combos by domain from the repository
		List< EventCombo > combos = eventComboRepository.findByDomain(domain);

		if ( log.isInfoEnabled() ) {
			logOperation(log, READ, EVENT_COMBO, null, format(COMBOS_RETRIEVED, combos.size()));
		}

		// step-2: map to public DTOs and return
		return combos.stream()
			.map(EventComboPublicResponse::fromCombo)
			.toList();
	}

	@Transactional ( readOnly = true )
	public List< CategorizedEventComboResponse > getEventCombosByStatus ( boolean isOpen ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Fetching event combos by registration status: {}", isOpen ? OPEN : CLOSED);
		}

		// step-1: fetch combos filtered by registration open/closed status
		List< EventCombo > combos = eventComboRepository.findByIsEventComboRegistrationOpen(isOpen);

		if ( log.isInfoEnabled() ) {
			logOperation(log, READ, EVENT_COMBO, null, format(COMBOS_RETRIEVED, combos.size()));
		}

		// step-2: group by domain and return
		return groupEventCombosByDomain(combos);
	}

	/*
	 * protected methods — require authentication
	 */

	@Transactional
	public EventComboAdminResponse createEventCombo ( EventComboRequest request, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to create combo: '{}' for domain: {}", request.name(), request.domain());
		}

		// step-1: security — only ADMIN/SUPERADMIN may create combos
		checkUserAccess(user, COMBO_CREATE);

		// step-2: fetch the referenced events by their IDs
		var events = eventRepository.findAllById(request.eventIds());

		// step-3: validate event existence and domain integrity
		validateEvents(request, events, request.domain(), user, COMBO_CREATE);

		// step-4: build and persist the new combo entity
		var combo = EventCombo.builder()
			.domain(request.domain())
			.eventComboName(request.name())
			.eventComboDescription(request.description())
			.events(new HashSet<>(events))
			.eventComboRegistrationFee(request.registrationFee())
			.build();

		var savedCombo = eventComboRepository.save(combo);

		// step-5: audit log the successful creation
		logOperation(log, COMBO_CREATE, EVENT_COMBO, user,
			format(COMBO_CREATED, savedCombo.getEventComboName(), savedCombo.getEventComboId(), user.getEmail(), user.getUserId()));

		return EventComboAdminResponse.fromCombo(savedCombo);
	}

	@Transactional
	public EventComboAdminResponse updateEventCombo ( Long id, EventComboRequest request, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update combo ID: {}", id);
		}

		// step-1: security — only ADMIN/SUPERADMIN may update combos
		checkUserAccess(user, COMBO_UPDATE);

		// step-2: fetch the existing combo — throws ComboNotFoundException if missing
		var combo = fetchEventComboById(id, user);

		// step-3: fetch the referenced events by their IDs
		var events = eventRepository.findAllById(request.eventIds());

		// step-4: validate event existence and domain integrity
		validateEvents(request, events, request.domain(), user, COMBO_UPDATE);

		// step-5: apply all updates to the combo fields
		combo.setEventComboName(request.name());
		combo.setEventComboDescription(request.description());
		combo.setDomain(request.domain());
		combo.setEventComboRegistrationFee(request.registrationFee());

		// step-6: mutate the existing Hibernate PersistentSet in-place to avoid orphan issues
		combo.getEvents().clear();
		combo.getEvents().addAll(events);

		var updatedCombo = eventComboRepository.save(combo);

		// step-7: audit log the successful update
		logOperation(log, COMBO_UPDATE, EVENT_COMBO, user,
			format(COMBO_UPDATED, updatedCombo.getEventComboName(), id, user.getEmail(), user.getUserId()));

		return EventComboAdminResponse.fromCombo(updatedCombo);
	}

	@Transactional
	public EventComboAdminResponse updateEventComboImage ( Long id, MultipartFile file, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to update image for combo ID: {} — file: '{}' ({} bytes)",
				id, file.getOriginalFilename(), file.getSize());
		}

		// step-1: security — only ADMIN/SUPERADMIN may update combo images
		checkUserAccess(user, COMBO_IMAGE_UPDATE);

		// step-2: fetch the existing combo
		var existingCombo = fetchEventComboById(id, user);

		// step-3: best-effort cleanup of the poster image (non-fatal)
		deleteOptionalComboImage(existingCombo, id, user, COMBO_IMAGE_UPDATE);

		// step-4: upload new image and persist updated URLs
		ImageUploadResult imageDetails = imageStorageService.uploadFile(file, COMBO);
		existingCombo.setEventComboPosterUrl(imageDetails.fileUrl());
		existingCombo.setEventComboPosterFileId(imageDetails.fileId());

		var updatedCombo = eventComboRepository.save(existingCombo);

		// step-5: audit log the successful image update
		logOperation(log, COMBO_IMAGE_UPDATE, COMBO_IMAGE, user,
			format(COMBO_IMAGE_UPDATED, updatedCombo.getEventComboName(), id, imageDetails.fileId(), user.getEmail(), user.getUserId()));

		return EventComboAdminResponse.fromCombo(updatedCombo);
	}

	@Transactional
	public void deleteEventCombo ( Long id, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to delete combo ID: {}", id);
		}

		// step-1: security — only ADMIN/SUPERADMIN may delete combos
		checkUserAccess(user, COMBO_DELETE);

		// step-2: fetch the existing combo — throws ComboNotFoundException if missing
		var existingCombo = fetchEventComboById(id, user);

		// step-3: best-effort cleanup of the poster image (non-fatal)
		deleteOptionalComboImage(existingCombo, id, user, COMBO_DELETE);

		// step-4: delete the combo from the database and audit log
		eventComboRepository.deleteById(id);
		logOperation(log, COMBO_DELETE, EVENT_COMBO, user,
			format(COMBO_DELETED_LOG, existingCombo.getEventComboName(), id, user.getEmail(), user.getUserId()));
	}

	@Transactional
	public EventComboAdminResponse toggleEventComboRegistrationStatus ( Long id, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to toggle registration status for combo ID: {}", id);
		}

		// step-1: security — only ADMIN/SUPERADMIN may toggle combo registration
		checkUserAccess(user, COMBO_TOGGLE_REGISTRATION);

		// step-2: fetch the existing combo
		var combo = fetchEventComboById(id, user);

		// step-3: flip registration status and persist
		boolean previousStatus = combo.isEventComboRegistrationOpen();
		combo.setEventComboRegistrationOpen(! previousStatus);
		var updatedCombo = eventComboRepository.save(combo);

		// step-4: audit log the toggle result (old -> new status)
		logOperation(log, COMBO_TOGGLE_REGISTRATION, EVENT_COMBO, user,
			format(COMBO_REGISTRATION_TOGGLED,
				updatedCombo.getEventComboName(), id,
				previousStatus ? OPEN : CLOSED,
				! previousStatus ? OPEN : CLOSED,
				user.getEmail(), user.getUserId()));

		return EventComboAdminResponse.fromCombo(updatedCombo);
	}

	@Transactional
	public EventComboAdminResponse addSingleEventToEventCombo ( Long eventComboId, Long eventId, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to add event ID: {} to combo ID: {}", eventId, eventComboId);
		}

		// step-1: security — only ADMIN/SUPERADMIN may mutate combo membership
		checkUserAccess(user, COMBO_ADD_EVENT);

		// step-2: fetch the combo and the event
		var combo = fetchEventComboById(eventComboId, user);
		var event = fetchEventById(eventId, user);

		// step-3: guard — enforce domain integrity before adding the event
		if ( ! event.getDomain().equals(combo.getDomain()) ) {
			logError(log, COMBO_ADD_EVENT, EVENT_COMBO, user,
				format(COMBO_DOMAIN_MISMATCH, event.getDomain(), combo.getDomain(), eventId, eventComboId), null);
			throw new InvalidDomainException(
				format(INVALID_DOMAIN, combo.getDomain()) + " (event domain: " + event.getDomain() + ")");
		}

		// step-4: add the event using the entity helper method and audit log
		combo.addEvent(event);
		logOperation(log, COMBO_ADD_EVENT, EVENT_COMBO, user,
			format(COMBO_EVENT_ADDED, event.getEventName(), eventId, combo.getEventComboName(), eventComboId, user.getEmail(), user.getUserId()));

		return EventComboAdminResponse.fromCombo(combo);
	}

	@Transactional
	public EventComboAdminResponse removeSingleEventFromEventCombo ( Long eventComboId, Long eventId, User user ) {
		if ( log.isDebugEnabled() ) {
			log.debug("Received request to remove event ID: {} from combo ID: {}", eventId, eventComboId);
		}

		// step-1: security — only ADMIN/SUPERADMIN may mutate combo membership
		checkUserAccess(user, COMBO_REMOVE_EVENT);

		// step-2: fetch the combo and the event
		var combo = fetchEventComboById(eventComboId, user);
		var event = fetchEventById(eventId, user);

		// step-3: guard — enforce minimum combo size (must have at least 2 events)
		if ( combo.getEvents().size() <= 2 ) {
			logError(log, COMBO_REMOVE_EVENT, EVENT_COMBO, user,
				format(COMBO_MIN_SIZE_VIOLATED, eventId, eventComboId), null);
			throw new IllegalArgumentException(
				"A combo must contain at least 2 events. You cannot remove this event without adding another one first.");
		}

		// step-4: remove the event using the entity helper method and audit log
		combo.removeEvent(event);
		logOperation(log, COMBO_REMOVE_EVENT, EVENT_COMBO, user,
			format(COMBO_EVENT_REMOVED, event.getEventName(), eventId, combo.getEventComboName(), eventComboId, user.getEmail(), user.getUserId()));

		return EventComboAdminResponse.fromCombo(combo);
	}

	/*
	 * private methods — used internally only
	 */

	private List< CategorizedEventComboResponse > groupEventCombosByDomain ( List< EventCombo > combos ) {
		return combos.stream()
			.collect(Collectors.groupingBy(
				e -> e.getDomain().name(),
				TreeMap::new,
				Collectors.mapping(EventComboPublicResponse::fromCombo, toList())
			))
			.entrySet().stream()
			.map(entry -> new CategorizedEventComboResponse(entry.getKey(), entry.getValue()))
			.toList();
	}

	private EventCombo fetchEventComboById ( Long id, User user ) {
		return eventComboRepository.findById(id)
			.orElseThrow(() -> {
				logError(log, READ, EVENT_COMBO, user, format(COMBO_NOT_FOUND, id), null);
				return new ComboNotFoundException(format(NOT_FOUND, COMBO));
			});
	}

	private Event fetchEventById ( Long eventId, User user ) {
		return eventRepository.findById(eventId)
			.orElseThrow(() -> {
				logError(log, READ, EVENT_COMBO, user, format(NOT_FOUND_LOG, EVENT, "ID", eventId), null);
				return new EventNotFoundException(format(NOT_FOUND, EVENT));
			});
	}

	private void checkUserAccess ( User user, String operation ) {
		// step-1: reject unauthenticated requests outright
		if ( user == null ) {
			logSecurity(log, AppConstant.ACCESS_DENIED, null, FAILED,
				format(AUTHENTICATION_REQUIRED, operation + " " + EVENT_COMBO));
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}

		// step-2: reject regular users — only ADMIN and above may perform combo mutations
		if ( user.getRole().equals(Role.ROLE_USER) ) {
			logSecurity(log, AppConstant.ACCESS_DENIED, user, FAILED,
				format(NOT_AUTHORIZED, operation + " " + EVENT_COMBO));
			throw new ForbiddenAccessException(ACCESS_DENIED);
		}

		// step-3: access is granted — log for observability at debug level
		if ( log.isDebugEnabled() ) {
			log.debug("Access granted to user: {} (ID: {}) for operation: {}", user.getEmail(), user.getUserId(), operation);
		}
	}

	private void validateEvents ( EventComboRequest request, List< Event > events, Domain domain, User user, String operation ) {
		// step-1: verify all requested event IDs resolved to actual entities
		if ( events.size() != request.eventIds().size() ) {
			logError(log, operation, EVENT_COMBO, user,
				format("One or more of the %d requested event IDs were not found.", request.eventIds().size()), null);
			throw new EventNotFoundException(format(NOT_FOUND, EVENT));
		}

		// step-2: verify all events belong to the specified domain
		boolean eventsAreFromSameDomain = events.stream()
			.allMatch(event -> event.getDomain().equals(domain));

		if ( ! eventsAreFromSameDomain ) {
			logError(log, operation, EVENT_COMBO, user,
				format("All events must belong to the same domain: %s", domain), null);
			throw new InvalidDomainException(format(INVALID_DOMAIN, domain));
		}
	}

	private void deleteOptionalComboImage ( EventCombo combo, Long id, User user, String operation ) {
		if ( combo.getEventComboPosterUrl() != null && combo.getEventComboPosterFileId() != null ) {
			try {
				imageStorageService.deleteFile(combo.getEventComboPosterFileId());
			} catch ( Exception e ) {
				logError(log, operation, IMAGE_STORAGE, user,
					format(COMBO_IMAGE_DELETE_FAILED, id, e.getMessage()), e);
			}
		}
	}
}
