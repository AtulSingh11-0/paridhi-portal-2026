package com.megatronix.paridhi.constant;

import com.megatronix.paridhi.model.Team;
import com.megatronix.paridhi.service.EmailService;

public enum EmailType {
	REGISTRATION {
		@Override
		public void sendEmail ( EmailService emailService, String[] emails, Team t, com.megatronix.paridhi.model.User user ) {
			emailService.sendEventRegistration(emails, t.getEvent().getEventName(), t.getTeamName(), t.getTid(), user);
		}
	},
	QUALIFICATION {
		@Override
		public void sendEmail ( EmailService emailService, String[] emails, Team t, com.megatronix.paridhi.model.User user ) {
			emailService.sendQualificationCongratulations(emails, t.getEvent().getEventName(), t.getTeamName(), t.getTid(), user);
		}
	},
	POSITION {
		@Override
		public void sendEmail ( EmailService emailService, String[] emails, Team t, com.megatronix.paridhi.model.User user ) {
			emailService.sendPositionCongratulations(emails, t.getEvent().getEventName(), t.getTeamName(), t.getTid(), t.getPosition(), user);
		}
	};

	public abstract void sendEmail ( EmailService emailService, String[] emails, Team t, com.megatronix.paridhi.model.User user );
}
