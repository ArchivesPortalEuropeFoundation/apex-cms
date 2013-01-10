package eu.archivesportaleurope.portal.contact;

import eu.archivesportaleurope.portal.common.email.EmailSender;
import eu.archivesportaleurope.portal.search.simple.SimpleSearch;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.ResourceRequest;

/**
 * User: Yoann Moranville
 * Date: 09/01/2013
 *
 * reCAPTCHA information:
 *  - public key: 6LebX9sSAAAAAPNhohQ93xnzFSbMdKsdLeuSI2dq
 *  - private key: 6LebX9sSAAAAABqCOOeDCKMWdB0fMApqxJk87AqQ
 *
 * @author Yoann Moranville
 */

@Controller(value = "contactController")
@RequestMapping(value = "VIEW")
public class ContactController {

    private static final Logger LOG = Logger.getLogger(ContactController.class);

    @RenderMapping
    public ModelAndView showContact() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("contact");
        return modelAndView;
    }

    @ModelAttribute("contact")
    public Contact getCommandObject() {
        return new Contact();
    }

    @ResourceMapping(value = "contact")
    public ModelAndView showResult(@ModelAttribute("contact") Contact contact, BindingResult result, ResourceRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            EmailSender.sendEmail(contact.getType(), contact.getEmail(), contact.getFeedback());
            modelAndView.setViewName("redirect:contact?success=true");
//            modelAndView.getModelMap().addAttribute("result", true);
        } catch (Exception e) {
            LOG.error("Error sending the email", e);
            modelAndView.setViewName("redirect:contact?success=false");
//            modelAndView.getModelMap().addAttribute("result", false);
        }
        return modelAndView;
    }
}
