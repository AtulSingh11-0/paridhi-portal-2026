package com.megatronix.paridhi.dto.request;

import com.megatronix.paridhi.constant.Department;
import com.megatronix.paridhi.constant.Year;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request payload for updating an existing user profile")
public record ProfileUpdateRequest(
	@NotBlank ( message = "Phone number cannot be empty" )
	@Pattern ( regexp = "^\\+?\\d{10,14}$", message = "Phone Number must be between 10 and 15 characters, optionally " +
		"starting with '+'" )
	@Schema(description = "Updated phone number (10-14 digits, optionally prefixed with '+')", example = "+9876543210")
	String phoneNumber,

	@NotBlank(message = "College name cannot be empty")
	@Schema(description = "Updated college or institution name", example = "NIT Trichy")
	String college,

	@NotNull(message = "Year cannot be null")
	@Schema(description = "Updated year of study", example = "FOURTH",
		allowableValues = {"FIRST", "SECOND", "THIRD", "FOURTH", "PASSOUT", "ALUMNI", "OTHER", "OTHERS", "NOT_APPLICABLE"})
	Year year,

	@NotNull(message = "Department cannot be null")
	@Schema(description = "Updated department or major", example = "ECE",
		allowableValues = {"CSE", "IT", "AIML", "DS", "CSBS", "CYS", "AIDS", "ECE", "EE", "CE", "ME", "BBA", "MBA", "BCA", "MCA", "BSC", "MSC", "BCOM", "MCOM", "BA", "MA", "LLB", "LLM", "OTHERS"})
	Department department,

	@NotBlank(message = "Roll number cannot be empty")
	@Schema(description = "Updated student roll number or ID", example = "ECE2023002")
	String rollNo
) {}