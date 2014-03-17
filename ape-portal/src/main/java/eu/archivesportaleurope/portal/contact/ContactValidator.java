import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;

/**
 * User: Yoann Moranville Date: 11/01/2013
 * 
 * @author Yoann Moranville
 */
public class ContactValidator implements Validator {
	private static final Pattern EMAIL_PATTERN = Pattern
			.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

	private boolean loggedIn = false;
	private boolean feedback = false;

	public boolean supports(Class<?> klass) {
		return Contact.class.isAssignableFrom(klass);
	}

	public ContactValidator(boolean loggedIn, boolean feedback) {
		this.loggedIn = loggedIn;
		this.feedback = feedback;
	}

	public void validate(Object target, Errors errors) {
		Contact contact = (Contact) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "feedback", "feedback.error.feedback");
		if (!feedback){
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "feedback.error.type");
		}
		

		if (!loggedIn) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "feedback.error.email");
			// Email test.
			String email = contact.getEmail();
			if (StringUtils.isNotBlank(email)) { // it's rejected in preview
									// check(rejectIfEmptyOrWhitespace), prevent
									// repeated messages
				// RFC regexp for emails
				Matcher matcher = EMAIL_PATTERN.matcher(email);
				if (!matcher.find()) {
					errors.rejectValue("email", "feedback.error.email");
				}
			}

			// Captcha test.
			if (StringUtils.isBlank(contact.getRecaptcha_challenge_field()) || StringUtils.isBlank(contact.getRecaptcha_response_field()) ){
				errors.rejectValue("captcha", "feedback.error.captcha.incorrect");
			}else {
				String remoteAddr = PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_URL_VERIFY);
				ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
				reCaptcha.setPrivateKey(PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_PRIVATE_KEY));
				ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr,
						contact.getRecaptcha_challenge_field(), contact.getRecaptcha_response_field());
	
				if (!reCaptchaResponse.isValid()) {
					errors.rejectValue("captcha", "feedback.error.captcha.incorrect");
				}
			}
		}
	}
}
