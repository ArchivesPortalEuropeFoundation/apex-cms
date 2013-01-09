package eu.archivesportaleurope.portal.common.email;

import com.liferay.portal.service.UserService;
import eu.apenet.commons.infraestructure.EmailComposer;
import eu.apenet.commons.infraestructure.Emailer;
import eu.apenet.commons.utils.APEnetUtilities;

/**
 * User: Yoann Moranville
 * Date: 09/01/2013
 *
 * @author Yoann Moranville
 */
public abstract class EmailSender {
    private final static String feedbackEmails = "jara.alvarez@mcu.es;wim.van.dongen@nationaalarchief.nl";
    private final static String contributeEmails = "susanne.danelius@riksarkivet.ra.se;lucile.grand@culture.gouv.fr";
    private final static String suggestionEmails = "k.arnold@bundesarchiv.de;lucile.grand@culture.gouv.fr";

    public static void sendEmail(String subject, String email, String body) {

        EmailComposer emailComposer;
        Emailer emailer = new Emailer();

        switch (Integer.parseInt(subject)) {
            case 1:
                emailComposer = new EmailComposer("emails/technicalIssues.txt", "Portal feedback subject topic: technical issues", true, false);
                emailComposer.setProperty("email", email);
                emailComposer.setProperty("body", body);
                emailer.sendMessage(feedbackEmails, null, null, email, emailComposer);
                break;
            case 2:
                emailComposer = new EmailComposer("emails/howToJoin.txt", "Portal feedback subject topic: how to join and contribute", true, false);
                emailComposer.setProperty("email", email);
                emailComposer.setProperty("body", body);
                emailer.sendMessage(contributeEmails, null, null, email, emailComposer);
                break;
            case 3:
                emailComposer = new EmailComposer("emails/suggestions.txt", "Portal feedback subject topic: suggestions and other issues", true, false);
                emailComposer.setProperty("email", email);
                emailComposer.setProperty("body", body);
                emailer.sendMessage(suggestionEmails, null, null, email, emailComposer);
                break;
        }
    }

}
