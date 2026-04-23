package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.model.Team;
import lombok.Builder;

@Builder
public record ContactDto(
	String name,
	String phoneNumber
) {
	public static ContactDto fromContact ( Team.Contact contact ) {
		return ContactDto.builder()
			.name(contact.getName())
			.phoneNumber(contact.getPhoneNumber())
			.build();
	}
}
