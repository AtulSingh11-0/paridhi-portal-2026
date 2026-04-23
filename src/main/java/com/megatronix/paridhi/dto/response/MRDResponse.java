package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.constant.Department;
import com.megatronix.paridhi.constant.Year;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MRDResponse {
	private Long id;
	private String gid;
	private String name;
	private String email;
	private String phoneNumber;
	private String contact;
	private String college;
	private Year year;
	private Department department;
	private String rollNo;
	private boolean hasPaid;
	private Instant registeredAt;
}