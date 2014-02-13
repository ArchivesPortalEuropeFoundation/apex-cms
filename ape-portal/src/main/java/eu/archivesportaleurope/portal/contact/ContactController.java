package eu.archivesportaleurope.portal.contact;

import java.security.Principal;

import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

import eu.archivesportaleurope.portal.common.email.EmailSender;
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

    private Principal principal;

    public Principal getPrincipal() {
		return this.principal;
	}

	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}

	@RenderMapping
    public ModelAndView showContact(RenderRequest renderRequest) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("contact");
        this.setPrincipal(renderRequest.getUserPrincipal());
        modelAndView.getModelMap().addAttribute("loggedIn", renderRequest.getUserPrincipal());
        if (this.getPrincipal() != null) {
        	try {
				User user = UserLocalServiceUtil.getUser(Long.parseLong(this.getPrincipal().getName()));

		        modelAndView.getModelMap().addAttribute("eMail", user.getEmailAddress());
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PortalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
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
        if (this.getPrincipal() != null) {
        	contactValidator.setUserName(this.getPrincipal().getName());
        }
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

    @ActionMapping(params = "myaction=recaptcha")
    public void getRecaptcha(@ModelAttribute("contact") Contact contact, BindingResult result, ActionResponse response) {
            response.setRenderParameter("myaction", "success");
            response.setRenderParameter("recaptchaPubKey", contact.getCaptcha());
    }

}
