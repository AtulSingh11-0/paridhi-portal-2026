package com.megatronix.paridhi.service;

import com.megatronix.paridhi.repository.UserRepository;
import com.megatronix.paridhi.util.LoggingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.megatronix.paridhi.constant.AppConstant.Common.FAILED;
import static com.megatronix.paridhi.constant.AppConstant.Resource.EMAIL;
import static com.megatronix.paridhi.constant.AppConstant.Resource.USER;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.NOT_FOUND;
import static com.megatronix.paridhi.constant.MessageConstant.ErrorTemplate.NOT_FOUND_LOG;
import static com.megatronix.paridhi.constant.MessageConstant.Operation.USER_LOGIN;
import static java.lang.String.format;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	private final UserRepository userRepository;

	public UserDetailsServiceImpl ( UserRepository userRepository ) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername ( String username ) throws UsernameNotFoundException {
		if ( log.isDebugEnabled() ) {
			log.debug("Loading user details for username (email): {}", username);
		}

		// step-1: attempt to fetch the USER from the DB by email
		return userRepository.findUserByEmail(username)
			.orElseThrow(() -> {
				LoggingUtil.logSecurity(log, USER_LOGIN, null, FAILED, format(NOT_FOUND_LOG, USER, EMAIL, username));
				return new UsernameNotFoundException(format(NOT_FOUND, USER));
			});
	}
}
