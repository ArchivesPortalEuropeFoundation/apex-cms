package eu.archivesportaleurope.portal.contact;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;

import eu.archivesportaleurope.portal.common.email.EmailSender;

@Controller(value = "feedbackController")
@RequestMapping(value = "VIEW")
public class FeedbackController {

    private static final Logger LOGGER = Logger.getLogger(FeedbackController.class);

	@ResourceMapping(value="feedback")
    public ModelAndView showInitialPage(@ModelAttribute("contact") Contact contact,ResourceRequest request) throws PortalException, SystemException {
		boolean loggedIn = request.getUserPrincipal() != null;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("feedback");
        modelAndView.getModelMap().addAttribute("loggedIn",loggedIn);
    	if (loggedIn){
    		User currentUser = PortalUtil.getUser(request);
    		contact.setEmail(currentUser.getEmailAddress());
    	}
        return modelAndView;
    }


	
    @ModelAttribute("contact")
    public Contact getCommandObject() {
        return new Contact();
    }


    @ResourceMapping(value="feedbackAction")
    public ModelAndView showResult(@ModelAttribute("contact") Contact contact, BindingResult result, ResourceRequest request) throws PortalException, SystemException {
        ModelAndView modelAndView = new ModelAndView();
    	boolean loggedIn = request.getUserPrincipal() != null;
    	ContactValidator contactValidator = new ContactValidator(loggedIn, true);
        contactValidator.validate(contact, result);
		if (result.hasErrors()){
			 modelAndView.setViewName("feedback");
		}else {
        	User currentUser = null;
        	if (loggedIn){
        		currentUser = PortalUtil.getUser(request);
        	}
	        try {
        		EmailSender.sendFeedbackFormEmail(contact, currentUser);
        		modelAndView.setViewName("success");
	        } catch (Exception e) {
	        	LOGGER.error("Error sending the email", e);
	        	modelAndView.setViewName("error");
	        }
		}
		return modelAndView;
    }
}
