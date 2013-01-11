package eu.archivesportaleurope.portal.contact;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Yoann Moranville
 * Date: 11/01/2013
 *
 * @author Yoann Moranville
 */
public class ContactValidator implements Validator {

    public boolean supports(Class<?> klass) {
        return Contact.class.isAssignableFrom(klass);
    }

    public void validate(Object target, Errors errors) {
        Contact contact = (Contact)target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "feedback.error.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "feedback", "feedback.error.feedback");
        if(contact.getType().equals("-1")) {
            errors.rejectValue("type", "feedback.error.type");
        }
        String email = contact.getEmail();
        //RFC regexp for emails
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher matcher = pattern.matcher(email);
        if(!matcher.find()) {
            errors.rejectValue("email", "feedback.error.email");
        }
    }
}
