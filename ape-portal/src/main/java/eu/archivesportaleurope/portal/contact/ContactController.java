package eu.archivesportaleurope.portal.contact;

import javax.portlet.ActionRequest;
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
import com.liferay.portal.util.PortalUtil;

import eu.archivesportaleurope.portal.common.email.EmailSender;

@Controller(value = "contactController")
@RequestMapping(value = "VIEW")
public class ContactController {

    private static final Logger LOGGER = Logger.getLogger(ContactController.class);

	@RenderMapping
    public ModelAndView showInitialPage(@ModelAttribute("contact") Contact contact,RenderRequest request) throws PortalException, SystemException {
		boolean loggedIn = request.getUserPrincipal() != null;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("contact");
        modelAndView.getModelMap().addAttribute("loggedIn",loggedIn);
    	if (loggedIn){
    		User currentUser = PortalUtil.getUser(request);
    		contact.setEmail(currentUser.getEmailAddress());
    	}
        return modelAndView;
    }

    @RenderMapping(params = "myaction=input")
    public ModelAndView showInput(RenderRequest request) {
		boolean loggedIn = request.getUserPrincipal() != null;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("contact");
        modelAndView.getModelMap().addAttribute("loggedIn",loggedIn);
        return modelAndView;
    }
	
    @ModelAttribute("contact")
    public Contact getCommandObject() {
        return new Contact();
    }


    
    @RenderMapping(params = "myaction=success")
    public String showPageResult() {
        return "success";
    }

    @RenderMapping(params = "myaction=error")
    public String showPageError() {
        return "error";
    }

    @ActionMapping(params = "myaction=contact")
    public void showResult(@ModelAttribute("contact") Contact contact, BindingResult result, ActionRequest request, ActionResponse response) {
    	boolean loggedIn = request.getUserPrincipal() != null;
    	ContactValidator contactValidator = new ContactValidator(loggedIn);
        contactValidator.validate(contact, result);
		if (result.hasErrors()){
			 response.setRenderParameter("myaction", "input");
		}else {
	        try {
        		EmailSender.sendEmail(contact.getType(), contact.getEmail(), contact.getFeedback());
	            response.setRenderParameter("myaction", "success");
	        } catch (Exception e) {
	        	LOGGER.error("Error sending the email", e);
	            response.setRenderParameter("myaction", "error");
	        }
		}
    }
}
