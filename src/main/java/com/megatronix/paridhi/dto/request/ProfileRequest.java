package com.megatronix.paridhi.dto.request;

import com.megatronix.paridhi.constant.Department;
import com.megatronix.paridhi.constant.Year;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema ( description = "Request payload for creating a new user profile" )
public record ProfileRequest(
	@NotBlank ( message = "Email cannot be empty" )
	@Email ( message = "Email should be valid" )
	@Schema ( description = "Must match the authenticated user's email", example = "john.doe@example.com" )
	String email,

	@NotBlank ( message = "Phone number cannot be empty" )
	@Pattern ( regexp = "^\\d{10}$", message = "Phone Number must be 10 digits" )
	@Schema ( description = "Phone number (10 digits)", example = "9876543210" )
	String phoneNumber,

	@NotBlank ( message = "College name cannot be empty" )
	@Schema ( description = "Name of the user's college or institution", example = "IIT Delhi" )
	String college,

	@NotNull ( message = "Year cannot be null" )
	@Schema (
		description = "Current year of study",
		example = "THIRD",
		allowableValues = {
			"FIRST", "SECOND", "THIRD", "FOURTH", "PASSOUT", "ALUMNI", "OTHER", "OTHERS", "NOT_APPLICABLE"
		}
	)
	Year year,

	@NotNull ( message = "Department cannot be null" )
	@Schema (
		description = "Department or major",
		example = "CSE",
		allowableValues = {
			"CSE", "IT", "AIML", "DS", "CSBS", "CYS", "AIDS", "ECE", "EE", "CE", "ME", "BBA", "MBA", "BCA", "MCA", "BSC", "MSC", "BCOM", "MCOM", "BA", "MA", "LLB", "LLM", "OTHERS"
		}
	)
	Department department,

	@NotBlank ( message = "Roll number cannot be empty" )
	@Schema ( description = "Student roll number or ID", example = "CSE2023001" )
	String rollNo
) {
}