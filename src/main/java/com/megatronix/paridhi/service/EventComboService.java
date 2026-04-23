package com.megatronix.paridhi.service;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.Domain;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.constant.Role;
import com.megatronix.paridhi.dto.request.EventComboRequest;
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
import com.megatronix.paridhi.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

@Service
public class EventComboService {
	private static final Logger log = LoggerFactory.getLogger(EventComboService.class);
	private final EventComboRepository eventComboRepository;
	private final EventRepository eventRepository;
	private final ImageStorageService imageStorageService;

	public EventComboService (
		EventComboRepository eventComboRepository,
		EventRepository eventRepository,
		ImageStorageService imageStorageService
	) {
		this.eventComboRepository = eventComboRepository;
		this.eventRepository = eventRepository;
		this.imageStorageService = imageStorageService;
	}

	/*
	 * Public methods - don't require authentication
	 */

	/**
	 * Retrieves all event combos from the database
	 *
	 * @return List of EventComboPublicResponse objects containing all combos
	 */
	public List< EventComboPublicResponse > getAllEventCombos () {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.COMBO_SERVICE,
			null,
			"Fetching all combos"
		);

		// fetch all combos from the repository
		List< EventCombo > combos = eventComboRepository.findAll();

		// log the successful retrieval of combos
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.COMBO_SERVICE,
			null,
			String.format(MessageConstant.SuccessTemplate.FETCHED_COUNT, combos.size(), "combos")
		);

		// return the list of combos as EventComboPublicResponse objects
		return combos.stream()
			.map(EventComboPublicResponse::fromCombo)
			.toList();
	}

	/**
	 * Retrieves a specific combo by its ID
	 *
	 * @param id The ID of the combo to retrieve
	 *
	 * @return EventComboPublicResponse object containing the combo details
	 *
	 * @throws ComboNotFoundException if combo with given ID doesn't exist
	 */
	public EventComboPublicResponse getEventComboById ( Long id ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.COMBO_SERVICE,
			null,
			"Fetching combo with ID: " + id
		);

		// find the combo
		var combo = fetchEventComboById(id, null);

		// log the successful retrieval of the combo
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.COMBO_SERVICE,
			null,
			String.format(MessageConstant.SuccessTemplate.FETCHED, "combo with ID: " + id + " - " + combo.getEventComboName())
		);

		// return the combo as a EventComboPublicResponse object
		return EventComboPublicResponse.fromCombo(combo);
	}

	/**
	 * Retrieves all combos for a specific domain
	 *
	 * @param domain The domain to filter combos by
	 *
	 * @return List of EventComboPublicResponse objects for the specified domain
	 */
	public List< EventComboPublicResponse > getEventCombosByDomain ( Domain domain ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.COMBO_SERVICE,
			null,
			"Fetching combos for domain: " + domain
		);

		// fetch combos by domain from the repository
		List< EventCombo > combos = eventComboRepository.findByDomain(domain);

		// log the successful retrieval of combos
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.COMBO_SERVICE,
			null,
			String.format(MessageConstant.SuccessTemplate.FETCHED_COUNT, combos.size(), "combos for domain: " + domain)
		);

		// return the list of combos as EventComboPublicResponse objects
		return combos.stream()
			.map(EventComboPublicResponse::fromCombo)
			.toList();
	}

	/**
	 * Retrieves combos filtered by their registration status
	 *
	 * @param isEventComboRegistrationOpen true to get combos with open registration, false
	 *                                     for closed registration
	 *
	 * @return List of EventComboPublicResponse objects with the specified registration status
	 */
	public List< EventComboPublicResponse > getEventCombosByStatus ( boolean isEventComboRegistrationOpen ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.COMBO_SERVICE,
			null,
			"Fetching combos by registration status: " + ( isEventComboRegistrationOpen ? AppConstant.OPEN : AppConstant.CLOSED )
		);

		// fetch combos by registration status from the repository
		List< EventCombo > combos = eventComboRepository.findByIsEventComboRegistrationOpen(isEventComboRegistrationOpen);

		// log the successful retrieval of combos
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.READ,
			AppConstant.COMBO_SERVICE,
			null,
			String.format(MessageConstant.SuccessTemplate.FETCHED_COUNT, combos.size(), "combos with registration status: " + ( isEventComboRegistrationOpen ? AppConstant.OPEN : AppConstant.CLOSED ))
		);

		// return the list of combos as EventComboPublicResponse objects
		return combos.stream()
			.map(EventComboPublicResponse::fromCombo)
			.toList();
	}

	/*
	 * Protected methods - require authentication
	 */

	/**
	 * Creates a new event combo
	 *
	 * @param request The combo creation request containing all necessary details
	 * @param user    The authenticated user creating the combo
	 *
	 * @return EventComboPublicResponse object for the newly created combo
	 *
	 * @throws EventNotFoundException   if any event in the request doesn't exist
	 * @throws InvalidDomainException   if events are from different domains
	 * @throws ForbiddenAccessException if user lacks permission
	 */
	@Transactional
	public EventComboAdminResponse createEventCombo ( EventComboRequest request, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.CREATE,
			AppConstant.COMBO_SERVICE,
			user,
			"Creating combo: " + request.name()
		);

		// validate user access
		checkUserAccess(user, MessageConstant.Operation.CREATE);

		// get events from IDs
		var events = eventRepository.findAllById(request.eventIds());

		// validate if all events exist and are from the same domain
		validateEvents(request, events, request.domain(), user, MessageConstant.Operation.CREATE);

		// create a new combo builder object
		var combo = EventCombo.builder()
			.domain(request.domain())
			.eventComboName(request.name())
			.eventComboDescription(request.description())
			.events(new HashSet<>(events))
			.eventComboRegistrationFee(request.registrationFee())
			.build();

		// save the combo to the database
		var savedCombo = eventComboRepository.save(combo);

		// log the successful creation of the combo
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.CREATE,
			AppConstant.COMBO_SERVICE,
			user,
			String.format(MessageConstant.SuccessTemplate.CREATED, AppConstant.COMBO, savedCombo.getEventComboId()) + " - " + savedCombo.getEventComboName()
		);

		// return the EventComboAdminResponse object
		return EventComboAdminResponse.fromCombo(savedCombo);
	}

	/**
	 * Updates an existing combo with new information
	 *
	 * @param id      ID of the combo to update
	 * @param request The combo update request containing all necessary details
	 * @param user    The authenticated user updating the combo
	 *
	 * @return EventComboPublicResponse object for the updated combo
	 *
	 * @throws ComboNotFoundException   if combo with given ID doesn't exist
	 * @throws EventNotFoundException   if any event in the request doesn't exist
	 * @throws InvalidDomainException   if events are from different domains
	 * @throws ForbiddenAccessException if user lacks permission
	 */

	@Transactional
	public EventComboAdminResponse updateEventCombo ( Long id, EventComboRequest request, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_SERVICE,
			user,
			"Updating combo with ID: " + id
		);

		// validate user access
		checkUserAccess(user, MessageConstant.Operation.UPDATE);

		// find the combo
		var combo = fetchEventComboById(id, user);

		// get events from IDs
		var events = eventRepository.findAllById(request.eventIds());

		// validate if all events exist
		validateEvents(request, events, request.domain(), user, MessageConstant.Operation.UPDATE);

		// update the combo
		combo.setEventComboName(request.name());
		combo.setEventComboDescription(request.description());
		combo.setDomain(request.domain());
		combo.setEventComboRegistrationFee(request.registrationFee());

		// clear existing events and set new ones (MUTATE the existing Hibernate PersistentSet)
		combo.getEvents().clear();
		combo.getEvents().addAll(events);

		// save and return the combo
		var updatedCombo = eventComboRepository.save(combo);

		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_SERVICE,
			user,
			String.format(MessageConstant.SuccessTemplate.UPDATED, AppConstant.COMBO, id) + " - " + updatedCombo.getEventComboName()
		);

		// return the EventComboAdminResponse object
		return EventComboAdminResponse.fromCombo(updatedCombo);
	}

	/**
	 * Updates the image associated with a combo
	 *
	 * @param id   ID of the combo to update
	 * @param file The image file to upload
	 * @param user The authenticated user updating the combo image
	 *
	 * @return EventComboPublicResponse object for the updated combo
	 *
	 * @throws ComboNotFoundException   if combo with given ID doesn't exist
	 * @throws ForbiddenAccessException if user lacks permission
	 */
	@Transactional
	public EventComboAdminResponse updateEventComboImage ( Long id, MultipartFile file, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_IMAGE,
			user,
			"Updating image for combo with ID: " + id + " - File: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes)"
		);

		// validate user access
		checkUserAccess(user, "update image of");

		// get combo by ID
		var existingCombo = fetchEventComboById(id, user);

		// delete old image if it exists
		if ( existingCombo.getEventComboPosterUrl() != null && existingCombo.getEventComboId() != null ) {
			try {
				imageStorageService.deleteFile(existingCombo.getEventComboPosterFileId());
			} catch ( Exception e ) {
				// log but continue - failure to delete old image shouldn't stop upload of new image
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.DELETE,
					AppConstant.IMAGE_STORAGE,
					user,
					"Failed to delete previous image for combo ID: " + id + " - Continuing with upload - Error: " + e.getMessage(),
					e
				);
			}
		}

		ImageUploadResult imageDetails = imageStorageService.uploadFile(file, AppConstant.COMBO);

		// update combo image fields
		existingCombo.setEventComboPosterUrl(imageDetails.fileUrl());
		existingCombo.setEventComboPosterFileId(imageDetails.fileId());

		// save updated combo
		var updatedCombo = eventComboRepository.save(existingCombo);

		// log the successful image update
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_IMAGE,
			user,
			"Successfully updated image for combo ID: " + id + " - New image public ID: " + imageDetails.fileId()
		);

		// return the updated combo as EventComboAdminResponse object
		return EventComboAdminResponse.fromCombo(updatedCombo);
	}

	/**
	 * Deletes a combo and its associated image
	 *
	 * @param id   ID of the combo to delete
	 * @param user The authenticated user deleting the combo
	 *
	 * @throws ComboNotFoundException   if combo with given ID doesn't exist
	 * @throws ForbiddenAccessException if user lacks permission
	 */
	@Transactional
	public void deleteEventCombo ( Long id, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.DELETE,
			AppConstant.COMBO_SERVICE,
			user,
			"Deleting combo with ID: " + id
		);

		// validate user access
		checkUserAccess(user, MessageConstant.Operation.DELETE);

		// find the combo
		var existingCombo = fetchEventComboById(id, user);

		// delete image if it exists
		if ( existingCombo.getEventComboPosterUrl() != null && existingCombo.getEventComboPosterFileId() != null ) {
			try {
				imageStorageService.deleteFile(existingCombo.getEventComboPosterFileId());
			} catch ( Exception e ) {
				// log but continue - failure to delete image shouldn't stop combo deletion
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.DELETE,
					AppConstant.IMAGE_STORAGE,
					user,
					"Failed to delete image for combo ID: " + id + " - Error: " + e.getMessage(),
					e
				);
			}
		}

		// delete the combo
		eventComboRepository.deleteById(id);

		// log the successful deletion of the combo
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.DELETE,
			AppConstant.COMBO_SERVICE,
			user,
			String.format(MessageConstant.SuccessTemplate.DELETED, AppConstant.COMBO, id) + " - " + existingCombo.getEventComboName()
		);
	}

	/**
	 * Toggles the registration status of a combo between open and closed
	 *
	 * @param id   ID of the combo to update
	 * @param user The authenticated user toggling the status
	 *
	 * @return EventComboPublicResponse object for the updated combo
	 *
	 * @throws ComboNotFoundException   if combo with given ID doesn't exist
	 * @throws ForbiddenAccessException if user lacks permission
	 */
	@Transactional
	public EventComboAdminResponse toggleEventComboRegistrationStatus ( Long id, User user ) {
		// log the operation
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_SERVICE,
			user,
			"Toggling registration status for combo with ID: " + id
		);

		// validate user access
		checkUserAccess(user, "toggle registration status of");

		// find the combo
		var combo = fetchEventComboById(id, user);

		// get current status for logging
		boolean currentStatus = combo.isEventComboRegistrationOpen();
		boolean newStatus = ! currentStatus;

		// toggle the status
		combo.setEventComboRegistrationOpen(newStatus);

		// save and return the combo
		var updatedCombo = eventComboRepository.save(combo);

		// log the successful status update
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_SERVICE,
			user,
			String.format(MessageConstant.SuccessTemplate.STATUS_CHANGED, "registration status for combo ID: " + id,
				( currentStatus ? AppConstant.OPEN : AppConstant.CLOSED ), ( newStatus ? AppConstant.OPEN :
					AppConstant.CLOSED )) + " - Name: " + updatedCombo.getEventComboName()
		);

		// return the updated combo as EventComboAdminResponse object
		return EventComboAdminResponse.fromCombo(updatedCombo);
	}

	/**
	 * Adds a single event to an existing combo
	 *
	 * @param eventComboId ID of the combo
	 * @param eventId      ID of the event to add
	 * @param user         The authenticated user performing the action
	 *
	 * @return EventComboAdminResponse object for the updated combo
	 */
	@Transactional
	public EventComboAdminResponse addSingleEventToEventCombo ( Long eventComboId, Long eventId, User user ) {
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_SERVICE,
			user,
			"Adding event ID: " + eventId + " to combo ID: " + eventComboId
		);

		checkUserAccess(user, MessageConstant.Operation.UPDATE);

		var combo = fetchEventComboById(eventComboId, user);

		var event = eventRepository.findById(eventId)
			.orElseThrow(
				() -> new EventNotFoundException(
					String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT)
				)
			);

		// 1. Business Rule: Check Domain Integrity
		if ( ! event.getDomain().equals(combo.getDomain()) ) {
			LoggingUtil.logError(
				log,
				MessageConstant.Operation.UPDATE,
				AppConstant.COMBO_SERVICE,
				user,
				"Cannot add event. Domain mismatch.", null
			);
			throw new InvalidDomainException("Cannot add event to combo. The event domain (" + event.getDomain() + ") does not match the combo domain (" + combo.getDomain() + ").");
		}

		// 2. Add the event using your entity helper method!
		combo.addEvent(event);

		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_SERVICE,
			user,
			"Successfully added event ID: " + eventId + " to combo: " + combo.getEventComboName()
		);

		return EventComboAdminResponse.fromCombo(combo);
	}

	/**
	 * Removes a single event from an existing combo
	 *
	 * @param eventComboId ID of the combo
	 * @param eventId      ID of the event to remove
	 * @param user         The authenticated user performing the action
	 *
	 * @return EventComboAdminResponse object for the updated combo
	 */
	@Transactional
	public EventComboAdminResponse removeSingleEventFromEventCombo ( Long eventComboId, Long eventId, User user ) {
		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_SERVICE,
			user,
			"Removing event ID: " + eventId + " from combo ID: " + eventComboId
		);

		checkUserAccess(user, MessageConstant.Operation.UPDATE);

		var combo = fetchEventComboById(eventComboId, user);

		var event = eventRepository.findById(eventId)
			.orElseThrow(
				() -> new EventNotFoundException(
					String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT)
				)
			);

		// 1. Business Rule: Enforce minimum combo size
		if ( combo.getEvents().size() <= 2 ) {
			LoggingUtil.logError(
				log,
				MessageConstant.Operation.UPDATE,
				AppConstant.COMBO_SERVICE,
				user,
				"Cannot remove event. Combo must have at least 2 events.",
				null
			);
			throw new IllegalArgumentException("A combo must contain at least 2 events. You cannot remove this event without adding another one first.");
		}

		// 2. Remove the event using your entity helper method!
		combo.removeEvent(event);

		LoggingUtil.logOperation(
			log,
			MessageConstant.Operation.UPDATE,
			AppConstant.COMBO_SERVICE,
			user,
			"Successfully removed event ID: " + eventId + " from combo: " + combo.getEventComboName()
		);

		return EventComboAdminResponse.fromCombo(combo);
	}

	/*
	 * Private methods - is used internally only
	 */

	/**
	 * Validates that the user has appropriate permissions to perform operations on
	 * combos
	 *
	 * @param user       The user attempting the operation
	 * @param methodType The operation type being performed
	 *
	 * @throws ForbiddenAccessException if the user lacks appropriate permissions
	 */
	private void checkUserAccess ( User user, String methodType ) {
		// check if user is null
		if ( user == null ) {
			LoggingUtil.logSecurity(
				log,
				AppConstant.ACCESS_DENIED,
				null,
				AppConstant.FAILED,
				String.format(MessageConstant.ErrorTemplate.AUTHENTICATION_REQUIRED, methodType + " " + AppConstant.COMBO)
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
				String.format(MessageConstant.ErrorTemplate.NOT_AUTHORIZED, methodType + " " + AppConstant.COMBO)
			);
			throw new ForbiddenAccessException(MessageConstant.UserMessage.ACCESS_DENIED);
		}

		// log the successful authorization
		LoggingUtil.logSecurity(
			log,
			AppConstant.ACCESS_GRANTED,
			user,
			AppConstant.SUCCESS,
			String.format(MessageConstant.SuccessTemplate.AUTHORIZED, methodType + " " + AppConstant.COMBO)
		);
	}

	/**
	 * Validates that all events in the request exist and belong to the same domain
	 *
	 * @param request   The combo creation request containing event IDs
	 * @param events    List of events fetched from the database
	 * @param domain    The domain to check against
	 * @param user      The authenticated user creating the combo
	 * @param operation The operation being performed (create/update)
	 *
	 * @throws EventNotFoundException if any event doesn't exist
	 * @throws InvalidDomainException if events are from different domains
	 */
	private void validateEvents ( EventComboRequest request, List< Event > events, Domain domain, User user, String operation ) {
		// check if all events exist
		if ( events.size() != request.eventIds().size() ) {
			LoggingUtil.logError(
				log,
				MessageConstant.Operation.CREATE,
				AppConstant.COMBO_SERVICE,
				user,
				"One or more events not found for combo creation", null
			);
			throw new EventNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.EVENT));
		}

		// check if all events belong to the same domain
		boolean eventsAreFromSameDomain = events.stream()
			.allMatch(event -> event.getDomain().equals(domain));

		if ( ! eventsAreFromSameDomain ) {
			LoggingUtil.logError(
				log,
				operation,
				AppConstant.COMBO_SERVICE,
				user,
				"All events must belong to the same domain: " + domain,
				null
			);
			throw new InvalidDomainException(String.format(MessageConstant.ErrorTemplate.INVALID_DOMAIN, domain));
		}
	}

	/**
	 * Fetches a combo by its ID from the database
	 *
	 * @param id   The ID of the combo to fetch
	 * @param user The authenticated user requesting the combo
	 *
	 * @return EventCombo		object containing the combo details
	 *
	 * @throws ComboNotFoundException if combo with given ID doesn't exist
	 */
	private EventCombo fetchEventComboById ( Long id, User user ) {
		return eventComboRepository.findById(id)
			.orElseThrow(() -> {
				LoggingUtil.logError(
					log,
					MessageConstant.Operation.READ,
					AppConstant.COMBO_SERVICE,
					user,
					String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.COMBO),
					null
				);
				return new ComboNotFoundException(String.format(MessageConstant.ErrorTemplate.NOT_FOUND, AppConstant.COMBO));
			});
	}

}