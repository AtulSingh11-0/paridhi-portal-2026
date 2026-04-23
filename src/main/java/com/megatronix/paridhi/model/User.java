package com.megatronix.paridhi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.megatronix.paridhi.constant.Department;
import com.megatronix.paridhi.constant.Role;
import com.megatronix.paridhi.constant.Year;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.megatronix.paridhi.constant.AppConstant.DEFAULT_PROFILE_PIC;
import static com.megatronix.paridhi.constant.Role.ROLE_USER;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table ( name = "users" )
public class User extends Auditable implements UserDetails {
	@Id
	@GeneratedValue ( strategy = IDENTITY )
	@Column ( name = "user_id", updatable = false, nullable = false )
	private Long userId;

	@Column ( name = "name", nullable = false )
	private String name;

	@Column ( name = "email", unique = true, nullable = false )
	private String email;

	@Column ( name = "password", nullable = false, length = 64 )
	private String password;

	@Column ( name = "profile_picture_url" )
	@Builder.Default
	private String profilePictureUrl = DEFAULT_PROFILE_PIC;

	@Column ( name = "phone_number", length = 15 )
	private String phoneNumber;

	@Column ( name = "college" )
	private String college;

	@Enumerated ( STRING )
	private Year year;

	@Enumerated ( STRING )
	private Department department;

	@Column ( name = "roll_no" )
	private String rollNo;

	@OneToMany (
		mappedBy = "user",
		cascade = CascadeType.ALL,
		fetch = FetchType.LAZY,
		orphanRemoval = true
	)
	@Builder.Default
	private transient List< MRD > mrdRegistrations = new ArrayList<>();

	@Column ( name = "is_verified", nullable = false )
	@Builder.Default
	private boolean isVerified = false;

	@Column ( name = "is_profile_created", nullable = false )
	@Builder.Default
	private boolean isProfileCreated = false;

	@Enumerated ( STRING )
	@Column ( nullable = false )
	@Builder.Default
	private Role role = ROLE_USER;

	@Column ( name = "last_login" )
	private Instant lastLogin;

	@JsonIgnore
	public List< String > getGids () {
		if ( mrdRegistrations == null ) {
			return new ArrayList<>();
		}
		return mrdRegistrations.stream()
			.map(MRD::getGid)
			.toList();
	}

	@Override
	public Collection< ? extends GrantedAuthority > getAuthorities () {
		return Collections.singletonList(
			new SimpleGrantedAuthority(this.role.name())
		);
	}

	@Override
	public String getUsername () {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired () {
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked () {
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired () {
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled () {
		return UserDetails.super.isEnabled();
	}
}
