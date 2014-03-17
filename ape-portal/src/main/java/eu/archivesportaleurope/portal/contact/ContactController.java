package eu.archivesportaleurope.portal.contact;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
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

import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.email.EmailSender;

@Controller(value = "contactController")
@RequestMapping(value = "VIEW")
public class ContactController {
	private MessageSource messageSource;
    private static final Logger LOGGER = Logger.getLogger(ContactController.class);

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	

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
    public Contact getCommandObject(PortletRequest portletRequest) {
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource,
				portletRequest.getLocale());
		Contact contact = new Contact();
		contact.getTypeList().put(PropertiesKeys.APE_EMAILS_FEEDBACK_TECHNICAL, source.getString("label.contact.item.issues"));
		contact.getTypeList().put(PropertiesKeys.APE_EMAILS_CONTRIBUTE, source.getString("label.contact.item.contribute"));
		contact.getTypeList().put(PropertiesKeys.APE_EMAILS_SUGGESTIONS, source.getString("label.contact.item.suggestions"));
		contact.getTypeList().put(PropertiesKeys.APE_EMAILS_FEEDBACK_USER, source.getString("label.contact.item.feedback"));
        return contact;
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
    	ContactValidator contactValidator = new ContactValidator(loggedIn,false);
        contactValidator.validate(contact, result);
		if (result.hasErrors()){
			 response.setRenderParameter("myaction", "input");
		}else {
	        try {
	        	User currentUser = null;
	        	if (loggedIn){
	        		currentUser = PortalUtil.getUser(request);
	        	}
        		EmailSender.sendContactFormEmail(contact, currentUser);
	            response.setRenderParameter("myaction", "success");
	        } catch (Exception e) {
	        	LOGGER.error("Error sending the email", e);
	            response.setRenderParameter("myaction", "error");
	        }
		}
    }
}
