package com.megatronix.paridhi.dto.request;

import com.megatronix.paridhi.constant.Department;
import com.megatronix.paridhi.constant.Year;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
@Schema ( description = "Request payload for registering an MRD by an admin" )
public record AdminMRDRequest(

	@NotBlank ( message = "Email is required" )
	@Email ( message = "Email should be valid" )
	@Schema ( description = "Email of the user being registered", example = "john@doe.com" )
	String email,

	@NotBlank ( message = "Name is required" )
	@Schema ( description = "Full name of the user", example = "John Doe" )
	String name,

	@NotBlank ( message = "Contact number is required" )
	@Pattern ( regexp = "^\\d{10}$", message = "Contact must be 10 digits" )
	@Schema ( description = "10-digit contact number", example = "9876543210" )
	String contact,

	@NotBlank ( message = "College is required" )
	@Schema ( description = "Name of the college", example = "MSIT" )
	String college,

	@NotNull ( message = "Year is required" )
	@Schema ( description = "Current academic year of the user", example = "FIRST" )
	Year year,

	@NotNull ( message = "Department is required" )
	@Schema ( description = "Academic department of the user", example = "CSE" )
	Department department,

	@NotBlank ( message = "Roll number is required" )
	@Schema ( description = "College roll number or university registration number", example = "12345678901" )
	String rollNo
) {

}