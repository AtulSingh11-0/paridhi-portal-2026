package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.model.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Response payload representing a contact person for a team")
public record ContactDto(
	@Schema(description = "Name of the contact person", example = "John Doe")
	String name,
	
	@Schema(description = "Phone number of the contact person", example = "9876543210")
	String phoneNumber
) {
	public static ContactDto fromContact ( Team.Contact contact ) {
		return ContactDto.builder()
			.name(contact.getName())
			.phoneNumber(contact.getPhoneNumber())
			.build();
	}
}
