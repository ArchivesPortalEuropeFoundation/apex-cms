package eu.archivesportaleurope.portal.common.email;





import org.apache.commons.lang.StringUtils;

import eu.apenet.commons.infraestructure.EmailComposer;
import eu.apenet.commons.infraestructure.EmailComposer.Priority;
import eu.apenet.commons.infraestructure.Emailer;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.User;
import eu.archivesportaleurope.portal.api.ApiKey;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.contact.Contact;
import eu.archivesportaleurope.util.ApeUtil;

/**
 * User: Yoann Moranville
 * Date: 09/01/2013
 *
 * @author Yoann Moranville
 */
public final class EmailSender {

	private final static String ADMINS_EMAIL = PropertiesUtil.get(PropertiesKeys.APE_EMAILS_ADMINS);
	
    public static void sendContactFormEmail(Contact contact, com.liferay.portal.model.User user) {
    	
        Emailer emailer = new Emailer();
        String title = "Portal feedback subject topic: ";
        String emails = null;
        if (PropertiesKeys.APE_EMAILS_CONTRIBUTE.equalsIgnoreCase(contact.getType())){
            title += "how to join and contribute";
            emails = PropertiesUtil.get(PropertiesKeys.APE_EMAILS_CONTRIBUTE);        	
        }else if (PropertiesKeys.APE_EMAILS_SUGGESTIONS.equalsIgnoreCase(contact.getType())){
            title += "how to join and contribute";
            emails = PropertiesUtil.get(PropertiesKeys.APE_EMAILS_SUGGESTIONS);        	       	
        }else if (PropertiesKeys.APE_EMAILS_FEEDBACK_USER.equalsIgnoreCase(contact.getType())){
            title += "User feedback";
            emails = PropertiesUtil.get(PropertiesKeys.APE_EMAILS_FEEDBACK_USER);        	       	
        }else {
        	title += "technical issues";
        	 emails = PropertiesUtil.get(PropertiesKeys.APE_EMAILS_FEEDBACK_TECHNICAL);        	       	
        }
        EmailComposer emailComposer = new EmailComposer("emails/contact.txt", title, true, false);
        emailComposer.setProperty("email", contact.getEmail().replaceAll("[><]","_"));
        emailComposer.setProperty("body", contact.getFeedback().replaceAll("[><]","_"));
        if (user == null){
        	 emailComposer.setProperty("username", contact.getUsername());
        	 emailComposer.setProperty("userid", "");
        }else {
       	 emailComposer.setProperty("username", user.getFullName());
       	 emailComposer.setProperty("userid", user.getUserId()+"");        	
        }

        emailer.sendMessage(emails, null, null, contact.getEmail(), emailComposer);
        if (contact.isReceiveCopy()){
        	emailer.sendMessage(contact.getEmail(), null, null, null, emailComposer);
        }
    }
    
    public static void sendApiKeyConfirmationEmail(ApiKey apiKey, com.liferay.portal.model.User user){
        Emailer emailer = new Emailer();
        String title = "Your Api key "+apiKey.getKey();
        EmailComposer emailComposer = new EmailComposer("emails/apiConfirmationEmail.txt", title, true, false);
        emailComposer.setProperty("username", user.getFullName());
        emailComposer.setProperty("apikey", apiKey.getKey());
        emailComposer.setProperty("domain", apiKey.getDomain());
        emailComposer.setProperty("email", PropertiesUtil.get(PropertiesKeys.APE_EMAILS_FEEDBACK_USER));
        emailer.sendMessage(user.getEmailAddress(), null, null, null, emailComposer);
    }
    
    public static void sendFeedbackFormEmail(Contact contact, com.liferay.portal.model.User user) {
    	
        Emailer emailer = new Emailer();
        String title = "Content feedback: " + contact.getTitle();
        EmailComposer emailComposer = new EmailComposer("emails/feedback.txt", title, true, false);
        emailComposer.setProperty("email", contact.getEmail().replaceAll("[><]","_"));
        emailComposer.setProperty("body", contact.getFeedback().replaceAll("[><]","_"));
        emailComposer.setProperty("title", contact.getTitle());
        emailComposer.setProperty("url", contact.getUrl());
        if (user == null){
        	 emailComposer.setProperty("username", contact.getUsername());
        	 emailComposer.setProperty("userid", "");
        }else {
       	 emailComposer.setProperty("username", user.getFullName());
       	 emailComposer.setProperty("userid", user.getUserId()+"");        	
        }

        emailer.sendMessage(PropertiesUtil.get(PropertiesKeys.APE_EMAILS_FEEDBACK_USER), null, null, contact.getEmail(), emailComposer);
        if (contact.isReceiveCopy()){
        	emailer.sendMessage(contact.getEmail(), null, null, null, emailComposer);
        }
    }
    public static void sendFeedbackFormEmail(Contact contact, com.liferay.portal.model.User user, ArchivalInstitution archivalInstitution, User countryManager) {
		String toEmail = null;
		String ccEmail = null;
		String name = "UNKNOWN";
		String archivalInstitutionName = archivalInstitution.getAiname();
		if (archivalInstitution != null && StringUtils.isNotBlank(archivalInstitution.getFeedbackEmail())){
			toEmail = archivalInstitution.getFeedbackEmail();
			name = archivalInstitutionName;
		}
		if (archivalInstitution != null && archivalInstitution.getPartner() != null && toEmail == null) {
			toEmail = archivalInstitution.getPartner() .getEmailAddress();
			name = archivalInstitution.getPartner().getName();
		}
		if (archivalInstitution != null && countryManager != null) {
			if (toEmail  == null){
				toEmail = countryManager.getEmailAddress();
				name = countryManager.getName();
			}else {
				ccEmail = countryManager.getEmailAddress();
			}
		}
		if (toEmail ==null){
			toEmail = PropertiesUtil.get(PropertiesKeys.APE_EMAILS_FEEDBACK_USER);
			name = "Feedback Taskforce";
			archivalInstitutionName = "Archives Portal Europe";
		}
        Emailer emailer = new Emailer();
        String title = "Content feedback: " + contact.getTitle();
        EmailComposer emailComposer = new EmailComposer("emails/feedback.txt", title, true, false);
        emailComposer.setProperty("email", contact.getEmail().replaceAll("[><]","_"));
        emailComposer.setProperty("body", contact.getFeedback().replaceAll("[><]","_"));
        emailComposer.setProperty("title", contact.getTitle());
        emailComposer.setProperty("url", contact.getUrl());
        emailComposer.setProperty("name", name);
        emailComposer.setProperty("archivalInstitution", archivalInstitutionName);
        if (user == null){
        	 emailComposer.setProperty("username", contact.getUsername());
        	 emailComposer.setProperty("userid", "");
        }else {
       	 emailComposer.setProperty("username", user.getFullName());
       	 emailComposer.setProperty("userid", user.getUserId()+"");        	
        }

        emailer.sendMessage(toEmail, ccEmail, null, contact.getEmail(), emailComposer);
        if (contact.isReceiveCopy()){
        	emailer.sendMessage(contact.getEmail(), null, null, null, emailComposer);
        }
    }
    public static void sendExceptionToAdmin(String title, Exception e) {
        Emailer emailer = new Emailer();
        EmailComposer emailComposer = new EmailComposer("emails/admins.txt", title, true, false);
        emailComposer.setProperty("body", ApeUtil.generateThrowableLog(e));
        emailComposer.setPriority(Priority.HIGH);
        emailer.sendMessage(ADMINS_EMAIL, null, null, null, emailComposer);
    }
}
