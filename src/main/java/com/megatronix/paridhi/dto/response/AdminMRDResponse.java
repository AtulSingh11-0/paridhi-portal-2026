package com.megatronix.paridhi.dto.response;

import com.megatronix.paridhi.constant.Department;
import com.megatronix.paridhi.constant.Year;
import com.megatronix.paridhi.model.MRD;
import lombok.Builder;

import java.time.Instant;

@Builder
public record AdminMRDResponse(
	Long id,
	String gid,
	String name,
	String email,
	String phoneNumber,
	String college,
	Year year,
	Department department,
	String rollNo,
	boolean hasPaid,
	Instant createdAt,
	Instant updatedAt,
	String createdBy,
	String updatedBy
) {
	public static AdminMRDResponse from ( MRD mrd ) {
		return AdminMRDResponse.builder()
			.id(mrd.getMrdId())
			.gid(mrd.getGid())
			.name(mrd.getName())
			.email(mrd.getEmail())
			.phoneNumber(mrd.getPhoneNumber())
			.college(mrd.getCollege())
			.year(mrd.getYear())
			.department(mrd.getDepartment())
			.rollNo(mrd.getRollNo())
			.hasPaid(mrd.isHasPaid())
			.createdAt(mrd.getCreatedAt())
			.updatedAt(mrd.getUpdatedAt())
			.createdBy(mrd.getCreatedBy())
			.updatedBy(mrd.getUpdatedBy())
			.build();
	}
}
