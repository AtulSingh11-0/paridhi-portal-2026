package com.megatronix.paridhi.security;

import com.megatronix.paridhi.dto.response.TeamPublicResponse;
import com.megatronix.paridhi.repository.MRDRepository;
import com.megatronix.paridhi.service.TeamService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamSecurity {
	private final TeamService teamService;
	private final MRDRepository mrdRepository;

	public TeamSecurity (
		TeamService teamService,
		MRDRepository mrdRepository
	) {
		this.teamService = teamService;
		this.mrdRepository = mrdRepository;
	}

	public boolean isTeamMember ( String tid, String email ) {
		try {
			TeamPublicResponse team = teamService.getTeamByTid(tid, email);

			// get GIDs for the email
			List< String > gids = mrdRepository.findGidListByUserEmail(email);

			// check if any of the user's GIDs are in the team
			return team.gidList().stream().anyMatch(gids::contains);
		} catch ( Exception e ) {
			return false;
		}
	}

	public boolean isGidOwner ( String gid, String email ) {
		try {
			return mrdRepository.existsByGidAndUserEmail(gid, email);
		} catch ( Exception e ) {
			return false;
		}
	}
}
