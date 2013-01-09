package eu.archivesportaleurope.portal.contact;

import eu.archivesportaleurope.portal.common.email.EmailSender;
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
 * @author Yoann Moranville
 */

@Controller(value = "contactController")
@RequestMapping(value = "VIEW")
public class ContactController {

    @RenderMapping
    public ModelAndView showContact() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("contact");
        return modelAndView;
    }


    @ResourceMapping(value = "result") //or RequestMapping?
    public ModelAndView showResult(@ModelAttribute("contact") Contact contact, BindingResult result, ResourceRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("contact");
        try {
            EmailSender.sendEmail(contact.getType(), contact.getEmail(), contact.getFeedback());
            modelAndView.getModelMap().addAttribute("result", true);
        } catch (Exception e) {
            modelAndView.getModelMap().addAttribute("result", false);
        }
        return modelAndView;
    }
}
