package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.constant.Department;
import com.megatronix.paridhi.constant.Year;
import com.megatronix.paridhi.model.User;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Schema(description = "Response payload containing user profile details")
public record UserResponse(
	@Schema(description = "User's unique identifier", example = "1")
	Long id,
	@Schema(description = "URL to the user's profile picture")
	String profilePictureUrl,
	@Schema(description = "User's full name", example = "John Doe")
	String name,
	@Schema(description = "User's email address", example = "john.doe@example.com")
	String email,
	@Schema(description = "User's phone number", example = "+1234567890")
	String phoneNumber,
	@Schema(description = "User's college or institution name")
	String college,
	@Schema(description = "User's current year of study")
	Year year,
	@Schema(description = "User's department or major")
	Department department,
	@Schema(description = "User's roll number or student ID")
	String roll,
	@Schema(description = "List of GIDs associated with the user")
	List< String > gids,
	@Schema(description = "Indicates whether the user's email is verified", example = "true")
	boolean isVerified,
	@Schema(description = "Indicates whether the user has completed their profile setup", example = "true")
	boolean isProfileCreated,
	@Schema(description = "Timestamp of the user's last login")
	Instant lastLogin
) {
	public static UserResponse fromUser ( User user ) {
		return new UserResponse(
			user.getUserId(),
			user.getProfilePictureUrl(),
			user.getName(),
			user.getEmail(),
			user.getPhoneNumber(),
			user.getCollege(),
			user.getYear(),
			user.getDepartment(),
			user.getRollNo(),
			user.getGids(),
			user.isVerified(),
			user.isProfileCreated(),
			user.getLastLogin()
		);
	}
}