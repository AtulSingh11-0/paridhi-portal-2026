package com.megatronix.paridhi.model;

import com.megatronix.paridhi.constant.Department;
import com.megatronix.paridhi.constant.Year;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table ( name = "mrd_registrations" )
public class MRD extends Auditable implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue ( strategy = GenerationType.IDENTITY )
	@Column ( name = "mrd_id", updatable = false, nullable = false )
	private Long mrdId;

	@ManyToOne ( fetch = FetchType.LAZY )
	@JoinColumn ( name = "user_id", nullable = false )
	private User user;

	@Column ( unique = true, nullable = false, updatable = false )
	private String gid;

	@Column( name = "name", nullable = false )
	private String name;

	@Column ( name = "email", nullable = false )
	private String email;

	@Column ( name = "phone_number", nullable = false, length = 15 )
	private String phoneNumber;

	@Column ( name = "college", nullable = false )
	private String college;

	@Enumerated ( EnumType.STRING )
	@Column( name = "year", nullable = false )
	private Year year;

	@Enumerated ( EnumType.STRING )
	@Column( name = "department", nullable = false )
	private Department department;

	@Column ( name = "roll_no")
	private String rollNo;

	@Column ( name = "has_paid", nullable = false )
	@Builder.Default
	private boolean hasPaid = false;
}
