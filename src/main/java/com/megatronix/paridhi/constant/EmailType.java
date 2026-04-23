package com.megatronix.paridhi.constant;

import com.megatronix.paridhi.model.Team;
import com.megatronix.paridhi.service.EmailService;

public enum EmailType {
	REGISTRATION {
		@Override
		public void sendEmail ( EmailService emailService, String[] emails, Team t ) {
			emailService.sendEventRegistration(emails, t.getEvent().getEventName(), t.getTeamName(), t.getTid());
		}
	},
	QUALIFICATION {
		@Override
		public void sendEmail ( EmailService emailService, String[] emails, Team t ) {
			emailService.sendQualificationCongratulations(emails, t.getEvent().getEventName(), t.getTeamName(), t.getTid());
		}
	},
	POSITION {
		@Override
		public void sendEmail ( EmailService emailService, String[] emails, Team t ) {
			emailService.sendPositionCongratulations(emails, t.getEvent().getEventName(), t.getTeamName(), t.getTid(), t.getPosition());
		}
	};

	public abstract void sendEmail ( EmailService emailService, String[] emails, Team t );
}
