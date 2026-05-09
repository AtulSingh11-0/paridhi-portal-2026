package com.megatronix.paridhi.security;

import com.megatronix.paridhi.dto.request.GidListRequest;
import com.megatronix.paridhi.dto.response.TeamPublicResponse;
import com.megatronix.paridhi.model.User;
import com.megatronix.paridhi.repository.MRDRepository;
import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.service.TeamService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component
public class TeamSecurity {

	private final TeamService teamService;
	private final MRDRepository mrdRepository;
	private final UserRepository userRepository;

	public TeamSecurity (
		TeamService teamService,
		MRDRepository mrdRepository,
		UserRepository userRepository
	) {
		this.teamService = teamService;
		this.mrdRepository = mrdRepository;
		this.userRepository = userRepository;
	}

	public boolean isTeamMember ( String tid, String email ) {
		try {
			User user = userRepository.findUserByEmail(email).orElse(null);
			if ( user == null ) return false;

			TeamPublicResponse team = teamService.getTeamByTid(tid, user);

			// get GIDs for the email
			List< String > gids = mrdRepository.findGidListByUserEmail(email);

			// check if any of the user's GIDs are in the team
			return team.gidList().stream().anyMatch(gids::contains);
		} catch ( Exception e ) {
			return false;
		}
	}

	public boolean isOwnerOfGid ( String gid, String email ) {
		try {
			return mrdRepository.existsByGidAndUserEmail(gid, email);
		} catch ( Exception e ) {
			return false;
		}
	}

	public boolean isOwnerOfGids ( GidListRequest request, String email ) {
		try {
			var gids = request.gids();

			// get GIDs for the email
			List< String > userGids = mrdRepository.findGidListByUserEmail(email);

			// check if all the provided GIDs are in the user's GIDs
			return new HashSet<>(userGids).containsAll(gids);
		} catch ( Exception e ) {
			return false;
		}
	}
}

