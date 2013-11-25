package eu.archivesportaleurope.portal.search.saved;

import java.security.Principal;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;

import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;

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
			User user = (User) request.getAttribute(WebKeys.USER);
			modelAndView.getModelMap().addAttribute("timeZone", user.getTimeZone());
			modelAndView.getModelMap().addAttribute("pageNumber", 1);
			modelAndView.getModelMap().addAttribute("totalNumberOfResults", eadSavedSearchDAO.countEadSavedSearches(liferayUserId));
			modelAndView.getModelMap().addAttribute("pageSize", 20);
			modelAndView.getModelMap().addAttribute("eadSavedSearches",eadSavedSearchDAO.getEadSavedSearches(liferayUserId, 1, 20));
		}
		return modelAndView;
	}

	@ActionMapping(params="myaction=deleteSavedSearch")
	public void deleteSavedSearch(ActionRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			Long id = Long.parseLong(request.getParameter("id"));
			EadSavedSearch eadSavedSearch = eadSavedSearchDAO.getEadSavedSearch(id, liferayUserId);
			if (eadSavedSearch.getLiferayUserId() == liferayUserId){
				eadSavedSearchDAO.delete(eadSavedSearch);
			}
		}
	}
	@RenderMapping(params="myaction=editSavedSearchForm")
	public String showEditSavedSearchForm() {
		return "editSavedSearchForm";
	}

	@ActionMapping(params="myaction=saveEditSavedSearch")
	public void saveSavedSearch(@ModelAttribute("savedSearch") SavedSearch savedSearch, BindingResult bindingResult,ActionRequest request, ActionResponse response)  {
//		myValidator.validate(book, bindingResult);
//		if (!bindingResult.hasErrors()) {
		Principal principal = request.getUserPrincipal();
		if (principal != null){
			Long liferayUserId = Long.parseLong(principal.toString());
			EadSavedSearch eadSavedSearch = eadSavedSearchDAO.getEadSavedSearch(Long.parseLong(savedSearch.getId()), liferayUserId);
			if (eadSavedSearch.getLiferayUserId() == liferayUserId){
				eadSavedSearch.setLabel(savedSearch.getDescription());
				eadSavedSearchDAO.store(eadSavedSearch);
			}
		}	
//			bookService.editBook(book);
//			response.setRenderParameter("myaction", "books");
//		} else {
//			//--this is required. the getBook method is not invoked but the @RequestParam
//			//--is still evaluated
//			response.setRenderParameter("isbnNumber", book.getIsbnNumber().toString());
//			response.setRenderParameter("myaction", "editBookForm");
//		}
	}
	
	@ModelAttribute("savedSearch")
	public SavedSearch getEadSavedSearch(PortletRequest request) {
		Principal principal = request.getUserPrincipal();
		String id = request.getParameter("id");
		if (principal != null && StringUtils.isNotBlank(id)){
			Long liferayUserId = Long.parseLong(principal.toString());
			EadSavedSearch eadSavedSearch = eadSavedSearchDAO.getEadSavedSearch(Long.parseLong(id), liferayUserId);
			if (eadSavedSearch.getLiferayUserId() == liferayUserId){
				SavedSearch savedSearch = new SavedSearch();
				savedSearch.setDescription(eadSavedSearch.getLabel());
				savedSearch.setTerm(eadSavedSearch.getTerm());
				savedSearch.setModifiedDate(eadSavedSearch.getModifiedDate());
				savedSearch.setId(eadSavedSearch.getId() +"");
				return savedSearch;
			}
		}		
		
		return  new SavedSearch();
	}
}
