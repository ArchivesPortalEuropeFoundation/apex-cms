package eu.archivesportaleurope.portal.common.email;

import eu.apenet.commons.infraestructure.EmailComposer;
import eu.apenet.commons.infraestructure.Emailer;

/**
 * User: Yoann Moranville
 * Date: 09/01/2013
 *
 * @author Yoann Moranville
 */
public abstract class EmailSender {
    private final static String feedbackEmails = "luis.ensenat@mecd.es;wim.van.dongen@nationaalarchief.nl;beatriz.gonzalezvi@mecd.es";
    private final static String contributeEmails = "susanne.danelius@riksarkivet.ra.se;lucile.grand@culture.gouv.fr";
    private final static String suggestionEmails = "k.arnold@bundesarchiv.de;lucile.grand@culture.gouv.fr";

    public static void sendEmail(String subject, String email, String body) {

        Emailer emailer = new Emailer();
        String title = "Portal feedback subject topic: ";
        String emails = feedbackEmails;
        switch (Integer.parseInt(subject)) {
            case 1:
                title += "technical issues";
                break;
            case 2:
                title += "how to join and contribute";
                emails = contributeEmails;
                break;
            case 3:
                title += "suggestions and other issues";
                emails = suggestionEmails;
                break;
        }
        EmailComposer emailComposer = new EmailComposer("emails/feedback.txt", title, true, false);
        emailComposer.setProperty("email", email);
        emailComposer.setProperty("body", body);
        emailer.sendMessage(emails, null, null, email, emailComposer);
    }

}
