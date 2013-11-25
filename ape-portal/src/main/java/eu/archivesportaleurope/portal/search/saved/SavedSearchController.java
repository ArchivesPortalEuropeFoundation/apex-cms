package eu.archivesportaleurope.portal.search.saved;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.infraestructure.CountryUnit;
import eu.apenet.commons.infraestructure.NavigationTree;
import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;

@Controller(value = "savedSearchController")
@RequestMapping(value = "VIEW")
public class SavedSearchController {
	private final static Logger LOGGER = Logger.getLogger(SavedSearchController.class);

	private EadSavedSearchDAO eadSavedSearchDAO;
	private ResourceBundleMessageSource messageSource;

	
	public void setEadSavedSearchDAO(EadSavedSearchDAO eadSavedSearchDAO) {
		this.eadSavedSearchDAO = eadSavedSearchDAO;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	// --maps the incoming portlet request to this method
	@RenderMapping
	public ModelAndView showSavedSearches(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SAVED_SEARCH);
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			modelAndView.getModelMap().addAttribute("pageNumber", 1);
			modelAndView.getModelMap().addAttribute("totalNumberOfResults", eadSavedSearchDAO.countEadSavedSearches(liferayUserId));
			modelAndView.getModelMap().addAttribute("pageSize", 20);
			modelAndView.getModelMap().addAttribute("eadSavedSearches",eadSavedSearchDAO.getEadSavedSearches(liferayUserId, 1, 20));
		}
		return modelAndView;
	}


}
