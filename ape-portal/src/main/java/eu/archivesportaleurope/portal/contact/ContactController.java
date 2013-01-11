package eu.archivesportaleurope.portal.contact;

import eu.archivesportaleurope.portal.common.email.EmailSender;
import eu.archivesportaleurope.portal.search.simple.SimpleSearch;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
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

    @RenderMapping(params = "myaction=success")
    public String showPageResult(RenderResponse response, Model model) {
        return "success";
    }

    @RenderMapping(params = "myaction=error")
    public String showPageError(RenderResponse response, Model model) {
        return "error";
    }


    @ActionMapping(params = "myaction=contact")
    public void showResult(@ModelAttribute("contact") Contact contact, BindingResult result, ActionResponse response) {
        ContactValidator contactValidator = new ContactValidator();
        contactValidator.validate(contact, result);
        if(result.hasErrors()) {
            response.setRenderParameter("myaction", "contact");
            return;
        }
        try {
            EmailSender.sendEmail(contact.getType(), contact.getEmail(), contact.getFeedback());
            response.setRenderParameter("myaction", "success");
        } catch (Exception e) {
            LOG.error("Error sending the email", e);
            response.setRenderParameter("myaction", "error");
        }
    }
}
