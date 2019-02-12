package eu.archivesportaleurope.portal.contact;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.UserDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.User;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.email.EmailSender;


/***
 * This is the feedback controller class
 */
@Controller(value = "feedbackController")
@RequestMapping(value = "VIEW")
public class FeedbackController {

    private static final Logger LOGGER = Logger.getLogger(FeedbackController.class);
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private UserDAO userDAO;
	
	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/***
	 * This function shows the contact form
	 * 
	 * @param contact {@link Contact} Contact object
	 * @param request {@link ResourceRequest} gets all data sent by the request
	 * 
	 * @return modelAndView {@link ModelAndView} modelAndView reference to view with name 'contact'
	 * 
	 * @throws PortalException
	 * @throws SystemException
	 */
	@ResourceMapping(value="feedback")
    public ModelAndView showInitialPage(@ModelAttribute("contact") Contact contact,ResourceRequest request) throws PortalException, SystemException {
		boolean loggedIn = request.getUserPrincipal() != null;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("feedback");
        modelAndView.getModelMap().addAttribute("loggedIn",loggedIn);
        if (StringUtils.isNotBlank(contact.getAiId()) && StringUtils.isNumeric(contact.getAiId())){
    		ArchivalInstitution archivalInstitution = archivalInstitutionDAO.getArchivalInstitution(Integer.parseInt(contact.getAiId()));
    		contact.setInstitution(PortalDisplayUtil.replaceQuotesAndReturns(archivalInstitution.getAiname()));
    		contact.setRepoCode(archivalInstitution.getRepositorycode());
    		contact = fixContactCharacters(contact);
        }
    	if (loggedIn){
    		com.liferay.portal.model.User currentUser = PortalUtil.getUser(request);
    		contact.setEmail(currentUser.getEmailAddress());
    		contact.setUsername(currentUser.getFullName());
    	}
        return modelAndView;
    }
	
	/***
	 * Returns a new contact object
	 * 
	 * @return {@link Contact} object
	 */
    @ModelAttribute("contact")
    public Contact getCommandObject() {
        return new Contact();
    }

    /***
     * @param result
     * @ResourceMapping(value="feedbackAction")
     * 
     * @ModelAttribute("contact")
     * 
     * This function sends the email with the feeback to the country manager of the selected resource
     * 
     * @param contact {@link Contact} email address
	 * @param request {@link ResourceRequest} request has not value vhen comes from feedback form
     * 
     * @return modelAndView {@link ModelAndView} ModelAndView: reference to view with name 'contact'
     * 
     * @throws PortalException
     * @throws SystemException
     */
    @ResourceMapping(value="feedbackAction")
    public ModelAndView showResult(@ModelAttribute("contact") Contact contact, BindingResult result, ResourceRequest request) throws PortalException, SystemException {
        ModelAndView modelAndView = new ModelAndView();
    	boolean loggedIn = request.getUserPrincipal() != null;
    	ContactValidator contactValidator = new ContactValidator(loggedIn, true);
        contact.setRecaptcha_response_field(request.getParameter("g-recaptcha-response"));
        contactValidator.validate(contact, result);
		if (result.hasErrors()){
			 modelAndView.setViewName("feedback");
		}else {
			contact = fixContactCharacters(contact);
			com.liferay.portal.model.User currentUser = null;
        	if (loggedIn){
        		currentUser = PortalUtil.getUser(request);
        	}
        	if (StringUtils.isNotBlank(contact.getAiId()) && StringUtils.isNumeric(contact.getAiId())){
        		ArchivalInstitution archivalInstitution = archivalInstitutionDAO.getArchivalInstitution(Integer.parseInt(contact.getAiId()));
		        try {
		        	User countryManager = null;
		        	if (archivalInstitution != null){
		        		countryManager= userDAO.getCountryManagerOfCountry(archivalInstitution.getCountry());
		        	}
	        		EmailSender.sendFeedbackFormEmail(contact, currentUser, archivalInstitution, countryManager);
	        		modelAndView.setViewName("success");
		        } catch (Exception e) {
		        	LOGGER.error("Error sending the email", e);
		        	modelAndView.setViewName("error");
		        }
        	}else {
		        try {
	        		EmailSender.sendFeedbackFormEmail(contact, currentUser);
	        		modelAndView.setViewName("success");
		        } catch (Exception e) {
		        	LOGGER.error("Error sending the email", e);
		        	modelAndView.setViewName("error");
		        }
        	}
		}
		return modelAndView;
    }
    
    /***
     * This function returns a contact with title and related institution
     * 
     * @param contact {@link Contact} object
     * 
     * @return contact {@link Contact} object with title and related institution
     */
	private Contact fixContactCharacters(Contact contact){
		if(contact!=null){
			contact.setTitle(PortalDisplayUtil.replaceHTMLSingleQuotes(contact.getTitle()));
			contact.setInstitution(PortalDisplayUtil.replaceHTMLSingleQuotes(contact.getInstitution()));
		}
		return contact;
	}
}
