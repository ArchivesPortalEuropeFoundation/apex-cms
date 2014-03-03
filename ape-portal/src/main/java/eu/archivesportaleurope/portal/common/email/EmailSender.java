package eu.archivesportaleurope.portal.common.email;



import com.liferay.portal.model.User;

import eu.apenet.commons.infraestructure.EmailComposer;
import eu.apenet.commons.infraestructure.Emailer;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.contact.Contact;

/**
 * User: Yoann Moranville
 * Date: 09/01/2013
 *
 * @author Yoann Moranville
 */
public final class EmailSender {

    public static void sendContactFormEmail(Contact contact, User user) {
    	
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
        	 emailComposer.setProperty("username", "");
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
    public static void sendFeedbackFormEmail(Contact contact, User user) {
    	
        Emailer emailer = new Emailer();
        String title = "Content feedback: " + contact.getTitle();
        EmailComposer emailComposer = new EmailComposer("emails/feedback.txt", title, true, false);
        emailComposer.setProperty("email", contact.getEmail().replaceAll("[><]","_"));
        emailComposer.setProperty("body", contact.getFeedback().replaceAll("[><]","_"));
        emailComposer.setProperty("title", contact.getTitle());
        emailComposer.setProperty("url", contact.getUrl());
        if (user == null){
        	 emailComposer.setProperty("username", "");
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
}
