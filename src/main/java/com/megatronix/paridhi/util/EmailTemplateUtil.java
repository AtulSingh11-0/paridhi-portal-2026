package com.megatronix.paridhi.util;

import com.megatronix.paridhi.constant.AppConstant;
import com.megatronix.paridhi.constant.MessageConstant;
import com.megatronix.paridhi.constant.Position;

/**
 * Utility class for generating HTML email templates with consistent branding.
 */
public final class EmailTemplateUtil {

	private EmailTemplateUtil () {
		// Utility class
	}

	/**
	 * Generates HTML content for password reset token email.
	 */
	public static String getResetTokenContent ( String name, String token ) {
		return """
			<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>%s - Password Reset Token</title>
			</head>
			<body style="font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; color: #FFFFFF; background-color: %s;">
				<div class="card" style="border: 1px solid #333333; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.3); background-color: %s;">
					<div style="text-align: center; padding: 20px; background-color: %s; color: white; border-bottom: 3px solid %s;">
						<h1 style="margin-top: 0; color: %s;">%s</h1>
						<h2 style="margin-top: 0; color: %s;">Password Reset Request</h2>
					</div>
					<div style="padding: 25px; background-color: %s;">
						<h3 style="margin-top: 0; color: %s;">Dear %s,</h3>
						<p style="color: %s;">You requested a password reset for your %s account. Please use the following token to reset your password:</p>
						<div style="background-color: %s; padding: 15px; text-align: center; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;">Your password reset token is:</p>
							<div class="code" style="font-size: 24px; font-weight: bold; color: #B2FFBF; margin: 10px 0; letter-spacing: 2px;">%s</div>
						</div>
						<p style="color: %s;">This token will expire in <span style="color: #FF6060;">10 minutes</span>.</p>
						<p style="color: %s;">If you did not request a password reset, please ignore this email or contact support.</p>
						<div style="margin-top: 30px; padding-top: 15px; border-top: 1px solid #333333; color: #999999;">
							<p style="color: %s;">Regards,<br>Team %s</p>
						</div>
					</div>
					<div style="text-align: center; padding: 10px; background-color: %s; font-size: 12px; color: #777; border-radius: 0 0 4px 4px;">
						<p style="color: %s;">© 2026 Paridhi. All rights reserved.</p>
					</div>
				</div>
			</body>
			</html>
			"""
			.formatted(
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX.trim(), // title
				AppConstant.Style.BACKGROUND_COLOR, // body background
				AppConstant.Style.BACKGROUND_COLOR, // card background
				AppConstant.Style.PRIMARY_COLOR, // header background
				AppConstant.Style.BORDER_BOTTOM_COLOR, // border bottom
				AppConstant.Style.H_COLOR, // h1 color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // h1 text
				AppConstant.Style.H_COLOR, // h2 color
				AppConstant.Style.BACKGROUND_COLOR, // content background
				AppConstant.Style.H3_TEXT_COLOR, // h3 color
				name, // recipient name
				AppConstant.Style.TEXT_COLOR, // paragraph color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // application name
				AppConstant.Style.HEADER_BACKGROUND, // token box background
				AppConstant.Style.SECONDARY_COLOR, // token box left border
				AppConstant.Style.TEXT_COLOR, // token label color
				token, // the actual token
				AppConstant.Style.TEXT_COLOR, // expiry text color
				AppConstant.Style.TEXT_COLOR, // disclaimer color
				AppConstant.Style.TEXT_COLOR, // signature color
				MessageConstant.EmailConstants.MEGATRONIX, // signature name
				AppConstant.Style.HEADER_BACKGROUND, // footer background
				AppConstant.Style.TEXT_COLOR // footer text color
			);
	}

	/**
	 * Generates HTML content for OTP verification email.
	 */
	public static String getOtpContent ( String name, String otp ) {
		return """
			<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>%s - Email Verification</title>
			</head>
			<body style="font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; color: #FFFFFF; background-color: %s;">
				<div class="card" style="border: 1px solid #333333; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.3); background-color: %s;">
					<div style="text-align: center; padding: 20px; background-color: %s; color: white; border-bottom: 3px solid %s;">
						<h1 style="margin-top: 0; color: %s;">%s</h1>
						<h2 style="margin-top: 0; color: %s;">Email Verification</h2>
					</div>
					<div style="padding: 25px; background-color: %s;">
						<h3 style="margin-top: 0; color: %s;">Dear %s,</h3>
						<p style="color: %s;">Thank you for registering for %s!</p>
						<div style="background-color: %s; padding: 15px; text-align: center; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;">Your verification code is:</p>
							<div class="code" style="font-size: 24px; font-weight: bold; color: #B2FFBF; margin: 10px 0; letter-spacing: 2px;">%s</div>
						</div>
						<p style="color: %s;">Please enter this code to verify your email address. This code will expire in <strong>10 minutes</strong>.</p>
						<p style="color: %s;">If you didn't request this verification, please ignore this email.</p>
						<div style="margin-top: 30px; padding-top: 15px; border-top: 1px solid #333333; color: #999999;">
							<p style="color: %s;">Regards,<br>Team %s</p>
						</div>
					</div>
					<div style="text-align: center; padding: 10px; background-color: %s; font-size: 12px; color: #777; border-radius: 0 0 4px 4px;">
						<p style="color: %s;">© 2026 Paridhi. All rights reserved.</p>
					</div>
				</div>
			</body>
			</html>
			"""
			.formatted(
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX.trim(), // title
				AppConstant.Style.BACKGROUND_COLOR, // body background
				AppConstant.Style.BACKGROUND_COLOR, // card background
				AppConstant.Style.PRIMARY_COLOR, // header background
				AppConstant.Style.BORDER_BOTTOM_COLOR, // border bottom
				AppConstant.Style.H_COLOR, // h1 color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // h1 text
				AppConstant.Style.H_COLOR, // h2 color
				AppConstant.Style.BACKGROUND_COLOR, // content background
				AppConstant.Style.H3_TEXT_COLOR, // h3 color
				name, // recipient name
				AppConstant.Style.TEXT_COLOR, // paragraph color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // application name
				AppConstant.Style.HEADER_BACKGROUND, // OTP box background
				AppConstant.Style.SECONDARY_COLOR, // OTP box left border
				AppConstant.Style.TEXT_COLOR, // OTP label color
				otp, // the actual OTP
				AppConstant.Style.TEXT_COLOR, // expiry text color
				AppConstant.Style.TEXT_COLOR, // disclaimer color
				AppConstant.Style.TEXT_COLOR, // signature color
				MessageConstant.EmailConstants.MEGATRONIX, // signature name
				AppConstant.Style.HEADER_BACKGROUND, // footer background
				AppConstant.Style.TEXT_COLOR // footer text color
			);
	}

	/**
	 * Generates HTML content for MRD (Member Registration Details) welcome email.
	 */
	public static String getMRDContent ( String name, String gid ) {
		return """
			<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>%s - Registration Confirmation</title>
			</head>
			<body style="font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; color: #FFFFFF; background-color: %s;">
				<div class="card" style="border: 1px solid #333333; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.3); background-color: %s;">
					<div style="text-align: center; padding: 20px; background-color: %s; color: white; border-bottom: 3px solid %s;">
						<h1 style="margin-top: 0; color: %s;">%s</h1>
						<h2 style="margin-top: 0; color: %s;">General Registration Confirmation</h2>
					</div>
					<div style="padding: 25px; background-color: %s;">
						<h3 style="margin-top: 0; color: %s;">Dear %s,</h3>
						<p style="color: %s;">Thank you for registering for %s! We are thrilled to have you join us for the 13th edition of Paridhi, organized by Megatronix.</p>
						<div style="background-color: %s; padding: 15px; text-align: center; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;">Your Paridhi ID (GID) is:</p>
							<div class="code" style="font-size: 24px; font-weight: bold; color: #B2FFBF; margin: 10px 0; letter-spacing: 2px;">%s</div>
						</div>
						<p style="color: %s;">This email contains your official registration credentials, which you need to present at the registration desk on the day of the event, to complete further formalities and participate in the event. This unique GID number will be required for participation in every event of Paridhi '26.</p>
						<p style="color: %s;">We look forward to seeing you at Paridhi '26!</p>
						<div style="margin-top: 30px; padding-top: 15px; border-top: 1px solid #333333; color: #999999;">
							<p style="color: %s;">Sincerely,<br>Team %s</p>
						</div>
					</div>
					<div style="text-align: center; padding: 10px; background-color: %s; font-size: 12px; color: #777; border-radius: 0 0 4px 4px;">
						<p style="color: %s;">© 2026 Paridhi. All rights reserved.</p>
					</div>
				</div>
			</body>
			</html>
			"""
			.formatted(
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX.trim(), // title
				AppConstant.Style.BACKGROUND_COLOR, // body background
				AppConstant.Style.BACKGROUND_COLOR, // card background
				AppConstant.Style.PRIMARY_COLOR, // header background
				AppConstant.Style.BORDER_BOTTOM_COLOR, // border bottom
				AppConstant.Style.H_COLOR, // h1 color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // h1 text
				AppConstant.Style.H_COLOR, // h2 color
				AppConstant.Style.BACKGROUND_COLOR, // content background
				AppConstant.Style.H3_TEXT_COLOR, // h3 color
				name, // recipient name
				AppConstant.Style.TEXT_COLOR, // paragraph color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // application name
				AppConstant.Style.HEADER_BACKGROUND, // GID box background
				AppConstant.Style.SECONDARY_COLOR, // GID box left border
				AppConstant.Style.TEXT_COLOR, // GID label color
				gid, // the actual GID
				AppConstant.Style.TEXT_COLOR, // instruction text color
				AppConstant.Style.TEXT_COLOR, // closing text color
				AppConstant.Style.TEXT_COLOR, // signature color
				MessageConstant.EmailConstants.MEGATRONIX, // signature name
				AppConstant.Style.HEADER_BACKGROUND, // footer background
				AppConstant.Style.TEXT_COLOR // footer text color
			);
	}

	/**
	 * Generates HTML content for event registration confirmation email.
	 */
	public static String getRDContent ( String teamName, String eventName, String tid ) {
		return """
			<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>%s - Event Registration</title>
			</head>
			<body style="font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; color: #FFFFFF; background-color: %s;">
				<div class="card" style="border: 1px solid #333333; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.3); background-color: %s;">
					<div style="text-align: center; padding: 20px; background-color: %s; color: white; border-bottom: 3px solid %s;">
						<h1 style="margin-top: 0; color: %s;">%s</h1>
						<h2 style="margin-top: 0; color: %s;">Event Registration Confirmation</h2>
					</div>
					<div style="padding: 25px; background-color: %s;">
						<h3 style="margin-top: 0; color: %s;">Dear %s,</h3>
						<p style="color: %s;">Thank you for registering for <span style="color: #FF6060;">%s</span> at %s!</p>
						<div style="background-color: %s; padding: 15px; text-align: center; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;">Your Team ID (TID) is:</p>
							<div class="code" style="font-size: 24px; font-weight: bold; color: #B2FFBF; margin: 10px 0; letter-spacing: 2px;">%s</div>
						</div>
						<p style="color: %s;">We've received your registration and you're all set for the event. Make sure to keep it handy, as it will be essential for verifying your registration and moving forward with the event process. Kindly ensure all necessary formalities associated with your TID are completed at the earliest to avoid any last-minute issues.<br>Please keep an eye on your email for further updates about the event schedule and requirements.</p>
						<p style="color: %s;">If you have any questions, feel free to contact the event coordinators.</p>
						<div style="margin-top: 30px; padding-top: 15px; border-top: 1px solid #333333; color: #999999;">
							<p style="color: %s;">Best of luck,<br>Team %s</p>
						</div>
					</div>
					<div style="text-align: center; padding: 10px; background-color: %s; font-size: 12px; color: #777; border-radius: 0 0 4px 4px;">
						<p style="color: %s;">© 2026 Paridhi. All rights reserved.</p>
					</div>
				</div>
			</body>
			</html>
			"""
			.formatted(
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX.trim(), // title
				AppConstant.Style.BACKGROUND_COLOR, // body background
				AppConstant.Style.BACKGROUND_COLOR, // card background
				AppConstant.Style.PRIMARY_COLOR, // header background
				AppConstant.Style.BORDER_BOTTOM_COLOR, // border bottom
				AppConstant.Style.H_COLOR, // h1 color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // h1 text
				AppConstant.Style.H_COLOR, // h2 color
				AppConstant.Style.BACKGROUND_COLOR, // content background
				AppConstant.Style.H3_TEXT_COLOR, // h3 color
				teamName, // team name
				AppConstant.Style.TEXT_COLOR, // paragraph color
				eventName, // event name
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // application name
				AppConstant.Style.HEADER_BACKGROUND, // TID box background
				AppConstant.Style.SECONDARY_COLOR, // TID box left border
				AppConstant.Style.TEXT_COLOR, // TID label color
				tid, // the actual TID
				AppConstant.Style.TEXT_COLOR, // instruction text color
				AppConstant.Style.TEXT_COLOR, // closing text color
				AppConstant.Style.TEXT_COLOR, // signature color
				MessageConstant.EmailConstants.MEGATRONIX, // signature name
				AppConstant.Style.HEADER_BACKGROUND, // footer background
				AppConstant.Style.TEXT_COLOR // footer text color
			);
	}

	/**
	 * Generates HTML content for qualification congratulations email.
	 */
	public static String getQualificationContent ( String teamName, String eventName, String tid ) {
		return """
			<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>%s - Qualified for Finals</title>
			</head>
			<body style="font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; color: #FFFFFF; background-color: %s;">
				<div class="card" style="border: 1px solid #333333; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.3); background-color: %s;">
					<div style="text-align: center; padding: 20px; background-color: %s; color: white; border-bottom: 3px solid %s;">
						<h1 style="margin-top: 0; color: %s;">%s</h1>
						<h2 style="margin-top: 0; color: %s;">Congratulations!</h2>
					</div>
					<div style="padding: 25px; background-color: %s;">
						<h3 style="margin-top: 0; color: %s;">Team %s,</h3>
						<p style="color: %s;">Congratulations! Your team has <span style="color: %s; font-weight: bold;">qualified for the finals</span> of <span style="color: #FF6060;">%s</span> at %s!</p>
						<div style="background-color: %s; padding: 15px; text-align: center; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;">Your Team ID (TID):</p>
							<div class="code" style="font-size: 24px; font-weight: bold; color: #B2FFBF; margin: 10px 0; letter-spacing: 2px;">%s</div>
						</div>
						<p style="color: %s;">We're impressed with your performance in the preliminary round and excited to see what you'll bring to the finals!</p>
						<p style="color: %s;">Please keep an eye on your email for details about the final round schedule, venue, and any specific requirements.</p>
						<p style="color: %s;">This is your moment to shine - good luck!</p>
						<div style="margin-top: 30px; padding-top: 15px; border-top: 1px solid #333333; color: #999999;">
							<p style="color: %s;">Best regards,<br>Team %s</p>
						</div>
					</div>
					<div style="text-align: center; padding: 10px; background-color: %s; font-size: 12px; color: #777; border-radius: 0 0 4px 4px;">
						<p style="color: %s;">© 2026 Paridhi. All rights reserved.</p>
					</div>
				</div>
			</body>
			</html>
			"""
			.formatted(
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX.trim(), // title
				AppConstant.Style.BACKGROUND_COLOR, // body background
				AppConstant.Style.BACKGROUND_COLOR, // card background
				AppConstant.Style.PRIMARY_COLOR, // header background
				AppConstant.Style.BORDER_BOTTOM_COLOR, // border bottom
				AppConstant.Style.H_COLOR, // h1 color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // h1 text
				AppConstant.Style.H_COLOR, // h2 color
				AppConstant.Style.BACKGROUND_COLOR, // content background
				AppConstant.Style.H3_TEXT_COLOR, // h3 color
				teamName, // team name
				AppConstant.Style.TEXT_COLOR, // paragraph color
				AppConstant.Style.SECONDARY_COLOR, // qualified highlight color
				eventName, // event name
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // application name
				AppConstant.Style.HEADER_BACKGROUND, // TID box background
				AppConstant.Style.SECONDARY_COLOR, // TID box left border
				AppConstant.Style.TEXT_COLOR, // TID label color
				tid, // the actual TID
				AppConstant.Style.TEXT_COLOR, // message text color
				AppConstant.Style.TEXT_COLOR, // instruction text color
				AppConstant.Style.TEXT_COLOR, // closing text color
				AppConstant.Style.TEXT_COLOR, // signature color
				MessageConstant.EmailConstants.MEGATRONIX, // signature name
				AppConstant.Style.HEADER_BACKGROUND, // footer background
				AppConstant.Style.TEXT_COLOR // footer text color
			);
	}

	/**
	 * Generates HTML content for position congratulations email.
	 */
	public static String getPositionContent ( String teamName, String eventName, String tid, Position position ) {
		// Get appropriate medal emoji and color based on position
		String medal;
		String positionColor;
		String positionText = position.toString();

		switch ( position ) {
			case FIRST:
				medal = "🥇";
				positionColor = AppConstant.Style.FIRST_PLACE_COLOR;
				positionText = "FIRST PLACE";
				break;
			case SECOND:
				medal = "🥈";
				positionColor = AppConstant.Style.SECOND_PLACE_COLOR;
				positionText = "SECOND PLACE";
				break;
			case THIRD:
				medal = "🥉";
				positionColor = AppConstant.Style.THIRD_PLACE_COLOR;
				positionText = "THIRD PLACE";
				break;
			default:
				medal = "🏆";
				positionColor = AppConstant.Style.SECONDARY_COLOR;
				break;
		}

		return """
			<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>%s - Congratulations on Your Win!</title>
			</head>
			<body style="font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; color: #FFFFFF; background-color: %s;">
				<div class="card" style="border: 1px solid #333333; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.3); background-color: %s;">
					<div style="text-align: center; padding: 20px; background-color: %s; color: white; border-bottom: 3px solid %s;">
						<h1 style="margin-top: 0; color: %s;">%s</h1>
						<h2 style="margin-top: 0; color: %s;">Congratulations on Your Victory!</h2>
					</div>
					<div style="padding: 25px; background-color: %s;">
						<h3 style="margin-top: 0; color: %s;">Team %s,</h3>
						<p style="color: %s;">We're thrilled to announce that your team has secured <span style="color: %s; font-weight: bold;">%s %s</span> in <span style="color: #FF6060;">%s</span> at %s!</p>
						<div style="background-color: %s; padding: 15px; text-align: center; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;">Your Team ID (TID):</p>
							<div class="code" style="font-size: 24px; font-weight: bold; color: #B2FFBF; margin: 10px 0; letter-spacing: 2px;">%s</div>
						</div>
						<p style="color: %s;">Your exceptional performance and dedication have paid off. This achievement is a testament to your hard work, creativity, and teamwork.</p>
						<p style="color: %s;">You'll be contacted shortly regarding the prize distribution ceremony and certificates.</p>
						<p style="color: %s;">Once again, congratulations on your outstanding achievement!</p>
						<div style="margin-top: 30px; padding-top: 15px; border-top: 1px solid #333333; color: #999999;">
							<p style="color: %s;">With appreciation,<br>Team %s</p>
						</div>
					</div>
					<div style="text-align: center; padding: 10px; background-color: %s; font-size: 12px; color: #777; border-radius: 0 0 4px 4px;">
						<p style="color: %s;">© 2026 Paridhi. All rights reserved.</p>
					</div>
				</div>
			</body>
			</html>
			"""
			.formatted(
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX.trim(), // title
				AppConstant.Style.BACKGROUND_COLOR, // body background
				AppConstant.Style.BACKGROUND_COLOR, // card background
				AppConstant.Style.PRIMARY_COLOR, // header background
				AppConstant.Style.BORDER_BOTTOM_COLOR, // border bottom
				AppConstant.Style.H_COLOR, // h1 color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // h1 text
				AppConstant.Style.H_COLOR, // h2 color
				AppConstant.Style.BACKGROUND_COLOR, // content background
				AppConstant.Style.H3_TEXT_COLOR, // h3 color
				teamName, // team name
				AppConstant.Style.TEXT_COLOR, // paragraph color
				positionColor, // position color
				medal, // medal emoji
				positionText, // position text
				eventName, // event name
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // application name
				AppConstant.Style.HEADER_BACKGROUND, // TID box background
				AppConstant.Style.SECONDARY_COLOR, // TID box left border
				AppConstant.Style.TEXT_COLOR, // TID label color
				tid, // the actual TID
				AppConstant.Style.TEXT_COLOR, // congratulations text color
				AppConstant.Style.TEXT_COLOR, // info text color
				AppConstant.Style.TEXT_COLOR, // closing text color
				AppConstant.Style.TEXT_COLOR, // signature color
				MessageConstant.EmailConstants.MEGATRONIX, // signature name
				AppConstant.Style.HEADER_BACKGROUND, // footer background
				AppConstant.Style.TEXT_COLOR // footer text color
			);
	}

	/**
	 * Generates enhanced HTML content for profile credentials email.
	 */
	public static String getProfileCredentialsContent ( String name, String email, String password ) {
		return """
			<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>%s - Account Credentials</title>
			</head>
			<body style="font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; color: #FFFFFF; background-color: %s;">
				<div class="card" style="border: 1px solid #333333; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.3); background-color: %s;">
					<div style="text-align: center; padding: 20px; background-color: %s; color: white; border-bottom: 3px solid %s;">
						<h1 style="margin-top: 0; color: %s;">%s</h1>
						<h2 style="margin-top: 0; color: %s;">Your Account Credentials</h2>
					</div>
					<div style="padding: 25px; background-color: %s;">
						<h3 style="margin-top: 0; color: %s;">Dear %s,</h3>
						<p style="color: %s;">Your account has been created for %s. Below are your login credentials:</p>
						<div style="background-color: %s; padding: 15px; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;"><strong>Email:</strong></p>
							<p style="color: %s; margin-top: 5px; word-break: break-all;">%s</p>
						</div>
						<div style="background-color: %s; padding: 15px; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;"><strong>Temporary Password:</strong></p>
							<p style="color: #B2FFBF; font-size: 18px; font-weight: bold; margin-top: 5px; letter-spacing: 1px;">%s</p>
						</div>
						<p style="color: %s;"><strong style="color: #FF6060;">IMPORTANT:</strong> To ensure the security of your account, we strongly recommend updating your password immediately after your first login. This helps keep your profile safe and ensures only you have access to your information.</p>
						<p style="color: %s;">You can use these credentials to log in through the Megatronix app and explore your profile, track your registrations, and stay updated with everything happening at Megatronix.</p>
						<p style="color: %s;">If you have any questions, please contact us.</p>
						<div style="margin-top: 30px; padding-top: 15px; border-top: 1px solid #333333; color: #999999;">
							<p style="color: %s;">Regards,<br>Team %s</p>
						</div>
					</div>
					<div style="text-align: center; padding: 10px; background-color: %s; font-size: 12px; color: #777; border-radius: 0 0 4px 4px;">
						<p style="color: %s;">© 2026 Paridhi. All rights reserved.</p>
					</div>
				</div>
			</body>
			</html>
			"""
			.formatted(
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX.trim(), // title
				AppConstant.Style.BACKGROUND_COLOR, // body background
				AppConstant.Style.BACKGROUND_COLOR, // card background
				AppConstant.Style.PRIMARY_COLOR, // header background
				AppConstant.Style.BORDER_BOTTOM_COLOR, // border bottom
				AppConstant.Style.H_COLOR, // h1 color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // h1 text
				AppConstant.Style.H_COLOR, // h2 color
				AppConstant.Style.BACKGROUND_COLOR, // content background
				AppConstant.Style.H3_TEXT_COLOR, // h3 color
				name, // recipient name
				AppConstant.Style.TEXT_COLOR, // paragraph color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // application name
				AppConstant.Style.HEADER_BACKGROUND, // email box background
				AppConstant.Style.SECONDARY_COLOR, // email box left border
				AppConstant.Style.TEXT_COLOR, // email label color
				AppConstant.Style.TEXT_COLOR, // email value color
				email, // the actual email
				AppConstant.Style.HEADER_BACKGROUND, // password box background
				AppConstant.Style.PRIMARY_COLOR, // password box left border
				AppConstant.Style.TEXT_COLOR, // password label color
				password, // the actual password
				AppConstant.Style.TEXT_COLOR, // warning text color
				AppConstant.Style.TEXT_COLOR, // info paragraph color
				AppConstant.Style.TEXT_COLOR, // questions text color
				AppConstant.Style.TEXT_COLOR, // signature color
				MessageConstant.EmailConstants.MEGATRONIX, // signature name
				AppConstant.Style.HEADER_BACKGROUND, // footer background
				AppConstant.Style.TEXT_COLOR // footer text color
			);
	}

	/**
	 * Generates HTML content for consolidated email with credentials.
	 */
	public static String getConsolidatedEmailContent ( String to, String name, String password, String gid ) {
		return """
			<!DOCTYPE html>
			<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>%s - Registration Confirmation & Credentials</title>
			</head>
			<body style="font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; color: #FFFFFF; background-color: %s;">
				<div class="card" style="border: 1px solid #333333; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0,0,0,0.3); background-color: %s;">
					<div style="text-align: center; padding: 20px; background-color: %s; color: white; border-bottom: 3px solid %s;">
						<h1 style="margin-top: 0; color: %s;">%s</h1>
						<h2 style="margin-top: 0; color: %s;">Registration Confirmation & Credentials</h2>
					</div>
					<div style="padding: 25px; background-color: %s;">
						<h3 style="margin-top: 0; color: %s;">Dear %s,</h3>
						<p style="color: %s;">Thank you for registering for %s! We are thrilled to have you join us for the 13th edition of Paridhi, organized by Megatronix.</p>
						<div style="background-color: %s; padding: 15px; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;">
								<strong>Email:</strong> <span style="color: %s; word-break: break-all;">%s</span>
							</p>
						</div>
						<div style="background-color: %s; padding: 15px; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;">
								<strong>Temporary Password:</strong> <span style="color: #B2FFBF; font-size: 18px; font-weight: bold; letter-spacing: 1px;">%s</span>
							</p>
						</div>
						<div style="background-color: %s; padding: 15px; text-align: center; border-radius: 4px; margin: 20px 0; border-left: 4px solid %s;">
							<p style="margin: 0; font-size: 16px; color: %s;">Your Paridhi ID (GID) is:</p>
							<div class="code" style="font-size: 24px; font-weight: bold; color: #B2FFBF; margin: 10px 0; letter-spacing: 2px;">%s</div>
						</div>
						<p style="color: %s;"><strong style="color: #FF6060;">IMPORTANT:</strong> To ensure the security of your account, we strongly recommend updating your password immediately after your first login.</p>
						<p style="color: %s;">This email contains your official registration credentials, which you need to present at the registration desk on the day of the event. The unique GID number will be required for participation in every event of Paridhi '26.</p>
						<p style="color: %s;">We look forward to seeing you at Paridhi '26!</p>
						<div style="margin-top: 30px; padding-top: 15px; border-top: 1px solid #333333; color: #999999;">
							<p style="color: %s;">Sincerely,<br>Team %s</p>
						</div>
					</div>
					<div style="text-align: center; padding: 10px; background-color: %s; font-size: 12px; color: #777; border-radius: 0 0 4px 4px;">
						<p style="color: %s;">© 2026 Paridhi. All rights reserved.</p>
					</div>
				</div>
			</body>
			</html>
			"""
			.formatted(
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX.trim(), // title
				AppConstant.Style.BACKGROUND_COLOR, // body background
				AppConstant.Style.BACKGROUND_COLOR, // card background
				AppConstant.Style.PRIMARY_COLOR, // header background
				AppConstant.Style.BORDER_BOTTOM_COLOR, // border bottom
				AppConstant.Style.H_COLOR, // h1 color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // h1 text
				AppConstant.Style.H_COLOR, // h2 color
				AppConstant.Style.BACKGROUND_COLOR, // content background
				AppConstant.Style.H3_TEXT_COLOR, // h3 color
				name, // recipient name
				AppConstant.Style.TEXT_COLOR, // paragraph color
				MessageConstant.EmailConstants.EMAIL_SUBJECT_PREFIX, // application name
				AppConstant.Style.HEADER_BACKGROUND, // email box background
				AppConstant.Style.SECONDARY_COLOR, // email box left border
				AppConstant.Style.TEXT_COLOR, // email label color
				AppConstant.Style.TEXT_COLOR, // email value color
				to, // actual email
				AppConstant.Style.HEADER_BACKGROUND, // password box background
				AppConstant.Style.PRIMARY_COLOR, // password box left border
				AppConstant.Style.TEXT_COLOR, // password label color
				password, // actual password
				AppConstant.Style.HEADER_BACKGROUND, // GID box background
				AppConstant.Style.SECONDARY_COLOR, // GID box left border
				AppConstant.Style.TEXT_COLOR, // GID label color
				gid, // actual GID
				AppConstant.Style.TEXT_COLOR, // warning text color
				AppConstant.Style.TEXT_COLOR, // info paragraph color
				AppConstant.Style.TEXT_COLOR, // closing text color
				AppConstant.Style.TEXT_COLOR, // signature color
				MessageConstant.EmailConstants.MEGATRONIX, // signature name
				AppConstant.Style.HEADER_BACKGROUND, // footer background
				AppConstant.Style.TEXT_COLOR // footer text color
			);
	}
}
