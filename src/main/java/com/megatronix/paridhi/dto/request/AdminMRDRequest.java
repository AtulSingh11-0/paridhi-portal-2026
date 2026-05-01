package com.megatronix.paridhi.dto.request;

import com.megatronix.paridhi.constant.Department;
import com.megatronix.paridhi.constant.Year;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request payload for registering an MRD by an admin")
public class AdminMRDRequest {
	@NotBlank(message = "Email is required")
	@Email(message = "Email should be valid")
	@Schema(description = "Email of the user being registered", example = "john@doe.com")
	private String email;

	@NotBlank(message = "Name is required")
	@Schema(description = "Full name of the user", example = "John Doe")
	private String name;

	@NotBlank(message = "Contact number is required")
	@Pattern(regexp = "^\\d{10}$", message = "Contact must be 10 digits")
	@Schema(description = "10-digit contact number", example = "9876543210")
	private String contact;

	@NotBlank(message = "College is required")
	@Schema(description = "Name of the college", example = "MSIT")
	private String college;

	@NotNull(message = "Year is required")
	@Schema(description = "Current academic year of the user", example = "FIRST")
	private Year year;

	@NotNull(message = "Department is required")
	@Schema(description = "Academic department of the user", example = "CSE")
	private Department department;

	@NotBlank(message = "Roll number is required")
	@Schema(description = "College roll number or university registration number", example = "12345678901")
	private String rollNo;
}